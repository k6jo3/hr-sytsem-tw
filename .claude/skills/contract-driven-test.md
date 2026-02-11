# Contract-Driven Test Skill

**名稱：** 合約驅動測試開發流程
**版本：** 1.0
**適用範圍：** 所有微服務的合約測試開發

---

## 🎯 Skill 目標

自動化執行「合約驅動測試」的完整開發流程，從合約文件補充到測試執行驗證，確保實作符合 SA 定義的業務規格。

---

## 📋 執行流程

### 階段一：補充合約文件

**目標：** 將 SA 定義的業務場景轉換為 JSON Schema 格式的合約定義

**輸入：**
- 服務代碼（如 `01` = IAM）
- 現有合約文件路徑（如 `contracts/iam_contracts.md`）
- API 規格文件（如 `knowledge/04_API_Specifications/01_IAM服務系統設計書_API詳細規格.md`）

**執行步驟：**

1. **讀取現有合約文件**
   ```bash
   檔案：contracts/{service_name}_contracts.md
   ```

2. **檢查合約完整性**
   - 比對 API 規格文件中的所有端點
   - 找出缺少的場景定義
   - 列出需要補充的場景清單

3. **補充 JSON Schema 合約**

   對於每個缺少的場景，建立以下格式：

   **Query 操作（GET）合約範本：**
   ```markdown
   #### {SCENARIO_ID}: {場景名稱}

   **API 端點：** `GET {endpoint}`

   **業務場景描述：**

   {詳細描述業務規則、角色權限、過濾條件等}

   **測試合約：**

   ```json
   {
     "scenarioId": "{SCENARIO_ID}",
     "apiEndpoint": "GET {endpoint}",
     "controller": "{ControllerName}",
     "service": "{ServiceName}",
     "permission": "{permission:action}",
     "request": {
       // 請求參數範例
     },
     "expectedQueryFilters": [
       {"field": "{field_name}", "operator": "=", "value": "{value}"},
       {"field": "tenant_id", "operator": "=", "value": "{currentUserTenantId}"},
       {"field": "is_deleted", "operator": "=", "value": false}
     ],
     "expectedResponse": {
       "statusCode": 200,
       "dataPath": "data.content",  // 或 "data" for single object
       "minRecords": 0,
       "requiredFields": [
         {"name": "{fieldName}", "type": "{type}", "notNull": true}
       ],
       "pagination": {
         "required": true  // List API 需要分頁
       },
       "assertions": [
         {"field": "{fieldName}", "operator": "equals", "value": "{expectedValue}"}
       ]
     }
   }
   ```
   ```

   **Command 操作（POST/PUT/DELETE）合約範本：**
   ```markdown
   #### {SCENARIO_ID}: {場景名稱}

   **API 端點：** `{METHOD} {endpoint}`

   **業務場景描述：**

   {詳細描述業務規則、資料異動、領域事件等}

   **測試合約：**

   ```json
   {
     "scenarioId": "{SCENARIO_ID}",
     "apiEndpoint": "{METHOD} {endpoint}",
     "controller": "{ControllerName}",
     "service": "{ServiceName}",
     "permission": "{permission:action}",
     "request": {
       // 請求參數範例
     },
     "businessRules": [
       "{業務規則描述 1}",
       "{業務規則描述 2}"
     ],
     "expectedDataChanges": {
       "tables": ["table_name"],
       "operations": [
         {
           "type": "INSERT",  // 或 UPDATE, DELETE, SOFT_DELETE
           "table": "table_name",
           "expectedFields": {
             "field_name": "{expected_value}"
           }
         }
       ]
     },
     "expectedDomainEvents": [
       {
         "eventType": "{EventClassName}",
         "expectedFields": {
           "field_name": "{expected_value}"
         }
       }
     ],
     "expectedResponse": {
       "statusCode": 200,
       "dataPath": "data",
       "requiredFields": [
         {"name": "{fieldName}", "type": "{type}", "notNull": true}
       ]
     }
   }
   ```
   ```

4. **驗證合約格式**
   - 確認 JSON 格式正確
   - 確認必要欄位完整
   - 確認場景 ID 唯一性

5. **更新合約文件**
   - 將補充的合約加入文件
   - 更新 API 清單表格
   - 標記實作狀態

**輸出：**
- 更新後的合約文件
- 補充場景清單報告

---

### 階段二：補充測試程式

**目標：** 根據合約文件建立對應的測試類別和測試方法

**輸入：**
- 更新後的合約文件
- 服務測試目錄（如 `backend/hrms-iam/src/test/java/.../api/contract/`）

**執行步驟：**

1. **分析合約場景分類**
   - User 相關場景 → `IamApiContractTest.java`
   - Authentication 相關 → `AuthenticationApiContractTest.java`
   - Permission 相關 → `PermissionApiContractTest.java`
   - Profile 相關 → `ProfileApiContractTest.java`

2. **建立或更新測試類別**

   **測試類別範本：**
   ```java
   package com.company.hrms.{service}.api.contract;

   import static org.mockito.ArgumentMatchers.*;
   import static org.mockito.Mockito.*;
   import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
   import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

   import java.util.*;
   import org.junit.jupiter.api.*;
   import org.mockito.ArgumentCaptor;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
   import org.springframework.boot.test.context.SpringBootTest;
   import org.springframework.boot.test.mock.mockito.MockBean;
   import org.springframework.http.MediaType;
   import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
   import org.springframework.security.core.authority.AuthorityUtils;
   import org.springframework.security.core.context.SecurityContextHolder;
   import org.springframework.test.context.ActiveProfiles;
   import org.springframework.test.web.servlet.MockMvc;

   import com.company.hrms.common.model.JWTModel;
   import com.company.hrms.common.query.QueryGroup;
   import com.company.hrms.common.test.contract.BaseContractTest;
   import com.company.hrms.common.test.contract.ContractSpec;
   import com.company.hrms.{service}.domain.repository.*;
   import com.fasterxml.jackson.databind.ObjectMapper;

   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
   @AutoConfigureMockMvc(addFilters = false)
   @ActiveProfiles("test")
   public class {Entity}ApiContractTest extends BaseContractTest {

       @Autowired
       private MockMvc mockMvc;

       @MockBean
       private I{Entity}Repository repository;

       @Autowired
       private ObjectMapper objectMapper;

       private JWTModel mockUser;

       @BeforeEach
       void setUp() throws Exception {
           mockUser = new JWTModel();
           mockUser.setUserId("user-id");
           mockUser.setUsername("username");
           mockUser.setEmail("user@company.com");
           mockUser.setRoles(Collections.singletonList("ADMIN"));
           mockUser.setTenantId("T001");
       }

       private void mockSecurityContext(JWTModel user) {
           List<String> auths = new ArrayList<>(user.getRoles());
           auths.add("authenticated");
           var authorities = AuthorityUtils.createAuthorityList(auths.toArray(new String[0]));
           var auth = new UsernamePasswordAuthenticationToken(user, "password", authorities);
           SecurityContextHolder.getContext().setAuthentication(auth);
       }

       // Query 測試和 Command 測試的 @Nested 類別
   }
   ```

3. **建立 Query 測試方法**

   **Query 測試範本：**
   ```java
   @Nested
   class {Entity}QueryApiContractTests {
       @Test
       void {testMethodName}_{SCENARIO_ID}() throws Exception {
           // 載入合約
           ContractSpec contract = loadContract("{service}", "{SCENARIO_ID}");

           mockSecurityContext(mockUser);

           // 捕獲 QueryGroup
           ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
           when(repository.findPage(queryCaptor.capture(), any(Pageable.class)))
                   .thenReturn(new PageImpl<>(Collections.emptyList()));

           // 執行 API 並捕獲回應
           var result = mockMvc.perform(get("{endpoint}"))
                   .andExpect(status().isOk())
                   .andReturn();

           String responseJson = result.getResponse().getContentAsString();

           // 驗證合約
           verifyQueryContract(queryCaptor.getValue(), responseJson, contract);
       }
   }
   ```

4. **建立 Command 測試方法**

   **Command 測試範本：**
   ```java
   @Nested
   class {Entity}CommandApiContractTests {
       @Test
       void {testMethodName}_{SCENARIO_ID}() throws Exception {
           // 載入合約
           ContractSpec contract = loadContract("{service}", "{SCENARIO_ID}");

           mockSecurityContext(mockUser);

           // 準備請求
           Map<String, Object> request = new HashMap<>();
           request.put("field1", "value1");
           request.put("field2", "value2");

           // 擷取快照
           Map<String, List<Map<String, Object>>> beforeSnapshot =
               captureDataSnapshot("{table_name}");

           // 執行 API
           mockMvc.perform(post("{endpoint}")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(request)))
                   .andExpect(status().isOk());

           Map<String, List<Map<String, Object>>> afterSnapshot =
               captureDataSnapshot("{table_name}");

           // 驗證合約
           List<Map<String, Object>> capturedEvents = new ArrayList<>();
           verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);
       }
   }
   ```

5. **檢查編譯錯誤**
   ```bash
   cd backend/hrms-{service}
   mvn test-compile
   ```

6. **修正常見問題**
   - Repository 方法簽名不符 → 使用正確的方法
   - Missing imports → 補充 import
   - ContractSpec 找不到 → 確保 hrms-common 已編譯

**輸出：**
- 測試類別檔案（新建或更新）
- 編譯成功確認
- 測試方法清單報告

---

### 階段三：執行測試驗證

**目標：** 執行合約測試，驗證實作是否符合合約規格

**輸入：**
- 測試類別檔案
- 服務專案目錄

**執行步驟：**

1. **編譯 hrms-common 模組**（如果尚未編譯）
   ```bash
   cd backend/hrms-common
   mvn clean install -DskipTests
   ```

2. **執行合約測試**
   ```bash
   cd backend/hrms-{service}
   mvn test -Dtest=*ApiContractTest
   ```

3. **分析測試結果**

   **結果分類：**

   - ✅ **成功** - 實作完全符合合約
   - ⚠️ **合約驗證失敗** - 實作缺少過濾條件或資料不符
   - ❌ **執行錯誤** - 測試執行時發生錯誤（404, 403, SQL 錯誤等）

   **常見問題類型：**

   | 問題類型 | 錯誤訊息關鍵字 | 原因 |
   |:---|:---|:---|
   | 缺少過濾條件 | `缺失的過濾條件: ❌ is_deleted = false` | Service 未加入必要過濾 |
   | API 不存在 | `Status expected:<200> but was:<404>` | Controller 缺少端點 |
   | 權限不足 | `Status expected:<200> but was:<403>` | 權限配置錯誤 |
   | 資料庫錯誤 | `BadSqlGrammar` | 測試資料庫未建立表格 |

4. **建立執行結果報告**

   **報告內容：**
   - 測試執行統計（總數、成功、失敗、錯誤）
   - 問題分類與分析
   - 每個失敗測試的詳細說明
   - 修正建議
   - 下一步行動

5. **輸出報告檔案**
   ```
   contracts/{SERVICE}_合約測試執行結果報告.md
   ```

**輸出：**
- 測試執行結果報告
- 問題清單
- 修正建議清單

---

## 🛠️ 使用方式

### 方式一：完整流程執行

```
請幫我執行合約驅動測試流程，服務代碼：01（IAM）
```

Assistant 會依序執行三個階段：
1. 補充合約文件
2. 補充測試程式
3. 執行測試驗證

### 方式二：單一階段執行

```
請執行合約驅動測試流程的階段二：補充測試程式，服務代碼：02（組織員工）
```

### 方式三：批次執行多個服務

```
請幫我執行合約驅動測試流程，服務代碼：01, 02, 03
```

---

## 📝 注意事項

### 1. 合約定義原則

- **由 SA 定義** - 合約是 SA 的工作產出，不是工程師
- **業務導向** - 合約描述業務規則，不是技術實作
- **完整性** - 三層驗證：輸入 + 輸出 + 副作用

### 2. 測試實作原則

- **Query 測試** - 使用 `verifyQueryContract()`，驗證過濾條件 + 回應結果
- **Command 測試** - 使用 `verifyCommandContract()`，驗證資料異動 + 領域事件
- **參數替換** - `{currentUserTenantId}` 等佔位符需在測試中替換

### 3. 常見錯誤處理

**錯誤一：合約檔案載入失敗**
```
找不到合約檔案: iam_contracts_contracts.md
```
**解決：** 檢查 `loadContractSpec()` 參數，應為 `"iam"` 而非 `"iam_contracts"`

**錯誤二：Repository 方法簽名不符**
```
method findById cannot be applied to given types
```
**解決：** 使用 `findAll(QueryGroup)` 而非 `findById(QueryGroup, String)`

**錯誤三：ContractSpec 找不到**
```
cannot find symbol: class ContractSpec
```
**解決：** 先編譯 hrms-common 模組：`mvn clean install -DskipTests`

### 4. 測試環境設定

**Command 測試需要真實資料庫：**

推薦使用 Testcontainers：
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:tc:postgresql:15:///hrms_test
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

---

## 📊 品質指標

### 合約完整性

- ✅ 所有 API 端點都有合約定義
- ✅ 合約包含完整的三層驗證
- ✅ JSON Schema 格式正確

### 測試覆蓋率

- ✅ 所有合約場景都有對應測試
- ✅ 測試方法命名符合規範
- ✅ 測試能正確執行（可能失敗，但能執行）

### 實作符合度

- 🎯 目標：100% 測試通過
- ⚠️ 現實：初次執行通常 0-30% 通過
- ✅ 價值：測試失敗 = 發現缺陷

---

## 🎓 最佳實踐

### 1. 先定義合約，再寫實作

```
合約（SA）→ 測試（QA）→ 實作（工程師）→ 驗證（自動化）
```

### 2. 合約測試不只是單元測試

- 涵蓋完整 API → Service → Domain → Repository 流程
- 驗證業務規則，不只是技術細節
- 整合測試的角度，單元測試的速度

### 3. 測試失敗是正常的

- 初次執行 0% 通過率很正常
- 重點是「發現問題」而非「全部通過」
- 逐步修正實作，最終達到 100%

### 4. 持續維護合約

- API 變更 → 更新合約
- 新增功能 → 補充合約
- 合約是活文件，不是一次性產出

---

## 📚 參考文件

- **合約測試規範手冊：** `contracts/合約測試規範手冊.md`
- **測試架構規範：** `framework/testing/測試架構規範.md`
- **BaseContractTest API：** `backend/hrms-common/src/test/java/.../BaseContractTest.java`
- **範例合約：** `contracts/iam_contracts.md`
- **範例測試：** `backend/hrms-iam/src/test/java/.../IamApiContractTest.java`

---

**建立者：** Claude Sonnet 4.5
**建立日期：** 2026-02-11
**版本：** 1.0
