package com.company.hrms.project.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.RemoveProjectMemberRequest;
import com.company.hrms.project.api.response.RemoveProjectMemberResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.aggregate.ProjectMember;
import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectBudget;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.model.valueobject.ProjectSchedule;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.model.valueobject.ProjectType;
import com.company.hrms.project.domain.repository.IProjectRepository;

/**
 * 移除專案成員服務單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RemoveProjectMemberServiceImpl 單元測試")
class RemoveProjectMemberServiceTest {

    @Mock
    private IProjectRepository projectRepository;

    @InjectMocks
    private RemoveProjectMemberServiceImpl removeProjectMemberService;

    private JWTModel currentUser;

    @BeforeEach
    void setup() {
        currentUser = new JWTModel();
        currentUser.setUserId("test-user");
        currentUser.setUsername("test-user");
    }

    @Nested
    @DisplayName("移除成員")
    class RemoveMemberTests {

        @Test
        @DisplayName("成功移除專案成員")
        void removeMember_ShouldSucceed() throws Exception {
            // Arrange
            ProjectId projectId = ProjectId.generate();
            UUID memberId = UUID.randomUUID();
            UUID employeeId = UUID.randomUUID();

            ProjectMember member = ProjectMember.reconstitute(
                    memberId,
                    projectId,
                    employeeId,
                    "DEVELOPER",
                    BigDecimal.valueOf(40),
                    BigDecimal.valueOf(800),
                    LocalDate.now().minusDays(30),
                    null);

            List<ProjectMember> members = new ArrayList<>();
            members.add(member);

            Project project = createProjectWithMembers(projectId, members);

            RemoveProjectMemberRequest request = new RemoveProjectMemberRequest();
            request.setProjectId(projectId.getValue().toString());
            request.setMemberId(memberId.toString());

            when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));
            when(projectRepository.save(any(Project.class))).thenReturn(project);

            // Act
            RemoveProjectMemberResponse response = removeProjectMemberService.execCommand(request, currentUser);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getMemberId()).isEqualTo(memberId.toString());
            assertThat(response.isRemoved()).isTrue();
            assertThat(response.getLeaveDate()).isEqualTo(LocalDate.now());

            verify(projectRepository).save(any(Project.class));
        }

        @Test
        @DisplayName("移除不存在的成員應拋出異常")
        void removeMember_WhenMemberNotFound_ShouldThrowException() {
            // Arrange
            ProjectId projectId = ProjectId.generate();
            UUID nonExistentMemberId = UUID.randomUUID();

            Project project = createProjectWithMembers(projectId, new ArrayList<>());

            RemoveProjectMemberRequest request = new RemoveProjectMemberRequest();
            request.setProjectId(projectId.getValue().toString());
            request.setMemberId(nonExistentMemberId.toString());

            when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));

            // Act & Assert
            assertThatThrownBy(() -> removeProjectMemberService.execCommand(request, currentUser))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("成員不存在於此專案");
        }

        @Test
        @DisplayName("專案不存在應拋出異常")
        void removeMember_WhenProjectNotFound_ShouldThrowException() {
            // Arrange
            String projectId = UUID.randomUUID().toString();
            String memberId = UUID.randomUUID().toString();

            RemoveProjectMemberRequest request = new RemoveProjectMemberRequest();
            request.setProjectId(projectId);
            request.setMemberId(memberId);

            when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> removeProjectMemberService.execCommand(request, currentUser))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("專案不存在");
        }
    }

    /**
     * 建立含成員的專案
     */
    private Project createProjectWithMembers(ProjectId projectId, List<ProjectMember> members) {
        return Project.reconstitute(
                projectId,
                "P-2026-001",
                "測試專案",
                ProjectType.DEVELOPMENT,
                LocalDate.now().minusDays(30),
                LocalDate.now().plusDays(60),
                "專案描述",
                ProjectStatus.IN_PROGRESS,
                null,
                new ProjectSchedule(LocalDate.now().minusDays(30), LocalDate.now().plusDays(60)),
                new ProjectBudget(BudgetType.FIXED_PRICE, BigDecimal.valueOf(500000), BigDecimal.valueOf(1000)),
                members,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L);
    }
}
