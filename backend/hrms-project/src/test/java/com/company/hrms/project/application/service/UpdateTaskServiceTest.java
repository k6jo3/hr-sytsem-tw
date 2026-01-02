package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.UpdateTaskRequest;
import com.company.hrms.project.api.response.UpdateTaskResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.command.UpdateTaskCommand;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateTaskServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @InjectMocks
    private UpdateTaskServiceImpl updateTaskService;

    @Mock
    private Task task;

    private UpdateTaskRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new UpdateTaskRequest();
        request.setTaskId("TASK-001");
        request.setTaskName("Updated Task");
        request.setDescription("Updated Desc");
        request.setPlannedStartDate(LocalDate.now());
        request.setPlannedEndDate(LocalDate.now().plusDays(5));
        request.setEstimatedHours(new BigDecimal("20"));
    }

    @Test
    void updateTask_ShouldSucceed() throws Exception {
        // Arrange
        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        UpdateTaskResponse response = updateTaskService.execCommand(request, currentUser);

        // Assert
        assertTrue(response.isSuccess());
        verify(task).update(any(UpdateTaskCommand.class));
        verify(taskRepository).save(task);
    }
}
