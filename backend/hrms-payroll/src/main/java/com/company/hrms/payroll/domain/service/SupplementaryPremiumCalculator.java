package com.company.hrms.payroll.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

/**
 * 二代健保補充保費計算器
 *
 * 補充保費規則（獎金類）：
 * - 單次獎金超過投保薪資 4 倍時，超出部分需計算補充保費
 * - 補充保費 = (獎金金額 - 投保薪資 x 4) x 費率
 * - 若差額為負或零，則不收取補充保費
 * - 現行費率：2.11%
 */
@Component
public class SupplementaryPremiumCalculator {

    /** 二代健保補充保費費率 2.11% */
    private static final BigDecimal RATE = new BigDecimal("0.0211");

    /** 獎金門檻倍數：投保薪資 4 倍 */
    private static final BigDecimal THRESHOLD_MULTIPLIER = new BigDecimal("4");

    /**
     * 計算獎金類補充保費
     *
     * @param bonusAmount   獎金金額（可為 null，視為 0）
     * @param insuredSalary 投保薪資（不可為 null）
     * @return 補充保費金額（四捨五入至整數），若未超過門檻則為 0
     * @throws IllegalArgumentException 若 insuredSalary 為 null
     */
    public BigDecimal calculateBonusPremium(BigDecimal bonusAmount, BigDecimal insuredSalary) {
        if (insuredSalary == null) {
            throw new IllegalArgumentException("投保薪資不可為 null");
        }
        if (bonusAmount == null || bonusAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 門檻 = 投保薪資 x 4
        BigDecimal threshold = insuredSalary.multiply(THRESHOLD_MULTIPLIER);

        // 差額 = 獎金 - 門檻
        BigDecimal excess = bonusAmount.subtract(threshold);

        if (excess.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 補充保費 = 差額 x 2.11%
        return excess.multiply(RATE).setScale(0, RoundingMode.HALF_UP);
    }
}
