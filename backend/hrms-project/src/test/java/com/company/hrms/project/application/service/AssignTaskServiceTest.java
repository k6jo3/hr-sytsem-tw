package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.AssignTaskRequest;
import com.company.hrms.project.api.response.AssignTaskResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;

@ExtendWith(MockitoExtension.class)
public class AssignTaskServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private AssignTaskServiceImpl assignTaskService;

    @Mock
    private Task task;

    private AssignTaskRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new AssignTaskRequest();
        request.setTaskId("TASK-001");
        request.setAssigneeId(UUID.randomUUID());
    }

    @Test
    void assignTask_ShouldSucceed() throws Exception {
        // Arrange
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        AssignTaskResponse response = assignTaskService.execCommand(request, currentUser);

        // Assert
        assertTrue(response.isSuccess());
        verify(task).assign(request.getAssigneeId());
        verify(taskRepository).save(task);
    }
}
