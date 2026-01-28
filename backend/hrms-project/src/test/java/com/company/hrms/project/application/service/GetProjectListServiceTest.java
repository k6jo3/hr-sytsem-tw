package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.api.request.GetProjectListRequest;
import com.company.hrms.project.api.response.GetProjectListResponse;
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
public class GetProjectListServiceTest {

    @Mock
    private IProjectRepository projectRepository;

    @InjectMocks
    private GetProjectListServiceImpl getProjectListService;

    private GetProjectListRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");

        request = new GetProjectListRequest();
        request.setPage(0);
        request.setSize(10);
    }

    @Test
    void getProjectList_ShouldReturnData() throws Exception {
        // Arrange
        Project project = mock(Project.class);
        when(project.getId()).thenReturn(new ProjectId("PROJ-001"));
        when(project.getProjectCode()).thenReturn("P001");
        when(project.getProjectName()).thenReturn("Project A");
        when(project.getProjectType()).thenReturn(ProjectType.DEVELOPMENT);
        when(project.getStatus()).thenReturn(ProjectStatus.PLANNING);
        when(project.getSchedule()).thenReturn(new ProjectSchedule(LocalDate.now(), LocalDate.now().plusDays(30)));
        when(project.getBudget())
                .thenReturn(new ProjectBudget(BudgetType.FIXED_PRICE, new BigDecimal("10000"), new BigDecimal("100")));
        // when(project.getOwnerId()).thenReturn(UUID.randomUUID());
        when(project.getCustomerId()).thenReturn(
                new CustomerId(UUID.randomUUID().toString()));

        Page<Project> pageResult = new PageImpl<>(Collections.singletonList(project));
        when(projectRepository.findProjects(any(QueryGroup.class), any(Pageable.class))).thenReturn(pageResult);

        // Act
        GetProjectListResponse response = getProjectListService.getResponse(request, currentUser);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotal());
        assertEquals("P001", response.getItems().get(0).getProjectCode());
        verify(projectRepository).findProjects(any(QueryGroup.class), any(Pageable.class));
    }
}
