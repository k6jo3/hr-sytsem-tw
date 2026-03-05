package com.company.hrms.insurance.domain.model.valueobject;

import java.time.LocalDate;

/**
 * 加退保生效日期規則
 *
 * <p>依保險類型決定加保/退保的生效日期：
 * <ul>
 *   <li>勞保：到職日加保、離職日退保</li>
 *   <li>健保：到職日加保、離職日退保（月底離職者次月 1 日退保）</li>
 *   <li>勞退：同勞保</li>
 *   <li>團保：依合約指定日期生效</li>
 * </ul>
 */
public class EffectiveDateRule {

    private EffectiveDateRule() {
    }

    /**
     * 計算加保生效日
     *
     * @param insuranceType 保險類型
     * @param hireDate      到職日
     * @param contractDate  合約指定日（團保用，可為 null）
     * @return 加保生效日
     */
    public static LocalDate calculateEnrollDate(InsuranceType insuranceType,
            LocalDate hireDate, LocalDate contractDate) {
        if (insuranceType.isGroupInsurance()) {
            return contractDate != null ? contractDate : hireDate;
        }
        // 法定保險：一律以到職日生效
        return hireDate;
    }

    /**
     * 計算退保生效日
     *
     * @param insuranceType  保險類型
     * @param terminationDate 離職日
     * @return 退保生效日
     */
    public static LocalDate calculateWithdrawDate(InsuranceType insuranceType,
            LocalDate terminationDate) {
        if (insuranceType == InsuranceType.HEALTH) {
            // 健保：月底離職者次月 1 日退保
            if (isLastDayOfMonth(terminationDate)) {
                return terminationDate.plusDays(1);
            }
        }
        // 勞保/勞退/團保：離職日退保
        return terminationDate;
    }

    private static boolean isLastDayOfMonth(LocalDate date) {
        return date.getDayOfMonth() == date.lengthOfMonth();
    }
}
