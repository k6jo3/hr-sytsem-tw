# HR03 考勤管理服務業務合約

> **服務代碼:** HR03
> **服務名稱:** 考勤管理服務 (Attendance Management)
> **版本:** 1.0
> **更新日期:** 2026-02-12

---

## 概述

考勤管理服務是 HR 系統中功能最豐富的服務之一，涵蓋打卡（上班/下班/補卡）、請假（申請/核准/駁回/取消）、加班（申請/核准/駁回）、班別管理、假別管理等模組。

---

## API 端點概覽

### 出勤管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/attendance/records?employeeId=E001&startDate=2025-01-15&endDate=2025-01-15` | GET | ATT_QRY_A001 | 查詢員工當日出勤 | ✅ 已實作 |
| 2 | `GET /api/v1/attendance/records?departmentId=D001&startDate=2025-01-15&endDate=2025-01-18` | GET | ATT_QRY_A002 | 查詢部門月出勤 | ✅ 已實作 |
| 3 | `GET /api/v1/attendance/records?status=ABNORMAL` | GET | ATT_QRY_A003 | 查詢異常出勤 | ✅ 已實作 |
| 4 | `POST /api/v1/attendance/check-in` | POST | ATT_CMD_A001 | 員工打卡上班 | ✅ 已實作 |
| 5 | `POST /api/v1/attendance/check-out` | POST | ATT_CMD_A002 | 員工打卡下班 | ✅ 已實作 |
| 6 | `POST /api/v1/attendance/corrections` | POST | ATT_CMD_A003 | 申請補卡 | ✅ 已實作 |
| 7 | `PUT /api/v1/attendance/corrections/{id}/approve` | PUT | ATT_CMD_A004 | 核准補卡 | ✅ 已實作 |

### 請假管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/leave/applications?status=PENDING` | GET | ATT_QRY_L001 | 查詢待審核請假 | ✅ 已實作 |
| 2 | `GET /api/v1/leave/applications?status=APPROVED` | GET | ATT_QRY_L002 | 查詢已核准請假 | ✅ 已實作 |
| 3 | `GET /api/v1/leave/applications?leaveTypeId=ANNUAL` | GET | ATT_QRY_L003 | 查詢特休假申請 | ✅ 已實作 |
| 4 | `GET /api/v1/leave/balances/{employeeId}` | GET | ATT_QRY_L004 | 查詢假期餘額 | ✅ 已實作 |
| 5 | `POST /api/v1/leave/applications` | POST | ATT_CMD_L001 | 申請請假 | ✅ 已實作 |
| 6 | `PUT /api/v1/leave/applications/{id}/approve` | PUT | ATT_CMD_L002 | 核准請假 | ✅ 已實作 |
| 7 | `PUT /api/v1/leave/applications/{id}/reject` | PUT | ATT_CMD_L003 | 駁回請假 | ✅ 已實作 |
| 8 | `PUT /api/v1/leave/applications/{id}/cancel` | PUT | ATT_CMD_L004 | 取消請假 | ✅ 已實作 |

### 加班管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/overtime/applications?status=PENDING` | GET | ATT_QRY_O001 | 查詢待審核加班 | ✅ 已實作 |
| 2 | `GET /api/v1/overtime/applications?status=APPROVED` | GET | ATT_QRY_O002 | 查詢已核准加班 | ✅ 已實作 |
| 3 | `GET /api/v1/overtime/applications?overtimeType=WORKDAY` | GET | ATT_QRY_O003 | 查詢平日加班 | ✅ 已實作 |
| 4 | `POST /api/v1/overtime/applications` | POST | ATT_CMD_O001 | 申請加班 | ✅ 已實作 |
| 5 | `PUT /api/v1/overtime/applications/{id}/approve` | PUT | ATT_CMD_O002 | 核准加班 | ✅ 已實作 |
| 6 | `PUT /api/v1/overtime/applications/{id}/reject` | PUT | ATT_CMD_O003 | 駁回加班 | ✅ 已實作 |

### 班別管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/shifts` | GET | ATT_QRY_S001 | 查詢班別列表 | ✅ 已實作 |
| 2 | `POST /api/v1/shifts` | POST | ATT_CMD_S001 | 建立班別 | ✅ 已實作 |
| 3 | `PUT /api/v1/shifts/{id}` | PUT | ATT_CMD_S002 | 更新班別 | ✅ 已實作 |
| 4 | `PUT /api/v1/shifts/{id}/deactivate` | PUT | ATT_CMD_S003 | 停用班別 | ✅ 已實作 |

### 假別管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/leave/types` | GET | ATT_QRY_T001 | 查詢假別列表 | ✅ 已實作 |
| 2 | `POST /api/v1/leave/types` | POST | ATT_CMD_T001 | 建立假別 | ✅ 已實作 |
| 3 | `PUT /api/v1/leave/types/{id}` | PUT | ATT_CMD_T002 | 更新假別 | ✅ 已實作 |
| 4 | `PUT /api/v1/leave/types/{id}/deactivate` | PUT | ATT_CMD_T003 | 停用假別 | ✅ 已實作 |

**總計：30 個場景（13 個 Query + 17 個 Command）**

---

## 1. Command 操作業務合約

### 1.1 出勤管理

#### ATT_CMD_A001: 員工打卡上班

**API 端點：** `POST /api/v1/attendance/check-in`

**業務場景描述：**

員工進行上班打卡。系統記錄打卡時間，並與排班資訊比對是否遲到。若超過班別規定上班時間則標記遲到。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_A001",
  "apiEndpoint": "POST /api/v1/attendance/check-in",
  "controller": "HR03CheckInCmdController",
  "service": "checkInServiceImpl",
  "permission": "attendance:check-in",
  "request": {
    "employeeId": "E001",
    "checkInTime": "2025-02-01T09:00:00"
  },
  "businessRules": [
    {"rule": "員工當日尚未打卡上班"},
    {"rule": "打卡時間不可為未來時間"},
    {"rule": "比對班別判斷是否遲到"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "attendance_records",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "employee_id", "operator": "equals", "value": "E001"},
        {"field": "check_in_time", "operator": "notNull"},
        {"field": "status", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "AttendanceRecordedEvent",
      "payload": [
        {"field": "recordId", "operator": "notNull"},
        {"field": "employeeId", "operator": "equals", "value": "E001"}
      ]
    }
  ]
}
```

---

#### ATT_CMD_A002: 員工打卡下班

**API 端點：** `POST /api/v1/attendance/check-out`

**業務場景描述：**

員工進行下班打卡。系統更新出勤記錄，並與排班資訊比對是否早退。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_A002",
  "apiEndpoint": "POST /api/v1/attendance/check-out",
  "controller": "HR03CheckInCmdController",
  "service": "checkOutServiceImpl",
  "permission": "attendance:check-out",
  "request": {
    "employeeId": "E001",
    "checkOutTime": "2025-01-15T18:00:00"
  },
  "businessRules": [
    {"rule": "員工當日已完成上班打卡"},
    {"rule": "員工當日尚未打卡下班"},
    {"rule": "下班時間須晚於上班時間"},
    {"rule": "比對班別判斷是否早退"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "attendance_records",
      "count": 1,
      "assertions": [
        {"field": "employee_id", "operator": "equals", "value": "E001"},
        {"field": "check_out_time", "operator": "notNull"},
        {"field": "status", "operator": "equals", "value": "NORMAL"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "AttendanceRecordedEvent",
      "payload": [
        {"field": "recordId", "operator": "notNull"},
        {"field": "employeeId", "operator": "equals", "value": "E001"}
      ]
    }
  ]
}
```

---

#### ATT_CMD_A003: 申請補卡

**API 端點：** `POST /api/v1/attendance/corrections`

**業務場景描述：**

員工因忘記打卡或設備故障等原因提交補卡申請。補卡申請建立後狀態為 PENDING，等待主管審核。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_A003",
  "apiEndpoint": "POST /api/v1/attendance/corrections",
  "controller": "HR03CheckInCmdController",
  "service": "createCorrectionServiceImpl",
  "permission": "attendance:correction",
  "request": {
    "employeeId": "E001",
    "attendanceRecordId": "AR007",
    "correctionDate": "2025-01-17",
    "correctionType": "FORGET_CHECK_IN",
    "correctedCheckInTime": "09:00",
    "reason": "忘記打卡"
  },
  "businessRules": [
    {"rule": "該日需有出勤記錄或為工作日"},
    {"rule": "補卡申請不可超過期限（通常 3 天）"},
    {"rule": "同日期同類型不可重複申請"},
    {"rule": "新申請狀態為 PENDING"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "attendance_corrections",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "employee_id", "operator": "equals", "value": "E001"},
        {"field": "correction_type", "operator": "equals", "value": "FORGET_CHECK_IN"},
        {"field": "status", "operator": "equals", "value": "PENDING"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### ATT_CMD_A004: 核准補卡

**API 端點：** `PUT /api/v1/attendance/corrections/{id}/approve`

**業務場景描述：**

主管核准補卡申請。核准後系統自動更新對應出勤記錄的打卡時間。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_A004",
  "apiEndpoint": "PUT /api/v1/attendance/corrections/{id}/approve",
  "controller": "HR03CheckInCmdController",
  "service": "approveCorrectionServiceImpl",
  "permission": "attendance:correction:approve",
  "request": {},
  "businessRules": [
    {"rule": "補卡申請必須存在且狀態為 PENDING"},
    {"rule": "審核者不可為申請者本人"},
    {"rule": "核准後更新 attendance_records 的打卡時間"},
    {"rule": "核准後 correction 狀態改為 APPROVED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "attendance_corrections",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "APPROVED"},
        {"field": "approver_id", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

### 1.2 請假管理

#### ATT_CMD_L001: 申請請假

**API 端點：** `POST /api/v1/leave/applications`

**業務場景描述：**

員工提交請假申請。系統檢查假別餘額是否足夠，建立請假記錄後狀態為 PENDING。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_L001",
  "apiEndpoint": "POST /api/v1/leave/applications",
  "controller": "HR03LeaveCmdController",
  "service": "applyLeaveServiceImpl",
  "permission": "leave:apply",
  "request": {
    "employeeId": "E001",
    "leaveTypeId": "ANNUAL",
    "startDate": "2025-03-10",
    "endDate": "2025-03-12",
    "startPeriod": "FULL_DAY",
    "endPeriod": "FULL_DAY",
    "reason": "出國旅遊"
  },
  "businessRules": [
    {"rule": "假別必須存在且為啟用狀態"},
    {"rule": "假別餘額需足夠"},
    {"rule": "開始日期不可晚於結束日期"},
    {"rule": "不可與已核准假期重疊"},
    {"rule": "新申請狀態為 PENDING"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "leave_applications",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "employee_id", "operator": "equals", "value": "E001"},
        {"field": "leave_type_id", "operator": "equals", "value": "ANNUAL"},
        {"field": "status", "operator": "equals", "value": "PENDING"},
        {"field": "is_deleted", "operator": "equals", "value": 0}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "LeaveAppliedEvent",
      "payload": [
        {"field": "applicationId", "operator": "notNull"},
        {"field": "employeeId", "operator": "equals", "value": "E001"},
        {"field": "leaveTypeId", "operator": "equals", "value": "ANNUAL"}
      ]
    }
  ]
}
```

---

#### ATT_CMD_L002: 核准請假

**API 端點：** `PUT /api/v1/leave/applications/{id}/approve`

**業務場景描述：**

主管核准請假申請。核准後扣除假別餘額，並通知員工。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_L002",
  "apiEndpoint": "PUT /api/v1/leave/applications/{id}/approve",
  "controller": "HR03LeaveCmdController",
  "service": "approveLeaveServiceImpl",
  "permission": "leave:approve",
  "request": {},
  "businessRules": [
    {"rule": "請假申請必須存在且狀態為 PENDING"},
    {"rule": "審核者須有核准權限"},
    {"rule": "核准後扣除假別餘額"},
    {"rule": "核准後狀態改為 APPROVED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "leave_applications",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "APPROVED"},
        {"field": "approver_id", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "LeaveApprovedEvent",
      "payload": [
        {"field": "applicationId", "operator": "notNull"},
        {"field": "approvedBy", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### ATT_CMD_L003: 駁回請假

**API 端點：** `PUT /api/v1/leave/applications/{id}/reject`

**業務場景描述：**

主管駁回請假申請。駁回須填寫原因，不影響假別餘額。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_L003",
  "apiEndpoint": "PUT /api/v1/leave/applications/{id}/reject",
  "controller": "HR03LeaveCmdController",
  "service": "rejectLeaveServiceImpl",
  "permission": "leave:reject",
  "request": {
    "reason": "人力不足，請調整日期"
  },
  "businessRules": [
    {"rule": "請假申請必須存在且狀態為 PENDING"},
    {"rule": "駁回原因不可為空"},
    {"rule": "駁回後狀態改為 REJECTED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "leave_applications",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "REJECTED"},
        {"field": "approver_id", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "LeaveRejectedEvent",
      "payload": [
        {"field": "applicationId", "operator": "notNull"},
        {"field": "rejectedBy", "operator": "notNull"},
        {"field": "reason", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### ATT_CMD_L004: 取消請假

**API 端點：** `PUT /api/v1/leave/applications/{id}/cancel`

**業務場景描述：**

員工取消自己的請假申請。僅限 PENDING 或 APPROVED（尚未開始）的申請可取消。取消已核准的假期需恢復假別餘額。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_L004",
  "apiEndpoint": "PUT /api/v1/leave/applications/{id}/cancel",
  "controller": "HR03LeaveCmdController",
  "service": "cancelLeaveServiceImpl",
  "permission": "leave:cancel",
  "request": {},
  "businessRules": [
    {"rule": "請假申請必須存在"},
    {"rule": "只有 PENDING 或 APPROVED（尚未開始）的申請可取消"},
    {"rule": "取消已核准的假期需恢復假別餘額"},
    {"rule": "取消後狀態改為 CANCELLED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "leave_applications",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "CANCELLED"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "LeaveCancelledEvent",
      "payload": [
        {"field": "applicationId", "operator": "notNull"},
        {"field": "cancelledBy", "operator": "notNull"}
      ]
    }
  ]
}
```

---

### 1.3 加班管理

#### ATT_CMD_O001: 申請加班

**API 端點：** `POST /api/v1/overtime/applications`

**業務場景描述：**

員工提交加班申請。系統檢查當月加班時數是否超過上限（勞基法每月 46 小時），建立記錄後狀態為 PENDING。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_O001",
  "apiEndpoint": "POST /api/v1/overtime/applications",
  "controller": "HR03OvertimeCmdController",
  "service": "applyOvertimeServiceImpl",
  "permission": "overtime:apply",
  "request": {
    "employeeId": "E001",
    "date": "2025-02-10",
    "hours": 3.0,
    "overtimeType": "WORKDAY",
    "reason": "專案趕工"
  },
  "businessRules": [
    {"rule": "加班日期不可為過去超過 7 天"},
    {"rule": "當月累計加班時數不可超過上限"},
    {"rule": "同日期不可重複申請"},
    {"rule": "新申請狀態為 PENDING"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "overtime_applications",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "employee_id", "operator": "equals", "value": "E001"},
        {"field": "overtime_type", "operator": "equals", "value": "WORKDAY"},
        {"field": "hours", "operator": "equals", "value": 3.0},
        {"field": "status", "operator": "equals", "value": "PENDING"},
        {"field": "is_deleted", "operator": "equals", "value": 0}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "OvertimeAppliedEvent",
      "payload": [
        {"field": "applicationId", "operator": "notNull"},
        {"field": "employeeId", "operator": "equals", "value": "E001"},
        {"field": "hours", "operator": "equals", "value": 3.0}
      ]
    }
  ]
}
```

---

#### ATT_CMD_O002: 核准加班

**API 端點：** `PUT /api/v1/overtime/applications/{id}/approve`

**業務場景描述：**

主管核准加班申請。核准後加班時數計入當月統計。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_O002",
  "apiEndpoint": "PUT /api/v1/overtime/applications/{id}/approve",
  "controller": "HR03OvertimeCmdController",
  "service": "approveOvertimeServiceImpl",
  "permission": "overtime:approve",
  "request": {},
  "businessRules": [
    {"rule": "加班申請必須存在且狀態為 PENDING"},
    {"rule": "審核者須有核准權限"},
    {"rule": "核准後狀態改為 APPROVED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "overtime_applications",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "APPROVED"},
        {"field": "approver_id", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "OvertimeApprovedEvent",
      "payload": [
        {"field": "applicationId", "operator": "notNull"},
        {"field": "approvedBy", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### ATT_CMD_O003: 駁回加班

**API 端點：** `PUT /api/v1/overtime/applications/{id}/reject`

**業務場景描述：**

主管駁回加班申請。駁回須填寫原因。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_O003",
  "apiEndpoint": "PUT /api/v1/overtime/applications/{id}/reject",
  "controller": "HR03OvertimeCmdController",
  "service": "rejectOvertimeServiceImpl",
  "permission": "overtime:reject",
  "request": {
    "reason": "非緊急工作，請於正常時間完成"
  },
  "businessRules": [
    {"rule": "加班申請必須存在且狀態為 PENDING"},
    {"rule": "駁回原因不可為空"},
    {"rule": "駁回後狀態改為 REJECTED"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "overtime_applications",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "REJECTED"},
        {"field": "approver_id", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

### 1.4 班別管理

#### ATT_CMD_S001: 建立班別

**API 端點：** `POST /api/v1/shifts`

**業務場景描述：**

HR 人員建立新班別。班別代碼在組織內需唯一，新建班別預設為啟用狀態。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_S001",
  "apiEndpoint": "POST /api/v1/shifts",
  "controller": "HR03ShiftCmdController",
  "service": "createShiftServiceImpl",
  "permission": "shift:create",
  "request": {
    "shiftCode": "NIGHT-01",
    "shiftName": "夜班",
    "organizationId": "ORG001",
    "shiftType": "STANDARD",
    "workStartTime": "22:00",
    "workEndTime": "06:00",
    "lateToleranceMinutes": 5,
    "earlyLeaveToleranceMinutes": 0
  },
  "businessRules": [
    {"rule": "班別代碼在組織內必須唯一"},
    {"rule": "上班時間與下班時間不可相同"},
    {"rule": "新班別預設 is_active = true"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "shifts",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "code", "operator": "equals", "value": "NIGHT-01"},
        {"field": "name", "operator": "equals", "value": "夜班"},
        {"field": "is_active", "operator": "equals", "value": true}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### ATT_CMD_S002: 更新班別

**API 端點：** `PUT /api/v1/shifts/{id}`

**業務場景描述：**

HR 人員更新班別資訊（名稱、工時、容許時間等）。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_S002",
  "apiEndpoint": "PUT /api/v1/shifts/{id}",
  "controller": "HR03ShiftCmdController",
  "service": "updateShiftServiceImpl",
  "permission": "shift:write",
  "request": {
    "shiftName": "標準班（已更新）",
    "lateToleranceMinutes": 10
  },
  "businessRules": [
    {"rule": "班別必須存在且為啟用狀態"},
    {"rule": "更新不影響歷史出勤記錄"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "shifts",
      "count": 1,
      "assertions": [
        {"field": "name", "operator": "equals", "value": "標準班（已更新）"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### ATT_CMD_S003: 停用班別

**API 端點：** `PUT /api/v1/shifts/{id}/deactivate`

**業務場景描述：**

HR 人員停用班別。停用後不可再指派給員工，但不影響歷史記錄。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_S003",
  "apiEndpoint": "PUT /api/v1/shifts/{id}/deactivate",
  "controller": "HR03ShiftCmdController",
  "service": "deactivateShiftServiceImpl",
  "permission": "shift:deactivate",
  "request": {},
  "businessRules": [
    {"rule": "班別必須存在且為啟用狀態"},
    {"rule": "停用後 is_active = false"},
    {"rule": "若有員工正在使用此班別需警告"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "shifts",
      "count": 1,
      "assertions": [
        {"field": "is_active", "operator": "equals", "value": false},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

### 1.5 假別管理

#### ATT_CMD_T001: 建立假別

**API 端點：** `POST /api/v1/leave/types`

**業務場景描述：**

HR 人員建立新假別（如特休假、病假、事假等）。假別代碼在組織內需唯一，新建假別預設為啟用狀態。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_T001",
  "apiEndpoint": "POST /api/v1/leave/types",
  "controller": "HR03LeaveTypeCmdController",
  "service": "createLeaveTypeServiceImpl",
  "permission": "leavetype:create",
  "request": {
    "leaveTypeCode": "MATERNITY",
    "leaveTypeName": "產假",
    "organizationId": "ORG001",
    "isPaid": true,
    "annualQuotaDays": 56,
    "allowCarryOver": false,
    "requiresProof": true,
    "minimumDays": 1,
    "affectsAttendance": true,
    "applicableGender": "FEMALE",
    "description": "女性員工生產假"
  },
  "businessRules": [
    {"rule": "假別代碼在組織內必須唯一"},
    {"rule": "假別名稱不可為空"},
    {"rule": "新假別預設 is_active = true"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "leave_types",
      "count": 1,
      "assertions": [
        {"field": "id", "operator": "notNull"},
        {"field": "leave_type_code", "operator": "equals", "value": "MATERNITY"},
        {"field": "leave_type_name", "operator": "equals", "value": "產假"},
        {"field": "is_paid", "operator": "equals", "value": true},
        {"field": "is_active", "operator": "equals", "value": true}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### ATT_CMD_T002: 更新假別

**API 端點：** `PUT /api/v1/leave/types/{id}`

**業務場景描述：**

HR 人員更新假別資訊（名稱、額度、是否帶薪等）。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_T002",
  "apiEndpoint": "PUT /api/v1/leave/types/{id}",
  "controller": "HR03LeaveTypeCmdController",
  "service": "updateLeaveTypeServiceImpl",
  "permission": "leavetype:write",
  "request": {
    "leaveTypeName": "特休假（已更新）",
    "annualQuotaDays": 10
  },
  "businessRules": [
    {"rule": "假別必須存在且為啟用狀態"},
    {"rule": "更新不影響已核准的請假記錄"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "leave_types",
      "count": 1,
      "assertions": [
        {"field": "leave_type_name", "operator": "equals", "value": "特休假（已更新）"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### ATT_CMD_T003: 停用假別

**API 端點：** `PUT /api/v1/leave/types/{id}/deactivate`

**業務場景描述：**

HR 人員停用假別。停用後員工不可再使用此假別申請請假。

**測試合約：**

```json
{
  "scenarioId": "ATT_CMD_T003",
  "apiEndpoint": "PUT /api/v1/leave/types/{id}/deactivate",
  "controller": "HR03LeaveTypeCmdController",
  "service": "deactivateLeaveTypeServiceImpl",
  "permission": "leavetype:deactivate",
  "request": {},
  "businessRules": [
    {"rule": "假別必須存在且為啟用狀態"},
    {"rule": "停用後 is_active = false"},
    {"rule": "不影響已提交或已核准的請假申請"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "leave_types",
      "count": 1,
      "assertions": [
        {"field": "is_active", "operator": "equals", "value": false},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

## 2. Query 操作業務合約

### 2.1 出勤記錄查詢

#### ATT_QRY_A001: 查詢員工當日出勤

**API 端點：** `GET /api/v1/attendance/records?employeeId=E001&startDate=2025-01-15&endDate=2025-01-15`

**業務場景描述：**

HR 或主管查詢特定員工在指定日期的出勤記錄。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_A001",
  "apiEndpoint": "GET /api/v1/attendance/records",
  "controller": "HR03CheckInQryController",
  "service": "getAttendanceRecordsServiceImpl",
  "permission": "attendance:read",
  "request": {
    "employeeId": "E001",
    "startDate": "2025-01-15",
    "endDate": "2025-01-15"
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 1,
    "maxRecords": 1,
    "requiredFields": [
      {"name": "recordId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "attendanceDate", "type": "date", "notNull": true},
      {"name": "checkInTime", "type": "datetime"},
      {"name": "checkOutTime", "type": "datetime"},
      {"name": "status", "type": "string", "notNull": true}
    ],
    "assertions": [
      {"field": "employeeId", "operator": "equals", "value": "E001"}
    ]
  }
}
```

---

#### ATT_QRY_A002: 查詢部門月出勤

**API 端點：** `GET /api/v1/attendance/records?departmentId=D001&startDate=2025-01-15&endDate=2025-01-18`

**業務場景描述：**

HR 或主管查詢特定部門在指定期間的出勤記錄。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_A002",
  "apiEndpoint": "GET /api/v1/attendance/records",
  "controller": "HR03CheckInQryController",
  "service": "getAttendanceRecordsServiceImpl",
  "permission": "attendance:read",
  "request": {
    "departmentId": "D001",
    "startDate": "2025-01-15",
    "endDate": "2025-01-18"
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 1,
    "requiredFields": [
      {"name": "recordId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "attendanceDate", "type": "date", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

---

#### ATT_QRY_A003: 查詢異常出勤

**API 端點：** `GET /api/v1/attendance/records?status=ABNORMAL`

**業務場景描述：**

HR 查詢所有異常出勤記錄（缺卡、設備故障等）。測試資料中有 2 筆 ABNORMAL。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_A003",
  "apiEndpoint": "GET /api/v1/attendance/records",
  "controller": "HR03CheckInQryController",
  "service": "getAttendanceRecordsServiceImpl",
  "permission": "attendance:read",
  "request": {
    "status": "ABNORMAL"
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 2,
    "requiredFields": [
      {"name": "recordId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ],
    "assertions": [
      {"field": "status", "operator": "equals", "value": "ABNORMAL"}
    ]
  }
}
```

---

### 2.2 請假查詢

#### ATT_QRY_L001: 查詢待審核請假

**API 端點：** `GET /api/v1/leave/applications?status=PENDING`

**業務場景描述：**

主管或 HR 查詢所有待審核的請假申請。測試資料中有 3 筆 PENDING。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_L001",
  "apiEndpoint": "GET /api/v1/leave/applications",
  "controller": "HR03LeaveQryController",
  "service": "getLeaveApplicationsServiceImpl",
  "permission": "leave:read",
  "request": {
    "status": "PENDING"
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 3,
    "requiredFields": [
      {"name": "applicationId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "leaveTypeCode", "type": "string", "notNull": true},
      {"name": "startDate", "type": "date", "notNull": true},
      {"name": "endDate", "type": "date", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ],
    "assertions": [
      {"field": "status", "operator": "equals", "value": "PENDING"}
    ]
  }
}
```

---

#### ATT_QRY_L002: 查詢已核准請假

**API 端點：** `GET /api/v1/leave/applications?status=APPROVED`

**業務場景描述：**

查詢所有已核准的請假記錄。測試資料中有 3 筆 APPROVED。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_L002",
  "apiEndpoint": "GET /api/v1/leave/applications",
  "controller": "HR03LeaveQryController",
  "service": "getLeaveApplicationsServiceImpl",
  "permission": "leave:read",
  "request": {
    "status": "APPROVED"
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 3,
    "requiredFields": [
      {"name": "applicationId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ],
    "assertions": [
      {"field": "status", "operator": "equals", "value": "APPROVED"}
    ]
  }
}
```

---

#### ATT_QRY_L003: 查詢特休假申請

**API 端點：** `GET /api/v1/leave/applications?leaveTypeId=ANNUAL`

**業務場景描述：**

HR 查詢所有特休假申請記錄。測試資料中有 4 筆 leaveTypeId=ANNUAL。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_L003",
  "apiEndpoint": "GET /api/v1/leave/applications",
  "controller": "HR03LeaveQryController",
  "service": "getLeaveApplicationsServiceImpl",
  "permission": "leave:read",
  "request": {
    "leaveTypeId": "ANNUAL"
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 4,
    "requiredFields": [
      {"name": "applicationId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "leaveTypeCode", "type": "string", "notNull": true}
    ],
    "assertions": [
      {"field": "leaveTypeCode", "operator": "equals", "value": "ANNUAL"}
    ]
  }
}
```

---

#### ATT_QRY_L004: 查詢假期餘額

**API 端點：** `GET /api/v1/leave/balances/{employeeId}`

**業務場景描述：**

員工或 HR 查詢特定員工的各假別餘額。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_L004",
  "apiEndpoint": "GET /api/v1/leave/balances/{employeeId}",
  "controller": "HR03LeaveQryController",
  "service": "getLeaveBalanceServiceImpl",
  "permission": "leave:read",
  "request": {
    "employeeId": "E001"
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

### 2.3 加班查詢

#### ATT_QRY_O001: 查詢待審核加班

**API 端點：** `GET /api/v1/overtime/applications?status=PENDING`

**業務場景描述：**

主管或 HR 查詢所有待審核的加班申請。測試資料中有 2 筆 PENDING。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_O001",
  "apiEndpoint": "GET /api/v1/overtime/applications",
  "controller": "HR03OvertimeQryController",
  "service": "getOvertimeApplicationsServiceImpl",
  "permission": "overtime:read",
  "request": {
    "status": "PENDING"
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 2,
    "requiredFields": [
      {"name": "applicationId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "overtimeType", "type": "string", "notNull": true},
      {"name": "overtimeHours", "type": "number", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ],
    "assertions": [
      {"field": "status", "operator": "equals", "value": "PENDING"}
    ]
  }
}
```

---

#### ATT_QRY_O002: 查詢已核准加班

**API 端點：** `GET /api/v1/overtime/applications?status=APPROVED`

**業務場景描述：**

查詢所有已核准的加班記錄。測試資料中有 3 筆 APPROVED。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_O002",
  "apiEndpoint": "GET /api/v1/overtime/applications",
  "controller": "HR03OvertimeQryController",
  "service": "getOvertimeApplicationsServiceImpl",
  "permission": "overtime:read",
  "request": {
    "status": "APPROVED"
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 3,
    "requiredFields": [
      {"name": "applicationId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ],
    "assertions": [
      {"field": "status", "operator": "equals", "value": "APPROVED"}
    ]
  }
}
```

---

#### ATT_QRY_O003: 查詢平日加班

**API 端點：** `GET /api/v1/overtime/applications?overtimeType=WORKDAY`

**業務場景描述：**

HR 查詢所有平日加班記錄。測試資料中有 4 筆 overtimeType=WORKDAY。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_O003",
  "apiEndpoint": "GET /api/v1/overtime/applications",
  "controller": "HR03OvertimeQryController",
  "service": "getOvertimeApplicationsServiceImpl",
  "permission": "overtime:read",
  "request": {
    "overtimeType": "WORKDAY"
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 4,
    "requiredFields": [
      {"name": "applicationId", "type": "string", "notNull": true},
      {"name": "employeeId", "type": "string", "notNull": true},
      {"name": "overtimeType", "type": "string", "notNull": true}
    ],
    "assertions": [
      {"field": "overtimeType", "operator": "equals", "value": "WORKDAY"}
    ]
  }
}
```

---

### 2.4 班別查詢

#### ATT_QRY_S001: 查詢班別列表

**API 端點：** `GET /api/v1/shifts`

**業務場景描述：**

HR 查詢所有啟用中的班別列表。測試資料中至少有 1 筆班別。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_S001",
  "apiEndpoint": "GET /api/v1/shifts",
  "controller": "HR03ShiftQryController",
  "service": "getShiftListServiceImpl",
  "permission": "shift:read",
  "request": {},
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 1
  }
}
```

---

### 2.5 假別查詢

#### ATT_QRY_T001: 查詢假別列表

**API 端點：** `GET /api/v1/leave/types`

**業務場景描述：**

HR 或員工查詢所有啟用中的假別列表。

**測試合約：**

```json
{
  "scenarioId": "ATT_QRY_T001",
  "apiEndpoint": "GET /api/v1/leave/types",
  "controller": "HR03LeaveTypeQryController",
  "service": "getLeaveTypeListServiceImpl",
  "permission": "leavetype:read",
  "request": {},
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

## 3. 測試資料規格

### 3.1 出勤記錄 (attendance_records) - 10 筆

| ID | 員工 | 日期 | 上班 | 下班 | 狀態 | 遲到 | 早退 |
|:---|:---|:---|:---|:---|:---|:---|:---|
| AR001 | E001 | 2025-01-15 | 09:00 | 18:00 | NORMAL | false | false |
| AR002 | E001 | 2025-01-16 | 08:55 | 18:05 | NORMAL | false | false |
| AR003 | E002 | 2025-01-15 | 09:00 | 18:00 | NORMAL | false | false |
| AR004 | E002 | 2025-01-16 | 09:00 | 18:00 | NORMAL | false | false |
| AR005 | E003 | 2025-01-15 | 09:00 | 18:00 | NORMAL | false | false |
| AR006 | E003 | 2025-01-16 | 09:00 | 18:00 | NORMAL | false | false |
| AR007 | E001 | 2025-01-17 | 09:30 | NULL | ABNORMAL | true | false |
| AR008 | E002 | 2025-01-17 | NULL | 18:00 | ABNORMAL | false | false |
| AR009 | E003 | 2025-01-17 | 09:15 | 18:00 | NORMAL | true | false |
| AR010 | E001 | 2025-01-18 | 09:00 | 17:00 | NORMAL | false | true |

### 3.2 請假申請 (leave_applications) - 8 筆

| ID | 員工 | 部門 | 假別 | 狀態 |
|:---|:---|:---|:---|:---|
| LA001 | E001 | D001 | ANNUAL | PENDING |
| LA002 | E002 | D001 | SICK | PENDING |
| LA003 | E003 | D002 | PERSONAL | PENDING |
| LA004 | E001 | D001 | ANNUAL | APPROVED |
| LA005 | E002 | D001 | SICK | APPROVED |
| LA006 | E003 | D002 | ANNUAL | APPROVED |
| LA007 | E001 | D001 | PERSONAL | REJECTED |
| LA008 | E002 | D001 | ANNUAL | REJECTED |

### 3.3 加班申請 (overtime_applications) - 6 筆

| ID | 員工 | 部門 | 類型 | 狀態 |
|:---|:---|:---|:---|:---|
| OT001 | E001 | D001 | WORKDAY | PENDING |
| OT002 | E002 | D001 | HOLIDAY | PENDING |
| OT003 | E001 | D001 | WORKDAY | APPROVED |
| OT004 | E003 | D002 | HOLIDAY | APPROVED |
| OT005 | E002 | D001 | WORKDAY | APPROVED |
| OT006 | E003 | D002 | WORKDAY | REJECTED |

---

## 擴充功能合約（2026-03-05 新增）

### 彈性工時判斷

#### ATT_FLEX_001 — 彈性工時遲到判斷

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | ATT_FLEX_001 |
| **場景名稱** | 彈性班別遲到判定 |
| **前置條件** | 員工指定班別為 FLEXIBLE 類型, flexStartTime/flexEndTime 已設定 |
| **輸入** | checkInTime, shift (type=FLEXIBLE) |
| **預期行為** | flexStartTime ≤ checkInTime ≤ flexEndTime → 正常; 超過 flexEndTime → 遲到 |
| **輸出** | isLate (boolean), actualCheckIn |
| **業務規則** | 彈性區間內打卡皆為正常, 工時以實際打卡時間計算（非班別固定起迄） |

#### ATT_FLEX_002 — 彈性班別工時計算

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | ATT_FLEX_002 |
| **場景名稱** | 彈性班別實際工時計算 |
| **前置條件** | 員工打卡上下班完成, 班別 type=FLEXIBLE |
| **輸入** | checkInTime, checkOutTime, shift |
| **預期行為** | 工時 = checkOutTime - checkInTime - 午休時數 |
| **輸出** | workingHours (decimal), overtimeHours (decimal) |
| **業務規則** | 超過 standardHours 部分計為加班, 需減去 breakDuration |

---

### 輪班/值班排程管理

#### ATT_SCHED_001 — 建立排班表

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | ATT_SCHED_001 |
| **場景名稱** | 建立員工排班記錄 |
| **前置條件** | 班別已存在, 員工已存在 |
| **輸入** | employeeId, shiftId, scheduleDate |
| **預期行為** | 建立 DRAFT 狀態排班記錄 |
| **輸出** | ShiftSchedule (status=DRAFT) |
| **副作用** | shift_schedules 表 INSERT |
| **業務規則** | 初始狀態 DRAFT, LOCKED 狀態不可變更 |

#### ATT_SCHED_002 — 發佈排班表

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | ATT_SCHED_002 |
| **場景名稱** | 發佈排班表（DRAFT → PUBLISHED） |
| **前置條件** | 排班記錄存在, 狀態為 DRAFT |
| **輸入** | scheduleId |
| **預期行為** | 狀態變更為 PUBLISHED |
| **輸出** | ShiftSchedule (status=PUBLISHED) |
| **副作用** | shift_schedules 表 UPDATE |

#### ATT_SCHED_003 — 鎖定排班表

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | ATT_SCHED_003 |
| **場景名稱** | 鎖定排班表（PUBLISHED → LOCKED） |
| **前置條件** | 排班記錄存在, 狀態為 PUBLISHED |
| **輸入** | scheduleId |
| **預期行為** | 狀態變更為 LOCKED, 之後不可修改班別 |
| **輸出** | ShiftSchedule (status=LOCKED) |
| **業務規則** | LOCKED 後 changeShift() 拋出 IllegalStateException |

#### ATT_SCHED_004 — 自動產生輪班排程

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | ATT_SCHED_004 |
| **場景名稱** | 依輪班模式自動產生排班 |
| **前置條件** | RotationPattern 已建立, cycleDays/rotationDays 已設定 |
| **輸入** | employeeId, patternId, startDate, endDate, rotationStartDate |
| **預期行為** | 依循環天序產生排班記錄, 休息日跳過 |
| **輸出** | List\<ShiftSchedule\> |
| **副作用** | shift_schedules 表 INSERT (多筆) |
| **業務規則** | getDayForIndex 使用 modulo 循環, restDay 不產生排班 |

#### ATT_SCHED_005 — 換班申請

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | ATT_SCHED_005 |
| **場景名稱** | 員工換班申請（兩階段審核） |
| **前置條件** | 申請人 ≠ 被換人, 兩人排班存在 |
| **輸入** | requesterId, counterpartId, requesterScheduleId, counterpartScheduleId, reason |
| **預期行為** | 建立 PENDING_COUNTERPART 狀態 → 對方接受 → PENDING_APPROVAL → 主管核准 → APPROVED → 自動交換班別 |
| **輸出** | ShiftSwapRequest |
| **副作用** | shift_swap_requests 表 INSERT, shift_schedules 表 UPDATE (交換 shiftId) |
| **業務規則** | 狀態流: PENDING_COUNTERPART → PENDING_APPROVAL → APPROVED/REJECTED/CANCELLED |

---

### 曠職自動判定

#### ATT_ABSENT_001 — 曠職自動判定排程

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | ATT_ABSENT_001 |
| **場景名稱** | 排程自動判定曠職 |
| **前置條件** | 前一工作日已過, 員工有排班但無出勤記錄 |
| **輸入** | targetDate (預設: 前一工作日) |
| **預期行為** | 查詢無出勤記錄的排班員工 → 建立 ABSENT 記錄 |
| **輸出** | 曠職記錄數量 |
| **副作用** | attendance_records 表 INSERT (status=ABSENT) |
