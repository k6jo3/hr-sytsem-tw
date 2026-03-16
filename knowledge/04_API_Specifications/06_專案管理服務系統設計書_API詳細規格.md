# HR06 專案管理服務 API 詳細規格

**版本:** 1.1
**建立日期:** 2025-12-29
**最後更新:** 2026-03-16
**Domain 代號:** 06 (PRJ)
**API 總數:** 22 個端點

---

## 目錄

1. [API 總覽](#1-api-總覽)
2. [客戶管理 API](#2-客戶管理-api)
3. [專案管理 API](#3-專案管理-api)
4. [專案成員 API](#4-專案成員-api)
5. [工項管理 API](#5-工項管理-api)
6. [成本分析 API](#6-成本分析-api)
7. [員工自助查詢 API](#7-員工自助查詢-api)
8. [共用定義](#8-共用定義)
9. [領域事件](#9-領域事件)

---

## 1. API 總覽

### 1.1 Controller 對照表

| Controller | 說明 | API 數量 |
|:---|:---|:---:|
| `HR06CustomerCmdController` | 客戶 Command 操作 | 2 |
| `HR06CustomerQryController` | 客戶 Query 操作 | 2 |
| `HR06ProjectCmdController` | 專案 Command 操作 | 5 |
| `HR06ProjectQryController` | 專案 Query 操作 | 3 |
| `HR06MemberCmdController` | 成員 Command 操作 | 2 |
| `HR06MemberQryController` | 成員 Query 操作 | 1 |
| `HR06TaskCmdController` | 工項 Command 操作 | 4 |
| `HR06TaskQryController` | 工項 Query 操作 | 2 |
| `HR06CostQryController` | 成本分析 Query 操作 | 1 |

### 1.2 API 端點清單

| # | 端點 | 方法 | 說明 | Controller |
|:---:|:---|:---:|:---|:---|
| 1 | `/api/v1/customers` | POST | 建立客戶 | HR06CustomerCmdController |
| 2 | `/api/v1/customers/{id}` | PUT | 更新客戶 | HR06CustomerCmdController |
| 3 | `/api/v1/customers` | GET | 查詢客戶列表 | HR06CustomerQryController |
| 4 | `/api/v1/customers/{id}` | GET | 查詢客戶詳情 | HR06CustomerQryController |
| 5 | `/api/v1/projects` | POST | 建立專案 | HR06ProjectCmdController |
| 6 | `/api/v1/projects/{id}` | PUT | 更新專案 | HR06ProjectCmdController |
| 7 | `/api/v1/projects/{id}/start` | PUT | 開始專案 | HR06ProjectCmdController |
| 8 | `/api/v1/projects/{id}/complete` | PUT | 結案 | HR06ProjectCmdController |
| 9 | `/api/v1/projects/{id}/hold` | PUT | 暫停專案 | HR06ProjectCmdController |
| 10 | `/api/v1/projects` | GET | 查詢專案列表 | HR06ProjectQryController |
| 11 | `/api/v1/projects/{id}` | GET | 查詢專案詳情 | HR06ProjectQryController |
| 12 | `/api/v1/projects/my` | GET | 我參與的專案 | HR06ProjectQryController |
| 13 | `/api/v1/projects/{projectId}/members` | POST | 新增成員 | HR06MemberCmdController |
| 14 | `/api/v1/projects/{projectId}/members/{memberId}` | DELETE | 移除成員 | HR06MemberCmdController |
| 15 | `/api/v1/projects/{projectId}/members` | GET | 查詢專案成員列表 | HR06MemberQryController |
| 16 | `/api/v1/projects/{projectId}/tasks` | POST | 建立工項 | HR06TaskCmdController |
| 17 | `/api/v1/projects/{projectId}/tasks/{taskId}` | PUT | 更新工項 | HR06TaskCmdController |
| 18 | `/api/v1/projects/{projectId}/tasks/{taskId}/progress` | PUT | 更新進度 | HR06TaskCmdController |
| 19 | `/api/v1/projects/{projectId}/tasks/{taskId}/assign` | PUT | 指派工項 | HR06TaskCmdController |
| 20 | `/api/v1/projects/{projectId}/wbs` | GET | 查詢 WBS 樹 | HR06TaskQryController |
| 21 | `/api/v1/projects/{projectId}/tasks/{taskId}` | GET | 查詢工項詳情 | HR06TaskQryController |
| 22 | `/api/v1/projects/{projectId}/cost` | GET | 查詢成本分析 | HR06CostQryController |

### 1.3 專案類型與預算模式

**專案類型 (ProjectType)**

| 值 | 說明 |
|:---|:---|
| `DEVELOPMENT` | 新開發專案 |
| `MAINTENANCE` | 維護專案 |
| `CONSULTING` | 顧問專案 |

**預算模式 (BudgetType)**

| 值 | 說明 | 成本追蹤重點 |
|:---|:---|:---|
| `FIXED_PRICE` | 固定價格 | 追蹤成本是否超支 |
| `TIME_AND_MATERIAL` | 實報實銷 | 追蹤工時計費 |

---

## 2. 客戶管理 API

### 2.1 建立客戶

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `POST /api/v1/customers` |
| Controller | `HR06CustomerCmdController` |
| Service | `CreateCustomerServiceImpl` |
| 權限 | `customer:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 建立客戶基本資料，供專案關聯使用 |
| 使用者 | HR、專案管理人員 |
| 頁面 | HR06-P01 客戶管理頁面、HR06-M01 客戶編輯對話框 |

**業務邏輯**

| 驗證規則 | 說明 |
|:---|:---|
| 客戶代碼唯一 | 系統內不可重複 |
| 統一編號格式 | 台灣統一編號 8 碼 |

**Request Body**

```json
{
  "customerCode": "CUST-001",
  "customerName": "XX銀行股份有限公司",
  "taxId": "12345678",
  "industry": "金融業",
  "contacts": [
    {
      "name": "王小明",
      "title": "資訊部經理",
      "phone": "02-12345678",
      "email": "wang@xxbank.com",
      "isPrimary": true
    }
  ],
  "address": "台北市信義區信義路五段7號",
  "phoneNumber": "02-12345678",
  "email": "contact@xxbank.com"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| customerCode | string | ✅ | 最長 50 字元，唯一 | 客戶代碼 |
| customerName | string | ✅ | 最長 255 字元 | 客戶名稱 |
| taxId | string | ❌ | 8 碼數字 | 統一編號 |
| industry | string | ❌ | 最長 100 字元 | 產業別 |
| contacts | array | ❌ | - | 聯絡人列表 |
| address | string | ❌ | - | 地址 |
| phoneNumber | string | ❌ | 最長 50 字元 | 電話 |
| email | string | ❌ | Email 格式 | 電子郵件 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "customerId": "cust-001",
    "customerCode": "CUST-001",
    "customerName": "XX銀行股份有限公司",
    "createdAt": "2025-01-01T09:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PRJ_CUSTOMER_CODE_DUPLICATE` | 客戶代碼已存在 | 使用其他代碼 |
| 400 | `PRJ_INVALID_TAX_ID` | 統一編號格式錯誤 | 確認 8 碼數字 |

---

### 2.2 更新客戶

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/customers/{id}` |
| Controller | `HR06CustomerCmdController` |
| Service | `UpdateCustomerServiceImpl` |
| 權限 | `customer:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 修改客戶基本資料、聯絡人、狀態等 |
| 使用者 | HR、專案管理人員 |
| 頁面 | HR06-P01 客戶管理頁面、HR06-M01 客戶編輯對話框 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證客戶存在 |
| 2 | 驗證客戶代碼若有變更需唯一 |
| 3 | 更新客戶資料 |
| 4 | 記錄更新時間 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 客戶 ID |

**Request Body**

```json
{
  "customerName": "XX銀行股份有限公司 (更新)",
  "industry": "金融業",
  "contacts": [
    {
      "contactId": "contact-001",
      "name": "王小明",
      "title": "資訊部經理",
      "phone": "02-12345678",
      "email": "wang@xxbank.com",
      "isPrimary": true
    }
  ],
  "address": "台北市信義區信義路五段7號",
  "phoneNumber": "02-12345678",
  "email": "contact@xxbank.com",
  "status": "ACTIVE"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| customerName | string | ❌ | 最長 255 字元 | 客戶名稱 |
| industry | string | ❌ | 最長 100 字元 | 產業別 |
| contacts | array | ❌ | - | 聯絡人列表 |
| address | string | ❌ | - | 地址 |
| phoneNumber | string | ❌ | 最長 50 字元 | 電話 |
| email | string | ❌ | Email 格式 | 電子郵件 |
| status | string | ❌ | `ACTIVE`, `INACTIVE` | 客戶狀態 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "customerId": "cust-001",
    "customerName": "XX銀行股份有限公司 (更新)",
    "updatedAt": "2025-01-15T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_CUSTOMER_NOT_FOUND` | 客戶不存在 | 確認客戶 ID |
| 400 | `PRJ_CUSTOMER_CODE_DUPLICATE` | 客戶代碼已存在 | 使用其他代碼 |
| 400 | `PRJ_INVALID_EMAIL` | Email 格式錯誤 | 確認 Email 格式 |

---

### 2.3 查詢客戶列表

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/customers` |
| Controller | `HR06CustomerQryController` |
| Service | `GetCustomerListServiceImpl` |
| 權限 | `customer:read` |

**Query Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| keyword | string | ❌ | 關鍵字搜尋 (代碼、名稱) |
| industry | string | ❌ | 產業別篩選 |
| status | string | ❌ | 狀態篩選 |
| page | number | ❌ | 頁碼 (預設 1) |
| pageSize | number | ❌ | 每頁筆數 (預設 20) |

**Response Body**

```json
{
  "success": true,
  "data": {
    "customers": [
      {
        "customerId": "cust-001",
        "customerCode": "CUST-001",
        "customerName": "XX銀行股份有限公司",
        "taxId": "12345678",
        "industry": "金融業",
        "primaryContact": {
          "name": "王小明",
          "phone": "02-12345678",
          "email": "wang@xxbank.com"
        },
        "status": "ACTIVE",
        "projectCount": 3
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 50
    }
  }
}
```

---

### 2.4 查詢客戶詳情

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/customers/{id}` |
| Controller | `HR06CustomerQryController` |
| Service | `GetCustomerDetailServiceImpl` |
| 權限 | `customer:read` |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 客戶 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "customerId": "cust-001",
    "customerCode": "CUST-001",
    "customerName": "XX銀行股份有限公司",
    "taxId": "12345678",
    "industry": "金融業",
    "contacts": [
      {
        "contactId": "contact-001",
        "name": "王小明",
        "title": "資訊部經理",
        "phone": "02-12345678",
        "email": "wang@xxbank.com",
        "isPrimary": true
      }
    ],
    "address": "台北市信義區信義路五段7號",
    "phoneNumber": "02-12345678",
    "email": "contact@xxbank.com",
    "status": "ACTIVE",
    "projects": [
      {
        "projectId": "prj-001",
        "projectCode": "PRJ-2025-001",
        "projectName": "核心系統開發",
        "status": "IN_PROGRESS"
      }
    ],
    "createdAt": "2025-01-01T09:00:00Z",
    "updatedAt": "2025-01-15T10:00:00Z"
  }
}
```

---

## 3. 專案管理 API

### 3.1 建立專案

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `POST /api/v1/projects` |
| Controller | `HR06ProjectCmdController` |
| Service | `CreateProjectServiceImpl` |
| 權限 | `project:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 建立新專案，設定預算、時程、指派 PM |
| 使用者 | 專案經理、管理人員 |
| 頁面 | HR06-P04 專案建立頁面 |
| 觸發事件 | `ProjectCreated` |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證客戶存在 |
| 2 | 驗證專案代碼唯一 |
| 3 | 驗證計畫時程 (結束日 >= 開始日) |
| 4 | 建立專案 (狀態 = PLANNING) |
| 5 | 自動將 PM 加入專案成員 |
| 6 | 發布 `ProjectCreated` 事件 |

**Request Body**

```json
{
  "projectCode": "PRJ-2025-001",
  "projectName": "XX銀行核心系統開發",
  "customerId": "cust-001",
  "projectType": "DEVELOPMENT",
  "plannedStartDate": "2025-01-01",
  "plannedEndDate": "2025-12-31",
  "budgetType": "FIXED_PRICE",
  "budgetAmount": 10000000,
  "budgetHours": 2500,
  "projectManager": "emp-001",
  "description": "XX銀行核心系統全面改版開發專案",
  "members": [
    {
      "employeeId": "emp-002",
      "role": "Tech Lead",
      "allocatedHours": 1000,
      "hourlyRate": 1200
    }
  ]
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| projectCode | string | ✅ | 最長 50 字元，唯一 | 專案代碼 |
| projectName | string | ✅ | 最長 255 字元 | 專案名稱 |
| customerId | string | ✅ | UUID 格式 | 客戶 ID |
| projectType | string | ✅ | 枚舉值 | 專案類型 |
| plannedStartDate | string | ✅ | YYYY-MM-DD | 計畫開始日 |
| plannedEndDate | string | ✅ | YYYY-MM-DD | 計畫結束日 |
| budgetType | string | ✅ | 枚舉值 | 預算模式 |
| budgetAmount | number | ❌ | >= 0 | 預算金額 |
| budgetHours | number | ❌ | >= 0 | 預算工時 |
| projectManager | string | ✅ | UUID 格式 | 專案經理 ID |
| description | string | ❌ | - | 專案說明 |
| members | array | ❌ | - | 初始成員列表 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projectId": "prj-001",
    "projectCode": "PRJ-2025-001",
    "projectName": "XX銀行核心系統開發",
    "status": "PLANNING",
    "createdAt": "2025-01-01T09:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PRJ_CODE_DUPLICATE` | 專案代碼已存在 | 使用其他代碼 |
| 400 | `PRJ_INVALID_DATES` | 結束日早於開始日 | 修正日期範圍 |
| 404 | `PRJ_CUSTOMER_NOT_FOUND` | 客戶不存在 | 確認客戶 ID |
| 404 | `EMP_NOT_FOUND` | 專案經理不存在 | 確認員工 ID |

---

### 3.2 更新專案

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/projects/{id}` |
| Controller | `HR06ProjectCmdController` |
| Service | `UpdateProjectServiceImpl` |
| 權限 | `project:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 修改專案基本資訊、預算、時程等 |
| 使用者 | 專案經理、管理人員 |
| 頁面 | HR06-P04 專案建立/編輯頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證專案存在 |
| 2 | 驗證專案狀態允許編輯 (非 COMPLETED/CANCELLED) |
| 3 | 驗證時程 (結束日 >= 開始日) |
| 4 | 驗證專案經理存在 (若有變更) |
| 5 | 更新專案資料 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 專案 ID |

**Request Body**

```json
{
  "projectName": "XX銀行核心系統開發 (Phase 1)",
  "plannedStartDate": "2025-01-01",
  "plannedEndDate": "2026-03-31",
  "budgetAmount": 12000000,
  "budgetHours": 3000,
  "projectManager": "emp-001",
  "description": "專案範圍擴大"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| projectName | string | ❌ | 最長 255 字元 | 專案名稱 |
| plannedStartDate | string | ❌ | YYYY-MM-DD | 計畫開始日 |
| plannedEndDate | string | ❌ | YYYY-MM-DD | 計畫結束日 |
| budgetAmount | number | ❌ | >= 0 | 預算金額 |
| budgetHours | number | ❌ | >= 0 | 預算工時 |
| projectManager | string | ❌ | UUID 格式 | 專案經理 ID |
| description | string | ❌ | - | 專案說明 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projectId": "prj-001",
    "projectCode": "PRJ-2025-001",
    "projectName": "XX銀行核心系統開發 (Phase 1)",
    "updatedAt": "2025-03-01T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_NOT_FOUND` | 專案不存在 | 確認專案 ID |
| 400 | `PRJ_INVALID_DATES` | 結束日早於開始日 | 修正日期範圍 |
| 400 | `PRJ_CANNOT_EDIT` | 已結案/取消的專案無法編輯 | 確認專案狀態 |
| 404 | `EMP_NOT_FOUND` | 專案經理不存在 | 確認員工 ID |

---

### 3.3 開始專案

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/projects/{id}/start` |
| Controller | `HR06ProjectCmdController` |
| Service | `StartProjectServiceImpl` |
| 權限 | `project:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 專案正式啟動，開始追蹤成本與工時 |
| 使用者 | 專案經理 |
| 前置條件 | 專案狀態為 PLANNING |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證專案狀態為 PLANNING |
| 2 | 設定 actual_start_date = 今日 |
| 3 | 更新狀態為 IN_PROGRESS |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 專案 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projectId": "prj-001",
    "status": "IN_PROGRESS",
    "actualStartDate": "2025-01-15"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PRJ_INVALID_STATUS_TRANSITION` | 只有規劃中的專案可以開始 | 確認專案狀態 |

---

### 3.4 結案

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/projects/{id}/complete` |
| Controller | `HR06ProjectCmdController` |
| Service | `CompleteProjectServiceImpl` |
| 權限 | `project:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 專案結案，鎖定成本資料 |
| 使用者 | 專案經理 |
| 前置條件 | 專案狀態為 IN_PROGRESS |
| 觸發事件 | `ProjectCompleted` |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證專案狀態為 IN_PROGRESS |
| 2 | 設定 actual_end_date = 今日 |
| 3 | 更新狀態為 COMPLETED |
| 4 | 發布 `ProjectCompleted` 事件 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 專案 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projectId": "prj-001",
    "status": "COMPLETED",
    "actualStartDate": "2025-01-15",
    "actualEndDate": "2025-12-20",
    "summary": {
      "totalHours": 2380,
      "totalCost": 9520000,
      "budgetUtilization": 95.2
    }
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_NOT_FOUND` | 專案不存在 | 確認專案 ID |
| 400 | `PRJ_INVALID_STATUS_TRANSITION` | 只有進行中的專案可以結案 | 確認專案狀態 |
| 400 | `PRJ_HAS_INCOMPLETE_TASKS` | 仍有未完成的工項 | 完成或關閉所有工項後再結案 |

---

### 3.5 暫停專案

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/projects/{id}/hold` |
| Controller | `HR06ProjectCmdController` |
| Service | `HoldProjectServiceImpl` |
| 權限 | `project:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 暫停專案執行 (客戶要求、預算問題等) |
| 使用者 | 專案經理 |
| 前置條件 | 專案狀態為 IN_PROGRESS |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證專案存在 |
| 2 | 驗證專案狀態為 IN_PROGRESS |
| 3 | 更新狀態為 ON_HOLD |
| 4 | 記錄暫停原因與時間 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 專案 ID |

**Request Body**

```json
{
  "reason": "客戶要求暫停，等待預算審批"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| reason | string | ✅ | 最長 500 字元 | 暫停原因 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projectId": "prj-001",
    "status": "ON_HOLD",
    "holdReason": "客戶要求暫停，等待預算審批",
    "holdDate": "2025-06-01"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_NOT_FOUND` | 專案不存在 | 確認專案 ID |
| 400 | `PRJ_INVALID_STATUS_TRANSITION` | 只有進行中的專案可以暫停 | 確認專案狀態 |
| 400 | `PRJ_REASON_REQUIRED` | 暫停原因為必填 | 填寫暫停原因 |

---

### 3.6 查詢專案列表

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/projects` |
| Controller | `HR06ProjectQryController` |
| Service | `GetProjectListServiceImpl` |
| 權限 | `project:read` |

**Query Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| keyword | string | ❌ | 關鍵字搜尋 |
| customerId | string | ❌ | 客戶篩選 |
| projectType | string | ❌ | 專案類型篩選 |
| status | string | ❌ | 狀態篩選 |
| projectManager | string | ❌ | PM 篩選 |
| startDateFrom | string | ❌ | 開始日期範圍起 |
| startDateTo | string | ❌ | 開始日期範圍迄 |
| page | number | ❌ | 頁碼 |
| pageSize | number | ❌ | 每頁筆數 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projects": [
      {
        "projectId": "prj-001",
        "projectCode": "PRJ-2025-001",
        "projectName": "XX銀行核心系統開發",
        "customer": {
          "customerId": "cust-001",
          "customerName": "XX銀行股份有限公司"
        },
        "projectType": "DEVELOPMENT",
        "budgetType": "FIXED_PRICE",
        "budgetAmount": 10000000,
        "actualCost": 1800000,
        "budgetUtilization": 18.0,
        "projectManager": {
          "employeeId": "emp-001",
          "employeeName": "張三"
        },
        "plannedStartDate": "2025-01-01",
        "plannedEndDate": "2025-12-31",
        "progress": 35,
        "status": "IN_PROGRESS"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 25
    }
  }
}
```

---

### 3.7 查詢專案詳情

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/projects/{id}` |
| Controller | `HR06ProjectQryController` |
| Service | `GetProjectDetailServiceImpl` |
| 權限 | `project:read` |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 專案 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projectId": "prj-001",
    "projectCode": "PRJ-2025-001",
    "projectName": "XX銀行核心系統開發",
    "customer": {
      "customerId": "cust-001",
      "customerCode": "CUST-001",
      "customerName": "XX銀行股份有限公司"
    },
    "projectType": "DEVELOPMENT",
    "budgetType": "FIXED_PRICE",
    "budget": {
      "budgetAmount": 10000000,
      "budgetHours": 2500
    },
    "actual": {
      "actualCost": 1800000,
      "actualHours": 620,
      "budgetUtilization": 18.0,
      "hoursUtilization": 24.8
    },
    "schedule": {
      "plannedStartDate": "2025-01-01",
      "plannedEndDate": "2025-12-31",
      "actualStartDate": "2025-01-15",
      "actualEndDate": null
    },
    "projectManager": {
      "employeeId": "emp-001",
      "employeeName": "張三",
      "email": "zhangsan@company.com"
    },
    "members": [
      {
        "memberId": "mem-001",
        "employeeId": "emp-002",
        "employeeName": "李四",
        "role": "Tech Lead",
        "allocatedHours": 1000,
        "actualHours": 200,
        "hourlyRate": 1200,
        "joinDate": "2025-01-01"
      }
    ],
    "description": "XX銀行核心系統全面改版開發專案",
    "progress": 35,
    "status": "IN_PROGRESS",
    "createdAt": "2025-01-01T09:00:00Z",
    "updatedAt": "2025-03-01T10:00:00Z"
  }
}
```

---

## 4. 專案成員 API

### 4.1 新增成員

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `POST /api/v1/projects/{projectId}/members` |
| Controller | `HR06MemberCmdController` |
| Service | `AddProjectMemberServiceImpl` |
| 權限 | `project:member:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 將員工加入專案團隊 |
| 使用者 | 專案經理 |
| 頁面 | HR06-M02 新增成員對話框 |
| 觸發事件 | `ProjectMemberAdded` |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證員工存在 |
| 2 | 驗證員工未在該專案中 |
| 3 | 新增專案成員記錄 |
| 4 | 發布 `ProjectMemberAdded` 事件 (通知 Timesheet) |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |

**Request Body**

```json
{
  "employeeId": "emp-003",
  "role": "Developer",
  "allocatedHours": 800,
  "hourlyRate": 800,
  "joinDate": "2025-02-01"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| employeeId | string | ✅ | UUID 格式 | 員工 ID |
| role | string | ✅ | 最長 100 字元 | 專案角色 |
| allocatedHours | number | ❌ | >= 0 | 分配工時 |
| hourlyRate | number | ❌ | >= 0 | 計費時薪 |
| joinDate | string | ✅ | YYYY-MM-DD | 加入日期 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "memberId": "mem-002",
    "projectId": "prj-001",
    "employeeId": "emp-003",
    "employeeName": "王五",
    "role": "Developer",
    "joinDate": "2025-02-01"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PRJ_MEMBER_ALREADY_EXISTS` | 該成員已在專案中 | 確認成員列表 |
| 404 | `EMP_NOT_FOUND` | 員工不存在 | 確認員工 ID |
| 404 | `PRJ_NOT_FOUND` | 專案不存在 | 確認專案 ID |

---

### 4.2 移除成員

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `DELETE /api/v1/projects/{projectId}/members/{memberId}` |
| Controller | `HR06MemberCmdController` |
| Service | `RemoveProjectMemberServiceImpl` |
| 權限 | `project:member:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 將成員從專案團隊中移除 |
| 使用者 | 專案經理 |
| 頁面 | HR06-P03 專案詳情頁面 (成員分頁) |
| 觸發事件 | `ProjectMemberRemoved` |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證專案存在 |
| 2 | 驗證成員存在於該專案中 |
| 3 | 驗證專案經理不可自行移除 |
| 4 | 設定成員離開日期 |
| 5 | 發布 `ProjectMemberRemoved` 事件 (通知 Timesheet) |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |
| memberId | string | ✅ | 成員 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "memberId": "mem-002",
    "removed": true,
    "leaveDate": "2025-06-30"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_NOT_FOUND` | 專案不存在 | 確認專案 ID |
| 404 | `PRJ_MEMBER_NOT_FOUND` | 成員不存在於該專案 | 確認成員 ID |
| 400 | `PRJ_CANNOT_REMOVE_PM` | 專案經理不可移除 | 先變更專案經理再移除 |
| 400 | `PRJ_MEMBER_HAS_PENDING_TIMESHEET` | 成員有未結算的工時 | 先處理工時後再移除 |

### 4.3 查詢專案成員列表

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/projects/{projectId}/members` |
| Controller | `HR06MemberQryController` |
| Service | `GetProjectMembersServiceImpl` |
| 權限 | `project:member:read` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查詢專案的成員列表，含角色、分配工時等資訊 |
| 使用者 | 專案經理、團隊成員 |
| 頁面 | HR06-P03 專案詳情頁面 (成員分頁) |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |

**Response Body**

```json
{
  "success": true,
  "data": [
    {
      "memberId": "mem-001",
      "employeeId": "emp-002",
      "employeeName": "李四",
      "role": "Tech Lead",
      "allocatedHours": 1000,
      "actualHours": 200,
      "hourlyRate": 1200,
      "joinDate": "2025-01-01",
      "leaveDate": null
    }
  ]
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_NOT_FOUND` | 專案不存在 | 確認專案 ID |

---

## 5. 工項管理 API

### 5.1 建立工項

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `POST /api/v1/projects/{projectId}/tasks` |
| Controller | `HR06TaskCmdController` |
| Service | `CreateTaskServiceImpl` |
| 權限 | `project:task:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 建立 WBS 工項，支援 5 層級結構 |
| 使用者 | 專案經理 |
| 頁面 | HR06-P05 WBS 工項管理頁面、HR06-M03 工項編輯對話框 |
| 觸發事件 | `TaskAssigned` (若有指派人) |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證父工項存在 (若指定) |
| 2 | 計算層級 (父層級 + 1，最多 5 層) |
| 3 | 驗證工項代碼唯一 (專案內) |
| 4 | 建立工項 (狀態 = NOT_STARTED) |
| 5 | 若有指派人，發布 `TaskAssigned` 事件 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |

**Request Body**

```json
{
  "parentTaskId": null,
  "taskCode": "1",
  "taskName": "需求分析",
  "description": "進行客戶需求訪談與分析",
  "plannedStartDate": "2025-01-15",
  "plannedEndDate": "2025-02-28",
  "estimatedHours": 200,
  "assigneeId": "emp-002"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| parentTaskId | string | ❌ | UUID 格式 | 父工項 ID (空 = 第一層) |
| taskCode | string | ✅ | 最長 50 字元 | 工項代碼 |
| taskName | string | ✅ | 最長 255 字元 | 工項名稱 |
| description | string | ❌ | - | 工項說明 |
| plannedStartDate | string | ❌ | YYYY-MM-DD | 計畫開始日 |
| plannedEndDate | string | ❌ | YYYY-MM-DD | 計畫結束日 |
| estimatedHours | number | ✅ | >= 0 | 預估工時 |
| assigneeId | string | ❌ | UUID 格式 | 負責人 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "projectId": "prj-001",
    "taskCode": "1",
    "taskName": "需求分析",
    "level": 1,
    "status": "NOT_STARTED",
    "createdAt": "2025-01-15T09:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PRJ_MAX_LEVEL_EXCEEDED` | WBS 最多支援 5 層 | 調整工項結構 |
| 400 | `PRJ_TASK_CODE_DUPLICATE` | 工項代碼已存在 | 使用其他代碼 |
| 404 | `PRJ_PARENT_TASK_NOT_FOUND` | 父工項不存在 | 確認父工項 ID |

---

### 5.2 更新工項

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/projects/{projectId}/tasks/{taskId}` |
| Controller | `HR06TaskCmdController` |
| Service | `UpdateTaskServiceImpl` |
| 權限 | `project:task:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 修改工項名稱、說明、時程、工時、負責人等 |
| 使用者 | 專案經理 |
| 頁面 | HR06-P05 WBS 工項管理頁面、HR06-M03 工項編輯對話框 |
| 觸發事件 | `TaskAssigned` (若負責人變更) |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工項存在 |
| 2 | 驗證工項所屬專案狀態允許編輯 |
| 3 | 驗證時程 (結束日 >= 開始日) |
| 4 | 若負責人變更，發布 `TaskAssigned` 事件 |
| 5 | 更新工項資料 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |
| taskId | string | ✅ | 工項 ID |

**Request Body**

```json
{
  "taskName": "需求分析 (更新)",
  "description": "進行客戶需求訪談與分析，含原型設計",
  "plannedStartDate": "2025-01-15",
  "plannedEndDate": "2025-03-15",
  "estimatedHours": 250,
  "assigneeId": "emp-003"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| taskName | string | ❌ | 最長 255 字元 | 工項名稱 |
| description | string | ❌ | - | 工項說明 |
| plannedStartDate | string | ❌ | YYYY-MM-DD | 計畫開始日 |
| plannedEndDate | string | ❌ | YYYY-MM-DD | 計畫結束日 |
| estimatedHours | number | ❌ | >= 0 | 預估工時 |
| assigneeId | string | ❌ | UUID 格式 | 負責人 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "taskName": "需求分析 (更新)",
    "updatedAt": "2025-02-01T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_TASK_NOT_FOUND` | 工項不存在 | 確認工項 ID |
| 400 | `PRJ_CANNOT_EDIT_TASK` | 工項所屬專案已結案/取消 | 確認專案狀態 |
| 400 | `PRJ_INVALID_TASK_DATES` | 結束日早於開始日 | 修正日期範圍 |
| 404 | `PRJ_ASSIGNEE_NOT_IN_PROJECT` | 負責人非專案成員 | 先將該員工加入專案 |

---

### 5.3 更新進度

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/projects/{projectId}/tasks/{taskId}/progress` |
| Controller | `HR06TaskCmdController` |
| Service | `UpdateTaskProgressServiceImpl` |
| 權限 | `project:task:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 更新工項完成進度 |
| 使用者 | 負責人、專案經理 |
| 觸發事件 | `TaskCompleted` (進度 = 100%) |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證進度值 (0-100) |
| 2 | 更新進度 |
| 3 | 自動更新狀態：進度 > 0 → IN_PROGRESS，進度 = 100 → COMPLETED |
| 4 | 若完成，發布 `TaskCompleted` 事件 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |
| taskId | string | ✅ | 工項 ID |

**Request Body**

```json
{
  "progress": 80
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| progress | number | ✅ | 0-100 整數 | 完成進度 (%) |

**Response Body**

```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "progress": 80,
    "status": "IN_PROGRESS",
    "updatedAt": "2025-02-15T14:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_TASK_NOT_FOUND` | 工項不存在 | 確認工項 ID |
| 400 | `PRJ_INVALID_PROGRESS` | 進度值必須在 0-100 之間 | 修正進度值 |
| 400 | `PRJ_CANNOT_UPDATE_PROGRESS` | 工項所屬專案已結案/取消 | 確認專案狀態 |
| 400 | `PRJ_PARENT_TASK_PROGRESS` | 父工項進度由子工項計算，無法直接修改 | 更新子工項進度 |

---

### 5.4 查詢 WBS 樹

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/projects/{projectId}/wbs` |
| Controller | `HR06TaskQryController` |
| Service | `GetWbsTreeServiceImpl` |
| 權限 | `project:task:read` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 取得專案完整 WBS 樹狀結構 |
| 使用者 | 專案經理、團隊成員 |
| 頁面 | HR06-P05 WBS 工項管理頁面 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projectId": "prj-001",
    "projectName": "XX銀行核心系統開發",
    "totalEstimatedHours": 1500,
    "totalActualHours": 264,
    "overallProgress": 35,
    "tasks": [
      {
        "taskId": "task-001",
        "taskCode": "1",
        "taskName": "需求分析",
        "level": 1,
        "estimatedHours": 200,
        "actualHours": 120,
        "progress": 60,
        "status": "IN_PROGRESS",
        "assignee": {
          "employeeId": "emp-002",
          "employeeName": "李四"
        },
        "children": [
          {
            "taskId": "task-002",
            "taskCode": "1.1",
            "taskName": "訪談客戶",
            "level": 2,
            "estimatedHours": 40,
            "actualHours": 40,
            "progress": 100,
            "status": "COMPLETED",
            "assignee": {
              "employeeId": "emp-002",
              "employeeName": "李四"
            },
            "children": []
          },
          {
            "taskId": "task-003",
            "taskCode": "1.2",
            "taskName": "撰寫需求文件",
            "level": 2,
            "estimatedHours": 80,
            "actualHours": 64,
            "progress": 80,
            "status": "IN_PROGRESS",
            "assignee": {
              "employeeId": "emp-002",
              "employeeName": "李四"
            },
            "children": []
          }
        ]
      },
      {
        "taskId": "task-010",
        "taskCode": "2",
        "taskName": "系統設計",
        "level": 1,
        "estimatedHours": 300,
        "actualHours": 0,
        "progress": 0,
        "status": "NOT_STARTED",
        "assignee": null,
        "children": [...]
      }
    ]
  }
}
```

### 5.5 指派工項

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/projects/{projectId}/tasks/{taskId}/assign` |
| Controller | `HR06TaskCmdController` |
| Service | `AssignTaskServiceImpl` |
| 權限 | `project:task:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 將工項指派給專案成員負責 |
| 使用者 | 專案經理 |
| 觸發事件 | `TaskAssigned` |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證工項存在 |
| 2 | 驗證負責人為專案成員 |
| 3 | 更新工項負責人 |
| 4 | 發布 `TaskAssigned` 事件 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |
| taskId | string | ✅ | 工項 ID |

**Request Body**

```json
{
  "assigneeId": "emp-003"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| assigneeId | string | ✅ | UUID 格式 | 負責人 ID (專案成員) |

**Response Body**

```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "assigneeId": "emp-003",
    "assigneeName": "王五",
    "updatedAt": "2025-02-01T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_TASK_NOT_FOUND` | 工項不存在 | 確認工項 ID |
| 404 | `PRJ_ASSIGNEE_NOT_IN_PROJECT` | 負責人非專案成員 | 先將該員工加入專案 |

---

### 5.6 查詢工項詳情

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/projects/{projectId}/tasks/{taskId}` |
| Controller | `HR06TaskQryController` |
| Service | `GetTaskDetailServiceImpl` |
| 權限 | `project:task:read` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查詢工項的詳細資訊，含負責人、進度、工時等 |
| 使用者 | 專案經理、團隊成員 |
| 頁面 | HR06-P05 WBS 工項管理頁面 (工項詳情) |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |
| taskId | string | ✅ | 工項 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "taskId": "task-001",
    "projectId": "prj-001",
    "parentTaskId": null,
    "taskCode": "1",
    "taskName": "需求分析",
    "description": "進行客戶需求訪談與分析",
    "level": 1,
    "plannedStartDate": "2025-01-15",
    "plannedEndDate": "2025-02-28",
    "estimatedHours": 200,
    "actualHours": 120,
    "progress": 60,
    "status": "IN_PROGRESS",
    "assignee": {
      "employeeId": "emp-002",
      "employeeName": "李四"
    },
    "createdAt": "2025-01-15T09:00:00Z",
    "updatedAt": "2025-02-15T14:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PRJ_TASK_NOT_FOUND` | 工項不存在 | 確認工項 ID |

---

## 6. 成本分析 API

### 6.1 查詢成本分析

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/projects/{projectId}/cost` |
| Controller | `HR06CostQryController` |
| Service | `GetProjectCostServiceImpl` |
| 權限 | `project:cost:read` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查詢專案成本分析，含成員工時、成本明細 |
| 使用者 | 專案經理、管理人員 |
| 頁面 | HR06-P06 專案成本分析頁面 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| projectId | string | ✅ | 專案 ID |

**Query Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| periodFrom | string | ❌ | 期間起 (YYYY-MM) |
| periodTo | string | ❌ | 期間迄 (YYYY-MM) |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projectId": "prj-001",
    "projectCode": "PRJ-2025-001",
    "projectName": "XX銀行核心系統開發",
    "budget": {
      "budgetType": "FIXED_PRICE",
      "budgetAmount": 10000000,
      "budgetHours": 2500
    },
    "summary": {
      "totalHours": 620,
      "totalCost": 567000,
      "budgetUtilization": 5.67,
      "hoursUtilization": 24.8,
      "estimatedGrossProfit": 9433000,
      "estimatedGrossProfitMargin": 94.33,
      "burnRate": 18.0
    },
    "byMember": [
      {
        "employeeId": "emp-002",
        "employeeName": "李四",
        "role": "Tech Lead",
        "hours": 200,
        "hourlyRate": 1200,
        "cost": 240000,
        "costPercentage": 42.3
      },
      {
        "employeeId": "emp-003",
        "employeeName": "王五",
        "role": "Developer",
        "hours": 180,
        "hourlyRate": 800,
        "cost": 144000,
        "costPercentage": 25.4
      },
      {
        "employeeId": "emp-004",
        "employeeName": "趙六",
        "role": "Developer",
        "hours": 150,
        "hourlyRate": 800,
        "cost": 120000,
        "costPercentage": 21.2
      },
      {
        "employeeId": "emp-005",
        "employeeName": "陳七",
        "role": "QA",
        "hours": 90,
        "hourlyRate": 700,
        "cost": 63000,
        "costPercentage": 11.1
      }
    ],
    "byMonth": [
      {
        "yearMonth": "2025-01",
        "hours": 120,
        "cost": 102000
      },
      {
        "yearMonth": "2025-02",
        "hours": 200,
        "cost": 180000
      },
      {
        "yearMonth": "2025-03",
        "hours": 300,
        "cost": 285000
      }
    ],
    "costTrend": {
      "plannedCostCurve": [...],
      "actualCostCurve": [...]
    }
  }
}
```

---

## 7. 員工自助查詢 API

### 7.1 我參與的專案

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/projects/my` |
| Controller | `HR06ProjectQryController` |
| Service | `GetMyProjectsServiceImpl` |
| 權限 | 登入使用者 (自動取得當前員工) |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工查看自己參與的專案列表 |
| 使用者 | 員工 (ESS 自助服務) |
| 頁面 | HR06-P08 我參與的專案頁面 |

**Query Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| status | string | ❌ | 專案狀態篩選 |
| page | number | ❌ | 頁碼 |
| pageSize | number | ❌ | 每頁筆數 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "projects": [
      {
        "projectId": "prj-001",
        "projectCode": "PRJ-2025-001",
        "projectName": "XX銀行核心系統開發",
        "customerName": "XX銀行股份有限公司",
        "myRole": "Developer",
        "myAllocatedHours": 800,
        "myActualHours": 180,
        "myUtilization": 22.5,
        "projectProgress": 35,
        "projectStatus": "IN_PROGRESS",
        "myTasks": [
          {
            "taskId": "task-003",
            "taskCode": "1.2",
            "taskName": "撰寫需求文件",
            "estimatedHours": 80,
            "actualHours": 64,
            "progress": 80,
            "status": "IN_PROGRESS"
          }
        ]
      }
    ],
    "summary": {
      "activeProjects": 2,
      "totalAllocatedHours": 1600,
      "totalActualHours": 380
    },
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 5
    }
  }
}
```

---

## 8. 共用定義

### 8.1 專案狀態枚舉 (ProjectStatus)

| 值 | 說明 | 允許操作 |
|:---|:---|:---|
| `PLANNING` | 規劃中 | 開始、取消 |
| `IN_PROGRESS` | 進行中 | 暫停、結案 |
| `ON_HOLD` | 暫停 | 恢復、取消 |
| `COMPLETED` | 已結案 | 無 |
| `CANCELLED` | 已取消 | 無 |

### 8.2 工項狀態枚舉 (TaskStatus)

| 值 | 說明 |
|:---|:---|
| `NOT_STARTED` | 未開始 |
| `IN_PROGRESS` | 進行中 |
| `COMPLETED` | 已完成 |
| `BLOCKED` | 已阻塞 |

### 8.3 專案類型枚舉 (ProjectType)

| 值 | 說明 |
|:---|:---|
| `DEVELOPMENT` | 新開發專案 |
| `MAINTENANCE` | 維護專案 |
| `CONSULTING` | 顧問專案 |

### 8.4 預算類型枚舉 (BudgetType)

| 值 | 說明 |
|:---|:---|
| `FIXED_PRICE` | 固定價格 |
| `TIME_AND_MATERIAL` | 實報實銷 |

### 8.5 通用錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | `INVALID_REQUEST` | 請求格式錯誤 |
| 401 | `UNAUTHORIZED` | 未授權存取 |
| 403 | `FORBIDDEN` | 無權限執行操作 |
| 404 | `NOT_FOUND` | 資源不存在 |
| 500 | `INTERNAL_ERROR` | 系統內部錯誤 |

### 8.6 資料庫表結構

#### customers (客戶表)

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| customer_id | UUID | 主鍵 |
| customer_code | VARCHAR(50) | 客戶代碼 (唯一) |
| customer_name | VARCHAR(255) | 客戶名稱 |
| tax_id | VARCHAR(20) | 統一編號 |
| industry | VARCHAR(100) | 產業別 |
| contacts | JSONB | 聯絡人列表 |
| status | VARCHAR(20) | 狀態 |

#### projects (專案表)

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| project_id | UUID | 主鍵 |
| project_code | VARCHAR(50) | 專案代碼 (唯一) |
| project_name | VARCHAR(255) | 專案名稱 |
| customer_id | UUID | 客戶 ID (FK) |
| project_type | VARCHAR(20) | 專案類型 |
| budget_type | VARCHAR(30) | 預算模式 |
| budget_amount | DECIMAL(15,2) | 預算金額 |
| budget_hours | DECIMAL(10,2) | 預算工時 |
| project_manager | UUID | 專案經理 (FK) |
| status | VARCHAR(20) | 專案狀態 |
| actual_hours | DECIMAL(10,2) | 實際工時 |
| actual_cost | DECIMAL(15,2) | 實際成本 |

#### project_members (專案成員表)

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| member_id | UUID | 主鍵 |
| project_id | UUID | 專案 ID (FK) |
| employee_id | UUID | 員工 ID |
| role | VARCHAR(100) | 專案角色 |
| allocated_hours | DECIMAL(10,2) | 分配工時 |
| hourly_rate | DECIMAL(10,2) | 時薪費率 |
| join_date | DATE | 加入日期 |
| leave_date | DATE | 離開日期 |

#### tasks (工項表 - WBS)

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| task_id | UUID | 主鍵 |
| project_id | UUID | 專案 ID (FK) |
| parent_task_id | UUID | 父工項 ID (自參照) |
| task_code | VARCHAR(50) | 工項代碼 |
| task_name | VARCHAR(255) | 工項名稱 |
| level | INTEGER | 層級 (1-5) |
| estimated_hours | DECIMAL(10,2) | 預估工時 |
| actual_hours | DECIMAL(10,2) | 實際工時 |
| assignee_id | UUID | 負責人 ID |
| status | VARCHAR(20) | 工項狀態 |
| progress | INTEGER | 完成進度 (0-100) |

---

## 9. 領域事件

### 9.1 事件總覽

| 事件名稱 | Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| `ProjectCreated` | `project.created` | 建立專案 | - |
| `ProjectMemberAdded` | `project.member.added` | 新增成員 | Timesheet |
| `ProjectCompleted` | `project.completed` | 專案結案 | Reporting |
| `ProjectBudgetAlert` | `project.budget.alert` | 預算超過 80% | Notification |
| `TaskAssigned` | `project.task.assigned` | 指派工項 | Notification |
| `TaskCompleted` | `project.task.completed` | 完成工項 | - |

### 9.2 事件訂閱 (本服務)

| 事件名稱 | 來源服務 | 處理邏輯 |
|:---|:---|:---|
| `TimesheetApproved` | Timesheet | 更新專案實際工時與成本 |

### 9.3 ProjectCreated 事件

```json
{
  "eventId": "evt-prj-001",
  "eventType": "ProjectCreated",
  "timestamp": "2025-01-01T09:00:00Z",
  "source": "project-service",
  "payload": {
    "projectId": "prj-001",
    "projectCode": "PRJ-2025-001",
    "projectName": "XX銀行核心系統開發",
    "customerId": "cust-001",
    "projectManager": "emp-001",
    "budgetAmount": 10000000,
    "plannedStartDate": "2025-01-01",
    "plannedEndDate": "2025-12-31"
  }
}
```

### 9.4 ProjectMemberAdded 事件

```json
{
  "eventId": "evt-prj-002",
  "eventType": "ProjectMemberAdded",
  "timestamp": "2025-01-02T10:00:00Z",
  "source": "project-service",
  "payload": {
    "projectId": "prj-001",
    "projectCode": "PRJ-2025-001",
    "memberId": "mem-001",
    "employeeId": "emp-002",
    "role": "Tech Lead",
    "joinDate": "2025-01-01"
  }
}
```

### 9.5 ProjectBudgetAlert 事件

```json
{
  "eventId": "evt-prj-003",
  "eventType": "ProjectBudgetAlert",
  "timestamp": "2025-06-15T08:00:00Z",
  "source": "project-service",
  "payload": {
    "projectId": "prj-001",
    "projectCode": "PRJ-2025-001",
    "projectName": "XX銀行核心系統開發",
    "budgetAmount": 10000000,
    "actualCost": 8200000,
    "budgetUtilization": 82.0,
    "progress": 65,
    "alertLevel": "WARNING",
    "projectManager": "emp-001"
  }
}
```

### 9.6 ProjectCompleted 事件

```json
{
  "eventId": "evt-prj-004",
  "eventType": "ProjectCompleted",
  "timestamp": "2025-12-20T17:00:00Z",
  "source": "project-service",
  "payload": {
    "projectId": "prj-001",
    "projectCode": "PRJ-2025-001",
    "projectName": "XX銀行核心系統開發",
    "actualStartDate": "2025-01-15",
    "actualEndDate": "2025-12-20",
    "totalHours": 2380,
    "totalCost": 9520000,
    "budgetAmount": 10000000,
    "budgetUtilization": 95.2,
    "finalStatus": "ON_BUDGET"
  }
}
```

---

**文件完成日期:** 2025-12-29
**最後更新:** 2026-03-16
**版本:** 1.1
**API 總數:** 22 個端點
**變更說明:** v1.1 - 修正工項 API 路徑為巢狀資源格式 (`/projects/{projectId}/tasks/{taskId}`)，統一 Path Parameter 命名，新增指派工項 (5.5)、工項詳情 (5.6)、成員列表查詢 (4.3) 詳細規格
