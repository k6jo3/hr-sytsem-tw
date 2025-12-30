package com.company.hrms.attendance.domain.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class AttendanceRecordedEvent extends DomainEvent {
    private final String recordId;
    private final String employeeId;
    private final LocalDate recordDate;
    private final LocalDateTime checkInTime;
    private final LocalDateTime checkOutTime;
    private final boolean isLate;
    private final boolean isEarlyLeave;

    public AttendanceRecordedEvent(String recordId, String employeeId, LocalDate recordDate,
            LocalDateTime checkInTime, LocalDateTime checkOutTime,
            boolean isLate, boolean isEarlyLeave) {
        super();
        this.recordId = recordId;
        this.employeeId = employeeId;
        this.recordDate = recordDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.isLate = isLate;
        this.isEarlyLeave = isEarlyLeave;
    }

    @Override
    public String getAggregateId() {
        return recordId;
    }

    @Override
    public String getAggregateType() {
        return "AttendanceRecord";
    }
}
