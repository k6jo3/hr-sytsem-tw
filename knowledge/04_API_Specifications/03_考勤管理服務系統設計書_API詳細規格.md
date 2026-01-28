# HR03 考勤管理服務 API 詳細規格

**版本:** 1.0
**建立日期:** 2025-12-29
**服務代碼:** HR03
**服務名稱:** 考勤管理服務 (Attendance Service)

---

## 目錄

1. [API 總覽](#1-api-總覽)
2. [打卡管理 API](#2-打卡管理-api)
3. [請假管理 API](#3-請假管理-api)
4. [加班管理 API](#4-加班管理-api)
5. [班別管理 API](#5-班別管理-api)
6. [假別管理 API](#6-假別管理-api)
7. [報表結算 API](#7-報表結算-api)
8. [附錄：列舉值定義](#8-附錄列舉值定義)

---

## 1. API 總覽

### 1.1 端點統計

| 模組 | API 數量 | 說明 |
|:---|:---:|:---|
| 打卡管理 | 6 | 上下班打卡、查詢記錄、補卡申請/審核 |
| 請假管理 | 8 | 假期餘額、請假申請/審核/取消 |
| 加班管理 | 6 | 加班申請/審核、統計查詢 |
| 班別管理 | 4 | 班別 CRUD |
| 假別管理 | 4 | 假別 CRUD |
| 報表結算 | 3 | 月報、月結算 |
| **合計** | **31** | |

### 1.2 Controller 對照表

| Controller | 說明 | API 數量 |
|:---|:---|:---:|
| `HR03CheckInCmdController` | 打卡 Command 操作 | 3 |
| `HR03CheckInQryController` | 打卡記錄 Query 操作 | 3 |
| `HR03LeaveCmdController` | 請假 Command 操作 | 5 |
| `HR03LeaveQryController` | 請假 Query 操作 | 3 |
| `HR03OvertimeCmdController` | 加班 Command 操作 | 4 |
| `HR03OvertimeQryController` | 加班 Query 操作 | 2 |
| `HR03ShiftCmdController` | 班別管理 Command 操作 | 3 |
| `HR03ShiftQryController` | 班別 Query 操作 | 1 |
| `HR03LeaveTypeCmdController` | 假別管理 Command 操作 | 3 |
| `HR03LeaveTypeQryController` | 假別 Query 操作 | 1 |
| `HR03MonthCloseCmdController` | 月結 Command 操作 | 1 |
| `HR03ReportQryController` | 報表 Query 操作 | 2 |

### 1.3 通用 Headers

所有 API 請求需包含以下 Headers：

| Header | 必填 | 說明 |
|:---|:---:|:---|
| `Authorization` | Y | Bearer Token (JWT) |
| `Content-Type` | Y | `application/json` |
| `X-Tenant-Id` | Y | 租戶識別碼 |
| `X-Request-Id` | N | 請求追蹤 ID |

### 1.4 通用錯誤回應格式

```json
{
  "success": false,
  "code": "ATT_ALREADY_CHECKED_IN",
  "message": "今日已完成上班打卡",
  "timestamp": "2025-12-06T09:10:00Z",
  "path": "/api/v1/attendance/check-in",
  "details": null
}
```

---

## 2. 打卡管理 API

### 2.1 上班打卡

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/attendance/check-in` |
| **方法** | POST |
| **Controller** | `HR03CheckInCmdController` |
| **Service** | `CheckInServiceImpl` |
| **權限** | `attendance:checkin` |

#### 用途說明

- **業務場景:** 員工上班打卡
- **使用者:** 全體員工
- **解決問題:** 記錄員工到達辦公地點的時間，並檢測是否遲到

#### 業務邏輯

1. **驗證規則:**
   - 驗證員工 ID 是否存在且狀態為在職
   - 驗證今日是否已完成上班打卡
   - 驗證打卡位置是否在允許範圍內（若啟用 GPS 驗證）
   - 驗證員工是否已設定班別

2. **處理步驟:**
   - 取得員工今日排定的班別
   - 根據班別的上班時間與遲到容許計算是否遲到
   - 建立打卡記錄並標記異常狀態
   - 若遲到，發布 `AttendanceAnomalyDetectedEvent`
   - 發布 `AttendanceRecordedEvent`

3. **遲到判定:**
   - 遲到 = 打卡時間 > (上班時間 + 遲到容許分鐘)
   - 遲到分鐘數 = 打卡時間 - 上班時間

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `employeeId` | string (UUID) | Y | 有效 UUID | 員工 ID |
| `checkInTime` | datetime | Y | ISO 8601 格式 | 打卡時間 |
| `location` | object | N | - | GPS 定位資訊 |
| `location.latitude` | number | N | -90 ~ 90 | 緯度 |
| `location.longitude` | number | N | -180 ~ 180 | 經度 |
| `ipAddress` | string | N | 有效 IP 格式 | 打卡 IP 位址 |

**Request 範例:**

```json
{
  "employeeId": "550e8400-e29b-41d4-a716-446655440000",
  "checkInTime": "2025-12-06T09:05:00",
  "location": {
    "latitude": 25.0330,
    "longitude": 121.5654
  },
  "ipAddress": "192.168.1.100"
}
```

#### Response 規格

**成功回應 (200 OK):**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| `recordId` | string | 打卡記錄 ID |
| `checkInTime` | datetime | 打卡時間 |
| `isLate` | boolean | 是否遲到 |
| `lateMinutes` | integer | 遲到分鐘數 |
| `shift` | object | 班別資訊 |
| `shift.shiftName` | string | 班別名稱 |
| `shift.workStartTime` | string | 上班時間 |
| `shift.lateToleranceMinutes` | integer | 遲到容許分鐘 |

**Response 範例:**

```json
{
  "success": true,
  "data": {
    "recordId": "550e8400-e29b-41d4-a716-446655440001",
    "checkInTime": "2025-12-06T09:05:00",
    "isLate": true,
    "lateMinutes": 5,
    "shift": {
      "shiftName": "標準班",
      "workStartTime": "09:00",
      "lateToleranceMinutes": 0
    }
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `ATT_ALREADY_CHECKED_IN` | 今日已完成上班打卡 | 提示用戶已打過卡 |
| 400 | `ATT_LOCATION_OUT_OF_RANGE` | 打卡位置不在允許範圍內 | 提示用戶確認位置 |
| 404 | `ATT_NO_SHIFT_ASSIGNED` | 員工未設定班別 | 請聯繫 HR 設定班別 |
| 404 | `EMP_NOT_FOUND` | 員工不存在 | 檢查員工 ID |

#### 領域事件

**AttendanceRecordedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.attendance.recorded` |
| **觸發時機** | 打卡成功後 |
| **訂閱服務** | - |

```json
{
  "eventId": "evt-att-001",
  "eventType": "AttendanceRecorded",
  "timestamp": "2025-12-06T09:05:00Z",
  "payload": {
    "recordId": "550e8400-e29b-41d4-a716-446655440001",
    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
    "recordDate": "2025-12-06",
    "checkInTime": "2025-12-06T09:05:00",
    "checkOutTime": null,
    "isLate": true,
    "lateMinutes": 5
  }
}
```

**AttendanceAnomalyDetectedEvent (若遲到):**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.attendance.anomaly-detected` |
| **觸發時機** | 偵測到遲到/早退/異常 |
| **訂閱服務** | Notification Service |

```json
{
  "eventId": "evt-att-002",
  "eventType": "AttendanceAnomalyDetected",
  "timestamp": "2025-12-06T09:05:00Z",
  "payload": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
    "recordDate": "2025-12-06",
    "anomalyType": "LATE",
    "anomalyMinutes": 5
  }
}
```

---

### 2.2 下班打卡

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/attendance/check-out` |
| **方法** | POST |
| **Controller** | `HR03CheckInCmdController` |
| **Service** | `CheckOutServiceImpl` |
| **權限** | `attendance:checkout` |

#### 用途說明

- **業務場景:** 員工下班打卡
- **使用者:** 全體員工
- **解決問題:** 記錄員工離開辦公地點的時間，計算工時並檢測早退

#### 業務邏輯

1. **驗證規則:**
   - 驗證今日是否已有上班打卡記錄
   - 驗證今日是否已完成下班打卡
   - 驗證打卡位置是否在允許範圍內（若啟用）

2. **處理步驟:**
   - 根據班別的下班時間與早退容許計算是否早退
   - 計算實際工時（扣除休息時間）
   - 更新打卡記錄
   - 若早退，發布 `AttendanceAnomalyDetectedEvent`

3. **早退判定:**
   - 早退 = 打卡時間 < (下班時間 - 早退容許分鐘)

4. **工時計算:**
   - 工時 = 下班時間 - 上班時間 - 休息時間

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `employeeId` | string (UUID) | Y | 有效 UUID | 員工 ID |
| `checkOutTime` | datetime | Y | ISO 8601 格式 | 打卡時間 |
| `location` | object | N | - | GPS 定位資訊 |
| `location.latitude` | number | N | -90 ~ 90 | 緯度 |
| `location.longitude` | number | N | -180 ~ 180 | 經度 |
| `ipAddress` | string | N | 有效 IP 格式 | 打卡 IP 位址 |

**Request 範例:**

```json
{
  "employeeId": "550e8400-e29b-41d4-a716-446655440000",
  "checkOutTime": "2025-12-06T18:30:00",
  "location": {
    "latitude": 25.0330,
    "longitude": 121.5654
  },
  "ipAddress": "192.168.1.100"
}
```

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "recordId": "550e8400-e29b-41d4-a716-446655440001",
    "checkInTime": "2025-12-06T09:05:00",
    "checkOutTime": "2025-12-06T18:30:00",
    "workingHours": 8.42,
    "isEarlyLeave": false,
    "earlyLeaveMinutes": 0
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `ATT_ALREADY_CHECKED_OUT` | 今日已完成下班打卡 | 提示用戶已打過卡 |
| 400 | `ATT_NO_CHECK_IN_RECORD` | 今日尚未上班打卡 | 提示用戶先打上班卡或申請補卡 |
| 400 | `ATT_LOCATION_OUT_OF_RANGE` | 打卡位置不在允許範圍內 | 提示用戶確認位置 |

---

### 2.3 查詢今日打卡狀態

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/attendance/today` |
| **方法** | GET |
| **Controller** | `HR03CheckInQryController` |
| **Service** | `GetTodayAttendanceServiceImpl` |
| **權限** | `attendance:read` |

#### 用途說明

- **業務場景:** 查詢當日打卡狀態及班別資訊
- **使用者:** 全體員工
- **解決問題:** 顯示打卡頁面所需的所有資訊

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `employeeId` | string (UUID) | Y | 員工 ID |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
    "date": "2025-12-06",
    "shift": {
      "shiftId": "shift-001",
      "shiftName": "標準班",
      "shiftType": "STANDARD",
      "workStartTime": "09:00",
      "workEndTime": "18:00",
      "breakStartTime": "12:00",
      "breakEndTime": "13:00",
      "lateToleranceMinutes": 5,
      "earlyLeaveToleranceMinutes": 0
    },
    "record": {
      "recordId": "record-001",
      "checkInTime": "2025-12-06T08:55:00",
      "checkOutTime": null,
      "isLate": false,
      "isEarlyLeave": false,
      "workingHours": null
    },
    "canCheckIn": false,
    "canCheckOut": true
  }
}
```

---

### 2.4 查詢打卡記錄列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/attendance/records` |
| **方法** | GET |
| **Controller** | `HR03CheckInQryController` |
| **Service** | `GetAttendanceRecordsServiceImpl` |
| **權限** | `attendance:read` |

#### 用途說明

- **業務場景:** 查詢員工的打卡記錄
- **使用者:** 員工本人、主管、HR
- **解決問題:** 提供打卡記錄的查詢與匯出

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `employeeId` | string (UUID) | Y | 員工 ID |
| `startDate` | date | Y | 起始日期 (YYYY-MM-DD) |
| `endDate` | date | Y | 結束日期 (YYYY-MM-DD) |
| `anomalyOnly` | boolean | N | 僅顯示異常記錄 (預設 false) |
| `page` | integer | N | 頁碼 (預設 1) |
| `pageSize` | integer | N | 每頁筆數 (預設 20，最大 100) |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "records": [
      {
        "recordId": "record-001",
        "recordDate": "2025-12-06",
        "shiftName": "標準班",
        "checkInTime": "2025-12-06T09:05:00",
        "checkOutTime": "2025-12-06T18:30:00",
        "workingHours": 8.42,
        "isLate": true,
        "lateMinutes": 5,
        "isEarlyLeave": false,
        "earlyLeaveMinutes": 0,
        "anomalyType": "LATE",
        "isCorrected": false
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "totalRecords": 22,
      "totalPages": 2
    },
    "summary": {
      "totalWorkDays": 22,
      "actualWorkDays": 20,
      "lateCount": 3,
      "earlyLeaveCount": 1,
      "absentCount": 2
    }
  }
}
```

---

### 2.5 提交補卡申請

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/attendance/corrections` |
| **方法** | POST |
| **Controller** | `HR03CheckInCmdController` |
| **Service** | `CreateCorrectionServiceImpl` |
| **權限** | `attendance:correction:create` |

#### 用途說明

- **業務場景:** 員工忘記打卡時申請補卡
- **使用者:** 全體員工
- **解決問題:** 處理漏打卡的異常情況

#### 業務邏輯

1. **驗證規則:**
   - 驗證該日期的打卡記錄是否存在
   - 驗證補卡類型 (上班/下班) 的時間是否合理
   - 驗證是否在允許的補卡期限內

2. **處理步驟:**
   - 建立補卡申請記錄
   - 送出簽核流程
   - 發布 `CorrectionRequestedEvent`

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `employeeId` | string (UUID) | Y | 有效 UUID | 員工 ID |
| `recordDate` | date | Y | YYYY-MM-DD 格式 | 補卡日期 |
| `correctionType` | string | Y | `CHECK_IN` 或 `CHECK_OUT` | 補卡類型 |
| `correctedTime` | datetime | Y | ISO 8601 格式 | 補卡時間 |
| `reason` | string | Y | 1-500 字元 | 補卡原因 |

**Request 範例:**

```json
{
  "employeeId": "550e8400-e29b-41d4-a716-446655440000",
  "recordDate": "2025-12-05",
  "correctionType": "CHECK_IN",
  "correctedTime": "2025-12-05T09:00:00",
  "reason": "開會後忘記打卡"
}
```

#### Response 規格

**成功回應 (201 Created):**

```json
{
  "success": true,
  "data": {
    "correctionId": "corr-001",
    "status": "PENDING",
    "workflowInstanceId": "wf-001",
    "createdAt": "2025-12-06T10:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `ATT_CORRECTION_EXPIRED` | 超過補卡申請期限 | 提示期限規定 |
| 400 | `ATT_DUPLICATE_CORRECTION` | 已有相同補卡申請 | 提示檢查現有申請 |
| 404 | `ATT_RECORD_NOT_FOUND` | 該日無打卡記錄 | 確認日期是否正確 |

---

### 2.6 審核補卡申請

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/attendance/corrections/{correctionId}/approve` |
| **方法** | PUT |
| **Controller** | `HR03CheckInCmdController` |
| **Service** | `ApproveCorrectionServiceImpl` |
| **權限** | `attendance:correction:approve` |

#### 用途說明

- **業務場景:** 主管審核員工的補卡申請
- **使用者:** 直屬主管、HR
- **解決問題:** 完成補卡審核流程

#### 業務邏輯

1. **處理步驟:**
   - 驗證審核人權限
   - 更新補卡申請狀態為 APPROVED
   - 更新原打卡記錄的時間
   - 清除異常標記

#### Request 規格

**Path Parameters:**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| `correctionId` | string (UUID) | 補卡申請 ID |

**Request Body:**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `comment` | string | N | 審核備註 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "correctionId": "corr-001",
    "status": "APPROVED",
    "approvedBy": "mgr-001",
    "approvedAt": "2025-12-06T11:00:00Z"
  }
}
```

---

## 3. 請假管理 API

### 3.1 查詢假期餘額

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/leave/balances` |
| **方法** | GET |
| **Controller** | `HR03LeaveQryController` |
| **Service** | `GetLeaveBalancesServiceImpl` |
| **權限** | `leave:balance:read` |

#### 用途說明

- **業務場景:** 查詢員工各類假別的剩餘天數
- **使用者:** 員工本人、HR
- **解決問題:** 提供請假申請前的餘額查詢

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `employeeId` | string (UUID) | Y | 員工 ID |
| `year` | integer | N | 年度 (預設當年) |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "employeeId": "emp-001",
    "year": 2025,
    "balances": [
      {
        "leaveTypeId": "lt-annual",
        "leaveTypeName": "特休假",
        "leaveCode": "ANNUAL",
        "totalDays": 10,
        "usedDays": 3.5,
        "remainingDays": 6.5,
        "unit": "FULL_DAY",
        "expiryDate": "2026-12-31",
        "isExpiringSoon": false
      },
      {
        "leaveTypeId": "lt-sick",
        "leaveTypeName": "病假",
        "leaveCode": "SICK",
        "totalDays": 30,
        "usedDays": 2,
        "remainingDays": 28,
        "unit": "HALF_DAY",
        "expiryDate": null,
        "isExpiringSoon": false
      },
      {
        "leaveTypeId": "lt-personal",
        "leaveTypeName": "事假",
        "leaveCode": "PERSONAL",
        "totalDays": 14,
        "usedDays": 1,
        "remainingDays": 13,
        "unit": "HALF_DAY",
        "expiryDate": null,
        "isExpiringSoon": false
      }
    ]
  }
}
```

---

### 3.2 提交請假申請

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/leave/applications` |
| **方法** | POST |
| **Controller** | `HR03LeaveCmdController` |
| **Service** | `CreateLeaveApplicationServiceImpl` |
| **權限** | `leave:application:create` |

#### 用途說明

- **業務場景:** 員工提交請假申請
- **使用者:** 全體員工
- **解決問題:** 建立請假申請並啟動審核流程

#### 業務邏輯

1. **驗證規則:**
   - 驗證假別餘額是否足夠
   - 驗證日期是否與其他申請重疊
   - 驗證是否需要上傳證明文件（如病假、婚假）
   - 驗證請假天數計算正確性

2. **處理步驟:**
   - 計算請假天數（含半天處理）
   - 建立請假申請記錄
   - 啟動簽核流程
   - 發布 `LeaveAppliedEvent`

3. **天數計算:**
   - FULL_DAY = 1 天
   - AM 或 PM = 0.5 天
   - 跨日計算含週末/假日排除邏輯

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `employeeId` | string (UUID) | Y | 有效 UUID | 員工 ID |
| `leaveTypeId` | string (UUID) | Y | 有效假別 ID | 假別 ID |
| `startDate` | date | Y | YYYY-MM-DD | 起始日期 |
| `endDate` | date | Y | >= startDate | 結束日期 |
| `startPeriod` | string | Y | `AM`, `PM`, `FULL_DAY` | 起始時段 |
| `endPeriod` | string | Y | `AM`, `PM`, `FULL_DAY` | 結束時段 |
| `reason` | string | Y | 1-500 字元 | 請假原因 |
| `proofAttachmentUrl` | string | C | 有效 URL | 證明文件 URL (特定假別必填) |

**Request 範例:**

```json
{
  "employeeId": "emp-001",
  "leaveTypeId": "lt-annual",
  "startDate": "2025-12-09",
  "endDate": "2025-12-10",
  "startPeriod": "FULL_DAY",
  "endPeriod": "FULL_DAY",
  "reason": "家庭事務",
  "proofAttachmentUrl": null
}
```

#### Response 規格

**成功回應 (201 Created):**

```json
{
  "success": true,
  "data": {
    "applicationId": "app-001",
    "totalDays": 2,
    "remainingBalance": 4.5,
    "status": "PENDING",
    "workflowInstanceId": "wf-001",
    "createdAt": "2025-12-06T10:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `LEAVE_INSUFFICIENT_BALANCE` | 假期餘額不足 | 顯示剩餘天數 |
| 400 | `LEAVE_DATE_OVERLAP` | 日期與其他申請重疊 | 提示已有申請的日期 |
| 400 | `LEAVE_PROOF_REQUIRED` | 此假別需要上傳證明文件 | 提示用戶上傳文件 |
| 400 | `LEAVE_INVALID_DATE_RANGE` | 日期範圍無效 | 檢查起訖日期 |

#### 領域事件

**LeaveAppliedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.leave.applied` |
| **觸發時機** | 請假申請提交後 |
| **訂閱服務** | Workflow Service |

```json
{
  "eventId": "evt-lv-001",
  "eventType": "LeaveApplied",
  "timestamp": "2025-12-06T10:00:00Z",
  "payload": {
    "applicationId": "app-001",
    "employeeId": "emp-001",
    "leaveTypeId": "lt-annual",
    "totalDays": 2
  }
}
```

---

### 3.3 查詢請假申請列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/leave/applications` |
| **方法** | GET |
| **Controller** | `HR03LeaveQryController` |
| **Service** | `GetLeaveApplicationsServiceImpl` |
| **權限** | `leave:application:read` |

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `employeeId` | string (UUID) | N | 員工 ID (主管查詢用) |
| `status` | string | N | 狀態篩選 |
| `startDate` | date | N | 起始日期 |
| `endDate` | date | N | 結束日期 |
| `page` | integer | N | 頁碼 |
| `pageSize` | integer | N | 每頁筆數 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "applications": [
      {
        "applicationId": "app-001",
        "employeeId": "emp-001",
        "employeeName": "王小明",
        "leaveTypeName": "特休假",
        "startDate": "2025-12-09",
        "endDate": "2025-12-10",
        "totalDays": 2,
        "status": "PENDING",
        "reason": "家庭事務",
        "appliedAt": "2025-12-06T10:00:00Z",
        "approverName": null,
        "approvedAt": null
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "totalRecords": 5,
      "totalPages": 1
    }
  }
}
```

---

### 3.4 查詢請假申請詳情

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/leave/applications/{applicationId}` |
| **方法** | GET |
| **Controller** | `HR03LeaveQryController` |
| **Service** | `GetLeaveApplicationDetailServiceImpl` |
| **權限** | `leave:application:read` |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "applicationId": "app-001",
    "employeeId": "emp-001",
    "employeeName": "王小明",
    "departmentName": "資訊部",
    "leaveTypeId": "lt-annual",
    "leaveTypeName": "特休假",
    "leaveCode": "ANNUAL",
    "startDate": "2025-12-09",
    "endDate": "2025-12-10",
    "startPeriod": "FULL_DAY",
    "endPeriod": "FULL_DAY",
    "totalDays": 2,
    "reason": "家庭事務",
    "proofAttachmentUrl": null,
    "status": "PENDING",
    "appliedAt": "2025-12-06T10:00:00Z",
    "approverName": null,
    "approvedAt": null,
    "rejectionReason": null,
    "workflowHistory": [
      {
        "step": 1,
        "approverName": "李經理",
        "action": null,
        "actionAt": null,
        "comment": null
      }
    ]
  }
}
```

---

### 3.5 核准請假申請

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/leave/applications/{applicationId}/approve` |
| **方法** | PUT |
| **Controller** | `HR03LeaveCmdController` |
| **Service** | `ApproveLeaveServiceImpl` |
| **權限** | `leave:application:approve` |

#### 用途說明

- **業務場景:** 主管核准請假申請
- **使用者:** 直屬主管、HR
- **解決問題:** 完成請假審核流程

#### 業務邏輯

1. **處理步驟:**
   - 驗證審核人權限
   - 更新申請狀態為 APPROVED
   - 扣除假期餘額
   - 發布 `LeaveApprovedEvent`

#### Request 規格

**Path Parameters:**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| `applicationId` | string (UUID) | 申請 ID |

**Request Body:**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `comment` | string | N | 審核備註 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "applicationId": "app-001",
    "status": "APPROVED",
    "approvedBy": "李經理",
    "approvedAt": "2025-12-06T10:30:00Z"
  }
}
```

#### 領域事件

**LeaveApprovedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.leave.approved` |
| **觸發時機** | 請假審核通過後 |
| **訂閱服務** | Payroll Service, Notification Service |

```json
{
  "eventId": "evt-lv-002",
  "eventType": "LeaveApproved",
  "timestamp": "2025-12-06T10:30:00Z",
  "payload": {
    "applicationId": "app-001",
    "employeeId": "emp-001",
    "leaveTypeId": "lt-annual",
    "leaveTypeName": "特休假",
    "startDate": "2025-12-09",
    "endDate": "2025-12-10",
    "totalDays": 2,
    "isPaid": true,
    "payRate": 1.0,
    "approverId": "mgr-001",
    "approvedAt": "2025-12-06T10:30:00"
  }
}
```

---

### 3.6 駁回請假申請

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/leave/applications/{applicationId}/reject` |
| **方法** | PUT |
| **Controller** | `HR03LeaveCmdController` |
| **Service** | `RejectLeaveServiceImpl` |
| **權限** | `leave:application:approve` |

#### 用途說明

- **業務場景:** 主管駁回請假申請
- **使用者:** 直屬主管、HR
- **解決問題:** 處理不符合規定的請假申請

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `reason` | string | Y | 駁回原因 (1-500 字元) |

**Request 範例:**

```json
{
  "reason": "該時段已有重要會議安排，請調整請假日期"
}
```

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "applicationId": "app-001",
    "status": "REJECTED",
    "rejectedBy": "李經理",
    "rejectedAt": "2025-12-06T10:30:00Z",
    "rejectionReason": "該時段已有重要會議安排，請調整請假日期"
  }
}
```

#### 領域事件

**LeaveRejectedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.leave.rejected` |
| **觸發時機** | 請假審核駁回後 |
| **訂閱服務** | Notification Service |

```json
{
  "eventId": "evt-lv-003",
  "eventType": "LeaveRejected",
  "timestamp": "2025-12-06T10:30:00Z",
  "payload": {
    "applicationId": "app-001",
    "employeeId": "emp-001",
    "reason": "該時段已有重要會議安排，請調整請假日期"
  }
}
```

---

### 3.7 取消請假申請

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/leave/applications/{applicationId}/cancel` |
| **方法** | PUT |
| **Controller** | `HR03LeaveCmdController` |
| **Service** | `CancelLeaveServiceImpl` |
| **權限** | `leave:application:cancel` |

#### 用途說明

- **業務場景:** 員工取消請假申請
- **使用者:** 申請人本人
- **解決問題:** 允許員工在請假開始前取消申請

#### 業務邏輯

1. **驗證規則:**
   - 僅允許 PENDING 或 APPROVED 狀態取消
   - 已開始的請假不可取消

2. **處理步驟:**
   - 更新狀態為 CANCELLED
   - 若已核准，退回假期餘額
   - 發布 `LeaveCancelledEvent`

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "applicationId": "app-001",
    "status": "CANCELLED",
    "cancelledAt": "2025-12-06T11:00:00Z",
    "balanceRefunded": true,
    "refundedDays": 2
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `LEAVE_ALREADY_STARTED` | 請假已開始，無法取消 | 提示聯繫 HR |
| 400 | `LEAVE_CANNOT_CANCEL` | 此狀態無法取消 | 顯示當前狀態 |

---

### 3.8 待審核請假查詢

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/leave/applications/pending` |
| **方法** | GET |
| **Controller** | `HR03LeaveQryController` |
| **Service** | `GetPendingLeaveApplicationsServiceImpl` |
| **權限** | `leave:application:approve` |

#### 用途說明

- **業務場景:** 主管查詢待審核的請假申請
- **使用者:** 主管、HR
- **解決問題:** 提供審核工作台的待辦清單

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "pendingCount": 3,
    "applications": [
      {
        "applicationId": "app-001",
        "employeeId": "emp-001",
        "employeeName": "王小明",
        "departmentName": "資訊部",
        "leaveTypeName": "特休假",
        "startDate": "2025-12-09",
        "endDate": "2025-12-10",
        "totalDays": 2,
        "appliedAt": "2025-12-06T10:00:00Z"
      }
    ]
  }
}
```

---

## 4. 加班管理 API

### 4.1 提交加班申請

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/overtime/applications` |
| **方法** | POST |
| **Controller** | `HR03OvertimeCmdController` |
| **Service** | `CreateOvertimeApplicationServiceImpl` |
| **權限** | `overtime:application:create` |

#### 用途說明

- **業務場景:** 員工提交加班申請
- **使用者:** 全體員工
- **解決問題:** 記錄加班並啟動審核流程

#### 業務邏輯

1. **驗證規則:**
   - 驗證加班時數是否超過勞基法限制
   - 月加班上限：46 小時
   - 季加班上限：138 小時
   - 驗證加班日期與類型是否一致

2. **處理步驟:**
   - 計算加班時數
   - 檢查月/季累計時數
   - 建立加班申請
   - 發布 `OvertimeAppliedEvent`
   - 若超過上限，發布 `OvertimeLimitExceededEvent`

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `employeeId` | string (UUID) | Y | 有效 UUID | 員工 ID |
| `overtimeDate` | date | Y | YYYY-MM-DD | 加班日期 |
| `startTime` | time | Y | HH:mm 格式 | 開始時間 |
| `endTime` | time | Y | > startTime | 結束時間 |
| `overtimeType` | string | Y | WEEKDAY/REST_DAY/HOLIDAY | 加班類型 |
| `reason` | string | Y | 1-500 字元 | 加班原因 |
| `compensationType` | string | Y | PAY/COMP_TIME | 補償方式 |

**Request 範例:**

```json
{
  "employeeId": "emp-001",
  "overtimeDate": "2025-12-06",
  "startTime": "18:00",
  "endTime": "20:30",
  "overtimeType": "WEEKDAY",
  "reason": "專案趕工",
  "compensationType": "PAY"
}
```

#### Response 規格

**成功回應 (201 Created):**

```json
{
  "success": true,
  "data": {
    "overtimeId": "ot-001",
    "overtimeHours": 2.5,
    "status": "PENDING",
    "monthlyStatistics": {
      "accumulatedHours": 15.5,
      "monthlyLimit": 46,
      "quarterlyAccumulatedHours": 45.5,
      "quarterlyLimit": 138
    },
    "warnings": []
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `OT_MONTHLY_LIMIT_EXCEEDED` | 超過月加班上限 (46 小時) | 顯示累計時數 |
| 400 | `OT_QUARTERLY_LIMIT_EXCEEDED` | 超過季加班上限 (138 小時) | 顯示累計時數 |
| 400 | `OT_INVALID_TIME_RANGE` | 時間範圍無效 | 檢查起訖時間 |
| 400 | `OT_DATE_TYPE_MISMATCH` | 日期與加班類型不符 | 確認日期是否為假日 |

#### 領域事件

**OvertimeAppliedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.overtime.applied` |
| **觸發時機** | 加班申請提交後 |
| **訂閱服務** | Workflow Service |

**OvertimeLimitExceededEvent (若接近上限):**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.overtime.limit-exceeded` |
| **觸發時機** | 累計時數超過上限時 |
| **訂閱服務** | Notification Service |

---

### 4.2 查詢加班統計

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/overtime/statistics` |
| **方法** | GET |
| **Controller** | `HR03OvertimeQryController` |
| **Service** | `GetOvertimeStatisticsServiceImpl` |
| **權限** | `overtime:statistics:read` |

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `employeeId` | string (UUID) | Y | 員工 ID |
| `month` | string | Y | 年月 (YYYY-MM) |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "employeeId": "emp-001",
    "month": "2025-12",
    "totalHours": 15.5,
    "byType": {
      "WEEKDAY": 12.0,
      "REST_DAY": 3.5,
      "HOLIDAY": 0
    },
    "monthlyLimit": 46,
    "monthlyUsageRate": 0.337,
    "quarterlyAccumulatedHours": 45.5,
    "quarterlyLimit": 138,
    "quarterlyUsageRate": 0.330,
    "warnings": [],
    "applications": [
      {
        "overtimeId": "ot-001",
        "overtimeDate": "2025-12-05",
        "hours": 2.5,
        "type": "WEEKDAY",
        "status": "APPROVED"
      }
    ]
  }
}
```

---

### 4.3 查詢加班申請列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/overtime/applications` |
| **方法** | GET |
| **Controller** | `HR03OvertimeQryController` |
| **Service** | `GetOvertimeApplicationsServiceImpl` |
| **權限** | `overtime:application:read` |

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `employeeId` | string (UUID) | N | 員工 ID |
| `status` | string | N | 狀態篩選 |
| `startDate` | date | N | 起始日期 |
| `endDate` | date | N | 結束日期 |
| `page` | integer | N | 頁碼 |
| `pageSize` | integer | N | 每頁筆數 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "applications": [
      {
        "overtimeId": "ot-001",
        "employeeId": "emp-001",
        "employeeName": "王小明",
        "overtimeDate": "2025-12-06",
        "startTime": "18:00",
        "endTime": "20:30",
        "overtimeHours": 2.5,
        "overtimeType": "WEEKDAY",
        "compensationType": "PAY",
        "status": "PENDING",
        "reason": "專案趕工",
        "appliedAt": "2025-12-06T15:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "totalRecords": 10,
      "totalPages": 1
    }
  }
}
```

---

### 4.4 核准加班申請

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/overtime/applications/{overtimeId}/approve` |
| **方法** | PUT |
| **Controller** | `HR03OvertimeCmdController` |
| **Service** | `ApproveOvertimeServiceImpl` |
| **權限** | `overtime:application:approve` |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "overtimeId": "ot-001",
    "status": "APPROVED",
    "approvedBy": "李經理",
    "approvedAt": "2025-12-06T16:00:00Z"
  }
}
```

#### 領域事件

**OvertimeApprovedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.overtime.approved` |
| **觸發時機** | 加班審核通過後 |
| **訂閱服務** | Payroll Service |

```json
{
  "eventId": "evt-ot-003",
  "eventType": "OvertimeApproved",
  "timestamp": "2025-12-06T16:00:00Z",
  "payload": {
    "overtimeId": "ot-001",
    "employeeId": "emp-001",
    "overtimeDate": "2025-12-05",
    "overtimeHours": 2.5,
    "overtimeType": "WEEKDAY",
    "compensationType": "PAY",
    "approverId": "mgr-001"
  }
}
```

---

### 4.5 駁回加班申請

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/overtime/applications/{overtimeId}/reject` |
| **方法** | PUT |
| **Controller** | `HR03OvertimeCmdController` |
| **Service** | `RejectOvertimeServiceImpl` |
| **權限** | `overtime:application:approve` |

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `reason` | string | Y | 駁回原因 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "overtimeId": "ot-001",
    "status": "REJECTED",
    "rejectedBy": "李經理",
    "rejectedAt": "2025-12-06T16:00:00Z",
    "rejectionReason": "請改為補休方式"
  }
}
```

---

### 4.6 待審核加班查詢

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/overtime/applications/pending` |
| **方法** | GET |
| **Controller** | `HR03OvertimeCmdController` |
| **Service** | `GetPendingOvertimeServiceImpl` |
| **權限** | `overtime:application:approve` |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "pendingCount": 1,
    "applications": [
      {
        "overtimeId": "ot-001",
        "employeeId": "emp-001",
        "employeeName": "王小明",
        "departmentName": "資訊部",
        "overtimeDate": "2025-12-06",
        "overtimeHours": 2.5,
        "overtimeType": "WEEKDAY",
        "appliedAt": "2025-12-06T15:00:00Z"
      }
    ]
  }
}
```

---

## 5. 班別管理 API

### 5.1 建立班別

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/shifts` |
| **方法** | POST |
| **Controller** | `HR03ShiftCmdController` |
| **Service** | `CreateShiftServiceImpl` |
| **權限** | `shift:manage` |

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `shiftCode` | string | Y | 唯一、1-50 字元 | 班別代碼 |
| `shiftName` | string | Y | 1-255 字元 | 班別名稱 |
| `organizationId` | string (UUID) | Y | 有效 UUID | 組織 ID |
| `shiftType` | string | Y | STANDARD/FLEXIBLE/ROTATING | 班別類型 |
| `workStartTime` | time | Y | HH:mm 格式 | 上班時間 |
| `workEndTime` | time | Y | HH:mm 格式 | 下班時間 |
| `breakStartTime` | time | N | HH:mm 格式 | 休息開始 |
| `breakEndTime` | time | N | HH:mm 格式 | 休息結束 |
| `lateToleranceMinutes` | integer | N | 0-60 | 遲到容許分鐘 |
| `earlyLeaveToleranceMinutes` | integer | N | 0-60 | 早退容許分鐘 |

**Request 範例:**

```json
{
  "shiftCode": "STD-01",
  "shiftName": "標準班",
  "organizationId": "org-001",
  "shiftType": "STANDARD",
  "workStartTime": "09:00",
  "workEndTime": "18:00",
  "breakStartTime": "12:00",
  "breakEndTime": "13:00",
  "lateToleranceMinutes": 5,
  "earlyLeaveToleranceMinutes": 0
}
```

#### Response 規格

**成功回應 (201 Created):**

```json
{
  "success": true,
  "data": {
    "shiftId": "shift-001",
    "shiftCode": "STD-01",
    "shiftName": "標準班",
    "workingHours": 8,
    "createdAt": "2025-12-06T10:00:00Z"
  }
}
```

---

### 5.2 查詢班別列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/shifts` |
| **方法** | GET |
| **Controller** | `HR03ShiftQryController` |
| **Service** | `GetShiftsServiceImpl` |
| **權限** | `shift:read` |

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `organizationId` | string (UUID) | N | 組織 ID |
| `shiftType` | string | N | 班別類型篩選 |
| `isActive` | boolean | N | 是否啟用 (預設 true) |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "shifts": [
      {
        "shiftId": "shift-001",
        "shiftCode": "STD-01",
        "shiftName": "標準班",
        "shiftType": "STANDARD",
        "workStartTime": "09:00",
        "workEndTime": "18:00",
        "breakStartTime": "12:00",
        "breakEndTime": "13:00",
        "workingHours": 8,
        "lateToleranceMinutes": 5,
        "earlyLeaveToleranceMinutes": 0,
        "isActive": true
      },
      {
        "shiftId": "shift-002",
        "shiftCode": "FLEX-01",
        "shiftName": "彈性班",
        "shiftType": "FLEXIBLE",
        "workStartTime": "08:00",
        "workEndTime": "17:00",
        "breakStartTime": "12:00",
        "breakEndTime": "13:00",
        "workingHours": 8,
        "lateToleranceMinutes": 30,
        "earlyLeaveToleranceMinutes": 30,
        "isActive": true
      }
    ]
  }
}
```

---

### 5.3 更新班別

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/shifts/{shiftId}` |
| **方法** | PUT |
| **Controller** | `HR03ShiftCmdController` |
| **Service** | `UpdateShiftServiceImpl` |
| **權限** | `shift:manage` |

#### Request 規格

**Request Body:** (同建立班別，所有欄位選填)

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "shiftId": "shift-001",
    "shiftName": "標準班（已更新）",
    "updatedAt": "2025-12-06T11:00:00Z"
  }
}
```

---

### 5.4 停用班別

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/shifts/{shiftId}/deactivate` |
| **方法** | PUT |
| **Controller** | `HR03ShiftCmdController` |
| **Service** | `DeactivateShiftServiceImpl` |
| **權限** | `shift:manage` |

#### 業務邏輯

- 不會刪除班別，僅設定 isActive = false
- 已使用此班別的員工需重新指派

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "shiftId": "shift-001",
    "isActive": false,
    "deactivatedAt": "2025-12-06T11:00:00Z",
    "affectedEmployeeCount": 5
  }
}
```

---

## 6. 假別管理 API

### 6.1 建立假別

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/leave-types` |
| **方法** | POST |
| **Controller** | `HR03LeaveTypeCmdController` |
| **Service** | `CreateLeaveTypeServiceImpl` |
| **權限** | `leave-type:manage` |

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `leaveCode` | string | Y | 唯一、1-50 字元 | 假別代碼 |
| `leaveName` | string | Y | 1-255 字元 | 假別名稱 |
| `organizationId` | string (UUID) | N | 有效 UUID | 組織 ID (法定假別可為空) |
| `isPaid` | boolean | Y | - | 是否支薪 |
| `payRate` | number | C | 0.0-1.0 | 支薪比例 (isPaid=true 時必填) |
| `requiresProof` | boolean | Y | - | 是否需要證明 |
| `proofDescription` | string | C | - | 證明文件說明 |
| `unit` | string | Y | HOUR/HALF_DAY/FULL_DAY | 請假單位 |
| `maxDaysPerYear` | number | N | - | 年度上限天數 |
| `canCarryover` | boolean | N | - | 是否可結轉 |
| `isStatutoryLeave` | boolean | Y | - | 是否為法定假別 |
| `statutoryType` | string | C | - | 法定假別類型 |

**Request 範例:**

```json
{
  "leaveCode": "COMPANY_LEAVE",
  "leaveName": "公司特休",
  "organizationId": "org-001",
  "isPaid": true,
  "payRate": 1.0,
  "requiresProof": false,
  "unit": "FULL_DAY",
  "maxDaysPerYear": 5,
  "canCarryover": false,
  "isStatutoryLeave": false
}
```

#### Response 規格

**成功回應 (201 Created):**

```json
{
  "success": true,
  "data": {
    "leaveTypeId": "lt-company",
    "leaveCode": "COMPANY_LEAVE",
    "leaveName": "公司特休",
    "createdAt": "2025-12-06T10:00:00Z"
  }
}
```

---

### 6.2 查詢假別列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/leave-types` |
| **方法** | GET |
| **Controller** | `HR03LeaveTypeQryController` |
| **Service** | `GetLeaveTypesServiceImpl` |
| **權限** | `leave-type:read` |

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `organizationId` | string (UUID) | N | 組織 ID |
| `isStatutory` | boolean | N | 是否僅顯示法定假別 |
| `isActive` | boolean | N | 是否啟用 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "leaveTypes": [
      {
        "leaveTypeId": "lt-annual",
        "leaveCode": "ANNUAL",
        "leaveName": "特休假",
        "isPaid": true,
        "payRate": 1.0,
        "requiresProof": false,
        "unit": "FULL_DAY",
        "maxDaysPerYear": null,
        "isStatutoryLeave": true,
        "statutoryType": "ANNUAL_LEAVE",
        "isActive": true
      },
      {
        "leaveTypeId": "lt-sick",
        "leaveCode": "SICK",
        "leaveName": "病假",
        "isPaid": false,
        "payRate": 0.5,
        "requiresProof": true,
        "proofDescription": "需附診斷證明",
        "unit": "HALF_DAY",
        "maxDaysPerYear": 30,
        "isStatutoryLeave": true,
        "statutoryType": "SICK_LEAVE",
        "isActive": true
      }
    ]
  }
}
```

---

### 6.3 更新假別

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/leave-types/{leaveTypeId}` |
| **方法** | PUT |
| **Controller** | `HR03LeaveTypeCmdController` |
| **Service** | `UpdateLeaveTypeServiceImpl` |
| **權限** | `leave-type:manage` |

#### 業務邏輯

- 法定假別僅允許修改 proofDescription、maxDaysPerYear
- 非法定假別可修改所有欄位

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "leaveTypeId": "lt-company",
    "leaveName": "公司特休（已更新）",
    "updatedAt": "2025-12-06T11:00:00Z"
  }
}
```

---

### 6.4 停用假別

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/leave-types/{leaveTypeId}/deactivate` |
| **方法** | PUT |
| **Controller** | `HR03LeaveTypeCmdController` |
| **Service** | `DeactivateLeaveTypeServiceImpl` |
| **權限** | `leave-type:manage` |

#### 業務邏輯

- 法定假別不可停用
- 停用後員工無法再使用此假別申請

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "leaveTypeId": "lt-company",
    "isActive": false,
    "deactivatedAt": "2025-12-06T11:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `LT_STATUTORY_CANNOT_DEACTIVATE` | 法定假別不可停用 | 提示此限制 |

---

## 7. 報表結算 API

### 7.1 執行月度結算

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/attendance/monthly-close` |
| **方法** | POST |
| **Controller** | `HR03MonthCloseCmdController` |
| **Service** | `MonthlyCloseServiceImpl` |
| **權限** | `attendance:close` |

#### 用途說明

- **業務場景:** HR 執行月度差勤結算
- **使用者:** HR 管理員
- **解決問題:** 產生月結資料供薪資計算使用

#### 業務邏輯

1. **處理步驟:**
   - 彙總所有員工該月的出勤資料
   - 計算各類統計（工作天數、遲到次數、請假天數等）
   - 鎖定該月資料（不可再修改）
   - 為每位員工發布 `AttendanceMonthClosedEvent`

2. **副作用:**
   - Payroll Service 訂閱事件開始薪資計算
   - Report Service 訂閱事件更新報表

#### Request 規格

**Request Body:**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `month` | string | Y | 結算年月 (YYYY-MM) |
| `organizationId` | string (UUID) | Y | 組織 ID |

**Request 範例:**

```json
{
  "month": "2025-11",
  "organizationId": "org-001"
}
```

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "month": "2025-11",
    "organizationId": "org-001",
    "processedEmployees": 150,
    "closedAt": "2025-12-01T00:00:00Z",
    "summary": {
      "totalWorkDays": 22,
      "averageAttendanceRate": 0.95,
      "totalLeaveApplications": 45,
      "totalOvertimeHours": 320.5
    }
  }
}
```

#### 領域事件

**AttendanceMonthClosedEvent:**

| 項目 | 內容 |
|:---|:---|
| **Topic** | `hrms.attendance.month-closed` |
| **觸發時機** | 月結算完成後 (每位員工一筆) |
| **訂閱服務** | Payroll Service, Report Service |

```json
{
  "eventId": "evt-close-001",
  "eventType": "AttendanceMonthClosed",
  "timestamp": "2025-12-01T00:00:00Z",
  "payload": {
    "month": "2025-11",
    "employeeId": "emp-001",
    "summary": {
      "totalWorkDays": 22,
      "actualWorkDays": 20,
      "totalLeaveDays": 2,
      "totalOvertimeHours": 15.5,
      "lateCount": 3,
      "lateTotalMinutes": 25,
      "earlyLeaveCount": 1,
      "earlyLeaveTotalMinutes": 10
    },
    "leaveDetails": [
      {"leaveType": "ANNUAL", "days": 1.5, "isPaid": true},
      {"leaveType": "SICK", "days": 0.5, "isPaid": false}
    ],
    "overtimeDetails": [
      {"type": "WEEKDAY", "hours": 10.5, "compensation": "PAY"},
      {"type": "REST_DAY", "hours": 5, "compensation": "PAY"}
    ]
  }
}
```

---

### 7.2 查詢月度報表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/attendance/reports/monthly` |
| **方法** | GET |
| **Controller** | `HR03ReportQryController` |
| **Service** | `GetMonthlyReportServiceImpl` |
| **權限** | `attendance:report:read` |

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `month` | string | Y | 年月 (YYYY-MM) |
| `organizationId` | string (UUID) | N | 組織 ID |
| `departmentId` | string (UUID) | N | 部門 ID |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "month": "2025-11",
    "isClosed": true,
    "closedAt": "2025-12-01T00:00:00Z",
    "organizationSummary": {
      "totalEmployees": 150,
      "totalWorkDays": 22,
      "averageAttendanceRate": 0.95,
      "totalLeaveDays": 125.5,
      "totalOvertimeHours": 320.5
    },
    "departmentBreakdown": [
      {
        "departmentId": "dept-001",
        "departmentName": "資訊部",
        "employeeCount": 30,
        "attendanceRate": 0.97,
        "leaveDays": 25,
        "overtimeHours": 85.5
      }
    ],
    "anomalyStatistics": {
      "totalLateCount": 45,
      "totalEarlyLeaveCount": 12,
      "totalAbsentCount": 3
    }
  }
}
```

---

### 7.3 查詢差勤異常報表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/attendance/reports/anomalies` |
| **方法** | GET |
| **Controller** | `HR03ReportQryController` |
| **Service** | `GetAnomalyReportServiceImpl` |
| **權限** | `attendance:report:read` |

#### Request 規格

**Query Parameters:**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `startDate` | date | Y | 起始日期 |
| `endDate` | date | Y | 結束日期 |
| `organizationId` | string (UUID) | N | 組織 ID |
| `departmentId` | string (UUID) | N | 部門 ID |
| `anomalyType` | string | N | 異常類型篩選 |

#### Response 規格

**成功回應 (200 OK):**

```json
{
  "success": true,
  "data": {
    "period": {
      "startDate": "2025-11-01",
      "endDate": "2025-11-30"
    },
    "anomalies": [
      {
        "recordId": "record-001",
        "employeeId": "emp-001",
        "employeeName": "王小明",
        "departmentName": "資訊部",
        "recordDate": "2025-11-05",
        "anomalyType": "LATE",
        "anomalyMinutes": 15,
        "isCorrected": false
      }
    ],
    "summary": {
      "totalAnomalies": 60,
      "byType": {
        "LATE": 45,
        "EARLY_LEAVE": 12,
        "MISSING_CHECK_IN": 2,
        "MISSING_CHECK_OUT": 1
      }
    },
    "pagination": {
      "page": 1,
      "pageSize": 50,
      "totalRecords": 60,
      "totalPages": 2
    }
  }
}
```

---

## 8. 附錄：列舉值定義

### 8.1 班別類型 (ShiftType)

| 值 | 說明 |
|:---|:---|
| `STANDARD` | 標準班 - 固定上下班時間 |
| `FLEXIBLE` | 彈性班 - 上下班時間有彈性範圍 |
| `ROTATING` | 輪班 - 依排班表輪替 |

### 8.2 請假時段 (LeavePeriod)

| 值 | 說明 |
|:---|:---|
| `AM` | 上午 (0.5 天) |
| `PM` | 下午 (0.5 天) |
| `FULL_DAY` | 全天 (1 天) |

### 8.3 請假單位 (LeaveUnit)

| 值 | 說明 |
|:---|:---|
| `HOUR` | 以小時計 |
| `HALF_DAY` | 以半天計 |
| `FULL_DAY` | 以全天計 |

### 8.4 申請狀態 (ApplicationStatus)

| 值 | 說明 |
|:---|:---|
| `DRAFT` | 草稿 |
| `PENDING` | 待審核 |
| `APPROVED` | 已核准 |
| `REJECTED` | 已駁回 |
| `CANCELLED` | 已取消 |

### 8.5 加班類型 (OvertimeType)

| 值 | 說明 | 加班費倍率 |
|:---|:---|:---:|
| `WEEKDAY` | 平日加班 | 1.34 / 1.67 |
| `REST_DAY` | 休息日加班 | 1.34 / 1.67 / 2.67 |
| `HOLIDAY` | 國定假日加班 | 2.0 |

### 8.6 補償方式 (CompensationType)

| 值 | 說明 |
|:---|:---|
| `PAY` | 加班費 |
| `COMP_TIME` | 補休 |

### 8.7 異常類型 (AnomalyType)

| 值 | 說明 |
|:---|:---|
| `LATE` | 遲到 |
| `EARLY_LEAVE` | 早退 |
| `MISSING_CHECK_IN` | 漏打上班卡 |
| `MISSING_CHECK_OUT` | 漏打下班卡 |
| `ABNORMAL_LOCATION` | 異常位置 |

### 8.8 補卡類型 (CorrectionType)

| 值 | 說明 |
|:---|:---|
| `CHECK_IN` | 補打上班卡 |
| `CHECK_OUT` | 補打下班卡 |

### 8.9 法定假別類型 (StatutoryLeaveType)

| 值 | 說明 | 是否支薪 | 年度上限 |
|:---|:---|:---:|:---:|
| `ANNUAL_LEAVE` | 特休假 | 是 | 依年資 |
| `SICK_LEAVE` | 病假 | 半薪 | 30 天 |
| `PERSONAL_LEAVE` | 事假 | 否 | 14 天 |
| `MARRIAGE_LEAVE` | 婚假 | 是 | 8 天 |
| `BEREAVEMENT_LEAVE` | 喪假 | 是 | 3-8 天 |
| `MATERNITY_LEAVE` | 產假 | 是 | 56 天 |
| `PATERNITY_LEAVE` | 陪產假 | 是 | 7 天 |
| `MENSTRUAL_LEAVE` | 生理假 | 半薪 | 12 天 |
| `PARENTAL_LEAVE` | 育嬰留停 | 否 | 依規定 |

---

## 9. 領域事件總覽

| 事件名稱 | Kafka Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| `AttendanceRecorded` | `hrms.attendance.recorded` | 打卡成功 | - |
| `AttendanceAnomalyDetected` | `hrms.attendance.anomaly-detected` | 偵測到異常 | Notification |
| `LeaveApplied` | `hrms.leave.applied` | 請假申請提交 | Workflow |
| `LeaveApproved` | `hrms.leave.approved` | 請假核准 | Payroll, Notification |
| `LeaveRejected` | `hrms.leave.rejected` | 請假駁回 | Notification |
| `LeaveCancelled` | `hrms.leave.cancelled` | 請假取消 | - |
| `OvertimeApplied` | `hrms.overtime.applied` | 加班申請提交 | Workflow |
| `OvertimeApproved` | `hrms.overtime.approved` | 加班核准 | Payroll |
| `OvertimeLimitExceeded` | `hrms.overtime.limit-exceeded` | 加班超時 | Notification |
| `AnnualLeaveExpiring` | `hrms.leave.annual-expiring` | 特休即將到期 | Notification |
| `AttendanceMonthClosed` | `hrms.attendance.month-closed` | 月結算完成 | Payroll, Report |

---

**文件結束**

**版本歷史:**

| 版本 | 日期 | 說明 |
|:---|:---|:---|
| 1.0 | 2025-12-29 | 初版建立 |
