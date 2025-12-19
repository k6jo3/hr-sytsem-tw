package com.company.hrms.common.test.assertion;

import com.company.hrms.common.domain.calculation.CalculationResult;

import java.util.Objects;

/**
 * CalculationResult DSL 斷言器
 * 提供流暢的計算結果驗證 API
 *
 * <p>使用範例：
 * <pre>
 * CalculationResultAssert.assertThat(result)
 *     .hasValue(new BigDecimal("50000"))
 *     .hasAppliedRule("基本薪資計算")
 *     .hasBreakdown("底薪", new BigDecimal("45000"))
 *     .hasBreakdownCount(3);
 * </pre>
 *
 * @param <T> 計算結果型別
 */
public class CalculationResultAssert<T> {

    private final CalculationResult<T> actual;

    private CalculationResultAssert(CalculationResult<T> result) {
        this.actual = Objects.requireNonNull(result, "待驗證的 CalculationResult 不可為 null");
    }

    /**
     * 建立斷言實例
     */
    public static <T> CalculationResultAssert<T> assertThat(CalculationResult<T> result) {
        return new CalculationResultAssert<>(result);
    }

    /**
     * 驗證計算結果值
     */
    public CalculationResultAssert<T> hasValue(T expected) {
        if (!Objects.equals(actual.getValue(), expected)) {
            throw new AssertionError(String.format(
                "預期計算結果為 [%s]，但實際為 [%s]",
                expected, actual.getValue()));
        }
        return this;
    }

    /**
     * 驗證有套用特定規則
     */
    public CalculationResultAssert<T> hasAppliedRule(String ruleName) {
        if (!actual.hasRule(ruleName)) {
            throw new AssertionError(String.format(
                "預期計算結果包含規則 [%s]，但未找到。\n已套用的規則: %s",
                ruleName, actual.getAppliedRules()));
        }
        return this;
    }

    /**
     * 驗證沒有套用特定規則
     */
    public CalculationResultAssert<T> doesNotHaveRule(String ruleName) {
        if (actual.hasRule(ruleName)) {
            throw new AssertionError(String.format(
                "預期計算結果不包含規則 [%s]，但找到了。",
                ruleName));
        }
        return this;
    }

    /**
     * 驗證套用規則數量
     */
    public CalculationResultAssert<T> hasRuleCount(int expectedCount) {
        int actualCount = actual.getAppliedRules().size();
        if (actualCount != expectedCount) {
            throw new AssertionError(String.format(
                "預期套用 [%d] 條規則，但實際套用 [%d] 條。\n規則列表: %s",
                expectedCount, actualCount, actual.getAppliedRules()));
        }
        return this;
    }

    /**
     * 驗證明細項目存在且值正確
     */
    public CalculationResultAssert<T> hasBreakdown(String itemName, Object expectedValue) {
        Object actualValue = actual.getBreakdownValue(itemName);
        if (actualValue == null) {
            throw new AssertionError(String.format(
                "預期計算結果包含明細項目 [%s]，但未找到。\n已有的明細: %s",
                itemName, actual.getBreakdown()));
        }
        if (!Objects.equals(actualValue, expectedValue)) {
            throw new AssertionError(String.format(
                "明細項目 [%s] 預期值為 [%s]，但實際為 [%s]",
                itemName, expectedValue, actualValue));
        }
        return this;
    }

    /**
     * 驗證明細項目存在
     */
    public CalculationResultAssert<T> hasBreakdownItem(String itemName) {
        Object actualValue = actual.getBreakdownValue(itemName);
        if (actualValue == null) {
            throw new AssertionError(String.format(
                "預期計算結果包含明細項目 [%s]，但未找到。\n已有的明細: %s",
                itemName, actual.getBreakdown()));
        }
        return this;
    }

    /**
     * 驗證明細數量
     */
    public CalculationResultAssert<T> hasBreakdownCount(int expectedCount) {
        int actualCount = actual.getBreakdown().size();
        if (actualCount != expectedCount) {
            throw new AssertionError(String.format(
                "預期 [%d] 項明細，但實際有 [%d] 項。\n明細列表: %s",
                expectedCount, actualCount, actual.getBreakdown()));
        }
        return this;
    }

    /**
     * 驗證有元資料
     */
    public CalculationResultAssert<T> hasMetadata(String key, Object expectedValue) {
        Object actualValue = actual.getMetadata(key);
        if (!Objects.equals(actualValue, expectedValue)) {
            throw new AssertionError(String.format(
                "元資料 [%s] 預期值為 [%s]，但實際為 [%s]",
                key, expectedValue, actualValue));
        }
        return this;
    }

    /**
     * 驗證沒有明細
     */
    public CalculationResultAssert<T> hasNoBreakdown() {
        if (!actual.getBreakdown().isEmpty()) {
            throw new AssertionError(String.format(
                "預期無明細，但找到: %s",
                actual.getBreakdown()));
        }
        return this;
    }

    /**
     * 驗證沒有套用任何規則
     */
    public CalculationResultAssert<T> hasNoRules() {
        if (!actual.getAppliedRules().isEmpty()) {
            throw new AssertionError(String.format(
                "預期無套用規則，但找到: %s",
                actual.getAppliedRules()));
        }
        return this;
    }

    /**
     * 取得被測試的 CalculationResult
     */
    public CalculationResult<T> getResult() {
        return actual;
    }
}
