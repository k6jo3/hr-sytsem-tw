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
import com.company.hrms.project.api.request.UpdateProjectRequest;
import com.company.hrms.project.api.response.UpdateProjectResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.command.UpdateProjectCommand;
import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.model.valueobject.ProjectType;
import com.company.hrms.project.domain.repository.IProjectRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateProjectServiceTest {

    @Mock
    private IProjectRepository projectRepository;

    @InjectMocks
    private UpdateProjectServiceImpl updateProjectService;

    @Mock
    private Project project;

    private UpdateProjectRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new UpdateProjectRequest();
        request.setProjectId("PRJ-001");
        request.setProjectName("Updated Name");
        request.setProjectType(ProjectType.MAINTENANCE);
        request.setDescription("Updated Desc");
        request.setPlannedStartDate(LocalDate.now());
        request.setPlannedEndDate(LocalDate.now().plusMonths(1));
        request.setBudgetType(BudgetType.TIME_AND_MATERIAL);
        request.setBudgetAmount(new BigDecimal("50000"));
        request.setBudgetHours(new BigDecimal("100"));
    }

    @Test
    void updateProject_ShouldSucceed() throws Exception {
        // Arrange
        when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Act
        UpdateProjectResponse response = updateProjectService.execCommand(request, currentUser);

        // Assert
        assertTrue(response.isSuccess());
        verify(project).update(any(UpdateProjectCommand.class));
        verify(projectRepository).save(project);
    }
}
