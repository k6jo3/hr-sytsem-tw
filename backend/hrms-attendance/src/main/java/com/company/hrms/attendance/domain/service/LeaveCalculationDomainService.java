package com.company.hrms.attendance.domain.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
public class LeaveCalculationDomainService {

    public BigDecimal calculateTotalDays(LocalDate start, LocalDate end) {
        if (start == null || end == null)
            return BigDecimal.ZERO;

        BigDecimal days = BigDecimal.ZERO;
        LocalDate current = start;

        while (!current.isAfter(end)) {
            // Basic logic: Exclude weekends
            if (current.getDayOfWeek() != DayOfWeek.SATURDAY && current.getDayOfWeek() != DayOfWeek.SUNDAY) {
                days = days.add(BigDecimal.ONE);
            }
            current = current.plusDays(1);
        }

        return days;
    }
}
