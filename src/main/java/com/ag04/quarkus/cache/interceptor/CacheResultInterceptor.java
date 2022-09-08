package com.ag04.quarkus.cache.interceptor;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.exceptions.TransportException;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.jboss.logging.Logger;

import com.ag04.quarkus.cache.Cache;
import com.ag04.quarkus.cache.CacheManager;
import com.ag04.quarkus.cache.annotations.CacheResult;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.Optional;

/**
 * 
 * @author dmadunic
 */
@Interceptor
@Priority(BaseCacheInterceptor.BASE_PRIORITY + 2)
@CacheResult(cacheName = "")
public class CacheResultInterceptor extends BaseCacheInterceptor {
    protected static final String OPTIONAL_CLASSNAME = "java.util.Optional"; 

    @Inject
    Logger log;

    @AroundInvoke
    <T> Object intercept(InvocationContext context) throws Throwable {
        CacheResult cachedAnnotation = context.getMethod().getAnnotation(CacheResult.class);
        String cacheName = cachedAnnotation.cacheName();
        Class keyGenerator = cachedAnnotation.keyGenerator();
                
        Object key = getCacheKey(cacheName, keyGenerator, context);
        log.debugf("Loading entry with key [%s] from cache [%s]", key, cacheName);

        String resultType = context.getMethod().getReturnType().getCanonicalName();

        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warnf("(SKIPPING) No cache with name '%s' found >> NO VALUE will be CACHED", cacheName);
        } else {
            Object cachedValue = cache.get(key);
            if (cachedValue != null) {
                log.infof("Cache hit for key: %s", key);
                if (OPTIONAL_CLASSNAME.equals(resultType)) {
                    return Optional.of(cachedValue);
                }
                return cachedValue;
            }
        }

        log.infof("Cache miss for key: %s", key);
        Object data = context.proceed();
        if (data != null) {
            if (OPTIONAL_CLASSNAME.equals(resultType)) {
                Optional opVal = (Optional) data;
                if (!opVal.isEmpty()) {
                    cache.put(key, opVal.get());
                }
            } else {
                cache.put(key, data);
            }
        }
        return data;
    }

}