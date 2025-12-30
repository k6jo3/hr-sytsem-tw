package com.company.hrms.common.query;

import java.io.Serializable;
import java.util.Objects;

/**
 * 聚合欄位定義
 * 表示單一聚合運算: function(field) as alias
 *
 * <p>使用範例:</p>
 * <pre>
 * // SUM(hours) AS totalHours
 * AggregateField.sum("hours", "totalHours");
 *
 * // COUNT(DISTINCT employeeId) AS headCount
 * AggregateField.countDistinct("employeeId", "headCount");
 * </pre>
 */
public class AggregateField implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 欄位路徑，如 "amount" 或 "timesheet.hours" */
    private final String field;

    /** 聚合函數 */
    private final AggregateFunction function;

    /** 結果別名 */
    private final String alias;

    public AggregateField(String field, AggregateFunction function, String alias) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.function = Objects.requireNonNull(function, "function cannot be null");
        this.alias = alias != null ? alias : field;
    }

    // ========== 靜態工廠方法 ==========

    /**
     * 建立 COUNT 聚合
     */
    public static AggregateField count(String field, String alias) {
        return new AggregateField(field, AggregateFunction.COUNT, alias);
    }

    /**
     * 建立 COUNT 聚合 (使用欄位名作為別名)
     */
    public static AggregateField count(String field) {
        return count(field, field + "Count");
    }

    /**
     * 建立 COUNT DISTINCT 聚合
     */
    public static AggregateField countDistinct(String field, String alias) {
        return new AggregateField(field, AggregateFunction.COUNT_DISTINCT, alias);
    }

    /**
     * 建立 COUNT DISTINCT 聚合 (使用欄位名作為別名)
     */
    public static AggregateField countDistinct(String field) {
        return countDistinct(field, field + "Count");
    }

    /**
     * 建立 SUM 聚合
     */
    public static AggregateField sum(String field, String alias) {
        return new AggregateField(field, AggregateFunction.SUM, alias);
    }

    /**
     * 建立 SUM 聚合 (使用欄位名作為別名)
     */
    public static AggregateField sum(String field) {
        return sum(field, field + "Sum");
    }

    /**
     * 建立 AVG 聚合
     */
    public static AggregateField avg(String field, String alias) {
        return new AggregateField(field, AggregateFunction.AVG, alias);
    }

    /**
     * 建立 AVG 聚合 (使用欄位名作為別名)
     */
    public static AggregateField avg(String field) {
        return avg(field, field + "Avg");
    }

    /**
     * 建立 MAX 聚合
     */
    public static AggregateField max(String field, String alias) {
        return new AggregateField(field, AggregateFunction.MAX, alias);
    }

    /**
     * 建立 MAX 聚合 (使用欄位名作為別名)
     */
    public static AggregateField max(String field) {
        return max(field, field + "Max");
    }

    /**
     * 建立 MIN 聚合
     */
    public static AggregateField min(String field, String alias) {
        return new AggregateField(field, AggregateFunction.MIN, alias);
    }

    /**
     * 建立 MIN 聚合 (使用欄位名作為別名)
     */
    public static AggregateField min(String field) {
        return min(field, field + "Min");
    }

    // ========== Getters ==========

    public String getField() {
        return field;
    }

    public AggregateFunction getFunction() {
        return function;
    }

    public String getAlias() {
        return alias;
    }

    /**
     * 檢查是否為巢狀欄位 (包含點號)
     */
    public boolean isNestedField() {
        return field.contains(".");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateField that = (AggregateField) o;
        return Objects.equals(field, that.field) &&
               function == that.function &&
               Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, function, alias);
    }

    @Override
    public String toString() {
        return String.format("%s(%s) AS %s", function.name(), field, alias);
    }
}
