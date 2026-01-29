package com.company.hrms.workflow.contract;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

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
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;

/**
 * Workflow 業務合約測試
 *
 * <p>驗證 workflow_contracts.md 定義的業務查詢場景
 * 每個測試對應合約文件中的一個場景 ID
 *
 * @author SA Team
 * @since 2026-01-29
 * @see contracts/workflow_contracts.md
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/workflow_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Workflow 業務合約測試")
class WorkflowBusinessContractTest extends BaseTest {

    @Autowired
    private IWorkflowInstanceRepository workflowInstanceRepository;

    @Nested
    @DisplayName("1. 流程實例查詢合約 (Instance Query Contract)")
    class InstanceQueryContractTests {

        @Test
        @DisplayName("WFL_I001: 查詢進行中流程實例")
        void WFL_I001_QueryRunningInstances() {
            // Given - 合約規格:
            // 輸入: {"status":"RUNNING"}
            // 必須條件: status = 'RUNNING', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "RUNNING")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I001: 應找到所有進行中的流程實例")
                    .hasSize(4)
                    .allMatch(inst -> "RUNNING".equals(inst.getStatus().name()));
        }

        @Test
        @DisplayName("WFL_I002: 查詢已完成流程實例")
        void WFL_I002_QueryCompletedInstances() {
            // Given - 合約規格:
            // 輸入: {"status":"COMPLETED"}
            // 必須條件: status = 'COMPLETED', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "COMPLETED")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I002: 應找到所有已完成的流程實例")
                    .hasSize(3)
                    .allMatch(inst -> "COMPLETED".equals(inst.getStatus().name()));
        }

        @Test
        @DisplayName("WFL_I003: 依業務類型查詢流程實例")
        void WFL_I003_QueryByBusinessType() {
            // Given - 合約規格:
            // 輸入: {"businessType":"LEAVE"}
            // 必須條件: business_type = 'LEAVE', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("business_type", "LEAVE")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I003: 應找到所有請假類型的流程實例")
                    .hasSize(5)
                    .allMatch(inst -> "LEAVE".equals(inst.getBusinessType()));
        }

        @Test
        @DisplayName("WFL_I004: 依申請人查詢流程實例")
        void WFL_I004_QueryByApplicant() {
            // Given - 合約規格:
            // 輸入: {"applicantId":"E001"}
            // 必須條件: applicant_id = 'E001', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("applicant_id", "E001")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I004: 應找到申請人 E001 的所有流程實例")
                    .hasSize(3)
                    .allMatch(inst -> "E001".equals(inst.getApplicantId()));
        }

        @Test
        @DisplayName("WFL_I005: 依部門查詢流程實例")
        void WFL_I005_QueryByDepartment() {
            // Given - 合約規格:
            // 輸入: {"departmentId":"D001"}
            // 必須條件: department_id = 'D001', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("department_id", "D001")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I005: 應找到部門 D001 的所有流程實例")
                    .hasSize(6)
                    .allMatch(inst -> "D001".equals(inst.getDepartmentId()));
        }

        @Test
        @DisplayName("WFL_I006: 查詢已取消流程實例")
        void WFL_I006_QueryCancelledInstances() {
            // Given - 合約規格:
            // 輸入: {"status":"CANCELLED"}
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "CANCELLED")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I006: 應找到所有已取消的流程實例")
                    .hasSize(2)
                    .allMatch(inst -> "CANCELLED".equals(inst.getStatus().name()));
        }

        @Test
        @DisplayName("WFL_I007: 查詢已駁回流程實例")
        void WFL_I007_QueryRejectedInstances() {
            // Given - 合約規格:
            // 輸入: {"status":"REJECTED"}
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "REJECTED")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("WFL_I007: 應找到所有已駁回的流程實例")
                    .hasSize(1)
                    .allMatch(inst -> "REJECTED".equals(inst.getStatus().name()));
        }
    }

    @Nested
    @DisplayName("2. 複合業務場景測試")
    class ComplexBusinessScenarioTests {

        @Test
        @DisplayName("主管查詢部門待審核流程")
        void manager_QueryDepartmentPendingInstances() {
            // Given - 主管查詢自己部門進行中的流程
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "RUNNING")
                    .eq("department_id", "D001")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應找到部門 D001 進行中的流程實例")
                    .hasSize(3)
                    .allMatch(inst ->
                        "RUNNING".equals(inst.getStatus().name()) &&
                        "D001".equals(inst.getDepartmentId()));
        }

        @Test
        @DisplayName("查詢特定業務類型的進行中流程")
        void queryRunningInstancesByBusinessType() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "RUNNING")
                    .eq("business_type", "LEAVE")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應找到進行中的請假流程")
                    .hasSize(2)
                    .allMatch(inst ->
                        "RUNNING".equals(inst.getStatus().name()) &&
                        "LEAVE".equals(inst.getBusinessType()));
        }

        @Test
        @DisplayName("查詢多種狀態的流程實例")
        void queryMultipleStatusInstances() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .in("status", List.of("COMPLETED", "REJECTED"))
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應找到已完成或已駁回的流程實例")
                    .hasSize(4) // 3 COMPLETED + 1 REJECTED
                    .allMatch(inst ->
                        "COMPLETED".equals(inst.getStatus().name()) ||
                        "REJECTED".equals(inst.getStatus().name()));
        }

        @Test
        @DisplayName("員工查詢自己的所有流程")
        void employee_QueryOwnInstances() {
            // Given
            String currentUserId = "E001";
            QueryGroup query = QueryBuilder.where()
                    .eq("applicant_id", currentUserId)
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("員工應能看到自己申請的所有流程")
                    .hasSize(3)
                    .allMatch(inst -> currentUserId.equals(inst.getApplicantId()));
        }

        @Test
        @DisplayName("查詢特定業務單據的流程")
        void queryByBusinessId() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("business_id", "LV-2025-001")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應找到特定業務單據的流程實例")
                    .hasSize(1)
                    .allMatch(inst -> "LV-2025-001".equals(inst.getBusinessId()));
        }
    }

    @Nested
    @DisplayName("3. 權限過濾場景測試")
    class PermissionFilterTests {

        @Test
        @DisplayName("HR 查詢所有部門的流程 - 應能看到所有")
        void hr_ShouldSeeAllDepartments() {
            // Given - HR 無部門限制
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "RUNNING")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("HR 應能看到所有部門的進行中流程")
                    .hasSize(4);
        }

        @Test
        @DisplayName("部門主管查詢 - 應只看到自己部門")
        void departmentManager_ShouldOnlySeeDepartment() {
            // Given - 部門主管只能看 D002
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "RUNNING")
                    .eq("department_id", "D002")
                    .build();

            // When
            Page<WorkflowInstance> result = workflowInstanceRepository.search(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("部門主管應只能看到自己部門的流程")
                    .hasSize(1)
                    .allMatch(inst -> "D002".equals(inst.getDepartmentId()));
        }
    }
}
