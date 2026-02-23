package com.company.hrms.common.test.base;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * API 整合測試基類
 * 完整測試 Controller → Service → Repository → H2 DB 流程
 *
 * <p>
 * 與 BaseApiContractTest 的差異:
 * <ul>
 * <li>BaseApiContractTest: Mock Repository，只驗證 QueryGroup 組裝</li>
 * <li>BaseApiIntegrationTest: 實際連接 H2 DB，驗證完整資料流</li>
 * </ul>
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>完整 API 請求/回應流程</li>
 * <li>資料持久化驗證</li>
 * <li>交易處理</li>
 * <li>錯誤處理</li>
 * </ul>
 *
 * <p>
 * 使用範例:
 * 
 * <pre>
 * {@literal @}Sql(scripts = "classpath:test-data/employee_integration_data.sql",
 *      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
 * {@literal @}Sql(scripts = "classpath:test-data/cleanup.sql",
 *      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
 * class EmployeeApiIntegrationTest extends BaseApiIntegrationTest {
 *
 *     {@literal @}Test
 *     {@literal @}DisplayName("查詢員工列表 - 應返回正確資料")
 *     void getEmployeeList_ShouldReturnCorrectData() throws Exception {
 *         // When &amp; Then
 *         performGet("/api/v1/employees?status=ACTIVE")
 *             .andExpect(status().isOk())
 *             .andExpect(jsonPath("$.content").isArray())
 *             .andExpect(jsonPath("$.content.length()").value(10))
 *             .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
 *     }
 *
 *     {@literal @}Test
 *     {@literal @}DisplayName("新增員工 - 應成功儲存並返回 ID")
 *     void createEmployee_ShouldPersistAndReturnId() throws Exception {
 *         // Given
 *         CreateEmployeeRequest request = CreateEmployeeRequest.builder()
 *             .name("張三")
 *             .email("zhang@example.com")
 *             .departmentId("DEPT-001")
 *             .build();
 *
 *         // When
 *         MvcResult result = performPost("/api/v1/employees", request)
 *             .andExpect(status().isOk())
 *             .andExpect(jsonPath("$.employeeId").exists())
 *             .andReturn();
 *
 *         // Then - 驗證資料已持久化
 *         String employeeId = extractJsonPath(result, "$.employeeId");
 *         performGet("/api/v1/employees/" + employeeId)
 *             .andExpect(status().isOk())
 *             .andExpect(jsonPath("$.name").value("張三"));
 *     }
 *
 *     {@literal @}Test
 *     {@literal @}DisplayName("刪除員工 - 應軟刪除")
 *     void deleteEmployee_ShouldSoftDelete() throws Exception {
 *         // Given - 已存在的員工 ID (由 SQL 腳本載入)
 *         String employeeId = "EMP-001";
 *
 *         // When
 *         performDelete("/api/v1/employees/" + employeeId)
 *             .andExpect(status().isOk());
 *
 *         // Then - 查詢應找不到 (軟刪除)
 *         performGet("/api/v1/employees/" + employeeId)
 *             .andExpect(status().isNotFound());
 *     }
 * }
 * </pre>
 *
 * @author SA Team
 * @since 2026-01-28
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional

public abstract class BaseApiIntegrationTest extends BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected DataSource dataSource;

    // ===== HTTP 請求方法 =====

    /**
     * 執行 GET 請求
     */
    protected ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 執行帶認證的 GET 請求
     */
    protected ResultActions performGetWithAuth(String url, String token) throws Exception {
        return mockMvc.perform(get(url)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 執行 POST 請求
     */
    protected ResultActions performPost(String url, Object request) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    /**
     * 執行帶認證的 POST 請求
     */
    protected ResultActions performPostWithAuth(String url, Object request, String token) throws Exception {
        return mockMvc.perform(post(url)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    /**
     * 執行 PUT 請求
     */
    protected ResultActions performPut(String url, Object request) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    /**
     * 執行 DELETE 請求
     */
    protected ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 執行 PATCH 請求
     */
    protected ResultActions performPatch(String url, Object request) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    // ===== 回應解析方法 =====

    /**
     * 從 MvcResult 中提取 JSON 路徑的值
     */
    protected String extractJsonPath(MvcResult result, String jsonPath) throws Exception {
        String content = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);

        // 簡易 JSON Path 解析 (支援 $.field.subfield 格式)
        String path = jsonPath.startsWith("$.") ? jsonPath.substring(2) : jsonPath;
        String[] parts = path.split("\\.");

        JsonNode current = root;
        for (String part : parts) {
            if (part.contains("[")) {
                // 陣列索引
                String fieldName = part.substring(0, part.indexOf("["));
                int index = Integer.parseInt(part.substring(part.indexOf("[") + 1, part.indexOf("]")));
                current = current.get(fieldName).get(index);
            } else {
                current = current.get(part);
            }
            if (current == null) {
                return null;
            }
        }
        return current.asText();
    }

    /**
     * 從 MvcResult 中解析回應物件
     */
    protected <T> T parseResponse(MvcResult result, Class<T> responseType) throws Exception {
        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, responseType);
    }

    /**
     * 從 MvcResult 中解析回應物件 (泛型支援)
     */
    protected <T> T parseResponse(MvcResult result, TypeReference<T> typeReference) throws Exception {
        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, typeReference);
    }

    // ===== 資料庫工具方法 =====

    /**
     * 載入測試資料 SQL 腳本
     */
    protected void loadTestData(String sqlScriptPath) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(sqlScriptPath));
        populator.execute(dataSource);
    }

    /**
     * 執行清理腳本
     */
    protected void cleanupTestData(String sqlScriptPath) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(sqlScriptPath));
        populator.execute(dataSource);
    }

    // ===== 驗證輔助方法 =====

    /**
     * 驗證分頁回應
     */
    protected ResultActions verifyPageResponse(ResultActions resultActions,
            int expectedPage,
            int expectedSize) throws Exception {
        return resultActions
                .andExpect(jsonPath("$.pageable.pageNumber").value(expectedPage))
                .andExpect(jsonPath("$.pageable.pageSize").value(expectedSize))
                .andExpect(jsonPath("$.content").isArray());
    }

    /**
     * 驗證錯誤回應
     */
    protected ResultActions verifyErrorResponse(ResultActions resultActions,
            int expectedStatus,
            String expectedErrorCode) throws Exception {
        return resultActions
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.errorCode").value(expectedErrorCode));
    }

    /**
     * 取得整合測試快照目錄
     */
    protected String getSnapshotDirectory() {
        return "src/test/resources/snapshots/integration/" + getTestClassName();
    }
}
