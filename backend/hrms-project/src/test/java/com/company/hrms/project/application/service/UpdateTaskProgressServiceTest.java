package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.UpdateTaskProgressRequest;
import com.company.hrms.project.api.response.UpdateTaskProgressResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.model.valueobject.TaskStatus;
import com.company.hrms.project.domain.repository.ITaskRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateTaskProgressServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private UpdateTaskProgressServiceImpl updateTaskProgressService;

    @Mock
    private Task task;

    private UpdateTaskProgressRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new UpdateTaskProgressRequest();
        request.setTaskId("TASK-001");
        request.setProgress(50);
    }

    @Test
    void updateProgress_ShouldSucceed() throws Exception {
        // Arrange
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(task.getStatus()).thenReturn(TaskStatus.IN_PROGRESS);
        when(task.getProgress()).thenReturn(50);

        // Act
        UpdateTaskProgressResponse response = updateTaskProgressService.execCommand(request, currentUser);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(50, response.getCurrentProgress());
        verify(task).updateProgress(50);
        verify(taskRepository).save(task);
    }
}
