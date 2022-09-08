package com.ag04.quarkus.cache.impl.infinispan;

import org.infinispan.client.hotrod.RemoteCache;

import com.ag04.quarkus.cache.Cache;

/**
 * Wrapper around Infinispan RemoteCache.
 * 
 */
public class CacheRemoteImpl implements Cache {

    private RemoteCache cache;

    public CacheRemoteImpl(RemoteCache cache) {
        this.cache = cache;
    }

    @Override
    public Object get(Object key) {
        return cache.get(key);
    }

    @Override
    public String getName() {
        return cache.getName();
    }

    @Override
    public void invalidate(Object key) {
        cache.removeAsync(key);
    }

    @Override
    public void invalidateAll() {
        cache.clearAsync();
    }

    @Override
    public void put(Object key, Object value) {
        cache.put(key, value);        
    }
    
    

}
