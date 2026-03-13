# CLAUDE.md - AI Assistant Guide

**Version:** 4.2
**Last Updated:** 2026-03-13

---

## 回答語言

請用繁體中文回答

---

## Coding 注意事項

1. **開發前必須完成所有前置文件** — 需求分析、系統分析（Use Case / Activity Diagram）、系統設計（Class / State / Sequence Diagram）、資料庫設計（ERD）、API 設計、UI/UX 設計（UI Flow）、合約測試規格，全部確認後才能寫程式（詳見 `/pre-dev-checklist` skill）
2. **所有功能開發、修改功能、調整邏輯，必須走 TDD 流程**（詳見 `/tdd` skill）
3. **合約測試是 TDD 的起點** — 開發前必須先確認 `contracts/{service}_contracts.md` 中是否有對應場景，沒有就先補合約（詳見 `/contract-driven-test` skill）
4. 全部的程式含架構設計，需符合 SOLID 與 clean code 的原則
5. 針對抽象泛型的實體建立，以工廠模式為第一優先（非絕對，但優先）
6. 程式碼需符合資安規範
7. 所有的 api 實作時需確認 domain 所提供的方法與事件，若所需的方法或事件不存在，再自行補充
8. 實作時，必須說明流程，與這時所要調用的事件或方法
9. 前端開發時，需確認每個事件或 api，後端是否有提供相符合的 api 或事件解決方案，並針對缺漏的部份建立相關文件，以供後端修正
10. 先確認 framework 底下的全部文件，再進行開發
11. repository、dao 層的開發參考 `framework/architecture/Fluent-Query-Engine.md`
12. 查詢條件的建立用的是 QueryBuilder
13. application service 需依照 `framework/architecture/03_Business_Pipeline.md` 去實做，原則上步驟 ≥ 2 步以上就需建立 task
14. 註解用繁體中文
15. 開發前，先確認系統分析書、設計書、api 規格文件等
16. API 會建立 swagger 文件，請依照 api 規格文件去實做
17. 整合測試只在 api 的端點測試執行
18. 測試重點在於合約測試、domain 以及 business pipeline 的單元測試、api 的整合測試
19. 有測試失敗的部份，需先確認是測試本身寫錯還是程式邏輯錯誤或是合約測試文件內容有錯誤，看是那邊有錯就修那邊，先在需修正的部份留下 todo 說明要修正什麼即可，不用立即修正
20. 合約測試的重點在於案例的正確，而不是 sql 的生成是否符合
21. **合約變更必須同步測試程式** — 新增/修改合約場景後，必須有對應的測試方法，否則合約形同虛設
22. **H2 測試 schema 必須與 Mapper SQL 同步** — 新增欄位時需同步更新 `test-data/` 下的 SQL
23. **前端 Adapter 函式必須有單元測試** — 每個 `adapt*Response()` / `adapt*Item()` 函式都必須測試欄位缺失、null 值、未知 enum 值（如 `PROBATION`）的行為，**禁止使用 `|| 'DEFAULT'` 靜默 fallback**
24. **合約 `requiredFields` 必須三方一致** — 後端 Response DTO 欄位名 = 合約 `requiredFields.name` = 前端 API DTO type 欄位名，不一致時透過合約的 `frontendAdapterMapping` 明確記錄映射關係
25. **錯誤處理必須覆蓋所有 HTTP 狀態碼** — 前端 API 層必須處理 400/401/403/404/409/500，後端 `GlobalExceptionHandler` 的每個 ErrorCode → HTTP Status 映射都必須有整合測試
26. **測試資料必須反映真實場景** — 使用中文名、各種 status（含 PROBATION/INACTIVE/LOCKED）、邊界值；禁止只用 3 筆英文名簡化資料
27. **H2 與 PostgreSQL 差異意識** — H2 不支援 PostgreSQL 專有語法（JSON 函式、`::` 型別轉換、部分索引）。涉及這些語法時必須在合約測試留下 `// TODO: H2 不支援，需 Testcontainers 驗證` 標記
28. **前後端欄位映射禁止靜默轉換** — Adapter 中 `dto.status || 'ACTIVE'` 這類 fallback 會隱藏後端回傳未知值的問題，應改為明確檢查 + console.warn 或拋錯

### 架構師三原則

* **原則一：能用「宣告」的，就不要用「程式碼」**（如 `QueryFilter` 註解）
* **原則二：能回傳「結構化物件」的，就不要回傳「基本型別」**（方便快照測試）
* **原則三：Service 只做「編排（Orchestration）」，不做「決策（Decision）」**

---

## 專案概述

企業級 HR 暨專案管理系統，14 個微服務，採 DDD + CQRS + Event-Driven 架構。

### 微服務一覽

| 代碼 | 服務 | 說明 | 後端 | 前端 | Local Profile |
|:---:|:---|:---|:---:|:---:|:---:|
| 01 | IAM | 認證、RBAC、SSO、多租戶 | Scaffolded | Complete | 8081 |
| 02 | Organization | 員工生命週期、組織結構 | Scaffolded | Complete | 8082 |
| 03 | Attendance | 打卡、請假、加班、彈性工時 | Scaffolded | Complete | 8083 |
| 04 | Payroll | 薪資計算（Saga）、稅務 | Scaffolded | Complete | 8084 |
| 05 | Insurance | 勞健保、退休金 | Scaffolded | Complete | 8085 |
| 06 | Project | 客戶、多層 WBS、成本追蹤 | Scaffolded | Complete | 8086 |
| 07 | Timesheet | 週報、PM 審核 | Scaffolded | Complete | 8087 |
| 08 | Performance | 考核週期、彈性表單 | Scaffolded | Complete | 8088 |
| 09 | Recruitment | 職缺、Kanban、面試 | Scaffolded | Complete | 8089 |
| 10 | Training | 課程管理、證照 | Scaffolded | Complete | 8090 |
| 11 | Workflow | 視覺化流程設計、多層簽核 | Scaffolded | Skeleton | 8091 |
| 12 | Notification | Email/Push/Teams/LINE | Scaffolded | Skeleton | 8092 |
| 13 | Document | 儲存、版控、範本、加密 | Scaffolded | Skeleton | 8093 |
| 14 | Reporting | CQRS ReadModel、儀表板 | Scaffolded | Skeleton | 8094 |

**進度：** 設計 14/14 | 後端骨架 14/14 | Local Profile 14/14 | 前端完整 10/14 | 前端骨架 4/14 | E2E 驗證 14/14

---

## 目錄結構

```
hr-system-2/
├── knowledge/                    # 規格文件
│   ├── 01_Client_Requirements/   # 客戶需求
│   ├── 02_Requirements_Analysis/ # 需求分析書 (14 服務)
│   ├── 02_System_Design/         # 系統設計書 (14 服務)
│   ├── 03_Logic_Specifications/  # 複雜業務邏輯規格
│   ├── 04_API_Specifications/    # API 詳細規格 (14 服務)
│   └── 05_Reports/               # 合規性報告
├── framework/                    # 架構與開發規範
│   ├── architecture/             # DDD、Business Pipeline、Query Engine
│   ├── development/              # 開發流程、命名規範、前後端指南
│   ├── testing/                  # 測試架構（三階測試法、合約驅動）
│   └── templates/                # 後端模組模板
├── contracts/                    # 合約測試規格 (14 服務)
├── backend/                      # 後端微服務 (14 modules + common)
└── frontend/                     # React 前端 (14 features)
```

---

## 架構與設計模式

### DDD 四層架構

每個微服務 **必須** 遵循此分層：

| 層級 | 職責 | 關鍵規則 |
|:---|:---|:---|
| Interface（介面層） | REST Controllers、Request/Response DTO | 命名：`HR{DD}{Screen}{Cmd/Qry}Controller` |
| Application（應用層） | Use Case 編排、Saga 協調 | **不含** 核心業務規則 |
| Domain（領域層） | Aggregate Root、Entity、Value Object、Domain Service | 純 Java POJO，**不依賴** 任何框架 |
| Infrastructure（基礎設施層） | Repository 實作、DAO、Mapper、外部適配器 | |

### CQRS

Controllers 分為 Command（POST/PUT/DELETE）和 Query（GET）：
- Command: `HR01UserCmdController extends CommandBaseController`
- Query: `HR01UserQryController extends QueryBaseController`

### Service Factory 模式

Controller method name 自動對應 Service bean name：
- 方法 `createUser()` → 自動解析 bean `createUserServiceImpl`
- 透過 `ApiServiceAspect` (AOP) + `BeanNameConfig` (request-scoped) 實現
- Controller 呼叫 `execCommand(request, currentUser)` 或 `getResponse(request, currentUser)`

```java
// Controller
@PostMapping
public ResponseEntity<CreateUserResponse> createUser(...) throws Exception {
    return ResponseEntity.ok(execCommand(request, currentUser));
}

// Service（bean name 必須匹配）
@Service("createUserServiceImpl")
@Transactional
public class CreateUserServiceImpl implements CommandApiService<CreateUserRequest, CreateUserResponse> {
    @Override
    public CreateUserResponse execCommand(CreateUserRequest req, JWTModel currentUser, String... args) { }
}
```

### Event-Driven（Kafka）

服務間透過 Domain Event 非同步通訊：
- 事件命名：`{Aggregate}{PastVerb}Event`（如 `EmployeeCreatedEvent`）
- 發布：`eventPublisher.publish(new EmployeeCreatedEvent(...))`

### 前端 Factory 模式（強制）

**禁止** 直接使用 API 回傳資料，必須經過 Factory 轉換：

```typescript
// API DTO → ViewModel
const viewModel = UserViewModelFactory.createFromDTO(dto);
return <div>{viewModel.fullName}</div>;
```

---

## 命名規範

### Domain Code

| 代碼 | 後端 Package | 前端 Feature |
|:---:|:---|:---|
| 01 | `com.company.hrms.iam` | `features/auth` |
| 02 | `com.company.hrms.organization` | `features/organization` |
| 03 | `com.company.hrms.attendance` | `features/attendance` |
| 04 | `com.company.hrms.payroll` | `features/payroll` |
| 05 | `com.company.hrms.insurance` | `features/insurance` |
| 06 | `com.company.hrms.project` | `features/project` |
| 07 | `com.company.hrms.timesheet` | `features/timesheet` |
| 08 | `com.company.hrms.performance` | `features/performance` |
| 09 | `com.company.hrms.recruitment` | `features/recruitment` |
| 10 | `com.company.hrms.training` | `features/training` |
| 11 | `com.company.hrms.workflow` | `features/workflow` |
| 12 | `com.company.hrms.notification` | `features/notification` |
| 13 | `com.company.hrms.document` | `features/document` |
| 14 | `com.company.hrms.reporting` | `features/report` |

### 後端命名

| 元素 | 格式 | 範例 |
|:---|:---|:---|
| Controller (Cmd) | `HR{DD}{Screen}CmdController` | `HR01UserCmdController` |
| Controller (Qry) | `HR{DD}{Screen}QryController` | `HR01UserQryController` |
| Application Service | `{Verb}{Noun}ServiceImpl` | `CreateUserServiceImpl` |
| Domain Service | `{Event}DomainService` | `AccountLockingDomainService` |
| Request DTO | `{Verb}{Noun}Request` | `CreateUserRequest` |
| Response DTO | `{Noun}{Type}Response` | `UserDetailResponse` |
| Domain Event | `{Aggregate}{PastVerb}Event` | `UserCreatedEvent` |
| Repository | `I{Noun}Repository` / `{Noun}RepositoryImpl` | `IUserRepository` |
| PO | `{Noun}PO` | `UserPO` |

### 前端命名

| 元素 | 格式 | 範例 |
|:---|:---|:---|
| Page | `HR{DD}{PageName}Page.tsx` | `HR01LoginPage.tsx` |
| Factory | `{Name}ViewModelFactory.ts` | `UserViewModelFactory.ts` |
| API Module | `{Feature}Api.ts` | `AuthApi.ts` |
| Hook | `use{Name}.ts` | `useLogin.ts` |

### API 路徑

```
/api/v{version}/{resource-plural}/{id}/{sub-resource}/{action}
```

---

## 開發流程

> **核心原則：先文件 → 先合約 → 先測試 → 再實作 → 串接驗證**
>
> **完整流程：** `/pre-dev-checklist`（前置文件）→ `/tdd`（開發）→ `/contract-driven-test`（合約驗證）→ **串接驗證閘門**（啟動後端+前端，實際操作確認）
>
> **串接驗證閘門未通過，功能不可標記為完成。** 測試全綠 ≠ 功能正確。

### 後端新增 API（TDD 流程）

1. **確認合約**：讀 `contracts/{service}_contracts.md`，找對應場景 ID；若缺少場景，先執行 `/contract-driven-test` 補充合約
2. **讀規格**：`knowledge/02_System_Design/{NN}_*.md` + `knowledge/04_API_Specifications/{NN}_*.md`
3. **寫失敗測試（Red）**：在 `api/contract/` 建立合約測試 + Domain 單元測試
4. 定義 Request/Response DTO
5. 建立 Controller method（method name 決定 service bean name）
6. 實作 Service：`@Service("{methodName}ServiceImpl")` + Pipeline Tasks
7. 實作 Domain 邏輯（純 POJO，驗證在 Domain 層）
8. **執行測試通過（Green）**：確認合約測試 + 單元測試 + 整合測試全部通過
9. **重構（Refactor）**：確認 SOLID + Clean Code，測試仍通過
10. 加 Swagger 註解

### 後端修改功能 / 調整邏輯（TDD 流程）

1. **確認合約是否需要更新**（HTTP 狀態碼、回應欄位、業務規則變更）
2. **先改測試**：修改測試預期值或補充新測試案例（Red）
3. **再改實作**：修改程式碼讓測試通過（Green）
4. **同步更新合約文件**：確保合約 ↔ 測試 ↔ 實作三者一致

### 前端新增頁面（TDD 流程）

1. 讀設計書（UX Flow、wireframe）+ 確認後端 API 合約
2. **先寫測試**：Factory test → Hook test → Component test
3. 建立 `src/pages/HR{DD}{Name}Page.tsx`
4. 建立 feature module：`api/` + `factory/` + `components/` + `hooks/` + `model/`
5. **必須** 實作 Factory（DTO → ViewModel 轉換，欄位映射參考合約的 `frontendAdapterMapping`）
6. 確認所有測試通過

---

## 測試規範

### 覆蓋率要求

| 層級 | 覆蓋率 |
|:---|:---:|
| Domain 邏輯 | 100% |
| API 端點 | 整合測試必要 |
| GlobalExceptionHandler | 所有 ErrorCode 映射必須有測試 |
| 前端 Factory | 單元測試必要 |
| 前端 Adapter | 單元測試必要（含缺失欄位、null、未知 enum） |
| 前端 Component | 組件測試必要 |
| 前端 Hook | Hook 測試必要 |

### 合約驅動測試（Contract-Driven Test）

- SA 定義業務規格 → 工程師依合約實作 → 自動驗證（非對稱驗證）
- 合約規格檔案位於 `contracts/{service}_contracts.md`
- 三層驗證：輸入 + 輸出 + 副作用
- **合約文件的 JSON 區塊是機器可讀的**，會被 `BaseContractTest.loadContractFromMarkdown()` 解析
- **新增/修改合約場景後，必須同步補充測試程式**（否則合約形同虛設）
- **合約變更的預期行為**：SA 修改合約 → 現有測試失敗（預期中）→ 工程師更新實作/測試 → 測試通過
- 使用 `/contract-driven-test` skill 自動化執行合約補充與測試建立流程
- 詳見 `framework/testing/04_合約驅動測試.md`

---

## 重要參考文件

| 文件 | 用途 | 何時查閱 |
|:---|:---|:---|
| `framework/architecture/03_Business_Pipeline.md` | 宣告式業務流水線 | 實作複雜業務邏輯 |
| `framework/architecture/Fluent-Query-Engine.md` | 宣告式查詢引擎 | 寫 Repository 查詢 |
| `framework/testing/測試架構規範.md` | 完整測試規範 | 寫測試前 |
| `knowledge/02_System_Design/{NN}_*.md` | 各服務系統設計 | 實作前必讀 |
| `knowledge/04_API_Specifications/{NN}_*.md` | API 詳細規格 | 實作 API 端點 |
| `knowledge/03_Logic_Specifications/*.md` | 複雜規則（工時、稅保、SSO） | 實作薪資/考勤/保險 |
| `frontend/src/config/MockConfig.ts` | Mock 開關 | 前後端串接時 |
| `frontend/vite.config.ts` | Vite Proxy 配置 | API 串接問題排查 |

---

## 技術棧

**後端：** Spring Boot 3.1 | Spring Cloud 2023 | PostgreSQL 15+ | Querydsl + JPA | MyBatis | Redis | Kafka | Eureka

**前端：** React 18 | TypeScript 5 | Vite 5 | Redux Toolkit | Ant Design 5 | ECharts | Axios | React Router 6 | Vitest
