package com.company.hrms.iam.application.service.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.application.service.user.task.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceImplTest {

    @Mock private LoadUserTask loadUserTask;
    @Mock private DeleteUserTask deleteUserTask;
    @Mock private PublishUserDeletedEventTask publishUserDeletedEventTask;

    private DeleteUserServiceImpl deleteUserService;

    @BeforeEach
    void setUp() {
        deleteUserService = new DeleteUserServiceImpl(
            loadUserTask,
            deleteUserTask,
            publishUserDeletedEventTask
        );

        when(loadUserTask.shouldExecute(any())).thenReturn(true);
        when(deleteUserTask.shouldExecute(any())).thenReturn(true);
        when(publishUserDeletedEventTask.shouldExecute(any())).thenReturn(true);
    }

    @Test
    void execCommand_ShouldExecutePipelineTasks() throws Exception {
        // Arrange
        String userId = "user-123";

        // Act
        deleteUserService.execCommand(null, null, userId);

        // Assert
        verify(loadUserTask).execute(any(UserPipelineContext.class));
        verify(deleteUserTask).execute(any(UserPipelineContext.class));
        verify(publishUserDeletedEventTask).execute(any(UserPipelineContext.class));
    }
}
