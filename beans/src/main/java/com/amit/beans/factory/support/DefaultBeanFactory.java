package com.amit.beans.factory.support;

import com.amit.beans.factory.BeanPostProcessor;
import com.amit.core.annotation.Autowired;
import com.amit.beans.definition.BeanDefinition;
import com.amit.beans.factory.BeanFactory;
import com.amit.beans.singleton.DefaultSingletonBeanRegistry;
import com.amit.core.annotation.PostConstruct;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class DefaultBeanFactory implements BeanFactory {

    private final Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();

    private final DefaultSingletonBeanRegistry singletonBeanRegistry = new DefaultSingletonBeanRegistry();

    private final ThreadLocal<Set<Class<?>>> singletonsCurrentlyInCreation = ThreadLocal.withInitial(HashSet::new);

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

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
        }else {
            return type.cast(createBean(bd));
        }


    }

    public void addBeanPostProcessor(BeanPostProcessor bpp){
        this.beanPostProcessors.add(bpp);
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
           Object bean = instantiateBean(bd);
           populateProperties(bean, bd);
           bean = initializeBean(bean, bd);
            return bean;
        }catch (Exception e) {
            throw new RuntimeException("Failed to create bean " + bd.getBeanClass().getName(), e);
        }
    }


    private Object instantiateBean(BeanDefinition bd) throws Exception{
        Class<?> clazz = bd.getBeanClass();
        Constructor<?> constructor = resolveConstructor(clazz);
        Object[] args = Arrays.stream(constructor.getParameterTypes())
                .map(this::getBean)
                .toArray();
        return constructor.newInstance(args);
    }

    private void populateProperties(Object bean,BeanDefinition bd) throws  IllegalAccessException {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)){
                field.setAccessible(true);
                Object dependency = getBean(field.getType());
                field.set(bean,dependency);
            }
        }

    }
    private  Object initializeBean(Object bean, BeanDefinition bd) {
        invokePostConstruct(bean);
        bean = applyBeanPostProcessorsBeforeInitialization(bean, bd.getBeanClass().getName());
        bean = applyBeanPostProcessorsAfterInitialization(bean, bd.getBeanClass().getName());
        return bean;

    }
    private Constructor<?> resolveConstructor(Class<?> clazz) {
        Constructor<?> [] constructors = clazz.getConstructors();
        if (constructors.length == 0){
            throw new RuntimeException("No public constructor found for " + clazz.getName());
        }
        return constructors[0];
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
        Set<Class<?>> creating = singletonsCurrentlyInCreation.get();
        if (Objects.nonNull(creating)){
            creating.remove(beanClass);
        }

    }

    private void invokePostConstruct(Object bean) {
        Class<?> clazz = bean.getClass();
        for (Method method : clazz.getDeclaredMethods()){
            if (method.isAnnotationPresent(PostConstruct.class)){
                if(method.getParameterCount()!=0){
                    throw new RuntimeException("@PostConstruct method must have no arguments: "
                            + method.getName());
                }
                try {
                    method.setAccessible(true);
                    method.invoke(bean);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to invoke @PostConstruct method: "
                            + method.getName(),e);
                }
            }
        }
    }

    private Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName){
        Object result = bean;
        for (BeanPostProcessor bpp : beanPostProcessors){
            result = bpp.postProcessBeforeInitialization(result, beanName);
        }
        return result;
    }

    private Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName){
        Object result = bean;
        for (BeanPostProcessor bpp : beanPostProcessors){
            result = bpp.postProcessAfterInitialization(result, beanName);
        }
        return result;
    }
}
