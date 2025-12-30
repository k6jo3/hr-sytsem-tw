# 組織員工服務業務合約 (Organization Service Contract)

> **服務代碼:** 02
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義組織員工服務的業務合約，涵蓋員工管理、部門結構、職位等查詢場景。

---

## 1. 員工查詢合約 (Employee Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ORG_E001 | 查詢在職員工 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| ORG_E002 | 查詢離職員工 | HR | `{"status":"RESIGNED"}` | `status = 'RESIGNED'`, `is_deleted = 0` |
| ORG_E003 | 依部門查詢員工 | HR | `{"deptId":"D001"}` | `department_id = 'D001'`, `is_deleted = 0` |
| ORG_E004 | 依姓名模糊查詢 | HR | `{"name":"王"}` | `name LIKE '王'`, `is_deleted = 0` |
| ORG_E005 | 依工號查詢 | HR | `{"employeeNo":"EMP001"}` | `employee_no = 'EMP001'`, `is_deleted = 0` |
| ORG_E006 | 依職位查詢 | HR | `{"positionId":"P001"}` | `position_id = 'P001'`, `is_deleted = 0` |
| ORG_E007 | 查詢試用期員工 | HR | `{"employmentType":"PROBATION"}` | `employment_type = 'PROBATION'`, `is_deleted = 0` |
| ORG_E008 | 查詢正式員工 | HR | `{"employmentType":"REGULAR"}` | `employment_type = 'REGULAR'`, `is_deleted = 0` |
| ORG_E009 | 主管查詢下屬 | MANAGER | `{}` | `department_id IN ('{managedDeptIds}')`, `is_deleted = 0` |
| ORG_E010 | 員工查詢同部門 | EMPLOYEE | `{}` | `department_id = '{currentUserDeptId}'`, `status = 'ACTIVE'`, `is_deleted = 0` |
| ORG_E011 | 依到職日期範圍查詢 | HR | `{"hireStartDate":"2025-01-01"}` | `hire_date >= '2025-01-01'`, `is_deleted = 0` |
| ORG_E012 | 查詢留職停薪員工 | HR | `{"status":"ON_LEAVE"}` | `status = 'ON_LEAVE'`, `is_deleted = 0` |

---

## 2. 部門查詢合約 (Department Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ORG_D001 | 查詢所有啟用部門 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| ORG_D002 | 查詢頂層部門 | HR | `{"parentId":null}` | `parent_id IS NULL`, `is_deleted = 0` |
| ORG_D003 | 查詢子部門 | HR | `{"parentId":"D001"}` | `parent_id = 'D001'`, `is_deleted = 0` |
| ORG_D004 | 依名稱模糊查詢 | HR | `{"name":"研發"}` | `name LIKE '研發'`, `is_deleted = 0` |
| ORG_D005 | 依部門代碼查詢 | HR | `{"code":"RD"}` | `code = 'RD'`, `is_deleted = 0` |
| ORG_D006 | 查詢已停用部門 | ADMIN | `{"status":"INACTIVE"}` | `status = 'INACTIVE'`, `is_deleted = 0` |

---

## 3. 職位查詢合約 (Position Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ORG_P001 | 查詢所有啟用職位 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| ORG_P002 | 依部門查詢職位 | HR | `{"deptId":"D001"}` | `department_id = 'D001'`, `is_deleted = 0` |
| ORG_P003 | 依職等查詢 | HR | `{"grade":"M1"}` | `grade = 'M1'`, `is_deleted = 0` |
| ORG_P004 | 依名稱模糊查詢 | HR | `{"name":"工程師"}` | `name LIKE '工程師'`, `is_deleted = 0` |

---

## 4. 組織異動紀錄查詢合約 (Organization Change Log Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ORG_L001 | 查詢員工異動紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| ORG_L002 | 依異動類型查詢 | HR | `{"changeType":"TRANSFER"}` | `change_type = 'TRANSFER'` |
| ORG_L003 | 依生效日期查詢 | HR | `{"effectiveDate":"2025-01-01"}` | `effective_date = '2025-01-01'` |
| ORG_L004 | 依部門查詢異動紀錄 | HR | `{"deptId":"D001"}` | `department_id = 'D001'` |

---

## 補充說明

### 通用安全規則

1. **所有查詢都必須包含 `is_deleted = 0`** (異動紀錄除外)
2. **部門層級權限**: 主管只能查詢所管轄部門
3. **敏感資料遮蔽**: 員工查詢同部門時，薪資等敏感欄位應遮蔽

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全公司 | 可查看完整資訊 |
| MANAGER | 所管轄部門 | 可查看下屬完整資訊 |
| EMPLOYEE | 自己 + 同部門基本資訊 | 敏感資料遮蔽 |
