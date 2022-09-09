package com.ag04.quarkus.cache.impl.redis;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RedissonClient;

import com.ag04.quarkus.cache.Cache;


public class RedisCache implements Cache {

    private final RedissonClient redisson;
    private final String name;
    private final Long ttl;

    public RedisCache(RedissonClient redisson, String name, Long ttl) {
        this.redisson = redisson;
        this.name = name;
        this.ttl = ttl;
    }

    @Override
    public void put(Object key, Object value) {
        redisson.getMapCache(name).put(key, value, ttl, TimeUnit.SECONDS);
    }

    @Override
    public Object get(Object key) {
        return redisson.getMapCache(name).get(key);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void invalidate(Object key) {
        redisson.getMapCache(name).remove(key);
    }

    @Override
    public void invalidateAll() {
        redisson.getMapCache(name).clear();        
    }
    
}
