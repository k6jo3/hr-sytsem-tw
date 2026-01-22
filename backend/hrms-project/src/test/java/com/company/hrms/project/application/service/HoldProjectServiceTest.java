package com.company.hrms.project.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.company.hrms.project.api.request.HoldProjectRequest;
import com.company.hrms.project.api.response.HoldProjectResponse;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectBudget;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.model.valueobject.ProjectSchedule;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.model.valueobject.ProjectType;
import com.company.hrms.project.domain.repository.IProjectRepository;

/**
 * 暫停專案服務單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HoldProjectServiceImpl 單元測試")
class HoldProjectServiceTest {

    @Mock
    private IProjectRepository projectRepository;

    @InjectMocks
    private HoldProjectServiceImpl holdProjectService;

    private JWTModel currentUser;

    @BeforeEach
    void setup() {
        currentUser = new JWTModel();
        currentUser.setUserId("test-user");
        currentUser.setUsername("test-user");
    }

    @Nested
    @DisplayName("暫停專案")
    class HoldProjectTests {

        @Test
        @DisplayName("成功暫停進行中的專案")
        void holdProject_WhenInProgress_ShouldSucceed() throws Exception {
            // Arrange
            ProjectId projectId = ProjectId.generate();
            Project project = createInProgressProject(projectId);

            HoldProjectRequest request = new HoldProjectRequest();
            request.setProjectId(projectId.getValue().toString());
            request.setReason("客戶需求變更，暫停開發");

            when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));
            when(projectRepository.save(any(Project.class))).thenReturn(project);

            // Act
            HoldProjectResponse response = holdProjectService.execCommand(request, currentUser);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo(ProjectStatus.ON_HOLD);
            assertThat(response.getHoldReason()).isEqualTo("客戶需求變更，暫停開發");
            assertThat(response.getHoldDate()).isEqualTo(LocalDate.now());

            verify(projectRepository).save(any(Project.class));
        }

        @Test
        @DisplayName("暫停非進行中的專案應拋出異常")
        void holdProject_WhenNotInProgress_ShouldThrowException() {
            // Arrange
            ProjectId projectId = ProjectId.generate();
            Project project = createPlanningProject(projectId);

            HoldProjectRequest request = new HoldProjectRequest();
            request.setProjectId(projectId.getValue().toString());
            request.setReason("測試暫停");

            when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));

            // Act & Assert
            assertThatThrownBy(() -> holdProjectService.execCommand(request, currentUser))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("只有進行中的專案可以暫停");
        }

        @Test
        @DisplayName("無暫停原因應拋出異常")
        void holdProject_WithoutReason_ShouldThrowException() {
            // Arrange
            ProjectId projectId = ProjectId.generate();
            Project project = createInProgressProject(projectId);

            HoldProjectRequest request = new HoldProjectRequest();
            request.setProjectId(projectId.getValue().toString());
            request.setReason("");

            when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));

            // Act & Assert
            assertThatThrownBy(() -> holdProjectService.execCommand(request, currentUser))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("暫停原因為必填");
        }

        @Test
        @DisplayName("專案不存在應拋出異常")
        void holdProject_WhenProjectNotFound_ShouldThrowException() {
            // Arrange
            String projectId = UUID.randomUUID().toString();
            HoldProjectRequest request = new HoldProjectRequest();
            request.setProjectId(projectId);
            request.setReason("測試暫停");

            when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> holdProjectService.execCommand(request, currentUser))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("專案不存在");
        }
    }

    /**
     * 建立進行中專案
     */
    private Project createInProgressProject(ProjectId projectId) {
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
                new ArrayList<>(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L);
    }

    /**
     * 建立規劃中專案
     */
    private Project createPlanningProject(ProjectId projectId) {
        return Project.reconstitute(
                projectId,
                "P-2026-002",
                "規劃中專案",
                ProjectType.DEVELOPMENT,
                LocalDate.now(),
                LocalDate.now().plusDays(90),
                "專案描述",
                ProjectStatus.PLANNING,
                null,
                new ProjectSchedule(LocalDate.now(), LocalDate.now().plusDays(90)),
                new ProjectBudget(BudgetType.FIXED_PRICE, BigDecimal.valueOf(500000), BigDecimal.valueOf(1000)),
                new ArrayList<>(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L);
    }
}
