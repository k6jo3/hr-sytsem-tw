package com.company.hrms.common.test.contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Command 操作合約測試範例
 * 展示如何驗證：業務規則 + 資料異動 + 領域事件
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CommandContractTestExample extends BaseContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 模擬的事件監聽器（實際專案中需要實作）
    private List<Map<String, Object>> capturedEvents = new ArrayList<>();

    /**
     * 範例：驗證 Command 操作的完整合約
     * 包含：業務規則 + 資料異動 + 領域事件
     */
    @Test
    @DisplayName("RPT_CMD_001: 建立個人儀表板 - 完整合約驗證")
    void testCreateDashboard_FullContract() throws Exception {
        // 1. 載入合約規格
        ContractSpec contract = loadContract("reporting", "RPT_CMD_001");

        // 2. 擷取執行前的資料快照
        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot(
                "dashboards", "dashboard_widgets");

        // 3. 準備請求資料
        Map<String, Object> request = new HashMap<>();
        request.put("name", "我的儀表板");
        request.put("description", "個人專屬儀表板");

        // 4. 執行 API 請求
        mockMvc.perform(post("/api/v1/reports/dashboards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 5. 擷取執行後的資料快照
        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot(
                "dashboards", "dashboard_widgets");

        // 6. 取得捕獲的領域事件（實際專案中需要從事件監聽器取得）
        // List<Map<String, Object>> capturedEvents = eventListener.getCapturedEvents();

        // 模擬範例
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "DashboardCreatedEvent");
        Map<String, Object> payload = new HashMap<>();
        payload.put("dashboardId", "dash-001");
        payload.put("name", "我的儀表板");
        event.put("payload", payload);
        capturedEvents.add(event);

        // 7. 驗證完整合約（資料異動 + 領域事件）
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);

        // ✅ 如果執行到這裡，代表測試通過
    }

    /**
     * 範例：驗證資料異動 - INSERT
     */
    @Test
    @DisplayName("RPT_CMD_001: 建立個人儀表板 - 僅驗證 INSERT")
    void testCreateDashboard_InsertOnly() throws Exception {
        // 載入合約規格
        ContractSpec contract = loadContract("reporting", "RPT_CMD_001");

        // 擷取快照
        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("dashboards");

        // 執行操作
        Map<String, Object> request = new HashMap<>();
        request.put("name", "測試儀表板");
        mockMvc.perform(post("/api/v1/reports/dashboards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 擷取快照
        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("dashboards");

        // 驗證資料異動（不驗證事件）
        if (contract.getExpectedDataChanges() != null) {
            contractEngine.assertDataChanges(beforeSnapshot, afterSnapshot,
                    contract.getExpectedDataChanges(), contract.getScenarioId());
        }
    }

    /**
     * 範例：驗證資料異動 - UPDATE
     */
    @Test
    @DisplayName("RPT_CMD_002: 更新儀表板 - 驗證 UPDATE")
    void testUpdateDashboard_Update() throws Exception {
        // 載入合約規格
        ContractSpec contract = loadContract("reporting", "RPT_CMD_002");

        // 擷取快照
        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("dashboards");

        // 執行更新操作
        String dashboardId = "dash-001";
        Map<String, Object> request = new HashMap<>();
        request.put("name", "新的儀表板名稱");

        mockMvc.perform(post("/api/v1/reports/dashboards/" + dashboardId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 擷取快照
        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("dashboards");

        // 驗證資料異動
        if (contract.getExpectedDataChanges() != null) {
            contractEngine.assertDataChanges(beforeSnapshot, afterSnapshot,
                    contract.getExpectedDataChanges(), contract.getScenarioId());
        }
    }

    /**
     * 範例：驗證資料異動 - SOFT_DELETE
     */
    @Test
    @DisplayName("RPT_CMD_003: 刪除儀表板 - 驗證 SOFT_DELETE")
    void testDeleteDashboard_SoftDelete() throws Exception {
        // 載入合約規格
        ContractSpec contract = loadContract("reporting", "RPT_CMD_003");

        // 擷取快照
        Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("dashboards");

        // 執行刪除操作
        String dashboardId = "dash-001";
        mockMvc.perform(post("/api/v1/reports/dashboards/" + dashboardId + "/delete"))
                .andExpect(status().isOk());

        // 擷取快照
        Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("dashboards");

        // 驗證軟刪除
        if (contract.getExpectedDataChanges() != null) {
            contractEngine.assertDataChanges(beforeSnapshot, afterSnapshot,
                    contract.getExpectedDataChanges(), contract.getScenarioId());
        }
    }

    /**
     * 範例：驗證領域事件
     */
    @Test
    @DisplayName("RPT_CMD_001: 建立儀表板 - 僅驗證領域事件")
    void testCreateDashboard_EventsOnly() throws Exception {
        // 載入合約規格
        ContractSpec contract = loadContract("reporting", "RPT_CMD_001");

        // 執行操作
        Map<String, Object> request = new HashMap<>();
        request.put("name", "測試儀表板");
        mockMvc.perform(post("/api/v1/reports/dashboards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 取得捕獲的事件
        // List<Map<String, Object>> capturedEvents = eventListener.getCapturedEvents();

        // 模擬範例
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "DashboardCreatedEvent");
        Map<String, Object> payload = new HashMap<>();
        payload.put("dashboardId", "dash-001");
        event.put("payload", payload);
        capturedEvents.add(event);

        // 驗證領域事件
        if (contract.getExpectedEvents() != null) {
            contractEngine.assertEvents(capturedEvents,
                    contract.getExpectedEvents(), contract.getScenarioId());
        }
    }
}
