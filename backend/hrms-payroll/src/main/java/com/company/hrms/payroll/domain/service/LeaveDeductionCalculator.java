package com.company.hrms.payroll.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

/**
 * 請假扣薪計算器 Domain Service
 * 負責計算因請假產生的扣薪金額
 */
@Service
public class LeaveDeductionCalculator {

    /**
     * 計算請假扣款
     * 
     * @param hourlyRate       時薪
     * @param unpaidLeaveHours 事假/無薪假時數 (扣全薪)
     * @param sickLeaveHours   病假時數 (扣半薪)
     * @return 總扣款金額
     */
    public BigDecimal calculate(BigDecimal hourlyRate,
            BigDecimal unpaidLeaveHours,
            BigDecimal sickLeaveHours) {

        if (hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal deduction = BigDecimal.ZERO;

        // 事假/無薪假：扣 100%
        if (unpaidLeaveHours != null && unpaidLeaveHours.compareTo(BigDecimal.ZERO) > 0) {
            deduction = deduction.add(hourlyRate.multiply(unpaidLeaveHours));
        }

        // 病假：扣 50% (即發半薪)
        // 注意：勞基法規定一年內未超過 30 天部分，工資折半發給 => 扣除 50%
        if (sickLeaveHours != null && sickLeaveHours.compareTo(BigDecimal.ZERO) > 0) {
            deduction = deduction.add(hourlyRate.multiply(sickLeaveHours)
                    .multiply(new BigDecimal("0.5")));
        }

        return deduction.setScale(0, RoundingMode.HALF_UP);
    }
}
