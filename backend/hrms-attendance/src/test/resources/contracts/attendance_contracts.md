# 考勤管理服務業務合約 (Attendance Service Contract)

> **服務代碼:** 03
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義考勤管理服務的業務合約，涵蓋出勤打卡、請假申請、加班申請等查詢場景。

---

## 1. 出勤紀錄查詢合約 (Attendance Record Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ATT_A001 | 查詢員工當日出勤 | HR | `{"employeeId":"E001","date":"2025-01-15"}` | `employee_id = 'E001'`, `attendance_date = '2025-01-15'` |
| ATT_A002 | 查詢部門月出勤 | HR | `{"deptId":"D001","month":"2025-01"}` | `department_id = 'D001'`, `attendance_date >= '2025-01-01'`, `attendance_date <= '2025-01-31'` |
| ATT_A003 | 查詢異常出勤 | HR | `{"status":"ABNORMAL"}` | `status = 'ABNORMAL'` |
| ATT_A004 | 查詢遲到紀錄 | HR | `{"lateFlag":true}` | `late_flag = 1` |
| ATT_A005 | 查詢早退紀錄 | HR | `{"earlyLeaveFlag":true}` | `early_leave_flag = 1` |
| ATT_A006 | 員工查詢自己出勤 | EMPLOYEE | `{"month":"2025-01"}` | `employee_id = '{currentUserId}'`, `attendance_date >= '2025-01-01'`, `attendance_date <= '2025-01-31'` |
| ATT_A007 | 主管查詢下屬出勤 | MANAGER | `{"month":"2025-01"}` | `department_id IN ('{managedDeptIds}')`, `attendance_date >= '2025-01-01'`, `attendance_date <= '2025-01-31'` |

---

## 2. 請假申請查詢合約 (Leave Request Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ATT_L001 | 查詢待審核請假 | HR | `{"status":"PENDING"}` | `status = 'PENDING'`, `is_deleted = 0` |
| ATT_L002 | 查詢已核准請假 | HR | `{"status":"APPROVED"}` | `status = 'APPROVED'`, `is_deleted = 0` |
| ATT_L003 | 查詢已駁回請假 | HR | `{"status":"REJECTED"}` | `status = 'REJECTED'`, `is_deleted = 0` |
| ATT_L004 | 依請假類型查詢 | HR | `{"leaveType":"ANNUAL"}` | `leave_type = 'ANNUAL'`, `is_deleted = 0` |
| ATT_L005 | 依員工查詢請假 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| ATT_L006 | 依日期範圍查詢 | HR | `{"startDate":"2025-01-01","endDate":"2025-01-31"}` | `start_date <= '2025-01-31'`, `end_date >= '2025-01-01'`, `is_deleted = 0` |
| ATT_L007 | 員工查詢自己請假 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |
| ATT_L008 | 主管查詢待審核請假 | MANAGER | `{"status":"PENDING"}` | `status = 'PENDING'`, `department_id IN ('{managedDeptIds}')`, `is_deleted = 0` |
| ATT_L009 | 查詢病假紀錄 | HR | `{"leaveType":"SICK"}` | `leave_type = 'SICK'`, `is_deleted = 0` |
| ATT_L010 | 查詢特休假紀錄 | HR | `{"leaveType":"ANNUAL"}` | `leave_type = 'ANNUAL'`, `is_deleted = 0` |

---

## 3. 加班申請查詢合約 (Overtime Request Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ATT_O001 | 查詢待審核加班 | HR | `{"status":"PENDING"}` | `status = 'PENDING'`, `is_deleted = 0` |
| ATT_O002 | 查詢已核准加班 | HR | `{"status":"APPROVED"}` | `status = 'APPROVED'`, `is_deleted = 0` |
| ATT_O003 | 依員工查詢加班 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| ATT_O004 | 依加班類型查詢 | HR | `{"overtimeType":"WORKDAY"}` | `overtime_type = 'WORKDAY'`, `is_deleted = 0` |
| ATT_O005 | 查詢假日加班 | HR | `{"overtimeType":"HOLIDAY"}` | `overtime_type = 'HOLIDAY'`, `is_deleted = 0` |
| ATT_O006 | 員工查詢自己加班 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |
| ATT_O007 | 主管查詢待審核加班 | MANAGER | `{"status":"PENDING"}` | `status = 'PENDING'`, `department_id IN ('{managedDeptIds}')`, `is_deleted = 0` |
| ATT_O008 | 依日期範圍查詢加班 | HR | `{"startDate":"2025-01-01"}` | `overtime_date >= '2025-01-01'`, `is_deleted = 0` |

---

## 4. 假別餘額查詢合約 (Leave Balance Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ATT_B001 | 查詢員工假別餘額 | HR | `{"employeeId":"E001","year":"2025"}` | `employee_id = 'E001'`, `year = 2025` |
| ATT_B002 | 查詢特定假別餘額 | HR | `{"leaveType":"ANNUAL","year":"2025"}` | `leave_type = 'ANNUAL'`, `year = 2025` |
| ATT_B003 | 員工查詢自己餘額 | EMPLOYEE | `{"year":"2025"}` | `employee_id = '{currentUserId}'`, `year = 2025` |
| ATT_B004 | 查詢部門假別餘額 | HR | `{"deptId":"D001","year":"2025"}` | `department_id = 'D001'`, `year = 2025` |

---

## 5. 班別查詢合約 (Shift Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ATT_S001 | 查詢啟用中班別 | HR | `{"isActive":true}` | `is_active = 1`, `is_deleted = 0` |
| ATT_S002 | 查詢停用班別 | HR | `{"isActive":false}` | `is_active = 0`, `is_deleted = 0` |
| ATT_S003 | 依組織查詢班別 | HR | `{"organizationId":"ORG001"}` | `organization_id = 'ORG001'`, `is_deleted = 0` |
| ATT_S004 | 依班別類型查詢 | HR | `{"shiftType":"NORMAL"}` | `shift_type = 'NORMAL'`, `is_deleted = 0` |
| ATT_S005 | 查詢彈性班別 | HR | `{"shiftType":"FLEXIBLE"}` | `shift_type = 'FLEXIBLE'`, `is_deleted = 0` |

---

## 6. 假別查詢合約 (Leave Type Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ATT_T001 | 查詢啟用中假別 | HR | `{"isActive":true}` | `is_active = 1`, `is_deleted = 0` |
| ATT_T002 | 查詢支薪假別 | HR | `{"isPaid":true}` | `is_paid = 1`, `is_deleted = 0` |
| ATT_T003 | 查詢無薪假別 | HR | `{"isPaid":false}` | `is_paid = 0`, `is_deleted = 0` |
| ATT_T004 | 依組織查詢假別 | HR | `{"organizationId":"ORG001"}` | `organization_id = 'ORG001'`, `is_deleted = 0` |

---

## 7. 補卡申請查詢合約 (Correction Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ATT_C001 | 查詢待審核補卡 | HR | `{"status":"PENDING"}` | `status = 'PENDING'`, `is_deleted = 0` |
| ATT_C002 | 查詢已核准補卡 | HR | `{"status":"APPROVED"}` | `status = 'APPROVED'`, `is_deleted = 0` |
| ATT_C003 | 依員工查詢補卡 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| ATT_C004 | 依日期範圍查詢補卡 | HR | `{"startDate":"2025-01-01","endDate":"2025-01-31"}` | `correction_date >= '2025-01-01'`, `correction_date <= '2025-01-31'`, `is_deleted = 0` |
| ATT_C005 | 員工查詢自己補卡 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |

---

## 8. 報表查詢合約 (Report Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| ATT_R001 | 查詢月報表 | HR | `{"organizationId":"ORG001","year":2025,"month":1}` | `organization_id = 'ORG001'`, `attendance_date >= '2025-01-01'`, `attendance_date <= '2025-01-31'` |
| ATT_R002 | 查詢部門月報表 | HR | `{"organizationId":"ORG001","year":2025,"month":1,"departmentId":"D001"}` | `organization_id = 'ORG001'`, `department_id = 'D001'`, `attendance_date >= '2025-01-01'`, `attendance_date <= '2025-01-31'` |
| ATT_R003 | 查詢日報表 | HR | `{"organizationId":"ORG001","date":"2025-01-15"}` | `organization_id = 'ORG001'`, `attendance_date = '2025-01-15'` |
| ATT_R004 | 查詢部門日報表 | HR | `{"organizationId":"ORG001","date":"2025-01-15","departmentId":"D001"}` | `organization_id = 'ORG001'`, `department_id = 'D001'`, `attendance_date = '2025-01-15'` |

---

## 9. 出勤命令操作合約 (Attendance Command Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須驗證項目 (Assertions) |
| :--- | :--- | :--- | :--- | :--- |
| ATT_CMD_001 | 員工打卡上班 | EMPLOYEE | `{"employeeId":"{currentUserId}","clockType":"IN"}` | 應建立出勤記錄, clock_in_time 不為空 |
| ATT_CMD_002 | 員工打卡下班 | EMPLOYEE | `{"employeeId":"{currentUserId}","clockType":"OUT"}` | 應更新出勤記錄, clock_out_time 不為空 |
| ATT_CMD_003 | 員工申請請假 | EMPLOYEE | `{"employeeId":"{currentUserId}","leaveType":"ANNUAL","startDate":"2025-02-01","endDate":"2025-02-03"}` | 應建立請假申請, status = 'PENDING' |
| ATT_CMD_004 | 主管核准請假 | MANAGER | `{"applicationId":"LA001","action":"APPROVE"}` | 應更新申請, status = 'APPROVED', approver_id 不為空 |
| ATT_CMD_005 | 主管駁回請假 | MANAGER | `{"applicationId":"LA001","action":"REJECT","reason":"人力不足"}` | 應更新申請, status = 'REJECTED', reject_reason 不為空 |
| ATT_CMD_006 | 員工申請加班 | EMPLOYEE | `{"employeeId":"{currentUserId}","overtimeType":"WORKDAY","date":"2025-02-01","hours":2}` | 應建立加班申請, status = 'PENDING' |
| ATT_CMD_007 | 員工申請補卡 | EMPLOYEE | `{"employeeId":"{currentUserId}","correctionDate":"2025-01-15","correctionType":"IN","correctionTime":"09:00"}` | 應建立補卡申請, status = 'PENDING' |

---

## 10. 權限邊界合約 (Permission Boundary Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 預期結果 |
| :--- | :--- | :--- | :--- | :--- |
| ATT_SEC_001 | 員工查詢他人出勤 | EMPLOYEE | `{"employeeId":"E999"}` | HTTP 403 Forbidden |
| ATT_SEC_002 | 員工代他人打卡 | EMPLOYEE | `{"employeeId":"E999","clockType":"IN"}` | HTTP 403 Forbidden |
| ATT_SEC_003 | 主管查詢非管轄部門 | MANAGER | `{"departmentId":"D999"}` | HTTP 403 Forbidden |
| ATT_SEC_004 | 主管核准非下屬申請 | MANAGER | `{"applicationId":"LA999","action":"APPROVE"}` | HTTP 403 Forbidden |
| ATT_SEC_005 | 未授權存取 | ANONYMOUS | 任意 API | HTTP 401 Unauthorized |

---

## 補充說明

### 通用安全規則

1. **軟刪除過濾**: 申請類查詢須包含 `is_deleted = 0`
2. **部門隔離**: 主管只能查詢所管轄部門
3. **個人資料保護**: 員工只能查詢自己的考勤資料

### 請假類型代碼

| 代碼 | 說明 |
|:---|:---|
| ANNUAL | 特休假 |
| SICK | 病假 |
| PERSONAL | 事假 |
| MARRIAGE | 婚假 |
| MATERNITY | 產假 |
| PATERNITY | 陪產假 |
| FUNERAL | 喪假 |
| COMPENSATORY | 補休 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全公司 | 無限制 |
| MANAGER | 所管轄部門 | 可審核下屬申請 |
| EMPLOYEE | 僅自己 | 只能查詢/申請自己的 |
