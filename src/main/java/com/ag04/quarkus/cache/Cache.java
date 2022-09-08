package com.ag04.quarkus.cache;

/**
 * Abstraction for different Cache implementations (Redis, Infinispan, ...)
 * 
 */
public interface Cache {
    
    /**
     * Returns the cache name.
     *
     * @return cache name
     */
    String getName();

     /**
     * Removes all entries from the cache.
     */
    void invalidateAll();

     /**
     * Removes the cache entry identified by {@code key} from the cache. If the key does not identify any cache entry, nothing
     * will happen.
     *
     * @param key cache key
     * @throws NullPointerException if the key is {@code null}
     */
    void invalidate(Object key);

    /**
     * Removes the cache entry identified by {@code key} from the cache. If the key does not identify any cache entry, nothing
     * will happen.
     *
     * @param key cache key
     * @throws NullPointerException if the key is {@code null}
     */
    Object get(Object key);

    /**
     *  
     * @param key key to use
     * @param value value to store
     */
    void put(Object key, Object value);

}
