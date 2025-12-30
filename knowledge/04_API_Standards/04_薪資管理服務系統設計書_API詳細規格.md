# HR04 薪資管理服務 API 詳細規格

**版本:** 1.0
**建立日期:** 2025-12-29
**服務代碼:** HR04
**服務名稱:** 薪資管理服務 (Payroll Service)

---

## 目錄

1. [API 總覽](#1-api-總覽)
2. [薪資結構管理 API](#2-薪資結構管理-api)
3. [薪資計算批次 API](#3-薪資計算批次-api)
4. [薪資單管理 API](#4-薪資單管理-api)
5. [薪轉檔案 API](#5-薪轉檔案-api)
6. [附錄：列舉值定義](#6-附錄列舉值定義)

---

## 1. API 總覽

### 1.1 端點統計

| 模組 | API 數量 | 說明 |
|:---|:---:|:---|
| 薪資結構管理 | 5 | 薪資結構 CRUD、員工薪資查詢 |
| 薪資計算批次 | 8 | 建立、執行、送審、核准、發放、重算 |
| 薪資單管理 | 4 | 員工查詢、詳情、PDF 下載、Email 發送 |
| 薪轉檔案 | 2 | 產生、下載銀行媒體檔 |
| **合計** | **19** | |

### 1.2 Controller 對照表

| Controller | 說明 | API 數量 |
|:---|:---|:---:|
| `HR04SalaryStructureCmdController` | 薪資結構 Command 操作 | 3 |
| `HR04SalaryStructureQryController` | 薪資結構 Query 操作 | 2 |
| `HR04PayrollRunCmdController` | 薪資計算批次 Command 操作 | 6 |
| `HR04PayrollRunQryController` | 薪資計算批次 Query 操作 | 2 |
| `HR04PayslipCmdController` | 薪資單 Command 操作 | 1 |
| `HR04PayslipQryController` | 薪資單 Query 操作 | 3 |
| `HR04BankTransferCmdController` | 薪轉檔案 Command 操作 | 2 |

### 1.3 Saga 模式整合

薪資計算需整合多個服務數據，採用 Saga 模式協調：

```
Organization Service → 員工清單
Attendance Service → 差勤數據 (請假、加班)
Insurance Service → 勞健保費用
     ↓
Payroll Service → 薪資計算 → Payslip 產生
     ↓
Document Service → PDF 生成
Notification Service → Email 發送
```

### 1.4 通用 Headers

所有 API 請求需包含以下 Headers：

| Header | 必填 | 說明 |
|:---|:---:|:---|
| `Authorization` | Y | Bearer Token (JWT) |
| `Content-Type` | Y | `application/json` |
| `X-Tenant-Id` | Y | 租戶識別碼 |
| `X-Request-Id` | N | 請求追蹤 ID |

### 1.5 通用錯誤回應格式

```json
{
  "success": false,
  "code": "PAY_STRUCTURE_NOT_FOUND",
  "message": "找不到該員工的薪資結構",
  "timestamp": "2025-12-06T10:00:00Z",
  "path": "/api/v1/salary-structures/emp-001",
  "details": null
}
```

---

## 2. 薪資結構管理 API

### 2.1 建立薪資結構

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/salary-structures` |
| **方法** | POST |
| **Controller** | `HR04SalaryStructureCmdController` |
| **Service** | `CreateSalaryStructureServiceImpl` |
| **權限** | `payroll:structure:manage` |

#### 用途說明

- **業務場景:** HR 為員工建立薪資結構
- **使用者:** HR 管理員
- **解決問題:** 設定員工的薪資計算基礎，包含底薪、津貼等項目

#### 業務邏輯

1. **驗證規則:**
   - 驗證員工 ID 是否存在且為在職狀態
   - 驗證生效日期不可早於當前日期
   - 月薪制需填寫 monthlySalary，時薪制需填寫 hourlyRate
   - 薪資項目代碼需在組織的項目定義中存在

2. **處理步驟:**
   - 檢查員工是否已有生效中的薪資結構
   - 若有，將舊結構設為失效 (endDate = effectiveDate - 1)
   - 計算時薪 (月薪 ÷ 240)
   - 建立新薪資結構
   - 發布 `SalaryStructureCreatedEvent`

3. **計算邏輯:**
   - 時薪 = 月薪 ÷ 240 (勞基法規定)
   - 投保薪資 = 底薪 + 可投保津貼

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `employeeId` | string (UUID) | Y | 有效 UUID | 員工 ID |
| `payrollSystem` | string | Y | `HOURLY` 或 `MONTHLY` | 薪資制度 |
| `payrollCycle` | string | Y | 見列舉值 | 領薪週期 |
| `hourlyRate` | number | C | > 0 (時薪制必填) | 時薪 |
| `monthlySalary` | number | C | > 0 (月薪制必填) | 月薪 |
| `salaryItems` | array | N | - | 薪資項目列表 |
| `salaryItems[].itemCode` | string | Y | 1-50 字元 | 項目代碼 |
| `salaryItems[].itemName` | string | Y | 1-100 字元 | 項目名稱 |
| `salaryItems[].itemType` | string | Y | `EARNING` 或 `DEDUCTION` | 項目類型 |
| `salaryItems[].amount` | number | Y | >= 0 | 金額 |
| `salaryItems[].isFixedAmount` | boolean | N | - | 是否固定金額 |
| `salaryItems[].isTaxable` | boolean | N | - | 是否課稅 |
| `salaryItems[].isInsurable` | boolean | N | - | 是否納入投保 |
| `effectiveDate` | date | Y | >= 今日 | 生效日期 |

**Request 範例:**

```json
{
  "employeeId": "emp-001",
  "payrollSystem": "MONTHLY",
  "payrollCycle": "MONTHLY",
  "monthlySalary": 50000,
  "salaryItems": [
    {
      "itemCode": "JOB_ALLOWANCE",
      "itemName": "職務加給",
      "itemType": "EARNING",
      "amount": 5000,
      "isFixedAmount": true,
      "isTaxable": true,
      "isInsurable": true
    },
    {
      "itemCode": "MEAL_ALLOWANCE",
      "itemName": "伙食津貼",
      "itemType": "EARNING",
      "amount": 2400,
      "isFixedAmount": true,
      "isTaxable": false,
      "isInsurable": false
    }
  ],
  "effectiveDate": "2025-01-01"
}
```

#### Response 規格

**成功回應 (201 Created):**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| `structureId` | string | 薪資結構 ID |
| `employeeId` | string | 員工 ID |
| `monthlySalary` | number | 月薪 |
| `calculatedHourlyRate` | number | 計算時薪 (月薪÷240) |
| `totalMonthlyGross` | number | 月總收入 (底薪+津貼) |
| `insurableSalary` | number | 投保薪資 |
| `effectiveDate` | date | 生效日期 |

**Response 範例:**

```json
{
  "success": true,
  "data": {
    "structureId": "struct-001",
    "employeeId": "emp-001",
    "monthlySalary": 50000,
    "calculatedHourlyRate": 208.33,
    "totalMonthlyGross": 57400,
    "insurableSalary": 55000,
    "effectiveDate": "2025-01-01"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PAY_INVALID_SALARY_SYSTEM` | 薪資制度與填寫項目不符 | 月薪制填月薪，時薪制填時薪 |
| 400 | `PAY_INVALID_EFFECTIVE_DATE` | 生效日期不可早於今日 | 檢查日期 |
| 404 | `EMP_NOT_FOUND` | 員工不存在 | 確認員工 ID |
| 409 | `PAY_STRUCTURE_ALREADY_EXISTS` | 該日期已有薪資結構 | 使用更新 API |

#### 領域事件

**SalaryStructureCreatedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.payroll.salary-structure-created` |
| **觸發時機** | 薪資結構建立後 |
| **訂閱服務** | Insurance Service |

```json
{
  "eventId": "evt-struct-001",
  "eventType": "SalaryStructureCreated",
  "timestamp": "2025-12-06T10:00:00Z",
  "payload": {
    "structureId": "struct-001",
    "employeeId": "emp-001",
    "insurableSalary": 55000,
    "effectiveDate": "2025-01-01"
  }
}
```

---

### 2.2 查詢員工薪資結構

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/salary-structures/employee/{employeeId}` |
| **方法** | GET |
| **Controller** | `HR04SalaryStructureQryController` |
| **Service** | `GetEmployeeSalaryStructureServiceImpl` |
| **權限** | `payroll:structure:read` |

#### 用途說明

- **業務場景:** 查詢特定員工目前生效的薪資結構
- **使用者:** HR、主管、員工本人
- **解決問題:** 檢視員工的薪資組成

#### Request 規格

**Path Parameters:**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| `employeeId` | string (UUID) | 員工 ID |

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `asOfDate` | date | N | 查詢特定日期的結構 (預設今日) |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "structureId": "struct-001",
    "employeeId": "emp-001",
    "employeeName": "張三",
    "employeeNumber": "E0001",
    "payrollSystem": "MONTHLY",
    "payrollCycle": "MONTHLY",
    "monthlySalary": 50000,
    "hourlyRate": null,
    "calculatedHourlyRate": 208.33,
    "salaryItems": [
      {
        "itemCode": "JOB_ALLOWANCE",
        "itemName": "職務加給",
        "itemType": "EARNING",
        "amount": 5000,
        "isTaxable": true,
        "isInsurable": true
      },
      {
        "itemCode": "MEAL_ALLOWANCE",
        "itemName": "伙食津貼",
        "itemType": "EARNING",
        "amount": 2400,
        "isTaxable": false,
        "isInsurable": false
      }
    ],
    "totalMonthlyGross": 57400,
    "insurableSalary": 55000,
    "effectiveDate": "2025-01-01",
    "endDate": null,
    "isActive": true
  }
}
```

---

### 2.3 更新薪資結構

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/salary-structures/{structureId}` |
| **方法** | PUT |
| **Controller** | `HR04SalaryStructureCmdController` |
| **Service** | `UpdateSalaryStructureServiceImpl` |
| **權限** | `payroll:structure:manage` |

#### 用途說明

- **業務場景:** 調整員工薪資
- **使用者:** HR 管理員
- **解決問題:** 處理調薪、津貼變更等情況

#### 業務邏輯

1. **驗證規則:**
   - 僅能更新 DRAFT 狀態的結構
   - 已生效的結構需建立新版本

2. **處理步驟:**
   - 若結構已生效，建立新版本並設定新生效日
   - 發布 `SalaryStructureChangedEvent`

#### Request 規格

**Request Body:** (同建立，所有欄位選填)

```json
{
  "monthlySalary": 55000,
  "salaryItems": [
    {
      "itemCode": "JOB_ALLOWANCE",
      "itemName": "職務加給",
      "itemType": "EARNING",
      "amount": 8000,
      "isTaxable": true,
      "isInsurable": true
    }
  ],
  "effectiveDate": "2025-04-01"
}
```

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "structureId": "struct-002",
    "employeeId": "emp-001",
    "monthlySalary": 55000,
    "totalMonthlyGross": 65400,
    "effectiveDate": "2025-04-01",
    "isNewVersion": true,
    "previousStructureId": "struct-001"
  }
}
```

#### 領域事件

**SalaryStructureChangedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.payroll.salary-structure-changed` |
| **觸發時機** | 薪資結構變更後 |
| **訂閱服務** | Insurance Service |

```json
{
  "eventId": "evt-struct-002",
  "eventType": "SalaryStructureChanged",
  "timestamp": "2025-12-06T11:00:00Z",
  "payload": {
    "structureId": "struct-002",
    "employeeId": "emp-001",
    "previousMonthlySalary": 50000,
    "newMonthlySalary": 55000,
    "previousInsurableSalary": 55000,
    "newInsurableSalary": 63000,
    "effectiveDate": "2025-04-01",
    "changeType": "SALARY_ADJUSTMENT"
  }
}
```

---

### 2.4 查詢薪資結構列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/salary-structures` |
| **方法** | GET |
| **Controller** | `HR04SalaryStructureQryController` |
| **Service** | `GetSalaryStructuresServiceImpl` |
| **權限** | `payroll:structure:read` |

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `organizationId` | string (UUID) | N | 組織 ID |
| `departmentId` | string (UUID) | N | 部門 ID |
| `payrollSystem` | string | N | 篩選薪資制度 |
| `isActive` | boolean | N | 是否僅查詢生效中 |
| `page` | integer | N | 頁碼 |
| `pageSize` | integer | N | 每頁筆數 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "structures": [
      {
        "structureId": "struct-001",
        "employeeId": "emp-001",
        "employeeName": "張三",
        "employeeNumber": "E0001",
        "departmentName": "資訊部",
        "payrollSystem": "MONTHLY",
        "monthlySalary": 50000,
        "totalMonthlyGross": 57400,
        "effectiveDate": "2025-01-01",
        "isActive": true
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "totalRecords": 150,
      "totalPages": 8
    },
    "summary": {
      "totalEmployees": 150,
      "avgMonthlySalary": 52000,
      "totalMonthlyPayroll": 7800000
    }
  }
}
```

---

### 2.5 刪除薪資結構

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `DELETE /api/v1/salary-structures/{structureId}` |
| **方法** | DELETE |
| **Controller** | `HR04SalaryStructureCmdController` |
| **Service** | `DeleteSalaryStructureServiceImpl` |
| **權限** | `payroll:structure:manage` |

#### 業務邏輯

- 僅能刪除尚未生效的薪資結構
- 已生效或已用於薪資計算的結構不可刪除

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "structureId": "struct-001",
    "deleted": true,
    "deletedAt": "2025-12-06T10:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PAY_STRUCTURE_ALREADY_EFFECTIVE` | 結構已生效，不可刪除 | 建立新版本替代 |
| 400 | `PAY_STRUCTURE_USED_IN_PAYROLL` | 結構已用於薪資計算 | 無法刪除 |

---

## 3. 薪資計算批次 API

### 3.1 建立薪資計算批次

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/payroll-runs` |
| **方法** | POST |
| **Controller** | `HR04PayrollRunCmdController` |
| **Service** | `CreatePayrollRunServiceImpl` |
| **權限** | `payroll:run:create` |

#### 用途說明

- **業務場景:** 建立月度薪資計算批次
- **使用者:** HR 管理員、薪資專員
- **解決問題:** 啟動月度薪資計算流程

#### 業務邏輯

1. **驗證規則:**
   - 檢查該薪資週期是否已有批次
   - 驗證薪資週期日期的合理性
   - 發薪日須晚於週期結束日

2. **處理步驟:**
   - 建立批次記錄，狀態為 DRAFT
   - 發布 `PayrollRunCreatedEvent`

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `organizationId` | string (UUID) | Y | 有效 UUID | 組織 ID |
| `payPeriodStart` | date | Y | YYYY-MM-DD | 薪資週期起日 |
| `payPeriodEnd` | date | Y | > payPeriodStart | 薪資週期迄日 |
| `payDate` | date | Y | > payPeriodEnd | 發薪日 |

**Request 範例:**

```json
{
  "organizationId": "org-001",
  "payPeriodStart": "2025-11-01",
  "payPeriodEnd": "2025-11-30",
  "payDate": "2025-12-05"
}
```

#### Response 規格

**成功回應 (201 Created):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "organizationId": "org-001",
    "status": "DRAFT",
    "payPeriodStart": "2025-11-01",
    "payPeriodEnd": "2025-11-30",
    "payDate": "2025-12-05",
    "createdAt": "2025-12-01T09:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PAY_INVALID_DATE_RANGE` | 日期範圍無效 | 檢查週期日期 |
| 409 | `PAY_RUN_ALREADY_EXISTS` | 該薪資週期已有批次 | 查詢現有批次 |

---

### 3.2 執行薪資計算

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/payroll-runs/{runId}/execute` |
| **方法** | POST |
| **Controller** | `HR04PayrollRunCmdController` |
| **Service** | `ExecutePayrollRunServiceImpl` |
| **權限** | `payroll:run:execute` |

#### 用途說明

- **業務場景:** 執行薪資計算 Saga
- **使用者:** 薪資專員
- **解決問題:** 自動計算所有員工的月薪

#### 業務邏輯

1. **Saga 流程:**
   - 從 Organization Service 獲取在職員工清單
   - 從 Attendance Service 獲取差勤月結數據
   - 從 Insurance Service 獲取勞健保費用
   - 對每位員工計算薪資單
   - 儲存所有薪資單

2. **計算公式:**
   ```
   應發薪資 = 底薪 + Σ(收入項目) + 加班費 - 請假扣款
   應扣項目 = 勞保費 + 健保費 + 勞退自提 + 所得稅 + 二代健保補充保費
   實發薪資 = 應發薪資 - 應扣項目
   ```

3. **加班費計算:**
   - 平日前2小時：時薪 × 1.34
   - 平日後2小時：時薪 × 1.67
   - 休息日前2小時：時薪 × 1.34
   - 休息日2-8小時：時薪 × 1.67
   - 休息日8小時後：時薪 × 2.67
   - 國定假日：時薪 × 2.0

#### Request 規格

**Path Parameters:**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| `runId` | string (UUID) | 批次 ID |

#### Response 規格

**成功回應 (202 Accepted):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "status": "CALCULATING",
    "message": "薪資計算已啟動，請稍後查詢結果",
    "estimatedCompletionTime": "2025-12-01T09:10:00Z"
  }
}
```

**計算完成後查詢狀態 (200 OK):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "status": "COMPLETED",
    "totalEmployees": 150,
    "processedEmployees": 148,
    "failedEmployees": 2,
    "totalGrossAmount": 8500000,
    "totalNetAmount": 7200000,
    "totalDeductions": 1300000,
    "executedBy": "薪資專員",
    "executedAt": "2025-12-01T09:05:00Z",
    "completedAt": "2025-12-01T09:08:00Z",
    "failedDetails": [
      {
        "employeeId": "emp-099",
        "employeeName": "李四",
        "errorCode": "PAY_STRUCTURE_NOT_FOUND",
        "errorMessage": "找不到薪資結構"
      }
    ]
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PAY_RUN_INVALID_STATUS` | 批次狀態不允許執行 | 僅 DRAFT 可執行 |
| 400 | `PAY_RUN_ALREADY_CALCULATING` | 批次正在計算中 | 等待計算完成 |
| 424 | `PAY_ATTENDANCE_NOT_CLOSED` | 差勤尚未月結 | 請先執行差勤月結 |

#### 領域事件

**PayrollRunStartedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.payroll.run-started` |
| **觸發時機** | 開始執行計算 |
| **訂閱服務** | - |

**PayrollRunCompletedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.payroll.run-completed` |
| **觸發時機** | 計算完成 |
| **訂閱服務** | Notification Service, Report Service |

```json
{
  "eventId": "evt-pay-001",
  "eventType": "PayrollRunCompleted",
  "timestamp": "2025-12-01T09:08:00Z",
  "payload": {
    "runId": "run-202511",
    "organizationId": "org-001",
    "payPeriod": "2025-11-01 ~ 2025-11-30",
    "payDate": "2025-12-05",
    "statistics": {
      "totalEmployees": 150,
      "processedEmployees": 148,
      "failedEmployees": 2,
      "totalGrossAmount": 8500000,
      "totalNetAmount": 7200000,
      "totalDeductions": 1300000
    }
  }
}
```

---

### 3.3 查詢薪資計算批次

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/payroll-runs/{runId}` |
| **方法** | GET |
| **Controller** | `HR04PayrollRunQryController` |
| **Service** | `GetPayrollRunServiceImpl` |
| **權限** | `payroll:run:read` |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "organizationId": "org-001",
    "organizationName": "ABC公司",
    "payPeriodStart": "2025-11-01",
    "payPeriodEnd": "2025-11-30",
    "payDate": "2025-12-05",
    "status": "COMPLETED",
    "statistics": {
      "totalEmployees": 150,
      "processedEmployees": 148,
      "failedEmployees": 2,
      "totalGrossAmount": 8500000,
      "totalNetAmount": 7200000,
      "totalDeductions": 1300000,
      "avgGrossWage": 56667,
      "avgNetWage": 48000
    },
    "timeline": {
      "createdAt": "2025-12-01T09:00:00Z",
      "createdBy": "薪資專員",
      "executedAt": "2025-12-01T09:05:00Z",
      "executedBy": "薪資專員",
      "completedAt": "2025-12-01T09:08:00Z",
      "submittedAt": null,
      "approvedAt": null,
      "paidAt": null
    }
  }
}
```

---

### 3.4 查詢薪資計算批次列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/payroll-runs` |
| **方法** | GET |
| **Controller** | `HR04PayrollRunQryController` |
| **Service** | `GetPayrollRunsServiceImpl` |
| **權限** | `payroll:run:read` |

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `organizationId` | string (UUID) | N | 組織 ID |
| `year` | integer | N | 年度 (預設當年) |
| `status` | string | N | 狀態篩選 |
| `page` | integer | N | 頁碼 |
| `pageSize` | integer | N | 每頁筆數 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "runs": [
      {
        "runId": "run-202511",
        "payPeriod": "2025-11",
        "payDate": "2025-12-05",
        "status": "COMPLETED",
        "totalEmployees": 150,
        "totalNetAmount": 7200000,
        "createdAt": "2025-12-01T09:00:00Z"
      },
      {
        "runId": "run-202510",
        "payPeriod": "2025-10",
        "payDate": "2025-11-05",
        "status": "PAID",
        "totalEmployees": 148,
        "totalNetAmount": 7100000,
        "createdAt": "2025-11-01T09:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 12,
      "totalRecords": 11,
      "totalPages": 1
    }
  }
}
```

---

### 3.5 送審薪資

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/payroll-runs/{runId}/submit` |
| **方法** | PUT |
| **Controller** | `HR04PayrollRunCmdController` |
| **Service** | `SubmitPayrollRunServiceImpl` |
| **權限** | `payroll:run:submit` |

#### 用途說明

- **業務場景:** 薪資計算完成後送交主管核准
- **使用者:** 薪資專員
- **解決問題:** 啟動薪資核准流程

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "status": "SUBMITTED",
    "submittedBy": "薪資專員",
    "submittedAt": "2025-12-01T10:00:00Z",
    "nextApprover": "財務經理"
  }
}
```

---

### 3.6 核准薪資

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/payroll-runs/{runId}/approve` |
| **方法** | PUT |
| **Controller** | `HR04PayrollRunCmdController` |
| **Service** | `ApprovePayrollRunServiceImpl` |
| **權限** | `payroll:run:approve` |

#### 用途說明

- **業務場景:** 財務主管核准薪資
- **使用者:** 財務經理、總經理
- **解決問題:** 完成薪資發放前的核准程序

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `comment` | string | N | 核准備註 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "status": "APPROVED",
    "approvedBy": "財務經理",
    "approvedAt": "2025-12-02T09:00:00Z"
  }
}
```

#### 領域事件

**PayrollApprovedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.payroll.approved` |
| **觸發時機** | 薪資核准後 |
| **訂閱服務** | - |

---

### 3.7 駁回薪資

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/payroll-runs/{runId}/reject` |
| **方法** | PUT |
| **Controller** | `HR04PayrollRunCmdController` |
| **Service** | `RejectPayrollRunServiceImpl` |
| **權限** | `payroll:run:approve` |

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `reason` | string | Y | 駁回原因 |

**Request 範例:**

```json
{
  "reason": "員工 E0023 的加班費計算有誤，請重新確認"
}
```

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "status": "COMPLETED",
    "rejectedBy": "財務經理",
    "rejectedAt": "2025-12-02T09:00:00Z",
    "rejectionReason": "員工 E0023 的加班費計算有誤，請重新確認"
  }
}
```

---

### 3.8 確認發放

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/payroll-runs/{runId}/pay` |
| **方法** | PUT |
| **Controller** | `HR04PayrollRunCmdController` |
| **Service** | `PayPayrollRunServiceImpl` |
| **權限** | `payroll:run:pay` |

#### 用途說明

- **業務場景:** 確認薪資已發放完成
- **使用者:** 財務人員
- **解決問題:** 標記薪資已透過銀行轉帳發放

#### 業務邏輯

1. **處理步驟:**
   - 更新批次狀態為 PAID
   - 更新所有薪資單狀態為 FINALIZED
   - 發布 `PayrollPaidEvent`

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "status": "PAID",
    "paidAt": "2025-12-05T00:00:00Z",
    "totalAmount": 7200000,
    "employeeCount": 148
  }
}
```

#### 領域事件

**PayrollPaidEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.payroll.paid` |
| **觸發時機** | 薪資發放確認後 |
| **訂閱服務** | Report Service |

```json
{
  "eventId": "evt-pay-002",
  "eventType": "PayrollPaid",
  "timestamp": "2025-12-05T00:00:00Z",
  "payload": {
    "runId": "run-202511",
    "organizationId": "org-001",
    "payPeriod": "2025-11",
    "payDate": "2025-12-05",
    "totalAmount": 7200000,
    "employeeCount": 148
  }
}
```

---

## 4. 薪資單管理 API

### 4.1 查詢我的薪資單 (ESS)

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/payslips/my` |
| **方法** | GET |
| **Controller** | `HR04PayslipQryController` |
| **Service** | `GetMyPayslipsServiceImpl` |
| **權限** | `payroll:payslip:read:self` |

#### 用途說明

- **業務場景:** 員工查詢自己的薪資單
- **使用者:** 全體員工
- **解決問題:** 員工自助服務查詢歷史薪資

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `year` | integer | N | 年度 (預設當年) |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "employeeId": "emp-001",
    "employeeName": "張三",
    "year": 2025,
    "payslips": [
      {
        "payslipId": "slip-001",
        "payPeriod": "2025-11",
        "payDate": "2025-12-05",
        "grossWage": 60600,
        "netWage": 56600,
        "status": "SENT",
        "hasPdf": true
      },
      {
        "payslipId": "slip-002",
        "payPeriod": "2025-10",
        "payDate": "2025-11-05",
        "grossWage": 58400,
        "netWage": 54500,
        "status": "SENT",
        "hasPdf": true
      }
    ],
    "yearSummary": {
      "totalGrossWage": 660000,
      "totalNetWage": 600000,
      "totalIncomeTax": 24000,
      "monthCount": 11
    }
  }
}
```

---

### 4.2 查詢薪資單詳情

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/payslips/{payslipId}` |
| **方法** | GET |
| **Controller** | `HR04PayslipQryController` |
| **Service** | `GetPayslipDetailServiceImpl` |
| **權限** | `payroll:payslip:read` |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "payslipId": "slip-001",
    "payrollRunId": "run-202511",
    "employeeNumber": "E0001",
    "employeeName": "張三",
    "departmentName": "資訊部",
    "payPeriod": "2025-11",
    "payPeriodStart": "2025-11-01",
    "payPeriodEnd": "2025-11-30",
    "payDate": "2025-12-05",
    "payrollSystem": "MONTHLY",

    "baseSalary": 50000,
    "earnings": [
      {"itemCode": "JOB_ALLOWANCE", "itemName": "職務加給", "amount": 5000},
      {"itemCode": "MEAL_ALLOWANCE", "itemName": "伙食津貼", "amount": 2400}
    ],
    "totalEarnings": 57400,

    "overtimePay": {
      "weekdayHours": 8,
      "weekdayRate": 1.34,
      "weekdayPay": 2234,
      "weekdayHours2": 2,
      "weekdayRate2": 1.67,
      "weekdayPay2": 696,
      "restDayHours": 0,
      "restDayPay": 0,
      "holidayHours": 0,
      "holidayPay": 0,
      "total": 2930
    },

    "leaveDeduction": 0,
    "leaveDetails": [],

    "grossWage": 60330,

    "deductions": {
      "laborInsurance": 1200,
      "healthInsurance": 800,
      "pensionSelfContribution": 0,
      "incomeTax": 1800,
      "supplementaryPremium": 0
    },
    "otherDeductions": [],
    "totalDeductions": 3800,

    "netWage": 56530,

    "bankAccount": {
      "bankCode": "012",
      "bankName": "台北富邦",
      "accountNumber": "****5678"
    },

    "status": "SENT",
    "pdfUrl": "/api/v1/payslips/slip-001/pdf",
    "emailSentAt": "2025-12-05T08:00:00Z"
  }
}
```

---

### 4.3 下載薪資單 PDF

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/payslips/{payslipId}/pdf` |
| **方法** | GET |
| **Controller** | `HR04PayslipQryController` |
| **Service** | `DownloadPayslipPdfServiceImpl` |
| **權限** | `payroll:payslip:read` |

#### 用途說明

- **業務場景:** 下載薪資單 PDF 檔案
- **使用者:** 員工本人、HR
- **解決問題:** 提供正式的薪資單文件

#### 業務邏輯

- PDF 採用員工身分證後 4 碼加密
- PDF 包含公司名稱、員工資訊、薪資明細
- 符合勞基法規定的薪資單格式

#### Response 規格

**成功回應 (200 OK):**

```
Content-Type: application/pdf
Content-Disposition: attachment; filename="payslip_202511_E0001.pdf"
X-PDF-Encrypted: true

(加密 PDF 二進位檔案)
```

---

### 4.4 發送薪資單 Email

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/payroll-runs/{runId}/send-payslips` |
| **方法** | POST |
| **Controller** | `HR04PayslipCmdController` |
| **Service** | `SendPayslipsServiceImpl` |
| **權限** | `payroll:payslip:send` |

#### 用途說明

- **業務場景:** 批次發送薪資單 Email 給所有員工
- **使用者:** 薪資專員
- **解決問題:** 自動化薪資單通知

#### 業務邏輯

1. **處理步驟:**
   - 生成所有員工的 PDF 薪資單
   - 透過 Notification Service 發送 Email
   - 更新薪資單狀態為 SENT
   - 發布 `PayslipSentEvent`

#### Response 規格

**成功回應 (202 Accepted):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "status": "SENDING",
    "totalPayslips": 148,
    "message": "薪資單正在發送中，請稍後查詢結果"
  }
}
```

**發送完成後 (200 OK):**

```json
{
  "success": true,
  "data": {
    "runId": "run-202511",
    "sentCount": 146,
    "failedCount": 2,
    "failedEmployees": [
      {"employeeId": "emp-050", "reason": "Email 地址無效"}
    ],
    "completedAt": "2025-12-05T08:30:00Z"
  }
}
```

#### 領域事件

**PayslipSentEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.payroll.payslip-sent` |
| **觸發時機** | 薪資單發送完成 |
| **訂閱服務** | - |

---

## 5. 薪轉檔案 API

### 5.1 產生銀行薪轉檔

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/payroll-runs/{runId}/bank-transfer` |
| **方法** | POST |
| **Controller** | `HR04BankTransferCmdController` |
| **Service** | `GenerateBankTransferServiceImpl` |
| **權限** | `payroll:bank-transfer:create` |

#### 用途說明

- **業務場景:** 產生銀行薪轉媒體檔
- **使用者:** 財務人員
- **解決問題:** 產生可上傳至網銀的薪轉檔案

#### 業務邏輯

1. **驗證規則:**
   - 批次狀態須為 APPROVED
   - 所有員工須有有效的銀行帳戶

2. **檔案格式:**
   - 支援各大銀行的標準媒體檔格式
   - 包含：員工姓名、銀行代碼、帳號、金額

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `bankCode` | string | Y | 主要往來銀行代碼 |
| `transferDate` | date | Y | 轉帳日期 |
| `format` | string | N | 檔案格式 (預設 STANDARD) |

**Request 範例:**

```json
{
  "bankCode": "012",
  "transferDate": "2025-12-05",
  "format": "STANDARD"
}
```

#### Response 規格

**成功回應 (201 Created):**

```json
{
  "success": true,
  "data": {
    "fileId": "bank-file-001",
    "runId": "run-202511",
    "bankCode": "012",
    "bankName": "台北富邦銀行",
    "transferDate": "2025-12-05",
    "totalRecords": 148,
    "totalAmount": 7200000,
    "fileUrl": "/api/v1/payroll-runs/run-202511/bank-transfer/download",
    "generatedAt": "2025-12-04T10:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PAY_RUN_NOT_APPROVED` | 批次尚未核准 | 先完成核准流程 |
| 400 | `PAY_MISSING_BANK_ACCOUNT` | 部分員工無銀行帳戶 | 補齊銀行帳戶資料 |

---

### 5.2 下載銀行薪轉檔

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/payroll-runs/{runId}/bank-transfer/download` |
| **方法** | GET |
| **Controller** | `HR04BankTransferCmdController` |
| **Service** | `DownloadBankTransferServiceImpl` |
| **權限** | `payroll:bank-transfer:download` |

#### Response 規格

**成功回應 (200 OK):**

```
Content-Type: text/plain; charset=Big5
Content-Disposition: attachment; filename="payroll_202511_012.txt"

(銀行薪轉媒體檔內容)
```

---

## 6. 附錄：列舉值定義

### 6.1 薪資制度 (PayrollSystem)

| 值 | 說明 |
|:---|:---|
| `HOURLY` | 時薪制 - 依實際工時計算 |
| `MONTHLY` | 月薪制 - 固定月薪 |

### 6.2 領薪週期 (PayrollCycle)

| 值 | 說明 |
|:---|:---|
| `DAILY` | 日結日領 |
| `WEEKLY` | 週結週領 |
| `BI_WEEKLY` | 雙週結薪 |
| `MONTHLY` | 月結月領 |

### 6.3 薪資項目類型 (ItemType)

| 值 | 說明 |
|:---|:---|
| `EARNING` | 收入項 - 計入應發 |
| `DEDUCTION` | 扣除項 - 計入應扣 |

### 6.4 薪資批次狀態 (PayrollRunStatus)

| 值 | 說明 | 可轉換至 |
|:---|:---|:---|
| `DRAFT` | 草稿 | CALCULATING |
| `CALCULATING` | 計算中 | COMPLETED |
| `COMPLETED` | 計算完成 | SUBMITTED, CANCELLED |
| `SUBMITTED` | 已送審 | APPROVED, COMPLETED (駁回) |
| `APPROVED` | 已核准 | PAID |
| `PAID` | 已發放 | - |
| `CANCELLED` | 已取消 | - |

### 6.5 薪資單狀態 (PayslipStatus)

| 值 | 說明 |
|:---|:---|
| `DRAFT` | 草稿 - 計算中 |
| `FINALIZED` | 已確定 - 薪資已發放 |
| `SENT` | 已發送 - Email 已寄出 |

### 6.6 常用薪資項目代碼

| 代碼 | 名稱 | 類型 | 課稅 | 投保 |
|:---|:---|:---|:---:|:---:|
| `BASIC_SALARY` | 本薪 | EARNING | Y | Y |
| `JOB_ALLOWANCE` | 職務加給 | EARNING | Y | Y |
| `SKILL_ALLOWANCE` | 技術津貼 | EARNING | Y | Y |
| `MEAL_ALLOWANCE` | 伙食津貼 | EARNING | N | N |
| `TRANSPORT_ALLOWANCE` | 交通津貼 | EARNING | Y | N |
| `OVERTIME_PAY` | 加班費 | EARNING | Y | N |
| `BONUS` | 獎金 | EARNING | Y | N |
| `LABOR_INSURANCE` | 勞保費 | DEDUCTION | - | - |
| `HEALTH_INSURANCE` | 健保費 | DEDUCTION | - | - |
| `PENSION_SELF` | 勞退自提 | DEDUCTION | - | - |
| `INCOME_TAX` | 所得稅 | DEDUCTION | - | - |

### 6.7 加班費率 (勞基法規定)

| 加班類型 | 時段 | 費率 |
|:---|:---|:---:|
| 平日加班 | 前 2 小時 | 1.34 |
| 平日加班 | 第 3-4 小時 | 1.67 |
| 休息日 | 前 2 小時 | 1.34 |
| 休息日 | 第 3-8 小時 | 1.67 |
| 休息日 | 第 9 小時起 | 2.67 |
| 國定假日 | 全部 | 2.0 |

---

## 7. 領域事件總覽

| 事件名稱 | Kafka Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| `SalaryStructureCreated` | `hrms.payroll.salary-structure-created` | 建立薪資結構 | Insurance |
| `SalaryStructureChanged` | `hrms.payroll.salary-structure-changed` | 薪資調整 | Insurance |
| `PayrollRunStarted` | `hrms.payroll.run-started` | 開始薪資計算 | - |
| `PayrollRunCompleted` | `hrms.payroll.run-completed` | 薪資計算完成 | Notification, Report |
| `PayslipGenerated` | `hrms.payroll.payslip-generated` | 產生薪資單 | Document |
| `PayrollApproved` | `hrms.payroll.approved` | 薪資核准 | - |
| `PayrollPaid` | `hrms.payroll.paid` | 薪資已發放 | Report |
| `PayslipSent` | `hrms.payroll.payslip-sent` | 薪資單已寄送 | - |

---

**文件結束**

**版本歷史:**

| 版本 | 日期 | 說明 |
|:---|:---|:---|
| 1.0 | 2025-12-29 | 初版建立 |
