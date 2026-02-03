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
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.application.dto.response.PayslipResponse;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Payslip API 整合測試
 * 驗證薪資單相關 API 的功能
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>薪資單計算、定案、發送等操作</li>
 * <li>薪資單狀態管理</li>
 * <li>薪資單查詢功能</li>
 * </ul>
 *
 * <p>
 * TODO: 待實現項目
 * </p>
 * <ul>
 * <li>建立測試資料 SQL 檔案：
 *     <ul>
 *         <li>src/test/resources/test-data/payslip_test_data.sql (30 筆測試薪資單)</li>
 *         <li>src/test/resources/test-data/cleanup.sql (清理腳本)</li>
 *     </ul>
 * </li>
 * <li>實現 Payslip API 端點：
 *     <ul>
 *         <li>POST /api/v1/payroll-runs/{runId}/payslips/send (發送薪資單)</li>
 *         <li>GET /api/v1/payslips (查詢薪資單列表)</li>
 *         <li>GET /api/v1/employees/{employeeId}/payslips (按員工查詢)</li>
 *         <li>PUT /api/v1/payslips/{id}/finalize (定案薪資單)</li>
 *         <li>PUT /api/v1/payslips/{id}/recalculate (重新計算)</li>
 *         <li>GET /api/v1/payslips/{id}/pdf (下載 PDF)</li>
 *     </ul>
 * </li>
 * <li>實現對應的 Service 和 Domain Logic</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/payslip_test_data.sql",
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql",
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("薪資單 API 整合測試")
class PayslipApiIntegrationTest extends BaseApiIntegrationTest {

	@Autowired
	private IPayslipRepository payslipRepository;

	@Autowired
	private ObjectMapper objectMapper;

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

		/**
		 * PAY_SLIP_API_001: 發送薪資單 Email
		 */
		@Test
		@DisplayName("PAY_SLIP_API_001: 批量發送薪資單 Email - 應標記所有薪資單為已發送")
		void PAY_SLIP_API_001_sendPayslipEmail_ShouldMarkAsEmailSent() throws Exception {
			// Given: 薪資批次 ID
			String runId = "RUN-202512";
			PayrollRunActionRequest request = new PayrollRunActionRequest();
			request.setReason("月份薪資單批量發送");

			// TODO: When & Then: 執行發送 API 並驗證響應
			// 預期行為：
			// 1. 返回 200 OK
			// 2. 回應包含發送的薪資單數量
			// 3. 所有薪資單狀態變為 SENT
		}
	}

	/**
	 * 薪資單查詢 API 測試
	 */
	@Nested
	@DisplayName("薪資單查詢 API")
	class PayslipQueryApiTests {

		/**
		 * PAY_SLIP_API_002: 查詢薪資單列表
		 */
		@Test
		@DisplayName("PAY_SLIP_API_002: 查詢薪資單列表 - 應返回分頁結果")
		void PAY_SLIP_API_002_queryPayslipList_ShouldReturnPaginatedResults() throws Exception {
			// TODO: Given: 查詢條件
			// TODO: When: 執行查詢 API
			// TODO: Then: 驗證返回結果包含薪資單列表、分頁信息等
		}

		/**
		 * PAY_SLIP_API_003: 按員工查詢薪資單
		 */
		@Test
		@DisplayName("PAY_SLIP_API_003: 查詢員工薪資單 - 應返回該員工的薪資單")
		void PAY_SLIP_API_003_queryEmployeePayslips_ShouldReturnEmployeePayslips() throws Exception {
			// TODO: Given: 員工 ID
			// TODO: When: 執行查詢 API
			// TODO: Then: 驗證返回的薪資單屬於指定員工
		}
	}

	/**
	 * 薪資單狀態管理測試
	 */
	@Nested
	@DisplayName("薪資單狀態管理")
	class PayslipStatusManagementTests {

		/**
		 * PAY_SLIP_API_004: 定案薪資單
		 */
		@Test
		@DisplayName("PAY_SLIP_API_004: 定案薪資單 - 應將狀態從 DRAFT 變為 FINALIZED")
		void PAY_SLIP_API_004_finalizePayslip_ShouldChangeStatusToFinalized() throws Exception {
			// TODO: Given: 薪資單 ID
			// TODO: When: 執行定案 API
			// TODO: Then: 驗證狀態變為 FINALIZED
		}

		/**
		 * PAY_SLIP_API_005: 重新計算薪資單
		 */
		@Test
		@DisplayName("PAY_SLIP_API_005: 重新計算薪資單 - 應更新薪資金額")
		void PAY_SLIP_API_005_recalculatePayslip_ShouldUpdateSalaryAmounts() throws Exception {
			// TODO: Given: 薪資單 ID 和修正後的薪資數據
			// TODO: When: 執行重新計算 API
			// TODO: Then: 驗證薪資金額已更新，狀態回到 DRAFT
		}
	}

	/**
	 * 薪資單下載與導出測試
	 */
	@Nested
	@DisplayName("薪資單下載與導出")
	class PayslipDownloadAndExportTests {

		/**
		 * PAY_SLIP_API_006: 下載薪資單 PDF
		 */
		@Test
		@DisplayName("PAY_SLIP_API_006: 下載薪資單 PDF - 應返回 PDF 文件")
		void PAY_SLIP_API_006_downloadPayslipPdf_ShouldReturnPdfFile() throws Exception {
			// TODO: Given: 薪資單 ID
			// TODO: When: 執行下載 API
			// TODO: Then: 驗證返回 PDF 文件內容，Content-Type 為 application/pdf
		}
	}

	/**
	 * 完整薪資單流程測試
	 */
	@Test
	@DisplayName("PAY_SLIP_API_010: 薪資單完整流程 - 從建立到發送")
	void PAY_SLIP_API_010_completePayslipWorkflow_ShouldSucceed() throws Exception {
		// TODO: Step 1: 建立薪資批次
		// TODO: Step 2: 執行薪資計算，生成薪資單
		// TODO: Step 3: 定案所有薪資單
		// TODO: Step 4: 發送薪資單 Email
		// TODO: Step 5: 驗證最終狀態和結果
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
			// Given: 不存在的薪資單 ID
			String nonExistentPayslipId = "NONEXISTENT-ID";

			// TODO: When & Then: 查詢不存在的薪資單應返回 404
		}

		@Test
		@DisplayName("應拒絕在非 DRAFT 狀態下修改薪資單")
		void shouldRejectModifyingNonDraftPayslip() throws Exception {
			// Given: 已定案的薪資單 ID
			String finalizedPayslipId = "PAYSLIP-FINALIZED";

			// TODO: When & Then: 嘗試修改應被拒絕並返回 409 Conflict
		}

		@Test
		@DisplayName("應拒絕未授權的發送操作")
		void shouldRejectUnauthorizedSendOperation() throws Exception {
			// Given: 薪資批次 ID，但用戶無權限
			String runId = "RUN-202512";
			PayrollRunActionRequest request = new PayrollRunActionRequest();

			// TODO: When & Then: 無權限的用戶應被拒絕，返回 403 Forbidden
		}
	}
}
