package com.company.hrms.common.query;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * 查詢條件建構器
 * 提供流暢的 API 來建構 QueryGroup
 *
 * 使用範例:
 * 
 * <pre>
 * QueryGroup group = QueryBuilder.where()
 *         .eq("status", "ACTIVE")
 *         .eq("isDeleted", 0)
 *         .orGroup(sub -> sub
 *                 .eq("role", "ADMIN")
 *                 .eq("role", "MANAGER"))
 *         .build();
 * </pre>
 */
public class QueryBuilder {

    private final QueryGroup group;

    private QueryBuilder(LogicalOp junction) {
        this.group = new QueryGroup(junction);
    }

    // ========== 靜態工廠方法 ==========

    /**
     * 建立 AND 查詢建構器
     */
    public static QueryBuilder where() {
        return new QueryBuilder(LogicalOp.AND);
    }

    /**
     * 建立 OR 查詢建構器
     */
    public static QueryBuilder whereOr() {
        return new QueryBuilder(LogicalOp.OR);
    }

    /**
     * 從帶有 QueryCondition 註解的條件物件直接建立 QueryGroup
     * <p>
     * 這是最便捷的使用方式，只需要在條件物件欄位上標註運算子註解，
     * 即可自動建構查詢條件，無需手動撰寫 if-else 判斷。
     * </p>
     *
     * <pre>
     * // 定義條件物件
     * public class EmployeeSearchCondition {
     *     &#64;EQ
     *     private String employeeId;
     *
     *     &#64;LIKE
     *     private String name;
     *
     *     &#64;IN("status")
     *     private List&lt;String&gt; statuses;
     * }
     *
     * // 使用方式
     * EmployeeSearchCondition cond = new EmployeeSearchCondition();
     * cond.setName("John");
     *
     * QueryGroup group = QueryBuilder.fromCondition(cond);
     * List&lt;Employee&gt; result = repository.findAll(group);
     * </pre>
     *
     * @param condition 帶有 QueryCondition 註解的條件物件
     * @return QueryGroup
     */
    public static QueryGroup fromCondition(Object condition) {
        return ConditionParser.parse(condition);
    }

    // ========== 條件新增 ==========

    /**
     * 新增過濾條件
     */
    public QueryBuilder and(String field, Operator operator, Object value) {
        group.add(new FilterUnit(field, operator, value));
        return this;
    }

    /**
     * 新增等於條件
     */
    public QueryBuilder eq(String field, Object value) {
        return and(field, Operator.EQ, value);
    }

    /**
     * 新增不等於條件
     */
    public QueryBuilder ne(String field, Object value) {
        return and(field, Operator.NE, value);
    }

    /**
     * 新增模糊查詢條件
     */
    public QueryBuilder like(String field, String value) {
        return and(field, Operator.LIKE, value);
    }

    /**
     * 新增 IN 條件
     */
    public QueryBuilder in(String field, Object... values) {
        group.add(FilterUnit.in(field, values));
        return this;
    }

    /**
     * 新增 NOT IN 條件
     */
    public QueryBuilder notIn(String field, Object... values) {
        // Assuming FilterUnit has notIn or we manually create it
        // Checking existing patterns... group.add(FilterUnit.in(field, values));
        // FilterUnit likely has a constructor or static factory?
        // I'll check if FilterUnit has notIn. If not, I use new FilterUnit.
        // But for safety I'll use generic add based on Operator.NOT_IN
        // However, standard pattern in this file is to add FilterUnit directly for
        // special ops?
        // Method 'and' creates new FilterUnit(field, operator, value
        // So I can use: return and(field, Operator.NOT_IN,
        // java.util.Arrays.asList(values));
        // But FilterUnit.in(...) takes array.
        // Let's use generic approach:
        return and(field, Operator.NOT_IN, java.util.Arrays.asList(values));
    }

    /**
     * 新增 IS NULL 條件
     */
    public QueryBuilder isNull(String field) {
        group.add(FilterUnit.isNull(field));
        return this;
    }

    /**
     * 新增 IS NOT NULL 條件
     */
    public QueryBuilder isNotNull(String field) {
        group.add(FilterUnit.isNotNull(field));
        return this;
    }

    /**
     * 新增大於條件
     */
    public QueryBuilder gt(String field, Object value) {
        return and(field, Operator.GT, value);
    }

    /**
     * 新增大於等於條件
     */
    public QueryBuilder gte(String field, Object value) {
        return and(field, Operator.GTE, value);
    }

    /**
     * 新增小於條件
     */
    public QueryBuilder lt(String field, Object value) {
        return and(field, Operator.LT, value);
    }

    /**
     * 新增小於等於條件
     */
    public QueryBuilder lte(String field, Object value) {
        return and(field, Operator.LTE, value);
    }

    /**
     * 新增 BETWEEN 條件
     */
    public QueryBuilder between(String field, Object low, Object high) {
        return and(field, Operator.BETWEEN, java.util.Arrays.asList(low, high));
    }

    // ========== DTO 自動解析 ==========

    /**
     * 從帶有 @QueryFilter 註解的 DTO 自動解析查詢條件
     *
     * @param dto 查詢請求 DTO
     * @return this
     */
    public QueryBuilder fromDto(Object dto) {
        if (dto == null) {
            return this;
        }

        for (Field field : dto.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                if (value != null && field.isAnnotationPresent(QueryFilter.class)) {
                    QueryFilter filter = field.getAnnotation(QueryFilter.class);
                    String property = filter.property().isEmpty() ? field.getName() : filter.property();
                    group.add(new FilterUnit(property, filter.operator(), value));
                }
            } catch (IllegalAccessException e) {
                // 忽略無法存取的欄位
            }
        }
        return this;
    }

    // ========== 子群組操作 ==========

    /**
     * 新增 AND 子群組
     */
    public QueryBuilder andGroup(Consumer<QueryBuilder> consumer) {
        QueryBuilder subBuilder = new QueryBuilder(LogicalOp.AND);
        consumer.accept(subBuilder);
        group.addSubGroup(subBuilder.build());
        return this;
    }

    /**
     * 新增 OR 子群組
     */
    public QueryBuilder orGroup(Consumer<QueryBuilder> consumer) {
        QueryBuilder subBuilder = new QueryBuilder(LogicalOp.OR);
        consumer.accept(subBuilder);
        group.addSubGroup(subBuilder.build());
        return this;
    }

    // ========== 建構 ==========

    /**
     * 建構 QueryGroup
     */
    public QueryGroup build() {
        return group;
    }
}
