package com.ag04.quarkus.cache.impl.infinispan;

import java.util.Objects;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.exceptions.TransportException;
import org.jboss.logging.Logger;

import com.ag04.quarkus.cache.Cache;
import com.ag04.quarkus.cache.CacheManager;

/**
 * Infinispan specific implementation of CacheManager.
 * 
 */
public class CacheManagerRemoteImpl implements CacheManager {
    private final Logger log = Logger.getLogger(CacheManagerRemoteImpl.class);
    
    private final RemoteCacheManager remoteCacheManager;

    public CacheManagerRemoteImpl(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        Objects.requireNonNull(name);
        RemoteCache remoteCache = remoteCacheManager.getCache(name);
        CacheRemoteImpl cache = new CacheRemoteImpl(remoteCache);
        return cache;
    }

    @Override
    public boolean cacheExists(String name) {
        try {
            if (remoteCacheManager.getCache(name) == null) {
                log.debugf("Cache %s not present.", name);
                return false;
            }
            return true;
        } catch (TransportException ex) {
            log.errorf("Error while trying to connect to the remote cache: %s", ex.getCause());
            return false;
        }
    }
    
}
