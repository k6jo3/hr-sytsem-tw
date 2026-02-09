# 流程引擎服務業務合約 (Workflow Service Business Contract)

> **服務代碼:** HR11
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/11_簽核流程服務系統設計書.md`

---

## 📋 概述

本合約文件定義流程引擎服務的**完整業務場景**，包括：
1. **Command 操作場景**（建立流程、啟動實例、審核任務）- 驗證業務規則與領域事件
2. **Query 操作場景**（查詢）- 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景
- ✅ 新增 4 個領域事件的詳細定義
- ✅ 對應到實際的 API 端點
- ✅ 使用實際存在的欄位（status 欄位）
- ✅ 包含完整的業務規則驗證

**服務定位：**
流程引擎服務是整個 HR 系統的核心基礎服務，提供通用的多層級審核流程管理功能。本服務與 Attendance、Payroll、Training 等業務服務整合，統一管理所有需要審核的業務流程（請假、加班、訓練報名等）。

**資料軟刪除策略：**
- **流程定義**: 使用 `is_active` 欄位，TRUE 為啟用，FALSE 為停用
- **流程實例**: 使用 `status` 欄位，'RUNNING' 為進行中，'COMPLETED' 為已完成，'REJECTED' 為已駁回，'CANCELLED' 為已取消
- **審核任務**: 使用 `status` 欄位，'PENDING' 為待審核，'APPROVED' 為已核准，'REJECTED' 為已駁回
- **歷史記錄**: 不進行軟刪除，保留所有歷史記錄（用於稽核追蹤）

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [流程定義管理 Command](#11-流程定義管理-command)
   - 1.2 [流程實例管理 Command](#12-流程實例管理-command)
   - 1.3 [審核任務管理 Command](#13-審核任務管理-command)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [流程定義查詢](#21-流程定義查詢)
   - 2.2 [流程實例查詢](#22-流程實例查詢)
   - 2.3 [審核任務查詢](#23-審核任務查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 流程定義管理 Command

#### WFL_CMD_001: 建立流程定義

**業務場景描述：**
HR 管理員或系統管理員建立新的流程定義，定義審核節點、路由規則、審核人角色等。

**API 端點：**
```
POST /api/v1/workflows/definitions
```

**前置條件：**
- 執行者必須擁有 `workflow:definition:manage` 權限
- 流程類型（flowType）必須唯一

**輸入 (Request)：**
```json
{
  "flowName": "請假審核流程",
  "flowType": "LEAVE_APPROVAL",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "START",
      "nodeName": "開始"
    },
    {
      "nodeId": "node-1",
      "nodeType": "APPROVAL",
      "nodeName": "直屬主管審核",
      "assigneeRole": "DIRECT_MANAGER",
      "dueDays": 2
    },
    {
      "nodeId": "node-2",
      "nodeType": "APPROVAL",
      "nodeName": "部門經理審核",
      "assigneeRole": "DEPARTMENT_MANAGER",
      "dueDays": 1,
      "condition": "totalDays > 3"
    },
    {
      "nodeId": "end",
      "nodeType": "END",
      "nodeName": "結束"
    }
  ],
  "edges": [
    {"from": "start", "to": "node-1"},
    {"from": "node-1", "to": "node-2"},
    {"from": "node-2", "to": "end"}
  ]
}
```

**業務規則驗證：**

1. ✅ **流程類型唯一性檢查**
   - 查詢條件：`flow_type = ?`
   - 預期結果：不存在重複的 flowType

2. ✅ **流程定義結構驗證**
   - 規則：必須包含 START 節點和 END 節點
   - 規則：每個節點的 nodeId 必須唯一
   - 規則：edges 中的 from/to 必須對應到實際的 nodeId

3. ✅ **審核節點驗證**
   - 規則：APPROVAL 節點必須指定 assigneeRole
   - 規則：dueDays 必須 > 0

4. ✅ **流程名稱必填檢查**
   - 規則：flowName 不可為空

**必須發布的領域事件：**
```json
{
  "eventType": "WorkflowDefinitionCreatedEvent",
  "aggregateId": "def-001",
  "timestamp": "2026-02-09T09:00:00Z",
  "payload": {
    "definitionId": "def-001",
    "flowName": "請假審核流程",
    "flowType": "LEAVE_APPROVAL",
    "version": 1,
    "totalNodes": 4,
    "isActive": true,
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "definitionId": "def-001",
    "flowName": "請假審核流程",
    "flowType": "LEAVE_APPROVAL",
    "version": 1,
    "isActive": true,
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

---

### 1.2 流程實例管理 Command

#### WFL_CMD_002: 啟動流程實例

**業務場景描述：**
業務服務（如 Attendance Service）調用此 API 啟動審核流程，系統建立流程實例並指派第一個審核任務。

**API 端點：**
```
POST /api/v1/workflows/start
```

**前置條件：**
- 執行者必須為業務服務或擁有 `workflow:instance:start` 權限
- flowType 必須存在且為 ACTIVE 狀態
- businessId 不可重複啟動（同一業務 ID 只能有一個進行中的流程）

**輸入 (Request)：**
```json
{
  "flowType": "LEAVE_APPROVAL",
  "businessType": "LEAVE_APPLICATION",
  "businessId": "leave-001",
  "applicantId": "emp-001",
  "variables": {
    "leaveType": "ANNUAL",
    "totalDays": 5,
    "startDate": "2026-03-01",
    "endDate": "2026-03-05"
  },
  "businessUrl": "/attendance/leave/applications/leave-001"
}
```

**業務規則驗證：**

1. ✅ **流程定義存在性檢查**
   - 查詢條件：`flow_type = ? AND is_active = TRUE`
   - 預期結果：流程定義存在且為 ACTIVE 狀態

2. ✅ **重複啟動檢查**
   - 查詢條件：`business_type = ? AND business_id = ? AND status = 'RUNNING'`
   - 預期結果：不存在進行中的流程實例

3. ✅ **申請人存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

4. ✅ **審核人解析**
   - 規則：根據 assigneeRole 和 applicantId 解析實際的審核人
   - 例：DIRECT_MANAGER → 查詢申請人的直屬主管

**必須發布的領域事件：**
```json
{
  "eventType": "WorkflowInstanceStartedEvent",
  "aggregateId": "instance-001",
  "timestamp": "2026-02-10T10:00:00Z",
  "payload": {
    "instanceId": "instance-001",
    "definitionId": "def-001",
    "flowType": "LEAVE_APPROVAL",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-001",
    "applicantId": "emp-001",
    "applicantName": "張三",
    "currentNode": "node-1",
    "status": "RUNNING",
    "startedAt": "2026-02-10T10:00:00Z"
  }
}
```

**必須發布的第二個事件（TaskAssignedEvent）：**
```json
{
  "eventType": "TaskAssignedEvent",
  "aggregateId": "task-001",
  "timestamp": "2026-02-10T10:00:01Z",
  "payload": {
    "taskId": "task-001",
    "instanceId": "instance-001",
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
    "taskSummary": "員工張三申請年假5天",
    "dueDate": "2026-02-12T10:00:00Z",
    "businessUrl": "/attendance/leave/applications/leave-001"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "instanceId": "instance-001",
    "status": "RUNNING",
    "currentNode": "node-1",
    "currentAssignee": "mgr-001",
    "startedAt": "2026-02-10T10:00:00Z"
  }
}
```

---

### 1.3 審核任務管理 Command

#### WFL_CMD_003: 核准任務

**業務場景描述：**
審核人核准審核任務，系統推進流程到下一個節點或完成流程。

**API 端點：**
```
PUT /api/v1/workflows/tasks/{id}/approve
```

**前置條件：**
- 執行者必須為任務的指派人（assignee）或代理人（delegatee）
- 任務必須存在且狀態為 PENDING

**輸入 (Request)：**
```json
{
  "comments": "同意請假申請"
}
```

**業務規則驗證：**

1. ✅ **任務狀態檢查**
   - 查詢條件：`task_id = ? AND status = 'PENDING'`
   - 預期結果：任務存在且為 PENDING 狀態

2. ✅ **審核權限檢查**
   - 規則：執行者為 assignee 或 delegatee
   - 查詢條件：`task_id = ? AND (assignee_id = ? OR delegated_to = ?)`

3. ✅ **流程實例狀態檢查**
   - 查詢條件：`instance_id = ? AND status = 'RUNNING'`
   - 預期結果：流程實例為 RUNNING 狀態

**必須發布的領域事件：**

**情況 1：還有後續節點（TaskApprovedEvent）**
```json
{
  "eventType": "TaskApprovedEvent",
  "aggregateId": "task-001",
  "timestamp": "2026-02-10T14:00:00Z",
  "payload": {
    "taskId": "task-001",
    "instanceId": "instance-001",
    "approverId": "mgr-001",
    "approverName": "李經理",
    "comments": "同意請假申請",
    "approvedAt": "2026-02-10T14:00:00Z"
  }
}
```

**接著發布 TaskAssignedEvent 給下一個審核人**

**情況 2：流程完成（ApprovalCompletedEvent）**
```json
{
  "eventType": "ApprovalCompletedEvent",
  "aggregateId": "instance-001",
  "timestamp": "2026-02-10T15:30:00Z",
  "payload": {
    "instanceId": "instance-001",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-001",
    "applicantId": "emp-001",
    "applicantName": "張三",
    "finalApprover": {
      "employeeId": "mgr-002",
      "fullName": "王總監"
    },
    "approvalPath": [
      {
        "nodeId": "node-1",
        "approver": "李經理",
        "approvedAt": "2026-02-10T14:00:00Z"
      },
      {
        "nodeId": "node-2",
        "approver": "王總監",
        "approvedAt": "2026-02-10T15:30:00Z"
      }
    ],
    "totalDuration": "5.5h",
    "completedAt": "2026-02-10T15:30:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "status": "APPROVED",
    "instanceStatus": "RUNNING",
    "nextNode": "node-2",
    "approvedAt": "2026-02-10T14:00:00Z"
  }
}
```

---

#### WFL_CMD_004: 駁回任務

**業務場景描述：**
審核人駁回審核任務，系統終止流程並通知申請人。

**API 端點：**
```
PUT /api/v1/workflows/tasks/{id}/reject
```

**前置條件：**
- 執行者必須為任務的指派人（assignee）或代理人（delegatee）
- 任務必須存在且狀態為 PENDING

**輸入 (Request)：**
```json
{
  "reason": "請假日期與重要專案衝突"
}
```

**業務規則驗證：**

1. ✅ **任務狀態檢查**
   - 查詢條件：`task_id = ? AND status = 'PENDING'`
   - 預期結果：任務存在且為 PENDING 狀態

2. ✅ **審核權限檢查**
   - 規則：執行者為 assignee 或 delegatee

3. ✅ **駁回原因必填檢查**
   - 規則：reason 不可為空

**必須發布的領域事件：**
```json
{
  "eventType": "ApprovalRejectedEvent",
  "aggregateId": "instance-002",
  "timestamp": "2026-02-10T12:00:00Z",
  "payload": {
    "instanceId": "instance-002",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-002",
    "applicantId": "emp-003",
    "applicantName": "王五",
    "rejectedBy": {
      "employeeId": "mgr-001",
      "fullName": "李經理"
    },
    "rejectedAt": "2026-02-10T12:00:00Z",
    "rejectionReason": "請假日期與重要專案衝突",
    "nodeId": "node-1"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "taskId": "task-002",
    "status": "REJECTED",
    "instanceStatus": "REJECTED",
    "rejectedAt": "2026-02-10T12:00:00Z"
  }
}
```

---

#### WFL_CMD_005: 設定審核代理人

**業務場景描述：**
員工設定審核代理人，在特定期間（如休假）由代理人處理審核任務。

**API 端點：**
```
POST /api/v1/workflows/delegations
```

**前置條件：**
- 執行者只能設定自己的代理人
- delegateeId 必須存在於 Organization Service

**輸入 (Request)：**
```json
{
  "delegatorId": "mgr-001",
  "delegateeId": "mgr-002",
  "startDate": "2026-03-01",
  "endDate": "2026-03-10"
}
```

**業務規則驗證：**

1. ✅ **代理人存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

2. ✅ **日期合理性檢查**
   - 規則：結束日期不可早於開始日期
   - 規則：開始日期不可為過去日期

3. ✅ **重複代理檢查**
   - 查詢條件：`delegator_id = ? AND start_date <= ? AND end_date >= ? AND is_active = TRUE`
   - 預期結果：不存在重疊的代理設定

**必須發布的領域事件：**
```json
{
  "eventType": "DelegationCreatedEvent",
  "aggregateId": "delegation-001",
  "timestamp": "2026-02-09T10:00:00Z",
  "payload": {
    "delegationId": "delegation-001",
    "delegatorId": "mgr-001",
    "delegatorName": "李經理",
    "delegateeId": "mgr-002",
    "delegateeName": "王經理",
    "startDate": "2026-03-01",
    "endDate": "2026-03-10",
    "createdAt": "2026-02-09T10:00:00Z"
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 流程定義查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| WFL_QRY_001 | 查詢啟用流程定義 | HR | `GET /api/v1/workflows/definitions` | `{"isActive":true}` | `is_active = TRUE` |
| WFL_QRY_002 | 查詢特定流程類型 | SYSTEM | `GET /api/v1/workflows/definitions` | `{"flowType":"LEAVE_APPROVAL"}` | `flow_type = 'LEAVE_APPROVAL'` |
| WFL_QRY_003 | 查詢所有流程定義 | HR | `GET /api/v1/workflows/definitions` | `{}` | 無過濾條件 |

#### 2.1.2 業務場景說明

**WFL_QRY_001: 查詢啟用流程定義**

- **使用者：** HR 管理員
- **業務目的：** 查詢目前啟用的所有流程定義
- **權限控制：** `workflow:definition:read`
- **過濾邏輯：**
  ```sql
  WHERE is_active = TRUE
  ORDER BY created_at DESC
  ```

---

### 2.2 流程實例查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| WFL_QRY_004 | 查詢流程實例 | HR | `GET /api/v1/workflows/instances` | `{"instanceId":"instance-001"}` | `instance_id = 'instance-001'` |
| WFL_QRY_005 | 查詢業務流程實例 | SYSTEM | `GET /api/v1/workflows/instances` | `{"businessType":"LEAVE_APPLICATION","businessId":"leave-001"}` | `business_type = 'LEAVE_APPLICATION'`, `business_id = 'leave-001'` |
| WFL_QRY_006 | 查詢申請人的流程 | EMPLOYEE | `GET /api/v1/workflows/my-applications` | `{}` | `applicant_id = '{currentUserId}'` |
| WFL_QRY_007 | 查詢進行中的流程 | HR | `GET /api/v1/workflows/instances` | `{"status":"RUNNING"}` | `status = 'RUNNING'` |

#### 2.2.2 業務場景說明

**WFL_QRY_006: 查詢申請人的流程**

- **使用者：** 一般員工
- **業務目的：** 員工查詢自己提交的所有審核申請
- **權限控制：** 無需特殊權限，但只能查詢自己
- **過濾邏輯：**
  ```sql
  WHERE applicant_id = '{currentUserId}'
  ORDER BY started_at DESC
  ```

---

### 2.3 審核任務查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| WFL_QRY_008 | 查詢待辦任務 | EMPLOYEE | `GET /api/v1/workflows/tasks/pending` | `{}` | `assignee_id = '{currentUserId}'`, `status = 'PENDING'` |
| WFL_QRY_009 | 查詢已完成任務 | EMPLOYEE | `GET /api/v1/workflows/tasks/completed` | `{}` | `assignee_id = '{currentUserId}'`, `status IN ('APPROVED', 'REJECTED')` |
| WFL_QRY_010 | 查詢逾期任務 | MANAGER | `GET /api/v1/workflows/tasks/overdue` | `{}` | `assignee_id = '{currentUserId}'`, `status = 'PENDING'`, `is_overdue = TRUE` |
| WFL_QRY_011 | 查詢流程的所有任務 | HR | `GET /api/v1/workflows/tasks` | `{"instanceId":"instance-001"}` | `instance_id = 'instance-001'` |

#### 2.3.2 業務場景說明

**WFL_QRY_008: 查詢待辦任務**

- **使用者：** 一般員工/主管
- **業務目的：** 查詢需要自己審核的待辦任務
- **權限控制：** 無需特殊權限
- **過濾邏輯：**
  ```sql
  WHERE (assignee_id = '{currentUserId}' OR delegated_to = '{currentUserId}')
    AND status = 'PENDING'
  ORDER BY due_date ASC
  ```

**WFL_QRY_010: 查詢逾期任務**

- **使用者：** 主管
- **業務目的：** 查詢已逾期的審核任務
- **權限控制：** 無需特殊權限
- **過濾邏輯：**
  ```sql
  WHERE assignee_id = '{currentUserId}'
    AND status = 'PENDING'
    AND is_overdue = TRUE
  ORDER BY due_date ASC
  ```

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `WorkflowInstanceStartedEvent` | 啟動流程實例 | Workflow | - | 記錄流程開始 |
| `TaskAssignedEvent` | 分配任務給審核人 | Workflow | Notification | 通知審核人 |
| `ApprovalCompletedEvent` | 流程核准完成 | Workflow | 業務服務 (Attendance 等) | 更新業務狀態 |
| `ApprovalRejectedEvent` | 流程駁回 | Workflow | Notification, 業務服務 | 通知申請人，更新業務狀態 |
| `TaskOverdueEvent` | 任務逾期 | Workflow | Notification | 提醒審核人 |

---

### 3.2 TaskAssignedEvent (任務指派事件)

**觸發時機：**
流程啟動或前一個審核節點完成後，系統指派新的審核任務給下一個審核人時發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-wkf-task-001",
  "eventType": "TaskAssignedEvent",
  "timestamp": "2026-02-10T10:00:00Z",
  "aggregateId": "task-001",
  "aggregateType": "ApprovalTask",
  "payload": {
    "taskId": "task-001",
    "instanceId": "instance-001",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-001",
    "applicant": {
      "employeeId": "emp-001",
      "fullName": "張三",
      "email": "zhang@company.com"
    },
    "assignee": {
      "employeeId": "mgr-001",
      "fullName": "李經理",
      "email": "lee@company.com"
    },
    "nodeId": "node-1",
    "taskSummary": "員工張三申請年假5天 (2026-03-01 ~ 2026-03-05)",
    "dueDate": "2026-02-12T10:00:00Z",
    "businessUrl": "/attendance/leave/applications/leave-001"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送 Email 給李經理：「您有一筆審核任務：員工張三申請年假5天」
  - 發送站內通知
  - 在到期日前 1 天發送提醒

---

### 3.3 ApprovalCompletedEvent (審核完成事件)

**觸發時機：**
流程的所有審核節點都通過後，系統完成流程時發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-wkf-complete-001",
  "eventType": "ApprovalCompletedEvent",
  "timestamp": "2026-02-10T15:30:00Z",
  "aggregateId": "instance-001",
  "aggregateType": "WorkflowInstance",
  "payload": {
    "instanceId": "instance-001",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-001",
    "applicantId": "emp-001",
    "applicantName": "張三",
    "applicantEmail": "zhang@company.com",
    "finalApprover": {
      "employeeId": "mgr-002",
      "fullName": "王總監"
    },
    "approvalPath": [
      {
        "nodeId": "node-1",
        "nodeName": "直屬主管審核",
        "approver": "李經理",
        "approverId": "mgr-001",
        "approvedAt": "2026-02-10T14:00:00Z",
        "comments": "同意請假申請"
      },
      {
        "nodeId": "node-2",
        "nodeName": "部門經理審核",
        "approver": "王總監",
        "approverId": "mgr-002",
        "approvedAt": "2026-02-10T15:30:00Z",
        "comments": "核准"
      }
    ],
    "totalDuration": "5.5h",
    "completedAt": "2026-02-10T15:30:00Z"
  }
}
```

**訂閱服務處理：**

- **Attendance Service:**
  - 更新請假申請狀態為 APPROVED
  - 計算可休假天數扣除

- **Notification Service:**
  - 發送 Email 給申請人：「您的請假申請已核准」
  - 發送站內通知

---

### 3.4 ApprovalRejectedEvent (審核駁回事件)

**觸發時機：**
任何審核節點駁回時，系統終止流程並發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-wkf-reject-001",
  "eventType": "ApprovalRejectedEvent",
  "timestamp": "2026-02-10T12:00:00Z",
  "aggregateId": "instance-002",
  "aggregateType": "WorkflowInstance",
  "payload": {
    "instanceId": "instance-002",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-002",
    "applicantId": "emp-003",
    "applicantName": "王五",
    "applicantEmail": "wang@company.com",
    "rejectedBy": {
      "employeeId": "mgr-001",
      "fullName": "李經理"
    },
    "rejectedAt": "2026-02-10T12:00:00Z",
    "rejectionReason": "請假日期與重要專案衝突，建議調整時間",
    "nodeId": "node-1",
    "nodeName": "直屬主管審核"
  }
}
```

**訂閱服務處理：**

- **Attendance Service:**
  - 更新請假申請狀態為 REJECTED

- **Notification Service:**
  - 發送 Email 給申請人：「您的請假申請已駁回，原因：請假日期與重要專案衝突」
  - 發送站內通知

---

### 3.5 TaskOverdueEvent (任務逾期事件)

**觸發時機：**
系統排程檢查到審核任務超過到期日仍未處理時發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-wkf-overdue-001",
  "eventType": "TaskOverdueEvent",
  "timestamp": "2026-02-12T10:00:00Z",
  "aggregateId": "task-003",
  "aggregateType": "ApprovalTask",
  "payload": {
    "taskId": "task-003",
    "instanceId": "instance-003",
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-003",
    "assignee": {
      "employeeId": "mgr-001",
      "fullName": "李經理",
      "email": "lee@company.com"
    },
    "dueDate": "2026-02-12T10:00:00Z",
    "overdueHours": 2,
    "applicant": {
      "employeeId": "emp-005",
      "fullName": "趙六"
    },
    "taskSummary": "員工趙六申請年假3天"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送 Email 給審核人：「您有一筆審核任務已逾期 2 小時」
  - 發送 Email 給審核人的主管：「李經理有逾期的審核任務」

---

## 4. 測試斷言規格

### 4.1 Command 操作測試斷言

**測試目標：** 驗證 Command 操作是否正確執行業務規則並發布領域事件。

**範例：WFL_CMD_002 啟動流程測試**

```java
@Test
@DisplayName("WFL_CMD_002: 啟動流程實例 - 應建立實例和第一個任務並發布事件")
void startWorkflow_ShouldCreateInstanceAndPublishEvents() {
    // Given
    var request = StartWorkflowRequest.builder()
        .flowType("LEAVE_APPROVAL")
        .businessType("LEAVE_APPLICATION")
        .businessId("leave-001")
        .applicantId("emp-001")
        .variables(Map.of("leaveType", "ANNUAL", "totalDays", 5))
        .build();

    // Mock flow definition exists
    when(definitionRepository.findByFlowType("LEAVE_APPROVAL"))
        .thenReturn(Optional.of(createLeaveApprovalDefinition()));

    // Mock no duplicate instance
    when(instanceRepository.findRunningInstance("LEAVE_APPLICATION", "leave-001"))
        .thenReturn(Optional.empty());

    // Mock assignee resolution
    when(organizationService.getDirectManager("emp-001"))
        .thenReturn("mgr-001");

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify instance saved
    var instanceCaptor = ArgumentCaptor.forClass(WorkflowInstance.class);
    verify(instanceRepository).save(instanceCaptor.capture());

    var instance = instanceCaptor.getValue();
    assertThat(instance.getStatus()).isEqualTo(InstanceStatus.RUNNING);
    assertThat(instance.getCurrentNode()).isEqualTo("node-1");

    // Then - Verify task created
    var taskCaptor = ArgumentCaptor.forClass(ApprovalTask.class);
    verify(taskRepository).save(taskCaptor.capture());

    var task = taskCaptor.getValue();
    assertThat(task.getAssigneeId()).isEqualTo("mgr-001");
    assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);

    // Then - Verify events published
    verify(eventPublisher, times(2)).publish(any());
    verify(eventPublisher).publish(argThat(event ->
        event instanceof WorkflowInstanceStartedEvent));
    verify(eventPublisher).publish(argThat(event ->
        event instanceof TaskAssignedEvent));
}
```

---

### 4.2 Query 操作測試斷言

**範例：WFL_QRY_008 查詢待辦任務測試**

```java
@Test
@DisplayName("WFL_QRY_008: 查詢待辦任務 - 應包含 assignee 和 status 過濾")
void searchPendingTasks_ShouldIncludeRequiredFilters() {
    // Given
    String contractSpec = loadContractSpec("workflow");

    var request = TaskSearchRequest.builder().build();

    // When
    var captor = ArgumentCaptor.forClass(QueryGroup.class);
    service.getResponse(request, currentUser);

    // Then
    verify(taskRepository).findPage(captor.capture(), any());

    var queryGroup = captor.getValue();
    assertContract(queryGroup, contractSpec, "WFL_QRY_008");

    // Additional assertions
    assertThat(queryGroup).containsFilter("assignee_id", Operator.EQUAL, currentUser.getUserId());
    assertThat(queryGroup).containsFilter("status", Operator.EQUAL, "PENDING");
}
```

---

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 流程定義使用 `is_active` 欄位
   - 流程實例使用 `status` 欄位
   - 審核任務使用 `status` 欄位
   - **不使用 `is_deleted` 欄位**

2. **權限控制:**
   - 審核人只能查詢和處理指派給自己的任務
   - 代理人可以處理委託給自己的任務
   - HR 管理員可以查詢所有流程和任務

3. **租戶隔離:**
   - 所有查詢自動加上 `tenant_id = ?` 過濾條件

---

### 5.2 審核人解析規則

- **DIRECT_MANAGER:** 申請人的直屬主管（Organization Service 提供）
- **DEPARTMENT_MANAGER:** 申請人部門的主管
- **HR:** HR 部門的員工
- **SPECIFIC_ROLE:** 特定角色的員工

---

### 5.3 逾期檢查排程

- **執行頻率：** 每小時執行
- **檢查範圍：** 所有 PENDING 狀態且超過 due_date 的任務
- **處理動作：** 標記 is_overdue = TRUE 並發布 TaskOverdueEvent

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景、業務規則驗證、Domain Events Payload 定義、測試斷言規格 |
| 1.0 | 2026-02-06 | 精簡版建立 |
