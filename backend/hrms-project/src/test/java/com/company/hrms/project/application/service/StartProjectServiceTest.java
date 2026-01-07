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

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.StartProjectRequest;
import com.company.hrms.project.api.response.StartProjectResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.repository.IProjectRepository;

@ExtendWith(MockitoExtension.class)
public class StartProjectServiceTest {

    @Mock
    private IProjectRepository projectRepository;

    @InjectMocks
    private StartProjectServiceImpl startProjectService;

    @Mock
    private Project project;

    private StartProjectRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new StartProjectRequest();
        request.setProjectId("PRJ-001");
    }

    @Test
    void startProject_ShouldSucceed() throws Exception {
        // Arrange
        when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Mocking the behavior of project.start() to verify side effects if needed,
        // or just verify it was called.
        // Since project is a Mock, we need to stub getStatus() if it's called in
        // response.
        when(project.getStatus()).thenReturn(ProjectStatus.IN_PROGRESS);

        // Act
        StartProjectResponse response = startProjectService.execCommand(request, currentUser);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("IN_PROGRESS", response.getCurrentStatus());
        verify(project).start();
        verify(projectRepository).save(project);
    }
}
