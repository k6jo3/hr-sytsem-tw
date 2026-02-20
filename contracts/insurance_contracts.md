# HR05 保險管理服務業務合約

> **服務代碼:** HR05
> **服務名稱:** 保險管理服務 (Insurance Management)
> **版本:** 1.0
> **更新日期:** 2026-02-20

---

## 概述

保險管理服務負責勞健保、勞退的加退保管理、費用計算、投保級距管理及申報檔案匯出等功能。涵蓋投保單位管理、加退保管理、保費計算、補充保費計算、投保級距查詢及員工自助查詢（ESS）等模組。

---

## API 端點概覽

### 投保單位管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/insurance/units` | POST | INS_CMD_U001 | 建立投保單位 | ✅ 已實作 |
| 2 | `PUT /api/v1/insurance/units/{id}` | PUT | INS_CMD_U002 | 更新投保單位 | ✅ 已實作 |
| 3 | `GET /api/v1/insurance/units` | GET | INS_QRY_U001 | 查詢投保單位列表 | ✅ 已實作 |

### 加退保管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/insurance/enrollments` | POST | INS_CMD_E001 | 手動加保 | ✅ 已實作 |
| 2 | `PUT /api/v1/insurance/enrollments/{id}/withdraw` | PUT | INS_CMD_E002 | 退保 | ✅ 已實作 |
| 3 | `PUT /api/v1/insurance/enrollments/{id}/adjust-level` | PUT | INS_CMD_E003 | 調整投保級距 | ✅ 已實作 |
| 4 | `GET /api/v1/insurance/enrollments` | GET | INS_QRY_E001 | 查詢加退保記錄 | ✅ 已實作 |

### 費用計算 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/insurance/fees/calculate` | POST | INS_CMD_F001 | 計算保費 | ✅ 已實作 |
| 2 | `POST /api/v1/insurance/supplementary-premium/calculate` | POST | INS_CMD_F002 | 計算補充保費 | ✅ 已實作 |

### 投保級距查詢 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/insurance/levels` | GET | INS_QRY_LV001 | 查詢投保級距表 | ✅ 已實作 |

### 申報檔案 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/insurance/export/enrollment-report` | POST | INS_CMD_X001 | 匯出加退保申報檔 | ✅ 已實作 |

### 員工自助查詢 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/insurance/my` | GET | INS_QRY_MY001 | 查詢我的保險資訊 | ✅ 已實作 |

**總計：12 個場景（7 個 Command + 5 個 Query）**

---

## 1. Command 操作業務合約

### 1.1 投保單位管理

#### INS_CMD_U001: 建立投保單位

**API 端點：** `POST /api/v1/insurance/units`

**業務場景描述：**

母子公司分別投保時，HR 管理員建立不同投保單位。單位代碼在同組織內需唯一。

**測試合約：**

```json
{
  "scenarioId": "INS_CMD_U001",
  "apiEndpoint": "POST /api/v1/insurance/units",
  "controller": "HR05UnitCmdController",
  "service": "createInsuranceUnitServiceImpl",
  "permission": "insurance:unit:manage",
  "request": {
    "organizationId": "org-001",
    "unitCode": "INS-UNIT-001",
    "unitName": "ABC科技股份有限公司",
    "laborInsuranceNumber": "12345678",
    "healthInsuranceNumber": "H12345678",
    "pensionNumber": "P12345678"
  },
  "businessRules": [
    {"rule": "單位代碼在同組織內必須唯一"},
    {"rule": "勞保局代碼格式需符合規範 (8-10 碼)"},
    {"rule": "健保局代碼格式需符合規範"},
    {"rule": "新投保單位預設 is_active = true"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "insurance_units",
      "count": 1,
      "assertions": [
        {"field": "unit_id", "operator": "notNull"},
        {"field": "unit_code", "operator": "equals", "value": "INS-UNIT-001"},
        {"field": "unit_name", "operator": "equals", "value": "ABC科技股份有限公司"},
        {"field": "labor_insurance_number", "operator": "equals", "value": "12345678"},
        {"field": "is_active", "operator": "equals", "value": true}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### INS_CMD_U002: 更新投保單位

**API 端點：** `PUT /api/v1/insurance/units/{id}`

**業務場景描述：**

HR 管理員更新投保單位資訊（名稱、各局編號、啟用狀態等）。

**測試合約：**

```json
{
  "scenarioId": "INS_CMD_U002",
  "apiEndpoint": "PUT /api/v1/insurance/units/{id}",
  "controller": "HR05UnitCmdController",
  "service": "updateInsuranceUnitServiceImpl",
  "permission": "insurance:unit:manage",
  "request": {
    "unitName": "ABC科技股份有限公司 (更新)",
    "laborInsuranceNumber": "12345678",
    "isActive": true
  },
  "businessRules": [
    {"rule": "投保單位必須存在"},
    {"rule": "更新不影響已存在的加保記錄"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "insurance_units",
      "count": 1,
      "assertions": [
        {"field": "unit_name", "operator": "equals", "value": "ABC科技股份有限公司 (更新)"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

### 1.2 加退保管理

#### INS_CMD_E001: 手動加保

**API 端點：** `POST /api/v1/insurance/enrollments`

**業務場景描述：**

HR 專員手動為員工辦理勞健保加保。系統根據薪資自動對應投保級距，建立加保記錄並計算保費，完成後發布 `InsuranceEnrollmentCompleted` 事件通知 Payroll 服務。

**測試合約：**

```json
{
  "scenarioId": "INS_CMD_E001",
  "apiEndpoint": "POST /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentCmdController",
  "service": "createEnrollmentServiceImpl",
  "permission": "insurance:enrollment:manage",
  "request": {
    "employeeId": "emp-001",
    "insuranceUnitId": "unit-001",
    "insuranceTypes": ["LABOR", "HEALTH", "PENSION"],
    "enrollDate": "2025-01-01",
    "monthlySalary": 50000
  },
  "businessRules": [
    {"rule": "員工不可有相同保險類型的有效加保"},
    {"rule": "根據薪資自動對應投保級距"},
    {"rule": "加保日期需為未來或當月"},
    {"rule": "勞保、健保、勞退可分別加保"},
    {"rule": "計算保費並記錄"},
    {"rule": "新加保記錄狀態為 ACTIVE"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "insurance_enrollments",
      "count": 3,
      "assertions": [
        {"field": "enrollment_id", "operator": "notNull"},
        {"field": "employee_id", "operator": "equals", "value": "emp-001"},
        {"field": "insurance_unit_id", "operator": "equals", "value": "unit-001"},
        {"field": "status", "operator": "equals", "value": "ACTIVE"},
        {"field": "is_deleted", "operator": "equals", "value": 0}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "InsuranceEnrollmentCompleted",
      "payload": [
        {"field": "employeeId", "operator": "equals", "value": "emp-001"},
        {"field": "enrollments", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### INS_CMD_E002: 退保

**API 端點：** `PUT /api/v1/insurance/enrollments/{id}/withdraw`

**業務場景描述：**

員工離職時，HR 專員辦理勞健保退保。退保後發布 `InsuranceWithdrawalCompleted` 事件。

**測試合約：**

```json
{
  "scenarioId": "INS_CMD_E002",
  "apiEndpoint": "PUT /api/v1/insurance/enrollments/{id}/withdraw",
  "controller": "HR05EnrollmentCmdController",
  "service": "withdrawEnrollmentServiceImpl",
  "permission": "insurance:enrollment:manage",
  "request": {
    "withdrawDate": "2025-12-31",
    "reason": "RESIGNATION"
  },
  "businessRules": [
    {"rule": "加保記錄必須存在且狀態為 ACTIVE"},
    {"rule": "退保日期需晚於加保日期"},
    {"rule": "退保後狀態改為 WITHDRAWN"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "insurance_enrollments",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "WITHDRAWN"},
        {"field": "withdraw_date", "operator": "equals", "value": "2025-12-31"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "InsuranceWithdrawalCompleted",
      "payload": [
        {"field": "employeeId", "operator": "notNull"},
        {"field": "insuranceType", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### INS_CMD_E003: 調整投保級距

**API 端點：** `PUT /api/v1/insurance/enrollments/{id}/adjust-level`

**業務場景描述：**

員工調薪後，HR 專員調整投保級距。系統根據新薪資計算適當級距，驗證是否變更（避免無意義調整），更新後發布 `InsuranceLevelAdjusted` 事件。

**測試合約：**

```json
{
  "scenarioId": "INS_CMD_E003",
  "apiEndpoint": "PUT /api/v1/insurance/enrollments/{id}/adjust-level",
  "controller": "HR05EnrollmentCmdController",
  "service": "adjustEnrollmentLevelServiceImpl",
  "permission": "insurance:enrollment:manage",
  "request": {
    "newMonthlySalary": 55000,
    "effectiveDate": "2025-07-01",
    "reason": "年度調薪"
  },
  "businessRules": [
    {"rule": "加保記錄必須存在且狀態為 ACTIVE"},
    {"rule": "根據新薪資計算適當投保級距"},
    {"rule": "新級距與舊級距不可相同"},
    {"rule": "生效日期需為未來日期"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "insurance_enrollments",
      "count": 1,
      "assertions": [
        {"field": "monthly_salary", "operator": "notNull"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "InsuranceLevelAdjusted",
      "payload": [
        {"field": "employeeId", "operator": "notNull"},
        {"field": "previousLevel", "operator": "notNull"},
        {"field": "newLevel", "operator": "notNull"},
        {"field": "effectiveDate", "operator": "equals", "value": "2025-07-01"}
      ]
    }
  ]
}
```

---

### 1.3 費用計算

#### INS_CMD_F001: 計算保費

**API 端點：** `POST /api/v1/insurance/fees/calculate`

**業務場景描述：**

薪資計算時調用，取得員工保費資訊；或 HR 試算保費。支援實際員工計算與試算模式。

**測試合約：**

```json
{
  "scenarioId": "INS_CMD_F001",
  "apiEndpoint": "POST /api/v1/insurance/fees/calculate",
  "controller": "HR05FeeCmdController",
  "service": "calculateInsuranceFeeServiceImpl",
  "permission": "insurance:calculate",
  "request": {
    "employeeId": "emp-001",
    "yearMonth": "2025-01"
  },
  "businessRules": [
    {"rule": "勞保費(員工) = 投保薪資 × 11.5% × 20%"},
    {"rule": "勞保費(雇主) = 投保薪資 × 11.5% × 70%"},
    {"rule": "健保費(員工) = 投保薪資 × 5.17% × 30%"},
    {"rule": "健保費(雇主) = 投保薪資 × 5.17% × 60%"},
    {"rule": "勞退(雇主) = 投保薪資 × 6%"},
    {"rule": "員工需有有效加保記錄或使用試算模式"}
  ],
  "expectedDataChanges": [],
  "expectedEvents": []
}
```

---

#### INS_CMD_F002: 計算補充保費

**API 端點：** `POST /api/v1/insurance/supplementary-premium/calculate`

**業務場景描述：**

發放獎金、兼職所得時，計算二代健保補充保費。門檻為投保薪資 × 4，超過部分以 2.11% 費率計算。

**測試合約：**

```json
{
  "scenarioId": "INS_CMD_F002",
  "apiEndpoint": "POST /api/v1/insurance/supplementary-premium/calculate",
  "controller": "HR05FeeCmdController",
  "service": "calculateSupplementaryPremiumServiceImpl",
  "permission": "insurance:calculate",
  "request": {
    "employeeId": "emp-001",
    "incomeType": "BONUS",
    "incomeAmount": 250000,
    "incomeDate": "2025-01-31"
  },
  "businessRules": [
    {"rule": "取得員工投保薪資"},
    {"rule": "計算門檻 = 投保薪資 × 4"},
    {"rule": "判斷所得是否超過門檻"},
    {"rule": "計費基準 = 所得金額 - 門檻 (上限 1000 萬)"},
    {"rule": "補充保費 = 計費基準 × 2.11%"},
    {"rule": "員工需有有效健保加保"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "supplementary_premiums",
      "count": 1,
      "assertions": [
        {"field": "premium_id", "operator": "notNull"},
        {"field": "employee_id", "operator": "equals", "value": "emp-001"},
        {"field": "income_type", "operator": "equals", "value": "BONUS"},
        {"field": "premium_rate", "operator": "equals", "value": 0.0211}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "SupplementaryPremiumCalculated",
      "payload": [
        {"field": "employeeId", "operator": "equals", "value": "emp-001"},
        {"field": "premiumAmount", "operator": "notNull"}
      ]
    }
  ]
}
```

---

### 1.4 申報檔案

#### INS_CMD_X001: 匯出加退保申報檔

**API 端點：** `POST /api/v1/insurance/export/enrollment-report`

**業務場景描述：**

HR 專員產生勞保局/健保局規範格式的加退保申報檔。系統查詢指定期間未申報的加退保記錄，依規範格式產生檔案，並標記記錄為已申報。

**測試合約：**

```json
{
  "scenarioId": "INS_CMD_X001",
  "apiEndpoint": "POST /api/v1/insurance/export/enrollment-report",
  "controller": "HR05ExportCmdController",
  "service": "exportEnrollmentReportServiceImpl",
  "permission": "insurance:report:export",
  "request": {
    "insuranceUnitId": "unit-001",
    "reportType": "LABOR_ENROLLMENT",
    "dateFrom": "2025-01-01",
    "dateTo": "2025-01-31"
  },
  "businessRules": [
    {"rule": "投保單位必須存在"},
    {"rule": "查詢指定期間未申報的加退保記錄"},
    {"rule": "依勞保局/健保局格式產生申報檔"},
    {"rule": "標記記錄為已申報 (is_reported = true)"},
    {"rule": "無待申報記錄時回傳錯誤"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "insurance_enrollments",
      "assertions": [
        {"field": "is_reported", "operator": "equals", "value": true}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

## 2. Query 操作業務合約

### 查詢過濾條件驗證表

以下表格定義各查詢場景必須包含的過濾條件，供合約測試引擎自動驗證。

#### 勞保投保紀錄查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| INS_L001 | 查詢員工勞保紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0`, `insurance_type = 'LABOR'` |
| INS_L002 | 查詢有效勞保 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0`, `insurance_type = 'LABOR'` |
| INS_L003 | 查詢退保紀錄 | HR | `{"status":"TERMINATED"}` | `status = 'TERMINATED'`, `is_deleted = 0`, `insurance_type = 'LABOR'` |
| INS_L004 | 依投保日期查詢 | HR | `{"enrollDate":"2025-01-01"}` | `enroll_date = '2025-01-01'`, `is_deleted = 0`, `insurance_type = 'LABOR'` |
| INS_L006 | 依投保級距查詢 | HR | `{"salaryGrade":"45800"}` | `salary_grade = '45800'`, `is_deleted = 0`, `insurance_type = 'LABOR'` |

#### 健保投保紀錄查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| INS_H001 | 查詢員工健保紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0`, `insurance_type = 'HEALTH'` |
| INS_H002 | 查詢有效健保 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0`, `insurance_type = 'HEALTH'` |
| INS_H003 | 查詢含眷屬健保 | HR | `{"hasDependents":true}` | `has_dependents = 1`, `is_deleted = 0`, `insurance_type = 'HEALTH'` |
| INS_H005 | 依投保單位查詢 | HR | `{"insuranceUnit":"U001"}` | `insurance_unit = 'U001'`, `is_deleted = 0`, `insurance_type = 'HEALTH'` |

#### 勞退提撥紀錄查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| INS_P001 | 查詢員工勞退紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0`, `insurance_type = 'PENSION'` |
| INS_P002 | 查詢月提撥紀錄 | HR | `{"yearMonth":"2025-01"}` | `year_month = '2025-01'`, `is_deleted = 0`, `insurance_type = 'PENSION'` |
| INS_P003 | 依提撥率查詢 | HR | `{"contributionRate":"6"}` | `contribution_rate = '6'`, `is_deleted = 0`, `insurance_type = 'PENSION'` |
| INS_P004 | 查詢自提勞退 | HR | `{"hasVoluntary":true}` | `voluntary_rate > 0`, `is_deleted = 0`, `insurance_type = 'PENSION'` |

#### 眷屬資料查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| INS_D001 | 查詢員工眷屬 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| INS_D002 | 依眷屬關係查詢 | HR | `{"relationship":"SPOUSE"}` | `relationship = 'SPOUSE'`, `is_deleted = 0` |
| INS_D003 | 查詢有效眷屬 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |

#### 職災紀錄查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| INS_W001 | 查詢員工職災紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| INS_W002 | 查詢處理中職災 | HR | `{"status":"PROCESSING"}` | `status = 'PROCESSING'`, `is_deleted = 0` |
| INS_W003 | 查詢已結案職災 | HR | `{"status":"CLOSED"}` | `status = 'CLOSED'`, `is_deleted = 0` |
| INS_W004 | 依發生日期查詢 | HR | `{"incidentDate":"2025-01-15"}` | `incident_date = '2025-01-15'`, `is_deleted = 0` |

> **注意：** INS_L005、INS_H004、INS_P005、INS_D004 為 ESS 模式（員工查詢自己），使用 `assertHasFilterForField` 驗證而非表格驗證。

---

### 2.1 勞保投保紀錄查詢

#### INS_L001: 查詢員工勞保紀錄

**API 端點：** `GET /api/v1/insurance/enrollments?employeeId=E001&insuranceType=LABOR`

**業務場景描述：**

HR 查詢特定員工的勞保投保紀錄。查詢結果需包含員工 ID 過濾、保險類型過濾與軟刪除過濾。

**測試合約：**

```json
{
  "scenarioId": "INS_L001",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "employeeId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "LABOR"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "enrollments",
    "requiredFields": [
      {"name": "enrollmentId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "insuranceType", "type": "string", "notNull": true},
      {"name": "enrollDate", "type": "date", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

---

#### INS_L002: 查詢有效勞保

**API 端點：** `GET /api/v1/insurance/enrollments?status=ACTIVE&insuranceType=LABOR`

**業務場景描述：**

HR 查詢所有有效的勞保投保紀錄。查詢結果需包含狀態過濾、保險類型過濾與軟刪除過濾。

**測試合約：**

```json
{
  "scenarioId": "INS_L002",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "status": "ACTIVE"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "ACTIVE"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "LABOR"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "assertions": [
      {"field": "status", "operator": "equals", "value": "ACTIVE"}
    ]
  }
}
```

---

#### INS_L003: 查詢退保紀錄

**API 端點：** `GET /api/v1/insurance/enrollments?status=TERMINATED&insuranceType=LABOR`

**業務場景描述：**

HR 查詢已退保的勞保紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_L003",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "status": "TERMINATED"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "TERMINATED"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "LABOR"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "assertions": [
      {"field": "status", "operator": "equals", "value": "TERMINATED"}
    ]
  }
}
```

---

#### INS_L004: 依投保日期查詢勞保

**API 端點：** `GET /api/v1/insurance/enrollments?enrollDate=2025-01-01&insuranceType=LABOR`

**業務場景描述：**

HR 依投保日期查詢勞保投保紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_L004",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "enrollDate": "2025-01-01"
  },
  "expectedQueryFilters": [
    {"field": "enroll_date", "operator": "=", "value": "2025-01-01"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "LABOR"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_L005: 員工查詢自己勞保

**API 端點：** `GET /api/v1/insurance/enrollments` (ESS 模式)

**業務場景描述：**

員工透過 ESS 查詢自己的勞保紀錄。系統需自動過濾當前使用者的資料。

**測試合約：**

```json
{
  "scenarioId": "INS_L005",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "currentUserId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "LABOR"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_L006: 依投保級距查詢勞保

**API 端點：** `GET /api/v1/insurance/enrollments?salaryGrade=45800&insuranceType=LABOR`

**業務場景描述：**

HR 依投保級距查詢勞保投保紀錄，用於級距調整的檢視。

**測試合約：**

```json
{
  "scenarioId": "INS_L006",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "salaryGrade": "45800"
  },
  "expectedQueryFilters": [
    {"field": "salary_grade", "operator": "=", "value": "45800"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "LABOR"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

### 2.2 健保投保紀錄查詢

#### INS_H001: 查詢員工健保紀錄

**API 端點：** `GET /api/v1/insurance/enrollments?employeeId=E001&insuranceType=HEALTH`

**業務場景描述：**

HR 查詢特定員工的健保投保紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_H001",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "employeeId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "HEALTH"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_H002: 查詢有效健保

**API 端點：** `GET /api/v1/insurance/enrollments?status=ACTIVE&insuranceType=HEALTH`

**業務場景描述：**

HR 查詢所有有效的健保投保紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_H002",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "status": "ACTIVE"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "ACTIVE"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "HEALTH"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "assertions": [
      {"field": "status", "operator": "equals", "value": "ACTIVE"}
    ]
  }
}
```

---

#### INS_H003: 查詢含眷屬的健保

**API 端點：** `GET /api/v1/insurance/enrollments?hasDependents=true&insuranceType=HEALTH`

**業務場景描述：**

HR 查詢含有眷屬投保的健保紀錄。系統將布林值轉換為整數 (true → 1) 進行過濾。

**測試合約：**

```json
{
  "scenarioId": "INS_H003",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "hasDependents": true
  },
  "expectedQueryFilters": [
    {"field": "has_dependents", "operator": "=", "value": 1},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "HEALTH"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_H004: 員工查詢自己健保

**API 端點：** `GET /api/v1/insurance/enrollments` (ESS 模式)

**業務場景描述：**

員工透過 ESS 查詢自己的健保紀錄。系統需自動過濾當前使用者的資料。

**測試合約：**

```json
{
  "scenarioId": "INS_H004",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "currentUserId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "HEALTH"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_H005: 依投保單位查詢健保

**API 端點：** `GET /api/v1/insurance/enrollments?insuranceUnit=U001&insuranceType=HEALTH`

**業務場景描述：**

HR 依投保單位查詢健保投保紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_H005",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "insuranceUnit": "U001"
  },
  "expectedQueryFilters": [
    {"field": "insurance_unit", "operator": "=", "value": "U001"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "HEALTH"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

### 2.3 勞退提撥紀錄查詢

#### INS_P001: 查詢員工勞退紀錄

**API 端點：** `GET /api/v1/insurance/enrollments?employeeId=E001&insuranceType=PENSION`

**業務場景描述：**

HR 查詢特定員工的勞退提撥紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_P001",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "employeeId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "PENSION"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_P002: 查詢月提撥紀錄

**API 端點：** `GET /api/v1/insurance/enrollments?yearMonth=2025-01&insuranceType=PENSION`

**業務場景描述：**

HR 查詢特定年月的勞退提撥紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_P002",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "yearMonth": "2025-01"
  },
  "expectedQueryFilters": [
    {"field": "year_month", "operator": "=", "value": "2025-01"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "PENSION"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_P003: 依提撥率查詢勞退

**API 端點：** `GET /api/v1/insurance/enrollments?contributionRate=6&insuranceType=PENSION`

**業務場景描述：**

HR 依提撥率查詢勞退提撥紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_P003",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "contributionRate": "6"
  },
  "expectedQueryFilters": [
    {"field": "contribution_rate", "operator": "=", "value": "6"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "PENSION"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_P004: 查詢自提勞退

**API 端點：** `GET /api/v1/insurance/enrollments?hasVoluntary=true&insuranceType=PENSION`

**業務場景描述：**

HR 查詢有自提勞退的員工紀錄。系統將 hasVoluntary=true 轉換為 voluntary_rate > 0 進行過濾。

**測試合約：**

```json
{
  "scenarioId": "INS_P004",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "hasVoluntary": true
  },
  "expectedQueryFilters": [
    {"field": "voluntary_rate", "operator": ">", "value": 0},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "PENSION"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_P005: 員工查詢自己勞退

**API 端點：** `GET /api/v1/insurance/enrollments` (ESS 模式)

**業務場景描述：**

員工透過 ESS 查詢自己的勞退提撥紀錄。系統需自動過濾當前使用者的資料。

**測試合約：**

```json
{
  "scenarioId": "INS_P005",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "currentUserId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0},
    {"field": "insurance_type", "operator": "=", "value": "PENSION"}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

### 2.4 眷屬資料查詢

#### INS_D001: 查詢員工眷屬

**API 端點：** `GET /api/v1/insurance/enrollments?employeeId=E001` (眷屬查詢模式)

**業務場景描述：**

HR 查詢特定員工的眷屬投保資料。

**測試合約：**

```json
{
  "scenarioId": "INS_D001",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "employeeId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_D002: 依眷屬關係查詢

**API 端點：** `GET /api/v1/insurance/enrollments?relationship=SPOUSE`

**業務場景描述：**

HR 依眷屬關係（配偶、子女等）查詢眷屬投保資料。

**測試合約：**

```json
{
  "scenarioId": "INS_D002",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "relationship": "SPOUSE"
  },
  "expectedQueryFilters": [
    {"field": "relationship", "operator": "=", "value": "SPOUSE"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_D003: 查詢有效眷屬

**API 端點：** `GET /api/v1/insurance/enrollments?status=ACTIVE` (眷屬查詢模式)

**業務場景描述：**

HR 查詢所有有效的眷屬投保資料。

**測試合約：**

```json
{
  "scenarioId": "INS_D003",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "status": "ACTIVE"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "ACTIVE"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "assertions": [
      {"field": "status", "operator": "equals", "value": "ACTIVE"}
    ]
  }
}
```

---

#### INS_D004: 員工查詢自己眷屬

**API 端點：** `GET /api/v1/insurance/enrollments` (ESS 眷屬模式)

**業務場景描述：**

員工透過 ESS 查詢自己的眷屬投保資料。系統需自動過濾當前使用者的資料。

**測試合約：**

```json
{
  "scenarioId": "INS_D004",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "currentUserId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

### 2.5 職災紀錄查詢

#### INS_W001: 查詢員工職災紀錄

**API 端點：** `GET /api/v1/insurance/enrollments?employeeId=E001` (職災查詢模式)

**業務場景描述：**

HR 查詢特定員工的職災紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_W001",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "employeeId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

#### INS_W002: 查詢處理中職災

**API 端點：** `GET /api/v1/insurance/enrollments?status=PROCESSING`

**業務場景描述：**

HR 查詢正在處理中的職災紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_W002",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "status": "PROCESSING"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "PROCESSING"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "assertions": [
      {"field": "status", "operator": "equals", "value": "PROCESSING"}
    ]
  }
}
```

---

#### INS_W003: 查詢已結案職災

**API 端點：** `GET /api/v1/insurance/enrollments?status=CLOSED`

**業務場景描述：**

HR 查詢已結案的職災紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_W003",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "status": "CLOSED"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "CLOSED"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "assertions": [
      {"field": "status", "operator": "equals", "value": "CLOSED"}
    ]
  }
}
```

---

#### INS_W004: 依發生日期查詢職災

**API 端點：** `GET /api/v1/insurance/enrollments?incidentDate=2025-01-15`

**業務場景描述：**

HR 依職災發生日期查詢紀錄。

**測試合約：**

```json
{
  "scenarioId": "INS_W004",
  "apiEndpoint": "GET /api/v1/insurance/enrollments",
  "controller": "HR05EnrollmentQryController",
  "service": "getEnrollmentListServiceImpl",
  "permission": "insurance:enrollment:read",
  "request": {
    "incidentDate": "2025-01-15"
  },
  "expectedQueryFilters": [
    {"field": "incident_date", "operator": "=", "value": "2025-01-15"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

## 3. 測試資料規格

### 3.1 投保單位 (insurance_units) - 2 筆

| ID | 單位代碼 | 單位名稱 | 勞保編號 | 健保編號 | 啟用 |
|:---|:---|:---|:---|:---|:---|
| unit-001 | INS-UNIT-001 | ABC科技股份有限公司 | 12345678 | H12345678 | true |
| unit-002 | INS-UNIT-002 | XYZ子公司 | 87654321 | H87654321 | true |

### 3.2 加保記錄 (insurance_enrollments) - 9 筆

| ID | 員工 | 投保單位 | 保險類型 | 投保薪資 | 狀態 | 加保日期 | 退保日期 |
|:---|:---|:---|:---|:---|:---|:---|:---|
| enroll-001 | emp-001 | unit-001 | LABOR | 48200 | ACTIVE | 2025-01-01 | NULL |
| enroll-002 | emp-001 | unit-001 | HEALTH | 48200 | ACTIVE | 2025-01-01 | NULL |
| enroll-003 | emp-001 | unit-001 | PENSION | 48200 | ACTIVE | 2025-01-01 | NULL |
| enroll-004 | emp-002 | unit-001 | LABOR | 36300 | ACTIVE | 2025-02-01 | NULL |
| enroll-005 | emp-002 | unit-001 | HEALTH | 36300 | ACTIVE | 2025-02-01 | NULL |
| enroll-006 | emp-002 | unit-001 | PENSION | 36300 | ACTIVE | 2025-02-01 | NULL |
| enroll-007 | emp-003 | unit-001 | LABOR | 27470 | WITHDRAWN | 2024-06-01 | 2024-12-31 |
| enroll-008 | emp-003 | unit-001 | HEALTH | 27470 | WITHDRAWN | 2024-06-01 | 2024-12-31 |
| enroll-009 | emp-003 | unit-001 | PENSION | 27470 | WITHDRAWN | 2024-06-01 | 2024-12-31 |

### 3.3 投保級距表 (insurance_levels) - 部分

| 級距號 | 保險類型 | 投保薪資 | 薪資下限 | 薪資上限 |
|:---|:---|:---|:---|:---|
| 1 | LABOR | 27,470 | 0 | 27,469 |
| 10 | LABOR | 36,300 | 35,100 | 36,299 |
| 15 | LABOR | 48,200 | 47,900 | 50,599 |
| 17 | LABOR | 53,000 | 50,600 | 55,399 |

### 3.4 補充保費記錄 (supplementary_premiums) - 1 筆

| ID | 員工 | 所得類型 | 所得金額 | 投保薪資 | 門檻 | 保費基準 | 保費金額 |
|:---|:---|:---|:---|:---|:---|:---|:---|
| sp-001 | emp-001 | BONUS | 250,000 | 48,200 | 192,800 | 57,200 | 1,207 |

---

## 4. 2025 年保險費率參考

| 項目 | 費率 | 個人負擔 | 雇主負擔 | 政府負擔 |
|:---|:---:|:---:|:---:|:---:|
| 勞保 | 11.5% | 20% | 70% | 10% |
| 健保 | 5.17% | 30% | 60% | 10% |
| 勞退 | 6% | - | 100% | - |
| 補充保費 | 2.11% | 100% | - | - |

---

**文件完成日期:** 2026-02-20
**版本:** 1.0
