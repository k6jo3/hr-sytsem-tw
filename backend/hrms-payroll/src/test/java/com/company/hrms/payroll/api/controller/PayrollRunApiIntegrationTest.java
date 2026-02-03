package com.company.hrms.payroll.api.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.company.hrms.payroll.application.dto.request.CalculatePayrollRequest;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.application.dto.request.StartPayrollRunRequest;
import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;
import com.company.hrms.payroll.domain.model.valueobject.PayrollRunStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PayrollRun API 整合測試
 * 驗證薪資批次完整生命週期的 API 流程
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>薪資批次建立、計算、送審、核准、發薪的完整流程</li>
 * <li>狀態轉換驗證</li>
 * <li>API 回應格式驗證</li>
 * <li>異常情況處理</li>
 * </ul>
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/payroll_run_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("薪資批次 API 整合測試")
class PayrollRunApiIntegrationTest extends BaseApiIntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	private String currentRunId;

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-user-001");
		mockUser.setUsername("test-user");
		mockUser.setRoles(Collections.singletonList("HR"));

		List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.toList();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	@DisplayName("PAY_API_001: 建立薪資批次 - 應回傳 runId 和 DRAFT 狀態")
	void PAY_API_001_createPayrollRun_ShouldReturnRunIdAndDraftStatus() throws Exception {
		// Given
		StartPayrollRunRequest request = new StartPayrollRunRequest();
		request.setOrganizationId("ORG-001");
		request.setPayrollSystem("MONTHLY");
		request.setStartDate(LocalDate.of(2025, 12, 1));
		request.setEndDate(LocalDate.of(2025, 12, 31));
		request.setPayDate(LocalDate.of(2026, 1, 5));
		request.setName("2025年12月薪資核准");

		// When & Then
		var response = performPost("/api/v1/payroll-runs", request)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.runId").isNotEmpty())
				.andExpect(jsonPath("$.status").value("DRAFT"))
				.andExpect(jsonPath("$.organizationId").value("ORG-001"))
				.andReturn();

		String responseBody = response.getResponse().getContentAsString();
		PayrollRunResponse payrollRun = objectMapper.readValue(responseBody, PayrollRunResponse.class);
		currentRunId = payrollRun.getRunId();

		assertThat(currentRunId).isNotBlank();
		assertThat(payrollRun.getStatus()).isEqualTo(PayrollRunStatus.DRAFT.toString());
	}

	/**
	 * PAY_API_002: 執行薪資計算
	 */
	@Nested
	@DisplayName("薪資計算 API")
	class PayrollCalculationApiTests {

		@Test
		@DisplayName("PAY_API_002: 執行薪資計算 - 應從 DRAFT 轉為 CALCULATING 再到 COMPLETED")
		void PAY_API_002_executePayrollCalculation_ShouldTransitionToCalculating() throws Exception {
			// Given: 建立一個薪資批次
			StartPayrollRunRequest createReq = new StartPayrollRunRequest();
			createReq.setOrganizationId("ORG-001");
			createReq.setPayrollSystem("MONTHLY");
			createReq.setStartDate(LocalDate.of(2025, 12, 1));
			createReq.setEndDate(LocalDate.of(2025, 12, 31));
			createReq.setPayDate(LocalDate.of(2026, 1, 5));
			createReq.setName("2025年12月薪資計算");

			var createResponse = performPost("/api/v1/payroll-runs", createReq)
					.andExpect(status().isOk())
					.andReturn();

			String createResponseBody = createResponse.getResponse().getContentAsString();
			PayrollRunResponse createdRun = objectMapper.readValue(createResponseBody, PayrollRunResponse.class);
			String runId = createdRun.getRunId();

			// When: 執行薪資計算
			CalculatePayrollRequest calcReq = new CalculatePayrollRequest();
			var calcResponse = performPost("/api/v1/payroll-runs/" + runId + "/execute", calcReq)
					.andExpect(status().isOk())
					.andReturn();

			// Then: 驗證狀態轉換和回應
			String calcResponseBody = calcResponse.getResponse().getContentAsString();
			PayrollRunResponse calculatedRun = objectMapper.readValue(calcResponseBody, PayrollRunResponse.class);

			assertThat(calculatedRun.getStatus())
					.as("PAY_API_002: 薪資計算後狀態應為 COMPLETED")
					.isEqualTo(PayrollRunStatus.COMPLETED.toString());

			assertThat(calculatedRun.getExecutedAt())
					.as("PAY_API_002: executedAt 時間應被記錄")
					.isNotNull();

			assertThat(calculatedRun.getCompletedAt())
					.as("PAY_API_002: completedAt 時間應被記錄")
					.isNotNull();

			// 驗證統計資料已計算
			assertThat(calculatedRun.getTotalEmployees())
					.as("PAY_API_002: 總員工數應被計算")
					.isGreaterThanOrEqualTo(0);
		}
	}

	/**
	 * PAY_API_003: 送審薪資批次
	 */
	@Nested
	@DisplayName("薪資批次送審 API")
	class PayrollSubmissionApiTests {

		@Test
		@DisplayName("PAY_API_003: 送審薪資批次 - 應從 COMPLETED 轉為 SUBMITTED")
		void PAY_API_003_submitPayrollRun_ShouldTransitionToSubmitted() throws Exception {
			// Given: 使用已計算完成的批次 RUN-004 (CALCULATING → COMPLETED 狀態)
			// 從測試資料中獲取
			String runId = "RUN-004";

			// When: 執行送審操作
			PayrollRunActionRequest submitReq = new PayrollRunActionRequest();
			submitReq.setRunId(runId);
			var submitResponse = performPut("/api/v1/payroll-runs/" + runId + "/submit", submitReq)
					.andExpect(status().isOk())
					.andReturn();

			// Then: 驗證狀態轉換和回應
			String submitResponseBody = submitResponse.getResponse().getContentAsString();
			PayrollRunResponse submittedRun = objectMapper.readValue(submitResponseBody, PayrollRunResponse.class);

			assertThat(submittedRun.getStatus())
					.as("PAY_API_003: 送審後狀態應為 SUBMITTED")
					.isEqualTo(PayrollRunStatus.SUBMITTED.toString());

			assertThat(submittedRun.getCompletedAt())
					.as("PAY_API_003: 完成時間應被保留")
					.isNotNull();
		}
	}

	/**
	 * PAY_API_004: 核准薪資批次
	 */
	@Nested
	@DisplayName("薪資批次核准 API")
	class PayrollApprovalApiTests {

		@Test
		@DisplayName("PAY_API_004: 核准薪資批次 - 應從 SUBMITTED 轉為 APPROVED")
		void PAY_API_004_approvePayrollRun_ShouldTransitionToApproved() throws Exception {
			// Given: 使用已送審的批次 RUN-007 (SUBMITTED 狀態)
			String runId = "RUN-007";

			// When: 執行核准操作
			PayrollRunActionRequest approveReq = new PayrollRunActionRequest();
			approveReq.setRunId(runId);
			var approveResponse = performPut("/api/v1/payroll-runs/" + runId + "/approve", approveReq)
					.andExpect(status().isOk())
					.andReturn();

			// Then: 驗證狀態轉換和回應
			String approveResponseBody = approveResponse.getResponse().getContentAsString();
			PayrollRunResponse approvedRun = objectMapper.readValue(approveResponseBody, PayrollRunResponse.class);

			assertThat(approvedRun.getStatus())
					.as("PAY_API_004: 核准後狀態應為 APPROVED")
					.isEqualTo(PayrollRunStatus.APPROVED.toString());

			assertThat(approvedRun.getApprovedAt())
					.as("PAY_API_004: 核准時間應被記錄")
					.isNotNull();

			// 驗證先前的時間戳記被保留
			assertThat(approvedRun.getCompletedAt())
					.as("PAY_API_004: 完成時間應被保留")
					.isNotNull();
		}
	}

	/**
	 * PAY_API_005: 退回薪資批次
	 */
	@Nested
	@DisplayName("薪資批次退回 API")
	class PayrollRejectionApiTests {

		@Test
		@DisplayName("PAY_API_005: 退回薪資批次 - 應從 SUBMITTED 轉回 COMPLETED")
		void PAY_API_005_rejectPayrollRun_ShouldTransitionToCompleted() throws Exception {
			// Given: 使用已送審的批次 RUN-008 (SUBMITTED 狀態)
			String runId = "RUN-008";
			String rejectReason = "資料核對有誤，需要重新計算";

			// When: 執行退回操作
			PayrollRunActionRequest rejectReq = new PayrollRunActionRequest();
			rejectReq.setRunId(runId);
			rejectReq.setReason(rejectReason);
			var rejectResponse = performPut("/api/v1/payroll-runs/" + runId + "/reject", rejectReq)
					.andExpect(status().isOk())
					.andReturn();

			// Then: 驗證狀態轉換和回應
			String rejectResponseBody = rejectResponse.getResponse().getContentAsString();
			PayrollRunResponse rejectedRun = objectMapper.readValue(rejectResponseBody, PayrollRunResponse.class);

			assertThat(rejectedRun.getStatus())
					.as("PAY_API_005: 退回後狀態應轉回 COMPLETED")
					.isEqualTo(PayrollRunStatus.COMPLETED.toString());

			// 注意：根據業務邏輯，退回原因可能被保存在其他欄位
			// 這裡驗證操作成功執行
			assertThat(rejectedRun.getRunId())
					.as("PAY_API_005: 批次 ID 應保持不變")
					.isEqualTo(runId);
		}
	}

	/**
	 * PAY_API_006: 標記已發薪
	 */
	@Nested
	@DisplayName("薪資發放 API")
	class PayrollPaymentApiTests {

		@Test
		@DisplayName("PAY_API_006: 標記已發薪 - 應從 APPROVED 轉為 PAID")
		void PAY_API_006_markPayrollRunPaid_ShouldTransitionToPaid() throws Exception {
			// Given: 使用已核准的批次 RUN-009 (APPROVED 狀態)
			String runId = "RUN-009";
			String bankFileUrl = "/bank/transfers/RUN-009-20250203.txt";

			// When: 執行標記已發薪操作
			PayrollRunActionRequest payReq = new PayrollRunActionRequest();
			payReq.setRunId(runId);
			payReq.setBankFileUrl(bankFileUrl);
			var payResponse = performPut("/api/v1/payroll-runs/" + runId + "/pay", payReq)
					.andExpect(status().isOk())
					.andReturn();

			// Then: 驗證狀態轉換和回應
			String payResponseBody = payResponse.getResponse().getContentAsString();
			PayrollRunResponse paidRun = objectMapper.readValue(payResponseBody, PayrollRunResponse.class);

			assertThat(paidRun.getStatus())
					.as("PAY_API_006: 發薪後狀態應為 PAID")
					.isEqualTo(PayrollRunStatus.PAID.toString());

			assertThat(paidRun.getPaidAt())
					.as("PAY_API_006: 發薪時間應被記錄")
					.isNotNull();

			// 驗證先前的時間戳記被保留
			assertThat(paidRun.getApprovedAt())
					.as("PAY_API_006: 核准時間應被保留")
					.isNotNull();
		}
	}

	@Test
	@DisplayName("PAY_API_010: 完整薪資批次生命週期 - DRAFT → PAID")
	void PAY_API_010_completePayrollRunLifecycle_ShouldSucceed() throws Exception {
		// Step 1: 建立薪資批次 (DRAFT)
		StartPayrollRunRequest createReq = new StartPayrollRunRequest();
		createReq.setOrganizationId("ORG-001");
		createReq.setPayrollSystem("MONTHLY");
		createReq.setStartDate(LocalDate.of(2025, 1, 1));
		createReq.setEndDate(LocalDate.of(2025, 1, 31));
		createReq.setPayDate(LocalDate.of(2025, 2, 5));
		createReq.setName("完整生命週期測試批次");

		var createResponse = performPost("/api/v1/payroll-runs", createReq)
				.andExpect(status().isOk())
				.andReturn();

		String createResponseBody = createResponse.getResponse().getContentAsString();
		PayrollRunResponse createdRun = objectMapper.readValue(createResponseBody, PayrollRunResponse.class);
		String runId = createdRun.getRunId();

		assertThat(createdRun.getStatus())
				.as("Step 1: 新建立的批次狀態應為 DRAFT")
				.isEqualTo(PayrollRunStatus.DRAFT.toString());

		// Step 2: 執行薪資計算 (DRAFT → CALCULATING → COMPLETED)
		CalculatePayrollRequest calcReq = new CalculatePayrollRequest();
		var calcResponse = performPost("/api/v1/payroll-runs/" + runId + "/execute", calcReq)
				.andExpect(status().isOk())
				.andReturn();

		String calcResponseBody = calcResponse.getResponse().getContentAsString();
		PayrollRunResponse calculatedRun = objectMapper.readValue(calcResponseBody, PayrollRunResponse.class);

		assertThat(calculatedRun.getStatus())
				.as("Step 2: 計算完成後狀態應為 COMPLETED")
				.isEqualTo(PayrollRunStatus.COMPLETED.toString());

		assertThat(calculatedRun.getExecutedAt())
				.as("Step 2: executedAt 時間應被記錄")
				.isNotNull();

		// Step 3: 送審薪資批次 (COMPLETED → SUBMITTED)
		PayrollRunActionRequest submitReq = new PayrollRunActionRequest();
		submitReq.setRunId(runId);
		var submitResponse = performPut("/api/v1/payroll-runs/" + runId + "/submit", submitReq)
				.andExpect(status().isOk())
				.andReturn();

		String submitResponseBody = submitResponse.getResponse().getContentAsString();
		PayrollRunResponse submittedRun = objectMapper.readValue(submitResponseBody, PayrollRunResponse.class);

		assertThat(submittedRun.getStatus())
				.as("Step 3: 送審後狀態應為 SUBMITTED")
				.isEqualTo(PayrollRunStatus.SUBMITTED.toString());

		// Step 4: 核准薪資批次 (SUBMITTED → APPROVED)
		PayrollRunActionRequest approveReq = new PayrollRunActionRequest();
		approveReq.setRunId(runId);
		var approveResponse = performPut("/api/v1/payroll-runs/" + runId + "/approve", approveReq)
				.andExpect(status().isOk())
				.andReturn();

		String approveResponseBody = approveResponse.getResponse().getContentAsString();
		PayrollRunResponse approvedRun = objectMapper.readValue(approveResponseBody, PayrollRunResponse.class);

		assertThat(approvedRun.getStatus())
				.as("Step 4: 核准後狀態應為 APPROVED")
				.isEqualTo(PayrollRunStatus.APPROVED.toString());

		assertThat(approvedRun.getApprovedAt())
				.as("Step 4: approvedAt 時間應被記錄")
				.isNotNull();

		// Step 5: 標記已發薪 (APPROVED → PAID)
		PayrollRunActionRequest payReq = new PayrollRunActionRequest();
		payReq.setRunId(runId);
		payReq.setBankFileUrl("/bank/transfers/lifecycle-test-" + runId + ".txt");
		var payResponse = performPut("/api/v1/payroll-runs/" + runId + "/pay", payReq)
				.andExpect(status().isOk())
				.andReturn();

		String payResponseBody = payResponse.getResponse().getContentAsString();
		PayrollRunResponse paidRun = objectMapper.readValue(payResponseBody, PayrollRunResponse.class);

		// 驗證最終狀態和完整的時間戳記
		assertThat(paidRun.getStatus())
				.as("Step 5: 發薪後狀態應為 PAID")
				.isEqualTo(PayrollRunStatus.PAID.toString());

		assertThat(paidRun.getPaidAt())
				.as("Step 5: paidAt 時間應被記錄")
				.isNotNull();

		// 驗證完整的生命週期追蹤 - 所有時間戳記應被保留
		assertThat(paidRun.getExecutedAt())
				.as("完整流程: executedAt 時間應被保留")
				.isNotNull();

		assertThat(paidRun.getApprovedAt())
				.as("完整流程: approvedAt 時間應被保留")
				.isNotNull();

		assertThat(paidRun.getCompletedAt())
				.as("完整流程: completedAt 時間應被保留")
				.isNotNull();
	}

	/**
	 * 異常情況測試
	 */
	@Nested
	@DisplayName("異常情況處理")
	class ExceptionHandlingTests {

		@Test
		@DisplayName("應拒絕無效的組織 ID")
		void shouldRejectInvalidOrganizationId() throws Exception {
			// Given
			StartPayrollRunRequest request = new StartPayrollRunRequest();
			request.setOrganizationId(null);
			request.setPayrollSystem("MONTHLY");
			request.setStartDate(LocalDate.of(2025, 12, 1));
			request.setEndDate(LocalDate.of(2025, 12, 31));
			request.setPayDate(LocalDate.of(2026, 1, 5));
			request.setName("Test Payroll");

			// When & Then
			performPost("/api/v1/payroll-runs", request)
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("應拒絕發薪日早於計薪期間結束日期的請求")
		void shouldRejectPayDateBeforePeriodEnd() throws Exception {
			// Given
			StartPayrollRunRequest request = new StartPayrollRunRequest();
			request.setOrganizationId("ORG-001");
			request.setPayrollSystem("MONTHLY");
			request.setStartDate(LocalDate.of(2025, 12, 1));
			request.setEndDate(LocalDate.of(2025, 12, 31));
			request.setPayDate(LocalDate.of(2025, 12, 10)); // 早於 12/31
			request.setName("Test Payroll");

			// When & Then
			performPost("/api/v1/payroll-runs", request)
					.andExpect(status().isBadRequest());
		}
	}
}
