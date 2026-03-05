package com.company.hrms.payroll.domain.model.valueobject;

/**
 * 薪資預借狀態
 */
public enum AdvanceStatus {
    PENDING,      // 待審核
    APPROVED,     // 已核准
    REJECTED,     // 已駁回
    DISBURSED,    // 已撥款
    REPAYING,     // 扣回中
    FULLY_REPAID, // 已全額扣回
    CANCELLED     // 已取消
}
