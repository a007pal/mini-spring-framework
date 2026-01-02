package com.amit.beans.definition;

public class PropertyDependency {

    private final String fieldName;
    private final Class<?> type;
    private final boolean isRef;

    public PropertyDependency(String fieldName, Class<?> type, boolean isRef) {
        this.fieldName = fieldName;
        this.type = type;
        this.isRef = isRef;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isRef() {
        return isRef;
    }
}
