# 保險管理服務業務合約 (Insurance Service Contract)

> **服務代碼:** HR05
> **版本:** 2.0
> **建立日期:** 2026-02-06
> **維護者:** SA Team
> **變更說明:** 移除不存在的 is_deleted 欄位，新增 API 端點、Command 操作、Domain Events，採用雙層結構設計

---

## 📋 目錄

1. [合約概述](#合約概述)
2. [查詢操作合約 (Query Contracts)](#查詢操作合約-query-contracts)
3. [命令操作合約 (Command Contracts)](#命令操作合約-command-contracts)
4. [Domain Events 定義](#domain-events-定義)
5. [補充說明](#補充說明)

---

## 合約概述

### 服務定位
保險管理服務負責勞保、健保、勞退等社會保險的加退保管理、投保級距調整、保費計算等功能。本服務必須確保**符合台灣勞動法規**的所有規定。

### 資料軟刪除策略

**⚠️ 重要：本服務不使用 `is_deleted` 欄位進行軟刪除**

- **保險加退保紀錄**: 使用 `status` 欄位，'ACTIVE' 為有效加保，'TERMINATED' 為已退保
- **眷屬資料**: 使用 `status` 欄位，'ACTIVE' 為有效，'INACTIVE' 為已失效
- **歷史記錄**: 不進行軟刪除，保留所有歷史記錄（用於稽核與申報）

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊權限 |
|:---|:---|:---|
| `HR` | 全公司 | 可管理保險異動、調整級距、產生申報檔 |
| `EMPLOYEE` | 僅自己 | 只能查詢自己的保險資料 |

---

## 查詢操作合約 (Query Contracts)

### 2.1 勞保投保紀錄查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| INS_QRY_L001 | 查詢員工勞保紀錄 | HR | `GET /api/v1/insurance/labor` | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `insurance_type = 'LABOR'` |
| INS_QRY_L002 | 查詢有效勞保 | HR | `GET /api/v1/insurance/labor` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `insurance_type = 'LABOR'` |
| INS_QRY_L003 | 查詢退保紀錄 | HR | `GET /api/v1/insurance/labor` | `{"status":"TERMINATED"}` | `status = 'TERMINATED'`, `insurance_type = 'LABOR'` |
| INS_QRY_L004 | 依投保日期查詢 | HR | `GET /api/v1/insurance/labor` | `{"enrollDate":"2025-01-01"}` | `enroll_date = '2025-01-01'`, `insurance_type = 'LABOR'` |
| INS_QRY_L005 | 員工查詢自己勞保 | EMPLOYEE | `GET /api/v1/insurance/my/labor` | `{}` | `employee_id = '{currentUserId}'`, `insurance_type = 'LABOR'` |
| INS_QRY_L006 | 依投保級距查詢 | HR | `GET /api/v1/insurance/labor` | `{"salaryGrade":"45800"}` | `salary_grade = 45800`, `insurance_type = 'LABOR'` |

### 2.2 健保投保紀錄查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| INS_QRY_H001 | 查詢員工健保紀錄 | HR | `GET /api/v1/insurance/health` | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `insurance_type = 'HEALTH'` |
| INS_QRY_H002 | 查詢有效健保 | HR | `GET /api/v1/insurance/health` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `insurance_type = 'HEALTH'` |
| INS_QRY_H003 | 查詢含眷屬的健保 | HR | `GET /api/v1/insurance/health` | `{"hasDependents":true}` | `dependent_count > 0`, `insurance_type = 'HEALTH'` |
| INS_QRY_H004 | 員工查詢自己健保 | EMPLOYEE | `GET /api/v1/insurance/my/health` | `{}` | `employee_id = '{currentUserId}'`, `insurance_type = 'HEALTH'` |
| INS_QRY_H005 | 依投保單位查詢 | HR | `GET /api/v1/insurance/health` | `{"insuranceUnit":"U001"}` | `insurance_unit = 'U001'`, `insurance_type = 'HEALTH'` |

### 2.3 勞退提撥紀錄查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| INS_QRY_P001 | 查詢員工勞退紀錄 | HR | `GET /api/v1/insurance/pension` | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `insurance_type = 'PENSION'` |
| INS_QRY_P002 | 查詢月提撥紀錄 | HR | `GET /api/v1/insurance/pension` | `{"yearMonth":"2025-01"}` | `year_month = '2025-01'` |
| INS_QRY_P003 | 依提撥率查詢 | HR | `GET /api/v1/insurance/pension` | `{"contributionRate":"6"}` | `employer_rate = 6`, `insurance_type = 'PENSION'` |
| INS_QRY_P004 | 查詢自提勞退 | HR | `GET /api/v1/insurance/pension` | `{"hasVoluntary":true}` | `employee_voluntary_rate > 0`, `insurance_type = 'PENSION'` |
| INS_QRY_P005 | 員工查詢自己勞退 | EMPLOYEE | `GET /api/v1/insurance/my/pension` | `{}` | `employee_id = '{currentUserId}'`, `insurance_type = 'PENSION'` |

### 2.4 眷屬資料查詢

#### 2.4.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| INS_QRY_D001 | 查詢員工眷屬 | HR | `GET /api/v1/insurance/dependents` | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `status = 'ACTIVE'` |
| INS_QRY_D002 | 依眷屬關係查詢 | HR | `GET /api/v1/insurance/dependents` | `{"relationship":"SPOUSE"}` | `relationship = 'SPOUSE'`, `status = 'ACTIVE'` |
| INS_QRY_D003 | 查詢有效眷屬 | HR | `GET /api/v1/insurance/dependents` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| INS_QRY_D004 | 員工查詢自己眷屬 | EMPLOYEE | `GET /api/v1/insurance/my/dependents` | `{}` | `employee_id = '{currentUserId}'`, `status = 'ACTIVE'` |

---

## 命令操作合約 (Command Contracts)

### 3.1 加保操作

#### 3.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| INS_CMD_001 | 新員工加保（三保合一） | HR | `POST /api/v1/insurance/enrollments` | `{"employeeId":"E001","monthlySalary":48200,"enrollDate":"2025-01-01"}` | 員工存在檢查, 投保級距計算, 不可重複加保檢查 | `InsuranceEnrollmentCompleted` |
| INS_CMD_002 | 調整投保級距 | HR | `PUT /api/v1/insurance/enrollments/{id}/adjust-level` | `{"newSalaryGrade":50600}` | 級距有效性檢查, 調整原因必填 | `InsuranceLevelAdjusted` |
| INS_CMD_003 | 員工退保 | HR | `PUT /api/v1/insurance/enrollments/{id}/withdraw` | `{"withdrawDate":"2025-12-31","reason":"離職"}` | 狀態檢查（必須為 ACTIVE）, 退保日期合理性檢查 | `InsuranceWithdrawalCompleted` |

---

## Domain Events 定義

### 4.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `InsuranceEnrollmentCompleted` | 加保完成 | Insurance | Payroll | 計算保險費用 |
| `InsuranceWithdrawalCompleted` | 退保完成 | Insurance | Payroll | 停止扣保費 |
| `InsuranceLevelAdjusted` | 投保級距調整 | Insurance | Payroll, Notification | 重新計算保費 |
| `SupplementaryPremiumCalculated` | 補充保費計算 | Insurance | Payroll | 扣補充保費 |

### 4.2 InsuranceEnrollmentCompletedEvent (加保完成事件)

**Event Payload:**
```json
{
  "eventId": "evt-ins-enroll-001",
  "eventType": "InsuranceEnrollmentCompletedEvent",
  "timestamp": "2026-02-06T09:00:00Z",
  "aggregateId": "enrollment-001",
  "payload": {
    "employeeId": "E001",
    "employeeName": "王小華",
    "enrollDate": "2026-02-01",
    "enrollments": [
      {"type": "LABOR", "monthlySalary": 48200},
      {"type": "HEALTH", "monthlySalary": 48200},
      {"type": "PENSION", "monthlySalary": 48200}
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

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 保險紀錄使用 `status` 欄位（'ACTIVE', 'TERMINATED'）
   - 眷屬資料使用 `status` 欄位（'ACTIVE', 'INACTIVE'）
   - **不使用 `is_deleted` 欄位**

2. **個人資料保護:**
   - 員工只能查詢自己的保險資料
   - 眷屬身分證字號需遮蔽（顯示前 3 後 2 碼）

### 5.2 投保級距說明

- 勞保投保薪資級距由勞動部公告（2025 年為 27,470 ~ 50,600 元，共 14 級）
- 健保投保金額由衛福部公告（2025 年為 27,470 ~ 219,500 元，共 50 級）
- 勞退月提繳工資分級表由勞動部公告

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-06 | 移除不存在的 is_deleted 欄位，新增 API 端點、Command 操作、Domain Events，採用雙層結構設計 |
| 1.0 | 2025-12-19 | 初版建立 |
