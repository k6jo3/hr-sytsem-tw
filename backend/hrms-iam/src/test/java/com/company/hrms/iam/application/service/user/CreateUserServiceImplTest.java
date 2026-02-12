package com.company.hrms.iam.application.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.iam.api.request.user.CreateUserRequest;
import com.company.hrms.iam.api.response.user.CreateUserResponse;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.application.service.user.task.AssignUserRolesTask;
import com.company.hrms.iam.application.service.user.task.CheckUserExistenceTask;
import com.company.hrms.iam.application.service.user.task.CreateUserAggregateTask;
import com.company.hrms.iam.application.service.user.task.HashPasswordTask;
import com.company.hrms.iam.application.service.user.task.PublishUserEventTask;
import com.company.hrms.iam.application.service.user.task.SaveUserTask;
import com.company.hrms.iam.domain.model.aggregate.User;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceImplTest {

    @Mock
    private CheckUserExistenceTask checkUserExistenceTask;
    @Mock
    private HashPasswordTask hashPasswordTask;
    @Mock
    private CreateUserAggregateTask createUserAggregateTask;
    @Mock
    private SaveUserTask saveUserTask;
    @Mock
    private AssignUserRolesTask assignUserRolesTask;
    @Mock
    private PublishUserEventTask publishUserEventTask;

    private CreateUserServiceImpl createUserService;

    @BeforeEach
    void setUp() {
        createUserService = new CreateUserServiceImpl(
                checkUserExistenceTask,
                hashPasswordTask,
                createUserAggregateTask,
                saveUserTask,
                assignUserRolesTask,
                publishUserEventTask);

        when(checkUserExistenceTask.shouldExecute(any())).thenReturn(true);
        when(hashPasswordTask.shouldExecute(any())).thenReturn(true);
        when(createUserAggregateTask.shouldExecute(any())).thenReturn(true);
        when(saveUserTask.shouldExecute(any())).thenReturn(true);
        when(assignUserRolesTask.shouldExecute(any())).thenReturn(true);
        when(publishUserEventTask.shouldExecute(any())).thenReturn(true);
    }

    @Test
    void execCommand_ShouldExecutePipelineTasks() throws Exception {
        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .displayName("Test User")
                .build();

        // Simulate task execution results
        doAnswer(invocation -> {
            UserPipelineContext ctx = invocation.getArgument(0);
            User user = User.create("testuser", "test@example.com", "hashed", "Test User");
            ctx.setUser(user);
            return null;
        }).when(createUserAggregateTask).execute(any(UserPipelineContext.class));

        // Act
        CreateUserResponse response = createUserService.execCommand(request, null);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());

        // Verify sequence
        verify(checkUserExistenceTask).execute(any(UserPipelineContext.class));
        verify(hashPasswordTask).execute(any(UserPipelineContext.class));
        verify(createUserAggregateTask).execute(any(UserPipelineContext.class));
        verify(saveUserTask).execute(any(UserPipelineContext.class));
        verify(publishUserEventTask).execute(any(UserPipelineContext.class));
    }
}
