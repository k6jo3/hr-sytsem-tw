package com.company.hrms.organization.domain.model.valueobject;

/**
 * 證明文件申請狀態列舉
 */
public enum CertificateRequestStatus {
    /**
     * 待處理
     */
    PENDING("待處理"),

    /**
     * 已核准
     */
    APPROVED("已核准"),

    /**
     * 已拒絕
     */
    REJECTED("已拒絕"),

    /**
     * 已完成
     */
    COMPLETED("已完成");

    private final String displayName;

    CertificateRequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 是否為終態
     * @return 是否為終態
     */
    public boolean isFinal() {
        return this == REJECTED || this == COMPLETED;
    }

    /**
     * 是否可取消
     * @return 是否可取消
     */
    public boolean isCancellable() {
        return this == PENDING;
    }
}
