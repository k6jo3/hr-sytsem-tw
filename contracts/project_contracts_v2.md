# 專案管理服務業務合約 (Project Service Business Contract)

> **服務代碼:** HR06
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/06_專案管理服務系統設計書.md`
> - `knowledge/04_API_Specifications/06_專案管理服務系統設計書_API詳細規格.md`

---

## 📋 概述

本合約文件定義專案管理服務的**完整業務場景**，包括：
1. **Command 操作場景**（建立、更新、刪除）- 驗證業務規則與領域事件
2. **Query 操作場景**（查詢）- 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景
- ✅ 新增 6 個領域事件的詳細定義
- ✅ 對應到實際的 API 端點
- ✅ 使用實際存在的欄位（移除 is_deleted，使用 status）
- ✅ 包含完整的業務規則驗證

**服務定位：**
專案管理服務負責客戶管理、專案建立與維護、多層級工項管理(WBS)及專案成本追蹤。這是軟體公司專案成本核算的關鍵服務，需與工時服務緊密整合。

**資料軟刪除策略：**
- **專案**: 使用 `status` 欄位，'PLANNING' | 'IN_PROGRESS' | 'COMPLETED' | 'ON_HOLD' | 'CANCELLED'
- **客戶**: 使用 `status` 欄位，'ACTIVE' | 'INACTIVE'
- **工項 (Tasks)**: 使用 `status` 欄位，'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'BLOCKED'
- **專案成員**: 使用 `leave_date` 欄位，NULL 表示仍在專案中
- **歷史記錄**: 不進行軟刪除，保留所有歷史記錄（用於成本追蹤與稽核）

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [客戶管理 Command](#11-客戶管理-command)
   - 1.2 [專案管理 Command](#12-專案管理-command)
   - 1.3 [專案成員管理 Command](#13-專案成員管理-command)
   - 1.4 [工項管理 Command](#14-工項管理-command)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [專案查詢](#21-專案查詢)
   - 2.2 [客戶查詢](#22-客戶查詢)
   - 2.3 [WBS 查詢](#23-wbs-查詢)
   - 2.4 [成本分析查詢](#24-成本分析查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 客戶管理 Command

#### PRJ_CMD_001: 建立客戶

**業務場景描述：**
PM 或業務人員建立新客戶資料，系統自動生成客戶代碼，並儲存客戶基本資料與聯絡人資訊。

**API 端點：**
```
POST /api/v1/customers
```

**前置條件：**
- 執行者必須擁有 `customer:manage` 權限
- 客戶名稱不可重複

**輸入 (Request)：**
```json
{
  "customerName": "ABC科技股份有限公司",
  "taxId": "12345678",
  "industry": "軟體開發",
  "address": "台北市信義區信義路五段7號",
  "phoneNumber": "02-12345678",
  "email": "contact@abc.com",
  "contacts": [
    {
      "name": "王小明",
      "title": "專案經理",
      "phone": "0912-345-678",
      "email": "ming@abc.com"
    }
  ]
}
```

**業務規則驗證：**

1. ✅ **客戶名稱唯一性檢查**
   - 查詢條件：`customer_name = ? AND status = 'ACTIVE'`
   - 預期結果：不存在重複
   - 錯誤訊息：`CUSTOMER_NAME_DUPLICATED`

2. ✅ **統一編號格式檢查**
   - 規則：8 碼數字
   - 錯誤訊息：`INVALID_TAX_ID_FORMAT`

3. ✅ **Email 格式檢查**
   - 規則：符合 Email 規範格式
   - 錯誤訊息：`INVALID_EMAIL_FORMAT`

4. ✅ **客戶代碼自動生成**
   - 規則：`CUST-YYYYMMDD-XXX` 格式
   - 例：`CUST-20260209-001`

**必須發布的領域事件：**
```json
{
  "eventType": "CustomerCreatedEvent",
  "aggregateId": "customer-001",
  "timestamp": "2026-02-09T09:00:00Z",
  "payload": {
    "customerId": "customer-001",
    "customerCode": "CUST-20260209-001",
    "customerName": "ABC科技股份有限公司",
    "taxId": "12345678",
    "industry": "軟體開發",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "customerId": "customer-001",
    "customerCode": "CUST-20260209-001",
    "customerName": "ABC科技股份有限公司",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

---

#### PRJ_CMD_002: 更新客戶

**業務場景描述：**
PM 或業務人員更新客戶資料，例如變更聯絡人或地址。

**API 端點：**
```
PUT /api/v1/customers/{id}
```

**前置條件：**
- 執行者必須擁有 `customer:manage` 權限
- 客戶必須存在且狀態為 ACTIVE

**輸入 (Request)：**
```json
{
  "customerName": "ABC科技股份有限公司",
  "address": "台北市信義區信義路五段8號",
  "phoneNumber": "02-87654321",
  "email": "new-contact@abc.com",
  "contacts": [
    {
      "name": "李小華",
      "title": "專案經理",
      "phone": "0912-345-999",
      "email": "hua@abc.com"
    }
  ]
}
```

**業務規則驗證：**

1. ✅ **客戶存在性檢查**
   - 查詢條件：`customer_id = ? AND status = 'ACTIVE'`
   - 預期結果：客戶存在

2. ✅ **客戶名稱唯一性檢查**（如果名稱有變更）
   - 查詢條件：`customer_name = ? AND customer_id != ? AND status = 'ACTIVE'`
   - 預期結果：不存在重複

3. ✅ **Email 格式檢查**
   - 規則：符合 Email 規範格式

**必須發布的領域事件：**
```json
{
  "eventType": "CustomerUpdatedEvent",
  "aggregateId": "customer-001",
  "timestamp": "2026-02-09T10:00:00Z",
  "payload": {
    "customerId": "customer-001",
    "customerName": "ABC科技股份有限公司",
    "updatedAt": "2026-02-09T10:00:00Z"
  }
}
```

---

### 1.2 專案管理 Command

#### PRJ_CMD_003: 建立專案

**業務場景描述：**
PM 建立新專案，設定專案基本資料、時程、預算、類型，並指派專案經理與初始團隊成員。

**API 端點：**
```
POST /api/v1/projects
```

**前置條件：**
- 執行者必須擁有 `project:manage` 權限
- customerId 必須存在
- projectManager 必須存在於 Organization Service

**輸入 (Request)：**
```json
{
  "projectName": "ERP系統開發專案",
  "customerId": "customer-001",
  "projectType": "DEVELOPMENT",
  "plannedStartDate": "2026-03-01",
  "plannedEndDate": "2026-12-31",
  "budgetType": "FIXED_PRICE",
  "budgetAmount": 5000000,
  "budgetHours": 2000,
  "projectManager": "emp-001",
  "description": "開發客戶ERP系統",
  "members": [
    {
      "employeeId": "emp-002",
      "role": "DEVELOPER",
      "allocatedHours": 800
    },
    {
      "employeeId": "emp-003",
      "role": "QA",
      "allocatedHours": 400
    }
  ]
}
```

**業務規則驗證：**

1. ✅ **客戶存在性檢查**
   - 呼叫內部查詢：`customer_id = ? AND status = 'ACTIVE'`
   - 預期結果：客戶存在且為 ACTIVE 狀態

2. ✅ **專案經理權限檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

3. ✅ **專案代碼自動生成**
   - 規則：`PRJ-YYYYMMDD-XXX` 格式
   - 例：`PRJ-20260209-001`

4. ✅ **日期合理性檢查**
   - 規則：`planned_end_date >= planned_start_date`
   - 錯誤訊息：`INVALID_PROJECT_DATE_RANGE`

5. ✅ **預算類型與金額一致性檢查**
   - 規則：FIXED_PRICE 必須有 budgetAmount
   - 規則：TIME_AND_MATERIAL 必須有 budgetHours
   - 錯誤訊息：`BUDGET_TYPE_AMOUNT_MISMATCH`

6. ✅ **成員重複檢查**
   - 規則：同一員工不可重複加入
   - 錯誤訊息：`DUPLICATE_PROJECT_MEMBER`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-prj-create-001",
  "eventType": "ProjectCreatedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "project-001",
  "payload": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "customerId": "customer-001",
    "customerName": "ABC科技股份有限公司",
    "projectType": "DEVELOPMENT",
    "budgetType": "FIXED_PRICE",
    "budgetAmount": 5000000,
    "budgetHours": 2000,
    "projectManager": "emp-001",
    "projectManagerName": "王大明",
    "plannedStartDate": "2026-03-01",
    "plannedEndDate": "2026-12-31",
    "status": "PLANNING",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "status": "PLANNING",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

---

#### PRJ_CMD_004: 開始專案

**業務場景描述：**
專案規劃完成後，PM 正式啟動專案，系統記錄實際開始日期並變更專案狀態。

**API 端點：**
```
PUT /api/v1/projects/{id}/start
```

**前置條件：**
- 執行者必須擁有 `project:manage` 權限
- 專案必須存在且狀態為 PLANNING

**輸入 (Request)：**
```json
{
  "actualStartDate": "2026-03-01"
}
```

**業務規則驗證：**

1. ✅ **專案狀態檢查**
   - 查詢條件：`project_id = ? AND status = 'PLANNING'`
   - 預期結果：專案存在且狀態為 PLANNING
   - 錯誤訊息：`PROJECT_NOT_IN_PLANNING_STATUS`

2. ✅ **開始日期合理性檢查**
   - 規則：`actual_start_date >= planned_start_date`
   - 錯誤訊息：`START_DATE_BEFORE_PLANNED_DATE`

3. ✅ **專案團隊完整性檢查**
   - 規則：至少要有 1 名成員（除了 PM）
   - 錯誤訊息：`PROJECT_TEAM_INCOMPLETE`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-prj-start-001",
  "eventType": "ProjectStartedEvent",
  "timestamp": "2026-02-09T09:30:00Z",
  "aggregateId": "project-001",
  "payload": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "projectManager": "emp-001",
    "actualStartDate": "2026-03-01",
    "status": "IN_PROGRESS",
    "teamMembers": [
      {
        "employeeId": "emp-002",
        "role": "DEVELOPER"
      },
      {
        "employeeId": "emp-003",
        "role": "QA"
      }
    ]
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "projectId": "project-001",
    "status": "IN_PROGRESS",
    "actualStartDate": "2026-03-01",
    "updatedAt": "2026-02-09T09:30:00Z"
  }
}
```

---

#### PRJ_CMD_005: 完成專案（結案）

**業務場景描述：**
專案完成後，PM 執行結案作業，系統記錄實際完成日期、計算最終成本，並發布專案完成事件。

**API 端點：**
```
PUT /api/v1/projects/{id}/complete
```

**前置條件：**
- 執行者必須擁有 `project:manage` 權限
- 專案必須存在且狀態為 IN_PROGRESS

**輸入 (Request)：**
```json
{
  "actualEndDate": "2026-12-31",
  "completionNotes": "專案如期完成，客戶驗收通過"
}
```

**業務規則驗證：**

1. ✅ **專案狀態檢查**
   - 查詢條件：`project_id = ? AND status = 'IN_PROGRESS'`
   - 預期結果：專案存在且狀態為 IN_PROGRESS
   - 錯誤訊息：`PROJECT_NOT_IN_PROGRESS`

2. ✅ **完成日期合理性檢查**
   - 規則：`actual_end_date >= actual_start_date`
   - 錯誤訊息：`END_DATE_BEFORE_START_DATE`

3. ✅ **所有工項完成檢查**
   - 查詢條件：`project_id = ? AND status != 'COMPLETED'`
   - 預期結果：不存在未完成的工項
   - 錯誤訊息：`INCOMPLETE_TASKS_EXIST`

4. ✅ **計算最終成本**
   - 規則：彙總所有已審核工時記錄
   - 公式：`SUM(hours × hourly_rate)`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-prj-complete-001",
  "eventType": "ProjectCompletedEvent",
  "timestamp": "2026-12-31T18:00:00Z",
  "aggregateId": "project-001",
  "payload": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "customerId": "customer-001",
    "projectManager": "emp-001",
    "plannedStartDate": "2026-03-01",
    "plannedEndDate": "2026-12-31",
    "actualStartDate": "2026-03-01",
    "actualEndDate": "2026-12-31",
    "budgetAmount": 5000000,
    "budgetHours": 2000,
    "actualHours": 1950,
    "actualCost": 4850000,
    "budgetUtilization": 97.0,
    "status": "COMPLETED",
    "completionNotes": "專案如期完成，客戶驗收通過"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "projectId": "project-001",
    "status": "COMPLETED",
    "actualEndDate": "2026-12-31",
    "actualHours": 1950,
    "actualCost": 4850000,
    "budgetUtilization": 97.0,
    "updatedAt": "2026-12-31T18:00:00Z"
  }
}
```

---

### 1.3 專案成員管理 Command

#### PRJ_CMD_006: 新增專案成員

**業務場景描述：**
PM 新增成員至專案團隊，設定成員角色與預計工時，系統通知該成員並更新 Timesheet Service。

**API 端點：**
```
POST /api/v1/projects/{id}/members
```

**前置條件：**
- 執行者必須擁有 `project:member:manage` 權限
- 專案必須存在且狀態為 PLANNING 或 IN_PROGRESS
- employeeId 必須存在於 Organization Service

**輸入 (Request)：**
```json
{
  "employeeId": "emp-004",
  "role": "DEVELOPER",
  "allocatedHours": 500,
  "hourlyRate": 1500
}
```

**業務規則驗證：**

1. ✅ **專案狀態檢查**
   - 查詢條件：`project_id = ? AND status IN ('PLANNING', 'IN_PROGRESS')`
   - 預期結果：專案存在且狀態正確
   - 錯誤訊息：`PROJECT_NOT_ACTIVE`

2. ✅ **員工存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

3. ✅ **重複成員檢查**
   - 查詢條件：`project_id = ? AND employee_id = ? AND leave_date IS NULL`
   - 預期結果：不存在重複的有效成員
   - 錯誤訊息：`MEMBER_ALREADY_IN_PROJECT`

4. ✅ **分配工時合理性檢查**
   - 規則：`allocated_hours > 0`
   - 錯誤訊息：`INVALID_ALLOCATED_HOURS`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-prj-member-001",
  "eventType": "ProjectMemberAddedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "project-001",
  "payload": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "memberId": "member-004",
    "employeeId": "emp-004",
    "employeeName": "陳小美",
    "role": "DEVELOPER",
    "allocatedHours": 500,
    "hourlyRate": 1500,
    "joinDate": "2026-02-09"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "memberId": "member-004",
    "employeeId": "emp-004",
    "employeeName": "陳小美",
    "role": "DEVELOPER",
    "allocatedHours": 500,
    "joinDate": "2026-02-09"
  }
}
```

---

### 1.4 工項管理 Command

#### PRJ_CMD_007: 建立工項（WBS）

**業務場景描述：**
PM 建立專案工項（WBS），支援最多 5 層級的工作分解結構，系統自動計算層級並通知 Timesheet Service。

**API 端點：**
```
POST /api/v1/projects/{id}/tasks
```

**前置條件：**
- 執行者必須擁有 `project:task:manage` 權限
- 專案必須存在
- 如果有 parentTaskId，父工項必須存在且層級 < 5

**輸入 (Request)：**
```json
{
  "parentTaskId": null,
  "taskName": "需求分析階段",
  "description": "收集並分析客戶需求",
  "estimatedHours": 200,
  "plannedStartDate": "2026-03-01",
  "plannedEndDate": "2026-03-31",
  "assigneeId": "emp-002"
}
```

**業務規則驗證：**

1. ✅ **專案存在性檢查**
   - 查詢條件：`project_id = ?`
   - 預期結果：專案存在

2. ✅ **父工項檢查**（如果 parentTaskId 不為空）
   - 查詢條件：`task_id = ? AND project_id = ?`
   - 預期結果：父工項存在
   - 檢查父工項層級：`level < 5`
   - 錯誤訊息：`MAX_TASK_LEVEL_EXCEEDED`

3. ✅ **工項代碼自動生成**
   - 規則：`T-{層級}-{序號}` 格式
   - 例：`T-1-001`（第一層第一個工項）
   - 例：`T-2-003`（第二層第三個工項）

4. ✅ **日期合理性檢查**
   - 規則：`planned_end_date >= planned_start_date`
   - 規則：工項日期範圍必須在專案日期範圍內
   - 錯誤訊息：`INVALID_TASK_DATE_RANGE`

5. ✅ **預估工時檢查**
   - 規則：`estimated_hours > 0`
   - 錯誤訊息：`INVALID_ESTIMATED_HOURS`

6. ✅ **負責人檢查**（如果 assigneeId 不為空）
   - 查詢條件：`project_id = ? AND employee_id = ? AND leave_date IS NULL`
   - 預期結果：負責人必須是專案成員
   - 錯誤訊息：`ASSIGNEE_NOT_PROJECT_MEMBER`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-task-create-001",
  "eventType": "TaskCreatedEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "task-001",
  "payload": {
    "taskId": "task-001",
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "parentTaskId": null,
    "taskCode": "T-1-001",
    "taskName": "需求分析階段",
    "level": 1,
    "estimatedHours": 200,
    "plannedStartDate": "2026-03-01",
    "plannedEndDate": "2026-03-31",
    "assigneeId": "emp-002",
    "assigneeName": "李開發",
    "status": "NOT_STARTED",
    "createdAt": "2026-02-09T11:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "taskCode": "T-1-001",
    "taskName": "需求分析階段",
    "level": 1,
    "status": "NOT_STARTED",
    "createdAt": "2026-02-09T11:00:00Z"
  }
}
```

---

#### PRJ_CMD_008: 更新工項進度

**業務場景描述：**
PM 或工項負責人更新工項進度，系統記錄進度百分比並自動更新工項狀態。

**API 端點：**
```
PUT /api/v1/tasks/{id}/progress
```

**前置條件：**
- 執行者必須擁有 `project:task:manage` 權限或為工項負責人
- 工項必須存在

**輸入 (Request)：**
```json
{
  "progress": 50,
  "actualHours": 100,
  "notes": "需求分析已完成50%"
}
```

**業務規則驗證：**

1. ✅ **工項存在性檢查**
   - 查詢條件：`task_id = ?`
   - 預期結果：工項存在

2. ✅ **權限檢查**
   - 規則：執行者為工項負責人或擁有 `project:task:manage` 權限
   - 錯誤訊息：`INSUFFICIENT_PERMISSION`

3. ✅ **進度範圍檢查**
   - 規則：`0 <= progress <= 100`
   - 錯誤訊息：`INVALID_PROGRESS_VALUE`

4. ✅ **狀態自動更新**
   - 規則：
     - `progress = 0` → `status = 'NOT_STARTED'`
     - `0 < progress < 100` → `status = 'IN_PROGRESS'`
     - `progress = 100` → `status = 'COMPLETED'`

5. ✅ **實際工時累加**
   - 規則：累加到工項的 `actual_hours` 欄位

**必須發布的領域事件：**
```json
{
  "eventId": "evt-task-progress-001",
  "eventType": "TaskProgressUpdatedEvent",
  "timestamp": "2026-03-15T15:00:00Z",
  "aggregateId": "task-001",
  "payload": {
    "taskId": "task-001",
    "taskCode": "T-1-001",
    "taskName": "需求分析階段",
    "projectId": "project-001",
    "oldProgress": 0,
    "newProgress": 50,
    "status": "IN_PROGRESS",
    "estimatedHours": 200,
    "actualHours": 100,
    "notes": "需求分析已完成50%",
    "updatedBy": "emp-002",
    "updatedAt": "2026-03-15T15:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "progress": 50,
    "status": "IN_PROGRESS",
    "actualHours": 100,
    "updatedAt": "2026-03-15T15:00:00Z"
  }
}
```

---

#### PRJ_CMD_009: 完成工項

**業務場景描述：**
工項負責人完成工項後，系統自動記錄完成日期、計算實際工時，並檢查是否需要發布專案預算警示。

**API 端點：**
```
PUT /api/v1/tasks/{id}/complete
```

**前置條件：**
- 執行者必須擁有 `project:task:manage` 權限或為工項負責人
- 工項必須存在且狀態為 IN_PROGRESS

**輸入 (Request)：**
```json
{
  "actualEndDate": "2026-03-31",
  "actualHours": 200,
  "completionNotes": "需求分析完成，產出需求規格書"
}
```

**業務規則驗證：**

1. ✅ **工項狀態檢查**
   - 查詢條件：`task_id = ? AND status = 'IN_PROGRESS'`
   - 預期結果：工項存在且狀態為 IN_PROGRESS
   - 錯誤訊息：`TASK_NOT_IN_PROGRESS`

2. ✅ **完成日期合理性檢查**
   - 規則：`actual_end_date >= planned_start_date`
   - 錯誤訊息：`INVALID_COMPLETION_DATE`

3. ✅ **子工項完成檢查**（如果有子工項）
   - 查詢條件：`parent_task_id = ? AND status != 'COMPLETED'`
   - 預期結果：所有子工項都已完成
   - 錯誤訊息：`INCOMPLETE_SUBTASKS_EXIST`

4. ✅ **更新專案累計工時與成本**
   - 規則：將工項實際工時累加到專案 `actual_hours`

5. ✅ **預算警示檢查**
   - 規則：如果 `actual_hours / budget_hours >= 0.8`
   - 發布：`ProjectBudgetAlertEvent`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-task-complete-001",
  "eventType": "TaskCompletedEvent",
  "timestamp": "2026-03-31T18:00:00Z",
  "aggregateId": "task-001",
  "payload": {
    "taskId": "task-001",
    "taskCode": "T-1-001",
    "taskName": "需求分析階段",
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "assigneeId": "emp-002",
    "assigneeName": "李開發",
    "plannedStartDate": "2026-03-01",
    "plannedEndDate": "2026-03-31",
    "actualStartDate": "2026-03-01",
    "actualEndDate": "2026-03-31",
    "estimatedHours": 200,
    "actualHours": 200,
    "variance": 0,
    "status": "COMPLETED",
    "completionNotes": "需求分析完成，產出需求規格書"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "status": "COMPLETED",
    "actualEndDate": "2026-03-31",
    "actualHours": 200,
    "variance": 0,
    "updatedAt": "2026-03-31T18:00:00Z"
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 專案查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PRJ_QRY_P001 | 查詢進行中專案 | PM | `GET /api/v1/projects` | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'` |
| PRJ_QRY_P002 | 查詢已完成專案 | PM | `GET /api/v1/projects` | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| PRJ_QRY_P003 | 依客戶查詢專案 | PM | `GET /api/v1/projects` | `{"customerId":"C001"}` | `customer_id = 'C001'` |
| PRJ_QRY_P004 | 依 PM 查詢專案 | PM | `GET /api/v1/projects` | `{"pmId":"E001"}` | `project_manager = 'E001'` |
| PRJ_QRY_P005 | 員工查詢參與專案 | EMPLOYEE | `GET /api/v1/projects/my` | `{}` | `EXISTS (SELECT 1 FROM project_members WHERE project_id = projects.project_id AND employee_id = '{currentUserId}' AND leave_date IS NULL)` |
| PRJ_QRY_P006 | 依專案類型查詢 | PM | `GET /api/v1/projects` | `{"projectType":"DEVELOPMENT"}` | `project_type = 'DEVELOPMENT'` |
| PRJ_QRY_P007 | 依預算類型查詢 | PM | `GET /api/v1/projects` | `{"budgetType":"FIXED_PRICE"}` | `budget_type = 'FIXED_PRICE'` |

#### 2.1.2 業務場景說明

**PRJ_QRY_P001: 查詢進行中專案**

- **使用者：** PM 或專案管理人員
- **業務目的：** 查看所有正在進行的專案以進行追蹤管理
- **權限控制：** `project:read`
- **過濾邏輯：**
  ```sql
  WHERE status = 'IN_PROGRESS'
  ORDER BY actual_start_date DESC
  ```

**PRJ_QRY_P005: 員工查詢參與專案（ESS）**

- **使用者：** 一般員工
- **業務目的：** 員工自助查詢自己參與的所有專案
- **權限控制：** 無需特殊權限，但只能查詢自己
- **過濾邏輯：**
  ```sql
  WHERE EXISTS (
    SELECT 1 FROM project_members pm
    WHERE pm.project_id = projects.project_id
      AND pm.employee_id = '{currentUserId}'
      AND pm.leave_date IS NULL
  )
  ORDER BY projects.actual_start_date DESC
  ```

---

### 2.2 客戶查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PRJ_QRY_C001 | 查詢有效客戶 | PM | `GET /api/v1/customers` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| PRJ_QRY_C002 | 依名稱模糊查詢 | PM | `GET /api/v1/customers` | `{"keyword":"科技"}` | `customer_name LIKE '%科技%'` |
| PRJ_QRY_C003 | 依產業查詢 | PM | `GET /api/v1/customers` | `{"industry":"軟體開發"}` | `industry = '軟體開發'`, `status = 'ACTIVE'` |
| PRJ_QRY_C004 | 依統編查詢 | PM | `GET /api/v1/customers` | `{"taxId":"12345678"}` | `tax_id = '12345678'` |

#### 2.2.2 業務場景說明

**PRJ_QRY_C002: 依名稱模糊查詢**

- **使用者：** PM 或業務人員
- **業務目的：** 快速搜尋客戶資料
- **權限控制：** `customer:read`
- **過濾邏輯：**
  ```sql
  WHERE customer_name LIKE '%科技%'
    AND status = 'ACTIVE'
  ORDER BY customer_name ASC
  ```

---

### 2.3 WBS 查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PRJ_QRY_W001 | 查詢專案 WBS | PM | `GET /api/v1/projects/{id}/tasks/tree` | `{"projectId":"P001"}` | `project_id = 'P001'` |
| PRJ_QRY_W002 | 查詢頂層工作包 | PM | `GET /api/v1/tasks` | `{"projectId":"P001","level":1}` | `project_id = 'P001'`, `level = 1` |
| PRJ_QRY_W003 | 查詢未完成工項 | PM | `GET /api/v1/tasks` | `{"projectId":"P001","status":"IN_PROGRESS"}` | `project_id = 'P001'`, `status = 'IN_PROGRESS'` |
| PRJ_QRY_W004 | 查詢指派給我的工項 | EMPLOYEE | `GET /api/v1/tasks/my` | `{}` | `assignee_id = '{currentUserId}'`, `status IN ('NOT_STARTED', 'IN_PROGRESS')` |
| PRJ_QRY_W005 | 查詢子工項 | PM | `GET /api/v1/tasks` | `{"parentTaskId":"T001"}` | `parent_task_id = 'T001'` |

#### 2.3.2 業務場景說明

**PRJ_QRY_W001: 查詢專案 WBS（樹狀結構）**

- **使用者：** PM
- **業務目的：** 查看專案的完整工作分解結構
- **權限控制：** `project:read`
- **過濾邏輯：**
  ```sql
  WITH RECURSIVE task_tree AS (
    -- 頂層工項
    SELECT task_id, parent_task_id, task_code, task_name, level,
           estimated_hours, actual_hours, status, progress, display_order
    FROM tasks
    WHERE project_id = 'P001' AND parent_task_id IS NULL

    UNION ALL

    -- 遞迴查詢子工項
    SELECT t.task_id, t.parent_task_id, t.task_code, t.task_name, t.level,
           t.estimated_hours, t.actual_hours, t.status, t.progress, t.display_order
    FROM tasks t
    INNER JOIN task_tree tt ON t.parent_task_id = tt.task_id
  )
  SELECT * FROM task_tree
  ORDER BY level, display_order
  ```

**PRJ_QRY_W004: 查詢指派給我的工項（ESS）**

- **使用者：** 一般員工
- **業務目的：** 員工查看自己的待辦工項
- **權限控制：** 無需特殊權限
- **過濾邏輯：**
  ```sql
  WHERE assignee_id = '{currentUserId}'
    AND status IN ('NOT_STARTED', 'IN_PROGRESS')
  ORDER BY planned_end_date ASC
  ```

---

### 2.4 成本分析查詢

#### 2.4.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PRJ_QRY_COST001 | 查詢專案成本分析 | PM | `GET /api/v1/projects/{id}/cost` | `{"projectId":"P001"}` | `project_id = 'P001'` |
| PRJ_QRY_COST002 | 查詢月度成本快照 | PM | `GET /api/v1/projects/{id}/cost/snapshots` | `{"projectId":"P001","yearMonth":"2026-03"}` | `project_id = 'P001'`, `DATE_FORMAT(snapshot_date, '%Y-%m') = '2026-03'` |
| PRJ_QRY_COST003 | 查詢預算警示專案 | PM | `GET /api/v1/projects/budget-alerts` | `{}` | `(actual_hours / budget_hours) >= 0.8`, `status = 'IN_PROGRESS'` |

#### 2.4.2 業務場景說明

**PRJ_QRY_COST001: 查詢專案成本分析**

- **使用者：** PM 或高階主管
- **業務目的：** 分析專案成本與預算使用狀況
- **權限控制：** `project:cost:read`
- **過濾邏輯：**
  ```sql
  -- 主查詢：專案基本資料
  SELECT p.project_id, p.project_code, p.project_name,
         p.budget_type, p.budget_amount, p.budget_hours,
         p.actual_hours, p.actual_cost,
         (p.actual_cost / NULLIF(p.budget_amount, 0) * 100) AS cost_utilization,
         (p.actual_hours / NULLIF(p.budget_hours, 0) * 100) AS hours_utilization
  FROM projects p
  WHERE p.project_id = 'P001'

  -- 成員成本明細
  SELECT pm.employee_id, pm.role, pm.allocated_hours,
         COALESCE(SUM(t.actual_hours), 0) AS actual_hours,
         COALESCE(SUM(t.actual_hours * pm.hourly_rate), 0) AS member_cost
  FROM project_members pm
  LEFT JOIN (
    SELECT assignee_id, SUM(actual_hours) AS actual_hours
    FROM tasks
    WHERE project_id = 'P001'
    GROUP BY assignee_id
  ) t ON pm.employee_id = t.assignee_id
  WHERE pm.project_id = 'P001' AND pm.leave_date IS NULL
  GROUP BY pm.employee_id, pm.role, pm.allocated_hours
  ```

**PRJ_QRY_COST003: 查詢預算警示專案**

- **使用者：** PM 或高階主管
- **業務目的：** 快速識別預算即將超支的專案
- **權限控制：** `project:cost:read`
- **過濾邏輯：**
  ```sql
  WHERE status = 'IN_PROGRESS'
    AND budget_hours > 0
    AND (actual_hours / budget_hours) >= 0.8
  ORDER BY (actual_hours / budget_hours) DESC
  ```

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `ProjectCreatedEvent` | 建立專案 | Project | Notification | 發送通知給 PM 與團隊成員 |
| `ProjectStartedEvent` | 開始專案 | Project | Notification, Timesheet | 啟動工時追蹤，發送通知 |
| `ProjectCompletedEvent` | 專案結案 | Project | Reporting, Notification | 生成結案報告，發送完成通知 |
| `ProjectMemberAddedEvent` | 新增成員 | Project | Timesheet, Notification | 更新工時系統，發送通知 |
| `TaskCreatedEvent` | 建立工項 | Project | Timesheet | 工時系統同步工項資料 |
| `TaskCompletedEvent` | 完成工項 | Project | Notification | 發送完成通知 |
| `ProjectBudgetAlertEvent` | 預算超過80% | Project | Notification | 發送預算警示給 PM |

---

### 3.2 ProjectCreatedEvent (專案建立事件)

**觸發時機：**
PM 建立新專案並儲存成功後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-prj-create-001",
  "eventType": "ProjectCreatedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "project-001",
  "aggregateType": "Project",
  "payload": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "customerId": "customer-001",
    "customerName": "ABC科技股份有限公司",
    "projectType": "DEVELOPMENT",
    "budgetType": "FIXED_PRICE",
    "budgetAmount": 5000000,
    "budgetHours": 2000,
    "projectManager": "emp-001",
    "projectManagerName": "王大明",
    "plannedStartDate": "2026-03-01",
    "plannedEndDate": "2026-12-31",
    "status": "PLANNING",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送通知給 PM：「您的專案已建立成功」
  - 發送通知給團隊成員：「您已被加入專案 XXX」

---

### 3.3 ProjectStartedEvent (專案開始事件)

**觸發時機：**
PM 啟動專案後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-prj-start-001",
  "eventType": "ProjectStartedEvent",
  "timestamp": "2026-02-09T09:30:00Z",
  "aggregateId": "project-001",
  "aggregateType": "Project",
  "payload": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "projectManager": "emp-001",
    "projectManagerName": "王大明",
    "actualStartDate": "2026-03-01",
    "status": "IN_PROGRESS",
    "teamMembers": [
      {
        "employeeId": "emp-002",
        "employeeName": "李開發",
        "role": "DEVELOPER"
      },
      {
        "employeeId": "emp-003",
        "employeeName": "陳測試",
        "role": "QA"
      }
    ]
  }
}
```

**訂閱服務處理：**

- **Timesheet Service:**
  - 啟動工時追蹤功能
  - 允許團隊成員開始填報工時

- **Notification Service:**
  - 發送通知給團隊：「專案已正式啟動」

---

### 3.4 ProjectCompletedEvent (專案完成事件)

**觸發時機：**
PM 執行專案結案後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-prj-complete-001",
  "eventType": "ProjectCompletedEvent",
  "timestamp": "2026-12-31T18:00:00Z",
  "aggregateId": "project-001",
  "aggregateType": "Project",
  "payload": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "customerId": "customer-001",
    "customerName": "ABC科技股份有限公司",
    "projectManager": "emp-001",
    "projectManagerName": "王大明",
    "plannedStartDate": "2026-03-01",
    "plannedEndDate": "2026-12-31",
    "actualStartDate": "2026-03-01",
    "actualEndDate": "2026-12-31",
    "budgetAmount": 5000000,
    "budgetHours": 2000,
    "actualHours": 1950,
    "actualCost": 4850000,
    "budgetUtilization": 97.0,
    "hoursUtilization": 97.5,
    "status": "COMPLETED",
    "completionNotes": "專案如期完成，客戶驗收通過",
    "teamMemberCount": 5,
    "taskCompletionRate": 100.0
  }
}
```

**訂閱服務處理：**

- **Reporting Service:**
  - 生成專案結案報告
  - 建立成本分析快照

- **Notification Service:**
  - 發送通知給 PM 與高階主管：「專案已結案」
  - 發送感謝信給團隊成員

---

### 3.5 ProjectMemberAddedEvent (成員加入事件)

**觸發時機：**
PM 新增成員至專案後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-prj-member-001",
  "eventType": "ProjectMemberAddedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "project-001",
  "aggregateType": "Project",
  "payload": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "memberId": "member-004",
    "employeeId": "emp-004",
    "employeeName": "陳小美",
    "role": "DEVELOPER",
    "allocatedHours": 500,
    "hourlyRate": 1500,
    "joinDate": "2026-02-09"
  }
}
```

**訂閱服務處理：**

- **Timesheet Service:**
  - 更新工時系統，允許該成員填報工時
  - 建立工時配額記錄

- **Notification Service:**
  - 發送通知給新成員：「您已被加入專案 XXX」

---

### 3.6 TaskCreatedEvent (工項建立事件)

**觸發時機：**
PM 建立工項（WBS）後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-task-create-001",
  "eventType": "TaskCreatedEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "task-001",
  "aggregateType": "Task",
  "payload": {
    "taskId": "task-001",
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "parentTaskId": null,
    "taskCode": "T-1-001",
    "taskName": "需求分析階段",
    "level": 1,
    "estimatedHours": 200,
    "plannedStartDate": "2026-03-01",
    "plannedEndDate": "2026-03-31",
    "assigneeId": "emp-002",
    "assigneeName": "李開發",
    "status": "NOT_STARTED",
    "createdAt": "2026-02-09T11:00:00Z"
  }
}
```

**訂閱服務處理：**

- **Timesheet Service:**
  - 同步工項資料，允許成員選擇此工項填報工時

---

### 3.7 TaskCompletedEvent (工項完成事件)

**觸發時機：**
工項負責人完成工項後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-task-complete-001",
  "eventType": "TaskCompletedEvent",
  "timestamp": "2026-03-31T18:00:00Z",
  "aggregateId": "task-001",
  "aggregateType": "Task",
  "payload": {
    "taskId": "task-001",
    "taskCode": "T-1-001",
    "taskName": "需求分析階段",
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "assigneeId": "emp-002",
    "assigneeName": "李開發",
    "plannedStartDate": "2026-03-01",
    "plannedEndDate": "2026-03-31",
    "actualStartDate": "2026-03-01",
    "actualEndDate": "2026-03-31",
    "estimatedHours": 200,
    "actualHours": 200,
    "variance": 0,
    "variancePercentage": 0.0,
    "status": "COMPLETED",
    "completionNotes": "需求分析完成，產出需求規格書"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送通知給 PM：「工項 XXX 已完成」

---

### 3.8 ProjectBudgetAlertEvent (預算警示事件)

**觸發時機：**
專案實際工時或成本超過預算 80% 時發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-budget-alert-001",
  "eventType": "ProjectBudgetAlertEvent",
  "timestamp": "2026-10-15T10:00:00Z",
  "aggregateId": "project-001",
  "aggregateType": "Project",
  "payload": {
    "projectId": "project-001",
    "projectCode": "PRJ-20260209-001",
    "projectName": "ERP系統開發專案",
    "projectManager": "emp-001",
    "projectManagerName": "王大明",
    "budgetType": "FIXED_PRICE",
    "budgetAmount": 5000000,
    "budgetHours": 2000,
    "actualHours": 1650,
    "actualCost": 4125000,
    "hoursUtilization": 82.5,
    "costUtilization": 82.5,
    "alertLevel": "WARNING",
    "message": "專案預算已使用 82.5%，請注意控管"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送警示通知給 PM 與高階主管
  - 發送 Email 與推播通知

---

## 4. 測試斷言規格

### 4.1 Command 操作測試斷言

**測試目標：** 驗證 Command 操作是否正確執行業務規則並發布領域事件。

**測試方法：**

1. **業務規則驗證**
   - 使用 Mock Repository 驗證查詢條件
   - 使用 ArgumentCaptor 捕獲儲存的 Entity
   - 斷言 Entity 狀態符合業務規則

2. **領域事件驗證**
   - 使用 Mock EventPublisher 驗證事件發布
   - 斷言事件類型、Payload 內容正確
   - 驗證事件時序（先儲存後發布）

**範例：PRJ_CMD_003 建立專案測試**

```java
@Test
@DisplayName("PRJ_CMD_003: 建立專案 - 應建立專案並發布事件")
void createProject_ShouldCreateProjectAndPublishEvent() {
    // Given
    var request = CreateProjectRequest.builder()
        .projectName("ERP系統開發專案")
        .customerId("customer-001")
        .projectType("DEVELOPMENT")
        .plannedStartDate(LocalDate.of(2026, 3, 1))
        .plannedEndDate(LocalDate.of(2026, 12, 31))
        .budgetType("FIXED_PRICE")
        .budgetAmount(new BigDecimal("5000000"))
        .budgetHours(new BigDecimal("2000"))
        .projectManager("emp-001")
        .build();

    // Mock customer exists
    when(customerRepository.findById("customer-001"))
        .thenReturn(Optional.of(mockCustomer));

    // Mock employee exists
    when(organizationService.employeeExists("emp-001")).thenReturn(true);

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify project saved
    var captor = ArgumentCaptor.forClass(Project.class);
    verify(projectRepository).save(captor.capture());

    var savedProject = captor.getValue();
    assertThat(savedProject.getProjectName()).isEqualTo("ERP系統開發專案");
    assertThat(savedProject.getStatus()).isEqualTo(ProjectStatus.PLANNING);
    assertThat(savedProject.getProjectCode()).startsWith("PRJ-");

    // Then - Verify event published
    var eventCaptor = ArgumentCaptor.forClass(ProjectCreatedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("ProjectCreatedEvent");
    assertThat(event.getPayload().getProjectName()).isEqualTo("ERP系統開發專案");
    assertThat(event.getPayload().getStatus()).isEqualTo("PLANNING");
}
```

---

### 4.2 Query 操作測試斷言

**測試目標：** 驗證 Query 操作是否正確套用過濾條件與權限控制。

**測試方法：**

1. **QueryGroup 攔截**
   - 使用 ArgumentCaptor 捕獲 QueryGroup
   - 遍歷 QueryFilter 斷言欄位、操作符、值正確

2. **合約比對**
   - 載入 Markdown 合約規格
   - 根據場景 ID 比對必須包含的過濾條件
   - 斷言所有必要條件都存在於 QueryGroup

**範例：PRJ_QRY_P001 查詢測試**

```java
@Test
@DisplayName("PRJ_QRY_P001: 查詢進行中專案 - 應包含狀態過濾")
void searchProject_ByStatus_ShouldIncludeStatusFilter() {
    // Given
    String contractSpec = loadContractSpec("project");

    var request = ProjectSearchRequest.builder()
        .status("IN_PROGRESS")
        .build();

    // When
    var captor = ArgumentCaptor.forClass(QueryGroup.class);
    service.getResponse(request, currentUser);

    // Then
    verify(projectRepository).findPage(captor.capture(), any());

    var queryGroup = captor.getValue();
    assertContract(queryGroup, contractSpec, "PRJ_QRY_P001");

    // Additional assertions
    assertThat(queryGroup).containsFilter("status", Operator.EQUAL, "IN_PROGRESS");
}
```

---

### 4.3 Integration Test 斷言

**測試目標：** 驗證完整的 API → Service → Repository 流程。

**測試方法：**

1. **使用 MockMvc 執行 API 請求**
2. **驗證 HTTP 狀態碼**
3. **驗證 Response Body 結構**
4. **驗證資料庫狀態變更**（使用 Testcontainers）

**範例：PRJ_CMD_003 整合測試**

```java
@Test
@DisplayName("PRJ_CMD_003: 建立專案整合測試 - 應建立記錄並返回正確回應")
void createProject_Integration_ShouldCreateRecordAndReturnResponse() throws Exception {
    // Given
    var request = CreateProjectRequest.builder()
        .projectName("ERP系統開發專案")
        .customerId("customer-001")
        .projectType("DEVELOPMENT")
        .plannedStartDate(LocalDate.of(2026, 3, 1))
        .plannedEndDate(LocalDate.of(2026, 12, 31))
        .budgetType("FIXED_PRICE")
        .budgetAmount(new BigDecimal("5000000"))
        .budgetHours(new BigDecimal("2000"))
        .projectManager("emp-001")
        .build();

    // When
    var result = mockMvc.perform(post("/api/v1/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.projectId").exists())
        .andExpect(jsonPath("$.data.projectCode").value(startsWith("PRJ-")))
        .andExpect(jsonPath("$.data.status").value("PLANNING"))
        .andReturn();

    // Then - Verify database
    var projects = projectRepository.findByProjectName("ERP系統開發專案");
    assertThat(projects).hasSize(1);
    assertThat(projects.get(0).getStatus()).isEqualTo(ProjectStatus.PLANNING);
}
```

---

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 專案使用 `status` 欄位（'PLANNING', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD', 'CANCELLED'）
   - 客戶使用 `status` 欄位（'ACTIVE', 'INACTIVE'）
   - 工項使用 `status` 欄位（'NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'BLOCKED'）
   - **不使用 `is_deleted` 欄位**

2. **權限控制:**
   - PM 可查看所有專案
   - 一般員工只能查看參與的專案
   - 成本資訊需要特殊權限 `project:cost:read`

3. **租戶隔離:**
   - 所有查詢自動加上 `tenant_id = ?` 過濾條件（多租戶架構）

---

### 5.2 專案類型說明

| 專案類型 | 說明 | 預算模式建議 |
|:---|:---|:---|
| DEVELOPMENT | 新開發專案 | FIXED_PRICE 或 TIME_AND_MATERIAL |
| MAINTENANCE | 維護專案 | TIME_AND_MATERIAL |
| CONSULTING | 顧問專案 | TIME_AND_MATERIAL |

---

### 5.3 預算類型說明

| 預算類型 | 說明 | 成本追蹤重點 |
|:---|:---|:---|
| FIXED_PRICE | 固定價格專案 | 追蹤實際成本是否超過預算金額 |
| TIME_AND_MATERIAL | 實報實銷專案 | 追蹤實際工時是否超過預算工時 |

---

### 5.4 工項層級限制

- 最多支援 5 層級的 WBS 結構
- 層級 1：專案階段（如：需求分析、設計、開發、測試）
- 層級 2-5：逐步細分的工作包

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景、業務規則驗證、Domain Events Payload 定義、測試斷言規格 |
| 1.0 | 2026-02-06 | 精簡版建立 |
