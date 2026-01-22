package com.company.hrms.recruitment.application.contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 招募報表 API 合約測試
 * 
 * 驗證 API 規格文件中定義的 Report 相關端點
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("招募報表 API 合約測試")
public class ReportContractTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 合約場景：RPT_SC_001
     * 驗證 GET /api/v1/recruitment/dashboard 端點存在並回傳正確結構
     */
    @Test
    @WithMockUser(username = "hr_admin", roles = { "HR_ADMIN" })
    @DisplayName("[RPT_SC_001] GET /dashboard - 查詢招募儀表板")
    void getDashboard_shouldReturnDashboardData() throws Exception {
        mockMvc.perform(get("/api/v1/recruitment/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").exists())
                .andExpect(jsonPath("$.kpis").exists())
                .andExpect(jsonPath("$.sourceAnalytics").exists())
                .andExpect(jsonPath("$.conversionFunnel").exists());

        System.out.println("✓ 場景 [RPT_SC_001] 驗證通過，符合業務合約。");
    }

    /**
     * 合約場景：RPT_SC_002
     * 驗證 GET /api/v1/recruitment/dashboard/export 端點存在
     */
    @Test
    @WithMockUser(username = "hr_admin", roles = { "HR_ADMIN" })
    @DisplayName("[RPT_SC_002] GET /dashboard/export - 匯出招募報表")
    void exportDashboard_shouldReturnFile() throws Exception {
        mockMvc.perform(get("/api/v1/recruitment/dashboard/export")
                .param("format", "EXCEL")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        System.out.println("✓ 場景 [RPT_SC_002] 驗證通過，符合業務合約。");
    }

    /**
     * 合約場景：RPT_SC_003
     * 驗證儀表板 KPI 資料格式
     */
    @Test
    @WithMockUser(username = "hr_admin", roles = { "HR_ADMIN" })
    @DisplayName("[RPT_SC_003] Dashboard KPIs 結構驗證")
    void getDashboard_kpisStructure() throws Exception {
        mockMvc.perform(get("/api/v1/recruitment/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kpis.openJobsCount").isNumber())
                .andExpect(jsonPath("$.kpis.totalApplications").isNumber())
                .andExpect(jsonPath("$.kpis.interviewsScheduled").isNumber())
                .andExpect(jsonPath("$.kpis.offersExtended").isNumber())
                .andExpect(jsonPath("$.kpis.hiredCount").isNumber());

        System.out.println("✓ 場景 [RPT_SC_003] 驗證通過，KPIs 結構正確。");
    }
}
