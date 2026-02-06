package com.company.hrms.timesheet.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "timesheet_entries")
@Data
public class TimesheetEntryEntity {

    @Id
    @Column(name = "entry_id")
    private UUID entryId;

    @Column(name = "timesheet_id", insertable = false, updatable = false)
    private UUID timesheetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesheet_id", insertable = false, updatable = false)
    private TimesheetEntity timesheet;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "task_id")
    private UUID taskId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "hours", nullable = false)
    private BigDecimal hours;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
