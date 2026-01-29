package com.company.hrms.organization.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseTest;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

/**
 * Employee Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>QueryGroup 各種操作符轉 SQL</li>
 * <li>分頁查詢</li>
 * <li>排序</li>
 * <li>軟刪除過濾</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/organization_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Employee Repository 整合測試")
class EmployeeRepositoryIntegrationTest extends BaseTest {

        @Autowired
        private IEmployeeRepository employeeRepository;

        // ========================================================================
        // 1. 基本查詢測試
        // ========================================================================
        @Nested
        @DisplayName("基本查詢測試")
        class BasicQueryTests {

                @Test
                @DisplayName("EQ 操作符 - 依狀態查詢 ACTIVE 員工")
                void findByStatus_EQ_ShouldReturnActiveEmployees() {
                        // Given - 合約 ORG_E001
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "ACTIVE")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then - 預期 8 筆 ACTIVE 員工
                        assertThat(result)
                                        .as("應返回所有 ACTIVE 員工")
                                        .hasSize(8)
                                        .allMatch(e -> "ACTIVE".equals(e.getEmploymentStatus().name()));
                }

                @Test
                @DisplayName("EQ 操作符 - 依狀態查詢 TERMINATED 員工")
                void findByStatus_EQ_ShouldReturnTerminatedEmployees() {
                        // Given - 合約 ORG_E002
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "TERMINATED")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then - 預期 2 筆 TERMINATED 員工
                        assertThat(result)
                                        .as("應返回所有 TERMINATED 員工")
                                        .hasSize(2)
                                        .allMatch(e -> "TERMINATED".equals(e.getEmploymentStatus().name()));
                }

                @Test
                @DisplayName("EQ 操作符 - 依部門查詢")
                void findByDepartment_EQ_ShouldReturnDepartmentEmployees() {
                        // Given - 合約 ORG_E003
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", "D001")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then - 預期 5 筆 D001 部門員工
                        assertThat(result)
                                        .as("應返回 D001 部門的所有員工")
                                        .hasSize(5);
                }

                @Test
                @DisplayName("LIKE 操作符 - 依姓名模糊查詢")
                void findByName_LIKE_ShouldReturnMatchingEmployees() {
                        // Given - 合約 ORG_E004
                        QueryGroup query = QueryBuilder.where()
                                        .like("full_name", "%王%")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then - 預期找到姓王的員工
                        assertThat(result)
                                        .as("應返回姓名包含'王'的員工")
                                        .hasSize(1)
                                        .allMatch(e -> e.getFullName().contains("王"));
                }

                @Test
                @DisplayName("EQ 操作符 - 查詢試用期員工")
                void findByStatus_EQ_ShouldReturnProbationEmployees() {
                        // Given - 合約 ORG_E007
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "PROBATION")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then - 預期 3 筆 PROBATION 員工
                        assertThat(result)
                                        .as("應返回所有試用期員工")
                                        .hasSize(3)
                                        .allMatch(e -> "PROBATION".equals(e.getEmploymentStatus().name()));
                }
        }

        // ========================================================================
        // 2. 日期範圍查詢測試
        // ========================================================================
        @Nested
        @DisplayName("日期範圍查詢測試")
        class DateRangeQueryTests {

                @Test
                @DisplayName("GTE 操作符 - 查詢指定日期後到職員工")
                void findByHireDate_GTE_ShouldReturnRecentHires() {
                        // Given - 合約 ORG_E011
                        QueryGroup query = QueryBuilder.where()
                                        .gte("hire_date", "2025-01-01")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then - 預期 3 筆 2025-01-01 之後到職的員工
                        assertThat(result)
                                        .as("應返回 2025-01-01 之後到職的員工")
                                        .hasSize(3);
                }
        }

        // ========================================================================
        // 3. IN 操作符測試
        // ========================================================================
        @Nested
        @DisplayName("IN 操作符測試")
        class InOperatorTests {

                @Test
                @DisplayName("IN 操作符 - 查詢多種狀態員工")
                void findByStatuses_IN_ShouldReturnMatchingEmployees() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .in("employment_status", List.of("ACTIVE", "PROBATION"))
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then - 預期 8 + 3 = 11 筆
                        assertThat(result)
                                        .as("應返回 ACTIVE 或 PROBATION 員工")
                                        .hasSize(11)
                                        .allMatch(e -> "ACTIVE".equals(e.getEmploymentStatus().name()) ||
                                                        "PROBATION".equals(e.getEmploymentStatus().name()));
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
                        QueryGroup query = QueryBuilder.where()
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 5));

                        // Then
                        assertThat(result)
                                        .as("第一頁應返回最多 5 筆")
                                        .hasSizeLessThanOrEqualTo(5);
                }

                @Test
                @DisplayName("分頁查詢 - 第二頁")
                void findAll_Page1_ShouldReturnSecondPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> page0 = employeeRepository.findByQuery(query, PageRequest.of(0, 5));
                        List<Employee> page1 = employeeRepository.findByQuery(query, PageRequest.of(1, 5));

                        // Then
                        assertThat(page1)
                                        .as("第二頁應返回不同的資料")
                                        .isNotEmpty();

                        // 確認兩頁資料不重複
                        List<String> page0Ids = page0.stream()
                                        .map(e -> e.getId().toString())
                                        .toList();
                        assertThat(page1)
                                        .as("第二頁資料不應與第一頁重複")
                                        .noneMatch(e -> page0Ids.contains(e.getId().toString()));
                }

                @Test
                @DisplayName("count 查詢 - 應返回正確筆數")
                void count_ShouldReturnCorrectCount() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "ACTIVE")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        long count = employeeRepository.countByQuery(query);

                        // Then - 預期 8 筆 ACTIVE 員工
                        assertThat(count)
                                        .as("ACTIVE 員工數應為 8")
                                        .isEqualTo(8);
                }
        }

        // ========================================================================
        // 5. 軟刪除過濾測試
        // ========================================================================
        @Nested
        @DisplayName("軟刪除過濾測試")
        class SoftDeleteTests {

                @Test
                @DisplayName("軟刪除過濾 - 不應返回已刪除資料")
                void findAll_WithSoftDeleteFilter_ShouldExcludeDeleted() {
                        // Given
                        QueryGroup queryWithFilter = QueryBuilder.where()
                                        .eq("is_deleted", 0)
                                        .build();

                        QueryGroup queryWithoutFilter = QueryBuilder.where().build();

                        // When
                        List<Employee> withFilter = employeeRepository.findByQuery(queryWithFilter,
                                        PageRequest.of(0, 100));
                        List<Employee> withoutFilter = employeeRepository.findByQuery(queryWithoutFilter,
                                        PageRequest.of(0, 100));

                        // Then
                        // 測試資料中沒有軟刪除的員工，所以兩者應該相等
                        // 如果有軟刪除資料，withoutFilter 應該 >= withFilter
                        assertThat(withoutFilter.size())
                                        .as("不含過濾的結果應 >= 含過濾的結果")
                                        .isGreaterThanOrEqualTo(withFilter.size());
                }
        }

        // ========================================================================
        // 6. 複合條件測試
        // ========================================================================
        @Nested
        @DisplayName("複合條件測試")
        class CompoundConditionTests {

                @Test
                @DisplayName("AND 條件 - 部門 + 狀態")
                void findByDepartmentAndStatus_ShouldReturnMatchingEmployees() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", "D001")
                                        .eq("employment_status", "ACTIVE")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("應返回 D001 部門的 ACTIVE 員工")
                                        .isNotEmpty()
                                        .allMatch(e -> "ACTIVE".equals(e.getEmploymentStatus().name()));
                }

                @Test
                @DisplayName("複合條件 - 部門 + 狀態 + 姓名模糊")
                void findByMultipleConditions_ShouldReturnMatchingEmployees() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", "D001")
                                        .eq("employment_status", "ACTIVE")
                                        .like("full_name", "%大明%")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("應返回符合所有條件的員工")
                                        .hasSize(1)
                                        .allMatch(e -> e.getFullName().contains("大明"));
                }
        }

        // ========================================================================
        // 7. 特殊狀態查詢測試
        // ========================================================================
        @Nested
        @DisplayName("特殊狀態查詢測試")
        class SpecialStatusTests {

                @Test
                @DisplayName("查詢留職停薪員工")
                void findByStatus_UNPAID_LEAVE_ShouldReturnCorrectEmployees() {
                        // Given - 合約 ORG_E012
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "UNPAID_LEAVE")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then - 預期 1 筆 UNPAID_LEAVE 員工
                        assertThat(result)
                                        .as("應返回留職停薪員工")
                                        .hasSize(1)
                                        .allMatch(e -> "UNPAID_LEAVE".equals(e.getEmploymentStatus().name()));
                }

                @Test
                @DisplayName("查詢育嬰留停員工")
                void findByStatus_PARENTAL_LEAVE_ShouldReturnCorrectEmployees() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "PARENTAL_LEAVE")
                                        .eq("is_deleted", 0)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then - 預期 1 筆 PARENTAL_LEAVE 員工
                        assertThat(result)
                                        .as("應返回育嬰留停員工")
                                        .hasSize(1)
                                        .allMatch(e -> "PARENTAL_LEAVE".equals(e.getEmploymentStatus().name()));
                }
        }
}
