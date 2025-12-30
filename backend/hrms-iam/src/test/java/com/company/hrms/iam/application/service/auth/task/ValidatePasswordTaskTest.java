package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

/**
 * ValidatePasswordTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValidatePasswordTask 測試")
class ValidatePasswordTaskTest {

    @Mock
    private PasswordHashingDomainService passwordHashingService;

    @Mock
    private AccountLockingDomainService accountLockingService;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private ValidatePasswordTask task;

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
    @DisplayName("密碼驗證成功")
    class SuccessTests {

        @Test
        @DisplayName("密碼正確應通過驗證")
        void shouldPassWhenPasswordCorrect() throws Exception {
            // Given
            when(passwordHashingService.verify("Password1!", "hashedPassword")).thenReturn(true);

            // When & Then
            assertDoesNotThrow(() -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("密碼驗證失敗")
    class FailureTests {

        @Test
        @DisplayName("密碼錯誤應拋出 LOGIN_FAILED 例外")
        void shouldThrowExceptionWhenPasswordIncorrect() {
            // Given
            when(passwordHashingService.verify(anyString(), anyString())).thenReturn(false);
            when(accountLockingService.recordFailureAndCheckLock(any())).thenReturn(false);
            when(accountLockingService.getMaxFailedAttempts()).thenReturn(5);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("LOGIN_FAILED", exception.getErrorCode());
            assertTrue(exception.getMessage().contains("剩餘嘗試次數"));

            // 驗證記錄失敗
            verify(accountLockingService).recordFailureAndCheckLock(testUser);
            verify(userRepository).update(testUser);
        }

        @Test
        @DisplayName("連續失敗達上限應鎖定帳號")
        void shouldLockAccountAfterMaxFailedAttempts() {
            // Given
            when(passwordHashingService.verify(anyString(), anyString())).thenReturn(false);
            when(accountLockingService.recordFailureAndCheckLock(any())).thenReturn(true);
            when(accountLockingService.getLockDurationMinutes()).thenReturn(30);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("USER_LOCKED", exception.getErrorCode());
            assertTrue(exception.getMessage().contains("30 分鐘"));
        }
    }

    @Test
    @DisplayName("getName 應返回 '驗證密碼'")
    void shouldReturnCorrectName() {
        assertEquals("驗證密碼", task.getName());
    }
}
