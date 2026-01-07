package com.company.hrms.performance.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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
import com.company.hrms.performance.api.request.CreateCycleRequest;
import com.company.hrms.performance.api.request.DeleteCycleRequest;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.request.UpdateCycleRequest;
import com.company.hrms.performance.api.response.CreateCycleResponse;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.CompleteCycleServiceImpl;
import com.company.hrms.performance.application.service.CreateCycleServiceImpl;
import com.company.hrms.performance.application.service.DeleteCycleServiceImpl;
import com.company.hrms.performance.application.service.StartCycleServiceImpl;
import com.company.hrms.performance.application.service.UpdateCycleServiceImpl;
import com.company.hrms.performance.domain.model.valueobject.CycleType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR08 考核週期維護 Controller 測試")
class HR08CycleCmdControllerTest extends BaseApiContractTest {

    @MockBean(name = "createCycleServiceImpl")
    private CreateCycleServiceImpl createCycleService;

    @MockBean(name = "updateCycleServiceImpl")
    private UpdateCycleServiceImpl updateCycleService;

    @MockBean(name = "deleteCycleServiceImpl")
    private DeleteCycleServiceImpl deleteCycleService;

    @MockBean(name = "startCycleServiceImpl")
    private StartCycleServiceImpl startCycleService;

    @MockBean(name = "completeCycleServiceImpl")
    private CompleteCycleServiceImpl completeCycleService;

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
    @DisplayName("建立考核週期 - 成功")
    void createCycle_ShouldReturnOk() throws Exception {
        // Arrange
        CreateCycleRequest request = CreateCycleRequest.builder()
                .cycleName("2026 Q1 Review")
                .cycleType(CycleType.QUARTERLY)
                .year(2026)
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 3, 31))
                .build();

        CreateCycleResponse response = CreateCycleResponse.builder()
                .cycleId("CYCLE-001")
                .build();

        when(createCycleService.execCommand(any(CreateCycleRequest.class), any(JWTModel.class)))
                .thenReturn(response);

        // Act & Assert
        performPost("/api/v1/performance/cycles", request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("更新考核週期 - 成功")
    void updateCycle_ShouldReturnOk() throws Exception {
        // Arrange
        UpdateCycleRequest request = UpdateCycleRequest.builder()
                .cycleName("Updated Name")
                .build();

        when(updateCycleService.execCommand(any(UpdateCycleRequest.class), any(JWTModel.class)))
                .thenReturn(new SuccessResponse("Updated", true));

        // Act & Assert
        performPut("/api/v1/performance/cycles/CYCLE-001", request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("刪除考核週期 - 成功")
    void deleteCycle_ShouldReturnOk() throws Exception {
        // Arrange
        when(deleteCycleService.execCommand(any(DeleteCycleRequest.class), any(JWTModel.class)))
                .thenReturn(new SuccessResponse("Deleted", true));

        // Act & Assert
        performDelete("/api/v1/performance/cycles/CYCLE-001")
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("啟動考核週期 - 成功")
    void startCycle_ShouldReturnOk() throws Exception {
        // Arrange
        when(startCycleService.execCommand(any(StartCycleRequest.class), any(JWTModel.class)))
                .thenReturn(new SuccessResponse("Started", true));

        // Act & Assert
        performPut("/api/v1/performance/cycles/CYCLE-001/start", null)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("完成考核週期 - 成功")
    void completeCycle_ShouldReturnOk() throws Exception {
        // Arrange
        when(completeCycleService.execCommand(any(StartCycleRequest.class), any(JWTModel.class)))
                .thenReturn(new SuccessResponse("Completed", true)); // Note: Controller uses StartCycleRequest for
                                                                     // completeCycle currently

        // Act & Assert
        performPut("/api/v1/performance/cycles/CYCLE-001/complete", null)
                .andExpect(status().isOk());
    }
}
