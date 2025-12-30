package com.company.hrms.attendance.domain.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.Shift;

@Service
public class AttendanceCalculationDomainService {

    /**
     * Calculate actual working hours based on check-in/out and shift break times.
     */
    public double calculateWorkingHours(AttendanceRecord record, Shift shift) {
        if (record.getCheckInTime() == null || record.getCheckOutTime() == null) {
            return 0.0;
        }

        LocalDateTime start = record.getCheckInTime();
        LocalDateTime end = record.getCheckOutTime();

        // If check-in is before shift start, count from shift start?
        // Usually, effective work time starts at Shift Start unless early shift is
        // approved.
        // For simplicity, let's just take raw check-in/out duration first, then
        // subtract break.
        // Better: Use Shift Rules (e.g. only count from Shift Start).
        // Let's implement simple duration - break.

        long minutes = Duration.between(start, end).toMinutes();

        // Deduct break if overlap
        // This is complex if break is fixed time (12:00-13:00) vs flexible.
        // Shift has `breakStartTime` and `breakEndTime`.
        if (shift.getBreakStartTime() != null && shift.getBreakEndTime() != null) {
            LocalDateTime breakStart = LocalDateTime.of(record.getDate(), shift.getBreakStartTime());
            LocalDateTime breakEnd = LocalDateTime.of(record.getDate(), shift.getBreakEndTime());

            // If the work period fully covers the break
            if (start.isBefore(breakStart) && end.isAfter(breakEnd)) {
                minutes -= Duration.between(breakStart, breakEnd).toMinutes();
            }
            // Partial overlap logic would go here
        }

        return minutes / 60.0;
    }
}
