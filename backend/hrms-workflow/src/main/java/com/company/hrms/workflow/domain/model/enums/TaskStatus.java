package com.company.hrms.workflow.domain.model.enums;

/**
 * 審核任務狀態
 */
public enum TaskStatus {
    PENDING, // 待處理
    APPROVED, // 已核准
    REJECTED, // 已駁回
    DELEGATED, // 已轉交
    CANCELLED // 已取消 (隨流程取消)
}
