---
name: contract-driven-test
description: 自動化執行「合約驅動測試」的完整開發流程，從合約文件補充到測試執行驗證
user_invocable: true
---

# Contract-Driven Test Skill

**名稱：** 合約驅動測試開發流程
**版本：** 1.0
**適用範圍：** 所有微服務的合約測試開發

---

## 執行流程

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
     "request": {},
     "expectedQueryFilters": [
       {"field": "{field_name}", "operator": "=", "value": "{value}"},
       {"field": "tenant_id", "operator": "=", "value": "{currentUserTenantId}"},
       {"field": "is_deleted", "operator": "=", "value": false}
     ],
     "expectedResponse": {
       "statusCode": 200,
       "dataPath": "data.content",
       "minRecords": 0,
       "requiredFields": [
         {"name": "{fieldName}", "type": "{type}", "notNull": true}
       ],
       "pagination": {
         "required": true
       },
       "assertions": [
         {"field": "{fieldName}", "operator": "equals", "value": "{expectedValue}"}
       ]
     }
   }
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
     "request": {},
     "businessRules": [
       "{業務規則描述 1}",
       "{業務規則描述 2}"
     ],
     "expectedDataChanges": {
       "tables": ["table_name"],
       "operations": [
         {
           "type": "INSERT",
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
   - 按功能模組分組測試類別

2. **建立或更新測試類別**

   **測試類別範本：**
   ```java
   package com.company.hrms.{service}.api.contract;

   import static org.mockito.ArgumentMatchers.*;
   import static org.mockito.Mockito.*;

   import java.util.*;
   import org.junit.jupiter.api.*;
   import org.mockito.ArgumentCaptor;

   import com.company.hrms.common.test.contract.BaseContractTest;
   import com.company.hrms.common.test.contract.ContractSpec;
   import com.company.hrms.{service}.domain.repository.*;

   public class {Entity}ApiContractTest extends BaseContractTest {
       // Query 測試和 Command 測試的 @Nested 類別
   }
   ```

3. **建立 Query 測試方法** - 驗證過濾條件 + 回應結果

4. **建立 Command 測試方法** - 驗證資料異動 + 領域事件

5. **檢查編譯錯誤**
   ```bash
   cd backend/hrms-{service}
   mvn test-compile
   ```

**輸出：**
- 測試類別檔案（新建或更新）
- 編譯成功確認

---

### 階段三：執行測試驗證

**目標：** 執行合約測試，驗證實作是否符合合約規格

**執行步驟：**

1. **編譯 hrms-common 模組**（如果尚未編譯）
   ```bash
   cd backend/hrms-common
   mvn clean install -DskipTests
   ```

2. **執行合約測試**
   ```bash
   cd backend/hrms-{service}
   mvn test -Dtest=*ContractTest
   ```

3. **分析測試結果**
   - 成功 - 實作完全符合合約
   - 合約驗證失敗 - 實作缺少過濾條件或資料不符
   - 執行錯誤 - 測試執行時發生錯誤

4. **根據 CLAUDE.md 規則 17 處理失敗**
   - 先確認是測試本身寫錯還是程式邏輯錯誤
   - 在需修正處留下 TODO 說明

**輸出：**
- 測試執行結果報告
- 問題清單與 TODO 標記

---

## 注意事項

### 合約定義原則
- **由 SA 定義** - 合約是 SA 的工作產出
- **業務導向** - 合約描述業務規則，不是技術實作
- **完整性** - 三層驗證：輸入 + 輸出 + 副作用

### 測試實作原則
- **Query 測試** - 驗證過濾條件 + 回應結果
- **Command 測試** - 驗證資料異動 + 領域事件
- **參數替換** - `{currentUserTenantId}` 等佔位符需在測試中替換

### 常見錯誤處理
- 合約檔案載入失敗 → 檢查 `loadContractSpec()` 參數
- Repository 方法簽名不符 → 使用正確的方法
- ContractSpec 找不到 → 先編譯 hrms-common 模組
- 變數替換失效 → 使用 `loadContractFromMarkdown(contractSpec, scenarioId)`

---

## 參考文件

- **測試架構規範：** `framework/testing/測試架構規範.md`
- **BaseContractTest API：** `backend/hrms-common/src/test/java/.../BaseContractTest.java`
- **合約規格：** `contracts/{service}_contracts.md`
