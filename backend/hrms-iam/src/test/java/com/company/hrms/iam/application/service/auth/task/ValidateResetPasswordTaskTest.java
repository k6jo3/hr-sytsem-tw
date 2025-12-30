package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.api.request.auth.ResetPasswordRequest;
import com.company.hrms.iam.application.service.auth.context.AuthContext;

/**
 * ValidateResetPasswordTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValidateResetPasswordTask 測試")
class ValidateResetPasswordTaskTest {

    @InjectMocks
    private ValidateResetPasswordTask task;

    private AuthContext context;
    private ResetPasswordRequest request;

    @BeforeEach
    void setUp() {
        request = new ResetPasswordRequest();
        request.setNewPassword("ValidPass1!");
        request.setConfirmPassword("ValidPass1!");

        context = new AuthContext();
        context.setResetPasswordRequest(request);
    }

    @Nested
    @DisplayName("驗證成功")
    class SuccessTests {

        @Test
        @DisplayName("密碼一致且強度符合應通過驗證")
        void shouldPassWhenPasswordValid() throws Exception {
            // When & Then
            assertDoesNotThrow(() -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("驗證失敗")
    class FailureTests {

        @Test
        @DisplayName("確認密碼不一致應拋出 PASSWORD_MISMATCH 例外")
        void shouldThrowExceptionWhenPasswordMismatch() {
            // Given
            request.setConfirmPassword("DifferentPass1!");

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("PASSWORD_MISMATCH", exception.getErrorCode());
        }

        @Test
        @DisplayName("密碼強度不足應拋出異常 (由 Password Value Object 驗證)")
        void shouldThrowExceptionWhenPasswordWeak() {
            // Given
            request.setNewPassword("weak");
            request.setConfirmPassword("weak");

            // When & Then
            assertThrows(DomainException.class, () -> task.execute(context));
        }
    }

    @Test
    @DisplayName("getName 應返回 '驗證新密碼格式'")
    void shouldReturnCorrectName() {
        assertEquals("驗證新密碼格式", task.getName());
    }
}
