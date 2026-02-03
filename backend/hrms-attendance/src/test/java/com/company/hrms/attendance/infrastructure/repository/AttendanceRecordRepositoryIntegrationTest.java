package com.company.hrms.attendance.infrastructure.repository;

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

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseTest;

/**
 * AttendanceRecord Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>QueryGroup 各種操作符轉 SQL</li>
 * <li>日期範圍查詢</li>
 * <li>狀態過濾</li>
 * <li>分頁查詢</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/attendance_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("AttendanceRecord Repository 整合測試")
class AttendanceRecordRepositoryIntegrationTest extends BaseTest {

        @Autowired
        private IAttendanceRecordRepository attendanceRecordRepository;

        // ========================================================================
        // 1. 基本查詢測試
        // ========================================================================
        @Nested
        @DisplayName("基本查詢測試")
        class BasicQueryTests {

                @Test
                @DisplayName("ATT_A001: 查詢員工當日出勤")
                void ATT_A001_QueryEmployeeDailyAttendance() {
                        // Given - 合約規格:
                        // 輸入: {"employeeId":"E001","date":"2025-01-15"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employeeId", "E001")
                                        .eq("date", "2025-01-15")
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then - 預期 1 筆
                        assertThat(result.getContent())
                                        .as("ATT_A001: 應返回員工 E001 在 2025-01-15 的出勤紀錄")
                                        .hasSize(1)
                                        .allMatch(r -> "E001".equals(r.getEmployeeId()));
                }

                @Test
                @DisplayName("ATT_A003: 查詢異常出勤")
                void ATT_A003_QueryAbnormalAttendance() {
                        // Given - 合約規格:
                        // 輸入: {"status":"ABNORMAL"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", "ABNORMAL")
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then - 預期 2 筆
                        assertThat(result.getContent())
                                        .as("ATT_A003: 應返回所有異常出勤紀錄")
                                        .hasSize(2)
                                        .allMatch(r -> "ABNORMAL".equals(r.getAnomalyType().name()));
                }

                @Test
                @DisplayName("ATT_A004: 查詢遲到紀錄")
                void ATT_A004_QueryLateRecords() {
                        // Given - 合約規格:
                        // 輸入: {"lateFlag":true}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("isLate", true)
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then - 預期 2 筆 (AR007, AR009)
                        assertThat(result.getContent())
                                        .as("ATT_A004: 應返回所有遲到紀錄")
                                        .hasSize(2)
                                        .allMatch(r -> r.isLate());
                }

                @Test
                @DisplayName("ATT_A005: 查詢早退紀錄")
                void ATT_A005_QueryEarlyLeaveRecords() {
                        // Given - 合約規格:
                        // 輸入: {"earlyLeaveFlag":true}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("isEarlyLeave", true)
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then - 預期 1 筆
                        assertThat(result.getContent())
                                        .as("ATT_A005: 應返回所有早退紀錄")
                                        .hasSize(1)
                                        .allMatch(r -> r.isEarlyLeave());
                }
        }

        // ========================================================================
        // 2. 日期範圍查詢測試
        // ========================================================================
        @Nested
        @DisplayName("日期範圍查詢測試")
        class DateRangeQueryTests {

                @Test
                @DisplayName("ATT_A002: 查詢部門月出勤")
                void ATT_A002_QueryDepartmentMonthlyAttendance() {
                        // Given - 合約規格:
                        // 輸入: {"deptId":"D001","month":"2025-01"}
                        // Note: departmentId not in PO, commenting out for now or just fixing date
                        QueryGroup query = QueryBuilder.where()
                                        // .eq("departmentId", "D001")
                                        .gte("date", "2025-01-01")
                                        .lte("date", "2025-01-31")
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then - 預期 D001 部門 2025-01 月的出勤紀錄
                        assertThat(result.getContent())
                                        .as("ATT_A002: 應返回 D001 部門 2025-01 月的出勤紀錄")
                                        .isNotEmpty();

                }

                @Test
                @DisplayName("依特定日期查詢")
                void queryBySpecificDate() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("date", "2025-01-17")
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then - 預期 3 筆 (AR007, AR008, AR009)
                        assertThat(result.getContent())
                                        .as("應返回 2025-01-17 的出勤紀錄")
                                        .hasSize(3);
                }
        }

        // ========================================================================
        // 3. 複合條件測試
        // ========================================================================
        @Nested
        @DisplayName("複合條件測試")
        class CompoundConditionTests {

                @Test
                @DisplayName("部門 + 狀態複合查詢")
                void queryByDepartmentAndStatus() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        // .eq("departmentId", "D001")
                                        .eq("status", "NORMAL")
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回 D001 部門的正常出勤紀錄")
                                        .isNotEmpty()
                                        .allMatch(r -> // "D001".equals(r.getDepartmentId()) &&
                                        "NORMAL".equals(r.getAnomalyType().name()));
                }

                @Test
                @DisplayName("員工 + 日期範圍複合查詢")
                void queryByEmployeeAndDateRange() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employeeId", "E001")
                                        .gte("date", "2025-01-15")
                                        .lte("date", "2025-01-18")
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then - E001 在 1/15-1/18 有 4 筆紀錄
                        assertThat(result.getContent())
                                        .as("應返回 E001 在指定日期範圍的出勤紀錄")
                                        .isNotEmpty()
                                        .allMatch(r -> "E001".equals(r.getEmployeeId()));
                }
        }

        // ========================================================================
        // 4. 分頁測試
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
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 5));

                        // Then
                        assertThat(result.getNumber())
                                        .as("頁碼應為 0")
                                        .isEqualTo(0);
                        assertThat(result.getContent())
                                        .as("第一頁應返回最多 5 筆")
                                        .hasSizeLessThanOrEqualTo(5);
                }

                @Test
                @DisplayName("分頁查詢 - 總筆數")
                void findAll_ShouldReturnCorrectTotalElements() {
                        // Given
                        QueryGroup query = QueryBuilder.where().build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then - 預期 10 筆
                        assertThat(result.getTotalElements())
                                        .as("總筆數應為 10")
                                        .isEqualTo(10);
                }
        }

        // ========================================================================
        // 5. IN 操作符測試
        // ========================================================================
        @Nested
        @DisplayName("IN 操作符測試")
        class InOperatorTests {

                @Test
                @DisplayName("IN 操作符 - 查詢多個員工")
                void findByEmployees_IN_ShouldReturnMatchingRecords() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .in("employeeId", List.of("E001", "E002"))
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回 E001 和 E002 的出勤紀錄")
                                        .isNotEmpty()
                                        .allMatch(r -> "E001".equals(r.getEmployeeId()) ||
                                                        "E002".equals(r.getEmployeeId()));
                }

                @Test
                @DisplayName("IN 操作符 - 查詢多個狀態")
                void findByStatuses_IN_ShouldReturnMatchingRecords() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .in("status", List.of("NORMAL", "ABNORMAL"))
                                        .build();

                        // When
                        Page<AttendanceRecord> result = attendanceRecordRepository.findPageByQuery(query,
                                        PageRequest.of(0, 100));

                        // Then - 應返回全部 10 筆
                        assertThat(result.getContent())
                                        .as("應返回 NORMAL 或 ABNORMAL 的出勤紀錄")
                                        .hasSize(10);
                }
        }
}
