package com.company.hrms.payroll.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 資遣費計算 Domain Service
 *
 * 新制（勞退新制，2005/7/1 後）：
 * - 每滿 1 年發給 0.5 個月的平均工資
 * - 未滿 1 年以比例計算
 * - 最高發給 6 個月平均工資
 *
 * 平均工資 = 離職前 6 個月工資總額 / 6
 *
 * 此為純 Domain Service（Pure POJO），不依賴任何框架。
 */
public class SeverancePayCalculationDomainService {

    /** 新制每年發給倍數：0.5 個月 */
    private static final BigDecimal RATE_PER_YEAR = new BigDecimal("0.5");

    /** 上限：最高 6 個月平均工資 */
    private static final BigDecimal CAP_MONTHS = new BigDecimal("6");

    /** 一年的月數 */
    private static final BigDecimal MONTHS_PER_YEAR = new BigDecimal("12");

    /**
     * 計算資遣費（新制）
     *
     * @param serviceMonths       年資月數（不可為負）
     * @param averageMonthlySalary 平均月薪（離職前 6 個月工資總額 / 6）
     * @return 資遣費金額（四捨五入至整數）
     * @throws IllegalArgumentException 若 serviceMonths 為負或 averageMonthlySalary 為 null
     */
    public BigDecimal calculate(int serviceMonths, BigDecimal averageMonthlySalary) {
        if (serviceMonths < 0) {
            throw new IllegalArgumentException("年資月數不可為負數");
        }
        if (averageMonthlySalary == null) {
            throw new IllegalArgumentException("平均月薪不可為 null");
        }

        if (serviceMonths == 0 || averageMonthlySalary.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // 年資（年） = serviceMonths / 12
        BigDecimal years = BigDecimal.valueOf(serviceMonths)
                .divide(MONTHS_PER_YEAR, 4, RoundingMode.HALF_UP);

        // 資遣費 = 年資 x 0.5 x 平均月薪
        BigDecimal severance = years.multiply(RATE_PER_YEAR).multiply(averageMonthlySalary);

        // 上限 = 6 x 平均月薪
        BigDecimal cap = CAP_MONTHS.multiply(averageMonthlySalary);

        return severance.min(cap).setScale(0, RoundingMode.HALF_UP);
    }
}
