package com.amit.beans.definition;

import com.amit.constant.BeanType;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinition {

    private final Class<?> beanClass;
    private final BeanType scope;

    private final List<ConstructorArg> constructorArgs = new ArrayList<>();
    private final List<PropertyDependency> propertyDependencies = new ArrayList<>();

    private  String initMethod;
    private String destroyMethod;

    private boolean lazyInit = false;


    public BeanDefinition(Class<?> beanClass, BeanType scope) {
        this.beanClass = beanClass;
        this.scope = scope;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }
    public BeanType getScope() {
        return scope;
    }
    public boolean isSingleton(){
        return BeanType.SINGLETON == scope;
    }
    public boolean isProtoType() {
        return BeanType.PROTOTYPE == scope;
    }

    public void addConstructorArg(ConstructorArg arg) {
        this.constructorArgs.add(arg);
    }
    public List<ConstructorArg> getConstructorArgs() {
        return constructorArgs;
    }

    public void addPropertyDependency(PropertyDependency dependency) {
        this.propertyDependencies.add(dependency);
    }

    public List<PropertyDependency> getPropertyDependencies() {
        return propertyDependencies;
    }

    public String getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public String getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
}
