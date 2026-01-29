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

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.performance.api.request.GetDistributionRequest;
import com.company.hrms.performance.api.response.GetDistributionResponse;
import com.company.hrms.performance.application.service.GetDistributionServiceImpl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR08 報表查詢 Controller 測試")
class HR08ReportQryControllerTest extends BaseApiContractTest {

        @MockBean(name = "getDistributionServiceImpl")
        private GetDistributionServiceImpl getDistributionService;

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
        @DisplayName("查詢績效分布 - 成功")
        void getDistribution_ShouldReturnOk() throws Exception {
                // Arrange
                GetDistributionResponse response = GetDistributionResponse.builder()
                                .cycleId("CYCLE-001")
                                .totalReviews(100)
                                .completedReviews(80)
                                .build();

                when(getDistributionService.getResponse(any(GetDistributionRequest.class), any(JWTModel.class)))
                                .thenReturn(response);

                // Act & Assert
                performGet("/api/v1/performance/reports/distribution/CYCLE-001")
                                .andExpect(status().isOk());
        }

}
