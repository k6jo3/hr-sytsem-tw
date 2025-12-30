package com.company.hrms.common.query;

import java.io.Serializable;
import java.util.Objects;

/**
 * 過濾條件單元
 * 表示單一查詢條件: field operator value
 * 例如: status = 'ACTIVE', name LIKE '%王%'
 */
public class FilterUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 欄位名稱，支援巢狀路徑如 "department.name" */
    private final String field;

    /** 運算子 */
    private final Operator op;

    /** 值 */
    private final Object value;

    public FilterUnit(String field, Operator op, Object value) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.op = Objects.requireNonNull(op, "operator cannot be null");
        this.value = value;
    }

    /**
     * 建立等於條件
     */
    public static FilterUnit eq(String field, Object value) {
        return new FilterUnit(field, Operator.EQ, value);
    }

    /**
     * 建立不等於條件
     */
    public static FilterUnit ne(String field, Object value) {
        return new FilterUnit(field, Operator.NE, value);
    }

    /**
     * 建立模糊查詢條件
     */
    public static FilterUnit like(String field, String value) {
        return new FilterUnit(field, Operator.LIKE, value);
    }

    /**
     * 建立 IN 條件
     */
    public static FilterUnit in(String field, Object... values) {
        return new FilterUnit(field, Operator.IN, values);
    }

    /**
     * 建立 NOT IN 條件
     */
    public static FilterUnit notIn(String field, Object... values) {
        return new FilterUnit(field, Operator.NOT_IN, values);
    }

    /**
     * 建立 BETWEEN 條件
     * @param field 欄位名稱
     * @param start 起始值 (包含)
     * @param end 結束值 (包含)
     */
    public static FilterUnit between(String field, Object start, Object end) {
        return new FilterUnit(field, Operator.BETWEEN, new Object[]{start, end});
    }

    /**
     * 建立 IS NULL 條件
     */
    public static FilterUnit isNull(String field) {
        return new FilterUnit(field, Operator.IS_NULL, null);
    }

    /**
     * 建立 IS NOT NULL 條件
     */
    public static FilterUnit isNotNull(String field) {
        return new FilterUnit(field, Operator.IS_NOT_NULL, null);
    }

    /**
     * 建立大於條件
     */
    public static FilterUnit gt(String field, Object value) {
        return new FilterUnit(field, Operator.GT, value);
    }

    /**
     * 建立大於等於條件
     */
    public static FilterUnit gte(String field, Object value) {
        return new FilterUnit(field, Operator.GTE, value);
    }

    /**
     * 建立小於條件
     */
    public static FilterUnit lt(String field, Object value) {
        return new FilterUnit(field, Operator.LT, value);
    }

    /**
     * 建立小於等於條件
     */
    public static FilterUnit lte(String field, Object value) {
        return new FilterUnit(field, Operator.LTE, value);
    }

    public String getField() {
        return field;
    }

    public Operator getOp() {
        return op;
    }

    public Object getValue() {
        return value;
    }

    /**
     * 檢查是否為巢狀欄位 (包含點號)
     */
    public boolean isNestedField() {
        return field.contains(".");
    }

    /**
     * 取得巢狀路徑的各部分
     */
    public String[] getFieldParts() {
        return field.split("\\.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterUnit that = (FilterUnit) o;
        return Objects.equals(field, that.field) &&
               op == that.op &&
               Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, op, value);
    }

    @Override
    public String toString() {
        if (op == Operator.IS_NULL || op == Operator.IS_NOT_NULL) {
            return String.format("%s %s", field, op.getSymbol());
        }
        return String.format("%s %s '%s'", field, op.getSymbol(), value);
    }
}
