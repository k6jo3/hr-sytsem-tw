package com.company.hrms.insurance.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 保險費用 Value Object
 * 封裝勞保/健保/勞退/職災/就業保險費用計算結果
 *
 * [2026-03-17 更新]
 * - 進位方式由 CEILING 改為 HALF_UP（四捨五入）
 * - 新增 occupationalAccidentFee（職災保險費，雇主全額負擔）
 * - 新增 employmentInsuranceEmployeeFee / employmentInsuranceEmployerFee（就業保險費）
 */
public class InsuranceFees {
    private final BigDecimal laborEmployeeFee;
    private final BigDecimal laborEmployerFee;
    private final BigDecimal healthEmployeeFee;
    private final BigDecimal healthEmployerFee;
    private final BigDecimal pensionEmployerFee;
    private final BigDecimal pensionSelfContribution;
    private final BigDecimal occupationalAccidentFee;
    private final BigDecimal employmentInsuranceEmployeeFee;
    private final BigDecimal employmentInsuranceEmployerFee;

    /**
     * 完整建構子（含職災與就業保險）
     */
    public InsuranceFees(
            BigDecimal laborEmployeeFee,
            BigDecimal laborEmployerFee,
            BigDecimal healthEmployeeFee,
            BigDecimal healthEmployerFee,
            BigDecimal pensionEmployerFee,
            BigDecimal pensionSelfContribution,
            BigDecimal occupationalAccidentFee,
            BigDecimal employmentInsuranceEmployeeFee,
            BigDecimal employmentInsuranceEmployerFee) {
        this.laborEmployeeFee = round(laborEmployeeFee);
        this.laborEmployerFee = round(laborEmployerFee);
        this.healthEmployeeFee = round(healthEmployeeFee);
        this.healthEmployerFee = round(healthEmployerFee);
        this.pensionEmployerFee = round(pensionEmployerFee);
        this.pensionSelfContribution = pensionSelfContribution != null
                ? round(pensionSelfContribution)
                : BigDecimal.ZERO;
        this.occupationalAccidentFee = round(occupationalAccidentFee);
        this.employmentInsuranceEmployeeFee = round(employmentInsuranceEmployeeFee);
        this.employmentInsuranceEmployerFee = round(employmentInsuranceEmployerFee);
    }

    /**
     * 向下相容建構子（不含職災與就業保險，預設為 0）
     */
    public InsuranceFees(
            BigDecimal laborEmployeeFee,
            BigDecimal laborEmployerFee,
            BigDecimal healthEmployeeFee,
            BigDecimal healthEmployerFee,
            BigDecimal pensionEmployerFee,
            BigDecimal pensionSelfContribution) {
        this(laborEmployeeFee, laborEmployerFee,
                healthEmployeeFee, healthEmployerFee,
                pensionEmployerFee, pensionSelfContribution,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * 四捨五入至整數
     */
    private static BigDecimal round(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 員工每月負擔總計
     * = 勞保員工 + 健保員工 + 就業保險員工 + 自提
     */
    public BigDecimal getTotalEmployeeFee() {
        return laborEmployeeFee
                .add(healthEmployeeFee)
                .add(employmentInsuranceEmployeeFee)
                .add(pensionSelfContribution);
    }

    /**
     * 雇主每月負擔總計
     * = 勞保雇主 + 健保雇主 + 勞退 + 職災 + 就業保險雇主
     */
    public BigDecimal getTotalEmployerFee() {
        return laborEmployerFee
                .add(healthEmployerFee)
                .add(pensionEmployerFee)
                .add(occupationalAccidentFee)
                .add(employmentInsuranceEmployerFee);
    }

    // Getters
    public BigDecimal getLaborEmployeeFee() {
        return laborEmployeeFee;
    }

    public BigDecimal getLaborEmployerFee() {
        return laborEmployerFee;
    }

    public BigDecimal getHealthEmployeeFee() {
        return healthEmployeeFee;
    }

    public BigDecimal getHealthEmployerFee() {
        return healthEmployerFee;
    }

    public BigDecimal getPensionEmployerFee() {
        return pensionEmployerFee;
    }

    public BigDecimal getPensionSelfContribution() {
        return pensionSelfContribution;
    }

    public BigDecimal getOccupationalAccidentFee() {
        return occupationalAccidentFee;
    }

    public BigDecimal getEmploymentInsuranceEmployeeFee() {
        return employmentInsuranceEmployeeFee;
    }

    public BigDecimal getEmploymentInsuranceEmployerFee() {
        return employmentInsuranceEmployerFee;
    }
}
