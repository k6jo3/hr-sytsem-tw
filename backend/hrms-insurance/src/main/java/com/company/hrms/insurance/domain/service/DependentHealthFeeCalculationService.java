package com.company.hrms.insurance.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 眷屬健保費計算服務
 * 根據 2025 年健保法規，眷屬健保費計算公式
 */
@Service
@Slf4j
public class DependentHealthFeeCalculationService {

    // 2025 年健保費率
    private static final BigDecimal HEALTH_RATE = new BigDecimal("0.0517");

    // 個人負擔比例
    private static final BigDecimal EMPLOYEE_RATIO = new BigDecimal("0.30");

    // 雇主負擔比例
    private static final BigDecimal EMPLOYER_RATIO = new BigDecimal("0.60");

    // 眷屬費用由員工全額負擔
    private static final BigDecimal DEPENDENT_RATIO = new BigDecimal("0.30");

    // 平均眷口數上限 (超過部分不收費)
    private static final int MAX_DEPENDENTS = 3;

    /**
     * 計算眷屬健保費
     * 
     * @param monthlySalary  投保薪資
     * @param dependentCount 眷屬人數
     * @return 眷屬健保費 (由員工負擔)
     */
    public BigDecimal calculateDependentFee(BigDecimal monthlySalary, int dependentCount) {
        if (dependentCount <= 0) {
            return BigDecimal.ZERO;
        }

        // 實際計費眷口數 (上限 3 人)
        int effectiveCount = Math.min(dependentCount, MAX_DEPENDENTS);

        // 計算單人健保費 (員工負擔比例)
        BigDecimal singleFee = monthlySalary
                .multiply(HEALTH_RATE)
                .multiply(DEPENDENT_RATIO)
                .setScale(0, RoundingMode.HALF_UP);

        // 眷屬總費用
        BigDecimal totalFee = singleFee.multiply(BigDecimal.valueOf(effectiveCount));

        log.debug("眷屬健保費: salary={}, count={}, fee={}",
                monthlySalary, dependentCount, totalFee);

        return totalFee;
    }

    /**
     * 計算含眷屬的員工健保費總額
     * 
     * @param monthlySalary  投保薪資
     * @param dependentCount 眷屬人數
     * @return 員工 + 眷屬健保費總額
     */
    public BigDecimal calculateTotalHealthFee(BigDecimal monthlySalary, int dependentCount) {
        // 員工本人健保費
        BigDecimal employeeFee = monthlySalary
                .multiply(HEALTH_RATE)
                .multiply(EMPLOYEE_RATIO)
                .setScale(0, RoundingMode.HALF_UP);

        // 眷屬健保費
        BigDecimal dependentFee = calculateDependentFee(monthlySalary, dependentCount);

        return employeeFee.add(dependentFee);
    }

    /**
     * 計算雇主負擔健保費 (不含眷屬)
     */
    public BigDecimal calculateEmployerHealthFee(BigDecimal monthlySalary) {
        return monthlySalary
                .multiply(HEALTH_RATE)
                .multiply(EMPLOYER_RATIO)
                .setScale(0, RoundingMode.HALF_UP);
    }
}
