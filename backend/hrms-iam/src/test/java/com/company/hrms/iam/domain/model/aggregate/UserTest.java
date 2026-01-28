package com.company.hrms.iam.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;

/**
 * User 聚合根單元測試
 * 遵循 TDD 原則，測試所有業務邏輯
 */
@DisplayName("User 聚合根測試")
class UserTest {

    // ==================== Factory Method Tests ====================

    @Nested
    @DisplayName("建立使用者")
    class CreateUserTests {

        @Test
        @DisplayName("應成功建立使用者，並產生唯一 ID")
        void shouldCreateUserWithUniqueId() {
            // When
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");

            // Then
            assertNotNull(user.getId());
            assertNotNull(user.getId().getValue());
            assertEquals("john.doe", user.getUsername());
            assertEquals("john@example.com", user.getEmail().getValue());
            assertEquals("hashedPassword123", user.getPasswordHash());
            assertEquals("John Doe", user.getDisplayName());
            assertEquals(UserStatus.PENDING, user.getStatus());
            assertEquals(0, user.getFailedLoginAttempts());
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getRoles());
            assertTrue(user.getRoles().isEmpty());
        }

        @Test
        @DisplayName("建立的使用者初始狀態應為 PENDING")
        void shouldCreateUserWithPendingStatus() {
            // When
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");

            // Then
            assertEquals(UserStatus.PENDING, user.getStatus());
            assertFalse(user.isActive());
        }
    }

    // ==================== Status Transition Tests ====================

    @Nested
    @DisplayName("使用者狀態轉換")
    class StatusTransitionTests {

        @Test
        @DisplayName("啟用使用者應將狀態變更為 ACTIVE")
        void shouldActivateUser() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");

            // When
            user.activate();

            // Then
            assertEquals(UserStatus.ACTIVE, user.getStatus());
            assertTrue(user.isActive());
        }

        @Test
        @DisplayName("停用使用者應將狀態變更為 INACTIVE")
        void shouldDeactivateUser() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");
            user.activate();

            // When
            user.deactivate();

            // Then
            assertEquals(UserStatus.INACTIVE, user.getStatus());
            assertFalse(user.isActive());
        }

        @Test
        @DisplayName("無法啟用已刪除的使用者")
        void shouldNotActivateDeletedUser() {
            // Given
            User user = User.builder()
                    .id(UserId.generate())
                    .username("john.doe")
                    .email(new Email("john@example.com"))
                    .passwordHash("hashedPassword123")
                    .displayName("John Doe")
                    .status(UserStatus.DELETED)
                    .createdAt(LocalDateTime.now())
                    .build();

            // When & Then
            DomainException exception = assertThrows(DomainException.class, user::activate);
            assertEquals("USER_DELETED", exception.getErrorCode());
        }

        @Test
        @DisplayName("鎖定使用者應設定狀態為 LOCKED 並記錄到期時間")
        void shouldLockUser() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");
            user.activate();
            LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(30);

            // When
            user.lock(lockUntil);

            // Then
            assertEquals(UserStatus.LOCKED, user.getStatus());
            assertTrue(user.isLocked());
            assertEquals(lockUntil, user.getLockedUntil());
        }

        @Test
        @DisplayName("解鎖使用者應重置失敗次數並設定狀態為 ACTIVE")
        void shouldUnlockUser() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");
            user.activate();
            user.incrementFailedAttempts();
            user.incrementFailedAttempts();
            user.lock(LocalDateTime.now().plusMinutes(30));

            // When
            user.unlock();

            // Then
            assertEquals(UserStatus.ACTIVE, user.getStatus());
            assertFalse(user.isLocked());
            assertEquals(0, user.getFailedLoginAttempts());
            assertNull(user.getLockedUntil());
        }
    }

    // ==================== Login Attempt Tests ====================

    @Nested
    @DisplayName("登入嘗試")
    class LoginAttemptTests {

        @Test
        @DisplayName("記錄登入失敗應增加失敗次數")
        void shouldIncrementFailedAttempts() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");
            user.activate();

            // When
            user.incrementFailedAttempts();

            // Then
            assertEquals(1, user.getFailedLoginAttempts());

            // When
            user.incrementFailedAttempts();

            // Then
            assertEquals(2, user.getFailedLoginAttempts());
        }

        @Test
        @DisplayName("記錄登入成功應重置失敗次數並更新最後登入時間")
        void shouldRecordSuccessfulLogin() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");
            user.activate();
            user.incrementFailedAttempts();
            user.incrementFailedAttempts();

            // When
            user.recordLogin();

            // Then
            assertEquals(0, user.getFailedLoginAttempts());
            assertNotNull(user.getLastLoginAt());
        }
    }

    // ==================== Profile Update Tests ====================

    @Nested
    @DisplayName("個人資料更新")
    class ProfileUpdateTests {

        @Test
        @DisplayName("應成功更新 Email 和顯示名稱")
        void shouldUpdateProfile() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");

            // When
            user.updateProfile("newemail@example.com", "Johnny Doe");

            // Then
            assertEquals("newemail@example.com", user.getEmail().getValue());
            assertEquals("Johnny Doe", user.getDisplayName());
        }

        @Test
        @DisplayName("更新時 Email 為 null 應保留原值")
        void shouldKeepOriginalEmailWhenNull() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");

            // When
            user.updateProfile(null, "Johnny Doe");

            // Then
            assertEquals("john@example.com", user.getEmail().getValue());
            assertEquals("Johnny Doe", user.getDisplayName());
        }

        @Test
        @DisplayName("更新時顯示名稱為空白應保留原值")
        void shouldKeepOriginalDisplayNameWhenBlank() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");

            // When
            user.updateProfile("newemail@example.com", "  ");

            // Then
            assertEquals("newemail@example.com", user.getEmail().getValue());
            assertEquals("John Doe", user.getDisplayName());
        }
    }

    // ==================== Password Change Tests ====================

    @Nested
    @DisplayName("密碼變更")
    class PasswordChangeTests {

        @Test
        @DisplayName("應成功變更密碼")
        void shouldChangePassword() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "oldHashedPassword", "John Doe");

            // When
            user.changePassword("newHashedPassword");

            // Then
            assertEquals("newHashedPassword", user.getPasswordHash());
        }

        @Test
        @DisplayName("密碼為空時應拋出例外")
        void shouldThrowExceptionWhenPasswordIsNull() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "oldHashedPassword", "John Doe");

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> user.changePassword(null));
            assertEquals("PASSWORD_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("密碼為空白時應拋出例外")
        void shouldThrowExceptionWhenPasswordIsBlank() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "oldHashedPassword", "John Doe");

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> user.changePassword("   "));
            assertEquals("PASSWORD_REQUIRED", exception.getErrorCode());
        }
    }

    // ==================== Role Management Tests ====================

    @Nested
    @DisplayName("角色管理")
    class RoleManagementTests {

        @Test
        @DisplayName("應成功指派角色")
        void shouldAssignRole() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");

            // When
            user.assignRole("ADMIN");

            // Then
            assertTrue(user.getRoles().contains("ADMIN"));
            assertEquals(1, user.getRoles().size());
        }

        @Test
        @DisplayName("重複指派相同角色不應增加角色數量")
        void shouldNotDuplicateRole() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");
            user.assignRole("ADMIN");

            // When
            user.assignRole("ADMIN");

            // Then
            assertEquals(1, user.getRoles().size());
        }

        @Test
        @DisplayName("應成功移除角色")
        void shouldRemoveRole() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");
            user.assignRole("ADMIN");
            user.assignRole("USER");

            // When
            user.removeRole("ADMIN");

            // Then
            assertFalse(user.getRoles().contains("ADMIN"));
            assertTrue(user.getRoles().contains("USER"));
            assertEquals(1, user.getRoles().size());
        }

        @Test
        @DisplayName("移除不存在的角色不應影響角色列表")
        void shouldNotAffectRolesWhenRemovingNonExistent() {
            // Given
            User user = User.create("john.doe", "john@example.com",
                    "hashedPassword123", "John Doe");
            user.assignRole("USER");

            // When
            user.removeRole("ADMIN");

            // Then
            assertTrue(user.getRoles().contains("USER"));
            assertEquals(1, user.getRoles().size());
        }
    }
}
