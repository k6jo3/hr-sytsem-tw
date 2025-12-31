package com.company.hrms.insurance.domain.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;

/**
 * 保險費用計算Domain Service
 * 計算勞保、健保、勞退費用
 */
@Service
public class InsuranceFeeCalculationService {

    // 2025年法定費率常數
    private static final BigDecimal LABOR_RATE = new BigDecimal("0.115"); // 勞保 11.5%
    private static final BigDecimal LABOR_EMPLOYEE_RATIO = new BigDecimal("0.20"); // 個人 20%
    private static final BigDecimal LABOR_EMPLOYER_RATIO = new BigDecimal("0.70"); // 雇主 70%

    private static final BigDecimal HEALTH_RATE = new BigDecimal("0.0517"); // 健保 5.17%
    private static final BigDecimal HEALTH_EMPLOYEE_RATIO = new BigDecimal("0.30"); // 個人 30%
    private static final BigDecimal HEALTH_EMPLOYER_RATIO = new BigDecimal("0.60"); // 雇主 60%

    private static final BigDecimal PENSION_EMPLOYER_RATE = new BigDecimal("0.06"); // 勞退 6%

    /**
     * 計算完整保險費用
     * 
     * @param level                投保級距
     * @param selfContributionRate 個人自提比例 (0~6%)，null 表示不自提
     */
    public InsuranceFees calculate(InsuranceLevel level, BigDecimal selfContributionRate) {
        BigDecimal salary = level.getMonthlySalary();

        // 勞保費用
        BigDecimal laborTotal = salary.multiply(LABOR_RATE);
        BigDecimal laborEmployee = laborTotal.multiply(LABOR_EMPLOYEE_RATIO);
        BigDecimal laborEmployer = laborTotal.multiply(LABOR_EMPLOYER_RATIO);

        // 健保費用
        BigDecimal healthTotal = salary.multiply(HEALTH_RATE);
        BigDecimal healthEmployee = healthTotal.multiply(HEALTH_EMPLOYEE_RATIO);
        BigDecimal healthEmployer = healthTotal.multiply(HEALTH_EMPLOYER_RATIO);

        // 勞退提繳
        BigDecimal pensionEmployer = salary.multiply(PENSION_EMPLOYER_RATE);

        // 個人自提 (選填)
        BigDecimal pensionSelf = BigDecimal.ZERO;
        if (selfContributionRate != null && selfContributionRate.compareTo(BigDecimal.ZERO) > 0) {
            pensionSelf = salary.multiply(selfContributionRate);
        }

        return new InsuranceFees(
                laborEmployee,
                laborEmployer,
                healthEmployee,
                healthEmployer,
                pensionEmployer,
                pensionSelf);
    }

    /**
     * 計算完整保險費用 (無個人自提)
     */
    public InsuranceFees calculate(InsuranceLevel level) {
        return calculate(level, null);
    }
}
