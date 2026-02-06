package com.company.hrms.common.infrastructure.persistence.querydsl.engine;

import java.beans.Introspector;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
                    // 嘗試取得屬性的真實類型
                    Class<?> nextType = Object.class;
                    Class<?> originalFieldType = Object.class;
                    boolean isCollection = false;
                    try {
                        java.lang.reflect.Field field = findField(currentPath.getType(), part);
                        if (field != null) {
                            Class<?> fieldType = field.getType();
                            originalFieldType = fieldType;
                            nextType = fieldType;

                            if (Collection.class.isAssignableFrom(fieldType)) {
                                isCollection = true;
                                Type genericType = field.getGenericType();
                                if (genericType instanceof ParameterizedType) {
                                    nextType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                                }
                            }
                        }
                    } catch (Exception e) {
                        try {
                            String getterName = "get" + part.substring(0, 1).toUpperCase() + part.substring(1);
                            java.lang.reflect.Method method = currentPath.getType().getMethod(getterName);
                            Class<?> returnType = method.getReturnType();
                            originalFieldType = returnType;
                            nextType = returnType;

                            if (Collection.class.isAssignableFrom(returnType)) {
                                isCollection = true;
                                Type genericType = method.getGenericReturnType();
                                if (genericType instanceof ParameterizedType) {
                                    nextType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                                }
                            }
                        } catch (Exception e2) {
                        }
                    }

                    String joinAlias = part + "_" + i + "_" + System.nanoTime() % 1000;
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    PathBuilder<?> nextPath = new PathBuilder<>(nextType, joinAlias);

                    try {
                        if (isCollection) {
                            // 使用更具體的集合路徑方法，協助 Querydsl/Hibernate 正確解析實體類型
                            // 使用 Raw Type 轉型以避開 Querydsl/Hibernate 泛型路徑解析的限制
                            if (java.util.List.class.isAssignableFrom(originalFieldType)) {
                                query.leftJoin((com.querydsl.core.types.CollectionExpression) currentPath.getList(part,
                                        nextType), nextPath);
                            } else if (java.util.Set.class.isAssignableFrom(originalFieldType)) {
                                query.leftJoin((com.querydsl.core.types.CollectionExpression) currentPath.getSet(part,
                                        nextType), nextPath);
                            } else {
                                query.leftJoin((com.querydsl.core.types.CollectionExpression) currentPath
                                        .getCollection(part, nextType), nextPath);
                            }
                        } else {
                            query.leftJoin((com.querydsl.core.types.EntityPath) currentPath.get(part), nextPath);
                        }
                    } catch (Exception e) {
                        // 發生錯誤時嘗試回退到基本 EntityPath 轉型
                        query.leftJoin((com.querydsl.core.types.EntityPath) currentPath.get(part), nextPath);
                    }
                    joinedPaths.put(pathAcc, nextPath);
                }
                currentPath = joinedPaths.get(pathAcc);
            }
        }

        String fieldName = parts[parts.length - 1];
        return createPredicate(currentPath, fieldName, unit.getOp(), unit.getValue());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private BooleanExpression createPredicate(PathBuilder<?> path, String fieldName, Operator op, Object value) {
        // 1. 自動處理底線轉駝峰 (例如 total_amount -> totalAmount)
        String actualFieldName = fieldName;
        if (fieldName.contains("_")) {
            StringBuilder sb = new StringBuilder();
            boolean nextUpper = false;
            for (char c : fieldName.toCharArray()) {
                if (c == '_') {
                    nextUpper = true;
                } else if (nextUpper) {
                    sb.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    sb.append(c);
                }
            }
            actualFieldName = sb.toString();
        }

        // 取得基本欄位路徑
        PathBuilder<?> fieldPath = path.get(actualFieldName);
        Class<?> fieldType = Object.class;

        // 2. 遞迴尋找欄位類型 (支援繼承)
        if (path.getType() != null) {
            java.lang.reflect.Field field = findField(path.getType(), actualFieldName);
            if (field != null) {
                fieldType = field.getType();
            } else {
                // Try getter
                try {
                    String getterName = "get" + actualFieldName.substring(0, 1).toUpperCase()
                            + actualFieldName.substring(1);
                    fieldType = path.getType().getMethod(getterName).getReturnType();
                } catch (Exception e) {
                }
            }
        }

        Object finalValue = value;
        final Class<?> targetFieldType = fieldType;
        fieldName = actualFieldName; // 更新欄位名稱供後續 switch 使用

        // 1. 扁平化處理: 解決可能出現的嵌套 Collection (例如 Object[] { List })
        // 這通常發生在 Varargs 傳遞時誤傳入了一個集合
        if (finalValue instanceof Object[] && ((Object[]) finalValue).length == 1) {
            Object first = ((Object[]) finalValue)[0];
            if (first instanceof Collection || (first != null && first.getClass().isArray())) {
                finalValue = first;
            }
        }

        // 自動處理 Enum 轉換 (如果值是 String)
        if (targetFieldType != null && targetFieldType.isEnum() && finalValue != null) {
            if (finalValue instanceof String) {
                finalValue = Enum.valueOf((Class<Enum>) targetFieldType, (String) finalValue);
            } else if (finalValue instanceof Collection) {
                finalValue = ((Collection<?>) finalValue).stream()
                        .map(v -> v instanceof String ? Enum.valueOf((Class<Enum>) targetFieldType, (String) v) : v)
                        .toList();
            } else if (finalValue.getClass().isArray()) {
                Object[] arr = (Object[]) finalValue;
                finalValue = java.util.Arrays.stream(arr)
                        .map(v -> v instanceof String ? Enum.valueOf((Class<Enum>) targetFieldType, (String) v) : v)
                        .toArray();
            }
        }

        // 自動處理日期類型轉換
        boolean isDateType = targetFieldType != null
                && (java.time.temporal.Temporal.class.isAssignableFrom(targetFieldType)
                        || targetFieldType.getName().startsWith("java.time.")
                        || targetFieldType.getName().contains("Date"));

        if (isDateType && finalValue != null) {
            if (finalValue instanceof String) {
                String strValue = (String) finalValue;
                if (targetFieldType == java.time.LocalDateTime.class) {
                    if (strValue.length() == 10) {
                        finalValue = java.time.LocalDate.parse(strValue).atStartOfDay();
                    } else {
                        finalValue = java.time.LocalDateTime.parse(strValue.replace(" ", "T"));
                    }
                } else if (targetFieldType == java.time.LocalDate.class) {
                    finalValue = java.time.LocalDate.parse(strValue);
                }
            } else if (finalValue instanceof Collection) {
                finalValue = ((Collection<?>) finalValue).stream()
                        .map(v -> {
                            if (v instanceof String) {
                                String strV = (String) v;
                                if (targetFieldType == java.time.LocalDateTime.class) {
                                    return strV.length() == 10 ? java.time.LocalDate.parse(strV).atStartOfDay()
                                            : java.time.LocalDateTime.parse(strV.replace(" ", "T"));
                                } else if (targetFieldType == java.time.LocalDate.class) {
                                    return java.time.LocalDate.parse(strV);
                                }
                            }
                            return v;
                        })
                        .toList();
            }
        }

        switch (op) {
            case EQ:
                return path.get(fieldName).eq(finalValue);

            case NE:
                return path.get(fieldName).ne(finalValue);

            case GT:
                return path.getComparable(fieldName, (Class) targetFieldType).gt((Comparable) finalValue);

            case LT:
                return path.getComparable(fieldName, (Class) targetFieldType).lt((Comparable) finalValue);

            case GTE:
                return path.getComparable(fieldName, (Class) targetFieldType).goe((Comparable) finalValue);

            case LTE:
                return path.getComparable(fieldName, (Class) targetFieldType).loe((Comparable) finalValue);

            case LIKE:
                String likeValue = String.valueOf(finalValue);
                // 如果值已經包含通配符，使用 like；否則使用 contains (自動包裝通配符)
                if (likeValue.contains("%") || likeValue.contains("_")) {
                    return path.getString(fieldName).likeIgnoreCase(likeValue);
                }
                return path.getString(fieldName).containsIgnoreCase(likeValue);

            case IN:
                if (finalValue instanceof Collection) {
                    return ((PathBuilder) fieldPath).in((Collection) finalValue);
                } else if (finalValue instanceof Object[]) {
                    return ((PathBuilder) fieldPath).in((Object[]) finalValue);
                }
                return ((PathBuilder) fieldPath).eq(finalValue); // 回退到 eq

            case NOT_IN:
                if (finalValue instanceof Collection) {
                    return ((PathBuilder) fieldPath).notIn((Collection) finalValue);
                } else if (finalValue instanceof Object[]) {
                    return ((PathBuilder) fieldPath).notIn((Object[]) finalValue);
                }
                return ((PathBuilder) fieldPath).ne(finalValue); // 回退到 ne

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

    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
