package com.ag04.geodata.config;

import org.jboss.logging.Logger;
import org.redisson.api.RedissonClient;

import com.ag04.geodata.service.dto.CountryDTO;
import com.ag04.geodata.service.dto.CurrencyDTO;
import com.ag04.quarkus.cache.CacheManager;
import com.ag04.quarkus.cache.redis.RedisCacheManager;

import io.quarkus.runtime.StartupEvent;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 
 * @author dmadunic
 */
@Singleton
public class RedisConfig {
    private final Logger log = Logger.getLogger(RedisConfig.class);

    public static final Long MINUTE = 60L;
    public static final Long HALF_HOUR = 30*60L;
    public static final Long HOUR = 60*60L;


    @ConfigProperty(name = "redis.default-ttl")
    Long defaultTtl;

    @Inject
    RedissonClient redisson;

    void onStart(@Observes StartupEvent ev) {               
        log.info("Creating Redis caches ...");
        this.createCacheManager();
    }

    @Produces
    @ApplicationScoped
    public CacheManager createCacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager(this.redisson, this.defaultTtl);
        cacheManager.createCache(CountryDTO.class.getCanonicalName(), HOUR);
        cacheManager.createCache(CurrencyDTO.class.getCanonicalName(), HOUR);
        return cacheManager;
    }

}
