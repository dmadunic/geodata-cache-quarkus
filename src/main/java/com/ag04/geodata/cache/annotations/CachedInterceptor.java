package com.ag04.geodata.cache.annotations;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.exceptions.TransportException;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.jboss.logging.Logger;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Interceptor
@Priority(1000)
@Cached(cacheName = "")
public class CachedInterceptor {
    protected static final String OPTIONAL_CLASSNAME = "java.util.Optional"; 

    @Inject
    RemoteCacheManager remoteCacheManager;

    @Inject
    Logger log;

    @ConfigProperty(name = "cache")
    String cacheConfiguration;

    @AroundInvoke
    <T> Object checkCache(InvocationContext context) throws Exception {
        Cached cachedAnnotation = context.getMethod().getAnnotation(Cached.class);
        String cacheName = cachedAnnotation.cacheName();
        String key = String.valueOf(context.getParameters()[0]);

        String resultType = context.getMethod().getReturnType().getCanonicalName();

        if (!cacheExists(cacheName)) {
            remoteCacheManager.administration().createCache(cacheName,
                    new XMLStringConfiguration(cacheConfiguration.replace("<name-override>", cacheName))
            );
        }
        Object cachedValue = getValue(cacheName, key);
        if (cachedValue != null) {
            log.infof("Cache hit for key: %s", key);
            if (OPTIONAL_CLASSNAME.equals(resultType)) {
                return Optional.of(cachedValue);
            }
            return cachedValue;
        } else {
            log.infof("Cache miss for key: %s", key);
            Object data = context.proceed();
            if (OPTIONAL_CLASSNAME.equals(resultType)) {
                putValue(cacheName, key, ((Optional)data).get());
            } else {
                putValue(cacheName, key, data);
            }
            return data;
        }
    }

    private void putValue(String cacheName, String key, Object value) {
        log.debugf("Setting cached value for key: %s with value: %s", key, value);
        remoteCacheManager.getCache(cacheName).putAsync(key, value, 60, TimeUnit.SECONDS);
    }

    private Object getValue(String cacheName, Object key) {
        log.debugf("Check cache for key: %s", key);
        return remoteCacheManager.getCache(cacheName).get(key);
    }

    private boolean cacheExists(String cacheName) {
        try {
            if (remoteCacheManager.getCache(cacheName) == null) {
                log.warnf("Cache %s not present.", cacheName);
                return false;
            }
            return true;
        } catch (TransportException ex) {
            log.errorf("Error while trying to connect to the remote cache: %s", ex.getCause());
            return false;
        }
    }
}