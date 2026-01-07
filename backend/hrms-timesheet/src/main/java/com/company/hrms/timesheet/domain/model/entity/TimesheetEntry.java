package com.company.hrms.timesheet.domain.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TimesheetEntry {
    private UUID id;
    private UUID projectId;
    private UUID taskId;
    private LocalDate workDate;
    private BigDecimal hours;
    private String description;
    private LocalDateTime createdAt;

    public static TimesheetEntry create(UUID projectId, UUID taskId, LocalDate workDate, BigDecimal hours,
            String description) {
        TimesheetEntry entry = new TimesheetEntry();
        entry.id = UUID.randomUUID();
        entry.projectId = projectId;
        entry.taskId = taskId;
        entry.workDate = workDate;
        entry.hours = hours;
        entry.description = description;
        entry.createdAt = LocalDateTime.now();
        return entry;
    }
}
