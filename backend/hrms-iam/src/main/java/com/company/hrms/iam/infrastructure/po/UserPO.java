package com.company.hrms.iam.infrastructure.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * User Persistent Object
 * 資料庫映射物件，對應 users 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPO {

    /**
     * 使用者 ID (主鍵)
     */
    private String userId;

    /**
     * 使用者名稱
     */
    private String username;

    /**
     * Email
     */
    private String email;

    /**
     * 密碼雜湊
     */
    private String passwordHash;

    /**
     * 顯示名稱
     */
    private String displayName;

    /**
     * 使用者狀態
     */
    private String status;

    /**
     * 登入失敗次數
     */
    private Integer failedLoginAttempts;

    /**
     * 鎖定到期時間
     */
    private Timestamp lockedUntil;

    /**
     * 最後登入時間
     */
    private Timestamp lastLoginAt;

    /**
     * 建立時間
     */
    private Timestamp createdAt;

    /**
     * 更新時間
     */
    private Timestamp updatedAt;
}
