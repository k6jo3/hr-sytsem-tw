package com.company.hrms.iam.application.service.auth.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.iam.api.request.auth.LoginRequest;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * RecordLoginTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RecordLoginTask 測試")
class RecordLoginTaskTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private RecordLoginTask task;

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

    @Test
    @DisplayName("應成功記錄登入並更新使用者")
    void shouldRecordLoginSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        assertNotNull(testUser.getLastLoginAt());
        assertEquals(0, testUser.getFailedLoginAttempts());
        verify(userRepository).update(testUser);
    }

    @Test
    @DisplayName("getName 應返回 '記錄登入'")
    void shouldReturnCorrectName() {
        assertEquals("記錄登入", task.getName());
    }
}
