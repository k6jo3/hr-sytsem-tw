package com.company.hrms.performance.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.performance.api.request.GetCycleDetailRequest;
import com.company.hrms.performance.api.request.GetCyclesRequest;
import com.company.hrms.performance.api.response.GetCyclesResponse;
import com.company.hrms.performance.application.service.GetCycleDetailServiceImpl;
import com.company.hrms.performance.application.service.GetCyclesServiceImpl;
import com.company.hrms.performance.domain.model.valueobject.CycleType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR08 考核週期查詢 Controller 測試")
class HR08CycleQryControllerTest extends BaseApiContractTest {

        @MockBean(name = "getCyclesServiceImpl")
        private GetCyclesServiceImpl getCyclesService;

        @MockBean(name = "getCycleDetailServiceImpl")
        private GetCycleDetailServiceImpl getCycleDetailService;

        @BeforeEach
        void setupSecurity() {
                JWTModel mockUser = new JWTModel();
                mockUser.setUserId("admin-user");
                mockUser.setUsername("admin");
                mockUser.setRoles(Collections.singletonList("ADMIN"));

                List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockUser,
                                null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        @Test
        @DisplayName("查詢考核週期列表 - 成功")
        void getCycles_ShouldReturnOk() throws Exception {
                // Arrange
                GetCyclesResponse.CycleSummary summary = GetCyclesResponse.CycleSummary.builder()
                                .cycleId("CYCLE-001")
                                .cycleName("2026 Q1 Review")
                                .cycleType(CycleType.QUARTERLY)
                                .build();

                PageResponse<GetCyclesResponse.CycleSummary> response = PageResponse.of(
                                Collections.singletonList(summary), 1, 20, 1);

                when(getCyclesService.getResponse(any(GetCyclesRequest.class), any(JWTModel.class), any()))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/performance/cycles?page=1&size=20")
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("查詢考核週期詳情 - 成功")
        void getCycleDetail_ShouldReturnOk() throws Exception {
                // Arrange
                GetCyclesResponse.CycleSummary response = GetCyclesResponse.CycleSummary.builder()
                                .cycleId("CYCLE-001")
                                .cycleName("2026 Q1 Review")
                                .build();

                when(getCycleDetailService.getResponse(any(GetCycleDetailRequest.class), any(JWTModel.class), any()))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/performance/cycles/CYCLE-001")
                                .andExpect(status().isOk());
        }
}
