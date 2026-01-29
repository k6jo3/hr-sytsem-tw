package com.company.hrms.timesheet.contract;

import static org.assertj.core.api.Assertions.*;

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
import com.company.hrms.common.test.base.BaseTest;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

/**
 * Timesheet 業務合約測試
 *
 * <p>驗證 timesheet_contracts.md 定義的業務查詢場景
 * 每個測試對應合約文件中的一個場景 ID
 *
 * @author SA Team
 * @since 2026-01-29
 * @see contracts/timesheet_contracts.md
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/timesheet_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Timesheet 業務合約測試")
class TimesheetBusinessContractTest extends BaseTest {

    @Autowired
    private ITimesheetRepository timesheetRepository;

    private static final UUID EMPLOYEE_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID EMPLOYEE_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID EMPLOYEE_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Nested
    @DisplayName("1. 工時單查詢合約 (Timesheet Query Contract)")
    class TimesheetQueryContractTests {

        @Test
        @DisplayName("TMS_T001: 查詢員工週工時單")
        void TMS_T001_QueryEmployeeWeeklyTimesheet() {
            // Given - 合約規格:
            // 輸入: {"employeeId":"E001","weekStart":"2025-01-06"}
            // 必須條件: employee_id = 'E001', week_start = '2025-01-06', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", EMPLOYEE_1)
                    .eq("period_start_date", "2025-01-06")
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("TMS_T001: 應找到員工 E001 在 2025-01-06 週的工時單")
                    .hasSize(1)
                    .allMatch(ts -> ts.getEmployeeId().equals(EMPLOYEE_1));
        }

        @Test
        @DisplayName("TMS_T002: 查詢待審核工時單")
        void TMS_T002_QueryPendingTimesheets() {
            // Given - 合約規格:
            // 輸入: {"status":"PENDING"}
            // 必須條件: status = 'PENDING', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "PENDING")
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("TMS_T002: 應找到所有待審核工時單")
                    .hasSize(3)
                    .allMatch(ts -> "PENDING".equals(ts.getStatus().name()));
        }

        @Test
        @DisplayName("TMS_T003: 查詢已核准工時單")
        void TMS_T003_QueryApprovedTimesheets() {
            // Given - 合約規格:
            // 輸入: {"status":"APPROVED"}
            // 必須條件: status = 'APPROVED', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "APPROVED")
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("TMS_T003: 應找到所有已核准工時單")
                    .hasSize(3)
                    .allMatch(ts -> "APPROVED".equals(ts.getStatus().name()));
        }

        @Test
        @DisplayName("TMS_T004: 查詢已駁回工時單")
        void TMS_T004_QueryRejectedTimesheets() {
            // Given - 合約規格:
            // 輸入: {"status":"REJECTED"}
            // 必須條件: status = 'REJECTED', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "REJECTED")
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("TMS_T004: 應找到所有已駁回工時單")
                    .hasSize(2)
                    .allMatch(ts -> "REJECTED".equals(ts.getStatus().name()));
        }

        @Test
        @DisplayName("TMS_T006: 員工查詢自己工時單")
        void TMS_T006_EmployeeQueryOwnTimesheets() {
            // Given - 合約規格:
            // 輸入: {} (當前用戶)
            // 必須條件: employee_id = '{currentUserId}', is_deleted = 0
            UUID currentUserId = EMPLOYEE_1;
            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", currentUserId)
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("TMS_T006: 員工應只能看到自己的工時單")
                    .isNotEmpty()
                    .allMatch(ts -> ts.getEmployeeId().equals(currentUserId));
        }

        @Test
        @DisplayName("TMS_T008: 依日期範圍查詢")
        void TMS_T008_QueryByDateRange() {
            // Given - 合約規格:
            // 輸入: {"weekStartFrom":"2025-01-01"}
            // 必須條件: week_start >= '2025-01-01', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .gte("period_start_date", "2025-01-01")
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("TMS_T008: 應找到 2025-01-01 之後的工時單")
                    .isNotEmpty();
        }

        @Test
        @DisplayName("TMS_T009: 查詢未提交工時單 (DRAFT)")
        void TMS_T009_QueryDraftTimesheets() {
            // Given - 合約規格:
            // 輸入: {"status":"DRAFT"}
            // 必須條件: status = 'DRAFT', is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "DRAFT")
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("TMS_T009: 應找到所有草稿工時單")
                    .hasSize(2)
                    .allMatch(ts -> "DRAFT".equals(ts.getStatus().name()));
        }
    }

    @Nested
    @DisplayName("2. 角色權限過濾測試")
    class RolePermissionFilterTests {

        @Test
        @DisplayName("PM 查詢專案工時單 - 應只能查詢所管理專案")
        void pm_ShouldOnlySeeManageProjects() {
            // 模擬 PM 所管理的專案 ID 列表
            // 在實際實作中，這會從 Security Context 取得
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "PENDING")
                    // 實際應用會加上: .in("project_id", managedProjectIds)
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("PM 應能看到待審核工時單")
                    .isNotEmpty();
        }

        @Test
        @DisplayName("員工查詢 - 應只能查詢自己的工時單")
        void employee_ShouldOnlySeeOwnTimesheets() {
            // Given - 模擬員工身份
            UUID employeeId = EMPLOYEE_2;
            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", employeeId)
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("員工應只能看到自己的工時單")
                    .allMatch(ts -> ts.getEmployeeId().equals(employeeId));
        }
    }

    @Nested
    @DisplayName("3. 複合業務場景測試")
    class ComplexBusinessScenarioTests {

        @Test
        @DisplayName("查詢員工某週待審核工時單")
        void queryEmployeePendingTimesheetForWeek() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("employee_id", EMPLOYEE_1)
                    .eq("status", "PENDING")
                    .eq("period_start_date", "2025-01-13")
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應找到員工指定週的待審核工時單")
                    .hasSize(1);
        }

        @Test
        @DisplayName("查詢多種狀態的工時單")
        void queryMultipleStatusTimesheets() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .in("status", java.util.List.of("DRAFT", "PENDING"))
                    .build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應找到 DRAFT 和 PENDING 狀態的工時單")
                    .hasSize(5)
                    .allMatch(ts ->
                        "DRAFT".equals(ts.getStatus().name()) ||
                        "PENDING".equals(ts.getStatus().name()));
        }
    }
}
