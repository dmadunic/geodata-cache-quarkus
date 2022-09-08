package com.ag04.quarkus.cache.annotations;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ag04.quarkus.cache.CacheKeyGenerator;
import com.ag04.quarkus.cache.UndefinedCacheKeyGenerator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD})
public @interface CacheResult {
    /**
     * The name of the cache.
     */
    @Nonbinding String cacheName();

    /**
     * The {@link CacheKeyGenerator} implementation to use to generate a cache key.
     */
    @Nonbinding
    Class<? extends CacheKeyGenerator> keyGenerator() default UndefinedCacheKeyGenerator.class;
}
