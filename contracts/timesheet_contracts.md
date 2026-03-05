# HR07 工時管理服務業務合約

> **服務代碼:** HR07
> **服務名稱:** 工時管理服務 (Timesheet Management)
> **版本:** 1.0
> **更新日期:** 2026-02-22

---

## 概述

工時管理服務負責員工週工時回報、工時審核、以及工時統計報表功能。員工在每週填報各專案的工時明細，提交後由 PM 審核，審核通過後計入專案實際工時。

---

## API 端點概覽

### 工時表管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/timesheets/entry` | POST | TSH_CMD_001 | 新增工時條目 | ✅ 已實作 |
| 2 | `PUT /api/v1/timesheets/{id}/entries/{entryId}` | PUT | TSH_CMD_002 | 更新工時條目 | ✅ 已實作 |
| 3 | `DELETE /api/v1/timesheets/{id}/entries/{entryId}` | DELETE | TSH_CMD_003 | 刪除工時條目 | ✅ 已實作 |
| 4 | `POST /api/v1/timesheets/submit` | POST | TSH_CMD_004 | 提交工時表 | ✅ 已實作 |

### 工時審核 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/timesheets/{id}/approve` | POST | TSH_CMD_005 | 核准工時表 | ✅ 已實作 |
| 2 | `PUT /api/v1/timesheets/batch-approve` | PUT | TSH_CMD_006 | 批次核准工時表 | ✅ 已實作 |
| 3 | `POST /api/v1/timesheets/{id}/reject` | POST | TSH_CMD_007 | 駁回工時表 | ✅ 已實作 |
| 4 | `POST /api/v1/timesheets/{id}/lock` | POST | TSH_CMD_008 | 鎖定工時表 | ✅ 已實作 |

### 工時查詢 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/timesheets/my` | GET | TSH_QRY_001 | 查詢我的工時 | ✅ 已實作 |
| 2 | `GET /api/v1/timesheets/approvals` | GET | TSH_QRY_002 | 查詢待簽核列表 | ✅ 已實作 |
| 3 | `GET /api/v1/timesheets/{id}` | GET | TSH_QRY_003 | 查詢工時表詳情 | ✅ 已實作 |

### 工時報表 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/timesheets/summary` | GET | TSH_QRY_004 | 個人工時統計 | ✅ 已實作 |
| 2 | `GET /api/v1/timesheets/project-summary` | GET | TSH_QRY_005 | 專案工時統計 | ✅ 已實作 |
| 3 | `GET /api/v1/timesheets/unreported` | GET | TSH_QRY_006 | 未回報員工列表 | ✅ 已實作 |

**總計：14 個場景（8 個 Command + 6 個 Query）**

**場景分類：**
- **Command 操作：** 8 個（4 工時明細 + 4 審核管理）
- **Query 操作：** 6 個（3 工時查詢 + 3 報表查詢）

---

## 測試資料

### 工時表資料（timesheets）

| timesheet_id | employee_id | period_start_date | period_end_date | total_hours | status | is_locked |
|:---|:---|:---|:---|:---:|:---|:---:|
| ts-uuid-001 | emp-uuid-001 | 2025-11-25 | 2025-12-01 | 40.0 | DRAFT | false |
| ts-uuid-002 | emp-uuid-001 | 2025-12-02 | 2025-12-08 | 32.0 | PENDING | false |
| ts-uuid-003 | emp-uuid-002 | 2025-11-25 | 2025-12-01 | 40.0 | PENDING | false |
| ts-uuid-004 | emp-uuid-003 | 2025-11-25 | 2025-12-01 | 38.5 | APPROVED | true |
| ts-uuid-005 | emp-uuid-002 | 2025-12-02 | 2025-12-08 | 24.0 | REJECTED | false |
| ts-uuid-006 | emp-uuid-004 | 2025-11-25 | 2025-12-01 | 40.0 | LOCKED | true |

### 工時明細資料（timesheet_entries）

| entry_id | timesheet_id | project_id | task_id | work_date | hours | description |
|:---|:---|:---|:---|:---|:---:|:---|
| entry-001 | ts-uuid-001 | prj-uuid-001 | task-001 | 2025-11-25 | 8.0 | 需求分析 |
| entry-002 | ts-uuid-001 | prj-uuid-001 | task-002 | 2025-11-26 | 8.0 | 系統設計 |
| entry-003 | ts-uuid-001 | prj-uuid-002 | null | 2025-11-27 | 8.0 | 程式開發 |
| entry-004 | ts-uuid-001 | prj-uuid-001 | task-001 | 2025-11-28 | 8.0 | 需求修訂 |
| entry-005 | ts-uuid-001 | prj-uuid-002 | null | 2025-11-29 | 8.0 | 測試撰寫 |
| entry-006 | ts-uuid-002 | prj-uuid-001 | task-001 | 2025-12-02 | 8.0 | 需求分析 |
| entry-007 | ts-uuid-002 | prj-uuid-001 | task-002 | 2025-12-03 | 8.0 | 系統設計 |
| entry-008 | ts-uuid-002 | prj-uuid-002 | null | 2025-12-04 | 8.0 | 程式開發 |
| entry-009 | ts-uuid-002 | prj-uuid-002 | null | 2025-12-05 | 8.0 | 測試撰寫 |

---

## 1. Command 操作業務合約

### 1.1 工時明細管理

#### TSH_CMD_001: 新增工時條目

**API 端點：** `POST /api/v1/timesheets/entry`

**業務場景描述：**

員工新增一筆工時記錄。若該週工時表不存在，系統會自動建立。系統驗證工時表未鎖定、不可回報未來日期、單日工時不超過 24 小時。

**測試合約：**

```json
{
  "scenarioId": "TSH_CMD_001",
  "apiEndpoint": "POST /api/v1/timesheets/entry",
  "controller": "HR07TimesheetCmdController",
  "service": "CreateEntryServiceImpl",
  "permission": "authenticated",
  "request": {
    "projectId": "550e8400-e29b-41d4-a716-446655440001",
    "taskId": "550e8400-e29b-41d4-a716-446655440002",
    "workDate": "2025-11-25",
    "hours": 8.0,
    "description": "完成需求分析文件"
  },
  "businessRules": [
    {"rule": "若該週工時表不存在，自動建立（狀態為 DRAFT）"},
    {"rule": "工時表未鎖定才可新增"},
    {"rule": "工時表狀態為 DRAFT 或 REJECTED 才可新增"},
    {"rule": "不可回報未來日期的工時"},
    {"rule": "單日工時不可超過 24 小時"},
    {"rule": "hours 必須大於 0"},
    {"rule": "新增後重新計算工時表總工時"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "timesheet_entries",
      "count": 1,
      "assertions": [
        {"field": "entry_id", "operator": "notNull"},
        {"field": "timesheet_id", "operator": "notNull"},
        {"field": "project_id", "operator": "equals", "value": "550e8400-e29b-41d4-a716-446655440001"},
        {"field": "task_id", "operator": "equals", "value": "550e8400-e29b-41d4-a716-446655440002"},
        {"field": "work_date", "operator": "equals", "value": "2025-11-25"},
        {"field": "hours", "operator": "equals", "value": 8.0},
        {"field": "description", "operator": "equals", "value": "完成需求分析文件"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### TSH_CMD_002: 更新工時條目

**API 端點：** `PUT /api/v1/timesheets/{id}/entries/{entryId}`

**業務場景描述：**

員工修改已填報的工時明細，包括工時數、工作說明等。系統確保更新後單日工時不超過 24 小時。

**測試合約：**

```json
{
  "scenarioId": "TSH_CMD_002",
  "apiEndpoint": "PUT /api/v1/timesheets/{id}/entries/{entryId}",
  "controller": "HR07TimesheetCmdController",
  "service": "UpdateTimesheetEntryServiceImpl",
  "permission": "authenticated",
  "request": {
    "timesheetId": "ts-uuid-001",
    "entryId": "entry-001",
    "taskId": "550e8400-e29b-41d4-a716-446655440002",
    "hours": 6.0,
    "description": "修訂需求分析文件"
  },
  "businessRules": [
    {"rule": "工時表必須存在"},
    {"rule": "工時明細必須存在且屬於該工時表"},
    {"rule": "工時表未鎖定才可修改"},
    {"rule": "工時表狀態為 DRAFT 或 REJECTED 才可修改"},
    {"rule": "更新後單日工時不可超過 24 小時"},
    {"rule": "hours 必須大於 0"},
    {"rule": "更新後重新計算工時表總工時"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "timesheet_entries",
      "count": 1,
      "assertions": [
        {"field": "hours", "operator": "equals", "value": 6.0},
        {"field": "description", "operator": "equals", "value": "修訂需求分析文件"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### TSH_CMD_003: 刪除工時條目

**API 端點：** `DELETE /api/v1/timesheets/{id}/entries/{entryId}`

**業務場景描述：**

員工刪除錯誤填報的工時明細。刪除後系統自動重新計算工時表總工時。

**測試合約：**

```json
{
  "scenarioId": "TSH_CMD_003",
  "apiEndpoint": "DELETE /api/v1/timesheets/{id}/entries/{entryId}",
  "controller": "HR07TimesheetCmdController",
  "service": "DeleteTimesheetEntryServiceImpl",
  "permission": "authenticated",
  "request": {
    "timesheetId": "ts-uuid-001",
    "entryId": "entry-001"
  },
  "businessRules": [
    {"rule": "工時表必須存在"},
    {"rule": "工時明細必須存在且屬於該工時表"},
    {"rule": "工時表未鎖定才可刪除"},
    {"rule": "工時表狀態為 DRAFT 或 REJECTED 才可刪除"},
    {"rule": "刪除後重新計算工時表總工時"}
  ],
  "expectedDataChanges": [
    {
      "action": "DELETE",
      "table": "timesheet_entries",
      "count": 1,
      "assertions": [
        {"field": "entry_id", "operator": "equals", "value": "entry-001"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### TSH_CMD_004: 提交工時表

**API 端點：** `POST /api/v1/timesheets/submit`

**業務場景描述：**

員工完成工時填報後，提交給 PM 審核。工時表至少須有一筆工時記錄，且狀態為 DRAFT 或 REJECTED 才可提交。

**測試合約：**

```json
{
  "scenarioId": "TSH_CMD_004",
  "apiEndpoint": "POST /api/v1/timesheets/submit",
  "controller": "HR07TimesheetCmdController",
  "service": "SubmitTimesheetServiceImpl",
  "permission": "authenticated",
  "request": {
    "timesheetId": "ts-uuid-001"
  },
  "businessRules": [
    {"rule": "工時表必須存在且為本人所有"},
    {"rule": "狀態必須為 DRAFT 或 REJECTED"},
    {"rule": "至少需要一筆工時記錄"},
    {"rule": "更新狀態為 PENDING"},
    {"rule": "記錄提交時間 submittedAt"},
    {"rule": "發布 TimesheetSubmittedEvent"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "timesheets",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "PENDING"},
        {"field": "submitted_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "TimesheetSubmittedEvent",
      "payload": [
        {"field": "timesheetId", "operator": "notNull"},
        {"field": "employeeId", "operator": "notNull"},
        {"field": "totalHours", "operator": "notNull"},
        {"field": "periodStartDate", "operator": "notNull"},
        {"field": "periodEndDate", "operator": "notNull"}
      ]
    }
  ]
}
```

---

### 1.2 工時審核

#### TSH_CMD_005: 核准工時表

**API 端點：** `POST /api/v1/timesheets/{id}/approve`

**業務場景描述：**

PM 審核員工提交的工時表，核准後工時將計入專案實際工時。核准後自動鎖定工時表。

**測試合約：**

```json
{
  "scenarioId": "TSH_CMD_005",
  "apiEndpoint": "POST /api/v1/timesheets/{id}/approve",
  "controller": "HR07TimesheetCmdController",
  "service": "ApproveTimesheetServiceImpl",
  "permission": "timesheet:approve",
  "request": {
    "timesheetId": "ts-uuid-002"
  },
  "businessRules": [
    {"rule": "工時表必須存在"},
    {"rule": "狀態必須為 PENDING"},
    {"rule": "審核者須有 timesheet:approve 權限"},
    {"rule": "更新狀態為 APPROVED"},
    {"rule": "記錄核准者 approvedBy 與核准時間 approvedAt"},
    {"rule": "自動鎖定工時表 isLocked = true"},
    {"rule": "發布 TimesheetApprovedEvent"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "timesheets",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "APPROVED"},
        {"field": "approved_by", "operator": "notNull"},
        {"field": "approved_at", "operator": "notNull"},
        {"field": "is_locked", "operator": "equals", "value": true}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "TimesheetApprovedEvent",
      "payload": [
        {"field": "timesheetId", "operator": "notNull"},
        {"field": "employeeId", "operator": "notNull"},
        {"field": "approverId", "operator": "notNull"},
        {"field": "approvedAt", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### TSH_CMD_006: 批次核准工時表

**API 端點：** `PUT /api/v1/timesheets/batch-approve`

**業務場景描述：**

PM 一次核准多筆工時表，提升審核效率。系統逐一處理每筆工時表，回傳成功與失敗的統計。

**測試合約：**

```json
{
  "scenarioId": "TSH_CMD_006",
  "apiEndpoint": "PUT /api/v1/timesheets/batch-approve",
  "controller": "HR07TimesheetCmdController",
  "service": "BatchApproveTimesheetServiceImpl",
  "permission": "timesheet:approve",
  "request": {
    "timesheetIds": ["ts-uuid-002", "ts-uuid-003"]
  },
  "businessRules": [
    {"rule": "timesheetIds 至少包含一筆"},
    {"rule": "timesheetIds 最多 100 筆"},
    {"rule": "逐一驗證每筆工時表狀態為 PENDING"},
    {"rule": "成功核准的工時表自動鎖定"},
    {"rule": "逐一發布 TimesheetApprovedEvent"},
    {"rule": "回傳成功與失敗的統計"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "timesheets",
      "count": 2,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "APPROVED"},
        {"field": "approved_by", "operator": "notNull"},
        {"field": "approved_at", "operator": "notNull"},
        {"field": "is_locked", "operator": "equals", "value": true}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "TimesheetApprovedEvent",
      "count": 2,
      "payload": [
        {"field": "timesheetId", "operator": "notNull"},
        {"field": "employeeId", "operator": "notNull"},
        {"field": "approverId", "operator": "notNull"}
      ]
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "totalCount", "type": "integer", "notNull": true},
      {"name": "successCount", "type": "integer", "notNull": true},
      {"name": "failedCount", "type": "integer", "notNull": true}
    ]
  }
}
```

---

#### TSH_CMD_007: 駁回工時表

**API 端點：** `POST /api/v1/timesheets/{id}/reject`

**業務場景描述：**

PM 審核發現工時有問題，駁回給員工修改。駁回時必須填寫駁回原因。

**測試合約：**

```json
{
  "scenarioId": "TSH_CMD_007",
  "apiEndpoint": "POST /api/v1/timesheets/{id}/reject",
  "controller": "HR07TimesheetCmdController",
  "service": "RejectTimesheetServiceImpl",
  "permission": "timesheet:approve",
  "request": {
    "timesheetId": "ts-uuid-003",
    "reason": "工時與差勤記錄不符，請確認11/26是否有請假"
  },
  "businessRules": [
    {"rule": "工時表必須存在"},
    {"rule": "狀態必須為 PENDING"},
    {"rule": "駁回原因不可為空"},
    {"rule": "更新狀態為 REJECTED"},
    {"rule": "記錄駁回者與駁回原因"},
    {"rule": "isLocked 設為 false（允許員工修改）"},
    {"rule": "發布 TimesheetRejectedEvent"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "timesheets",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "REJECTED"},
        {"field": "approved_by", "operator": "notNull"},
        {"field": "rejection_reason", "operator": "equals", "value": "工時與差勤記錄不符，請確認11/26是否有請假"},
        {"field": "is_locked", "operator": "equals", "value": false}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "TimesheetRejectedEvent",
      "payload": [
        {"field": "timesheetId", "operator": "notNull"},
        {"field": "employeeId", "operator": "notNull"},
        {"field": "reason", "operator": "equals", "value": "工時與差勤記錄不符，請確認11/26是否有請假"}
      ]
    }
  ]
}
```

---

#### TSH_CMD_008: 鎖定工時表

**API 端點：** `POST /api/v1/timesheets/{id}/lock`

**業務場景描述：**

系統或管理員鎖定工時表，通常在薪資結算後執行。鎖定後工時表不可修改。

**測試合約：**

```json
{
  "scenarioId": "TSH_CMD_008",
  "apiEndpoint": "POST /api/v1/timesheets/{id}/lock",
  "controller": "HR07TimesheetCmdController",
  "service": "LockTimesheetServiceImpl",
  "permission": "timesheet:lock",
  "request": {
    "timesheetId": "ts-uuid-004"
  },
  "businessRules": [
    {"rule": "工時表必須存在"},
    {"rule": "設定 isLocked = true"},
    {"rule": "鎖定後工時表不可再修改"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "timesheets",
      "count": 1,
      "assertions": [
        {"field": "is_locked", "operator": "equals", "value": true}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

## 2. Query 操作業務合約

### 2.1 工時查詢

#### TSH_QRY_001: 查詢我的工時

**API 端點：** `GET /api/v1/timesheets/my`

**業務場景描述：**

員工查詢自己指定週次的工時表及所有明細。若該週無工時表則回傳空資料，前端可顯示建立按鈕。

**測試合約：**

```json
{
  "scenarioId": "TSH_QRY_001",
  "apiEndpoint": "GET /api/v1/timesheets/my",
  "controller": "HR07TimesheetQryController",
  "service": "GetMyTimesheetServiceImpl",
  "permission": "authenticated",
  "request": {
    "week": "2025-W48"
  },
  "expectedQueryFilters": [
    {"field": "employeeId", "operator": "=", "value": "{currentUserEmployeeId}"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

> **TODO:** `requiredFields` 應對齊 `GetMyTimesheetResponse` 的實際結構（items 陣列內的 TimesheetSummaryDto），而非單筆工時表詳情。待前後端介面穩定後補充。

---

#### TSH_QRY_002: 查詢待簽核列表

**API 端點：** `GET /api/v1/timesheets/approvals`

**業務場景描述：**

PM 查看待審核的工時表列表。僅顯示狀態為 PENDING 的工時表，支援分頁與篩選。

**測試合約：**

```json
{
  "scenarioId": "TSH_QRY_002",
  "apiEndpoint": "GET /api/v1/timesheets/approvals",
  "controller": "HR07TimesheetQryController",
  "service": "GetPendingApprovalsServiceImpl",
  "permission": "timesheet:approve",
  "request": {
    "page": 1,
    "size": 10
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "PENDING"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.content",
    "requiredFields": [
      {"name": "timesheetId", "type": "uuid", "notNull": true},
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "periodStartDate", "type": "date", "notNull": true},
      {"name": "periodEndDate", "type": "date", "notNull": true},
      {"name": "totalHours", "type": "decimal", "notNull": true},
      {"name": "submittedAt", "type": "datetime", "notNull": true}
    ],
    "pagination": {
      "required": true
    },
    "assertions": [
      {"field": "status", "operator": "equals", "value": "PENDING"}
    ]
  }
}
```

---

#### TSH_QRY_003: 查詢工時表詳情

**API 端點：** `GET /api/v1/timesheets/{id}`

**業務場景描述：**

查詢指定工時表的完整資訊，包含所有工時明細。本人或有讀取權限的 PM 可查詢。

**測試合約：**

```json
{
  "scenarioId": "TSH_QRY_003",
  "apiEndpoint": "GET /api/v1/timesheets/{id}",
  "controller": "HR07TimesheetQryController",
  "service": "GetTimesheetDetailServiceImpl",
  "permission": "authenticated",
  "request": {
    "timesheetId": "ts-uuid-001"
  },
  "expectedQueryFilters": [
    {"field": "timesheet_id", "operator": "=", "value": "ts-uuid-001"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "timesheetId", "type": "uuid", "notNull": true},
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "periodStartDate", "type": "date", "notNull": true},
      {"name": "periodEndDate", "type": "date", "notNull": true},
      {"name": "totalHours", "type": "decimal", "notNull": true},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "entries", "type": "array", "notNull": true}
    ]
  }
}
```

---

### 2.2 報表查詢

#### TSH_QRY_004: 個人工時統計

**API 端點：** `GET /api/v1/timesheets/summary`

**業務場景描述：**

查詢指定期間的工時統計摘要，包含總工時、專案工時分佈、平均日工時等。僅統計已核准的工時。

**測試合約：**

```json
{
  "scenarioId": "TSH_QRY_004",
  "apiEndpoint": "GET /api/v1/timesheets/summary",
  "controller": "HR07TimesheetReportQryController",
  "service": "GetTimesheetSummaryServiceImpl",
  "permission": "timesheet:report:read",
  "request": {
    "startDate": "2025-11-01",
    "endDate": "2025-11-30"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "APPROVED"},
    {"field": "period_start_date", "operator": ">=", "value": "2025-11-01"},
    {"field": "period_end_date", "operator": "<=", "value": "2025-11-30"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "period", "type": "object", "notNull": true},
      {"name": "summary", "type": "object", "notNull": true}
    ]
  }
}
```

---

#### TSH_QRY_005: 專案工時統計

**API 端點：** `GET /api/v1/timesheets/project-summary`

**業務場景描述：**

依專案維度統計工時，用於成本分析。僅統計已核准的工時，可整合專案服務取得預算消耗率。

**測試合約：**

```json
{
  "scenarioId": "TSH_QRY_005",
  "apiEndpoint": "GET /api/v1/timesheets/project-summary",
  "controller": "HR07TimesheetReportQryController",
  "service": "GetProjectTimesheetSummaryServiceImpl",
  "permission": "timesheet:report:read",
  "request": {
    "startDate": "2025-11-01",
    "endDate": "2025-11-30"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "APPROVED"},
    {"field": "period_start_date", "operator": ">=", "value": "2025-11-01"},
    {"field": "period_end_date", "operator": "<=", "value": "2025-11-30"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "period", "type": "object", "notNull": true},
      {"name": "projects", "type": "array", "notNull": true}
    ]
  }
}
```

---

#### TSH_QRY_006: 未回報員工列表

**API 端點：** `GET /api/v1/timesheets/unreported`

**業務場景描述：**

查詢指定週次尚未回報工時的員工。篩選對象為需回報工時的在職員工（不含離職、留停），尚未建立工時表或狀態為 DRAFT 的算未回報。

**測試合約：**

```json
{
  "scenarioId": "TSH_QRY_006",
  "apiEndpoint": "GET /api/v1/timesheets/unreported",
  "controller": "HR07TimesheetReportQryController",
  "service": "GetUnreportedEmployeesServiceImpl",
  "permission": "timesheet:report:read",
  "request": {
    "week": "2025-W48"
  },
  "expectedQueryFilters": [
    {"field": "period_start_date", "operator": "=", "value": "2025-11-24"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "week", "type": "string", "notNull": true},
      {"name": "periodStartDate", "type": "date", "notNull": true},
      {"name": "periodEndDate", "type": "date", "notNull": true},
      {"name": "unreportedCount", "type": "integer", "notNull": true},
      {"name": "employees", "type": "array", "notNull": true}
    ]
  }
}
```

---

## 附錄 A：工時狀態流轉

```
                 ┌──────────────────┐
                 │                  │
                 ▼                  │
┌────────┐    ┌────────┐    ┌──────────┐    ┌────────┐
│ DRAFT  │───▶│PENDING │───▶│ APPROVED │───▶│ LOCKED │
└────────┘    └────────┘    └──────────┘    └────────┘
     ▲              │
     │              │
     │        ┌─────┴─────┐
     │        ▼           │
     │   ┌────────┐       │
     └───│REJECTED│───────┘
         └────────┘
```

> **注意：** 實作中使用 `PENDING` 狀態（API 規格書中為 `SUBMITTED`），兩者語義相同。

| 狀態 | 說明 | 可修改 |
|:---|:---|:---:|
| DRAFT | 草稿 | ✅ |
| PENDING | 已提交（待審核） | ❌ |
| APPROVED | 已核准 | ❌ |
| REJECTED | 已駁回 | ✅ |
| LOCKED | 已鎖定 | ❌ |

---

## 附錄 B：領域事件總覽

| 事件名稱 | Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| TimesheetSubmittedEvent | `timesheet.submitted` | 提交工時表 | Workflow, Notification |
| TimesheetApprovedEvent | `timesheet.approved` | 核准工時表 | Project, Payroll, Notification |
| TimesheetRejectedEvent | `timesheet.rejected` | 駁回工時表 | Notification |

---

**文件建立日期:** 2026-02-22
**版本:** 1.0
