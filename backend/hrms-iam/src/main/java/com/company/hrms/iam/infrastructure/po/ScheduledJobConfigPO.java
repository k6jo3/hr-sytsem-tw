package com.company.hrms.iam.infrastructure.po;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排程任務配置持久化物件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheduled_job_configs")
public class ScheduledJobConfigPO {

    @Id
    private String id;

    private String jobCode;
    private String jobName;
    private String module;
    private String cronExpression;
    private Boolean enabled;
    private String description;
    private Timestamp lastExecutedAt;
    private String lastExecutionStatus;
    private String lastErrorMessage;
    private Integer consecutiveFailures;
    private String tenantId;
    private Timestamp updatedAt;
    private String updatedBy;
    private Timestamp createdAt;
}
