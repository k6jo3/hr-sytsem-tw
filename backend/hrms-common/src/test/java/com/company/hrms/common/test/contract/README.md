# 合約測試架構使用說明

## 概述

本架構提供完整的合約驅動測試（Contract-Driven Testing）功能，將 SA 定義的業務規格直接轉化為自動化測試。

## 核心功能

### 1. Query 操作驗證
- ✅ 查詢過濾條件（QueryGroup）
- ✅ API 回應結果（筆數、欄位、型別、格式）
- ✅ 排序規則驗證
- ✅ 分頁資訊驗證
- ✅ 自訂欄位斷言

### 2. Command 操作驗證
- ✅ 業務規則檢查
- ✅ 資料異動驗證（INSERT/UPDATE/DELETE/SOFT_DELETE）
- ✅ 領域事件發布驗證

## 快速開始

### Step 1: 建立 JSON 格式合約

在 `contracts/{service}_contracts.md` 中定義合約：

```markdown
# HR14 報表分析服務 - 合約測試規格

## Query 操作合約

### RPT_QRY_001: 查詢在職人員名冊

\`\`\`json
{
  "scenarioId": "RPT_QRY_001",
  "apiEndpoint": "GET /api/v1/reports/hr/employee-roster",
  "request": {
    "organizationId": "org-001",
    "status": "ACTIVE"
  },
  "expectedQueryFilters": [
    {"field": "organization_id", "operator": "=", "value": "org-001"},
    {"field": "employment_status", "operator": "=", "value": "ACTIVE"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.content",
    "minRecords": 1,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid"},
      {"name": "employeeName", "type": "string"},
      {"name": "nationalIdMasked", "type": "string", "format": "masked"}
    ],
    "orderBy": {"field": "employeeNumber", "direction": "ASC"},
    "pagination": {"required": true}
  }
}
\`\`\`

## Command 操作合約

### RPT_CMD_001: 建立個人儀表板

\`\`\`json
{
  "scenarioId": "RPT_CMD_001",
  "apiEndpoint": "POST /api/v1/reports/dashboards",
  "businessRules": [
    {"rule": "使用者只能建立自己的儀表板"},
    {"rule": "儀表板名稱不可重複"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "dashboards",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "name", "operator": "equals", "value": "我的儀表板"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "DashboardCreatedEvent",
      "payload": [
        {"field": "dashboardId", "operator": "notNull"}
      ]
    }
  ]
}
\`\`\`
```

### Step 2: 撰寫測試類別

繼承 `BaseContractTest` 並使用新的驗證方法：

#### Query 測試範例

```java
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeRosterContractTest extends BaseContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IReportRepository reportRepository;

    @Test
    @DisplayName("RPT_QRY_001: 查詢在職人員名冊")
    void testEmployeeRoster() throws Exception {
        // 1. 載入合約
        ContractSpec contract = loadContract("reporting", "RPT_QRY_001");

        // 2. 捕獲 QueryGroup
        ArgumentCaptor<QueryGroup> captor = createQueryGroupCaptor();

        // 3. 執行 API
        MvcResult result = mockMvc.perform(get("/api/v1/reports/hr/employee-roster")
                .param("organizationId", "org-001")
                .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        // 4. 從 Repository 捕獲查詢
        verify(reportRepository).findPage(captor.capture(), any());
        QueryGroup actualQuery = captor.getValue();

        // 5. 驗證完整合約
        verifyQueryContract(actualQuery, responseJson, contract);
    }
}
```

#### Command 測試範例

```java
@SpringBootTest
@AutoConfigureMockMvc
public class DashboardContractTest extends BaseContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("RPT_CMD_001: 建立個人儀表板")
    void testCreateDashboard() throws Exception {
        // 1. 載入合約
        ContractSpec contract = loadContract("reporting", "RPT_CMD_001");

        // 2. 執行前快照
        Map<String, List<Map<String, Object>>> beforeSnapshot =
            captureDataSnapshot("dashboards", "dashboard_widgets");

        // 3. 執行 API
        Map<String, Object> request = new HashMap<>();
        request.put("name", "我的儀表板");

        mockMvc.perform(post("/api/v1/reports/dashboards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 4. 執行後快照
        Map<String, List<Map<String, Object>>> afterSnapshot =
            captureDataSnapshot("dashboards", "dashboard_widgets");

        // 5. 捕獲事件（需實作事件監聽器）
        List<Map<String, Object>> capturedEvents = eventListener.getCapturedEvents();

        // 6. 驗證完整合約
        verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
    }
}
```

## 核心類別說明

### BaseContractTest

測試基類，提供所有合約驗證方法。

**主要方法：**
- `loadContract(serviceName, scenarioId)` - 載入合約規格
- `verifyQueryContract(query, response, contract)` - 驗證 Query 完整合約
- `verifyCommandContract(before, after, events, contract)` - 驗證 Command 完整合約
- `captureDataSnapshot(tables...)` - 擷取資料快照
- `assertContract(query, spec, scenarioId)` - 驗證查詢條件（向下相容）

### MarkdownContractEngine

合約解析與驗證引擎。

**主要方法：**
- `extractJsonContract(markdown, scenarioId)` - 從 Markdown 提取 JSON
- `parseContract(jsonContract)` - 解析 JSON 為 ContractSpec
- `assertResponse(response, expected, scenarioId)` - 驗證 API 回應
- `assertDataChanges(before, after, expected, scenarioId)` - 驗證資料異動
- `assertEvents(captured, expected, scenarioId)` - 驗證領域事件

### ContractSpec

合約規格物件，包含以下屬性：
- `scenarioId` - 場景 ID
- `apiEndpoint` - API 端點
- `expectedQueryFilters` - 預期查詢過濾條件
- `expectedResponse` - 預期回應結果
- `expectedDataChanges` - 預期資料異動
- `expectedEvents` - 預期領域事件

## 驗證規則

### 欄位型別驗證

支援的型別：
- `uuid` - UUID 格式（36 字元，包含破折號）
- `string` - 字串型別
- `integer` - 整數型別
- `decimal` - 小數型別
- `boolean` - 布林型別
- `date` - 日期格式（YYYY-MM-DD）
- `datetime` - 日期時間格式（ISO 8601）
- `email` - Email 格式
- `phone` - 電話號碼

特殊格式：
- `format: "masked"` - 遮罩格式（需包含 `*`）

### 斷言操作符

支援的操作符：
- `equals` - 等於
- `notEquals` - 不等於
- `contains` - 包含
- `notContains` - 不包含
- `greaterThan` - 大於
- `lessThan` - 小於
- `notNull` - 不為 null
- `null` - 為 null

### 資料異動類型

- `INSERT` - 新增記錄
- `UPDATE` - 更新記錄
- `DELETE` - 刪除記錄（硬刪除）
- `SOFT_DELETE` - 軟刪除（設定 `is_deleted = true`）

## 向下相容性

新架構完全向下相容原有的 Markdown 表格格式：

```java
// 原有方式（僅驗證查詢條件）
String contractSpec = loadContractSpec("reporting");
assertContract(actualQuery, contractSpec, "RPT_QRY_001");

// 新方式（完整驗證）
ContractSpec contract = loadContract("reporting", "RPT_QRY_001");
verifyQueryContract(actualQuery, responseJson, contract);
```

## 最佳實踐

### 1. Query 測試
- ✅ 使用 `verifyQueryContract()` 進行完整驗證
- ✅ 驗證分頁資訊和排序規則
- ✅ 驗證敏感資料遮罩（nationalId, phone 等）
- ✅ 驗證欄位型別和格式

### 2. Command 測試
- ✅ 使用 `captureDataSnapshot()` 擷取快照
- ✅ 驗證所有相關表的資料異動
- ✅ 驗證領域事件發布
- ✅ 驗證業務規則執行結果

### 3. 合約定義
- ✅ 使用 JSON Schema 格式
- ✅ 明確定義所有必要欄位
- ✅ 包含完整的業務規則說明
- ✅ 定義所有預期的資料異動和事件

## 錯誤訊息

當驗證失敗時，會拋出 `ContractViolationException`，包含詳細的錯誤資訊：

```
[RPT_QRY_001] 資料筆數少於預期: 至少 1 筆, 實際 0 筆
[RPT_QRY_001] 欄位 nationalIdMasked 應為遮罩格式（需包含 *）(第 1 筆)
[RPT_CMD_001] 資料表 dashboards INSERT 筆數不符: 預期 1 筆, 實際 0 筆
[RPT_CMD_001] 缺少預期的領域事件: DashboardCreatedEvent
```

## 範例專案

完整的測試範例請參考：
- `QueryContractTestExample.java` - Query 操作測試範例
- `CommandContractTestExample.java` - Command 操作測試範例

## 參考文件

- `contracts/合約測試規範手冊.md` - 完整規範文件
- `framework/testing/04_合約驅動測試.md` - 測試方法論
