package com.ag04.quarkus.cache.interceptor;
import org.jboss.logging.Logger;

import com.ag04.quarkus.cache.Cache;
import com.ag04.quarkus.cache.DefaultCacheKey;
import com.ag04.quarkus.cache.annotations.CacheInvalidate;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Priority(1000)
@CacheInvalidate(cacheName = "")
public class CacheInvalidateInterceptor extends BaseCacheInterceptor {

    @Inject
    Logger log;

    @AroundInvoke
    <T> Object intercept(InvocationContext context) throws Exception {
        CacheInvalidate cacheAnnotation = context.getMethod().getAnnotation(CacheInvalidate.class);
        String cacheName = cacheAnnotation.cacheName();
        Class keyGenerator = cacheAnnotation.keyGenerator();
                
        Object key = getCacheKey(cacheName, keyGenerator, context);
        Cache cache = cacheManager.getCache(cacheName);
        
        if (cache == null) {
            log.warnf("(SKIPPING) No cache with name '%s' found >> Skipping INVALIDATE operation", cacheName);
            return context.proceed();
        }

        if (key != null) {
            if (key instanceof DefaultCacheKey) {
                log.infof("Clearing entire cache [%s]", cacheName);
                cache.invalidateAll();
            } else {
                log.infof("Removing entry with key [%s] from cache [%s]", key, cacheName);
                cache.invalidate(key);
            }
    
        }
        return context.proceed();
    }
    
}
