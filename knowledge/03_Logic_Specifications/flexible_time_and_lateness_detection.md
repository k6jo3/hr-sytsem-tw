# 彈性工時與遲到判斷邏輯規格書

**版本:** 1.0
**日期:** 2026-03-05
**適用服務:** HR03 考勤管理服務 (hrms-attendance)
**適用法規:** 勞動基準法第30條

---

## 1. 文件概述

本文件定義考勤管理服務中「彈性工時制度」與「遲到/早退/曠職」判斷的完整業務邏輯規格。涵蓋固定工時制與彈性工時制的差異化判定規則、寬限時間（Grace Period）處理機制、曠職自動偵測排程，以及異常出勤（補打卡）的修正流程。

### 1.1 適用對象

- 後端開發工程師：實作 Domain 層遲到/早退/曠職判定邏輯
- 前端開發工程師：打卡頁面狀態顯示與異常提示
- SA/QA：驗收測試案例設計依據

### 1.2 相關文件

| 文件 | 路徑 | 說明 |
|:---|:---|:---|
| 考勤系統設計書 | `knowledge/02_System_Design/03_考勤管理服務系統設計書.md` | 完整系統設計 |
| 考勤 API 規格 | `knowledge/04_API_Specifications/03_考勤管理服務系統設計書_API詳細規格.md` | API 詳細規格 |
| 變形工時規則 | `knowledge/03_Logic_Specifications/variable_hours_rules.md` | 二週/四週/八週變形工時 |
| 合約測試規格 | `contracts/attendance_contracts.md` | 合約驗證案例 |

---

## 2. 班別類型定義

### 2.1 ShiftType 列舉

系統支援三種班別類型，定義於 `ShiftType` 列舉：

```java
public enum ShiftType {
    REGULAR,    // 常規班（固定工時制）
    FLEXIBLE,   // 彈性班（彈性工時制）
    SHIFT       // 排班制（輪班）
}
```

### 2.2 各班別類型特性比較

| 特性 | REGULAR（固定工時） | FLEXIBLE（彈性工時） | SHIFT（排班） |
|:---|:---|:---|:---|
| 上班時間 | 固定（如 09:00） | 彈性區間（如 08:00-10:00） | 排班指定 |
| 下班時間 | 固定（如 18:00） | 依上班時間推算 | 排班指定 |
| 遲到基準 | 上班時間 + 寬限 | 彈性區間結束時間 | 排班上班時間 + 寬限 |
| 早退基準 | 下班時間 - 寬限 | 應達工時是否滿足 | 排班下班時間 - 寬限 |
| 每日基本工時 | 8 小時 | 8 小時（可設定） | 依排班設定 |

### 2.3 現有班別 Seed Data

| 班別代碼 | 名稱 | 類型 | 上班 | 下班 | 遲到寬限 | 早退寬限 |
|:---|:---|:---|:---:|:---:|:---:|:---:|
| `DAY` | 日班 | REGULAR | 09:00 | 18:00 | 5 分鐘 | 5 分鐘 |
| `FLEX` | 彈性班 | FLEXIBLE | 08:00 | 17:00 | 30 分鐘 | 30 分鐘 |
| `NIGHT` | 晚班 | REGULAR | 14:00 | 23:00 | 5 分鐘 | 5 分鐘 |

> **設計備註：** 彈性班的 `start_time`/`end_time` 在 Shift 表中記錄的是彈性區間的「最早上班時間」與「對應下班時間」。實際彈性區間由 `FlexTimePolicy` Value Object 定義。

---

## 3. 彈性工時定義

### 3.1 FlexTimePolicy Value Object

彈性工時政策由 `FlexTimePolicy` 值物件封裝，定義於 `domain/model/valueobject/FlexTimePolicy.java`：

```java
public class FlexTimePolicy {
    private final LocalTime flexStartEarliest;    // 彈性上班最早時間（如 08:00）
    private final LocalTime flexStartLatest;       // 彈性上班最晚時間（如 10:00）
    private final LocalTime flexEndEarliest;       // 彈性下班最早時間（如 17:00）
    private final LocalTime flexEndLatest;         // 彈性下班最晚時間（如 19:00）
    private final LocalTime coreStartTime;         // 核心時段開始（如 10:00）
    private final LocalTime coreEndTime;           // 核心時段結束（如 17:00）
    private final double requiredHoursPerDay;      // 每日應達工時（如 8.0）
}
```

### 3.2 核心概念

#### 3.2.1 彈性上班區間

員工可在 `flexStartEarliest` 至 `flexStartLatest` 之間的任意時間上班打卡。在此區間內打卡均視為「正常」，不計入遲到。

```
時間軸：
  08:00         10:00
    |=============|
    彈性上班區間
    （此區間打卡 = 正常）
```

#### 3.2.2 核心工作時段

核心時段（`coreStartTime` ~ `coreEndTime`）為全體員工必須在崗的時間段。缺席核心時段視為異常。

```
時間軸：
  08:00    10:00                    17:00    19:00
    |--------|========================|--------|
    彈性上班    核心工作時段（必須在崗）   彈性下班
```

#### 3.2.3 每日應達工時

不論何時打卡上班，員工的「上班打卡時間」到「下班打卡時間」之間的有效工時（扣除休息時間）必須達到 `requiredHoursPerDay`（預設 8 小時）。

### 3.3 驗證規則

```
規則 1：flexStartEarliest ≤ flexStartLatest（彈性上班最早不可晚於最晚）
規則 2：coreStartTime ≤ coreEndTime（核心時段開始不可晚於結束）
規則 3：0 < requiredHoursPerDay ≤ 24（每日應達工時合理範圍）
規則 4：flexStartLatest ≤ coreStartTime（彈性上班最晚 ≤ 核心時段開始）[建議]
規則 5：coreEndTime ≤ flexEndEarliest（核心時段結束 ≤ 彈性下班最早）[建議]
```

---

## 4. 遲到判斷規則

### 4.1 固定工時制（REGULAR / SHIFT）

#### 4.1.1 判定公式

```
遲到 = 打卡時間 > (上班時間 + 遲到寬限分鐘數)
遲到分鐘數 = 打卡時間 - 上班時間（從上班時間起算，非從寬限結束起算）
```

#### 4.1.2 程式實作（AttendanceRecord.checkIn）

```java
public void checkIn(LocalDateTime time, Shift shift) {
    // 遲到判定（若班別關閉遲到判定則跳過）
    if (shift.isLateCheckEnabled()) {
        LocalDateTime expectedStart = LocalDateTime.of(date, shift.getWorkStartTime());
        int tolerance = shift.getLateToleranceMinutes();

        if (time.isAfter(expectedStart.plusMinutes(tolerance))) {
            this.isLate = true;
            this.lateMinutes = (int) Duration.between(expectedStart, time).toMinutes();
            this.anomalyType = AnomalyType.LATE;
        }
    }
}
```

#### 4.1.3 Shift 控制開關

| 開關 | 欄位 | 預設值 | 說明 |
|:---|:---|:---:|:---|
| 遲到判定開關 | `lateCheckEnabled` | `true` | 設為 `false` 時跳過遲到判定 |
| 遲到扣薪開關 | `lateSalaryDeduction` | `true` | 設為 `false` 時不扣薪 |

#### 4.1.4 固定工時遲到範例

**情境：** 日班（09:00-18:00），寬限 5 分鐘

| 打卡時間 | 是否遲到 | 遲到分鐘數 | 異常類型 | 說明 |
|:---:|:---:|:---:|:---:|:---|
| 08:50 | 否 | 0 | NORMAL | 提前到達 |
| 09:00 | 否 | 0 | NORMAL | 準時 |
| 09:03 | 否 | 0 | NORMAL | 在寬限內（≤ 09:05） |
| 09:05 | 否 | 0 | NORMAL | 剛好在寬限邊界（不超過） |
| 09:06 | 是 | 6 | LATE | 超過寬限（> 09:05） |
| 09:30 | 是 | 30 | LATE | 遲到 30 分鐘 |
| 10:00 | 是 | 60 | LATE | 遲到 1 小時 |

### 4.2 彈性工時制（FLEXIBLE）

#### 4.2.1 判定公式

```
遲到 = 打卡時間 > 彈性上班最晚時間（flexStartLatest）
```

> **關鍵差異：** 彈性工時制不使用 `lateToleranceMinutes` 寬限，而是以 `flexStartLatest` 作為遲到基準線。只要在 `flexStartEarliest` ~ `flexStartLatest` 區間內打卡，一律視為正常。

#### 4.2.2 程式實作（FlexTimePolicy）

```java
public boolean isLateForFlexShift(LocalTime checkInTime) {
    return checkInTime.isAfter(flexStartLatest);
}

public boolean isWithinFlexStart(LocalTime checkInTime) {
    return !checkInTime.isBefore(flexStartEarliest)
        && !checkInTime.isAfter(flexStartLatest);
}
```

#### 4.2.3 彈性工時遲到範例

**情境：** 彈性班（彈性上班 08:00-10:00，核心 10:00-17:00）

| 打卡時間 | 是否遲到 | 是否在彈性區間 | 說明 |
|:---:|:---:|:---:|:---|
| 07:50 | 否 | 否（早於區間） | 提早打卡，視為正常 |
| 08:00 | 否 | 是 | 彈性區間內 |
| 09:00 | 否 | 是 | 彈性區間內 |
| 09:59 | 否 | 是 | 彈性區間內 |
| 10:00 | 否 | 是 | 彈性區間邊界（不超過） |
| 10:01 | 是 | 否 | 超過彈性區間 |
| 10:30 | 是 | 否 | 遲到 30 分鐘 |

### 4.3 寬限時間（Grace Period）處理

#### 4.3.1 概念說明

寬限時間是「遲到容許分鐘數」（`lateToleranceMinutes`），在計算是否遲到時給予員工的緩衝。

- **固定工時制：** 使用 `Shift.lateToleranceMinutes` 作為寬限
- **彈性工時制：** 不使用寬限（彈性區間本身即為寬限機制）

#### 4.3.2 寬限時間與遲到分鐘數的區別

| 項目 | 寬限時間 | 遲到分鐘數 |
|:---|:---|:---|
| 用途 | 判斷是否視為遲到 | 記錄遲到的嚴重程度 |
| 計算方式 | 打卡時間 > 上班時間 + 寬限 → 才算遲到 | 打卡時間 - 上班時間 = 遲到分鐘數 |
| 範例 | 上班 09:00，寬限 5 分鐘，09:06 才算遲到 | 09:06 打卡 → 遲到 6 分鐘（非 1 分鐘） |

> **重要：** 遲到分鐘數從「上班時間」起算，而非從「寬限結束時間」起算。這確保薪資扣除的計算基準一致。

---

## 5. 早退判斷規則

### 5.1 固定工時制

#### 5.1.1 判定公式

```
早退 = 打卡時間 < (下班時間 - 早退寬限分鐘數)
早退分鐘數 = 下班時間 - 打卡時間
```

#### 5.1.2 程式實作（AttendanceRecord.checkOut）

```java
public void checkOut(LocalDateTime time, Shift shift) {
    LocalDateTime expectedEnd = LocalDateTime.of(date, shift.getWorkEndTime());
    int tolerance = shift.getEarlyLeaveToleranceMinutes();

    if (time.isBefore(expectedEnd.minusMinutes(tolerance))) {
        this.isEarlyLeave = true;
        this.earlyLeaveMinutes = (int) Duration.between(time, expectedEnd).toMinutes();
        if (this.anomalyType == AnomalyType.NORMAL) {
            this.anomalyType = AnomalyType.EARLY_LEAVE;
        }
    }
}
```

#### 5.1.3 早退異常複合處理

若同一筆記錄既遲到又早退：
- `isLate = true` + `isEarlyLeave = true`
- `anomalyType` 維持 `LATE`（先發生的異常優先）
- 遲到分鐘數與早退分鐘數各自獨立記錄

#### 5.1.4 固定工時早退範例

**情境：** 日班（09:00-18:00），早退寬限 5 分鐘

| 打卡時間 | 是否早退 | 早退分鐘數 | 說明 |
|:---:|:---:|:---:|:---|
| 17:50 | 是 | 10 | 早退（< 17:55） |
| 17:55 | 否 | 0 | 在寬限內（≥ 17:55） |
| 18:00 | 否 | 0 | 準時 |
| 18:30 | 否 | 0 | 正常加班 |

### 5.2 彈性工時制

彈性工時制的早退判定較為複雜，需考慮兩個面向：

1. **核心時段：** 下班打卡時間不可早於 `coreEndTime`
2. **應達工時：** 實際工時（扣除休息）必須達到 `requiredHoursPerDay`

```
早退條件 =（下班打卡 < coreEndTime）OR（實際工時 < requiredHoursPerDay）
```

---

## 6. 曠職判定規則

### 6.1 判定邏輯

曠職（ABSENT）由排程任務 `AbsentDetectionJob` 於每日 19:00 自動執行判定。

#### 6.1.1 判定公式

```
曠職員工 = 全部在職員工 - 已打卡員工 - 已核准請假員工
```

#### 6.1.2 Domain Service 實作

```java
public class AbsentDetectionDomainService {

    public List<String> detectAbsentEmployees(
            List<String> allEmployeeIds,
            List<String> employeeIdsWithRecord,
            List<String> employeeIdsOnLeave) {

        return allEmployeeIds.stream()
                .filter(id -> !employeeIdsWithRecord.contains(id))
                .filter(id -> !employeeIdsOnLeave.contains(id))
                .collect(Collectors.toList());
    }
}
```

### 6.2 排程流程

```
每日 19:00 觸發（cron: 0 0 19 * * ?）
    │
    ├─ 1. 取得所有在職員工 ID
    │      ├─ 優先：employee_read_models 表
    │      └─ 降級：leave_balances 表推導
    │
    ├─ 2. 查詢當日已有打卡記錄的員工 ID
    │
    ├─ 3. 查詢當日有核准請假的員工 ID
    │
    ├─ 4. AbsentDetectionDomainService 計算差集 → 缺勤員工
    │
    ├─ 5. 為每位缺勤員工建立 AttendanceRecord (ABSENT)
    │
    └─ 6. 發布 AttendanceAnomalyDetectedEvent（通知 HR/主管）
```

### 6.3 曠職記錄建立

```java
public static AttendanceRecord createAbsentRecord(RecordId id, String employeeId, LocalDate date) {
    AttendanceRecord record = new AttendanceRecord(id, employeeId, date);
    record.anomalyType = AnomalyType.ABSENT;
    return record;
}
```

- 缺勤記錄的 `checkInTime` 和 `checkOutTime` 均為 `null`
- `anomalyType` 設為 `ABSENT`
- `isLate` 和 `isEarlyLeave` 保持 `false`

### 6.4 排除條件

以下情況不判定為曠職：

| 條件 | 說明 |
|:---|:---|
| 當日有打卡記錄 | 無論上班或下班打卡，只要有任一紀錄即排除 |
| 當日有核准請假 | 請假狀態為 `APPROVED` 且涵蓋該日 |
| 例假日/休息日 | 非工作日不判定（需搭配排班表判斷） |
| 國定假日 | 依公司行事曆排除 |

---

## 7. 異常出勤處理

### 7.1 AnomalyType 列舉

```java
public enum AnomalyType {
    NORMAL,             // 正常
    LATE,               // 遲到
    EARLY_LEAVE,        // 早退
    ABSENT,             // 缺勤（曠職）
    MISSING_CHECK_IN,   // 缺上班卡
    MISSING_CHECK_OUT,  // 缺下班卡
    ABNORMAL,           // 異常
    ABNORMAL_LOCATION   // 異常地點
}
```

### 7.2 補打卡（補卡）流程

#### 7.2.1 業務規則

- 員工忘記打卡時，可提交補卡申請
- 補卡需主管審核通過後生效
- 補卡成功後，重置遲到/早退狀態與異常類型

#### 7.2.2 程式實作（AttendanceRecord.correctRecord）

```java
public void correctRecord(LocalDateTime checkIn, LocalDateTime checkOut, Shift shift) {
    this.checkInTime = checkIn;
    this.checkOutTime = checkOut;
    this.shiftId = shift.getId().getValue();
    this.isCorrected = true;
    this.anomalyType = AnomalyType.NORMAL;  // 重置異常
    this.isLate = false;                     // 清除遲到
    this.isEarlyLeave = false;               // 清除早退
    this.lateMinutes = 0;
    this.earlyLeaveMinutes = 0;
}
```

#### 7.2.3 補卡後的狀態變化

| 欄位 | 補卡前（遲到） | 補卡後 |
|:---|:---|:---|
| `isLate` | `true` | `false` |
| `lateMinutes` | 30 | 0 |
| `anomalyType` | `LATE` | `NORMAL` |
| `isCorrected` | `false` | `true` |
| `checkInTime` | 原始打卡時間 | 修正後時間 |

### 7.3 缺上班卡 / 缺下班卡

| 異常類型 | 偵測時機 | 處理方式 |
|:---|:---|:---|
| `MISSING_CHECK_IN` | 有下班卡但無上班卡 | 提示員工申請補卡 |
| `MISSING_CHECK_OUT` | 有上班卡但無下班卡（至下班時間後仍未打卡） | 提示員工申請補卡 |

---

## 8. Domain Model 總覽

### 8.1 聚合根與值物件關係

```
Shift（班別聚合根）
├── ShiftId (Value Object)
├── ShiftType (Enum: REGULAR, FLEXIBLE, SHIFT)
├── workStartTime / workEndTime (LocalTime)
├── breakStartTime / breakEndTime (LocalTime)
├── lateToleranceMinutes (int)
├── earlyLeaveToleranceMinutes (int)
├── lateCheckEnabled (boolean)
└── lateSalaryDeduction (boolean)

AttendanceRecord（出勤記錄聚合根）
├── RecordId (Value Object)
├── employeeId (String)
├── date (LocalDate)
├── shiftId (String)
├── checkInTime / checkOutTime (LocalDateTime)
├── isLate (boolean) + lateMinutes (int)
├── isEarlyLeave (boolean) + earlyLeaveMinutes (int)
├── AnomalyType (Enum)
└── isCorrected (boolean)

FlexTimePolicy（彈性工時政策值物件）
├── flexStartEarliest / flexStartLatest (LocalTime)
├── flexEndEarliest / flexEndLatest (LocalTime)
├── coreStartTime / coreEndTime (LocalTime)
└── requiredHoursPerDay (double)
```

### 8.2 Domain Service

| 服務 | 類別 | 職責 |
|:---|:---|:---|
| 出勤計算 | `AttendanceCalculationDomainService` | 計算實際工時（扣除休息時間） |
| 缺勤偵測 | `AbsentDetectionDomainService` | 比對在職/打卡/請假 → 判定缺勤 |

---

## 9. 計算公式與範例

### 9.1 實際工時計算

```
實際工時 = (下班打卡時間 - 上班打卡時間) - 休息時間
```

若打卡時段完整涵蓋休息時段，則扣除休息時間：

```java
if (start.isBefore(breakStart) && end.isAfter(breakEnd)) {
    minutes -= Duration.between(breakStart, breakEnd).toMinutes();
}
```

### 9.2 綜合範例：固定工時制

**員工 A — 日班（09:00-18:00，午休 12:00-13:00，寬限 5 分鐘）**

| 日期 | 上班打卡 | 下班打卡 | 遲到? | 遲到分鐘 | 早退? | 早退分鐘 | 實際工時 | 異常 |
|:---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 03/01 | 08:55 | 18:05 | 否 | 0 | 否 | 0 | 8.17h | NORMAL |
| 03/02 | 09:03 | 18:00 | 否 | 0 | 否 | 0 | 7.95h | NORMAL |
| 03/03 | 09:12 | 18:00 | 是 | 12 | 否 | 0 | 7.80h | LATE |
| 03/04 | 09:00 | 17:50 | 否 | 0 | 是 | 10 | 7.83h | EARLY_LEAVE |
| 03/05 | — | — | — | — | — | — | 0 | ABSENT |

### 9.3 綜合範例：彈性工時制

**員工 B — 彈性班（彈性 08:00-10:00，核心 10:00-17:00，每日 8h）**

| 日期 | 上班打卡 | 下班打卡 | 遲到? | 在彈性區間? | 實際工時 | 工時足夠? | 異常 |
|:---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 03/01 | 08:30 | 17:35 | 否 | 是 | 8.08h | 是 | NORMAL |
| 03/02 | 09:30 | 18:35 | 否 | 是 | 8.08h | 是 | NORMAL |
| 03/03 | 10:00 | 19:05 | 否 | 是 | 8.08h | 是 | NORMAL |
| 03/04 | 10:15 | 19:20 | 是 | 否 | 8.08h | 是 | LATE |
| 03/05 | 09:00 | 16:30 | 否 | 是 | 6.50h | 否 | EARLY_LEAVE |

---

## 10. Domain Event 列表

與遲到/早退/曠職相關的 Domain Event：

### 10.1 AttendanceRecordedEvent

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| `recordId` | String | 打卡記錄 ID |
| `employeeId` | String | 員工 ID |
| `recordDate` | LocalDate | 記錄日期 |
| `checkInTime` | LocalDateTime | 上班打卡時間 |
| `checkOutTime` | LocalDateTime | 下班打卡時間 |
| `isLate` | boolean | 是否遲到 |
| `isEarlyLeave` | boolean | 是否早退 |

**觸發時機：** 上班打卡 / 下班打卡成功後
**訂閱服務：** 無直接訂閱（資料留存供月結使用）

### 10.2 AttendanceAnomalyDetectedEvent

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| `recordId` | String | 打卡記錄 ID |
| `employeeId` | String | 員工 ID |
| `recordDate` | LocalDate | 記錄日期 |
| `anomalyType` | String | 異常類型（LATE / EARLY_LEAVE / ABSENT 等） |
| `anomalyMinutes` | int | 異常分鐘數 |

**觸發時機：** 偵測到遲到、早退、曠職時
**訂閱服務：** Notification Service（發送通知給 HR/主管）

### 10.3 AttendanceMonthClosedEvent

**觸發時機：** 月度差勤結算時
**包含數據：** 月度遲到次數、遲到總分鐘數、早退次數、早退總分鐘數
**訂閱服務：** Payroll Service（計算遲到/早退扣款）、Reporting Service

---

## 11. 相關 API 端點對照

| API | 端點 | 方法 | Controller | 與遲到/早退的關係 |
|:---|:---|:---:|:---|:---|
| 上班打卡 | `/api/v1/attendance/check-in` | POST | `HR03CheckInCmdController` | 判定遲到，回傳 `isLate` + `lateMinutes` |
| 下班打卡 | `/api/v1/attendance/check-out` | POST | `HR03CheckInCmdController` | 判定早退，回傳 `isEarlyLeave` + `earlyLeaveMinutes` |
| 查詢今日狀態 | `/api/v1/attendance/today` | GET | `HR03CheckInQryController` | 顯示今日遲到/早退狀態 |
| 查詢記錄列表 | `/api/v1/attendance/records` | GET | `HR03CheckInQryController` | 可依異常類型篩選 |
| 查詢記錄詳情 | `/api/v1/attendance/records/{id}` | GET | `HR03CheckInQryController` | 含遲到/早退詳細資訊 |
| 補卡申請 | `/api/v1/attendance/corrections` | POST | `HR03CheckInCmdController` | 修正後清除遲到/早退 |
| 班別管理 | `/api/v1/attendance/shifts` | CRUD | `HR03ShiftCmdController` | 設定寬限分鐘數與遲到開關 |
| 月度結算 | `/api/v1/attendance/monthly-close` | POST | `HR03MonthCloseCmdController` | 統計遲到/早退次數與分鐘 |
| 每日報表 | `/api/v1/attendance/reports/daily` | GET | `HR03ReportQryController` | 包含遲到/早退統計 |
| 月度報表 | `/api/v1/attendance/reports/monthly` | GET | `HR03ReportQryController` | 包含遲到/早退匯總 |

---

## 12. 判定流程圖

### 12.1 上班打卡遲到判定

```
開始：員工執行上班打卡
    │
    ├─ [班別遲到判定開關 lateCheckEnabled = false?]
    │     └─ 是 → 跳過遲到判定 → 記錄為 NORMAL
    │
    ├─ [班別類型 = FLEXIBLE?]
    │     ├─ 是 → [打卡時間 > flexStartLatest?]
    │     │         ├─ 是 → 標記遲到（LATE）
    │     │         └─ 否 → NORMAL
    │     │
    │     └─ 否 → [打卡時間 > workStartTime + lateToleranceMinutes?]
    │               ├─ 是 → 標記遲到（LATE）
    │               │       遲到分鐘 = 打卡時間 - workStartTime
    │               │       anomalyType = LATE
    │               │       發布 AttendanceAnomalyDetectedEvent
    │               └─ 否 → NORMAL
    │
    └─ 發布 AttendanceRecordedEvent
```

### 12.2 下班打卡早退判定

```
開始：員工執行下班打卡
    │
    ├─ [打卡時間 < workEndTime - earlyLeaveToleranceMinutes?]
    │     ├─ 是 → 標記早退（EARLY_LEAVE）
    │     │       早退分鐘 = workEndTime - 打卡時間
    │     │       if (anomalyType == NORMAL) anomalyType = EARLY_LEAVE
    │     │       發布 AttendanceAnomalyDetectedEvent
    │     └─ 否 → 不修改異常類型
    │
    └─ 計算實際工時
```

---

## 13. 資料庫欄位對照

### 13.1 shifts 表

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| `late_tolerance_minutes` | INTEGER | 遲到容許分鐘數 |
| `early_leave_tolerance_minutes` | INTEGER | 早退容許分鐘數 |
| `late_check_enabled` | BOOLEAN | 遲到判定開關 |
| `late_salary_deduction` | BOOLEAN | 遲到扣薪開關 |
| `type` | VARCHAR | 班別類型（REGULAR / FLEXIBLE / SHIFT） |

### 13.2 attendance_records 表

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| `is_late` | BOOLEAN | 是否遲到 |
| `late_minutes` | INTEGER | 遲到分鐘數 |
| `is_early_leave` | BOOLEAN | 是否早退 |
| `early_leave_minutes` | INTEGER | 早退分鐘數 |
| `anomaly_type` | VARCHAR | 異常類型 |
| `is_corrected` | BOOLEAN | 是否已補卡修正 |

---

## 14. 邊界案例與注意事項

### 14.1 跨日打卡

若班別跨午夜（如夜班 22:00-06:00），遲到/早退判定需注意日期跨越問題。目前系統以 `record_date` 為基準日期，`checkInTime` 和 `checkOutTime` 為完整的 `LocalDateTime`。

### 14.2 重複打卡

- 同一日重複上班打卡：拋出 `IllegalStateException("Already checked in")`
- 同一日重複下班打卡：拋出 `IllegalStateException("Must check in first")` 或設計書中的 `DomainException("今日已完成下班打卡")`
- `attendance_records` 表有唯一約束 `(employee_id, record_date)`

### 14.3 彈性班的 Shift 表 start_time/end_time 解讀

彈性班在 `shifts` 表中的 `start_time`/`end_time` 記錄的是彈性區間對應的基本上下班時間，而非嚴格的上班/下班限制。真正的彈性區間由 `FlexTimePolicy` 定義。目前實作中，彈性班的 `lateToleranceMinutes` 設為 30 分鐘，等效於彈性區間的長度。

### 14.4 假日/非工作日

曠職偵測排程目前未排除假日與非工作日。在完整實作中，應搭配排班表（work_schedules）或公司行事曆排除非工作日的員工。

---

**文件結束**
