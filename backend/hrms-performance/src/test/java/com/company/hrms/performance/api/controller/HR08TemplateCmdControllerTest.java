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
import com.company.hrms.performance.api.request.SaveTemplateRequest;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.PublishTemplateServiceImpl;
import com.company.hrms.performance.application.service.SaveTemplateServiceImpl;
import com.company.hrms.performance.domain.model.valueobject.ScoringSystem;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR08 考核範本維護 Controller 測試")
class HR08TemplateCmdControllerTest extends BaseApiContractTest {

    @MockBean(name = "saveTemplateServiceImpl")
    private SaveTemplateServiceImpl saveTemplateService;

    @MockBean(name = "publishTemplateServiceImpl")
    private PublishTemplateServiceImpl publishTemplateService;

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
    @DisplayName("儲存考核範本 - 成功")
    void saveTemplate_ShouldReturnOk() throws Exception {
        // Arrange
        SaveTemplateRequest request = SaveTemplateRequest.builder()
                .templateName("2025年度考核表")
                .scoringSystem(ScoringSystem.FIVE_POINT)
                .enableDistribution(true)
                .items(Collections.singletonList(
                        SaveTemplateRequest.EvaluationItemRequest.builder()
                                .itemName("工作品質")
                                .weight(30)
                                .description("說明")
                                .criteria("標準")
                                .build()))
                .build();

        SuccessResponse response = new SuccessResponse("儲存成功", true);

        when(saveTemplateService.execCommand(any(SaveTemplateRequest.class), any(JWTModel.class)))
                .thenReturn(response);

        // Act & Assert
        performPost("/api/v1/performance/cycles/CYCLE-001/template", request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("發布考核範本 - 成功")
    void publishTemplate_ShouldReturnOk() throws Exception {
        // Arrange
        SuccessResponse response = new SuccessResponse("發布成功", true);

        when(publishTemplateService.execCommand(any(StartCycleRequest.class), any(JWTModel.class)))
                .thenReturn(response);

        // Act & Assert
        performPut("/api/v1/performance/cycles/CYCLE-001/template/publish", null)
                .andExpect(status().isOk());
    }
}
