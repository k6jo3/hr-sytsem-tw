package com.company.hrms.common.infrastructure.persistence.querydsl.engine;

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

import java.beans.Introspector;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用查詢引擎
 * 將 QueryGroup 轉換為 Querydsl BooleanExpression
 * 支援自動 JOIN 偵測與嵌套路徑解析
 *
 * <p>使用範例:</p>
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    private BooleanExpression createPredicate(PathBuilder<?> path, String fieldName, Operator op, Object value) {
        switch (op) {
            case EQ:
                return path.get(fieldName).eq(value);

            case NE:
                return path.get(fieldName).ne(value);

            case GT:
                return path.getComparable(fieldName, Comparable.class).gt((Comparable) value);

            case LT:
                return path.getComparable(fieldName, Comparable.class).lt((Comparable) value);

            case GTE:
                return path.getComparable(fieldName, Comparable.class).goe((Comparable) value);

            case LTE:
                return path.getComparable(fieldName, Comparable.class).loe((Comparable) value);

            case LIKE:
                return path.getString(fieldName).containsIgnoreCase((String) value);

            case IN:
                if (value instanceof Collection) {
                    return path.get(fieldName).in((Collection<?>) value);
                } else if (value instanceof Object[]) {
                    return path.get(fieldName).in((Object[]) value);
                }
                throw new IllegalArgumentException("IN 運算子需要 Collection 或 Array 類型的值");

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
