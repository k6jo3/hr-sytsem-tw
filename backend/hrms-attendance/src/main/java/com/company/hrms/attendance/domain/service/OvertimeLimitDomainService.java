package com.company.hrms.attendance.domain.service;

import org.springframework.stereotype.Service;

@Service
public class OvertimeLimitDomainService {

    private static final double MONTHLY_LIMIT_HOURS = 46.0;

    public boolean isLimitExceeded(Double currentMonthHours, Double newRequestHours) {
        return (currentMonthHours + newRequestHours) > MONTHLY_LIMIT_HOURS;
    }
}
