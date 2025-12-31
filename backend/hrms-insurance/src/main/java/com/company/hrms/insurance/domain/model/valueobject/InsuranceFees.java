package com.company.hrms.insurance.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 保險費用Value Object
 * 封裝勞保/健保/勞退費用計算結果
 */
public class InsuranceFees {
    private final BigDecimal laborEmployeeFee;
    private final BigDecimal laborEmployerFee;
    private final BigDecimal healthEmployeeFee;
    private final BigDecimal healthEmployerFee;
    private final BigDecimal pensionEmployerFee;
    private final BigDecimal pensionSelfContribution;

    public InsuranceFees(
            BigDecimal laborEmployeeFee,
            BigDecimal laborEmployerFee,
            BigDecimal healthEmployeeFee,
            BigDecimal healthEmployerFee,
            BigDecimal pensionEmployerFee,
            BigDecimal pensionSelfContribution) {
        this.laborEmployeeFee = laborEmployeeFee.setScale(0, RoundingMode.CEILING);
        this.laborEmployerFee = laborEmployerFee.setScale(0, RoundingMode.CEILING);
        this.healthEmployeeFee = healthEmployeeFee.setScale(0, RoundingMode.CEILING);
        this.healthEmployerFee = healthEmployerFee.setScale(0, RoundingMode.CEILING);
        this.pensionEmployerFee = pensionEmployerFee.setScale(0, RoundingMode.CEILING);
        this.pensionSelfContribution = pensionSelfContribution != null
                ? pensionSelfContribution.setScale(0, RoundingMode.CEILING)
                : BigDecimal.ZERO;
    }

    /**
     * 員工每月負擔總計
     */
    public BigDecimal getTotalEmployeeFee() {
        return laborEmployeeFee
                .add(healthEmployeeFee)
                .add(pensionSelfContribution);
    }

    /**
     * 雇主每月負擔總計
     */
    public BigDecimal getTotalEmployerFee() {
        return laborEmployerFee
                .add(healthEmployerFee)
                .add(pensionEmployerFee);
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
}
