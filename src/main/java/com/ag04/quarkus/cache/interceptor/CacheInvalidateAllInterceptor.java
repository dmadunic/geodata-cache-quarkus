package com.ag04.quarkus.cache.interceptor;
import org.jboss.logging.Logger;

import javax.interceptor.Interceptor;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import com.ag04.quarkus.cache.Cache;
import com.ag04.quarkus.cache.annotations.CacheInvalidateAll;


@Interceptor
@Priority(1000)
@CacheInvalidateAll(cacheName = "")
public class CacheInvalidateAllInterceptor extends BaseCacheInterceptor {

    @Inject
    Logger log;

    @AroundInvoke
    <T> Object intercept(InvocationContext context) throws Exception {
        CacheInvalidateAll cacheAnnotation = context.getMethod().getAnnotation(CacheInvalidateAll.class);
        String cacheName = cacheAnnotation.cacheName();

        Cache cache = cacheManager.getCache(cacheName);
        
        if (cache == null) {
            log.warnf("(SKIPPING) No cache with name '%s' found >> Skipping INVALIDATE operation", cacheName);
            return context.proceed();
        }

        log.debugf("Clearing entire cache [%s]", cacheName);
        cache.invalidateAll();
    
        return context.proceed();
    }
}
