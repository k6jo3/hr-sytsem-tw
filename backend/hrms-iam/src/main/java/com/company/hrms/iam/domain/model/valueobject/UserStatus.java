package com.company.hrms.iam.domain.model.valueobject;

/**
 * 使用者狀態列舉
 */
public enum UserStatus {

    /**
     * 啟用
     */
    ACTIVE,

    /**
     * 停用
     */
    INACTIVE,

    /**
     * 鎖定 (密碼錯誤次數過多)
     */
    LOCKED,

    /**
     * 待驗證
     */
    PENDING,

    /**
     * 已刪除
     */
    DELETED
}
