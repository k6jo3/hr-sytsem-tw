# 績效管理服務業務合約 (Performance Service Contract)

> **服務代碼:** 08
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義績效管理服務的業務合約，涵蓋考核週期、考核表單、目標管理等查詢場景。

---

## 1. 考核週期查詢合約 (Review Cycle Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PFM_C001 | 查詢進行中週期 | HR | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'`, `is_deleted = 0` |
| PFM_C002 | 查詢已完成週期 | HR | `{"status":"COMPLETED"}` | `status = 'COMPLETED'`, `is_deleted = 0` |
| PFM_C003 | 依年度查詢週期 | HR | `{"year":"2025"}` | `year = 2025`, `is_deleted = 0` |
| PFM_C004 | 依類型查詢週期 | HR | `{"type":"ANNUAL"}` | `type = 'ANNUAL'`, `is_deleted = 0` |
| PFM_C005 | 查詢規劃中週期 | HR | `{"status":"PLANNING"}` | `status = 'PLANNING'`, `is_deleted = 0` |

---

## 2. 考核紀錄查詢合約 (Review Record Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PFM_R001 | 查詢員工考核紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PFM_R002 | 查詢週期考核紀錄 | HR | `{"cycleId":"C001"}` | `cycle_id = 'C001'`, `is_deleted = 0` |
| PFM_R003 | 查詢待自評紀錄 | HR | `{"stage":"SELF_REVIEW"}` | `stage = 'SELF_REVIEW'`, `is_deleted = 0` |
| PFM_R004 | 查詢待主管評核 | MANAGER | `{"stage":"MANAGER_REVIEW"}` | `stage = 'MANAGER_REVIEW'`, `reviewer_id = '{currentUserId}'`, `is_deleted = 0` |
| PFM_R005 | 員工查詢自己考核 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |
| PFM_R006 | 主管查詢下屬考核 | MANAGER | `{}` | `employee.department_id IN ('{managedDeptIds}')`, `is_deleted = 0` |
| PFM_R007 | 查詢已完成考核 | HR | `{"status":"COMPLETED"}` | `status = 'COMPLETED'`, `is_deleted = 0` |
| PFM_R008 | 依等第查詢 | HR | `{"grade":"A"}` | `grade = 'A'`, `is_deleted = 0` |

---

## 3. 目標管理查詢合約 (Goal Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PFM_G001 | 查詢員工目標 | MANAGER | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PFM_G002 | 查詢進行中目標 | MANAGER | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'`, `is_deleted = 0` |
| PFM_G003 | 查詢已達成目標 | MANAGER | `{"status":"ACHIEVED"}` | `status = 'ACHIEVED'`, `is_deleted = 0` |
| PFM_G004 | 員工查詢自己目標 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |
| PFM_G005 | 依週期查詢目標 | HR | `{"cycleId":"C001"}` | `cycle_id = 'C001'`, `is_deleted = 0` |
| PFM_G006 | 查詢部門目標 | MANAGER | `{"deptId":"D001"}` | `department_id = 'D001'`, `is_deleted = 0` |

---

## 4. 考核表單查詢合約 (Review Form Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PFM_F001 | 查詢啟用的表單 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| PFM_F002 | 依類型查詢表單 | HR | `{"type":"ANNUAL"}` | `type = 'ANNUAL'`, `is_deleted = 0` |
| PFM_F003 | 依職等查詢表單 | HR | `{"grade":"M1"}` | `applicable_grades LIKE 'M1'`, `is_deleted = 0` |
| PFM_F004 | 查詢預設表單 | HR | `{"isDefault":true}` | `is_default = 1`, `is_deleted = 0` |

---

## 5. 360 度回饋查詢合約 (360 Feedback Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PFM_360_001 | 查詢待填寫回饋 | EMPLOYEE | `{"status":"PENDING"}` | `reviewer_id = '{currentUserId}'`, `status = 'PENDING'` |
| PFM_360_002 | 查詢已提交回饋 | EMPLOYEE | `{"status":"SUBMITTED"}` | `reviewer_id = '{currentUserId}'`, `status = 'SUBMITTED'` |
| PFM_360_003 | HR 查詢員工回饋 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| PFM_360_004 | 查詢匿名回饋 | HR | `{"isAnonymous":true}` | `is_anonymous = 1` |

---

## 補充說明

### 通用安全規則

1. **軟刪除過濾**: 所有查詢須包含 `is_deleted = 0`
2. **考核隱私**: 員工只能看自己的考核結果
3. **主管權限**: 主管可查詢直屬下屬的考核
4. **匿名保護**: 360 回饋的匿名性需確保

### 考核等第代碼

| 代碼 | 說明 | 比例建議 |
|:---|:---|:---|
| A | 傑出 | 10% |
| B | 優良 | 25% |
| C | 稱職 | 50% |
| D | 待改進 | 10% |
| E | 不適任 | 5% |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全公司考核 | 可設定週期與表單 |
| MANAGER | 直屬下屬考核 | 可評核下屬 |
| EMPLOYEE | 僅自己 | 可自評、查詢自己結果 |
