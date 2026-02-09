# 報表分析服務業務合約 (Reporting Service Business Contract)

> **服務代碼:** HR14
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/14_報表分析服務系統設計書.md`
> - `knowledge/04_API_Specifications/14_報表分析服務系統設計書_API詳細規格.md`

---

## 📋 概述

本合約文件定義報表分析服務的**完整業務場景**，包括：
1. **Command 操作場景**（生成報表、匯出、儀表板管理）- 驗證業務規則與領域事件
2. **Query 操作場景**（查詢報表、讀模型查詢）- 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構中的 CQRS 模式

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景（生成、匯出、儀表板管理）
- ✅ 新增詳細的 ReadModel 查詢場景
- ✅ 對應到實際的 API 端點
- ✅ 完整的業務規則驗證（權限、資料來源等）
- ✅ 包含 CQRS 架構的事件訂閱模式

**服務定位：**
報表分析服務採用 CQRS（命令查詢責任分離）模式，訂閱所有業務服務的領域事件，更新非正規化的 ReadModel。提供多維度分析報表（HR、專案、財務），支援自定義儀表板、圖表配置、Excel/PDF 匯出等功能。

**資料軟刪除策略：**
- **讀模型資料**: 使用快照機制，根據事件定期計算新的快照（不進行軟刪除）
- **儀表板配置**: 使用 `is_active` 欄位，true 為有效，false 為停用
- **歷史報表**: 保留所有生成的報表記錄用於審計

**角色權限說明：**
- **EMPLOYEE**: 查詢自己的相關報表（薪資、考勤等）
- **MANAGER**: 查詢直屬部門的報表
- **PROJECT_MANAGER**: 查詢所屬項目的成本報表
- **HR**: 查詢全公司 HR 報表
- **FINANCE**: 查詢財務報表
- **ADMIN**: 全部操作

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [報表生成](#11-報表生成)
   - 1.2 [報表匯出](#12-報表匯出)
   - 1.3 [儀表板管理](#13-儀表板管理)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [HR 報表查詢](#21-hr-報表查詢)
   - 2.2 [專案成本報表查詢](#22-專案成本報表查詢)
   - 2.3 [財務報表查詢](#23-財務報表查詢)
   - 2.4 [儀表板查詢](#24-儀表板查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 報表生成

#### RPT_CMD_001: 生成 HR 報表

**業務場景描述：**
HR 管理員生成人力資源報表（如員工花名冊、差勤統計），系統基於 ReadModel 資料產生報表，計算相關指標（離職率、加班時數等）。

**API 端點：**
```
POST /api/v1/reports/generate
```

**前置條件：**
- 執行者必須擁有相應的報表權限（`report:hr:view` 或 `report:project:view` 等）
- 報表類型必須有效
- 日期範圍必須有效（startDate <= endDate）

**輸入 (Request)：**
```json
{
  "reportType": "EMPLOYEE_ROSTER",
  "organizationId": "org-001",
  "startDate": "2026-02-01",
  "endDate": "2026-02-28",
  "filters": {
    "departmentId": "D001",
    "employmentStatus": "ACTIVE"
  },
  "groupBy": ["department", "job_title"],
  "metrics": ["count", "avgSalary", "turnoverRate"]
}
```

**業務規則驗證：**

1. ✅ **報表類型驗證**
   - 規則：reportType 必須為 ['EMPLOYEE_ROSTER', 'ATTENDANCE_SUMMARY', 'TURNOVER_ANALYSIS', 'PROJECT_COST', 'LABOR_COST', 'PAYROLL_SUMMARY']
   - 預期結果：reportType 有效

2. ✅ **日期範圍檢查**
   - 規則：startDate <= endDate，日期不可超過 12 個月範圍
   - 預期結果：日期範圍有效

3. ✅ **組織權限檢查**
   - 規則：用戶必須擁有查詢該組織報表的權限
   - 呼叫 IAM Service 驗證權限
   - 預期結果：權限檢查通過

4. ✅ **ReadModel 資料可用性檢查**
   - 規則：所需的 ReadModel 資料必須存在且最新
   - 查詢條件：檢查相關 snapshot 表是否有該日期範圍的資料
   - 預期結果：有足夠的歷史快照資料

**必須發布的領域事件：**
```json
{
  "eventId": "evt-rpt-gen-001",
  "eventType": "ReportGeneratedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "report-001",
  "aggregateType": "Report",
  "payload": {
    "reportId": "report-001",
    "reportType": "EMPLOYEE_ROSTER",
    "organizationId": "org-001",
    "startDate": "2026-02-01",
    "endDate": "2026-02-28",
    "generatedBy": "admin-001",
    "generatedAt": "2026-02-09T09:00:00Z",
    "totalRecords": 150,
    "metrics": {
      "totalEmployees": 150,
      "newHires": 5,
      "terminations": 2,
      "turnoverRate": 0.047,
      "averageSalary": 48200
    }
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "reportId": "report-001",
    "reportType": "EMPLOYEE_ROSTER",
    "generatedAt": "2026-02-09T09:00:00Z",
    "totalRecords": 150,
    "columns": [
      {"name": "employee_number", "label": "員工編號"},
      {"name": "employee_name", "label": "姓名"},
      {"name": "department", "label": "部門"},
      {"name": "job_title", "label": "職位"},
      {"name": "hire_date", "label": "到職日期"},
      {"name": "salary", "label": "薪資"}
    ],
    "rows": []
  }
}
```

---

#### RPT_CMD_002: 生成專案成本報表

**業務場景描述：**
項目經理生成專案成本分析報表，包括實際成本、預算、稼動率等指標。系統從 project_cost_snapshots 讀模型中讀取數據。

**API 端點：**
```
POST /api/v1/reports/project-cost
```

**前置條件：**
- 執行者必須擁有 `report:project:view` 權限或為該項目的 PM
- projectId 必須存在
- 項目必須處於進行中或已完成狀態

**輸入 (Request)：**
```json
{
  "projectId": "PRJ-001",
  "startDate": "2025-01-01",
  "endDate": "2026-02-28",
  "includeMetrics": ["totalHours", "totalCost", "budgetUtilization", "utilizationRate", "profitability"]
}
```

**業務規則驗證：**

1. ✅ **項目存在性檢查**
   - 呼叫 Project Service 驗證項目存在
   - 預期結果：Project 存在

2. ✅ **項目訪問權限檢查**
   - 規則：執行者必須是項目 PM 或 HR/ADMIN
   - 預期結果：權限檢查通過

3. ✅ **快照資料完整性檢查**
   - 查詢條件：`project_id = ? AND snapshot_date BETWEEN ? AND ?`
   - 規則：至少存在一個快照記錄
   - 預期結果：有足夠的快照資料

**必須發布的領域事件：**
```json
{
  "eventId": "evt-rpt-proj-001",
  "eventType": "ProjectCostReportGeneratedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "report-002",
  "aggregateType": "Report",
  "payload": {
    "reportId": "report-002",
    "reportType": "PROJECT_COST",
    "projectId": "PRJ-001",
    "projectName": "A 客戶 CRM 系統",
    "startDate": "2025-01-01",
    "endDate": "2026-02-28",
    "generatedBy": "pm-001",
    "generatedAt": "2026-02-09T10:00:00Z",
    "costSummary": {
      "contractAmount": 5000000,
      "actualCost": 3800000,
      "totalHours": 12000,
      "budgetUtilization": 0.76,
      "utilizationRate": 0.82,
      "profitability": 0.24
    }
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "reportId": "report-002",
    "reportType": "PROJECT_COST",
    "projectName": "A 客戶 CRM 系統",
    "generatedAt": "2026-02-09T10:00:00Z",
    "costSummary": {
      "contractAmount": 5000000,
      "actualCost": 3800000,
      "totalHours": 12000,
      "budgetUtilization": 0.76,
      "utilizationRate": 0.82,
      "profitability": 0.24
    }
  }
}
```

---

### 1.2 報表匯出

#### RPT_CMD_003: 匯出報表為 Excel

**業務場景描述：**
用戶將已生成的報表匯出為 Excel 格式，系統使用 Apache POI 將報表資料轉換為 XLSX 檔案並提供下載。

**API 端點：**
```
POST /api/v1/reports/{id}/export/excel
```

**前置條件：**
- 執行者必須有查詢該報表的權限
- 報表必須存在且狀態為 COMPLETED
- 匯出資料不超過 100,000 行

**輸入 (Request)：**
```json
{
  "reportId": "report-001",
  "fileName": "員工花名冊_20260209.xlsx",
  "includeCharts": false,
  "sheetName": "員工列表"
}
```

**業務規則驗證：**

1. ✅ **報表存在性檢查**
   - 查詢條件：`report_id = ?`
   - 預期結果：報表存在

2. ✅ **報表狀態檢查**
   - 規則：報表狀態必須為 COMPLETED 或 EXPORTED
   - 預期結果：報表可匯出

3. ✅ **檔案名稱驗證**
   - 規則：檔案名稱不超過 255 字符，包含有效的字符
   - 預期結果：檔案名稱有效

4. ✅ **資料量檢查**
   - 規則：導出的記錄數不超過 100,000 行
   - 預期結果：資料量在允許範圍內

**必須發布的領域事件：**
```json
{
  "eventId": "evt-rpt-exp-001",
  "eventType": "ReportExportedEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "report-001",
  "aggregateType": "Report",
  "payload": {
    "reportId": "report-001",
    "exportFormat": "EXCEL",
    "fileName": "員工花名冊_20260209.xlsx",
    "fileSize": 2097152,
    "exportedBy": "admin-001",
    "exportedAt": "2026-02-09T11:00:00Z",
    "downloadUrl": "https://api.hrms.com/download/report-001/export.xlsx"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "reportId": "report-001",
    "fileName": "員工花名冊_20260209.xlsx",
    "fileSize": 2097152,
    "downloadUrl": "https://api.hrms.com/download/report-001/export.xlsx",
    "expiryTime": "2026-02-16T11:00:00Z"
  }
}
```

---

#### RPT_CMD_004: 匯出報表為 PDF

**業務場景描述：**
用戶將報表匯出為 PDF 格式，系統根據報表配置生成格式化的 PDF 檔案。

**API 端點：**
```
POST /api/v1/reports/{id}/export/pdf
```

**前置條件：**
- 執行者必須有查詢該報表的權限
- 報表必須存在且狀態為 COMPLETED

**輸入 (Request)：**
```json
{
  "reportId": "report-001",
  "fileName": "員工花名冊_20260209.pdf",
  "pageSize": "A4",
  "orientation": "LANDSCAPE",
  "includeCharts": true,
  "companyLogo": true
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "reportId": "report-001",
    "fileName": "員工花名冊_20260209.pdf",
    "fileSize": 5242880,
    "downloadUrl": "https://api.hrms.com/download/report-001/export.pdf",
    "expiryTime": "2026-02-16T11:00:00Z"
  }
}
```

---

### 1.3 儀表板管理

#### RPT_CMD_005: 建立自定義儀表板

**業務場景描述：**
HR 管理員或高管為自己建立自定義儀表板，配置想要監控的 KPI 和圖表。儀表板由多個 Widget 組成，每個 Widget 對應一個報表或指標。

**API 端點：**
```
POST /api/v1/dashboards
```

**前置條件：**
- 執行者可以是任何認證用戶
- dashboardName 不可為空且唯一

**輸入 (Request)：**
```json
{
  "dashboardName": "HR 主管儀表板",
  "description": "用於高管監控人力資源關鍵指標",
  "isDefault": true,
  "widgets": [
    {
      "widgetId": "widget-1",
      "widgetType": "KPI_CARD",
      "title": "在職人數",
      "metric": "total_employees",
      "position": {"x": 0, "y": 0, "width": 3, "height": 2}
    },
    {
      "widgetId": "widget-2",
      "widgetType": "LINE_CHART",
      "title": "月度人力成本趨勢",
      "metric": "monthly_labor_cost",
      "position": {"x": 3, "y": 0, "width": 9, "height": 4}
    },
    {
      "widgetId": "widget-3",
      "widgetType": "PIE_CHART",
      "title": "部門人數分布",
      "metric": "employee_distribution_by_dept",
      "position": {"x": 0, "y": 2, "width": 3, "height": 4}
    }
  ]
}
```

**業務規則驗證：**

1. ✅ **儀表板名稱唯一性檢查**
   - 查詢條件：`dashboard_name = ? AND owner_id = ?`
   - 預期結果：不存在重複的儀表板名稱（每個用戶內唯一）

2. ✅ **Widget 有效性檢查**
   - 規則：每個 Widget 的 widgetType 必須有效
   - widgetType 支援：['KPI_CARD', 'LINE_CHART', 'BAR_CHART', 'PIE_CHART', 'TABLE', 'HEATMAP']
   - 預期結果：所有 Widget 類型有效

3. ✅ **Widget 位置檢查**
   - 規則：position 不可重疊，總寬度不超過 12（12 列網格佈局）
   - 預期結果：位置配置有效

4. ✅ **Widget 指標驗證**
   - 規則：metric 必須是系統定義的有效指標
   - 預期結果：所有指標存在

**必須發布的領域事件：**
```json
{
  "eventId": "evt-rpt-dash-001",
  "eventType": "DashboardCreatedEvent",
  "timestamp": "2026-02-09T12:00:00Z",
  "aggregateId": "dashboard-001",
  "aggregateType": "Dashboard",
  "payload": {
    "dashboardId": "dashboard-001",
    "dashboardName": "HR 主管儀表板",
    "ownerId": "admin-001",
    "isDefault": true,
    "widgetCount": 3,
    "createdAt": "2026-02-09T12:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "dashboardId": "dashboard-001",
    "dashboardName": "HR 主管儀表板",
    "isDefault": true,
    "widgetCount": 3,
    "createdAt": "2026-02-09T12:00:00Z"
  }
}
```

---

#### RPT_CMD_006: 更新儀表板 Widget 配置

**業務場景描述：**
用戶調整儀表板上的 Widget 位置、大小或更新指標配置。

**API 端點：**
```
PUT /api/v1/dashboards/{id}/widgets
```

**前置條件：**
- 執行者必須是儀表板所有者或 ADMIN
- 儀表板必須存在

**輸入 (Request)：**
```json
{
  "dashboardId": "dashboard-001",
  "widgets": [
    {
      "widgetId": "widget-1",
      "position": {"x": 0, "y": 0, "width": 4, "height": 2}
    },
    {
      "widgetId": "widget-2",
      "position": {"x": 4, "y": 0, "width": 8, "height": 4}
    }
  ]
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "dashboardId": "dashboard-001",
    "updatedAt": "2026-02-09T13:00:00Z"
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 HR 報表查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RPT_QRY_001 | 查詢員工花名冊 | HR | `GET /api/v1/reports/hr/employee-roster` | `{"organizationId":"org-001"}` | `organization_id = 'org-001'` |
| RPT_QRY_002 | 查詢差勤統計 | HR | `GET /api/v1/reports/hr/attendance-summary` | `{"yearMonth":"2026-02"}` | `year_month = '2026-02'` |
| RPT_QRY_003 | 查詢離職率分析 | HR | `GET /api/v1/reports/hr/turnover` | `{"organizationId":"org-001","yearMonth":"2026-02"}` | `organization_id = 'org-001'`, `year_month = '2026-02'` |

#### 2.1.2 業務場景說明

**RPT_QRY_001: 查詢員工花名冊**

- **使用者：** HR 管理員
- **業務目的：** 查看組織的員工名單及基本資訊
- **權限控制：** `report:hr:view`
- **過濾邏輯：**
  ```sql
  SELECT * FROM employee_report_view
  WHERE organization_id = 'org-001'
  ORDER BY hire_date DESC
  ```

**RPT_QRY_002: 查詢差勤統計**

- **使用者：** HR 管理員
- **業務目的：** 查看月度加班時數、請假時數、曠職等統計
- **權限控制：** `report:hr:view`
- **過濾邏輯：**
  ```sql
  SELECT * FROM monthly_hr_stats
  WHERE year_month = '2026-02'
  ```

---

### 2.2 專案成本報表查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RPT_QRY_004 | 查詢專案成本分析 | PM | `GET /api/v1/reports/project/cost-analysis` | `{"projectId":"PRJ-001"}` | `project_id = 'PRJ-001'` |
| RPT_QRY_005 | 查詢稼動率分析 | PM | `GET /api/v1/reports/project/utilization-rate` | `{"projectId":"PRJ-001"}` | `project_id = 'PRJ-001'` |

#### 2.2.2 業務場景說明

**RPT_QRY_004: 查詢專案成本分析**

- **使用者：** 項目經理或 PM
- **業務目的：** 查看專案的實際成本、預算、利潤率等
- **權限控制：** `report:project:view` 或為項目 PM
- **過濾邏輯：**
  ```sql
  SELECT * FROM project_cost_snapshots
  WHERE project_id = 'PRJ-001'
  ORDER BY snapshot_date DESC
  LIMIT 1
  ```

---

### 2.3 財務報表查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RPT_QRY_006 | 查詢人力成本分析 | FINANCE | `GET /api/v1/reports/finance/labor-cost` | `{"yearMonth":"2026-02"}` | `year_month = '2026-02'` |
| RPT_QRY_007 | 查詢薪資總表 | FINANCE | `GET /api/v1/reports/finance/payroll-summary` | `{"yearMonth":"2026-02"}` | `year_month = '2026-02'` |

---

### 2.4 儀表板查詢

#### 2.4.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RPT_QRY_008 | 查詢儀表板 | EMPLOYEE | `GET /api/v1/dashboards` | `{}` | `owner_id = '{currentUserId}'` |
| RPT_QRY_009 | 查詢預設儀表板 | EMPLOYEE | `GET /api/v1/dashboards/default` | `{}` | `owner_id = '{currentUserId}'`, `is_default = true` |
| RPT_QRY_010 | 查詢儀表板詳情 | EMPLOYEE | `GET /api/v1/dashboards/{id}` | `{}` | `dashboard_id = '{id}'`, `owner_id = '{currentUserId}'` |

#### 2.4.2 業務場景說明

**RPT_QRY_008: 查詢儀表板**

- **使用者：** 任何認證用戶
- **業務目的：** 查看自己的儀表板列表
- **權限控制：** 無需特殊權限，但只能查詢自己的
- **過濾邏輯：**
  ```sql
  WHERE owner_id = '{currentUserId}'
  ORDER BY is_default DESC, created_at DESC
  ```

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `ReportGeneratedEvent` | 報表產生完成 | Reporting | Notification | 發送報表產生通知 |
| `ReportExportedEvent` | 報表匯出完成 | Reporting | Notification | 發送匯出完成通知 |
| `DashboardCreatedEvent` | 儀表板建立 | Reporting | - | - |
| `ReadModelUpdatedEvent` | ReadModel 快照更新 | Reporting | - | 用於跟蹤快照更新 |

---

### 3.2 ReportGeneratedEvent (報表產生事件)

**觸發時機：**
報表資料計算完成並儲存後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-rpt-gen-001",
  "eventType": "ReportGeneratedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "report-001",
  "aggregateType": "Report",
  "payload": {
    "reportId": "report-001",
    "reportType": "EMPLOYEE_ROSTER",
    "organizationId": "org-001",
    "startDate": "2026-02-01",
    "endDate": "2026-02-28",
    "generatedBy": "admin-001",
    "generatedAt": "2026-02-09T09:00:00Z",
    "totalRecords": 150,
    "metrics": {
      "totalEmployees": 150,
      "newHires": 5,
      "terminations": 2,
      "turnoverRate": 0.047,
      "averageSalary": 48200
    }
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送報表產生完成通知給生成者
  - 包含報表下載連結

---

### 3.3 ReadModelUpdatedEvent (讀模型更新事件)

**觸發時機：**
系統訂閱業務事件並更新 ReadModel 快照後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-readmodel-001",
  "eventType": "ReadModelUpdatedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "snapshot-employee-report-20260209",
  "aggregateType": "ReadModelSnapshot",
  "payload": {
    "snapshotType": "EMPLOYEE_REPORT",
    "snapshotDate": "2026-02-09",
    "recordCount": 1500,
    "updatedBy": "SYSTEM",
    "updatedAt": "2026-02-09T10:00:00Z",
    "refreshedMetrics": [
      "total_employees",
      "active_employees",
      "terminated_employees",
      "turnover_rate"
    ]
  }
}
```

---

## 4. 測試斷言規格

### 4.1 Command 操作測試斷言

**測試目標：** 驗證 Command 操作是否正確執行業務規則並發布領域事件。

**範例：RPT_CMD_001 生成報表測試**

```java
@Test
@DisplayName("RPT_CMD_001: 生成HR報表 - 應基於ReadModel計算並發布事件")
void generateHRReport_ShouldComputeAndPublishEvent() {
    // Given
    var request = GenerateReportRequest.builder()
        .reportType("EMPLOYEE_ROSTER")
        .organizationId("org-001")
        .startDate(LocalDate.of(2026, 2, 1))
        .endDate(LocalDate.of(2026, 2, 28))
        .build();

    // Mock organization exists
    when(organizationService.organizationExists("org-001")).thenReturn(true);

    // Mock ReadModel data exists
    var readModelData = Arrays.asList(
        new EmployeeReportRow("E001", "王小華", "D001", ...),
        new EmployeeReportRow("E002", "李四", "D002", ...)
    );
    when(employeeReportRepository.findAll()).thenReturn(readModelData);

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify report saved
    var captor = ArgumentCaptor.forClass(Report.class);
    verify(reportRepository).save(captor.capture());

    var savedReport = captor.getValue();
    assertThat(savedReport.getReportType()).isEqualTo(ReportType.EMPLOYEE_ROSTER);
    assertThat(savedReport.getStatus()).isEqualTo(ReportStatus.COMPLETED);

    // Then - Verify event published
    var eventCaptor = ArgumentCaptor.forClass(ReportGeneratedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("ReportGeneratedEvent");
    assertThat(event.getPayload().getTotalRecords()).isGreaterThan(0);
}
```

---

### 4.2 Query 操作測試斷言

**測試目標：** 驗證 Query 操作是否正確查詢 ReadModel。

**範例：RPT_QRY_001 查詢花名冊測試**

```java
@Test
@DisplayName("RPT_QRY_001: 查詢員工花名冊 - 應從ReadModel查詢")
void queryEmployeeRoster_ShouldQueryReadModel() {
    // Given
    var request = QueryReportRequest.builder()
        .reportType("EMPLOYEE_ROSTER")
        .organizationId("org-001")
        .build();

    // Mock ReadModel repository
    var mockData = Arrays.asList(
        new EmployeeReportView("E001", "王小華", "D001", ...),
        new EmployeeReportView("E002", "李四", "D002", ...)
    );
    when(employeeReportRepository.findByOrganizationId("org-001"))
        .thenReturn(mockData);

    // When
    var response = service.getResponse(request, currentUser);

    // Then
    assertThat(response.getRecords()).hasSize(2);
    assertThat(response.getRecords().get(0).getEmployeeName()).isEqualTo("王小華");
}
```

---

### 4.3 Integration Test 斷言

**測試目標：** 驗證完整的 API → Service → ReadModel 流程。

**範例：RPT_CMD_001 整合測試**

```java
@Test
@DisplayName("RPT_CMD_001: 生成報表整合測試 - 應完整計算並返回")
void generateReport_Integration_ShouldCompleteAndReturn() throws Exception {
    // Given
    var request = GenerateReportRequest.builder()
        .reportType("EMPLOYEE_ROSTER")
        .organizationId("org-001")
        .startDate(LocalDate.of(2026, 2, 1))
        .endDate(LocalDate.of(2026, 2, 28))
        .build();

    // When
    var result = mockMvc.perform(post("/api/v1/reports/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.reportId").isNotEmpty())
        .andExpect(jsonPath("$.data.status").value("COMPLETED"))
        .andReturn();

    // Then - Verify database
    var report = reportRepository.findByReportType("EMPLOYEE_ROSTER").get(0);
    assertThat(report.getStatus()).isEqualTo(ReportStatus.COMPLETED);
    assertThat(report.getTotalRecords()).isGreaterThan(0);
}
```

---

## 補充說明

### 5.1 CQRS 架構說明

**Command 側（寫入）：**
- 接收命令請求（生成報表、匯出等）
- 執行業務規則驗證
- 發布領域事件
- 返回命令執行結果

**Query 側（讀取）：**
- 訂閱所有業務服務的領域事件
- 更新非正規化的 ReadModel
- 響應查詢請求（無業務邏輯）
- 使用 Materialized View 提升查詢效能

### 5.2 ReadModel 快照更新機制

1. **定期刷新 Job（每小時）：**
   - `RefreshEmployeeViewJob`: 刷新員工報表視圖
   - 計算當前員工狀態的去正規化資料

2. **事件驅動更新（實時）：**
   - 訂閱 `EmployeeCreatedEvent`, `EmployeeTerminatedEvent` 等
   - 即時更新相關讀模型

3. **快照計算（每日）：**
   - `CalculateProjectCostJob`: 計算專案成本快照
   - `CalculateMonthlyStatsJob`: 計算月度 HR 統計

### 5.3 KPI 和指標計算

| 指標 | 計算公式 | 更新頻率 |
|:---|:---|:---|
| 離職率 | 本月離職人數 / 月初人數 × 100% | 每月1日 |
| 稼動率 | 計費工時 / 總可用工時 × 100% | 每日 |
| 專案獲利率 | (合約金額 - 實際成本) / 合約金額 × 100% | 每日 |
| 人均成本 | 總人力成本 / 員工人數 | 每月 |

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景（生成、匯出、儀表板管理）、ReadModel Query 場景、Domain Events 定義、CQRS 架構說明、測試斷言規格 |
| 1.0 | 2026-02-06 | 精簡版建立 |
