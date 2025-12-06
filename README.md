# 人力資源暨專案管理系統 (HR & Project Management System)

**版本:** 2.1  
**更新日期:** 2025-12-06  
**架構:** 微服務架構 (Spring Cloud + ReactJS)

---

## 📋 專案概述

本系統為企業級人力資源與專案管理整合平台，採用 **DDD (領域驅動設計)**、**微服務架構**、**SOLID原則** 設計，涵蓋完整的HR業務流程與專案成本管理功能。

### 核心特色

- ✅ **14個微服務** - 完整的業務領域劃分
- ✅ **Event-Driven架構** - 使用Kafka實現服務間解耦
- ✅ **法規遵循** - 完全符合台灣勞基法規定
- ✅ **專案成本精算** - 整合工時與薪資，精確計算專案人力成本
- ✅ **企業整合** - 支援Teams/LINE/Slack通知整合
- ✅ **CQRS架構** - Query/Command分離設計
- ✅ **統一命名規範** - HR{DD}{Screen}{Type}Controller格式

---

## 🏗️ 技術架構

### 後端技術棧
| 元件 | 技術選型 |
|:---|:---|
| 核心框架 | Spring Boot 3.2+ |
| 微服務治理 | Spring Cloud 2023.x |
| API Gateway | Spring Cloud Gateway |
| 服務發現 | Eureka / Consul |
| 認證授權 | Spring Security + OAuth2 + JWT |
| 資料庫 | PostgreSQL 15+ (每服務獨立DB) |
| 快取 | Redis 7+ |
| 訊息佇列 | Kafka (事件驅動) |
| 分散式追蹤 | Sleuth + Zipkin |

### 前端技術棧
| 元件 | 技術選型 |
|:---|:---|
| 核心框架 | React 18+ with TypeScript |
| 狀態管理 | Redux Toolkit / Zustand |
| UI元件庫 | Ant Design |
| 圖表 | Apache ECharts |
| API通訊 | Axios + React Query |

---

## 📁 專案結構

```
hr-system-2/
├── README.md                                     # 本文件
├── 人力資源暨專案管理系統_正式需求規格書.md   # 原始客戶需求
├── PM需求審查報告.md                          # PM審查報告
│
├── 需求分析書/                                 # SA撰寫的需求分析
│   ├── 01_IAM服務需求分析書.md                # 認證授權服務
│   ├── 02_組織員工服務需求分析書.md           # 組織與員工管理
│   ├── 03_考勤管理服務需求分析書.md           # 差勤管理
│   ├── 04_薪資管理服務需求分析書.md           # 薪資計算
│   ├── ... (共14個服務)
│   └── 14_報表分析服務需求分析書.md           # 報表與儀表板
│
├── PM審查補充/                                 # PM審查後補充文件
│   ├── 01_IAM服務需求分析書_PM審查補充.md
│   ├── ... (每個服務一份)
│   └── 14_報表分析服務需求分析書_PM審查補充.md
│
└── spec/                                       # 🆕 系統設計文件
    ├── 系統架構設計文件.md                    # 整體技術架構 & DDD分層
    ├── 系統架構設計文件_命名規範.md           # 程式命名原則 (5.5節)
    ├── 系統設計書命名規範合規性檢查.md        # 🆕 命名合規性檢查 & UML
    │
    ├── 01_IAM服務系統設計書.md                # IAM系統設計
    ├── 01_IAM服務系統設計書_part2.md
    ├── 01_IAM服務系統設計書_part3.md
    │
    ├── 02_組織員工服務系統設計書.md           # 組織員工系統設計
    ├── 02_組織員工服務系統設計書_part2.md
    ├── 02_組織員工服務系統設計書_part3.md
    ├── 02_組織員工服務系統設計書_part4.md
    │
    ├── 03_考勤管理服務系統設計書.md           # 🆕 考勤系統設計
    ├── 03_考勤管理服務系統設計書_part2.md
    ├── 03_考勤管理服務系統設計書_part3.md
    │
    └── image/                                  # UI線稿圖片
        ├── iam_login_page.png
        ├── iam_user_management.png
        └── iam_role_management.png
```

---

## 🔧 微服務清單與命名代號

### 第一階段 (核心基礎服務)
| 代號 | 服務 | 說明 | 設計狀態 |
|:---:|:---|:---|:---:|
| **01** | IAM Service | 認證授權、RBAC、SSO | ✅ 系統設計完成 |
| **02** | Organization Service | 組織架構、員工管理、ESS | ✅ 系統設計完成 |
| **03** | Attendance Service | 打卡、請假、加班、特休 | ✅ 系統設計完成 |
| **04** | Payroll Service | 薪資計算、加班費、稅金 | ⏳ 待設計 |
| **05** | Insurance Service | 勞健保、勞退、補充保費 | ⏳ 待設計 |
| **11** | Workflow Service | 簽核流程、代理人、催辦 | ⏳ 待設計 |
| **12** | Notification Service | Email/Push/Teams/LINE | ⏳ 待設計 |
| **13** | Document Service | 文件儲存、範本產生 | ⏳ 待設計 |

### 第二階段 (專案管理核心)
| 代號 | 服務 | 說明 | 設計狀態 |
|:---:|:---|:---|:---:|
| **06** | Project Service | 客戶、專案、WBS工項 | ⏳ 待設計 |
| **07** | Timesheet Service | 工時回報、專案成本 | ⏳ 待設計 |
| **14** | Reporting Service | CQRS報表、儀表板 | ⏳ 待設計 |

### 第三階段 (進階HR功能)
| 代號 | 服務 | 說明 | 設計狀態 |
|:---:|:---|:---|:---:|
| **08** | Performance Service | 績效考核、360評估 | ⏳ 待設計 |
| **09** | Recruitment Service | 招募管理、面試評估 | ⏳ 待設計 |
| **10** | Training Service | 訓練管理、證照追蹤 | ⏳ 待設計 |

---

## 📐 程式命名規範

> 詳見 `spec/系統架構設計文件_命名規範.md`

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
| ATT (03) | `HR03LeaveQryController` | 請假Query操作 |

### 頁面代碼格式
```
HR{DD}-P{NN}   (頁面)
HR{DD}-M{NN}   (Modal對話框)
```

**範例:** `HR01-P01` (IAM登入頁), `HR02-P03` (員工列表頁), `HR03-P06` (差勤審核頁)

### Query/Command Controller拆分
本專案採用 **CQRS架構**，Controller強烈建議拆分：
- ✅ `*CmdController` - 處理Create/Update/Delete
- ✅ `*QryController` - 處理Query/List/Get

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
| **6. 資料庫設計** | ER圖、DDL、資料字典 |
| **7. Domain設計** | 聚合根、值對象、Repository (Java) |
| **8. 領域事件設計** | 事件清單、JSON Schema範例 |
| **9. API設計** | 端點、Request/Response、錯誤碼 |
| **10. 工項清單** | 前端/後端/資料庫開發項目 |

---

## ⚖️ 法規遵循

### 勞動基準法合規
- ✅ 每月加班上限 46 小時
- ✅ 三個月加班上限 138 小時
- ✅ 特休假自動計算 (依年資)
- ✅ 加班費計算 (平日/休息日/國定假日)
- ✅ 變形工時管理 (二週/四週/八週)
- ✅ 職業災害管理
- ✅ 女性員工保護 (哺乳時間)

### 保險法規合規
- ✅ 投保級距自動對應
- ✅ 二代健保補充保費計算 (2.11%)
- ✅ 政府申報格式匯出

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
   ├→ Timesheet: 取得工時 (Phase 2)
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
2. 閱讀各服務的需求分析書了解DDD設計
3. 閱讀 `spec/系統架構設計文件.md` 了解技術架構
4. 閱讀 `spec/系統架構設計文件_命名規範.md` 了解命名規則
5. 參考各服務系統設計書進行開發

---

## 📈 專案狀態

| 階段 | 狀態 | 說明 |
|:---|:---|:---|
| 需求分析 | ✅ 完成 | 14個服務需求分析書 |
| PM審查 | ✅ 完成 | 所有審查項目已補充 |
| 命名規範 | ✅ 完成 | HR{DD}格式、CQRS拆分 |
| 系統設計 (01 IAM) | ✅ 完成 | UI/UX/DB/Domain/API |
| 系統設計 (02 組織員工) | ✅ 完成 | 4個聚合根、31個API |
| 系統設計 (03 考勤) | ✅ 完成 | 6個聚合根、31個API |
| 系統設計 (04-14) | ⏳ 待開始 | - |
| 開發實作 | ⏳ 待開始 | - |
| 測試驗收 | ⏳ 待開始 | - |

---

## 📞 聯絡資訊

**專案經理:** PM  
**系統分析師:** SA  
**文件更新:** 2025-12-06

---

## 📄 授權

本專案文件版權歸公司所有。

