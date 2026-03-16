# 工時管理服務 API 詳細規格

**版本:** 2.0
**日期:** 2026-03-16
**Domain代號:** 07 (TSH)
**服務名稱:** hrms-timesheet

---

## 目錄

1. [Controller命名對照](#1-controller命名對照)
2. [API總覽](#2-api總覽)
3. [工時條目 Command API](#3-工時條目-command-api)
4. [工時表 Command API](#4-工時表-command-api)
5. [工時審核 Command API](#5-工時審核-command-api)
6. [工時鎖定 Command API](#6-工時鎖定-command-api)
7. [工時查詢 API](#7-工時查詢-api)
8. [工時報表 API](#8-工時報表-api)

---

## 1. Controller命名對照

| Controller | 說明 |
|:---|:---|
| `HR07TimesheetCmdController` | 工時表 Command 操作（含條目 CRUD、提交、審核、鎖定） |
| `HR07TimesheetQryController` | 工時表 Query 操作（我的工時、詳情、待簽核列表） |
| `HR07TimesheetReportQryController` | 報表 Query 操作（統計、專案工時、未回報） |

> **變更記錄 (v2.0)**：實際程式碼中不存在 `HR07EntryCmdController`、`HR07ApprovalCmdController`、`HR07ApprovalQryController`、`HR07ReportQryController`。所有 Command 操作統一在 `HR07TimesheetCmdController`，審核相關查詢在 `HR07TimesheetQryController`，報表查詢在 `HR07TimesheetReportQryController`。

---

## 2. API總覽

### 2.1 端點清單 (14個端點)

| 端點 | 方法 | Controller | 方法名稱 | Service | 說明 |
|:---|:---:|:---|:---|:---|:---|
| `/api/v1/timesheets/entry` | POST | HR07TimesheetCmdController | `createEntry` | CreateEntryServiceImpl | 新增工時條目 |
| `/api/v1/timesheets/{id}/entries/{entryId}` | PUT | HR07TimesheetCmdController | `updateTimesheetEntry` | UpdateTimesheetEntryServiceImpl | 更新工時條目 |
| `/api/v1/timesheets/{id}/entries/{entryId}` | DELETE | HR07TimesheetCmdController | `deleteTimesheetEntry` | DeleteTimesheetEntryServiceImpl | 刪除工時條目 |
| `/api/v1/timesheets/submit` | POST | HR07TimesheetCmdController | `submitTimesheet` | SubmitTimesheetServiceImpl | 提交工時表審核 |
| `/api/v1/timesheets/{id}/approve` | POST | HR07TimesheetCmdController | `approveTimesheet` | ApproveTimesheetServiceImpl | 核准工時表 |
| `/api/v1/timesheets/batch-approve` | PUT | HR07TimesheetCmdController | `batchApproveTimesheet` | BatchApproveTimesheetServiceImpl | 批次核准工時表 |
| `/api/v1/timesheets/{id}/reject` | POST | HR07TimesheetCmdController | `rejectTimesheet` | RejectTimesheetServiceImpl | 退回工時表 |
| `/api/v1/timesheets/{id}/lock` | POST | HR07TimesheetCmdController | `lockTimesheet` | LockTimesheetServiceImpl | 鎖定工時表 |
| `/api/v1/timesheets/my` | GET | HR07TimesheetQryController | `getMyTimesheet` | GetMyTimesheetServiceImpl | 查詢我的工時表 |
| `/api/v1/timesheets/approvals` | GET | HR07TimesheetQryController | `getPendingApprovals` | GetPendingApprovalsServiceImpl | 查詢待簽核列表 |
| `/api/v1/timesheets/{id}` | GET | HR07TimesheetQryController | `getTimesheet` | GetTimesheetDetailServiceImpl | 查詢工時表詳情 |
| `/api/v1/timesheets/summary` | GET | HR07TimesheetReportQryController | `getTimesheetSummary` | GetTimesheetSummaryServiceImpl | 個人/部門工時統計 |
| `/api/v1/timesheets/project-summary` | GET | HR07TimesheetReportQryController | `getProjectTimesheetSummary` | GetProjectTimesheetSummaryServiceImpl | 專案工時統計 |
| `/api/v1/timesheets/unreported` | GET | HR07TimesheetReportQryController | `getUnreportedEmployees` | GetUnreportedEmployeesServiceImpl | 未回報員工列表 |

### 2.2 v1.0 → v2.0 端點變更對照

| v1.0 文檔 | v2.0 實際 | 變更說明 |
|:---|:---|:---|
| `POST /api/v1/timesheets` | 已移除 | 建立工時表功能不存在於程式碼中 |
| `PUT /api/v1/timesheets/{id}/submit` | `POST /api/v1/timesheets/submit` | HTTP 方法由 PUT 改 POST；路徑從 path param 改為 request body |
| `POST /api/v1/timesheets/{id}/entries` | `POST /api/v1/timesheets/entry` | 路徑簡化，timesheetId 改由 request body 傳入 |
| `PUT /api/v1/timesheets/{id}/approve` | `POST /api/v1/timesheets/{id}/approve` | HTTP 方法由 PUT 改 POST |
| `PUT /api/v1/timesheets/{id}/reject` | `POST /api/v1/timesheets/{id}/reject` | HTTP 方法由 PUT 改 POST |
| `GET /api/v1/timesheets/{id}/entries` | `GET /api/v1/timesheets/{id}` | 查詢工時表詳情（含明細） |
| `GET /api/v1/timesheets/pending-approval` | `GET /api/v1/timesheets/approvals` | 路徑名稱變更 |
| 不存在 | `POST /api/v1/timesheets/{id}/lock` | 新增鎖定功能 |

---

## 3. 工時條目 Command API

### 3.1 新增工時條目

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/timesheets/entry` |
| Controller | `HR07TimesheetCmdController` |
| 方法名稱 | `createEntry` |
| Service | `CreateEntryServiceImpl` |
| 權限 | - (登入即可，限本人) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工在工時表中新增某日某專案的工時記錄 |
| 使用者 | 員工本人 |
| 頁面 | HR07-P01 我的工時回報頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工時表存在且為本人所有 |
| 2 | 驗證工時表狀態為 DRAFT 或 REJECTED |
| 3 | 驗證工時表未鎖定 |
| 4 | 驗證工作日期在週期範圍內 |
| 5 | 驗證不可回報未來日期 |
| 6 | 驗證同日同專案不可重複 |
| 7 | 驗證單日總工時不超過 24 小時 |
| 8 | 驗證專案存在且員工為專案成員 |
| 9 | 新增工時明細並重算總工時 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |
| Content-Type | Y | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| timesheetId | UUID | Y | 必須存在 | 工時表 ID | `"550e8400-e29b-41d4-a716-446655440000"` |
| projectId | UUID | Y | 必須存在且員工為成員 | 專案 ID | `"project-uuid-001"` |
| taskId | UUID | N | 若填寫須屬於該專案 | 任務 ID（WBS工項） | `"task-uuid-001"` |
| workDate | Date | Y | 在週期內、不可為未來 | 工作日期 | `"2025-11-25"` |
| hours | Decimal | Y | > 0 且 <= 24 | 工作時數 | `8.0` |
| description | String | N | 最長 500 字元 | 工作說明 | `"完成需求分析文件"` |

**範例：**
```json
{
  "timesheetId": "550e8400-e29b-41d4-a716-446655440000",
  "projectId": "550e8400-e29b-41d4-a716-446655440001",
  "taskId": "550e8400-e29b-41d4-a716-446655440002",
  "workDate": "2025-11-25",
  "hours": 8.0,
  "description": "完成需求分析文件"
}
```

**Response**

**成功回應 (201 Created)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| entryId | UUID | 工時明細 ID |
| projectId | UUID | 專案 ID |
| projectName | String | 專案名稱 |
| workDate | Date | 工作日期 |
| hours | Decimal | 工時 |

```json
{
  "code": "SUCCESS",
  "message": "工時已新增",
  "data": {
    "entryId": "550e8400-e29b-41d4-a716-446655440099",
    "projectId": "550e8400-e29b-41d4-a716-446655440001",
    "projectName": "PRJ-001 系統開發專案",
    "workDate": "2025-11-25",
    "hours": 8.0
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `TSH_TIMESHEET_NOT_FOUND` | 工時表不存在 | 確認工時表 ID |
| 403 | `TSH_NOT_OWNER` | 非工時表擁有者 | 只能編輯自己的工時表 |
| 400 | `TSH_TIMESHEET_LOCKED` | 工時表已鎖定 | 已鎖定的工時表無法修改 |
| 400 | `TSH_INVALID_STATUS` | 目前狀態不允許修改 | 只有 DRAFT 或 REJECTED 可修改 |
| 400 | `TSH_DATE_OUT_OF_RANGE` | 工作日期不在週期範圍內 | 選擇正確的日期 |
| 400 | `TSH_FUTURE_DATE` | 不可回報未來日期的工時 | 選擇今天或之前的日期 |
| 400 | `TSH_DUPLICATE_ENTRY` | 同日同專案已有工時記錄 | 編輯既有記錄或選擇其他專案 |
| 400 | `TSH_EXCEED_DAILY_LIMIT` | 單日工時不可超過 24 小時 | 調整工時數 |
| 400 | `TSH_INVALID_HOURS` | 工時必須大於 0 | 輸入正確的工時數 |
| 404 | `TSH_PROJECT_NOT_FOUND` | 專案不存在 | 確認專案 ID |
| 403 | `TSH_NOT_PROJECT_MEMBER` | 非專案成員 | 只能回報已加入專案的工時 |

---

### 3.2 更新工時條目

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/timesheets/{id}/entries/{entryId}` |
| Controller | `HR07TimesheetCmdController` |
| 方法名稱 | `updateTimesheetEntry` |
| Service | `UpdateTimesheetEntryServiceImpl` |
| 權限 | - (限本人) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工修改已填報的工時明細 |
| 使用者 | 員工本人 |
| 頁面 | HR07-P01、HR07-M01 工時填報對話框 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工時表與明細存在且為本人所有 |
| 2 | 驗證工時表狀態為 DRAFT 或 REJECTED |
| 3 | 驗證工時表未鎖定 |
| 4 | 驗證更新後單日總工時不超過 24 小時 |
| 5 | 更新工時明細並重算總工時 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |
| Content-Type | Y | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | Y | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |
| entryId | UUID | Y | 工時明細 ID | `550e8400-e29b-41d4-a716-446655440099` |

> **注意**：Controller 會將 path 中的 `id` 和 `entryId` 分別 set 到 request 的 `timesheetId` 和 `entryId` 欄位。

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| taskId | UUID | N | 若填寫須屬於該專案 | 任務 ID | `"task-uuid-001"` |
| hours | Decimal | Y | > 0 且 <= 24 | 工作時數 | `6.0` |
| description | String | N | 最長 500 字元 | 工作說明 | `"修訂需求分析文件"` |

**範例：**
```json
{
  "taskId": "550e8400-e29b-41d4-a716-446655440002",
  "hours": 6.0,
  "description": "修訂需求分析文件"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "工時已更新",
  "data": {
    "entryId": "550e8400-e29b-41d4-a716-446655440099",
    "hours": 6.0,
    "updatedAt": "2025-12-02T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `TSH_TIMESHEET_NOT_FOUND` | 工時表不存在 | 確認工時表 ID |
| 404 | `TSH_ENTRY_NOT_FOUND` | 工時明細不存在 | 確認明細 ID |
| 403 | `TSH_NOT_OWNER` | 非工時表擁有者 | 只能編輯自己的工時表 |
| 400 | `TSH_TIMESHEET_LOCKED` | 工時表已鎖定 | 已鎖定的工時表無法修改 |
| 400 | `TSH_INVALID_STATUS` | 目前狀態不允許修改 | 只有 DRAFT 或 REJECTED 可修改 |
| 400 | `TSH_EXCEED_DAILY_LIMIT` | 單日工時不可超過 24 小時 | 調整工時數 |
| 400 | `TSH_INVALID_HOURS` | 工時必須大於 0 | 輸入正確的工時數 |

---

### 3.3 刪除工時條目

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `DELETE /api/v1/timesheets/{id}/entries/{entryId}` |
| Controller | `HR07TimesheetCmdController` |
| 方法名稱 | `deleteTimesheetEntry` |
| Service | `DeleteTimesheetEntryServiceImpl` |
| 權限 | - (限本人) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工刪除錯誤填報的工時明細 |
| 使用者 | 員工本人 |
| 頁面 | HR07-P01 我的工時回報頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工時表與明細存在且為本人所有 |
| 2 | 驗證工時表狀態為 DRAFT 或 REJECTED |
| 3 | 驗證工時表未鎖定 |
| 4 | 刪除工時明細並重算總工時 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | Y | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |
| entryId | UUID | Y | 工時明細 ID | `550e8400-e29b-41d4-a716-446655440099` |

> **注意**：Controller 不接收 request body，而是以 path parameters 建構 `DeleteTimesheetEntryRequest`（設定 `timesheetId` 和 `entryId`）。

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "工時已刪除"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `TSH_TIMESHEET_NOT_FOUND` | 工時表不存在 | 確認工時表 ID |
| 404 | `TSH_ENTRY_NOT_FOUND` | 工時明細不存在 | 確認明細 ID |
| 403 | `TSH_NOT_OWNER` | 非工時表擁有者 | 只能刪除自己的工時 |
| 400 | `TSH_TIMESHEET_LOCKED` | 工時表已鎖定 | 已鎖定的工時表無法修改 |
| 400 | `TSH_INVALID_STATUS` | 目前狀態不允許修改 | 只有 DRAFT 或 REJECTED 可修改 |

---

## 4. 工時表 Command API

### 4.1 提交工時表審核

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/timesheets/submit` |
| Controller | `HR07TimesheetCmdController` |
| 方法名稱 | `submitTimesheet` |
| Service | `SubmitTimesheetServiceImpl` |
| 權限 | - (限本人) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工完成工時填報後，提交給 PM 審核 |
| 使用者 | 員工本人 |
| 頁面 | HR07-P01 我的工時回報頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工時表存在且為本人所有 |
| 2 | 驗證狀態為 DRAFT 或 REJECTED |
| 3 | 驗證至少有一筆工時明細 |
| 4 | 更新狀態為 SUBMITTED，記錄提交時間 |
| 5 | 發布 TimesheetSubmittedEvent |
| 6 | 通知相關專案的 PM |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |
| Content-Type | Y | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| timesheetId | UUID | Y | 工時表 ID | `"550e8400-e29b-41d4-a716-446655440000"` |

**範例：**
```json
{
  "timesheetId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "工時已提交審核"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `TSH_TIMESHEET_NOT_FOUND` | 工時表不存在 | 確認工時表 ID |
| 403 | `TSH_NOT_OWNER` | 非工時表擁有者 | 只能提交自己的工時表 |
| 400 | `TSH_INVALID_STATUS_FOR_SUBMIT` | 目前狀態不允許提交 | 只有 DRAFT 或 REJECTED 可提交 |
| 400 | `TSH_NO_ENTRIES` | 至少需要一筆工時記錄 | 新增工時明細後再提交 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| TimesheetSubmittedEvent | `timesheet.submitted` | 工時提交審核，通知 PM |

---

## 5. 工時審核 Command API

### 5.1 核准工時表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/timesheets/{id}/approve` |
| Controller | `HR07TimesheetCmdController` |
| 方法名稱 | `approveTimesheet` |
| Service | `ApproveTimesheetServiceImpl` |
| 權限 | `timesheet:approve` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | PM 審核員工提交的工時表，核准後工時將計入專案實際工時 |
| 使用者 | 專案經理 (PM)、部門主管 |
| 頁面 | HR07-P02 工時審核頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工時表存在 |
| 2 | 驗證狀態為 SUBMITTED |
| 3 | 驗證審核者有權限審核（為相關專案的 PM） |
| 4 | 更新狀態為 APPROVED，記錄核准者與時間 |
| 5 | 發布 TimesheetApprovedEvent |
| 6 | 通知員工審核結果 |
| 7 | 更新專案實際工時統計 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | Y | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |

> **注意**：Controller 不接收 request body，而是以 path parameter 建構 `ApproveTimesheetRequest`（設定 `timesheetId`）。

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "工時已核准"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `TSH_TIMESHEET_NOT_FOUND` | 工時表不存在 | 確認工時表 ID |
| 400 | `TSH_INVALID_STATUS_FOR_APPROVE` | 只有已提交狀態可核准 | 確認工時表狀態 |
| 403 | `TSH_NO_APPROVAL_PERMISSION` | 無權限審核此工時表 | 需為相關專案的 PM |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| TimesheetApprovedEvent | `timesheet.approved` | 工時核准，通知專案服務更新實際工時 |

**事件 Payload：**
```json
{
  "eventId": "evt-uuid-001",
  "eventType": "TimesheetApprovedEvent",
  "occurredAt": "2025-12-03T14:00:00Z",
  "aggregateId": "timesheet-uuid",
  "aggregateType": "Timesheet",
  "payload": {
    "timesheetId": "timesheet-uuid",
    "employeeId": "employee-uuid",
    "approvedBy": "manager-uuid",
    "projectHours": {
      "project-uuid-1": 28.0,
      "project-uuid-2": 12.0
    }
  }
}
```

---

### 5.2 批次核准工時表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/timesheets/batch-approve` |
| Controller | `HR07TimesheetCmdController` |
| 方法名稱 | `batchApproveTimesheet` |
| Service | `BatchApproveTimesheetServiceImpl` |
| 權限 | `timesheet:approve` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | PM 一次核准多筆工時表，提升審核效率 |
| 使用者 | 專案經理 (PM)、部門主管 |
| 頁面 | HR07-P02 工時審核頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證所有工時表存在 |
| 2 | 驗證所有工時表狀態為 SUBMITTED |
| 3 | 驗證審核者有權限審核所有工時表 |
| 4 | 逐一核准並發布事件 |
| 5 | 回傳成功與失敗的統計 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |
| Content-Type | Y | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| timesheetIds | UUID[] | Y | 1~100 個 | 工時表 ID 列表 | `["uuid-1", "uuid-2"]` |

**範例：**
```json
{
  "timesheetIds": [
    "550e8400-e29b-41d4-a716-446655440001",
    "550e8400-e29b-41d4-a716-446655440002",
    "550e8400-e29b-41d4-a716-446655440003"
  ]
}
```

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| totalCount | Integer | 總筆數 |
| successCount | Integer | 成功筆數 |
| failedCount | Integer | 失敗筆數 |
| failed | Object[] | 失敗詳情 |

```json
{
  "code": "SUCCESS",
  "message": "批次核准完成",
  "data": {
    "totalCount": 3,
    "successCount": 2,
    "failedCount": 1,
    "failed": [
      {
        "timesheetId": "550e8400-e29b-41d4-a716-446655440003",
        "errorCode": "TSH_INVALID_STATUS_FOR_APPROVE",
        "errorMessage": "只有已提交狀態可核准"
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `TSH_BATCH_EMPTY` | 至少選擇一筆工時表 | 選擇要核准的工時表 |
| 400 | `TSH_BATCH_LIMIT_EXCEEDED` | 單次最多 100 筆 | 分批處理 |

---

### 5.3 退回工時表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/timesheets/{id}/reject` |
| Controller | `HR07TimesheetCmdController` |
| 方法名稱 | `rejectTimesheet` |
| Service | `RejectTimesheetServiceImpl` |
| 權限 | `timesheet:approve` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | PM 審核發現工時有問題，駁回給員工修改 |
| 使用者 | 專案經理 (PM)、部門主管 |
| 頁面 | HR07-P02 工時審核頁面、HR07-M02 駁回原因對話框 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工時表存在 |
| 2 | 驗證狀態為 SUBMITTED |
| 3 | 驗證駁回原因不可為空 |
| 4 | 更新狀態為 REJECTED，記錄駁回者、時間與原因 |
| 5 | 發布 TimesheetRejectedEvent |
| 6 | 通知員工駁回原因 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |
| Content-Type | Y | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | Y | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |

> **注意**：Controller 會將 path 中的 `id` set 到 request 的 `timesheetId` 欄位。

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| reason | String | Y | 最長 500 字元 | 駁回原因 | `"工時與差勤記錄不符，請確認11/26是否有請假"` |

**範例：**
```json
{
  "reason": "工時與差勤記錄不符，請確認11/26是否有請假"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "工時已駁回"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `TSH_TIMESHEET_NOT_FOUND` | 工時表不存在 | 確認工時表 ID |
| 400 | `TSH_INVALID_STATUS_FOR_REJECT` | 只有已提交狀態可駁回 | 確認工時表狀態 |
| 400 | `TSH_REJECTION_REASON_REQUIRED` | 駁回原因不可為空 | 填寫駁回原因 |
| 403 | `TSH_NO_APPROVAL_PERMISSION` | 無權限審核此工時表 | 需為相關專案的 PM |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| TimesheetRejectedEvent | `timesheet.rejected` | 工時駁回，通知員工修改 |

**事件 Payload：**
```json
{
  "eventId": "evt-uuid-002",
  "eventType": "TimesheetRejectedEvent",
  "occurredAt": "2025-12-03T14:00:00Z",
  "aggregateId": "timesheet-uuid",
  "aggregateType": "Timesheet",
  "payload": {
    "timesheetId": "timesheet-uuid",
    "employeeId": "employee-uuid",
    "rejectedBy": "manager-uuid",
    "reason": "工時與差勤記錄不符，請確認11/26是否有請假"
  }
}
```

---

## 6. 工時鎖定 Command API

### 6.1 鎖定工時表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/timesheets/{id}/lock` |
| Controller | `HR07TimesheetCmdController` |
| 方法名稱 | `lockTimesheet` |
| Service | `LockTimesheetServiceImpl` |
| 權限 | 系統/管理員 |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 薪資結算後鎖定工時表，防止事後修改已計入薪資的工時記錄 |
| 使用者 | 系統自動（薪資結算流程觸發）、管理員手動操作 |
| 頁面 | 系統排程 / 管理後台 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工時表存在 |
| 2 | 驗證工時表狀態為 APPROVED（已核准才可鎖定） |
| 3 | 更新狀態為 LOCKED |
| 4 | 發布 TimesheetLockedEvent |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | String | Y | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |

> **注意**：Controller 不接收 request body，而是以 path parameter 建構 `LockTimesheetRequest`（設定 `timesheetId`，型別為 String）。

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "工時表已鎖定"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `TSH_TIMESHEET_NOT_FOUND` | 工時表不存在 | 確認工時表 ID |
| 400 | `TSH_INVALID_STATUS_FOR_LOCK` | 只有已核准狀態可鎖定 | 確認工時表狀態 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| TimesheetLockedEvent | `timesheet.locked` | 工時鎖定，薪資結算後觸發 |

---

## 7. 工時查詢 API

### 7.1 查詢我的工時表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/my` |
| Controller | `HR07TimesheetQryController` |
| 方法名稱 | `getMyTimesheet` |
| Service | `GetMyTimesheetServiceImpl` |
| 權限 | - (登入即可) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工查詢自己指定週次的工時表及明細 |
| 使用者 | 所有員工 |
| 頁面 | HR07-P01 我的工時回報頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 根據 week 參數計算週期起訖日 |
| 2 | 查詢員工該週期的工時表 |
| 3 | 若不存在則回傳空資料（前端可顯示建立按鈕） |
| 4 | 回傳工時表及所有明細 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |

**Query Parameters**（透過 `@ModelAttribute GetMyTimesheetRequest` 接收）

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| week | String | N | 當前週次 | 週次 (ISO 週格式) | `2025-W48` |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| timesheetId | UUID | 工時表 ID（若無則為 null） |
| periodStartDate | Date | 週期開始日 |
| periodEndDate | Date | 週期結束日 |
| totalHours | Decimal | 總工時 |
| status | Enum | 狀態 |
| entries | Entry[] | 工時明細列表 |

```json
{
  "code": "SUCCESS",
  "data": {
    "timesheetId": "550e8400-e29b-41d4-a716-446655440000",
    "periodStartDate": "2025-11-25",
    "periodEndDate": "2025-12-01",
    "totalHours": 40.0,
    "status": "DRAFT",
    "submittedAt": null,
    "approvedAt": null,
    "rejectionReason": null,
    "entries": [
      {
        "entryId": "entry-uuid-001",
        "projectId": "project-uuid-001",
        "projectName": "PRJ-001 系統開發專案",
        "taskId": "task-uuid-001",
        "taskName": "需求分析",
        "workDate": "2025-11-25",
        "hours": 8.0,
        "description": "完成需求分析文件"
      },
      {
        "entryId": "entry-uuid-002",
        "projectId": "project-uuid-001",
        "projectName": "PRJ-001 系統開發專案",
        "taskId": "task-uuid-002",
        "taskName": "系統設計",
        "workDate": "2025-11-26",
        "hours": 8.0,
        "description": "進行系統架構設計"
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `TSH_INVALID_WEEK_FORMAT` | 週次格式錯誤 | 使用 YYYY-Www 格式 |

---

### 7.2 查詢待簽核列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/approvals` |
| Controller | `HR07TimesheetQryController` |
| 方法名稱 | `getPendingApprovals` |
| Service | `GetPendingApprovalsServiceImpl` |
| 權限 | `timesheet:approve` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | PM 查看待審核的工時表列表 |
| 使用者 | 專案經理 (PM)、部門主管 |
| 頁面 | HR07-P02 工時審核頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 根據審核者權限篩選可審核的工時表 |
| 2 | 僅顯示狀態為 SUBMITTED 的工時表 |
| 3 | 支援依員工、專案篩選 |
| 4 | 支援分頁 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |

**Query Parameters**（透過 `@ModelAttribute GetPendingApprovalsRequest` 接收）

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| employeeId | UUID | N | - | 員工 ID 篩選 | `employee-uuid` |
| projectId | UUID | N | - | 專案 ID 篩選 | `project-uuid` |
| page | Integer | N | 1 | 頁碼 | `1` |
| size | Integer | N | 10 | 每頁筆數 | `20` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "content": [
      {
        "timesheetId": "550e8400-e29b-41d4-a716-446655440001",
        "employeeId": "employee-uuid-001",
        "employeeName": "張三",
        "departmentName": "研發部",
        "periodStartDate": "2025-11-25",
        "periodEndDate": "2025-12-01",
        "totalHours": 40.0,
        "submittedAt": "2025-12-02T09:30:00Z",
        "projectSummary": [
          {"projectCode": "PRJ-001", "projectName": "系統開發專案", "hours": 28.0},
          {"projectCode": "PRJ-002", "projectName": "維護專案", "hours": 12.0}
        ]
      },
      {
        "timesheetId": "550e8400-e29b-41d4-a716-446655440002",
        "employeeId": "employee-uuid-002",
        "employeeName": "李四",
        "departmentName": "研發部",
        "periodStartDate": "2025-11-25",
        "periodEndDate": "2025-12-01",
        "totalHours": 32.0,
        "submittedAt": "2025-12-02T10:15:00Z",
        "projectSummary": [
          {"projectCode": "PRJ-001", "projectName": "系統開發專案", "hours": 32.0}
        ]
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 15,
    "totalPages": 2
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 403 | `AUTHZ_PERMISSION_DENIED` | 無審核權限 | 需有 timesheet:approve 權限 |

---

### 7.3 查詢工時表詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/{id}` |
| Controller | `HR07TimesheetQryController` |
| 方法名稱 | `getTimesheet` |
| Service | `GetTimesheetDetailServiceImpl` |
| 權限 | - (本人) 或 `timesheet:read:all` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查詢指定工時表的完整詳細資訊（含所有明細） |
| 使用者 | 員工本人、專案經理 |
| 頁面 | HR07-P01、HR07-P02 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工時表存在 |
| 2 | 驗證查詢者為本人或有讀取權限 |
| 3 | 回傳工時表完整資訊（含所有明細） |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | String | Y | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |

> **注意**：Controller 以 path parameter 建構 `GetTimesheetDetailRequest`（設定 `timesheetId`，型別為 String）。

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "timesheetId": "550e8400-e29b-41d4-a716-446655440000",
    "employeeId": "employee-uuid",
    "employeeName": "張三",
    "periodStartDate": "2025-11-25",
    "periodEndDate": "2025-12-01",
    "totalHours": 40.0,
    "status": "SUBMITTED",
    "submittedAt": "2025-12-02T09:30:00Z",
    "approvedAt": null,
    "rejectionReason": null,
    "entries": [
      {
        "entryId": "entry-uuid-001",
        "projectId": "project-uuid-001",
        "projectName": "PRJ-001 系統開發專案",
        "projectCode": "PRJ-001",
        "taskId": "task-uuid-001",
        "taskName": "需求分析",
        "workDate": "2025-11-25",
        "hours": 8.0,
        "description": "完成需求分析文件"
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `TSH_TIMESHEET_NOT_FOUND` | 工時表不存在 | 確認工時表 ID |
| 403 | `TSH_ACCESS_DENIED` | 無權限查看此工時表 | 需為本人或有讀取權限 |

---

## 8. 工時報表 API

### 8.1 個人/部門工時統計

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/summary` |
| Controller | `HR07TimesheetReportQryController` |
| 方法名稱 | `getTimesheetSummary` |
| Service | `GetTimesheetSummaryServiceImpl` |
| 權限 | `timesheet:report:read` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查詢指定期間的工時統計摘要 |
| 使用者 | HR、部門主管、專案經理 |
| 頁面 | HR07-P03 工時統計報表頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 根據日期區間查詢已核准的工時 |
| 2 | 彙總總工時、專案工時、平均日工時 |
| 3 | 統計未回報人數 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |

**Query Parameters**（透過 `@ModelAttribute GetTimesheetSummaryRequest` 接收）

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| startDate | Date | Y | - | 開始日期 | `2025-11-01` |
| endDate | Date | Y | - | 結束日期 | `2025-11-30` |
| departmentId | UUID | N | - | 部門篩選 | `dept-uuid` |
| employeeId | UUID | N | - | 員工篩選 | `emp-uuid` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "period": {
      "startDate": "2025-11-01",
      "endDate": "2025-11-30"
    },
    "summary": {
      "totalHours": 4800.0,
      "projectHours": 4200.0,
      "averageDailyHours": 7.5,
      "unreportedEmployeeCount": 3
    },
    "byDepartment": [
      {
        "departmentId": "dept-uuid-001",
        "departmentName": "研發部",
        "totalHours": 3200.0,
        "employeeCount": 15
      },
      {
        "departmentId": "dept-uuid-002",
        "departmentName": "設計部",
        "totalHours": 1600.0,
        "employeeCount": 8
      }
    ],
    "byEmployee": [
      {
        "employeeId": "emp-uuid-001",
        "employeeName": "張三",
        "totalHours": 168.0,
        "projectCount": 2
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `TSH_INVALID_DATE_RANGE` | 日期區間無效 | 確認開始日期早於結束日期 |
| 400 | `TSH_DATE_RANGE_TOO_LARGE` | 日期區間過大（最多 1 年） | 縮小查詢區間 |
| 403 | `AUTHZ_PERMISSION_DENIED` | 無報表權限 | 需有 timesheet:report:read 權限 |

---

### 8.2 專案工時統計

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/project-summary` |
| Controller | `HR07TimesheetReportQryController` |
| 方法名稱 | `getProjectTimesheetSummary` |
| Service | `GetProjectTimesheetSummaryServiceImpl` |
| 權限 | `timesheet:report:read` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 依專案維度統計工時，用於成本分析 |
| 使用者 | HR、部門主管、專案經理 |
| 頁面 | HR07-P03、HR07-P04 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 根據日期區間查詢已核准的工時 |
| 2 | 依專案彙總工時 |
| 3 | 計算預算消耗率（需整合專案服務） |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |

**Query Parameters**（透過 `@ModelAttribute GetProjectTimesheetSummaryRequest` 接收）

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| startDate | Date | Y | - | 開始日期 | `2025-11-01` |
| endDate | Date | Y | - | 結束日期 | `2025-11-30` |
| projectId | UUID | N | - | 專案篩選 | `project-uuid` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "period": {
      "startDate": "2025-11-01",
      "endDate": "2025-11-30"
    },
    "projects": [
      {
        "projectId": "project-uuid-001",
        "projectCode": "PRJ-001",
        "projectName": "系統開發專案",
        "totalHours": 560.0,
        "budgetHours": 800.0,
        "budgetConsumption": 70.0,
        "memberCount": 5,
        "byMember": [
          {
            "employeeId": "emp-uuid-001",
            "employeeName": "張三",
            "hours": 120.0
          },
          {
            "employeeId": "emp-uuid-002",
            "employeeName": "李四",
            "hours": 100.0
          }
        ]
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `TSH_INVALID_DATE_RANGE` | 日期區間無效 | 確認開始日期早於結束日期 |
| 403 | `AUTHZ_PERMISSION_DENIED` | 無報表權限 | 需有 timesheet:report:read 權限 |

---

### 8.3 未回報員工列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/unreported` |
| Controller | `HR07TimesheetReportQryController` |
| 方法名稱 | `getUnreportedEmployees` |
| Service | `GetUnreportedEmployeesServiceImpl` |
| 權限 | `timesheet:report:read` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查詢指定週次尚未回報工時的員工 |
| 使用者 | HR、部門主管 |
| 頁面 | HR07-P03 工時統計報表頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 取得所有需回報工時的員工（不含離職、留停） |
| 2 | 篩選指定週次未建立工時表或狀態為 DRAFT 的員工 |
| 3 | 回傳未回報員工清單 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | Y | `Bearer {accessToken}` |

**Query Parameters**（透過 `@ModelAttribute GetUnreportedEmployeesRequest` 接收）

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| week | String | N | 上週 | 週次 (ISO 週格式) | `2025-W48` |
| departmentId | UUID | N | - | 部門篩選 | `dept-uuid` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "week": "2025-W48",
    "periodStartDate": "2025-11-25",
    "periodEndDate": "2025-12-01",
    "unreportedCount": 3,
    "employees": [
      {
        "employeeId": "emp-uuid-003",
        "employeeName": "王五",
        "departmentName": "研發部",
        "email": "wang.wu@company.com",
        "status": "NOT_CREATED"
      },
      {
        "employeeId": "emp-uuid-004",
        "employeeName": "趙六",
        "departmentName": "設計部",
        "email": "zhao.liu@company.com",
        "status": "DRAFT"
      }
    ]
  }
}
```

**回報狀態說明**

| 狀態 | 說明 |
|:---|:---|
| `NOT_CREATED` | 尚未建立工時表 |
| `DRAFT` | 工時表為草稿，尚未提交 |

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `TSH_INVALID_WEEK_FORMAT` | 週次格式錯誤 | 使用 YYYY-Www 格式 |
| 403 | `AUTHZ_PERMISSION_DENIED` | 無報表權限 | 需有 timesheet:report:read 權限 |

---

## 附錄 A：工時狀態流轉

```
                 +------------------+
                 |                  |
                 v                  |
+--------+    +----------+    +----------+    +--------+
| DRAFT  |--->|SUBMITTED |--->| APPROVED |--->| LOCKED |
+--------+    +----------+    +----------+    +--------+
     ^              |
     |              |
     |        +-----+-----+
     |        v           |
     |   +----------+     |
     +---|REJECTED  |-----+
         +----------+
```

| 狀態 | 說明 | 可修改 | 狀態轉換觸發端點 |
|:---|:---|:---:|:---|
| DRAFT | 草稿 | Y | （初始狀態） |
| SUBMITTED | 已提交 | N | `POST /api/v1/timesheets/submit` |
| APPROVED | 已核准 | N | `POST /api/v1/timesheets/{id}/approve` |
| REJECTED | 已駁回 | Y | `POST /api/v1/timesheets/{id}/reject` |
| LOCKED | 已鎖定 | N | `POST /api/v1/timesheets/{id}/lock` |

---

## 附錄 B：錯誤碼總覽

| 錯誤碼 | HTTP | 說明 |
|:---|:---:|:---|
| `TSH_TIMESHEET_NOT_FOUND` | 404 | 工時表不存在 |
| `TSH_ENTRY_NOT_FOUND` | 404 | 工時明細不存在 |
| `TSH_PROJECT_NOT_FOUND` | 404 | 專案不存在 |
| `TSH_NOT_OWNER` | 403 | 非工時表擁有者 |
| `TSH_NOT_PROJECT_MEMBER` | 403 | 非專案成員 |
| `TSH_NO_APPROVAL_PERMISSION` | 403 | 無審核權限 |
| `TSH_ACCESS_DENIED` | 403 | 無權限存取 |
| `TSH_TIMESHEET_EXISTS` | 409 | 工時表已存在 |
| `TSH_TIMESHEET_LOCKED` | 400 | 工時表已鎖定 |
| `TSH_INVALID_STATUS` | 400 | 目前狀態不允許此操作 |
| `TSH_INVALID_STATUS_FOR_SUBMIT` | 400 | 目前狀態不允許提交 |
| `TSH_INVALID_STATUS_FOR_APPROVE` | 400 | 目前狀態不允許核准 |
| `TSH_INVALID_STATUS_FOR_REJECT` | 400 | 目前狀態不允許駁回 |
| `TSH_INVALID_STATUS_FOR_LOCK` | 400 | 目前狀態不允許鎖定 |
| `TSH_INVALID_PERIOD_START` | 400 | 週期開始日必須為週一 |
| `TSH_FUTURE_PERIOD` | 400 | 不可建立未來週次 |
| `TSH_DATE_OUT_OF_RANGE` | 400 | 工作日期不在週期範圍內 |
| `TSH_FUTURE_DATE` | 400 | 不可回報未來日期 |
| `TSH_DUPLICATE_ENTRY` | 400 | 同日同專案重複 |
| `TSH_EXCEED_DAILY_LIMIT` | 400 | 單日超過 24 小時 |
| `TSH_INVALID_HOURS` | 400 | 工時必須大於 0 |
| `TSH_NO_ENTRIES` | 400 | 至少需要一筆工時 |
| `TSH_REJECTION_REASON_REQUIRED` | 400 | 駁回原因必填 |
| `TSH_BATCH_EMPTY` | 400 | 批次操作至少選擇一筆 |
| `TSH_BATCH_LIMIT_EXCEEDED` | 400 | 批次操作超過上限 |
| `TSH_INVALID_WEEK_FORMAT` | 400 | 週次格式錯誤 |
| `TSH_INVALID_DATE_RANGE` | 400 | 日期區間無效 |
| `TSH_DATE_RANGE_TOO_LARGE` | 400 | 日期區間過大 |

---

## 附錄 C：領域事件總覽

| 事件名稱 | Topic | 觸發時機 | 觸發端點 | 訂閱服務 |
|:---|:---|:---|:---|:---|
| TimesheetSubmittedEvent | `timesheet.submitted` | 提交審核 | `POST /submit` | Workflow, Notification |
| TimesheetApprovedEvent | `timesheet.approved` | 核准工時 | `POST /{id}/approve` | Project, Payroll, Notification |
| TimesheetRejectedEvent | `timesheet.rejected` | 駁回工時 | `POST /{id}/reject` | Notification |
| TimesheetLockedEvent | `timesheet.locked` | 鎖定工時（薪資結算後） | `POST /{id}/lock` | - |

---

**文件建立日期:** 2025-12-30
**最後更新日期:** 2026-03-16
**版本:** 2.0
