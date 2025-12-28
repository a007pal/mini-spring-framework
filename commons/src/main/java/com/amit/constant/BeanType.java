package com.amit.constant;

public enum BeanType {
    SINGLETON("singleton"),
    PROTOTYPE("prototype");
    private final String value;
    BeanType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
    public String getValue(){
        return value;
    }
}
