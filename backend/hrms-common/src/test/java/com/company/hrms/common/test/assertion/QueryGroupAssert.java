package com.company.hrms.common.test.assertion;

import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.LogicalOp;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * QueryGroup DSL 斷言器
 * 提供流暢的 API 驗證 QueryGroup 內容
 *
 * <p>使用範例:
 * <pre>
 * QueryGroupAssert.assertThat(queryGroup)
 *     .isJunction(LogicalOp.AND)
 *     .hasCondition("status", Operator.EQ, "ACTIVE")
 *     .hasCondition("department.name", Operator.LIKE, "研發")
 *     .hasConditionCount(2)
 *     .doesNotHaveConditionFor("deletedAt");
 * </pre>
 */
public class QueryGroupAssert {

    private final QueryGroup actual;
    private final List<String> errors = new ArrayList<>();

    private QueryGroupAssert(QueryGroup actual) {
        if (actual == null) {
            throw new AssertionError("QueryGroup 不應為 null");
        }
        this.actual = actual;
    }

    /**
     * 建立斷言
     */
    public static QueryGroupAssert assertThat(QueryGroup queryGroup) {
        return new QueryGroupAssert(queryGroup);
    }

    /**
     * 驗證邏輯運算子
     */
    public QueryGroupAssert isJunction(LogicalOp expected) {
        if (actual.getJunction() != expected) {
            errors.add(String.format(
                "預期 junction 為 [%s]，但實際為 [%s]",
                expected, actual.getJunction()));
        }
        return this;
    }

    /**
     * 驗證是否為 AND 群組
     */
    public QueryGroupAssert isAnd() {
        return isJunction(LogicalOp.AND);
    }

    /**
     * 驗證是否為 OR 群組
     */
    public QueryGroupAssert isOr() {
        return isJunction(LogicalOp.OR);
    }

    /**
     * 驗證包含指定條件
     */
    public QueryGroupAssert hasCondition(String field, Operator op, Object value) {
        boolean found = actual.getAllFilters().stream()
            .anyMatch(f -> matchesCondition(f, field, op, value));

        if (!found) {
            errors.add(String.format(
                "預期包含條件 [%s %s '%s']，但未找到。\n實際條件: %s",
                field, op.getSymbol(), value, formatConditions()));
        }
        return this;
    }

    /**
     * 驗證包含指定欄位的條件（不驗證運算子和值）
     */
    public QueryGroupAssert hasConditionFor(String field) {
        if (!actual.hasFilterForField(field)) {
            errors.add(String.format(
                "預期包含欄位 [%s] 的條件，但未找到。\n實際條件: %s",
                field, formatConditions()));
        }
        return this;
    }

    /**
     * 驗證不包含指定欄位的條件
     */
    public QueryGroupAssert doesNotHaveConditionFor(String field) {
        if (actual.hasFilterForField(field)) {
            List<FilterUnit> filters = actual.getFiltersForField(field);
            errors.add(String.format(
                "預期不包含欄位 [%s] 的條件，但找到了: %s",
                field, filters));
        }
        return this;
    }

    /**
     * 驗證條件數量
     */
    public QueryGroupAssert hasConditionCount(int expected) {
        int actualCount = actual.getConditions().size();
        if (actualCount != expected) {
            errors.add(String.format(
                "預期有 %d 個條件，但實際有 %d 個",
                expected, actualCount));
        }
        return this;
    }

    /**
     * 驗證總條件數量（含子群組）
     */
    public QueryGroupAssert hasTotalConditionCount(int expected) {
        int actualCount = actual.getTotalConditionCount();
        if (actualCount != expected) {
            errors.add(String.format(
                "預期總共有 %d 個條件（含子群組），但實際有 %d 個",
                expected, actualCount));
        }
        return this;
    }

    /**
     * 驗證子群組數量
     */
    public QueryGroupAssert hasSubGroupCount(int expected) {
        int actualCount = actual.getSubGroups().size();
        if (actualCount != expected) {
            errors.add(String.format(
                "預期有 %d 個子群組，但實際有 %d 個",
                expected, actualCount));
        }
        return this;
    }

    /**
     * 驗證為空群組
     */
    public QueryGroupAssert isEmpty() {
        if (!actual.isEmpty()) {
            errors.add(String.format(
                "預期為空群組，但包含 %d 個條件和 %d 個子群組",
                actual.getConditions().size(), actual.getSubGroups().size()));
        }
        return this;
    }

    /**
     * 驗證不為空群組
     */
    public QueryGroupAssert isNotEmpty() {
        if (actual.isEmpty()) {
            errors.add("預期不為空群組，但實際為空");
        }
        return this;
    }

    /**
     * 驗證子群組
     *
     * @param index 子群組索引
     * @param assertions 對子群組的斷言
     */
    public QueryGroupAssert subGroup(int index, java.util.function.Consumer<QueryGroupAssert> assertions) {
        if (index >= actual.getSubGroups().size()) {
            errors.add(String.format(
                "預期有第 %d 個子群組（索引從 0 開始），但只有 %d 個子群組",
                index, actual.getSubGroups().size()));
        } else {
            QueryGroupAssert subAssert = new QueryGroupAssert(actual.getSubGroups().get(index));
            assertions.accept(subAssert);
            errors.addAll(subAssert.errors);
        }
        return this;
    }

    /**
     * 完成斷言並檢查錯誤
     * 如果有任何錯誤，拋出 AssertionError
     */
    public void verify() {
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("╔══════════════════════════════════════════════════════════════╗\n");
            sb.append("║                    QueryGroup 斷言失敗                        ║\n");
            sb.append("╠══════════════════════════════════════════════════════════════╣\n");

            for (String error : errors) {
                sb.append("║ ❌ ").append(truncate(error, 60)).append("\n");
            }

            sb.append("╠══════════════════════════════════════════════════════════════╣\n");
            sb.append("║ 實際 QueryGroup:                                              ║\n");
            sb.append("╠══════════════════════════════════════════════════════════════╣\n");
            sb.append("║ Junction: ").append(actual.getJunction()).append("\n");
            sb.append("║ Conditions:\n");
            for (FilterUnit f : actual.getConditions()) {
                sb.append("║   - ").append(f.toString()).append("\n");
            }
            if (!actual.getSubGroups().isEmpty()) {
                sb.append("║ SubGroups: ").append(actual.getSubGroups().size()).append("\n");
            }
            sb.append("╚══════════════════════════════════════════════════════════════╝\n");

            throw new AssertionError(sb.toString());
        }
    }

    /**
     * 完成斷言（verify 的別名）
     */
    public void check() {
        verify();
    }

    private boolean matchesCondition(FilterUnit filter, String field, Operator op, Object value) {
        if (!filter.getField().equalsIgnoreCase(field)) {
            return false;
        }
        if (filter.getOp() != op) {
            return false;
        }
        if (value == null) {
            return filter.getValue() == null;
        }
        return String.valueOf(filter.getValue()).equalsIgnoreCase(String.valueOf(value));
    }

    private String formatConditions() {
        return actual.getAllFilters().stream()
            .map(FilterUnit::toString)
            .collect(Collectors.joining(", "));
    }

    private static String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
    }
}
