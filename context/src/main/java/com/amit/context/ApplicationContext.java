package com.amit.context;

import com.amit.core.scanner.ClassScanner;
import com.amit.beans.definition.BeanDefinition;
import com.amit.beans.factory.support.DefaultBeanFactory;
import com.amit.constant.BeanType;

public class ApplicationContext {
    private final DefaultBeanFactory beanFactory;

    private final ClassScanner classScanner;
    public ApplicationContext(String basePackage){
        this.beanFactory = new DefaultBeanFactory();
        this.classScanner = new ClassScanner();

    }

    public void refresh(String basePackage){
        for (Class<?> clazz : classScanner.scan(basePackage)){
            registerBeanDefinition(clazz);
        }
    }

    private void registerBeanDefinition(Class<?> clazz) {
        BeanDefinition bd = new BeanDefinition(clazz, BeanType.SINGLETON);
        beanFactory.registerBeanDefinition(clazz, bd);
    }
    public <T> T getBean(Class<T> type){
        return beanFactory.getBean(type);
    }
}
