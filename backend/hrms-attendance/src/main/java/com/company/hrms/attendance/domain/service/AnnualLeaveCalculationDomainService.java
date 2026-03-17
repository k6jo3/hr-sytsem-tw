package com.company.hrms.attendance.domain.service;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.domain.model.aggregate.AnnualLeavePolicy;

/**
 * 特休額度計算 Domain Service
 *
 * <p>依據企業自訂的 AnnualLeavePolicy 計算員工特休額度。
 * 若需使用法定基準，請搭配 {@link StatutoryAnnualLeaveCalculator}。
 */
@Service
public class AnnualLeaveCalculationDomainService {

    /**
     * 依年數計算特休額度（向後相容）
     *
     * @param policy         特休政策
     * @param yearsOfService 年資年數
     * @return 特休天數
     */
    public int calculateEntitlement(AnnualLeavePolicy policy, int yearsOfService) {
        if (policy == null || !policy.isActive())
            return 0;
        return policy.calculateDays(yearsOfService);
    }

    /**
     * 依月數計算特休額度
     *
     * @param policy        特休政策
     * @param serviceMonths 年資月數
     * @return 特休天數
     */
    public int calculateEntitlementByMonths(AnnualLeavePolicy policy, int serviceMonths) {
        if (policy == null || !policy.isActive())
            return 0;
        return policy.calculateDaysByMonths(serviceMonths);
    }
}
