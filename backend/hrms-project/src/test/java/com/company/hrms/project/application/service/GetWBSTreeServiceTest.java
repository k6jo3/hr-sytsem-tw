package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetWBSTreeRequest;
import com.company.hrms.project.api.response.GetWBSTreeResponse;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.model.valueobject.TaskStatus;
import com.company.hrms.project.domain.repository.ITaskRepository;

@ExtendWith(MockitoExtension.class)
public class GetWBSTreeServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @InjectMocks
    private GetWBSTreeServiceImpl getWBSTreeService;

    private GetWBSTreeRequest request;
    private JWTModel currentUser;
    // Use valid UUID for tests since Service uses UUID.fromString
    private final String PROJ_ID = "00000000-0000-0000-0000-000000000001";

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new GetWBSTreeRequest();
        request.setProjectId(PROJ_ID);
    }

    @Test
    void getWBSTree_ShouldReturnHierarchy() throws Exception {
        // Arrange
        UUID projectId = UUID.fromString(PROJ_ID);
        UUID parentUuid = UUID.randomUUID();
        UUID childUuid = UUID.randomUUID();

        // Parent Task
        Task parent = mock(Task.class);
        when(parent.getId()).thenReturn(new TaskId(parentUuid.toString()));
        when(parent.getTaskName()).thenReturn("Parent Task");
        when(parent.getParentTaskId()).thenReturn(null);
        when(parent.getStatus()).thenReturn(TaskStatus.NOT_STARTED);
        when(parent.getStartDate()).thenReturn(LocalDate.now());
        when(parent.getEndDate()).thenReturn(LocalDate.now());

        // Child Task
        Task child = mock(Task.class);
        when(child.getId()).thenReturn(new TaskId(childUuid.toString()));
        when(child.getTaskName()).thenReturn("Child Task");
        when(child.getParentTaskId()).thenReturn(parentUuid); // Point to Parent
        when(child.getStatus()).thenReturn(TaskStatus.NOT_STARTED);
        when(child.getStartDate()).thenReturn(LocalDate.now());
        when(child.getEndDate()).thenReturn(LocalDate.now());

        List<Task> tasks = Arrays.asList(parent, child);
        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);

        // Act
        GetWBSTreeResponse response = getWBSTreeService.getResponse(request, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(PROJ_ID, response.getProjectId());
        assertEquals(1, response.getRootTasks().size()); // Only parent is root
        assertEquals(parentUuid.toString(), response.getRootTasks().get(0).getTaskId());
        assertEquals(1, response.getRootTasks().get(0).getChildren().size()); // Parent has 1 child
        assertEquals(childUuid.toString(), response.getRootTasks().get(0).getChildren().get(0).getTaskId());

        verify(taskRepository).findByProjectId(projectId);
    }
}
