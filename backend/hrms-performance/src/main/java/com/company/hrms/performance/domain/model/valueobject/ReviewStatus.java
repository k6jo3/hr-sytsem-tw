package com.company.hrms.performance.domain.model.valueobject;

/**
 * 考核記錄狀態
 */
public enum ReviewStatus {
    /**
     * 等待自評
     */
    PENDING_SELF,

    /**
     * 等待主管評
     */
    PENDING_MANAGER,

    /**
     * 等待確認最終評等
     */
    PENDING_FINALIZE,

    /**
     * 已完成
     */
    FINALIZED
}
