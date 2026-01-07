package com.company.hrms.performance.domain.model.valueobject;

/**
 * 考核週期狀態
 */
public enum CycleStatus {
    /**
     * 草稿 - 尚未啟動
     */
    DRAFT,

    /**
     * 進行中 - 已啟動，員工可進行自評
     */
    IN_PROGRESS,

    /**
     * 已完成 - 所有考核已確認，週期結束
     */
    COMPLETED
}
