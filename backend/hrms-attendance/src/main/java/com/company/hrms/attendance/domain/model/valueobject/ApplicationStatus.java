package com.company.hrms.attendance.domain.model.valueobject;

/**
 * 申請狀態
 */
public enum ApplicationStatus {
    PENDING, // 待審核
    APPROVED, // 已核准
    REJECTED, // 已駁回
    CANCELLED // 已取消
}
