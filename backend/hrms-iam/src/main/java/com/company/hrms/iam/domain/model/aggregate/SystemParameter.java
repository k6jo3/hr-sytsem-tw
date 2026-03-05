package com.company.hrms.iam.domain.model.aggregate;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系統參數聚合根
 * 管理全域參數與各模組業務參數
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemParameter {

    private String id;
    private String paramCode;     // 參數代碼（如 MAX_FAILED_LOGIN_ATTEMPTS）
    private String paramName;     // 參數名稱
    private String paramValue;    // 參數值（字串格式，由業務層轉型）
    private String paramType;     // 參數型別（STRING/INTEGER/DECIMAL/BOOLEAN/JSON）
    private String module;        // 所屬模組代碼（GLOBAL 或 HR01-HR14）
    private String category;      // 參數分類（SECURITY/BUSINESS/UI/SYSTEM）
    private String description;
    private String defaultValue;  // 預設值
    private String tenantId;
    private boolean isEncrypted;  // 是否加密儲存（如密碼、密鑰）
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * 更新參數值（記錄異動）
     */
    public ParameterChange updateValue(String newValue, String operator) {
        String oldValue = this.paramValue;
        this.paramValue = newValue;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = operator;

        return new ParameterChange(
                this.paramCode, oldValue, newValue, operator, this.updatedAt);
    }

    /**
     * 重設為預設值
     */
    public ParameterChange resetToDefault(String operator) {
        return updateValue(this.defaultValue, operator);
    }

    /**
     * 取得整數值
     */
    public int getIntValue() {
        return Integer.parseInt(this.paramValue);
    }

    /**
     * 取得布林值
     */
    public boolean getBoolValue() {
        return Boolean.parseBoolean(this.paramValue);
    }

    /**
     * 參數異動記錄
     */
    @Data
    @AllArgsConstructor
    public static class ParameterChange {
        private String paramCode;
        private String oldValue;
        private String newValue;
        private String operator;
        private LocalDateTime changedAt;
    }
}
