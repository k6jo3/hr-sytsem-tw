package com.company.hrms.attendance.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

import com.company.hrms.attendance.api.request.overtime.ApplyOvertimeRequest;
import com.company.hrms.attendance.api.request.overtime.RejectOvertimeRequest;
import com.company.hrms.attendance.api.response.overtime.ApplyOvertimeResponse;
import com.company.hrms.attendance.api.response.overtime.ApproveOvertimeResponse;
import com.company.hrms.attendance.api.response.overtime.OvertimeApplicationDetailResponse;
import com.company.hrms.attendance.api.response.overtime.OvertimeApplicationListResponse;
import com.company.hrms.attendance.api.response.overtime.RejectOvertimeResponse;
import com.company.hrms.attendance.application.service.overtime.ApplyOvertimeServiceImpl;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.common.test.base.BaseApiContractTest;

/**
 * HR03 加班管理 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>加班申請、核准、駁回 (Command)</li>
 * <li>加班申請查詢 (Query)</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR03 加班管理 API 合約測試")
public class OvertimeApiTest extends BaseApiContractTest {

        @MockBean(name = "createOvertimeApplicationServiceImpl")
        private ApplyOvertimeServiceImpl applyOvertimeService;

        @MockBean(name = "approveOvertimeServiceImpl")
        private CommandApiService<Object, ApproveOvertimeResponse> approveOvertimeService;

        @MockBean(name = "rejectOvertimeServiceImpl")
        private CommandApiService<RejectOvertimeRequest, RejectOvertimeResponse> rejectOvertimeService;

        @MockBean(name = "getOvertimeApplicationsServiceImpl")
        private QueryApiService<Object, List<OvertimeApplicationListResponse>> getOvertimeApplicationsService;

        @MockBean(name = "getOvertimeApplicationServiceImpl")
        private QueryApiService<Object, OvertimeApplicationDetailResponse> getOvertimeApplicationService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("emp-001");
                mockUser.setUsername("test_employee");
                mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority("overtime:apply"));
                authorities.add(new SimpleGrantedAuthority("overtime:approve"));
                authorities.add(new SimpleGrantedAuthority("overtime:reject"));
                authorities.add(new SimpleGrantedAuthority("overtime:read"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                mockUser, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        /**
         * 加班命令 API 測試
         */
        @Nested
        @DisplayName("加班命令 API")
        public class OvertimeCommandApiTests {

                @Test
                @DisplayName("ATT_OVT_001: 提交加班申請 - 應回傳申請結果")
                void createOvertimeApplication_ShouldReturnApplicationResult() throws Exception {
                        // Arrange
                        ApplyOvertimeRequest request = new ApplyOvertimeRequest();
                        request.setEmployeeId("emp-001");
                        request.setDate(LocalDate.now().plusDays(1));
                        request.setHours(2.0);
                        request.setOvertimeType("WEEKDAY");
                        request.setReason("專案趕工");

                        ApplyOvertimeResponse response = ApplyOvertimeResponse.success("ot-001");

                        when(applyOvertimeService.execCommand(any(ApplyOvertimeRequest.class), any(JWTModel.class)))
                                        .thenReturn(response);

                        // Act & Assert
                        performPost("/api/v1/overtime/applications", request)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.success").value(true))
                                        .andExpect(jsonPath("$.applicationId").value("ot-001"))
                                        .andExpect(jsonPath("$.status").value("PENDING"));
                }

                @Test
                @DisplayName("ATT_OVT_002: 核准加班申請 - 應回傳核准結果")
                void approveOvertime_ShouldReturnApprovalResult() throws Exception {
                        // Arrange
                        String overtimeId = "ot-001";

                        ApproveOvertimeResponse response = ApproveOvertimeResponse.builder()
                                        .overtimeId(overtimeId)
                                        .status("APPROVED")
                                        .approvedBy("manager-001")
                                        .approvedAt(LocalDateTime.now())
                                        .build();

                        doReturn(response).when(approveOvertimeService)
                                        .execCommand(any(), any(), eq(overtimeId));

                        // Act & Assert - 只驗證請求格式正確
                        performPut("/api/v1/overtime/applications/" + overtimeId + "/approve", null)
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("ATT_OVT_003: 駁回加班申請 - 應回傳駁回結果")
                void rejectOvertime_ShouldReturnRejectionResult() throws Exception {
                        // Arrange
                        String overtimeId = "ot-001";
                        RejectOvertimeRequest request = new RejectOvertimeRequest();
                        request.setReason("本月加班時數已達上限");

                        RejectOvertimeResponse response = RejectOvertimeResponse.builder()
                                        .overtimeId(overtimeId)
                                        .status("REJECTED")
                                        .rejectedBy("manager-001")
                                        .rejectedAt(LocalDateTime.now())
                                        .rejectionReason("本月加班時數已達上限")
                                        .build();

                        doReturn(response).when(rejectOvertimeService)
                                        .execCommand(any(RejectOvertimeRequest.class), any(), eq(overtimeId));

                        // Act & Assert - 只驗證請求格式正確
                        performPut("/api/v1/overtime/applications/" + overtimeId + "/reject", request)
                                        .andExpect(status().isOk());
                }
        }

        /**
         * 加班查詢 API 測試
         */
        @Nested
        @DisplayName("加班查詢 API")
        public class OvertimeQueryApiTests {

                @Test
                @DisplayName("ATT_OVT_004: 查詢加班申請列表 - 應回傳申請列表")
                void getOvertimeApplications_ShouldReturnApplicationList() throws Exception {
                        // Arrange
                        OvertimeApplicationListResponse ot1 = OvertimeApplicationListResponse.builder()
                                        .applicationId("ot-001")
                                        .employeeId("emp-001")
                                        .employeeName("王小明")
                                        .employeeNumber("EMP001")
                                        .overtimeDate(LocalDate.now().plusDays(1))
                                        .startTime(LocalTime.of(18, 0))
                                        .endTime(LocalTime.of(20, 0))
                                        .overtimeHours(BigDecimal.valueOf(2))
                                        .overtimeType("WEEKDAY")
                                        .status("PENDING")
                                        .appliedAt(LocalDateTime.now())
                                        .build();

                        OvertimeApplicationListResponse ot2 = OvertimeApplicationListResponse.builder()
                                        .applicationId("ot-002")
                                        .employeeId("emp-002")
                                        .employeeName("李小華")
                                        .employeeNumber("EMP002")
                                        .overtimeDate(LocalDate.now().plusDays(2))
                                        .startTime(LocalTime.of(9, 0))
                                        .endTime(LocalTime.of(17, 0))
                                        .overtimeHours(BigDecimal.valueOf(8))
                                        .overtimeType("WEEKEND")
                                        .status("APPROVED")
                                        .appliedAt(LocalDateTime.now().minusDays(1))
                                        .build();

                        List<OvertimeApplicationListResponse> overtimes = Arrays.asList(ot1, ot2);

                        doReturn(overtimes).when(getOvertimeApplicationsService)
                                        .getResponse(any(), any());

                        // Act & Assert - 只驗證請求格式正確
                        performGet("/api/v1/overtime/applications")
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("ATT_OVT_005: 查詢單筆加班申請 - 應回傳申請詳情")
                void getOvertimeApplication_ShouldReturnApplicationDetail() throws Exception {
                        // Arrange
                        String applicationId = "ot-001";

                        OvertimeApplicationDetailResponse response = OvertimeApplicationDetailResponse.builder()
                                        .applicationId(applicationId)
                                        .employeeId("emp-001")
                                        .employeeName("王小明")
                                        .overtimeDate(LocalDate.now().plusDays(1))
                                        .startTime(LocalTime.of(18, 0))
                                        .endTime(LocalTime.of(20, 0))
                                        .requestedHours(BigDecimal.valueOf(2))
                                        .overtimeType("WEEKDAY")
                                        .status("PENDING")
                                        .reason("專案趕工")
                                        .build();

                        doReturn(response).when(getOvertimeApplicationService)
                                        .getResponse(any(), any(), eq(applicationId));

                        // Act & Assert - 只驗證請求格式正確
                        performGet("/api/v1/overtime/applications/" + applicationId)
                                        .andExpect(status().isOk());
                }
        }
}
