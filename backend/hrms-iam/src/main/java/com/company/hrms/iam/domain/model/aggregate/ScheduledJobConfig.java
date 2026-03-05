package com.company.hrms.iam.domain.model.aggregate;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排程任務配置聚合根
 * 管理系統排程任務（考勤日結、薪資月結、保險申報、特休年結等）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledJobConfig {

    private String id;
    private String jobCode;        // 任務代碼
    private String jobName;        // 任務名稱
    private String module;         // 所屬模組
    private String cronExpression; // Cron 表達式
    private boolean enabled;       // 是否啟用
    private String description;

    // 最近執行資訊
    private LocalDateTime lastExecutedAt;
    private String lastExecutionStatus; // SUCCESS / FAILED / RUNNING
    private String lastErrorMessage;
    private int consecutiveFailures; // 連續失敗次數

    private String tenantId;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * 啟用排程
     */
    public void enable(String operator) {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = operator;
    }

    /**
     * 停用排程
     */
    public void disable(String operator) {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = operator;
    }

    /**
     * 記錄執行成功
     */
    public void recordSuccess() {
        this.lastExecutedAt = LocalDateTime.now();
        this.lastExecutionStatus = "SUCCESS";
        this.lastErrorMessage = null;
        this.consecutiveFailures = 0;
    }

    /**
     * 記錄執行失敗
     */
    public void recordFailure(String errorMessage) {
        this.lastExecutedAt = LocalDateTime.now();
        this.lastExecutionStatus = "FAILED";
        this.lastErrorMessage = errorMessage;
        this.consecutiveFailures++;
    }

    /**
     * 記錄開始執行
     */
    public void recordStart() {
        this.lastExecutedAt = LocalDateTime.now();
        this.lastExecutionStatus = "RUNNING";
    }

    /**
     * 更新 Cron 表達式
     */
    public void updateCron(String newCron, String operator) {
        this.cronExpression = newCron;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = operator;
    }

    /**
     * 是否需要告警（連續失敗 >= 3 次）
     */
    public boolean needsAlert() {
        return this.consecutiveFailures >= 3;
    }
}
