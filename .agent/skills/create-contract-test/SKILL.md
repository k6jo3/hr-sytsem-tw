---
name: 建立合約測試 (Create Contract Test)
description: 指導如何針對 HRMS 系統撰寫符合規範的合約測試（包含 API 合約與 Business Pipeline 合約），涵蓋 TDD 與 Markdown 合約斷言原則。
---

# 建立合約測試 (Create Contract Test) 指南

在 HRMS 系統中，合約驅動測試 (Contract-Driven Testing) 是測試架構的核心。撰寫合約測試時，請務必遵守以下規範：

## 1. 檔案位置與命名規範
*   **Markdown 合約檔**：放置於專案根目錄的 `contracts/` 目錄中。例如：`contracts/attendance_contracts.md`。
*   **Java 測試檔**：
    *   API 合約測試：`src/test/java/.../api/contract/{Module}ApiContractTest.java`
    *   業務合約測試：`src/test/java/.../application/contract/{Module}ContractTest.java`

## 2. 測試類別繼承與基礎設定
*   所有合約測試都**必須**繼承 `BaseContractTest`。
*   在測試類別上方掛上：
    ```java
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    ```
*   針對 `@Transactional` 進行環境重置，並透過 `@Sql` 載入初始化及清理腳本（如：`xxx_test_data.sql` 和 `cleanup.sql`）。

## 3. 測試撰寫的三大區塊 (Given / When / Then)
*   **Given (準備)**：
    *   從 Markdown 讀取合約規格：`ContractSpec contract = loadContract("模組名稱", "場景ID");`
    *   紀錄操作前的資料快照：`Map<String, List<Map<String, Object>>> beforeSnapshot = captureDataSnapshot("table1", "table2");`
    *   設置安全性 Mock 上下文（若需要當前使用者身份）：例如設置 `employeeId`。
*   **When (執行)**：
    *   若為 API 測試，利用 `MockMvc` 發送請求。
    *   若為 Business 測試，呼叫對應的 Service 方法（如 `CommandApiService.execCommand(...)` 或 Repository 的 `findAll(QueryGroup)`）。
*   **Then (驗證)**：
    *   擷取操作後的資料快照：`afterSnapshot = captureDataSnapshot(...)`
    *   取得或準備領域事件 (Domain Events)
    *   呼叫驗證：
        *   Command 測試：`verifyCommandContract(beforeSnapshot, afterSnapshot, capturedEvents, contract);`
        *   Query 測試：`contractEngine.assertQueryContract(queryGroup, expectedQuery, contract.getScenarioId());`

## 4. QueryBuilder 重要注意事項 (防呆)
*   如果你的查詢條件類別 (Condition Class) 是使用 **`@QueryCondition.*`** (如 `@QueryCondition.EQ`, `@QueryCondition.LIKE`) 標註欄位：
    *   **絕對必須**使用 `QueryBuilder.fromCondition(conditionObject)` 來解析產生 `QueryGroup`。
    *   **嚴禁**使用 `QueryBuilder.where().fromDto(dto)`，因為 `fromDto` 僅支援 `@QueryFilter` 標註，會導致找不到條件並產生一個完全無條件的查詢。
*   Repository 中 `UUID` 型別的欄位（如 `department_id`），傳入條件時，必須傳入 `UUID` 物件本身，而**不是**字串 (`UUID.toString()`)，否則在 QueryDSL 產生 SQL 時將遇到資料型別不匹配例外 (`Argument did not match parameter type`)。

## 5. Markdown 合約撰寫結構
你的 `.md` 合約檔內必須包含特定的註解包裹標籤，以便正規表達式或引擎解析與自動校對比對：
```markdown
<!-- CONTRACT: RPT_CMD_001 -->
### 預期資料異動 (Expected Data Changes)
* `table_name` INSERT/UPDATE:
  * `field1` = `value1`
  * `status` = 'COMPLETED' (注意：字串狀態值一定要存在於程式碼列舉 Enum 中)
<!-- END CONTRACT -->
```
