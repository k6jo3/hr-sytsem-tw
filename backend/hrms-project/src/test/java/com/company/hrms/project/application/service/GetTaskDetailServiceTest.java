package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetTaskDetailRequest;
import com.company.hrms.project.api.response.GetTaskDetailResponse;
import com.company.hrms.project.application.service.task.BuildTaskDetailResponseTask;
import com.company.hrms.project.application.service.task.LoadTaskTask;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.model.valueobject.TaskStatus;
import com.company.hrms.project.domain.repository.ITaskRepository;

/**
 * GetTaskDetailServiceImpl 測試 - Business Pipeline 版本
 */
@ExtendWith(MockitoExtension.class)
public class GetTaskDetailServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private LoadTaskTask loadTaskTask;

    @Mock
    private BuildTaskDetailResponseTask buildTaskDetailResponseTask;

    @InjectMocks
    private GetTaskDetailServiceImpl getTaskDetailService;

    private GetTaskDetailRequest request;
    private JWTModel currentUser;
    private static final String TASK_ID = "00000000-0000-0000-0000-000000000001";
    private static final UUID PROJECT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new GetTaskDetailRequest();
        request.setTaskId(TASK_ID);
    }

    @Test
    void getTaskDetail_ShouldReturnData() throws Exception {
        // Arrange
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(new TaskId(TASK_ID));
        when(task.getProjectId()).thenReturn(PROJECT_ID);
        when(task.getTaskName()).thenReturn("Task 1");
        when(task.getDescription()).thenReturn("Desc");
        when(task.getStatus()).thenReturn(TaskStatus.IN_PROGRESS);
        when(task.getProgress()).thenReturn(50);
        when(task.getStartDate()).thenReturn(LocalDate.now());
        when(task.getEndDate()).thenReturn(LocalDate.now().plusDays(7));
        when(task.getEstimatedHours()).thenReturn(BigDecimal.valueOf(40));

        when(taskRepository.findById(any(TaskId.class))).thenReturn(Optional.of(task));

        // Act
        GetTaskDetailResponse response = getTaskDetailService.getResponse(request, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(TASK_ID, response.getTaskId());
        assertEquals("Task 1", response.getTaskName());
        assertEquals(50, response.getProgress());

        // Verify Pipeline Tasks were executed
        verify(loadTaskTask, times(1)).execute(any());
        verify(buildTaskDetailResponseTask, times(1)).execute(any());
    }
}
