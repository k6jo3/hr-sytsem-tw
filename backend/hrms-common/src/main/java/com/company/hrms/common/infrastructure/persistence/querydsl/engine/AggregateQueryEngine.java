package com.company.hrms.common.infrastructure.persistence.querydsl.engine;

import java.beans.Introspector;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.company.hrms.common.query.AggregateField;
import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.GroupByClause;
import com.company.hrms.common.query.LogicalOp;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 聚合查詢引擎
 * 支援 GROUP BY、HAVING、聚合函數 (SUM、COUNT、AVG、MAX、MIN)
 *
 * <p>
 * 使用範例:
 * </p>
 * 
 * <pre>
 * AggregateQueryEngine&lt;Timesheet&gt; engine = new AggregateQueryEngine&lt;&gt;(factory, Timesheet.class);
 *
 * QueryGroup where = QueryBuilder.where()
 *         .eq("status", "APPROVED")
 *         .build();
 *
 * GroupByClause groupBy = GroupByClause.builder()
 *         .groupBy("project.id", "project.name")
 *         .sum("hours", "totalHours")
 *         .countDistinct("employeeId", "headCount")
 *         .build();
 *
 * List&lt;Tuple&gt; results = engine.executeAggregate(where, groupBy);
 * </pre>
 *
 * @param <T> 實體類型
 */
public class AggregateQueryEngine<T> {

    private final JPAQueryFactory factory;
    private final Class<T> entityClass;
    private final PathBuilder<T> entityPath;
    private final Map<String, PathBuilder<?>> joinedPaths = new HashMap<>();
    private JPAQuery<Tuple> currentQuery;

    public AggregateQueryEngine(JPAQueryFactory factory, Class<T> entityClass) {
        this.factory = factory;
        this.entityClass = entityClass;
        this.entityPath = new PathBuilder<>(entityClass, Introspector.decapitalize(entityClass.getSimpleName()));
        this.joinedPaths.put("", entityPath);
    }

    /**
     * 執行聚合查詢，返回 Tuple 結果
     *
     * @param where   WHERE 條件
     * @param groupBy GROUP BY 子句
     * @return Tuple 列表
     */
    public List<Tuple> executeAggregate(QueryGroup where, GroupByClause groupBy) {
        // 重置 JOIN 狀態
        joinedPaths.clear();
        joinedPaths.put("", entityPath);

        // 收集所有需要的 JOIN 路徑
        Set<String> requiredJoins = new HashSet<>();
        if (where != null) {
            collectJoinPaths(where, requiredJoins);
        }
        for (String field : groupBy.getGroupByFields()) {
            if (field.contains(".")) {
                requiredJoins.add(field.substring(0, field.lastIndexOf(".")));
            }
        }
        for (AggregateField agg : groupBy.getAggregates()) {
            if (agg.isNestedField()) {
                requiredJoins.add(agg.getField().substring(0, agg.getField().lastIndexOf(".")));
            }
        }

        // 建構 SELECT 表達式
        List<Expression<?>> selectExpressions = new ArrayList<>();

        // 加入 GROUP BY 欄位到 SELECT
        List<Expression<?>> groupByExpressions = new ArrayList<>();
        for (String field : groupBy.getGroupByFields()) {
            PathBuilder<?> fieldPath = resolvePath(field);
            selectExpressions.add(fieldPath);
            groupByExpressions.add(fieldPath);
        }

        // 加入聚合欄位到 SELECT
        for (AggregateField agg : groupBy.getAggregates()) {
            Expression<?> aggExp = buildAggregateExpression(agg);
            selectExpressions.add(aggExp);
        }

        // 建構查詢
        currentQuery = factory.select(selectExpressions.toArray(new Expression[0]))
                .from(entityPath);

        // 套用 JOIN
        applyJoins(requiredJoins);

        // 套用 WHERE 條件
        if (where != null && !where.isEmpty()) {
            BooleanExpression predicate = parseWhereClause(where);
            if (predicate != null) {
                currentQuery.where(predicate);
            }
        }

        // 套用 GROUP BY
        if (!groupByExpressions.isEmpty()) {
            currentQuery.groupBy(groupByExpressions.toArray(new Expression[0]));
        }

        // 套用 HAVING
        if (groupBy.hasHaving()) {
            BooleanExpression havingPredicate = parseWhereClause(groupBy.getHaving());
            if (havingPredicate != null) {
                currentQuery.having(havingPredicate);
            }
        }

        return currentQuery.fetch();
    }

    /**
     * 建構聚合表達式
     */
    private Expression<?> buildAggregateExpression(AggregateField agg) {
        String field = agg.getField();
        String[] parts = field.split("\\.");
        PathBuilder<?> basePath = entityPath;

        // 解析巢狀路徑
        if (parts.length > 1) {
            for (int i = 0; i < parts.length - 1; i++) {
                String pathKey = String.join(".", Arrays.copyOfRange(parts, 0, i + 1));
                basePath = joinedPaths.getOrDefault(pathKey, entityPath);
            }
        }

        String fieldName = parts[parts.length - 1];

        switch (agg.getFunction()) {
            case COUNT:
                return basePath.get(fieldName).count();

            case COUNT_DISTINCT:
                return basePath.get(fieldName).countDistinct();

            case SUM:
                return basePath.getNumber(fieldName, BigDecimal.class).sum();

            case AVG:
                return basePath.getNumber(fieldName, BigDecimal.class).avg();

            case MAX:
                return basePath.getComparable(fieldName, Comparable.class).max();

            case MIN:
                return basePath.getComparable(fieldName, Comparable.class).min();

            default:
                throw new UnsupportedOperationException("不支援的聚合函數: " + agg.getFunction());
        }
    }

    /**
     * 解析欄位路徑
     */
    private PathBuilder<?> resolvePath(String field) {
        String[] parts = field.split("\\.");
        PathBuilder<?> currentPath = entityPath;

        if (parts.length > 1) {
            for (int i = 0; i < parts.length - 1; i++) {
                String pathKey = String.join(".", Arrays.copyOfRange(parts, 0, i + 1));
                if (joinedPaths.containsKey(pathKey)) {
                    currentPath = joinedPaths.get(pathKey);
                }
            }
        }

        return currentPath.get(parts[parts.length - 1]);
    }

    /**
     * 套用必要的 JOIN
     */
    private void applyJoins(Set<String> requiredJoins) {
        for (String joinPath : requiredJoins) {
            String[] parts = joinPath.split("\\.");
            PathBuilder<?> currentPath = entityPath;
            String pathAcc = "";

            for (String part : parts) {
                pathAcc = pathAcc.isEmpty() ? part : pathAcc + "." + part;
                if (!joinedPaths.containsKey(pathAcc)) {
                    PathBuilder<Object> nextPath = new PathBuilder<>(Object.class, part);
                    currentQuery.leftJoin((PathBuilder<Object>) currentPath.get(part, Object.class), nextPath);
                    joinedPaths.put(pathAcc, nextPath);
                }
                currentPath = joinedPaths.get(pathAcc);
            }
        }
    }

    /**
     * 收集需要 JOIN 的路徑
     */
    private void collectJoinPaths(QueryGroup group, Set<String> paths) {
        for (FilterUnit filter : group.getConditions()) {
            if (filter.isNestedField()) {
                String field = filter.getField();
                paths.add(field.substring(0, field.lastIndexOf(".")));
            }
        }
        for (QueryGroup subGroup : group.getSubGroups()) {
            collectJoinPaths(subGroup, paths);
        }
    }

    /**
     * 解析 WHERE 子句
     */
    private BooleanExpression parseWhereClause(QueryGroup group) {
        BooleanBuilder builder = new BooleanBuilder();

        for (FilterUnit unit : group.getConditions()) {
            BooleanExpression exp = buildFilterExpression(unit);
            if (group.getJunction() == LogicalOp.OR) {
                builder.or(exp);
            } else {
                builder.and(exp);
            }
        }

        for (QueryGroup subGroup : group.getSubGroups()) {
            BooleanExpression subExp = parseWhereClause(subGroup);
            if (group.getJunction() == LogicalOp.OR) {
                builder.or(subExp);
            } else {
                builder.and(subExp);
            }
        }

        return builder.hasValue() ? Expressions.asBoolean(builder) : null;
    }

    /**
     * 建構單一過濾條件的表達式
     */
    @SuppressWarnings({ "rawtypes" })
    private BooleanExpression buildFilterExpression(FilterUnit unit) {
        String field = unit.getField();
        String[] parts = field.split("\\.");
        PathBuilder<?> basePath = entityPath;

        if (parts.length > 1) {
            for (int i = 0; i < parts.length - 1; i++) {
                String pathKey = String.join(".", Arrays.copyOfRange(parts, 0, i + 1));
                if (joinedPaths.containsKey(pathKey)) {
                    basePath = joinedPaths.get(pathKey);
                }
            }
        }

        String fieldName = parts[parts.length - 1];
        Operator op = unit.getOp();
        Object value = unit.getValue();

        switch (op) {
            case EQ:
                return basePath.get(fieldName).eq(value);

            case NE:
                return basePath.get(fieldName).ne(value);

            case GT:
                return basePath.getComparable(fieldName, Comparable.class).gt((Comparable) value);

            case LT:
                return basePath.getComparable(fieldName, Comparable.class).lt((Comparable) value);

            case GTE:
                return basePath.getComparable(fieldName, Comparable.class).goe((Comparable) value);

            case LTE:
                return basePath.getComparable(fieldName, Comparable.class).loe((Comparable) value);

            case LIKE:
                return basePath.getString(fieldName).containsIgnoreCase((String) value);

            case IN:
                if (value instanceof Collection) {
                    return basePath.get(fieldName).in((Collection<?>) value);
                } else if (value instanceof Object[]) {
                    return basePath.get(fieldName).in((Object[]) value);
                }
                throw new IllegalArgumentException("IN 運算子需要 Collection 或 Array 類型的值");

            case NOT_IN:
                if (value instanceof Collection) {
                    return basePath.get(fieldName).notIn((Collection<?>) value);
                } else if (value instanceof Object[]) {
                    return basePath.get(fieldName).notIn((Object[]) value);
                }
                throw new IllegalArgumentException("NOT_IN 運算子需要 Collection 或 Array 類型的值");

            case BETWEEN:
                if (value instanceof Object[] && ((Object[]) value).length == 2) {
                    Object[] range = (Object[]) value;
                    return basePath.getComparable(fieldName, Comparable.class)
                        .between((Comparable) range[0], (Comparable) range[1]);
                }
                throw new IllegalArgumentException("BETWEEN 運算子需要包含兩個元素的陣列");

            case IS_NULL:
                return basePath.get(fieldName).isNull();

            case IS_NOT_NULL:
                return basePath.get(fieldName).isNotNull();

            default:
                throw new UnsupportedOperationException("不支援的運算子: " + op);
        }
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
}
