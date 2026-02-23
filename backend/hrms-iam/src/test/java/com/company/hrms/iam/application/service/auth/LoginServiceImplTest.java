package com.company.hrms.iam.application.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.api.response.auth.LoginResponse;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.application.service.auth.task.CheckUserStatusTask;
import com.company.hrms.iam.application.service.auth.task.GenerateTokenTask;
import com.company.hrms.iam.application.service.auth.task.LoadUserByUsernameTask;
import com.company.hrms.iam.application.service.auth.task.RecordLoginTask;
import com.company.hrms.iam.application.service.auth.task.ValidatePasswordTask;
import com.company.hrms.iam.domain.model.aggregate.User;

/**
 * LoginServiceImpl 測試 (Pipeline 模式)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginServiceImpl 測試")

class LoginServiceImplTest {

    @Mock
    private LoadUserByUsernameTask loadUserByUsernameTask;
    @Mock
    private CheckUserStatusTask checkUserStatusTask;
    @Mock
    private ValidatePasswordTask validatePasswordTask;
    @Mock
    private RecordLoginTask recordLoginTask;
    @Mock
    private GenerateTokenTask generateTokenTask;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(loginService, "accessTokenExpiry", 3600000L);
        lenient().when(loadUserByUsernameTask.shouldExecute(any())).thenReturn(true);
        lenient().when(checkUserStatusTask.shouldExecute(any())).thenReturn(true);
        lenient().when(validatePasswordTask.shouldExecute(any())).thenReturn(true);
        lenient().when(recordLoginTask.shouldExecute(any())).thenReturn(true);
        lenient().when(generateTokenTask.shouldExecute(any())).thenReturn(true);
    }

    @Test
    @DisplayName("應成功執行登入 Pipeline")
    void shouldLoginSuccessfully() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("john.doe")
                .password("Password1!")
                .build();

        User testUser = User.create("john.doe", "john@example.com", "hashed", "John Doe");
        testUser.assignRole("USER");

        // Mock tasks to populate context
        doAnswer(invocation -> {
            AuthContext ctx = invocation.getArgument(0);
            ctx.setUser(testUser);
            return null;
        }).when(loadUserByUsernameTask).execute(any());

        doAnswer(invocation -> {
            AuthContext ctx = invocation.getArgument(0);
            ctx.setAccessToken("access-token");
            ctx.setRefreshToken("refresh-token");
            return null;
        }).when(generateTokenTask).execute(any());

        // When
        LoginResponse response = loginService.execCommand(request, null);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("john.doe", response.getUser().getUsername());

        // Verify all tasks were called
        verify(loadUserByUsernameTask).execute(any());
        verify(checkUserStatusTask).execute(any());
        verify(validatePasswordTask).execute(any());
        verify(recordLoginTask).execute(any());
        verify(generateTokenTask).execute(any());
    }
}
