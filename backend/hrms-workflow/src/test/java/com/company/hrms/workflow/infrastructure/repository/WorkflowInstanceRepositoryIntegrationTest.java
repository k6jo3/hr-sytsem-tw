package com.company.hrms.workflow.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

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
import com.company.hrms.common.test.base.BaseTest;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.enums.InstanceStatus;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

/**
 * WorkflowInstance Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>QueryGroup 各種操作符轉 SQL</li>
 * <li>狀態查詢 (RUNNING/COMPLETED/CANCELLED/REJECTED)</li>
 * <li>業務類型查詢 (LEAVE/OVERTIME/EXPENSE)</li>
 * <li>部門過濾</li>
 * <li>申請人查詢</li>
 * <li>日期範圍查詢</li>
 * <li>分頁查詢</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/workflow_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("WorkflowInstance Repository 整合測試")
class WorkflowInstanceRepositoryIntegrationTest extends BaseTest {

    @Autowired
    private IWorkflowInstanceRepository workflowInstanceRepository;

    // ========================================================================
    // 1. 狀態查詢測試
    // ========================================================================
    @Nested
    @DisplayName("狀態查詢測試")
    class StatusQueryTests {

        @Test
        @DisplayName("WFL_I001: 查詢進行中流程實例 (RUNNING)")
        void WFL_I001_QueryRunningInstances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", InstanceStatus.RUNNING)
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I001: 應返回所有進行中的流程實例")
                    .hasSize(4)
                    .allMatch(inst -> inst.getStatus() == InstanceStatus.RUNNING);
        }

        @Test
        @DisplayName("WFL_I002: 查詢已完成流程實例 (COMPLETED)")
        void WFL_I002_QueryCompletedInstances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", InstanceStatus.COMPLETED)
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I002: 應返回所有已完成的流程實例")
                    .hasSize(3)
                    .allMatch(inst -> inst.getStatus() == InstanceStatus.COMPLETED);
        }

        @Test
        @DisplayName("WFL_I003: 查詢已取消流程實例 (CANCELLED)")
        void WFL_I003_QueryCancelledInstances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", InstanceStatus.CANCELLED)
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I003: 應返回所有已取消的流程實例")
                    .hasSize(2)
                    .allMatch(inst -> inst.getStatus() == InstanceStatus.CANCELLED);
        }

        @Test
        @DisplayName("WFL_I004: 查詢已駁回流程實例 (REJECTED)")
        void WFL_I004_QueryRejectedInstances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", InstanceStatus.REJECTED)
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I004: 應返回所有已駁回的流程實例")
                    .hasSize(1)
                    .allMatch(inst -> inst.getStatus() == InstanceStatus.REJECTED);
        }

        @Test
        @DisplayName("多狀態查詢 - IN 操作符")
        void queryByMultipleStatuses_IN() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .in("status", (Object[]) new InstanceStatus[] { InstanceStatus.RUNNING, InstanceStatus.COMPLETED })
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回 RUNNING 或 COMPLETED 的流程實例")
                    .hasSize(7)
                    .allMatch(inst -> inst.getStatus() == InstanceStatus.RUNNING ||
                            inst.getStatus() == InstanceStatus.COMPLETED);
        }
    }

    // ========================================================================
    // 2. 業務類型查詢測試
    // ========================================================================
    @Nested
    @DisplayName("業務類型查詢測試")
    class BusinessTypeQueryTests {

        @Test
        @DisplayName("WFL_BT001: 查詢請假類型流程")
        void WFL_BT001_QueryLeaveInstances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("businessType", "LEAVE")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回所有請假類型的流程實例")
                    .hasSize(5)
                    .allMatch(inst -> "LEAVE".equals(inst.getBusinessType()));
        }

        @Test
        @DisplayName("WFL_BT002: 查詢加班類型流程")
        void WFL_BT002_QueryOvertimeInstances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("businessType", "OVERTIME")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回所有加班類型的流程實例")
                    .hasSize(3)
                    .allMatch(inst -> "OVERTIME".equals(inst.getBusinessType()));
        }

        @Test
        @DisplayName("WFL_BT003: 查詢費用報銷類型流程")
        void WFL_BT003_QueryExpenseInstances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("businessType", "EXPENSE")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回所有費用報銷類型的流程實例")
                    .hasSize(2)
                    .allMatch(inst -> "EXPENSE".equals(inst.getBusinessType()));
        }
    }

    // ========================================================================
    // 3. 部門查詢測試
    // ========================================================================
    @Nested
    @DisplayName("部門查詢測試")
    class DepartmentQueryTests {

        @Test
        @DisplayName("WFL_DEPT001: 查詢研發部流程")
        void WFL_DEPT001_QueryD001Instances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("departmentId", "D001")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回研發部的所有流程實例")
                    .hasSize(6)
                    .allMatch(inst -> "D001".equals(inst.getDepartmentId()));
        }

        @Test
        @DisplayName("WFL_DEPT002: 查詢業務部流程")
        void WFL_DEPT002_QueryD002Instances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("departmentId", "D002")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回業務部的所有流程實例")
                    .hasSize(3)
                    .allMatch(inst -> "D002".equals(inst.getDepartmentId()));
        }
    }

    // ========================================================================
    // 4. 申請人查詢測試
    // ========================================================================
    @Nested
    @DisplayName("申請人查詢測試")
    class ApplicantQueryTests {

        @Test
        @DisplayName("WFL_APP001: 查詢特定申請人的流程")
        void WFL_APP001_QueryByApplicantId() {
            // Given - 張三 (E001)
            QueryGroup query = QueryBuilder.where()
                    .eq("applicantId", "E001")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回張三申請的所有流程實例")
                    .hasSize(3)
                    .allMatch(inst -> "E001".equals(inst.getApplicantId()));
        }

        @Test
        @DisplayName("LIKE - 依申請人名稱模糊查詢")
        void queryByApplicantName_Like() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("applicantName", "%三%")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應找到申請人名稱包含 '三' 的流程實例")
                    .hasSize(3); // 張三
        }
    }

    // ========================================================================
    // 5. 複合條件測試
    // ========================================================================
    @Nested
    @DisplayName("複合條件測試")
    class CompoundConditionTests {

        @Test
        @DisplayName("申請人 + 狀態複合查詢")
        void queryByApplicantAndStatus() {
            // Given - 張三的進行中流程
            QueryGroup query = QueryBuilder.where()
                    .eq("applicantId", "E001")
                    .eq("status", InstanceStatus.RUNNING)
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回張三進行中的流程實例")
                    .hasSize(2)
                    .allMatch(inst -> "E001".equals(inst.getApplicantId()) &&
                            inst.getStatus() == InstanceStatus.RUNNING);
        }

        @Test
        @DisplayName("部門 + 業務類型複合查詢")
        void queryByDepartmentAndBusinessType() {
            // Given - 研發部的請假流程
            QueryGroup query = QueryBuilder.where()
                    .eq("departmentId", "D001")
                    .eq("businessType", "LEAVE")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回研發部的請假流程")
                    .hasSize(4)
                    .allMatch(inst -> "D001".equals(inst.getDepartmentId()) &&
                            "LEAVE".equals(inst.getBusinessType()));
        }

        @Test
        @DisplayName("三重條件複合查詢")
        void queryByThreeConditions() {
            // Given - 研發部 + 請假 + 進行中
            QueryGroup query = QueryBuilder.where()
                    .eq("departmentId", "D001")
                    .eq("businessType", "LEAVE")
                    .eq("status", InstanceStatus.RUNNING)
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回研發部進行中的請假流程")
                    .hasSize(2);
        }
    }

    // ========================================================================
    // 6. 日期範圍查詢測試
    // ========================================================================
    @Nested
    @DisplayName("日期範圍查詢測試")
    class DateRangeQueryTests {

        @Test
        @DisplayName("GTE - 依啟動時間查詢")
        void queryByStartedAt_GTE() {
            // Given - 2025-01-15 之後啟動的流程
            QueryGroup query = QueryBuilder.where()
                    .gte("startedAt", "2025-01-15")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回 2025-01-15 之後啟動的流程")
                    .hasSize(5); // PI-001 ~ PI-004, PI-010
        }

        @Test
        @DisplayName("IS_NOT_NULL - 已完成的流程 (有完成時間)")
        void queryByCompletedAt_IsNotNull() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .isNotNull("completedAt")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then - COMPLETED + CANCELLED + REJECTED = 6
            assertThat(result.getContent())
                    .as("應返回有完成時間的流程")
                    .hasSize(6);
        }

        @Test
        @DisplayName("IS_NULL - 未完成的流程 (無完成時間)")
        void queryByCompletedAt_IsNull() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .isNull("completedAt")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then - RUNNING = 4
            assertThat(result.getContent())
                    .as("應返回沒有完成時間的流程 (進行中)")
                    .hasSize(4);
        }
    }

    // ========================================================================
    // 7. 分頁測試
    // ========================================================================
    @Nested
    @DisplayName("分頁測試")
    class PaginationTests {

        @Test
        @DisplayName("分頁查詢 - 第一頁")
        void findAll_Page0_ShouldReturnFirstPage() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 3));

            // Then
            assertThat(result.getNumber())
                    .as("頁碼應為 0")
                    .isEqualTo(0);
            assertThat(result.getContent())
                    .as("第一頁應返回 3 筆")
                    .hasSize(3);
            assertThat(result.getTotalElements())
                    .as("總筆數應為 10")
                    .isEqualTo(10);
        }

        @Test
        @DisplayName("分頁查詢 - 第二頁")
        void findAll_Page1_ShouldReturnSecondPage() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(1, 3));

            // Then
            assertThat(result.getNumber())
                    .as("頁碼應為 1")
                    .isEqualTo(1);
            assertThat(result.getContent())
                    .as("第二頁應有資料")
                    .isNotEmpty();
        }

        @Test
        @DisplayName("分頁查詢 - 最後一頁")
        void findAll_LastPage_ShouldReturnPartialPage() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(3, 3));

            // Then
            assertThat(result.getNumber())
                    .as("頁碼應為 3")
                    .isEqualTo(3);
            assertThat(result.getContent())
                    .as("最後一頁只有 1 筆")
                    .hasSize(1);
        }

        @Test
        @DisplayName("帶條件的分頁查詢")
        void findWithCondition_ShouldPaginateCorrectly() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("departmentId", "D001")
                    .build();

            // When
            Page<WorkflowInstance> page1 = workflowInstanceRepository.search(query, PageRequest.of(0, 3));
            Page<WorkflowInstance> page2 = workflowInstanceRepository.search(query, PageRequest.of(1, 3));

            // Then
            assertThat(page1.getTotalElements()).isEqualTo(6);
            assertThat(page1.getContent()).hasSize(3);
            assertThat(page2.getContent()).hasSize(3);
        }
    }

    // ========================================================================
    // 8. LIKE 操作符測試
    // ========================================================================
    @Nested
    @DisplayName("LIKE 操作符測試")
    class LikeOperatorTests {

        @Test
        @DisplayName("LIKE - 依摘要模糊查詢 (特休)")
        void queryBySummary_Like_SpecialLeave() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("summary", "%特休%")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應找到摘要包含 '特休' 的流程實例")
                    .hasSize(3);
        }

        @Test
        @DisplayName("LIKE - 依摘要模糊查詢 (加班)")
        void queryBySummary_Like_Overtime() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("summary", "%加班%")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應找到摘要包含 '加班' 的流程實例")
                    .hasSize(3);
        }
    }

    // ========================================================================
    // 9. NOT_IN 操作符測試
    // ========================================================================
    @Nested
    @DisplayName("NOT_IN 操作符測試")
    class NotInOperatorTests {

        @Test
        @DisplayName("NOT_IN - 排除多個狀態")
        void queryByStatus_NotIn() {
            // Given - 排除 RUNNING 和 COMPLETED
            QueryGroup query = QueryBuilder.where()
                    .notIn("status", (Object[]) new InstanceStatus[] { InstanceStatus.RUNNING, InstanceStatus.COMPLETED })
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回 CANCELLED 或 REJECTED 的流程實例")
                    .hasSize(3)
                    .noneMatch(inst -> inst.getStatus() == InstanceStatus.RUNNING ||
                            inst.getStatus() == InstanceStatus.COMPLETED);
        }

        @Test
        @DisplayName("NOT_IN - 排除多個業務類型")
        void queryByBusinessType_NotIn() {
            // Given - 排除 LEAVE 類型
            QueryGroup query = QueryBuilder.where()
                    .notIn("businessType", (Object[]) new String[] { "LEAVE" })
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應返回非請假類型的流程實例")
                    .hasSize(5)
                    .noneMatch(inst -> "LEAVE".equals(inst.getBusinessType()));
        }
    }
}
