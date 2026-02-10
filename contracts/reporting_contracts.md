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
| 1 | `GET /api/v1/reports/hr/employee-roster` | GET | RPT_QRY_001 |
| 2 | `GET /api/v1/reports/hr/headcount` | GET | RPT_QRY_002 |
| 3 | `GET /api/v1/reports/hr/attendance-summary` | GET | RPT_QRY_003 |
| 4 | `GET /api/v1/reports/hr/turnover` | GET | RPT_QRY_004 |

### 專案管理報表 API (2 個)

| # | 端點 | 方法 | 場景 ID |
|:---:|:---|:---:|:---|
| 5 | `GET /api/v1/reports/project/cost-analysis` | GET | RPT_QRY_005 |
| 6 | `GET /api/v1/reports/project/utilization-rate` | GET | RPT_QRY_006 |

### 財務報表 API (3 個)

| # | 端點 | 方法 | 場景 ID |
|:---:|:---|:---:|:---|
| 7 | `GET /api/v1/reports/finance/labor-cost` | GET | RPT_QRY_007 |
| 8 | `GET /api/v1/reports/finance/labor-cost-by-department` | GET | RPT_QRY_008 |
| 9 | `GET /api/v1/reports/finance/payroll-summary` | GET | RPT_QRY_009 |

### 儀表板 API (5 個)

| # | 端點 | 方法 | 場景 ID |
|:---:|:---|:---:|:---|
| 10 | `POST /api/v1/dashboards` | POST | RPT_CMD_001 |
| 11 | `GET /api/v1/dashboards` | GET | RPT_QRY_010 |
| 12 | `GET /api/v1/dashboards/{id}` | GET | RPT_QRY_011 |
| 13 | `PUT /api/v1/dashboards/{id}/widgets` | PUT | RPT_CMD_002 |
| 14 | `DELETE /api/v1/dashboards/{id}` | DELETE | RPT_CMD_003 |

### 報表匯出 API (4 個)

| # | 端點 | 方法 | 場景 ID |
|:---:|:---|:---:|:---|
| 15 | `POST /api/v1/reports/export/excel` | POST | RPT_CMD_004 |
| 16 | `POST /api/v1/reports/export/pdf` | POST | RPT_CMD_005 |
| 17 | `POST /api/v1/reports/export/government` | POST | RPT_CMD_006 |
| 18 | `GET /api/v1/reports/export/{id}/download` | GET | RPT_QRY_012 |

---

## 1. Command 操作業務合約

### 1.1 儀表板管理

#### RPT_CMD_001: 建立自定義儀表板

**API 端點：** `POST /api/v1/dashboards`

**業務場景描述：**

使用者建立個人化儀表板，可自訂版面配置、Widget 類型與數量。系統驗證配置格式後，建立儀表板記錄，預設為私有（僅自己可見）。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_001",
  "apiEndpoint": "POST /api/v1/dashboards",
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
      "name": "儀表板名稱唯一性檢查",
      "validation": {
        "type": "query",
        "query": "SELECT COUNT(*) FROM dashboards WHERE name = ? AND owner_id = ? AND is_active = true",
        "params": ["{request.name}", "{currentUserId}"],
        "expectedResult": 0,
        "errorCode": "RPT_DASHBOARD_NAME_DUPLICATE"
      }
    }
  ],

  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "dashboards",
      "count": 1,
      "assertions": [
        {"field": "id", "type": "uuid", "notNull": true},
        {"field": "name", "equals": "我的儀表板"},
        {"field": "owner_id", "equals": "{currentUserId}"},
        {"field": "is_active", "equals": true},
        {"field": "is_public", "equals": false},
        {"field": "is_default", "equals": false},
        {"field": "created_at", "type": "datetime", "notNull": true},
        {"field": "created_by", "equals": "{currentUserId}"}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "DashboardCreatedEvent",
      "payload": {
        "dashboardId": "{uuid}",
        "dashboardName": "我的儀表板",
        "ownerId": "{currentUserId}"
      }
    }
  ]
}
```

---

#### RPT_CMD_002: 更新儀表板 Widget 配置

**API 端點：** `PUT /api/v1/dashboards/{id}/widgets`

**業務場景描述：**

使用者更新儀表板的 Widget 配置（圖表類型、資料來源、顯示參數等）。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_002",
  "apiEndpoint": "PUT /api/v1/dashboards/{id}/widgets",
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
      "name": "儀表板存在性檢查",
      "validation": {
        "type": "query",
        "query": "SELECT COUNT(*) FROM dashboards WHERE id = ? AND is_active = true",
        "params": ["{request.dashboardId}"],
        "expectedResult": 1,
        "errorCode": "RPT_DASHBOARD_NOT_FOUND"
      }
    },
    {
      "name": "所有權檢查",
      "validation": {
        "type": "query",
        "query": "SELECT COUNT(*) FROM dashboards WHERE id = ? AND owner_id = ?",
        "params": ["{request.dashboardId}", "{currentUserId}"],
        "expectedResult": 1,
        "errorCode": "RPT_DASHBOARD_ACCESS_DENIED"
      }
    }
  ],

  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "dashboards",
      "count": 1,
      "where": {
        "id": "{request.dashboardId}"
      },
      "assertions": [
        {"field": "widgets_config", "notNull": true},
        {"field": "updated_at", "type": "datetime", "notNull": true},
        {"field": "updated_by", "equals": "{currentUserId}"},
        {"field": "name", "unchanged": true},
        {"field": "owner_id", "unchanged": true}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "DashboardUpdatedEvent",
      "payload": {
        "dashboardId": "{request.dashboardId}",
        "updatedFields": ["widgetsConfig"]
      }
    }
  ]
}
```

---

#### RPT_CMD_003: 刪除儀表板

**API 端點：** `DELETE /api/v1/dashboards/{id}`

**業務場景描述：**

使用者刪除自己的儀表板。系統執行軟刪除，將 is_active 設為 false。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_003",
  "apiEndpoint": "DELETE /api/v1/dashboards/{id}",
  "controller": "HR14DashboardCmdController",
  "service": "DeleteDashboardServiceImpl",
  "permission": "dashboard:delete",

  "request": {
    "dashboardId": "{testDashboardId}"
  },

  "businessRules": [
    {
      "name": "預設儀表板檢查",
      "validation": {
        "type": "query",
        "query": "SELECT COUNT(*) FROM dashboards WHERE id = ? AND is_default = true",
        "params": ["{request.dashboardId}"],
        "expectedResult": 0,
        "errorCode": "RPT_DASHBOARD_DEFAULT_CANNOT_DELETE"
      }
    }
  ],

  "expectedDataChanges": [
    {
      "action": "SOFT_DELETE",
      "table": "dashboards",
      "count": 1,
      "where": {
        "id": "{request.dashboardId}"
      },
      "assertions": [
        {"field": "is_active", "from": true, "to": false},
        {"field": "deleted_at", "type": "datetime", "notNull": true},
        {"field": "deleted_by", "equals": "{currentUserId}"},
        {"field": "name", "unchanged": true}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "DashboardDeletedEvent",
      "payload": {
        "dashboardId": "{request.dashboardId}"
      }
    }
  ]
}
```

---

### 1.2 報表匯出

#### RPT_CMD_004: 匯出報表為 Excel

**API 端點：** `POST /api/v1/reports/export/excel`

**業務場景描述：**

使用者請求匯出報表為 Excel 格式。系統建立匯出任務（非同步處理）。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_004",
  "apiEndpoint": "POST /api/v1/reports/export/excel",
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
        {"field": "id", "type": "uuid", "notNull": true},
        {"field": "report_type", "equals": "EMPLOYEE_ROSTER"},
        {"field": "format", "equals": "EXCEL"},
        {"field": "status", "equals": "PROCESSING"},
        {"field": "file_name", "equals": "員工名冊.xlsx"},
        {"field": "requester_id", "equals": "{currentUserId}"},
        {"field": "created_at", "type": "datetime", "notNull": true}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "ReportExportRequestedEvent",
      "payload": {
        "exportId": "{uuid}",
        "reportType": "EMPLOYEE_ROSTER",
        "format": "EXCEL"
      }
    }
  ]
}
```

---

#### RPT_CMD_005: 匯出報表為 PDF

**API 端點：** `POST /api/v1/reports/export/pdf`

**業務場景描述：**

使用者請求匯出報表為 PDF 格式。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_005",
  "apiEndpoint": "POST /api/v1/reports/export/pdf",
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
        {"field": "id", "type": "uuid", "notNull": true},
        {"field": "report_type", "equals": "PAYROLL_SUMMARY"},
        {"field": "format", "equals": "PDF"},
        {"field": "status", "equals": "PROCESSING"},
        {"field": "file_name", "equals": "薪資總表.pdf"},
        {"field": "requester_id", "equals": "{currentUserId}"},
        {"field": "created_at", "type": "datetime", "notNull": true}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "ReportExportRequestedEvent",
      "payload": {
        "exportId": "{uuid}",
        "reportType": "PAYROLL_SUMMARY",
        "format": "PDF"
      }
    }
  ]
}
```

---

#### RPT_CMD_006: 政府申報格式匯出

**API 端點：** `POST /api/v1/reports/export/government`

**業務場景描述：**

HR 人員匯出政府申報格式檔案（勞保、健保、勞退等）。

**測試合約：**

```json
{
  "scenarioId": "RPT_CMD_006",
  "apiEndpoint": "POST /api/v1/reports/export/government",
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
        {"field": "id", "type": "uuid", "notNull": true},
        {"field": "report_type", "equals": "GOVERNMENT_FORMAT"},
        {"field": "format", "equals": "TXT"},
        {"field": "format_type", "equals": "LABOR_INSURANCE"},
        {"field": "status", "equals": "PROCESSING"},
        {"field": "period", "equals": "2026-02"},
        {"field": "requester_id", "equals": "{currentUserId}"},
        {"field": "created_at", "type": "datetime", "notNull": true}
      ]
    }
  ],

  "expectedEvents": [
    {
      "eventType": "GovernmentReportExportRequestedEvent",
      "payload": {
        "exportId": "{uuid}",
        "formatType": "LABOR_INSURANCE",
        "period": "2026-02"
      }
    }
  ]
}
```

---

## 2. Query 操作業務合約

### 2.1 HR 報表查詢

#### RPT_QRY_001: 查詢員工花名冊

**API 端點：** `GET /api/v1/reports/hr/employee-roster`

**業務場景描述：**

查看組織的員工名單及基本資訊。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_001",
  "apiEndpoint": "GET /api/v1/reports/hr/employee-roster",
  "controller": "HR14HrQryController",
  "service": "GetEmployeeRosterServiceImpl",
  "permission": "report:hr:read",

  "request": {
    "organizationId": "org-001",
    "status": "ACTIVE",
    "page": 1,
    "size": 50
  },

  "expectedQueryFilters": [
    {"field": "organization_id", "operator": "=", "value": "org-001"},
    {"field": "employment_status", "operator": "=", "value": "ACTIVE"}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.content",
    "minRecords": 1,
    "maxRecords": 1000,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid"},
      {"name": "employeeNumber", "type": "string"},
      {"name": "fullName", "type": "string"},
      {"name": "nationalIdMasked", "type": "string", "format": "masked"},
      {"name": "email", "type": "email"},
      {"name": "serviceYears", "type": "decimal", "precision": 1},
      {"name": "hireDate", "type": "date"}
    ],
    "orderBy": {
      "field": "employeeNumber",
      "direction": "ASC"
    },
    "pagination": {
      "required": true,
      "fields": ["page", "size", "totalElements", "totalPages"]
    },
    "assertions": [
      {"field": "nationalIdMasked", "operator": "contains", "value": "*"},
      {"field": "employmentStatus", "operator": "in", "value": ["ACTIVE", "ON_LEAVE", "TERMINATED"]}
    ]
  }
}
```

---

#### RPT_QRY_002: 查詢人力盤點報表

**API 端點：** `GET /api/v1/reports/hr/headcount`

**業務場景描述：**

查看組織人力概況（在職人數、本月到離職、離職率、部門分布）。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_002",
  "apiEndpoint": "GET /api/v1/reports/hr/headcount",
  "controller": "HR14HrQryController",
  "service": "GetHeadcountReportServiceImpl",
  "permission": "report:hr:read",

  "request": {
    "organizationId": "org-001",
    "asOfDate": "2026-02-09"
  },

  "expectedQueryFilters": [
    {"field": "organization_id", "operator": "=", "value": "org-001"},
    {"field": "as_of_date", "operator": "=", "value": "2026-02-09"}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "exactRecords": 1,
    "requiredFields": [
      {"name": "totalEmployees", "type": "integer"},
      {"name": "newHires", "type": "integer"},
      {"name": "terminations", "type": "integer"},
      {"name": "turnoverRate", "type": "decimal", "precision": 2},
      {"name": "avgServiceYears", "type": "decimal", "precision": 1},
      {"name": "departmentBreakdown", "type": "array"}
    ]
  }
}
```

---

#### RPT_QRY_003: 查詢差勤統計報表

**API 端點：** `GET /api/v1/reports/hr/attendance-summary`

**業務場景描述：**

查看月度加班時數、請假時數、曠職等統計。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_003",
  "apiEndpoint": "GET /api/v1/reports/hr/attendance-summary",
  "controller": "HR14HrQryController",
  "service": "GetAttendanceSummaryServiceImpl",
  "permission": "report:hr:read",

  "request": {
    "yearMonth": "2026-02"
  },

  "expectedQueryFilters": [
    {"field": "year_month", "operator": "=", "value": "2026-02"}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.content",
    "minRecords": 1,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid"},
      {"name": "employeeName", "type": "string"},
      {"name": "totalWorkingHours", "type": "decimal", "precision": 1},
      {"name": "overtimeHours", "type": "decimal", "precision": 1},
      {"name": "leaveHours", "type": "decimal", "precision": 1}
    ],
    "orderBy": {
      "field": "overtimeHours",
      "direction": "DESC"
    },
    "pagination": {
      "required": true
    }
  }
}
```

---

#### RPT_QRY_004: 查詢離職率分析

**API 端點：** `GET /api/v1/reports/hr/turnover`

**業務場景描述：**

查看離職趨勢、離職原因分布、部門離職率比較。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_004",
  "apiEndpoint": "GET /api/v1/reports/hr/turnover",
  "controller": "HR14HrQryController",
  "service": "GetTurnoverAnalysisServiceImpl",
  "permission": "report:hr:read",

  "request": {
    "organizationId": "org-001",
    "year": 2026
  },

  "expectedQueryFilters": [
    {"field": "organization_id", "operator": "=", "value": "org-001"},
    {"field": "year", "operator": "=", "value": 2026}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "exactRecords": 12,
    "requiredFields": [
      {"name": "month", "type": "integer"},
      {"name": "headcount", "type": "integer"},
      {"name": "terminations", "type": "integer"},
      {"name": "turnoverRate", "type": "decimal", "precision": 2},
      {"name": "reasonBreakdown", "type": "object"}
    ],
    "orderBy": {
      "field": "month",
      "direction": "ASC"
    }
  }
}
```

---

### 2.2 專案報表查詢

#### RPT_QRY_005: 查詢專案成本分析

**API 端點：** `GET /api/v1/reports/project/cost-analysis`

**業務場景描述：**

查看專案的實際成本、預算、利潤率等。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_005",
  "apiEndpoint": "GET /api/v1/reports/project/cost-analysis",
  "controller": "HR14ProjectQryController",
  "service": "GetProjectCostAnalysisServiceImpl",
  "permission": "report:project:read",

  "request": {
    "projectId": "PRJ-001"
  },

  "expectedQueryFilters": [
    {"field": "project_id", "operator": "=", "value": "PRJ-001"}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "exactRecords": 1,
    "requiredFields": [
      {"name": "projectId", "type": "string"},
      {"name": "projectName", "type": "string"},
      {"name": "budgetAmount", "type": "number"},
      {"name": "actualCost", "type": "number"},
      {"name": "budgetUsageRate", "type": "decimal", "precision": 2},
      {"name": "profitMargin", "type": "decimal", "precision": 2},
      {"name": "costByPhase", "type": "array"},
      {"name": "costByEmployee", "type": "array"}
    ]
  }
}
```

---

#### RPT_QRY_006: 查詢稼動率分析

**API 端點：** `GET /api/v1/reports/project/utilization-rate`

**業務場景描述：**

查看員工稼動率（billable hours 占比）。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_006",
  "apiEndpoint": "GET /api/v1/reports/project/utilization-rate",
  "controller": "HR14ProjectQryController",
  "service": "GetUtilizationRateServiceImpl",
  "permission": "report:project:read",

  "request": {
    "departmentId": "D001",
    "month": "2026-02"
  },

  "expectedQueryFilters": [
    {"field": "department_id", "operator": "=", "value": "D001"},
    {"field": "month", "operator": "=", "value": "2026-02"}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.content",
    "minRecords": 1,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid"},
      {"name": "employeeName", "type": "string"},
      {"name": "totalAvailableHours", "type": "decimal", "precision": 1},
      {"name": "billableHours", "type": "decimal", "precision": 1},
      {"name": "utilizationRate", "type": "decimal", "precision": 2}
    ],
    "orderBy": {
      "field": "utilizationRate",
      "direction": "DESC"
    },
    "pagination": {
      "required": true
    }
  }
}
```

---

### 2.3 財務報表查詢

#### RPT_QRY_007: 查詢人力成本分析

**API 端點：** `GET /api/v1/reports/finance/labor-cost`

**業務場景描述：**

彙整薪資、勞保、健保、勞退等人力成本。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_007",
  "apiEndpoint": "GET /api/v1/reports/finance/labor-cost",
  "controller": "HR14FinanceQryController",
  "service": "GetLaborCostAnalysisServiceImpl",
  "permission": "report:finance:read",

  "request": {
    "year": 2026
  },

  "expectedQueryFilters": [
    {"field": "year", "operator": "=", "value": 2026}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "exactRecords": 12,
    "requiredFields": [
      {"name": "month", "type": "integer"},
      {"name": "totalSalary", "type": "number"},
      {"name": "laborInsurance", "type": "number"},
      {"name": "healthInsurance", "type": "number"},
      {"name": "pension", "type": "number"},
      {"name": "totalCost", "type": "number"},
      {"name": "avgCostPerEmployee", "type": "integer"}
    ],
    "orderBy": {
      "field": "month",
      "direction": "ASC"
    }
  }
}
```

---

#### RPT_QRY_008: 查詢部門人力成本分析

**API 端點：** `GET /api/v1/reports/finance/labor-cost-by-department`

**業務場景描述：**

比較各部門人力成本、人均成本。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_008",
  "apiEndpoint": "GET /api/v1/reports/finance/labor-cost-by-department",
  "controller": "HR14FinanceQryController",
  "service": "GetLaborCostByDepartmentServiceImpl",
  "permission": "report:finance:read",

  "request": {
    "year": 2026,
    "departmentId": "D001"
  },

  "expectedQueryFilters": [
    {"field": "year", "operator": "=", "value": 2026},
    {"field": "department_id", "operator": "=", "value": "D001"}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.content",
    "minRecords": 1,
    "requiredFields": [
      {"name": "departmentId", "type": "string"},
      {"name": "departmentName", "type": "string"},
      {"name": "totalEmployees", "type": "integer"},
      {"name": "totalCost", "type": "number"},
      {"name": "avgCostPerEmployee", "type": "number"},
      {"name": "costBreakdown", "type": "object"},
      {"name": "costRatio", "type": "decimal", "precision": 2}
    ],
    "orderBy": {
      "field": "totalCost",
      "direction": "DESC"
    },
    "pagination": {
      "required": true
    }
  }
}
```

---

#### RPT_QRY_009: 查詢薪資總表

**API 端點：** `GET /api/v1/reports/finance/payroll-summary`

**業務場景描述：**

彙整當月全體員工薪資發放明細。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_009",
  "apiEndpoint": "GET /api/v1/reports/finance/payroll-summary",
  "controller": "HR14FinanceQryController",
  "service": "GetPayrollSummaryServiceImpl",
  "permission": "report:finance:read",

  "request": {
    "month": "2026-02"
  },

  "expectedQueryFilters": [
    {"field": "month", "operator": "=", "value": "2026-02"}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "exactRecords": 1,
    "requiredFields": [
      {"name": "totalEmployees", "type": "integer"},
      {"name": "totalGrossSalary", "type": "number"},
      {"name": "totalDeductions", "type": "number"},
      {"name": "totalNetSalary", "type": "number"},
      {"name": "employerBurden", "type": "object"},
      {"name": "departmentBreakdown", "type": "array"}
    ]
  }
}
```

---

### 2.4 儀表板查詢

#### RPT_QRY_010: 查詢儀表板列表

**API 端點：** `GET /api/v1/dashboards`

**業務場景描述：**

查看自己的儀表板與公開儀表板列表。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_010",
  "apiEndpoint": "GET /api/v1/dashboards",
  "controller": "HR14DashboardQryController",
  "service": "GetDashboardListServiceImpl",
  "permission": "dashboard:read",

  "request": {
    "includePublic": true
  },

  "expectedQueryFilters": [
    {"field": "is_active", "operator": "=", "value": true}
  ],

  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.content",
    "minRecords": 0,
    "requiredFields": [
      {"name": "id", "type": "uuid"},
      {"name": "name", "type": "string"},
      {"name": "isPublic", "type": "boolean"},
      {"name": "isDefault", "type": "boolean"},
      {"name": "ownerName", "type": "string"},
      {"name": "createdAt", "type": "datetime"}
    ],
    "orderBy": {
      "field": "createdAt",
      "direction": "DESC"
    },
    "pagination": {
      "required": true
    }
  }
}
```

---

#### RPT_QRY_011: 查詢儀表板詳情

**API 端點：** `GET /api/v1/dashboards/{id}`

**業務場景描述：**

查看儀表板的完整配置。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_011",
  "apiEndpoint": "GET /api/v1/dashboards/{id}",
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
    "dataPath": "data",
    "exactRecords": 1,
    "requiredFields": [
      {"name": "id", "type": "uuid"},
      {"name": "name", "type": "string"},
      {"name": "layoutConfig", "type": "object"},
      {"name": "widgetsConfig", "type": "array"},
      {"name": "isPublic", "type": "boolean"},
      {"name": "isDefault", "type": "boolean"},
      {"name": "owner", "type": "object"}
    ]
  }
}
```

---

### 2.5 報表匯出查詢

#### RPT_QRY_012: 下載匯出檔案

**API 端點：** `GET /api/v1/reports/export/{id}/download`

**業務場景描述：**

下載已完成的匯出檔案。

**測試合約：**

```json
{
  "scenarioId": "RPT_QRY_012",
  "apiEndpoint": "GET /api/v1/reports/export/{id}/download",
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
