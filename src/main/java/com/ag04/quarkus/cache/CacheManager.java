package com.ag04.quarkus.cache;

public interface CacheManager {
    
     /**
     * Gets the cache identified by the given name.
     *
     * @param name cache name
     * @return an identified cache if it exists, or {@code null} otherwise
     */
    Cache getCache(String name);

    /**
     * 
     * @param name
     * @return true if cache with the specified name exists or false otherwise.
     */
    boolean cacheExists(String name);

}
