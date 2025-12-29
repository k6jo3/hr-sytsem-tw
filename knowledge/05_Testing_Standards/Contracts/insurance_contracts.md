# 保險管理服務業務合約 (Insurance Service Contract)

> **服務代碼:** 05
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義保險管理服務的業務合約，涵蓋勞保、健保、勞退等查詢場景。

---

## 1. 勞保投保紀錄查詢合約 (Labor Insurance Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| INS_L001 | 查詢員工勞保紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| INS_L002 | 查詢有效勞保 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| INS_L003 | 查詢退保紀錄 | HR | `{"status":"TERMINATED"}` | `status = 'TERMINATED'`, `is_deleted = 0` |
| INS_L004 | 依投保日期查詢 | HR | `{"enrollDate":"2025-01-01"}` | `enroll_date = '2025-01-01'`, `is_deleted = 0` |
| INS_L005 | 員工查詢自己勞保 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |
| INS_L006 | 依投保級距查詢 | HR | `{"salaryGrade":"45800"}` | `salary_grade = 45800`, `is_deleted = 0` |

---

## 2. 健保投保紀錄查詢合約 (Health Insurance Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| INS_H001 | 查詢員工健保紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| INS_H002 | 查詢有效健保 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| INS_H003 | 查詢含眷屬的健保 | HR | `{"hasDependents":true}` | `has_dependents = 1`, `is_deleted = 0` |
| INS_H004 | 員工查詢自己健保 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |
| INS_H005 | 依投保單位查詢 | HR | `{"insuranceUnit":"U001"}` | `insurance_unit = 'U001'`, `is_deleted = 0` |

---

## 3. 勞退提撥紀錄查詢合約 (Pension Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| INS_P001 | 查詢員工勞退紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| INS_P002 | 查詢月提撥紀錄 | HR | `{"yearMonth":"2025-01"}` | `year_month = '2025-01'` |
| INS_P003 | 依提撥率查詢 | HR | `{"contributionRate":"6"}` | `contribution_rate = 6`, `is_deleted = 0` |
| INS_P004 | 查詢自提勞退 | HR | `{"hasVoluntary":true}` | `voluntary_rate > 0`, `is_deleted = 0` |
| INS_P005 | 員工查詢自己勞退 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |

---

## 4. 眷屬資料查詢合約 (Dependent Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| INS_D001 | 查詢員工眷屬 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| INS_D002 | 依眷屬關係查詢 | HR | `{"relationship":"SPOUSE"}` | `relationship = 'SPOUSE'`, `is_deleted = 0` |
| INS_D003 | 查詢有效眷屬 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| INS_D004 | 員工查詢自己眷屬 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |

---

## 5. 職災紀錄查詢合約 (Work Injury Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| INS_W001 | 查詢員工職災紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| INS_W002 | 查詢處理中職災 | HR | `{"status":"PROCESSING"}` | `status = 'PROCESSING'`, `is_deleted = 0` |
| INS_W003 | 查詢已結案職災 | HR | `{"status":"CLOSED"}` | `status = 'CLOSED'`, `is_deleted = 0` |
| INS_W004 | 依發生日期查詢 | HR | `{"incidentDate":"2025-01-15"}` | `incident_date = '2025-01-15'`, `is_deleted = 0` |

---

## 補充說明

### 通用安全規則

1. **軟刪除過濾**: 所有主檔查詢須包含 `is_deleted = 0`
2. **個人資料保護**: 員工只能查詢自己的保險資料
3. **眷屬資料敏感**: 眷屬身分證字號等需遮蔽

### 眷屬關係代碼

| 代碼 | 說明 |
|:---|:---|
| SPOUSE | 配偶 |
| CHILD | 子女 |
| PARENT | 父母 |
| GRANDPARENT | 祖父母 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全公司 | 可管理保險異動 |
| EMPLOYEE | 僅自己 | 只能查詢自己的保險資料 |
