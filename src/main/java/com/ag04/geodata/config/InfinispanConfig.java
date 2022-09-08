package com.ag04.geodata.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.exceptions.TransportException;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ag04.geodata.service.dto.CountryDTO;
import com.ag04.geodata.service.dto.CurrencyDTO;
import com.ag04.quarkus.cache.CacheManager;
import com.ag04.quarkus.cache.impl.infinispan.CacheManagerRemoteImpl;

import io.quarkus.runtime.StartupEvent;

/**
 * 
 * @author dmadunic
 */
@Singleton
public class InfinispanConfig {
    private final Logger log = LoggerFactory.getLogger(InfinispanConfig.class);
    public static final Long MINUTE = 60*1000L;
    public static final Long HALF_HOUR = 30*60*1000L;
    public static final Long HOUR = 60*60*1000L;

    private final RemoteCacheManager remoteCacheManager;


    @ConfigProperty(name = "infinispan.cache-template")
    String cacheConfigTemplate;

    @Inject
    public InfinispanConfig(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
    }

    /**
     * Performs cache initializations.
     */
    void onStart(@Observes StartupEvent ev) {               
        log.info("Creating inifinispan caches ...");
    
        createCacheIfNotExist(CountryDTO.class.getCanonicalName(), HOUR, HOUR);
        createCacheIfNotExist(CurrencyDTO.class.getCanonicalName(), HOUR, HOUR);
    }
    
    @Produces
    @ApplicationScoped
    public CacheManager createCacheManager() {
        return new CacheManagerRemoteImpl(this.remoteCacheManager);
    }

    /**
     * 
     * @param cacheName - Name of the cache to be created
     * @param lifespan - Maximum lifespan of a cache entry, after which the entry is expired cluster-wide, in milliseconds. 
     * @param maxIdle Maximum idle time a cache entry will be maintained in the cache, in milliseconds. 
     */
    public void createCacheIfNotExist(String cacheName, Long lifespan, Long maxIdle) {
        if (cacheExists(cacheName)) {
            log.debug(">> Cache '{}' ALREADY EXIST --> Skipping", cacheName);
            return;
        }
        String cahceConfig = cacheConfigTemplate.replace("<name-override>", cacheName)
            .replace("<lifespan-override>", lifespan.toString())
            .replace("<maxidle-override>", maxIdle.toString());
        
        remoteCacheManager.administration().createCache(cacheName, new XMLStringConfiguration(cahceConfig));
        log.info("--> CREATED Cache '{}' with lifespan/maxIdle [{}/{}] (ms)", cacheName, lifespan, maxIdle);
    }

    private boolean cacheExists(String cacheName) {
        try {
            if (remoteCacheManager.getCache(cacheName) == null) {
                log.debug("Cache {} not present.", cacheName);
                return false;
            }
            return true;
        } catch (TransportException ex) {
            log.error("Error while trying to connect to the remote cache: {}", ex.getCause());
            return false;
        }
    }
}
