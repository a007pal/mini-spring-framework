package com.amit.beans.factory;


public interface BeanFactory {
    <T> T getBean(Class<T> type);
}
