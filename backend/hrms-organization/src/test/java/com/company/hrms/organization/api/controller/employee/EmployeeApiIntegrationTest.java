package com.company.hrms.organization.api.controller.employee;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiIntegrationTest;
import com.company.hrms.organization.api.request.employee.CreateEmployeeRequest;
import com.company.hrms.organization.api.request.employee.TerminateEmployeeRequest;
import com.company.hrms.organization.api.request.employee.UpdateEmployeeRequest;

/**
 * Employee API 整合測試
 * 驗證員工管理 API 的完整流程（Controller → Service → Repository → H2 DB）
 *
 * <p>
 * <b>TODO:</b> 測試資料腳本缺失，需建立以下檔案才能啟用測試：
 * <ul>
 * <li><b>organization_base_data.sql:</b>
 * <ul>
 * <li>建立組織架構資料表（organization, department）</li>
 * <li>插入測試用的組織資料</li>
 * <li>插入測試用的部門資料（DEPT-001, DEPT-002 等）</li>
 * </ul>
 * </li>
 * <li><b>employee_test_data.sql:</b>
 * <ul>
 * <li>插入測試用的員工資料（EMP-001, EMP-002 等）</li>
 * <li>員工應包含不同狀態（ACTIVE, TERMINATED, ON_LEAVE）</li>
 * <li>員工應關聯到部門</li>
 * <li>員工應包含完整資訊（姓名、Email、入職日期、職位等）</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * <b>測試涵蓋範圍:</b>
 * <ul>
 * <li>員工 CRUD API（新增、更新、查詢列表、查詢詳情）</li>
 * <li>員工離職 API</li>
 * <li>員工搜尋與過濾 API（依姓名、部門、狀態）</li>
 * <li>員工分頁查詢 API</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-02-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
		"classpath:test-data/organization_base_data.sql",
		"classpath:test-data/organization_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("員工管理 API 整合測試")
class EmployeeApiIntegrationTest extends BaseApiIntegrationTest {

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-user-001");
		mockUser.setUsername("hr_admin");
		mockUser.setRoles(Collections.singletonList("HR"));

		List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(java.util.stream.Collectors.toList());

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 員工 CRUD API 測試
	 */
	@Nested
	@DisplayName("員工 CRUD API")
	class EmployeeCrudApiTests {

		@Test
		@DisplayName("ORG_EMP_API_001: 新增員工 - 應返回員工 ID")
		void ORG_EMP_API_001_createEmployee_ShouldReturnEmployeeId() throws Exception {
			// Given
			CreateEmployeeRequest request = new CreateEmployeeRequest();
			request.setEmployeeNumber("EMP-NEW-001");
			request.setFirstName("John");
			request.setLastName("Doe");
			request.setCompanyEmail("john.doe@company.com");
			request.setOrganizationId("ORG001");
			request.setDepartmentId("D001");
			request.setHireDate(LocalDate.now());
			request.setJobTitle("Software Engineer");
			request.setEmploymentType("REGULAR");
			request.setNationalId("A123456789");
			request.setDateOfBirth(LocalDate.of(1990, 1, 1));
			request.setGender("MALE");
			request.setMobilePhone("0912345678");

			// When & Then
			var response = performPost("/api/v1/employees", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("employeeId");
			assertThat(responseBody).contains("employeeNumber");
		}

		@Test
		@DisplayName("ORG_EMP_API_002: 新增員工失敗 - 員工編號已存在應返回 409")
		void ORG_EMP_API_002_createEmployee_DuplicateNumber_ShouldReturn409() throws Exception {
			// Given
			CreateEmployeeRequest request = new CreateEmployeeRequest();
			request.setEmployeeNumber("EMP202501-001"); // 已存在的員工編號 (從 organization_test_data.sql 來的)
			request.setFirstName("Jane");
			request.setLastName("Smith");
			request.setCompanyEmail("jane@company.com");
			request.setOrganizationId("ORG001");
			request.setDepartmentId("D001");
			request.setHireDate(LocalDate.now());
			request.setEmploymentType("REGULAR");
			request.setNationalId("B234567890");
			request.setDateOfBirth(LocalDate.of(1990, 1, 1));
			request.setGender("FEMALE");
			request.setMobilePhone("0912345678");

			// When & Then
			performPost("/api/v1/employees", request)
					.andExpect(status().isConflict());
		}

		@Test
		@DisplayName("ORG_EMP_API_003: 更新員工 - 應返回更新後的員工資訊")
		void ORG_EMP_API_003_updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {
			// Given
			String employeeId = "E001";
			UpdateEmployeeRequest request = new UpdateEmployeeRequest();
			request.setJobTitle("Senior Software Engineer");

			// When & Then
			var response = performPut("/api/v1/employees/" + employeeId, request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("employeeId");
			assertThat(responseBody).contains("Senior Software Engineer");
		}

		@Test
		@DisplayName("ORG_EMP_API_004: 更新員工失敗 - 員工不存在應返回 404")
		void ORG_EMP_API_004_updateEmployee_NotFound_ShouldReturn404() throws Exception {
			// Given
			String employeeId = "NON-EXISTENT-EMP";
			UpdateEmployeeRequest request = new UpdateEmployeeRequest();
			request.setJobTitle("Manager");

			// When & Then
			performPut("/api/v1/employees/" + employeeId, request)
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("ORG_EMP_API_005: 查詢員工列表 - 應返回員工清單")
		void ORG_EMP_API_005_getEmployeeList_ShouldReturnEmployees() throws Exception {
			// When & Then
			var response = performGet("/api/v1/employees")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("data");
		}

		@Test
		@DisplayName("ORG_EMP_API_006: 查詢員工詳情 - 應返回完整員工資訊")
		void ORG_EMP_API_006_getEmployeeDetail_ShouldReturnEmployeeDetail() throws Exception {
			// Given
			String employeeId = "E001";

			// When & Then
			var response = performGet("/api/v1/employees/" + employeeId)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("employeeId");
			assertThat(responseBody).contains("employeeNumber");
			assertThat(responseBody).contains("department");
		}

		@Test
		@DisplayName("ORG_EMP_API_007: 查詢員工詳情失敗 - 員工不存在應返回 404")
		void ORG_EMP_API_007_getEmployeeDetail_NotFound_ShouldReturn404() throws Exception {
			// Given
			String employeeId = "NON-EXISTENT-EMP";

			// When & Then
			performGet("/api/v1/employees/" + employeeId)
					.andExpect(status().isNotFound());
		}
	}

	/**
	 * 員工離職 API 測試
	 */
	@Nested
	@DisplayName("員工離職 API")
	class EmployeeTerminationApiTests {

		@Test
		@DisplayName("ORG_EMP_API_008: 員工離職 - 應返回成功訊息")
		void ORG_EMP_API_008_terminateEmployee_ShouldReturnSuccess() throws Exception {
			// Given
			String employeeId = "E001";
			TerminateEmployeeRequest request = new TerminateEmployeeRequest();
			request.setTerminationDate(LocalDate.now());
			request.setReason("Resignation");

			// When & Then
			var response = performPost("/api/v1/employees/" + employeeId + "/terminate", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("employeeId");
		}

		@Test
		@DisplayName("ORG_EMP_API_009: 員工離職失敗 - 員工不存在應返回 404")
		void ORG_EMP_API_009_terminateEmployee_NotFound_ShouldReturn404() throws Exception {
			// Given
			String employeeId = "NON-EXISTENT-EMP";
			TerminateEmployeeRequest request = new TerminateEmployeeRequest();
			request.setTerminationDate(LocalDate.now());
			request.setReason("Resignation");

			// When & Then
			performPost("/api/v1/employees/" + employeeId + "/terminate", request)
					.andExpect(status().isNotFound());
		}
	}

	/**
	 * 員工搜尋與過濾 API 測試
	 */
	@Nested
	@DisplayName("員工搜尋與過濾 API")
	class EmployeeSearchApiTests {

		@Test
		@DisplayName("ORG_EMP_API_010: 依姓名搜尋 - 應返回符合的員工")
		void ORG_EMP_API_010_searchByName_ShouldReturnMatchingEmployees() throws Exception {
			// When & Then
			var response = performGet("/api/v1/employees?search=王")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("王");
		}

		@Test
		@DisplayName("ORG_EMP_API_011: 依部門過濾 - 應返回該部門的員工")
		void ORG_EMP_API_011_filterByDepartment_ShouldReturnDepartmentEmployees() throws Exception {
			// When & Then
			var response = performGet("/api/v1/employees?departmentId=D001")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("data");
		}

		@Test
		@DisplayName("ORG_EMP_API_012: 依狀態過濾 - 應返回指定狀態的員工")
		void ORG_EMP_API_012_filterByStatus_ShouldReturnFilteredEmployees() throws Exception {
			// When & Then
			var response = performGet("/api/v1/employees?status=ACTIVE")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("data");
		}

		@Test
		@DisplayName("ORG_EMP_API_013: 分頁查詢 - 應返回分頁結果")
		void ORG_EMP_API_013_pagination_ShouldReturnPagedResults() throws Exception {
			// When & Then
			var response = performGet("/api/v1/employees?page=1&size=10")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("data");
		}
	}

	/**
	 * 異常情況處理測試
	 */
	@Nested
	@DisplayName("異常情況處理")
	class ExceptionHandlingTests {

		@Test
		@DisplayName("應返回 400 當新增員工請求缺少必填欄位")
		void shouldReturn400WhenCreateEmployeeRequestMissingFields() throws Exception {
			// Given
			CreateEmployeeRequest request = new CreateEmployeeRequest();
			// 缺少必填欄位

			// When & Then
			performPost("/api/v1/employees", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 400 當 Email 格式不正確")
		void shouldReturn400WhenEmailFormatInvalid() throws Exception {
			// Given
			CreateEmployeeRequest request = new CreateEmployeeRequest();
			request.setEmployeeNumber("EMP-NEW-002");
			request.setFirstName("Test");
			request.setLastName("User");
			request.setCompanyEmail("invalid-email");
			request.setOrganizationId("ORG001");
			request.setDepartmentId("D001");
			request.setHireDate(LocalDate.now());

			// When & Then
			performPost("/api/v1/employees", request)
					.andExpect(status().isBadRequest());
		}
	}
}
