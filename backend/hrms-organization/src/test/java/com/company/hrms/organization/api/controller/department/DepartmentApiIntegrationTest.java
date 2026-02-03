package com.company.hrms.organization.api.controller.department;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import com.company.hrms.organization.api.request.department.CreateDepartmentRequest;
import com.company.hrms.organization.api.request.department.UpdateDepartmentRequest;

/**
 * Department API 整合測試
 * 驗證部門管理 API 的完整流程（Controller → Service → Repository → H2 DB）
 *
 * <p>
 * <b>TODO:</b> 測試資料腳本缺失，需建立以下檔案才能啟用測試：
 * <ul>
 * <li><b>organization_base_data.sql:</b>
 * <ul>
 * <li>建立組織架構資料表（organization, department）</li>
 * <li>插入測試用的組織資料（ORG-001, ORG-002 等）</li>
 * </ul>
 * </li>
 * <li><b>department_test_data.sql:</b>
 * <ul>
 * <li>插入測試用的部門資料（DEPT-001, DEPT-002 等）</li>
 * <li>部門應包含階層結構（parent_department_id）</li>
 * <li>部門應關聯到組織</li>
 * <li>部門應包含完整資訊（名稱、主管、員工數等）</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * <b>測試涵蓋範圍:</b>
 * <ul>
 * <li>部門 CRUD API（新增、更新、查詢列表、查詢詳情、刪除）</li>
 * <li>部門階層查詢 API（查詢子部門、查詢上層部門）</li>
 * <li>部門員工查詢 API</li>
 * <li>部門搜尋與過濾 API</li>
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
		"classpath:test-data/department_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("部門管理 API 整合測試")
class DepartmentApiIntegrationTest extends BaseApiIntegrationTest {

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
	 * 部門 CRUD API 測試
	 */
	@Nested
	@DisplayName("部門 CRUD API")
	class DepartmentCrudApiTests {

		@Test
		@DisplayName("ORG_DEPT_API_001: 新增部門 - 應返回部門 ID")
		void ORG_DEPT_API_001_createDepartment_ShouldReturnDepartmentId() throws Exception {
			// Given
			CreateDepartmentRequest request = new CreateDepartmentRequest();
			request.setCode("DEPT-NEW-001");
			request.setName("New Department");
			request.setOrganizationId("11111111-1111-1111-1111-111111111111");
			request.setParentId("33333333-3333-3333-3333-333333330001");

			// When & Then
			var response = performPost("/api/v1/departments", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("departmentId");
			assertThat(responseBody).contains("code");
		}

		@Test
		@DisplayName("ORG_DEPT_API_002: 新增部門失敗 - 部門代碼已存在應返回 409")
		void ORG_DEPT_API_002_createDepartment_DuplicateCode_ShouldReturn409() throws Exception {
			// Given
			CreateDepartmentRequest request = new CreateDepartmentRequest();
			request.setCode("RD"); // 已存在的部門代碼 (從 department_test_data.sql 來的)
			request.setName("Duplicate Department");
			request.setOrganizationId("11111111-1111-1111-1111-111111111111");

			// When & Then
			performPost("/api/v1/departments", request)
					.andExpect(status().isConflict());
		}

		@Test
		@DisplayName("ORG_DEPT_API_003: 更新部門 - 應返回更新後的部門資訊")
		void ORG_DEPT_API_003_updateDepartment_ShouldReturnUpdatedDepartment() throws Exception {
			// Given
			String departmentId = "33333333-3333-3333-3333-333333330001";
			UpdateDepartmentRequest request = new UpdateDepartmentRequest();
			request.setName("Updated Department Name");

			// When & Then
			var response = performPut("/api/v1/departments/" + departmentId, request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("departmentId");
			assertThat(responseBody).contains("Updated Department Name");
		}

		@Test
		@DisplayName("ORG_DEPT_API_004: 更新部門失敗 - 部門不存在應返回 404")
		void ORG_DEPT_API_004_updateDepartment_NotFound_ShouldReturn404() throws Exception {
			// Given
			String departmentId = "ffffffff-ffff-ffff-ffff-ffffffffffff";
			UpdateDepartmentRequest request = new UpdateDepartmentRequest();
			request.setName("Updated Name");

			// When & Then
			performPut("/api/v1/departments/" + departmentId, request)
					.andExpect(status().isNotFound());
		}

		@Test
		@Disabled("TODO: 缺少 GetDepartmentListService 實作")
		@DisplayName("ORG_DEPT_API_005: 查詢部門列表 - 應返回部門清單")
		void ORG_DEPT_API_005_getDepartmentList_ShouldReturnDepartments() throws Exception {
			// When & Then
			var response = performGet("/api/v1/departments")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
			assertThat(responseBody).contains("totalCount");
		}

		@Test
		@DisplayName("ORG_DEPT_API_006: 查詢部門詳情 - 應返回完整部門資訊")
		void ORG_DEPT_API_006_getDepartmentDetail_ShouldReturnDepartmentDetail() throws Exception {
			// Given
			String departmentId = "33333333-3333-3333-3333-333333330001";

			// When & Then
			var response = performGet("/api/v1/departments/" + departmentId)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("departmentId");
			assertThat(responseBody).contains("name");
		}

		@Test
		@DisplayName("ORG_DEPT_API_007: 查詢部門詳情失敗 - 部門不存在應返回 404")
		void ORG_DEPT_API_007_getDepartmentDetail_NotFound_ShouldReturn404() throws Exception {
			// Given
			String departmentId = "ffffffff-ffff-ffff-ffff-ffffffffffff";

			// When & Then
			performGet("/api/v1/departments/" + departmentId)
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("ORG_DEPT_API_008: 刪除部門 - 應返回 204")
		void ORG_DEPT_API_008_deleteDepartment_ShouldReturn204() throws Exception {
			// Given
			String departmentId = "33333333-3333-3333-3333-333333330003"; // 假設此部門無員工且無子部門

			// When & Then
			performDelete("/api/v1/departments/" + departmentId)
					.andExpect(status().isNoContent());
		}

		@Test
		@Disabled("TODO: 部門下有員工的驗證需要完整的 EmployeeRepository 與 Service 實作")
		@DisplayName("ORG_DEPT_API_009: 刪除部門失敗 - 部門下有員工應返回 400")
		void ORG_DEPT_API_009_deleteDepartment_HasEmployees_ShouldReturn400() throws Exception {
			// Given
			String departmentId = "33333333-3333-3333-3333-333333330001"; // 研發部有員工

			// When & Then
			performDelete("/api/v1/departments/" + departmentId)
					.andExpect(status().isBadRequest());
		}
	}

	/**
	 * 部門階層查詢 API 測試
	 */
	@Nested
	@DisplayName("部門階層查詢 API")
	class DepartmentHierarchyApiTests {

		@Test
		@Disabled("TODO: 缺少 GetSubDepartmentsService 實作")
		@DisplayName("ORG_DEPT_API_010: 查詢子部門 - 應返回子部門列表")
		void ORG_DEPT_API_010_getSubDepartments_ShouldReturnSubDepartments() throws Exception {
			// Given
			String parentDepartmentId = "33333333-3333-3333-3333-333333330001";

			// When & Then
			var response = performGet("/api/v1/departments/" + parentDepartmentId + "/sub-departments")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("ORG_DEPT_API_011: 查詢部門樹 - 應返回完整階層結構")
		void ORG_DEPT_API_011_getDepartmentTree_ShouldReturnHierarchy() throws Exception {
			// Given
			String organizationId = "11111111-1111-1111-1111-111111111111";

			// When & Then
			var response = performGet("/api/v1/organizations/" + organizationId + "/tree")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("departments");
		}
	}

	/**
	 * 部門員工查詢 API 測試
	 */
	@Nested
	@DisplayName("部門員工查詢 API")
	class DepartmentEmployeeApiTests {

		@Test
		@DisplayName("ORG_DEPT_API_012: 查詢部門員工 - 應返回員工列表")
		void ORG_DEPT_API_012_getDepartmentEmployees_ShouldReturnEmployees() throws Exception {
			// Given
			String departmentId = "33333333-3333-3333-3333-333333330001";

			// When & Then
			var response = performGet("/api/v1/employees?departmentId=" + departmentId)
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
		@DisplayName("應返回 400 當新增部門請求缺少必填欄位")
		void shouldReturn400WhenCreateDepartmentRequestMissingFields() throws Exception {
			// Given
			CreateDepartmentRequest request = new CreateDepartmentRequest();
			// 缺少必填欄位

			// When & Then
			performPost("/api/v1/departments", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 400 當上層部門不存在")
		void shouldReturn400WhenParentDepartmentNotFound() throws Exception {
			// Given
			CreateDepartmentRequest request = new CreateDepartmentRequest();
			request.setCode("DEPT-NEW-002");
			request.setName("New Department");
			request.setOrganizationId("11111111-1111-1111-1111-111111111111");
			request.setParentId("ffffffff-ffff-ffff-ffff-ffffffffffff");

			// When & Then
			performPost("/api/v1/departments", request)
					.andExpect(status().isBadRequest());
		}
	}
}
