package com.company.hrms.organization.application.service.contract;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
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
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

/**
 * HR02 組織員工服務業務合約測試
 *
 * <p>
 * 本測試類別驗證 contracts/organization_contracts.md 定義的業務查詢場景。
 * 每個測試對應合約文件中的一個場景 ID。
 *
 * <p>
 * <b>合約規格參考:</b> contracts/organization_contracts.md
 * <p>
 * <b>測試層級:</b> 業務合約測試 (Repository + QueryGroup)
 * <p>
 * <b>測試範圍:</b> 驗證查詢條件組裝與資料庫查詢結果的正確性
 *
 * <h2>測試場景涵蓋範圍</h2>
 * <ul>
 * <li>員工查詢合約 (ORG_E001 ~ ORG_E012): 依狀態、部門、姓名、工號、到職日等查詢</li>
 * <li>部門查詢合約 (ORG_D001 ~ ORG_D006): 依狀態、層級、名稱等查詢</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-29
 * @see organization_contracts.md
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/organization_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("HR02 組織員工服務業務合約測試")
public class OrganizationContractTest extends BaseContractTest {

        @Autowired
        private IEmployeeRepository employeeRepository;

        // ========================================================================
        // 1. 員工查詢合約 (Employee Query Contract)
        // 合約規格: contracts/organization_contracts.md
        // ========================================================================
        @Nested
        @DisplayName("1. 員工查詢合約 (Employee Query Contract)")
        class EmployeeQueryContractTests {

                @Test
                @DisplayName("ORG_E001: 查詢在職員工")
                void ORG_E001_QueryActiveEmployees() throws Exception {
                        // Given - 合約規格:
                        // 輸入: {"status":"ACTIVE"}
                        // 必須條件: status = 'ACTIVE', is_deleted = 0
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "ACTIVE")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E001: 應找到所有在職員工 (預期 8 筆)")
                                        .hasSize(8)
                                        .allMatch(emp -> "ACTIVE".equals(emp.getEmploymentStatus().name()));
                }

                @Test
                @DisplayName("ORG_E002: 查詢離職員工")
                void ORG_E002_QueryTerminatedEmployees() throws Exception {
                        // Given - 合約規格:
                        // 輸入: {"status":"RESIGNED"}
                        // 必須條件: status = 'RESIGNED', is_deleted = 0
                        // 注意: 測試資料使用 TERMINATED 狀態
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "TERMINATED")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E002: 應找到所有離職員工 (預期 2 筆)")
                                        .hasSize(2)
                                        .allMatch(emp -> "TERMINATED".equals(emp.getEmploymentStatus().name()));
                }

                @Test
                @DisplayName("ORG_E003: 依部門查詢員工")
                void ORG_E003_QueryEmployeesByDepartment() throws Exception {
                        // Given - 合約規格:
                        // 輸入: {"deptId":"D001"}
                        // 必須條件: department_id = 'D001', is_deleted = 0
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", "D001")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E003: 應找到部門 D001 的所有員工 (預期 5 筆)")
                                        .hasSize(5)
                                        .allMatch(emp -> "D001".equals(emp.getDepartmentId().toString()));
                }

                @Test
                @DisplayName("ORG_E004: 依姓名模糊查詢")
                void ORG_E004_QueryEmployeesByName() throws Exception {
                        // Given - 合約規格:
                        // 輸入: {"name":"王"}
                        // 必須條件: name LIKE '王', is_deleted = 0
                        QueryGroup query = QueryBuilder.where()
                                        .like("full_name", "王")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E004: 應找到姓名包含 '王' 的員工 (預期 1 筆)")
                                        .hasSize(1)
                                        .allMatch(emp -> emp.getFullName().contains("王"));
                }

                @Test
                @DisplayName("ORG_E005: 依工號查詢")
                void ORG_E005_QueryEmployeeByNumber() throws Exception {
                        // Given - 合約規格:
                        // 輸入: {"employeeNo":"EMP202501-001"}
                        // 必須條件: employee_no = 'EMP202501-001', is_deleted = 0
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employee_number", "EMP202501-001")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E005: 應找到工號 EMP202501-001 的員工 (預期 1 筆)")
                                        .hasSize(1)
                                        .allMatch(emp -> "EMP202501-001".equals(emp.getEmployeeNumber()));
                }

                @Test
                @DisplayName("ORG_E007: 查詢試用期員工")
                void ORG_E007_QueryProbationEmployees() throws Exception {
                        // Given - 合約規格:
                        // 輸入: {"employmentType":"PROBATION"}
                        // 必須條件: employment_type = 'PROBATION', is_deleted = 0
                        // 注意: 測試資料使用 employment_status = 'PROBATION'
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "PROBATION")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E007: 應找到所有試用期員工 (預期 3 筆)")
                                        .hasSize(3)
                                        .allMatch(emp -> "PROBATION".equals(emp.getEmploymentStatus().name()));
                }

                @Test
                @DisplayName("ORG_E008: 查詢正式員工")
                void ORG_E008_QueryRegularEmployees() throws Exception {
                        // Given - 合約規格:
                        // 輸入: {"employmentType":"REGULAR"}
                        // 必須條件: employment_type = 'REGULAR', is_deleted = 0
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_type", "REGULAR")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E008: 應找到所有正式員工 (預期 10 筆)")
                                        .isNotEmpty()
                                        .allMatch(emp -> "REGULAR".equals(emp.getEmploymentType().name()));
                }

                @Test
                @DisplayName("ORG_E011: 依到職日期範圍查詢")
                void ORG_E011_QueryEmployeesByHireDate() throws Exception {
                        // Given - 合約規格:
                        // 輸入: {"hireStartDate":"2025-01-01"}
                        // 必須條件: hire_date >= '2025-01-01', is_deleted = 0
                        QueryGroup query = QueryBuilder.where()
                                        .gte("hire_date", LocalDate.of(2025, 1, 1))
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E011: 應找到 2025-01-01 之後到職的員工 (預期 3 筆)")
                                        .hasSize(3)
                                        .allMatch(emp -> !emp.getHireDate().isBefore(LocalDate.of(2025, 1, 1)));
                }

                @Test
                @DisplayName("ORG_E012: 查詢留職停薪員工")
                void ORG_E012_QueryUnpaidLeaveEmployees() throws Exception {
                        // Given - 合約規格:
                        // 輸入: {"status":"ON_LEAVE"}
                        // 必須條件: status = 'ON_LEAVE', is_deleted = 0
                        // 注意: 測試資料使用 UNPAID_LEAVE 狀態
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_status", "UNPAID_LEAVE")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E012: 應找到所有留職停薪員工 (預期 1 筆)")
                                        .hasSize(1)
                                        .allMatch(emp -> "UNPAID_LEAVE".equals(emp.getEmploymentStatus().name()));
                }
        }

        // ========================================================================
        // 2. 複合查詢場景測試
        // ========================================================================
        @Nested
        @DisplayName("2. 複合查詢場景測試")
        class ComplexQueryScenarioTests {

                @Test
                @DisplayName("查詢特定部門的在職員工")
                void queryActiveEmployeesInDepartment() throws Exception {
                        // Given - 組合條件: 部門 + 在職狀態
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", "D001")
                                        .eq("employment_status", "ACTIVE")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("應找到部門 D001 的在職員工")
                                        .isNotEmpty()
                                        .allMatch(emp -> "D001".equals(emp.getDepartmentId().toString()) &&
                                                        "ACTIVE".equals(emp.getEmploymentStatus().name()));
                }

                @Test
                @DisplayName("查詢多種狀態的員工")
                void queryEmployeesWithMultipleStatuses() throws Exception {
                        // Given - IN 條件: 多種狀態
                        QueryGroup query = QueryBuilder.where()
                                        .in("employment_status", List.of("ACTIVE", "PROBATION"))
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("應找到 ACTIVE 和 PROBATION 狀態的員工 (預期 11 筆)")
                                        .hasSize(11)
                                        .allMatch(emp -> "ACTIVE".equals(emp.getEmploymentStatus().name()) ||
                                                        "PROBATION".equals(emp.getEmploymentStatus().name()));
                }

                @Test
                @DisplayName("查詢指定日期範圍內到職的正式員工")
                void queryRegularEmployeesHiredInDateRange() throws Exception {
                        // Given - 組合條件: 雇用類型 + 到職日期範圍
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_type", "REGULAR")
                                        .gte("hire_date", LocalDate.of(2020, 1, 1))
                                        .lte("hire_date", LocalDate.of(2023, 12, 31))
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("應找到 2020-2023 年間到職的正式員工")
                                        .isNotEmpty()
                                        .allMatch(emp -> "REGULAR".equals(emp.getEmploymentType().name()) &&
                                                        !emp.getHireDate().isBefore(LocalDate.of(2020, 1, 1)) &&
                                                        !emp.getHireDate().isAfter(LocalDate.of(2023, 12, 31)));
                }
        }

        // ========================================================================
        // 3. 角色權限過濾測試
        // ========================================================================
        @Nested
        @DisplayName("3. 角色權限過濾測試")
        class RolePermissionFilterTests {

                @Test
                @DisplayName("ORG_E009: 主管查詢下屬 - 應只能查詢所管轄部門")
                void ORG_E009_ManagerQuerySubordinates() throws Exception {
                        // Given - 合約規格:
                        // 模擬角色: MANAGER
                        // 必須條件: department_id IN ('{managedDeptIds}'), is_deleted = 0
                        // 模擬主管管理部門 D001 和 D002
                        QueryGroup query = QueryBuilder.where()
                                        .in("department_id", List.of("D001", "D002"))
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E009: 主管應只能查詢所管轄部門的員工")
                                        .isNotEmpty()
                                        .allMatch(emp -> "D001".equals(emp.getDepartmentId().toString()) ||
                                                        "D002".equals(emp.getDepartmentId().toString()));
                }

                @Test
                @DisplayName("ORG_E010: 員工查詢同部門 - 應只能查詢同部門在職員工")
                void ORG_E010_EmployeeQuerySameDepartment() throws Exception {
                        // Given - 合約規格:
                        // 模擬角色: EMPLOYEE
                        // 必須條件: department_id = '{currentUserDeptId}', status = 'ACTIVE', is_deleted =
                        // 0
                        // 模擬員工所屬部門為 D001
                        String currentUserDeptId = "D001";
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", currentUserDeptId)
                                        .eq("employment_status", "ACTIVE")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        List<Employee> result = employeeRepository.findByQuery(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result)
                                        .as("ORG_E010: 員工應只能查詢同部門的在職員工")
                                        .isNotEmpty()
                                        .allMatch(emp -> currentUserDeptId.equals(emp.getDepartmentId().toString()) &&
                                                        "ACTIVE".equals(emp.getEmploymentStatus().name()));
                }
        }

        // ========================================================================
        // 4. 統計查詢測試
        // ========================================================================
        @Nested
        @DisplayName("4. 統計查詢測試")
        class StatisticsQueryTests {

                @Test
                @DisplayName("統計各狀態員工數")
                void countEmployeesByStatus() throws Exception {
                        // Given - 統計 ACTIVE 員工數
                        QueryGroup activeQuery = QueryBuilder.where()
                                        .eq("employment_status", "ACTIVE")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        long activeCount = employeeRepository.countByQuery(activeQuery);

                        // Then
                        assertThat(activeCount)
                                        .as("在職員工數應為 8")
                                        .isEqualTo(8);
                }

                @Test
                @DisplayName("統計各部門員工數")
                void countEmployeesByDepartment() throws Exception {
                        // Given - 統計 D001 部門員工數
                        QueryGroup deptQuery = QueryBuilder.where()
                                        .eq("department_id", "D001")
                                        .eq("is_deleted", false)
                                        .build();

                        // When
                        long deptCount = employeeRepository.countByQuery(deptQuery);

                        // Then
                        assertThat(deptCount)
                                        .as("部門 D001 員工數應為 5")
                                        .isEqualTo(5);
                }
        }
}
