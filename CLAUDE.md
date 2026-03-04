# CLAUDE.md - AI Assistant Guide

**Version:** 4.0
**Last Updated:** 2026-03-04

---

## 回答語言

請用繁體中文回答

---

## Coding 注意事項

1. 採用 TDD 的開發流程
2. 全部的程式含架構設計，需符合 SOLID 與 clean code 的原則
3. 針對抽象泛型的實體建立，以工廠模式為第一優先（非絕對，但優先）
4. 程式碼需符合資安規範
5. 所有的 api 實作時需確認 domain 所提供的方法與事件，若所需的方法或事件不存在，再自行補充
6. 實作時，必須說明流程，與這時所要調用的事件或方法
7. 前端開發時，需確認每個事件或 api，後端是否有提供相符合的 api 或事件解決方案，並針對缺漏的部份建立相關文件，以供後端修正
8. 先確認 framework 底下的全部文件，再進行開發
9. repository、dao 層的開發參考 `framework/architecture/Fluent-Query-Engine.md`
10. 查詢條件的建立用的是 QueryBuilder
11. application service 需依照 `framework/architecture/03_Business_Pipeline.md` 去實做，原則上步驟 ≥ 2 步以上就需建立 task
12. 註解用繁體中文
13. 開發前，先確認系統分析書、設計書、api 規格文件等
14. API 會建立 swagger 文件，請依照 api 規格文件去實做
15. 整合測試只在 api 的端點測試執行
16. 測試重點在於合約測試、domain 以及 business pipeline 的單元測試、api 的整合測試
17. 有測試失敗的部份，需先確認是測試本身寫錯還是程式邏輯錯誤或是合約測試文件內容有錯誤，看是那邊有錯就修那邊，先在需修正的部份留下 todo 說明要修正什麼即可，不用立即修正
18. 合約測試的重點在於案例的正確，而不是 sql 的生成是否符合

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

### 後端新增 API

1. 讀設計書：`knowledge/02_System_Design/{NN}_*.md` + `knowledge/04_API_Specifications/{NN}_*.md`
2. 定義 Request/Response DTO
3. 建立 Controller method（method name 決定 service bean name）
4. 實作 Service：`@Service("{methodName}ServiceImpl")`
5. 實作 Domain 邏輯（純 POJO，驗證在 Domain 層）
6. 加 Swagger 註解
7. 寫測試（TDD：先寫失敗測試 → 實作 → 重構）

### 前端新增頁面

1. 讀設計書（UX Flow、wireframe）
2. 建立 `src/pages/HR{DD}{Name}Page.tsx`
3. 建立 feature module：`api/` + `factory/` + `components/` + `hooks/` + `model/`
4. **必須** 實作 Factory（DTO → ViewModel 轉換）
5. 寫測試（Factory test 必要、Component test 必要、Hook test 必要）

---

## 測試規範

### 覆蓋率要求

| 層級 | 覆蓋率 |
|:---|:---:|
| Domain 邏輯 | 100% |
| API 端點 | 整合測試必要 |
| 前端 Factory | 單元測試必要 |
| 前端 Component | 組件測試必要 |
| 前端 Hook | Hook 測試必要 |

### 合約驅動測試

- SA 定義業務規格 → 工程師依合約實作 → 自動驗證（非對稱驗證）
- 合約規格檔案位於 `contracts/{service}_contracts.md`
- 三層驗證：輸入 + 輸出 + 副作用
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
