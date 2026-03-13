package com.company.hrms.attendance.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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
import com.company.hrms.attendance.api.response.leave.ApplyLeaveResponse;
import com.company.hrms.attendance.api.response.leave.LeaveApplicationListResponse;
import com.company.hrms.attendance.application.service.leave.ApplyLeaveServiceImpl;
import com.company.hrms.attendance.application.service.leave.GetLeaveApplicationsServiceImpl;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;

/**
 * HR03 請假管理 API 合約測試 (Mock Service 版)
 *
 * <p>
 * 驗證 Controller 層的請求/回應格式，透過 MockBean 隔離下層服務。
 * </p>
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR03 請假管理 API 合約測試")
class LeaveApiTest extends BaseApiContractTest {

    @MockBean(name = "applyLeaveServiceImpl")
    private ApplyLeaveServiceImpl applyLeaveService;

    @MockBean(name = "getLeaveApplicationsServiceImpl")
    private GetLeaveApplicationsServiceImpl getLeaveApplicationsService;

    @BeforeEach
    void setupSecurity() {
        JWTModel mockUser = new JWTModel();
        mockUser.setUserId("test-emp-001");
        mockUser.setUsername("test_employee");
        mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

        List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("leave:apply"));
        authorities.add(new SimpleGrantedAuthority("leave:approve"));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                mockUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 請假命令 API 測試
     */
    @Nested
    @DisplayName("請假命令 API")
    class LeaveCommandApiTests {

        @Test
        @DisplayName("ATT_LEAVE_CMD_001: 新增請假申請 - 應返回申請 ID")
        void createLeaveApplication_ShouldReturnApplicationResult() throws Exception {
            // Arrange
            ApplyLeaveResponse response = ApplyLeaveResponse.success("leave-001");

            when(applyLeaveService.execCommand(any(ApplyLeaveRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            ApplyLeaveRequest request = new ApplyLeaveRequest();
            request.setEmployeeId("test-emp-001");
            request.setLeaveTypeId("ANNUAL");
            request.setStartDate(LocalDate.now().plusDays(1));
            request.setEndDate(LocalDate.now().plusDays(3));
            request.setReason("家庭事務");

            // Act & Assert
            performPost("/api/v1/leave/applications", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.applicationId").value("leave-001"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    /**
     * 請假查詢 API 測試
     */
    @Nested
    @DisplayName("請假查詢 API")
    class LeaveQueryApiTests {

        @Test
        @DisplayName("ATT_LEAVE_QRY_001: 查詢請假申請列表 - 應返回分頁結果")
        void getLeaveApplications_ShouldReturnList() throws Exception {
            // Arrange
            LeaveApplicationListResponse item = LeaveApplicationListResponse.builder()
                    .applicationId("leave-001")
                    .employeeId("test-emp-001")
                    .leaveTypeCode("ANNUAL")
                    .leaveTypeName("特休假")
                    .status("PENDING")
                    .startDate(LocalDate.of(2026, 2, 1))
                    .endDate(LocalDate.of(2026, 2, 3))
                    .build();

            PageResponse<LeaveApplicationListResponse> response = PageResponse.of(
                    List.of(item), 1, 20, 1L);

            when(getLeaveApplicationsService.getResponse(any(), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/leave/applications")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items[0].applicationId").value("leave-001"));
        }
    }
}
