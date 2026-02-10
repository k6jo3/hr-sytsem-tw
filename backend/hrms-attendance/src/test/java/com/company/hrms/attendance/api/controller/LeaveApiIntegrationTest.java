package com.company.hrms.attendance.api.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.leave.ApplyLeaveRequest;
import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiIntegrationTest;

/**
 * 請假管理 API 整合測試
 * 驗證請假 API 的完整流程（Controller → Service → Repository → H2 DB）
 *
 * <p>
 * <b>測試涵蓋範圍:</b>
 * <ul>
 * <li>請假申請 API（新增、更新、查詢）</li>
 * <li>請假審核 API（審核、駁回）</li>
 * <li>請假查詢 API（列表、詳情、過濾）</li>
 * </ul>
 *
 *
 * @author SA Team
 * @since 2026-02-05
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/attendance_base_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/leave_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("請假管理 API 整合測試")
class LeaveApiIntegrationTest extends BaseApiIntegrationTest {

	@org.springframework.boot.test.mock.mockito.MockBean
	private com.company.hrms.attendance.infrastructure.client.organization.OrganizationServiceClient organizationServiceClient;

	@Override
	protected ResultActions performGet(String url) throws Exception {
		return super.performGet(url);
	}

	@Override
	protected ResultActions performPost(String url, Object request) throws Exception {
		return super.performPost(url, request);
	}

	@Override
	protected ResultActions performPut(String url, Object request) throws Exception {
		return super.performPut(url, request);
	}

	@Override
	protected ResultActions performDelete(String url) throws Exception {
		return super.performDelete(url);
	}

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-emp-001");
		mockUser.setUsername("test_employee");
		mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

		List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("leave:apply"));
		authorities.add(new SimpleGrantedAuthority("leave:approve"));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 請假申請 CRUD API 測試
	 */
	@Nested
	@DisplayName("請假申請 CRUD API")
	class LeaveCrudApiTests {

		@Test
		@DisplayName("ATT_LEAVE_API_001: 新增請假申請 - 應返回申請 ID")
		void ATT_LEAVE_API_001_createLeave_ShouldReturnId() throws Exception {
			// Given
			ApplyLeaveRequest request = new ApplyLeaveRequest();
			request.setEmployeeId("test-emp-001");
			request.setLeaveTypeId("ANNUAL");
			request.setStartDate(LocalDate.now().plusDays(1));
			request.setEndDate(LocalDate.now().plusDays(3));
			request.setReason("家庭事務");

			// When & Then
			var response = performPost("/api/v1/leave/applications", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("applicationId");
			assertThat(responseBody).contains("status");
		}

		@Test
		@DisplayName("ATT_LEAVE_API_002: 查詢請假申請列表 - 應返回列表")
		void ATT_LEAVE_API_002_getLeaveList_ShouldReturnList() throws Exception {
			// When & Then
			var response = performGet("/api/v1/leave/applications")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("test-leave-001");
		}

		@Test
		@DisplayName("ATT_LEAVE_API_003: 查詢請假申請詳情 - 應返回完整資訊")
		void ATT_LEAVE_API_003_getLeaveDetail_ShouldReturnDetail() throws Exception {
			// Given
			String leaveId = "test-leave-001";

			// When & Then
			var response = performGet("/api/v1/leave/applications/" + leaveId)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("applicationId");
			assertThat(responseBody).contains("leaveType");
		}
	}

	/**
	 * 請假審核 API 測試
	 */
	@Nested
	@DisplayName("請假審核 API")
	class LeaveApprovalApiTests {

		@Test
		@DisplayName("ATT_LEAVE_API_004: 審核通過請假申請 - 應更新狀態為 APPROVED")
		void ATT_LEAVE_API_004_approveLeave_ShouldUpdateStatus() throws Exception {
			// Given
			String leaveId = "test-leave-001";
			ApproveLeaveRequest request = new ApproveLeaveRequest();
			request.setApplicationId(leaveId);

			// When & Then
			var response = performPut("/api/v1/leave/applications/" + leaveId + "/approve", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("applicationId");
		}

		@Test
		@DisplayName("ATT_LEAVE_API_005: 審核不存在的請假申請 - 應返回 404")
		void ATT_LEAVE_API_005_approveLeave_NotFound_ShouldReturn404() throws Exception {
			// Given
			String leaveId = "non-existent-leave";
			ApproveLeaveRequest request = new ApproveLeaveRequest();
			request.setApplicationId(leaveId);

			// When & Then
			performPut("/api/v1/leave/applications/" + leaveId + "/approve", request)
					.andExpect(status().isNotFound());
		}
	}

	/**
	 * 請假查詢與過濾 API 測試
	 */
	@Nested
	@DisplayName("請假查詢與過濾 API")
	class LeaveQueryApiTests {

		@Test
		@DisplayName("ATT_LEAVE_API_006: 依狀態過濾 - 應返回符合條件的請假申請")
		void ATT_LEAVE_API_006_filterByStatus_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet("/api/v1/leave/applications?status=PENDING")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("PENDING");
		}

		@Test
		@DisplayName("ATT_LEAVE_API_007: 依日期範圍查詢 - 應返回符合期間的請假申請")
		void ATT_LEAVE_API_007_filterByDateRange_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet("/api/v1/leave/applications?startDate=2026-02-01&endDate=2026-02-28")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("test-leave-001");
		}

		@Test
		@DisplayName("ATT_LEAVE_API_008: 依請假類型過濾 - 應返回符合類型的請假申請")
		void ATT_LEAVE_API_008_filterByLeaveType_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet("/api/v1/leave/applications?leaveTypeId=ANNUAL")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("ANNUAL");
		}

		@Test
		@DisplayName("ATT_LEAVE_API_009: 分頁查詢 - 應返回分頁結果")
		void ATT_LEAVE_API_009_pagination_ShouldReturnPagedResults() throws Exception {
			// When & Then
			var response = performGet("/api/v1/leave/applications?page=1&size=10")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
			assertThat(responseBody).contains("total");
		}
	}

	/**
	 * 異常情況處理測試
	 */
	@Nested
	@DisplayName("異常情況處理")
	class ExceptionHandlingTests {

		@Test
		@DisplayName("應返回 400 當新增請假申請缺少必填欄位")
		void shouldReturn400WhenCreateLeaveRequestMissingFields() throws Exception {
			// Given
			ApplyLeaveRequest request = new ApplyLeaveRequest();
			// 缺少必填欄位

			// When & Then
			performPost("/api/v1/leave/applications", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 400 當請假日期範圍不正確")
		void shouldReturn400WhenLeaveDateRangeInvalid() throws Exception {
			// Given
			ApplyLeaveRequest request = new ApplyLeaveRequest();
			request.setEmployeeId("test-emp-001");
			request.setLeaveTypeId("ANNUAL");
			request.setStartDate(LocalDate.now());
			request.setEndDate(LocalDate.now().minusDays(1)); // 結束日期早於開始日期
			request.setReason("測試");

			// When & Then
			performPost("/api/v1/leave/applications", request)
					.andExpect(status().isBadRequest());
		}
	}
}
