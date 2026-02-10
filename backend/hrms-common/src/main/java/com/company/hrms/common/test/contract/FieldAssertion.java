package com.company.hrms.common.test.contract;

/**
 * 欄位斷言
 */
public class FieldAssertion {
    private String field;
    private String operator; // equals, notEquals, contains, notContains, greaterThan, lessThan, etc.
    private Object value;

    // Getters and Setters
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
