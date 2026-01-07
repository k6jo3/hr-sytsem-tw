package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.CreateTaskRequest;
import com.company.hrms.project.api.response.CreateTaskResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.repository.ITaskRepository;

@ExtendWith(MockitoExtension.class)
public class CreateTaskServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CreateTaskServiceImpl createTaskService;

    private CreateTaskRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");
        currentUser.setUsername("Admin User");

        request = new CreateTaskRequest();
        request.setProjectId(UUID.randomUUID());
        request.setTaskCode("TASK-001");
        request.setTaskName("Test Task");
        request.setDescription("Task Description");
        request.setEstimatedHours(new BigDecimal("10"));
        request.setPlannedStartDate(LocalDate.now());
        request.setPlannedEndDate(LocalDate.now().plusDays(5));
        request.setAssigneeId(UUID.randomUUID());
    }

    @Test
    void createTask_ShouldSucceed() throws Exception {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CreateTaskResponse response = createTaskService.execCommand(request, currentUser);

        // Assert
        assertNotNull(response.getTaskId());
        verify(taskRepository).save(any(Task.class));
        verify(eventPublisher).publishAll(any());
    }
}
