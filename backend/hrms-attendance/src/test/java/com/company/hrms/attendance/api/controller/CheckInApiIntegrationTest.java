package com.company.hrms.attendance.api.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.attendance.CheckInRequest;
import com.company.hrms.attendance.api.request.attendance.CheckOutRequest;
import com.company.hrms.attendance.api.request.attendance.CreateCorrectionRequest;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiIntegrationTest;

/**
 * 打卡管理 API 整合測試
 * 驗證打卡 API 的完整流程（Controller → Service → Repository → H2 DB）
 *
 * <p>
 * <b>測試涵蓋範圍:</b>
 * <ul>
 * <li>打卡 API（上班打卡、下班打卡）</li>
 * <li>補卡 API（補卡申請、補卡審核）</li>
 * <li>出勤記錄查詢 API</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-02-05
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
		"classpath:test-data/attendance_base_data.sql",
		"classpath:test-data/checkin_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("打卡管理 API 整合測試")
class CheckInApiIntegrationTest extends BaseApiIntegrationTest {

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-emp-001");
		mockUser.setUsername("test_employee");
		mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

		List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("attendance:checkin"));
		authorities.add(new SimpleGrantedAuthority("attendance:checkout"));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 打卡 API 測試
	 */

	@Nested
	@DisplayName("打卡 API")
	class CheckInApiTests {
		@Test
		@DisplayName("ATT_CHECKIN_API_001: 上班打卡 - 應返回打卡記錄")
		void ATT_CHECKIN_API_001_checkIn_ShouldReturnRecord() throws Exception {
			// Given
			CheckInRequest request = new CheckInRequest();
			request.setEmployeeId("test-emp-checkin"); // 使用無打卡記錄的員工
			request.setCheckInTime(LocalDateTime.now());
			request.setIpAddress("192.168.1.1");

			// When & Then
			var response = performPost("/api/v1/attendance/check-in", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("recordId");
			assertThat(responseBody).contains("checkInTime");
		}

		@Test
		@DisplayName("ATT_CHECKIN_API_002: 下班打卡 - 應更新打卡記錄")
		void ATT_CHECKIN_API_002_checkOut_ShouldUpdateRecord() throws Exception {
			// Given
			CheckOutRequest request = new CheckOutRequest();
			request.setEmployeeId("test-emp-checkout"); // 使用已有上班打卡的員工
			request.setCheckOutTime(LocalDateTime.now());
			request.setIpAddress("192.168.1.1");

			// When & Then
			var response = performPost("/api/v1/attendance/check-out", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("recordId");
			assertThat(responseBody).contains("checkOutTime");
		}

		@Test
		@DisplayName("ATT_CHECKIN_API_003: 重複打卡 - 應返回 409")
		void ATT_CHECKIN_API_003_duplicateCheckIn_ShouldReturn409() throws Exception {
			// Given - 假設已經打卡過
			CheckInRequest request = new CheckInRequest();
			request.setEmployeeId("test-emp-duplicate"); // 使用當日已打卡的員工
			request.setCheckInTime(LocalDateTime.now());

			// When & Then
			performPost("/api/v1/attendance/check-in", request)
					.andExpect(status().isConflict());
		}
	}

	/**
	 * 補卡 API 測試
	 */
	@Nested
	@DisplayName("補卡 API")
	class CorrectionApiTests {

		@Test
		@DisplayName("ATT_CORR_API_001: 補卡申請 - 應返回申請 ID")
		void ATT_CORR_API_001_createCorrection_ShouldReturnId() throws Exception {
			// Given
			CreateCorrectionRequest request = new CreateCorrectionRequest();
			request.setEmployeeId("test-emp-001");
			request.setCorrectionDate(LocalDateTime.now().toLocalDate());
			request.setCorrectionType("FORGET_CHECK_IN");
			request.setCorrectedCheckInTime(LocalDateTime.now().toLocalTime());
			request.setReason("忘記打卡");

			// When & Then
			var response = performPost("/api/v1/attendance/corrections", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("correctionId");
		}

		@Test
		@DisplayName("ATT_CORR_API_002: 補卡審核 - 應更新狀態")
		void ATT_CORR_API_002_approveCorrection_ShouldUpdateStatus() throws Exception {
			// Given
			String correctionId = "test-correction-001";

			// When & Then
			var response = performPut("/api/v1/attendance/corrections/" + correctionId + "/approve", null)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("correctionId");
			assertThat(responseBody).contains("status");
		}
	}

	/**
	 * 查詢 API 測試
	 */
	@Nested
	@DisplayName("出勤記錄查詢 API")
	class AttendanceQueryApiTests {

		@Test
		@DisplayName("ATT_QRY_API_001: 查詢出勤記錄 - 應返回列表")
		void ATT_QRY_API_001_getRecords_ShouldReturnList() throws Exception {
			// When & Then
			var response = performGet("/api/v1/attendance/records")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("ATT_QRY_API_002: 依日期範圍查詢 - 應過濾結果")
		void ATT_QRY_API_002_getRecordsByDateRange_ShouldFilter() throws Exception {
			// When & Then
			var response = performGet("/api/v1/attendance/records?startDate=2026-02-01&endDate=2026-02-05")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("ATT_QRY_API_003: 查詢出勤記錄詳情 - 應返回完整資訊")
		void ATT_QRY_API_003_getRecordDetail_ShouldReturnDetail() throws Exception {
			// Given
			String recordId = "test-record-001";

			// When & Then
			var response = performGet("/api/v1/attendance/records/" + recordId)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("recordId");
			assertThat(responseBody).contains("checkInTime");
		}
	}

	/**
	 * 異常情況處理測試
	 */
	@Nested
	@DisplayName("異常情況處理")
	class ExceptionHandlingTests {

		@Test
		@DisplayName("應返回 400 當打卡請求缺少必填欄位")
		void shouldReturn400WhenCheckInRequestMissingFields() throws Exception {
			// Given
			CheckInRequest request = new CheckInRequest();
			// 缺少必填欄位

			// When & Then
			performPost("/api/v1/attendance/check-in", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 404 當查詢不存在的出勤記錄")
		void shouldReturn404WhenRecordNotFound() throws Exception {
			// Given
			String recordId = "non-existent-record";

			// When & Then
			performGet("/api/v1/attendance/records/" + recordId)
					.andExpect(status().isNotFound());
		}
	}
}
