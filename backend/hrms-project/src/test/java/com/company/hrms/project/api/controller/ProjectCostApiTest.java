package com.company.hrms.project.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.project.api.request.GetProjectCostRequest;
import com.company.hrms.project.api.response.GetProjectCostResponse;
import com.company.hrms.project.api.response.GetProjectCostResponse.BudgetInfo;
import com.company.hrms.project.api.response.GetProjectCostResponse.CostSummary;
import com.company.hrms.project.api.response.GetProjectCostResponse.MemberCost;
import com.company.hrms.project.application.service.GetProjectCostServiceImpl;
import com.company.hrms.project.domain.model.valueobject.BudgetType;

/**
 * HR06 專案成本分析 API 合約測試
 *
 * <p>
 * 測試範圍：
 * </p>
 * <ul>
 * <li>專案成本查詢</li>
 * <li>預算利用率計算</li>
 * <li>成員成本分攤</li>
 * <li>月份成本趨勢</li>
 * </ul>
 */
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HR06 專案成本分析 API 合約測試")
class ProjectCostApiTest extends BaseApiContractTest {

    @MockBean(name = "getProjectCostServiceImpl")
    private GetProjectCostServiceImpl getProjectCostService;

    @BeforeEach
    void setupSecurity() {
        JWTModel mockUser = new JWTModel();
        mockUser.setUserId("test-user");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("PM"));

        List<SimpleGrantedAuthority> authorities = mockUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                mockUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 專案成本查詢 API 測試
     */
    @Nested
    @DisplayName("專案成本查詢 API")
    class ProjectCostQueryTests {

        @Test
        @DisplayName("PRJ_COST_001: 查詢專案成本 - 應回傳預算與成本摘要")
        void getProjectCost_ShouldReturnBudgetAndCostSummary() throws Exception {
            // Arrange
            String projectId = UUID.randomUUID().toString();

            BudgetInfo budgetInfo = BudgetInfo.builder()
                    .budgetType(BudgetType.FIXED_PRICE)
                    .budgetAmount(BigDecimal.valueOf(500000))
                    .budgetHours(BigDecimal.valueOf(1000))
                    .build();

            CostSummary costSummary = CostSummary.builder()
                    .totalHours(BigDecimal.valueOf(350))
                    .totalCost(BigDecimal.valueOf(175000))
                    .budgetUtilization(BigDecimal.valueOf(35.00))
                    .hoursUtilization(BigDecimal.valueOf(35.00))
                    .estimatedGrossProfit(BigDecimal.valueOf(325000))
                    .estimatedGrossProfitMargin(BigDecimal.valueOf(65.00))
                    .burnRate(BigDecimal.valueOf(35.00))
                    .build();

            MemberCost memberCost = MemberCost.builder()
                    .employeeId(UUID.randomUUID().toString())
                    .employeeName("張三")
                    .role("DEVELOPER")
                    .hours(BigDecimal.valueOf(100))
                    .hourlyRate(BigDecimal.valueOf(800))
                    .cost(BigDecimal.valueOf(80000))
                    .costPercentage(BigDecimal.valueOf(45.71))
                    .build();

            GetProjectCostResponse response = GetProjectCostResponse.builder()
                    .projectId(projectId)
                    .projectCode("P-2026-001")
                    .projectName("測試專案")
                    .budget(budgetInfo)
                    .summary(costSummary)
                    .byMember(Collections.singletonList(memberCost))
                    .byMonth(Collections.emptyList())
                    .build();

            when(getProjectCostService.getResponse(any(GetProjectCostRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/projects/" + projectId + "/cost")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.projectId").value(projectId))
                    .andExpect(jsonPath("$.budget.budgetAmount").value(500000))
                    .andExpect(jsonPath("$.summary.totalCost").value(175000))
                    .andExpect(jsonPath("$.summary.budgetUtilization").value(35.00))
                    .andExpect(jsonPath("$.byMember[0].employeeName").value("張三"));
        }

        @Test
        @DisplayName("PRJ_COST_002: 查詢成本 - 無成本記錄應回傳零值")
        void getProjectCost_NoRecords_ShouldReturnZeroValues() throws Exception {
            // Arrange
            String projectId = UUID.randomUUID().toString();

            BudgetInfo budgetInfo = BudgetInfo.builder()
                    .budgetType(BudgetType.TIME_AND_MATERIAL)
                    .budgetAmount(BigDecimal.ZERO)
                    .budgetHours(BigDecimal.valueOf(500))
                    .build();

            CostSummary costSummary = CostSummary.builder()
                    .totalHours(BigDecimal.ZERO)
                    .totalCost(BigDecimal.ZERO)
                    .budgetUtilization(BigDecimal.ZERO)
                    .hoursUtilization(BigDecimal.ZERO)
                    .estimatedGrossProfit(BigDecimal.ZERO)
                    .estimatedGrossProfitMargin(BigDecimal.ZERO)
                    .burnRate(BigDecimal.ZERO)
                    .build();

            GetProjectCostResponse response = GetProjectCostResponse.builder()
                    .projectId(projectId)
                    .projectCode("P-2026-002")
                    .projectName("新啟動專案")
                    .budget(budgetInfo)
                    .summary(costSummary)
                    .byMember(Collections.emptyList())
                    .byMonth(Collections.emptyList())
                    .build();

            when(getProjectCostService.getResponse(any(GetProjectCostRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/projects/" + projectId + "/cost")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.summary.totalCost").value(0))
                    .andExpect(jsonPath("$.summary.budgetUtilization").value(0))
                    .andExpect(jsonPath("$.byMember").isEmpty());
        }

        @Test
        @DisplayName("PRJ_COST_003: 查詢成本 - 超出預算應顯示超支狀態")
        void getProjectCost_OverBudget_ShouldShowOverspentStatus() throws Exception {
            // Arrange
            String projectId = UUID.randomUUID().toString();

            BudgetInfo budgetInfo = BudgetInfo.builder()
                    .budgetType(BudgetType.FIXED_PRICE)
                    .budgetAmount(BigDecimal.valueOf(100000))
                    .budgetHours(BigDecimal.valueOf(200))
                    .build();

            CostSummary costSummary = CostSummary.builder()
                    .totalHours(BigDecimal.valueOf(250))
                    .totalCost(BigDecimal.valueOf(120000))
                    .budgetUtilization(BigDecimal.valueOf(120.00))
                    .hoursUtilization(BigDecimal.valueOf(125.00))
                    .estimatedGrossProfit(BigDecimal.valueOf(-20000))
                    .estimatedGrossProfitMargin(BigDecimal.valueOf(-20.00))
                    .burnRate(BigDecimal.valueOf(120.00))
                    .build();

            GetProjectCostResponse response = GetProjectCostResponse.builder()
                    .projectId(projectId)
                    .projectCode("P-2026-003")
                    .projectName("超支專案")
                    .budget(budgetInfo)
                    .summary(costSummary)
                    .byMember(Collections.emptyList())
                    .byMonth(Collections.emptyList())
                    .build();

            when(getProjectCostService.getResponse(any(GetProjectCostRequest.class), any(JWTModel.class)))
                    .thenReturn(response);

            // Act & Assert
            performGet("/api/v1/projects/" + projectId + "/cost")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.summary.budgetUtilization").value(120.00))
                    .andExpect(jsonPath("$.summary.estimatedGrossProfit").value(-20000))
                    .andExpect(jsonPath("$.summary.estimatedGrossProfitMargin").value(-20.00));
        }
    }
}
