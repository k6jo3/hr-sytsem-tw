package com.company.hrms.payroll.api.controller;

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
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;

/**
 * Payslip API 整合測試
 * 驗證薪資單相關 API 的功能
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
		"classpath:test-data/payroll_run_test_data.sql",
		"classpath:test-data/payslip_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("薪資單 API 整合測試")
class PayslipApiIntegrationTest extends BaseApiIntegrationTest {

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-user-002");
		mockUser.setUsername("test-user");
		mockUser.setRoles(Collections.singletonList("HR"));

		List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.toList();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 薪資單發送 API 測試
	 */
	@Nested
	@DisplayName("薪資單發送 API")
	class PayslipSendingApiTests {

		@Test
		@DisplayName("PAY_SLIP_API_001: 批量發送薪資單 Email - 應成功觸發")
		@Disabled("待解決: Controller 與 Service 對發送單筆 vs 批次的邏輯不一致問題")
		void PAY_SLIP_API_001_sendPayslipEmail_ShouldMarkAsEmailSent() throws Exception {
			String runId = "RUN-004";
			PayrollRunActionRequest request = new PayrollRunActionRequest();
			request.setReason("月份薪資單批量發送");

			performPost("/api/v1/payroll-runs/" + runId + "/send-payslips", request)
					.andExpect(status().isOk());
		}
	}

	/**
	 * 薪資單查詢 API 測試
	 */
	@Nested
	@DisplayName("薪資單查詢 API")
	class PayslipQueryApiTests {

		@Test
		@DisplayName("PAY_SLIP_API_002: 查詢薪資單列表 - 應返回分頁結果")
		void PAY_SLIP_API_002_queryPayslipList_ShouldReturnPaginatedResults() throws Exception {
			String queryUrl = "/api/v1/payslips?page=0&size=10";

			var response = performGet(queryUrl)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("items", "page", "size", "totalElements");
		}

		@Test
		@DisplayName("PAY_SLIP_API_003: 查詢詳情 - 應返回指定薪資單")
		void PAY_SLIP_API_003_queryPayslipDetail_ShouldReturnDetail() throws Exception {
			String payslipId = "SLIP-001";

			performGet("/api/v1/payslips/" + payslipId)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value("SLIP-001"))
					.andExpect(jsonPath("$.employeeName").value("張三"));
		}

		@Test
		@DisplayName("按員工 ID 查詢薪資單列表")
		void queryPayslipsByEmployeeId_ShouldReturnList() throws Exception {
			String employeeId = "E001";

			performGet("/api/v1/payslips?employeeId=" + employeeId)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.items").isArray())
					.andExpect(jsonPath("$.items[0].employeeName").value("張三"));
		}
	}

	/**
	 * 薪資單下載與導出測試
	 */
	@Nested
	@DisplayName("薪資單下載與導出")
	class PayslipDownloadAndExportTests {

		@Test
		@DisplayName("PAY_SLIP_API_006: 下載薪資單 PDF - 應返回 PDF URL")
		void PAY_SLIP_API_006_downloadPayslipPdf_ShouldReturnPdfFile() throws Exception {
			String payslipId = "SLIP-026";
			var response = performGet("/api/v1/payslips/" + payslipId + "/pdf")
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("pdfUrl");
		}
	}

	/**
	 * 異常情況測試
	 */
	@Nested
	@DisplayName("異常情況處理")
	class ExceptionHandlingTests {

		@Test
		@DisplayName("應返回 404 當薪資單不存在")
		void shouldReturn404WhenPayslipNotFound() throws Exception {
			String nonExistentPayslipId = "NONEXISTENT-ID";

			performGet("/api/v1/payslips/" + nonExistentPayslipId)
					.andExpect(status().isNotFound());
		}
	}

	@Test
	@DisplayName("PAY_SLIP_API_010: 薪資單完整流程連通性測試")
	@Disabled("完整流程依賴全系統 Task 協作")
	void PAY_SLIP_API_010_completePayslipWorkflow_ShouldSucceed() throws Exception {
		String runId = "RUN-004";
		PayrollRunActionRequest request = new PayrollRunActionRequest();
		performPost("/api/v1/payroll-runs/" + runId + "/send-payslips", request)
				.andExpect(status().isOk());
	}
}
