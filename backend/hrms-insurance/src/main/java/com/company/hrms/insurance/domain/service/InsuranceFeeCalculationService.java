package com.company.hrms.insurance.domain.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;

/**
 * 保險費用計算 Domain Service
 * 計算勞保（普通事故）、就業保險、職災保險、健保、勞退費用
 *
 * [2026-03-17 更新]
 * - 勞保費率拆分：普通事故 10.5%、就業保險 1%
 * - 新增職災保險：預設 0.21%，雇主 100% 負擔（可配置）
 * - 進位方式由 CEILING 改為 HALF_UP（在 InsuranceFees 中處理）
 * - 健保雇主費增加平均眷口數乘數 (1 + averageDependents)
 * - 新增 isSenior 參數：65 歲以上免勞保普通事故與就業保險
 */
@Service
public class InsuranceFeeCalculationService {

    // === 勞保普通事故費率（2026 年） ===
    private static final BigDecimal LABOR_ORDINARY_RATE = new BigDecimal("0.105"); // 10.5%
    private static final BigDecimal LABOR_EMPLOYEE_RATIO = new BigDecimal("0.20"); // 個人 20%
    private static final BigDecimal LABOR_EMPLOYER_RATIO = new BigDecimal("0.70"); // 雇主 70%

    // === 就業保險費率 ===
    private static final BigDecimal EMPLOYMENT_INSURANCE_RATE = new BigDecimal("0.01"); // 1%
    private static final BigDecimal EMPLOYMENT_EMPLOYEE_RATIO = new BigDecimal("0.20"); // 個人 20%
    private static final BigDecimal EMPLOYMENT_EMPLOYER_RATIO = new BigDecimal("0.70"); // 雇主 70%

    // === 健保費率 ===
    private static final BigDecimal HEALTH_RATE = new BigDecimal("0.0517"); // 健保 5.17%
    private static final BigDecimal HEALTH_EMPLOYEE_RATIO = new BigDecimal("0.30"); // 個人 30%
    private static final BigDecimal HEALTH_EMPLOYER_RATIO = new BigDecimal("0.60"); // 雇主 60%

    // === 勞退提繳率 ===
    private static final BigDecimal PENSION_EMPLOYER_RATE = new BigDecimal("0.06"); // 6%

    /**
     * 職災保險費率（可配置，預設 0.21%）
     * 各行業別費率不同，由外部配置注入
     */
    @Value("${insurance.occupational-accident.rate:0.0021}")
    private BigDecimal occupationalAccidentRate = new BigDecimal("0.0021");

    /**
     * 健保平均眷口數（可配置，預設 0.57）
     * 雇主負擔 = 保費 × 雇主比例 × (1 + 平均眷口數)
     */
    @Value("${insurance.health.average-dependents:0.57}")
    private BigDecimal averageDependents = new BigDecimal("0.57");

    /**
     * 計算完整保險費用（一般勞工，未滿 65 歲）
     *
     * @param level                投保級距
     * @param selfContributionRate 個人自提比例 (0~6%)，null 表示不自提
     */
    public InsuranceFees calculate(InsuranceLevel level, BigDecimal selfContributionRate) {
        return calculate(level, selfContributionRate, false);
    }

    /**
     * 計算完整保險費用（無個人自提，未滿 65 歲）
     */
    public InsuranceFees calculate(InsuranceLevel level) {
        return calculate(level, null, false);
    }

    /**
     * 計算完整保險費用
     *
     * @param level                投保級距
     * @param selfContributionRate 個人自提比例 (0~6%)，null 表示不自提
     * @param isSenior             是否為 65 歲以上（含）勞工
     */
    public InsuranceFees calculate(InsuranceLevel level, BigDecimal selfContributionRate, boolean isSenior) {
        BigDecimal salary = level.getMonthlySalary();

        // === 勞保普通事故費用（65 歲以上免投保） ===
        BigDecimal laborEmployee = BigDecimal.ZERO;
        BigDecimal laborEmployer = BigDecimal.ZERO;
        if (!isSenior) {
            BigDecimal laborTotal = salary.multiply(LABOR_ORDINARY_RATE);
            laborEmployee = laborTotal.multiply(LABOR_EMPLOYEE_RATIO);
            laborEmployer = laborTotal.multiply(LABOR_EMPLOYER_RATIO);
        }

        // === 就業保險費用（65 歲以上免投保） ===
        BigDecimal employmentEmployee = BigDecimal.ZERO;
        BigDecimal employmentEmployer = BigDecimal.ZERO;
        if (!isSenior) {
            BigDecimal employmentTotal = salary.multiply(EMPLOYMENT_INSURANCE_RATE);
            employmentEmployee = employmentTotal.multiply(EMPLOYMENT_EMPLOYEE_RATIO);
            employmentEmployer = employmentTotal.multiply(EMPLOYMENT_EMPLOYER_RATIO);
        }

        // === 職災保險費用（雇主 100% 負擔，不分年齡均須投保） ===
        BigDecimal occupationalAccidentFee = salary.multiply(occupationalAccidentRate);

        // === 健保費用 ===
        BigDecimal healthTotal = salary.multiply(HEALTH_RATE);
        BigDecimal healthEmployee = healthTotal.multiply(HEALTH_EMPLOYEE_RATIO);
        // 雇主負擔含平均眷口數乘數：保費 × 雇主比例 × (1 + 平均眷口數)
        BigDecimal healthEmployerMultiplier = BigDecimal.ONE.add(averageDependents);
        BigDecimal healthEmployer = healthTotal.multiply(HEALTH_EMPLOYER_RATIO).multiply(healthEmployerMultiplier);

        // === 勞退提繳 ===
        BigDecimal pensionEmployer = salary.multiply(PENSION_EMPLOYER_RATE);

        // === 個人自提（選填） ===
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
                pensionSelf,
                occupationalAccidentFee,
                employmentEmployee,
                employmentEmployer);
    }
}
