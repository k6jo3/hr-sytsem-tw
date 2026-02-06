# 考勤管理服務業務合約 (Attendance Service Contract)

> **服務代碼:** HR03
> **版本:** 2.0
> **建立日期:** 2026-02-06
> **維護者:** SA Team
> **變更說明:** 移除不存在的 is_deleted 欄位，新增 API 端點、Command 操作、Domain Events，採用雙層結構設計

---

## 📋 目錄

1. [合約概述](#合約概述)
2. [查詢操作合約 (Query Contracts)](#查詢操作合約-query-contracts)
   - [2.1 出勤紀錄查詢](#21-出勤紀錄查詢)
   - [2.2 請假申請查詢](#22-請假申請查詢)
   - [2.3 加班申請查詢](#23-加班申請查詢)
   - [2.4 假別餘額查詢](#24-假別餘額查詢)
   - [2.5 補卡申請查詢](#25-補卡申請查詢)
   - [2.6 假別設定查詢](#26-假別設定查詢)
   - [2.7 班表查詢](#27-班表查詢)
   - [2.8 報表查詢](#28-報表查詢)
3. [命令操作合約 (Command Contracts)](#命令操作合約-command-contracts)
   - [3.1 打卡操作](#31-打卡操作)
   - [3.2 請假操作](#32-請假操作)
   - [3.3 加班操作](#33-加班操作)
   - [3.4 補卡操作](#34-補卡操作)
4. [Domain Events 定義](#domain-events-定義)
5. [補充說明](#補充說明)

---

## 合約概述

### 服務定位
考勤管理服務負責員工的出勤管理、假勤申請與審核，以及加班管理。本服務必須確保**符合台灣勞動基準法**的所有規定。

### 資料軟刪除策略

**⚠️ 重要：本服務不使用 `is_deleted` 欄位進行軟刪除**

- **申請類資料** (請假、加班、補卡): 使用 `status` 欄位，CANCELLED 代表已取消
- **設定類資料** (班別、假別): 使用 `is_active` 欄位，FALSE 代表已停用
- **出勤紀錄**: 不進行軟刪除，保留所有歷史記錄

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊權限 |
|:---|:---|:---|
| `HR` | 全公司 | 可審核、可結算月報 |
| `MANAGER` | 所管轄部門 | 可審核下屬申請 |
| `EMPLOYEE` | 僅自己 | 可申請請假/加班/補卡 |

### 假別類型代碼

| 代碼 | 說明 | 是否支薪 | 支薪比例 |
|:---|:---|:---:|:---:|
| `ANNUAL` | 特休假 | ✅ | 100% |
| `SICK` | 病假 | ⚠️ | 50% |
| `PERSONAL` | 事假 | ❌ | 0% |
| `MARRIAGE` | 婚假 | ✅ | 100% |
| `MATERNITY` | 產假 | ✅ | 100% |
| `PATERNITY` | 陪產假 | ✅ | 100% |
| `BEREAVEMENT` | 喪假 | ✅ | 100% |
| `MENSTRUAL` | 生理假 | ⚠️ | 50% |
| `PARENTAL` | 育嬰留停 | ❌ | 0% |
| `COMP_TIME` | 補休 | ✅ | 100% |

---

## 查詢操作合約 (Query Contracts)

### 2.1 出勤紀錄查詢

#### 2.1.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_QRY_A001 | 查詢員工當日出勤 | HR | `GET /api/v1/attendance/records` | `{"employeeId":"E001","date":"2025-01-15"}` | `employee_id = 'E001'`, `record_date = '2025-01-15'` |
| ATT_QRY_A002 | 查詢部門月出勤 | HR | `GET /api/v1/attendance/records` | `{"deptId":"D001","month":"2025-01"}` | `department_id = 'D001'`, `record_date >= '2025-01-01'`, `record_date <= '2025-01-31'` |
| ATT_QRY_A003 | 查詢異常出勤 | HR | `GET /api/v1/attendance/records` | `{"hasAnomaly":true}` | `anomaly_type IS NOT NULL` |
| ATT_QRY_A004 | 查詢遲到紀錄 | HR | `GET /api/v1/attendance/records` | `{"isLate":true}` | `is_late = TRUE` |
| ATT_QRY_A005 | 查詢早退紀錄 | HR | `GET /api/v1/attendance/records` | `{"isEarlyLeave":true}` | `is_early_leave = TRUE` |
| ATT_QRY_A006 | 員工查詢自己出勤 | EMPLOYEE | `GET /api/v1/attendance/my-records` | `{"month":"2025-01"}` | `employee_id = '{currentUserId}'`, `record_date >= '2025-01-01'`, `record_date <= '2025-01-31'` |
| ATT_QRY_A007 | 主管查詢下屬出勤 | MANAGER | `GET /api/v1/attendance/records` | `{"month":"2025-01"}` | `department_id IN ('{managedDeptIds}')`, `record_date >= '2025-01-01'`, `record_date <= '2025-01-31'` |

#### 2.1.2 詳細業務描述

**場景 ATT_QRY_A001: 查詢員工當日出勤**

- **業務規則:**
  1. HR 可查詢任意員工的出勤記錄
  2. 返回指定日期的打卡記錄（含上班、下班時間）
  3. 包含遲到、早退、異常等標記

- **權限檢查:**
  - 需要 `attendance:view` 權限
  - HR 角色不受部門限制

- **測試範例:**
```java
@Test
@DisplayName("ATT_QRY_A001: 查詢員工當日出勤")
void getEmployeeDailyRecord_AsHR_ShouldFilterByEmployeeAndDate() throws Exception {
    String contractSpec = loadContractSpec("attendance");

    GetAttendanceRecordsRequest request = GetAttendanceRecordsRequest.builder()
        .employeeId("E001")
        .date(LocalDate.of(2025, 1, 15))
        .build();

    verifyApiContract("/api/v1/attendance/records", request, contractSpec, "ATT_QRY_A001");
}
```

**場景 ATT_QRY_A006: 員工查詢自己出勤**

- **業務規則:**
  1. 員工只能查詢自己的出勤記錄
  2. 自動套用 `employee_id = {currentUserId}` 過濾
  3. 支援按月份查詢

- **權限檢查:**
  - 不需要特殊權限（所有員工可用）
  - 自動限制為當前登入使用者

- **測試範例:**
```java
@Test
@DisplayName("ATT_QRY_A006: 員工查詢自己出勤")
@WithMockUser(username = "E001", roles = "EMPLOYEE")
void getMyRecords_AsEmployee_ShouldFilterByCurrentUser() throws Exception {
    String contractSpec = loadContractSpec("attendance");

    GetMyAttendanceRequest request = GetMyAttendanceRequest.builder()
        .month(YearMonth.of(2025, 1))
        .build();

    verifyApiContract("/api/v1/attendance/my-records", request, contractSpec, "ATT_QRY_A006");
}
```

---

### 2.2 請假申請查詢

#### 2.2.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_QRY_L001 | 查詢待審核請假 | HR | `GET /api/v1/leave/applications` | `{"status":"PENDING"}` | `status = 'PENDING'` |
| ATT_QRY_L002 | 查詢已核准請假 | HR | `GET /api/v1/leave/applications` | `{"status":"APPROVED"}` | `status = 'APPROVED'` |
| ATT_QRY_L003 | 查詢已駁回請假 | HR | `GET /api/v1/leave/applications` | `{"status":"REJECTED"}` | `status = 'REJECTED'` |
| ATT_QRY_L004 | 依請假類型查詢 | HR | `GET /api/v1/leave/applications` | `{"leaveType":"ANNUAL"}` | `leave_type_id = (SELECT leave_type_id FROM leave_types WHERE leave_code = 'ANNUAL')` |
| ATT_QRY_L005 | 依員工查詢請假 | HR | `GET /api/v1/leave/applications` | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| ATT_QRY_L006 | 依日期範圍查詢 | HR | `GET /api/v1/leave/applications` | `{"startDate":"2025-01-01","endDate":"2025-01-31"}` | `start_date <= '2025-01-31'`, `end_date >= '2025-01-01'` |
| ATT_QRY_L007 | 員工查詢自己請假 | EMPLOYEE | `GET /api/v1/leave/my-applications` | `{}` | `employee_id = '{currentUserId}'` |
| ATT_QRY_L008 | 主管查詢待審核請假 | MANAGER | `GET /api/v1/leave/pending-approvals` | `{"status":"PENDING"}` | `status = 'PENDING'`, `department_id IN ('{managedDeptIds}')` |
| ATT_QRY_L009 | 查詢病假紀錄 | HR | `GET /api/v1/leave/applications` | `{"leaveType":"SICK"}` | `leave_type_id = (SELECT leave_type_id FROM leave_types WHERE leave_code = 'SICK')` |
| ATT_QRY_L010 | 查詢特休假紀錄 | HR | `GET /api/v1/leave/applications` | `{"leaveType":"ANNUAL"}` | `leave_type_id = (SELECT leave_type_id FROM leave_types WHERE leave_code = 'ANNUAL')` |
| ATT_QRY_L011 | 排除已取消的請假 | HR | `GET /api/v1/leave/applications` | `{"excludeCancelled":true}` | `status != 'CANCELLED'` |

#### 2.2.2 詳細業務描述

**場景 ATT_QRY_L001: 查詢待審核請假**

- **業務規則:**
  1. 僅返回狀態為 PENDING 的請假申請
  2. HR 可查詢全公司待審核請假
  3. 按申請時間倒序排列

- **權限檢查:**
  - 需要 `leave:view` 權限
  - HR 角色可查詢所有部門

- **查詢邏輯:**
  - `status = 'PENDING'` - 只查詢待審核狀態
  - 不包含已取消（CANCELLED）的申請

**場景 ATT_QRY_L008: 主管查詢待審核請假**

- **業務規則:**
  1. 主管只能查詢所管轄部門的待審核請假
  2. 自動套用部門隔離過濾
  3. 用於主管審核頁面

- **權限檢查:**
  - 需要 `leave:approve` 權限
  - 自動限制為管轄部門範圍

- **查詢邏輯:**
  - `status = 'PENDING'` - 待審核狀態
  - `department_id IN ('{managedDeptIds}')` - 部門隔離

---

### 2.3 加班申請查詢

#### 2.3.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_QRY_O001 | 查詢待審核加班 | HR | `GET /api/v1/overtime/applications` | `{"status":"PENDING"}` | `status = 'PENDING'` |
| ATT_QRY_O002 | 查詢已核准加班 | HR | `GET /api/v1/overtime/applications` | `{"status":"APPROVED"}` | `status = 'APPROVED'` |
| ATT_QRY_O003 | 依員工查詢加班 | HR | `GET /api/v1/overtime/applications` | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| ATT_QRY_O004 | 依加班類型查詢 | HR | `GET /api/v1/overtime/applications` | `{"overtimeType":"WEEKDAY"}` | `overtime_type = 'WEEKDAY'` |
| ATT_QRY_O005 | 查詢假日加班 | HR | `GET /api/v1/overtime/applications` | `{"overtimeType":"HOLIDAY"}` | `overtime_type = 'HOLIDAY'` |
| ATT_QRY_O006 | 員工查詢自己加班 | EMPLOYEE | `GET /api/v1/overtime/my-applications` | `{}` | `employee_id = '{currentUserId}'` |
| ATT_QRY_O007 | 主管查詢待審核加班 | MANAGER | `GET /api/v1/overtime/pending-approvals` | `{"status":"PENDING"}` | `status = 'PENDING'`, `department_id IN ('{managedDeptIds}')` |
| ATT_QRY_O008 | 依日期範圍查詢加班 | HR | `GET /api/v1/overtime/applications` | `{"startDate":"2025-01-01"}` | `overtime_date >= '2025-01-01'` |
| ATT_QRY_O009 | 查詢員工月加班統計 | HR | `GET /api/v1/overtime/statistics` | `{"employeeId":"E001","month":"2025-01"}` | `employee_id = 'E001'`, `overtime_date >= '2025-01-01'`, `overtime_date <= '2025-01-31'` |
| ATT_QRY_O010 | 排除已取消的加班 | HR | `GET /api/v1/overtime/applications` | `{"excludeCancelled":true}` | `status != 'CANCELLED'` |

#### 2.3.2 詳細業務描述

**場景 ATT_QRY_O009: 查詢員工月加班統計**

- **業務規則:**
  1. 計算員工當月總加班時數
  2. 檢查是否超過勞基法上限（46 小時/月）
  3. 統計平日、休息日、假日加班時數

- **權限檢查:**
  - 需要 `overtime:view` 權限
  - HR 可查詢任意員工

- **測試範例:**
```java
@Test
@DisplayName("ATT_QRY_O009: 查詢員工月加班統計")
void getMonthlyOvertimeStatistics_AsHR_ShouldCalculateTotalHours() throws Exception {
    String contractSpec = loadContractSpec("attendance");

    GetOvertimeStatisticsRequest request = GetOvertimeStatisticsRequest.builder()
        .employeeId("E001")
        .month(YearMonth.of(2025, 1))
        .build();

    verifyApiContract("/api/v1/overtime/statistics", request, contractSpec, "ATT_QRY_O009");
}
```

---

### 2.4 假別餘額查詢

#### 2.4.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_QRY_B001 | 查詢員工假別餘額 | HR | `GET /api/v1/leave/balances` | `{"employeeId":"E001","year":"2025"}` | `employee_id = 'E001'`, `year = 2025` |
| ATT_QRY_B002 | 查詢特定假別餘額 | HR | `GET /api/v1/leave/balances` | `{"leaveType":"ANNUAL","year":"2025"}` | `leave_type_id = (SELECT leave_type_id FROM leave_types WHERE leave_code = 'ANNUAL')`, `year = 2025` |
| ATT_QRY_B003 | 員工查詢自己餘額 | EMPLOYEE | `GET /api/v1/leave/my-balances` | `{"year":"2025"}` | `employee_id = '{currentUserId}'`, `year = 2025` |
| ATT_QRY_B004 | 查詢部門假別餘額 | HR | `GET /api/v1/leave/balances` | `{"deptId":"D001","year":"2025"}` | `employee_id IN (SELECT employee_id FROM employees WHERE department_id = 'D001')`, `year = 2025` |
| ATT_QRY_B005 | 查詢即將到期特休 | HR | `GET /api/v1/leave/expiring-balances` | `{"expiryDays":30}` | `is_annual_leave = TRUE`, `expiry_date <= (CURRENT_DATE + INTERVAL '30 days')` |

#### 2.4.2 詳細業務描述

**場景 ATT_QRY_B005: 查詢即將到期特休**

- **業務規則:**
  1. 查詢未來 N 天內即將到期的特休假
  2. 只包含年度特休（is_annual_leave = TRUE）
  3. 用於到期提醒通知

- **權限檢查:**
  - 需要 `leave:view` 權限
  - 系統排程任務也會調用此 API

- **測試範例:**
```java
@Test
@DisplayName("ATT_QRY_B005: 查詢即將到期特休")
void getExpiringAnnualLeave_AsHR_ShouldFilterByExpiryDate() throws Exception {
    String contractSpec = loadContractSpec("attendance");

    GetExpiringBalancesRequest request = GetExpiringBalancesRequest.builder()
        .expiryDays(30)
        .build();

    verifyApiContract("/api/v1/leave/expiring-balances", request, contractSpec, "ATT_QRY_B005");
}
```

---

### 2.5 補卡申請查詢

#### 2.5.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_QRY_C001 | 查詢待審核補卡 | HR | `GET /api/v1/attendance/corrections` | `{"status":"PENDING"}` | `status = 'PENDING'` |
| ATT_QRY_C002 | 查詢已核准補卡 | HR | `GET /api/v1/attendance/corrections` | `{"status":"APPROVED"}` | `status = 'APPROVED'` |
| ATT_QRY_C003 | 依員工查詢補卡 | HR | `GET /api/v1/attendance/corrections` | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| ATT_QRY_C004 | 依日期範圍查詢補卡 | HR | `GET /api/v1/attendance/corrections` | `{"startDate":"2025-01-01","endDate":"2025-01-31"}` | `created_at >= '2025-01-01'`, `created_at <= '2025-01-31'` |
| ATT_QRY_C005 | 員工查詢自己補卡 | EMPLOYEE | `GET /api/v1/attendance/my-corrections` | `{}` | `employee_id = '{currentUserId}'` |
| ATT_QRY_C006 | 主管查詢待審核補卡 | MANAGER | `GET /api/v1/attendance/corrections/pending` | `{}` | `status = 'PENDING'`, `employee_id IN (SELECT employee_id FROM employees WHERE department_id IN ('{managedDeptIds}'))` |

---

### 2.6 假別設定查詢

#### 2.6.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_QRY_T001 | 查詢啟用假別 | HR | `GET /api/v1/leave/types` | `{"status":"ACTIVE"}` | `is_active = TRUE` |
| ATT_QRY_T002 | 查詢有薪假別 | HR | `GET /api/v1/leave/types` | `{"isPaid":true}` | `is_paid = TRUE`, `is_active = TRUE` |
| ATT_QRY_T003 | 查詢無薪假別 | HR | `GET /api/v1/leave/types` | `{"isPaid":false}` | `is_paid = FALSE`, `is_active = TRUE` |
| ATT_QRY_T004 | 依組織查詢假別 | HR | `GET /api/v1/leave/types` | `{"orgId":"ORG001"}` | `organization_id = 'ORG001'`, `is_active = TRUE` |
| ATT_QRY_T005 | 查詢法定假別 | HR | `GET /api/v1/leave/types` | `{"isStatutory":true}` | `is_statutory_leave = TRUE`, `is_active = TRUE` |
| ATT_QRY_T006 | 員工查詢可申請假別 | EMPLOYEE | `GET /api/v1/leave/types/available` | `{}` | `is_active = TRUE` |

#### 2.6.2 詳細業務描述

**場景 ATT_QRY_T001: 查詢啟用假別**

- **業務規則:**
  1. 僅返回啟用狀態的假別（is_active = TRUE）
  2. 不使用 is_deleted 欄位
  3. 停用的假別不會出現在查詢結果中

- **軟刪除策略:**
  - **不使用 is_deleted 欄位**
  - 使用 `is_active = TRUE` 過濾啟用的假別
  - 停用的假別仍保留在資料庫中，但不會被查詢到

---

### 2.7 班表查詢

#### 2.7.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_QRY_S001 | 查詢啟用班表 | HR | `GET /api/v1/attendance/shifts` | `{"status":"ACTIVE"}` | `is_active = TRUE` |
| ATT_QRY_S002 | 查詢停用班表 | HR | `GET /api/v1/attendance/shifts` | `{"status":"INACTIVE"}` | `is_active = FALSE` |
| ATT_QRY_S003 | 依組織查詢班表 | HR | `GET /api/v1/attendance/shifts` | `{"orgId":"ORG001"}` | `organization_id = 'ORG001'`, `is_active = TRUE` |
| ATT_QRY_S004 | 依班別類型查詢 | HR | `GET /api/v1/attendance/shifts` | `{"type":"STANDARD"}` | `shift_type = 'STANDARD'`, `is_active = TRUE` |
| ATT_QRY_S005 | 查詢彈性班表 | HR | `GET /api/v1/attendance/shifts` | `{"type":"FLEXIBLE"}` | `shift_type = 'FLEXIBLE'`, `is_active = TRUE` |

---

### 2.8 報表查詢

#### 2.8.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_QRY_R001 | 查詢月報表 | HR | `GET /api/v1/attendance/reports/monthly` | `{"month":"2025-01"}` | `record_date >= '2025-01-01'`, `record_date <= '2025-01-31'` |
| ATT_QRY_R002 | 查詢部門月報表 | HR | `GET /api/v1/attendance/reports/monthly` | `{"deptId":"D001","month":"2025-01"}` | `employee_id IN (SELECT employee_id FROM employees WHERE department_id = 'D001')`, `record_date >= '2025-01-01'`, `record_date <= '2025-01-31'` |
| ATT_QRY_R003 | 查詢日報表 | HR | `GET /api/v1/attendance/reports/daily` | `{"date":"2025-01-15"}` | `record_date = '2025-01-15'` |
| ATT_QRY_R004 | 查詢部門日報表 | HR | `GET /api/v1/attendance/reports/daily` | `{"deptId":"D001","date":"2025-01-15"}` | `employee_id IN (SELECT employee_id FROM employees WHERE department_id = 'D001')`, `record_date = '2025-01-15'` |
| ATT_QRY_R005 | 查詢員工月度統計 | HR | `GET /api/v1/attendance/reports/employee` | `{"employeeId":"E001","month":"2025-01"}` | `employee_id = 'E001'`, `record_date >= '2025-01-01'`, `record_date <= '2025-01-31'` |

---

## 命令操作合約 (Command Contracts)

### 3.1 打卡操作

#### 3.1.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_CMD_001 | 員工上班打卡 | EMPLOYEE | `POST /api/v1/attendance/check-in` | `{"latitude":25.03,"longitude":121.56}` | GPS 位置驗證, 重複打卡檢查, 遲到判定 | `AttendanceRecorded` |
| ATT_CMD_002 | 員工下班打卡 | EMPLOYEE | `POST /api/v1/attendance/check-out` | `{"latitude":25.03,"longitude":121.56}` | GPS 位置驗證, 早退判定, 工時計算 | `AttendanceRecorded` |
| ATT_CMD_003 | 打卡異常偵測 | SYSTEM | `POST /api/v1/attendance/check-in` | `{"latitude":24.99,"longitude":121.50}` | GPS 範圍檢查（超出 500 公尺） | `AttendanceAnomalyDetected` |

#### 3.1.2 詳細業務描述

**場景 ATT_CMD_001: 員工上班打卡**

- **業務規則:**
  1. 驗證 GPS 座標在允許範圍內（公司位置 ± 500 公尺）
  2. 檢查當日是否已打上班卡（避免重複）
  3. 比對班表時間，判定是否遲到
  4. 記錄 IP 位址、GPS 座標、打卡時間

- **Domain Logic:**
  ```java
  AttendanceRecord record = AttendanceRecord.checkIn(
      employeeId,
      LocalDateTime.now(),
      new GpsLocation(latitude, longitude),
      ipAddress,
      shift
  );

  // Domain 方法內部會：
  // 1. 驗證 GPS 範圍
  // 2. 判定遲到狀態（is_late, late_minutes）
  // 3. 發布 AttendanceRecorded 事件
  ```

- **Domain Event:**
  - 成功打卡後發布 `AttendanceRecorded` 事件
  - 若位置異常，發布 `AttendanceAnomalyDetected` 事件

- **測試範例:**
```java
@Test
@DisplayName("ATT_CMD_001: 員工上班打卡")
void checkIn_AsEmployee_ShouldRecordAndPublishEvent() throws Exception {
    CheckInRequest request = CheckInRequest.builder()
        .latitude(new BigDecimal("25.03"))
        .longitude(new BigDecimal("121.56"))
        .build();

    mockMvc.perform(post("/api/v1/attendance/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // 驗證 Domain Event 發布
    verify(eventPublisher).publish(argThat(event ->
        event instanceof AttendanceRecordedEvent
    ));
}
```

---

### 3.2 請假操作

#### 3.2.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_CMD_004 | 員工申請特休假 | EMPLOYEE | `POST /api/v1/leave/applications` | `{"leaveType":"ANNUAL","startDate":"2025-01-15","endDate":"2025-01-16"}` | 假期餘額檢查, 日期重疊檢查 | `LeaveApplied` |
| ATT_CMD_005 | 主管核准請假 | MANAGER | `PUT /api/v1/leave/applications/{id}/approve` | `{}` | 狀態檢查（必須為 PENDING）, 扣除餘額 | `LeaveApproved` |
| ATT_CMD_006 | 主管駁回請假 | MANAGER | `PUT /api/v1/leave/applications/{id}/reject` | `{"reason":"人力不足"}` | 狀態檢查（必須為 PENDING） | `LeaveRejected` |
| ATT_CMD_007 | 員工取消請假 | EMPLOYEE | `PUT /api/v1/leave/applications/{id}/cancel` | `{}` | 狀態檢查, 退回餘額（若已核准） | `LeaveCancelled` |
| ATT_CMD_008 | 申請病假（需證明） | EMPLOYEE | `POST /api/v1/leave/applications` | `{"leaveType":"SICK","proofUrl":"..."}` | 證明文件必填檢查 | `LeaveApplied` |

#### 3.2.2 詳細業務描述

**場景 ATT_CMD_004: 員工申請特休假**

- **業務規則:**
  1. 檢查假期餘額是否足夠（remaining_days >= 申請天數）
  2. 檢查日期範圍是否與其他請假重疊
  3. 計算請假天數（支援半天、全天）
  4. 若假別需要證明文件（requires_proof），必須上傳附件

- **Domain Logic:**
  ```java
  LeaveApplication application = LeaveApplication.create(
      employeeId,
      leaveType,
      leaveBalance,
      request
  );

  // Domain 方法內部會：
  // 1. 計算請假天數（calculateLeaveDays）
  // 2. 驗證餘額（balance.getRemainingDays() >= days）
  // 3. 驗證證明文件（若需要）
  // 4. 發布 LeaveApplied 事件
  ```

- **Domain Event:**
  - 成功申請後發布 `LeaveApplied` 事件
  - Event 包含: applicationId, employeeId, leaveTypeId, totalDays
  - Workflow Service 訂閱此事件，啟動審核流程

**場景 ATT_CMD_005: 主管核准請假**

- **業務規則:**
  1. 只能核准狀態為 PENDING 的申請
  2. 核准後扣除假期餘額
  3. 記錄審核人 ID 和審核時間
  4. 發布 LeaveApproved 事件

- **Domain Logic:**
  ```java
  application.approve(approverId, leaveBalance);

  // Domain 方法內部會：
  // 1. 檢查狀態（status == PENDING）
  // 2. 更新狀態為 APPROVED
  // 3. 扣除餘額（balance.deduct(totalDays)）
  // 4. 發布 LeaveApproved 事件
  ```

- **Domain Event:**
  - 發布 `LeaveApproved` 事件
  - Payroll Service 訂閱此事件，計算請假扣薪
  - Notification Service 訂閱此事件，發送核准通知

---

### 3.3 加班操作

#### 3.3.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_CMD_009 | 員工申請平日加班 | EMPLOYEE | `POST /api/v1/overtime/applications` | `{"date":"2025-01-15","startTime":"18:00","endTime":"20:00","type":"WEEKDAY"}` | 月加班上限檢查（46 小時）, 時間合理性檢查 | `OvertimeApplied` |
| ATT_CMD_010 | 員工申請休息日加班 | EMPLOYEE | `POST /api/v1/overtime/applications` | `{"date":"2025-01-18","startTime":"09:00","endTime":"17:00","type":"REST_DAY"}` | 月加班上限檢查, 季加班上限檢查（138 小時） | `OvertimeApplied` |
| ATT_CMD_011 | 主管核准加班 | MANAGER | `PUT /api/v1/overtime/applications/{id}/approve` | `{"compensationType":"PAY"}` | 狀態檢查, 指定補償方式（加班費或補休） | `OvertimeApproved` |
| ATT_CMD_012 | 主管駁回加班 | MANAGER | `PUT /api/v1/overtime/applications/{id}/reject` | `{"reason":"無必要性"}` | 狀態檢查 | `OvertimeRejected` |
| ATT_CMD_013 | 加班超過月上限 | EMPLOYEE | `POST /api/v1/overtime/applications` | `{"hours":10}` | 月加班 46 小時上限檢查 | `OvertimeLimitExceeded` |

#### 3.3.2 詳細業務描述

**場景 ATT_CMD_009: 員工申請平日加班**

- **業務規則:**
  1. 計算加班時數（end_time - start_time）
  2. 檢查月加班是否超過 46 小時（勞基法限制）
  3. 驗證加班日期與時間合理性
  4. 建立申請記錄，狀態為 PENDING

- **Domain Logic:**
  ```java
  OvertimeApplication application = OvertimeApplication.create(
      employeeId,
      overtimeStatistics,
      request
  );

  // Domain 方法內部會：
  // 1. 計算加班時數
  // 2. 檢查月上限（46 小時）和季上限（138 小時）
  // 3. 若超限，發布 OvertimeLimitExceeded 事件
  // 4. 發布 OvertimeApplied 事件
  ```

**場景 ATT_CMD_013: 加班超過月上限**

- **業務規則:**
  1. 若申請後月加班總時數超過 46 小時，發布警告事件
  2. 系統不阻止申請，但需主管特別核准
  3. Notification Service 會發送提醒給 HR 和主管

- **Domain Event:**
  - 發布 `OvertimeLimitExceeded` 事件
  - Event Payload:
    ```json
    {
      "employeeId": "E001",
      "currentMonthlyHours": 50,
      "limitHours": 46,
      "exceedType": "MONTHLY"
    }
    ```

---

### 3.4 補卡操作

#### 3.4.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| ATT_CMD_014 | 員工申請補卡 | EMPLOYEE | `POST /api/v1/attendance/corrections` | `{"recordId":"...","correctedTime":"09:00","reason":"忘記打卡"}` | 出勤記錄存在檢查, 補卡原因必填 | `CorrectionApplied` |
| ATT_CMD_015 | 主管核准補卡 | MANAGER | `PUT /api/v1/attendance/corrections/{id}/approve` | `{}` | 狀態檢查, 更新出勤記錄 | `CorrectionApproved` |
| ATT_CMD_016 | 主管駁回補卡 | MANAGER | `PUT /api/v1/attendance/corrections/{id}/reject` | `{"reason":"無正當理由"}` | 狀態檢查 | `CorrectionRejected` |

#### 3.4.2 詳細業務描述

**場景 ATT_CMD_014: 員工申請補卡**

- **業務規則:**
  1. 檢查關聯的出勤記錄是否存在
  2. 補卡原因必須填寫（reason NOT NULL）
  3. 記錄原始時間和修正時間
  4. 建立補卡申請，狀態為 PENDING

- **Domain Logic:**
  ```java
  AttendanceCorrection correction = AttendanceCorrection.create(
      recordId,
      employeeId,
      correctionType,
      originalTime,
      correctedTime,
      reason
  );

  // Domain 方法內部會：
  // 1. 驗證原因必填
  // 2. 驗證關聯的 record 存在
  // 3. 發布 CorrectionApplied 事件
  ```

**場景 ATT_CMD_015: 主管核准補卡**

- **業務規則:**
  1. 只能核准狀態為 PENDING 的補卡申請
  2. 核准後更新原始出勤記錄的時間
  3. 標記出勤記錄為已修正（is_corrected = TRUE）
  4. 重新計算遲到/早退狀態

- **Domain Logic:**
  ```java
  correction.approve(approverId, attendanceRecord);

  // Domain 方法內部會：
  // 1. 檢查狀態（status == PENDING）
  // 2. 更新狀態為 APPROVED
  // 3. 更新 attendanceRecord 的時間
  // 4. 重新計算 is_late, late_minutes
  // 5. 發布 CorrectionApproved 事件
  ```

---

## Domain Events 定義

### 4.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `AttendanceRecorded` | 員工打卡 | Attendance | - | 記錄出勤 |
| `AttendanceAnomalyDetected` | 偵測到打卡異常 | Attendance | Notification | 發送異常通知 |
| `LeaveApplied` | 請假申請提交 | Attendance | Workflow | 啟動審核流程 |
| `LeaveApproved` | 請假審核通過 | Attendance | Payroll, Notification | 計算扣薪、發送通知 |
| `LeaveRejected` | 請假審核駁回 | Attendance | Notification | 發送駁回通知 |
| `LeaveCancelled` | 請假取消 | Attendance | Payroll | 退回餘額 |
| `OvertimeApplied` | 加班申請提交 | Attendance | Workflow | 啟動審核流程 |
| `OvertimeApproved` | 加班審核通過 | Attendance | Payroll | 計算加班費 |
| `OvertimeRejected` | 加班審核駁回 | Attendance | Notification | 發送駁回通知 |
| `OvertimeLimitExceeded` | 加班時數超過上限 | Attendance | Notification | 發送超限警告 |
| `AnnualLeaveExpiring` | 特休即將到期 | Attendance | Notification | 發送到期提醒 |
| `AttendanceMonthClosed` | 月度差勤結算 | Attendance | Payroll, Report | 薪資計算基礎 |
| `CorrectionApplied` | 補卡申請提交 | Attendance | Workflow | 啟動審核流程 |
| `CorrectionApproved` | 補卡審核通過 | Attendance | - | 更新出勤記錄 |
| `CorrectionRejected` | 補卡審核駁回 | Attendance | Notification | 發送駁回通知 |

---

### 4.2 AttendanceRecordedEvent (員工打卡事件)

**觸發時機:** 員工完成上班或下班打卡

**Event Payload:**
```json
{
  "eventId": "evt-att-20250206-001",
  "eventType": "AttendanceRecordedEvent",
  "timestamp": "2026-02-06T09:05:23Z",
  "aggregateId": "record-550e8400-e29b-41d4-a716-446655440000",
  "payload": {
    "recordId": "550e8400-e29b-41d4-a716-446655440000",
    "employeeId": "E001",
    "recordDate": "2026-02-06",
    "checkInTime": "2026-02-06T09:05:23",
    "checkOutTime": null,
    "isLate": true,
    "lateMinutes": 5,
    "checkInLatitude": 25.033,
    "checkInLongitude": 121.564,
    "shiftId": "shift-standard-001"
  }
}
```

**下游消費者:**
- 無（純記錄事件）

---

### 4.3 AttendanceAnomalyDetectedEvent (打卡異常偵測事件)

**觸發時機:** 偵測到打卡異常（位置異常、遲到超過容許時間、忘打卡等）

**Event Payload:**
```json
{
  "eventId": "evt-att-anomaly-001",
  "eventType": "AttendanceAnomalyDetectedEvent",
  "timestamp": "2026-02-06T09:30:00Z",
  "aggregateId": "record-550e8400-e29b-41d4-a716-446655440001",
  "payload": {
    "recordId": "550e8400-e29b-41d4-a716-446655440001",
    "employeeId": "E002",
    "employeeName": "張小明",
    "anomalyType": "ABNORMAL_LOCATION",
    "anomalyNote": "打卡位置距離公司超過 500 公尺",
    "recordDate": "2026-02-06",
    "checkInTime": "2026-02-06T09:30:00",
    "latitude": 24.990,
    "longitude": 121.500,
    "expectedLatitude": 25.033,
    "expectedLongitude": 121.564,
    "distanceMeters": 5200
  }
}
```

**下游消費者:**
- **Notification Service**: 發送異常通知給員工和主管

---

### 4.4 LeaveAppliedEvent (請假申請事件)

**觸發時機:** 員工提交請假申請

**Event Payload:**
```json
{
  "eventId": "evt-leave-applied-001",
  "eventType": "LeaveAppliedEvent",
  "timestamp": "2026-02-06T10:00:00Z",
  "aggregateId": "app-leave-550e8400-e29b-41d4-a716-446655440002",
  "payload": {
    "applicationId": "app-leave-550e8400-e29b-41d4-a716-446655440002",
    "employeeId": "E001",
    "employeeName": "王小華",
    "leaveTypeId": "lt-annual-001",
    "leaveTypeCode": "ANNUAL",
    "leaveTypeName": "特休假",
    "startDate": "2026-02-10",
    "endDate": "2026-02-11",
    "totalDays": 2.0,
    "reason": "家庭旅遊",
    "appliedAt": "2026-02-06T10:00:00"
  }
}
```

**下游消費者:**
- **Workflow Service**: 啟動審核流程，依組織設定決定審核路徑

---

### 4.5 LeaveApprovedEvent (請假核准事件)

**觸發時機:** 主管或 HR 核准請假申請

**Event Payload:**
```json
{
  "eventId": "evt-leave-approved-001",
  "eventType": "LeaveApprovedEvent",
  "timestamp": "2026-02-06T14:30:00Z",
  "aggregateId": "app-leave-550e8400-e29b-41d4-a716-446655440002",
  "payload": {
    "applicationId": "app-leave-550e8400-e29b-41d4-a716-446655440002",
    "employeeId": "E001",
    "employeeName": "王小華",
    "leaveTypeId": "lt-annual-001",
    "leaveTypeCode": "ANNUAL",
    "leaveTypeName": "特休假",
    "startDate": "2026-02-10",
    "endDate": "2026-02-11",
    "totalDays": 2.0,
    "isPaid": true,
    "payRate": 1.0,
    "approverId": "MGR001",
    "approverName": "李主管",
    "approvedAt": "2026-02-06T14:30:00"
  }
}
```

**下游消費者:**
- **Payroll Service**: 計算請假扣薪（若為無薪假或半薪假）
- **Notification Service**: 發送核准通知給員工

---

### 4.6 LeaveRejectedEvent (請假駁回事件)

**觸發時機:** 主管或 HR 駁回請假申請

**Event Payload:**
```json
{
  "eventId": "evt-leave-rejected-001",
  "eventType": "LeaveRejectedEvent",
  "timestamp": "2026-02-06T15:00:00Z",
  "aggregateId": "app-leave-550e8400-e29b-41d4-a716-446655440003",
  "payload": {
    "applicationId": "app-leave-550e8400-e29b-41d4-a716-446655440003",
    "employeeId": "E003",
    "employeeName": "陳小美",
    "leaveTypeCode": "PERSONAL",
    "leaveTypeName": "事假",
    "startDate": "2026-02-12",
    "endDate": "2026-02-12",
    "totalDays": 1.0,
    "rejectionReason": "該日期人力不足，請改期申請",
    "approverId": "MGR001",
    "approverName": "李主管",
    "rejectedAt": "2026-02-06T15:00:00"
  }
}
```

**下游消費者:**
- **Notification Service**: 發送駁回通知給員工，包含駁回原因

---

### 4.7 LeaveCancelledEvent (請假取消事件)

**觸發時機:** 員工取消已提交的請假申請

**Event Payload:**
```json
{
  "eventId": "evt-leave-cancelled-001",
  "eventType": "LeaveCancelledEvent",
  "timestamp": "2026-02-06T16:00:00Z",
  "aggregateId": "app-leave-550e8400-e29b-41d4-a716-446655440004",
  "payload": {
    "applicationId": "app-leave-550e8400-e29b-41d4-a716-446655440004",
    "employeeId": "E004",
    "employeeName": "林小強",
    "leaveTypeCode": "ANNUAL",
    "leaveTypeName": "特休假",
    "startDate": "2026-02-15",
    "endDate": "2026-02-16",
    "totalDays": 2.0,
    "wasApproved": true,
    "refundedDays": 2.0,
    "cancelledAt": "2026-02-06T16:00:00"
  }
}
```

**下游消費者:**
- **Payroll Service**: 若已核准，需退回扣薪計算

---

### 4.8 OvertimeAppliedEvent (加班申請事件)

**觸發時機:** 員工提交加班申請

**Event Payload:**
```json
{
  "eventId": "evt-overtime-applied-001",
  "eventType": "OvertimeAppliedEvent",
  "timestamp": "2026-02-06T17:00:00Z",
  "aggregateId": "app-overtime-550e8400-e29b-41d4-a716-446655440005",
  "payload": {
    "overtimeId": "app-overtime-550e8400-e29b-41d4-a716-446655440005",
    "employeeId": "E005",
    "employeeName": "黃小玲",
    "overtimeDate": "2026-02-07",
    "startTime": "18:00",
    "endTime": "20:00",
    "overtimeHours": 2.0,
    "overtimeType": "WEEKDAY",
    "reason": "專案趕工",
    "appliedAt": "2026-02-06T17:00:00"
  }
}
```

**下游消費者:**
- **Workflow Service**: 啟動加班審核流程

---

### 4.9 OvertimeApprovedEvent (加班核准事件)

**觸發時機:** 主管或 HR 核准加班申請

**Event Payload:**
```json
{
  "eventId": "evt-overtime-approved-001",
  "eventType": "OvertimeApprovedEvent",
  "timestamp": "2026-02-07T09:00:00Z",
  "aggregateId": "app-overtime-550e8400-e29b-41d4-a716-446655440005",
  "payload": {
    "overtimeId": "app-overtime-550e8400-e29b-41d4-a716-446655440005",
    "employeeId": "E005",
    "employeeName": "黃小玲",
    "overtimeDate": "2026-02-07",
    "overtimeHours": 2.0,
    "overtimeType": "WEEKDAY",
    "compensationType": "PAY",
    "approverId": "MGR002",
    "approverName": "張主管",
    "approvedAt": "2026-02-07T09:00:00"
  }
}
```

**下游消費者:**
- **Payroll Service**: 計算加班費或建立補休假別餘額

---

### 4.10 OvertimeLimitExceededEvent (加班超限警告事件)

**觸發時機:** 員工申請加班後，月或季加班時數超過勞基法上限

**Event Payload:**
```json
{
  "eventId": "evt-overtime-limit-001",
  "eventType": "OvertimeLimitExceededEvent",
  "timestamp": "2026-02-07T17:00:00Z",
  "aggregateId": "E006",
  "payload": {
    "employeeId": "E006",
    "employeeName": "吳小強",
    "currentMonthlyHours": 50.0,
    "monthlyLimit": 46.0,
    "currentQuarterlyHours": 120.0,
    "quarterlyLimit": 138.0,
    "exceedType": "MONTHLY",
    "exceedHours": 4.0,
    "detectedAt": "2026-02-07T17:00:00"
  }
}
```

**下游消費者:**
- **Notification Service**: 發送超限警告給 HR 和部門主管

---

### 4.11 AnnualLeaveExpiringEvent (特休到期提醒事件)

**觸發時機:** 系統排程檢測到員工特休即將到期（預設 30 天內）

**Event Payload:**
```json
{
  "eventId": "evt-annual-expiring-001",
  "eventType": "AnnualLeaveExpiringEvent",
  "timestamp": "2026-02-06T08:00:00Z",
  "aggregateId": "balance-550e8400-e29b-41d4-a716-446655440006",
  "payload": {
    "balanceId": "balance-550e8400-e29b-41d4-a716-446655440006",
    "employeeId": "E007",
    "employeeName": "趙小芳",
    "year": 2025,
    "totalDays": 10.0,
    "usedDays": 3.0,
    "remainingDays": 7.0,
    "expiryDate": "2026-03-01",
    "daysUntilExpiry": 23
  }
}
```

**下游消費者:**
- **Notification Service**: 發送到期提醒給員工和 HR

---

### 4.12 AttendanceMonthClosedEvent (月度差勤結算事件)

**觸發時機:** HR 執行月度差勤結算作業

**Event Payload:**
```json
{
  "eventId": "evt-month-closed-202601",
  "eventType": "AttendanceMonthClosedEvent",
  "timestamp": "2026-02-01T00:00:00Z",
  "aggregateId": "month-close-202601",
  "payload": {
    "year": 2026,
    "month": 1,
    "organizationId": "ORG001",
    "totalEmployees": 150,
    "closedBy": "HR001",
    "closedAt": "2026-02-01T00:00:00",
    "employeeSummaries": [
      {
        "employeeId": "E001",
        "totalWorkDays": 21,
        "actualWorkDays": 20,
        "totalLeaveDays": 1.0,
        "totalOvertimeHours": 10.0,
        "lateCount": 2,
        "earlyLeaveCount": 0
      }
      // ... 其他員工
    ]
  }
}
```

**下游消費者:**
- **Payroll Service**: 取得差勤資料作為薪資計算基礎
- **Report Service**: 更新差勤報表數據

---

### 4.13 CorrectionAppliedEvent (補卡申請事件)

**觸發時機:** 員工提交補卡申請

**Event Payload:**
```json
{
  "eventId": "evt-correction-applied-001",
  "eventType": "CorrectionAppliedEvent",
  "timestamp": "2026-02-06T11:00:00Z",
  "aggregateId": "correction-550e8400-e29b-41d4-a716-446655440007",
  "payload": {
    "correctionId": "correction-550e8400-e29b-41d4-a716-446655440007",
    "recordId": "record-550e8400-e29b-41d4-a716-446655440008",
    "employeeId": "E008",
    "employeeName": "周小傑",
    "correctionType": "CHECK_IN",
    "originalTime": null,
    "correctedTime": "2026-02-05T09:00:00",
    "reason": "忘記打卡",
    "appliedAt": "2026-02-06T11:00:00"
  }
}
```

**下游消費者:**
- **Workflow Service**: 啟動補卡審核流程

---

### 4.14 CorrectionApprovedEvent (補卡核准事件)

**觸發時機:** 主管核准補卡申請

**Event Payload:**
```json
{
  "eventId": "evt-correction-approved-001",
  "eventType": "CorrectionApprovedEvent",
  "timestamp": "2026-02-06T14:00:00Z",
  "aggregateId": "correction-550e8400-e29b-41d4-a716-446655440007",
  "payload": {
    "correctionId": "correction-550e8400-e29b-41d4-a716-446655440007",
    "recordId": "record-550e8400-e29b-41d4-a716-446655440008",
    "employeeId": "E008",
    "employeeName": "周小傑",
    "correctionType": "CHECK_IN",
    "correctedTime": "2026-02-05T09:00:00",
    "approverId": "MGR003",
    "approverName": "劉主管",
    "approvedAt": "2026-02-06T14:00:00"
  }
}
```

**下游消費者:**
- 無（僅更新出勤記錄）

---

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 申請類查詢使用 `status != 'CANCELLED'` 過濾
   - 設定類查詢使用 `is_active = TRUE` 過濾
   - **不使用 `is_deleted` 欄位**

2. **部門隔離:**
   - 主管查詢時自動套用 `department_id IN ('{managedDeptIds}')`
   - 管理部門 ID 清單從當前使用者 Token 中取得

3. **個人資料保護:**
   - 員工只能查詢/申請自己的考勤資料
   - 自動套用 `employee_id = '{currentUserId}'` 過濾

### 5.2 測試注意事項

1. **合約測試重點:** 驗證業務規則執行，而非 SQL 生成
2. **欄位存在性:** 所有合約中的欄位必須在資料表中實際存在
3. **API 端點一致性:** 合約中的 API 端點必須與實際 Controller 路徑一致
4. **Domain Events:** 命令操作必須驗證 Domain Event 是否正確發布

### 5.3 勞基法合規性

本服務所有業務規則必須符合「勞動基準法」規定：

- **加班上限:** 月 46 小時、季 138 小時
- **特休計算:** 依年資自動計算（6 個月 3 天、1 年 7 天...）
- **特休到期:** 1 年到期，未休需補償工資
- **假別規定:** 病假半薪、事假無薪、婚假/喪假/產假全薪
- **女性保護:** 生理假、產假、育嬰留停
- **職災處理:** 職災期間不得解僱

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-06 | 移除不存在的 is_deleted 欄位，新增 API 端點、Command 操作、Domain Events，採用雙層結構設計 |
| 1.0 | 2025-12-19 | 初版建立 |
