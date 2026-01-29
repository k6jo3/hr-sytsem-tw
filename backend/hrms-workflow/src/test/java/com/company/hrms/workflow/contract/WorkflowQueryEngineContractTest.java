package com.company.hrms.workflow.contract;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseQueryEngineContractTest;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

/**
 * Workflow QueryEngine 契約測試
 *
 * <p>
 * 驗證 QueryEngine 各種操作符在 WorkflowInstance 實體上的正確運作
 * 使用 H2 資料庫實際執行 SQL 查詢
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/workflow_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Workflow QueryEngine 契約測試")
class WorkflowQueryEngineContractTest extends BaseQueryEngineContractTest<WorkflowInstance> {

    @Autowired
    private IWorkflowInstanceRepository workflowInstanceRepository;

    @Override
    protected String getTestDataScript() {
        return "classpath:test-data/workflow_test_data.sql";
    }

    @Override
    protected Page<WorkflowInstance> executeQuery(QueryGroup query) {
        return workflowInstanceRepository.search(query, PageRequest.of(0, 100));
    }

    /**
     * 提供參數化測試案例
     * 格式: (操作符名稱, 欄位名稱, 測試值, 預期結果數量)
     *
     * 測試資料分布 (workflow_test_data.sql):
     * - RUNNING: 4 筆 (PI-001, PI-002, PI-003, PI-004)
     * - COMPLETED: 3 筆 (PI-005, PI-006, PI-007)
     * - CANCELLED: 2 筆 (PI-008, PI-009)
     * - REJECTED: 1 筆 (PI-010)
     *
     * 業務類型分布:
     * - LEAVE: 5 筆
     * - OVERTIME: 3 筆
     * - EXPENSE: 2 筆
     *
     * 部門分布:
     * - D001 (研發部): 6 筆
     * - D002 (業務部): 3 筆
     * - D003 (財務部): 1 筆
     */
    static Stream<Arguments> operatorTestCases() {
        return Stream.of(
                // EQ 操作符測試
                Arguments.of("EQ", "status", "RUNNING", 4),
                Arguments.of("EQ", "status", "COMPLETED", 3),
                Arguments.of("EQ", "status", "CANCELLED", 2),
                Arguments.of("EQ", "status", "REJECTED", 1),
                Arguments.of("EQ", "business_type", "LEAVE", 5),
                Arguments.of("EQ", "business_type", "OVERTIME", 3),
                Arguments.of("EQ", "business_type", "EXPENSE", 2),
                Arguments.of("EQ", "department_id", "D001", 6),

                // NE 操作符測試
                Arguments.of("NE", "status", "RUNNING", 6),
                Arguments.of("NE", "status", "COMPLETED", 7),

                // IN 操作符測試
                Arguments.of("IN", "status", List.of("RUNNING", "COMPLETED"), 7),
                Arguments.of("IN", "status", List.of("CANCELLED", "REJECTED"), 3),
                Arguments.of("IN", "business_type", List.of("LEAVE", "OVERTIME"), 8),

                // NOT_IN 操作符測試
                Arguments.of("NOT_IN", "status", List.of("RUNNING", "COMPLETED"), 3),
                Arguments.of("NOT_IN", "business_type", List.of("EXPENSE"), 8),

                // IS_NOT_NULL 操作符測試 (completed_at 不為空)
                Arguments.of("IS_NOT_NULL", "completed_at", null, 6),

                // IS_NULL 操作符測試 (completed_at 為空 = RUNNING 狀態)
                Arguments.of("IS_NULL", "completed_at", null, 4));
    }

    @Nested
    @DisplayName("LIKE 操作符測試")
    class LikeOperatorTests {

        @Test
        @DisplayName("LIKE - 依摘要模糊查詢")
        void like_Summary_ShouldReturnMatchingRecords() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("summary", "%特休%")
                    .build();

            // When
            Page<WorkflowInstance> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到摘要包含 '特休' 的流程實例")
                    .hasSize(3);
        }

        @Test
        @DisplayName("LIKE - 依申請人名稱模糊查詢")
        void like_ApplicantName_ShouldReturnMatchingRecords() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("applicant_name", "%張%")
                    .build();

            // When
            Page<WorkflowInstance> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到申請人包含 '張' 的流程實例")
                    .hasSize(3); // 張三有 3 筆
        }
    }

    @Nested
    @DisplayName("日期範圍測試")
    class DateRangeTests {

        @Test
        @DisplayName("GTE - 依啟動時間範圍查詢")
        void gte_StartedAt_ShouldReturnMatchingRecords() {
            // Given - 查詢 2025-01-15 之後啟動的流程
            QueryGroup query = QueryBuilder.where()
                    .gte("started_at", "2025-01-15")
                    .build();

            // When
            Page<WorkflowInstance> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 2025-01-15 之後啟動的流程實例")
                    .isNotEmpty();
        }

        @Test
        @DisplayName("LTE - 依啟動時間範圍查詢")
        void lte_StartedAt_ShouldReturnMatchingRecords() {
            // Given - 查詢 2025-01-01 之前啟動的流程 (2024 年的)
            QueryGroup query = QueryBuilder.where()
                    .lte("started_at", "2025-01-01")
                    .build();

            // When
            Page<WorkflowInstance> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 2025-01-01 之前啟動的流程實例")
                    .hasSize(3); // PI-005, PI-006, PI-007
        }
    }

    @Nested
    @DisplayName("複合條件測試")
    class CompoundConditionTests {

        @Test
        @DisplayName("AND 條件 - 狀態 + 部門")
        void andCondition_StatusAndDepartment_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "RUNNING")
                    .eq("department_id", "D001")
                    .build();

            // When
            Page<WorkflowInstance> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 RUNNING 且部門為 D001 的流程實例")
                    .hasSize(3) // PI-001, PI-002, PI-003
                    .allMatch(inst -> "D001".equals(inst.getDepartmentId()));
        }

        @Test
        @DisplayName("AND 條件 - 業務類型 + 申請人")
        void andCondition_BusinessTypeAndApplicant_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("business_type", "LEAVE")
                    .eq("applicant_id", "E001")
                    .build();

            // When
            Page<WorkflowInstance> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 LEAVE 且申請人為 E001 的流程實例")
                    .hasSize(2) // PI-001, PI-005
                    .allMatch(inst -> "E001".equals(inst.getApplicantId()));
        }
    }

    @Nested
    @DisplayName("分頁與排序測試")
    class PaginationAndSortTests {

        @Test
        @DisplayName("分頁查詢 - 第一頁")
        void pagination_FirstPage_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 3));

            // Then
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(10);
        }

        @Test
        @DisplayName("分頁查詢 - 最後一頁")
        void pagination_LastPage_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(3, 3));

            // Then
            assertThat(result.getNumber()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(1); // 10 筆，第 4 頁只有 1 筆
        }
    }
}
