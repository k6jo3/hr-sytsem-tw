---
name: 程式碼審查指南 (Backend Code Review)
description: 指導在 HRMS 系統中進行程式碼審查 (Code Review) 時需檢查的架構、資安、命名規範與可測試性原則。
---

# 程式碼審查與重構 (Backend Code Review & Refactoring)

在 PR 或開發完成後進行 Code Review，請以一位嚴格的系統架構師角度，確保新的程式碼符合以下準則。

## 1. 架構原則審查 (Architecture & Layering)
*   **Controller 乾淨度**：Controller 除了負責接收 Request、呼叫 `execCommand` / `execQuery` 以及定義 Swagger `@Operation`，是否包含了業務邏輯？如果有，**退回重構**。
*   **Service 編排原則**：Application Service (`xxxServiceImpl`) 是否做了「決策 (Decision)」而不是「編排 (Orchestration)」？業務規則與狀態判斷必須留在 Domain Model 中。`IF-ELSE` 等核心業務邏輯若寫在 Application Layer，**退回重構**。
*   **Business Pipeline**：步驟若 $\ge 2$ 步（例如：校驗 + 存檔），是否有確實切分成獨立的 Task 並透過 `PipelineContext` 傳遞？
*   **CQRS 界線**：
    *   Command Service 可以寫入資料，也可以發布事件。
    *   Query Service **絕對禁止**修改系統狀態或資料 (No Side Effects)。

## 2. 命名與標註規範 (Naming & Annotations)
*   **Service Bean 名稱**：`CommandApiService` / `QueryApiService` 實作類的 `@Service` 名稱是否字首小寫且後綴 `ServiceImpl`？更重要的是，**是否與 Controller 對應的 Method 名稱完全一致？** 若不一致將導致 AOP 解析失敗拋出 NPE (Bean Not Found)。
*   **DTO 命名**：Request 必須是 `{Verb}{Noun}Request`（Command）或 `{Noun}QueryRequest`（Query），Response 必須是 `{Noun}{Action}Response`。
*   **Swagger 標註**：API 必須具備 `@Operation(summary = "...", operationId = "...")` 以便生成精確的前端 Client Code。

## 3. 型別與可測試性 (Types & Testability)
*   **原則二：回傳結構化物件**：API 或是 Service 方法的回傳值，能用「宣告」與「結構化物件 (DTO/Class)」的，絕不要只回傳基本型別 (Primitive Types 如 String, boolean)。這會導致無法進行有效的 JSON 快照測試。
*   **依賴注入與隔離**：是否全部透過 Constructor Injection 或 `@Autowired` 注入依賴？程式碼不能依賴 `new` 實體化基礎設施元件，確保 100% Mocking 的可能性。
*   **QueryBuilder 使用檢查**：檢查是否誤用 `QueryBuilder.where().fromDto()` 來處理標有 `@QueryCondition` 的物件（正確應為 `fromCondition()`）。

## 4. 安全與併發 (Security & Concurrency)
*   **權限與租戶**：Data 操作是否有限定 `@TenantId` 或組織隔離的範圍 (Organization/Department Isolation)？是否有取用合法的 `JWTModel currentUser` 進行越權防護？
*   **Transaction 管控**：Command Service 或其底層 Repository 操作是否有加上適當的 `@Transactional` 確保 ACID？特別是多表連動更新的情境。
*   **資安掃描盲區**：任何直接拼接 SQL 的行為都是**嚴格禁止**的。必須透過 MyBatis Mapper 的 `#` 或系統內建的 Fluent-Query-Engine / QueryDSL 進行參數化綁定，以防止 SQL Injection。

## 5. 註解與文件 (Documentation)
*   **強制繁體中文**：所有註解與 Javadoc 必須使用繁體中文編寫。若有特殊技術名詞可保留英文原意。
*   **合約測試覆蓋**：新增的 API 或主要業務是否已經有對應的 Markdown 合約規格以及 `BaseContractTest` 的整合測試？若為 TDD 開發，沒有測試即代表尚未完成。
