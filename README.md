# 人力資源暨專案管理系統 (HR & Project Management System)

**版本:** 3.0  
**更新日期:** 2025-12-07  
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
| ORM | MyBatis 3.5.x |
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
├── 人力資源暨專案管理系統_正式需求規格書.md   # 原始客戶需求
├── PM需求審查報告.md                          # PM審查報告
├── 系統開發工作計畫書.md                      # 開發工作計畫
│
├── 需求分析書/                                 # SA撰寫的需求分析 (14個服務)
│   ├── 01_IAM服務需求分析書.md
│   ├── ...
│   └── 14_報表分析服務需求分析書.md
│
├── PM審查補充/                                 # PM審查後補充文件
│   ├── 01_IAM服務需求分析書_PM審查補充.md
│   └── ...
│
├── spec/                                        # ✅ 系統設計文件 (已完成)
│   ├── 系統架構設計文件.md                     # 整體技術架構 & DDD分層
│   ├── 系統架構設計文件_命名規範.md            # 程式命名原則
│   │
│   ├── 01_IAM服務系統設計書.md                 # ✅ IAM (認證授權)
│   ├── 01_IAM服務系統設計書_part2.md
│   ├── 01_IAM服務系統設計書_part3.md
│   │
│   ├── 02_組織員工服務系統設計書.md            # ✅ 組織員工
│   ├── 02_組織員工服務系統設計書_part2.md
│   ├── 02_組織員工服務系統設計書_part3.md
│   ├── 02_組織員工服務系統設計書_part4.md
│   │
│   ├── 03_考勤管理服務系統設計書.md            # ✅ 考勤管理
│   ├── 03_考勤管理服務系統設計書_part2.md
│   ├── 03_考勤管理服務系統設計書_part3.md
│   │
│   ├── 04_薪資管理服務系統設計書.md            # ✅ 薪資管理 (含Saga)
│   ├── 05_保險管理服務系統設計書.md            # ✅ 勞健保管理
│   ├── 06_專案管理服務系統設計書.md            # ✅ 專案與WBS
│   ├── 07_工時管理服務系統設計書.md            # ✅ 工時填報
│   ├── 08_績效管理服務系統設計書.md            # ✅ 績效考核
│   ├── 09_招募管理服務系統設計書.md            # ✅ 招募 (Kanban)
│   ├── 10_訓練管理服務系統設計書.md            # ✅ 訓練與證照
│   ├── 11_簽核流程服務系統設計書.md            # ✅ 通用簽核引擎
│   ├── 12_通知服務系統設計書.md                # ✅ 多渠道通知
│   ├── 13_文件管理服務系統設計書.md            # ✅ 文件與範本
│   ├── 14_報表分析服務系統設計書.md            # ✅ CQRS報表
│   │
│   ├── logic_spec/                              # ✅ 邏輯規格書 (NEW)
│   │   ├── variable_hours_rules.md              # 變形工時計算邏輯
│   │   ├── occupational_injury_compensation.md  # 職災補償邏輯
│   │   ├── tax_insurance_tables_2025.md         # 2025年稅務保險級距
│   │   ├── sso_account_linking.md               # SSO帳號連結流程
│   │   └── regulatory_parameters_and_audit.md   # 法規參數與稽核設計
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
| `tax_insurance_tables_2025.md` | 2025年勞保27級距、健保51級距、補充保費、所得稅扣繳 |
| `sso_account_linking.md` | Google/Microsoft/SAML帳號連結流程、安全考量 |
| `regulatory_parameters_and_audit.md` | 年度法規參數管理、生效日邏輯、異動稽核設計 |

---

## 📐 程式命名規範

> 詳見 `spec/系統架構設計文件_命名規範.md`

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

**範例:**
| Domain | Controller | 說明 |
|:---|:---|:---|
| IAM (01) | `HR01UserCmdController` | 使用者管理Command操作 |
| IAM (01) | `HR01UserQryController` | 使用者管理Query操作 |
| ORG (02) | `HR02EmployeeCmdController` | 員工管理Command操作 |
| ATT (03) | `HR03LeaveCmdController` | 請假Command操作 |
| PAY (04) | `HR04PayrollRunCmdController` | 薪資計算Command操作 |

### 頁面代碼格式
```
HR{DD}-P{NN}   (頁面)
HR{DD}-M{NN}   (Modal對話框)
```

**範例:** `HR01-P01` (IAM登入頁), `HR02-P03` (員工列表頁), `HR04-P05` (薪資單頁)

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

## ⚖️ 法規遵循

### 勞動基準法合規
- ✅ 每月加班上限 46 小時
- ✅ 三個月加班上限 138 小時
- ✅ 特休假自動計算 (依年資)
- ✅ 加班費計算 (平日1.34x/1.67x, 休息日1.34x~2.67x)
- ✅ 變形工時管理 (二週/四週/八週) - 詳見邏輯規格書
- ✅ 職業災害補償 (醫療/工資/失能/死亡) - 詳見邏輯規格書
- ✅ 女性員工保護 (哺乳時間)

### 保險法規合規 (2025年度)
- ✅ 勞保投保級距 27級 (27,470~45,800元)
- ✅ 健保投保級距 51級 (27,470~219,500元)
- ✅ 二代健保補充保費 2.11%
- ✅ 勞退新制 6%/自提0-6%
- ✅ 政府申報格式匯出

> 詳見 `spec/logic_spec/tax_insurance_tables_2025.md`

---

## 📊 關鍵業務流程

### 1. 員工到職 (Event-Driven Saga)
```
Organization → EmployeeCreated事件
   ├→ IAM: 建立使用者帳號
   ├→ Insurance: 自動加保
   └→ Payroll: 建立薪資結構
```

### 2. 薪資計算 (Saga Pattern)
```
Payroll Service:
   ├→ Organization: 取得員工清單
   ├→ Attendance: 取得差勤數據
   ├→ Insurance: 取得保費
   ├→ Timesheet: 取得工時
   └→ 計算薪資 → 發布PayslipGenerated事件
```

### 3. 請假審核 (CQRS + Event)
```
Employee 申請請假
   → Attendance → LeaveApplied事件
   → Workflow: 建立審核流程
   → Manager 審核通過
   → Attendance → LeaveApproved事件
   → Payroll: 記錄請假扣薪
   → Notification: 發送通知
```

### 4. 專案成本追蹤
```
Timesheet → TimesheetApproved事件
   → Project: 累計專案工時成本
   → Reporting: 更新專案成本報表
```

---

## 🚀 快速開始

### 環境需求
- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Redis 7+
- Kafka 3+

### 開發指南
1. 閱讀 `人力資源暨專案管理系統_正式需求規格書.md` 了解業務需求
2. 閱讀 `spec/系統架構設計文件.md` 了解技術架構
3. 閱讀 `spec/系統架構設計文件_命名規範.md` 了解命名規則
4. 參考 `backend/架構說明與開發規範.md` 或 `frontend/架構說明與開發規範.md`
5. 參考各服務系統設計書進行開發
6. 複雜邏輯請參考 `spec/logic_spec/` 下的邏輯規格書

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
**文件更新:** 2025-12-07

---

## 📄 授權

本專案文件版權歸公司所有。
