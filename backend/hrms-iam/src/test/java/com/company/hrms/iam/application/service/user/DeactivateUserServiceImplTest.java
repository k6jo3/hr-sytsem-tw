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
class DeactivateUserServiceImplTest {

    @Mock private LoadUserTask loadUserTask;
    @Mock private DeactivateUserTask deactivateUserTask;
    @Mock private SaveUserTask saveUserTask;
    @Mock private PublishUserDeactivatedEventTask publishUserDeactivatedEventTask;

    private DeactivateUserServiceImpl deactivateUserService;

    @BeforeEach
    void setUp() {
        deactivateUserService = new DeactivateUserServiceImpl(
            loadUserTask,
            deactivateUserTask,
            saveUserTask,
            publishUserDeactivatedEventTask
        );

        when(loadUserTask.shouldExecute(any())).thenReturn(true);
        when(deactivateUserTask.shouldExecute(any())).thenReturn(true);
        when(saveUserTask.shouldExecute(any())).thenReturn(true);
        when(publishUserDeactivatedEventTask.shouldExecute(any())).thenReturn(true);
    }

    @Test
    void execCommand_ShouldExecutePipelineTasks() throws Exception {
        // Arrange
        String userId = "user-123";

        // Act
        deactivateUserService.execCommand(null, null, userId);

        // Assert
        verify(loadUserTask).execute(any(UserPipelineContext.class));
        verify(deactivateUserTask).execute(any(UserPipelineContext.class));
        verify(saveUserTask).execute(any(UserPipelineContext.class));
        verify(publishUserDeactivatedEventTask).execute(any(UserPipelineContext.class));
    }
}
