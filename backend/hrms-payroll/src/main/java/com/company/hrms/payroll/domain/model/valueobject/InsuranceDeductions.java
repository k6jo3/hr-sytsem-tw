package com.company.hrms.payroll.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 保險扣除值物件
 * 封裝勞保、健保、勞退自提的扣除金額
 */
@Getter
@EqualsAndHashCode
@Builder
public class InsuranceDeductions {

    /**
     * 勞保費 (員工自付)
     */
    @Builder.Default
    private final BigDecimal laborInsurance = BigDecimal.ZERO;

    /**
     * 健保費 (員工自付)
     */
    @Builder.Default
    private final BigDecimal healthInsurance = BigDecimal.ZERO;

    /**
     * 勞退自提
     */
    @Builder.Default
    private final BigDecimal pensionSelfContribution = BigDecimal.ZERO;

    /**
     * 二代健保補充保費
     */
    @Builder.Default
    private final BigDecimal supplementaryPremium = BigDecimal.ZERO;

    /**
     * 計算保險費總額
     * 
     * @return 保險費總額
     */
    public BigDecimal getTotal() {
        return laborInsurance
                .add(healthInsurance)
                .add(pensionSelfContribution)
                .add(supplementaryPremium)
                .setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 建立空的保險扣除
     * 
     * @return 空的保險扣除
     */
    public static InsuranceDeductions empty() {
        return InsuranceDeductions.builder().build();
    }

    /**
     * 從各項金額建立保險扣除
     * 
     * @param laborInsurance          勞保費
     * @param healthInsurance         健保費
     * @param pensionSelfContribution 勞退自提
     * @param supplementaryPremium    補充保費
     * @return 保險扣除
     */
    public static InsuranceDeductions of(
            BigDecimal laborInsurance,
            BigDecimal healthInsurance,
            BigDecimal pensionSelfContribution,
            BigDecimal supplementaryPremium) {
        return InsuranceDeductions.builder()
                .laborInsurance(laborInsurance != null ? laborInsurance : BigDecimal.ZERO)
                .healthInsurance(healthInsurance != null ? healthInsurance : BigDecimal.ZERO)
                .pensionSelfContribution(pensionSelfContribution != null ? pensionSelfContribution : BigDecimal.ZERO)
                .supplementaryPremium(supplementaryPremium != null ? supplementaryPremium : BigDecimal.ZERO)
                .build();
    }
}
