package com.company.hrms.iam.infrastructure.po;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Persistent Object
 * 資料庫映射物件，對應 users 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserPO {

    /**
     * 使用者 ID (主鍵)
     */
    @Id
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
     * 姓
     */
    private String firstName;

    /**
     * 名
     */
    private String lastName;

    /**
     * 員工 ID (關聯組織服務的員工)
     */
    private String employeeId;

    /**
     * 租戶 ID (多租戶隔離)
     */
    private String tenantId;

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
     * 最後登出時間
     */
    private Timestamp lastLogoutAt;

    /**
     * 最後登入 IP
     */
    private String lastLoginIp;

    /**
     * 偏好語言
     */
    private String preferredLanguage;

    /**
     * 時區
     */
    private String timezone;

    /**
     * 密碼變更時間
     */
    private Timestamp passwordChangedAt;

    /**
     * 認證來源 (LOCAL / LDAP)
     */
    private String authSource;

    /**
     * LDAP Distinguished Name
     */
    private String ldapDn;

    /**
     * 是否必須變更密碼 (首次登入強制改密)
     */
    private Boolean mustChangePassword;

    /**
     * 是否已刪除 (軟刪除標記)
     */
    private Boolean isDeleted;

    /**
     * 建立時間
     */
    private Timestamp createdAt;

    /**
     * 更新時間
     */
    private Timestamp updatedAt;
}
