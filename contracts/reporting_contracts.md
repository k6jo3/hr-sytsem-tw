# 報表分析服務業務合約 (Reporting Service Business Contract)

> **服務代碼:** HR14
> **版本:** 2.0
> **更新日期:** 2026-02-10
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/14_報表分析服務系統設計書.md`
> - `knowledge/04_API_Specifications/14_報表分析服務系統設計書_API詳細規格.md`
> - `contracts/合約測試規範手冊.md`

---

## 📋 概述

本合約文件定義報表分析服務的**完整業務場景**，採用 **JSON Schema 格式**，確保所有測試內容**完全可驗證**。

**與 API 規格的對應：**
- ✅ 18 個 API 端點完整對應
- ✅ 包含 6 個 Command 操作場景
- ✅ 包含 12 個 Query 操作場景
- ✅ 每個場景都可自動化測試

**服務定位：**
報表分析服務採用 CQRS 模式，訂閱業務事件更新 ReadModel，提供多維度分析報表（HR、專案、財務）、自定義儀表板、報表匯出等功能。

---

## API 端點概覽

**總計：18 個 API 端點**

### 人力資源報表 API (4 個)

| # | 端點 | 方法 | 場景 ID |
|:---:|:---|:---:|:---|
| 1 | `GET /api/v1/reporting/hr/employee-roster` | GET | RPT_QRY_001 |
| 2 | `GET /api/v1/reporting/hr/headcount` | GET | RPT_QRY_002 |
| 3 | `GET /api/v1/reporting/hr/attendance-statistics` | GET | RPT_QRY_003 |
| 4 | `GET /api/v1/reporting/hr/turnover` | GET | RPT_QRY_004 |

### 專案管理報表 API (2 個)

| # | 端點 | 方法 | 場景 ID |
|:---:|:---|:---:|:---|
| 5 | `GET /api/v1/reporting/project/cost-analysis` | GET | RPT_QRY_005 |
| 6 | `GET /api/v1/reporting/project/utilization-rate` | GET | RPT_QRY_006 |

### 財務報表 API (3 個)

| # | 端點 | 方法 | 場景 ID |
|:---:|:---|:---:|:---|
| 7 | `GET /api/v1/reporting/finance/labor-cost` | GET | RPT_QRY_007 |
| 8 | `GET /api/v1/reporting/finance/labor-cost-by-department` | GET | RPT_QRY_008 |
| 9 | `GET /api/v1/reporting/finance/payroll-summary` | GET | RPT_QRY_009 |

### 儀表板 API (5 個)

| # | 端點 | 方法 | 場景 ID |
|:---:|:---|:---:|:---|
| 10 | `POST /api/v1/reporting/dashboards` | POST | RPT_CMD_001 |
| 11 | `GET /api/v1/reporting/dashboards` | GET | RPT_QRY_010 |
| 12 | `GET /api/v1/reporting/dashboards/{dashboardId}` | GET | RPT_QRY_011 |
| 13 | `PUT /api/v1/reporting/dashboards/{dashboardId}/widgets` | PUT | RPT_CMD_002 |
| 14 | `DELETE /api/v1/reporting/dashboards/{dashboardId}` | DELETE | RPT_CMD_003 |

### 報表匯出 API (4 個)

| # | 端點 | 方法 | 場景 ID |
|:---:|:---|:---:|:---|
| 15 | `POST /api/v1/reporting/export/excel` | POST | RPT_CMD_004 |
| 16 | `POST /api/v1/reporting/export/pdf` | POST | RPT_CMD_005 |
| 17 | `POST /api/v1/reporting/export/government` | POST | RPT_CMD_006 |
| 18 | `GET /api/v1/reporting/export/{exportId}/download` | GET | RPT_QRY_012 |

---

## 1. Command 操作業務合約

### 1.1 儀表板管理

#### RPT_CMD_001: 建立自定義儀表板

**API 端點：** `POST /api/v1/reporting/dashboards`

**業務場景描述：**

使用者建立個人化儀表板，可自訂版面配置、Widget 類型與數量。系統驗證配置格式後，建立儀表板記錄，預設為私有（僅自己可見）。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_001",
  "apiEndpoint": "POST /api/v1/reporting/dashboards",
  "controller": "HR14DashboardCmdController",
  "service": "CreateDashboardServiceImpl",
  "permission": "dashboard:create",

  "request": {
    "name": "我的儀表板",
    "layoutConfig": {
      "cols": 12,
      "rowHeight": 100
    }
  },

  "businessRules": [
    {
      "rule": "儀表板名稱唯一性檢查",
      "description": "同一使用者下不可建立同名的儀表板，違反時回傳 RPT_DASHBOARD_NAME_DUPLICATE"
    }
  ],

  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "rpt_dashboard",
      "count": 1,
      "assertions": [
        {"field": "dashboard_id", "operator": "notNull"},
        {"field": "dashboard_name", "operator": "equals", "value": "我的儀表板"},
        {"field": "owner_id", "operator": "notNull"},
        {"field": "is_public", "operator": "equals", "value": false},
        {"field": "is_default", "operator": "equals", "value": false},
        {"field": "created_at", "operator": "notNull"}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "DashboardCreatedEvent",
      "payload": [
        {"field": "dashboardId", "operator": "notNull"},
        {"field": "dashboardName", "operator": "equals", "value": "我的儀表板"},
        {"field": "ownerId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### RPT_CMD_002: 更新儀表板 Widget 配置

**API 端點：** `PUT /api/v1/reporting/dashboards/{dashboardId}/widgets`

**業務場景描述：**

使用者更新儀表板的 Widget 配置（圖表類型、資料來源、顯示參數等）。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_002",
  "apiEndpoint": "PUT /api/v1/reporting/dashboards/{dashboardId}/widgets",
  "controller": "HR14DashboardCmdController",
  "service": "UpdateDashboardWidgetsServiceImpl",
  "permission": "dashboard:update",

  "request": {
    "dashboardId": "{testDashboardId}",
    "widgets": [
      {"type": "CHART", "config": {"chartType": "bar"}}
    ]
  },

  "businessRules": [
    {
      "rule": "儀表板存在性檢查",
      "description": "儀表板必須存在且為啟用狀態，違反時回傳 RPT_DASHBOARD_NOT_FOUND"
    },
    {
      "rule": "所有權檢查",
      "description": "只有儀表板的擁有者才能更新，違反時回傳 RPT_DASHBOARD_ACCESS_DENIED"
    }
  ],

  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "rpt_dashboard",
      "count": 1,
      "assertions": [
        {"field": "widgets_config", "operator": "notNull"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "DashboardUpdatedEvent",
      "payload": [
        {"field": "dashboardId", "operator": "notNull"},
        {"field": "updatedFields", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### RPT_CMD_003: 刪除儀表板

**API 端點：** `DELETE /api/v1/reporting/dashboards/{dashboardId}`

**業務場景描述：**

使用者刪除自己的儀表板。系統執行硬刪除，從資料庫中移除該筆記錄。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_003",
  "apiEndpoint": "DELETE /api/v1/reporting/dashboards/{dashboardId}",
  "controller": "HR14DashboardCmdController",
  "service": "DeleteDashboardServiceImpl",
  "permission": "dashboard:delete",

  "request": {
    "dashboardId": "{testDashboardId}"
  },

  "businessRules": [
    {
      "rule": "預設儀表板檢查",
      "description": "預設儀表板不可刪除，違反時回傳 RPT_DASHBOARD_DEFAULT_CANNOT_DELETE"
    }
  ],

  "expectedDataChanges": [
    {
      "action": "DELETE",
      "table": "rpt_dashboard",
      "count": 1
    }
  ],

  "expectedEvents": [
    {
      "eventType": "DashboardDeletedEvent",
      "payload": [
        {"field": "dashboardId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

### 1.2 報表匯出

#### RPT_CMD_004: 匯出報表為 Excel

**API 端點：** `POST /api/v1/reporting/export/excel`

**業務場景描述：**

使用者請求匯出報表為 Excel 格式。系統建立匯出任務（非同步處理）。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_004",
  "apiEndpoint": "POST /api/v1/reporting/export/excel",
  "controller": "HR14ExportCmdController",
  "service": "ExportExcelServiceImpl",
  "permission": "report:export",

  "request": {
    "reportType": "EMPLOYEE_ROSTER",
    "filters": {
      "organizationId": "org-001",
      "status": "ACTIVE"
    },
    "fileName": "員工名冊.xlsx"
  },

  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "report_exports",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "report_type", "operator": "equals", "value": "EMPLOYEE_ROSTER"},
        {"field": "format", "operator": "equals", "value": "EXCEL"},
        {"field": "status", "operator": "equals", "value": "PROCESSING"},
        {"field": "file_name", "operator": "equals", "value": "員工名冊.xlsx"},
        {"field": "requester_id", "operator": "notNull"},
        {"field": "created_at", "operator": "notNull"}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "ReportExportRequestedEvent",
      "payload": [
        {"field": "exportId", "operator": "notNull"},
        {"field": "reportType", "operator": "equals", "value": "EMPLOYEE_ROSTER"},
        {"field": "format", "operator": "equals", "value": "EXCEL"}
      ]
    }
  ]
}
```

---

#### RPT_CMD_005: 匯出報表為 PDF

**API 端點：** `POST /api/v1/reporting/export/pdf`

**業務場景描述：**

使用者請求匯出報表為 PDF 格式。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_005",
  "apiEndpoint": "POST /api/v1/reporting/export/pdf",
  "controller": "HR14ExportCmdController",
  "service": "ExportPdfServiceImpl",
  "permission": "report:export",

  "request": {
    "reportType": "PAYROLL_SUMMARY",
    "filters": {
      "month": "2026-02"
    },
    "fileName": "薪資總表.pdf"
  },

  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "report_exports",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "report_type", "operator": "equals", "value": "PAYROLL_SUMMARY"},
        {"field": "format", "operator": "equals", "value": "PDF"},
        {"field": "status", "operator": "equals", "value": "PROCESSING"},
        {"field": "file_name", "operator": "equals", "value": "薪資總表.pdf"},
        {"field": "requester_id", "operator": "notNull"},
        {"field": "created_at", "operator": "notNull"}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "ReportExportRequestedEvent",
      "payload": [
        {"field": "exportId", "operator": "notNull"},
        {"field": "reportType", "operator": "equals", "value": "PAYROLL_SUMMARY"},
        {"field": "format", "operator": "equals", "value": "PDF"}
      ]
    }
  ]
}
```

---

#### RPT_CMD_006: 政府申報格式匯出

**API 端點：** `POST /api/v1/reporting/export/government`

**業務場景描述：**

HR 人員匯出政府申報格式檔案（勞保、健保、勞退等）。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_006",
  "apiEndpoint": "POST /api/v1/reporting/export/government",
  "controller": "HR14ExportCmdController",
  "service": "ExportGovernmentFormatServiceImpl",
  "permission": "report:export:government",

  "request": {
    "formatType": "LABOR_INSURANCE",
    "period": "2026-02"
  },

  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "report_exports",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "report_type", "operator": "equals", "value": "GOVERNMENT_FORMAT"},
        {"field": "format", "operator": "equals", "value": "TXT"},
        {"field": "format_type", "operator": "equals", "value": "LABOR_INSURANCE"},
        {"field": "status", "operator": "equals", "value": "PROCESSING"},
        {"field": "period", "operator": "equals", "value": "2026-02"},
        {"field": "requester_id", "operator": "notNull"},
        {"field": "created_at", "operator": "notNull"}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "GovernmentReportExportRequestedEvent",
      "payload": [
        {"field": "exportId", "operator": "notNull"},
        {"field": "formatType", "operator": "equals", "value": "LABOR_INSURANCE"},
        {"field": "period", "operator": "equals", "value": "2026-02"}
      ]
    }
  ]
}
```

---

## 2. Query 操作業務合約

### 2.1 HR 報表查詢

#### RPT_QRY_001: 查詢員工花名冊

**API 端點：** `GET /api/v1/reporting/hr/employee-roster`

**業務場景描述：**

查看組織的員工名單及基本資訊。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_001",
  "apiEndpoint": "GET /api/v1/reporting/hr/employee-roster",
  "controller": "HR14ReportQryController",
  "service": "GetEmployeeRosterServiceImpl",
  "permission": "report:hr:read",

  "request": {
    "organizationId": "org-001",
    "status": "ACTIVE",
    "page": 0,
    "size": 50
  },

  "expectedQueryFilters": [
    {"field": "organization_id", "operator": "=", "value": "org-001"},
    {"field": "status", "operator": "=", "value": "ACTIVE"}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "content",
    "minRecords": 1,
    "requiredFields": [
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "name", "type": "string", "notNull": true},
      {"name": "departmentName", "type": "string"},
      {"name": "positionName", "type": "string"},
      {"name": "hireDate", "type": "date"},
      {"name": "serviceYears", "type": "decimal"},
      {"name": "status", "type": "string"},
      {"name": "email", "type": "string"}
    ]
  }
}
```

---

#### RPT_QRY_002: 查詢人力盤點報表

**API 端點：** `GET /api/v1/reporting/hr/headcount`

**業務場景描述：**

查看組織人力概況（在職人數、本月到離職、離職率、部門分布）。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_002",
  "apiEndpoint": "GET /api/v1/reporting/hr/headcount",
  "controller": "HR14ReportQryController",
  "service": "GetHeadcountReportServiceImpl",
  "permission": "report:hr:read",

  "request": {
    "organizationId": "org-001",
    "dimension": "DEPARTMENT"
  },

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "content",
    "minRecords": 0,
    "requiredFields": [
      {"name": "dimensionName", "type": "string"},
      {"name": "activeCount", "type": "integer"},
      {"name": "totalCount", "type": "integer"},
      {"name": "avgServiceYears", "type": "decimal"}
    ]
  }
}
```

---

#### RPT_QRY_003: 查詢差勤統計報表

**API 端點：** `GET /api/v1/reporting/hr/attendance-statistics`

**業務場景描述：**

查看月度加班時數、請假時數、曠職等統計。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_003",
  "apiEndpoint": "GET /api/v1/reporting/hr/attendance-statistics",
  "controller": "HR14ReportQryController",
  "service": "GetAttendanceStatisticsServiceImpl",
  "permission": "report:hr:read",

  "request": {
    "departmentId": "D001"
  },

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "content",
    "minRecords": 0,
    "requiredFields": [
      {"name": "employeeId", "type": "string"},
      {"name": "employeeName", "type": "string"},
      {"name": "departmentName", "type": "string"},
      {"name": "actualDays", "type": "integer"},
      {"name": "leaveDays", "type": "decimal"},
      {"name": "overtimeHours", "type": "decimal"},
      {"name": "attendanceRate", "type": "decimal"}
    ]
  }
}
```

---

#### RPT_QRY_004: 查詢離職率分析

**API 端點：** `GET /api/v1/reporting/hr/turnover`

**業務場景描述：**

查看離職趨勢、離職原因分布、部門離職率比較。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_004",
  "apiEndpoint": "GET /api/v1/reporting/hr/turnover",
  "controller": "HR14ReportQryController",
  "service": "GetTurnoverAnalysisServiceImpl",
  "permission": "report:hr:read",

  "request": {
    "organizationId": "org-001",
    "yearMonth": "2026-02"
  },

  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "organizationId", "type": "string"},
      {"name": "yearMonth", "type": "string"},
      {"name": "turnoverRate", "type": "decimal"},
      {"name": "totalEmployees", "type": "integer"},
      {"name": "newHires", "type": "integer"},
      {"name": "terminations", "type": "integer"}
    ]
  }
}
```

---

### 2.2 專案報表查詢

#### RPT_QRY_005: 查詢專案成本分析

**API 端點：** `GET /api/v1/reporting/project/cost-analysis`

**業務場景描述：**

查看專案的實際成本、預算、利潤率等。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_005",
  "apiEndpoint": "GET /api/v1/reporting/project/cost-analysis",
  "controller": "HR14ReportQryController",
  "service": "GetProjectCostAnalysisServiceImpl",
  "permission": "report:project:read",

  "request": {
    "projectId": "PRJ-001"
  },

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "content",
    "minRecords": 0,
    "requiredFields": [
      {"name": "projectId", "type": "string"},
      {"name": "projectName", "type": "string"},
      {"name": "budgetAmount", "type": "decimal"},
      {"name": "totalCost", "type": "decimal"},
      {"name": "costVarianceRate", "type": "decimal"},
      {"name": "totalHours", "type": "decimal"},
      {"name": "utilizationRate", "type": "decimal"}
    ]
  }
}
```

---

#### RPT_QRY_006: 查詢稼動率分析

**API 端點：** `GET /api/v1/reporting/project/utilization-rate`

**業務場景描述：**

查看員工稼動率（billable hours 占比）。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_006",
  "apiEndpoint": "GET /api/v1/reporting/project/utilization-rate",
  "controller": "HR14ReportQryController",
  "service": "GetUtilizationRateServiceImpl",
  "permission": "report:project:read",

  "request": {
    "projectId": "PRJ-001",
    "yearMonth": "2026-02"
  },

  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "projectId", "type": "string"},
      {"name": "projectName", "type": "string"},
      {"name": "yearMonth", "type": "string"},
      {"name": "utilizationRate", "type": "decimal"},
      {"name": "totalHours", "type": "integer"},
      {"name": "billableHours", "type": "integer"}
    ]
  }
}
```

---

### 2.3 財務報表查詢

#### RPT_QRY_007: 查詢人力成本分析

**API 端點：** `GET /api/v1/reporting/finance/labor-cost`

**業務場景描述：**

彙整薪資、勞保、健保、勞退等人力成本。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_007",
  "apiEndpoint": "GET /api/v1/reporting/finance/labor-cost",
  "controller": "HR14ReportQryController",
  "service": "GetLaborCostAnalysisServiceImpl",
  "permission": "report:finance:read",

  "request": {
    "organizationId": "org-001",
    "yearMonth": "2026-02"
  },

  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "organizationId", "type": "string"},
      {"name": "yearMonth", "type": "string"},
      {"name": "totalCost", "type": "decimal"},
      {"name": "employeeCount", "type": "integer"},
      {"name": "averageCost", "type": "decimal"}
    ]
  }
}
```

---

#### RPT_QRY_008: 查詢部門人力成本分析

**API 端點：** `GET /api/v1/reporting/finance/labor-cost-by-department`

**業務場景描述：**

比較各部門人力成本、人均成本。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_008",
  "apiEndpoint": "GET /api/v1/reporting/finance/labor-cost-by-department",
  "controller": "HR14ReportQryController",
  "service": "GetLaborCostByDepartmentServiceImpl",
  "permission": "report:finance:read",

  "request": {
    "departmentId": "D001",
    "yearMonth": "2026-02"
  },

  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "departmentId", "type": "string"},
      {"name": "departmentName", "type": "string"},
      {"name": "yearMonth", "type": "string"},
      {"name": "totalCost", "type": "decimal"},
      {"name": "employeeCount", "type": "integer"}
    ]
  }
}
```

---

#### RPT_QRY_009: 查詢薪資總表

**API 端點：** `GET /api/v1/reporting/finance/payroll-summary`

**業務場景描述：**

彙整當月全體員工薪資發放明細。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_009",
  "apiEndpoint": "GET /api/v1/reporting/finance/payroll-summary",
  "controller": "HR14ReportQryController",
  "service": "GetPayrollSummaryServiceImpl",
  "permission": "report:finance:read",

  "request": {
    "yearMonth": "2026-02"
  },

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "content",
    "minRecords": 0,
    "requiredFields": [
      {"name": "employeeId", "type": "string"},
      {"name": "employeeName", "type": "string"},
      {"name": "departmentName", "type": "string"},
      {"name": "baseSalary", "type": "decimal"},
      {"name": "grossPay", "type": "decimal"},
      {"name": "netPay", "type": "decimal"}
    ]
  }
}
```

---

### 2.4 儀表板查詢

#### RPT_QRY_010: 查詢儀表板列表

**API 端點：** `GET /api/v1/reporting/dashboards`

**業務場景描述：**

查看自己的儀表板與公開儀表板列表。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_010",
  "apiEndpoint": "GET /api/v1/reporting/dashboards",
  "controller": "HR14DashboardQryController",
  "service": "GetDashboardListServiceImpl",
  "permission": "dashboard:read",

  "request": {
    "includePublic": true
  },

  "expectedQueryFilters": [],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "content",
    "minRecords": 0,
    "requiredFields": [
      {"name": "dashboardId", "type": "string", "notNull": true},
      {"name": "dashboardName", "type": "string", "notNull": true},
      {"name": "isPublic", "type": "boolean"},
      {"name": "isDefault", "type": "boolean"},
      {"name": "createdAt", "type": "datetime"}
    ]
  }
}
```

---

#### RPT_QRY_011: 查詢儀表板詳情

**API 端點：** `GET /api/v1/reporting/dashboards/{dashboardId}`

**業務場景描述：**

查看儀表板的完整配置。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_011",
  "apiEndpoint": "GET /api/v1/reporting/dashboards/{dashboardId}",
  "controller": "HR14DashboardQryController",
  "service": "GetDashboardDetailServiceImpl",
  "permission": "dashboard:read",

  "request": {
    "dashboardId": "{testDashboardId}"
  },

  "expectedQueryFilters": [
    {"field": "id", "operator": "=", "value": "{request.dashboardId}"},
    {"field": "is_active", "operator": "=", "value": true}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "dashboardId", "type": "string", "notNull": true},
      {"name": "dashboardName", "type": "string", "notNull": true},
      {"name": "description", "type": "string"},
      {"name": "isDefault", "type": "boolean"},
      {"name": "widgets", "type": "array"}
    ]
  }
}
```

---

### 2.5 報表匯出查詢

#### RPT_QRY_012: 下載匯出檔案

**API 端點：** `GET /api/v1/reporting/export/{exportId}/download`

**業務場景描述：**

下載已完成的匯出檔案。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_012",
  "apiEndpoint": "GET /api/v1/reporting/export/{exportId}/download",
  "controller": "HR14ExportQryController",
  "service": "DownloadExportFileServiceImpl",
  "permission": "report:export",

  "request": {
    "exportId": "{testExportId}"
  },

  "expectedQueryFilters": [
    {"field": "id", "operator": "=", "value": "{request.exportId}"},
    {"field": "requester_id", "operator": "=", "value": "{currentUserId}"},
    {"field": "status", "operator": "=", "value": "COMPLETED"}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "isBinaryResponse": true,
    "expectedHeaders": {
      "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition": "attachment"
    }
  }
}
```

---

**文件建立時間：** 2026-02-10
**版本：** 2.0
