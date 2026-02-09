# 保險管理服務業務合約 (Insurance Service Business Contract)

> **服務代碼:** HR05
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/05_保險管理服務系統設計書.md`
> - `knowledge/04_API_Specifications/05_保險管理服務系統設計書_API詳細規格.md`

---

## 📋 概述

本合約文件定義保險管理服務的**完整業務場景**，包括：
1. **Command 操作場景**（建立、更新、刪除）- 驗證業務規則與領域事件
2. **Query 操作場景**（查詢）- 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景
- ✅ 新增 4 個領域事件的詳細定義
- ✅ 對應到實際的 API 端點
- ✅ 使用實際存在的欄位（移除 is_deleted，使用 status）
- ✅ 包含完整的業務規則驗證

**服務定位：**
保險管理服務負責勞保、健保、勞退等社會保險的加退保管理、投保級距調整、保費計算等功能。本服務必須確保**符合台灣勞動法規**的所有規定。

**資料軟刪除策略：**
- **保險加退保紀錄**: 使用 `status` 欄位，'ACTIVE' 為有效加保，'WITHDRAWN' 為已退保
- **眷屬資料**: 使用 `status` 欄位，'ACTIVE' 為有效，'INACTIVE' 為已失效
- **歷史記錄**: 不進行軟刪除，保留所有歷史記錄（用於稽核與申報）

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [投保單位管理 Command](#11-投保單位管理-command)
   - 1.2 [加退保管理 Command](#12-加退保管理-command)
   - 1.3 [費用計算 Command](#13-費用計算-command)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [勞保投保紀錄查詢](#21-勞保投保紀錄查詢)
   - 2.2 [健保投保紀錄查詢](#22-健保投保紀錄查詢)
   - 2.3 [勞退提撥紀錄查詢](#23-勞退提撥紀錄查詢)
   - 2.4 [眷屬資料查詢](#24-眷屬資料查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 投保單位管理 Command

#### INS_CMD_001: 建立投保單位

**業務場景描述：**
母子公司分別投保時，HR 管理員建立不同的投保單位，設定勞保局、健保局、勞退局的投保編號。

**API 端點：**
```
POST /api/v1/insurance/units
```

**前置條件：**
- 執行者必須擁有 `insurance:unit:manage` 權限
- organizationId 必須存在

**輸入 (Request)：**
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

**業務規則驗證：**

1. ✅ **單位代碼唯一性檢查**
   - 查詢條件：`unit_code = ? AND organization_id = ?`
   - 預期結果：不存在重複

2. ✅ **勞保局代碼格式檢查**
   - 規則：8-10 碼數字
   - 錯誤訊息：`INS_INVALID_LABOR_NUMBER`

3. ✅ **健保局代碼格式檢查**
   - 規則：符合健保局規範格式
   - 錯誤訊息：`INS_INVALID_HEALTH_NUMBER`

4. ✅ **組織存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Organization 存在且為 ACTIVE 狀態

**必須發布的領域事件：**
```json
{
  "eventType": "InsuranceUnitCreatedEvent",
  "aggregateId": "unit-001",
  "timestamp": "2026-02-09T09:00:00Z",
  "payload": {
    "unitId": "unit-001",
    "unitCode": "INS-UNIT-001",
    "unitName": "ABC科技股份有限公司",
    "organizationId": "org-001",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "unitId": "unit-001",
    "unitCode": "INS-UNIT-001",
    "unitName": "ABC科技股份有限公司",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

---

#### INS_CMD_002: 更新投保單位

**業務場景描述：**
HR 管理員更新投保單位的資訊，例如變更勞保局或健保局的投保編號。

**API 端點：**
```
PUT /api/v1/insurance/units/{id}
```

**前置條件：**
- 執行者必須擁有 `insurance:unit:manage` 權限
- 投保單位必須存在

**輸入 (Request)：**
```json
{
  "unitName": "ABC科技股份有限公司 (更新)",
  "laborInsuranceNumber": "12345678",
  "healthInsuranceNumber": "H12345678",
  "pensionNumber": "P12345678",
  "isActive": true
}
```

**業務規則驗證：**

1. ✅ **投保單位存在性檢查**
   - 查詢條件：`unit_id = ?`
   - 預期結果：投保單位存在

2. ✅ **勞保局代碼格式檢查**
   - 規則：8-10 碼數字（如果有變更）

3. ✅ **健保局代碼格式檢查**
   - 規則：符合健保局規範格式（如果有變更）

**必須發布的領域事件：**
```json
{
  "eventType": "InsuranceUnitUpdatedEvent",
  "aggregateId": "unit-001",
  "timestamp": "2026-02-09T10:00:00Z",
  "payload": {
    "unitId": "unit-001",
    "unitName": "ABC科技股份有限公司 (更新)",
    "updatedAt": "2026-02-09T10:00:00Z"
  }
}
```

---

### 1.2 加退保管理 Command

#### INS_CMD_003: 手動加保（三保合一）

**業務場景描述：**
新員工到職時，HR 專員手動執行三保加保（勞保、健保、勞退），系統自動計算適當的投保級距。

**API 端點：**
```
POST /api/v1/insurance/enrollments
```

**前置條件：**
- 執行者必須擁有 `insurance:enrollment:manage` 權限
- employeeId 必須存在於 Organization Service
- 員工不可已有有效的加保記錄

**輸入 (Request)：**
```json
{
  "employeeId": "E001",
  "insuranceUnitId": "unit-001",
  "monthlySalary": 48200,
  "enrollDate": "2026-01-01",
  "insuranceTypes": ["LABOR", "HEALTH", "PENSION"],
  "dependentCount": 0
}
```

**業務規則驗證：**

1. ✅ **員工存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

2. ✅ **重複加保檢查**
   - 查詢條件：`employee_id = ? AND insurance_type = ? AND status = 'ACTIVE'`
   - 預期結果：不存在有效的加保記錄

3. ✅ **投保級距自動對應**
   - 規則：根據月薪查詢對應的投保級距
   - 查詢條件：`monthly_salary >= ? ORDER BY monthly_salary ASC LIMIT 1`
   - 勞保級距範圍：27,470 ~ 50,600（2025 年）
   - 健保級距範圍：27,470 ~ 219,500（2025 年）

4. ✅ **加保日期合理性檢查**
   - 規則：不可早於員工到職日
   - 不可為未來日期

5. ✅ **投保單位存在性檢查**
   - 查詢條件：`unit_id = ? AND is_active = true`
   - 預期結果：投保單位存在且為 ACTIVE

**必須發布的領域事件：**
```json
{
  "eventId": "evt-ins-enroll-001",
  "eventType": "InsuranceEnrollmentCompletedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "enrollment-001",
  "payload": {
    "employeeId": "E001",
    "employeeName": "王小華",
    "enrollDate": "2026-02-01",
    "enrollments": [
      {
        "enrollmentId": "enroll-labor-001",
        "type": "LABOR",
        "monthlySalary": 48200,
        "levelNumber": 15,
        "status": "ACTIVE"
      },
      {
        "enrollmentId": "enroll-health-001",
        "type": "HEALTH",
        "monthlySalary": 48200,
        "levelNumber": 15,
        "dependentCount": 0,
        "status": "ACTIVE"
      },
      {
        "enrollmentId": "enroll-pension-001",
        "type": "PENSION",
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

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "enrollments": [
      {
        "enrollmentId": "enroll-labor-001",
        "insuranceType": "LABOR",
        "enrollDate": "2026-02-01",
        "monthlySalary": 48200,
        "levelNumber": 15,
        "status": "ACTIVE"
      },
      {
        "enrollmentId": "enroll-health-001",
        "insuranceType": "HEALTH",
        "enrollDate": "2026-02-01",
        "monthlySalary": 48200,
        "levelNumber": 15,
        "status": "ACTIVE"
      },
      {
        "enrollmentId": "enroll-pension-001",
        "insuranceType": "PENSION",
        "enrollDate": "2026-02-01",
        "monthlySalary": 48200,
        "status": "ACTIVE"
      }
    ],
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

---

#### INS_CMD_004: 調整投保級距

**業務場景描述：**
員工薪資調整時，HR 專員調整員工的投保級距，系統重新計算保費並通知 Payroll Service。

**API 端點：**
```
PUT /api/v1/insurance/enrollments/{id}/adjust-level
```

**前置條件：**
- 執行者必須擁有 `insurance:enrollment:manage` 權限
- 加保記錄必須存在且狀態為 ACTIVE

**輸入 (Request)：**
```json
{
  "newSalaryGrade": 50600,
  "adjustReason": "年度調薪",
  "effectiveDate": "2026-02-01"
}
```

**業務規則驗證：**

1. ✅ **加保記錄狀態檢查**
   - 查詢條件：`enrollment_id = ? AND status = 'ACTIVE'`
   - 預期結果：加保記錄存在且為 ACTIVE

2. ✅ **級距有效性檢查**
   - 規則：新級距必須在勞保局或健保局公告的級距表內
   - 查詢條件：`insurance_type = ? AND monthly_salary = ? AND is_active = true`

3. ✅ **調整原因必填檢查**
   - 規則：adjustReason 不可為空

4. ✅ **生效日期合理性檢查**
   - 規則：不可早於加保日期
   - 不可為過去日期（允許當月）

**必須發布的領域事件：**
```json
{
  "eventId": "evt-ins-adjust-001",
  "eventType": "InsuranceLevelAdjustedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "enrollment-001",
  "payload": {
    "enrollmentId": "enrollment-001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "insuranceType": "LABOR",
    "oldSalaryGrade": 48200,
    "newSalaryGrade": 50600,
    "oldLevelNumber": 15,
    "newLevelNumber": 16,
    "adjustReason": "年度調薪",
    "effectiveDate": "2026-02-01",
    "newFees": {
      "laborEmployee": 1162,
      "laborEmployer": 4067,
      "healthEmployee": 783,
      "healthEmployer": 1566,
      "pensionEmployer": 3036
    }
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "enrollmentId": "enrollment-001",
    "oldSalaryGrade": 48200,
    "newSalaryGrade": 50600,
    "effectiveDate": "2026-02-01",
    "updatedAt": "2026-02-09T10:00:00Z"
  }
}
```

---

#### INS_CMD_005: 員工退保

**業務場景描述：**
員工離職時，HR 專員執行退保作業，系統更新加保記錄狀態並通知 Payroll Service 停止扣保費。

**API 端點：**
```
PUT /api/v1/insurance/enrollments/{id}/withdraw
```

**前置條件：**
- 執行者必須擁有 `insurance:enrollment:manage` 權限
- 加保記錄必須存在且狀態為 ACTIVE

**輸入 (Request)：**
```json
{
  "withdrawDate": "2026-12-31",
  "reason": "離職"
}
```

**業務規則驗證：**

1. ✅ **加保記錄狀態檢查**
   - 查詢條件：`enrollment_id = ? AND status = 'ACTIVE'`
   - 預期結果：加保記錄存在且為 ACTIVE

2. ✅ **退保日期合理性檢查**
   - 規則：不可早於加保日期
   - 不可為未來日期

3. ✅ **退保原因必填檢查**
   - 規則：reason 不可為空

**必須發布的領域事件：**
```json
{
  "eventId": "evt-ins-withdraw-001",
  "eventType": "InsuranceWithdrawalCompletedEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "enrollment-001",
  "payload": {
    "enrollmentId": "enrollment-001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "insuranceType": "LABOR",
    "enrollDate": "2026-01-01",
    "withdrawDate": "2026-12-31",
    "reason": "離職",
    "totalMonths": 12
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "enrollmentId": "enrollment-001",
    "status": "WITHDRAWN",
    "withdrawDate": "2026-12-31",
    "updatedAt": "2026-02-09T11:00:00Z"
  }
}
```

---

### 1.3 費用計算 Command

#### INS_CMD_006: 計算保費

**業務場景描述：**
Payroll Service 調用此 API 計算員工的保險費用（供薪資計算使用）。

**API 端點：**
```
POST /api/v1/insurance/fees/calculate
```

**前置條件：**
- 執行者必須擁有 `insurance:calculate` 權限或為系統服務
- employeeId 必須有有效的加保記錄

**輸入 (Request)：**
```json
{
  "employeeId": "E001",
  "yearMonth": "2026-02"
}
```

**業務規則驗證：**

1. ✅ **有效加保記錄檢查**
   - 查詢條件：`employee_id = ? AND status = 'ACTIVE' AND enroll_date <= ? AND (withdraw_date IS NULL OR withdraw_date >= ?)`
   - 預期結果：存在有效的加保記錄

2. ✅ **費率查詢**
   - 規則：根據生效日期查詢對應的費率
   - 查詢條件：`effective_date <= ? AND (end_date IS NULL OR end_date >= ?) ORDER BY effective_date DESC LIMIT 1`

3. ✅ **保費計算**
   - 勞保：`投保薪資 × 11.5% × 20%`（員工負擔）
   - 健保：`投保薪資 × 5.17% × 30% × (1 + 眷屬數)`（員工負擔）
   - 勞退：`投保薪資 × 6%`（雇主負擔）

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "employeeId": "E001",
    "yearMonth": "2026-02",
    "fees": {
      "labor": {
        "monthlySalary": 48200,
        "rate": 0.115,
        "employeeAmount": 1109,
        "employerAmount": 3881,
        "governmentAmount": 554
      },
      "health": {
        "monthlySalary": 48200,
        "rate": 0.0517,
        "dependentCount": 0,
        "employeeAmount": 747,
        "employerAmount": 1494,
        "governmentAmount": 249
      },
      "pension": {
        "monthlySalary": 48200,
        "rate": 0.06,
        "employerAmount": 2892,
        "employeeVoluntaryRate": 0,
        "employeeVoluntaryAmount": 0
      },
      "totalEmployeeAmount": 1856,
      "totalEmployerAmount": 8267
    }
  }
}
```

---

#### INS_CMD_007: 計算補充保費

**業務場景描述：**
員工獎金超過 4 個月投保薪資時，Payroll Service 調用此 API 計算補充保費（二代健保）。

**API 端點：**
```
POST /api/v1/insurance/supplementary-premium/calculate
```

**前置條件：**
- 執行者必須擁有 `insurance:calculate` 權限或為系統服務
- employeeId 必須有有效的健保加保記錄

**輸入 (Request)：**
```json
{
  "employeeId": "E001",
  "incomeType": "BONUS",
  "amount": 250000,
  "paymentDate": "2026-02-15"
}
```

**業務規則驗證：**

1. ✅ **有效健保記錄檢查**
   - 查詢條件：`employee_id = ? AND insurance_type = 'HEALTH' AND status = 'ACTIVE'`
   - 預期結果：存在有效的健保加保記錄

2. ✅ **門檻計算**
   - 規則：`投保薪資 × 4`
   - 例：48,200 × 4 = 192,800

3. ✅ **補充保費計算**
   - 規則：`(獎金 - 門檻) × 2.11%`（如果獎金 > 門檻）
   - 例：(250,000 - 192,800) × 2.11% = 1,207

**必須發布的領域事件：**
```json
{
  "eventId": "evt-ins-supp-001",
  "eventType": "SupplementaryPremiumCalculatedEvent",
  "timestamp": "2026-02-09T12:00:00Z",
  "aggregateId": "supp-premium-001",
  "payload": {
    "supplementaryPremiumId": "supp-premium-001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "incomeType": "BONUS",
    "totalAmount": 250000,
    "monthlySalary": 48200,
    "threshold": 192800,
    "chargeableAmount": 57200,
    "premiumRate": 0.0211,
    "premiumAmount": 1207,
    "paymentDate": "2026-02-15"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "supplementaryPremiumId": "supp-premium-001",
    "employeeId": "E001",
    "incomeType": "BONUS",
    "totalAmount": 250000,
    "threshold": 192800,
    "chargeableAmount": 57200,
    "premiumAmount": 1207,
    "needToCharge": true
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 勞保投保紀錄查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| INS_QRY_L001 | 查詢員工勞保紀錄 | HR | `GET /api/v1/insurance/enrollments` | `{"employeeId":"E001","insuranceType":"LABOR"}` | `employee_id = 'E001'`, `insurance_type = 'LABOR'` |
| INS_QRY_L002 | 查詢有效勞保 | HR | `GET /api/v1/insurance/enrollments` | `{"status":"ACTIVE","insuranceType":"LABOR"}` | `status = 'ACTIVE'`, `insurance_type = 'LABOR'` |
| INS_QRY_L003 | 查詢退保紀錄 | HR | `GET /api/v1/insurance/enrollments` | `{"status":"WITHDRAWN","insuranceType":"LABOR"}` | `status = 'WITHDRAWN'`, `insurance_type = 'LABOR'` |
| INS_QRY_L004 | 依投保日期查詢 | HR | `GET /api/v1/insurance/enrollments` | `{"enrollDate":"2026-01-01","insuranceType":"LABOR"}` | `enroll_date = '2026-01-01'`, `insurance_type = 'LABOR'` |
| INS_QRY_L005 | 員工查詢自己勞保 | EMPLOYEE | `GET /api/v1/insurance/my` | `{"insuranceType":"LABOR"}` | `employee_id = '{currentUserId}'`, `insurance_type = 'LABOR'`, `status = 'ACTIVE'` |
| INS_QRY_L006 | 依投保級距查詢 | HR | `GET /api/v1/insurance/enrollments` | `{"salaryGrade":"45800","insuranceType":"LABOR"}` | `monthly_salary = 45800`, `insurance_type = 'LABOR'` |

#### 2.1.2 業務場景說明

**INS_QRY_L001: 查詢員工勞保紀錄**

- **使用者：** HR 專員
- **業務目的：** 查詢特定員工的所有勞保加退保歷程
- **權限控制：** `insurance:enrollment:read`
- **過濾邏輯：**
  ```sql
  WHERE employee_id = 'E001'
    AND insurance_type = 'LABOR'
  ORDER BY enroll_date DESC
  ```

**INS_QRY_L005: 員工查詢自己勞保（ESS）**

- **使用者：** 一般員工
- **業務目的：** 員工自助查詢自己的保險資訊
- **權限控制：** 無需特殊權限，但只能查詢自己
- **過濾邏輯：**
  ```sql
  WHERE employee_id = '{currentUserId}'
    AND insurance_type = 'LABOR'
    AND status = 'ACTIVE'
  ```

---

### 2.2 健保投保紀錄查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| INS_QRY_H001 | 查詢員工健保紀錄 | HR | `GET /api/v1/insurance/enrollments` | `{"employeeId":"E001","insuranceType":"HEALTH"}` | `employee_id = 'E001'`, `insurance_type = 'HEALTH'` |
| INS_QRY_H002 | 查詢有效健保 | HR | `GET /api/v1/insurance/enrollments` | `{"status":"ACTIVE","insuranceType":"HEALTH"}` | `status = 'ACTIVE'`, `insurance_type = 'HEALTH'` |
| INS_QRY_H003 | 查詢含眷屬的健保 | HR | `GET /api/v1/insurance/enrollments` | `{"hasDependents":true,"insuranceType":"HEALTH"}` | `dependent_count > 0`, `insurance_type = 'HEALTH'`, `status = 'ACTIVE'` |
| INS_QRY_H004 | 員工查詢自己健保 | EMPLOYEE | `GET /api/v1/insurance/my` | `{"insuranceType":"HEALTH"}` | `employee_id = '{currentUserId}'`, `insurance_type = 'HEALTH'`, `status = 'ACTIVE'` |
| INS_QRY_H005 | 依投保單位查詢 | HR | `GET /api/v1/insurance/enrollments` | `{"insuranceUnitId":"U001","insuranceType":"HEALTH"}` | `insurance_unit_id = 'U001'`, `insurance_type = 'HEALTH'` |

---

### 2.3 勞退提撥紀錄查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| INS_QRY_P001 | 查詢員工勞退紀錄 | HR | `GET /api/v1/insurance/enrollments` | `{"employeeId":"E001","insuranceType":"PENSION"}` | `employee_id = 'E001'`, `insurance_type = 'PENSION'` |
| INS_QRY_P002 | 查詢月提撥紀錄 | HR | `GET /api/v1/insurance/enrollments` | `{"yearMonth":"2026-01","insuranceType":"PENSION"}` | `DATE_FORMAT(enroll_date, '%Y-%m') <= '2026-01'`, `(withdraw_date IS NULL OR DATE_FORMAT(withdraw_date, '%Y-%m') >= '2026-01')`, `insurance_type = 'PENSION'`, `status = 'ACTIVE'` |
| INS_QRY_P003 | 查詢自提勞退 | HR | `GET /api/v1/insurance/enrollments` | `{"hasVoluntary":true,"insuranceType":"PENSION"}` | `employee_voluntary_rate > 0`, `insurance_type = 'PENSION'` |
| INS_QRY_P004 | 員工查詢自己勞退 | EMPLOYEE | `GET /api/v1/insurance/my` | `{"insuranceType":"PENSION"}` | `employee_id = '{currentUserId}'`, `insurance_type = 'PENSION'`, `status = 'ACTIVE'` |

---

### 2.4 眷屬資料查詢

#### 2.4.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| INS_QRY_D001 | 查詢員工眷屬 | HR | `GET /api/v1/insurance/dependents` | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `status = 'ACTIVE'` |
| INS_QRY_D002 | 依眷屬關係查詢 | HR | `GET /api/v1/insurance/dependents` | `{"relationship":"SPOUSE"}` | `relationship = 'SPOUSE'`, `status = 'ACTIVE'` |
| INS_QRY_D003 | 查詢有效眷屬 | HR | `GET /api/v1/insurance/dependents` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| INS_QRY_D004 | 員工查詢自己眷屬 | EMPLOYEE | `GET /api/v1/insurance/my/dependents` | `{}` | `employee_id = '{currentUserId}'`, `status = 'ACTIVE'` |

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `InsuranceEnrollmentCompletedEvent` | 加保完成 | Insurance | Payroll | 計算保險費用 |
| `InsuranceWithdrawalCompletedEvent` | 退保完成 | Insurance | Payroll | 停止扣保費 |
| `InsuranceLevelAdjustedEvent` | 投保級距調整 | Insurance | Payroll, Notification | 重新計算保費，發送通知 |
| `SupplementaryPremiumCalculatedEvent` | 補充保費計算 | Insurance | Payroll | 扣補充保費 |

---

### 3.2 InsuranceEnrollmentCompletedEvent (加保完成事件)

**觸發時機：**
新員工到職或手動加保完成後，系統建立三保加保記錄並計算保費後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-ins-enroll-001",
  "eventType": "InsuranceEnrollmentCompletedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "enrollment-001",
  "aggregateType": "InsuranceEnrollment",
  "payload": {
    "employeeId": "E001",
    "employeeName": "王小華",
    "enrollDate": "2026-02-01",
    "insuranceUnitId": "unit-001",
    "insuranceUnitName": "ABC科技股份有限公司",
    "enrollments": [
      {
        "enrollmentId": "enroll-labor-001",
        "type": "LABOR",
        "monthlySalary": 48200,
        "levelNumber": 15,
        "status": "ACTIVE"
      },
      {
        "enrollmentId": "enroll-health-001",
        "type": "HEALTH",
        "monthlySalary": 48200,
        "levelNumber": 15,
        "dependentCount": 0,
        "status": "ACTIVE"
      },
      {
        "enrollmentId": "enroll-pension-001",
        "type": "PENSION",
        "monthlySalary": 48200,
        "employerRate": 0.06,
        "employeeVoluntaryRate": 0,
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

**訂閱服務處理：**

- **Payroll Service:**
  - 建立員工保費扣款紀錄
  - 每月薪資計算時自動扣除保費

---

### 3.3 InsuranceWithdrawalCompletedEvent (退保完成事件)

**觸發時機：**
員工離職時執行退保作業完成後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-ins-withdraw-001",
  "eventType": "InsuranceWithdrawalCompletedEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "enrollment-001",
  "aggregateType": "InsuranceEnrollment",
  "payload": {
    "enrollmentId": "enrollment-001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "insuranceType": "LABOR",
    "enrollDate": "2026-01-01",
    "withdrawDate": "2026-12-31",
    "reason": "離職",
    "totalMonths": 12,
    "finalMonthlySalary": 48200
  }
}
```

**訂閱服務處理：**

- **Payroll Service:**
  - 停止扣除保費
  - 計算最後一個月的保費（依離職日按比例計算）

---

### 3.4 InsuranceLevelAdjustedEvent (投保級距調整事件)

**觸發時機：**
員工薪資調整導致投保級距變更時發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-ins-adjust-001",
  "eventType": "InsuranceLevelAdjustedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "enrollment-001",
  "aggregateType": "InsuranceEnrollment",
  "payload": {
    "enrollmentId": "enrollment-001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "insuranceType": "LABOR",
    "oldSalaryGrade": 48200,
    "newSalaryGrade": 50600,
    "oldLevelNumber": 15,
    "newLevelNumber": 16,
    "adjustReason": "年度調薪",
    "effectiveDate": "2026-02-01",
    "newFees": {
      "laborEmployee": 1162,
      "laborEmployer": 4067,
      "healthEmployee": 783,
      "healthEmployer": 1566,
      "pensionEmployer": 3036
    },
    "feeDifference": {
      "employeeDiff": 96,
      "employerDiff": 336
    }
  }
}
```

**訂閱服務處理：**

- **Payroll Service:**
  - 更新員工保費扣款金額
  - 從生效日期開始適用新保費

- **Notification Service:**
  - 發送通知給員工：「您的投保級距已調整，保費將有所變動」

---

### 3.5 SupplementaryPremiumCalculatedEvent (補充保費計算事件)

**觸發時機：**
員工獎金超過 4 個月投保薪資時，計算補充保費後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-ins-supp-001",
  "eventType": "SupplementaryPremiumCalculatedEvent",
  "timestamp": "2026-02-09T12:00:00Z",
  "aggregateId": "supp-premium-001",
  "aggregateType": "SupplementaryPremium",
  "payload": {
    "supplementaryPremiumId": "supp-premium-001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "incomeType": "BONUS",
    "totalAmount": 250000,
    "monthlySalary": 48200,
    "threshold": 192800,
    "chargeableAmount": 57200,
    "premiumRate": 0.0211,
    "premiumAmount": 1207,
    "paymentDate": "2026-02-15"
  }
}
```

**訂閱服務處理：**

- **Payroll Service:**
  - 在發放獎金時扣除補充保費
  - 記錄補充保費扣款明細

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

**範例：INS_CMD_003 加保測試**

```java
@Test
@DisplayName("INS_CMD_003: 手動加保 - 應建立三筆加保記錄並發布事件")
void enrollInsurance_ShouldCreateThreeEnrollmentsAndPublishEvent() {
    // Given
    var request = CreateEnrollmentRequest.builder()
        .employeeId("E001")
        .insuranceUnitId("unit-001")
        .monthlySalary(48200)
        .enrollDate(LocalDate.of(2026, 1, 1))
        .insuranceTypes(List.of("LABOR", "HEALTH", "PENSION"))
        .build();

    // Mock employee exists
    when(organizationService.employeeExists("E001")).thenReturn(true);

    // Mock no existing enrollment
    when(enrollmentRepository.findActiveEnrollment("E001", any())).thenReturn(Optional.empty());

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify 3 enrollments saved
    var captor = ArgumentCaptor.forClass(InsuranceEnrollment.class);
    verify(enrollmentRepository, times(3)).save(captor.capture());

    var savedEnrollments = captor.getAllValues();
    assertThat(savedEnrollments).hasSize(3);
    assertThat(savedEnrollments).extracting("insuranceType")
        .containsExactlyInAnyOrder("LABOR", "HEALTH", "PENSION");

    // Then - Verify event published
    var eventCaptor = ArgumentCaptor.forClass(InsuranceEnrollmentCompletedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("InsuranceEnrollmentCompletedEvent");
    assertThat(event.getPayload().getEmployeeId()).isEqualTo("E001");
    assertThat(event.getPayload().getEnrollments()).hasSize(3);
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

**範例：INS_QRY_L001 查詢測試**

```java
@Test
@DisplayName("INS_QRY_L001: 查詢員工勞保紀錄 - 應包含員工ID與保險類型過濾")
void searchEnrollment_ByEmployeeAndType_ShouldIncludeRequiredFilters() {
    // Given
    String contractSpec = loadContractSpec("insurance");

    var request = EnrollmentSearchRequest.builder()
        .employeeId("E001")
        .insuranceType("LABOR")
        .build();

    // When
    var captor = ArgumentCaptor.forClass(QueryGroup.class);
    service.getResponse(request, currentUser);

    // Then
    verify(enrollmentRepository).findPage(captor.capture(), any());

    var queryGroup = captor.getValue();
    assertContract(queryGroup, contractSpec, "INS_QRY_L001");

    // Additional assertions
    assertThat(queryGroup).containsFilter("employee_id", Operator.EQUAL, "E001");
    assertThat(queryGroup).containsFilter("insurance_type", Operator.EQUAL, "LABOR");
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

**範例：INS_CMD_003 整合測試**

```java
@Test
@DisplayName("INS_CMD_003: 手動加保整合測試 - 應建立三筆記錄並返回正確回應")
void enrollInsurance_Integration_ShouldCreateRecordsAndReturnResponse() throws Exception {
    // Given
    var request = CreateEnrollmentRequest.builder()
        .employeeId("E001")
        .insuranceUnitId("unit-001")
        .monthlySalary(48200)
        .enrollDate(LocalDate.of(2026, 1, 1))
        .insuranceTypes(List.of("LABOR", "HEALTH", "PENSION"))
        .build();

    // When
    var result = mockMvc.perform(post("/api/v1/insurance/enrollments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.enrollments").isArray())
        .andExpect(jsonPath("$.data.enrollments.length()").value(3))
        .andReturn();

    // Then - Verify database
    var enrollments = enrollmentRepository.findByEmployeeId("E001");
    assertThat(enrollments).hasSize(3);
    assertThat(enrollments).extracting("status").containsOnly("ACTIVE");
}
```

---

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 保險紀錄使用 `status` 欄位（'ACTIVE', 'WITHDRAWN', 'PENDING'）
   - 眷屬資料使用 `status` 欄位（'ACTIVE', 'INACTIVE'）
   - **不使用 `is_deleted` 欄位**

2. **個人資料保護:**
   - 員工只能查詢自己的保險資料
   - 眷屬身分證字號需遮蔽（顯示前 3 後 2 碼）

3. **租戶隔離:**
   - 所有查詢自動加上 `tenant_id = ?` 過濾條件

---

### 5.2 投保級距說明

- 勞保投保薪資級距由勞動部公告（2025 年為 27,470 ~ 50,600 元，共 14 級）
- 健保投保金額由衛福部公告（2025 年為 27,470 ~ 219,500 元，共 50 級）
- 勞退月提繳工資分級表由勞動部公告

詳細級距表請參考：`knowledge/03_Logic_Specifications/tax_insurance_tables_2025.md`

---

### 5.3 保費計算公式

**勞工保險：**
- 月保費 = 投保薪資 × 11.5%
- 員工負擔 = 月保費 × 20%
- 雇主負擔 = 月保費 × 70%
- 政府負擔 = 月保費 × 10%

**健康保險：**
- 月保費 = 投保薪資 × 5.17% × (1 + 眷屬數)
- 員工負擔 = 月保費 × 30%
- 雇主負擔 = 月保費 × 60%
- 政府負擔 = 月保費 × 10%

**勞工退休金：**
- 雇主提繳 = 投保薪資 × 6%
- 員工自提 = 投保薪資 × 員工自提率（0% ~ 6%，自願）

**補充保費（二代健保）：**
- 門檻 = 投保薪資 × 4
- 補充保費 = (獎金 - 門檻) × 2.11%（如果獎金 > 門檻）

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景、業務規則驗證、Domain Events Payload 定義、測試斷言規格 |
| 1.0 | 2026-02-06 | 精簡版建立 |
