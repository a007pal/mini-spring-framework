package com.amit.beans.factory.support;

import com.amit.core.annotation.Autowired;
import com.amit.beans.definition.BeanDefinition;
import com.amit.beans.factory.BeanFactory;
import com.amit.beans.singleton.DefaultSingletonBeanRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;


public class DefaultBeanFactory implements BeanFactory {

    private final Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();

    private final DefaultSingletonBeanRegistry singletonBeanRegistry = new DefaultSingletonBeanRegistry();

    private final ThreadLocal<Set<Class<?>>> singletonsCurrentlyInCreation = ThreadLocal.withInitial(HashSet::new);

    public void registerBeanDefinition(Class<?> type, BeanDefinition definition) {
        beanDefinitions.put(type, definition);
    }
    @Override
    public <T> T getBean(Class<T> type) {
        BeanDefinition bd = beanDefinitions.get(type);
        if (Objects.isNull(bd)) {
            throw new RuntimeException("No Bean Definition for "+type.getName());
        }
        if (bd.isSingleton()){
            Object singleton = singletonBeanRegistry.getOrCreateSingleton(type, ()->createSingletonBean(type,bd));

            return type.cast(singleton);
        }

        return type.cast(createBean(bd));
    }

    private Object createSingletonBean(Class<?> beanClass, BeanDefinition bd){
        beforeSingletonCreation(beanClass);
        try {
            return createBean(bd);
        }finally {
            afterSingletonCreation(beanClass);
        }
    }

    private Object createBean(BeanDefinition bd) {
        try{
            Class<?> clazz = bd.getBeanClass();
            Constructor<?> constructor = resolveConstructor(clazz);
            Object[] args = Arrays.stream(constructor.getParameterTypes())
                    .map(this::getBean)
                    .toArray();
            Object bean = constructor.newInstance(args);
            injectFields(bean, clazz);
            return bean;
        }catch (Exception e) {
            throw new RuntimeException("Failed to create bean " + bd.getBeanClass().getName(), e);
        }
    }

    private Constructor<?> resolveConstructor(Class<?> clazz) {
        Constructor<?> [] constructors = clazz.getConstructors();
        if (constructors.length == 0){
            throw new RuntimeException("No public constructor found for " + clazz.getName());
        }
        return constructors[0];
    }

    private void injectFields(Object bean,Class<?> clazz) throws  IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)){
                field.setAccessible(true);
                Object dependency = getBean(field.getType());
                field.set(bean,dependency);
            }
        }

    }

    private void beforeSingletonCreation(Class<?> beanClass){
        Set<Class<?>> creating = singletonsCurrentlyInCreation.get();
        if (!creating.add(beanClass)){
            throw new RuntimeException(
                    "Circular dependency detected while creating singleton bean: "
                            + beanClass.getName()
            );
        }
    }
    private void afterSingletonCreation(Class<?> beanClass) {
        singletonsCurrentlyInCreation.get().remove(beanClass);
    }
}
