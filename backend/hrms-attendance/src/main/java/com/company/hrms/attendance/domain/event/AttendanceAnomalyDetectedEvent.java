package com.company.hrms.attendance.domain.event;

import java.time.LocalDate;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class AttendanceAnomalyDetectedEvent extends DomainEvent {
    private final String recordId;
    private final String employeeId;
    private final LocalDate recordDate;
    private final String anomalyType; // String from Enum
    private final int anomalyMinutes;

    public AttendanceAnomalyDetectedEvent(String recordId, String employeeId, LocalDate recordDate,
            String anomalyType, int anomalyMinutes) {
        super();
        this.recordId = recordId;
        this.employeeId = employeeId;
        this.recordDate = recordDate;
        this.anomalyType = anomalyType;
        this.anomalyMinutes = anomalyMinutes;
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
