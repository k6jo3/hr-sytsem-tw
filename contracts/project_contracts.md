# HR06 專案管理服務合約

> **服務代碼:** HR06
> **服務名稱:** 專案管理服務
> **版本:** 1.1

---

## 2. Query 操作業務合約

### 2.2 客戶查詢合約 (Customer Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_C001 | 查詢有效客戶 | `status = ACTIVE`, `isDeleted = 0` |
| PRJ_C002 | 依名稱模糊查詢 | `customerName LIKE '科技'`, `isDeleted = 0` |
| PRJ_C003 | 依產業類型查詢 | `industry = IT`, `isDeleted = 0` |
| PRJ_C004 | 查詢有專案的客戶 | `projectCount > 0`, `isDeleted = 0` |
| PRJ_C005 | 依負責業務查詢 | `salesRepId = E001`, `isDeleted = 0` |

### 2.1 專案查詢合約 (Project Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_P001 | 查詢進行中專案 | `status = IN_PROGRESS`, `is_deleted = 0` |
| PRJ_P002 | 查詢已完成專案 | `status = COMPLETED`, `is_deleted = 0` |
| PRJ_P003 | 依客戶查詢專案 | `customer_id = C001`, `is_deleted = 0` |
| PRJ_P004 | 依 PM 查詢專案 | `pm_id = E001`, `is_deleted = 0` |
| PRJ_P005 | 依名稱模糊查詢 | `(name LIKE '系統' OR code LIKE '系統')`, `is_deleted = 0` |
| PRJ_P006 | 查詢延遲專案 | `is_delayed = 1`, `is_deleted = 0` |
| PRJ_P007 | 員工查詢參與專案 | `team_members.employee_id = '{currentUserId}'`, `is_deleted = 0` |
| PRJ_P008 | 依部門查詢專案 | `department_id = D001`, `is_deleted = 0` |
| PRJ_P009 | 查詢預算超支專案 | `actual_cost > 'budget'`, `is_deleted = 0` |
| PRJ_P010 | 依日期範圍查詢 | `start_date >= '2025-01-01'`, `is_deleted = 0` |

### 2.3 WBS 查詢合約 (WBS Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_W001 | 查詢專案 WBS | `project_id = P001`, `is_deleted = 0` |
| PRJ_W002 | 查詢頂層工作包 | `project_id = P001`, `parent_id IS NULL`, `is_deleted = 0` |
| PRJ_W003 | 查詢子工作包 | `parent_id = W001`, `is_deleted = 0` |
| PRJ_W004 | 查詢進行中工作包 | `status = IN_PROGRESS`, `is_deleted = 0` |
| PRJ_W005 | 查詢延遲工作包 | `is_delayed = 1`, `is_deleted = 0` |
| PRJ_W006 | 依負責人查詢 | `owner_id = E001`, `is_deleted = 0` |

### 2.4 專案成員查詢合約 (Project Member Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_M001 | 查詢專案成員 | `project_id = P001`, `is_deleted = 0` |
| PRJ_M002 | 依角色查詢成員 | `role = DEVELOPER`, `is_deleted = 0` |
| PRJ_M003 | 查詢有效成員 | `status = ACTIVE`, `is_deleted = 0` |
| PRJ_M004 | 查詢員工參與的專案 | `employee_id = {currentUserId}`, `is_deleted = 0` |

### 2.5 專案成本查詢合約 (Project Cost Query Contract)

| 場景 ID | 說明 | 查詢條件 (Filters) |
|:---|:---|:---|
| PRJ_T001 | 查詢專案成本 | `project_id = P001`, `is_deleted = 0` |
| PRJ_T002 | 依成本類型查詢 | `cost_type = LABOR`, `is_deleted = 0` |
| PRJ_T003 | 依月份查詢成本 | `year_month = 2025-01`, `is_deleted = 0` |
| PRJ_T004 | 查詢超預算項目 | `actual_amount > 'budget_amount'`, `is_deleted = 0` |
