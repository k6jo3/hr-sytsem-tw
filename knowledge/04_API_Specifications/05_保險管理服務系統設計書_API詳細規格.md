# HR05 保險管理服務 API 詳細規格

**版本:** 1.0
**建立日期:** 2025-12-29
**Domain 代號:** 05 (INS)
**API 總數:** 12 個端點

---

## 目錄

1. [API 總覽](#1-api-總覽)
2. [投保單位管理 API](#2-投保單位管理-api)
3. [加退保管理 API](#3-加退保管理-api)
4. [費用計算 API](#4-費用計算-api)
5. [投保級距查詢 API](#5-投保級距查詢-api)
6. [申報檔案 API](#6-申報檔案-api)
7. [員工自助查詢 API](#7-員工自助查詢-api)
8. [共用定義](#8-共用定義)
9. [領域事件](#9-領域事件)

---

## 1. API 總覽

### 1.1 Controller 對照表

| Controller | 說明 | API 數量 |
|:---|:---|:---:|
| `HR05UnitCmdController` | 投保單位 Command 操作 | 2 |
| `HR05UnitQryController` | 投保單位 Query 操作 | 1 |
| `HR05EnrollmentCmdController` | 加退保 Command 操作 | 3 |
| `HR05EnrollmentQryController` | 加退保 Query 操作 | 1 |
| `HR05FeeCmdController` | 費用計算 Command 操作 | 2 |
| `HR05LevelQryController` | 投保級距 Query 操作 | 1 |
| `HR05ExportCmdController` | 申報檔案匯出 | 1 |
| `HR05MyInsuranceQryController` | 員工自助查詢 (ESS) | 1 |

### 1.2 API 端點清單

| # | 端點 | 方法 | 說明 | Controller |
|:---:|:---|:---:|:---|:---|
| 1 | `/api/v1/insurance/units` | POST | 建立投保單位 | HR05UnitCmdController |
| 2 | `/api/v1/insurance/units/{id}` | PUT | 更新投保單位 | HR05UnitCmdController |
| 3 | `/api/v1/insurance/units` | GET | 查詢投保單位列表 | HR05UnitQryController |
| 4 | `/api/v1/insurance/enrollments` | POST | 手動加保 | HR05EnrollmentCmdController |
| 5 | `/api/v1/insurance/enrollments/{id}/withdraw` | PUT | 退保 | HR05EnrollmentCmdController |
| 6 | `/api/v1/insurance/enrollments/{id}/adjust-level` | PUT | 調整投保級距 | HR05EnrollmentCmdController |
| 7 | `/api/v1/insurance/enrollments` | GET | 查詢加退保記錄 | HR05EnrollmentQryController |
| 8 | `/api/v1/insurance/fees/calculate` | POST | 計算保費 | HR05FeeCmdController |
| 9 | `/api/v1/insurance/supplementary-premium/calculate` | POST | 計算補充保費 | HR05FeeCmdController |
| 10 | `/api/v1/insurance/levels` | GET | 查詢投保級距表 | HR05LevelQryController |
| 11 | `/api/v1/insurance/export/enrollment-report` | POST | 匯出加退保申報檔 | HR05ExportCmdController |
| 12 | `/api/v1/insurance/my` | GET | 查詢我的保險資訊 | HR05MyInsuranceQryController |

### 1.3 2025 年保險費率

| 項目 | 費率 | 個人負擔 | 雇主負擔 | 政府負擔 |
|:---|:---:|:---:|:---:|:---:|
| 勞保 | 11.5% | 20% | 70% | 10% |
| 健保 | 5.17% | 30% | 60% | 10% |
| 勞退 | 6% | - | 100% | - |
| 補充保費 | 2.11% | 100% | - | - |

---

## 2. 投保單位管理 API

### 2.1 建立投保單位

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `POST /api/v1/insurance/units` |
| Controller | `HR05UnitCmdController` |
| Service | `CreateInsuranceUnitServiceImpl` |
| 權限 | `insurance:unit:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 母子公司分別投保時，建立不同投保單位 |
| 使用者 | HR 管理員 |
| 前置條件 | 系統管理員已建立組織 |

**業務邏輯**

| 驗證規則 | 說明 |
|:---|:---|
| 單位代碼唯一 | 同組織內不可重複 |
| 勞保局代碼格式 | 需符合勞保局規範 (8-10 碼) |
| 健保局代碼格式 | 需符合健保局規範 |

**Request Body**

```json
{
  "organizationId": "org-001",
  "unitCode": "INS-UNIT-001",
  "unitName": "ABC科技股份有限公司",
  "laborInsuranceNumber": "12345678",
  "healthInsuranceNumber": "H12345678",
  "pensionNumber": "P12345678"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| organizationId | string | ✅ | UUID 格式 | 所屬組織 ID |
| unitCode | string | ✅ | 最長 50 字元，唯一 | 投保單位代碼 |
| unitName | string | ✅ | 最長 255 字元 | 投保單位名稱 |
| laborInsuranceNumber | string | ❌ | 勞保局格式 | 勞保投保單位編號 |
| healthInsuranceNumber | string | ❌ | 健保局格式 | 健保投保單位編號 |
| pensionNumber | string | ❌ | 勞退局格式 | 勞退提繳單位編號 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "unitId": "unit-001",
    "unitCode": "INS-UNIT-001",
    "unitName": "ABC科技股份有限公司",
    "createdAt": "2025-01-01T09:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `INS_UNIT_CODE_DUPLICATE` | 單位代碼已存在 | 檢查代碼是否重複 |
| 400 | `INS_INVALID_LABOR_NUMBER` | 勞保編號格式錯誤 | 確認格式符合規範 |
| 404 | `ORG_NOT_FOUND` | 組織不存在 | 確認組織 ID 正確 |

---

### 2.2 更新投保單位

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/insurance/units/{id}` |
| Controller | `HR05UnitCmdController` |
| Service | `UpdateInsuranceUnitServiceImpl` |
| 權限 | `insurance:unit:manage` |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 投保單位 ID |

**Request Body**

```json
{
  "unitName": "ABC科技股份有限公司 (更新)",
  "laborInsuranceNumber": "12345678",
  "healthInsuranceNumber": "H12345678",
  "pensionNumber": "P12345678",
  "isActive": true
}
```

**Response Body**

```json
{
  "success": true,
  "data": {
    "unitId": "unit-001",
    "unitCode": "INS-UNIT-001",
    "unitName": "ABC科技股份有限公司 (更新)",
    "updatedAt": "2025-01-15T10:00:00Z"
  }
}
```

---

### 2.3 查詢投保單位列表

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/insurance/units` |
| Controller | `HR05UnitQryController` |
| Service | `GetInsuranceUnitListServiceImpl` |
| 權限 | `insurance:unit:read` |

**Query Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| organizationId | string | ❌ | 按組織篩選 |
| isActive | boolean | ❌ | 按啟用狀態篩選 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "units": [
      {
        "unitId": "unit-001",
        "unitCode": "INS-UNIT-001",
        "unitName": "ABC科技股份有限公司",
        "laborInsuranceNumber": "12345678",
        "healthInsuranceNumber": "H12345678",
        "pensionNumber": "P12345678",
        "isActive": true,
        "employeeCount": 150
      }
    ],
    "total": 1
  }
}
```

---

## 3. 加退保管理 API

### 3.1 手動加保

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `POST /api/v1/insurance/enrollments` |
| Controller | `HR05EnrollmentCmdController` |
| Service | `CreateEnrollmentServiceImpl` |
| 權限 | `insurance:enrollment:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 手動為員工辦理勞健保加保 |
| 使用者 | HR 專員 |
| 前置條件 | 員工已建立、投保單位已設定 |
| 觸發事件 | `InsuranceEnrollmentCompleted` |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證員工是否已有相同保險類型的有效加保 |
| 2 | 根據薪資自動對應投保級距 |
| 3 | 建立加保記錄 (勞保、健保、勞退可分別加保) |
| 4 | 計算保費並記錄 |
| 5 | 發布 `InsuranceEnrollmentCompleted` 事件通知 Payroll |

**Request Body**

```json
{
  "employeeId": "emp-001",
  "insuranceUnitId": "unit-001",
  "insuranceTypes": ["LABOR", "HEALTH", "PENSION"],
  "enrollDate": "2025-01-01",
  "monthlySalary": 50000
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| employeeId | string | ✅ | UUID 格式 | 員工 ID |
| insuranceUnitId | string | ✅ | UUID 格式 | 投保單位 ID |
| insuranceTypes | array | ✅ | `LABOR`, `HEALTH`, `PENSION` | 加保類型 |
| enrollDate | string | ✅ | YYYY-MM-DD | 加保日期 |
| monthlySalary | number | ✅ | > 0 | 月薪 (用於對應級距) |

**Response Body**

```json
{
  "success": true,
  "data": {
    "enrollments": [
      {
        "enrollmentId": "enroll-001",
        "insuranceType": "LABOR",
        "enrollDate": "2025-01-01",
        "monthlySalary": 48200,
        "levelNumber": 15,
        "status": "ACTIVE"
      },
      {
        "enrollmentId": "enroll-002",
        "insuranceType": "HEALTH",
        "enrollDate": "2025-01-01",
        "monthlySalary": 48200,
        "levelNumber": 15,
        "status": "ACTIVE"
      },
      {
        "enrollmentId": "enroll-003",
        "insuranceType": "PENSION",
        "enrollDate": "2025-01-01",
        "monthlySalary": 48200,
        "status": "ACTIVE"
      }
    ],
    "fees": {
      "laborEmployee": 1109,
      "laborEmployer": 3881,
      "healthEmployee": 747,
      "healthEmployer": 1494,
      "pensionEmployer": 2892,
      "totalEmployee": 1856,
      "totalEmployer": 8267
    }
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `INS_ALREADY_ENROLLED` | 該保險類型已加保 | 確認員工投保狀態 |
| 400 | `INS_INVALID_ENROLL_DATE` | 加保日期不合法 | 加保日期需為未來或當月 |
| 404 | `EMP_NOT_FOUND` | 員工不存在 | 確認員工 ID 正確 |
| 404 | `INS_UNIT_NOT_FOUND` | 投保單位不存在 | 確認投保單位 ID |

**領域事件**

```json
{
  "eventType": "InsuranceEnrollmentCompleted",
  "topic": "insurance.enrollment.completed",
  "payload": {
    "employeeId": "emp-001",
    "enrollments": [
      {"type": "LABOR", "monthlySalary": 48200, "enrollDate": "2025-01-01"},
      {"type": "HEALTH", "monthlySalary": 48200, "enrollDate": "2025-01-01"},
      {"type": "PENSION", "monthlySalary": 48200, "enrollDate": "2025-01-01"}
    ],
    "fees": {
      "laborEmployee": 1109,
      "laborEmployer": 3881,
      "healthEmployee": 747,
      "healthEmployer": 1494,
      "pensionEmployer": 2892
    }
  }
}
```

---

### 3.2 退保

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/insurance/enrollments/{id}/withdraw` |
| Controller | `HR05EnrollmentCmdController` |
| Service | `WithdrawEnrollmentServiceImpl` |
| 權限 | `insurance:enrollment:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工離職時辦理勞健保退保 |
| 使用者 | HR 專員 |
| 前置條件 | 員工已加保且狀態為 ACTIVE |
| 觸發事件 | `InsuranceWithdrawalCompleted` |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證加保記錄存在且狀態為 ACTIVE |
| 2 | 設定退保日期 |
| 3 | 更新狀態為 WITHDRAWN |
| 4 | 發布 `InsuranceWithdrawalCompleted` 事件 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 加保記錄 ID |

**Request Body**

```json
{
  "withdrawDate": "2025-12-31",
  "reason": "RESIGNATION"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| withdrawDate | string | ✅ | YYYY-MM-DD | 退保日期 |
| reason | string | ❌ | 枚舉值 | 退保原因 |

**退保原因枚舉**

| 值 | 說明 |
|:---|:---|
| `RESIGNATION` | 離職 |
| `TRANSFER` | 調職 (轉投保單位) |
| `RETIREMENT` | 退休 |
| `OTHER` | 其他 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "enrollmentId": "enroll-001",
    "insuranceType": "LABOR",
    "withdrawDate": "2025-12-31",
    "status": "WITHDRAWN"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `INS_NOT_ACTIVE` | 該加保記錄非有效狀態 | 確認加保狀態 |
| 400 | `INS_INVALID_WITHDRAW_DATE` | 退保日期不合法 | 退保日期需晚於加保日期 |
| 404 | `INS_ENROLLMENT_NOT_FOUND` | 加保記錄不存在 | 確認加保記錄 ID |

---

### 3.3 調整投保級距

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `PUT /api/v1/insurance/enrollments/{id}/adjust-level` |
| Controller | `HR05EnrollmentCmdController` |
| Service | `AdjustEnrollmentLevelServiceImpl` |
| 權限 | `insurance:enrollment:manage` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工調薪後調整投保級距 |
| 使用者 | HR 專員 |
| 前置條件 | 員工已加保且狀態為 ACTIVE |
| 觸發事件 | `InsuranceLevelAdjusted` |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 根據新薪資計算適當投保級距 |
| 2 | 驗證級距是否變更 (避免無意義調整) |
| 3 | 更新投保薪資 |
| 4 | 發布 `InsuranceLevelAdjusted` 事件通知 Payroll 及員工 |

**Path Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| id | string | ✅ | 加保記錄 ID |

**Request Body**

```json
{
  "newMonthlySalary": 55000,
  "effectiveDate": "2025-07-01",
  "reason": "年度調薪"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| newMonthlySalary | number | ✅ | > 0 | 新月薪 |
| effectiveDate | string | ✅ | YYYY-MM-DD | 生效日期 |
| reason | string | ❌ | 最長 255 字元 | 調整原因 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "enrollmentId": "enroll-001",
    "previousLevel": {
      "levelNumber": 15,
      "monthlySalary": 48200
    },
    "newLevel": {
      "levelNumber": 17,
      "monthlySalary": 53000
    },
    "effectiveDate": "2025-07-01",
    "newFees": {
      "laborEmployee": 1219,
      "laborEmployer": 4266,
      "healthEmployee": 822,
      "healthEmployer": 1644,
      "pensionEmployer": 3180
    }
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `INS_LEVEL_NO_CHANGE` | 新級距與舊級距相同 | 確認薪資是否跨級距 |
| 400 | `INS_INVALID_EFFECTIVE_DATE` | 生效日期不合法 | 需為未來日期 |
| 404 | `INS_ENROLLMENT_NOT_FOUND` | 加保記錄不存在 | 確認加保記錄 ID |

---

### 3.4 查詢加退保記錄

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/insurance/enrollments` |
| Controller | `HR05EnrollmentQryController` |
| Service | `GetEnrollmentListServiceImpl` |
| 權限 | `insurance:enrollment:read` |

**Query Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| employeeId | string | ❌ | 按員工篩選 |
| insuranceUnitId | string | ❌ | 按投保單位篩選 |
| insuranceType | string | ❌ | 按保險類型篩選 |
| status | string | ❌ | 按狀態篩選 |
| enrollDateFrom | string | ❌ | 加保日期起 (YYYY-MM-DD) |
| enrollDateTo | string | ❌ | 加保日期迄 (YYYY-MM-DD) |
| page | number | ❌ | 頁碼 (預設 1) |
| pageSize | number | ❌ | 每頁筆數 (預設 20) |

**Response Body**

```json
{
  "success": true,
  "data": {
    "enrollments": [
      {
        "enrollmentId": "enroll-001",
        "employeeId": "emp-001",
        "employeeNo": "E0001",
        "employeeName": "張三",
        "insuranceUnitId": "unit-001",
        "insuranceUnitName": "ABC科技股份有限公司",
        "insuranceType": "LABOR",
        "enrollDate": "2025-01-01",
        "withdrawDate": null,
        "monthlySalary": 48200,
        "levelNumber": 15,
        "status": "ACTIVE",
        "isReported": true,
        "reportedAt": "2025-01-05T10:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 150
    }
  }
}
```

---

## 4. 費用計算 API

### 4.1 計算保費

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `POST /api/v1/insurance/fees/calculate` |
| Controller | `HR05FeeCmdController` |
| Service | `CalculateInsuranceFeeServiceImpl` |
| 權限 | `insurance:calculate` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 薪資計算時調用，取得員工保費資訊；或 HR 試算保費 |
| 使用者 | Payroll Service (內部調用)、HR 專員 |
| 前置條件 | 員工已加保或提供薪資試算 |

**業務邏輯 (費用計算公式)**

| 項目 | 計算公式 |
|:---|:---|
| 勞保費 (員工) | 投保薪資 × 11.5% × 20% |
| 勞保費 (雇主) | 投保薪資 × 11.5% × 70% |
| 健保費 (員工) | 投保薪資 × 5.17% × 30% |
| 健保費 (雇主) | 投保薪資 × 5.17% × 60% |
| 勞退 (雇主) | 投保薪資 × 6% |

**Request Body**

```json
{
  "employeeId": "emp-001",
  "yearMonth": "2025-01"
}
```

或試算模式：

```json
{
  "monthlySalary": 50000,
  "trial": true
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| employeeId | string | ❌ | UUID 格式 | 員工 ID (與 trial 二擇一) |
| yearMonth | string | ❌ | YYYY-MM | 計算年月 |
| monthlySalary | number | ❌ | > 0 | 試算用月薪 |
| trial | boolean | ❌ | - | 是否為試算模式 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "employeeId": "emp-001",
    "yearMonth": "2025-01",
    "insuranceLevel": {
      "levelNumber": 15,
      "monthlySalary": 48200
    },
    "fees": {
      "labor": {
        "rate": 0.115,
        "employeeAmount": 1109,
        "employerAmount": 3881,
        "governmentAmount": 554
      },
      "health": {
        "rate": 0.0517,
        "employeeAmount": 747,
        "employerAmount": 1494,
        "governmentAmount": 249
      },
      "pension": {
        "rate": 0.06,
        "employerAmount": 2892
      }
    },
    "summary": {
      "totalEmployeeDeduction": 1856,
      "totalEmployerContribution": 8267
    }
  }
}
```

---

### 4.2 計算補充保費

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `POST /api/v1/insurance/supplementary-premium/calculate` |
| Controller | `HR05FeeCmdController` |
| Service | `CalculateSupplementaryPremiumServiceImpl` |
| 權限 | `insurance:calculate` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 發放獎金、兼職所得時，計算二代健保補充保費 |
| 使用者 | Payroll Service (內部調用) |
| 前置條件 | 員工已加保、有投保薪資 |
| 觸發事件 | `SupplementaryPremiumCalculated` |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 取得員工投保薪資 |
| 2 | 計算門檻 = 投保薪資 × 4 |
| 3 | 判斷所得是否超過門檻 |
| 4 | 計費基準 = 所得金額 - 門檻 (上限 1000 萬) |
| 5 | 補充保費 = 計費基準 × 2.11% |

**Request Body**

```json
{
  "employeeId": "emp-001",
  "incomeType": "BONUS",
  "incomeAmount": 250000,
  "incomeDate": "2025-01-31"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| employeeId | string | ✅ | UUID 格式 | 員工 ID |
| incomeType | string | ✅ | 枚舉值 | 所得類型 |
| incomeAmount | number | ✅ | > 0 | 所得金額 |
| incomeDate | string | ✅ | YYYY-MM-DD | 所得日期 |

**所得類型枚舉**

| 值 | 說明 |
|:---|:---|
| `BONUS` | 獎金 |
| `PART_TIME_INCOME` | 兼職所得 |
| `PROFESSIONAL_FEE` | 執行業務收入 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "employeeId": "emp-001",
    "insuredSalary": 48200,
    "threshold": 192800,
    "incomeAmount": 250000,
    "premiumRequired": true,
    "premiumBase": 57200,
    "premiumAmount": 1207,
    "premiumRate": 0.0211
  }
}
```

若不需繳納：

```json
{
  "success": true,
  "data": {
    "employeeId": "emp-001",
    "insuredSalary": 48200,
    "threshold": 192800,
    "incomeAmount": 100000,
    "premiumRequired": false,
    "premiumAmount": 0
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `INS_NO_ACTIVE_ENROLLMENT` | 員工無有效健保加保 | 確認員工投保狀態 |

---

## 5. 投保級距查詢 API

### 5.1 查詢投保級距表

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/insurance/levels` |
| Controller | `HR05LevelQryController` |
| Service | `GetInsuranceLevelListServiceImpl` |
| 權限 | `insurance:level:read` |

**Query Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---:|:---:|:---|
| insuranceType | string | ❌ | 保險類型 (`LABOR`, `HEALTH`) |
| effectiveDate | string | ❌ | 生效日期 (預設今日) |
| salaryFrom | number | ❌ | 薪資範圍起 |
| salaryTo | number | ❌ | 薪資範圍迄 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "levels": [
      {
        "levelId": "level-001",
        "insuranceType": "LABOR",
        "levelNumber": 1,
        "monthlySalary": 27470,
        "salaryRangeMin": 0,
        "salaryRangeMax": 27469,
        "rates": {
          "employeeRate": 0.023,
          "employerRate": 0.0805,
          "governmentRate": 0.0115
        },
        "effectiveDate": "2025-01-01"
      },
      {
        "levelId": "level-015",
        "insuranceType": "LABOR",
        "levelNumber": 15,
        "monthlySalary": 48200,
        "salaryRangeMin": 47900,
        "salaryRangeMax": 50599,
        "rates": {
          "employeeRate": 0.023,
          "employerRate": 0.0805,
          "governmentRate": 0.0115
        },
        "effectiveDate": "2025-01-01"
      }
    ],
    "total": 18
  }
}
```

---

## 6. 申報檔案 API

### 6.1 匯出加退保申報檔

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `POST /api/v1/insurance/export/enrollment-report` |
| Controller | `HR05ExportCmdController` |
| Service | `ExportEnrollmentReportServiceImpl` |
| 權限 | `insurance:report:export` |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 產生勞保局/健保局規範格式的加退保申報檔 |
| 使用者 | HR 專員 |
| 前置條件 | 有待申報的加退保記錄 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 查詢指定期間未申報的加退保記錄 |
| 2 | 依勞保局/健保局格式產生申報檔 |
| 3 | 標記記錄為已申報 |
| 4 | 回傳下載連結 |

**Request Body**

```json
{
  "insuranceUnitId": "unit-001",
  "reportType": "LABOR_ENROLLMENT",
  "dateFrom": "2025-01-01",
  "dateTo": "2025-01-31"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---:|:---:|:---|:---|
| insuranceUnitId | string | ✅ | UUID 格式 | 投保單位 ID |
| reportType | string | ✅ | 枚舉值 | 申報類型 |
| dateFrom | string | ✅ | YYYY-MM-DD | 日期範圍起 |
| dateTo | string | ✅ | YYYY-MM-DD | 日期範圍迄 |

**申報類型枚舉**

| 值 | 說明 |
|:---|:---|
| `LABOR_ENROLLMENT` | 勞保加保申報 |
| `LABOR_WITHDRAWAL` | 勞保退保申報 |
| `HEALTH_ENROLLMENT` | 健保加保申報 |
| `HEALTH_WITHDRAWAL` | 健保退保申報 |
| `PENSION_ENROLLMENT` | 勞退提繳申報 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "reportId": "report-001",
    "reportType": "LABOR_ENROLLMENT",
    "recordCount": 15,
    "downloadUrl": "/api/v1/insurance/export/download/report-001",
    "expiresAt": "2025-01-15T23:59:59Z",
    "generatedAt": "2025-01-15T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `INS_NO_PENDING_RECORDS` | 無待申報記錄 | 確認日期範圍 |
| 404 | `INS_UNIT_NOT_FOUND` | 投保單位不存在 | 確認投保單位 ID |

---

## 7. 員工自助查詢 API

### 7.1 查詢我的保險資訊 (ESS)

**基本資訊**

| 項目 | 說明 |
|:---|:---|
| 端點 | `GET /api/v1/insurance/my` |
| Controller | `HR05MyInsuranceQryController` |
| Service | `GetMyInsuranceServiceImpl` |
| 權限 | 登入使用者 (自動取得當前員工) |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工查詢自己的保險投保狀態及費用 |
| 使用者 | 員工 (ESS 自助服務) |
| 頁面 | HR05-P07 我的保險資訊頁面 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "employeeId": "emp-001",
    "employeeName": "張三",
    "insuranceUnit": {
      "unitId": "unit-001",
      "unitName": "ABC科技股份有限公司"
    },
    "currentStatus": {
      "labor": {
        "enrolled": true,
        "enrollDate": "2025-01-01",
        "monthlySalary": 48200,
        "levelNumber": 15
      },
      "health": {
        "enrolled": true,
        "enrollDate": "2025-01-01",
        "monthlySalary": 48200,
        "levelNumber": 15
      },
      "pension": {
        "enrolled": true,
        "enrollDate": "2025-01-01",
        "monthlySalary": 48200,
        "selfContributionRate": 0
      }
    },
    "monthlyFees": {
      "laborEmployee": 1109,
      "healthEmployee": 747,
      "totalEmployee": 1856,
      "laborEmployer": 3881,
      "healthEmployer": 1494,
      "pensionEmployer": 2892,
      "totalEmployer": 8267
    },
    "history": [
      {
        "changeDate": "2025-01-01",
        "changeType": "ENROLLMENT",
        "previousSalary": null,
        "newSalary": 48200,
        "reason": "到職加保"
      }
    ]
  }
}
```

---

## 8. 共用定義

### 8.1 保險類型枚舉 (InsuranceType)

| 值 | 說明 | 適用 |
|:---|:---|:---|
| `LABOR` | 勞工保險 | 勞保局 |
| `HEALTH` | 全民健康保險 | 健保署 |
| `PENSION` | 勞工退休金 | 勞退局 |
| `GROUP_LIFE` | 團體壽險 | 依合約 |
| `GROUP_ACCIDENT` | 團體傷害險 | 依合約 |
| `GROUP_MEDICAL` | 團體醫療險 | 依合約 |

### 8.2 加保狀態枚舉 (EnrollmentStatus)

| 值 | 說明 | 允許操作 |
|:---|:---|:---|
| `PENDING` | 待生效 | 取消 |
| `ACTIVE` | 已加保 | 退保、調整級距 |
| `WITHDRAWN` | 已退保 | 無 |

### 8.3 所得類型枚舉 (IncomeType)

| 值 | 說明 | 需計算補充保費 |
|:---|:---|:---:|
| `BONUS` | 獎金 | ✅ |
| `PART_TIME_INCOME` | 兼職所得 | ✅ |
| `PROFESSIONAL_FEE` | 執行業務收入 | ✅ |

### 8.4 通用錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | `INVALID_REQUEST` | 請求格式錯誤 |
| 401 | `UNAUTHORIZED` | 未授權存取 |
| 403 | `FORBIDDEN` | 無權限執行操作 |
| 404 | `NOT_FOUND` | 資源不存在 |
| 500 | `INTERNAL_ERROR` | 系統內部錯誤 |

### 8.5 資料庫表結構

#### insurance_units (投保單位表)

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| unit_id | UUID | 主鍵 |
| organization_id | UUID | 所屬組織 |
| unit_code | VARCHAR(50) | 單位代碼 (唯一) |
| unit_name | VARCHAR(255) | 單位名稱 |
| labor_insurance_number | VARCHAR(50) | 勞保局編號 |
| health_insurance_number | VARCHAR(50) | 健保署編號 |
| pension_number | VARCHAR(50) | 勞退局編號 |
| is_active | BOOLEAN | 是否啟用 |

#### insurance_enrollments (加退保記錄表)

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| enrollment_id | UUID | 主鍵 |
| employee_id | UUID | 員工 ID |
| insurance_unit_id | UUID | 投保單位 ID |
| insurance_type | VARCHAR(20) | 保險類型 |
| enroll_date | DATE | 加保日期 |
| withdraw_date | DATE | 退保日期 |
| monthly_salary | DECIMAL(10,2) | 投保薪資 |
| status | VARCHAR(20) | 加保狀態 |
| is_reported | BOOLEAN | 是否已申報 |

#### supplementary_premiums (補充保費記錄表)

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| premium_id | UUID | 主鍵 |
| employee_id | UUID | 員工 ID |
| income_type | VARCHAR(30) | 所得類型 |
| income_date | DATE | 所得日期 |
| income_amount | DECIMAL(12,2) | 所得金額 |
| insured_salary | DECIMAL(10,2) | 投保薪資 |
| threshold | DECIMAL(12,2) | 門檻金額 |
| premium_base | DECIMAL(12,2) | 計費基準 |
| premium_amount | DECIMAL(10,2) | 補充保費 |

---

## 9. 領域事件

### 9.1 事件總覽

| 事件名稱 | Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| `InsuranceEnrollmentCompleted` | `insurance.enrollment.completed` | 加保完成 | Payroll |
| `InsuranceWithdrawalCompleted` | `insurance.withdrawal.completed` | 退保完成 | Payroll |
| `InsuranceLevelAdjusted` | `insurance.level.adjusted` | 級距調整 | Payroll, Notification |
| `SupplementaryPremiumCalculated` | `insurance.supplementary.calculated` | 補充保費計算 | Payroll |

### 9.2 事件訂閱 (本服務)

| 事件名稱 | 來源服務 | 處理邏輯 |
|:---|:---|:---|
| `EmployeeCreated` | Organization | 自動建立勞健保勞退加保 |
| `EmployeeTerminated` | Organization | 自動辦理退保 |
| `SalaryChanged` | Payroll | 檢查是否需調整投保級距 |

### 9.3 InsuranceEnrollmentCompleted 事件

```json
{
  "eventId": "evt-ins-001",
  "eventType": "InsuranceEnrollmentCompleted",
  "timestamp": "2025-01-01T09:00:00Z",
  "source": "insurance-service",
  "payload": {
    "employeeId": "emp-001",
    "insuranceUnitId": "unit-001",
    "enrollments": [
      {
        "enrollmentId": "enroll-001",
        "insuranceType": "LABOR",
        "monthlySalary": 48200,
        "enrollDate": "2025-01-01"
      },
      {
        "enrollmentId": "enroll-002",
        "insuranceType": "HEALTH",
        "monthlySalary": 48200,
        "enrollDate": "2025-01-01"
      },
      {
        "enrollmentId": "enroll-003",
        "insuranceType": "PENSION",
        "monthlySalary": 48200,
        "enrollDate": "2025-01-01"
      }
    ],
    "fees": {
      "laborEmployee": 1109,
      "laborEmployer": 3881,
      "healthEmployee": 747,
      "healthEmployer": 1494,
      "pensionEmployer": 2892
    }
  }
}
```

### 9.4 InsuranceLevelAdjusted 事件

```json
{
  "eventId": "evt-ins-002",
  "eventType": "InsuranceLevelAdjusted",
  "timestamp": "2025-07-01T00:00:00Z",
  "source": "insurance-service",
  "payload": {
    "employeeId": "emp-001",
    "insuranceType": "LABOR",
    "previousLevel": {
      "levelNumber": 15,
      "monthlySalary": 48200
    },
    "newLevel": {
      "levelNumber": 17,
      "monthlySalary": 53000
    },
    "effectiveDate": "2025-07-01",
    "reason": "年度調薪",
    "newFees": {
      "laborEmployee": 1219,
      "laborEmployer": 4266
    }
  }
}
```

### 9.5 SupplementaryPremiumCalculated 事件

```json
{
  "eventId": "evt-ins-003",
  "eventType": "SupplementaryPremiumCalculated",
  "timestamp": "2025-01-31T10:00:00Z",
  "source": "insurance-service",
  "payload": {
    "employeeId": "emp-001",
    "incomeType": "BONUS",
    "incomeDate": "2025-01-31",
    "incomeAmount": 250000,
    "insuredSalary": 48200,
    "threshold": 192800,
    "premiumBase": 57200,
    "premiumAmount": 1207,
    "premiumRate": 0.0211
  }
}
```

---

**文件完成日期:** 2025-12-29
**版本:** 1.0
