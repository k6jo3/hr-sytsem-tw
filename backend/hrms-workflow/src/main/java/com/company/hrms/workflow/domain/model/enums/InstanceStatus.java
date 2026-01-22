package com.company.hrms.workflow.domain.model.enums;

/**
 * 流程實例狀態
 */
public enum InstanceStatus {
    DRAFT, // 草稿 (新增)
    RUNNING, // 審核中
    COMPLETED, // 已核准
    REJECTED, // 已駁回
    CANCELLED // 已取消
}
