package com.company.hrms.insurance.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 眷屬健保費計算服務
 * 根據全民健康保險法規，眷屬健保費計算公式
 * [2026-03-17 更新] 雇主健保費須乘以 (1 + 平均眷口數)
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

    // 眷屬費用由員工負擔比例（同個人負擔比例）
    private static final BigDecimal DEPENDENT_RATIO = new BigDecimal("0.30");

    // 眷屬計費上限（超過部分不收費）
    private static final int MAX_DEPENDENTS = 3;

    /**
     * 平均眷口數，依衛生福利部公告
     * 雇主負擔健保費 = 投保薪資 × 健保費率 × 雇主負擔比例 × (1 + 平均眷口數)
     * 預設值 0.57（可透過 application.yml 設定覆寫）
     */
    @Value("${insurance.health.average-dependent-ratio:0.57}")
    private BigDecimal averageDependentRatio = new BigDecimal("0.57");

    /**
     * 供測試或外部呼叫使用的建構子，可注入平均眷口數
     */
    public DependentHealthFeeCalculationService(BigDecimal averageDependentRatio) {
        this.averageDependentRatio = averageDependentRatio;
    }

    /**
     * 預設建構子（使用預設平均眷口數 0.57）
     */
    public DependentHealthFeeCalculationService() {
        this.averageDependentRatio = new BigDecimal("0.57");
    }

    /**
     * 計算眷屬健保費（由員工負擔）
     *
     * @param monthlySalary  投保薪資
     * @param dependentCount 眷屬人數
     * @return 眷屬健保費（由員工負擔）
     */
    public BigDecimal calculateDependentFee(BigDecimal monthlySalary, int dependentCount) {
        if (dependentCount <= 0) {
            return BigDecimal.ZERO;
        }

        // 實際計費眷口數（上限 3 人）
        int effectiveCount = Math.min(dependentCount, MAX_DEPENDENTS);

        // 計算單人健保費（員工負擔比例）
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
     * 計算雇主負擔健保費
     * [2026-03-17 更新] 公式：投保薪資 × 健保費率 × 雇主負擔比例 × (1 + 平均眷口數)
     * 平均眷口數依衛生福利部公告，預設為 0.57
     * 進位方式：HALF_UP（四捨五入）
     *
     * @param monthlySalary 投保薪資
     * @return 雇主負擔健保費（含平均眷口數）
     */
    public BigDecimal calculateEmployerHealthFee(BigDecimal monthlySalary) {
        // (1 + 平均眷口數)
        BigDecimal dependentMultiplier = BigDecimal.ONE.add(averageDependentRatio);

        return monthlySalary
                .multiply(HEALTH_RATE)
                .multiply(EMPLOYER_RATIO)
                .multiply(dependentMultiplier)
                .setScale(0, RoundingMode.HALF_UP);
    }
}
