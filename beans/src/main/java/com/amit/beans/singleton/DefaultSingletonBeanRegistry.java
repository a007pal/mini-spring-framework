package com.amit.beans.singleton;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class DefaultSingletonBeanRegistry {

    private final Map<Class<?>, Object> singletonObjects = new ConcurrentHashMap<>();

    private final Object singletonCreationLock = new Object();

    public Object getSingletonObject(Class<?> type){
        return singletonObjects.get(type);
    }

    public Object getOrCreateSingleton(Class<?> beanClass, Supplier<Object> singletonFactory) {
        Object singleTon = singletonObjects.get(beanClass);
        if(Objects.nonNull(singleTon)){
            return singleTon;
        }
        synchronized (singletonCreationLock){
            singleTon = singletonObjects.get(beanClass);
            if (Objects.isNull(singleTon)) {
                singleTon = singletonFactory.get();
                singletonObjects.put(beanClass, singleTon);
            }
            return singleTon;
        }
    }
}
