package com.company.hrms.common.query;

import com.company.hrms.common.query.QueryCondition.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 條件解析器
 * 從帶有 QueryCondition 註解的物件自動建構 QueryGroup
 *
 * <p>使用範例:</p>
 * <pre>
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
 * EmployeeSearchCondition cond = new EmployeeSearchCondition();
 * cond.setName("John");
 * cond.setStatuses(List.of("ACTIVE", "ON_LEAVE"));
 *
 * QueryGroup group = ConditionParser.parse(cond);
 * // 生成: name LIKE '%John%' AND status IN ('ACTIVE', 'ON_LEAVE')
 * </pre>
 */
public class ConditionParser {

    private ConditionParser() {
        // 工具類，禁止實例化
    }

    /**
     * 解析條件物件為 QueryGroup
     *
     * @param condition 條件物件 (帶有 QueryCondition 註解)
     * @return QueryGroup，若條件為空則返回空的 QueryGroup
     */
    public static QueryGroup parse(Object condition) {
        if (condition == null) {
            return new QueryGroup(LogicalOp.AND);
        }

        QueryGroup mainGroup = new QueryGroup(LogicalOp.AND);
        List<FilterUnit> orConditions = new ArrayList<>();
        Map<String, List<FilterUnit>> orGroups = new LinkedHashMap<>();

        // 遍歷所有欄位 (包含父類別)
        Class<?> clazz = condition.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(condition);
                    if (value == null) {
                        continue;
                    }

                    // 跳過空字串
                    if (value instanceof String && ((String) value).isEmpty()) {
                        continue;
                    }

                    // 跳過空集合
                    if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                        continue;
                    }

                    FilterUnit filterUnit = buildFilterUnit(field, value);
                    if (filterUnit == null) {
                        continue;
                    }

                    // 處理邏輯組合
                    if (field.isAnnotationPresent(OR.class)) {
                        orConditions.add(filterUnit);
                    } else if (field.isAnnotationPresent(ORGroup.class)) {
                        String groupName = field.getAnnotation(ORGroup.class).value();
                        orGroups.computeIfAbsent(groupName, k -> new ArrayList<>()).add(filterUnit);
                    } else {
                        mainGroup.add(filterUnit);
                    }

                } catch (IllegalAccessException e) {
                    // 忽略無法存取的欄位
                }
            }
            clazz = clazz.getSuperclass();
        }

        // 處理 OR 條件
        if (!orConditions.isEmpty() || !orGroups.isEmpty()) {
            QueryGroup orGroup = new QueryGroup(LogicalOp.OR);

            // 加入單獨的 OR 條件
            for (FilterUnit unit : orConditions) {
                orGroup.add(unit);
            }

            // 加入 ORGroup (每個 group 是一個 AND 子群組)
            for (List<FilterUnit> groupFilters : orGroups.values()) {
                if (groupFilters.size() == 1) {
                    orGroup.add(groupFilters.get(0));
                } else {
                    QueryGroup andSubGroup = new QueryGroup(LogicalOp.AND);
                    for (FilterUnit unit : groupFilters) {
                        andSubGroup.add(unit);
                    }
                    orGroup.addSubGroup(andSubGroup);
                }
            }

            mainGroup.addSubGroup(orGroup);
        }

        return mainGroup;
    }

    /**
     * 從欄位和值建構 FilterUnit
     */
    private static FilterUnit buildFilterUnit(Field field, Object value) {
        String fieldName = field.getName();

        // @EQ
        if (field.isAnnotationPresent(EQ.class)) {
            EQ ann = field.getAnnotation(EQ.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            return new FilterUnit(property, Operator.EQ, value);
        }

        // @NEQ
        if (field.isAnnotationPresent(NEQ.class)) {
            NEQ ann = field.getAnnotation(NEQ.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            // 注意: includeNull 的處理需要在 Engine 層實現
            return new FilterUnit(property, Operator.NE, value);
        }

        // @LIKE
        if (field.isAnnotationPresent(LIKE.class)) {
            LIKE ann = field.getAnnotation(LIKE.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            // 處理前後綴
            String likeValue = value.toString();
            if (ann.prefix()) {
                likeValue = "%" + likeValue;
            }
            if (ann.suffix()) {
                likeValue = likeValue + "%";
            }
            return new FilterUnit(property, Operator.LIKE, likeValue);
        }

        // @GT
        if (field.isAnnotationPresent(GT.class)) {
            GT ann = field.getAnnotation(GT.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            return new FilterUnit(property, Operator.GT, value);
        }

        // @LT
        if (field.isAnnotationPresent(LT.class)) {
            LT ann = field.getAnnotation(LT.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            return new FilterUnit(property, Operator.LT, value);
        }

        // @GTE
        if (field.isAnnotationPresent(GTE.class)) {
            GTE ann = field.getAnnotation(GTE.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            return new FilterUnit(property, Operator.GTE, value);
        }

        // @LTE
        if (field.isAnnotationPresent(LTE.class)) {
            LTE ann = field.getAnnotation(LTE.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            return new FilterUnit(property, Operator.LTE, value);
        }

        // @BETWEEN
        if (field.isAnnotationPresent(BETWEEN.class)) {
            BETWEEN ann = field.getAnnotation(BETWEEN.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            Object[] range = toArray(value);
            if (range != null && range.length == 2) {
                return FilterUnit.between(property, range[0], range[1]);
            }
            return null;
        }

        // @IN
        if (field.isAnnotationPresent(IN.class)) {
            IN ann = field.getAnnotation(IN.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            Object[] values = toArray(value);
            if (values != null && values.length > 0) {
                return FilterUnit.in(property, values);
            }
            return null;
        }

        // @NOTIN
        if (field.isAnnotationPresent(NOTIN.class)) {
            NOTIN ann = field.getAnnotation(NOTIN.class);
            String property = ann.value().isEmpty() ? fieldName : ann.value();
            Object[] values = toArray(value);
            if (values != null && values.length > 0) {
                return FilterUnit.notIn(property, values);
            }
            return null;
        }

        // @ISNULL
        if (field.isAnnotationPresent(ISNULL.class)) {
            if (Boolean.TRUE.equals(value)) {
                ISNULL ann = field.getAnnotation(ISNULL.class);
                String property = ann.value().isEmpty() ? fieldName : ann.value();
                return FilterUnit.isNull(property);
            }
            return null;
        }

        // @ISNOTNULL
        if (field.isAnnotationPresent(ISNOTNULL.class)) {
            if (Boolean.TRUE.equals(value)) {
                ISNOTNULL ann = field.getAnnotation(ISNOTNULL.class);
                String property = ann.value().isEmpty() ? fieldName : ann.value();
                return FilterUnit.isNotNull(property);
            }
            return null;
        }

        // 兼容舊的 @QueryFilter 註解
        if (field.isAnnotationPresent(QueryFilter.class)) {
            QueryFilter filter = field.getAnnotation(QueryFilter.class);
            String property = filter.property().isEmpty() ? fieldName : filter.property();
            return new FilterUnit(property, filter.operator(), value);
        }

        // 沒有任何運算子註解，跳過此欄位
        return null;
    }

    /**
     * 將值轉換為陣列
     */
    private static Object[] toArray(Object value) {
        if (value instanceof Collection) {
            return ((Collection<?>) value).toArray();
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            Object[] result = new Object[length];
            for (int i = 0; i < length; i++) {
                result[i] = Array.get(value, i);
            }
            return result;
        }
        return null;
    }

    /**
     * 收集需要 Fetch Join 的路徑
     *
     * @param condition 條件物件
     * @return Fetch Join 路徑集合
     */
    public static Set<String> collectFetchJoins(Object condition) {
        Set<String> fetchJoins = new LinkedHashSet<>();

        if (condition == null) {
            return fetchJoins;
        }

        // 類別層級的 FetchJoin
        if (condition.getClass().isAnnotationPresent(FetchJoin.class)) {
            FetchJoin classFetchJoin = condition.getClass().getAnnotation(FetchJoin.class);
            Collections.addAll(fetchJoins, classFetchJoin.value());
        }

        // 欄位層級的 FetchJoin
        Class<?> clazz = condition.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(FetchJoin.class)) {
                    FetchJoin fieldFetchJoin = field.getAnnotation(FetchJoin.class);
                    Collections.addAll(fetchJoins, fieldFetchJoin.value());
                }
            }
            clazz = clazz.getSuperclass();
        }

        return fetchJoins;
    }

    /**
     * 收集排序設定
     *
     * @param condition 條件物件
     * @return 排序欄位列表 (格式: "field direction")
     */
    public static List<String> collectOrderBy(Object condition) {
        if (condition == null) {
            return Collections.emptyList();
        }

        if (condition.getClass().isAnnotationPresent(OrderBy.class)) {
            OrderBy orderBy = condition.getClass().getAnnotation(OrderBy.class);
            return Arrays.asList(orderBy.value());
        }

        return Collections.emptyList();
    }
}
