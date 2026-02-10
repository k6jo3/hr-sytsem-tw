package com.company.hrms.common.test.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.company.hrms.common.query.QueryGroup;

/**
 * Query 操作合約測試範例
 * 展示如何使用新的合約測試架構進行完整驗證
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class QueryContractTestExample extends BaseContractTest {

    @Autowired
    private MockMvc mockMvc;

    // @MockBean
    // private IReportRepository reportRepository; // 實際專案中需要注入

    /**
     * 範例：驗證 Query 操作的完整合約
     * 包含：查詢條件 + API 回應結果
     */
    @Test
    @DisplayName("RPT_QRY_001: 查詢在職人員名冊 - 完整合約驗證")
    void testEmployeeRosterQuery_FullContract() throws Exception {
        // 1. 載入合約規格
        ContractSpec contract = loadContract("reporting", "RPT_QRY_001");

        // 2. 建立 QueryGroup 捕獲器
        ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

        // 3. 執行 API 請求
        MvcResult result = mockMvc.perform(get("/api/v1/reports/hr/employee-roster")
                .param("organizationId", "org-001")
                .param("status", "ACTIVE")
                .param("page", "1")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andReturn();

        // 4. 取得實際回應
        String actualResponseJson = result.getResponse().getContentAsString();

        // 5. 捕獲 QueryGroup（從 Repository 呼叫）
        // verify(reportRepository).findPage(queryCaptor.capture(), any());
        // QueryGroup actualQuery = queryCaptor.getValue();

        // 模擬範例（實際測試中會從 Repository 捕獲）
        QueryGroup actualQuery = new QueryGroup();
        // actualQuery.addFilter(...);

        // 6. 驗證完整合約（查詢條件 + 回應結果）
        verifyQueryContract(actualQuery, actualResponseJson, contract);

        // ✅ 如果執行到這裡，代表測試通過
    }

    /**
     * 範例：只驗證查詢條件（向下相容原有功能）
     */
    @Test
    @DisplayName("RPT_QRY_001: 查詢在職人員名冊 - 僅驗證查詢條件")
    void testEmployeeRosterQuery_OnlyQueryFilters() throws Exception {
        // 載入 Markdown 合約（原有格式）
        String contractSpec = loadContractSpec("reporting");

        // 建立並執行查詢
        QueryGroup actualQuery = new QueryGroup();
        // actualQuery.addFilter(...);

        // 驗證查詢條件（原有功能）
        assertContract(actualQuery, contractSpec, "RPT_QRY_001");
    }
}
