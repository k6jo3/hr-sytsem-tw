package com.company.hrms.payroll.domain.model.valueobject;

/**
 * 法扣款狀態
 */
public enum GarnishmentStatus {
    ACTIVE,     // 執行中
    SUSPENDED,  // 暫停（法院暫緩執行）
    COMPLETED,  // 已全額扣完
    TERMINATED  // 終止（法院撤銷）
}
