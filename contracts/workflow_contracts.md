# HR11 簽核流程服務業務合約

> **服務代碼:** HR11
> **服務名稱:** 簽核流程服務 (Workflow Service)
> **版本:** 1.0
> **更新日期:** 2026-02-24

---

## 概述

簽核流程服務負責審核流程定義、啟動、任務管理、代理人設定等功能。
支援完整的審核流程：流程定義 → 啟動流程 → 任務指派 → 審核操作（核准/駁回/轉交）→ 流程完成。

---

## API 端點概覽

### 流程定義管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/workflows/definitions` | POST | WFL_CMD_DEF001 | 建立流程定義 | ✅ 已實作 |
| 2 | `PUT /api/v1/workflows/definitions/{id}/publish` | PUT | WFL_CMD_DEF002 | 發布流程定義 | ✅ 已實作 |

### 流程實例管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/workflows/start` | POST | WFL_CMD_001 | 啟動審核流程 | ✅ 已實作 |
| 2 | `GET /api/v1/workflows/my/applications` | GET | WFL_Q002 | 查詢我的申請 | ✅ 已實作 |

### 審核任務管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/workflows/pending-tasks` | GET | WFL_Q001 | 查詢待辦任務 | ✅ 已實作 |
| 2 | `POST /api/v1/workflows/approve` | POST | WFL_CMD_002 | 核准任務 | ✅ 已實作 |
| 3 | `POST /api/v1/workflows/reject` | POST | WFL_CMD_003 | 駁回任務 | ✅ 已實作 |

### 代理人管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/workflows/delegations` | POST | WFL_CMD_004 | 建立代理人設定 | ✅ 已實作 |
| 2 | `GET /api/v1/workflows/delegations` | GET | - | 查詢代理人設定 | ✅ 已實作 |

**總計：9 個主要 API 端點**

**場景分類：**
- **Query 操作：** 2 個（待辦任務 + 我的申請）
- **Command 操作：** 5 個（啟動流程 + 核准 + 駁回 + 建立代理人 + 建立定義）

---

## 1. Query 操作業務合約

### 1.0 Query 場景快速索引（合約驗證用表格）

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| WFL_Q001 | 審核人查詢自己的待辦任務 | MANAGER | `{}` | `assignee_id = '{currentUserEmployeeNumber}'`, `status = 'PENDING'` |
| WFL_Q002 | 員工查詢自己的申請紀錄 | EMPLOYEE | `{}` | `applicantId = '{currentUserId}'` |

---

### 1.1 待辦任務查詢

#### WFL_Q001: 審核人查詢自己的待辦任務

**API 端點：** `GET /api/v1/workflows/pending-tasks`

**業務場景描述：**

審核人（主管或指定審核人）登入後查看需要自己處理的待辦審核任務。
系統依當前登入使用者的員工編號（employeeNumber）自動過濾，只顯示指派給自己且狀態為 PENDING 的任務。
此機制確保不同審核人無法查看彼此的待辦清單。

**測試合約：**

```json
{
  "scenarioId": "WFL_Q001",
  "apiEndpoint": "GET /api/v1/workflows/pending-tasks",
  "controller": "HR11WorkflowQryController",
  "service": "GetPendingTasksServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {},
  "expectedQueryFilters": [
    {"field": "assignee_id", "operator": "=", "value": "{currentUserEmployeeNumber}"},
    {"field": "status", "operator": "=", "value": "PENDING"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.content",
    "minRecords": 0,
    "requiredFields": [
      {"name": "taskId", "type": "string", "notNull": false}
    ]
  }
}
```

---

### 1.2 我的申請查詢

#### WFL_Q002: 員工查詢自己的申請紀錄

**API 端點：** `GET /api/v1/workflows/my/applications`

**業務場景描述：**

員工查詢自己提交過的所有申請單及其審核進度（請假、加班、採購等）。
系統自動將當前登入使用者的 userId 帶入申請人 ID（applicantId）過濾條件，確保員工僅能查看自己的申請。
此安全過濾機制防止員工查看他人的申請資料。

**測試合約：**

```json
{
  "scenarioId": "WFL_Q002",
  "apiEndpoint": "GET /api/v1/workflows/my/applications",
  "controller": "HR11WorkflowQryController",
  "service": "GetMyApplicationsServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {},
  "expectedQueryFilters": [
    {"field": "applicantId", "operator": "=", "value": "{currentUserId}"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.content",
    "minRecords": 0,
    "requiredFields": []
  }
}
```

---

## 2. Command 操作業務合約

### 2.1 流程啟動

#### WFL_CMD_001: 發起請假審核流程

**API 端點：** `POST /api/v1/workflows/start`

**業務場景描述：**

業務服務（如考勤服務）在員工提交請假申請後，呼叫此 API 啟動對應的審核流程。
系統根據 flowType (LEAVE_APPROVAL) 找到最新啟用的流程定義，建立流程實例，
並進入 RUNNING 狀態，發布 WorkflowStartedEvent 通知相關方。

**業務規則：**
1. 依 `flowType` 查找最新 ACTIVE 版本的流程定義
2. 建立 WorkflowInstance 並呼叫 `instance.start()` 設定狀態為 RUNNING
3. 儲存流程實例至 Repository
4. 回傳 instanceId 及流程啟動資訊

**測試合約：**

```json
{
  "scenarioId": "WFL_CMD_001",
  "apiEndpoint": "POST /api/v1/workflows/start",
  "controller": "HR11WorkflowCmdController",
  "service": "StartWorkflowServiceImpl",
  "permission": "WORKFLOW:INSTANCE:START",
  "request": {
    "flowType": "LEAVE_APPROVAL",
    "applicantId": "EMP001",
    "businessId": "LEAVE-2026-001",
    "businessType": "LEAVE",
    "summary": "特休假申請 2 天（2026/03/01-2026/03/02）"
  },
  "businessRules": [
    "依 flowType 查找最新 ACTIVE 流程定義",
    "建立 WorkflowInstance 並呼叫 instance.start() 設定 RUNNING 狀態",
    "儲存流程實例"
  ],
  "expectedDataChanges": {
    "tables": ["workflow_instances"],
    "operations": [
      {
        "type": "INSERT",
        "table": "workflow_instances",
        "expectedFields": {
          "applicant_id": "EMP001",
          "business_id": "LEAVE-2026-001",
          "business_type": "LEAVE",
          "status": "RUNNING"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "WorkflowStartedEvent",
      "expectedFields": {
        "businessId": "LEAVE-2026-001",
        "applicantId": "EMP001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "instanceId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

---

### 2.2 核准任務

#### WFL_CMD_002: 主管核准待辦審核任務

**API 端點：** `POST /api/v1/workflows/approve`

**業務場景描述：**

主管收到審核任務通知後，進入系統查看申請詳情並核准。
系統驗證：(1) 任務為 PENDING 狀態、(2) 操作者為任務指派人。
核准後更新任務狀態為 APPROVED，推進流程至下一節點。
若此為最後審核節點，流程整體狀態更新為 COMPLETED，並發布 WorkflowCompletedEvent。

**業務規則：**
1. 載入對應的 WorkflowInstance
2. 驗證任務狀態為 PENDING
3. 驗證操作者（approverId）與任務的 assigneeId 相符
4. 呼叫 `instance.approveTask()` 更新任務狀態
5. 儲存流程實例

**測試合約：**

```json
{
  "scenarioId": "WFL_CMD_002",
  "apiEndpoint": "POST /api/v1/workflows/approve",
  "controller": "HR11WorkflowCmdController",
  "service": "ApproveTaskServiceImpl",
  "permission": "WORKFLOW:TASK:APPROVE",
  "request": {
    "instanceId": "WFL-INST-001",
    "taskId": "TASK-001",
    "comment": "同意，已確認申請內容無誤"
  },
  "businessRules": [
    "任務狀態必須為 PENDING",
    "操作者必須為任務的 assigneeId",
    "核准後任務狀態變為 APPROVED",
    "流程實例被更新並儲存"
  ],
  "expectedDataChanges": {
    "tables": ["workflow_instances"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "workflow_instances",
        "expectedFields": {
          "id": "WFL-INST-001"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "WorkflowCompletedEvent",
      "expectedFields": {
        "instanceId": "WFL-INST-001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "instanceId", "type": "string", "notNull": true},
      {"name": "taskId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

---

### 2.3 駁回任務

#### WFL_CMD_003: 主管駁回待辦審核任務

**API 端點：** `POST /api/v1/workflows/reject`

**業務場景描述：**

主管審核申請後認為不符合規定，選擇駁回。
系統驗證操作者權限後，更新任務狀態為 REJECTED，
同時將整個流程實例狀態更新為 REJECTED，取消其他待辦任務。
發布 ApprovalRejectedEvent 通知申請人。

**業務規則：**
1. 載入對應的 WorkflowInstance（透過 LoadRejectInstanceTask）
2. 驗證任務狀態為 PENDING
3. 驗證操作者（approverId）為任務的指派人
4. 呼叫 `instance.rejectTask()` 更新任務及流程狀態
5. 儲存流程實例

**測試合約：**

```json
{
  "scenarioId": "WFL_CMD_003",
  "apiEndpoint": "POST /api/v1/workflows/reject",
  "controller": "HR11WorkflowCmdController",
  "service": "RejectTaskServiceImpl",
  "permission": "WORKFLOW:TASK:REJECT",
  "request": {
    "instanceId": "WFL-INST-001",
    "taskId": "TASK-001",
    "reason": "申請理由不充分，請補充說明後重新提交"
  },
  "businessRules": [
    "任務狀態必須為 PENDING",
    "操作者必須為任務的 assigneeId",
    "駁回後任務狀態變為 REJECTED",
    "流程實例狀態變為 REJECTED",
    "其餘 PENDING 任務被取消（CANCELLED）",
    "流程實例被更新並儲存"
  ],
  "expectedDataChanges": {
    "tables": ["workflow_instances"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "workflow_instances",
        "expectedFields": {
          "id": "WFL-INST-001",
          "status": "REJECTED"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "WorkflowCompletedEvent",
      "expectedFields": {
        "instanceId": "WFL-INST-001",
        "status": "REJECTED"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "instanceId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

---

### 2.4 建立代理人設定

#### WFL_CMD_004: 主管設定出差期間的審核代理人

**API 端點：** `POST /api/v1/workflows/delegations`

**業務場景描述：**

主管出差或請假期間，預先設定代理人。
系統驗證日期範圍（結束日期不得早於開始日期）後，建立代理人設定並儲存。
設定生效期間，新指派給原審核人的任務，會自動轉給代理人處理。

**業務規則：**
1. 驗證日期範圍：endDate 不可早於 startDate
2. 建立 UserDelegation 聚合物件
3. 設定 delegatorId 為當前登入使用者的 userId
4. 儲存代理人設定

**測試合約：**

```json
{
  "scenarioId": "WFL_CMD_004",
  "apiEndpoint": "POST /api/v1/workflows/delegations",
  "controller": "HR11WorkflowCmdController",
  "service": "CreateDelegationServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {
    "delegatee_id": "EMP002",
    "start_date": "2026-03-01",
    "end_date": "2026-03-07",
    "reason": "出差期間無法及時審核"
  },
  "businessRules": [
    "endDate 不可早於 startDate",
    "delegatorId 自動設為當前使用者的 userId",
    "建立 UserDelegation 並設定 isActive=true",
    "代理人設定被儲存至 Repository"
  ],
  "expectedDataChanges": {
    "tables": ["user_delegations"],
    "operations": [
      {
        "type": "INSERT",
        "table": "user_delegations",
        "expectedFields": {
          "delegate_id": "EMP002",
          "is_active": true
        }
      }
    ]
  },
  "expectedDomainEvents": [],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "delegationId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

---

### 2.5 建立流程定義

#### WFL_CMD_DEF001: 管理員建立新的流程定義

**API 端點：** `POST /api/v1/workflows/definitions`

**業務場景描述：**

系統管理員建立新的審核流程範本（如請假審核、加班審核）。
流程定義初始狀態為 DRAFT，待設定完成後再發布（publish）啟用。
建立時需指定流程名稱、類型及完整的節點與邊線定義。

**業務規則：**
1. 建立 WorkflowDefinition 聚合物件，狀態為 DRAFT
2. 儲存流程定義至 Repository
3. 回傳 definitionId

**測試合約：**

```json
{
  "scenarioId": "WFL_CMD_DEF001",
  "apiEndpoint": "POST /api/v1/workflows/definitions",
  "controller": "HR11WorkflowCmdController",
  "service": "CreateWorkflowDefinitionServiceImpl",
  "permission": "WORKFLOW:DEFINITION:CREATE",
  "request": {
    "flowName": "請假審核流程",
    "flowType": "LEAVE_APPROVAL",
    "description": "員工請假申請的審核流程",
    "nodes": [],
    "edges": [],
    "defaultDueDays": 3
  },
  "businessRules": [
    "建立 WorkflowDefinition 初始狀態為 DRAFT",
    "流程定義被儲存至 Repository"
  ],
  "expectedDataChanges": {
    "tables": ["workflow_definitions"],
    "operations": [
      {
        "type": "INSERT",
        "table": "workflow_definitions",
        "expectedFields": {
          "flow_name": "請假審核流程",
          "status": "DRAFT"
        }
      }
    ]
  },
  "expectedDomainEvents": [],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "definitionId", "type": "string", "notNull": true}
    ]
  }
}
```

---

**文件完成日期:** 2026-02-24
**合約場景總數:** 7 個（2 Query + 5 Command）
