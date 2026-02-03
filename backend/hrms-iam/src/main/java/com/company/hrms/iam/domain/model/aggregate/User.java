package com.company.hrms.iam.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * User 聚合根
 * IAM 領域的核心聚合根，封裝使用者相關的業務邏輯
 */
@Getter
@Builder
public class User {

    /**
     * 使用者 ID
     */
    private final UserId id;

    /**
     * 使用者名稱 (登入帳號)
     */
    private String username;

    /**
     * Email
     */
    private Email email;

    /**
     * 密碼雜湊
     */
    private String passwordHash;

    /**
     * 顯示名稱
     */
    private String displayName;

    /**
     * 關聯員工 ID (來自組織服務)
     */
    private String employeeId;

    /**
     * 租戶 ID (多租戶隔離)
     */
    private String tenantId;

    /**
     * 使用者狀態
     */
    private UserStatus status;

    /**
     * 登入失敗次數
     */
    private int failedLoginAttempts;

    /**
     * 鎖定到期時間
     */
    private LocalDateTime lockedUntil;

    /**
     * 最後登入時間
     */
    private LocalDateTime lastLoginAt;

    /**
     * 最後登出時間
     */
    private LocalDateTime lastLogoutAt;

    /**
     * 密碼變更時間
     */
    private LocalDateTime passwordChangedAt;

    /**
     * 是否需要首次登入變更密碼
     */
    @Builder.Default
    private boolean mustChangePassword = false;

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    /**
     * 角色列表
     */
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    // ==================== 工廠方法 ====================

    /**
     * 建立新使用者
     * 
     * @param username     使用者名稱
     * @param email        Email
     * @param passwordHash 密碼雜湊
     * @param displayName  顯示名稱
     * @return 新的 User 實例
     */
    public static User create(String username, String email,
            String passwordHash, String displayName) {
        return User.builder()
                .id(UserId.generate())
                .username(username)
                .email(new Email(email))
                .passwordHash(passwordHash)
                .displayName(displayName)
                .status(UserStatus.PENDING)
                .failedLoginAttempts(0)
                .mustChangePassword(true)
                .passwordChangedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(new ArrayList<>())
                .build();
    }

    /**
     * 建立新使用者 (完整版)
     * 
     * @param username     使用者名稱
     * @param email        Email
     * @param passwordHash 密碼雜湊
     * @param displayName  顯示名稱
     * @param employeeId   員工 ID
     * @param tenantId     租戶 ID
     * @return 新的 User 實例
     */
    public static User createWithTenant(String username, String email,
            String passwordHash, String displayName,
            String employeeId, String tenantId) {
        return User.builder()
                .id(UserId.generate())
                .username(username)
                .email(new Email(email))
                .passwordHash(passwordHash)
                .displayName(displayName)
                .employeeId(employeeId)
                .tenantId(tenantId)
                .status(UserStatus.PENDING)
                .failedLoginAttempts(0)
                .mustChangePassword(true)
                .passwordChangedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(new ArrayList<>())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 啟用使用者
     * 
     * @throws DomainException 若使用者已被刪除
     */
    public void activate() {
        if (this.status == UserStatus.DELETED) {
            throw new DomainException("USER_DELETED", "無法啟用已刪除的使用者");
        }
        this.status = UserStatus.ACTIVE;
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用使用者
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 鎖定使用者
     * 
     * @param until 鎖定到期時間
     */
    public void lock(LocalDateTime until) {
        this.status = UserStatus.LOCKED;
        this.lockedUntil = until;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 解鎖使用者
     */
    public void unlock() {
        if (this.status == UserStatus.LOCKED) {
            this.status = UserStatus.ACTIVE;
            this.failedLoginAttempts = 0;
            this.lockedUntil = null;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 記錄登入失敗
     */
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 記錄登入成功
     */
    public void recordLogin() {
        this.failedLoginAttempts = 0;
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 記錄登出成功
     */
    public void recordLogout() {
        this.lastLogoutAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新基本資料
     * 
     * @param email       新的 Email
     * @param displayName 新的顯示名稱
     */
    public void updateProfile(String email, String displayName) {
        if (email != null) {
            this.email = new Email(email);
        }
        if (displayName != null && !displayName.isBlank()) {
            this.displayName = displayName;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 變更密碼
     * 
     * @param newPasswordHash 新密碼雜湊
     */
    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new DomainException("PASSWORD_REQUIRED", "密碼不可為空");
        }
        this.passwordHash = newPasswordHash;
        this.passwordChangedAt = LocalDateTime.now();
        this.mustChangePassword = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 管理員重置密碼
     * 
     * @param newPasswordHash 新密碼雜湊
     */
    public void resetPassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new DomainException("PASSWORD_REQUIRED", "密碼不可為空");
        }
        this.passwordHash = newPasswordHash;
        this.passwordChangedAt = LocalDateTime.now();
        this.mustChangePassword = true; // 強制首次登入變更密碼
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        if (this.status == UserStatus.LOCKED) {
            this.status = UserStatus.ACTIVE;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 設定員工 ID
     * 
     * @param employeeId 員工 ID
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 設定是否需要首次登入變更密碼
     * 
     * @param mustChangePassword 是否需要變更密碼
     */
    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 檢查使用者是否已鎖定
     * 
     * @return 是否已鎖定
     */
    public boolean isLocked() {
        return this.status == UserStatus.LOCKED;
    }

    /**
     * 檢查使用者是否已啟用
     * 
     * @return 是否啟用
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    /**
     * 檢查使用者是否已刪除
     * 
     * @return 是否已刪除
     */
    public boolean isDeleted() {
        return this.status == UserStatus.DELETED;
    }

    /**
     * 檢查密碼是否過期 (預設 90 天)
     * 
     * @param maxAgeDays 密碼最大使用天數
     * @return 是否已過期
     */
    public boolean isPasswordExpired(int maxAgeDays) {
        if (passwordChangedAt == null) {
            return true;
        }
        return passwordChangedAt.plusDays(maxAgeDays).isBefore(LocalDateTime.now());
    }

    /**
     * 指派角色
     * 
     * @param role 角色名稱
     */
    public void assignRole(String role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 移除角色
     * 
     * @param role 角色名稱
     */
    public void removeRole(String role) {
        if (this.roles.remove(role)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 檢查是否擁有指定角色
     * 
     * @param role 角色名稱
     * @return 是否擁有
     */
    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }
}
