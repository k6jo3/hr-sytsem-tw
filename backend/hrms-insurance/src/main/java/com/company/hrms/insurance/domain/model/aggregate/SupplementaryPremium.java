package com.company.hrms.insurance.domain.model.aggregate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.company.hrms.insurance.domain.model.valueobject.IncomeType;
import com.company.hrms.insurance.domain.model.valueobject.PremiumId;

/**
 * 二代健保補充保費聚合根
 */
public class SupplementaryPremium {
    private static final BigDecimal SUPPLEMENTARY_RATE = new BigDecimal("0.0211"); // 2.11%
    private static final BigDecimal MAX_BASE = new BigDecimal("10000000"); // 上限1000萬
    private static final int THRESHOLD_MULTIPLIER = 4; // 門檻 = 投保金額 × 4

    private final PremiumId id;
    private final String employeeId;
    private final IncomeType incomeType;
    private final LocalDate incomeDate;
    private final BigDecimal incomeAmount;
    private final BigDecimal insuredSalary;
    private final BigDecimal threshold;
    private final BigDecimal premiumBase;
    private final BigDecimal premiumAmount;
    private final int year;
    private final int month;

    private SupplementaryPremium(
            PremiumId id,
            String employeeId,
            IncomeType incomeType,
            LocalDate incomeDate,
            BigDecimal incomeAmount,
            BigDecimal insuredSalary,
            BigDecimal threshold,
            BigDecimal premiumBase,
            BigDecimal premiumAmount) {

        this.id = id;
        this.employeeId = employeeId;
        this.incomeType = incomeType;
        this.incomeDate = incomeDate;
        this.incomeAmount = incomeAmount;
        this.insuredSalary = insuredSalary;
        this.threshold = threshold;
        this.premiumBase = premiumBase;
        this.premiumAmount = premiumAmount;
        this.year = incomeDate.getYear();
        this.month = incomeDate.getMonthValue();
    }

    /**
     * 計算補充保費
     * 
     * @return null 如果不需要繳納補充保費
     */
    public static SupplementaryPremium calculate(
            String employeeId,
            IncomeType incomeType,
            LocalDate incomeDate,
            BigDecimal incomeAmount,
            BigDecimal insuredSalary) {

        if (employeeId == null || employeeId.isBlank())
            throw new IllegalArgumentException("EmployeeId cannot be null or blank");
        if (incomeType == null)
            throw new IllegalArgumentException("IncomeType cannot be null");
        if (incomeDate == null)
            throw new IllegalArgumentException("IncomeDate cannot be null");
        if (incomeAmount == null || incomeAmount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("IncomeAmount must be positive");
        if (insuredSalary == null || insuredSalary.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("InsuredSalary must be positive");

        // 計算門檻 = 投保金額 × 4
        BigDecimal threshold = insuredSalary.multiply(BigDecimal.valueOf(THRESHOLD_MULTIPLIER));

        // 若收入未超過門檻，不需繳納補充保費
        if (incomeAmount.compareTo(threshold) <= 0) {
            return null;
        }

        // 計費基準 = 收入 - 門檻
        BigDecimal premiumBase = incomeAmount.subtract(threshold);

        // 上限處理
        if (premiumBase.compareTo(MAX_BASE) > 0) {
            premiumBase = MAX_BASE;
        }

        // 補充保費 = 計費基準 × 2.11% (無條件進位至整數)
        BigDecimal premiumAmount = premiumBase.multiply(SUPPLEMENTARY_RATE)
                .setScale(0, RoundingMode.CEILING);

        return new SupplementaryPremium(
                PremiumId.generate(),
                employeeId,
                incomeType,
                incomeDate,
                incomeAmount,
                insuredSalary,
                threshold,
                premiumBase,
                premiumAmount);
    }

    /**
     * 檢查是否需要繳納補充保費
     */
    public static boolean needsSupplementaryPremium(BigDecimal incomeAmount, BigDecimal insuredSalary) {
        BigDecimal threshold = insuredSalary.multiply(BigDecimal.valueOf(THRESHOLD_MULTIPLIER));
        return incomeAmount.compareTo(threshold) > 0;
    }

    // Getters
    public PremiumId getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public IncomeType getIncomeType() {
        return incomeType;
    }

    public LocalDate getIncomeDate() {
        return incomeDate;
    }

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public BigDecimal getInsuredSalary() {
        return insuredSalary;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public BigDecimal getPremiumBase() {
        return premiumBase;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }
}
