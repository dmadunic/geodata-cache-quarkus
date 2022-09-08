package com.ag04.geodata.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ag04.quarkus.cache.CacheException;
import com.ag04.quarkus.cache.CacheKeyGenerator;

/**
 * This CacheKeyGenerator invokes getId() method on the first argument of the method.
 * 
 * @author dmadunic
 */
public class GetIdCacheKeyGenerator implements CacheKeyGenerator{
    public static final String GET_ID_METHOD_NAME = "getId";

    @Override
    public Object generate(Method method, Object... methodParams) {
        Method getIdmethod = null;
        Object targetEntity = methodParams[0];
        Object id = null;

        try {
            getIdmethod = targetEntity.getClass().getMethod(GET_ID_METHOD_NAME);
        } catch (SecurityException e) {
            new CacheException("Failed to get getId method from target object", e);
        } catch (NoSuchMethodException e) {
            new CacheException("No method named getId found on target object", e);
        }

        try {
            id = getIdmethod.invoke(targetEntity);
        } catch (IllegalArgumentException e) {
            new CacheException("Failed to invoke getId() method from target object", e);
        } catch (IllegalAccessException e) {
            new CacheException("Failed to invoke getId() method from target object", e);
        } catch (InvocationTargetException e) {
            new CacheException("Failed to invoke getId() method from target object", e);
        }
        return id;
    }
}
