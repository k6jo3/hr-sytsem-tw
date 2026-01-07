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
import com.company.hrms.project.api.request.CompleteProjectRequest;
import com.company.hrms.project.api.response.CompleteProjectResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.repository.IProjectRepository;

@ExtendWith(MockitoExtension.class)
public class CompleteProjectServiceTest {

    @Mock
    private IProjectRepository projectRepository;

    @InjectMocks
    private CompleteProjectServiceImpl completeProjectService;

    @Mock
    private Project project;

    private CompleteProjectRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new CompleteProjectRequest();
        request.setProjectId("PRJ-001");
    }

    @Test
    void completeProject_ShouldSucceed() throws Exception {
        // Arrange
        when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        when(project.getStatus()).thenReturn(ProjectStatus.COMPLETED);

        // Act
        CompleteProjectResponse response = completeProjectService.execCommand(request, currentUser);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("COMPLETED", response.getCurrentStatus());
        verify(project).complete();
        verify(projectRepository).save(project);
    }
}
