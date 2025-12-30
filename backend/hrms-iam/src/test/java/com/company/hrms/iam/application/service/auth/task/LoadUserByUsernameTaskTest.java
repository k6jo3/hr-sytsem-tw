package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

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

/**
 * LoadUserByUsernameTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoadUserByUsernameTask 測試")
class LoadUserByUsernameTaskTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private LoadUserByUsernameTask task;

    private AuthContext context;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .username("john.doe")
                .password("Password1!")
                .build();
        context = new AuthContext(loginRequest);
    }

    @Nested
    @DisplayName("成功載入使用者")
    class SuccessTests {

        @Test
        @DisplayName("應成功載入使用者並設置到 Context")
        void shouldLoadUserSuccessfully() throws Exception {
            // Given
            User user = User.create("john.doe", "john@example.com", "hashedPassword", "John Doe");
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getUser());
            assertEquals("john.doe", context.getUser().getUsername());
        }
    }

    @Nested
    @DisplayName("載入失敗")
    class FailureTests {

        @Test
        @DisplayName("使用者不存在應拋出 LOGIN_FAILED 例外")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertEquals("LOGIN_FAILED", exception.getErrorCode());
        }
    }

    @Test
    @DisplayName("getName 應返回 '載入使用者'")
    void shouldReturnCorrectName() {
        assertEquals("載入使用者", task.getName());
    }
}
