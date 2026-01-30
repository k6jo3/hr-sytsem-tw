package com.company.hrms.project.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.repository.IProjectRepository;

/**
 * Project Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>根據 ID 查詢</li>
 * <li>分頁動態查詢</li>
 * <li>根據專案狀態查詢</li>
 * <li>根據成員查詢專案</li>
 * <li>存在性檢查</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/project_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Project Repository 整合測試")
class ProjectRepositoryIntegrationTest {

    @Autowired
    private IProjectRepository projectRepository;

    // ========================================================================
    // 1. 根據 ID 查詢測試
    // ========================================================================
    @Nested
    @DisplayName("1. 根據 ID 查詢測試")
    class FindByIdTests {

        @Test
        @DisplayName("findById - 存在的專案應返回正確資料")
        void findById_ExistingProject_ShouldReturnProject() {
            // Given
            ProjectId projectId = new ProjectId("P001");

            // When
            Optional<Project> result = projectRepository.findById(projectId);

            // Then
            assertThat(result)
                    .as("應找到專案")
                    .isPresent();
            assertThat(result.get().getProjectCode())
                    .as("專案代碼應為 PRJ-2025-001")
                    .isEqualTo("PRJ-2025-001");
            assertThat(result.get().getProjectName())
                    .as("專案名稱應為 數位轉型專案")
                    .isEqualTo("數位轉型專案");
            assertThat(result.get().getStatus())
                    .as("狀態應為 IN_PROGRESS")
                    .isEqualTo(ProjectStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("findById - 不存在的專案應返回空")
        void findById_NonExisting_ShouldReturnEmpty() {
            // Given
            ProjectId projectId = new ProjectId("P999");

            // When
            Optional<Project> result = projectRepository.findById(projectId);

            // Then
            assertThat(result)
                    .as("不存在的專案應返回空")
                    .isEmpty();
        }
    }

    // ========================================================================
    // 2. 全部查詢測試
    // ========================================================================
    @Nested
    @DisplayName("2. 全部查詢測試")
    class FindAllTests {

        @Test
        @DisplayName("findAll - 應返回所有專案")
        void findAll_ShouldReturnAllProjects() {
            // When
            List<Project> result = projectRepository.findAll();

            // Then
            assertThat(result)
                    .as("應有 10 筆專案")
                    .hasSize(10);
        }
    }

    // ========================================================================
    // 3. 動態查詢測試
    // ========================================================================
    @Nested
    @DisplayName("3. 動態查詢測試")
    class FindProjectsTests {

        @Test
        @DisplayName("findProjects - 查詢進行中專案")
        void findProjects_InProgress_ShouldReturnInProgressProjects() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", ProjectStatus.IN_PROGRESS)
                    .build();

            // When
            Page<Project> result = projectRepository.findProjects(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應有 4 筆進行中專案")
                    .hasSize(4)
                    .allMatch(p -> p.getStatus() == ProjectStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("findProjects - 查詢規劃中專案")
        void findProjects_Planning_ShouldReturnPlanningProjects() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", ProjectStatus.PLANNING)
                    .build();

            // When
            Page<Project> result = projectRepository.findProjects(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應有 2 筆規劃中專案")
                    .hasSize(2)
                    .allMatch(p -> p.getStatus() == ProjectStatus.PLANNING);
        }

        @Test
        @DisplayName("findProjects - 查詢已完成專案")
        void findProjects_Completed_ShouldReturnCompletedProjects() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", ProjectStatus.COMPLETED)
                    .build();

            // When
            Page<Project> result = projectRepository.findProjects(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應有 2 筆已完成專案")
                    .hasSize(2)
                    .allMatch(p -> p.getStatus() == ProjectStatus.COMPLETED);
        }

        @Test
        @DisplayName("findProjects - 分頁查詢")
        void findProjects_Pagination_ShouldReturnCorrectPage() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<Project> page0 = projectRepository.findProjects(query, PageRequest.of(0, 5));
            Page<Project> page1 = projectRepository.findProjects(query, PageRequest.of(1, 5));

            // Then
            assertThat(page0.getNumber()).isEqualTo(0);
            assertThat(page0.getContent()).hasSize(5);
            assertThat(page1.getNumber()).isEqualTo(1);
            assertThat(page1.getContent()).hasSize(5);
            assertThat(page0.getTotalElements()).isEqualTo(10);
        }
    }

    // ========================================================================
    // 4. 成員專案查詢測試
    // ========================================================================
    @Nested
    @DisplayName("4. 成員專案查詢測試")
    class FindByMemberTests {

        @Test
        @DisplayName("findByMemberEmployeeId - E001 參與的專案")
        void findByMemberEmployeeId_E001_ShouldReturnMemberProjects() {
            // Given - E001 參與 P001 和 P002
            UUID employeeId = UUID.fromString("00000000-0000-0000-0000-000000000001");

            // When
            Page<Project> result = projectRepository.findByMemberEmployeeId(employeeId, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("E001 應參與 2 個專案")
                    .hasSize(2);
        }

        @Test
        @DisplayName("findByMemberEmployeeId - E002 參與的專案")
        void findByMemberEmployeeId_E002_ShouldReturnMemberProjects() {
            // Given - E002 參與 P001 和 P003
            UUID employeeId = UUID.fromString("00000000-0000-0000-0000-000000000002");

            // When
            Page<Project> result = projectRepository.findByMemberEmployeeId(employeeId, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("E002 應參與 2 個專案")
                    .hasSize(2);
        }

        @Test
        @DisplayName("findByMemberEmployeeId - 未參與專案的員工")
        void findByMemberEmployeeId_NoProjects_ShouldReturnEmpty() {
            // Given - E999 未參與任何專案
            UUID employeeId = UUID.fromString("00000000-0000-0000-0000-000000999999");

            // When
            Page<Project> result = projectRepository.findByMemberEmployeeId(employeeId, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("未參與專案的員工應返回空")
                    .isEmpty();
        }
    }

    // ========================================================================
    // 5. 存在性檢查測試
    // ========================================================================
    @Nested
    @DisplayName("5. 存在性檢查測試")
    class ExistsTests {

        @Test
        @DisplayName("existsById - 存在的專案應返回 true")
        void existsById_Existing_ShouldReturnTrue() {
            // When
            boolean exists = projectRepository.existsById(new ProjectId("P001"));

            // Then
            assertThat(exists)
                    .as("P001 專案應存在")
                    .isTrue();
        }

        @Test
        @DisplayName("existsById - 不存在的專案應返回 false")
        void existsById_NonExisting_ShouldReturnFalse() {
            // When
            boolean exists = projectRepository.existsById(new ProjectId("P999"));

            // Then
            assertThat(exists)
                    .as("P999 專案應不存在")
                    .isFalse();
        }
    }

    // ========================================================================
    // 6. 專案狀態統計測試
    // ========================================================================
    @Nested
    @DisplayName("6. 專案狀態統計測試")
    class ProjectStatusStatisticsTests {

        @Test
        @DisplayName("各狀態專案數量應正確")
        void statusStatistics_ShouldBeCorrect() {
            // When
            QueryGroup inProgress = QueryBuilder.where().eq("status", ProjectStatus.IN_PROGRESS).build();
            QueryGroup planning = QueryBuilder.where().eq("status", ProjectStatus.PLANNING).build();
            QueryGroup onHold = QueryBuilder.where().eq("status", ProjectStatus.ON_HOLD).build();
            QueryGroup completed = QueryBuilder.where().eq("status", ProjectStatus.COMPLETED).build();
            QueryGroup cancelled = QueryBuilder.where().eq("status", ProjectStatus.CANCELLED).build();

            Page<Project> inProgressProjects = projectRepository.findProjects(inProgress, PageRequest.of(0, 100));
            Page<Project> planningProjects = projectRepository.findProjects(planning, PageRequest.of(0, 100));
            Page<Project> onHoldProjects = projectRepository.findProjects(onHold, PageRequest.of(0, 100));
            Page<Project> completedProjects = projectRepository.findProjects(completed, PageRequest.of(0, 100));
            Page<Project> cancelledProjects = projectRepository.findProjects(cancelled, PageRequest.of(0, 100));

            // Then
            assertThat(inProgressProjects.getTotalElements()).as("IN_PROGRESS").isEqualTo(4);
            assertThat(planningProjects.getTotalElements()).as("PLANNING").isEqualTo(2);
            assertThat(onHoldProjects.getTotalElements()).as("ON_HOLD").isEqualTo(1);
            assertThat(completedProjects.getTotalElements()).as("COMPLETED").isEqualTo(2);
            assertThat(cancelledProjects.getTotalElements()).as("CANCELLED").isEqualTo(1);

            // 總和應為 10
            long total = inProgressProjects.getTotalElements() +
                    planningProjects.getTotalElements() +
                    onHoldProjects.getTotalElements() +
                    completedProjects.getTotalElements() +
                    cancelledProjects.getTotalElements();
            assertThat(total).as("總專案數").isEqualTo(10);
        }
    }
}
