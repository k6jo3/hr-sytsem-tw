package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;
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
import com.company.hrms.iam.api.request.auth.ResetPasswordRequest;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;

/**
 * ResetPasswordTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ResetPasswordTask 測試")
class ResetPasswordTaskTest {

    @Mock
    private PasswordHashingDomainService passwordHashingService;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private ResetPasswordTask task;

    private AuthContext context;
    private ResetPasswordRequest request;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        request = new ResetPasswordRequest();
        request.setNewPassword("NewPassword1!");
        // token is not in ResetPasswordRequest

        context = new AuthContext();
        context.setResetPasswordRequest(request);
        context.setUser(mockUser);
    }

    @Nested
    @DisplayName("重設密碼成功")
    class SuccessTests {

        @Test
        @DisplayName("密碼驗證通過應成功更新")
        void shouldResetPasswordSuccessfully() throws Exception {
            // Given
            when(mockUser.getPasswordHash()).thenReturn("old-hash");
            when(mockUser.getId()).thenReturn(new UserId("user-123"));
            when(passwordHashingService.verify("NewPassword1!", "old-hash")).thenReturn(false);
            when(passwordHashingService.hash("NewPassword1!")).thenReturn("new-hash");

            // When
            task.execute(context);

            // Then
            verify(mockUser).resetPassword("new-hash");
            verify(userRepository).update(mockUser);
        }
    }

    @Nested
    @DisplayName("重設密碼失敗")
    class FailureTests {

        @Test
        @DisplayName("當前密碼不正確應拋出錯誤")
        void shouldThrowExceptionWhenCurrentPasswordInvalid() {
            // Given
            request.setCurrentPassword("WrongPassword");
            when(mockUser.getPasswordHash()).thenReturn("old-hash");
            when(passwordHashingService.verify("WrongPassword", "old-hash")).thenReturn(false);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("INVALID_CURRENT_PASSWORD", exception.getErrorCode());
        }

        @Test
        @DisplayName("新密碼與舊密碼相同應拋出錯誤")
        void shouldThrowExceptionWhenNewPasswordSameAsOld() {
            // Given
            when(mockUser.getPasswordHash()).thenReturn("old-hash");
            when(passwordHashingService.verify("NewPassword1!", "old-hash")).thenReturn(true);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("SAME_PASSWORD", exception.getErrorCode());
        }
    }

    @Test
    @DisplayName("getName 應返回 '重設密碼'")
    void shouldReturnCorrectName() {
        assertEquals("重設密碼", task.getName());
    }
}
