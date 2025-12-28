package com.amit.beans.definition;

import com.amit.constant.BeanType;

public class BeanDefinition {

    private final Class<?> beanClass;
    private final String scope;

    public BeanDefinition(Class<?> beanClass, String scope) {
        this.beanClass = beanClass;
        this.scope = scope;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }
    public boolean isSingleton(){
        return BeanType.SINGLETON.toString().equals(scope);
    }
}
