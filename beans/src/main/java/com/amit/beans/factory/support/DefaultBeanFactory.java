package com.amit.beans.factory.support;

import com.amit.beans.definition.BeanDefinition;
import com.amit.beans.factory.BeanFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DefaultBeanFactory implements BeanFactory {

    private final Map<Class<?>, BeanDefinition> beanDefinition = new HashMap<>();
    private final Map<Class<?>, Object> singleObjects = new HashMap<>();

    public void registerBeanDefinition(Class<?> type, BeanDefinition definition) {
        beanDefinition.put(type, definition);
    }
    @Override
    public <T> T getBean(Class<T> type) {
        if (singleObjects.containsKey(type)) {
            return type.cast(singleObjects.get(type));
        }
        BeanDefinition bd = beanDefinition.get(type);
        if (Objects.isNull(bd)){
            throw new RuntimeException("No bean definition for " + type.getName());
        }
        Object bean = createBean(type, bd);
        if (bd.isSingleton()){
            singleObjects.put(type, bean);
        }
        return type.cast(bean);
    }

    private Object createBean(Class<?> type, BeanDefinition bd) {
        try{
            Constructor<?> constructor = type.getDeclaredConstructor();
            return constructor.newInstance();
        }catch (Exception e) {
            throw new RuntimeException("Failed to create bean " + type.getName(), e);
        }
    }
}
