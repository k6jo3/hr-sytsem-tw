# 工時管理服務業務合約 (Timesheet Service Contract)

> **服務代碼:** 07
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義工時管理服務的業務合約，涵蓋週工時單、工時填報、PM審核等查詢場景。

---

## 1. 工時單查詢合約 (Timesheet Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| TMS_T001 | 查詢員工週工時單 | PM | `{"employeeId":"E001","weekStart":"2025-01-06"}` | `employee_id = 'E001'`, `week_start = '2025-01-06'`, `is_deleted = 0` |
| TMS_T002 | 查詢待審核工時單 | PM | `{"status":"PENDING"}` | `status = 'PENDING'`, `is_deleted = 0` |
| TMS_T003 | 查詢已核准工時單 | PM | `{"status":"APPROVED"}` | `status = 'APPROVED'`, `is_deleted = 0` |
| TMS_T004 | 查詢已駁回工時單 | PM | `{"status":"REJECTED"}` | `status = 'REJECTED'`, `is_deleted = 0` |
| TMS_T005 | 依專案查詢工時單 | PM | `{"projectId":"P001"}` | `project_id = 'P001'`, `is_deleted = 0` |
| TMS_T006 | 員工查詢自己工時單 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |
| TMS_T007 | PM 查詢專案工時單 | PM | `{}` | `project_id IN ('{managedProjectIds}')`, `is_deleted = 0` |
| TMS_T008 | 依日期範圍查詢 | PM | `{"weekStartFrom":"2025-01-01"}` | `week_start >= '2025-01-01'`, `is_deleted = 0` |
| TMS_T009 | 查詢未提交工時單 | PM | `{"status":"DRAFT"}` | `status = 'DRAFT'`, `is_deleted = 0` |

---

## 2. 工時明細查詢合約 (Timesheet Entry Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| TMS_E001 | 查詢工時單明細 | PM | `{"timesheetId":"TS001"}` | `timesheet_id = 'TS001'` |
| TMS_E002 | 依日期查詢明細 | PM | `{"workDate":"2025-01-15"}` | `work_date = '2025-01-15'` |
| TMS_E003 | 依專案查詢明細 | PM | `{"projectId":"P001"}` | `project_id = 'P001'` |
| TMS_E004 | 依 WBS 查詢明細 | PM | `{"wbsId":"W001"}` | `wbs_id = 'W001'` |
| TMS_E005 | 員工查詢自己明細 | EMPLOYEE | `{"workDate":"2025-01-15"}` | `employee_id = '{currentUserId}'`, `work_date = '2025-01-15'` |

---

## 3. 工時統計查詢合約 (Timesheet Summary Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| TMS_S001 | 查詢專案工時統計 | PM | `{"projectId":"P001","yearMonth":"2025-01"}` | `project_id = 'P001'`, `year_month = '2025-01'` |
| TMS_S002 | 查詢員工月工時統計 | PM | `{"employeeId":"E001","yearMonth":"2025-01"}` | `employee_id = 'E001'`, `year_month = '2025-01'` |
| TMS_S003 | 查詢部門工時統計 | HR | `{"deptId":"D001","yearMonth":"2025-01"}` | `department_id = 'D001'`, `year_month = '2025-01'` |
| TMS_S004 | 員工查詢自己統計 | EMPLOYEE | `{"yearMonth":"2025-01"}` | `employee_id = '{currentUserId}'`, `year_month = '2025-01'` |
| TMS_S005 | 依 WBS 查詢統計 | PM | `{"wbsId":"W001"}` | `wbs_id = 'W001'` |

---

## 4. 工時成本查詢合約 (Timesheet Cost Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| TMS_C001 | 查詢專案工時成本 | PM | `{"projectId":"P001"}` | `project_id = 'P001'`, `status = 'APPROVED'` |
| TMS_C002 | 查詢月工時成本 | PM | `{"yearMonth":"2025-01"}` | `year_month = '2025-01'`, `status = 'APPROVED'` |
| TMS_C003 | 依員工職等計算成本 | PM | `{"projectId":"P001","grade":"M1"}` | `project_id = 'P001'`, `employee.grade = 'M1'`, `status = 'APPROVED'` |

---

## 補充說明

### 通用安全規則

1. **軟刪除過濾**: 工時單查詢須包含 `is_deleted = 0`
2. **專案權限**: PM 只能查詢所管理專案的工時
3. **個人資料**: 員工只能查詢/填報自己的工時

### 工時單狀態代碼

| 代碼 | 說明 |
|:---|:---|
| DRAFT | 草稿 |
| PENDING | 待審核 |
| APPROVED | 已核准 |
| REJECTED | 已駁回 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| PM | 所管理專案的工時 | 可審核工時單 |
| HR | 全公司工時統計 | 不可審核 |
| EMPLOYEE | 僅自己 | 只能填報/查詢自己的工時 |
