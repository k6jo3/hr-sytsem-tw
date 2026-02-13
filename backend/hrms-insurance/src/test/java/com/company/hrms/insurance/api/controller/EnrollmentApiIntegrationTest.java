package com.company.hrms.insurance.api.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiIntegrationTest;
import com.company.hrms.insurance.api.request.AdjustLevelRequest;
import com.company.hrms.insurance.api.request.EnrollEmployeeRequest;

/**
 * 投保管理 API 整合測試
 * 驗證投保 API 的完整流程（Controller → Service → Repository → H2 DB）
 *
 * <p>
 * <b>測試涵蓋範圍:</b>
 * <ul>
 * <li>投保 API（員工加保、查詢投保記錄）</li>
 * <li>投保級距調整 API</li>
 * <li>投保查詢 API（列表、詳情、過濾）</li>
 * </ul>
 *
 * - insurance_base_data.sql
 * - enrollment_test_data.sql
 * - cleanup.sql
 *
 * @author SA Team
 * @since 2026-02-05
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional

@Sql(scripts = {
		"classpath:test-data/insurance_base_data.sql",
		"classpath:test-data/enrollment_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("投保管理 API 整合測試")
class EnrollmentApiIntegrationTest extends BaseApiIntegrationTest {

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-user-001");
		mockUser.setUsername("hr_admin");
		mockUser.setRoles(Collections.singletonList("HR"));

		List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("insurance:enroll"));
		authorities.add(new SimpleGrantedAuthority("insurance:adjust"));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 投保 API 測試
	 */
	@Nested
	@DisplayName("投保 API")
	class EnrollmentApiTests {

		@Test
		@DisplayName("INS_ENROLL_API_001: 員工加保 - 應返回投保記錄 ID")
		void INS_ENROLL_API_001_enrollEmployee_ShouldReturnEnrollmentId() throws Exception {
			// Given
			EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
					.employeeId("test-emp-new")
					.insuranceUnitId("00000000-0000-0000-0000-000000000002")
					.monthlySalary(new BigDecimal("45000"))
					.enrollDate(LocalDate.now().toString())
					.selfContributionRate(new BigDecimal("6.0"))
					.build();

			// When & Then
			var response = performPost("/api/v1/insurance/enrollments", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("enrollmentId");
			assertThat(responseBody).contains("employeeId");
		}

		@Test
		@DisplayName("INS_ENROLL_API_002: 員工加保失敗 - 重複加保應返回 409")
		void INS_ENROLL_API_002_enrollEmployee_Duplicate_ShouldReturn409() throws Exception {
			// Given - 假設員工已加保
			EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
					.employeeId("test-emp-existing")
					.insuranceUnitId("00000000-0000-0000-0000-000000000002")
					.monthlySalary(new BigDecimal("45000"))
					.enrollDate(LocalDate.now().toString())
					.build();

			// When & Then
			performPost("/api/v1/insurance/enrollments", request)
					.andExpect(status().isConflict());
		}

		@Test
		@DisplayName("INS_ENROLL_API_003: 查詢投保記錄列表 - 應返回列表")
		void INS_ENROLL_API_003_getEnrollmentList_ShouldReturnList() throws Exception {
			// When & Then
			var response = performGet("/api/v1/insurance/enrollments")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("INS_ENROLL_API_004: 查詢投保記錄詳情 - 應返回完整資訊")
		void INS_ENROLL_API_004_getEnrollmentDetail_ShouldReturnDetail() throws Exception {
			// Given
			String enrollmentId = "00000000-0000-0000-0000-000000000301";

			// When & Then
			var response = performGet("/api/v1/insurance/enrollments/" + enrollmentId)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("enrollmentId");
			assertThat(responseBody).contains("monthlySalary");
		}
	}

	/**
	 * 投保級距調整 API 測試
	 */
	@Nested
	@DisplayName("投保級距調整 API")
	class LevelAdjustmentApiTests {

		@Test
		@DisplayName("INS_ADJUST_API_001: 調整投保級距 - 應更新級距")
		void INS_ADJUST_API_001_adjustLevel_ShouldUpdateLevel() throws Exception {
			// Given
			String enrollmentId = "00000000-0000-0000-0000-000000000301";
			AdjustLevelRequest request = AdjustLevelRequest.builder()
					.newMonthlySalary(new BigDecimal("50000"))
					.effectiveDate(LocalDate.now().toString())
					.reason("薪資調整")
					.build();

			// When & Then
			var response = performPut("/api/v1/insurance/enrollments/" + enrollmentId + "/adjust-level", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("enrollmentId");
		}

		@Test
		@DisplayName("INS_ADJUST_API_002: 調整投保級距失敗 - 投保記錄不存在應返回 404")
		void INS_ADJUST_API_002_adjustLevel_NotFound_ShouldReturn404() throws Exception {
			// Given
			String enrollmentId = "00000000-0000-0000-0000-ffffffffffff";
			AdjustLevelRequest request = AdjustLevelRequest.builder()
					.newMonthlySalary(new BigDecimal("50000"))
					.effectiveDate(LocalDate.now().toString())
					.reason("測試")
					.build();

			// When & Then
			performPut("/api/v1/insurance/enrollments/" + enrollmentId + "/adjust-level", request)
					.andExpect(status().isNotFound());
		}
	}

	/**
	 * 投保查詢與過濾 API 測試
	 */
	@Nested
	@DisplayName("投保查詢與過濾 API")
	class EnrollmentQueryApiTests {

		@Test
		@DisplayName("INS_QRY_API_001: 依員工過濾 - 應返回該員工的投保記錄")
		void INS_QRY_API_001_filterByEmployee_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet("/api/v1/insurance/enrollments?employeeId=test-emp-existing")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("INS_QRY_API_002: 依保險單位過濾 - 應返回該單位的投保記錄")
		void INS_QRY_API_002_filterByUnit_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet(
					"/api/v1/insurance/enrollments?insuranceUnitId=00000000-0000-0000-0000-000000000002")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("INS_QRY_API_003: 分頁查詢 - 應返回分頁結果")
		void INS_QRY_API_003_pagination_ShouldReturnPagedResults() throws Exception {
			// When & Then
			var response = performGet("/api/v1/insurance/enrollments?page=1&size=10")
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
		@DisplayName("應返回 400 當加保請求缺少必填欄位")
		void shouldReturn400WhenEnrollRequestMissingFields() throws Exception {
			// Given
			EnrollEmployeeRequest request = new EnrollEmployeeRequest();
			// 缺少必填欄位

			// When & Then
			performPost("/api/v1/insurance/enrollments", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 400 當月薪金額不正確")
		void shouldReturn400WhenSalaryInvalid() throws Exception {
			// Given
			EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
					.employeeId("test-emp-001")
					.insuranceUnitId("00000000-0000-0000-0000-000000000002")
					.monthlySalary(new BigDecimal("-1000")) // 負數金額
					.enrollDate(LocalDate.now().toString())
					.build();

			// When & Then
			performPost("/api/v1/insurance/enrollments", request)
					.andExpect(status().isBadRequest());
		}
	}
}
