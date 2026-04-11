package com.company.hrms.common.test.inmemory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射式欄位存取工具
 * 支援巢狀路徑（如 "department.name"）、snake_case 自動轉換、Value Object 自動解包
 *
 * <p>設計對齊 UltimateQueryEngine 的行為：
 * <ul>
 *   <li>snake_case → camelCase 自動轉換</li>
 *   <li>Value Object 偵測與解包（呼叫 getValue()）</li>
 *   <li>巢狀路徑遞迴解析</li>
 * </ul>
 */
public final class FieldAccessor {

    private FieldAccessor() {
    }

    /** 快取：Class + fieldName → Field，避免重複反射 */
    private static final Map<String, Field> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 從物件取得欄位值
     * 支援巢狀路徑如 "department.name"
     *
     * @param obj       目標物件
     * @param fieldPath 欄位路徑（支援點號分隔的巢狀路徑、snake_case）
     * @return 欄位值，物件為 null 時回傳 null
     */
    public static Object getValue(Object obj, String fieldPath) {
        if (obj == null || fieldPath == null) {
            return null;
        }

        String[] parts = fieldPath.split("\\.");
        Object current = obj;

        for (String part : parts) {
            if (current == null) {
                return null;
            }
            current = getDirectFieldValue(current, part);
        }

        return current;
    }

    /**
     * 從物件取得直接欄位值（單層，不處理巢狀）
     */
    private static Object getDirectFieldValue(Object obj, String fieldName) {
        // snake_case → camelCase
        String camelName = toCamelCase(fieldName);
        Class<?> clazz = obj.getClass();

        // 嘗試透過 Field 取得
        Field field = findField(clazz, camelName);
        if (field != null) {
            try {
                field.setAccessible(true);
                return field.get(obj);
            } catch (IllegalAccessException e) {
                // 降級到 getter
            }
        }

        // 嘗試透過 getter 取得
        try {
            String getterName = "get" + camelName.substring(0, 1).toUpperCase() + camelName.substring(1);
            Method getter = clazz.getMethod(getterName);
            return getter.invoke(obj);
        } catch (Exception e) {
            // 嘗試 is 前綴（boolean）
            try {
                String isGetterName = "is" + camelName.substring(0, 1).toUpperCase() + camelName.substring(1);
                Method isGetter = clazz.getMethod(isGetterName);
                return isGetter.invoke(obj);
            } catch (Exception e2) {
                throw new IllegalArgumentException(
                        String.format("無法從 %s 取得欄位 '%s'（嘗試: %s, getter, isGetter）",
                                clazz.getSimpleName(), fieldName, camelName));
            }
        }
    }

    /**
     * 取得欄位值並自動解包 Value Object
     * 若欄位值是 Value Object（具有 getValue() 方法），自動呼叫 getValue() 取出原始值
     *
     * @param obj       目標物件
     * @param fieldPath 欄位路徑
     * @return 解包後的欄位值
     */
    public static Object getUnwrappedValue(Object obj, String fieldPath) {
        Object value = getValue(obj, fieldPath);
        return unwrapValueObject(value);
    }

    /**
     * 解包 Value Object
     * 偵測物件是否具有 getValue() 方法，有則呼叫取出原始值
     */
    public static Object unwrapValueObject(Object value) {
        if (value == null) {
            return null;
        }

        // 基本型別、String、日期、Enum 不需解包
        if (value instanceof String || value instanceof Number || value instanceof Boolean
                || value instanceof Enum || value instanceof LocalDate || value instanceof LocalDateTime
                || value instanceof java.util.Date) {
            return value;
        }

        // 嘗試呼叫 getValue() 解包
        try {
            Method getValueMethod = value.getClass().getMethod("getValue");
            return getValueMethod.invoke(value);
        } catch (NoSuchMethodException e) {
            // 沒有 getValue()，回傳原始值
            return value;
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 取得物件的欄位型別
     *
     * @param clazz     類別
     * @param fieldPath 欄位路徑
     * @return 欄位型別，找不到則回傳 Object.class
     */
    public static Class<?> getFieldType(Class<?> clazz, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Class<?> currentClass = clazz;

        for (String part : parts) {
            String camelName = toCamelCase(part);
            Field field = findField(currentClass, camelName);
            if (field != null) {
                currentClass = field.getType();
            } else {
                // 嘗試 getter
                try {
                    String getterName = "get" + camelName.substring(0, 1).toUpperCase() + camelName.substring(1);
                    currentClass = currentClass.getMethod(getterName).getReturnType();
                } catch (Exception e) {
                    return Object.class;
                }
            }
        }

        return currentClass;
    }

    /**
     * snake_case 轉 camelCase
     * 例如: employment_status → employmentStatus
     */
    static String toCamelCase(String snakeCase) {
        if (snakeCase == null || !snakeCase.contains("_")) {
            return snakeCase;
        }

        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 遞迴尋找欄位（支援繼承鏈）
     */
    private static Field findField(Class<?> clazz, String fieldName) {
        String cacheKey = clazz.getName() + "#" + fieldName;

        return FIELD_CACHE.computeIfAbsent(cacheKey, k -> {
            Class<?> current = clazz;
            while (current != null && current != Object.class) {
                try {
                    return current.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    current = current.getSuperclass();
                }
            }
            return null;
        });
    }

    /**
     * 型別轉換：將 filter value 轉成欄位的實際型別
     * 對齊 UltimateQueryEngine 的型別轉換邏輯
     *
     * @param value     filter 的值
     * @param fieldType 欄位的實際型別
     * @return 轉換後的值
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object convertValue(Object value, Class<?> fieldType) {
        if (value == null || fieldType == null || fieldType == Object.class) {
            return value;
        }

        // Enum 轉換
        if (fieldType.isEnum() && value instanceof String) {
            return Enum.valueOf((Class<Enum>) fieldType, (String) value);
        }

        // 日期轉換
        if (value instanceof String strValue) {
            if (fieldType == LocalDateTime.class) {
                if (strValue.length() == 10) {
                    return LocalDate.parse(strValue).atStartOfDay();
                }
                return LocalDateTime.parse(strValue.replace(" ", "T"));
            }
            if (fieldType == LocalDate.class) {
                return LocalDate.parse(strValue);
            }
        }

        // Collection 內容轉換
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .map(v -> convertValue(v, fieldType))
                    .toList();
        }

        // Array 內容轉換
        if (value instanceof Object[] array) {
            return Arrays.stream(array)
                    .map(v -> convertValue(v, fieldType))
                    .toArray();
        }

        return value;
    }
}
