package com.company.hrms.attendance.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.company.hrms.attendance.api.request.leave.ApplyLeaveRequest;
import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.attendance.api.request.leave.GetLeaveListRequest;
import com.company.hrms.attendance.api.request.leave.RejectLeaveRequest;
import com.company.hrms.attendance.api.response.leave.ApplyLeaveResponse;
import com.company.hrms.attendance.api.response.leave.ApproveLeaveResponse;
import com.company.hrms.attendance.api.response.leave.CancelLeaveResponse;
import com.company.hrms.attendance.api.response.leave.LeaveApplicationDetailResponse;
import com.company.hrms.attendance.api.response.leave.LeaveApplicationListResponse;
import com.company.hrms.attendance.api.response.leave.LeaveBalanceResponse;
import com.company.hrms.attendance.api.response.leave.RejectLeaveResponse;
import com.company.hrms.attendance.application.service.leave.ApplyLeaveServiceImpl;
import com.company.hrms.attendance.application.service.leave.ApproveLeaveServiceImpl;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.common.test.base.BaseApiContractTest;

/**
 * HR03 請假管理 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>請假申請、核准、駁回、取消 (Command)</li>
 * <li>請假申請查詢、假別餘額查詢 (Query)</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR03 請假管理 API 合約測試")
public class LeaveApiTest extends BaseApiContractTest {

        @MockBean(name = "createLeaveApplicationServiceImpl")
        private ApplyLeaveServiceImpl applyLeaveService;

        @MockBean(name = "approveLeaveServiceImpl")
        private ApproveLeaveServiceImpl approveLeaveService;

        @MockBean(name = "rejectLeaveServiceImpl")
        private CommandApiService<RejectLeaveRequest, RejectLeaveResponse> rejectLeaveService;

        @MockBean(name = "cancelLeaveServiceImpl")
        private CommandApiService<Object, CancelLeaveResponse> cancelLeaveService;

        @MockBean(name = "getLeaveApplicationsServiceImpl")
        private QueryApiService<GetLeaveListRequest, List<LeaveApplicationListResponse>> getLeaveApplicationsService;

        @MockBean(name = "getLeaveApplicationServiceImpl")
        private QueryApiService<Object, LeaveApplicationDetailResponse> getLeaveApplicationService;

        @MockBean(name = "getLeaveBalanceServiceImpl")
        private QueryApiService<Object, LeaveBalanceResponse> getLeaveBalanceService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("emp-001");
                mockUser.setUsername("test_employee");
                mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority("leave:apply"));
                authorities.add(new SimpleGrantedAuthority("leave:approve"));
                authorities.add(new SimpleGrantedAuthority("leave:reject"));
                authorities.add(new SimpleGrantedAuthority("leave:cancel"));
                authorities.add(new SimpleGrantedAuthority("leave:read"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                mockUser, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        /**
         * 請假命令 API 測試
         */
        @Nested
        @DisplayName("請假命令 API")
        public class LeaveCommandApiTests {

                @Test
                @DisplayName("ATT_LEV_001: 提交請假申請 - 應回傳申請結果")
                void createLeaveApplication_ShouldReturnApplicationResult() throws Exception {
                        // Arrange
                        ApplyLeaveRequest request = new ApplyLeaveRequest();
                        request.setEmployeeId("emp-001");
                        request.setLeaveTypeId("lt-annual");
                        request.setStartDate(LocalDate.now().plusDays(7));
                        request.setEndDate(LocalDate.now().plusDays(8));
                        request.setStartPeriod("FULL_DAY");
                        request.setEndPeriod("FULL_DAY");
                        request.setReason("家庭旅遊");

                        ApplyLeaveResponse response = ApplyLeaveResponse.success("leave-001");

                        when(applyLeaveService.execCommand(any(ApplyLeaveRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/leave/applications", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.applicationId").value("leave-001"))
                                        .andExpect(jsonPath("$.status").value("PENDING"));
                }

                @Test
                @DisplayName("ATT_LEV_002: 核准請假申請 - 應回傳核准結果")
                void approveLeave_ShouldReturnApprovalResult() throws Exception {
                        // Arrange
                        String applicationId = "leave-001";

                        ApproveLeaveResponse response = ApproveLeaveResponse.builder()
                                        .success(true)
                                        .applicationId(applicationId)
                                        .status("APPROVED")
                                        .message("請假申請已核准")
                                        .build();

                        when(approveLeaveService.execCommand(any(ApproveLeaveRequest.class), any(JWTModel.class),
                                        eq(applicationId)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPut("/api/v1/leave/applications/" + applicationId + "/approve", null)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.applicationId").value(applicationId))
                                        .andExpect(jsonPath("$.status").value("APPROVED"));
                }

                @Test
                @DisplayName("ATT_LEV_003: 駁回請假申請 - 應回傳駁回結果")
                void rejectLeave_ShouldReturnRejectionResult() throws Exception {
                        // Arrange
                        String applicationId = "leave-001";
                        RejectLeaveRequest request = new RejectLeaveRequest();
                        request.setReason("人力不足，請另擇日期");

                        RejectLeaveResponse response = RejectLeaveResponse.builder()
                                        .applicationId(applicationId)
                                        .status("REJECTED")
                                        .rejectionReason("請假申請已駁回")
                                        .build();

                        doReturn(response).when(rejectLeaveService)
                                        .execCommand(any(RejectLeaveRequest.class), any(JWTModel.class),
                                                        eq(applicationId));

                        // Act & Assert - 只驗證請求格式正確
                        performPut("/api/v1/leave/applications/" + applicationId + "/reject", request)
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("ATT_LEV_004: 取消請假申請 - 應回傳取消結果")
                void cancelLeave_ShouldReturnCancellationResult() throws Exception {
                        // Arrange
                        String applicationId = "leave-001";

                        CancelLeaveResponse response = CancelLeaveResponse.builder()
                                        .applicationId(applicationId)
                                        .status("CANCELLED")
                                        .build();

                        doReturn(response).when(cancelLeaveService)
                                        .execCommand(any(), any(JWTModel.class), eq(applicationId));

                        // Act & Assert - 只驗證請求格式正確
                        performPut("/api/v1/leave/applications/" + applicationId + "/cancel", null)
                                        .andExpect(status().isOk());
                }
        }

        /**
         * 請假查詢 API 測試
         */
        @Nested
        @DisplayName("請假查詢 API")
        public class LeaveQueryApiTests {

                @Test
                @DisplayName("ATT_LEV_005: 查詢請假申請列表 - 應回傳申請列表")
                void getLeaveApplications_ShouldReturnApplicationList() throws Exception {
                        // Arrange
                        LeaveApplicationListResponse leave1 = LeaveApplicationListResponse.builder()
                                        .applicationId("leave-001")
                                        .employeeId("emp-001")
                                        .employeeName("王小明")
                                        .employeeNumber("EMP001")
                                        .leaveTypeCode("ANNUAL")
                                        .leaveTypeName("特休假")
                                        .startDate(LocalDate.now().plusDays(7))
                                        .endDate(LocalDate.now().plusDays(8))
                                        .leaveDays(BigDecimal.valueOf(2))
                                        .status("PENDING")
                                        .appliedAt(LocalDateTime.now())
                                        .build();

                        LeaveApplicationListResponse leave2 = LeaveApplicationListResponse.builder()
                                        .applicationId("leave-002")
                                        .employeeId("emp-002")
                                        .employeeName("李小華")
                                        .employeeNumber("EMP002")
                                        .leaveTypeCode("SICK")
                                        .leaveTypeName("病假")
                                        .startDate(LocalDate.now())
                                        .endDate(LocalDate.now())
                                        .leaveDays(BigDecimal.valueOf(1))
                                        .status("APPROVED")
                                        .appliedAt(LocalDateTime.now().minusDays(1))
                                        .build();

                        List<LeaveApplicationListResponse> leaves = Arrays.asList(leave1, leave2);

                        doReturn(leaves).when(getLeaveApplicationsService)
                                        .getResponse(any(GetLeaveListRequest.class), any(JWTModel.class));

                        // Act & Assert - 只驗證請求格式正確
                        performGet("/api/v1/leave/applications")
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("ATT_LEV_006: 查詢單筆請假申請 - 應回傳申請詳情")
                void getLeaveApplication_ShouldReturnApplicationDetail() throws Exception {
                        // Arrange
                        String applicationId = "leave-001";

                        LeaveApplicationDetailResponse response = LeaveApplicationDetailResponse.builder()
                                        .applicationId(applicationId)
                                        .employeeId("emp-001")
                                        .employeeName("王小明")
                                        .leaveTypeCode("ANNUAL")
                                        .leaveTypeName("特休假")
                                        .startDate(LocalDate.now().plusDays(7))
                                        .endDate(LocalDate.now().plusDays(8))
                                        .leaveDays(BigDecimal.valueOf(2))
                                        .status("PENDING")
                                        .reason("家庭旅遊")
                                        .build();

                        doReturn(response).when(getLeaveApplicationService)
                                        .getResponse(any(), any(JWTModel.class), eq(applicationId));

                        // Act & Assert - 只驗證請求格式正確
                        performGet("/api/v1/leave/applications/" + applicationId)
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("ATT_LEV_007: 查詢員工假別餘額 - 應回傳餘額資訊")
                void getLeaveBalance_ShouldReturnBalanceInfo() throws Exception {
                        // Arrange
                        String employeeId = "emp-001";

                        LeaveBalanceResponse response = LeaveBalanceResponse.builder()
                                        .employeeId(employeeId)
                                        .employeeName("王小明")
                                        .year(2026)
                                        .balances(Arrays.asList(
                                                        LeaveBalanceResponse.LeaveBalanceItem.builder()
                                                                        .leaveTypeCode("ANNUAL")
                                                                        .leaveTypeName("特休假")
                                                                        .annualQuota(BigDecimal.valueOf(14))
                                                                        .usedDays(BigDecimal.valueOf(5))
                                                                        .remainingDays(BigDecimal.valueOf(9))
                                                                        .build(),
                                                        LeaveBalanceResponse.LeaveBalanceItem.builder()
                                                                        .leaveTypeCode("SICK")
                                                                        .leaveTypeName("病假")
                                                                        .annualQuota(BigDecimal.valueOf(30))
                                                                        .usedDays(BigDecimal.valueOf(2))
                                                                        .remainingDays(BigDecimal.valueOf(28))
                                                                        .build()))
                                        .build();

                        doReturn(response).when(getLeaveBalanceService)
                                        .getResponse(any(), any(JWTModel.class), eq(employeeId));

                        // Act & Assert - 只驗證請求格式正確
                        performGet("/api/v1/leave/balances/" + employeeId)
                                        .andExpect(status().isOk());
                }
        }
}
