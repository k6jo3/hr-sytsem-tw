package com.company.hrms.iam.domain.model.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UserId id;
    private String username;
    private Email email;
    private String passwordHash;
    private String displayName;
    private String employeeId;
    private String tenantId;
    private UserStatus status;
    private int failedLoginAttempts;
    private LocalDateTime lockedUntil;
    private LocalDateTime lastLoginAt;
    private LocalDateTime lastLogoutAt;
    private String lastLoginIp;
    private LocalDateTime passwordChangedAt;
    private String preferredLanguage;
    private String timezone;

    @Builder.Default
    private String authSource = "LOCAL"; // LOCAL or LDAP

    private String ldapDn; // LDAP Distinguished Name（LDAP 使用者專用）

    @Builder.Default
    private boolean mustChangePassword = false;

    @Builder.Default
    private boolean isDeleted = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

    public static User create(String username, String email, String passwordHash, String displayName) {
        return User.builder()
                .id(UserId.generate())
                .username(username)
                .email(new Email(email))
                .passwordHash(passwordHash)
                .displayName(displayName)
                .status(UserStatus.PENDING)
                .failedLoginAttempts(0)
                .mustChangePassword(true)
                .passwordChangedAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(new ArrayList<>())
                .build();
    }

    public static User createWithTenant(String username, String email, String passwordHash,
            String displayName, String employeeId, String tenantId) {
        User user = create(username, email, passwordHash, displayName);
        user.setEmployeeId(employeeId);
        user.setTenantId(tenantId);
        return user;
    }

    public static User reconstitute(UserId id, String username, Email email, String passwordHash,
            String displayName, String employeeId, String tenantId, UserStatus status,
            int failedLoginAttempts, LocalDateTime lockedUntil, LocalDateTime lastLoginAt,
            LocalDateTime createdAt, LocalDateTime updatedAt, List<String> roles) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .displayName(displayName)
                .employeeId(employeeId)
                .tenantId(tenantId)
                .status(status)
                .failedLoginAttempts(failedLoginAttempts)
                .lockedUntil(lockedUntil)
                .lastLoginAt(lastLoginAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .roles(roles)
                .build();
    }

    public void updateProfile(String email, String displayName, String preferredLanguage, String timezone) {
        if (email != null && !email.isBlank()) {
            this.email = new Email(email);
        }
        if (displayName != null && !displayName.isBlank()) {
            this.displayName = displayName;
        }
        if (preferredLanguage != null) {
            this.preferredLanguage = preferredLanguage;
        }
        if (timezone != null) {
            this.timezone = timezone;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new com.company.hrms.common.exception.DomainException("PASSWORD_REQUIRED", "密碼不可為空");
        }
        this.passwordHash = newPasswordHash;
        this.passwordChangedAt = LocalDateTime.now();
        this.mustChangePassword = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void resetPassword(String newPasswordHash) {
        this.changePassword(newPasswordHash);
    }

    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
        this.updatedAt = LocalDateTime.now();
    }

    public void lock(int minutes) {
        this.status = UserStatus.LOCKED;
        this.lockedUntil = LocalDateTime.now().plusMinutes(minutes);
        this.updatedAt = LocalDateTime.now();
    }

    public void lock(LocalDateTime until) {
        this.status = UserStatus.LOCKED;
        this.lockedUntil = until;
        this.updatedAt = LocalDateTime.now();
    }

    public void unlock() {
        this.status = UserStatus.ACTIVE;
        this.lockedUntil = null;
        this.failedLoginAttempts = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordLoginFailure() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            lock(30);
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void recordLoginSuccess(String ip) {
        this.lastLoginAt = LocalDateTime.now();
        this.lastLoginIp = ip;
        this.failedLoginAttempts = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordLogin() {
        this.recordLoginSuccess(null);
    }

    public void recordLogout() {
        this.lastLogoutAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status == UserStatus.DELETED || this.isDeleted) {
            throw new com.company.hrms.common.exception.DomainException("USER_DELETED", "已刪除的使用者無法啟用");
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignRole(String role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeRole(String role) {
        this.roles.remove(role);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }

    public boolean isLocked() {
        return this.status == UserStatus.LOCKED || (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now()));
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE && !isDeleted;
    }

    public boolean isLdapUser() {
        return "LDAP".equals(this.authSource);
    }

    /**
     * JIT Provisioning：LDAP 首次登入自動建立本地帳號
     */
    public static User createFromLdap(String username, String email, String displayName,
            String ldapDn, String tenantId) {
        return User.builder()
                .id(UserId.generate())
                .username(username)
                .email(email != null ? new Email(email) : null)
                .passwordHash(null) // LDAP 使用者不儲存密碼
                .displayName(displayName)
                .tenantId(tenantId)
                .authSource("LDAP")
                .ldapDn(ldapDn)
                .status(UserStatus.ACTIVE) // LDAP 驗證通過即啟用
                .failedLoginAttempts(0)
                .mustChangePassword(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(new ArrayList<>())
                .build();
    }

    /**
     * 從 LDAP 同步更新使用者資訊
     */
    public void syncFromLdap(String displayName, String email, String ldapDn) {
        if (displayName != null && !displayName.isBlank()) {
            this.displayName = displayName;
        }
        if (email != null && !email.isBlank()) {
            this.email = new Email(email);
        }
        if (ldapDn != null) {
            this.ldapDn = ldapDn;
        }
        this.updatedAt = LocalDateTime.now();
    }
}
