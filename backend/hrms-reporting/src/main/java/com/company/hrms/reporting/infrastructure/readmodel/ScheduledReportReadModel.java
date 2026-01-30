package com.company.hrms.reporting.infrastructure.readmodel;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排程報表讀模型
 */
@Entity
@Table(name = "rm_scheduled_report", indexes = {
        @Index(name = "idx_sched_tenant", columnList = "tenant_id"),
        @Index(name = "idx_sched_name", columnList = "schedule_name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledReportReadModel {

    @Id
    @Column(name = "id", length = 50)
    private String id; // scheduleId

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "schedule_name", length = 100)
    private String scheduleName;

    @Column(name = "report_type", length = 50)
    private String reportType;

    @Column(name = "cron_expression", length = 50)
    private String cronExpression;

    @Column(name = "next_run_time")
    private LocalDateTime nextRunTime;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
