package com.company.hrms.common.test.inmemory;

import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.LogicalOp;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryGroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 記憶體查詢引擎
 * 將 QueryGroup 轉換為 Java Predicate，在記憶體集合中執行過濾
 *
 * <p>行為對齊 UltimateQueryEngine：
 * <ul>
 *   <li>支援全部 12 個 Operator</li>
 *   <li>null 值守衛：IS_NULL/IS_NOT_NULL 正常評估，其他 Operator 遇 null value 跳過</li>
 *   <li>Enum 自動轉換：String → Enum.valueOf()</li>
 *   <li>日期自動轉換：String → LocalDate/LocalDateTime</li>
 *   <li>LIKE 支援通配符 % 和無通配符（containsIgnoreCase）</li>
 *   <li>巢狀路徑支援（透過 FieldAccessor）</li>
 *   <li>Value Object 自動解包（透過 FieldAccessor）</li>
 * </ul>
 */
public final class InMemoryQueryEngine {

    private InMemoryQueryEngine() {
    }

    /**
     * 將 QueryGroup 轉換為 Predicate
     *
     * @param group 查詢條件群組
     * @param clazz 實體類別（用於型別推斷）
     * @return Predicate，若 group 為空則回傳 always-true
     */
    public static <T> Predicate<T> toPredicate(QueryGroup group, Class<T> clazz) {
        if (group == null || group.isEmpty()) {
            return t -> true;
        }
        return parseGroup(group, clazz);
    }

    /**
     * 遞迴解析 QueryGroup
     */
    private static <T> Predicate<T> parseGroup(QueryGroup group, Class<T> clazz) {
        Predicate<T> combined = null;
        boolean isOr = group.getJunction() == LogicalOp.OR;

        // 處理條件
        for (FilterUnit unit : group.getConditions()) {
            Predicate<T> predicate = toFilterPredicate(unit, clazz);
            if (predicate == null) {
                continue; // null value 跳過（對齊 UltimateQueryEngine 行為）
            }
            combined = combinePredicate(combined, predicate, isOr);
        }

        // 處理子群組
        for (QueryGroup subGroup : group.getSubGroups()) {
            Predicate<T> subPredicate = parseGroup(subGroup, clazz);
            combined = combinePredicate(combined, subPredicate, isOr);
        }

        return combined != null ? combined : (t -> true);
    }

    /**
     * 合併 Predicate
     */
    private static <T> Predicate<T> combinePredicate(Predicate<T> existing, Predicate<T> next, boolean isOr) {
        if (existing == null) {
            return next;
        }
        return isOr ? existing.or(next) : existing.and(next);
    }

    /**
     * 將單一 FilterUnit 轉換為 Predicate
     *
     * @return Predicate，若 filter value 為 null 且不是 IS_NULL/IS_NOT_NULL 則回傳 null（跳過）
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static <T> Predicate<T> toFilterPredicate(FilterUnit unit, Class<T> clazz) {
        String fieldPath = unit.getField();
        Operator op = unit.getOp();
        Object filterValue = unit.getValue();

        // 取得欄位型別（用於型別轉換）
        Class<?> fieldType = FieldAccessor.getFieldType(clazz, fieldPath);

        // 扁平化：解決 varargs 包裝的巢狀 Collection/Array
        filterValue = flattenValue(filterValue);

        // 型別轉換（Enum、日期）
        if (filterValue != null && fieldType != Object.class) {
            filterValue = FieldAccessor.convertValue(filterValue, fieldType);
        }

        final Object finalValue = filterValue;

        // null 值守衛
        if (finalValue == null) {
            return switch (op) {
                case IS_NULL -> entity -> getFieldValue(entity, fieldPath) == null;
                case IS_NOT_NULL -> entity -> getFieldValue(entity, fieldPath) != null;
                default -> null; // 其他 Operator 遇 null 跳過
            };
        }

        return switch (op) {
            case EQ -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return equalsWithConversion(fieldVal, finalValue);
            };

            case NE -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return !equalsWithConversion(fieldVal, finalValue);
            };

            case GT -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return compareValues(fieldVal, finalValue) > 0;
            };

            case GTE -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return compareValues(fieldVal, finalValue) >= 0;
            };

            case LT -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return compareValues(fieldVal, finalValue) < 0;
            };

            case LTE -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return compareValues(fieldVal, finalValue) <= 0;
            };

            case LIKE -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return matchesLike(fieldVal, String.valueOf(finalValue));
            };

            case IN -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return isInCollection(fieldVal, finalValue);
            };

            case NOT_IN -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return !isInCollection(fieldVal, finalValue);
            };

            case BETWEEN -> entity -> {
                Object fieldVal = getFieldValue(entity, fieldPath);
                return isBetween(fieldVal, finalValue);
            };

            case IS_NULL -> entity -> getFieldValue(entity, fieldPath) == null;

            case IS_NOT_NULL -> entity -> getFieldValue(entity, fieldPath) != null;
        };
    }

    /**
     * 從實體取得欄位值（自動解包 Value Object）
     */
    private static Object getFieldValue(Object entity, String fieldPath) {
        Object raw = FieldAccessor.getValue(entity, fieldPath);
        return FieldAccessor.unwrapValueObject(raw);
    }

    /**
     * 相等比較（支援 Value Object 解包後比較）
     */
    private static boolean equalsWithConversion(Object fieldVal, Object filterVal) {
        if (fieldVal == null && filterVal == null) {
            return true;
        }
        if (fieldVal == null || filterVal == null) {
            return false;
        }

        // 同型別直接比較
        if (fieldVal.getClass().equals(filterVal.getClass())) {
            return Objects.equals(fieldVal, filterVal);
        }

        // 不同型別嘗試 toString 比較（例如 Enum.name() vs String）
        return Objects.equals(fieldVal.toString(), filterVal.toString());
    }

    /**
     * 比較值大小（支援 Comparable）
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static int compareValues(Object fieldVal, Object filterVal) {
        if (fieldVal == null) {
            return -1;
        }
        if (filterVal == null) {
            return 1;
        }

        if (fieldVal instanceof Comparable && filterVal instanceof Comparable) {
            try {
                return ((Comparable) fieldVal).compareTo(filterVal);
            } catch (ClassCastException e) {
                // 型別不相容，嘗試 toString 比較
                return fieldVal.toString().compareTo(filterVal.toString());
            }
        }

        return fieldVal.toString().compareTo(filterVal.toString());
    }

    /**
     * LIKE 匹配（對齊 UltimateQueryEngine）
     * 有通配符 % → 模式匹配（忽略大小寫）
     * 無通配符 → containsIgnoreCase
     */
    private static boolean matchesLike(Object fieldVal, String pattern) {
        if (fieldVal == null) {
            return false;
        }

        String fieldStr = fieldVal.toString().toLowerCase();
        String lowerPattern = pattern.toLowerCase();

        if (lowerPattern.contains("%") || lowerPattern.contains("_")) {
            // 轉成正則：% → .*, _ → .
            String regex = lowerPattern
                    .replace("%", ".*")
                    .replace("_", ".");
            return fieldStr.matches(regex);
        }

        // 無通配符 → containsIgnoreCase
        return fieldStr.contains(lowerPattern);
    }

    /**
     * IN 判斷
     */
    private static boolean isInCollection(Object fieldVal, Object filterVal) {
        if (fieldVal == null) {
            return false;
        }

        Collection<?> collection;
        if (filterVal instanceof Collection<?> c) {
            collection = c;
        } else if (filterVal instanceof Object[] arr) {
            collection = Arrays.asList(arr);
        } else {
            // 回退到 EQ
            return equalsWithConversion(fieldVal, filterVal);
        }

        for (Object item : collection) {
            if (equalsWithConversion(fieldVal, item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * BETWEEN 判斷
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static boolean isBetween(Object fieldVal, Object filterVal) {
        if (fieldVal == null) {
            return false;
        }

        Object[] range;
        if (filterVal instanceof java.util.List<?> list) {
            range = list.toArray();
        } else if (filterVal instanceof Object[] arr) {
            range = arr;
        } else {
            throw new IllegalArgumentException("BETWEEN 運算子需要包含兩個元素的 List 或 Array");
        }

        if (range.length != 2) {
            throw new IllegalArgumentException("BETWEEN 運算子需要正好兩個元素");
        }

        return compareValues(fieldVal, range[0]) >= 0 && compareValues(fieldVal, range[1]) <= 0;
    }

    /**
     * 扁平化處理：解決 varargs 包裝的巢狀 Collection/Array
     * 對齊 UltimateQueryEngine 第 250-256 行
     */
    private static Object flattenValue(Object value) {
        if (value instanceof Object[] arr && arr.length == 1) {
            Object first = arr[0];
            if (first instanceof Collection || (first != null && first.getClass().isArray())) {
                return first;
            }
        }
        return value;
    }
}
