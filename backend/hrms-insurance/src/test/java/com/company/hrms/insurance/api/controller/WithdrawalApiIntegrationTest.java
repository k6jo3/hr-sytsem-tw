package com.company.hrms.insurance.api.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
import com.company.hrms.insurance.api.request.WithdrawEnrollmentRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = { "classpath:test-data/insurance_base_data.sql",
		"classpath:test-data/withdrawal_test_data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("退保管理 API 整合測試")
class WithdrawalApiIntegrationTest extends BaseApiIntegrationTest {

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-user-001");
		mockUser.setUsername("hr_admin");
		mockUser.setRoles(Collections.singletonList("HR"));

		List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority("insurance:withdraw"));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 退保 API 測試
	 */
	@Nested
	@DisplayName("退保 API")
	class WithdrawalApiTests {

		@Test
		@DisplayName("INS_WITHDRAW_API_001: 員工退保 - 應更新投保狀態")
		void INS_WITHDRAW_API_001_withdrawEmployee_ShouldUpdateStatus() throws Exception {
			// Given
			String enrollmentId = "11111111-1111-1111-1111-000000000001";
			WithdrawEnrollmentRequest request = WithdrawEnrollmentRequest.builder()
					.withdrawDate(LocalDate.now().toString())
					.reason("員工離職")
					.build();

			// When & Then
			var response = performPut("/api/v1/insurance/enrollments/" + enrollmentId + "/withdraw", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("enrollmentId");
			assertThat(responseBody).contains("withdrawDate");
		}

		@Test
		@DisplayName("INS_WITHDRAW_API_002: 員工退保失敗 - 投保記錄不存在應返回 404")
		void INS_WITHDRAW_API_002_withdrawEmployee_NotFound_ShouldReturn404() throws Exception {
			// Given
			String enrollmentId = UUID.randomUUID().toString();
			WithdrawEnrollmentRequest request = WithdrawEnrollmentRequest.builder()
					.withdrawDate(LocalDate.now().toString())
					.reason("測試")
					.build();

			// When & Then
			performPut("/api/v1/insurance/enrollments/" + enrollmentId + "/withdraw", request)
					.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("INS_WITHDRAW_API_003: 員工退保失敗 - 已退保應返回 409")
		void INS_WITHDRAW_API_003_withdrawEmployee_AlreadyWithdrawn_ShouldReturn409() throws Exception {
			// Given - 假設員工已退保
			String enrollmentId = "11111111-1111-1111-1111-000000000002";
			WithdrawEnrollmentRequest request = WithdrawEnrollmentRequest.builder()
					.withdrawDate(LocalDate.now().toString())
					.reason("重複退保測試")
					.build();

			// When & Then
			performPut("/api/v1/insurance/enrollments/" + enrollmentId + "/withdraw", request)
					.andExpect(status().isConflict());
		}
	}

	/**
	 * 退保查詢 API 測試
	 */
	@Nested
	@DisplayName("退保查詢 API")
	class WithdrawalQueryApiTests {

		@Test
		@DisplayName("INS_WITHDRAW_QRY_001: 查詢退保記錄列表 - 應返回列表")
		void INS_WITHDRAW_QRY_001_getWithdrawalList_ShouldReturnList() throws Exception {
			// When & Then
			var response = performGet("/api/v1/insurance/enrollments?status=WITHDRAWN")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("INS_WITHDRAW_QRY_002: 依退保日期範圍查詢 - 應返回符合期間的記錄")
		void INS_WITHDRAW_QRY_002_filterByDateRange_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet(
					"/api/v1/insurance/enrollments?withdrawStartDate=2026-01-01&withdrawEndDate=2026-12-31")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("INS_WITHDRAW_QRY_003: 依員工查詢退保記錄 - 應返回該員工的記錄")
		void INS_WITHDRAW_QRY_003_filterByEmployee_ShouldReturnFiltered() throws Exception {
			// When & Then
			var response = performGet("/api/v1/insurance/enrollments?employeeId=test-emp-001&status=WITHDRAWN")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}

		@Test
		@DisplayName("INS_WITHDRAW_QRY_004: 分頁查詢 - 應返回分頁結果")
		void INS_WITHDRAW_QRY_004_pagination_ShouldReturnPagedResults() throws Exception {
			// When & Then
			var response = performGet("/api/v1/insurance/enrollments?status=WITHDRAWN&page=1&size=10")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
			assertThat(responseBody).contains("total");
		}
	}

	/**
	 * 退保歷程查詢測試
	 */
	@Nested
	@DisplayName("退保歷程查詢")
	class WithdrawalHistoryTests {

		@Test
		@DisplayName("INS_HISTORY_API_001: 查詢投保歷程 - 應返回完整歷程")
		void INS_HISTORY_API_001_getEnrollmentHistory_ShouldReturnHistory() throws Exception {
			// Given
			String enrollmentId = "11111111-1111-1111-1111-000000000001";

			// When & Then
			var response = performGet("/api/v1/insurance/enrollments/" + enrollmentId + "/history")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items");
		}
	}

	/**
	 * 異常情況處理測試
	 */
	@Nested
	@DisplayName("異常情況處理")
	class ExceptionHandlingTests {

		@Test
		@DisplayName("應返回 400 當退保請求缺少必填欄位")
		void shouldReturn400WhenWithdrawRequestMissingFields() throws Exception {
			// Given
			String enrollmentId = "11111111-1111-1111-1111-000000000001";
			WithdrawEnrollmentRequest request = new WithdrawEnrollmentRequest();
			// 缺少必填欄位

			// When & Then
			performPut("/api/v1/insurance/enrollments/" + enrollmentId + "/withdraw", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應返回 400 當退保日期不正確")
		void shouldReturn400WhenWithdrawDateInvalid() throws Exception {
			// Given
			String enrollmentId = "11111111-1111-1111-1111-000000000001";
			WithdrawEnrollmentRequest request = WithdrawEnrollmentRequest.builder()
					.withdrawDate(LocalDate.now().plusYears(1).toString()) // 未來日期
					.reason("測試")
					.build();

			// When & Then
			performPut("/api/v1/insurance/enrollments/" + enrollmentId + "/withdraw", request)
					.andExpect(status().isBadRequest());
		}
	}
}
