# HR04 薪資管理服務合約規格

本文件定義 HR04 薪資管理服務的 API 合約，作為合約驅動測試的依據。

---

## 1. 薪資批次查詢合約 (PayrollRun Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_R001 | HR 查詢特定組織薪資批次 | HR | `{"organizationId":"ORG001"}` | `organization_id = 'ORG001'`, `is_deleted = 0` |
| PAY_R002 | HR 查詢特定狀態薪資批次 | HR | `{"status":"PENDING_APPROVAL"}` | `status = 'PENDING_APPROVAL'`, `is_deleted = 0` |
| PAY_R003 | HR 查詢日期範圍內批次 | HR | `{"startDate":"2025-01-01","endDate":"2025-01-31"}` | `pay_period_start >= '2025-01-01'`, `pay_period_end <= '2025-01-31'`, `is_deleted = 0` |
| PAY_R004 | 查詢草稿狀態批次 | HR | `{"status":"DRAFT"}` | `status = 'DRAFT'`, `is_deleted = 0` |
| PAY_R005 | 查詢已核准批次 | HR | `{"status":"APPROVED"}` | `status = 'APPROVED'`, `is_deleted = 0` |
| PAY_R006 | 查詢已發薪批次 | HR | `{"status":"PAID"}` | `status = 'PAID'`, `is_deleted = 0` |

---

## 2. 薪資單查詢合約 (Payslip Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_P001 | HR 查詢批次下薪資單 | HR | `{"runId":"RUN001"}` | `payroll_run_id = 'RUN001'`, `is_deleted = 0` |
| PAY_P002 | 員工查詢自己薪資單 | EMPLOYEE | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PAY_P003 | HR 查詢特定員工薪資單 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PAY_P004 | 組合條件查詢薪資單 | HR | `{"runId":"RUN001","employeeId":"E001"}` | `payroll_run_id = 'RUN001'`, `employee_id = 'E001'`, `is_deleted = 0` |

---

## 3. 薪資結構查詢合約 (SalaryStructure Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_S001 | HR 查詢員工薪資結構 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| PAY_S002 | HR 查詢有效薪資結構 | HR | `{"active":true}` | `active = true`, `is_deleted = 0` |
| PAY_S003 | HR 查詢月薪制結構 | HR | `{"payrollSystem":"MONTHLY"}` | `payroll_system = 'MONTHLY'`, `is_deleted = 0` |
| PAY_S004 | HR 查詢時薪制結構 | HR | `{"payrollSystem":"HOURLY"}` | `payroll_system = 'HOURLY'`, `is_deleted = 0` |
| PAY_S005 | 組合條件查詢 | HR | `{"employeeId":"E001","active":true}` | `employee_id = 'E001'`, `active = true`, `is_deleted = 0` |

---

## 4. 薪資批次命令合約 (PayrollRun Command Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 預期結果 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_CMD_001 | 建立薪資批次 | HR | `{"organizationId":"ORG001","periodType":"MONTHLY","periodStart":"2025-01-01","periodEnd":"2025-01-31"}` | 回傳 runId, status = 'DRAFT' |
| PAY_CMD_002 | 執行薪資計算 | HR | `{"runId":"RUN001"}` | 回傳 status = 'CALCULATED' |
| PAY_CMD_003 | 送審薪資批次 | HR | `{"runId":"RUN001"}` | 回傳 status = 'PENDING_APPROVAL' |
| PAY_CMD_004 | 核准薪資批次 | MANAGER | `{"runId":"RUN001"}` | 回傳 status = 'APPROVED' |
| PAY_CMD_005 | 退回薪資批次 | MANAGER | `{"runId":"RUN001","reason":"數據有誤"}` | 回傳 status = 'REJECTED' |
| PAY_CMD_006 | 標記已發薪 | HR | `{"runId":"RUN001"}` | 回傳 status = 'PAID' |

---

## 5. 薪資結構命令合約 (SalaryStructure Command Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 預期結果 |
| :--- | :--- | :--- | :--- | :--- |
| PAY_CMD_007 | 建立薪資結構 | HR | `{"employeeId":"E001","baseSalary":50000,"effectiveDate":"2025-01-01"}` | 回傳 structureId, active = true |
| PAY_CMD_008 | 更新薪資結構 | HR | `{"structureId":"S001","baseSalary":55000}` | 回傳 success = true |
| PAY_CMD_009 | 刪除薪資結構 | HR | `{"structureId":"S001"}` | 回傳 success = true |

---

## 6. 安全規則 (Security Rules)

| 場景 ID | 測試描述 | 驗證條件 |
| :--- | :--- | :--- |
| PAY_SEC_001 | 員工只能查詢自己的薪資單 | employee_id = currentUserId |
| PAY_SEC_002 | 所有查詢必須排除已刪除記錄 | is_deleted = 0 |
| PAY_SEC_003 | 只有 HR 可以建立薪資批次 | role = 'HR' |
| PAY_SEC_004 | 只有主管/HR 可以核准批次 | role IN ('MANAGER', 'HR') |
