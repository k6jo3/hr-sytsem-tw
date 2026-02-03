package com.company.hrms.common.infrastructure.persistence.querydsl.engine;

import java.beans.Introspector;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.LogicalOp;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 通用查詢引擎
 * 將 QueryGroup 轉換為 Querydsl BooleanExpression
 * 支援自動 JOIN 偵測與嵌套路徑解析
 *
 * <p>
 * 使用範例:
 * </p>
 * 
 * <pre>
 * UltimateQueryEngine&lt;Employee&gt; engine = new UltimateQueryEngine&lt;&gt;(factory, Employee.class);
 * BooleanExpression predicate = engine.parse(queryGroup);
 * List&lt;Employee&gt; result = engine.getQuery().where(predicate).fetch();
 * </pre>
 *
 * @param <T> 實體類型
 */
public class UltimateQueryEngine<T> {

    private final JPAQueryFactory factory;
    private final Class<T> entityClass;
    private final PathBuilder<T> entityPath;
    private final Map<String, PathBuilder<?>> joinedPaths = new HashMap<>();
    private JPAQuery<T> query;

    public UltimateQueryEngine(JPAQueryFactory factory, Class<T> entityClass) {
        this.factory = factory;
        this.entityClass = entityClass;
        this.entityPath = new PathBuilder<>(entityClass, Introspector.decapitalize(entityClass.getSimpleName()));
        this.joinedPaths.put("", entityPath);
    }

    /**
     * 建立新的查詢實例
     */
    private JPAQuery<T> createQuery() {
        JPAQuery<T> newQuery = factory.selectFrom(entityPath);
        joinedPaths.clear();
        joinedPaths.put("", entityPath);
        return newQuery;
    }

    /**
     * 解析 QueryGroup 為 BooleanExpression
     *
     * @param group 查詢條件群組
     * @return BooleanExpression
     */
    public BooleanExpression parse(QueryGroup group) {
        if (this.query == null) {
            this.query = createQuery();
        }

        if (group == null || group.isEmpty()) {
            return null;
        }

        return parseGroup(group);
    }

    /**
     * 遞迴解析 QueryGroup
     */
    private BooleanExpression parseGroup(QueryGroup group) {
        BooleanBuilder builder = new BooleanBuilder();

        // 處理條件
        for (FilterUnit unit : group.getConditions()) {
            BooleanExpression exp = buildExpression(unit);
            if (group.getJunction() == LogicalOp.OR) {
                builder.or(exp);
            } else {
                builder.and(exp);
            }
        }

        // 處理子群組
        for (QueryGroup subGroup : group.getSubGroups()) {
            BooleanExpression subExp = parseGroup(subGroup);
            if (group.getJunction() == LogicalOp.OR) {
                builder.or(subExp);
            } else {
                builder.and(subExp);
            }
        }

        // BooleanBuilder 實作 Predicate，使用 Expressions.asBoolean 包裝
        return builder.hasValue() ? Expressions.asBoolean(builder) : null;
    }

    /**
     * 建構單一條件的 BooleanExpression
     */
    private BooleanExpression buildExpression(FilterUnit unit) {
        String[] parts = unit.getField().split("\\.");
        PathBuilder<?> currentPath = entityPath;

        // 自動 JOIN 偵測邏輯
        if (parts.length > 1) {
            String pathAcc = "";
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                pathAcc = pathAcc.isEmpty() ? part : pathAcc + "." + part;
                if (!joinedPaths.containsKey(pathAcc)) {
                    PathBuilder<Object> nextPath = new PathBuilder<>(Object.class, part);
                    query.leftJoin((PathBuilder<Object>) currentPath.get(part, Object.class), nextPath);
                    joinedPaths.put(pathAcc, nextPath);
                }
                currentPath = joinedPaths.get(pathAcc);
            }
        }

        String fieldName = parts[parts.length - 1];
        return createPredicate(currentPath, fieldName, unit.getOp(), unit.getValue());
    }

    /**
     * 根據運算子類型建立對應的 Querydsl 謂詞
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private BooleanExpression createPredicate(PathBuilder<?> path, String fieldName, Operator op, Object value) {
        // 取得欄位類型以進行必要的轉換
        Class<?> fieldType = path.get(fieldName).getType();
        Object finalValue = value;

        // 自動處理 Enum 轉換 (如果值是 String)
        if (fieldType != null && fieldType.isEnum() && value != null) {
            if (value instanceof String) {
                finalValue = Enum.valueOf((Class<Enum>) fieldType, (String) value);
            } else if (value instanceof Collection) {
                finalValue = ((Collection<?>) value).stream()
                        .map(v -> v instanceof String ? Enum.valueOf((Class<Enum>) fieldType, (String) v) : v)
                        .toList();
            } else if (value.getClass().isArray()) {
                Object[] arr = (Object[]) value;
                finalValue = java.util.Arrays.stream(arr)
                        .map(v -> v instanceof String ? Enum.valueOf((Class<Enum>) fieldType, (String) v) : v)
                        .toArray();
            }
        }

        switch (op) {
            case EQ:
                return path.get(fieldName).eq(finalValue);

            case NE:
                return path.get(fieldName).ne(finalValue);

            case GT:
                return path.getComparable(fieldName, Comparable.class).gt((Comparable) finalValue);

            case LT:
                return path.getComparable(fieldName, Comparable.class).lt((Comparable) finalValue);

            case GTE:
                return path.getComparable(fieldName, Comparable.class).goe((Comparable) finalValue);

            case LTE:
                return path.getComparable(fieldName, Comparable.class).loe((Comparable) finalValue);

            case LIKE:
                String likeValue = String.valueOf(finalValue);
                // 如果值已經包含通配符，使用 like；否則使用 contains (自動包裝通配符)
                if (likeValue.contains("%") || likeValue.contains("_")) {
                    return path.getString(fieldName).likeIgnoreCase(likeValue);
                }
                return path.getString(fieldName).containsIgnoreCase(likeValue);

            case IN:
                if (finalValue instanceof Collection) {
                    return path.get(fieldName).in((Collection<?>) finalValue);
                } else if (finalValue instanceof Object[]) {
                    return path.get(fieldName).in((Object[]) finalValue);
                }
                throw new IllegalArgumentException("IN 運算子需要 Collection 或 Array 類型的值");

            case NOT_IN:
                if (finalValue instanceof Collection) {
                    return path.get(fieldName).notIn((Collection<?>) finalValue);
                } else if (finalValue instanceof Object[]) {
                    return path.get(fieldName).notIn((Object[]) finalValue);
                }
                throw new IllegalArgumentException("NOT_IN 運算子需要 Collection 或 Array 類型的值");

            case BETWEEN:
                Object[] range;
                if (finalValue instanceof List) {
                    range = ((List<?>) finalValue).toArray();
                } else if (finalValue instanceof Object[]) {
                    range = (Object[]) finalValue;
                } else {
                    throw new IllegalArgumentException("BETWEEN 運算子需要包含兩個元素的 List 或 Array");
                }

                if (range.length == 2) {
                    return path.getComparable(fieldName, Comparable.class)
                            .between((Comparable) range[0], (Comparable) range[1]);
                }
                throw new IllegalArgumentException("BETWEEN 運算子需要正好兩個元素");

            case IS_NULL:
                return path.get(fieldName).isNull();

            case IS_NOT_NULL:
                return path.get(fieldName).isNotNull();

            default:
                throw new UnsupportedOperationException("不支援的運算子: " + op);
        }
    }

    /**
     * 取得查詢實例
     */
    public JPAQuery<T> getQuery() {
        if (this.query == null) {
            this.query = createQuery();
        }
        return query;
    }

    /**
     * 取得實體路徑
     */
    public PathBuilder<T> getEntityPath() {
        return entityPath;
    }

    /**
     * 取得實體類型
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * 取得已建立的 JOIN 路徑
     */
    public Map<String, PathBuilder<?>> getJoinedPaths() {
        return new HashMap<>(joinedPaths);
    }

    /**
     * 重置查詢狀態
     */
    public void reset() {
        this.query = null;
        this.joinedPaths.clear();
        this.joinedPaths.put("", entityPath);
    }
}
