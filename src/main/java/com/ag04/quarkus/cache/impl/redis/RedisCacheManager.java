package com.ag04.quarkus.cache.impl.redis;

import org.jboss.logging.Logger;
import java.util.HashMap;
import java.util.Map;

import org.redisson.api.RedissonClient;

import com.ag04.quarkus.cache.Cache;
import com.ag04.quarkus.cache.CacheManager;

/**
 * 
 * 
 */
public class RedisCacheManager implements CacheManager {
    private final Logger log = Logger.getLogger(RedisCacheManager.class);

    private Map<String, RedisCache> caches = new HashMap<>();

    private final RedissonClient redisson;
    private final Long defaultTtl;

    public RedisCacheManager(RedissonClient redisson, Long defaultTtl) {
        this.redisson = redisson;
        this.defaultTtl = defaultTtl;
    }

    @Override
    public Cache getCache(String name) {
        RedisCache cache = caches.get(name);
        if (cache == null) {
            cache = createCache(name, defaultTtl);
        }
        return cache;
    }

    @Override
    public boolean cacheExists(String name) {
        if (caches.containsKey(name)) {
            return true;
        }
        return false;
    }

    public RedisCache createCache(String name, Long ttl) {
        RedisCache cache = new RedisCache(redisson, name, ttl);
        caches.put(name, cache);
        log.infof("--> CREATED Cache '%s' with ttl of %d(s)", name, ttl);
        return cache;
    }
    
}
