package com.company.hrms.common.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查詢條件註解集合
 * 提供宣告式查詢條件定義
 *
 * <p>使用範例:</p>
 * <pre>
 * public class EmployeeSearchCondition {
 *
 *     &#64;EQ
 *     private String employeeId;      // employeeId = ?
 *
 *     &#64;LIKE
 *     private String name;            // name LIKE '%?%'
 *
 *     &#64;GTE("hireDate")
 *     private LocalDate startDate;    // hireDate >= ?
 *
 *     &#64;LTE("hireDate")
 *     private LocalDate endDate;      // hireDate <= ?
 *
 *     &#64;IN("status")
 *     private List&lt;String&gt; statuses;  // status IN (?, ?, ?)
 *
 *     &#64;EQ
 *     &#64;OR
 *     private String department;      // OR department = ?
 *
 *     &#64;EQ
 *     &#64;ORGroup("group1")
 *     private String role;            // (role = ? AND level = ?) 作為 OR 子群組
 *
 *     &#64;GTE
 *     &#64;ORGroup("group1")
 *     private Integer level;
 * }
 *
 * // 使用方式
 * QueryGroup group = QueryBuilder.fromCondition(condition);
 * List&lt;Employee&gt; result = repository.findAll(group);
 * </pre>
 */
public interface QueryCondition {

    // ==================== 運算子註解 ====================

    /**
     * 等於條件 (=)
     * <pre>
     * &#64;EQ
     * private String status;  // status = ?
     *
     * &#64;EQ("department.id")
     * private String deptId;  // department.id = ?
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface EQ {
        /** 對應的資料庫屬性路徑，若未指定則使用欄位名稱 */
        String value() default "";
    }

    /**
     * 不等於條件 (<>)
     * <pre>
     * &#64;NEQ
     * private String status;  // status <> ?
     *
     * &#64;NEQ(includeNull = true)
     * private String type;    // type <> ? OR type IS NULL
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface NEQ {
        /** 對應的資料庫屬性路徑 */
        String value() default "";
        /** 是否包含 NULL 值 (預設 true) */
        boolean includeNull() default true;
    }

    /**
     * 模糊查詢條件 (LIKE)
     * <pre>
     * &#64;LIKE
     * private String name;  // name LIKE '%?%'
     *
     * &#64;LIKE(prefix = true, suffix = false)
     * private String code;  // code LIKE '?%'
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface LIKE {
        /** 對應的資料庫屬性路徑 */
        String value() default "";
        /** 是否加前綴 % (預設 true) */
        boolean prefix() default true;
        /** 是否加後綴 % (預設 true) */
        boolean suffix() default true;
        /** 是否忽略大小寫 (預設 true) */
        boolean ignoreCase() default true;
    }

    /**
     * 大於條件 (>)
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface GT {
        String value() default "";
    }

    /**
     * 小於條件 (<)
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface LT {
        String value() default "";
    }

    /**
     * 大於等於條件 (>=)
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface GTE {
        String value() default "";
    }

    /**
     * 小於等於條件 (<=)
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface LTE {
        String value() default "";
    }

    /**
     * 範圍條件 (BETWEEN)
     * 欄位值必須是包含兩個元素的陣列或 List
     * <pre>
     * &#64;BETWEEN("age")
     * private Integer[] ageRange;  // age BETWEEN ? AND ?
     *
     * &#64;BETWEEN("createTime")
     * private List&lt;LocalDateTime&gt; dateRange;  // createTime BETWEEN ? AND ?
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface BETWEEN {
        String value() default "";
    }

    /**
     * IN 條件
     * 欄位值必須是 Collection 或陣列
     * <pre>
     * &#64;IN
     * private List&lt;String&gt; status;  // status IN (?, ?, ?)
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface IN {
        String value() default "";
    }

    /**
     * NOT IN 條件
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface NOTIN {
        String value() default "";
    }

    /**
     * IS NULL 條件
     * 當欄位值為 Boolean.TRUE 時套用
     * <pre>
     * &#64;ISNULL("deletedAt")
     * private Boolean notDeleted = true;  // deletedAt IS NULL
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface ISNULL {
        String value() default "";
    }

    /**
     * IS NOT NULL 條件
     * 當欄位值為 Boolean.TRUE 時套用
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface ISNOTNULL {
        String value() default "";
    }

    // ==================== 邏輯組合註解 ====================

    /**
     * OR 條件標記
     * 標記此欄位應以 OR 連接
     * <pre>
     * &#64;EQ
     * &#64;OR
     * private String name;  // OR name = ?
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface OR {
    }

    /**
     * OR 群組標記
     * 將多個欄位組成一個 AND 群組，再以 OR 連接
     * <pre>
     * &#64;EQ
     * &#64;ORGroup("adminGroup")
     * private String role;     // (role = ? AND level >= ?)
     *
     * &#64;GTE
     * &#64;ORGroup("adminGroup")
     * private Integer level;   // 與 role 組成同一個 AND 群組
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface ORGroup {
        /** 群組名稱，相同名稱的欄位會組成一個 AND 群組 */
        String value();
    }

    // ==================== JOIN 控制註解 ====================

    /**
     * Fetch Join 標記
     * 可放在類別或欄位上
     * <pre>
     * &#64;FetchJoin({"department", "department.manager"})
     * public class EmployeeSearchCondition {
     *     // ...
     * }
     *
     * // 或放在欄位上
     * &#64;EQ("department.id")
     * &#64;FetchJoin("department")
     * private String deptId;
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD})
    @interface FetchJoin {
        /** 關聯路徑，支援多層 (如 "a.b.c") */
        String[] value();
        /** Join 類型 */
        JoinType joinType() default JoinType.LEFT;
    }

    /**
     * Join 類型
     */
    enum JoinType {
        INNER, LEFT, RIGHT
    }

    // ==================== 排序註解 ====================

    /**
     * 排序欄位標記 (放在類別上)
     * <pre>
     * &#64;OrderBy({"createTime desc", "id asc"})
     * public class EmployeeSearchCondition {
     *     // ...
     * }
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface OrderBy {
        /** 排序欄位，格式為 "field [asc|desc]" */
        String[] value();
    }
}
