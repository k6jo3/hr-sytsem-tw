package com.company.hrms.attendance.api.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.overtime.ApplyOvertimeRequest;
import com.company.hrms.attendance.api.request.overtime.ApproveOvertimeRequest;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiIntegrationTest;

/**
 * 加班管理 API 整合測試
 * 驗證加班 API 的完整流程（Controller → Service → Repository → H2 DB）
 *
 * <p>
 * <b>測試涵蓋範圍:</b>
 * <ul>
 * <li>加班申請 API（新增、更新、查詢）</li>
 * <li>加班審核 API（審核、駁回）</li>
 * <li>加班查詢 API（列表、詳情、過濾）</li>
 * </ul>
 *
 * TODO: 需建立測試資料腳本
 * - attendance_base_data.sql (員工基礎資料)
 * - overtime_test_data.sql (加班測試資料)
 * - cleanup.sql (清理腳本)
 *
 * @author SA Team
 * @since 2026-02-05
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Disabled("TODO:測試失敗 - 需建立測試資料腳本 (attendance_base_data.sql, overtime_test_data.sql)")
@DisplayName("加班管理 API 整合測試")
class OvertimeApiIntegrationTest extends BaseApiIntegrationTest {

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-emp-001");
		mockUser.setUsername("test_employee");
		mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

		List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("overtime:apply"));
		authorities.add(new SimpleGrantedAuthority("overtime:approve"));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 加班申請 CRUD API 測試
	 */
	@Nested
	@DisplayName("加班申請 CRUD API")
	class OvertimeCrudApiTests {

		@Test
		@DisplayName("ATT_OT_API_001: 新增加班申請 - 應返回申請 ID")
		void ATT_OT_API_001_createOvertime_ShouldReturnId() throws Exception {
			// Given
			ApplyOvertimeRequest request = new ApplyOvertimeRequest();
			request.setEmployeeId("test-emp-001");
			request.setDate(LocalDate.now());
			request.setHours(3.0);
			request.setOvertimeType("WEEKDAY");
			request.setReason("專案趕工");

			// When & Then
			var response = performPost("/api/v1/overtime", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("applicationId");
			assertThat(responseBody).contains("status");
		}

		@Test
		@DisplayName("ATT_OT_API_002: 查詢加班申請列表 - 應返回列表")
		void ATT_OT_API_002_getOvertimeList_ShouldReturnList() throws Exception {
			// When & Then
			var response = performGet("/api/v1/overtime")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("ATT_OT_API_003: 查詢加班申請詳情 - 應返回完整資訊")
		void ATT_OT_API_003_getOvertimeDetail_ShouldReturnDetail() throws Exception {
			// Given
			String overtimeId = "test-overtime-001";

			// When & Then
			var response = performGet("/api/v1/overtime/" + overtimeId)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("applicationId");
			assertThat(responseBody).contains("date");
		}
	}

	/**
	 * 加班審核 API 測試
	 */
	@Nested
	@DisplayName("加班審核 API")
	class OvertimeApprovalApiTests {

		@Test
		@DisplayName("ATT_OT_API_004: 審核通過加班申請 - 應更新狀態為 APPROVED")
		void ATT_OT_API_004_approveOvertime_ShouldUpdateStatus() throws Exception {
			// Given
			String overtimeId = "test-overtime-001";
			ApproveOvertimeRequest request = new ApproveOvertimeRequest();
			request.setComment("同意加班");

			// When & Then
			var response = performPost("/api/v1/overtime/" + overtimeId + "/approve", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("applicationId");
		}

		@Test
		@DisplayName("ATT_OT_API_005: 審核不存在的加班申請 - 應返回 404")
		void ATT_OT_API_005_approveOvertime_NotFound_ShouldReturn404() throws Exception {
			// Given
			String overtimeId = "non-existent-overtime";
			ApproveOvertimeRequest request = new ApproveOvertimeRequest();
			request.setComment("測試");

			// When & Then
			performPost("/api/v1/overtime/" + overtimeId + "/approve", request)
					.andExpect(status().isNotFound());
		}
	}

	/**
	 * 加班查詢與過濾 API 測試
	 */
	@Nested
	@DisplayName("加班查詢與過濾 API")
	class OvertimeQueryApiTests {

		@Test
		@DisplayName("ATT_OT_API_006: 依狀態過濾 - 應返回符合條件的加班申請")
		void ATT_OT_API_006_filterByStatus_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet("/api/v1/overtime?status=PENDING")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("ATT_OT_API_007: 依日期範圍查詢 - 應返回符合期間的加班申請")
		void ATT_OT_API_007_filterByDateRange_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet("/api/v1/overtime?startDate=2026-02-01&endDate=2026-02-28")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("ATT_OT_API_008: 依員工過濾 - 應返回該員工的加班申請")
		void ATT_OT_API_008_filterByEmployee_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet("/api/v1/overtime?employeeId=test-emp-001")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("ATT_OT_API_009: 分頁查詢 - 應返回分頁結果")
		void ATT_OT_API_009_pagination_ShouldReturnPagedResults() throws Exception {
			// When & Then
			var response = performGet("/api/v1/overtime?page=1&size=10")
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
		@DisplayName("應返回 400 當新增加班申請缺少必填欄位")
		void shouldReturn400WhenCreateOvertimeRequestMissingFields() throws Exception {
			// Given
			ApplyOvertimeRequest request = new ApplyOvertimeRequest();
			// 缺少必填欄位

			// When & Then
			performPost("/api/v1/overtime", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 400 當加班時數不正確")
		void shouldReturn400WhenOvertimeHoursInvalid() throws Exception {
			// Given
			ApplyOvertimeRequest request = new ApplyOvertimeRequest();
			request.setEmployeeId("test-emp-001");
			request.setDate(LocalDate.now());
			request.setHours(-1.0); // 負數時數
			request.setOvertimeType("WEEKDAY");
			request.setReason("測試");

			// When & Then
			performPost("/api/v1/overtime", request)
					.andExpect(status().isBadRequest());
		}
	}
}
