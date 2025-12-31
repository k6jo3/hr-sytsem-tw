package com.company.hrms.insurance.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;

/**
 * 投保級距聚合根
 */
public class InsuranceLevel {
    private final LevelId id;
    private final InsuranceType insuranceType;
    private final int levelNumber;
    private final BigDecimal monthlySalary;
    private BigDecimal laborEmployeeRate;
    private BigDecimal laborEmployerRate;
    private BigDecimal healthEmployeeRate;
    private BigDecimal healthEmployerRate;
    private BigDecimal pensionEmployerRate;
    private final LocalDate effectiveDate;
    private LocalDate endDate;
    private boolean isActive;

    public InsuranceLevel(
            LevelId id,
            InsuranceType insuranceType,
            int levelNumber,
            BigDecimal monthlySalary,
            LocalDate effectiveDate) {

        if (id == null)
            throw new IllegalArgumentException("LevelId cannot be null");
        if (insuranceType == null)
            throw new IllegalArgumentException("InsuranceType cannot be null");
        if (levelNumber <= 0)
            throw new IllegalArgumentException("LevelNumber must be positive");
        if (monthlySalary == null || monthlySalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("MonthlySalary must be positive");
        }
        if (effectiveDate == null)
            throw new IllegalArgumentException("EffectiveDate cannot be null");

        this.id = id;
        this.insuranceType = insuranceType;
        this.levelNumber = levelNumber;
        this.monthlySalary = monthlySalary;
        this.effectiveDate = effectiveDate;
        this.isActive = true;

        // 預設費率 (2025年)
        this.laborEmployeeRate = new BigDecimal("0.023"); // 11.5% × 20%
        this.laborEmployerRate = new BigDecimal("0.0805"); // 11.5% × 70%
        this.healthEmployeeRate = new BigDecimal("0.01551"); // 5.17% × 30%
        this.healthEmployerRate = new BigDecimal("0.03102"); // 5.17% × 60%
        this.pensionEmployerRate = new BigDecimal("0.06"); // 6%
    }

    /**
     * 設定勞保費率
     */
    public void setLaborRates(BigDecimal employeeRate, BigDecimal employerRate) {
        this.laborEmployeeRate = employeeRate;
        this.laborEmployerRate = employerRate;
    }

    /**
     * 設定健保費率
     */
    public void setHealthRates(BigDecimal employeeRate, BigDecimal employerRate) {
        this.healthEmployeeRate = employeeRate;
        this.healthEmployerRate = employerRate;
    }

    /**
     * 設定勞退提繳率
     */
    public void setPensionEmployerRate(BigDecimal rate) {
        this.pensionEmployerRate = rate;
    }

    /**
     * 設定結束日期 (級距失效)
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        if (endDate != null && endDate.isBefore(LocalDate.now())) {
            this.isActive = false;
        }
    }

    /**
     * 檢查是否在指定日期有效
     */
    public boolean isValidOn(LocalDate date) {
        if (!isActive)
            return false;
        if (date.isBefore(effectiveDate))
            return false;
        if (endDate != null && date.isAfter(endDate))
            return false;
        return true;
    }

    // Getters
    public LevelId getId() {
        return id;
    }

    public InsuranceType getInsuranceType() {
        return insuranceType;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }

    public BigDecimal getLaborEmployeeRate() {
        return laborEmployeeRate;
    }

    public BigDecimal getLaborEmployerRate() {
        return laborEmployerRate;
    }

    public BigDecimal getHealthEmployeeRate() {
        return healthEmployeeRate;
    }

    public BigDecimal getHealthEmployerRate() {
        return healthEmployerRate;
    }

    public BigDecimal getPensionEmployerRate() {
        return pensionEmployerRate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return isActive;
    }
}
