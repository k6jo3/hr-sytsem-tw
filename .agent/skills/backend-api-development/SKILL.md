---
name: 後端開發指南 (Backend API Development)
description: 指導如何在 HRMS 系統中開發微服務後端 API，遵循 DDD、CQRS、Service Factory 與宣告式架構。
---

# 後端 API 開發 (Backend API Development)

本指南規範在 HRMS 系統中開發新的 後端 API 或功能時，必須遵守的架構與實作原則。

## 1. 核心架構原則 (DDD + CQRS)
*   **四層架構**：嚴格遵守 Interface (介面) -> Application (應用) -> Domain (領域) -> Infrastructure (基礎設施) 分層。
    *   **Interface (Controller)**：只負責接收 HTTP 請求與定義 Swagger 規格。
    *   **Application (Service/Task)**：只做「編排 (Orchestration)」，不做「決策 (Decision)」。如果有超過 2 個以上的步驟，必須切割成獨立的 `Task` 類別並使用 `Business Pipeline` 組合。
    *   **Domain (Aggregate/Entity/Value Object)**：純 Java POJO，封裝所有核心業務邏輯與規則。盡可能採用工廠模式 (Factory) 建立實體。
    *   **Infrastructure (Repository/DAO)**：負責與 DB 或外部服務溝通。採用 Fluent Query Engine 進行查詢。
*   **CQRS 讀寫分離**：
    *   **Command (寫入)**：繼承 `CommandBaseController`，操作對應的 Command Service (`CommandApiService<Req, Res>`)。
    *   **Query (讀取)**：繼承 `QueryBaseController`，操作對應的 Query Service (`QueryApiService<Req, Res>`)。

## 2. API 開發流程 (Service Factory Pattern)
本系統使用 AOP 動態解析 Service 實作，Controller **不直接 Inject** Application Service。
1.  **定義 Controller 方法**：
    ```java
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(
            @Validated @RequestBody CreateUserRequest request) throws Exception {
        // 方法名稱 "createUser" 會被 AOP 攔截
        return ResponseEntity.ok(execCommand(request, currentUser)); 
    }
    ```
2.  **實作 Application Service**：
    *   命名規則：`{ControllerMethodName}ServiceImpl`
    *   標註 `@Service("{controllerMethodName}ServiceImpl")`，**Bean 名稱必須完全對應 Controller 的方法名稱加上 `ServiceImpl` 字尾**。
    ```java
    @Service("createUserServiceImpl")
    @Transactional
    public class CreateUserServiceImpl implements CommandApiService<CreateUserRequest, CreateUserResponse> {
        @Override
        public CreateUserResponse execCommand(CreateUserRequest request, JWTModel currentUser, String... args) throws Exception {
            // 實作業務編排
        }
    }
    ```

## 3. Business Pipeline 與 Task 拆分
當業務邏輯大於或等於 2 個步驟時，應用層服務應透過 `PipelineBuilder` 串接各個 Task，並定義專屬的 `Context` 來傳遞狀態。
```java
return PipelineBuilder.<CreateUserContext>create(context)
        .execute(validateUserTask)
        .execute(saveUserDataTask)
        .execute(publishUserCreatedEventTask)
        .build()
        .run()
        .getResponse(); // 回傳 DTO
```

## 4. 領域事件與查詢建構器 (QueryBuilder)
*   **領域事件 (Domain Events)**：所有關鍵狀態變更，必須觸發事件（如 `EmployeeCreatedEvent`），透過 Spring `EventPublisher` 廣播予其他微服務或非同步流程。
*   **查詢引擎 (QueryBuilder)**：
    *   若傳入的是含有 `@QueryFilter` 的 DTO，使用 `QueryBuilder.where().fromDto(dto)`。
    *   若傳入的是含有 `@QueryCondition.*` (如 `EQ`, `LIKE`, `IN`) 的查詢條件類別 (Condition Object)，**必須**使用 `QueryBuilder.fromCondition(condition)`，否則會發生條件丟失的問題。

## 5. 常見雷區預防
*   **UUID 型態對照**：Repository 或 QueryDSL 查詢有 UUID 定義的 DB 欄位時，必須傳入原生的 `java.util.UUID` 物件，**禁止**直接傳遞 `String`。
*   **依賴倒置 (DIP)**：Application 依賴 Domain 定義的 `IRepository` 介面，而不是直接依賴 Infrastructure 的 DAO 實作。
