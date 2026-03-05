# HR04 薪資管理服務業務合約

> **服務代碼:** HR04
> **服務名稱:** 薪資管理服務 (Payroll Management)
> **版本:** 1.0
> **更新日期:** 2026-02-23

---

## 概述

薪資管理服務是 HR 系統中計算最複雜的服務，負責薪資結構管理、薪資批次計算、薪資單管理、獎金管理、扣款管理、加班費計算及銀行薪轉檔案產生等功能。採用 Saga 模式整合差勤、保險、工時等跨服務數據進行薪資計算。

---

## API 端點概覽

### 薪資結構管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/salary-structures` | POST | PAY_CMD_S001 | 建立薪資結構 | ✅ 已實作 |
| 2 | `PUT /api/v1/salary-structures/{id}` | PUT | PAY_CMD_S002 | 更新薪資結構 | ✅ 已實作 |
| 3 | `DELETE /api/v1/salary-structures/{id}` | DELETE | PAY_CMD_S003 | 刪除薪資結構 | ✅ 已實作 |
| 4 | `GET /api/v1/salary-structures` | GET | PAY_QRY_S001~S007 | 查詢薪資結構列表 | ✅ 已實作 |
| 5 | `GET /api/v1/salary-structures/employee/{employeeId}` | GET | - | 查詢員工薪資結構 | ✅ 已實作 |

### 薪資批次管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/payroll-runs` | POST | PAY_CMD_R001 | 建立薪資計算批次 | ✅ 已實作 |
| 2 | `POST /api/v1/payroll-runs/{runId}/execute` | POST | PAY_CMD_R002 | 執行薪資計算 | ✅ 已實作 |
| 3 | `PUT /api/v1/payroll-runs/{runId}/submit` | PUT | PAY_CMD_R003 | 送審薪資批次 | ✅ 已實作 |
| 4 | `PUT /api/v1/payroll-runs/{runId}/approve` | PUT | PAY_CMD_R004 | 核准薪資批次 | ✅ 已實作 |
| 5 | `PUT /api/v1/payroll-runs/{runId}/reject` | PUT | PAY_CMD_R005 | 退回薪資批次 | ✅ 已實作 |
| 6 | `PUT /api/v1/payroll-runs/{runId}/pay` | PUT | PAY_CMD_R006 | 標記已發薪 | ✅ 已實作 |
| 7 | `GET /api/v1/payroll-runs` | GET | PAY_QRY_R001~R008 | 查詢薪資批次列表 | ✅ 已實作 |
| 8 | `GET /api/v1/payroll-runs/{runId}` | GET | - | 查詢單一薪資批次 | ✅ 已實作 |

### 薪資單管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/payroll-runs/{runId}/send-payslips` | POST | PAY_CMD_P001 | 發送薪資單 Email | ✅ 已實作 |
| 2 | `GET /api/v1/payslips` | GET | PAY_QRY_P001~P009 | 查詢薪資單列表 | ✅ 已實作 |
| 3 | `GET /api/v1/payslips/{payslipId}` | GET | - | 查詢單一薪資單 | ✅ 已實作 |
| 4 | `GET /api/v1/payslips/{payslipId}/pdf` | GET | - | 取得薪資單 PDF | ✅ 已實作 |

### 銀行薪轉 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/payroll-runs/{runId}/bank-transfer` | POST | PAY_CMD_BT001 | 產生薪轉檔案 | ✅ 已實作 |
| 2 | `GET /api/v1/payroll-runs/{runId}/bank-transfer/download` | GET | - | 下載薪轉檔案 | ✅ 已實作 |

**總計：19 個 API 端點**

**場景分類：**
- **Command 操作：** 11 個（3 薪資結構 + 6 薪資批次 + 1 薪資單 + 1 薪轉）
- **Query 操作：** 38 個（7 薪資結構 + 9 薪資單 + 6 獎金 + 5 扣款 + 3 加班費 + 8 薪資批次）

---

## 1. Command 操作業務合約

### 1.1 薪資結構管理

#### PAY_CMD_S001: 建立薪資結構

**API 端點：** `POST /api/v1/salary-structures`

**業務場景描述：**

HR 為員工建立薪資結構，定義底薪、津貼等薪資項目。支援月薪制與時薪制。每位員工同一時間只能有一個有效的薪資結構。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_S001",
  "apiEndpoint": "POST /api/v1/salary-structures",
  "controller": "HR04SalaryStructureCmdController",
  "service": "CreateSalaryStructureServiceImpl",
  "permission": "salary:create",
  "request": {
    "employeeId": "emp-uuid-001",
    "monthlySalary": 50000,
    "hourlyRate": null,
    "payrollSystem": "MONTHLY",
    "payrollCycle": "MONTHLY",
    "effectiveDate": "2025-01-01",
    "items": [
      {"code": "BASE", "name": "底薪", "type": "EARNING", "amount": 45000, "fixedAmount": true, "taxable": true, "insurable": true},
      {"code": "MEAL", "name": "伙食津貼", "type": "EARNING", "amount": 2400, "fixedAmount": true, "taxable": false, "insurable": false}
    ]
  },
  "businessRules": [
    {"rule": "employeeId 必須存在"},
    {"rule": "同一員工不可有重疊生效期間的有效結構"},
    {"rule": "monthlySalary 或 hourlyRate 至少填一項（依 payrollSystem 決定）"},
    {"rule": "薪資項目至少需包含底薪"},
    {"rule": "設定 isActive = true"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "salary_structures",
      "count": 1,
      "assertions": [
        {"field": "structure_id", "operator": "notNull"},
        {"field": "employee_id", "operator": "equals", "value": "emp-uuid-001"},
        {"field": "monthly_salary", "operator": "equals", "value": 50000},
        {"field": "payroll_system", "operator": "equals", "value": "MONTHLY"},
        {"field": "is_active", "operator": "equals", "value": true},
        {"field": "effective_date", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "SalaryStructureCreatedEvent",
      "payload": [
        {"field": "structureId", "operator": "notNull"},
        {"field": "employeeId", "operator": "equals", "value": "emp-uuid-001"},
        {"field": "payrollSystem", "operator": "equals", "value": "MONTHLY"}
      ]
    }
  ]
}
```

---

#### PAY_CMD_S002: 更新薪資結構

**API 端點：** `PUT /api/v1/salary-structures/{structureId}`

**業務場景描述：**

HR 調整員工薪資結構的底薪、津貼或到期日等。調薪記錄需保留歷史。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_S002",
  "apiEndpoint": "PUT /api/v1/salary-structures/{structureId}",
  "controller": "HR04SalaryStructureCmdController",
  "service": "UpdateSalaryStructureServiceImpl",
  "permission": "salary:write",
  "request": {
    "monthlySalary": 55000,
    "effectiveDate": "2025-07-01",
    "items": [
      {"code": "BASE", "name": "底薪", "type": "EARNING", "amount": 50000, "fixedAmount": true, "taxable": true, "insurable": true},
      {"code": "MEAL", "name": "伙食津貼", "type": "EARNING", "amount": 2400, "fixedAmount": true, "taxable": false, "insurable": false}
    ]
  },
  "businessRules": [
    {"rule": "薪資結構必須存在且為有效狀態"},
    {"rule": "調整後底薪不可低於基本工資"},
    {"rule": "更新薪資項目明細"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "salary_structures",
      "count": 1,
      "assertions": [
        {"field": "monthly_salary", "operator": "equals", "value": 55000}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "SalaryStructureUpdatedEvent",
      "payload": [
        {"field": "structureId", "operator": "notNull"},
        {"field": "employeeId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### PAY_CMD_S003: 刪除薪資結構

**API 端點：** `DELETE /api/v1/salary-structures/{structureId}`

**業務場景描述：**

HR 停用薪資結構。採用邏輯刪除（isActive = false），保留歷史記錄。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_S003",
  "apiEndpoint": "DELETE /api/v1/salary-structures/{structureId}",
  "controller": "HR04SalaryStructureCmdController",
  "service": "DeleteSalaryStructureServiceImpl",
  "permission": "salary:delete",
  "request": {
    "structureId": "structure-uuid-001"
  },
  "businessRules": [
    {"rule": "薪資結構必須存在"},
    {"rule": "設定 isActive = false（邏輯刪除）"},
    {"rule": "設定 endDate 為當前日期"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "salary_structures",
      "count": 1,
      "assertions": [
        {"field": "is_active", "operator": "equals", "value": false},
        {"field": "end_date", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

### 1.2 薪資批次管理

#### PAY_CMD_R001: 建立薪資計算批次

**API 端點：** `POST /api/v1/payroll-runs`

**業務場景描述：**

HR 建立薪資計算批次，指定計薪期間、組織範圍及發薪日。批次建立後狀態為 DRAFT。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_R001",
  "apiEndpoint": "POST /api/v1/payroll-runs",
  "controller": "HR04PayrollRunCmdController",
  "service": "StartPayrollRunServiceImpl",
  "permission": "payroll:run:create",
  "request": {
    "payrollSystem": "MONTHLY",
    "organizationId": "org-001",
    "startDate": "2025-01-01",
    "endDate": "2025-01-31",
    "payDate": "2025-02-05",
    "name": "2025年1月月薪"
  },
  "businessRules": [
    {"rule": "同組織同期間不可重複建立批次"},
    {"rule": "payDate 必須晚於 endDate"},
    {"rule": "設定狀態為 DRAFT"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "payroll_runs",
      "count": 1,
      "assertions": [
        {"field": "run_id", "operator": "notNull"},
        {"field": "payroll_system", "operator": "equals", "value": "MONTHLY"},
        {"field": "organization_id", "operator": "equals", "value": "org-001"},
        {"field": "status", "operator": "equals", "value": "DRAFT"},
        {"field": "name", "operator": "equals", "value": "2025年1月月薪"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### PAY_CMD_R002: 執行薪資計算

**API 端點：** `POST /api/v1/payroll-runs/{runId}/execute`

**業務場景描述：**

觸發 Saga 流程進行薪資計算。系統整合差勤（加班時數）、保險（勞健保費）、工時等數據計算每位員工的應發薪資、扣項及實發金額，並產生薪資單。

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_R002",
  "apiEndpoint": "POST /api/v1/payroll-runs/{runId}/execute",
  "controller": "HR04PayrollRunCmdController",
  "service": "CalculatePayrollServiceImpl",
  "permission": "payroll:run:execute",
  "request": {
    "runId": "run-uuid-001"
  },
  "businessRules": [
    {"rule": "批次狀態必須為 DRAFT"},
    {"rule": "整合差勤服務取得加班時數"},
    {"rule": "整合保險服務取得勞健保費"},
    {"rule": "依薪資結構計算各員工薪資"},
    {"rule": "計算所得稅扣繳"},
    {"rule": "產生每位員工的薪資單"},
    {"rule": "更新批次狀態為 COMPLETED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "payroll_runs",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "COMPLETED"}
      ]
    },
    {
      "action": "INSERT",
      "table": "payslips",
      "count": null,
      "assertions": [
        {"field": "payslip_id", "operator": "notNull"},
        {"field": "run_id", "operator": "notNull"},
        {"field": "employee_id", "operator": "notNull"},
        {"field": "status", "operator": "equals", "value": "DRAFT"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "PayrollCalculatedEvent",
      "payload": [
        {"field": "runId", "operator": "notNull"},
        {"field": "organizationId", "operator": "notNull"},
        {"field": "employeeCount", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### PAY_CMD_R003: 送審薪資批次

**API 端點：** `PUT /api/v1/payroll-runs/{runId}/submit`

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_R003",
  "apiEndpoint": "PUT /api/v1/payroll-runs/{runId}/submit",
  "controller": "HR04PayrollRunCmdController",
  "service": "SubmitPayrollRunServiceImpl",
  "permission": "payroll:run:submit",
  "request": {
    "runId": "run-uuid-001"
  },
  "businessRules": [
    {"rule": "批次狀態必須為 COMPLETED"},
    {"rule": "更新狀態為 SUBMITTED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "payroll_runs",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "SUBMITTED"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### PAY_CMD_R004: 核准薪資批次

**API 端點：** `PUT /api/v1/payroll-runs/{runId}/approve`

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_R004",
  "apiEndpoint": "PUT /api/v1/payroll-runs/{runId}/approve",
  "controller": "HR04PayrollRunCmdController",
  "service": "ApprovePayrollRunServiceImpl",
  "permission": "payroll:run:approve",
  "request": {
    "runId": "run-uuid-001"
  },
  "businessRules": [
    {"rule": "批次狀態必須為 SUBMITTED"},
    {"rule": "核准者不可為建立者"},
    {"rule": "更新狀態為 APPROVED"},
    {"rule": "更新所有薪資單狀態為 CONFIRMED"},
    {"rule": "發布 PayrollApprovedEvent"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "payroll_runs",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "APPROVED"},
        {"field": "approved_by", "operator": "notNull"},
        {"field": "approved_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "PayrollApprovedEvent",
      "payload": [
        {"field": "runId", "operator": "notNull"},
        {"field": "approverId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### PAY_CMD_R005: 退回薪資批次

**API 端點：** `PUT /api/v1/payroll-runs/{runId}/reject`

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_R005",
  "apiEndpoint": "PUT /api/v1/payroll-runs/{runId}/reject",
  "controller": "HR04PayrollRunCmdController",
  "service": "RejectPayrollRunServiceImpl",
  "permission": "payroll:run:approve",
  "request": {
    "runId": "run-uuid-001",
    "reason": "加班費計算有誤，請重新確認"
  },
  "businessRules": [
    {"rule": "批次狀態必須為 SUBMITTED"},
    {"rule": "退回原因不可為空"},
    {"rule": "更新狀態為 DRAFT（允許重新計算）"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "payroll_runs",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "DRAFT"},
        {"field": "rejection_reason", "operator": "equals", "value": "加班費計算有誤，請重新確認"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### PAY_CMD_R006: 標記已發薪

**API 端點：** `PUT /api/v1/payroll-runs/{runId}/pay`

**測試合約：**

```json
{
  "scenarioId": "PAY_CMD_R006",
  "apiEndpoint": "PUT /api/v1/payroll-runs/{runId}/pay",
  "controller": "HR04PayrollRunCmdController",
  "service": "MarkPayrollRunPaidServiceImpl",
  "permission": "payroll:run:pay",
  "request": {
    "runId": "run-uuid-001"
  },
  "businessRules": [
    {"rule": "批次狀態必須為 APPROVED"},
    {"rule": "更新狀態為 PAID"},
    {"rule": "更新所有薪資單狀態為 SENT"},
    {"rule": "發布 PayrollPaidEvent"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "payroll_runs",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "PAID"},
        {"field": "paid_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "PayrollPaidEvent",
      "payload": [
        {"field": "runId", "operator": "notNull"},
        {"field": "organizationId", "operator": "notNull"},
        {"field": "payDate", "operator": "notNull"}
      ]
    }
  ]
}
```

---

## 2. Query 操作業務合約

### 查詢過濾條件驗證表

以下表格定義各查詢場景必須包含的過濾條件，供合約測試引擎自動驗證。

> **軟刪除策略說明：**
> - **薪資結構：** 使用 `isActive` 欄位（非 `is_deleted`）
> - **薪資單：** 不進行軟刪除，無 `is_deleted` 欄位
> - **薪資批次：** 使用 `status`（CANCELLED 代表已取消）

#### 2.1 薪資結構查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_S001 | 查詢員工薪資結構 | HR | `{"employeeId":"E001"}` | `employeeId = 'E001'`, `isActive = 'true'` |
| PAY_QRY_S002 | 查詢有效薪資結構 | HR | `{"isActive":true}` | `isActive = 'true'` |
| PAY_QRY_S003 | 查詢月薪制結構 | HR | `{"payrollSystem":"MONTHLY"}` | `payrollSystem = 'MONTHLY'`, `isActive = 'true'` |
| PAY_QRY_S004 | 查詢時薪制結構 | HR | `{"payrollSystem":"HOURLY"}` | `payrollSystem = 'HOURLY'`, `isActive = 'true'` |
| PAY_QRY_S005 | 組合條件查詢 | HR | `{"employeeId":"E001","isActive":true}` | `employeeId = 'E001'`, `isActive = 'true'` |
| PAY_QRY_S006 | 查詢停用的薪資結構 | HR | `{"employeeId":"E001","isActive":false}` | `employeeId = 'E001'`, `isActive = 'false'` |
| PAY_QRY_S007 | 查詢特定生效日期結構 | HR | `{"employeeId":"E001","effectiveDate":"2025-01-01"}` | `employeeId = 'E001'`, `effectiveDate <= '2025-01-01'`, `(endDate IS NULL OR endDate > '2025-01-01')` |

#### 2.2 薪資單查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_P001 | 查詢批次下薪資單 | HR | `{"runId":"RUN001"}` | `runId = 'RUN001'` |
| PAY_QRY_P002 | 員工查詢自己薪資單 | EMPLOYEE | `{"employeeId":"{currentUserId}"}` | `employeeId = '{currentUserId}'` |
| PAY_QRY_P003 | HR 查詢特定員工薪資單 | HR | `{"employeeId":"E001"}` | `employeeId = 'E001'` |
| PAY_QRY_P004 | 組合條件查詢薪資單 | HR | `{"runId":"RUN001","employeeId":"E001"}` | `runId = 'RUN001'`, `employeeId = 'E001'` |
| PAY_QRY_P005 | 員工查詢特定月份薪資單 | EMPLOYEE | `{"employeeId":"{currentUserId}","yearMonth":"2025-01"}` | `employeeId = '{currentUserId}'`, `periodStartDate >= '2025-01-01'`, `periodEndDate <= '2025-01-31'` |
| PAY_QRY_P006 | 員工查詢歷史薪資單 | EMPLOYEE | `{"employeeId":"{currentUserId}","status":"SENT"}` | `employeeId = '{currentUserId}'`, `status = 'SENT'` |
| PAY_QRY_P007 | 依發放日期查詢 | HR | `{"payDate":"2025-01-05"}` | `payDate = '2025-01-05'` |
| PAY_QRY_P008 | 查詢草稿狀態薪資單 | HR | `{"status":"DRAFT"}` | `status = 'DRAFT'` |
| PAY_QRY_P009 | 查詢已寄送薪資單 | HR | `{"status":"SENT"}` | `status = 'SENT'` |

#### 2.3 獎金查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_B001 | 查詢員工獎金 | HR | `{"employeeId":"E001"}` | `employeeId = 'E001'` |
| PAY_QRY_B002 | 依獎金類型查詢 | HR | `{"bonusType":"PERFORMANCE"}` | `bonusType = 'PERFORMANCE'` |
| PAY_QRY_B003 | 查詢年終獎金 | HR | `{"bonusType":"YEAR_END","year":2025}` | `bonusType = 'YEAR_END'`, `payYear = '2025'` |
| PAY_QRY_B004 | 依發放狀態查詢 | HR | `{"status":"PAID"}` | `status = 'PAID'` |
| PAY_QRY_B005 | 員工查詢自己獎金 | EMPLOYEE | `{"employeeId":"{currentUserId}"}` | `employeeId = '{currentUserId}'` |
| PAY_QRY_B006 | 查詢待發放獎金 | HR | `{"status":"APPROVED"}` | `status = 'APPROVED'` |

#### 2.4 扣款項目查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_D001 | 查詢員工扣款項目 | HR | `{"employeeId":"E001"}` | `employeeId = 'E001'` |
| PAY_QRY_D002 | 依扣款類型查詢 | HR | `{"deductionType":"LOAN"}` | `deductionType = 'LOAN'` |
| PAY_QRY_D003 | 查詢進行中的扣款 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| PAY_QRY_D004 | 查詢已結清的扣款 | HR | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| PAY_QRY_D005 | 員工查詢自己扣款 | EMPLOYEE | `{"employeeId":"{currentUserId}"}` | `employeeId = '{currentUserId}'` |

#### 2.5 加班費計算查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_O001 | 查詢員工加班費 | HR | `{"employeeId":"E001","yearMonth":"2025-01"}` | `employeeId = 'E001'`, `yearMonth = '2025-01'` |
| PAY_QRY_O002 | 查詢部門加班費 | HR | `{"deptId":"D001","yearMonth":"2025-01"}` | `deptId = 'D001'`, `yearMonth = '2025-01'` |
| PAY_QRY_O003 | 員工查詢自己加班費 | EMPLOYEE | `{"employeeId":"{currentUserId}","yearMonth":"2025-01"}` | `employeeId = '{currentUserId}'`, `yearMonth = '2025-01'` |

#### 2.6 薪資批次查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_R001 | HR 查詢特定組織薪資批次 | HR | `{"organizationId":"ORG001"}` | `organizationId = 'ORG001'` |
| PAY_QRY_R002 | HR 查詢特定狀態薪資批次 | HR | `{"status":"SUBMITTED"}` | `status = 'SUBMITTED'` |
| PAY_QRY_R003 | HR 查詢日期範圍內批次 | HR | `{"startDate":"2025-01-01","endDate":"2025-01-31"}` | `periodStartDate >= '2025-01-01'`, `periodEndDate <= '2025-01-31'` |
| PAY_QRY_R004 | 查詢草稿狀態批次 | HR | `{"status":"DRAFT"}` | `status = 'DRAFT'` |
| PAY_QRY_R005 | 查詢已核准批次 | HR | `{"status":"APPROVED"}` | `status = 'APPROVED'` |
| PAY_QRY_R006 | 查詢已發薪批次 | HR | `{"status":"PAID"}` | `status = 'PAID'` |
| PAY_QRY_R007 | 排除已取消的批次 | HR | `{"excludeCancelled":true}` | `status != 'CANCELLED'` |
| PAY_QRY_R008 | 查詢計算完成的批次 | HR | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |

---

## 附錄 A：薪資批次狀態流轉

```
┌────────┐    ┌───────────┐    ┌───────────┐    ┌──────────┐    ┌────────┐
│ DRAFT  │───▶│ COMPLETED │───▶│ SUBMITTED │───▶│ APPROVED │───▶│  PAID  │
└────────┘    └───────────┘    └───────────┘    └──────────┘    └────────┘
     ▲                              │
     │                              │
     └──────────────────────────────┘
                (REJECT)

     任何狀態 ──▶ CANCELLED
```

| 狀態 | 說明 |
|:---|:---|
| DRAFT | 草稿（可修改、可執行計算） |
| COMPLETED | 計算完成（可送審） |
| SUBMITTED | 已送審（待核准） |
| APPROVED | 已核准（可發薪） |
| PAID | 已發薪 |
| CANCELLED | 已取消 |

---

## 附錄 B：領域事件總覽

| 事件名稱 | Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| SalaryStructureCreatedEvent | `payroll.salary-structure.created` | 建立薪資結構 | Reporting |
| SalaryStructureUpdatedEvent | `payroll.salary-structure.updated` | 更新薪資結構 | Reporting |
| PayrollCalculatedEvent | `payroll.calculated` | 完成薪資計算 | Reporting |
| PayrollApprovedEvent | `payroll.approved` | 核准薪資批次 | Notification |
| PayrollPaidEvent | `payroll.paid` | 已發薪 | Notification, Document |

---

---

## 擴充功能合約（2026-03-05 新增）

### 薪資更正機制

#### PAY_CORR_001 — 薪資單作廢

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | PAY_CORR_001 |
| **場景名稱** | 作廢已核准薪資單 |
| **前置條件** | 薪資單存在且狀態為 APPROVED/PAID |
| **輸入** | payslipId, reason, operator |
| **預期行為** | 狀態變更為 VOIDED, 記錄作廢原因與操作者 |
| **輸出** | VoidedPayslip |
| **副作用** | payslips 表 UPDATE, payslip_audit_logs 表 INSERT |

#### PAY_CORR_002 — 薪資沖正

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | PAY_CORR_002 |
| **場景名稱** | 沖正薪資差額 |
| **前置條件** | 已作廢薪資單, 新計算金額與原金額不同 |
| **輸入** | originalPayslipId, correctedAmount, reason |
| **預期行為** | 產生沖正記錄（正/負差額）, 加入下期薪資批次 |
| **輸出** | CorrectionRecord (差額金額) |
| **副作用** | payslip_corrections 表 INSERT |

---

### 薪資預借

#### PAY_ADV_001 — 申請薪資預借

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | PAY_ADV_001 |
| **場景名稱** | 員工申請薪資預借 |
| **前置條件** | 員工存在, 預借金額 > 0 |
| **輸入** | employeeId, amount, reason, expectedRepayMonths |
| **預期行為** | 建立 PENDING 狀態預借申請 |
| **輸出** | SalaryAdvance (status=PENDING) |
| **副作用** | hr04_salary_advances 表 INSERT |
| **業務規則** | 預借上限 = (總薪 - 法定扣除 - 法扣) × 0.9 |

#### PAY_ADV_002 — 核准/駁回薪資預借

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | PAY_ADV_002 |
| **場景名稱** | 核准或駁回薪資預借 |
| **前置條件** | 預借申請存在, 狀態為 PENDING |
| **輸入** | advanceId, action (APPROVE/REJECT), approverNote |
| **預期行為** | APPROVE → APPROVED → 撥款後 DISBURSED; REJECT → REJECTED |
| **輸出** | SalaryAdvance |
| **副作用** | hr04_salary_advances 表 UPDATE |

#### PAY_ADV_003 — 薪資預借扣回

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | PAY_ADV_003 |
| **場景名稱** | 每月薪資扣回預借金額 |
| **前置條件** | 預借狀態為 DISBURSED 或 REPAYING |
| **輸入** | advanceId, repayAmount |
| **預期行為** | 扣除金額, 更新已還/未還金額; 全額還清 → FULLY_REPAID |
| **輸出** | actualRepaid, remainingBalance |
| **副作用** | hr04_salary_advances 表 UPDATE |
| **業務規則** | repay(amount) 不超過 remainingBalance, 自動轉 REPAYING, 餘額 0 轉 FULLY_REPAID |

---

### 法扣款

#### PAY_LD_001 — 建立法扣命令

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | PAY_LD_001 |
| **場景名稱** | 建立法院/行政扣薪命令 |
| **前置條件** | 員工存在 |
| **輸入** | employeeId, caseNumber, courtName, garnishmentType, totalAmount, monthlyAmount, priority |
| **預期行為** | 建立 ACTIVE 狀態法扣記錄 |
| **輸出** | LegalDeduction (status=ACTIVE) |
| **副作用** | hr04_legal_deductions 表 INSERT |
| **業務規則** | garnishmentType: COURT_ORDER / ADMINISTRATIVE_LEVY |

#### PAY_LD_002 — 法扣每月扣除計算

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | PAY_LD_002 |
| **場景名稱** | 計算法扣最大可扣金額並扣除 |
| **前置條件** | 法扣記錄存在且 ACTIVE |
| **輸入** | netSalary, minimumLivingCost, dependentCost |
| **預期行為** | min(淨薪/3, 淨薪 - 最低生活費×1.2 - 扶養費) → 實際扣除 |
| **輸出** | actualDeducted, remainingBalance |
| **副作用** | hr04_legal_deductions 表 UPDATE |
| **業務規則** | 強制執行法 §115-1 三分之一規則, §122 最低生活保障, 法扣優先序: 法扣 > 勞健保 > 所得稅 > 預借 > 其他 |

---

**文件建立日期:** 2026-02-23
**版本:** 1.1（2026-03-05 擴充）
