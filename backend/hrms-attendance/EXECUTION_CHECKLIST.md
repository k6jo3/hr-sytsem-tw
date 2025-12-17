# HR03 考勤管理服務 - 後端執行清單

**版本:** 1.0
**建立日期:** 2025-12-17
**設計文件:** `spec/03_考勤管理服務系統設計書*.md`
**Domain代號:** 03 (ATT)

---

## 目錄

1. [專案結構](#1-專案結構)
2. [Interface Layer 介面層](#2-interface-layer-介面層)
3. [Application Layer 應用層](#3-application-layer-應用層)
4. [Domain Layer 領域層](#4-domain-layer-領域層)
5. [Infrastructure Layer 基礎設施層](#5-infrastructure-layer-基礎設施層)
6. [資料庫](#6-資料庫)
7. [排程任務](#7-排程任務)
8. [測試](#8-測試)
9. [進度追蹤](#9-進度追蹤)

---

## 1. 專案結構

```
backend/hrms-attendance/src/main/java/com/company/hrms/attendance/
├── api/
│   ├── controller/
│   │   ├── checkin/
│   │   │   ├── HR03CheckInCmdController.java
│   │   │   └── HR03CheckInQryController.java
│   │   ├── leave/
│   │   │   ├── HR03LeaveCmdController.java
│   │   │   └── HR03LeaveQryController.java
│   │   ├── overtime/
│   │   │   ├── HR03OvertimeCmdController.java
│   │   │   └── HR03OvertimeQryController.java
│   │   ├── shift/
│   │   │   ├── HR03ShiftCmdController.java
│   │   │   └── HR03ShiftQryController.java
│   │   ├── leavetype/
│   │   │   ├── HR03LeaveTypeCmdController.java
│   │   │   └── HR03LeaveTypeQryController.java
│   │   ├── report/
│   │   │   └── HR03ReportQryController.java
│   │   └── monthclose/
│   │       └── HR03MonthCloseCmdController.java
│   ├── request/
│   │   ├── checkin/
│   │   ├── leave/
│   │   ├── overtime/
│   │   ├── shift/
│   │   ├── leavetype/
│   │   └── monthclose/
│   └── response/
│       ├── checkin/
│       ├── leave/
│       ├── overtime/
│       ├── shift/
│       ├── leavetype/
│       └── report/
├── application/
│   └── service/
│       ├── checkin/
│       ├── leave/
│       ├── overtime/
│       ├── shift/
│       ├── leavetype/
│       ├── report/
│       └── monthclose/
├── domain/
│   ├── model/
│   │   ├── aggregate/
│   │   ├── entity/
│   │   └── valueobject/
│   ├── event/
│   ├── repository/
│   └── service/
├── infrastructure/
│   ├── dao/
│   ├── mapper/
│   ├── po/
│   ├── repository/
│   └── scheduler/
└── scheduler/
    └── job/
```

---

## 2. Interface Layer 介面層

### 2.1 Controllers

#### 2.1.1 CheckIn Controllers (打卡管理)

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR03CheckInCmdController.java` | ⬜ | P0 |
| `HR03CheckInQryController.java` | ⬜ | P0 |

**HR03CheckInCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `checkIn` | POST | `/api/v1/attendance/check-in` | checkInServiceImpl | ⬜ |
| `checkOut` | POST | `/api/v1/attendance/check-out` | checkOutServiceImpl | ⬜ |
| `applyCorrection` | POST | `/api/v1/attendance/corrections` | applyCorrectionServiceImpl | ⬜ |
| `approveCorrection` | PUT | `/api/v1/attendance/corrections/{id}/approve` | approveCorrectionServiceImpl | ⬜ |
| `rejectCorrection` | PUT | `/api/v1/attendance/corrections/{id}/reject` | rejectCorrectionServiceImpl | ⬜ |

**HR03CheckInQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `getTodayRecord` | GET | `/api/v1/attendance/today` | getTodayRecordServiceImpl | ⬜ |
| `getMyRecords` | GET | `/api/v1/attendance/records` | getMyRecordsServiceImpl | ⬜ |
| `getMonthlyStatistics` | GET | `/api/v1/attendance/statistics` | getMonthlyStatisticsServiceImpl | ⬜ |
| `getPendingCorrections` | GET | `/api/v1/attendance/corrections/pending` | getPendingCorrectionsServiceImpl | ⬜ |

---

#### 2.1.2 Leave Controllers (請假管理)

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR03LeaveCmdController.java` | ⬜ | P0 |
| `HR03LeaveQryController.java` | ⬜ | P0 |

**HR03LeaveCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `createLeaveApplication` | POST | `/api/v1/leave/applications` | createLeaveApplicationServiceImpl | ⬜ |
| `approveLeave` | PUT | `/api/v1/leave/applications/{id}/approve` | approveLeaveServiceImpl | ⬜ |
| `rejectLeave` | PUT | `/api/v1/leave/applications/{id}/reject` | rejectLeaveServiceImpl | ⬜ |
| `cancelLeave` | PUT | `/api/v1/leave/applications/{id}/cancel` | cancelLeaveServiceImpl | ⬜ |
| `batchApproveLeave` | PUT | `/api/v1/leave/applications/batch-approve` | batchApproveLeaveServiceImpl | ⬜ |

**HR03LeaveQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `getLeaveBalances` | GET | `/api/v1/leave/balances` | getLeaveBalancesServiceImpl | ⬜ |
| `calculateLeaveDays` | GET | `/api/v1/leave/calculate-days` | calculateLeaveDaysServiceImpl | ⬜ |
| `getMyLeaveApplications` | GET | `/api/v1/leave/applications/my` | getMyLeaveApplicationsServiceImpl | ⬜ |
| `getPendingLeaveApprovals` | GET | `/api/v1/leave/applications/pending` | getPendingLeaveApprovalsServiceImpl | ⬜ |
| `getLeaveApplicationDetail` | GET | `/api/v1/leave/applications/{id}` | getLeaveApplicationDetailServiceImpl | ⬜ |

---

#### 2.1.3 Overtime Controllers (加班管理)

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR03OvertimeCmdController.java` | ⬜ | P0 |
| `HR03OvertimeQryController.java` | ⬜ | P0 |

**HR03OvertimeCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `createOvertimeApplication` | POST | `/api/v1/overtime/applications` | createOvertimeApplicationServiceImpl | ⬜ |
| `approveOvertime` | PUT | `/api/v1/overtime/applications/{id}/approve` | approveOvertimeServiceImpl | ⬜ |
| `rejectOvertime` | PUT | `/api/v1/overtime/applications/{id}/reject` | rejectOvertimeServiceImpl | ⬜ |
| `cancelOvertime` | PUT | `/api/v1/overtime/applications/{id}/cancel` | cancelOvertimeServiceImpl | ⬜ |

**HR03OvertimeQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `getMyOvertimeApplications` | GET | `/api/v1/overtime/applications/my` | getMyOvertimeApplicationsServiceImpl | ⬜ |
| `getOvertimeStatistics` | GET | `/api/v1/overtime/statistics` | getOvertimeStatisticsServiceImpl | ⬜ |
| `getPendingOvertimeApprovals` | GET | `/api/v1/overtime/applications/pending` | getPendingOvertimeApprovalsServiceImpl | ⬜ |

---

#### 2.1.4 Shift Controllers (班別管理)

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR03ShiftCmdController.java` | ⬜ | P1 |
| `HR03ShiftQryController.java` | ⬜ | P1 |

**HR03ShiftCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `createShift` | POST | `/api/v1/shifts` | createShiftServiceImpl | ⬜ |
| `updateShift` | PUT | `/api/v1/shifts/{shiftId}` | updateShiftServiceImpl | ⬜ |
| `deactivateShift` | PUT | `/api/v1/shifts/{shiftId}/deactivate` | deactivateShiftServiceImpl | ⬜ |
| `assignShift` | POST | `/api/v1/employees/{employeeId}/shift` | assignShiftServiceImpl | ⬜ |

**HR03ShiftQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `getShiftList` | GET | `/api/v1/shifts` | getShiftListServiceImpl | ⬜ |
| `getShiftDetail` | GET | `/api/v1/shifts/{shiftId}` | getShiftDetailServiceImpl | ⬜ |

---

#### 2.1.5 LeaveType Controllers (假別管理)

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR03LeaveTypeCmdController.java` | ⬜ | P1 |
| `HR03LeaveTypeQryController.java` | ⬜ | P1 |

**HR03LeaveTypeCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `createLeaveType` | POST | `/api/v1/leave-types` | createLeaveTypeServiceImpl | ⬜ |
| `updateLeaveType` | PUT | `/api/v1/leave-types/{leaveTypeId}` | updateLeaveTypeServiceImpl | ⬜ |
| `deactivateLeaveType` | PUT | `/api/v1/leave-types/{leaveTypeId}/deactivate` | deactivateLeaveTypeServiceImpl | ⬜ |

**HR03LeaveTypeQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `getLeaveTypeList` | GET | `/api/v1/leave-types` | getLeaveTypeListServiceImpl | ⬜ |
| `getLeaveTypeDetail` | GET | `/api/v1/leave-types/{leaveTypeId}` | getLeaveTypeDetailServiceImpl | ⬜ |

---

#### 2.1.6 Report & MonthClose Controllers

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR03ReportQryController.java` | ⬜ | P2 |
| `HR03MonthCloseCmdController.java` | ⬜ | P2 |

**HR03ReportQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `getDepartmentReport` | GET | `/api/v1/attendance/reports/department` | getDepartmentReportServiceImpl | ⬜ |
| `getEmployeeReport` | GET | `/api/v1/attendance/reports/employee/{employeeId}` | getEmployeeReportServiceImpl | ⬜ |
| `exportReport` | GET | `/api/v1/attendance/reports/export` | exportReportServiceImpl | ⬜ |

**HR03MonthCloseCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | Service Bean | 狀態 |
|:---|:---:|:---|:---|:---:|
| `executeMonthlyClose` | POST | `/api/v1/attendance/monthly-close` | executeMonthlyCloseServiceImpl | ⬜ |

---

### 2.2 Request DTOs

#### 2.2.1 CheckIn Requests

| 檔案 | 狀態 |
|:---|:---:|
| `CheckInRequest.java` | ⬜ |
| `CheckOutRequest.java` | ⬜ |
| `ApplyCorrectionRequest.java` | ⬜ |

#### 2.2.2 Leave Requests

| 檔案 | 狀態 |
|:---|:---:|
| `CreateLeaveApplicationRequest.java` | ⬜ |
| `RejectLeaveRequest.java` | ⬜ |
| `BatchApproveLeaveRequest.java` | ⬜ |
| `CalculateLeaveDaysRequest.java` | ⬜ |
| `LeaveApplicationQueryRequest.java` | ⬜ |

#### 2.2.3 Overtime Requests

| 檔案 | 狀態 |
|:---|:---:|
| `CreateOvertimeApplicationRequest.java` | ⬜ |
| `RejectOvertimeRequest.java` | ⬜ |
| `OvertimeQueryRequest.java` | ⬜ |

#### 2.2.4 Shift Requests

| 檔案 | 狀態 |
|:---|:---:|
| `CreateShiftRequest.java` | ⬜ |
| `UpdateShiftRequest.java` | ⬜ |
| `AssignShiftRequest.java` | ⬜ |

#### 2.2.5 LeaveType Requests

| 檔案 | 狀態 |
|:---|:---:|
| `CreateLeaveTypeRequest.java` | ⬜ |
| `UpdateLeaveTypeRequest.java` | ⬜ |

#### 2.2.6 MonthClose Requests

| 檔案 | 狀態 |
|:---|:---:|
| `MonthlyCloseRequest.java` | ⬜ |

---

### 2.3 Response DTOs

#### 2.3.1 CheckIn Responses

| 檔案 | 狀態 |
|:---|:---:|
| `CheckInResponse.java` | ⬜ |
| `CheckOutResponse.java` | ⬜ |
| `TodayRecordResponse.java` | ⬜ |
| `AttendanceRecordListResponse.java` | ⬜ |
| `AttendanceRecordItemResponse.java` | ⬜ |
| `MonthlyStatisticsResponse.java` | ⬜ |
| `CorrectionResponse.java` | ⬜ |
| `PendingCorrectionListResponse.java` | ⬜ |

#### 2.3.2 Leave Responses

| 檔案 | 狀態 |
|:---|:---:|
| `LeaveBalanceListResponse.java` | ⬜ |
| `LeaveBalanceItemResponse.java` | ⬜ |
| `CalculateLeaveDaysResponse.java` | ⬜ |
| `CreateLeaveApplicationResponse.java` | ⬜ |
| `LeaveApplicationDetailResponse.java` | ⬜ |
| `LeaveApplicationListResponse.java` | ⬜ |
| `LeaveApplicationItemResponse.java` | ⬜ |
| `ApproveLeaveResponse.java` | ⬜ |

#### 2.3.3 Overtime Responses

| 檔案 | 狀態 |
|:---|:---:|
| `CreateOvertimeApplicationResponse.java` | ⬜ |
| `OvertimeApplicationListResponse.java` | ⬜ |
| `OvertimeApplicationItemResponse.java` | ⬜ |
| `OvertimeStatisticsResponse.java` | ⬜ |
| `ApproveOvertimeResponse.java` | ⬜ |

#### 2.3.4 Shift Responses

| 檔案 | 狀態 |
|:---|:---:|
| `CreateShiftResponse.java` | ⬜ |
| `ShiftDetailResponse.java` | ⬜ |
| `ShiftListResponse.java` | ⬜ |
| `ShiftItemResponse.java` | ⬜ |

#### 2.3.5 LeaveType Responses

| 檔案 | 狀態 |
|:---|:---:|
| `CreateLeaveTypeResponse.java` | ⬜ |
| `LeaveTypeDetailResponse.java` | ⬜ |
| `LeaveTypeListResponse.java` | ⬜ |
| `LeaveTypeItemResponse.java` | ⬜ |

#### 2.3.6 Report Responses

| 檔案 | 狀態 |
|:---|:---:|
| `DepartmentReportResponse.java` | ⬜ |
| `EmployeeReportResponse.java` | ⬜ |
| `MonthlyCloseResponse.java` | ⬜ |

---

## 3. Application Layer 應用層

### 3.1 CheckIn Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `CheckInServiceImpl.java` | checkIn | ⬜ |
| `CheckOutServiceImpl.java` | checkOut | ⬜ |
| `ApplyCorrectionServiceImpl.java` | applyCorrection | ⬜ |
| `ApproveCorrectionServiceImpl.java` | approveCorrection | ⬜ |
| `RejectCorrectionServiceImpl.java` | rejectCorrection | ⬜ |
| `GetTodayRecordServiceImpl.java` | getTodayRecord | ⬜ |
| `GetMyRecordsServiceImpl.java` | getMyRecords | ⬜ |
| `GetMonthlyStatisticsServiceImpl.java` | getMonthlyStatistics | ⬜ |
| `GetPendingCorrectionsServiceImpl.java` | getPendingCorrections | ⬜ |

### 3.2 Leave Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `CreateLeaveApplicationServiceImpl.java` | createLeaveApplication | ⬜ |
| `ApproveLeaveServiceImpl.java` | approveLeave | ⬜ |
| `RejectLeaveServiceImpl.java` | rejectLeave | ⬜ |
| `CancelLeaveServiceImpl.java` | cancelLeave | ⬜ |
| `BatchApproveLeaveServiceImpl.java` | batchApproveLeave | ⬜ |
| `GetLeaveBalancesServiceImpl.java` | getLeaveBalances | ⬜ |
| `CalculateLeaveDaysServiceImpl.java` | calculateLeaveDays | ⬜ |
| `GetMyLeaveApplicationsServiceImpl.java` | getMyLeaveApplications | ⬜ |
| `GetPendingLeaveApprovalsServiceImpl.java` | getPendingLeaveApprovals | ⬜ |
| `GetLeaveApplicationDetailServiceImpl.java` | getLeaveApplicationDetail | ⬜ |

### 3.3 Overtime Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `CreateOvertimeApplicationServiceImpl.java` | createOvertimeApplication | ⬜ |
| `ApproveOvertimeServiceImpl.java` | approveOvertime | ⬜ |
| `RejectOvertimeServiceImpl.java` | rejectOvertime | ⬜ |
| `CancelOvertimeServiceImpl.java` | cancelOvertime | ⬜ |
| `GetMyOvertimeApplicationsServiceImpl.java` | getMyOvertimeApplications | ⬜ |
| `GetOvertimeStatisticsServiceImpl.java` | getOvertimeStatistics | ⬜ |
| `GetPendingOvertimeApprovalsServiceImpl.java` | getPendingOvertimeApprovals | ⬜ |

### 3.4 Shift Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `CreateShiftServiceImpl.java` | createShift | ⬜ |
| `UpdateShiftServiceImpl.java` | updateShift | ⬜ |
| `DeactivateShiftServiceImpl.java` | deactivateShift | ⬜ |
| `AssignShiftServiceImpl.java` | assignShift | ⬜ |
| `GetShiftListServiceImpl.java` | getShiftList | ⬜ |
| `GetShiftDetailServiceImpl.java` | getShiftDetail | ⬜ |

### 3.5 LeaveType Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `CreateLeaveTypeServiceImpl.java` | createLeaveType | ⬜ |
| `UpdateLeaveTypeServiceImpl.java` | updateLeaveType | ⬜ |
| `DeactivateLeaveTypeServiceImpl.java` | deactivateLeaveType | ⬜ |
| `GetLeaveTypeListServiceImpl.java` | getLeaveTypeList | ⬜ |
| `GetLeaveTypeDetailServiceImpl.java` | getLeaveTypeDetail | ⬜ |

### 3.6 Report & MonthClose Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `GetDepartmentReportServiceImpl.java` | getDepartmentReport | ⬜ |
| `GetEmployeeReportServiceImpl.java` | getEmployeeReport | ⬜ |
| `ExportReportServiceImpl.java` | exportReport | ⬜ |
| `ExecuteMonthlyCloseServiceImpl.java` | executeMonthlyClose | ⬜ |

---

## 4. Domain Layer 領域層

### 4.1 Aggregate Roots (聚合根)

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `Shift.java` | 班別聚合根 | ⬜ |
| `AttendanceRecord.java` | 打卡記錄聚合根 | ⬜ |
| `LeaveApplication.java` | 請假申請聚合根 | ⬜ |
| `OvertimeApplication.java` | 加班申請聚合根 | ⬜ |
| `LeaveBalance.java` | 假期餘額聚合根 | ⬜ |
| `LeaveType.java` | 假別聚合根 | ⬜ |
| `AnnualLeavePolicy.java` | 特休假政策聚合根 | ⬜ |
| `AttendanceCorrection.java` | 補卡申請聚合根 | ⬜ |

**Shift 聚合根核心方法:**
| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| `checkLate()` | 檢查是否遲到 | ⬜ |
| `checkEarlyLeave()` | 檢查是否早退 | ⬜ |
| `calculateWorkingHours()` | 計算實際工時 | ⬜ |

**AttendanceRecord 聚合根核心方法:**
| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| `checkIn()` | 上班打卡 (Factory Method) | ⬜ |
| `checkOut()` | 下班打卡 | ⬜ |
| `correct()` | 補卡 | ⬜ |

**LeaveApplication 聚合根核心方法:**
| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| `create()` | 建立請假申請 (Factory Method) | ⬜ |
| `approve()` | 核准 | ⬜ |
| `reject()` | 駁回 | ⬜ |
| `cancel()` | 取消 | ⬜ |

**OvertimeApplication 聚合根核心方法:**
| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| `create()` | 建立加班申請 (Factory Method) | ⬜ |
| `approve()` | 核准 | ⬜ |
| `reject()` | 駁回 | ⬜ |

**LeaveBalance 聚合根核心方法:**
| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| `deduct()` | 扣除假期 | ⬜ |
| `refund()` | 退回假期 | ⬜ |
| `isExpiringSoon()` | 檢查是否即將到期 | ⬜ |

**AnnualLeavePolicy 聚合根核心方法:**
| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| `calculateAnnualLeaveDays()` | 依勞基法計算特休天數 | ⬜ |
| `calculateUnusedCompensation()` | 計算未休工資補償 | ⬜ |

---

### 4.2 Entities (實體)

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `AnnualLeaveRule.java` | 特休假規則實體 (年資對應天數) | ⬜ |
| `EmployeeShiftAssignment.java` | 員工班別指派實體 | ⬜ |

---

### 4.3 Value Objects (值對象)

**ID Value Objects:**
| 檔案 | 狀態 |
|:---|:---:|
| `ShiftId.java` | ⬜ |
| `RecordId.java` | ⬜ |
| `ApplicationId.java` | ⬜ |
| `OvertimeId.java` | ⬜ |
| `BalanceId.java` | ⬜ |
| `LeaveTypeId.java` | ⬜ |
| `PolicyId.java` | ⬜ |
| `CorrectionId.java` | ⬜ |
| `RuleId.java` | ⬜ |

**Domain Value Objects:**
| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `WorkingTime.java` | 工作時間 (上下班、休息時間) | ⬜ |
| `CheckInInfo.java` | 上班打卡資訊 (時間、GPS、IP) | ⬜ |
| `CheckOutInfo.java` | 下班打卡資訊 | ⬜ |
| `LeavePeriod.java` | 請假期間 (起訖日期、時段) | ⬜ |
| `GpsLocation.java` | GPS座標 | ⬜ |
| `LateResult.java` | 遲到結果 | ⬜ |
| `EarlyLeaveResult.java` | 早退結果 | ⬜ |
| `OvertimeStatistics.java` | 加班統計 (月/季累計) | ⬜ |

**Enum Value Objects:**
| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `ShiftType.java` | STANDARD, FLEXIBLE, ROTATING | ⬜ |
| `AnomalyType.java` | LATE, EARLY_LEAVE, MISSING_CHECK_IN, MISSING_CHECK_OUT, ABNORMAL_LOCATION | ⬜ |
| `ApplicationStatus.java` | DRAFT, PENDING, APPROVED, REJECTED, CANCELLED | ⬜ |
| `LeavePeriodType.java` | AM, PM, FULL_DAY | ⬜ |
| `LeaveUnit.java` | HOUR, HALF_DAY, FULL_DAY | ⬜ |
| `StatutoryLeaveType.java` | ANNUAL_LEAVE, SICK_LEAVE, MARRIAGE_LEAVE, BEREAVEMENT_LEAVE, MATERNITY_LEAVE, PATERNITY_LEAVE, MENSTRUAL_LEAVE, PARENTAL_LEAVE | ⬜ |
| `OvertimeType.java` | WEEKDAY, REST_DAY, HOLIDAY | ⬜ |
| `CompensationType.java` | PAY, COMP_TIME | ⬜ |
| `CorrectionType.java` | CHECK_IN, CHECK_OUT | ⬜ |

---

### 4.4 Domain Events (領域事件)

| 檔案 | 觸發時機 | 訂閱服務 | 狀態 |
|:---|:---|:---|:---:|
| `AttendanceRecordedEvent.java` | 員工打卡 | - | ⬜ |
| `AttendanceAnomalyDetectedEvent.java` | 偵測到打卡異常 | Notification | ⬜ |
| `LeaveAppliedEvent.java` | 請假申請提交 | Workflow | ⬜ |
| `LeaveApprovedEvent.java` | 請假審核通過 | Payroll, Notification | ⬜ |
| `LeaveRejectedEvent.java` | 請假審核駁回 | Notification | ⬜ |
| `LeaveCancelledEvent.java` | 請假取消 | - | ⬜ |
| `OvertimeAppliedEvent.java` | 加班申請提交 | Workflow | ⬜ |
| `OvertimeApprovedEvent.java` | 加班審核通過 | Payroll | ⬜ |
| `OvertimeLimitExceededEvent.java` | 加班時數超過上限 | Notification | ⬜ |
| `AnnualLeaveExpiringEvent.java` | 特休即將到期 | Notification | ⬜ |
| `AttendanceMonthClosedEvent.java` | 月度差勤結算 | Payroll, Report | ⬜ |

---

### 4.5 Repository Interfaces (Repository介面)

| 檔案 | 狀態 |
|:---|:---:|
| `IShiftRepository.java` | ⬜ |
| `IAttendanceRecordRepository.java` | ⬜ |
| `ILeaveApplicationRepository.java` | ⬜ |
| `IOvertimeApplicationRepository.java` | ⬜ |
| `ILeaveBalanceRepository.java` | ⬜ |
| `ILeaveTypeRepository.java` | ⬜ |
| `IAnnualLeavePolicyRepository.java` | ⬜ |
| `IAttendanceCorrectionRepository.java` | ⬜ |

**IAttendanceRecordRepository 方法清單:**
| 方法 | 狀態 |
|:---|:---:|
| `findById(RecordId id)` | ⬜ |
| `findByEmployeeAndDate(UUID employeeId, LocalDate date)` | ⬜ |
| `findByEmployeeAndMonth(UUID employeeId, YearMonth month)` | ⬜ |
| `findAnomalies(UUID employeeId, LocalDate from, LocalDate to)` | ⬜ |
| `save(AttendanceRecord record)` | ⬜ |

**ILeaveApplicationRepository 方法清單:**
| 方法 | 狀態 |
|:---|:---:|
| `findById(ApplicationId id)` | ⬜ |
| `findByEmployeeId(UUID employeeId)` | ⬜ |
| `findPendingByApprover(UUID approverId)` | ⬜ |
| `findOverlapping(UUID employeeId, LocalDate start, LocalDate end)` | ⬜ |
| `save(LeaveApplication application)` | ⬜ |

**IOvertimeApplicationRepository 方法清單:**
| 方法 | 狀態 |
|:---|:---:|
| `findById(OvertimeId id)` | ⬜ |
| `findByEmployeeId(UUID employeeId)` | ⬜ |
| `getStatistics(UUID employeeId, YearMonth month)` | ⬜ |
| `save(OvertimeApplication application)` | ⬜ |

**ILeaveBalanceRepository 方法清單:**
| 方法 | 狀態 |
|:---|:---:|
| `findByEmployeeAndTypeAndYear(UUID employeeId, UUID leaveTypeId, int year)` | ⬜ |
| `findByEmployeeId(UUID employeeId)` | ⬜ |
| `findExpiringBefore(LocalDate date)` | ⬜ |
| `save(LeaveBalance balance)` | ⬜ |

---

### 4.6 Domain Services (領域服務)

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `LateCheckDomainService.java` | 遲到判斷服務 | ⬜ |
| `LeaveDaysCalculationDomainService.java` | 請假天數計算服務 | ⬜ |
| `OvertimeLimitCheckDomainService.java` | 加班時數上限檢查服務 (46h/月, 138h/季) | ⬜ |
| `AnnualLeaveCalculationDomainService.java` | 特休假計算服務 (依勞基法) | ⬜ |
| `WorkingHoursCalculationDomainService.java` | 工時計算服務 | ⬜ |
| `MonthlyAttendanceCloseDomainService.java` | 月度差勤結算服務 | ⬜ |
| `GpsLocationValidationDomainService.java` | GPS 定位驗證服務 | ⬜ |

---

## 5. Infrastructure Layer 基礎設施層

### 5.1 Repository Implementations (Repository實作)

| 檔案 | 對應介面 | 狀態 |
|:---|:---|:---:|
| `ShiftRepositoryImpl.java` | IShiftRepository | ⬜ |
| `AttendanceRecordRepositoryImpl.java` | IAttendanceRecordRepository | ⬜ |
| `LeaveApplicationRepositoryImpl.java` | ILeaveApplicationRepository | ⬜ |
| `OvertimeApplicationRepositoryImpl.java` | IOvertimeApplicationRepository | ⬜ |
| `LeaveBalanceRepositoryImpl.java` | ILeaveBalanceRepository | ⬜ |
| `LeaveTypeRepositoryImpl.java` | ILeaveTypeRepository | ⬜ |
| `AnnualLeavePolicyRepositoryImpl.java` | IAnnualLeavePolicyRepository | ⬜ |
| `AttendanceCorrectionRepositoryImpl.java` | IAttendanceCorrectionRepository | ⬜ |

---

### 5.2 DAOs (Data Access Objects)

| 檔案 | 狀態 |
|:---|:---:|
| `ShiftDAO.java` | ⬜ |
| `AttendanceRecordDAO.java` | ⬜ |
| `LeaveApplicationDAO.java` | ⬜ |
| `OvertimeApplicationDAO.java` | ⬜ |
| `LeaveBalanceDAO.java` | ⬜ |
| `LeaveTypeDAO.java` | ⬜ |
| `AnnualLeavePolicyDAO.java` | ⬜ |
| `AnnualLeaveRuleDAO.java` | ⬜ |
| `AttendanceCorrectionDAO.java` | ⬜ |

---

### 5.3 MyBatis Mappers

| Java Interface | XML | 狀態 |
|:---|:---|:---:|
| `ShiftMapper.java` | `ShiftMapper.xml` | ⬜ |
| `AttendanceRecordMapper.java` | `AttendanceRecordMapper.xml` | ⬜ |
| `LeaveApplicationMapper.java` | `LeaveApplicationMapper.xml` | ⬜ |
| `OvertimeApplicationMapper.java` | `OvertimeApplicationMapper.xml` | ⬜ |
| `LeaveBalanceMapper.java` | `LeaveBalanceMapper.xml` | ⬜ |
| `LeaveTypeMapper.java` | `LeaveTypeMapper.xml` | ⬜ |
| `AnnualLeavePolicyMapper.java` | `AnnualLeavePolicyMapper.xml` | ⬜ |
| `AnnualLeaveRuleMapper.java` | `AnnualLeaveRuleMapper.xml` | ⬜ |
| `AttendanceCorrectionMapper.java` | `AttendanceCorrectionMapper.xml` | ⬜ |

---

### 5.4 Persistence Objects (PO)

| 檔案 | 對應資料表 | 狀態 |
|:---|:---|:---:|
| `ShiftPO.java` | shifts | ⬜ |
| `AttendanceRecordPO.java` | attendance_records | ⬜ |
| `LeaveApplicationPO.java` | leave_applications | ⬜ |
| `OvertimeApplicationPO.java` | overtime_applications | ⬜ |
| `LeaveBalancePO.java` | leave_balances | ⬜ |
| `LeaveTypePO.java` | leave_types | ⬜ |
| `AnnualLeavePolicyPO.java` | annual_leave_policies | ⬜ |
| `AnnualLeaveRulePO.java` | annual_leave_rules | ⬜ |
| `AttendanceCorrectionPO.java` | attendance_corrections | ⬜ |

---

## 6. 資料庫

### 6.1 DDL Scripts

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `V1__create_shifts_table.sql` | 班別表 | ⬜ |
| `V2__create_leave_types_table.sql` | 假別表 | ⬜ |
| `V3__create_attendance_records_table.sql` | 打卡記錄表 | ⬜ |
| `V4__create_leave_applications_table.sql` | 請假申請表 | ⬜ |
| `V5__create_overtime_applications_table.sql` | 加班申請表 | ⬜ |
| `V6__create_leave_balances_table.sql` | 假期餘額表 | ⬜ |
| `V7__create_annual_leave_policies_table.sql` | 特休假政策表 | ⬜ |
| `V8__create_annual_leave_rules_table.sql` | 特休假規則表 | ⬜ |
| `V9__create_attendance_corrections_table.sql` | 補卡申請表 | ⬜ |

### 6.2 Index Scripts

| 檔案 | 狀態 |
|:---|:---:|
| `V10__create_indexes.sql` | ⬜ |

### 6.3 Initial Data

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `V11__insert_statutory_leave_types.sql` | 法定假別 (特休、病假、事假、婚假、喪假、產假等) | ⬜ |
| `V12__insert_default_shifts.sql` | 預設班別 (標準班、彈性班) | ⬜ |
| `V13__insert_annual_leave_rules.sql` | 勞基法特休規則 | ⬜ |

---

## 7. 排程任務

### 7.1 Scheduler Jobs

| 檔案 | 說明 | 執行週期 | 狀態 |
|:---|:---|:---|:---:|
| `AnnualLeaveExpiryReminderJob.java` | 特休到期提醒 | 每日 09:00 | ⬜ |
| `MonthlyAttendanceCloseJob.java` | 月度差勤結算 | 每月1日 00:00 | ⬜ |
| `AnnualLeaveGrantJob.java` | 年度特休發放 | 員工到職週年日 | ⬜ |
| `LeaveBalanceExpiryJob.java` | 過期假期清零 | 每日 00:00 | ⬜ |

---

## 8. 測試

### 8.1 Domain Tests (必須 100% 覆蓋)

| 檔案 | 狀態 |
|:---|:---:|
| `ShiftTest.java` | ⬜ |
| `AttendanceRecordTest.java` | ⬜ |
| `LeaveApplicationTest.java` | ⬜ |
| `OvertimeApplicationTest.java` | ⬜ |
| `LeaveBalanceTest.java` | ⬜ |
| `AnnualLeavePolicyTest.java` | ⬜ |
| `WorkingTimeTest.java` | ⬜ |
| `LeavePeriodTest.java` | ⬜ |

### 8.2 Domain Service Tests

| 檔案 | 狀態 |
|:---|:---:|
| `LeaveDaysCalculationDomainServiceTest.java` | ⬜ |
| `OvertimeLimitCheckDomainServiceTest.java` | ⬜ |
| `AnnualLeaveCalculationDomainServiceTest.java` | ⬜ |

### 8.3 Application Service Tests

| 檔案 | 狀態 |
|:---|:---:|
| `CheckInServiceImplTest.java` | ⬜ |
| `CreateLeaveApplicationServiceImplTest.java` | ⬜ |
| `ApproveLeaveServiceImplTest.java` | ⬜ |
| `CreateOvertimeApplicationServiceImplTest.java` | ⬜ |

### 8.4 Integration Tests

| 檔案 | 狀態 |
|:---|:---:|
| `HR03CheckInCmdControllerIT.java` | ⬜ |
| `HR03LeaveCmdControllerIT.java` | ⬜ |
| `HR03OvertimeCmdControllerIT.java` | ⬜ |

---

## 9. 進度追蹤

### 總覽

| 分類 | 完成 | 總計 | 進度 |
|:---|:---:|:---:|:---:|
| Controllers | 0 | 14 | 0% |
| Request DTOs | 0 | 15 | 0% |
| Response DTOs | 0 | 26 | 0% |
| Application Services | 0 | 44 | 0% |
| Aggregates | 0 | 8 | 0% |
| Entities | 0 | 2 | 0% |
| Value Objects | 0 | 27 | 0% |
| Domain Events | 0 | 11 | 0% |
| Repository Interfaces | 0 | 8 | 0% |
| Domain Services | 0 | 7 | 0% |
| Repository Impls | 0 | 8 | 0% |
| DAOs | 0 | 9 | 0% |
| Mappers | 0 | 18 | 0% |
| POs | 0 | 9 | 0% |
| Database Scripts | 0 | 13 | 0% |
| Scheduler Jobs | 0 | 4 | 0% |
| Tests | 0 | 15 | 0% |
| **總計** | **0** | **228** | **0%** |

### 狀態說明

- ⬜ 未開始
- 🟡 進行中
- ✅ 已完成
- ❌ 已取消

---

## 附錄: 開發順序建議

### Phase 1: 核心基礎 (P0)

1. **Domain Layer** - 先建立領域模型
   - Value Objects (IDs, Enums)
   - Aggregates (Shift, AttendanceRecord, LeaveApplication, OvertimeApplication, LeaveBalance)
   - Repository Interfaces
   - Domain Services (LeaveDaysCalculation, OvertimeLimitCheck)

2. **Infrastructure Layer** - 資料存取
   - POs
   - Mappers
   - Repository Impls
   - DAOs

3. **Database** - DDL Scripts + Initial Data

### Phase 2: 打卡功能 (P0)

4. **CheckIn API**
   - Request/Response DTOs
   - Controllers
   - Application Services
   - Tests

### Phase 3: 請假與加班 (P0)

5. **Leave API**
   - 請假申請、審核、餘額查詢

6. **Overtime API**
   - 加班申請、審核、統計

### Phase 4: 管理功能 (P1)

7. **Shift & LeaveType API**
   - 班別管理
   - 假別管理

### Phase 5: 報表與結算 (P2)

8. **Report & MonthClose API**
9. **Scheduler Jobs**

---

## 附錄: 勞基法關鍵規則

### 加班時數上限
- 每月加班上限: **46 小時**
- 每三個月累計上限: **138 小時**

### 特休假規則 (依勞基法第38條)
| 年資 | 特休天數 |
|:---|:---:|
| 6個月~1年 | 3天 |
| 1~2年 | 7天 |
| 2~3年 | 10天 |
| 3~5年 | 14天 |
| 5~10年 | 15天 |
| 10年以上 | 15天 + 每年增加1天，最多30天 |

### 法定假別
- 病假: 30天/年，半薪
- 事假: 14天/年，不支薪
- 婚假: 8天，全薪
- 喪假: 3~8天 (依親屬)，全薪
- 產假: 56天，全薪
- 陪產假: 7天，全薪
- 生理假: 12天/年，半薪

---

**最後更新:** 2025-12-17
