package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetProjectDetailRequest;
import com.company.hrms.project.api.response.GetProjectDetailResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectBudget;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.model.valueobject.ProjectSchedule;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.model.valueobject.ProjectType;
import com.company.hrms.project.domain.repository.IProjectRepository;
import com.company.hrms.project.domain.model.valueobject.CustomerId;

@ExtendWith(MockitoExtension.class)
public class GetProjectDetailServiceTest {

    @Mock
    private IProjectRepository projectRepository;

    @InjectMocks
    private GetProjectDetailServiceImpl getProjectDetailService;

    private GetProjectDetailRequest request;
    private JWTModel currentUser;
    private final String PROJ_ID = "PROJ-001";

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new GetProjectDetailRequest();
        request.setProjectId(PROJ_ID);
    }

    @Test
    void getProjectDetail_ShouldReturnData() throws Exception {
        // Arrange
        Project project = mock(Project.class);
        ProjectId projectId = new ProjectId(PROJ_ID);

        when(project.getId()).thenReturn(projectId);
        when(project.getProjectCode()).thenReturn("P001");
        when(project.getProjectName()).thenReturn("Project A");
        when(project.getDescription()).thenReturn("Desc");
        when(project.getProjectType()).thenReturn(ProjectType.DEVELOPMENT);
        when(project.getStatus()).thenReturn(ProjectStatus.PLANNING);
        when(project.getSchedule()).thenReturn(new ProjectSchedule(LocalDate.now(), LocalDate.now().plusDays(30)));
        when(project.getBudget())
                .thenReturn(new ProjectBudget(BudgetType.FIXED_PRICE, new BigDecimal("10000"), new BigDecimal("100")));
        when(project.getCustomerId()).thenReturn(
                new CustomerId(UUID.randomUUID().toString()));
        when(project.getMembers()).thenReturn(Collections.emptyList());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        GetProjectDetailResponse response = getProjectDetailService.getResponse(request, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(PROJ_ID, response.getProjectId());
        assertEquals("P001", response.getProjectCode());
        verify(projectRepository).findById(projectId);
    }
}
