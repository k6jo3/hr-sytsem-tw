package com.company.hrms.attendance.domain.service;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.domain.model.aggregate.AnnualLeavePolicy;

@Service
public class AnnualLeaveCalculationDomainService {

    public int calculateEntitlement(AnnualLeavePolicy policy, int yearsOfService) {
        if (policy == null || !policy.isActive())
            return 0;
        return policy.calculateDays(yearsOfService);
    }
}
