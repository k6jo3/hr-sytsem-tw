package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.AccountLockingDomainService;

/**
 * CheckUserStatusTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CheckUserStatusTask 測試")
class CheckUserStatusTaskTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private AccountLockingDomainService accountLockingService;

    @InjectMocks
    private CheckUserStatusTask task;

    private AuthContext context;
    private User testUser;

    @BeforeEach
    void setUp() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("john.doe")
                .password("Password1!")
                .build();
        context = new AuthContext(loginRequest);

        testUser = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
        testUser.activate();
        context.setUser(testUser);
    }

    @Nested
    @DisplayName("狀態檢查成功")
    class SuccessTests {

        @Test
        @DisplayName("啟用中的使用者應通過檢查")
        void shouldPassWhenUserActive() throws Exception {
            // When & Then
            assertDoesNotThrow(() -> task.execute(context));
        }

        @Test
        @DisplayName("鎖定時間已過應自動解鎖")
        void shouldAutoUnlockWhenLockExpired() throws Exception {
            // Given
            testUser.lock(LocalDateTime.now().minusMinutes(1));
            when(accountLockingService.checkAndUnlock(testUser)).thenAnswer(inv -> {
                testUser.unlock();
                return true;
            });

            // When & Then
            assertDoesNotThrow(() -> task.execute(context));
            verify(userRepository).update(testUser);
        }
    }

    @Nested
    @DisplayName("狀態檢查失敗")
    class FailureTests {

        @Test
        @DisplayName("帳號已停用應拋出 USER_INACTIVE 例外")
        void shouldThrowExceptionWhenUserInactive() {
            // Given
            testUser.deactivate();

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("USER_INACTIVE", exception.getErrorCode());
        }

        @Test
        @DisplayName("帳號被鎖定且未過期應拋出 USER_LOCKED 例外")
        void shouldThrowExceptionWhenUserLocked() {
            // Given
            testUser.lock(LocalDateTime.now().plusMinutes(30));
            when(accountLockingService.checkAndUnlock(testUser)).thenReturn(false);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("USER_LOCKED", exception.getErrorCode());
        }
    }

    @Test
    @DisplayName("getName 應返回 '檢查使用者狀態'")
    void shouldReturnCorrectName() {
        assertEquals("檢查使用者狀態", task.getName());
    }
}
