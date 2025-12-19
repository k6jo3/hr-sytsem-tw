package com.company.hrms.common.test.base;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.common.test.snapshot.FluentAssert;
import com.company.hrms.common.test.snapshot.QuerySnapshotModule;
import com.company.hrms.common.test.snapshot.SnapshotUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API 契約測試基類
 * 驗證 Request → Service 的參數轉換與 QueryGroup 組裝
 *
 * <p>測試重點:
 * <ul>
 *   <li>API 請求參數解析</li>
 *   <li>DTO 驗證</li>
 *   <li>QueryGroup 組裝正確性</li>
 *   <li>回應格式</li>
 * </ul>
 *
 * <p>使用範例:
 * <pre>
 * class EmployeeApiContractTest extends BaseApiContractTest {
 *
 *     {@literal @}Test
 *     void searchApi_ShouldConvertCorrectly() throws Exception {
 *         EmployeeSearchRequest req = new EmployeeSearchRequest();
 *         req.setName("John");
 *
 *         performPost("/api/v1/employees/search", req)
 *             .andExpect(status().isOk());
 *
 *         // 驗證 DTO 快照
 *         verifyDtoSnapshot(req, "employee_search_dto.json");
 *     }
 * }
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseApiContractTest extends BaseContractTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /** QueryGroup 專用 Mapper */
    protected ObjectMapper queryMapper = QuerySnapshotModule.createMapper();

    /**
     * 執行 POST 請求
     */
    protected ResultActions performPost(String url, Object request) throws Exception {
        return mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
    }

    /**
     * 執行 GET 請求
     */
    protected ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url)
            .contentType(MediaType.APPLICATION_JSON));
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
     * 驗證 DTO 快照
     */
    protected void verifyDtoSnapshot(Object dto, String snapshotName) {
        FluentAssert.that(dto)
            .ignoringCommonDynamicFields()
            .inDirectory(getDtoSnapshotDirectory())
            .matchesSnapshot(snapshotName);
    }

    /**
     * 驗證 QueryGroup 快照
     */
    protected void verifyQuerySnapshot(QueryGroup queryGroup, String snapshotName) {
        FluentAssert.that(queryGroup)
            .withMapper(queryMapper)
            .inDirectory(getQuerySnapshotDirectory())
            .matchesSnapshot(snapshotName);
    }

    /**
     * 驗證完整 API 流程（DTO + Query）
     */
    protected void verifyApiFlow(String url, Object request,
                                  QueryGroup capturedQuery,
                                  String flowName) {
        // 驗證 DTO
        verifyDtoSnapshot(request, flowName + "_dto.json");

        // 驗證 Query
        if (capturedQuery != null) {
            verifyQuerySnapshot(capturedQuery, flowName + "_query.json");
        }
    }

    /**
     * 取得 DTO 快照目錄
     */
    protected String getDtoSnapshotDirectory() {
        return "src/test/resources/snapshots/contract/dto/" + getTestClassName();
    }

    /**
     * 取得 Query 快照目錄
     */
    protected String getQuerySnapshotDirectory() {
        return "src/test/resources/snapshots/contract/query/" + getTestClassName();
    }
}
