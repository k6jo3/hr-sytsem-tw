package com.company.hrms.payroll.domain.model.valueobject;

/**
 * 薪資調整單狀態
 */
public enum AdjustmentStatus {

    /**
     * 待審核
     */
    PENDING,

    /**
     * 已核准
     */
    APPROVED,

    /**
     * 已駁回
     */
    REJECTED,

    /**
     * 已執行（已併入薪資或直接發放）
     */
    EXECUTED,

    /**
     * 已取消
     */
    CANCELLED
}
