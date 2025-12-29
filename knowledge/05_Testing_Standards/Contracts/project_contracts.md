# 專案管理服務業務合約 (Project Service Contract)

> **服務代碼:** 06
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義專案管理服務的業務合約，涵蓋專案、客戶、WBS、成本追蹤等查詢場景。

---

## 1. 專案查詢合約 (Project Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PRJ_P001 | 查詢進行中專案 | PM | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'`, `is_deleted = 0` |
| PRJ_P002 | 查詢已完成專案 | PM | `{"status":"COMPLETED"}` | `status = 'COMPLETED'`, `is_deleted = 0` |
| PRJ_P003 | 依客戶查詢專案 | PM | `{"customerId":"C001"}` | `customer_id = 'C001'`, `is_deleted = 0` |
| PRJ_P004 | 依 PM 查詢專案 | PM | `{"pmId":"E001"}` | `pm_id = 'E001'`, `is_deleted = 0` |
| PRJ_P005 | 依名稱模糊查詢 | PM | `{"name":"系統"}` | `name LIKE '系統'`, `is_deleted = 0` |
| PRJ_P006 | 查詢延遲專案 | PM | `{"isDelayed":true}` | `is_delayed = 1`, `is_deleted = 0` |
| PRJ_P007 | 員工查詢參與專案 | EMPLOYEE | `{}` | `team_members.employee_id = '{currentUserId}'`, `is_deleted = 0` |
| PRJ_P008 | 依部門查詢專案 | HR | `{"deptId":"D001"}` | `department_id = 'D001'`, `is_deleted = 0` |
| PRJ_P009 | 查詢預算超支專案 | PM | `{"isBudgetExceeded":true}` | `actual_cost > budget`, `is_deleted = 0` |
| PRJ_P010 | 依日期範圍查詢 | PM | `{"startDateFrom":"2025-01-01"}` | `start_date >= '2025-01-01'`, `is_deleted = 0` |

---

## 2. 客戶查詢合約 (Customer Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PRJ_C001 | 查詢有效客戶 | PM | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| PRJ_C002 | 依名稱模糊查詢 | PM | `{"name":"科技"}` | `name LIKE '科技'`, `is_deleted = 0` |
| PRJ_C003 | 依產業類型查詢 | PM | `{"industry":"IT"}` | `industry = 'IT'`, `is_deleted = 0` |
| PRJ_C004 | 查詢有專案的客戶 | PM | `{"hasProjects":true}` | `project_count > 0`, `is_deleted = 0` |
| PRJ_C005 | 依負責業務查詢 | PM | `{"salesRepId":"E001"}` | `sales_rep_id = 'E001'`, `is_deleted = 0` |

---

## 3. WBS 查詢合約 (WBS Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PRJ_W001 | 查詢專案 WBS | PM | `{"projectId":"P001"}` | `project_id = 'P001'`, `is_deleted = 0` |
| PRJ_W002 | 查詢頂層工作包 | PM | `{"projectId":"P001","parentId":null}` | `project_id = 'P001'`, `parent_id IS NULL`, `is_deleted = 0` |
| PRJ_W003 | 查詢子工作包 | PM | `{"parentId":"W001"}` | `parent_id = 'W001'`, `is_deleted = 0` |
| PRJ_W004 | 查詢進行中工作包 | PM | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'`, `is_deleted = 0` |
| PRJ_W005 | 查詢延遲工作包 | PM | `{"isDelayed":true}` | `is_delayed = 1`, `is_deleted = 0` |
| PRJ_W006 | 依負責人查詢 | PM | `{"ownerId":"E001"}` | `owner_id = 'E001'`, `is_deleted = 0` |

---

## 4. 專案成員查詢合約 (Project Member Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PRJ_M001 | 查詢專案成員 | PM | `{"projectId":"P001"}` | `project_id = 'P001'`, `is_deleted = 0` |
| PRJ_M002 | 依角色查詢成員 | PM | `{"role":"DEVELOPER"}` | `role = 'DEVELOPER'`, `is_deleted = 0` |
| PRJ_M003 | 查詢有效成員 | PM | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| PRJ_M004 | 查詢員工參與的專案 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |

---

## 5. 專案成本查詢合約 (Project Cost Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PRJ_T001 | 查詢專案成本 | PM | `{"projectId":"P001"}` | `project_id = 'P001'` |
| PRJ_T002 | 依成本類型查詢 | PM | `{"costType":"LABOR"}` | `cost_type = 'LABOR'` |
| PRJ_T003 | 依月份查詢成本 | PM | `{"yearMonth":"2025-01"}` | `year_month = '2025-01'` |
| PRJ_T004 | 查詢超預算項目 | PM | `{"isOverBudget":true}` | `actual_amount > budget_amount` |

---

## 補充說明

### 通用安全規則

1. **軟刪除過濾**: 主檔查詢須包含 `is_deleted = 0`
2. **專案參與權限**: 員工只能查詢自己參與的專案
3. **成本資料權限**: 只有 PM 和財務可查詢成本資料

### 專案狀態代碼

| 代碼 | 說明 |
|:---|:---|
| PLANNING | 規劃中 |
| IN_PROGRESS | 進行中 |
| ON_HOLD | 暫停 |
| COMPLETED | 已完成 |
| CANCELLED | 已取消 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| PM | 負責的專案 + 部門專案 | 可查詢成本資料 |
| HR | 全部專案基本資訊 | 不可查詢成本 |
| EMPLOYEE | 參與的專案 | 僅基本資訊 |
