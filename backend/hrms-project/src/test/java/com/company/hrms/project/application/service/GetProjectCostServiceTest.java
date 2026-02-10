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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetProjectCostRequest;
import com.company.hrms.project.api.response.GetProjectCostResponse;
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
 * 查詢專案成本服務單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetProjectCostServiceImpl 單元測試")
class GetProjectCostServiceTest {

        @Mock
        private IProjectRepository projectRepository;

        private GetProjectCostServiceImpl getProjectCostService;

        private JWTModel currentUser;

        @BeforeEach
        void setup() {
                currentUser = new JWTModel();
                currentUser.setUserId("test-user");
                currentUser.setUsername("test-user");

                // 手動注入實例，確保 Pipeline 中的 Task 不是 null
                var loadTask = new com.company.hrms.project.application.service.task.LoadProjectForCostTask(
                                projectRepository);
                var calculateTask = new com.company.hrms.project.application.service.task.CalculateProjectCostTask();
                getProjectCostService = new GetProjectCostServiceImpl(loadTask, calculateTask);
        }

        @Nested
        @DisplayName("查詢專案成本")
        class GetProjectCostTests {

                @Test
                @DisplayName("成功查詢專案成本")
                void getProjectCost_ShouldReturnCostData() throws Exception {
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
                                        LocalDate.now().minusDays(30),
                                        null);

                        List<ProjectMember> members = new ArrayList<>();
                        members.add(member);

                        Project project = createProjectWithCost(projectId, members,
                                        BigDecimal.valueOf(200), BigDecimal.valueOf(100000));

                        GetProjectCostRequest request = new GetProjectCostRequest();
                        request.setProjectId(projectId.getValue().toString());

                        when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));

                        // Act
                        GetProjectCostResponse response = getProjectCostService.getResponse(request, currentUser);

                        // Assert
                        assertThat(response).isNotNull();
                        assertThat(response.getProjectId()).isEqualTo(projectId.getValue().toString());
                        assertThat(response.getProjectCode()).isEqualTo("P-2026-001");

                        // 驗證預算資訊
                        assertThat(response.getBudget()).isNotNull();
                        assertThat(response.getBudget().getBudgetAmount())
                                        .isEqualByComparingTo(BigDecimal.valueOf(500000));
                        assertThat(response.getBudget().getBudgetHours())
                                        .isEqualByComparingTo(BigDecimal.valueOf(1000));

                        // 驗證成本摘要
                        assertThat(response.getSummary()).isNotNull();
                        assertThat(response.getSummary().getTotalHours()).isEqualByComparingTo(BigDecimal.valueOf(200));
                        assertThat(response.getSummary().getTotalCost())
                                        .isEqualByComparingTo(BigDecimal.valueOf(100000));
                        assertThat(response.getSummary().getBudgetUtilization())
                                        .isEqualByComparingTo(BigDecimal.valueOf(20.00));

                        // 驗證成員成本
                        assertThat(response.getByMember()).hasSize(1);
                        assertThat(response.getByMember().get(0).getRole()).isEqualTo("DEVELOPER");
                }

                @Test
                @DisplayName("查詢無成本記錄的專案")
                void getProjectCost_NoCostRecords_ShouldReturnZeroValues() throws Exception {
                        // Arrange
                        ProjectId projectId = ProjectId.generate();
                        Project project = createProjectWithCost(projectId, new ArrayList<>(),
                                        BigDecimal.ZERO, BigDecimal.ZERO);

                        GetProjectCostRequest request = new GetProjectCostRequest();
                        request.setProjectId(projectId.getValue().toString());

                        when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));

                        // Act
                        GetProjectCostResponse response = getProjectCostService.getResponse(request, currentUser);

                        // Assert
                        assertThat(response.getSummary().getTotalHours()).isEqualByComparingTo(BigDecimal.ZERO);
                        assertThat(response.getSummary().getTotalCost()).isEqualByComparingTo(BigDecimal.ZERO);
                        assertThat(response.getSummary().getBudgetUtilization()).isEqualByComparingTo(BigDecimal.ZERO);
                        assertThat(response.getByMember()).isEmpty();
                }

                @Test
                @DisplayName("專案不存在應拋出異常")
                void getProjectCost_WhenProjectNotFound_ShouldThrowException() {
                        // Arrange
                        String projectId = UUID.randomUUID().toString();

                        GetProjectCostRequest request = new GetProjectCostRequest();
                        request.setProjectId(projectId);

                        when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.empty());

                        // Act & Assert
                        assertThatThrownBy(() -> getProjectCostService.getResponse(request, currentUser))
                                        .hasMessageContaining("專案不存在");

                }

                @Test
                @DisplayName("計算預算利用率與毛利率")
                void getProjectCost_ShouldCalculateUtilizationAndProfit() throws Exception {
                        // Arrange
                        ProjectId projectId = ProjectId.generate();
                        Project project = createProjectWithCost(projectId, new ArrayList<>(),
                                        BigDecimal.valueOf(500), BigDecimal.valueOf(250000));

                        GetProjectCostRequest request = new GetProjectCostRequest();
                        request.setProjectId(projectId.getValue().toString());

                        when(projectRepository.findById(any(ProjectId.class))).thenReturn(Optional.of(project));

                        // Act
                        GetProjectCostResponse response = getProjectCostService.getResponse(request, currentUser);

                        // Assert
                        // 預算 500,000，實際成本 250,000，利用率 50%
                        assertThat(response.getSummary().getBudgetUtilization())
                                        .isEqualByComparingTo(BigDecimal.valueOf(50.00));

                        // 工時預算 1000，實際工時 500，利用率 50%
                        assertThat(response.getSummary().getHoursUtilization())
                                        .isEqualByComparingTo(BigDecimal.valueOf(50.00));

                        // 預估毛利 = 500,000 - 250,000 = 250,000
                        assertThat(response.getSummary().getEstimatedGrossProfit())
                                        .isEqualByComparingTo(BigDecimal.valueOf(250000));

                        // 預估毛利率 = 250,000 / 500,000 * 100 = 50%
                        assertThat(response.getSummary().getEstimatedGrossProfitMargin())
                                        .isEqualByComparingTo(BigDecimal.valueOf(50.00));
                }
        }

        /**
         * 建立含成本的專案
         */
        private Project createProjectWithCost(ProjectId projectId, List<ProjectMember> members,
                        BigDecimal actualHours, BigDecimal actualCost) {
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
                                new ProjectBudget(BudgetType.FIXED_PRICE, BigDecimal.valueOf(500000),
                                                BigDecimal.valueOf(1000)),
                                members,
                                actualHours,
                                actualCost,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                1L);
        }
}
