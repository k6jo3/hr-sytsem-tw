# HR04 薪資管理服務 — 預借薪資業務合約

> **服務代碼:** HR04
> **功能模組:** 預借薪資 (Salary Advance)
> **版本:** 1.0
> **建立日期:** 2026-03-16
> **說明:** 本檔案為預借薪資獨立合約，待合併至 `payroll_contracts.md`

---

## API 端點概覽

### 預借薪資管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/payroll/salary-advances` | POST | PAY_CMD_SA001 | 申請預借薪資 | 待實作 |
| 2 | `PUT /api/v1/payroll/salary-advances/{id}/approve` | PUT | PAY_CMD_SA002 | 核准預借薪資 | 待實作 |
| 3 | `PUT /api/v1/payroll/salary-advances/{id}/reject` | PUT | PAY_CMD_SA003 | 駁回預借薪資 | 待實作 |
| 4 | `PUT /api/v1/payroll/salary-advances/{id}/disburse` | PUT | PAY_CMD_SA004 | 撥款預借薪資 | 待實作 |
| 5 | `PUT /api/v1/payroll/salary-advances/{id}/cancel` | PUT | PAY_CMD_SA005 | 取消預借薪資 | 待實作 |
| 6 | `GET /api/v1/payroll/salary-advances` | GET | PAY_QRY_SA001 | 查詢預借薪資列表 | 待實作 |
| 7 | `GET /api/v1/payroll/salary-advances/{id}` | GET | PAY_QRY_SA002 | 查詢單筆預借薪資 | 待實作 |

---

## 1. Command 操作業務合約

### 1.1 申請預借薪資

#### PAY_CMD_SA001: 申請預借薪資

**API 端點：** `POST /api/v1/payroll/salary-advances`

**業務場景描述：**

員工申請預借薪資，系統建立一筆待審核的預借申請記錄。申請金額必須大於零，分期月數至少為 1 個月。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_SA001",
  "apiEndpoint": "POST /api/v1/payroll/salary-advances",
  "controller": "HR04SalaryAdvanceCmdController",
  "service": "applySalaryAdvanceServiceImpl",
  "permission": "salary-advance:create",
  "request": {
    "employeeId": "emp-uuid-001",
    "requestedAmount": 30000,
    "installmentMonths": 3,
    "reason": "家庭急需"
  },
  "businessRules": [
    {"rule": "申請金額必須 > 0"},
    {"rule": "分期月數必須 >= 1"},
    {"rule": "員工 ID 不可為空"},
    {"rule": "預借上限 = (應發薪資 - 法定扣除 - 法扣款) x 90%"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "salary_advances",
      "count": 1,
      "assertions": [
        {"field": "advance_id", "operator": "notNull"},
        {"field": "employee_id", "operator": "equals", "value": "emp-uuid-001"},
        {"field": "requested_amount", "operator": "equals", "value": 30000},
        {"field": "installment_months", "operator": "equals", "value": 3},
        {"field": "status", "operator": "equals", "value": "PENDING"},
        {"field": "application_date", "operator": "notNull"},
        {"field": "reason", "operator": "equals", "value": "家庭急需"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "SalaryAdvanceAppliedEvent",
      "payload": [
        {"field": "advanceId", "operator": "notNull"},
        {"field": "employeeId", "operator": "equals", "value": "emp-uuid-001"},
        {"field": "requestedAmount", "operator": "equals", "value": 30000}
      ]
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "advanceId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "requestedAmount", "type": "number", "notNull": true},
      {"name": "installmentMonths", "type": "integer", "notNull": true},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "applicationDate", "type": "string", "notNull": true},
      {"name": "reason", "type": "string", "notNull": false}
    ]
  },
  "errorScenarios": [
    {
      "scenarioId": "PAY_CMD_SA001_ERR01",
      "description": "申請金額 <= 0",
      "expectedResponse": {"statusCode": 400, "errorCode": "INVALID_ADVANCE_AMOUNT"}
    },
    {
      "scenarioId": "PAY_CMD_SA001_ERR02",
      "description": "分期月數 < 1",
      "expectedResponse": {"statusCode": 400, "errorCode": "INVALID_INSTALLMENT_MONTHS"}
    },
    {
      "scenarioId": "PAY_CMD_SA001_ERR03",
      "description": "員工 ID 為空",
      "expectedResponse": {"statusCode": 400, "errorCode": "EMPLOYEE_ID_REQUIRED"}
    }
  ],
  "frontendAdapterMapping": {
    "status": {
      "backendField": "status",
      "possibleValues": ["PENDING", "APPROVED", "REJECTED", "DISBURSED", "REPAYING", "FULLY_REPAID", "CANCELLED"],
      "note": "前端必須處理所有可能值，禁止靜默 fallback"
    }
  }
}
```

---

### 1.2 核准預借薪資

#### PAY_CMD_SA002: 核准預借薪資

**API 端點：** `PUT /api/v1/payroll/salary-advances/{id}/approve`

**業務場景描述：**

主管核准員工的預借薪資申請，可調整核准金額（不得超過申請金額）。核准後系統自動計算每期扣回金額。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_SA002",
  "apiEndpoint": "PUT /api/v1/payroll/salary-advances/{id}/approve",
  "controller": "HR04SalaryAdvanceCmdController",
  "service": "approveSalaryAdvanceServiceImpl",
  "permission": "salary-advance:approve",
  "request": {
    "approvedAmount": 25000
  },
  "businessRules": [
    {"rule": "僅 PENDING 狀態可核准"},
    {"rule": "核准金額不可超過申請金額"},
    {"rule": "核准金額必須 > 0"},
    {"rule": "系統自動計算 installmentAmount = ceiling(approvedAmount / installmentMonths)"},
    {"rule": "remainingBalance 初始值 = approvedAmount"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "salary_advances",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "APPROVED"},
        {"field": "approved_amount", "operator": "equals", "value": 25000},
        {"field": "remaining_balance", "operator": "equals", "value": 25000},
        {"field": "installment_amount", "operator": "notNull"},
        {"field": "approver_id", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "SalaryAdvanceApprovedEvent",
      "payload": [
        {"field": "advanceId", "operator": "notNull"},
        {"field": "approvedAmount", "operator": "equals", "value": 25000}
      ]
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "advanceId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "approvedAmount", "type": "number", "notNull": true},
      {"name": "installmentAmount", "type": "number", "notNull": true},
      {"name": "remainingBalance", "type": "number", "notNull": true}
    ]
  },
  "errorScenarios": [
    {
      "scenarioId": "PAY_CMD_SA002_ERR01",
      "description": "非 PENDING 狀態核准",
      "expectedResponse": {"statusCode": 400, "errorCode": "INVALID_ADVANCE_STATUS"}
    },
    {
      "scenarioId": "PAY_CMD_SA002_ERR02",
      "description": "核准金額超過申請金額",
      "expectedResponse": {"statusCode": 400, "errorCode": "APPROVED_AMOUNT_EXCEEDS_REQUESTED"}
    },
    {
      "scenarioId": "PAY_CMD_SA002_ERR03",
      "description": "核准金額 <= 0",
      "expectedResponse": {"statusCode": 400, "errorCode": "INVALID_APPROVED_AMOUNT"}
    }
  ]
}
```

---

### 1.3 駁回預借薪資

#### PAY_CMD_SA003: 駁回預借薪資

**API 端點：** `PUT /api/v1/payroll/salary-advances/{id}/reject`

**業務場景描述：**

主管駁回員工的預借薪資申請，需填寫駁回原因。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_SA003",
  "apiEndpoint": "PUT /api/v1/payroll/salary-advances/{id}/reject",
  "controller": "HR04SalaryAdvanceCmdController",
  "service": "rejectSalaryAdvanceServiceImpl",
  "permission": "salary-advance:approve",
  "request": {
    "reason": "預借金額過高，建議調降後重新申請"
  },
  "businessRules": [
    {"rule": "僅 PENDING 狀態可駁回"},
    {"rule": "駁回原因記錄至 rejectionReason 欄位"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "salary_advances",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "REJECTED"},
        {"field": "rejection_reason", "operator": "notNull"},
        {"field": "approver_id", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "SalaryAdvanceRejectedEvent",
      "payload": [
        {"field": "advanceId", "operator": "notNull"},
        {"field": "reason", "operator": "notNull"}
      ]
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "advanceId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "rejectionReason", "type": "string", "notNull": true}
    ]
  },
  "errorScenarios": [
    {
      "scenarioId": "PAY_CMD_SA003_ERR01",
      "description": "非 PENDING 狀態駁回",
      "expectedResponse": {"statusCode": 400, "errorCode": "INVALID_ADVANCE_STATUS"}
    }
  ]
}
```

---

### 1.4 撥款預借薪資

#### PAY_CMD_SA004: 撥款預借薪資

**API 端點：** `PUT /api/v1/payroll/salary-advances/{id}/disburse`

**業務場景描述：**

財務人員對已核准的預借薪資進行撥款，系統記錄撥款日期並將狀態變更為已撥款。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_SA004",
  "apiEndpoint": "PUT /api/v1/payroll/salary-advances/{id}/disburse",
  "controller": "HR04SalaryAdvanceCmdController",
  "service": "disburseSalaryAdvanceServiceImpl",
  "permission": "salary-advance:disburse",
  "request": {},
  "businessRules": [
    {"rule": "僅 APPROVED 狀態可撥款"},
    {"rule": "撥款日期記錄為當天日期"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "salary_advances",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "DISBURSED"},
        {"field": "disbursement_date", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "SalaryAdvanceDisbursedEvent",
      "payload": [
        {"field": "advanceId", "operator": "notNull"},
        {"field": "disbursementDate", "operator": "notNull"}
      ]
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "advanceId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "disbursementDate", "type": "string", "notNull": true}
    ]
  },
  "errorScenarios": [
    {
      "scenarioId": "PAY_CMD_SA004_ERR01",
      "description": "非 APPROVED 狀態撥款",
      "expectedResponse": {"statusCode": 400, "errorCode": "INVALID_ADVANCE_STATUS"}
    }
  ]
}
```

---

### 1.5 取消預借薪資

#### PAY_CMD_SA005: 取消預借薪資

**API 端點：** `PUT /api/v1/payroll/salary-advances/{id}/cancel`

**業務場景描述：**

取消預借薪資申請。已撥款（DISBURSED）、扣回中（REPAYING）、已全額扣回（FULLY_REPAID）狀態不可取消。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_SA005",
  "apiEndpoint": "PUT /api/v1/payroll/salary-advances/{id}/cancel",
  "controller": "HR04SalaryAdvanceCmdController",
  "service": "cancelSalaryAdvanceServiceImpl",
  "permission": "salary-advance:cancel",
  "request": {},
  "businessRules": [
    {"rule": "PENDING 狀態可取消"},
    {"rule": "APPROVED 狀態可取消"},
    {"rule": "REJECTED 狀態可取消"},
    {"rule": "DISBURSED 狀態不可取消"},
    {"rule": "REPAYING 狀態不可取消"},
    {"rule": "FULLY_REPAID 狀態不可取消"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "salary_advances",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "CANCELLED"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "SalaryAdvanceCancelledEvent",
      "payload": [
        {"field": "advanceId", "operator": "notNull"}
      ]
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "advanceId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  },
  "errorScenarios": [
    {
      "scenarioId": "PAY_CMD_SA005_ERR01",
      "description": "已撥款/扣回中/已全額扣回狀態不可取消",
      "expectedResponse": {"statusCode": 400, "errorCode": "ADVANCE_CANNOT_CANCEL"}
    }
  ]
}
```

---

## 2. Query 操作業務合約

### 2.1 查詢預借薪資列表

#### PAY_QRY_SA001: 查詢預借薪資列表

**API 端點：** `GET /api/v1/payroll/salary-advances`

**業務場景描述：**

查詢預借薪資列表，支援依員工 ID 與狀態篩選。

**測試合約：**

```json
{
  "scenarioId": "PAY_QRY_SA001",
  "apiEndpoint": "GET /api/v1/payroll/salary-advances",
  "controller": "HR04SalaryAdvanceQryController",
  "service": "getSalaryAdvancesServiceImpl",
  "permission": "salary-advance:read",
  "request": {
    "employeeId": "emp-uuid-001",
    "status": "PENDING"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "emp-uuid-001"},
    {"field": "status", "operator": "=", "value": "PENDING"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "requiredFields": [
      {"name": "advanceId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "requestedAmount", "type": "number", "notNull": true},
      {"name": "approvedAmount", "type": "number", "notNull": false},
      {"name": "installmentMonths", "type": "integer", "notNull": true},
      {"name": "installmentAmount", "type": "number", "notNull": false},
      {"name": "repaidAmount", "type": "number", "notNull": false},
      {"name": "remainingBalance", "type": "number", "notNull": false},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "applicationDate", "type": "string", "notNull": true},
      {"name": "disbursementDate", "type": "string", "notNull": false},
      {"name": "reason", "type": "string", "notNull": false}
    ]
  }
}
```

---

### 2.2 查詢單筆預借薪資

#### PAY_QRY_SA002: 查詢單筆預借薪資

**API 端點：** `GET /api/v1/payroll/salary-advances/{id}`

**業務場景描述：**

依 ID 查詢單筆預借薪資的完整資訊。

**測試合約：**

```json
{
  "scenarioId": "PAY_QRY_SA002",
  "apiEndpoint": "GET /api/v1/payroll/salary-advances/{id}",
  "controller": "HR04SalaryAdvanceQryController",
  "service": "getSalaryAdvanceDetailServiceImpl",
  "permission": "salary-advance:read",
  "request": {},
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "advanceId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "requestedAmount", "type": "number", "notNull": true},
      {"name": "approvedAmount", "type": "number", "notNull": false},
      {"name": "installmentMonths", "type": "integer", "notNull": true},
      {"name": "installmentAmount", "type": "number", "notNull": false},
      {"name": "repaidAmount", "type": "number", "notNull": false},
      {"name": "remainingBalance", "type": "number", "notNull": false},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "applicationDate", "type": "string", "notNull": true},
      {"name": "disbursementDate", "type": "string", "notNull": false},
      {"name": "reason", "type": "string", "notNull": false},
      {"name": "rejectionReason", "type": "string", "notNull": false},
      {"name": "approverId", "type": "string", "notNull": false}
    ]
  },
  "errorScenarios": [
    {
      "scenarioId": "PAY_QRY_SA002_ERR01",
      "description": "預借薪資 ID 不存在",
      "expectedResponse": {"statusCode": 404, "errorCode": "SALARY_ADVANCE_NOT_FOUND"}
    }
  ]
}
```

---

## 附錄：預借薪資狀態流程

```
                       ┌──────────────┐
                       │   PENDING    │
                       │   (待審核)    │
                       └──────┬───────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
              ▼               ▼               ▼
       ┌──────────┐    ┌──────────┐    ┌──────────┐
       │ APPROVED │    │ REJECTED │    │CANCELLED │
       │ (已核准)  │    │ (已駁回)  │    │ (已取消)  │
       └─────┬────┘    └──────────┘    └──────────┘
             │
             ▼
       ┌──────────┐
       │DISBURSED │
       │ (已撥款)  │
       └─────┬────┘
             │
             ▼
       ┌──────────┐
       │ REPAYING │
       │ (扣回中)  │
       └─────┬────┘
             │
             ▼
       ┌────────────┐
       │FULLY_REPAID│
       │(已全額扣回) │
       └────────────┘
```

**可取消的狀態：** PENDING, APPROVED, REJECTED
**不可取消的狀態：** DISBURSED, REPAYING, FULLY_REPAID

---

**文件建立日期:** 2026-03-16
**版本:** 1.0
