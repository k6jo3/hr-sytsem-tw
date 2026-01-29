package com.company.hrms.timesheet.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
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
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

/**
 * Timesheet Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>QueryGroup 各種操作符轉 SQL</li>
 * <li>狀態查詢 (DRAFT/SUBMITTED/APPROVED/REJECTED)</li>
 * <li>員工過濾</li>
 * <li>日期範圍查詢</li>
 * <li>工時統計查詢</li>
 * <li>分頁查詢</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/timesheet_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Timesheet Repository 整合測試")
class TimesheetRepositoryIntegrationTest extends BaseTest {

        @Autowired
        private ITimesheetRepository timesheetRepository;

        // 測試用固定 UUID (對應測試資料)
        private static final UUID EMPLOYEE_E001 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        private static final UUID EMPLOYEE_E002 = UUID.fromString("22222222-2222-2222-2222-222222222222");

        // ========================================================================
        // 1. 狀態查詢測試
        // ========================================================================
        @Nested
        @DisplayName("狀態查詢測試")
        class StatusQueryTests {

                @Test
                @DisplayName("TMS_T002: 查詢待審核工時單 (SUBMITTED)")
                void TMS_T002_QuerySubmittedTimesheets() {
                        // Given - 合約規格:
                        // 輸入: {"status":"PENDING"} → Domain 使用 SUBMITTED
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", TimesheetStatus.SUBMITTED)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 3 筆
                        assertThat(result.getContent())
                                        .as("TMS_T002: 應返回所有待審核的工時單")
                                        .hasSize(3)
                                        .allMatch(ts -> ts.getStatus() == TimesheetStatus.SUBMITTED);
                }

                @Test
                @DisplayName("TMS_T003: 查詢已核准工時單")
                void TMS_T003_QueryApprovedTimesheets() {
                        // Given - 合約規格:
                        // 輸入: {"status":"APPROVED"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", TimesheetStatus.APPROVED)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 3 筆
                        assertThat(result.getContent())
                                        .as("TMS_T003: 應返回所有已核准的工時單")
                                        .hasSize(3)
                                        .allMatch(ts -> ts.getStatus() == TimesheetStatus.APPROVED);
                }

                @Test
                @DisplayName("TMS_T004: 查詢已駁回工時單")
                void TMS_T004_QueryRejectedTimesheets() {
                        // Given - 合約規格:
                        // 輸入: {"status":"REJECTED"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", TimesheetStatus.REJECTED)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 2 筆
                        assertThat(result.getContent())
                                        .as("TMS_T004: 應返回所有已駁回的工時單")
                                        .hasSize(2)
                                        .allMatch(ts -> ts.getStatus() == TimesheetStatus.REJECTED);
                }

                @Test
                @DisplayName("TMS_T009: 查詢未提交工時單 (DRAFT)")
                void TMS_T009_QueryDraftTimesheets() {
                        // Given - 合約規格:
                        // 輸入: {"status":"DRAFT"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", TimesheetStatus.DRAFT)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 2 筆
                        assertThat(result.getContent())
                                        .as("TMS_T009: 應返回所有草稿狀態的工時單")
                                        .hasSize(2)
                                        .allMatch(ts -> ts.getStatus() == TimesheetStatus.DRAFT);
                }
        }

        // ========================================================================
        // 2. 員工查詢測試
        // ========================================================================
        @Nested
        @DisplayName("員工查詢測試")
        class EmployeeQueryTests {

                @Test
                @DisplayName("TMS_T001: 查詢員工週工時單")
                void TMS_T001_QueryEmployeeWeeklyTimesheet() {
                        // Given - 合約規格:
                        // 輸入: {"employeeId":"E001","weekStart":"2025-01-06"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employeeId", EMPLOYEE_E001)
                                        .eq("periodStartDate", java.time.LocalDate.parse("2025-01-06"))
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 1 筆
                        assertThat(result.getContent())
                                        .as("TMS_T001: 應返回員工 E001 在 2025-01-06 週的工時單")
                                        .hasSize(1)
                                        .allMatch(ts -> ts.getEmployeeId().equals(EMPLOYEE_E001));
                }

                @Test
                @DisplayName("TMS_T006: 員工查詢自己全部工時單")
                void TMS_T006_QueryEmployeeAllTimesheets() {
                        // Given - 合約規格:
                        // 輸入: {"employeeId":"E001"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employeeId", EMPLOYEE_E001)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - E001 有 3 筆 (TS-001, TS-003, TS-006)
                        assertThat(result.getContent())
                                        .as("TMS_T006: 應返回員工 E001 的所有工時單")
                                        .hasSize(3)
                                        .allMatch(ts -> ts.getEmployeeId().equals(EMPLOYEE_E001));
                }

                @Test
                @DisplayName("查詢員工 E002 的工時單")
                void queryEmployeeE002Timesheets() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employeeId", EMPLOYEE_E002)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - E002 有 2 筆 (TS-002, TS-007)
                        assertThat(result.getContent())
                                        .as("應返回員工 E002 的所有工時單")
                                        .hasSize(2)
                                        .allMatch(ts -> ts.getEmployeeId().equals(EMPLOYEE_E002));
                }
        }

        // ========================================================================
        // 3. 日期範圍查詢測試
        // ========================================================================
        @Nested
        @DisplayName("日期範圍查詢測試")
        class DateRangeQueryTests {

                @Test
                @DisplayName("TMS_T008: 依日期範圍查詢 - 2025年1月開始")
                void TMS_T008_QueryByDateRangeFrom() {
                        // Given - 合約規格:
                        // 輸入: {"weekStartFrom":"2025-01-01"}
                        QueryGroup query = QueryBuilder.where()
                                        .gte("periodStartDate", java.time.LocalDate.parse("2025-01-01"))
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 從 2025-01-01 之後的週
                        assertThat(result.getContent())
                                        .as("TMS_T008: 應返回 2025-01-01 之後的工時單")
                                        .isNotEmpty();
                }

                @Test
                @DisplayName("依特定週查詢")
                void queryBySpecificWeek() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("periodStartDate", java.time.LocalDate.parse("2025-01-06"))
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 2025-01-06 週有多筆
                        assertThat(result.getContent())
                                        .as("應返回 2025-01-06 週的所有工時單")
                                        .hasSizeGreaterThanOrEqualTo(4);
                }

                @Test
                @DisplayName("依日期範圍查詢 - 完整範圍")
                void queryByFullDateRange() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .gte("periodStartDate", java.time.LocalDate.parse("2025-01-01"))
                                        .lte("periodStartDate", java.time.LocalDate.parse("2025-01-15"))
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回 2025-01-01 至 2025-01-15 的工時單")
                                        .isNotEmpty();
                }
        }

        // ========================================================================
        // 4. 工時統計查詢測試
        // ========================================================================
        @Nested
        @DisplayName("工時統計查詢測試")
        class HoursQueryTests {

                @Test
                @DisplayName("查詢工時 >= 40 小時的工時單")
                void queryByTotalHoursGte40() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .gte("totalHours", new BigDecimal("40"))
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 6 筆
                        assertThat(result.getContent())
                                        .as("應返回總工時 >= 40 的工時單")
                                        .hasSize(6)
                                        .allMatch(ts -> ts.getTotalHours().compareTo(new BigDecimal("40")) >= 0);
                }

                @Test
                @DisplayName("查詢工時 < 40 小時的工時單")
                void queryByTotalHoursLt40() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .lt("totalHours", new BigDecimal("40"))
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 4 筆
                        assertThat(result.getContent())
                                        .as("應返回總工時 < 40 的工時單")
                                        .hasSize(4)
                                        .allMatch(ts -> ts.getTotalHours().compareTo(new BigDecimal("40")) < 0);
                }
        }

        // ========================================================================
        // 5. 複合條件測試
        // ========================================================================
        @Nested
        @DisplayName("複合條件測試")
        class CompoundConditionTests {

                @Test
                @DisplayName("員工 + 狀態複合查詢")
                void queryByEmployeeAndStatus() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employeeId", EMPLOYEE_E001)
                                        .eq("status", TimesheetStatus.APPROVED)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - E001 已核准的工時單
                        assertThat(result.getContent())
                                        .as("應返回員工 E001 已核准的工時單")
                                        .hasSize(1)
                                        .allMatch(ts -> ts.getEmployeeId().equals(EMPLOYEE_E001) &&
                                                        ts.getStatus() == TimesheetStatus.APPROVED);
                }

                @Test
                @DisplayName("日期範圍 + 狀態複合查詢")
                void queryByDateRangeAndStatus() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .gte("periodStartDate", java.time.LocalDate.parse("2025-01-06"))
                                        .eq("status", TimesheetStatus.SUBMITTED)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回 2025-01-06 之後待審核的工時單")
                                        .isNotEmpty()
                                        .allMatch(ts -> ts.getStatus() == TimesheetStatus.SUBMITTED);
                }
        }

        // ========================================================================
        // 6. 分頁測試
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
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 5));

                        // Then
                        assertThat(result.getNumber())
                                        .as("頁碼應為 0")
                                        .isEqualTo(0);
                        assertThat(result.getContent())
                                        .as("第一頁應返回最多 5 筆")
                                        .hasSizeLessThanOrEqualTo(5);
                }

                @Test
                @DisplayName("分頁查詢 - 第二頁")
                void findAll_Page1_ShouldReturnSecondPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where().build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(1, 5));

                        // Then
                        assertThat(result.getNumber())
                                        .as("頁碼應為 1")
                                        .isEqualTo(1);
                        assertThat(result.getContent())
                                        .as("第二頁應有資料")
                                        .isNotEmpty();
                }

                @Test
                @DisplayName("分頁查詢 - 總筆數")
                void findAll_ShouldReturnCorrectTotalElements() {
                        // Given
                        QueryGroup query = QueryBuilder.where().build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 10 筆
                        assertThat(result.getTotalElements())
                                        .as("總筆數應為 10")
                                        .isEqualTo(10);
                }
        }

        // ========================================================================
        // 7. IN 操作符測試
        // ========================================================================
        @Nested
        @DisplayName("IN 操作符測試")
        class InOperatorTests {

                @Test
                @DisplayName("IN 操作符 - 查詢多個狀態")
                void findByStatuses_IN_ShouldReturnMatchingRecords() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .in("status", (Object[]) new TimesheetStatus[] { TimesheetStatus.SUBMITTED,
                                                        TimesheetStatus.REJECTED })
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 3 + 2 = 5 筆
                        assertThat(result.getContent())
                                        .as("應返回 SUBMITTED 或 REJECTED 的工時單")
                                        .hasSize(5)
                                        .allMatch(ts -> ts.getStatus() == TimesheetStatus.SUBMITTED ||
                                                        ts.getStatus() == TimesheetStatus.REJECTED);
                }

                @Test
                @DisplayName("IN 操作符 - 查詢多個員工")
                void findByEmployees_IN_ShouldReturnMatchingRecords() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .in("employeeId", (Object[]) new UUID[] { EMPLOYEE_E001, EMPLOYEE_E002 })
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - E001=3 + E002=2 = 5 筆
                        assertThat(result.getContent())
                                        .as("應返回 E001 或 E002 的工時單")
                                        .hasSize(5)
                                        .allMatch(ts -> ts.getEmployeeId().equals(EMPLOYEE_E001) ||
                                                        ts.getEmployeeId().equals(EMPLOYEE_E002));
                }
        }

        // ========================================================================
        // 8. 鎖定狀態測試
        // ========================================================================
        @Nested
        @DisplayName("鎖定狀態測試")
        class LockStatusTests {

                @Test
                @DisplayName("查詢已鎖定工時單")
                void queryLockedTimesheets() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("isLocked", true)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 已核准的工時單會被鎖定
                        assertThat(result.getContent())
                                        .as("應返回所有已鎖定的工時單")
                                        .isNotEmpty()
                                        .allMatch(Timesheet::isLocked);
                }

                @Test
                @DisplayName("查詢未鎖定工時單")
                void queryUnlockedTimesheets() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("isLocked", false)
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回所有未鎖定的工時單")
                                        .isNotEmpty()
                                        .noneMatch(Timesheet::isLocked);
                }
        }

        // ========================================================================
        // 9. 駁回原因查詢測試
        // ========================================================================
        @Nested
        @DisplayName("駁回原因查詢測試")
        class RejectionReasonQueryTests {

                @Test
                @DisplayName("依駁回原因模糊查詢")
                void queryByRejectionReason_Like() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .like("rejectionReason", "加班")
                                        .build();

                        // When
                        Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 1 筆
                        assertThat(result.getContent())
                                        .as("應返回駁回原因包含'加班'的工時單")
                                        .hasSize(1);
                }
        }
}
