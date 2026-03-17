# 人力資源暨專案管理系統 (HRMS)

**版本:** 4.1
**更新日期:** 2026-03-17
**作者:** k6jo3
**架構:** 微服務架構 (Spring Cloud + React)

> 本專案為個人作品集項目，展示企業級 HR 系統的完整設計與實作能力。

---

## 專案概述

企業級人力資源與專案管理整合平台，採用 **DDD (領域驅動設計)**、**CQRS**、**Event-Driven** 架構，涵蓋完整的 HR 業務流程與專案成本管理功能。

### 核心特色

- **14 個微服務** — 完整的業務領域劃分，全部設計 + 實作完成
- **DDD 四層架構** — Interface / Application / Domain / Infrastructure 嚴格分層
- **CQRS** — Command / Query 分離，獨立 Controller
- **Event-Driven** — Kafka 服務間非同步通訊
- **Business Pipeline** — 宣告式業務流水線，Service 只做編排不做決策
- **Fluent Query Engine** — 宣告式查詢引擎 (Querydsl + JPA)
- **合約驅動測試** — SA 定義規格 → 工程師實作 → 自動驗證
- **法規遵循** — 完全符合台灣勞基法、勞健保法規

---

## 技術棧

### 後端

| 元件 | 技術 |
|:---|:---|
| 核心框架 | Spring Boot 3.1 |
| 微服務治理 | Spring Cloud 2023 |
| 服務發現 | Eureka |
| 認證授權 | Spring Security + OAuth2 + JWT |
| 資料庫 | PostgreSQL 15+ (每服務獨立 DB) |
| ORM | Querydsl + JPA / MyBatis |
| 快取 | Redis 7+ |
| 訊息佇列 | Kafka |
| 雲端平台 | Microsoft Azure |
| Java 版本 | 21 |

### 前端

| 元件 | 技術 |
|:---|:---|
| 核心框架 | React 18 + TypeScript 5 |
| 構建工具 | Vite 5 |
| 狀態管理 | Redux Toolkit |
| UI 元件庫 | Ant Design 5 |
| 圖表 | Apache ECharts |
| API 通訊 | Axios |
| 路由 | React Router 6 |
| 測試 | Vitest |

---

## 微服務一覽

| 代號 | 服務 | 說明 | 後端 | 前端 | Port |
|:---:|:---|:---|:---:|:---:|:---:|
| 01 | IAM | 認證、RBAC、SSO、多租戶 | Done | Done | 8081 |
| 02 | Organization | 員工生命週期、組織結構 | Done | Done | 8082 |
| 03 | Attendance | 打卡、請假、加班、彈性工時 | Done | Done | 8083 |
| 04 | Payroll | 薪資計算 (Saga)、稅務 | Done | Done | 8084 |
| 05 | Insurance | 勞健保、退休金 | Done | Done | 8085 |
| 06 | Project | 客戶、多層 WBS、成本追蹤 | Done | Done | 8086 |
| 07 | Timesheet | 週報、PM 審核 | Done | Done | 8087 |
| 08 | Performance | 考核週期、彈性表單 | Done | Done | 8088 |
| 09 | Recruitment | 職缺、Kanban、面試 | Done | Done | 8089 |
| 10 | Training | 課程管理、證照 | Done | Done | 8090 |
| 11 | Workflow | 視覺化流程設計、多層簽核 | Done | Done | 8091 |
| 12 | Notification | Email/Push/Teams/LINE | Done | Done | 8092 |
| 13 | Document | 儲存、版控、範本、加密 | Done | Done | 8093 |
| 14 | Reporting | CQRS ReadModel、儀表板 | Done | Done | 8094 |

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
│   ├── development/              # 開發流程、命名規範
│   ├── testing/                  # 測試架構（合約驅動、三階測試法）
│   └── templates/                # 後端模組模板
├── contracts/                    # 合約測試規格 (14 服務)
├── docs/                         # 產出文件 (PDF/PPTX)
├── deploy/                       # 部署配置
├── backend/                      # 後端微服務 (14 modules + common)
└── frontend/                     # React 前端 (14 features)
```

---

## 架構設計

### DDD 四層架構

每個微服務遵循嚴格分層：

| 層級 | 職責 |
|:---|:---|
| Interface | REST Controllers、Request/Response DTO |
| Application | Use Case 編排、Saga 協調（不含核心業務規則） |
| Domain | Aggregate Root、Entity、Value Object（純 Java POJO） |
| Infrastructure | Repository 實作、DAO、外部適配器 |

### Service Factory 模式

Controller method name 自動對應 Service bean name：

```java
// Controller — 方法名 createUser 自動解析 bean "createUserServiceImpl"
@PostMapping
public ResponseEntity<CreateUserResponse> createUser(...) {
    return ResponseEntity.ok(execCommand(request, currentUser));
}

// Service
@Service("createUserServiceImpl")
public class CreateUserServiceImpl implements CommandApiService<CreateUserRequest, CreateUserResponse> { }
```

### 關鍵業務流程

**員工到職 (Event-Driven Saga)**
```
Organization → EmployeeCreatedEvent
   ├→ IAM: 建立使用者帳號
   ├→ Insurance: 自動加保
   └→ Payroll: 建立薪資結構
```

**薪資計算 (Saga Pattern)**
```
Payroll → 彙整差勤/保費/工時 → 計算薪資 → PayslipGeneratedEvent
```

---

## 測試

- **後端測試:** 1,843 tests, 0 failures
- **合約驅動測試:** SA 定義規格 → 工程師實作 → 三層驗證（輸入 + 輸出 + 副作用）
- **前端測試:** 83 files, 1,241 tests 全通過（Factory 14/14、Hook 14/14、Component 9/14）

| 層級 | 覆蓋率要求 |
|:---|:---|
| Domain 邏輯 | 100% |
| API 端點 | 整合測試 |
| 前端 Factory / Hook / Component | 單元測試 |

---

## 法規遵循

### 勞動基準法
- 每月加班上限 46 小時 / 三個月 138 小時
- 特休假自動計算（依年資）
- 加班費率（平日 1.34x/1.67x、休息日 1.34x~2.67x）
- 變形工時（二週/四週/八週）
- 職災補償（醫療/工資/失能/死亡）

### 保險法規
- 勞保 27 級距、健保 51 級距
- 二代健保補充保費 2.11%
- 勞退新制 6% / 自提 0-6%

---

## 快速開始

### 環境需求
- Java 21+
- Node.js 18+
- PostgreSQL 15+
- Redis 7+
- Kafka 3+

### 後端啟動
```bash
cd backend
./gradlew bootRun -p hrms-{service} --args='--spring.profiles.active=local'
```

### 前端啟動
```bash
cd frontend
npm install
npm run dev
```

---

## 專案狀態

| 項目 | 狀態 |
|:---|:---:|
| 需求分析 (14 服務) | Done |
| 系統設計 (14 服務) | Done |
| API 規格 (14 服務) | Done |
| 後端實作 (14 服務) | Done |
| 前端實作 (14 服務) | Done |
| 合約測試 (14 服務) | Done |
| E2E 驗證 (14 服務) | Done |
| 文件產出 (PDF/PPTX) | Done |

---

## 授權

本專案採用 [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/) 授權。

**版權所有 (c) 2025-2026 k6jo3**

您可以自由分享與改作本專案內容，但 **禁止商業用途**。詳見 [LICENSE](./LICENSE)。
