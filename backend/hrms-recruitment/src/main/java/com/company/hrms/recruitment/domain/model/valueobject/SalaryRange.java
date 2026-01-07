package com.company.hrms.recruitment.domain.model.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 薪資範圍 Value Object
 */
public class SalaryRange {

    private final BigDecimal min;
    private final BigDecimal max;
    private final String currency;

    private SalaryRange(BigDecimal min, BigDecimal max, String currency) {
        this.min = min;
        this.max = max;
        this.currency = currency;
    }

    /**
     * 建立薪資範圍
     */
    public static SalaryRange of(BigDecimal min, BigDecimal max, String currency) {
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new IllegalArgumentException("最低薪資不可大於最高薪資");
        }
        if (min != null && min.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("最低薪資不可為負數");
        }
        return new SalaryRange(min, max, currency != null ? currency : "TWD");
    }

    /**
     * 建立預設幣別(TWD)的薪資範圍
     */
    public static SalaryRange of(BigDecimal min, BigDecimal max) {
        return of(min, max, "TWD");
    }

    /**
     * 檢查薪資是否在範圍內
     */
    public boolean contains(BigDecimal salary) {
        if (salary == null) {
            return false;
        }
        boolean aboveMin = min == null || salary.compareTo(min) >= 0;
        boolean belowMax = max == null || salary.compareTo(max) <= 0;
        return aboveMin && belowMax;
    }

    /**
     * 格式化顯示（如 "60K-80K"）
     */
    public String toDisplayString() {
        if (min == null && max == null) {
            return "面議";
        }

        String minStr = min != null ? formatAmount(min) : "";
        String maxStr = max != null ? formatAmount(max) : "";

        if (min != null && max != null) {
            return minStr + "-" + maxStr;
        } else if (min != null) {
            return minStr + "以上";
        } else {
            return maxStr + "以下";
        }
    }

    private String formatAmount(BigDecimal amount) {
        // 簡化顯示：將金額轉換為 K 表示
        BigDecimal thousands = amount.divide(new BigDecimal("1000"));
        if (thousands.scale() <= 0) {
            return thousands.intValue() + "K";
        }
        return thousands.stripTrailingZeros().toPlainString() + "K";
    }

    // === Getters ===

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SalaryRange that = (SalaryRange) o;
        return Objects.equals(min, that.min) &&
                Objects.equals(max, that.max) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, currency);
    }

    @Override
    public String toString() {
        return "SalaryRange{" +
                "min=" + min +
                ", max=" + max +
                ", currency='" + currency + '\'' +
                '}';
    }
}
