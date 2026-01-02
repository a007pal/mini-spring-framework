package com.amit.beans.definition;

public class ConstructorArg {

    private final Class<?> type;
    private final boolean isRef;
    private final Object value;

    public ConstructorArg(Class<?> type, boolean isRef, Object value) {
        this.type = type;
        this.isRef = isRef;
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isRef() {
        return isRef;
    }

    public Object getValue() {
        return value;
    }
}
