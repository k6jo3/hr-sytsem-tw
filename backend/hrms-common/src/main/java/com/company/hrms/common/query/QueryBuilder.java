package com.company.hrms.common.query;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * 查詢條件建構器
 * 提供流暢的 API 來建構 QueryGroup
 *
 * 使用範例:
 * <pre>
 * QueryGroup group = QueryBuilder.where()
 *     .eq("status", "ACTIVE")
 *     .eq("isDeleted", 0)
 *     .orGroup(sub -> sub
 *         .eq("role", "ADMIN")
 *         .eq("role", "MANAGER"))
 *     .build();
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
