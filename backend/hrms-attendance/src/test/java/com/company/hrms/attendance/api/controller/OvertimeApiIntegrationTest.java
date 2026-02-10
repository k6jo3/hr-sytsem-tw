package com.company.hrms.attendance.api.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.overtime.ApplyOvertimeRequest;
import com.company.hrms.attendance.api.request.overtime.ApproveOvertimeRequest;
import com.company.hrms.attendance.api.request.overtime.RejectOvertimeRequest;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiIntegrationTest;

/**
 * 加班 API 整合測試
 * 基於合約: attendance_contracts_v2.md
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@Sql(scripts = {
		"classpath:test-data/cleanup.sql",
		"classpath:test-data/attendance_base_data.sql",
		"classpath:test-data/overtime_test_data.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("加班 API 整合測試")
public class OvertimeApiIntegrationTest extends BaseApiIntegrationTest {

	@BeforeEach
	void setupSecurity() {
		JWTModel mockUser = new JWTModel();
		mockUser.setUserId("test-emp-001");
		mockUser.setUsername("test-user");
		mockUser.setTenantId("test-tenant");
		mockUser.setRoles(Collections.singletonList("HR"));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				mockUser,
				null,
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_HR")));

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * 查詢操作測試 (Query Contracts)
	 * 對應合約: 2.3 加班申請查詢
	 */
	@Nested
	@DisplayName("查詢操作測試")
	class OvertimeQueryApiTests {

		@Test
		@DisplayName("ATT_QRY_O001: 查詢待審核加班 - 應只返回 PENDING 狀態")
		void ATT_QRY_O001_filterByStatusPending_ShouldReturnOnlyPending() throws Exception {
			performGet("/api/v1/overtime/applications?status=PENDING")
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.items").isArray())
					.andExpect(jsonPath("$.items[0].status").value("PENDING"));
		}

		@Test
		@DisplayName("ATT_QRY_O002: 查詢已核准加班 - 應只返回 APPROVED 狀態")
		void ATT_QRY_O002_filterByStatusApproved_ShouldReturnOnlyApproved() throws Exception {
			performGet("/api/v1/overtime/applications?status=APPROVED")
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.items").isArray())
					.andExpect(jsonPath("$.items[0].status").value("APPROVED"));
		}

		@Test
		@DisplayName("ATT_QRY_O003: 依員工查詢加班 - 應返回該員工的所有加班")
		void ATT_QRY_O003_filterByEmployee_ShouldReturnEmployeeOvertimes() throws Exception {
			performGet("/api/v1/overtime/applications?employeeId=test-emp-001")
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.items").isArray())
					.andExpect(jsonPath("$.items[0].employeeId").value("test-emp-001"));
		}

		@Test
		@DisplayName("ATT_QRY_O004: 依加班類型查詢 - 應只返回 WORKDAY 類型")
		void ATT_QRY_O004_filterByTypeWorkday_ShouldReturnWorkdayOnly() throws Exception {
			performGet("/api/v1/overtime/applications?overtimeType=WORKDAY")
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.items").isArray())
					.andExpect(jsonPath("$.items[0].overtimeType").value("WORKDAY"));
		}

		@Test
		@DisplayName("ATT_QRY_O008: 依日期範圍查詢加班 - 應返回指定日期後的加班")
		void ATT_QRY_O008_filterByDateRange_ShouldReturnFiltered() throws Exception {
			performGet("/api/v1/overtime/applications?startDate=2026-02-11")
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.items").isArray());
		}

		@Test
		@DisplayName("查詢加班申請詳情 - 應返回完整資訊")
		void getOvertimeDetail_ShouldReturnDetail() throws Exception {
			String overtimeId = "test-overtime-001";
			performGet("/api/v1/overtime/applications/" + overtimeId)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.applicationId").value(overtimeId))
					.andExpect(jsonPath("$.employeeId").exists())
					.andExpect(jsonPath("$.overtimeDate").exists());
		}

		@Test
		@DisplayName("查詢不存在的加班申請 - 應返回 404")
		void getOvertimeDetail_NotFound_ShouldReturn404() throws Exception {
			performGet("/api/v1/overtime/applications/non-existent-overtime")
					.andExpect(status().isNotFound());
		}
	}

	/**
	 * 命令操作測試 (Command Contracts)
	 * 對應合約: 3.3 加班操作
	 */
	@Nested
	@DisplayName("命令操作測試")
	class OvertimeCmdApiTests {

		@Test
		@DisplayName("ATT_CMD_009: 員工申請平日加班 - 應返回申請 ID")
		void ATT_CMD_009_applyWorkdayOvertime_ShouldReturnId() throws Exception {
			ApplyOvertimeRequest request = new ApplyOvertimeRequest();
			request.setEmployeeId("test-emp-001");
			request.setDate(LocalDate.of(2026, 2, 25));
			request.setHours(2.0);
			request.setOvertimeType("WORKDAY");
			request.setReason("Emergency Fix");

			var response = performPost("/api/v1/overtime/applications", request)
					.andExpect(status().isOk())
					.andReturn();

			String responseBody = response.getResponse().getContentAsString();
			assertThat(responseBody).contains("applicationId");
			assertThat(responseBody).contains("success");
		}

		@Test
		@DisplayName("ATT_CMD_010: 員工申請休息日加班 - 應返回申請 ID")
		void ATT_CMD_010_applyRestDayOvertime_ShouldReturnId() throws Exception {
			ApplyOvertimeRequest request = new ApplyOvertimeRequest();
			request.setEmployeeId("test-emp-001");
			request.setDate(LocalDate.of(2026, 2, 16));
			request.setHours(8.0);
			request.setOvertimeType("REST_DAY");
			request.setReason("Weekend Support");

			performPost("/api/v1/overtime/applications", request)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.applicationId").exists());
		}

		@Test
		@DisplayName("ATT_CMD_011: 主管核准加班 - 應更新狀態為 APPROVED")
		void ATT_CMD_011_approveOvertime_ShouldUpdateStatus() throws Exception {
			String overtimeId = "test-overtime-001";
			ApproveOvertimeRequest request = new ApproveOvertimeRequest();
			request.setComment("Looks good");

			performPut("/api/v1/overtime/applications/" + overtimeId + "/approve", request)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.overtimeId").value(overtimeId))
					.andExpect(jsonPath("$.status").value("APPROVED"));
		}

		@Test
		@DisplayName("ATT_CMD_012: 主管駁回加班 - 應更新狀態為 REJECTED")
		void ATT_CMD_012_rejectOvertime_ShouldUpdateStatus() throws Exception {
			String overtimeId = "test-overtime-001";
			RejectOvertimeRequest request = new RejectOvertimeRequest();
			request.setReason("Not needed");

			performPut("/api/v1/overtime/applications/" + overtimeId + "/reject", request)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.overtimeId").value(overtimeId))
					.andExpect(jsonPath("$.status").value("REJECTED"));
		}
	}

	/**
	 * 異常處理測試
	 */
	@Nested
	@DisplayName("異常處理測試")
	class OvertimeExceptionTests {

		@Test
		@DisplayName("駁回加班時未提供原因 - 應返回 400")
		void rejectOvertime_MissingReason_ShouldReturn400() throws Exception {
			String overtimeId = "test-overtime-001";
			RejectOvertimeRequest request = new RejectOvertimeRequest();
			// Missing reason

			performPut("/api/v1/overtime/applications/" + overtimeId + "/reject", request)
					.andExpect(status().isBadRequest());
		}
	}
}
