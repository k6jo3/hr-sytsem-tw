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
import com.company.hrms.project.api.request.CreateProjectRequest;
import com.company.hrms.project.api.response.CreateProjectResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectType;
import com.company.hrms.project.domain.repository.IProjectRepository;

@ExtendWith(MockitoExtension.class)
public class CreateProjectServiceTest {

    @Mock
    private IProjectRepository projectRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CreateProjectServiceImpl createProjectService;

    private CreateProjectRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");
        currentUser.setUsername("Admin User");

        request = new CreateProjectRequest();
        request.setProjectCode("Test-Project-001");
        request.setProjectName("Test Project");
        request.setDescription("Description");
        request.setCustomerId(UUID.randomUUID());
        request.setProjectType(ProjectType.DEVELOPMENT);
        request.setPlannedStartDate(LocalDate.now());
        request.setPlannedEndDate(LocalDate.now().plusMonths(3));
        request.setBudgetType(BudgetType.FIXED_PRICE);
        request.setBudgetAmount(new BigDecimal("100000"));
        request.setBudgetHours(new BigDecimal("500"));
    }

    @Test
    void createProject_ShouldSucceed() throws Exception {
        // Arrange
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CreateProjectResponse response = createProjectService.execCommand(request, currentUser);

        // Assert
        assertNotNull(response.getProjectId());
        verify(projectRepository).save(any(Project.class));
        verify(eventPublisher).publishAll(any());
    }
}
