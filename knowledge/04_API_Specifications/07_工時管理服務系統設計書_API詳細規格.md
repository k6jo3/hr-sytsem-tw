# 工時管理服務 API 詳細規格

**版本:** 1.0
**日期:** 2025-12-30
**Domain代號:** 07 (TSH)
**服務名稱:** hrms-timesheet

---

## 目錄

1. [Controller命名對照](#1-controller命名對照)
2. [API總覽](#2-api總覽)
3. [工時表管理 API](#3-工時表管理-api)
4. [工時明細 API](#4-工時明細-api)
5. [工時審核 API](#5-工時審核-api)
6. [工時查詢 API](#6-工時查詢-api)
7. [工時報表 API](#7-工時報表-api)

---

## 1. Controller命名對照

| Controller | 說明 |
|:---|:---|
| `HR07TimesheetCmdController` | 工時表 Command 操作 |
| `HR07TimesheetQryController` | 工時表 Query 操作 |
| `HR07EntryCmdController` | 工時明細 Command 操作 |
| `HR07ApprovalCmdController` | 審核 Command 操作 |
| `HR07ApprovalQryController` | 審核 Query 操作 |
| `HR07ReportQryController` | 報表 Query 操作 |

---

## 2. API總覽

### 2.1 端點清單 (14個端點)

| 端點 | 方法 | Controller | 說明 | 權限 |
|:---|:---:|:---|:---|:---|
| `/api/v1/timesheets` | POST | HR07TimesheetCmdController | 建立工時表 | - |
| `/api/v1/timesheets/{id}/submit` | PUT | HR07TimesheetCmdController | 提交審核 | - |
| `/api/v1/timesheets/{id}/entries` | POST | HR07EntryCmdController | 新增工時明細 | - |
| `/api/v1/timesheets/{id}/entries/{entryId}` | PUT | HR07EntryCmdController | 更新工時明細 | - |
| `/api/v1/timesheets/{id}/entries/{entryId}` | DELETE | HR07EntryCmdController | 刪除工時明細 | - |
| `/api/v1/timesheets/{id}/approve` | PUT | HR07ApprovalCmdController | 核准工時 | timesheet:approve |
| `/api/v1/timesheets/{id}/reject` | PUT | HR07ApprovalCmdController | 駁回工時 | timesheet:approve |
| `/api/v1/timesheets/batch-approve` | PUT | HR07ApprovalCmdController | 批次核准 | timesheet:approve |
| `/api/v1/timesheets/my` | GET | HR07TimesheetQryController | 查詢我的工時 | - |
| `/api/v1/timesheets/{id}/entries` | GET | HR07TimesheetQryController | 查詢工時明細 | - |
| `/api/v1/timesheets/pending-approval` | GET | HR07ApprovalQryController | 查詢待審核列表 | timesheet:approve |
| `/api/v1/timesheets/summary` | GET | HR07ReportQryController | 個人工時統計 | timesheet:report:read |
| `/api/v1/timesheets/project-summary` | GET | HR07ReportQryController | 專案工時統計 | timesheet:report:read |
| `/api/v1/timesheets/unreported` | GET | HR07ReportQryController | 未回報員工列表 | timesheet:report:read |

---

## 3. 工時表管理 API

### 3.1 建立工時表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/timesheets` |
| Controller | `HR07TimesheetCmdController` |
| Service | `CreateTimesheetServiceImpl` |
| 權限 | - (登入即可) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工建立新的週工時表，作為工時回報的容器 |
| 使用者 | 所有員工 |
| 頁面 | HR07-P01 我的工時回報頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證週次是否已有工時表（同一員工同一週期不可重複） |
| 2 | 計算週期起訖日（週一至週日） |
| 3 | 建立工時表，狀態為 DRAFT |
| 4 | 發布 TimesheetCreatedEvent |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| periodStartDate | Date | ✅ | 必須為週一、不可為未來週次 | 週期開始日 | `"2025-11-25"` |

**範例：**
```json
{
  "periodStartDate": "2025-11-25"
}
```

**Response**

**成功回應 (201 Created)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| timesheetId | UUID | 工時表 ID |
| periodStartDate | Date | 週期開始日 |
| periodEndDate | Date | 週期結束日 |
| status | Enum | 狀態 (DRAFT) |
| createdAt | DateTime | 建立時間 |

```json
{
  "code": "SUCCESS",
  "message": "工時表建立成功",
  "data": {
    "timesheetId": "550e8400-e29b-41d4-a716-446655440000",
    "periodStartDate": "2025-11-25",
    "periodEndDate": "2025-12-01",
    "status": "DRAFT",
    "createdAt": "2025-12-02T09:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `TSH_INVALID_PERIOD_START` | 週期開始日必須為週一 | 選擇正確的週一日期 |
| 400 | `TSH_FUTURE_PERIOD` | 不可建立未來週次的工時表 | 選擇當前或過去週次 |
| 409 | `TSH_TIMESHEET_EXISTS` | 該週次工時表已存在 | 查詢並編輯既有工時表 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| TimesheetCreatedEvent | `timesheet.created` | 工時表建立完成 |

---

### 3.2 提交審核

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/timesheets/{id}/submit` |
| Controller | `HR07TimesheetCmdController` |
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
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |

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

## 4. 工時明細 API

### 4.1 新增工時明細

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/timesheets/{id}/entries` |
| Controller | `HR07EntryCmdController` |
| Service | `CreateEntryServiceImpl` |
| 權限 | - (限本人) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工在工時表中新增某日某專案的工時記錄 |
| 使用者 | 員工本人 |
| 頁面 | HR07-P01、HR07-M01 工時填報對話框 |

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
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| projectId | UUID | ✅ | 必須存在且員工為成員 | 專案 ID | `"project-uuid-001"` |
| taskId | UUID | ⬚ | 若填寫須屬於該專案 | 任務 ID（WBS工項） | `"task-uuid-001"` |
| workDate | Date | ✅ | 在週期內、不可為未來 | 工作日期 | `"2025-11-25"` |
| hours | Decimal | ✅ | > 0 且 <= 24 | 工作時數 | `8.0` |
| description | String | ⬚ | 最長 500 字元 | 工作說明 | `"完成需求分析文件"` |

**範例：**
```json
{
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

### 4.2 更新工時明細

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/timesheets/{id}/entries/{entryId}` |
| Controller | `HR07EntryCmdController` |
| Service | `UpdateEntryServiceImpl` |
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
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |
| entryId | UUID | ✅ | 工時明細 ID | `550e8400-e29b-41d4-a716-446655440099` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| taskId | UUID | ⬚ | 若填寫須屬於該專案 | 任務 ID | `"task-uuid-001"` |
| hours | Decimal | ✅ | > 0 且 <= 24 | 工作時數 | `6.0` |
| description | String | ⬚ | 最長 500 字元 | 工作說明 | `"修訂需求分析文件"` |

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

### 4.3 刪除工時明細

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `DELETE /api/v1/timesheets/{id}/entries/{entryId}` |
| Controller | `HR07EntryCmdController` |
| Service | `DeleteEntryServiceImpl` |
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
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |
| entryId | UUID | ✅ | 工時明細 ID | `550e8400-e29b-41d4-a716-446655440099` |

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

## 5. 工時審核 API

### 5.1 核准工時

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/timesheets/{id}/approve` |
| Controller | `HR07ApprovalCmdController` |
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
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |

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

### 5.2 駁回工時

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/timesheets/{id}/reject` |
| Controller | `HR07ApprovalCmdController` |
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
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| reason | String | ✅ | 最長 500 字元 | 駁回原因 | `"工時與差勤記錄不符，請確認11/26是否有請假"` |

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

### 5.3 批次核准

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/timesheets/batch-approve` |
| Controller | `HR07ApprovalCmdController` |
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
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| timesheetIds | UUID[] | ✅ | 1~100 個 | 工時表 ID 列表 | `["uuid-1", "uuid-2"]` |

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

## 6. 工時查詢 API

### 6.1 查詢我的工時

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/my` |
| Controller | `HR07TimesheetQryController` |
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
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| week | String | ⬚ | 當前週次 | 週次 (ISO 週格式) | `2025-W48` |

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

### 6.2 查詢工時明細

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/{id}/entries` |
| Controller | `HR07TimesheetQryController` |
| Service | `GetEntriesServiceImpl` |
| 權限 | - (本人) 或 `timesheet:read:all` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查詢指定工時表的所有明細（審核時查看詳情） |
| 使用者 | 員工本人、專案經理 |
| 頁面 | HR07-P01、HR07-P02 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工時表存在 |
| 2 | 驗證查詢者為本人或有讀取權限 |
| 3 | 回傳所有工時明細 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 工時表 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "timesheetId": "550e8400-e29b-41d4-a716-446655440000",
    "employeeId": "employee-uuid",
    "employeeName": "張三",
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

### 6.3 查詢待審核列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/pending-approval` |
| Controller | `HR07ApprovalQryController` |
| Service | `GetPendingApprovalServiceImpl` |
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
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| employeeId | UUID | ⬚ | - | 員工 ID 篩選 | `employee-uuid` |
| projectId | UUID | ⬚ | - | 專案 ID 篩選 | `project-uuid` |
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `20` |

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

## 7. 工時報表 API

### 7.1 個人工時統計

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/summary` |
| Controller | `HR07ReportQryController` |
| Service | `GetSummaryServiceImpl` |
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
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| startDate | Date | ✅ | - | 開始日期 | `2025-11-01` |
| endDate | Date | ✅ | - | 結束日期 | `2025-11-30` |
| departmentId | UUID | ⬚ | - | 部門篩選 | `dept-uuid` |
| employeeId | UUID | ⬚ | - | 員工篩選 | `emp-uuid` |

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

### 7.2 專案工時統計

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/project-summary` |
| Controller | `HR07ReportQryController` |
| Service | `GetProjectSummaryServiceImpl` |
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
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| startDate | Date | ✅ | - | 開始日期 | `2025-11-01` |
| endDate | Date | ✅ | - | 結束日期 | `2025-11-30` |
| projectId | UUID | ⬚ | - | 專案篩選 | `project-uuid` |

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

### 7.3 未回報員工列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/timesheets/unreported` |
| Controller | `HR07ReportQryController` |
| Service | `GetUnreportedServiceImpl` |
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
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| week | String | ⬚ | 上週 | 週次 (ISO 週格式) | `2025-W48` |
| departmentId | UUID | ⬚ | - | 部門篩選 | `dept-uuid` |

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
                 ┌──────────────────┐
                 │                  │
                 ▼                  │
┌────────┐    ┌────────┐    ┌──────────┐    ┌────────┐
│ DRAFT  │───▶│SUBMITTED│───▶│ APPROVED │───▶│ LOCKED │
└────────┘    └────────┘    └──────────┘    └────────┘
     ▲              │
     │              │
     │        ┌─────┴─────┐
     │        ▼           │
     │   ┌────────┐       │
     └───│REJECTED│───────┘
         └────────┘
```

| 狀態 | 說明 | 可修改 |
|:---|:---|:---:|
| DRAFT | 草稿 | ✅ |
| SUBMITTED | 已提交 | ❌ |
| APPROVED | 已核准 | ❌ |
| REJECTED | 已駁回 | ✅ |
| LOCKED | 已鎖定 | ❌ |

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

| 事件名稱 | Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| TimesheetCreatedEvent | `timesheet.created` | 建立工時表 | - |
| TimesheetSubmittedEvent | `timesheet.submitted` | 提交審核 | Workflow, Notification |
| TimesheetApprovedEvent | `timesheet.approved` | 核准工時 | Project, Payroll, Notification |
| TimesheetRejectedEvent | `timesheet.rejected` | 駁回工時 | Notification |
| TimesheetLockedEvent | `timesheet.locked` | 鎖定工時（薪資結算後） | - |

---

**文件建立日期:** 2025-12-30
**版本:** 1.0
