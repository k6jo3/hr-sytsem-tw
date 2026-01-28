# 薪資管理服務業務合約 (Payroll Service Contract)

> **服務代碼:** 04
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義薪資管理服務的業務合約，涵蓋薪資結構、薪資單、獎金等查詢場景。
**注意**: 薪資資料屬高度敏感資料，需嚴格控管存取權限。

---

## 1. 薪資結構查詢合約 (Salary Structure Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PAY_S001 | 查詢員工薪資結構 | HR_PAYROLL | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PAY_S002 | 查詢有效薪資結構 | HR_PAYROLL | `{"isActive":true}` | `active = 'true'`, `is_deleted = 0` |
| PAY_S003 | 查詢月薪制結構 | HR_PAYROLL | `{"payrollSystem":"MONTHLY"}` | `payroll_system = 'MONTHLY'`, `is_deleted = 0` |
| PAY_S004 | 查詢時薪制結構 | HR_PAYROLL | `{"payrollSystem":"HOURLY"}` | `payroll_system = 'HOURLY'`, `is_deleted = 0` |
| PAY_S005 | 組合條件查詢 | HR_PAYROLL | `{"employeeId":"E001","isActive":true}` | `employee_id = 'E001'`, `active = 'true'`, `is_deleted = 0` |

---

## 2. 薪資單查詢合約 (Payslip Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PAY_P001 | 查詢批次下薪資單 | HR_PAYROLL | `{"runId":"RUN001"}` | `payroll_run_id = 'RUN001'`, `is_deleted = 0` |
| PAY_P002 | 員工查詢自己薪資單 | EMPLOYEE | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PAY_P003 | HR 查詢特定員工薪資單 | HR_PAYROLL | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PAY_P004 | 組合條件查詢薪資單 | HR_PAYROLL | `{"runId":"RUN001","employeeId":"E001"}` | `payroll_run_id = 'RUN001'`, `employee_id = 'E001'`, `is_deleted = 0` |
| PAY_P005 | 員工查詢自己薪資單 | EMPLOYEE | `{"yearMonth":"2025-01"}` | `employee_id = '{currentUserId}'`, `year_month = '2025-01'`, `is_deleted = 0` |
| PAY_P006 | 員工查詢歷史薪資單 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `status = 'PAID'`, `is_deleted = 0` |
| PAY_P007 | 依發放日期查詢 | HR_PAYROLL | `{"payDate":"2025-01-05"}` | `pay_date = '2025-01-05'`, `is_deleted = 0` |

---

## 3. 獎金查詢合約 (Bonus Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PAY_B001 | 查詢員工獎金 | HR_PAYROLL | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PAY_B002 | 依獎金類型查詢 | HR_PAYROLL | `{"bonusType":"PERFORMANCE"}` | `bonus_type = 'PERFORMANCE'`, `is_deleted = 0` |
| PAY_B003 | 查詢年終獎金 | HR_PAYROLL | `{"bonusType":"YEAR_END","year":"2025"}` | `bonus_type = 'YEAR_END'`, `year = 2025`, `is_deleted = 0` |
| PAY_B004 | 依發放狀態查詢 | HR_PAYROLL | `{"status":"PAID"}` | `status = 'PAID'`, `is_deleted = 0` |
| PAY_B005 | 員工查詢自己獎金 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |

---

## 4. 扣款項目查詢合約 (Deduction Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PAY_D001 | 查詢員工扣款項目 | HR_PAYROLL | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PAY_D002 | 依扣款類型查詢 | HR_PAYROLL | `{"deductionType":"LOAN"}` | `deduction_type = 'LOAN'`, `is_deleted = 0` |
| PAY_D003 | 查詢進行中的扣款 | HR_PAYROLL | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| PAY_D004 | 查詢已結清的扣款 | HR_PAYROLL | `{"status":"COMPLETED"}` | `status = 'COMPLETED'`, `is_deleted = 0` |

---

## 5. 加班費計算查詢合約 (Overtime Pay Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PAY_O001 | 查詢員工加班費 | HR_PAYROLL | `{"employeeId":"E001","yearMonth":"2025-01"}` | `employee_id = 'E001'`, `year_month = '2025-01'` |
| PAY_O002 | 查詢部門加班費 | HR_PAYROLL | `{"deptId":"D001","yearMonth":"2025-01"}` | `department_id = 'D001'`, `year_month = '2025-01'` |
| PAY_O003 | 員工查詢自己加班費 | EMPLOYEE | `{"yearMonth":"2025-01"}` | `employee_id = '{currentUserId}'`, `year_month = '2025-01'` |

---

---

## 6. 薪資批次查詢合約 (Payroll Run Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PAY_R001 | HR 查詢特定組織薪資批次 | HR_PAYROLL | `{"organizationId":"ORG001"}` | `organization_id = 'ORG001'`, `is_deleted = 0` |
| PAY_R002 | HR 查詢特定狀態薪資批次 | HR_PAYROLL | `{"status":"PENDING_APPROVAL"}` | `status = 'PENDING_APPROVAL'`, `is_deleted = 0` |
| PAY_R003 | HR 查詢日期範圍內批次 | HR_PAYROLL | `{"startDate":"2025-01-01","endDate":"2025-01-31"}` | `pay_period_start >= '2025-01-01'`, `pay_period_end <= '2025-01-31'`, `is_deleted = 0` |
| PAY_R004 | 查詢草稿狀態批次 | HR_PAYROLL | `{"status":"DRAFT"}` | `status = 'DRAFT'`, `is_deleted = 0` |
| PAY_R005 | 查詢已核准批次 | HR_PAYROLL | `{"status":"APPROVED"}` | `status = 'APPROVED'`, `is_deleted = 0` |
| PAY_R006 | 查詢已發薪批次 | HR_PAYROLL | `{"status":"PAID"}` | `status = 'PAID'`, `is_deleted = 0` |

---

## 補充說明

### 通用安全規則

1. **高度敏感資料**: 薪資資料需嚴格控管，只有 HR_PAYROLL 可查詢他人
2. **軟刪除過濾**: 結構性資料須包含 `is_deleted = 0`
3. **個人資料保護**: 員工只能查詢自己的薪資資料
4. **稽核軌跡**: 所有薪資查詢需記錄稽核日誌

### 獎金類型代碼

| 代碼 | 說明 |
|:---|:---|
| PERFORMANCE | 績效獎金 |
| YEAR_END | 年終獎金 |
| PROJECT | 專案獎金 |
| REFERRAL | 推薦獎金 |
| OTHER | 其他獎金 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR_PAYROLL | 全公司薪資資料 | 需記錄稽核日誌 |
| HR | 基本薪資結構 | 不可查詢薪資明細 |
| MANAGER | 無 | 不可查詢下屬薪資 |
| EMPLOYEE | 僅自己 | 只能查詢自己的薪資資料 |
