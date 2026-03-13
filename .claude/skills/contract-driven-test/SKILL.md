---
name: contract-driven-test
description: 自動化執行「合約驅動測試」的完整開發流程，從合約文件補充到測試執行驗證
user_invocable: true
---

# Contract-Driven Test Skill

**名稱：** 合約驅動測試開發流程
**版本：** 3.0
**適用範圍：** 所有微服務的合約測試開發

---

## 觸發時機

當以下情境發生時，必須執行此 Skill：

- 新增 API 端點或場景時，需同步補充合約定義 + 測試程式
- 修改既有 API 行為（HTTP 狀態碼、回應欄位、業務規則）時，需更新合約 + 測試
- SA 更新合約文件後，需補充或調整對應的測試程式
- 發現合約文件有場景但缺少測試程式時

---

## 核心原則

### 合約文件的雙層結構

合約文件（`contracts/{service}_contracts.md`）包含兩層：

| 層級 | 格式 | 用途 | 被測試讀取 |
|:---|:---|:---|:---:|
| 機器可讀層 | JSON（`ContractSpec`） | 測試框架自動解析驗證 | 是 |
| 人類可讀層 | JSON 額外欄位 + 描述文字 | SA/工程師閱讀理解 | 否（`@JsonIgnoreProperties`） |

**機器可讀欄位**（`ContractSpec` 認識的）：
`scenarioId`, `apiEndpoint`, `controller`, `service`, `permission`, `request`, `expectedQueryFilters`, `expectedResponse`, `businessRules`, `expectedDataChanges`, `expectedEvents`

**人類可讀欄位**（會被忽略，僅供文件參考）：
`errorScenarios`, `frontendAdapterMapping`, `frontendAdapterRestructure`, `precondition` 等

### 合約變更必須同步測試

```
SA 更新合約 → 測試應失敗 → 工程師更新實作/測試 → 測試通過
```

**任何合約變更都必須有對應的測試程式。** 如果新增了合約場景但沒有測試，等於合約形同虛設。

---

## 執行流程

### 階段一：分析與補充合約文件

**輸入：** 服務代碼（如 `01` = IAM, `02` = Organization）

**步驟：**

1. **讀取現有合約文件與 API 規格**
   ```
   contracts/{service_name}_contracts.md
   knowledge/04_API_Specifications/{NN}_*.md
   ```

2. **比對場景完整性**
   - 列出 API 規格中所有端點
   - 對照合約文件中已定義的場景
   - 找出缺少的場景（含正常流程與錯誤流程）

3. **補充合約定義**

   **Query 合約範本：**
   ```json
   {
     "scenarioId": "{MODULE}_{QRY/CMD}_{ID}",
     "apiEndpoint": "GET /api/v1/{resource}",
     "controller": "HR{DD}{Screen}QryController",
     "service": "{Verb}{Noun}ServiceImpl",
     "permission": "{resource}:read",
     "request": {},
     "expectedQueryFilters": [
       {"field": "is_deleted", "operator": "=", "value": false}
     ],
     "expectedResponse": {
       "statusCode": 200,
       "dataPath": "items",
       "requiredFields": [
         {"name": "fieldName", "type": "string", "notNull": true}
       ]
     }
   }
   ```

   **Command 合約範本：**
   ```json
   {
     "scenarioId": "{MODULE}_CMD_{ID}",
     "apiEndpoint": "POST /api/v1/{resource}",
     "controller": "HR{DD}{Screen}CmdController",
     "service": "{Verb}{Noun}ServiceImpl",
     "permission": "{resource}:create",
     "request": { },
     "businessRules": [
       {"rule": "業務規則描述"}
     ],
     "expectedDataChanges": [
       {"table": "table_name", "operation": "INSERT", "count": 1}
     ],
     "expectedEvents": [
       {"eventType": "EntityCreatedEvent"}
     ],
     "expectedResponse": {
       "statusCode": 200
     }
   }
   ```

   **錯誤場景合約範本：**
   ```json
   {
     "scenarioId": "{MODULE}_CMD_{ID}_ERR",
     "apiEndpoint": "POST /api/v1/{resource}",
     "expectedResponse": {
       "statusCode": 409,
       "errorCode": "RESOURCE_ALREADY_EXISTS"
     },
     "expectedDataChanges": [],
     "expectedEvents": []
   }
   ```

4. **`requiredFields` 三方一致性驗證（必須）**

   合約的 `requiredFields` 是前後端的「單一事實來源」，必須三方一致：

   ```
   後端 Response DTO 欄位名 = 合約 requiredFields.name = 前端 API DTO type 欄位名
   ```

   **驗證步驟：**
   1. 檢查後端 `api/response/` 目錄下的 DTO 類別，確認欄位名
   2. 檢查前端 `features/{feature}/api/types.ts` 或 `model/` 下的 DTO type
   3. 確認三者欄位名完全一致
   4. 若前端 ViewModel 欄位名 ≠ 後端欄位名，**必須**在合約中用 `frontendAdapterMapping` 明確記錄映射關係

   **常見錯誤範例：**
   | 後端 DTO | 合約 requiredFields | 前端 DTO | 問題 |
   |:---|:---|:---|:---|
   | `organizationId` | `id` | `id` | ❌ 合約與後端不一致 |
   | `employeeName` | `employeeName` | `name` | ❌ 前端與合約不一致 |
   | `status` = `PROBATION` | 未定義 enum 值 | `status \|\| 'ACTIVE'` | ❌ 靜默 fallback |

5. **Adapter 函式的 enum 值必須在合約中列舉**

   如果回應欄位包含 enum 值（如 `status`），合約的人類可讀層必須列出所有可能值：
   ```json
   {
     "frontendAdapterMapping": {
       "status": {
         "backendField": "status",
         "possibleValues": ["ACTIVE", "INACTIVE", "PROBATION", "LOCKED", "PENDING"],
         "note": "前端必須處理所有可能值，禁止靜默 fallback"
       }
     }
   }
   ```

### 階段二：補充測試程式

**步驟：**

1. **找到對應的測試類別**
   ```
   backend/hrms-{service}/src/test/java/.../api/contract/{Entity}ContractTest.java
   ```

2. **判斷測試類型**

   | 測試基類 | 用途 | 資料庫 |
   |:---|:---|:---|
   | `BaseContractTest` | 合約驗證（整合測試） | 真實 H2 DB |
   | `BaseApiContractTest` | 合約驗證（Mock Repository） | 無 |
   | `BaseApiIntegrationTest` | API 整合測試 | 真實 H2 DB |

3. **建立測試方法**

   **Query 測試（使用 BaseContractTest）：**
   ```java
   @Test
   @DisplayName("{SCENARIO_ID}: {場景描述}")
   void methodName_{SCENARIO_ID}() throws Exception {
       ContractSpec contract = loadContractFromMarkdown(contractSpec, "{SCENARIO_ID}");

       var result = mockMvc.perform(get("/api/v1/{resource}"))
               .andExpect(status().isOk())
               .andReturn();

       String responseJson = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
       verifyQueryContract(null, responseJson, contract);
   }
   ```

   **Command 測試（使用 BaseContractTest）：**
   ```java
   @Test
   @DisplayName("{SCENARIO_ID}: {場景描述}")
   void methodName_{SCENARIO_ID}() throws Exception {
       ContractSpec contract = loadContractFromMarkdown(contractSpec, "{SCENARIO_ID}");

       Map<String, Object> request = new HashMap<>();
       request.put("field", "value");

       Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("table_name");

       var result = mockMvc.perform(post("/api/v1/{resource}")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andReturn();

       if (result.getResponse().getStatus() != 200) return;

       Map<String, List<Map<String, Object>>> afterSnapshot = captureDataSnapshot("table_name");
       List<Map<String, Object>> capturedEvents = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());
       verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
   }
   ```

   **錯誤場景測試（不需合約解析）：**
   ```java
   @Test
   @DisplayName("{SCENARIO_ID}: {錯誤場景描述}")
   void methodName_{SCENARIO_ID}() throws Exception {
       Map<String, Object> request = new HashMap<>();
       request.put("field", "duplicate_value");

       mockMvc.perform(post("/api/v1/{resource}")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isConflict()); // 或 isUnauthorized() 等
   }
   ```

### 階段三：執行測試驗證

**步驟：**

1. **編譯 hrms-common**（ContractSpec 等基類在此模組）
   ```bash
   cd backend && JAVA_HOME="..." mvn install -pl hrms-common -DskipTests
   ```

2. **執行合約測試**
   ```bash
   mvn test -Dtest="{TestClassName}" -pl hrms-{service}
   ```

3. **分析失敗原因**

   | 失敗類型 | 原因 | 處理方式 |
   |:---|:---|:---|
   | `Unrecognized field` | 合約 JSON 有 ContractSpec 不認識的欄位 | 確認 `@JsonIgnoreProperties(ignoreUnknown = true)` |
   | `缺少必要欄位` | `requiredFields` 名稱與後端 DTO 不一致 | 修正合約文件中的欄位名 |
   | `Status 500` | H2 schema 與 Mapper SQL 不同步 | 補充缺少的欄位到 schema |
   | `Status 409/401` 符合預期 | 錯誤場景測試通過 | 正常 |

4. **依 CLAUDE.md 規則 19 處理失敗**
   - 測試寫錯 → 修測試
   - 程式邏輯錯誤 → 修程式
   - 合約內容錯誤 → 修合約
   - 暫時無法修復 → 留 TODO

### 階段四：前後端一致性驗證（新增/修改合約時必做）

**步驟：**

1. **欄位名稱三方比對**
   ```
   後端 Response DTO → 合約 requiredFields → 前端 API DTO type
   ```
   - 讀取後端 `api/response/{Entity}*Response.java` 的欄位
   - 讀取前端 `features/{feature}/api/types.ts` 的 DTO 介面
   - 確認合約 `requiredFields.name` 與兩者完全一致

2. **Adapter 函式檢查**
   - 找到前端對應的 `adapt*()` 函式
   - 確認沒有 `|| 'DEFAULT'` 靜默 fallback
   - 確認有處理所有後端可能回傳的 enum 值

3. **錯誤處理覆蓋檢查**
   - 確認合約的錯誤場景（`_ERR` 場景）都有對應的前端錯誤處理
   - 確認前端 API 層處理了 400/401/403/404/409/500

4. **測試資料品質檢查**
   - 確認 `test-data/` 中的 SQL 使用中文名、多種 status、邊界值
   - 確認測試資料量與欄位多樣性足夠反映真實場景

---

## 注意事項

### HTTP 狀態碼對照表

| 錯誤碼 | HTTP Status | 說明 |
|:---|:---:|:---|
| `LOGIN_FAILED` | 401 | 帳號密碼錯誤 |
| `ACCOUNT_LOCKED` | 401 | 帳號鎖定 |
| `USER_INACTIVE` | 400 | 帳號停用（非鎖定） |
| `RESOURCE_ALREADY_EXISTS` | 409 | 資源已存在（如代碼重複） |
| 驗證失敗 | 400 | Request DTO 驗證不通過 |
| 權限不足 | 403 | 缺少必要權限 |

### 測試資料來源

各服務的測試資料 SQL 位於：
```
backend/hrms-{service}/src/test/resources/test-data/
```

### 變數替換機制

```java
@BeforeEach
void setUp() throws Exception {
    contractSpec = loadContractSpec("{service}");
    contractSpec = contractSpec.replace("{currentUserTenantId}", TestData.TENANT_ID);
}
```

---

## 參考文件

| 文件 | 用途 |
|:---|:---|
| `framework/testing/04_合約驅動測試.md` | 合約測試架構設計 |
| `framework/testing/測試架構規範.md` | 完整測試規範 |
| `contracts/{service}_contracts.md` | 各服務合約規格 |
| `backend/hrms-common/src/main/java/.../test/contract/` | BaseContractTest、ContractSpec 等基類 |
