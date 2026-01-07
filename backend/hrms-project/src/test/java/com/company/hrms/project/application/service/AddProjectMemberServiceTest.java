package com.company.hrms.project.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.AddProjectMemberRequest;
import com.company.hrms.project.api.response.AddProjectMemberResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

@ExtendWith(MockitoExtension.class)
public class AddProjectMemberServiceTest {

    @Mock
    private IProjectRepository projectRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private AddProjectMemberServiceImpl addProjectMemberService;

    @Mock
    private Project project;

    private AddProjectMemberRequest request;
    private JWTModel currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new JWTModel();
        currentUser.setUserId("USER-001");
        currentUser.setUsername("Admin User");

        request = new AddProjectMemberRequest();
        request.setProjectId("PRJ-ID-001");
        request.setEmployeeId(UUID.randomUUID());
        request.setRole("Developer");
        request.setAllocatedHours(new BigDecimal("100"));
    }

    @Test
    void addMember_ShouldSucceed() throws Exception {
        // Arrange
        when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Act
        AddProjectMemberResponse response = addProjectMemberService.execCommand(request, currentUser);

        // Assert
        assertTrue(response.isSuccess());
        verify(project).addMember(request.getEmployeeId(), request.getRole(), request.getAllocatedHours());
        verify(projectRepository).save(project);
    }
}
