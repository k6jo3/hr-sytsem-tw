package com.company.hrms.common.test.contract;

/**
 * 必要欄位定義
 */
public class RequiredField {
    private String name;
    private String type; // uuid, string, integer, decimal, boolean, date, datetime, email, phone, etc.
    private String format; // masked, etc.
    private Integer precision; // For decimal type
    private Boolean notNull;
    private Object value; // 期望值（用於驗證欄位值是否符合預期）

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
