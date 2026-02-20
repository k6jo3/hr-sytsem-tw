# HR06 專案管理服務合約

> **服務代碼:** HR06
> **服務名稱:** 專案管理服務
> **版本:** 2.0
> **更新日期:** 2026-02-20

---

## 概述

專案管理服務負責客戶管理、專案建立與追蹤、WBS 工項管理、成員管理、以及成本分析功能。

---

## API 端點概覽

### 客戶管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/customers` | POST | PRJ_CMD_C001 | 建立客戶 | ❌ 未實作 |
| 2 | `PUT /api/v1/customers/{id}` | PUT | PRJ_CMD_C002 | 更新客戶 | ❌ 未實作 |
| 3 | `GET /api/v1/customers` | GET | PRJ_C001~C005 | 查詢客戶列表 | ✅ 已實作 |

### 專案管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/projects` | POST | PRJ_CMD_P001 | 建立專案 | ❌ 未實作 |
| 2 | `PUT /api/v1/projects/{id}` | PUT | PRJ_CMD_P002 | 更新專案 | ❌ 未實作 |
| 3 | `PUT /api/v1/projects/{id}/start` | PUT | PRJ_CMD_P003 | 開始專案 | ❌ 未實作 |
| 4 | `PUT /api/v1/projects/{id}/complete` | PUT | PRJ_CMD_P004 | 結案 | ❌ 未實作 |
| 5 | `PUT /api/v1/projects/{id}/hold` | PUT | PRJ_CMD_P005 | 暫停專案 | ❌ 未實作 |
| 6 | `GET /api/v1/projects` | GET | PRJ_P001~P010 | 查詢專案列表 | ✅ 已實作 |

### 專案成員 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/projects/{id}/members` | POST | PRJ_CMD_M001 | 新增成員 | ❌ 未實作 |
| 2 | `DELETE /api/v1/projects/{id}/members/{memberId}` | DELETE | PRJ_CMD_M002 | 移除成員 | ❌ 未實作 |

### 工項管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/projects/{id}/tasks` | POST | PRJ_CMD_T001 | 建立工項 | ❌ 未實作 |
| 2 | `PUT /api/v1/tasks/{id}` | PUT | PRJ_CMD_T002 | 更新工項 | ❌ 未實作 |
| 3 | `PUT /api/v1/tasks/{id}/progress` | PUT | PRJ_CMD_T003 | 更新進度 | ❌ 未實作 |

**總計：41 個場景（29 個 Query + 12 個 Command）**

---

## 1. Command 操作業務合約

### 1.1 客戶管理

#### PRJ_CMD_C001: 建立客戶

**API 端點：** `POST /api/v1/customers`

**業務場景描述：**

HR 或專案管理人員建立客戶基本資料，供專案關聯使用。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_C001",
  "apiEndpoint": "POST /api/v1/customers",
  "controller": "HR06CustomerCmdController",
  "service": "CreateCustomerServiceImpl",
  "permission": "customer:manage",
  "request": {
    "customerCode": "CUST-001",
    "customerName": "XX銀行股份有限公司",
    "taxId": "12345678",
    "industry": "金融業",
    "address": "台北市信義區信義路五段7號",
    "phoneNumber": "02-12345678",
    "email": "contact@xxbank.com"
  },
  "businessRules": [
    {"rule": "customerCode 在系統內必須唯一"},
    {"rule": "taxId 若有填寫須為 8 碼數字"},
    {"rule": "新客戶預設 status = ACTIVE"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "customers",
      "count": 1,
      "assertions": [
        {"field": "customer_id", "operator": "notNull"},
        {"field": "customer_code", "operator": "equals", "value": "CUST-001"},
        {"field": "customer_name", "operator": "equals", "value": "XX銀行股份有限公司"},
        {"field": "tax_id", "operator": "equals", "value": "12345678"},
        {"field": "industry", "operator": "equals", "value": "金融業"},
        {"field": "status", "operator": "equals", "value": "ACTIVE"},
        {"field": "is_deleted", "operator": "equals", "value": false}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### PRJ_CMD_C002: 更新客戶

**API 端點：** `PUT /api/v1/customers/{id}`

**業務場景描述：**

HR 或專案管理人員修改客戶基本資料、聯絡人、狀態等。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_C002",
  "apiEndpoint": "PUT /api/v1/customers/{id}",
  "controller": "HR06CustomerCmdController",
  "service": "UpdateCustomerServiceImpl",
  "permission": "customer:manage",
  "request": {
    "customerName": "XX銀行股份有限公司 (更新)",
    "industry": "金融業",
    "status": "ACTIVE"
  },
  "businessRules": [
    {"rule": "客戶必須存在"},
    {"rule": "customerCode 若有變更需唯一"},
    {"rule": "email 若有填寫須符合 Email 格式"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "customers",
      "count": 1,
      "assertions": [
        {"field": "customer_name", "operator": "equals", "value": "XX銀行股份有限公司 (更新)"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

### 1.2 專案管理

#### PRJ_CMD_P001: 建立專案

**API 端點：** `POST /api/v1/projects`

**業務場景描述：**

專案經理建立新專案，設定預算、時程、指派 PM。專案建立後會發布 `ProjectCreated` 事件。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_P001",
  "apiEndpoint": "POST /api/v1/projects",
  "controller": "HR06ProjectCmdController",
  "service": "CreateProjectServiceImpl",
  "permission": "project:manage",
  "request": {
    "projectCode": "PRJ-2025-001",
    "projectName": "XX銀行核心系統開發",
    "customerId": "cust-001",
    "projectType": "DEVELOPMENT",
    "plannedStartDate": "2025-01-01",
    "plannedEndDate": "2025-12-31",
    "budgetType": "FIXED_PRICE",
    "budgetAmount": 10000000,
    "budgetHours": 2500,
    "projectManager": "emp-001"
  },
  "businessRules": [
    {"rule": "projectCode 在系統內必須唯一"},
    {"rule": "customerId 必須存在"},
    {"rule": "plannedEndDate >= plannedStartDate"},
    {"rule": "projectManager 必須存在"},
    {"rule": "新專案預設 status = PLANNING"},
    {"rule": "自動將 PM 加入專案成員"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "projects",
      "count": 1,
      "assertions": [
        {"field": "project_id", "operator": "notNull"},
        {"field": "project_code", "operator": "equals", "value": "PRJ-2025-001"},
        {"field": "project_name", "operator": "equals", "value": "XX銀行核心系統開發"},
        {"field": "customer_id", "operator": "equals", "value": "cust-001"},
        {"field": "project_type", "operator": "equals", "value": "DEVELOPMENT"},
        {"field": "budget_type", "operator": "equals", "value": "FIXED_PRICE"},
        {"field": "budget_amount", "operator": "equals", "value": 10000000},
        {"field": "status", "operator": "equals", "value": "PLANNING"},
        {"field": "is_deleted", "operator": "equals", "value": false}
      ]
    },
    {
      "action": "INSERT",
      "table": "project_members",
      "count": 1,
      "assertions": [
        {"field": "employee_id", "operator": "equals", "value": "emp-001"},
        {"field": "role", "operator": "equals", "value": "PM"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "ProjectCreatedEvent",
      "payload": [
        {"field": "projectId", "operator": "notNull"},
        {"field": "projectCode", "operator": "equals", "value": "PRJ-2025-001"},
        {"field": "projectName", "operator": "equals", "value": "XX銀行核心系統開發"},
        {"field": "customerId", "operator": "equals", "value": "cust-001"},
        {"field": "projectManager", "operator": "equals", "value": "emp-001"}
      ]
    }
  ]
}
```

---

#### PRJ_CMD_P002: 更新專案

**API 端點：** `PUT /api/v1/projects/{id}`

**業務場景描述：**

專案經理修改專案基本資訊、預算、時程等。已結案或取消的專案不可編輯。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_P002",
  "apiEndpoint": "PUT /api/v1/projects/{id}",
  "controller": "HR06ProjectCmdController",
  "service": "UpdateProjectServiceImpl",
  "permission": "project:manage",
  "request": {
    "projectName": "XX銀行核心系統開發 (Phase 1)",
    "plannedEndDate": "2026-03-31",
    "budgetAmount": 12000000,
    "budgetHours": 3000
  },
  "businessRules": [
    {"rule": "專案必須存在"},
    {"rule": "專案狀態非 COMPLETED 或 CANCELLED"},
    {"rule": "plannedEndDate >= plannedStartDate"},
    {"rule": "projectManager 若有變更必須存在"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "projects",
      "count": 1,
      "assertions": [
        {"field": "project_name", "operator": "equals", "value": "XX銀行核心系統開發 (Phase 1)"},
        {"field": "budget_amount", "operator": "equals", "value": 12000000},
        {"field": "budget_hours", "operator": "equals", "value": 3000},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### PRJ_CMD_P003: 開始專案

**API 端點：** `PUT /api/v1/projects/{id}/start`

**業務場景描述：**

專案正式啟動，開始追蹤成本與工時。狀態從 PLANNING 轉為 IN_PROGRESS。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_P003",
  "apiEndpoint": "PUT /api/v1/projects/{id}/start",
  "controller": "HR06ProjectCmdController",
  "service": "StartProjectServiceImpl",
  "permission": "project:manage",
  "request": {},
  "businessRules": [
    {"rule": "專案必須存在"},
    {"rule": "專案狀態必須為 PLANNING"},
    {"rule": "設定 actual_start_date = 今日"},
    {"rule": "更新狀態為 IN_PROGRESS"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "projects",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "IN_PROGRESS"},
        {"field": "actual_start_date", "operator": "notNull"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### PRJ_CMD_P004: 結案

**API 端點：** `PUT /api/v1/projects/{id}/complete`

**業務場景描述：**

專案結案，鎖定成本資料。結案後發布 `ProjectCompleted` 事件通知 Reporting 服務。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_P004",
  "apiEndpoint": "PUT /api/v1/projects/{id}/complete",
  "controller": "HR06ProjectCmdController",
  "service": "CompleteProjectServiceImpl",
  "permission": "project:manage",
  "request": {},
  "businessRules": [
    {"rule": "專案必須存在"},
    {"rule": "專案狀態必須為 IN_PROGRESS"},
    {"rule": "不可有未完成的工項"},
    {"rule": "設定 actual_end_date = 今日"},
    {"rule": "更新狀態為 COMPLETED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "projects",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "COMPLETED"},
        {"field": "actual_end_date", "operator": "notNull"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "ProjectCompletedEvent",
      "payload": [
        {"field": "projectId", "operator": "notNull"},
        {"field": "projectCode", "operator": "notNull"},
        {"field": "actualEndDate", "operator": "notNull"},
        {"field": "totalCost", "operator": "notNull"},
        {"field": "budgetUtilization", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### PRJ_CMD_P005: 暫停專案

**API 端點：** `PUT /api/v1/projects/{id}/hold`

**業務場景描述：**

專案經理因客戶要求或預算問題暫停專案執行。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_P005",
  "apiEndpoint": "PUT /api/v1/projects/{id}/hold",
  "controller": "HR06ProjectCmdController",
  "service": "HoldProjectServiceImpl",
  "permission": "project:manage",
  "request": {
    "reason": "客戶要求暫停，等待預算審批"
  },
  "businessRules": [
    {"rule": "專案必須存在"},
    {"rule": "專案狀態必須為 IN_PROGRESS"},
    {"rule": "reason 為必填"},
    {"rule": "更新狀態為 ON_HOLD"},
    {"rule": "記錄暫停原因與時間"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "projects",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "ON_HOLD"},
        {"field": "hold_reason", "operator": "equals", "value": "客戶要求暫停，等待預算審批"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

### 1.3 專案成員管理

#### PRJ_CMD_M001: 新增成員

**API 端點：** `POST /api/v1/projects/{id}/members`

**業務場景描述：**

專案經理將員工加入專案團隊。新增成員後發布 `ProjectMemberAdded` 事件通知 Timesheet 服務。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_M001",
  "apiEndpoint": "POST /api/v1/projects/{id}/members",
  "controller": "HR06MemberCmdController",
  "service": "AddProjectMemberServiceImpl",
  "permission": "project:member:manage",
  "request": {
    "employeeId": "emp-003",
    "role": "Developer",
    "allocatedHours": 800,
    "hourlyRate": 800,
    "joinDate": "2025-02-01"
  },
  "businessRules": [
    {"rule": "專案必須存在"},
    {"rule": "employeeId 必須存在"},
    {"rule": "該員工不可已在此專案中"},
    {"rule": "joinDate 為必填"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "project_members",
      "count": 1,
      "assertions": [
        {"field": "member_id", "operator": "notNull"},
        {"field": "project_id", "operator": "notNull"},
        {"field": "employee_id", "operator": "equals", "value": "emp-003"},
        {"field": "role", "operator": "equals", "value": "Developer"},
        {"field": "allocated_hours", "operator": "equals", "value": 800},
        {"field": "status", "operator": "equals", "value": "ACTIVE"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "ProjectMemberAddedEvent",
      "payload": [
        {"field": "projectId", "operator": "notNull"},
        {"field": "memberId", "operator": "notNull"},
        {"field": "employeeId", "operator": "equals", "value": "emp-003"},
        {"field": "role", "operator": "equals", "value": "Developer"}
      ]
    }
  ]
}
```

---

#### PRJ_CMD_M002: 移除成員

**API 端點：** `DELETE /api/v1/projects/{id}/members/{memberId}`

**業務場景描述：**

專案經理將成員從專案團隊中移除。移除後發布 `ProjectMemberRemoved` 事件通知 Timesheet 服務。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_M002",
  "apiEndpoint": "DELETE /api/v1/projects/{id}/members/{memberId}",
  "controller": "HR06MemberCmdController",
  "service": "RemoveProjectMemberServiceImpl",
  "permission": "project:member:manage",
  "request": {},
  "businessRules": [
    {"rule": "專案必須存在"},
    {"rule": "成員必須存在於該專案中"},
    {"rule": "專案經理不可自行移除"},
    {"rule": "成員不可有未結算的工時"},
    {"rule": "設定成員離開日期"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "project_members",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "REMOVED"},
        {"field": "leave_date", "operator": "notNull"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "ProjectMemberRemovedEvent",
      "payload": [
        {"field": "projectId", "operator": "notNull"},
        {"field": "memberId", "operator": "notNull"},
        {"field": "employeeId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

### 1.4 工項管理

#### PRJ_CMD_T001: 建立工項

**API 端點：** `POST /api/v1/projects/{id}/tasks`

**業務場景描述：**

專案經理建立 WBS 工項，支援 5 層級結構。若指派負責人則發布 `TaskAssigned` 事件。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_T001",
  "apiEndpoint": "POST /api/v1/projects/{id}/tasks",
  "controller": "HR06TaskCmdController",
  "service": "CreateTaskServiceImpl",
  "permission": "project:task:manage",
  "request": {
    "parentTaskId": null,
    "taskCode": "1",
    "taskName": "需求分析",
    "plannedStartDate": "2025-01-15",
    "plannedEndDate": "2025-02-28",
    "estimatedHours": 200,
    "assigneeId": "emp-002"
  },
  "businessRules": [
    {"rule": "專案必須存在"},
    {"rule": "taskCode 在專案內必須唯一"},
    {"rule": "parentTaskId 若指定則必須存在"},
    {"rule": "WBS 層級最多 5 層"},
    {"rule": "新工項預設 status = NOT_STARTED"},
    {"rule": "assigneeId 若指定必須為專案成員"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "tasks",
      "count": 1,
      "assertions": [
        {"field": "task_id", "operator": "notNull"},
        {"field": "project_id", "operator": "notNull"},
        {"field": "task_code", "operator": "equals", "value": "1"},
        {"field": "task_name", "operator": "equals", "value": "需求分析"},
        {"field": "level", "operator": "equals", "value": 1},
        {"field": "status", "operator": "equals", "value": "NOT_STARTED"},
        {"field": "estimated_hours", "operator": "equals", "value": 200},
        {"field": "is_deleted", "operator": "equals", "value": false}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "TaskAssignedEvent",
      "payload": [
        {"field": "taskId", "operator": "notNull"},
        {"field": "projectId", "operator": "notNull"},
        {"field": "assigneeId", "operator": "equals", "value": "emp-002"},
        {"field": "taskName", "operator": "equals", "value": "需求分析"}
      ]
    }
  ]
}
```

---

#### PRJ_CMD_T002: 更新工項

**API 端點：** `PUT /api/v1/tasks/{id}`

**業務場景描述：**

專案經理修改工項名稱、說明、時程、工時、負責人等。若負責人變更則發布 `TaskAssigned` 事件。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_T002",
  "apiEndpoint": "PUT /api/v1/tasks/{id}",
  "controller": "HR06TaskCmdController",
  "service": "UpdateTaskServiceImpl",
  "permission": "project:task:manage",
  "request": {
    "taskName": "需求分析 (更新)",
    "plannedEndDate": "2025-03-15",
    "estimatedHours": 250,
    "assigneeId": "emp-003"
  },
  "businessRules": [
    {"rule": "工項必須存在"},
    {"rule": "工項所屬專案狀態非 COMPLETED 或 CANCELLED"},
    {"rule": "plannedEndDate >= plannedStartDate"},
    {"rule": "assigneeId 若指定必須為專案成員"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "tasks",
      "count": 1,
      "assertions": [
        {"field": "task_name", "operator": "equals", "value": "需求分析 (更新)"},
        {"field": "estimated_hours", "operator": "equals", "value": 250},
        {"field": "assignee_id", "operator": "equals", "value": "emp-003"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "TaskAssignedEvent",
      "payload": [
        {"field": "taskId", "operator": "notNull"},
        {"field": "assigneeId", "operator": "equals", "value": "emp-003"}
      ]
    }
  ]
}
```

---

#### PRJ_CMD_T003: 更新進度

**API 端點：** `PUT /api/v1/tasks/{id}/progress`

**業務場景描述：**

負責人或專案經理更新工項完成進度。進度為 100% 時自動設定狀態為 COMPLETED 並發布 `TaskCompleted` 事件。

**測試合約：**

```json
{
  "scenarioId": "PRJ_CMD_T003",
  "apiEndpoint": "PUT /api/v1/tasks/{id}/progress",
  "controller": "HR06TaskCmdController",
  "service": "UpdateTaskProgressServiceImpl",
  "permission": "project:task:manage",
  "request": {
    "progress": 100
  },
  "businessRules": [
    {"rule": "工項必須存在"},
    {"rule": "progress 必須在 0-100 之間"},
    {"rule": "工項所屬專案狀態非 COMPLETED 或 CANCELLED"},
    {"rule": "父工項進度由子工項計算，不可直接修改"},
    {"rule": "進度 > 0 時自動更新狀態為 IN_PROGRESS"},
    {"rule": "進度 = 100 時自動更新狀態為 COMPLETED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "tasks",
      "count": 1,
      "assertions": [
        {"field": "progress", "operator": "equals", "value": 100},
        {"field": "status", "operator": "equals", "value": "COMPLETED"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "TaskCompletedEvent",
      "payload": [
        {"field": "taskId", "operator": "notNull"},
        {"field": "projectId", "operator": "notNull"},
        {"field": "taskName", "operator": "notNull"}
      ]
    }
  ]
}
```

---

## 2. Query 操作業務合約

### 2.1 專案查詢合約 (Project Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_P001 | 查詢進行中專案 | `status = IN_PROGRESS`, `is_deleted = 0` |
| PRJ_P002 | 查詢已完成專案 | `status = COMPLETED`, `is_deleted = 0` |
| PRJ_P003 | 依客戶查詢專案 | `customer_id = C001`, `is_deleted = 0` |
| PRJ_P004 | 依 PM 查詢專案 | `pm_id = E001`, `is_deleted = 0` |
| PRJ_P005 | 依名稱模糊查詢 | `(name LIKE '系統' OR code LIKE '系統')`, `is_deleted = 0` |
| PRJ_P006 | 查詢延遲專案 | `is_delayed = 1`, `is_deleted = 0` |
| PRJ_P007 | 員工查詢參與專案 | `team_members.employee_id = '{currentUserId}'`, `is_deleted = 0` |
| PRJ_P008 | 依部門查詢專案 | `department_id = D001`, `is_deleted = 0` |
| PRJ_P009 | 查詢預算超支專案 | `actual_cost > 'budget'`, `is_deleted = 0` |
| PRJ_P010 | 依日期範圍查詢 | `start_date >= '2025-01-01'`, `is_deleted = 0` |

### 2.2 客戶查詢合約 (Customer Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_C001 | 查詢有效客戶 | `status = ACTIVE`, `is_deleted = 0` |
| PRJ_C002 | 依關鍵字模糊查詢 | `(customer_name LIKE '科技' OR customer_code LIKE '科技' OR tax_id LIKE '科技')`, `is_deleted = 0` |
| PRJ_C003 | 依產業類型查詢 | `industry = IT`, `is_deleted = 0` |
| PRJ_C004 | 查詢有專案的客戶 | `project_count > 0`, `is_deleted = 0` |
| PRJ_C005 | 依負責業務查詢 | `sales_rep_id = E001`, `is_deleted = 0` |

### 2.3 WBS 查詢合約 (WBS Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_W001 | 查詢專案 WBS | `project_id = P001`, `is_deleted = 0` |
| PRJ_W002 | 查詢頂層工作包 | `project_id = P001`, `parent_id IS NULL`, `is_deleted = 0` |
| PRJ_W003 | 查詢子工作包 | `parent_id = W001`, `is_deleted = 0` |
| PRJ_W004 | 查詢進行中工作包 | `status = IN_PROGRESS`, `is_deleted = 0` |
| PRJ_W005 | 查詢延遲工作包 | `is_delayed = 1`, `is_deleted = 0` |
| PRJ_W006 | 依負責人查詢 | `owner_id = E001`, `is_deleted = 0` |

### 2.4 專案成員查詢合約 (Project Member Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_M001 | 查詢專案成員 | `project_id = P001`, `is_deleted = 0` |
| PRJ_M002 | 依角色查詢成員 | `role = DEVELOPER`, `is_deleted = 0` |
| PRJ_M003 | 查詢有效成員 | `status = ACTIVE`, `is_deleted = 0` |
| PRJ_M004 | 查詢員工參與的專案 | `employee_id = {currentUserId}`, `is_deleted = 0` |

### 2.5 專案成本查詢合約 (Project Cost Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_T001 | 查詢專案成本 | `project_id = P001`, `is_deleted = 0` |
| PRJ_T002 | 依成本類型查詢 | `cost_type = LABOR`, `is_deleted = 0` |
| PRJ_T003 | 依月份查詢成本 | `year_month = 2025-01`, `is_deleted = 0` |
| PRJ_T004 | 查詢超預算項目 | `actual_amount > 'budget_amount'`, `is_deleted = 0` |
