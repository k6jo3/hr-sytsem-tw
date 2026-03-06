package com.company.hrms.iam.api.response.system;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排程任務配置 Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledJobConfigResponse {

    private String jobCode;
    private String jobName;
    private String module;
    private String cronExpression;
    private boolean enabled;
    private String description;
    private LocalDateTime lastExecutedAt;
    private String lastExecutionStatus;
    private String lastErrorMessage;
    private int consecutiveFailures;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
