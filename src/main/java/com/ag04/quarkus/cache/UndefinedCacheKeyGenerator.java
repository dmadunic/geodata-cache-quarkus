package com.ag04.quarkus.cache;
import java.lang.reflect.Method;

/**
 * This {@link CacheKeyGenerator} implementation is ignored by {@link CacheInterceptor} when a cache key is computed.
 * 
 * This file is taken from: https://github.com/quarkusio/quarkus/blob/main/extensions/cache/runtime/src/main/java/io/quarkus/cache/runtime/UndefinedCacheKeyGenerator.java
 */
public class UndefinedCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public Object generate(Method method, Object... methodParams) {
        throw new UnsupportedOperationException("This cache key generator should never be invoked");
    }
}
