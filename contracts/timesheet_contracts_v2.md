# 工時管理服務業務合約 (Timesheet Service Business Contract)

> **服務代碼:** HR07
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/07_工時管理服務系統設計書.md`
> - `knowledge/04_API_Specifications/07_工時管理服務系統設計書_API詳細規格.md`

---

## 📋 概述

本合約文件定義工時管理服務的**完整業務場景**，包括：
1. **Command 操作場景**（建立、更新、刪除）- 驗證業務規則與領域事件
2. **Query 操作場景**（查詢）- 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景
- ✅ 新增 5 個領域事件的詳細定義
- ✅ 對應到實際的 API 端點
- ✅ 使用實際存在的欄位（移除 is_deleted，使用 status）
- ✅ 包含完整的業務規則驗證

**服務定位：**
工時管理服務負責員工工時回報、PM 審核、工時統計分析。這是專案成本核算的關鍵服務，需與專案服務、薪資服務緊密整合。

**資料軟刪除策略：**
- **工時單**: 使用 `status` 欄位，'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'REJECTED' | 'LOCKED'
- **工時明細**: 級聯刪除（CASCADE），隨工時單刪除
- **歷史記錄**: 不進行軟刪除，保留所有歷史記錄（用於成本追蹤與稽核）

**工時單狀態流程：**
```
DRAFT (草稿) → SUBMITTED (待審核) → APPROVED (已核准) / REJECTED (已駁回)
                                  ↓
                              LOCKED (已鎖定，用於薪資計算後鎖定)
```

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [工時單管理 Command](#11-工時單管理-command)
   - 1.2 [工時明細管理 Command](#12-工時明細管理-command)
   - 1.3 [工時審核 Command](#13-工時審核-command)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [工時單查詢](#21-工時單查詢)
   - 2.2 [工時明細查詢](#22-工時明細查詢)
   - 2.3 [工時統計查詢](#23-工時統計查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 工時單管理 Command

#### TMS_CMD_001: 建立週工時單

**業務場景描述：**
員工在每週一建立新的週工時單，系統自動計算週期範圍（週一至週日）並初始化為草稿狀態。

**API 端點：**
```
POST /api/v1/timesheets
```

**前置條件：**
- 執行者必須為一般員工（可查詢自己的 employeeId）
- 該週期不可已存在工時單

**輸入 (Request)：**
```json
{
  "periodStartDate": "2026-01-05"
}
```

**業務規則驗證：**

1. ✅ **週期開始日必須為週一**
   - 規則：`periodStartDate.getDayOfWeek() == MONDAY`
   - 錯誤訊息：`PERIOD_START_MUST_BE_MONDAY`

2. ✅ **重複工時單檢查**
   - 查詢條件：`employee_id = ? AND period_start_date = ? AND period_end_date = ?`
   - 預期結果：不存在重複的工時單
   - 錯誤訊息：`TIMESHEET_ALREADY_EXISTS`

3. ✅ **不可建立未來週期的工時單**
   - 規則：`periodStartDate <= LocalDate.now()`
   - 錯誤訊息：`CANNOT_CREATE_FUTURE_TIMESHEET`

4. ✅ **自動計算週期結束日**
   - 規則：`period_end_date = period_start_date + 6 days`（週日）

**必須發布的領域事件：**
```json
{
  "eventId": "evt-tms-create-001",
  "eventType": "TimesheetCreatedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "timesheet-001",
  "payload": {
    "timesheetId": "timesheet-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "periodType": "WEEKLY",
    "periodStartDate": "2026-01-05",
    "periodEndDate": "2026-01-11",
    "status": "DRAFT",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "timesheetId": "timesheet-001",
    "periodStartDate": "2026-01-05",
    "periodEndDate": "2026-01-11",
    "status": "DRAFT",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

---

### 1.2 工時明細管理 Command

#### TMS_CMD_002: 填報工時

**業務場景描述：**
員工在工時單中填報每日的工時明細，選擇專案、工項、填寫工時與說明。

**API 端點：**
```
POST /api/v1/timesheets/{id}/entries
```

**前置條件：**
- 執行者必須為工時單的擁有者
- 工時單狀態必須為 DRAFT 或 REJECTED
- 工時單未被鎖定

**輸入 (Request)：**
```json
{
  "projectId": "project-001",
  "taskId": "task-001",
  "workDate": "2026-01-05",
  "hours": 8.0,
  "description": "完成需求分析文件"
}
```

**業務規則驗證：**

1. ✅ **工時單狀態檢查**
   - 查詢條件：`timesheet_id = ? AND status IN ('DRAFT', 'REJECTED') AND is_locked = false`
   - 預期結果：工時單存在且可編輯
   - 錯誤訊息：`TIMESHEET_NOT_EDITABLE`

2. ✅ **權限檢查**
   - 規則：`timesheet.employee_id = currentUserId`
   - 錯誤訊息：`INSUFFICIENT_PERMISSION`

3. ✅ **專案成員檢查**
   - 呼叫 Project Service 驗證
   - 查詢條件：`project_id = ? AND employee_id = ? AND leave_date IS NULL`
   - 預期結果：員工是專案成員
   - 錯誤訊息：`NOT_PROJECT_MEMBER`

4. ✅ **工作日期合理性檢查**
   - 規則：`work_date >= period_start_date AND work_date <= period_end_date`
   - 規則：`work_date <= LocalDate.now()`（不可回報未來日期）
   - 錯誤訊息：`INVALID_WORK_DATE`

5. ✅ **工時範圍檢查**
   - 規則：`0 < hours <= 24`
   - 錯誤訊息：`INVALID_HOURS_RANGE`

6. ✅ **單日工時上限檢查**
   - 查詢條件：`SUM(hours) WHERE timesheet_id = ? AND work_date = ?`
   - 規則：`existing_total + hours <= 24`
   - 錯誤訊息：`DAILY_HOURS_EXCEEDED`

7. ✅ **重複工時檢查**
   - 查詢條件：`timesheet_id = ? AND project_id = ? AND work_date = ?`
   - 預期結果：同日同專案不可重複
   - 錯誤訊息：`DUPLICATE_ENTRY`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-tms-entry-001",
  "eventType": "TimesheetEntryAddedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "timesheet-001",
  "payload": {
    "timesheetId": "timesheet-001",
    "entryId": "entry-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "projectId": "project-001",
    "projectName": "ERP系統開發",
    "taskId": "task-001",
    "taskName": "需求分析",
    "workDate": "2026-01-05",
    "hours": 8.0,
    "description": "完成需求分析文件"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "entryId": "entry-001",
    "workDate": "2026-01-05",
    "hours": 8.0,
    "createdAt": "2026-02-09T10:00:00Z"
  }
}
```

---

#### TMS_CMD_003: 更新工時明細

**業務場景描述：**
員工修改已填報的工時明細，系統重新驗證並更新工時單總計。

**API 端點：**
```
PUT /api/v1/timesheets/{id}/entries/{entryId}
```

**前置條件：**
- 執行者必須為工時單的擁有者
- 工時單狀態必須為 DRAFT 或 REJECTED
- 工時明細必須存在

**輸入 (Request)：**
```json
{
  "hours": 6.5,
  "description": "完成需求分析文件（修訂）"
}
```

**業務規則驗證：**

1. ✅ **工時明細存在性檢查**
   - 查詢條件：`entry_id = ? AND timesheet_id = ?`
   - 預期結果：工時明細存在

2. ✅ **工時單狀態檢查**
   - 同 TMS_CMD_002

3. ✅ **工時範圍檢查**
   - 規則：`0 < hours <= 24`

4. ✅ **單日工時上限檢查**
   - 規則：`(existing_total - old_hours) + new_hours <= 24`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-tms-update-001",
  "eventType": "TimesheetEntryUpdatedEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "timesheet-001",
  "payload": {
    "timesheetId": "timesheet-001",
    "entryId": "entry-001",
    "employeeId": "emp-001",
    "oldHours": 8.0,
    "newHours": 6.5,
    "workDate": "2026-01-05"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "entryId": "entry-001",
    "hours": 6.5,
    "updatedAt": "2026-02-09T11:00:00Z"
  }
}
```

---

#### TMS_CMD_004: 刪除工時明細

**業務場景描述：**
員工刪除錯誤填報的工時明細，系統重新計算工時單總計。

**API 端點：**
```
DELETE /api/v1/timesheets/{id}/entries/{entryId}
```

**前置條件：**
- 執行者必須為工時單的擁有者
- 工時單狀態必須為 DRAFT 或 REJECTED
- 工時明細必須存在

**輸入 (Request)：**
（無 Body，僅 URL 參數）

**業務規則驗證：**

1. ✅ **工時明細存在性檢查**
   - 查詢條件：`entry_id = ? AND timesheet_id = ?`
   - 預期結果：工時明細存在

2. ✅ **工時單狀態檢查**
   - 同 TMS_CMD_002

**必須發布的領域事件：**
```json
{
  "eventId": "evt-tms-delete-001",
  "eventType": "TimesheetEntryDeletedEvent",
  "timestamp": "2026-02-09T12:00:00Z",
  "aggregateId": "timesheet-001",
  "payload": {
    "timesheetId": "timesheet-001",
    "entryId": "entry-001",
    "employeeId": "emp-001",
    "deletedHours": 6.5,
    "workDate": "2026-01-05"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "message": "工時明細已刪除"
}
```

---

### 1.3 工時審核 Command

#### TMS_CMD_005: 提交工時單

**業務場景描述：**
員工完成工時填報後，提交工時單至 PM 審核，系統變更狀態並發送通知。

**API 端點：**
```
PUT /api/v1/timesheets/{id}/submit
```

**前置條件：**
- 執行者必須為工時單的擁有者
- 工時單狀態必須為 DRAFT
- 工時單必須有至少一筆工時明細

**輸入 (Request)：**
（無 Body）

**業務規則驗證：**

1. ✅ **工時單狀態檢查**
   - 查詢條件：`timesheet_id = ? AND status = 'DRAFT' AND employee_id = ?`
   - 預期結果：工時單存在且為草稿狀態
   - 錯誤訊息：`TIMESHEET_NOT_DRAFT`

2. ✅ **工時明細完整性檢查**
   - 查詢條件：`COUNT(*) WHERE timesheet_id = ?`
   - 規則：至少要有 1 筆工時明細
   - 錯誤訊息：`NO_ENTRIES_FOUND`

3. ✅ **週工時合理性檢查**（建議但非強制）
   - 規則：`total_hours >= 30 AND total_hours <= 60`
   - 如不符合，發出警告但允許提交

4. ✅ **更新提交時間**
   - 設定：`submitted_at = LocalDateTime.now()`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-tms-submit-001",
  "eventType": "TimesheetSubmittedEvent",
  "timestamp": "2026-02-09T13:00:00Z",
  "aggregateId": "timesheet-001",
  "payload": {
    "timesheetId": "timesheet-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "periodStartDate": "2026-01-05",
    "periodEndDate": "2026-01-11",
    "totalHours": 40.0,
    "entryCount": 5,
    "submittedAt": "2026-02-09T13:00:00Z",
    "projectIds": ["project-001", "project-002"]
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "timesheetId": "timesheet-001",
    "status": "SUBMITTED",
    "submittedAt": "2026-02-09T13:00:00Z"
  }
}
```

---

#### TMS_CMD_006: PM 核准工時單

**業務場景描述：**
PM 審核工時單內容無誤後核准，系統更新狀態並通知 Payroll Service 與 Project Service。

**API 端點：**
```
PUT /api/v1/timesheets/{id}/approve
```

**前置條件：**
- 執行者必須擁有 `timesheet:approve` 權限或為專案 PM
- 工時單狀態必須為 SUBMITTED

**輸入 (Request)：**
```json
{
  "comment": "工時核准，請確實執行"
}
```

**業務規則驗證：**

1. ✅ **工時單狀態檢查**
   - 查詢條件：`timesheet_id = ? AND status = 'SUBMITTED'`
   - 預期結果：工時單存在且為待審核狀態
   - 錯誤訊息：`TIMESHEET_NOT_SUBMITTED`

2. ✅ **審核權限檢查**
   - 規則：執行者擁有 `timesheet:approve` 權限
   - 或：執行者為工時單中任一專案的 PM
   - 查詢條件：`EXISTS (SELECT 1 FROM timesheet_entries te JOIN projects p ON te.project_id = p.project_id WHERE te.timesheet_id = ? AND p.project_manager = ?)`
   - 錯誤訊息：`INSUFFICIENT_PERMISSION`

3. ✅ **更新核准資訊**
   - 設定：`approved_by = currentUserId`
   - 設定：`approved_at = LocalDateTime.now()`
   - 設定：`status = 'APPROVED'`

4. ✅ **計算各專案工時**
   - 查詢條件：`SELECT project_id, SUM(hours) FROM timesheet_entries WHERE timesheet_id = ? GROUP BY project_id`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-tms-approve-001",
  "eventType": "TimesheetApprovedEvent",
  "timestamp": "2026-02-09T14:00:00Z",
  "aggregateId": "timesheet-001",
  "payload": {
    "timesheetId": "timesheet-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "approvedBy": "manager-001",
    "approverName": "李經理",
    "approvedAt": "2026-02-09T14:00:00Z",
    "periodStartDate": "2026-01-05",
    "periodEndDate": "2026-01-11",
    "totalHours": 40.0,
    "projectHours": [
      {
        "projectId": "project-001",
        "projectName": "ERP系統開發",
        "hours": 28.0
      },
      {
        "projectId": "project-002",
        "projectName": "維護專案",
        "hours": 12.0
      }
    ],
    "entries": [
      {
        "entryId": "entry-001",
        "projectId": "project-001",
        "taskId": "task-001",
        "workDate": "2026-01-05",
        "hours": 8.0
      },
      {
        "entryId": "entry-002",
        "projectId": "project-001",
        "taskId": "task-002",
        "workDate": "2026-01-06",
        "hours": 8.0
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
    "timesheetId": "timesheet-001",
    "status": "APPROVED",
    "approvedBy": "manager-001",
    "approvedAt": "2026-02-09T14:00:00Z"
  }
}
```

---

#### TMS_CMD_007: PM 駁回工時單

**業務場景描述：**
PM 審核工時單發現問題後駁回，系統更新狀態並通知員工重新填報。

**API 端點：**
```
PUT /api/v1/timesheets/{id}/reject
```

**前置條件：**
- 執行者必須擁有 `timesheet:approve` 權限或為專案 PM
- 工時單狀態必須為 SUBMITTED

**輸入 (Request)：**
```json
{
  "reason": "工時與差勤記錄不符，請確認"
}
```

**業務規則驗證：**

1. ✅ **工時單狀態檢查**
   - 查詢條件：`timesheet_id = ? AND status = 'SUBMITTED'`
   - 預期結果：工時單存在且為待審核狀態
   - 錯誤訊息：`TIMESHEET_NOT_SUBMITTED`

2. ✅ **審核權限檢查**
   - 同 TMS_CMD_006

3. ✅ **駁回原因必填檢查**
   - 規則：`reason != null AND reason.length() > 0`
   - 錯誤訊息：`REJECT_REASON_REQUIRED`

4. ✅ **更新駁回資訊**
   - 設定：`rejection_reason = reason`
   - 設定：`status = 'REJECTED'`

**必須發布的領域事件：**
```json
{
  "eventId": "evt-tms-reject-001",
  "eventType": "TimesheetRejectedEvent",
  "timestamp": "2026-02-09T15:00:00Z",
  "aggregateId": "timesheet-001",
  "payload": {
    "timesheetId": "timesheet-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "rejectedBy": "manager-001",
    "rejectorName": "李經理",
    "rejectedAt": "2026-02-09T15:00:00Z",
    "reason": "工時與差勤記錄不符，請確認",
    "totalHours": 40.0
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "timesheetId": "timesheet-001",
    "status": "REJECTED",
    "rejectionReason": "工時與差勤記錄不符，請確認",
    "updatedAt": "2026-02-09T15:00:00Z"
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 工時單查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TMS_QRY_T001 | 查詢員工週工時單 | PM | `GET /api/v1/timesheets` | `{"employeeId":"E001","periodStartDate":"2026-01-05"}` | `employee_id = 'E001'`, `period_start_date = '2026-01-05'` |
| TMS_QRY_T002 | 查詢待審核工時單 | PM | `GET /api/v1/timesheets` | `{"status":"SUBMITTED"}` | `status = 'SUBMITTED'` |
| TMS_QRY_T003 | 查詢已核准工時單 | PM | `GET /api/v1/timesheets` | `{"status":"APPROVED"}` | `status = 'APPROVED'` |
| TMS_QRY_T004 | 員工查詢自己工時單 | EMPLOYEE | `GET /api/v1/timesheets/my` | `{}` | `employee_id = '{currentUserId}'` |
| TMS_QRY_T005 | 依專案查詢工時單 | PM | `GET /api/v1/timesheets` | `{"projectId":"P001"}` | `EXISTS (SELECT 1 FROM timesheet_entries WHERE timesheet_entries.timesheet_id = timesheets.timesheet_id AND timesheet_entries.project_id = 'P001')` |
| TMS_QRY_T006 | 依週期查詢工時單 | PM | `GET /api/v1/timesheets` | `{"periodStartDate":"2026-01-05","periodEndDate":"2026-01-11"}` | `period_start_date = '2026-01-05'`, `period_end_date = '2026-01-11'` |

#### 2.1.2 業務場景說明

**TMS_QRY_T002: 查詢待審核工時單**

- **使用者：** PM
- **業務目的：** PM 查看所有待審核的工時單
- **權限控制：** `timesheet:approve`
- **過濾邏輯：**
  ```sql
  WHERE status = 'SUBMITTED'
  ORDER BY submitted_at ASC
  ```

**TMS_QRY_T004: 員工查詢自己工時單（ESS）**

- **使用者：** 一般員工
- **業務目的：** 員工查詢自己的工時記錄
- **權限控制：** 無需特殊權限
- **過濾邏輯：**
  ```sql
  WHERE employee_id = '{currentUserId}'
  ORDER BY period_start_date DESC
  ```

---

### 2.2 工時明細查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TMS_QRY_E001 | 查詢工時單明細 | PM | `GET /api/v1/timesheet-entries` | `{"timesheetId":"TS001"}` | `timesheet_id = 'TS001'` |
| TMS_QRY_E002 | 依專案查詢明細 | PM | `GET /api/v1/timesheet-entries` | `{"projectId":"P001"}` | `project_id = 'P001'` |
| TMS_QRY_E003 | 依工作日期查詢 | PM | `GET /api/v1/timesheet-entries` | `{"workDate":"2026-01-05"}` | `work_date = '2026-01-05'` |
| TMS_QRY_E004 | 依員工與專案查詢 | PM | `GET /api/v1/timesheet-entries` | `{"employeeId":"E001","projectId":"P001"}` | `EXISTS (SELECT 1 FROM timesheets WHERE timesheets.timesheet_id = timesheet_entries.timesheet_id AND timesheets.employee_id = 'E001')`, `project_id = 'P001'` |

#### 2.2.2 業務場景說明

**TMS_QRY_E001: 查詢工時單明細**

- **使用者：** PM 或員工（自己的工時單）
- **業務目的：** 查看工時單的所有明細記錄
- **權限控制：** `timesheet:read` 或為工時單擁有者
- **過濾邏輯：**
  ```sql
  WHERE timesheet_id = 'TS001'
  ORDER BY work_date ASC, project_id ASC
  ```

---

### 2.3 工時統計查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TMS_QRY_S001 | 員工月度工時統計 | EMPLOYEE | `GET /api/v1/timesheets/summary` | `{"month":"2026-01"}` | `employee_id = '{currentUserId}'`, `DATE_FORMAT(period_start_date, '%Y-%m') = '2026-01'`, `status = 'APPROVED'` |
| TMS_QRY_S002 | 專案月度工時統計 | PM | `GET /api/v1/timesheets/project-summary` | `{"projectId":"P001","month":"2026-01"}` | `project_id = 'P001'`, `DATE_FORMAT(work_date, '%Y-%m') = '2026-01'`, `EXISTS (SELECT 1 FROM timesheets WHERE timesheets.timesheet_id = timesheet_entries.timesheet_id AND timesheets.status = 'APPROVED')` |
| TMS_QRY_S003 | 未回報員工清單 | PM | `GET /api/v1/timesheets/unreported` | `{"periodStartDate":"2026-01-05"}` | `NOT EXISTS (SELECT 1 FROM timesheets WHERE timesheets.employee_id = employees.employee_id AND timesheets.period_start_date = '2026-01-05')` |

#### 2.3.2 業務場景說明

**TMS_QRY_S001: 員工月度工時統計**

- **使用者：** 一般員工
- **業務目的：** 員工查看自己的月度工時統計
- **權限控制：** 無需特殊權限
- **過濾邏輯：**
  ```sql
  SELECT
    ts.employee_id,
    SUM(ts.total_hours) AS total_hours,
    COUNT(*) AS timesheet_count
  FROM timesheets ts
  WHERE ts.employee_id = '{currentUserId}'
    AND DATE_FORMAT(ts.period_start_date, '%Y-%m') = '2026-01'
    AND ts.status = 'APPROVED'
  GROUP BY ts.employee_id
  ```

**TMS_QRY_S002: 專案月度工時統計**

- **使用者：** PM
- **業務目的：** PM 查看專案的月度工時使用狀況
- **權限控制：** `project:read`
- **過濾邏輯：**
  ```sql
  SELECT
    te.project_id,
    COUNT(DISTINCT ts.employee_id) AS employee_count,
    SUM(te.hours) AS total_hours
  FROM timesheet_entries te
  JOIN timesheets ts ON te.timesheet_id = ts.timesheet_id
  WHERE te.project_id = 'P001'
    AND DATE_FORMAT(te.work_date, '%Y-%m') = '2026-01'
    AND ts.status = 'APPROVED'
  GROUP BY te.project_id
  ```

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `TimesheetCreatedEvent` | 建立工時單 | Timesheet | - | 初始化工時單 |
| `TimesheetEntryAddedEvent` | 填報工時 | Timesheet | Project | 更新專案實際工時 |
| `TimesheetSubmittedEvent` | 提交審核 | Timesheet | Notification, Workflow | 發送通知給 PM |
| `TimesheetApprovedEvent` | 核准工時 | Timesheet | Payroll, Project, Notification | 計算薪資，更新專案成本 |
| `TimesheetRejectedEvent` | 駁回工時 | Timesheet | Notification | 發送駁回通知給員工 |

---

### 3.2 TimesheetCreatedEvent (工時單建立事件)

**觸發時機：**
員工建立新的週工時單後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-tms-create-001",
  "eventType": "TimesheetCreatedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "timesheet-001",
  "aggregateType": "Timesheet",
  "payload": {
    "timesheetId": "timesheet-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "periodType": "WEEKLY",
    "periodStartDate": "2026-01-05",
    "periodEndDate": "2026-01-11",
    "status": "DRAFT",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**訂閱服務處理：**
（本事件僅用於記錄，暫無訂閱服務）

---

### 3.3 TimesheetEntryAddedEvent (工時明細新增事件)

**觸發時機：**
員工填報工時明細後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-tms-entry-001",
  "eventType": "TimesheetEntryAddedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "timesheet-001",
  "aggregateType": "Timesheet",
  "payload": {
    "timesheetId": "timesheet-001",
    "entryId": "entry-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "projectId": "project-001",
    "projectName": "ERP系統開發",
    "taskId": "task-001",
    "taskName": "需求分析",
    "workDate": "2026-01-05",
    "hours": 8.0,
    "description": "完成需求分析文件"
  }
}
```

**訂閱服務處理：**

- **Project Service:**
  - 更新工項的實際工時（`actual_hours += 8.0`）
  - 更新專案的實際工時

---

### 3.4 TimesheetSubmittedEvent (工時單提交事件)

**觸發時機：**
員工提交工時單至 PM 審核後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-tms-submit-001",
  "eventType": "TimesheetSubmittedEvent",
  "timestamp": "2026-02-09T13:00:00Z",
  "aggregateId": "timesheet-001",
  "aggregateType": "Timesheet",
  "payload": {
    "timesheetId": "timesheet-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "periodStartDate": "2026-01-05",
    "periodEndDate": "2026-01-11",
    "totalHours": 40.0,
    "entryCount": 5,
    "submittedAt": "2026-02-09T13:00:00Z",
    "projectIds": ["project-001", "project-002"],
    "projectManagers": ["manager-001"]
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送通知給 PM：「員工 XXX 已提交工時單，請審核」

- **Workflow Service:**
  - 啟動工時審核流程

---

### 3.5 TimesheetApprovedEvent (工時單核准事件)

**觸發時機：**
PM 核准工時單後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-tms-approve-001",
  "eventType": "TimesheetApprovedEvent",
  "timestamp": "2026-02-09T14:00:00Z",
  "aggregateId": "timesheet-001",
  "aggregateType": "Timesheet",
  "payload": {
    "timesheetId": "timesheet-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "approvedBy": "manager-001",
    "approverName": "李經理",
    "approvedAt": "2026-02-09T14:00:00Z",
    "periodStartDate": "2026-01-05",
    "periodEndDate": "2026-01-11",
    "totalHours": 40.0,
    "projectHours": [
      {
        "projectId": "project-001",
        "projectName": "ERP系統開發",
        "hours": 28.0
      },
      {
        "projectId": "project-002",
        "projectName": "維護專案",
        "hours": 12.0
      }
    ],
    "entries": [
      {
        "entryId": "entry-001",
        "projectId": "project-001",
        "taskId": "task-001",
        "workDate": "2026-01-05",
        "hours": 8.0
      },
      {
        "entryId": "entry-002",
        "projectId": "project-001",
        "taskId": "task-002",
        "workDate": "2026-01-06",
        "hours": 8.0
      }
    ]
  }
}
```

**訂閱服務處理：**

- **Payroll Service:**
  - 計算該週期的加班工時（如有超過法定工時）
  - 準備薪資計算資料

- **Project Service:**
  - 確認工時記錄並鎖定，用於成本分析

- **Notification Service:**
  - 發送通知給員工：「您的工時已核准」

---

### 3.6 TimesheetRejectedEvent (工時單駁回事件)

**觸發時機：**
PM 駁回工時單後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-tms-reject-001",
  "eventType": "TimesheetRejectedEvent",
  "timestamp": "2026-02-09T15:00:00Z",
  "aggregateId": "timesheet-001",
  "aggregateType": "Timesheet",
  "payload": {
    "timesheetId": "timesheet-001",
    "employeeId": "emp-001",
    "employeeName": "王小華",
    "rejectedBy": "manager-001",
    "rejectorName": "李經理",
    "rejectedAt": "2026-02-09T15:00:00Z",
    "reason": "工時與差勤記錄不符，請確認",
    "totalHours": 40.0,
    "periodStartDate": "2026-01-05",
    "periodEndDate": "2026-01-11"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送通知給員工：「您的工時已被駁回，原因：XXX」

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

**範例：TMS_CMD_002 填報工時測試**

```java
@Test
@DisplayName("TMS_CMD_002: 填報工時 - 應新增工時明細並發布事件")
void addEntry_ShouldCreateEntryAndPublishEvent() {
    // Given
    var request = AddTimesheetEntryRequest.builder()
        .timesheetId("timesheet-001")
        .projectId("project-001")
        .taskId("task-001")
        .workDate(LocalDate.of(2026, 1, 5))
        .hours(new BigDecimal("8.0"))
        .description("完成需求分析文件")
        .build();

    // Mock timesheet exists
    when(timesheetRepository.findById("timesheet-001"))
        .thenReturn(Optional.of(mockTimesheet));

    // Mock project member check
    when(projectService.isProjectMember("project-001", "emp-001"))
        .thenReturn(true);

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify entry saved
    var captor = ArgumentCaptor.forClass(Timesheet.class);
    verify(timesheetRepository).save(captor.capture());

    var savedTimesheet = captor.getValue();
    assertThat(savedTimesheet.getEntries()).hasSize(1);
    assertThat(savedTimesheet.getEntries().get(0).getHours())
        .isEqualByComparingTo(new BigDecimal("8.0"));

    // Then - Verify event published
    var eventCaptor = ArgumentCaptor.forClass(TimesheetEntryAddedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("TimesheetEntryAddedEvent");
    assertThat(event.getPayload().getHours()).isEqualTo(8.0);
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

**範例：TMS_QRY_T002 查詢測試**

```java
@Test
@DisplayName("TMS_QRY_T002: 查詢待審核工時單 - 應包含狀態過濾")
void searchTimesheet_ByStatus_ShouldIncludeStatusFilter() {
    // Given
    String contractSpec = loadContractSpec("timesheet");

    var request = TimesheetSearchRequest.builder()
        .status("SUBMITTED")
        .build();

    // When
    var captor = ArgumentCaptor.forClass(QueryGroup.class);
    service.getResponse(request, currentUser);

    // Then
    verify(timesheetRepository).findPage(captor.capture(), any());

    var queryGroup = captor.getValue();
    assertContract(queryGroup, contractSpec, "TMS_QRY_T002");

    // Additional assertions
    assertThat(queryGroup).containsFilter("status", Operator.EQUAL, "SUBMITTED");
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

**範例：TMS_CMD_002 整合測試**

```java
@Test
@DisplayName("TMS_CMD_002: 填報工時整合測試 - 應建立記錄並返回正確回應")
void addEntry_Integration_ShouldCreateRecordAndReturnResponse() throws Exception {
    // Given
    var request = AddTimesheetEntryRequest.builder()
        .timesheetId("timesheet-001")
        .projectId("project-001")
        .taskId("task-001")
        .workDate(LocalDate.of(2026, 1, 5))
        .hours(new BigDecimal("8.0"))
        .description("完成需求分析文件")
        .build();

    // When
    var result = mockMvc.perform(post("/api/v1/timesheets/timesheet-001/entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.entryId").exists())
        .andExpect(jsonPath("$.data.hours").value(8.0))
        .andReturn();

    // Then - Verify database
    var entries = entryRepository.findByTimesheetId("timesheet-001");
    assertThat(entries).hasSize(1);
    assertThat(entries.get(0).getHours()).isEqualByComparingTo(new BigDecimal("8.0"));
}
```

---

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 工時單使用 `status` 欄位（'DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'LOCKED'）
   - **不使用 `is_deleted` 欄位**

2. **權限控制:**
   - 員工只能查看與編輯自己的工時單
   - PM 可查看所有待審核的工時單
   - 核准權限需額外檢查

3. **租戶隔離:**
   - 所有查詢自動加上 `tenant_id = ?` 過濾條件（多租戶架構）

---

### 5.2 工時單狀態流程

```
DRAFT (草稿)
  ↓ submit()
SUBMITTED (待審核)
  ↓ approve() / reject()
APPROVED (已核准) / REJECTED (已駁回)
  ↓ lock() (Payroll Service 觸發)
LOCKED (已鎖定)
```

- DRAFT: 員工可編輯
- SUBMITTED: 員工不可編輯，等待 PM 審核
- APPROVED: 已核准，員工與 PM 都不可編輯
- REJECTED: 已駁回，員工可重新編輯
- LOCKED: 已用於薪資計算，完全鎖定

---

### 5.3 工時驗證規則

1. **單日工時上限：** 24 小時
2. **週工時建議範圍：** 30-60 小時（非強制）
3. **不可回報未來日期**
4. **工作日期必須在週期範圍內**
5. **同日同專案不可重複回報**

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景、業務規則驗證、Domain Events Payload 定義、測試斷言規格 |
| 1.0 | 2026-02-06 | 精簡版建立 |
