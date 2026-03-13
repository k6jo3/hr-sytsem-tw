package com.company.hrms.attendance.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
import com.company.hrms.attendance.api.response.overtime.ApplyOvertimeResponse;
import com.company.hrms.attendance.api.response.overtime.OvertimeApplicationListResponse;
import com.company.hrms.attendance.application.service.overtime.ApplyOvertimeServiceImpl;
import com.company.hrms.attendance.application.service.overtime.GetOvertimeApplicationsServiceImpl;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;

/**
 * HR03 加班管理 API 合約測試 (Mock Service 版)
 *
 * <p>
 * 驗證 Controller 層的請求/回應格式，透過 MockBean 隔離下層服務。
 * </p>
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR03 加班管理 API 合約測試")
class OvertimeApiTest extends BaseApiContractTest {

    @MockBean(name = "applyOvertimeServiceImpl")
    private ApplyOvertimeServiceImpl applyOvertimeService;

    @MockBean(name = "getOvertimeApplicationsServiceImpl")
    private GetOvertimeApplicationsServiceImpl getOvertimeApplicationsService;

    @BeforeEach
    void setupSecurity() {
        JWTModel mockUser = new JWTModel();
        mockUser.setUserId("test-emp-001");
        mockUser.setUsername("test_employee");
        mockUser.setTenantId("test-tenant");
        mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                mockUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 加班命令 API 測試
     */
    @Nested
    @DisplayName("加班命令 API")
    class OvertimeCommandApiTests {

        @Test
        @DisplayName("ATT_OT_CMD_001: 申請加班 - 應返回申請 ID")
        void createOvertimeApplication_ShouldReturnApplicationResult() throws Exception {
            // Arrange
            ApplyOvertimeResponse response = ApplyOvertimeResponse.success("overtime-001");

            when(applyOvertimeService.execCommand(any(ApplyOvertimeRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            ApplyOvertimeRequest request = new ApplyOvertimeRequest();
            request.setEmployeeId("test-emp-001");
            request.setDate(LocalDate.of(2026, 2, 25));
            request.setHours(2.0);
            request.setOvertimeType("WORKDAY");
            request.setReason("Emergency Fix");

            // Act & Assert
            performPost("/api/v1/overtime/applications", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.applicationId").value("overtime-001"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    /**
     * 加班查詢 API 測試
     */
    @Nested
    @DisplayName("加班查詢 API")
    class OvertimeQueryApiTests {

        @Test
        @DisplayName("ATT_OT_QRY_001: 查詢加班申請列表 - 應返回分頁結果")
        void getOvertimeApplications_ShouldReturnList() throws Exception {
            // Arrange
            OvertimeApplicationListResponse item = OvertimeApplicationListResponse.builder()
                    .applicationId("overtime-001")
                    .employeeId("test-emp-001")
                    .overtimeDate(LocalDate.of(2026, 2, 25))
                    .overtimeType("WORKDAY")
                    .status("PENDING")
                    .build();

            PageResponse<OvertimeApplicationListResponse> response = PageResponse.of(
                    List.of(item), 1, 20, 1L);

            when(getOvertimeApplicationsService.getResponse(any(), any(JWTModel.class), any(String.class), any(String.class)))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/overtime/applications")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items[0].applicationId").value("overtime-001"));
        }
    }
}
