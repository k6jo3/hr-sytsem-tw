package com.company.hrms.attendance.domain.model.valueobject;

/**
 * 換班申請狀態
 */
public enum SwapStatus {
    PENDING_COUNTERPART,  // 等待對方同意
    PENDING_APPROVAL,     // 等待主管審核
    APPROVED,             // 已核准
    REJECTED,             // 已拒絕
    CANCELLED             // 已取消
}
