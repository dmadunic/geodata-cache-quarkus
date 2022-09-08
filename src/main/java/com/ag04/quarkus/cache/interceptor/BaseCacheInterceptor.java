package com.ag04.quarkus.cache.interceptor;

import org.jboss.logging.Logger;

import javax.interceptor.Interceptor.Priority;

import java.lang.reflect.InvocationTargetException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.reflect.Method;
import javax.interceptor.InvocationContext;

import com.ag04.quarkus.cache.CompositeCacheKey;
import com.ag04.quarkus.cache.DefaultCacheKey;
import com.ag04.quarkus.cache.CacheKeyGenerator;
import com.ag04.quarkus.cache.CacheManager;
import com.ag04.quarkus.cache.UndefinedCacheKeyGenerator;
import com.ag04.quarkus.cache.CacheException;

public abstract class BaseCacheInterceptor {
    private static final Logger log = Logger.getLogger(BaseCacheInterceptor.class);

    public static final int BASE_PRIORITY = Priority.PLATFORM_BEFORE;

    @Inject
    CacheManager cacheManager;
    
    @Inject
    Instance<CacheKeyGenerator> keyGenerator;

    protected Object getCacheKey(
        String cacheName, 
        Class<? extends CacheKeyGenerator> keyGeneratorClass, 
        InvocationContext context
    ) {
        //
        Object[] methodParameterValues = context.getParameters();

        if (keyGeneratorClass != UndefinedCacheKeyGenerator.class) {
            return generateKey(keyGeneratorClass, context.getMethod(), methodParameterValues);
        } else if (methodParameterValues == null || methodParameterValues.length == 0) {
            // If the intercepted method doesn't have any parameter, then the default cache key will be used.
            return getDefaultKey(cacheName);
        } else if (methodParameterValues.length == 1) {
            // If the intercepted method has exactly one parameter, then this parameter will be used as the cache key.
            return methodParameterValues[0];
        } else {
            // If the intercepted method has two or more parameters, then a composite cache key built from all these parameters
            // will be used.
            return new CompositeCacheKey(methodParameterValues);
        }
    }

    private Object getDefaultKey(String cacheName) {
        return new DefaultCacheKey(cacheName);
    }

    private <T extends CacheKeyGenerator> Object generateKey(Class<T> keyGeneratorClass, Method method,
            Object[] methodParameterValues) {
        Instance<T> keyGenInstance = keyGenerator.select(keyGeneratorClass);
        if (keyGenInstance.isResolvable()) {
            log.tracef("Using cache key generator bean from Arc [class=%s]", keyGeneratorClass.getName());
            T keyGen = keyGenInstance.get();
            try {
                return keyGen.generate(method, methodParameterValues);
            } finally {
                keyGenerator.destroy(keyGen);
            }
        } else {
            try {
                log.tracef("Creating a new cache key generator instance [class=%s]", keyGeneratorClass.getName());
                return keyGeneratorClass.getConstructor().newInstance().generate(method, methodParameterValues);
            } catch (NoSuchMethodException e) {
                // This should never be thrown because the default constructor availability is checked at build time.
                throw new CacheException("No default constructor found in cache key generator [class="
                        + keyGeneratorClass.getName() + "]", e);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new CacheException("Cache key generator instantiation failed", e);
            }
        }
    }

}
