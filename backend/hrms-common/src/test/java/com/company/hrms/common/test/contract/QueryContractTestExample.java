package com.company.hrms.common.test.contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.company.hrms.common.query.QueryGroup;

/**
 * Query 操作合約測試範例
 * 展示如何使用新的合約測試架構進行完整驗證
 */
@Disabled("Example test, requires complex context setup")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)

public class QueryContractTestExample extends BaseContractTest {

    @org.springframework.boot.autoconfigure.SpringBootApplication(exclude = {
            org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
            org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class,
            org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
            org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class,
            org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class,
            org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
            org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
    })
    @org.springframework.web.bind.annotation.RestController
    static class TestConfig {
        @org.springframework.web.bind.annotation.GetMapping("/api/v1/reports/hr/employee-roster")
        public String mockEndpoint() {
            return """
                        {
                            "data": {
                                "content": [
                                    {
                                        "employeeId": "123e4567-e89b-12d3-a456-426614174000",
                                        "employeeNumber": "EMP001",
                                        "fullName": "王大明",
                                        "nationalIdMasked": "A123****89",
                                        "email": "daming@example.com",
                                        "serviceYears": 3.5,
                                        "hireDate": "2022-08-01",
                                        "employmentStatus": "ACTIVE"
                                    }
                                ],
                                "page": 1,
                                "size": 50,
                                "totalElements": 1,
                                "totalPages": 1
                            }
                        }
                    """;
        }
    }

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
        // ArgumentCaptor<QueryGroup> queryCaptor = createQueryGroupCaptor();

        // 3. 執行 API 請求
        MvcResult result = mockMvc.perform(get("/api/v1/reports/hr/employee-roster")
                .param("organizationId", "org-001")
                .param("status", "ACTIVE")
                .param("page", "1")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andReturn();

        // 4. 取得實際回應
        String actualResponseJson = result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);

        // 5. 捕獲 QueryGroup（從 Repository 呼叫）
        // verify(reportRepository).findPage(queryCaptor.capture(), any());
        // QueryGroup actualQuery = queryCaptor.getValue();

        // 模擬範例（實際測試中會從 Repository 捕獲）
        QueryGroup actualQuery = new QueryGroup();
        actualQuery.eq("organization_id", "org-001");
        actualQuery.eq("employment_status", "ACTIVE");

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
