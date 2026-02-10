package com.company.hrms.reporting.contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HR14 報表分析服務合約測試
 *
 * 依據 contracts/reporting_contracts_v2.md 定義的 21 個合約場景
 *
 * @author SA Team
 * @since 2026-02-10
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
@DisplayName("HR14 報表分析服務合約測試")
public class ReportingContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ========================================
    // Command 操作測試 (8 個場景)
    // ========================================

    @Nested
    @DisplayName("1. 報表生成 Command 測試")
    class ReportGenerationCommandTests {

        @Test
        @DisplayName("RPT_CMD_001: 生成 HR 報表")
        @WithMockUser(username = "hr_admin", roles = {"HR"})
        @Disabled("待實作：需要 ReportRepository 和 EmployeeReportViewRepository")
        void generateHrReport_ShouldCreateReport() throws Exception {
            // Given
            String requestBody = """
                {
                  "reportType": "EMPLOYEE_ROSTER",
                  "organizationId": "org-001",
                  "startYearMonth": "2026-02",
                  "endYearMonth": "2026-02"
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/v1/reporting/reports/generate/hr")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.reportId").exists())
                    .andExpect(jsonPath("$.reportName").exists())
                    .andExpect(jsonPath("$.status").value("COMPLETED"));

            // TODO: 驗證 ReportRepository.save() 被調用
            // TODO: 驗證從 EmployeeReportViewRepository 查詢資料
        }

        @Test
        @DisplayName("RPT_CMD_002: 生成專案成本報表")
        @WithMockUser(username = "pm", roles = {"PROJECT_MANAGER"})
        @Disabled("待實作：需要 ProjectCostSnapshotRepository 和 ReportRepository")
        void generateProjectReport_ShouldCreateProjectCostReport() throws Exception {
            // Given
            String requestBody = """
                {
                  "projectId": "PRJ-001",
                  "organizationId": "org-001",
                  "startYearMonth": "2026-01",
                  "endYearMonth": "2026-02"
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/v1/reporting/reports/generate/project")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.reportId").exists())
                    .andExpect(jsonPath("$.status").value("COMPLETED"));

            // TODO: 驗證從 ProjectCostSnapshotRepository 查詢專案成本資料
            // TODO: 驗證報表儲存到 ReportRepository
        }
    }

    @Nested
    @DisplayName("2. 報表匯出 Command 測試")
    class ReportExportCommandTests {

        @Test
        @DisplayName("RPT_CMD_003: 匯出報表為 Excel")
        @WithMockUser(username = "hr_user", roles = {"HR"})
        @Disabled("待實作：Excel 匯出功能已部分實作，需補充測試")
        void exportExcel_ShouldGenerateExcelFile() throws Exception {
            // Given
            String requestBody = """
                {
                  "reportType": "EMPLOYEE_ROSTER",
                  "organizationId": "org-001",
                  "yearMonth": "2026-02",
                  "fileName": "員工花名冊.xlsx"
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/v1/reporting/export/excel")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Content-Disposition"))
                    .andExpect(content().contentType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            // TODO: 驗證 Excel 檔案內容
        }

        @Test
        @DisplayName("RPT_CMD_004: 匯出報表為 PDF")
        @WithMockUser(username = "hr_user", roles = {"HR"})
        @Disabled("待實作：需要 PDF 生成工具整合")
        void exportPdf_ShouldGeneratePdfFile() throws Exception {
            // Given
            String requestBody = """
                {
                  "reportType": "ATTENDANCE_STATISTICS",
                  "organizationId": "org-001",
                  "yearMonth": "2026-02",
                  "fileName": "差勤統計.pdf"
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/v1/reporting/export/pdf")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Content-Disposition"))
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF));

            // TODO: 驗證 PDF 檔案內容
            // TODO: 整合 iText 或其他 PDF 生成工具
        }

        @Test
        @DisplayName("RPT_CMD_008: 政府申報格式匯出")
        @WithMockUser(username = "hr_admin", roles = {"HR"})
        @Disabled("待實作：需要保險申報資料 Repository")
        void exportGovernmentFormat_ShouldGenerateGovernmentFile() throws Exception {
            // Given
            String requestBody = """
                {
                  "declarationType": "LABOR_INSURANCE",
                  "organizationId": "org-001",
                  "yearMonth": "2026-02"
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/v1/reporting/export/government")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exportId").exists())
                    .andExpect(jsonPath("$.fileName").exists())
                    .andExpect(jsonPath("$.status").value("COMPLETED"));

            // TODO: 驗證政府申報格式符合勞保局規範
            // TODO: 驗證檔案可下載
        }
    }

    @Nested
    @DisplayName("3. 儀表板管理 Command 測試")
    class DashboardManagementCommandTests {

        @Test
        @DisplayName("RPT_CMD_005: 建立自定義儀表板")
        @WithMockUser(username = "user001", roles = {"EMPLOYEE"})
        void createDashboard_ShouldCreateNewDashboard() throws Exception {
            // Given
            String requestBody = """
                {
                  "dashboardName": "我的儀表板",
                  "description": "個人工作儀表板",
                  "isDefault": true,
                  "widgetsConfig": [
                    {
                      "widgetType": "KPI_CARD",
                      "title": "待辦事項",
                      "position": {"x": 0, "y": 0, "width": 4, "height": 2}
                    }
                  ]
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/v1/reporting/dashboards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dashboardId").exists())
                    .andExpect(jsonPath("$.dashboardName").value("我的儀表板"));

            // TODO: 驗證儀表板已儲存到 DashboardRepository
            // TODO: 驗證 ownerId = currentUser
        }

        @Test
        @DisplayName("RPT_CMD_006: 更新儀表板 Widget 配置")
        @WithMockUser(username = "user001", roles = {"EMPLOYEE"})
        @Disabled("待實作：需要先建立測試用儀表板")
        void updateDashboardWidgets_ShouldUpdateWidgetConfig() throws Exception {
            // Given
            String dashboardId = "dashboard-001"; // 需先建立
            String requestBody = """
                {
                  "widgetsConfig": [
                    {
                      "widgetType": "LINE_CHART",
                      "title": "月度趨勢",
                      "position": {"x": 0, "y": 0, "width": 8, "height": 4}
                    }
                  ]
                }
                """;

            // When & Then
            mockMvc.perform(put("/api/v1/reporting/dashboards/" + dashboardId + "/widgets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());

            // TODO: 驗證 Widget 配置已更新
            // TODO: 驗證只有 owner 可以更新
        }

        @Test
        @DisplayName("RPT_CMD_007: 刪除儀表板")
        @WithMockUser(username = "user001", roles = {"EMPLOYEE"})
        @Disabled("待實作：需要先建立測試用儀表板")
        void deleteDashboard_ShouldMarkAsDeleted() throws Exception {
            // Given
            String dashboardId = "dashboard-002"; // 需先建立

            // When & Then
            mockMvc.perform(delete("/api/v1/reporting/dashboards/" + dashboardId))
                    .andExpect(status().isNoContent());

            // TODO: 驗證儀表板標記為 deleted
            // TODO: 驗證只有 owner 可以刪除
        }
    }

    // ========================================
    // Query 操作測試 (13 個場景)
    // ========================================

    @Nested
    @DisplayName("4. HR 報表查詢測試")
    class HRReportQueryTests {

        @Test
        @DisplayName("RPT_QRY_001: 查詢員工花名冊")
        @WithMockUser(username = "hr_admin", roles = {"HR"})
        void getEmployeeRoster_ShouldReturnEmployeeList() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/hr/employee-roster")
                    .param("organizationId", "org-001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            // TODO: 驗證過濾條件 organization_id = 'org-001'
            // TODO: 驗證從 EmployeeReportViewRepository 查詢
        }

        @Test
        @DisplayName("RPT_QRY_002: 查詢差勤統計")
        @WithMockUser(username = "hr_admin", roles = {"HR"})
        void getAttendanceStatistics_ShouldReturnStats() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/hr/attendance-statistics")
                    .param("yearMonth", "2026-02"))
                    .andExpect(status().isOk());

            // TODO: 驗證過濾條件 year_month = '2026-02'
            // TODO: 驗證從 MonthlyHrStatsRepository 查詢
        }

        @Test
        @DisplayName("RPT_QRY_003: 查詢離職率分析")
        @WithMockUser(username = "hr_admin", roles = {"HR"})
        @Disabled("待實作：需要 MonthlyHrStatsRepository")
        void getTurnoverAnalysis_ShouldReturnTurnoverData() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/hr/turnover")
                    .param("organizationId", "org-001")
                    .param("yearMonth", "2026-02"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.turnoverRate").exists());

            // TODO: 驗證離職率計算公式
            // TODO: 驗證過濾條件 organization_id, year_month
        }

        @Test
        @DisplayName("RPT_QRY_011: 查詢人力盤點報表")
        @WithMockUser(username = "hr_admin", roles = {"HR"})
        void getHeadcountReport_ShouldReturnHeadcountData() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/hr/headcount")
                    .param("organizationId", "org-001")
                    .param("month", "2026-02"))
                    .andExpect(status().isOk());

            // TODO: 驗證人力盤點統計
            // TODO: 驗證按部門分組
        }
    }

    @Nested
    @DisplayName("5. 專案成本報表查詢測試")
    class ProjectReportQueryTests {

        @Test
        @DisplayName("RPT_QRY_004: 查詢專案成本分析")
        @WithMockUser(username = "pm", roles = {"PROJECT_MANAGER"})
        void getProjectCostAnalysis_ShouldReturnCostData() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/project/cost-analysis")
                    .param("projectId", "PRJ-001"))
                    .andExpect(status().isOk());

            // TODO: 驗證過濾條件 project_id = 'PRJ-001'
            // TODO: 驗證從 ProjectCostSnapshotRepository 查詢
        }

        @Test
        @DisplayName("RPT_QRY_005: 查詢稼動率分析")
        @WithMockUser(username = "pm", roles = {"PROJECT_MANAGER"})
        @Disabled("待實作：需要 ProjectCostSnapshotRepository")
        void getUtilizationRate_ShouldReturnUtilizationData() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/project/utilization-rate")
                    .param("projectId", "PRJ-001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.utilizationRate").exists());

            // TODO: 驗證稼動率計算公式（計費工時 / 總工時）
            // TODO: 驗證專案權限檢查
        }
    }

    @Nested
    @DisplayName("6. 財務報表查詢測試")
    class FinanceReportQueryTests {

        @Test
        @DisplayName("RPT_QRY_006: 查詢人力成本分析")
        @WithMockUser(username = "finance", roles = {"FINANCE"})
        @Disabled("待實作：需要 LaborCostViewRepository")
        void getLaborCostAnalysis_ShouldReturnLaborCost() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/finance/labor-cost")
                    .param("yearMonth", "2026-02"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalLaborCost").exists());

            // TODO: 驗證人力成本計算（薪資 + 加班費 + 保險費）
            // TODO: 驗證過濾條件 year_month = '2026-02'
        }

        @Test
        @DisplayName("RPT_QRY_007: 查詢薪資總表")
        @WithMockUser(username = "finance", roles = {"FINANCE"})
        void getPayrollSummary_ShouldReturnPayrollData() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/finance/payroll-summary")
                    .param("yearMonth", "2026-02"))
                    .andExpect(status().isOk());

            // TODO: 驗證薪資總表統計
            // TODO: 驗證從 PayrollSummaryRepository 查詢
        }

        @Test
        @DisplayName("RPT_QRY_012: 查詢部門人力成本分析")
        @WithMockUser(username = "finance", roles = {"FINANCE"})
        @Disabled("待實作：需要 LaborCostViewRepository")
        void getLaborCostByDepartment_ShouldReturnDeptCost() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/finance/labor-cost-by-department")
                    .param("departmentId", "D001")
                    .param("yearMonth", "2026-02"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            // TODO: 驗證按部門分組統計
            // TODO: 驗證成本占比計算
        }
    }

    @Nested
    @DisplayName("7. 儀表板查詢測試")
    class DashboardQueryTests {

        @Test
        @DisplayName("RPT_QRY_008: 查詢儀表板列表")
        @WithMockUser(username = "user001", roles = {"EMPLOYEE"})
        void getDashboardList_ShouldReturnUserDashboards() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/dashboards"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            // TODO: 驗證過濾條件 owner_id = currentUser
            // TODO: 驗證排序 is_default DESC, created_at DESC
        }

        @Test
        @DisplayName("RPT_QRY_009: 查詢預設儀表板")
        @WithMockUser(username = "user001", roles = {"EMPLOYEE"})
        @Disabled("待實作：需要 DashboardRepository.findDefaultDashboard()")
        void getDefaultDashboard_ShouldReturnDefaultDashboard() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/reporting/dashboards/default"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isDefault").value(true));

            // TODO: 驗證過濾條件 owner_id = currentUser AND is_default = true
            // TODO: 驗證只返回一個儀表板
        }

        @Test
        @DisplayName("RPT_QRY_010: 查詢儀表板詳情")
        @WithMockUser(username = "user001", roles = {"EMPLOYEE"})
        @Disabled("待實作：需要 DashboardRepository.findById() 並檢查權限")
        void getDashboardDetail_ShouldReturnDashboardWithWidgets() throws Exception {
            // Given
            String dashboardId = "dashboard-001";

            // When & Then
            mockMvc.perform(get("/api/v1/reporting/dashboards/" + dashboardId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dashboardId").value(dashboardId))
                    .andExpect(jsonPath("$.widgetsConfig").isArray());

            // TODO: 驗證只能查詢自己的儀表板
            // TODO: 驗證或可查詢公開儀表板 (isPublic = true)
        }
    }

    @Nested
    @DisplayName("8. 報表匯出查詢測試")
    class ExportQueryTests {

        @Test
        @DisplayName("RPT_QRY_013: 下載匯出檔案")
        @WithMockUser(username = "user001", roles = {"EMPLOYEE"})
        @Disabled("待實作：需要 ExportRecordRepository 和檔案儲存服務")
        void downloadExportFile_ShouldReturnFile() throws Exception {
            // Given
            String exportId = "export-001";

            // When & Then
            mockMvc.perform(get("/api/v1/reporting/export/" + exportId + "/download"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Content-Disposition"))
                    .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));

            // TODO: 驗證過濾條件 export_id = {id} AND created_by = currentUser
            // TODO: 驗證檔案未過期 expiry_time > NOW()
            // TODO: 驗證過期檔案返回 410 Gone
        }
    }
}
