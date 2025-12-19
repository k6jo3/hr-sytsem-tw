package com.company.hrms.common.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查詢過濾條件註解
 * 標註在 DTO 欄位上，用於自動建構 QueryGroup
 *
 * 使用範例:
 * <pre>
 * public class EmployeeSearchRequest {
 *     @QueryFilter(operator = Operator.LIKE)
 *     private String name;
 *
 *     @QueryFilter(property = "department.id", operator = Operator.EQ)
 *     private String deptId;
 *
 *     @QueryFilter(property = "status", operator = Operator.EQ)
 *     private String status;
 * }
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryFilter {

    /**
     * 對應的資料庫屬性路徑
     * 若未指定，則使用欄位名稱
     * 支援巢狀路徑，如 "department.name"
     */
    String property() default "";

    /**
     * 運算子類型
     * 預設為 EQ (等於)
     */
    Operator operator() default Operator.EQ;
}
