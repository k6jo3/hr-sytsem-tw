# HR11 簽核流程服務 API 詳細規格

**版本:** 1.0
**建立日期:** 2025-12-30
**服務代碼:** HR11 (WKF)
**服務名稱:** 簽核流程服務 (Workflow Service)

---

## 目錄

1. [API 總覽](#1-api-總覽)
2. [流程定義管理 API](#2-流程定義管理-api)
3. [流程實例管理 API](#3-流程實例管理-api)
4. [審核任務管理 API](#4-審核任務管理-api)
5. [代理人管理 API](#5-代理人管理-api)
6. [流程報表 API](#6-流程報表-api)
7. [錯誤碼總覽](#7-錯誤碼總覽)
8. [領域事件總覽](#8-領域事件總覽)

---

## 1. API 總覽

### 1.1 端點清單

| 序號 | 端點 | 方法 | 說明 | Controller |
|:---:|:---|:---:|:---|:---|
| 1 | `/api/v1/workflows/definitions` | POST | 建立流程定義 | HR11DefinitionCmdController |
| 2 | `/api/v1/workflows/definitions` | GET | 查詢流程定義列表 | HR11DefinitionQryController |
| 3 | `/api/v1/workflows/definitions/{id}` | GET | 查詢流程定義詳情 | HR11DefinitionQryController |
| 4 | `/api/v1/workflows/definitions/{id}` | PUT | 更新流程定義 | HR11DefinitionCmdController |
| 5 | `/api/v1/workflows/definitions/{id}/publish` | PUT | 發布流程定義 | HR11DefinitionCmdController |
| 6 | `/api/v1/workflows/definitions/{id}/deactivate` | PUT | 停用流程定義 | HR11DefinitionCmdController |
| 7 | `/api/v1/workflows/start` | POST | 啟動審核流程 | HR11InstanceCmdController |
| 8 | `/api/v1/workflows/instances` | GET | 查詢流程實例列表 | HR11InstanceQryController |
| 9 | `/api/v1/workflows/instances/{id}` | GET | 查詢流程實例詳情 | HR11InstanceQryController |
| 10 | `/api/v1/workflows/instances/{id}/cancel` | PUT | 取消流程實例 | HR11InstanceCmdController |
| 11 | `/api/v1/workflows/tasks/pending` | GET | 查詢我的待辦任務 | HR11TaskQryController |
| 12 | `/api/v1/workflows/tasks/{id}` | GET | 查詢任務詳情 | HR11TaskQryController |
| 13 | `/api/v1/workflows/tasks/{id}/approve` | PUT | 核准任務 | HR11TaskCmdController |
| 14 | `/api/v1/workflows/tasks/{id}/reject` | PUT | 駁回任務 | HR11TaskCmdController |
| 15 | `/api/v1/workflows/tasks/{id}/delegate` | PUT | 轉交任務 | HR11TaskCmdController |
| 16 | `/api/v1/workflows/tasks/{id}/add-approver` | POST | 加簽 | HR11TaskCmdController |
| 17 | `/api/v1/workflows/my/applications` | GET | 查詢我的申請 | HR11InstanceQryController |
| 18 | `/api/v1/workflows/delegations` | POST | 建立代理人設定 | HR11DelegationCmdController |
| 19 | `/api/v1/workflows/delegations` | GET | 查詢代理人設定 | HR11DelegationQryController |
| 20 | `/api/v1/workflows/delegations/{id}` | DELETE | 刪除代理人設定 | HR11DelegationCmdController |
| 21 | `/api/v1/workflows/statistics` | GET | 查詢審核統計 | HR11StatisticsQryController |
| 22 | `/api/v1/workflows/statistics/export` | GET | 匯出審核報表 | HR11StatisticsQryController |

### 1.2 節點類型

| 節點類型 | 說明 | 特性 |
|:---|:---|:---|
| START | 開始節點 | 流程起點，唯一 |
| APPROVAL | 審核節點 | 單人或多人審核 |
| CONDITION | 條件分流 | 依變數（金額/天數等）決定路徑 |
| PARALLEL | 平行會簽 | 需全部核准才能通過 |
| END | 結束節點 | 流程終點，唯一 |

### 1.3 流程狀態

| 狀態 | 說明 |
|:---|:---|
| RUNNING | 審核中 |
| COMPLETED | 已核准 |
| REJECTED | 已駁回 |
| CANCELLED | 已取消 |

### 1.4 任務狀態

| 狀態 | 說明 |
|:---|:---|
| PENDING | 待處理 |
| APPROVED | 已核准 |
| REJECTED | 已駁回 |
| DELEGATED | 已轉交 |

---

## 2. 流程定義管理 API

### 2.1 建立流程定義

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/workflows/definitions` |
| **方法** | POST |
| **Controller** | HR11DefinitionCmdController |
| **Service** | CreateWorkflowDefinitionServiceImpl |
| **權限** | `WORKFLOW:DEFINITION:CREATE` |

**用途說明**

- **業務場景:** 系統管理員建立新的審核流程範本（如請假審核、加班審核）
- **使用者:** 系統管理員
- **前置條件:** 流程類型代碼不可重複

**Request Body**

```json
{
  "flowName": "請假審核流程",
  "flowType": "LEAVE_APPROVAL",
  "description": "員工請假申請的審核流程",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "START",
      "name": "開始",
      "nextNodeId": "node1"
    },
    {
      "nodeId": "node1",
      "nodeType": "APPROVAL",
      "name": "主管審核",
      "approverType": "DIRECT_SUPERVISOR",
      "nextNodeId": "node2",
      "rejectNodeId": "end"
    },
    {
      "nodeId": "node2",
      "nodeType": "CONDITION",
      "name": "天數判斷",
      "conditions": [
        {
          "expression": "totalDays > 3",
          "nextNodeId": "node3"
        },
        {
          "expression": "totalDays <= 3",
          "nextNodeId": "end"
        }
      ]
    },
    {
      "nodeId": "node3",
      "nodeType": "APPROVAL",
      "name": "部門主管審核",
      "approverType": "DEPARTMENT_HEAD",
      "nextNodeId": "end",
      "rejectNodeId": "end"
    },
    {
      "nodeId": "end",
      "nodeType": "END",
      "name": "結束"
    }
  ],
  "edges": [
    { "source": "start", "target": "node1" },
    { "source": "node1", "target": "node2" },
    { "source": "node2", "target": "node3" },
    { "source": "node2", "target": "end" },
    { "source": "node3", "target": "end" }
  ],
  "defaultDueDays": 3
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| flowName | String | ✅ | 1-100字元 | 流程名稱 |
| flowType | String | ✅ | 唯一，英文大寫_分隔 | 流程類型代碼 |
| description | String | | 最多500字元 | 流程說明 |
| nodes | Array | ✅ | 至少包含START和END | 節點定義 |
| nodes[].nodeId | String | ✅ | 流程內唯一 | 節點識別碼 |
| nodes[].nodeType | String | ✅ | 有效節點類型 | 節點類型 |
| nodes[].name | String | ✅ | 1-50字元 | 節點名稱 |
| nodes[].approverType | String | 條件 | APPROVAL節點必填 | 審核人類型 |
| nodes[].conditions | Array | 條件 | CONDITION節點必填 | 條件設定 |
| nodes[].nextNodeId | String | | | 下一節點 |
| nodes[].rejectNodeId | String | | | 駁回跳轉節點 |
| edges | Array | ✅ | 節點連線 | 邊線定義 |
| defaultDueDays | Integer | | >=1 | 預設處理天數 |

**審核人類型（approverType）**

| 值 | 說明 |
|:---|:---|
| DIRECT_SUPERVISOR | 直屬主管 |
| DEPARTMENT_HEAD | 部門主管 |
| SPECIFIC_ROLE | 指定角色 |
| SPECIFIC_USER | 指定人員 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "definitionId": "def-001",
    "flowName": "請假審核流程",
    "flowType": "LEAVE_APPROVAL",
    "version": 1,
    "status": "DRAFT",
    "createdAt": "2025-12-30T10:00:00Z"
  },
  "message": "流程定義建立成功"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | WKF_INVALID_FLOW_TYPE | 流程類型格式不正確 | 使用英文大寫_分隔 |
| 400 | WKF_DUPLICATE_FLOW_TYPE | 流程類型已存在 | 使用不同的類型代碼 |
| 400 | WKF_INVALID_NODES | 節點定義不完整 | 確保有START和END節點 |
| 400 | WKF_INVALID_EDGES | 節點連線不正確 | 確保所有節點可達 |

---

### 2.2 查詢流程定義列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/definitions` |
| **方法** | GET |
| **Controller** | HR11DefinitionQryController |
| **Service** | GetWorkflowDefinitionListServiceImpl |
| **權限** | `WORKFLOW:DEFINITION:READ` |

**用途說明**

- **業務場景:** 管理員查看系統中已建立的流程定義
- **使用者:** 系統管理員

**Query Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| status | String | | 狀態篩選（DRAFT/ACTIVE/INACTIVE） |
| keyword | String | | 流程名稱關鍵字 |
| page | Integer | | 頁碼，預設 1 |
| pageSize | Integer | | 每頁筆數，預設 20 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "definitionId": "def-001",
        "flowName": "請假審核流程",
        "flowType": "LEAVE_APPROVAL",
        "description": "員工請假申請的審核流程",
        "status": "ACTIVE",
        "version": 2,
        "nodeCount": 5,
        "createdAt": "2025-12-01T10:00:00Z",
        "updatedAt": "2025-12-15T14:30:00Z"
      },
      {
        "definitionId": "def-002",
        "flowName": "加班審核流程",
        "flowType": "OVERTIME_APPROVAL",
        "description": "員工加班申請的審核流程",
        "status": "ACTIVE",
        "version": 1,
        "nodeCount": 3,
        "createdAt": "2025-12-02T10:00:00Z",
        "updatedAt": null
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 2,
      "totalPages": 1
    }
  }
}
```

---

### 2.3 查詢流程定義詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/definitions/{id}` |
| **方法** | GET |
| **Controller** | HR11DefinitionQryController |
| **Service** | GetWorkflowDefinitionDetailServiceImpl |
| **權限** | `WORKFLOW:DEFINITION:READ` |

**用途說明**

- **業務場景:** 查看流程定義的完整節點與邊線配置
- **使用者:** 系統管理員

**Path Parameters**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| id | String | 流程定義 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "definitionId": "def-001",
    "flowName": "請假審核流程",
    "flowType": "LEAVE_APPROVAL",
    "description": "員工請假申請的審核流程",
    "status": "ACTIVE",
    "version": 2,
    "nodes": [
      {
        "nodeId": "start",
        "nodeType": "START",
        "name": "開始",
        "config": {}
      },
      {
        "nodeId": "node1",
        "nodeType": "APPROVAL",
        "name": "主管審核",
        "config": {
          "approverType": "DIRECT_SUPERVISOR",
          "dueDays": 3
        }
      }
    ],
    "edges": [
      { "source": "start", "target": "node1" },
      { "source": "node1", "target": "end" }
    ],
    "defaultDueDays": 3,
    "createdAt": "2025-12-01T10:00:00Z",
    "createdBy": "admin",
    "updatedAt": "2025-12-15T14:30:00Z",
    "updatedBy": "admin"
  }
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 404 | WKF_DEFINITION_NOT_FOUND | 流程定義不存在 |

---

### 2.4 更新流程定義

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/workflows/definitions/{id}` |
| **方法** | PUT |
| **Controller** | HR11DefinitionCmdController |
| **Service** | UpdateWorkflowDefinitionServiceImpl |
| **權限** | `WORKFLOW:DEFINITION:UPDATE` |

**用途說明**

- **業務場景:** 修改草稿狀態的流程定義
- **使用者:** 系統管理員
- **前置條件:** 僅能修改 DRAFT 或 INACTIVE 狀態的定義

**Request Body**

```json
{
  "flowName": "請假審核流程（修訂版）",
  "description": "員工請假申請的審核流程，新增部門主管審核",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "START",
      "name": "開始",
      "nextNodeId": "node1"
    },
    {
      "nodeId": "node1",
      "nodeType": "APPROVAL",
      "name": "主管審核",
      "approverType": "DIRECT_SUPERVISOR",
      "nextNodeId": "end",
      "rejectNodeId": "end"
    },
    {
      "nodeId": "end",
      "nodeType": "END",
      "name": "結束"
    }
  ],
  "edges": [
    { "source": "start", "target": "node1" },
    { "source": "node1", "target": "end" }
  ],
  "defaultDueDays": 5
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | WKF_CANNOT_UPDATE_ACTIVE | 無法修改已發布的流程 |
| 404 | WKF_DEFINITION_NOT_FOUND | 流程定義不存在 |

---

### 2.5 發布流程定義

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/workflows/definitions/{id}/publish` |
| **方法** | PUT |
| **Controller** | HR11DefinitionCmdController |
| **Service** | PublishWorkflowDefinitionServiceImpl |
| **權限** | `WORKFLOW:DEFINITION:PUBLISH` |

**用途說明**

- **業務場景:** 將草稿流程定義發布為正式版本
- **使用者:** 系統管理員
- **後置條件:** 流程定義變為 ACTIVE，版本號 +1

**業務邏輯**

1. 驗證流程定義完整性（有START、END節點，所有節點可達）
2. 更新狀態為 ACTIVE
3. 版本號 +1
4. 發布 WorkflowDefinitionPublishedEvent

**Response Body**

```json
{
  "success": true,
  "data": {
    "definitionId": "def-001",
    "flowName": "請假審核流程",
    "flowType": "LEAVE_APPROVAL",
    "status": "ACTIVE",
    "version": 2,
    "publishedAt": "2025-12-30T14:00:00Z"
  },
  "message": "流程定義發布成功"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | WKF_DEFINITION_INCOMPLETE | 流程定義不完整 |
| 400 | WKF_ALREADY_ACTIVE | 流程已是發布狀態 |

---

### 2.6 停用流程定義

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/workflows/definitions/{id}/deactivate` |
| **方法** | PUT |
| **Controller** | HR11DefinitionCmdController |
| **Service** | DeactivateWorkflowDefinitionServiceImpl |
| **權限** | `WORKFLOW:DEFINITION:DEACTIVATE` |

**用途說明**

- **業務場景:** 停用不再使用的流程定義
- **使用者:** 系統管理員
- **注意:** 不影響已啟動的流程實例

**Response Body**

```json
{
  "success": true,
  "data": {
    "definitionId": "def-001",
    "status": "INACTIVE",
    "deactivatedAt": "2025-12-30T16:00:00Z"
  },
  "message": "流程定義已停用"
}
```

---

## 3. 流程實例管理 API

### 3.1 啟動審核流程

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/workflows/start` |
| **方法** | POST |
| **Controller** | HR11InstanceCmdController |
| **Service** | StartWorkflowServiceImpl |
| **權限** | `WORKFLOW:INSTANCE:START` |

**用途說明**

- **業務場景:** 業務服務（請假、加班等）啟動審核流程
- **使用者:** 業務服務（透過 Feign Client）
- **後置條件:** 建立流程實例，建立第一個審核任務

**業務邏輯**

1. 根據 flowType 載入流程定義
2. 建立 WorkflowInstance
3. 找到 START 節點的下一個節點
4. 若下一個是 APPROVAL 節點，建立 ApprovalTask
5. 若下一個是 CONDITION 節點，根據變數決定路徑
6. 發布 TaskAssignedEvent 通知審核人

**Request Body**

```json
{
  "flowType": "LEAVE_APPROVAL",
  "businessType": "LEAVE_APPLICATION",
  "businessId": "leave-001",
  "applicantId": "emp-001",
  "applicantName": "張三",
  "summary": "特休假2天（2025/12/20-2025/12/21）",
  "variables": {
    "leaveType": "ANNUAL",
    "totalDays": 2,
    "startDate": "2025-12-20",
    "endDate": "2025-12-21"
  },
  "businessUrl": "/attendance/leave/applications/leave-001"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| flowType | String | ✅ | 流程類型代碼 |
| businessType | String | ✅ | 業務類型 |
| businessId | String | ✅ | 業務單據 ID |
| applicantId | String | ✅ | 申請人員工 ID |
| applicantName | String | ✅ | 申請人姓名 |
| summary | String | ✅ | 申請摘要 |
| variables | Object | | 流程變數（供條件判斷） |
| businessUrl | String | | 業務單據連結 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "instanceId": "inst-001",
    "definitionId": "def-001",
    "flowType": "LEAVE_APPROVAL",
    "businessId": "leave-001",
    "status": "RUNNING",
    "currentNode": "node1",
    "currentNodeName": "主管審核",
    "startedAt": "2025-12-30T10:00:00Z",
    "firstTask": {
      "taskId": "task-001",
      "assigneeId": "mgr-001",
      "assigneeName": "李經理",
      "dueDate": "2026-01-02T10:00:00Z"
    }
  },
  "message": "流程啟動成功"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | WKF_DEFINITION_NOT_FOUND | 流程定義不存在 |
| 400 | WKF_DEFINITION_INACTIVE | 流程定義未啟用 |
| 400 | WKF_DUPLICATE_INSTANCE | 此業務單據已有流程實例 |
| 400 | WKF_SUPERVISOR_NOT_FOUND | 找不到直屬主管 |

**領域事件**

| 事件名稱 | Kafka Topic | 說明 |
|:---|:---|:---|
| WorkflowStartedEvent | workflow.started | 流程啟動 |
| TaskAssignedEvent | workflow.task.assigned | 任務指派 |

---

### 3.2 查詢流程實例列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/instances` |
| **方法** | GET |
| **Controller** | HR11InstanceQryController |
| **Service** | GetWorkflowInstanceListServiceImpl |
| **權限** | `WORKFLOW:INSTANCE:READ` |

**用途說明**

- **業務場景:** 管理員查看所有流程實例
- **使用者:** 系統管理員

**Query Parameters**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| flowType | String | 流程類型篩選 |
| status | String | 狀態篩選 |
| applicantId | String | 申請人 ID |
| startDateFrom | String | 開始日期起 |
| startDateTo | String | 開始日期迄 |
| page | Integer | 頁碼 |
| pageSize | Integer | 每頁筆數 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "instanceId": "inst-001",
        "flowType": "LEAVE_APPROVAL",
        "flowName": "請假審核流程",
        "businessType": "LEAVE_APPLICATION",
        "businessId": "leave-001",
        "applicant": {
          "employeeId": "emp-001",
          "fullName": "張三",
          "departmentName": "研發部"
        },
        "summary": "特休假2天",
        "status": "RUNNING",
        "currentNodeName": "主管審核",
        "startedAt": "2025-12-30T10:00:00Z",
        "completedAt": null,
        "duration": null
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 1,
      "totalPages": 1
    }
  }
}
```

---

### 3.3 查詢流程實例詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/instances/{id}` |
| **方法** | GET |
| **Controller** | HR11InstanceQryController |
| **Service** | GetWorkflowInstanceDetailServiceImpl |
| **權限** | `WORKFLOW:INSTANCE:READ` |

**用途說明**

- **業務場景:** 查看流程實例的審核進度與歷史
- **使用者:** 申請人、審核人、管理員

**Response Body**

```json
{
  "success": true,
  "data": {
    "instanceId": "inst-001",
    "definition": {
      "definitionId": "def-001",
      "flowName": "請假審核流程",
      "flowType": "LEAVE_APPROVAL"
    },
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-001",
    "businessUrl": "/attendance/leave/applications/leave-001",
    "applicant": {
      "employeeId": "emp-001",
      "fullName": "張三",
      "departmentName": "研發部"
    },
    "summary": "特休假2天（2025/12/20-2025/12/21）",
    "status": "COMPLETED",
    "currentNode": "end",
    "startedAt": "2025-12-30T10:00:00Z",
    "completedAt": "2025-12-30T14:30:00Z",
    "duration": "4.5小時",
    "timeline": [
      {
        "nodeId": "start",
        "nodeName": "開始",
        "action": "STARTED",
        "timestamp": "2025-12-30T10:00:00Z"
      },
      {
        "nodeId": "node1",
        "nodeName": "主管審核",
        "action": "APPROVED",
        "actor": {
          "employeeId": "mgr-001",
          "fullName": "李經理"
        },
        "comments": "同意",
        "timestamp": "2025-12-30T11:30:00Z"
      },
      {
        "nodeId": "node2",
        "nodeName": "部門主管審核",
        "action": "APPROVED",
        "actor": {
          "employeeId": "dir-001",
          "fullName": "王總監"
        },
        "comments": "核准",
        "timestamp": "2025-12-30T14:30:00Z"
      },
      {
        "nodeId": "end",
        "nodeName": "結束",
        "action": "COMPLETED",
        "timestamp": "2025-12-30T14:30:00Z"
      }
    ],
    "pendingTasks": [],
    "variables": {
      "leaveType": "ANNUAL",
      "totalDays": 2
    }
  }
}
```

---

### 3.4 取消流程實例

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/workflows/instances/{id}/cancel` |
| **方法** | PUT |
| **Controller** | HR11InstanceCmdController |
| **Service** | CancelWorkflowInstanceServiceImpl |
| **權限** | `WORKFLOW:INSTANCE:CANCEL` |

**用途說明**

- **業務場景:** 申請人取消審核中的申請
- **使用者:** 申請人本人
- **前置條件:** 流程狀態為 RUNNING

**Request Body**

```json
{
  "reason": "因行程變更，取消請假申請"
}
```

**Response Body**

```json
{
  "success": true,
  "data": {
    "instanceId": "inst-001",
    "status": "CANCELLED",
    "cancelledAt": "2025-12-30T15:00:00Z"
  },
  "message": "流程已取消"
}
```

**領域事件**

| 事件名稱 | Kafka Topic | 說明 |
|:---|:---|:---|
| WorkflowCancelledEvent | workflow.cancelled | 流程取消 |

---

### 3.5 查詢我的申請

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/my/applications` |
| **方法** | GET |
| **Controller** | HR11InstanceQryController |
| **Service** | GetMyApplicationsServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 員工查看自己提交的所有申請進度
- **使用者:** 一般員工

**Query Parameters**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| status | String | 狀態篩選 |
| flowType | String | 流程類型篩選 |
| page | Integer | 頁碼 |
| pageSize | Integer | 每頁筆數 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "instanceId": "inst-001",
        "flowType": "LEAVE_APPROVAL",
        "flowName": "請假審核流程",
        "summary": "特休假2天",
        "status": "RUNNING",
        "currentNodeName": "主管審核",
        "currentAssignee": {
          "employeeId": "mgr-001",
          "fullName": "李經理"
        },
        "startedAt": "2025-12-30T10:00:00Z",
        "businessUrl": "/attendance/leave/applications/leave-001"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 1,
      "totalPages": 1
    }
  }
}
```

---

## 4. 審核任務管理 API

### 4.1 查詢我的待辦任務

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/tasks/pending` |
| **方法** | GET |
| **Controller** | HR11TaskQryController |
| **Service** | GetPendingTasksServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 審核人查看需要處理的待辦任務
- **使用者:** 主管、審核人

**Query Parameters**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| businessType | String | 業務類型篩選 |
| isOverdue | Boolean | 是否只顯示逾期任務 |
| page | Integer | 頁碼 |
| pageSize | Integer | 每頁筆數 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "taskId": "task-001",
        "instanceId": "inst-001",
        "businessType": "LEAVE_APPLICATION",
        "businessTypeName": "請假申請",
        "businessId": "leave-001",
        "businessUrl": "/attendance/leave/applications/leave-001",
        "applicant": {
          "employeeId": "emp-001",
          "fullName": "張三",
          "departmentName": "研發部"
        },
        "summary": "特休假2天（2025/12/20-2025/12/21）",
        "nodeName": "主管審核",
        "status": "PENDING",
        "isOverdue": false,
        "dueDate": "2026-01-02T10:00:00Z",
        "createdAt": "2025-12-30T10:00:00Z"
      },
      {
        "taskId": "task-002",
        "instanceId": "inst-002",
        "businessType": "OVERTIME_APPLICATION",
        "businessTypeName": "加班申請",
        "businessId": "ot-001",
        "businessUrl": "/attendance/overtime/applications/ot-001",
        "applicant": {
          "employeeId": "emp-002",
          "fullName": "李四",
          "departmentName": "研發部"
        },
        "summary": "加班4小時（2025/12/25）",
        "nodeName": "主管審核",
        "status": "PENDING",
        "isOverdue": true,
        "dueDate": "2025-12-28T10:00:00Z",
        "overdueHours": 48,
        "createdAt": "2025-12-25T10:00:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 2,
      "totalPages": 1
    },
    "summary": {
      "totalPending": 2,
      "totalOverdue": 1
    }
  }
}
```

---

### 4.2 查詢任務詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/tasks/{id}` |
| **方法** | GET |
| **Controller** | HR11TaskQryController |
| **Service** | GetTaskDetailServiceImpl |
| **權限** | 任務指派人或管理員 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "instanceId": "inst-001",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-001",
    "businessUrl": "/attendance/leave/applications/leave-001",
    "applicant": {
      "employeeId": "emp-001",
      "fullName": "張三",
      "departmentName": "研發部",
      "jobTitle": "資深工程師"
    },
    "summary": "特休假2天（2025/12/20-2025/12/21）",
    "nodeName": "主管審核",
    "assignee": {
      "employeeId": "mgr-001",
      "fullName": "李經理"
    },
    "delegatedFrom": null,
    "status": "PENDING",
    "dueDate": "2026-01-02T10:00:00Z",
    "isOverdue": false,
    "createdAt": "2025-12-30T10:00:00Z",
    "variables": {
      "leaveType": "ANNUAL",
      "totalDays": 2,
      "startDate": "2025-12-20",
      "endDate": "2025-12-21"
    },
    "previousApprovals": []
  }
}
```

---

### 4.3 核准任務

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/workflows/tasks/{id}/approve` |
| **方法** | PUT |
| **Controller** | HR11TaskCmdController |
| **Service** | ApproveTaskServiceImpl |
| **權限** | 任務指派人或代理人 |

**用途說明**

- **業務場景:** 審核人核准待辦任務
- **使用者:** 主管、審核人
- **後置條件:** 推進流程至下一節點

**業務邏輯**

1. 驗證任務狀態為 PENDING
2. 驗證操作者為指派人或有效代理人
3. 更新任務狀態為 APPROVED
4. 推進流程至下一節點
5. 若下一節點為 END，發布 ApprovalCompletedEvent
6. 若下一節點為 APPROVAL，建立新的 ApprovalTask

**Request Body**

```json
{
  "comments": "同意，已確認請假期間無重要會議"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| comments | String | | 審核意見，最多500字元 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "status": "APPROVED",
    "approvedAt": "2025-12-30T14:00:00Z",
    "nextStep": {
      "hasNext": true,
      "nextNodeName": "部門主管審核",
      "nextAssignee": {
        "employeeId": "dir-001",
        "fullName": "王總監"
      }
    }
  },
  "message": "核准成功"
}
```

**完成流程時的回應**

```json
{
  "success": true,
  "data": {
    "taskId": "task-002",
    "status": "APPROVED",
    "approvedAt": "2025-12-30T16:00:00Z",
    "nextStep": {
      "hasNext": false,
      "instanceStatus": "COMPLETED"
    }
  },
  "message": "核准成功，流程已完成"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | WKF_TASK_NOT_PENDING | 任務非待處理狀態 |
| 403 | WKF_NOT_ASSIGNEE | 您不是此任務的審核人 |
| 404 | WKF_TASK_NOT_FOUND | 任務不存在 |

**領域事件**

| 事件名稱 | Kafka Topic | 說明 |
|:---|:---|:---|
| TaskApprovedEvent | workflow.task.approved | 任務核准 |
| TaskAssignedEvent | workflow.task.assigned | 下一任務指派（若有） |
| ApprovalCompletedEvent | workflow.approval.completed | 流程完成（若為最後節點） |

---

### 4.4 駁回任務

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/workflows/tasks/{id}/reject` |
| **方法** | PUT |
| **Controller** | HR11TaskCmdController |
| **Service** | RejectTaskServiceImpl |
| **權限** | 任務指派人或代理人 |

**用途說明**

- **業務場景:** 審核人駁回待辦任務
- **使用者:** 主管、審核人
- **後置條件:** 流程狀態變為 REJECTED

**Request Body**

```json
{
  "reason": "請假期間有重要專案交付，建議調整日期"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| reason | String | ✅ | 駁回原因，1-500字元 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "status": "REJECTED",
    "rejectedAt": "2025-12-30T14:00:00Z",
    "instanceStatus": "REJECTED"
  },
  "message": "已駁回"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | WKF_REJECT_REASON_REQUIRED | 駁回必須提供原因 |
| 400 | WKF_TASK_NOT_PENDING | 任務非待處理狀態 |

**領域事件**

| 事件名稱 | Kafka Topic | 說明 |
|:---|:---|:---|
| TaskRejectedEvent | workflow.task.rejected | 任務駁回 |
| ApprovalRejectedEvent | workflow.approval.rejected | 流程駁回 |

---

### 4.5 轉交任務

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/workflows/tasks/{id}/delegate` |
| **方法** | PUT |
| **Controller** | HR11TaskCmdController |
| **Service** | DelegateTaskServiceImpl |
| **權限** | 任務指派人 |

**用途說明**

- **業務場景:** 審核人將任務轉交給他人處理
- **使用者:** 主管、審核人
- **注意:** 轉交後原審核人無法再處理此任務

**Request Body**

```json
{
  "delegateeId": "mgr-002",
  "reason": "因出差無法及時處理，轉交副理協助審核"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| delegateeId | String | ✅ | 受委託人員工 ID |
| reason | String | | 轉交原因 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "status": "DELEGATED",
    "delegatedTo": {
      "employeeId": "mgr-002",
      "fullName": "陳副理"
    },
    "delegatedAt": "2025-12-30T14:00:00Z"
  },
  "message": "任務已轉交"
}
```

**領域事件**

| 事件名稱 | Kafka Topic | 說明 |
|:---|:---|:---|
| TaskDelegatedEvent | workflow.task.delegated | 任務轉交 |
| TaskAssignedEvent | workflow.task.assigned | 通知新指派人 |

---

### 4.6 加簽

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/workflows/tasks/{id}/add-approver` |
| **方法** | POST |
| **Controller** | HR11TaskCmdController |
| **Service** | AddApproverServiceImpl |
| **權限** | 任務指派人 |

**用途說明**

- **業務場景:** 審核人需要額外人員協助審核（會簽）
- **使用者:** 主管、審核人
- **注意:** 加簽的審核人也需核准後，流程才會推進

**Request Body**

```json
{
  "approverId": "expert-001",
  "reason": "涉及法務問題，需法務部門確認"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| approverId | String | ✅ | 加簽審核人員工 ID |
| reason | String | | 加簽原因 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "originalTaskId": "task-001",
    "addedTaskId": "task-003",
    "addedApprover": {
      "employeeId": "expert-001",
      "fullName": "林法務"
    },
    "addedAt": "2025-12-30T14:00:00Z"
  },
  "message": "已加簽"
}
```

---

## 5. 代理人管理 API

### 5.1 建立代理人設定

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/workflows/delegations` |
| **方法** | POST |
| **Controller** | HR11DelegationCmdController |
| **Service** | CreateDelegationServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 員工設定請假/出差期間的審核代理人
- **使用者:** 一般員工
- **注意:** 設定期間內，新指派的任務會自動轉給代理人

**Request Body**

```json
{
  "delegateeId": "mgr-002",
  "startDate": "2025-12-25",
  "endDate": "2025-12-31",
  "reason": "出差期間"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| delegateeId | String | ✅ | 有效員工 ID | 代理人 ID |
| startDate | String | ✅ | 不可早於今天 | 開始日期 |
| endDate | String | ✅ | 不可早於開始日期 | 結束日期 |
| reason | String | | 最多200字元 | 代理原因 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "delegationId": "del-001",
    "delegator": {
      "employeeId": "mgr-001",
      "fullName": "李經理"
    },
    "delegatee": {
      "employeeId": "mgr-002",
      "fullName": "陳副理"
    },
    "startDate": "2025-12-25",
    "endDate": "2025-12-31",
    "isActive": true,
    "createdAt": "2025-12-20T10:00:00Z"
  },
  "message": "代理人設定成功"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | WKF_INVALID_DELEGATEE | 代理人不存在或無效 |
| 400 | WKF_INVALID_DATE_RANGE | 日期範圍不正確 |
| 400 | WKF_DELEGATION_OVERLAP | 與現有代理設定重疊 |
| 400 | WKF_SELF_DELEGATION | 不能指定自己為代理人 |

---

### 5.2 查詢代理人設定

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/delegations` |
| **方法** | GET |
| **Controller** | HR11DelegationQryController |
| **Service** | GetDelegationsServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 員工查看自己設定的代理人
- **使用者:** 一般員工

**Query Parameters**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| includeExpired | Boolean | 是否包含已過期的設定（預設 false） |

**Response Body**

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "delegationId": "del-001",
        "delegatee": {
          "employeeId": "mgr-002",
          "fullName": "陳副理",
          "departmentName": "研發部"
        },
        "startDate": "2025-12-25",
        "endDate": "2025-12-31",
        "reason": "出差期間",
        "isActive": true,
        "isEffective": false,
        "createdAt": "2025-12-20T10:00:00Z"
      }
    ]
  }
}
```

---

### 5.3 刪除代理人設定

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `DELETE /api/v1/workflows/delegations/{id}` |
| **方法** | DELETE |
| **Controller** | HR11DelegationCmdController |
| **Service** | DeleteDelegationServiceImpl |
| **權限** | 設定者本人 |

**用途說明**

- **業務場景:** 員工取消尚未生效或進行中的代理設定
- **使用者:** 設定者本人

**Response Body**

```json
{
  "success": true,
  "message": "代理人設定已刪除"
}
```

---

## 6. 流程報表 API

### 6.1 查詢審核統計

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/statistics` |
| **方法** | GET |
| **Controller** | HR11StatisticsQryController |
| **Service** | GetWorkflowStatisticsServiceImpl |
| **權限** | `WORKFLOW:STATISTICS:READ` |

**用途說明**

- **業務場景:** 管理員查看各類審核流程的統計數據
- **使用者:** 系統管理員、HR 主管

**Query Parameters**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| startDate | String | 統計開始日期 |
| endDate | String | 統計結束日期 |
| flowType | String | 流程類型篩選 |
| departmentId | String | 部門篩選 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "period": {
      "startDate": "2025-12-01",
      "endDate": "2025-12-31"
    },
    "summary": {
      "totalInstances": 150,
      "completedCount": 120,
      "rejectedCount": 15,
      "runningCount": 10,
      "cancelledCount": 5,
      "approvalRate": 88.9,
      "averageDuration": "1.5天"
    },
    "byFlowType": [
      {
        "flowType": "LEAVE_APPROVAL",
        "flowName": "請假審核",
        "totalCount": 80,
        "completedCount": 70,
        "rejectedCount": 5,
        "averageDuration": "1.2天"
      },
      {
        "flowType": "OVERTIME_APPROVAL",
        "flowName": "加班審核",
        "totalCount": 50,
        "completedCount": 40,
        "rejectedCount": 8,
        "averageDuration": "0.8天"
      }
    ],
    "overdueAnalysis": {
      "totalOverdueTasks": 12,
      "overdueRate": 8.0,
      "topOverdueApprovers": [
        {
          "employeeId": "mgr-005",
          "fullName": "趙經理",
          "overdueCount": 5
        }
      ]
    }
  }
}
```

---

### 6.2 匯出審核報表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/workflows/statistics/export` |
| **方法** | GET |
| **Controller** | HR11StatisticsQryController |
| **Service** | ExportWorkflowStatisticsServiceImpl |
| **權限** | `WORKFLOW:STATISTICS:EXPORT` |

**用途說明**

- **業務場景:** 匯出審核流程統計報表
- **使用者:** 系統管理員、HR 主管

**Query Parameters**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| startDate | String | 統計開始日期 |
| endDate | String | 統計結束日期 |
| flowType | String | 流程類型篩選 |
| format | String | 匯出格式（EXCEL/PDF） |

**Response**

- Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` (Excel)
- Content-Type: `application/pdf` (PDF)
- Content-Disposition: `attachment; filename="workflow_statistics_202512.xlsx"`

---

## 7. 錯誤碼總覽

### 7.1 流程定義錯誤 (WKF_DEF_*)

| 錯誤碼 | HTTP狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| WKF_DEFINITION_NOT_FOUND | 404 | 流程定義不存在 | 確認定義 ID 正確 |
| WKF_DUPLICATE_FLOW_TYPE | 400 | 流程類型已存在 | 使用不同的類型代碼 |
| WKF_INVALID_FLOW_TYPE | 400 | 流程類型格式不正確 | 使用英文大寫_分隔 |
| WKF_INVALID_NODES | 400 | 節點定義不完整 | 確保有START和END節點 |
| WKF_INVALID_EDGES | 400 | 節點連線不正確 | 確保所有節點可達 |
| WKF_DEFINITION_INCOMPLETE | 400 | 流程定義不完整 | 補齊必要節點 |
| WKF_CANNOT_UPDATE_ACTIVE | 400 | 無法修改已發布的流程 | 先停用再修改 |
| WKF_ALREADY_ACTIVE | 400 | 流程已是發布狀態 | 無需重複發布 |

### 7.2 流程實例錯誤 (WKF_INST_*)

| 錯誤碼 | HTTP狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| WKF_INSTANCE_NOT_FOUND | 404 | 流程實例不存在 | 確認實例 ID 正確 |
| WKF_DUPLICATE_INSTANCE | 400 | 此業務單據已有流程實例 | 查詢現有實例 |
| WKF_DEFINITION_INACTIVE | 400 | 流程定義未啟用 | 聯繫管理員啟用 |
| WKF_INSTANCE_NOT_RUNNING | 400 | 流程非審核中狀態 | 無法操作已完成的流程 |
| WKF_NOT_APPLICANT | 403 | 您不是此流程的申請人 | 只有申請人可取消 |

### 7.3 審核任務錯誤 (WKF_TASK_*)

| 錯誤碼 | HTTP狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| WKF_TASK_NOT_FOUND | 404 | 任務不存在 | 確認任務 ID 正確 |
| WKF_TASK_NOT_PENDING | 400 | 任務非待處理狀態 | 任務已被處理 |
| WKF_NOT_ASSIGNEE | 403 | 您不是此任務的審核人 | 確認您是指派人或代理人 |
| WKF_REJECT_REASON_REQUIRED | 400 | 駁回必須提供原因 | 填寫駁回原因 |
| WKF_SUPERVISOR_NOT_FOUND | 400 | 找不到直屬主管 | 確認員工有設定主管 |

### 7.4 代理人錯誤 (WKF_DEL_*)

| 錯誤碼 | HTTP狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| WKF_DELEGATION_NOT_FOUND | 404 | 代理設定不存在 | 確認設定 ID 正確 |
| WKF_INVALID_DELEGATEE | 400 | 代理人不存在或無效 | 選擇有效的員工 |
| WKF_INVALID_DATE_RANGE | 400 | 日期範圍不正確 | 開始日期需小於結束日期 |
| WKF_DELEGATION_OVERLAP | 400 | 與現有代理設定重疊 | 調整日期範圍或刪除舊設定 |
| WKF_SELF_DELEGATION | 400 | 不能指定自己為代理人 | 選擇其他員工 |

---

## 8. 領域事件總覽

### 8.1 事件清單

| 事件名稱 | Kafka Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| WorkflowStartedEvent | workflow.started | 流程啟動 | Notification |
| WorkflowCancelledEvent | workflow.cancelled | 流程取消 | 業務服務 |
| TaskAssignedEvent | workflow.task.assigned | 任務指派 | Notification |
| TaskApprovedEvent | workflow.task.approved | 任務核准 | - |
| TaskRejectedEvent | workflow.task.rejected | 任務駁回 | - |
| TaskDelegatedEvent | workflow.task.delegated | 任務轉交 | Notification |
| TaskOverdueEvent | workflow.task.overdue | 任務逾期 | Notification |
| ApprovalCompletedEvent | workflow.approval.completed | 流程核准完成 | 業務服務、Notification |
| ApprovalRejectedEvent | workflow.approval.rejected | 流程駁回 | 業務服務、Notification |

### 8.2 事件 Payload 結構

#### TaskAssignedEvent

```json
{
  "eventId": "evt-wkf-001",
  "eventType": "TaskAssigned",
  "timestamp": "2025-12-30T10:00:00Z",
  "payload": {
    "taskId": "task-001",
    "instanceId": "inst-001",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-001",
    "applicant": {
      "employeeId": "emp-001",
      "fullName": "張三"
    },
    "assignee": {
      "employeeId": "mgr-001",
      "fullName": "李經理"
    },
    "taskSummary": "員工張三申請特休假2天",
    "dueDate": "2026-01-02T10:00:00Z",
    "businessUrl": "/attendance/leave/applications/leave-001"
  }
}
```

#### ApprovalCompletedEvent

```json
{
  "eventId": "evt-wkf-002",
  "eventType": "ApprovalCompleted",
  "timestamp": "2025-12-30T14:30:00Z",
  "payload": {
    "instanceId": "inst-001",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-001",
    "applicantId": "emp-001",
    "finalApprover": {
      "employeeId": "dir-001",
      "fullName": "王總監"
    },
    "approvalPath": [
      {
        "nodeId": "node-1",
        "approver": "李經理",
        "approvedAt": "2025-12-30T11:00:00Z"
      },
      {
        "nodeId": "node-2",
        "approver": "王總監",
        "approvedAt": "2025-12-30T14:30:00Z"
      }
    ],
    "totalDuration": "4.5h"
  }
}
```

#### ApprovalRejectedEvent

```json
{
  "eventId": "evt-wkf-003",
  "eventType": "ApprovalRejected",
  "timestamp": "2025-12-30T12:00:00Z",
  "payload": {
    "instanceId": "inst-002",
    "businessType": "OVERTIME_APPLICATION",
    "businessId": "ot-002",
    "applicantId": "emp-003",
    "rejectedBy": {
      "employeeId": "mgr-001",
      "fullName": "李經理"
    },
    "rejectedAt": "2025-12-30T12:00:00Z",
    "rejectionReason": "加班時數已達本月上限",
    "nodeId": "node-1"
  }
}
```

#### TaskOverdueEvent

```json
{
  "eventId": "evt-wkf-004",
  "eventType": "TaskOverdue",
  "timestamp": "2025-12-30T10:00:00Z",
  "payload": {
    "taskId": "task-003",
    "instanceId": "inst-003",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-005",
    "assignee": {
      "employeeId": "mgr-001",
      "fullName": "李經理"
    },
    "dueDate": "2025-12-28T10:00:00Z",
    "overdueHours": 48,
    "applicant": {
      "employeeId": "emp-005",
      "fullName": "趙六"
    }
  }
}
```

---

**文件完成日期:** 2025-12-30
**API 端點數量:** 22 個
