package com.company.hrms.common.infrastructure.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表格元資料註解
 * 定義於 Entity 類別上，用於 Native Batch Insert
 *
 * <p>使用範例:</p>
 * <pre>
 * &#64;Entity
 * &#64;TableMeta(
 *     name = "employees",
 *     columns = {"id", "name", "email", "department_id", "created_at"},
 *     fields = {"id", "name", "email", "departmentId", "createdAt"}
 * )
 * public class Employee {
 *     private String id;
 *     private String name;
 *     private String email;
 *     private String departmentId;
 *     private LocalDateTime createdAt;
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableMeta {

    /**
     * 資料表名稱
     */
    String name();

    /**
     * 資料庫欄位名稱 (按順序對應 fields)
     */
    String[] columns();

    /**
     * Java 欄位名稱 (按順序對應 columns)
     */
    String[] fields();
}
