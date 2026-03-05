package com.company.hrms.iam.domain.model.aggregate;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能開關聚合根
 * 控制各模組業務功能的啟停，不需重啟服務即時生效
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureToggle {

    private String id;
    private String featureCode;   // 功能代碼（如 ATTENDANCE_LATE_CHECK, SALARY_ADVANCE）
    private String featureName;   // 功能名稱
    private String module;        // 所屬模組代碼（HR01-HR14）
    private boolean enabled;      // 是否啟用
    private String description;
    private String tenantId;      // 多租戶隔離
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * 啟用功能
     */
    public void enable(String operator) {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = operator;
    }

    /**
     * 停用功能
     */
    public void disable(String operator) {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = operator;
    }

    /**
     * 切換開關
     */
    public void toggle(String operator) {
        this.enabled = !this.enabled;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = operator;
    }
}
