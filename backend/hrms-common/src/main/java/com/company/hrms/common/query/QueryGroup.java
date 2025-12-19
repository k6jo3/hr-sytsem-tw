package com.company.hrms.common.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 查詢條件群組
 * 表示一組以邏輯運算子連接的過濾條件
 * 支援巢狀結構，可組合複雜查詢邏輯
 *
 * 範例:
 * (status = 'ACTIVE' AND is_deleted = 0) OR (role = 'ADMIN')
 */
public class QueryGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 條件間的邏輯運算子 */
    private LogicalOp junction = LogicalOp.AND;

    /** 本群組內的過濾條件 */
    private final List<FilterUnit> conditions = new ArrayList<>();

    /** 子群組 (用於巢狀邏輯) */
    private final List<QueryGroup> subGroups = new ArrayList<>();

    public QueryGroup() {
    }

    public QueryGroup(LogicalOp junction) {
        this.junction = junction;
    }

    // ========== 建構方法 ==========

    /**
     * 建立 AND 群組
     */
    public static QueryGroup and() {
        return new QueryGroup(LogicalOp.AND);
    }

    /**
     * 建立 OR 群組
     */
    public static QueryGroup or() {
        return new QueryGroup(LogicalOp.OR);
    }

    // ========== 條件新增 ==========

    /**
     * 新增過濾條件
     */
    public QueryGroup add(FilterUnit filter) {
        Objects.requireNonNull(filter, "filter cannot be null");
        this.conditions.add(filter);
        return this;
    }

    /**
     * 新增等於條件
     */
    public QueryGroup eq(String field, Object value) {
        return add(FilterUnit.eq(field, value));
    }

    /**
     * 新增不等於條件
     */
    public QueryGroup ne(String field, Object value) {
        return add(FilterUnit.ne(field, value));
    }

    /**
     * 新增模糊查詢條件
     */
    public QueryGroup like(String field, String value) {
        return add(FilterUnit.like(field, value));
    }

    /**
     * 新增 IN 條件
     */
    public QueryGroup in(String field, Object... values) {
        return add(FilterUnit.in(field, values));
    }

    /**
     * 新增 IS NULL 條件
     */
    public QueryGroup isNull(String field) {
        return add(FilterUnit.isNull(field));
    }

    /**
     * 新增 IS NOT NULL 條件
     */
    public QueryGroup isNotNull(String field) {
        return add(FilterUnit.isNotNull(field));
    }

    /**
     * 新增大於條件
     */
    public QueryGroup gt(String field, Object value) {
        return add(FilterUnit.gt(field, value));
    }

    /**
     * 新增大於等於條件
     */
    public QueryGroup gte(String field, Object value) {
        return add(FilterUnit.gte(field, value));
    }

    /**
     * 新增小於條件
     */
    public QueryGroup lt(String field, Object value) {
        return add(FilterUnit.lt(field, value));
    }

    /**
     * 新增小於等於條件
     */
    public QueryGroup lte(String field, Object value) {
        return add(FilterUnit.lte(field, value));
    }

    // ========== 子群組操作 ==========

    /**
     * 新增子群組
     */
    public QueryGroup addSubGroup(QueryGroup subGroup) {
        Objects.requireNonNull(subGroup, "subGroup cannot be null");
        this.subGroups.add(subGroup);
        return this;
    }

    /**
     * 新增 AND 子群組並設定條件
     */
    public QueryGroup andGroup(java.util.function.Consumer<QueryGroup> builder) {
        QueryGroup subGroup = QueryGroup.and();
        builder.accept(subGroup);
        return addSubGroup(subGroup);
    }

    /**
     * 新增 OR 子群組並設定條件
     */
    public QueryGroup orGroup(java.util.function.Consumer<QueryGroup> builder) {
        QueryGroup subGroup = QueryGroup.or();
        builder.accept(subGroup);
        return addSubGroup(subGroup);
    }

    // ========== 查詢方法 ==========

    /**
     * 取得所有過濾條件 (不含子群組)
     */
    public List<FilterUnit> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    /**
     * 取得所有過濾條件 (包含子群組，遞迴展開)
     */
    public List<FilterUnit> getAllFilters() {
        List<FilterUnit> all = new ArrayList<>(conditions);
        for (QueryGroup subGroup : subGroups) {
            all.addAll(subGroup.getAllFilters());
        }
        return all;
    }

    /**
     * 取得所有子群組
     */
    public List<QueryGroup> getSubGroups() {
        return Collections.unmodifiableList(subGroups);
    }

    /**
     * 取得邏輯運算子
     */
    public LogicalOp getJunction() {
        return junction;
    }

    /**
     * 設定邏輯運算子
     */
    public void setJunction(LogicalOp junction) {
        this.junction = junction;
    }

    /**
     * 檢查是否為空群組
     */
    public boolean isEmpty() {
        return conditions.isEmpty() && subGroups.isEmpty();
    }

    /**
     * 取得條件總數 (包含子群組)
     */
    public int getTotalConditionCount() {
        int count = conditions.size();
        for (QueryGroup subGroup : subGroups) {
            count += subGroup.getTotalConditionCount();
        }
        return count;
    }

    /**
     * 檢查是否包含指定欄位的過濾條件
     */
    public boolean hasFilterForField(String field) {
        // 檢查本群組
        boolean found = conditions.stream()
            .anyMatch(f -> f.getField().equalsIgnoreCase(field));
        if (found) return true;

        // 遞迴檢查子群組
        for (QueryGroup subGroup : subGroups) {
            if (subGroup.hasFilterForField(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取得指定欄位的所有過濾條件
     */
    public List<FilterUnit> getFiltersForField(String field) {
        List<FilterUnit> result = conditions.stream()
            .filter(f -> f.getField().equalsIgnoreCase(field))
            .collect(Collectors.toList());

        for (QueryGroup subGroup : subGroups) {
            result.addAll(subGroup.getFiltersForField(field));
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("QueryGroup{junction=").append(junction);

        if (!conditions.isEmpty()) {
            sb.append(", conditions=[");
            sb.append(conditions.stream()
                .map(FilterUnit::toString)
                .collect(Collectors.joining(", ")));
            sb.append("]");
        }

        if (!subGroups.isEmpty()) {
            sb.append(", subGroups=[");
            sb.append(subGroups.stream()
                .map(QueryGroup::toString)
                .collect(Collectors.joining(", ")));
            sb.append("]");
        }

        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryGroup that = (QueryGroup) o;
        return junction == that.junction &&
               Objects.equals(conditions, that.conditions) &&
               Objects.equals(subGroups, that.subGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(junction, conditions, subGroups);
    }
}
