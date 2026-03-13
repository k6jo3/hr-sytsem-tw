# HR02 組織員工服務業務合約

> **服務代碼:** HR02
> **服務名稱:** 組織員工服務 (Organization & Employee Management)
> **版本:** 1.0
> **更新日期:** 2026-02-12

---

## 概述

組織員工服務是 HR 系統的核心主數據服務，負責組織架構管理、部門管理、員工生命週期管理（到職、調動、升遷、離職、試用期轉正）等功能。

---

## API 端點概覽

### 員工管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/employees?status=ACTIVE` | GET | ORG_QRY_E001 | 查詢在職員工 | ✅ 已實作 |
| 2 | `GET /api/v1/employees` | GET | ORG_QRY_E002 | 查詢所有員工（含離職）| ✅ 已實作 |
| 3 | `GET /api/v1/employees?status=PROBATION` | GET | ORG_QRY_E003 | 查詢試用期員工 | ✅ 已實作 |
| 4 | `GET /api/v1/employees?search=張` | GET | ORG_QRY_E004 | 關鍵字搜尋員工 | ✅ 已實作 |
| 5 | `GET /api/v1/employees?departmentId=...` | GET | ORG_QRY_E005 | 依部門查詢員工 | ✅ 已實作 |
| 6 | `GET /api/v1/employees?name=王` | GET | ORG_QRY_E006 | 依姓名模糊查詢 | ✅ 已實作 |
| 7 | `GET /api/v1/employees?employeeNumber=EMP001` | GET | ORG_QRY_E007 | 依工號查詢 | ✅ 已實作 |
| 8 | `GET /api/v1/employees` | GET | ORG_QRY_E008 | 主管查詢下屬 | ✅ 已實作 |
| 9 | `GET /api/v1/employees` | GET | ORG_QRY_E009 | 員工查詢同部門 | ✅ 已實作 |
| 10 | `GET /api/v1/employees?hireDateFrom=...&hireDateTo=...` | GET | ORG_QRY_E010 | 依到職日期範圍查詢 | ✅ 已實作 |
| 11 | `POST /api/v1/employees` | POST | ORG_CMD_E001 | 建立員工（到職）| ✅ 已實作 |
| 12 | `PUT /api/v1/employees/{id}` | PUT | ORG_CMD_E002 | 更新員工 | ✅ 已實作 |
| 13 | `POST /api/v1/employees/{id}/transfer` | POST | ORG_CMD_E003 | 部門調動 | ✅ 已實作 |
| 14 | `POST /api/v1/employees/{id}/promote` | POST | ORG_CMD_E004 | 員工升遷 | ✅ 已實作 |
| 15 | `POST /api/v1/employees/{id}/terminate` | POST | ORG_CMD_E005 | 員工離職 | ✅ 已實作 |
| 16 | `POST /api/v1/employees/{id}/regularize` | POST | ORG_CMD_E006 | 試用期轉正 | ✅ 已實作 |

### 部門管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/departments?status=ACTIVE` | GET | ORG_QRY_D001 | 查詢啟用部門 | ✅ 已實作 |
| 2 | `GET /api/v1/departments?parentId=null` | GET | ORG_QRY_D002 | 查詢頂層部門 | ✅ 已實作 |
| 3 | `GET /api/v1/departments/{id}/sub-departments` | GET | ORG_QRY_D003 | 查詢子部門 | ✅ 已實作 |
| 4 | `POST /api/v1/departments` | POST | ORG_CMD_D001 | 建立部門 | ✅ 已實作 |
| 5 | `PUT /api/v1/departments/{id}` | PUT | ORG_CMD_D002 | 更新部門 | ✅ 已實作 |
| 6 | `PUT /api/v1/departments/{id}/deactivate` | PUT | ORG_CMD_D003 | 停用部門 | ✅ 已實作 |
| 7 | `PUT /api/v1/departments/{id}/assign-manager` | PUT | ORG_CMD_D004 | 指派部門主管 | ✅ 已實作 |

### 組織管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/organizations` | POST | ORG_CMD_O001 | 建立組織 | ✅ 已實作 |
| 2 | `GET /api/v1/organizations` | GET | ORG_QRY_O001 | 查詢組織列表 | ✅ 已實作 |
| 3 | `GET /api/v1/organizations/{id}/tree` | GET | ORG_QRY_O002 | 查詢組織樹 | ✅ 已實作 |

**總計：27 個場景（15 個 Query + 12 個 Command）**

---

## 1. Command 操作業務合約

### 1.1 員工管理

#### ORG_CMD_E001: 建立員工（到職）

**API 端點：** `POST /api/v1/employees`

**業務場景描述：**

HR 人員為新到職員工建立主數據。員工建立後會觸發事件通知 IAM（建立帳號）、Insurance（加保）、Payroll（建立薪資結構）等服務。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_E001",
  "apiEndpoint": "POST /api/v1/employees",
  "controller": "HR02EmployeeCmdController",
  "service": "CreateEmployeeServiceImpl",
  "permission": "employee:create",
  "request": {
    "employeeNumber": "EMP202603-001",
    "firstName": "新人",
    "lastName": "測",
    "fullName": "測新人",
    "nationalId": "A111222333",
    "dateOfBirth": "1995-06-15",
    "gender": "MALE",
    "companyEmail": "test.new@company.com",
    "mobilePhone": "0911222333",
    "organizationId": "11111111-1111-1111-1111-111111111111",
    "departmentId": "d0000001-0001-0001-0001-000000000001",
    "employmentType": "FULL_TIME",
    "jobTitle": "軟體工程師",
    "hireDate": "2026-03-01"
  },
  "businessRules": [
    {"rule": "employeeNumber 在組織內必須唯一"},
    {"rule": "nationalId 在系統內必須唯一"},
    {"rule": "companyEmail 在系統內必須唯一"},
    {"rule": "organizationId 必須存在且為 ACTIVE"},
    {"rule": "departmentId 必須存在且為 ACTIVE"},
    {"rule": "新員工預設 employment_status = PROBATION"},
    {"rule": "hireDate 不可為過去日期（可為今天）"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "employees",
      "count": 1,
      "assertions": [
        {"field": "employee_id", "operator": "notNull"},
        {"field": "employee_number", "operator": "equals", "value": "EMP202603-001"},
        {"field": "full_name", "operator": "equals", "value": "測新人"},
        {"field": "company_email", "operator": "equals", "value": "test.new@company.com"},
        {"field": "organization_id", "operator": "equals", "value": "11111111-1111-1111-1111-111111111111"},
        {"field": "department_id", "operator": "equals", "value": "d0000001-0001-0001-0001-000000000001"},
        {"field": "employment_status", "operator": "equals", "value": "PROBATION"},
        {"field": "employment_type", "operator": "equals", "value": "FULL_TIME"},
        {"field": "is_deleted", "operator": "equals", "value": false}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "EmployeeCreatedEvent",
      "payload": [
        {"field": "employeeId", "operator": "notNull"},
        {"field": "employeeNumber", "operator": "equals", "value": "EMP202603-001"},
        {"field": "fullName", "operator": "equals", "value": "測新人"},
        {"field": "companyEmail", "operator": "equals", "value": "test.new@company.com"}
      ]
    }
  ]
}
```

---

#### ORG_CMD_E002: 更新員工

**API 端點：** `PUT /api/v1/employees/{id}`

**業務場景描述：**

HR 人員更新員工的基本資料（聯絡方式、地址等）。若 Email 變更會觸發 EmployeeEmailChangedEvent 通知 IAM 服務同步更新帳號。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_E002",
  "apiEndpoint": "PUT /api/v1/employees/{id}",
  "controller": "HR02EmployeeCmdController",
  "service": "UpdateEmployeeServiceImpl",
  "permission": "employee:write",
  "request": {
    "companyEmail": "updated.email@company.com",
    "mobilePhone": "0999888777"
  },
  "businessRules": [
    {"rule": "員工必須存在且未刪除"},
    {"rule": "companyEmail 若變更則必須唯一"},
    {"rule": "Email 變更時觸發 EmployeeEmailChangedEvent"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "employees",
      "count": 1,
      "assertions": [
        {"field": "company_email", "operator": "equals", "value": "updated.email@company.com"},
        {"field": "mobile_phone", "operator": "equals", "value": "0999888777"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "EmployeeEmailChangedEvent",
      "payload": [
        {"field": "employeeId", "operator": "notNull"},
        {"field": "newEmail", "operator": "equals", "value": "updated.email@company.com"}
      ]
    }
  ]
}
```

---

#### ORG_CMD_E003: 部門調動

**API 端點：** `POST /api/v1/employees/{id}/transfer`

**業務場景描述：**

HR 人員執行員工部門調動。調動記錄會寫入人事歷程，並通知 Attendance、Payroll 等服務。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_E003",
  "apiEndpoint": "POST /api/v1/employees/{id}/transfer",
  "controller": "HR02EmployeeCmdController",
  "service": "TransferEmployeeServiceImpl",
  "permission": "employee:transfer",
  "request": {
    "newDepartmentId": "d0000002-0002-0002-0002-000000000002",
    "effectiveDate": "2026-04-01",
    "reason": "組織調整"
  },
  "businessRules": [
    {"rule": "員工必須存在且為在職狀態（ACTIVE 或 PROBATION）"},
    {"rule": "newDepartmentId 必須存在且為 ACTIVE"},
    {"rule": "新部門不可與現有部門相同"},
    {"rule": "記錄人事歷程（employee_history）"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "employees",
      "count": 1,
      "assertions": [
        {"field": "department_id", "operator": "equals", "value": "d0000002-0002-0002-0002-000000000002"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    },
    {
      "action": "INSERT",
      "table": "employee_history",
      "count": 1,
      "assertions": [
        {"field": "employee_id", "operator": "notNull"},
        {"field": "event_type", "operator": "equals", "value": "DEPARTMENT_TRANSFER"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "EmployeeDepartmentChangedEvent",
      "payload": [
        {"field": "employeeId", "operator": "notNull"},
        {"field": "oldDepartmentId", "operator": "notNull"},
        {"field": "newDepartmentId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### ORG_CMD_E004: 員工升遷

**API 端點：** `POST /api/v1/employees/{id}/promote`

**業務場景描述：**

HR 人員執行員工升遷，更新職稱和職級。升遷記錄寫入人事歷程，通知 Payroll、Performance 等服務。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_E004",
  "apiEndpoint": "POST /api/v1/employees/{id}/promote",
  "controller": "HR02EmployeeCmdController",
  "service": "PromoteEmployeeServiceImpl",
  "permission": "employee:promote",
  "request": {
    "newJobTitle": "資深軟體工程師",
    "newJobLevel": "SENIOR",
    "effectiveDate": "2026-04-01",
    "reason": "年度晉升"
  },
  "businessRules": [
    {"rule": "員工必須存在且為在職狀態（ACTIVE）"},
    {"rule": "記錄人事歷程（employee_history）"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "employees",
      "count": 1,
      "assertions": [
        {"field": "job_title", "operator": "equals", "value": "資深軟體工程師"},
        {"field": "job_level", "operator": "equals", "value": "SENIOR"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    },
    {
      "action": "INSERT",
      "table": "employee_history",
      "count": 1,
      "assertions": [
        {"field": "employee_id", "operator": "notNull"},
        {"field": "event_type", "operator": "equals", "value": "PROMOTION"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "EmployeePromotedEvent",
      "payload": [
        {"field": "employeeId", "operator": "notNull"},
        {"field": "newJobTitle", "operator": "equals", "value": "資深軟體工程師"},
        {"field": "newJobLevel", "operator": "equals", "value": "SENIOR"}
      ]
    }
  ]
}
```

---

#### ORG_CMD_E005: 員工離職

**API 端點：** `POST /api/v1/employees/{id}/terminate`

**業務場景描述：**

HR 人員辦理員工離職。離職後 IAM 帳號停用、保險退保、薪資結算。此為系統關鍵事件，會通知多個服務。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_E005",
  "apiEndpoint": "POST /api/v1/employees/{id}/terminate",
  "controller": "HR02EmployeeCmdController",
  "service": "TerminateEmployeeServiceImpl",
  "permission": "employee:terminate",
  "request": {
    "terminationDate": "2026-03-31",
    "terminationReason": "個人生涯規劃"
  },
  "businessRules": [
    {"rule": "員工必須存在且非 TERMINATED 狀態"},
    {"rule": "terminationDate 不可早於 hireDate"},
    {"rule": "設定 employment_status = TERMINATED"},
    {"rule": "記錄人事歷程（employee_history）"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "employees",
      "count": 1,
      "assertions": [
        {"field": "employment_status", "operator": "equals", "value": "TERMINATED"},
        {"field": "termination_date", "operator": "notNull"},
        {"field": "termination_reason", "operator": "equals", "value": "個人生涯規劃"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    },
    {
      "action": "INSERT",
      "table": "employee_history",
      "count": 1,
      "assertions": [
        {"field": "employee_id", "operator": "notNull"},
        {"field": "event_type", "operator": "equals", "value": "TERMINATION"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "EmployeeTerminatedEvent",
      "payload": [
        {"field": "employeeId", "operator": "notNull"},
        {"field": "terminationDate", "operator": "notNull"},
        {"field": "terminationReason", "operator": "equals", "value": "個人生涯規劃"}
      ]
    }
  ]
}
```

---

#### ORG_CMD_E006: 試用期轉正

**API 端點：** `POST /api/v1/employees/{id}/regularize`

**業務場景描述：**

HR 人員將試用期員工轉為正式員工。轉正後通知 Payroll 調整薪資結構。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_E006",
  "apiEndpoint": "POST /api/v1/employees/{id}/regularize",
  "controller": "HR02EmployeeCmdController",
  "service": "RegularizeEmployeeServiceImpl",
  "permission": "employee:regularize",
  "request": {},
  "businessRules": [
    {"rule": "員工必須存在"},
    {"rule": "員工狀態必須為 PROBATION"},
    {"rule": "設定 employment_status = ACTIVE"},
    {"rule": "記錄人事歷程（employee_history）"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "employees",
      "count": 1,
      "assertions": [
        {"field": "employment_status", "operator": "equals", "value": "ACTIVE"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    },
    {
      "action": "INSERT",
      "table": "employee_history",
      "count": 1,
      "assertions": [
        {"field": "employee_id", "operator": "notNull"},
        {"field": "event_type", "operator": "equals", "value": "PROBATION_PASSED"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "EmployeeProbationPassedEvent",
      "payload": [
        {"field": "employeeId", "operator": "notNull"},
        {"field": "effectiveDate", "operator": "notNull"}
      ]
    }
  ]
}
```

---

### 1.2 部門管理

#### ORG_CMD_D001: 建立部門

**API 端點：** `POST /api/v1/departments`

**業務場景描述：**

HR 人員建立新部門。部門有層級限制（最多 5 層），部門代碼在組織內唯一。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_D001",
  "apiEndpoint": "POST /api/v1/departments",
  "controller": "HR02DepartmentCmdController",
  "service": "CreateDepartmentServiceImpl",
  "permission": "department:create",
  "request": {
    "code": "MKT",
    "name": "行銷部",
    "organizationId": "11111111-1111-1111-1111-111111111111",
    "parentDepartmentId": null,
    "description": "負責品牌行銷與市場推廣"
  },
  "businessRules": [
    {"rule": "code 在同一組織內必須唯一"},
    {"rule": "organizationId 必須存在且為 ACTIVE"},
    {"rule": "parentDepartmentId 若指定則必須存在"},
    {"rule": "層級不可超過 5 層"},
    {"rule": "新部門預設 status = ACTIVE"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "departments",
      "count": 1,
      "assertions": [
        {"field": "department_id", "operator": "notNull"},
        {"field": "department_code", "operator": "equals", "value": "MKT"},
        {"field": "department_name", "operator": "equals", "value": "行銷部"},
        {"field": "organization_id", "operator": "equals", "value": "11111111-1111-1111-1111-111111111111"},
        {"field": "status", "operator": "equals", "value": "ACTIVE"},
        {"field": "level", "operator": "equals", "value": 1}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "DepartmentCreatedEvent",
      "payload": [
        {"field": "departmentId", "operator": "notNull"},
        {"field": "departmentCode", "operator": "equals", "value": "MKT"},
        {"field": "departmentName", "operator": "equals", "value": "行銷部"}
      ]
    }
  ]
}
```

---

#### ORG_CMD_D002: 更新部門

**API 端點：** `PUT /api/v1/departments/{id}`

**業務場景描述：**

HR 人員更新部門的基本資訊（名稱、描述）。部門代碼建立後不可修改。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_D002",
  "apiEndpoint": "PUT /api/v1/departments/{id}",
  "controller": "HR02DepartmentCmdController",
  "service": "UpdateDepartmentServiceImpl",
  "permission": "department:write",
  "request": {
    "name": "研發部（更新）",
    "description": "研發部門-更新說明"
  },
  "businessRules": [
    {"rule": "部門必須存在"},
    {"rule": "department_code 不可修改"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "departments",
      "count": 1,
      "assertions": [
        {"field": "department_name", "operator": "equals", "value": "研發部（更新）"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### ORG_CMD_D003: 停用部門

**API 端點：** `PUT /api/v1/departments/{id}/deactivate`

**業務場景描述：**

HR 人員停用部門。停用前須確認部門下無在職員工且無啟用的子部門。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_D003",
  "apiEndpoint": "PUT /api/v1/departments/{id}/deactivate",
  "controller": "HR02DepartmentCmdController",
  "service": "DeactivateDepartmentServiceImpl",
  "permission": "department:deactivate",
  "request": {},
  "businessRules": [
    {"rule": "部門必須存在且狀態為 ACTIVE"},
    {"rule": "部門下不可有在職員工"},
    {"rule": "部門下不可有啟用的子部門"},
    {"rule": "設定 status = INACTIVE"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "departments",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "INACTIVE"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### ORG_CMD_D004: 指派部門主管

**API 端點：** `PUT /api/v1/departments/{id}/assign-manager`

**業務場景描述：**

HR 人員指派或更換部門主管。主管異動會通知 Attendance 服務更新簽核流程。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_D004",
  "apiEndpoint": "PUT /api/v1/departments/{id}/assign-manager",
  "controller": "HR02DepartmentCmdController",
  "service": "AssignManagerServiceImpl",
  "permission": "department:assign-manager",
  "request": {
    "managerId": "e0000002-0002-0002-0002-000000000002"
  },
  "businessRules": [
    {"rule": "部門必須存在"},
    {"rule": "managerId 必須存在且為在職員工"},
    {"rule": "新主管不可與現有主管相同"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "departments",
      "count": 1,
      "assertions": [
        {"field": "manager_id", "operator": "equals", "value": "e0000002-0002-0002-0002-000000000002"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "DepartmentManagerChangedEvent",
      "payload": [
        {"field": "departmentId", "operator": "notNull"},
        {"field": "newManagerId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

### 1.3 組織管理

#### ORG_CMD_O001: 建立組織

**API 端點：** `POST /api/v1/organizations`

**業務場景描述：**

系統管理員建立新的組織（母公司或子公司）。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_O001",
  "apiEndpoint": "POST /api/v1/organizations",
  "controller": "HR02OrganizationCmdController",
  "service": "CreateOrganizationServiceImpl",
  "permission": "organization:create",
  "request": {
    "code": "BRANCH_B",
    "name": "B分公司",
    "type": "SUBSIDIARY",
    "parentId": "11111111-1111-1111-1111-111111111111",
    "taxId": "87654321",
    "phone": "02-87654321",
    "address": "台北市大安區復興南路一段1號",
    "establishedDate": "2022-01-01"
  },
  "frontendAdapterMapping": {
    "code → organizationCode": "前端簡短欄位 → 後端完整欄位",
    "name → organizationName": "前端簡短欄位 → 後端完整欄位",
    "type → organizationType": "前端簡短欄位 → 後端完整欄位",
    "parentId → parentOrganizationId": "前端簡短欄位 → 後端完整欄位",
    "phone → phoneNumber": "前端簡短欄位 → 後端完整欄位"
  },
  "businessRules": [
    {"rule": "code（organizationCode）必須唯一（CheckOrgCodeExistenceTask 驗證，重複時拋出 ResourceAlreadyExistsException → 409 Conflict）"},
    {"rule": "name（organizationName）允許重複，不做唯一性檢查"},
    {"rule": "type（organizationType）須為 PARENT 或 SUBSIDIARY"},
    {"rule": "若 type=SUBSIDIARY，parentId（parentOrganizationId）為必填且必須存在"},
    {"rule": "新組織預設 status = ACTIVE"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "organizations",
      "count": 1,
      "assertions": [
        {"field": "organization_id", "operator": "notNull"},
        {"field": "organization_code", "operator": "equals", "value": "BRANCH_B"},
        {"field": "organization_name", "operator": "equals", "value": "B分公司"},
        {"field": "organization_type", "operator": "equals", "value": "SUBSIDIARY"},
        {"field": "parent_organization_id", "operator": "equals", "value": "11111111-1111-1111-1111-111111111111"},
        {"field": "status", "operator": "equals", "value": "ACTIVE"},
        {"field": "is_deleted", "operator": "equals", "value": false}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### ORG_CMD_O001_ERR: 建立組織 — 組織代碼重複

**API 端點：** `POST /api/v1/organizations`

**業務場景描述：**

系統管理員嘗試建立已存在代碼的組織，系統應回傳 409 Conflict。

**測試合約：**

```json
{
  "scenarioId": "ORG_CMD_O001_ERR",
  "apiEndpoint": "POST /api/v1/organizations",
  "controller": "HR02OrganizationCmdController",
  "service": "CreateOrganizationServiceImpl",
  "permission": "organization:create",
  "precondition": {
    "existingOrganization": {
      "organization_code": "WU",
      "status": "ACTIVE"
    }
  },
  "request": {
    "code": "WU",
    "name": "重複代碼公司",
    "type": "PARENT"
  },
  "businessRules": [
    {"rule": "CheckOrgCodeExistenceTask 檢查代碼唯一性"},
    {"rule": "代碼已存在時拋出 ResourceAlreadyExistsException"}
  ],
  "expectedResponse": {
    "statusCode": 409,
    "errorCode": "RESOURCE_ORG_CODE_EXISTS",
    "message": "組織代碼已存在: WU"
  },
  "expectedDataChanges": [],
  "expectedEvents": []
}
```

---

## 2. Query 操作業務合約

### 2.1 員工查詢

#### ORG_QRY_E001: 查詢在職員工

**API 端點：** `GET /api/v1/employees?status=ACTIVE`

**業務場景描述：**

HR 人員查詢所有在職員工。系統自動過濾已刪除的記錄。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E001",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {
    "status": "ACTIVE"
  },
  "expectedQueryFilters": [
    {"field": "employment_status", "operator": "=", "value": "ACTIVE"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 1,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "employeeNumber", "type": "string", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true},
      {"name": "departmentName", "type": "string"},
      {"name": "jobTitle", "type": "string"},
      {"name": "employmentStatus", "type": "string"},
      {"name": "hireDate", "type": "date"}
    ],
    "pagination": {
      "required": true
    },
    "assertions": [
      {"field": "employmentStatus", "operator": "equals", "value": "ACTIVE"}
    ]
  }
}
```

---

#### ORG_QRY_E002: 查詢所有員工（含離職）

**API 端點：** `GET /api/v1/employees`

**業務場景描述：**

HR 人員查詢所有員工，包含在職、試用期、離職、留職停薪等各種狀態。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E002",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 1,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "employeeNumber", "type": "string", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true},
      {"name": "employmentStatus", "type": "string", "notNull": true}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

#### ORG_QRY_E003: 查詢試用期員工

**API 端點：** `GET /api/v1/employees?status=PROBATION`

**業務場景描述：**

HR 人員查詢目前在試用期的員工，用於追蹤轉正事宜。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E003",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {
    "status": "PROBATION"
  },
  "expectedQueryFilters": [
    {"field": "employment_status", "operator": "=", "value": "PROBATION"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 1,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "employeeNumber", "type": "string", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true},
      {"name": "employmentStatus", "type": "string"}
    ],
    "pagination": {
      "required": true
    },
    "assertions": [
      {"field": "employmentStatus", "operator": "equals", "value": "PROBATION"}
    ]
  }
}
```

---

#### ORG_QRY_E004: 關鍵字搜尋員工

**API 端點：** `GET /api/v1/employees?search=張`

**業務場景描述：**

HR 人員使用關鍵字搜尋員工，支援姓名、工號、Email 等多欄位模糊搜尋。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E004",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {
    "search": "張"
  },
  "expectedQueryFilters": [
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

#### ORG_QRY_E005: 依部門查詢員工

**API 端點：** `GET /api/v1/employees?departmentId=DEPT-001`

**業務場景描述：**

HR 人員查詢特定部門的員工。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E005",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {
    "departmentId": "DEPT-001"
  },
  "expectedQueryFilters": [
    {"field": "department_id", "operator": "=", "value": "DEPT-001"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true},
      {"name": "departmentName", "type": "string"}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

#### ORG_QRY_E006: 依姓名模糊查詢

**API 端點：** `GET /api/v1/employees?name=王`

**業務場景描述：**

HR 人員依姓名模糊查詢員工。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E006",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {
    "name": "王"
  },
  "expectedQueryFilters": [
    {"field": "full_name", "operator": "LIKE", "value": "%王%"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

#### ORG_QRY_E007: 依工號查詢

**API 端點：** `GET /api/v1/employees?employeeNumber=EMP001`

**業務場景描述：**

HR 人員依工號精確查詢員工。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E007",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {
    "employeeNumber": "EMP001"
  },
  "expectedQueryFilters": [
    {"field": "employee_number", "operator": "=", "value": "EMP001"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "employeeNumber", "type": "string", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

#### ORG_QRY_E008: 主管查詢下屬

**API 端點：** `GET /api/v1/employees`

**業務場景描述：**

主管查詢自己管理的部門下的員工。系統會自動依據主管的管轄部門過濾。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E008",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "department_id", "operator": "IN", "value": "{managedDeptIds}"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

#### ORG_QRY_E009: 員工查詢同部門

**API 端點：** `GET /api/v1/employees`

**業務場景描述：**

一般員工只能查詢自己所屬部門的同仁。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E009",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "department_id", "operator": "=", "value": "{currentUserDeptId}"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

#### ORG_QRY_E010: 依到職日期範圍查詢

**API 端點：** `GET /api/v1/employees?hireDateFrom=2025-01-01&hireDateTo=2025-12-31`

**業務場景描述：**

HR 人員依到職日期範圍查詢員工，常用於統計新進人員。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_E010",
  "apiEndpoint": "GET /api/v1/employees",
  "controller": "HR02EmployeeQryController",
  "service": "GetEmployeeListServiceImpl",
  "permission": "employee:read",
  "request": {
    "hireDateFrom": "2025-01-01",
    "hireDateTo": "2025-12-31"
  },
  "expectedQueryFilters": [
    {"field": "hire_date", "operator": ">=", "value": "2025-01-01"},
    {"field": "hire_date", "operator": "<=", "value": "2025-12-31"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "employeeId", "type": "uuid", "notNull": true},
      {"name": "fullName", "type": "string", "notNull": true},
      {"name": "hireDate", "type": "date", "notNull": true}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

### 2.2 部門查詢

#### ORG_QRY_D001: 查詢啟用部門

**API 端點：** `GET /api/v1/departments?status=ACTIVE`

**業務場景描述：**

HR 人員查詢所有啟用中的部門。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_D001",
  "apiEndpoint": "GET /api/v1/departments",
  "controller": "HR02DepartmentQryController",
  "service": "GetDepartmentListServiceImpl",
  "permission": "department:read",
  "request": {
    "status": "ACTIVE"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "ACTIVE"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "departmentId", "type": "uuid", "notNull": true},
      {"name": "code", "type": "string", "notNull": true},
      {"name": "name", "type": "string", "notNull": true},
      {"name": "level", "type": "integer", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ],
    "pagination": {
      "required": true
    },
    "assertions": [
      {"field": "status", "operator": "equals", "value": "ACTIVE"}
    ]
  }
}
```

---

#### ORG_QRY_D002: 查詢頂層部門

**API 端點：** `GET /api/v1/departments?parentId=null`

**業務場景描述：**

查詢沒有父部門的頂層部門，用於組織架構樹的根節點。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_D002",
  "apiEndpoint": "GET /api/v1/departments",
  "controller": "HR02DepartmentQryController",
  "service": "GetDepartmentListServiceImpl",
  "permission": "department:read",
  "request": {
    "parentId": "null"
  },
  "expectedQueryFilters": [
    {"field": "parent_department_id", "operator": "IS NULL"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "departmentId", "type": "uuid", "notNull": true},
      {"name": "code", "type": "string", "notNull": true},
      {"name": "name", "type": "string", "notNull": true},
      {"name": "level", "type": "integer", "notNull": true}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

#### ORG_QRY_D003: 查詢子部門

**API 端點：** `GET /api/v1/departments/{id}/sub-departments`

**業務場景描述：**

查詢指定部門的直接子部門。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_D003",
  "apiEndpoint": "GET /api/v1/departments/{id}/sub-departments",
  "controller": "HR02DepartmentQryController",
  "service": "GetSubDepartmentsServiceImpl",
  "permission": "department:read",
  "request": {
    "departmentId": "DEPT-001"
  },
  "expectedQueryFilters": [
    {"field": "parent_department_id", "operator": "=", "value": "DEPT-001"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "departmentId", "type": "uuid", "notNull": true},
      {"name": "code", "type": "string", "notNull": true},
      {"name": "name", "type": "string", "notNull": true},
      {"name": "level", "type": "integer", "notNull": true}
    ],
    "pagination": {
      "required": true
    }
  }
}
```

---

### 2.3 組織查詢

#### ORG_QRY_O001: 查詢組織列表

**API 端點：** `GET /api/v1/organizations`

**業務場景描述：**

查詢所有組織列表。後端回傳 `{ items: [...] }`，每筆紀錄使用簡短欄位名（`id/code/name/type/parentId/status`），前端 adapter 映射為完整語義欄位（`organizationId/organizationCode/organizationName/organizationType/parentOrganizationId/status`）。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_O001",
  "apiEndpoint": "GET /api/v1/organizations",
  "controller": "HR02OrganizationQryController",
  "service": "GetOrganizationListServiceImpl",
  "permission": "organization:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "id", "type": "uuid", "notNull": true},
      {"name": "code", "type": "string", "notNull": true},
      {"name": "name", "type": "string", "notNull": true},
      {"name": "type", "type": "string", "notNull": true},
      {"name": "parentId", "type": "uuid", "notNull": false},
      {"name": "status", "type": "string", "notNull": true}
    ]
  },
  "frontendAdapterMapping": {
    "id → organizationId": "後端欄位 → 前端 ViewModel 欄位",
    "code → organizationCode": "後端欄位 → 前端 ViewModel 欄位",
    "name → organizationName": "後端欄位 → 前端 ViewModel 欄位",
    "type → organizationType": "後端欄位 → 前端 ViewModel 欄位",
    "parentId → parentOrganizationId": "後端欄位 → 前端 ViewModel 欄位"
  }
}
```

---

#### ORG_QRY_O002: 查詢組織樹

**API 端點：** `GET /api/v1/organizations/{id}/tree`

**業務場景描述：**

查詢指定組織的架構樹。後端回傳扁平物件 `{ organizationId, code, name, type, status, departments: [...] }`，前端 adapter 重組為 `{ data: OrganizationDto, departments: DepartmentDto[] }`。

**測試合約：**

```json
{
  "scenarioId": "ORG_QRY_O002",
  "apiEndpoint": "GET /api/v1/organizations/{id}/tree",
  "controller": "HR02OrganizationQryController",
  "service": "GetOrganizationTreeServiceImpl",
  "permission": "organization:read",
  "request": {
    "organizationId": "11111111-1111-1111-1111-111111111111"
  },
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "organizationId", "type": "uuid", "notNull": true},
      {"name": "code", "type": "string", "notNull": true},
      {"name": "name", "type": "string", "notNull": true},
      {"name": "type", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "departments", "type": "array", "notNull": true}
    ]
  },
  "frontendAdapterRestructure": {
    "description": "前端 adapter 將扁平回應重組為結構化物件",
    "input": "{ organizationId, code, name, type, status, departments }",
    "output": "{ data: { organizationId, organizationCode, organizationName, organizationType, status }, departments: DepartmentDto[] }"
  }
}
```

---

## 附註

### 變數替換規則

測試執行時，合約中的變數會自動替換為實際值：

- `{managedDeptIds}` - 主管管理的部門 ID 列表
- `{currentUserDeptId}` - 當前登入使用者的部門 ID

### 資料異動操作類型

- `INSERT` - 新增記錄
- `UPDATE` - 更新記錄
- `DELETE` - 實體刪除記錄
- `SOFT_DELETE` - 軟刪除（設定 is_deleted = true）

### 測試資料庫表

**組織相關：**
- `organizations` - 組織主表
- `departments` - 部門主表

**員工相關：**
- `employees` - 員工主表
- `employee_history` - 員工人事歷程表
