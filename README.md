# 人力資源暨專案管理系統 (HR & Project Management System)

**版本:** 3.2
**更新日期:** 2025-12-29
**架構:** 微服務架構 (Spring Cloud + ReactJS)

---

## 📋 專案概述

本系統為企業級人力資源與專案管理整合平台，採用 **DDD (領域驅動設計)**、**微服務架構**、**SOLID原則** 設計，涵蓋完整的HR業務流程與專案成本管理功能。

### 核心特色

- ✅ **14個微服務** - 完整的業務領域劃分，所有系統設計已完成
- ✅ **Event-Driven架構** - 使用Kafka實現服務間解耦
- ✅ **法規遵循** - 完全符合台灣勞基法規定
- ✅ **專案成本精算** - 整合工時與薪資，精確計算專案人力成本
- ✅ **企業整合** - 支援Teams/LINE/Slack通知整合
- ✅ **CQRS架構** - Query/Command分離設計
- ✅ **統一命名規範** - HR{DD}{Screen}{Type}Controller格式
- ✅ **法規參數管理** - 可配置的年度法規參數與異動稽核

---

## 🏗️ 技術架構

### 後端技術棧
| 元件 | 技術選型 |
|:---|:---|
| 核心框架 | Spring Boot 3.1.x |
| 微服務治理 | Spring Cloud 2023.x |
| API Gateway | Spring Cloud Gateway |
| 服務發現 | Eureka |
| 認證授權 | Spring Security + OAuth2 + JWT |
| 資料庫 | PostgreSQL 15+ (每服務獨立DB) |
| ORM (既有) | MyBatis 3.5.x |
| ORM (新功能) | Querydsl + JPA 5.0.0 (見 `framework/architecture/07_Fluent_Query_Engine.md`) |
| 快取 | Redis 7+ |
| 訊息佇列 | Kafka (事件驅動) |
| 分散式追蹤 | Sleuth + Zipkin |

### 前端技術棧
| 元件 | 技術選型 |
|:---|:---|
| 核心框架 | React 18 + TypeScript 5.x |
| 構建工具 | Vite 5.x |
| 狀態管理 | Redux Toolkit |
| UI元件庫 | Ant Design 5.x |
| 圖表 | Apache ECharts |
| API通訊 | Axios + React Query |
| 路由 | React Router 6 |

---

## 📁 專案結構

```
hr-system-2/
├── README.md                                     # 本文件
├── CLAUDE.md                                     # AI 開發輔助指南
│
├── framework/                                    # ✅ 可重用架構框架 (Refactored)
│   ├── README.md                                 # 架構框架總覽
│   ├── architecture/                             # 架構設計規範
│   │   ├── 01_核心架構原則.md
│   │   ├── 02_DDD分層設計.md
│   │   ├── 03_Business_Pipeline.md              # ⭐ 宣告式業務流水線
│   │   ├── 07_Fluent_Query_Engine.md            # Querydsl 查詢引擎規範
│   │   └── 08_Generic_Library.md                # 泛型程式庫架構規範
│   ├── development/                              # 開發流程規範
│   │   ├── 02_命名規範.md                        # 統一命名標準
│   │   └── 05_API開發規範.md                     # API 標準
│   └── testing/                                  # 測試架構規範
│       ├── 01_測試架構總覽.md
│       ├── 02_三階測試法.md
│       └── 04_合約驅動測試.md
│
├── archive/                                      # 📦 專案文檔歸檔 (Archived)
│   └── knowledge/                                # 原始需求與分析文件
│       ├── 01_Client_Requirements/
│       ├── 02_Requirements_Analysis/
│       └── 03_System_Architecture/
│
├── spec/                                        # ✅ 系統設計文件 (已完成)
│   ├── 系統架構設計文件.md                     # 整體技術架構 & DDD分層
│   ├── 系統架構設計文件_命名規範.md            # (請參考 framework/development/02_命名規範.md)
│   │
│   ├── 01_IAM服務系統設計書.md                 # ✅ IAM (認證授權)
│   ├── ... (略)
│   │
│   ├── logic_spec/                              # ✅ 邏輯規格書
│   │   ├── variable_hours_rules.md              # 變形工時計算邏輯
│   │   ├── ... (略)
│   │
│   └── image/                                   # UI線稿圖片
│
├── backend/                                     # 後端專案
│   └── 架構說明與開發規範.md                   # ✅ 後端開發規範 (已對齊)
│
└── frontend/                                    # 前端專案
    └── 架構說明與開發規範.md                   # ✅ 前端開發規範 (已對齊)
```

---

## 🔧 微服務清單與設計狀態

### 第一階段 (核心基礎服務)
| 代號 | 服務 | 說明 | 設計狀態 |
|:---:|:---|:---|:---:|
| **01** | IAM Service | 認證授權、RBAC、SSO、多租戶 | ✅ 完成 |
| **02** | Organization Service | 組織架構、員工生命週期、ESS | ✅ 完成 |
| **03** | Attendance Service | 打卡、請假、加班、特休、變形工時 | ✅ 完成 |
| **04** | Payroll Service | 薪資計算Saga、稅金、加班費 | ✅ 完成 |
| **05** | Insurance Service | 勞健保、勞退、二代健保補充保費 | ✅ 完成 |
| **11** | Workflow Service | 可視化流程設計、多級簽核、代理人 | ✅ 完成 |
| **12** | Notification Service | Email/Push/Teams/LINE、事件驅動 | ✅ 完成 |
| **13** | Document Service | 文件儲存、版本控管、範本產生、加密 | ✅ 完成 |

### 第二階段 (專案管理核心)
| 代號 | 服務 | 說明 | 設計狀態 |
|:---:|:---|:---|:---:|
| **06** | Project Service | 客戶管理、多層WBS、專案成本追蹤 | ✅ 完成 |
| **07** | Timesheet Service | 週曆式工時填報、PM審核、防呆機制 | ✅ 完成 |
| **14** | Reporting Service | CQRS讀模型、儀表板、資料匯出 | ✅ 完成 |

### 第三階段 (進階HR功能)
| 代號 | 服務 | 說明 | 設計狀態 |
|:---:|:---|:---|:---:|
| **08** | Performance Service | 考核週期、彈性表單、績效評等 | ✅ 完成 |
| **09** | Recruitment Service | 職缺管理、Kanban、面試評估、Offer | ✅ 完成 |
| **10** | Training Service | 課程管理、報名審核、證照到期提醒 | ✅ 完成 |

---

## 📚 邏輯規格書 (Logic Specifications)

針對工程師關心的複雜業務邏輯，提供詳細的實作規格：

| 文件 | 說明 |
|:---|:---|
| `variable_hours_rules.md` | 台灣勞基法2週/4週/8週變形工時計算邏輯、加班費率 |
| `occupational_injury_compensation.md` | 職災醫療/工資/失能(15級)/死亡補償計算 |
| `tax_insurance_tables_2025.md` | 2025年稅務保險級距 |
| `sso_account_linking.md` | Google/Microsoft/SAML帳號連結流程、安全考量 |
| `regulatory_parameters_and_audit.md` | 年度法規參數管理、生效日邏輯、異動稽核設計 |

---

## 📐 程式命名規範

> 詳見 `framework/development/02_命名規範.md`

### Domain代號對照表
| 代號 | 服務名稱 | 說明 |
|:---:|:---|:---|
| 01 | IAM | 認證授權 |
| 02 | Organization | 組織員工 |
| 03 | Attendance | 考勤管理 |
| 04 | Payroll | 薪資管理 |
| 05 | Insurance | 保險管理 |
| 06 | Project | 專案管理 |
| 07 | Timesheet | 工時管理 |
| 08 | Performance | 績效管理 |
| 09 | Recruitment | 招募管理 |
| 10 | Training | 訓練管理 |
| 11 | Workflow | 簽核流程 |
| 12 | Notification | 通知服務 |
| 13 | Document | 文件管理 |
| 14 | Reporting | 報表分析 |

### Controller命名格式
```
HR{Domain代號}{畫面名稱}{Controller類型}Controller
```

---

## 📊 系統設計書內容

每份系統設計書包含以下章節：

| 章節 | 說明 |
|:---|:---|
| **1. 服務概述** | 服務定位、核心功能、技術架構 |
| **2. UI設計** | 頁面清單、Mermaid線稿圖 |
| **3. UX流程設計** | 完整循序圖 (Sequence Diagram) |
| **4. 畫面事件說明** | 事件ID、觸發處理、API對應 |
| **5. Data Flow設計** | Redux State、服務間資料流 |
| **6. 資料庫設計** | ER圖、DDL Script、資料字典 |
| **7. Domain設計** | 聚合根、值對象、Java程式碼範例 |
| **8. 領域事件設計** | 事件清單、JSON Schema範例 |
| **9. API設計** | 端點、Request/Response、錯誤碼 |
| **10. 工項清單** | 前端/後端/資料庫開發項目 |

---

## 🚀 快速開始

### 環境需求
- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Redis 7+
- Kafka 3+

### 開發指南
1. 閱讀 `archive/knowledge/01_Client_Requirements/人力資源暨專案管理系統_正式需求規格書.md` 了解業務需求
2. 閱讀 `spec/系統架構設計文件.md` 了解技術架構
3. 閱讀 `framework/development/02_命名規範.md` 了解命名規則
4. 參考 `framework/development/` 下的前後端開發規範
5. 參考各服務系統設計書進行開發
6. 複雜邏輯請參考 `spec/logic_spec/` 下的邏輯規格書
7. **新增查詢功能請參考 `framework/architecture/07_Fluent_Query_Engine.md`** (持久層技術選擇指引)
8. **測試開發請參考 `framework/testing/`** (快照測試、三階測試法、合約驅動測試)
9. **泛型基類設計請參考 `framework/architecture/08_Generic_Library.md`**
10. **複雜業務邏輯請參考 `framework/architecture/03_Business_Pipeline.md`** (Service 流水線模式)

---

## 📈 專案狀態

| 階段 | 狀態 | 說明 |
|:---|:---|:---|
| 需求分析 | ✅ 完成 | 14個服務需求分析書 |
| PM審查 | ✅ 完成 | 所有審查項目已補充 |
| 命名規範 | ✅ 完成 | HR{DD}格式、CQRS拆分 |
| 系統設計 (01-14) | ✅ **全部完成** | 所有微服務系統設計書 |
| 邏輯規格書 | ✅ 完成 | 5份詳細邏輯規格 |
| 前後端開發規範 | ✅ 完成 | 已與架構設計文件對齊 |
| 開發實作 | ⏳ 待開始 | - |
| 測試驗收 | ⏳ 待開始 | - |

---

## 📞 聯絡資訊

**專案經理:** PM  
**系統分析師:** SA  
**文件更新:** 2025-12-29

---

## 📄 授權

本專案文件版權歸公司所有。
