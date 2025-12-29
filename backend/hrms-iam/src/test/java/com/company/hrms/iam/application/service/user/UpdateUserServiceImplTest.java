package com.company.hrms.iam.application.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.iam.api.request.user.UpdateUserRequest;
import com.company.hrms.iam.api.response.user.UserDetailResponse;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.application.service.user.task.*;
import com.company.hrms.iam.domain.model.aggregate.User;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceImplTest {

    @Mock private LoadUserTask loadUserTask;
    @Mock private UpdateProfileTask updateProfileTask;
    @Mock private SaveUserTask saveUserTask;
    @Mock private PublishUserUpdatedEventTask publishUserUpdatedEventTask;

    private UpdateUserServiceImpl updateUserService;

    @BeforeEach
    void setUp() {
        updateUserService = new UpdateUserServiceImpl(
            loadUserTask,
            updateProfileTask,
            saveUserTask,
            publishUserUpdatedEventTask
        );

        when(loadUserTask.shouldExecute(any())).thenReturn(true);
        when(updateProfileTask.shouldExecute(any())).thenReturn(true);
        when(saveUserTask.shouldExecute(any())).thenReturn(true);
        when(publishUserUpdatedEventTask.shouldExecute(any())).thenReturn(true);
    }

    @Test
    void execCommand_ShouldExecutePipelineTasks() throws Exception {
        // Arrange
        String userId = "user-123";
        UpdateUserRequest request = UpdateUserRequest.builder()
                .displayName("Updated Name")
                .build();

        // Simulate task execution results
        doAnswer(invocation -> {
            UserPipelineContext ctx = invocation.getArgument(0);
            User user = User.create("testuser", "test@example.com", "hashed", "Test User");
            ctx.setUser(user);
            return null;
        }).when(loadUserTask).execute(any(UserPipelineContext.class));

        // Act
        UserDetailResponse response = updateUserService.execCommand(request, null, userId);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());

        // Verify sequence
        verify(loadUserTask).execute(any(UserPipelineContext.class));
        verify(updateProfileTask).execute(any(UserPipelineContext.class));
        verify(saveUserTask).execute(any(UserPipelineContext.class));
        verify(publishUserUpdatedEventTask).execute(any(UserPipelineContext.class));
    }
}
