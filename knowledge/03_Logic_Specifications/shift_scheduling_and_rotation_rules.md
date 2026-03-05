# 輪班/值班排程邏輯規格書

**版本:** 1.0
**日期:** 2026-03-05
**適用法規:** 勞動基準法第34條、第36條、第37條、第40條
**所屬服務:** HR03 考勤管理服務 (Attendance)

---

## 目錄

1. [文件概述](#1-文件概述)
2. [班別定義規則](#2-班別定義規則)
3. [排班規則](#3-排班規則)
4. [輪班規則](#4-輪班規則)
5. [值班規則](#5-值班規則)
6. [換班規則](#6-換班規則)
7. [勞基法合規檢查](#7-勞基法合規檢查)
8. [Domain Model](#8-domain-model)
9. [排班演算法](#9-排班演算法)
10. [Domain Event 列表](#10-domain-event-列表)
11. [相關 API 端點對照](#11-相關-api-端點對照)

---

## 1. 文件概述

### 1.1 目的

本文件定義輪班/值班排程的完整業務邏輯規格，供後端工程師實作排班引擎、合規檢查、自動排班演算法等功能。涵蓋班別定義、排班模式（手動/自動）、輪班循環、值班津貼計算、以及勞基法合規驗證等核心邏輯。

### 1.2 適用範圍

- **班別管理（Shift）**：定義各類工作班別的上下班時間、休息時段、容許規則
- **排班管理（ShiftSchedule）**：手動或自動為員工安排每日班別
- **輪班模式（RotationPattern）**：定義循環性的班別序列
- **換班管理（ShiftSwapRequest）**：員工間的班別交換流程
- **值班管理（On-Call/Duty）**：非正常班次的值班安排與津貼

### 1.3 相關文件

| 文件 | 路徑 | 關聯 |
|:---|:---|:---|
| 考勤管理系統設計書 | `knowledge/02_System_Design/03_考勤管理服務系統設計書.md` | 整體架構與 Domain 設計 |
| 考勤管理 API 規格 | `knowledge/04_API_Specifications/03_考勤管理服務系統設計書_API詳細規格.md` | API 端點詳細定義 |
| 變形工時邏輯規格書 | `knowledge/03_Logic_Specifications/variable_hours_rules.md` | 二週/四週/八週變形工時 |
| 加班費計算 | `knowledge/03_Logic_Specifications/variable_hours_rules.md#5` | 加班費率與計算 |

---

## 2. 班別定義規則

### 2.1 班別類型

系統支援三種班別類型（`ShiftType` 列舉）：

| 類型 | 代碼 | 說明 | 遲到判定 | 彈性上下班 |
|:---|:---|:---|:---:|:---:|
| 標準班 | `REGULAR` | 固定上下班時間 | 是（依容許分鐘） | 否 |
| 彈性班 | `FLEXIBLE` | 上下班時間有彈性範圍 | 是（超過最晚時間） | 是 |
| 排班制 | `SHIFT` | 依輪班模式或手動排班 | 是（依容許分鐘） | 否 |

### 2.2 班別屬性定義

```
班別屬性 = {
  shiftId:        UUID        -- 班別唯一識別碼
  code:           String      -- 班別代碼（唯一，如 DAY, NIGHT, FLEX）
  name:           String      -- 班別名稱（如「日班」、「夜班」）
  organizationId: UUID        -- 所屬組織
  type:           ShiftType   -- 班別類型
  workStartTime:  LocalTime   -- 上班時間
  workEndTime:    LocalTime   -- 下班時間
  breakStartTime: LocalTime?  -- 休息開始時間（nullable）
  breakEndTime:   LocalTime?  -- 休息結束時間（nullable）
  lateToleranceMinutes:       int  -- 遲到容許分鐘數（預設 0）
  earlyLeaveToleranceMinutes: int  -- 早退容許分鐘數（預設 0）
  lateCheckEnabled:           boolean -- 是否啟用遲到判定
  lateSalaryDeduction:        boolean -- 遲到是否扣薪
  isActive:       boolean     -- 是否啟用
}
```

### 2.3 預設班別定義

| 代碼 | 名稱 | 類型 | 上班 | 下班 | 休息 | 遲到容許 | 備註 |
|:---|:---|:---|:---:|:---:|:---:|:---:|:---|
| `DAY` | 日班 | REGULAR | 09:00 | 18:00 | 12:00-13:00 | 5 分鐘 | 標準朝九晚六 |
| `FLEX` | 彈性班 | FLEXIBLE | 08:00 | 17:00 | 12:00-13:00 | 30 分鐘 | 彈性上下班 |
| `NIGHT` | 晚班 | REGULAR | 14:00 | 23:00 | 18:00-19:00 | 5 分鐘 | 適用輪班制 |
| `MORNING` | 早班 | REGULAR | 06:00 | 14:00 | 10:00-10:30 | 5 分鐘 | 製造業常用 |
| `GRAVEYARD` | 大夜班 | REGULAR | 22:00 | 06:00 | 02:00-02:30 | 5 分鐘 | 跨日班別 |

### 2.4 跨日班別處理

當 `workEndTime < workStartTime` 時，視為跨日班別（如大夜班 22:00-06:00）。

```typescript
function isOvernight(shift: Shift): boolean {
  return shift.workEndTime.isBefore(shift.workStartTime);
}

// 計算實際工時（跨日）
function calculateWorkingHours(shift: Shift): number {
  if (isOvernight(shift)) {
    // 24 小時 - 開始到午夜 + 午夜到結束 - 休息
    const totalMinutes = (24 * 60 - toMinutes(shift.workStartTime)) + toMinutes(shift.workEndTime);
    const breakMinutes = calculateBreakMinutes(shift);
    return (totalMinutes - breakMinutes) / 60;
  }
  const totalMinutes = toMinutes(shift.workEndTime) - toMinutes(shift.workStartTime);
  const breakMinutes = calculateBreakMinutes(shift);
  return (totalMinutes - breakMinutes) / 60;
}
```

### 2.5 班別驗證規則

| 編號 | 驗證項目 | 規則 | 錯誤碼 |
|:---:|:---|:---|:---|
| V-S01 | 上下班時間 | 不可為 null | `SHIFT_TIME_REQUIRED` |
| V-S02 | 休息時間一致性 | breakStart 與 breakEnd 必須同時有值或同時為 null | `BREAK_TIME_INCONSISTENT` |
| V-S03 | 休息時間合理性 | breakStart < breakEnd | `BREAK_TIME_INVALID` |
| V-S04 | 班別代碼唯一性 | 同組織下不可重複 | `SHIFT_CODE_DUPLICATE` |
| V-S05 | 容許分鐘非負 | lateToleranceMinutes >= 0、earlyLeaveToleranceMinutes >= 0 | `TOLERANCE_NEGATIVE` |
| V-S06 | 最低工時 | 排除休息後，每日正常工時 <= 12 小時 | `SHIFT_HOURS_EXCEED` |

---

## 3. 排班規則

### 3.1 排班模式

系統支援兩種排班模式：

| 模式 | 說明 | rotationPatternId |
|:---|:---|:---|
| **手動排班** | HR 或主管逐日為員工指定班別 | `null` |
| **自動排班** | 依據輪班模式自動產生排班表 | 關聯的輪班模式 ID |

### 3.2 排班狀態流轉

```
                   ┌─────────────┐
                   │   DRAFT     │  草稿（可編輯）
                   │   排班中     │
                   └──────┬──────┘
                          │ publish()
                          ▼
                   ┌─────────────┐
         ┌────────│  PUBLISHED  │  已發佈（員工可見）
         │        │   已發佈     │
         │        └──────┬──────┘
         │               │ lock()
         │ revertToDraft()│
         │               ▼
         │        ┌─────────────┐
         │        │   LOCKED    │  已鎖定（月結後不可修改）
         │        │   已鎖定     │
         │        └─────────────┘
         │
         └──────→ 退回草稿（LOCKED 不可退回）
```

### 3.3 排班約束條件

| 編號 | 約束 | 規則 | 說明 |
|:---:|:---|:---|:---|
| C-SC01 | 單日唯一 | 同一員工同一日期只能有一筆排班 | DB UNIQUE (employee_id, schedule_date) |
| C-SC02 | 班別存在 | 排班指定的 shiftId 必須為已啟用的班別 | 驗證 `isActive = true` |
| C-SC03 | 鎖定不可改 | LOCKED 狀態的排班不可變更班別 | `changeShift()` 會拋出例外 |
| C-SC04 | 發佈條件 | 僅 DRAFT 可發佈 | `publish()` 驗證前置狀態 |
| C-SC05 | 鎖定條件 | 僅 PUBLISHED 可鎖定 | `lock()` 驗證前置狀態 |
| C-SC06 | 未來排班 | 排班日期不可早於當前日期（新建時） | 防止回溯建立排班 |
| C-SC07 | 排班範圍 | 一次排班最長 90 天 | 防止過長範圍影響效能 |

### 3.4 手動排班流程

```
1. HR 選擇員工與日期範圍
2. 逐日或批次指定班別
3. 系統驗證約束條件（C-SC01 ~ C-SC07）
4. 儲存為 DRAFT 狀態
5. HR 確認後發佈（DRAFT → PUBLISHED）
6. 月結時鎖定（PUBLISHED → LOCKED）
```

### 3.5 批次排班

支援一次為多名員工在指定日期範圍設定相同班別：

```typescript
interface BatchScheduleRequest {
  employeeIds: string[];        // 員工清單
  shiftId: string;              // 指定班別
  startDate: LocalDate;         // 起始日
  endDate: LocalDate;           // 結束日
  excludeWeekends: boolean;     // 是否排除週末
  excludeHolidays: boolean;     // 是否排除國定假日
}

// 驗證邏輯
function validateBatchSchedule(request: BatchScheduleRequest): ValidationResult {
  let errors: string[] = [];

  // 日期範圍不超過 90 天
  if (daysBetween(request.startDate, request.endDate) > 90) {
    errors.push("排班範圍不可超過 90 天");
  }

  // 員工數量上限
  if (request.employeeIds.length > 200) {
    errors.push("單次排班員工數不可超過 200 人");
  }

  // 檢查衝突
  for (const empId of request.employeeIds) {
    const existing = findExistingSchedules(empId, request.startDate, request.endDate);
    if (existing.length > 0) {
      errors.push(`員工 ${empId} 在 ${existing[0].scheduleDate} 已有排班`);
    }
  }

  return { isValid: errors.length === 0, errors };
}
```

---

## 4. 輪班規則

### 4.1 輪班模式定義

輪班模式（`RotationPattern`）定義一個循環的班別序列，系統依據此序列自動為員工產生排班表。

```
輪班模式 = {
  patternId:       UUID                -- 輪班模式 ID
  organizationId:  UUID                -- 所屬組織
  name:            String              -- 名稱（如「三班兩輪」）
  code:            String              -- 代碼（如 ROT-3S2R）
  cycleDays:       int                 -- 循環天數
  rotationDays:    List<RotationDay>   -- 天序清單
  isActive:        boolean             -- 是否啟用
}

RotationDay = {
  dayOrder:   int       -- 循環中第幾天（1-based）
  shiftId:    ShiftId?  -- 班別（休息日為 null）
  isRestDay:  boolean   -- 是否為休息日
}
```

### 4.2 常見輪班模式範例

#### 4.2.1 三班制（早/中/晚）— 21 天循環

```
循環天數: 21
天序:
  Day 1-5:  早班 (MORNING 06:00-14:00)
  Day 6-7:  休息
  Day 8-12: 中班 (DAY 14:00-22:00)
  Day 13-14: 休息
  Day 15-19: 晚班 (NIGHT 22:00-06:00)
  Day 20-21: 休息
```

| 天序 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 | 18 | 19 | 20 | 21 |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 班別 | 早 | 早 | 早 | 早 | 早 | 休 | 休 | 中 | 中 | 中 | 中 | 中 | 休 | 休 | 晚 | 晚 | 晚 | 晚 | 晚 | 休 | 休 |

#### 4.2.2 兩班制（日/夜）— 14 天循環

```
循環天數: 14
天序:
  Day 1-5:  日班 (DAY 09:00-18:00)
  Day 6-7:  休息
  Day 8-12: 夜班 (NIGHT 14:00-23:00)
  Day 13-14: 休息
```

#### 4.2.3 四班二輪 — 8 天循環

```
循環天數: 8
天序:
  Day 1-2: 日班
  Day 3-4: 夜班
  Day 5-8: 休息
```

### 4.3 輪班驗證規則

| 編號 | 驗證項目 | 規則 | 說明 |
|:---:|:---|:---|:---|
| V-R01 | 天序數量 | `rotationDays.size() == cycleDays` | 天序必須恰好等於循環天數 |
| V-R02 | 天序連續 | dayOrder 從 1 開始連續 | 不可跳號 |
| V-R03 | 工作日班別 | 非休息日必須指定有效班別 | `!isRestDay => shiftId != null` |
| V-R04 | 最少休息 | 每 7 天至少 1 天休息 | 勞基法第 36 條 |
| V-R05 | 循環天數 | cycleDays >= 1 且 cycleDays <= 56 | 最長不超過 8 週循環 |
| V-R06 | 夜班後休息 | 夜班後至少 11 小時間隔再上班 | 勞基法第 34 條 |

### 4.4 輪班起算日

每位員工有各自的「輪班起算日」（`rotationStart`），決定其在循環中從第幾天開始。

```typescript
// 計算員工在指定日期應排的班別
function getShiftForDate(
  pattern: RotationPattern,
  targetDate: LocalDate,
  rotationStart: LocalDate
): RotationDay {
  // 計算目標日期距離起算日的天數
  let dayOffset = ChronoUnit.DAYS.between(rotationStart, targetDate);

  // 處理 mod 負數（起算日在目標日期之後）
  dayOffset = ((dayOffset % pattern.cycleDays) + pattern.cycleDays) % pattern.cycleDays;

  return pattern.getDayForIndex(dayOffset);
}
```

**範例：** 輪班起算日為 2026-03-01，循環天數為 7

| 日期 | 3/1 | 3/2 | 3/3 | 3/4 | 3/5 | 3/6 | 3/7 | 3/8 | 3/9 |
|:---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| dayOffset | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 0 | 1 |
| 天序 | Day1 | Day2 | Day3 | Day4 | Day5 | Day6 | Day7 | Day1 | Day2 |

### 4.5 輪換間隔規則

依據勞基法第 34 條，勞工工作採輪班制者，其工作班次每週更換一次。更換班次時，至少應有連續 11 小時之休息時間。

```typescript
// 驗證輪班更換間隔
function validateRotationGap(
  currentShift: Shift,   // 舊班別
  nextShift: Shift,      // 新班別
  gapHours: number       // 兩班間隔小時數
): ValidationResult {
  const MIN_GAP_HOURS = 11; // 勞基法規定最少 11 小時

  if (gapHours < MIN_GAP_HOURS) {
    return {
      isValid: false,
      errors: [`班次更換間隔 ${gapHours} 小時，未達法定 ${MIN_GAP_HOURS} 小時`]
    };
  }
  return { isValid: true, errors: [] };
}

// 計算兩班之間的間隔
function calculateGapHours(prevShift: Shift, nextShift: Shift): number {
  // 前班下班時間到後班上班時間的小時數
  if (prevShift.isOvernight()) {
    // 跨日班：下班時間為隔天
    return hoursBetween(prevShift.workEndTime, nextShift.workStartTime);
  }
  return hoursBetween(prevShift.workEndTime, nextShift.workStartTime);
}
```

---

## 5. 值班規則

### 5.1 值班類型

值班是指在非正常排定班次期間，員工需待命或到場處理緊急事務。

| 值班類型 | 代碼 | 說明 | 是否需到場 |
|:---|:---|:---|:---:|
| 假日值班 | `HOLIDAY_DUTY` | 國定假日或休息日到場值班 | 是 |
| 電話待命 | `ON_CALL` | 非上班時段電話待命 | 視情況 |
| 緊急召回 | `EMERGENCY_CALLBACK` | 非上班時段被召回到場 | 是 |
| 夜間巡查 | `NIGHT_PATROL` | 夜間巡邏值班 | 是 |

### 5.2 值班資料模型

```
DutySchedule = {
  dutyId:        UUID
  employeeId:    UUID
  dutyType:      DutyType            -- 值班類型
  dutyDate:      LocalDate           -- 值班日期
  startTime:     LocalTime           -- 值班開始時間
  endTime:       LocalTime           -- 值班結束時間
  actualHours:   BigDecimal?         -- 實際值班時數（結算時填入）
  isActivated:   boolean             -- 是否被實際啟動（ON_CALL 是否被叫出）
  note:          String?             -- 備註
  status:        DutyStatus          -- 狀態
}

DutyStatus = SCHEDULED | IN_PROGRESS | COMPLETED | CANCELLED
```

### 5.3 值班津貼計算

值班津貼依值班類型與日期類型計算：

| 值班類型 | 平日 | 休息日 | 例假日/國定假日 |
|:---|:---:|:---:|:---:|
| 假日值班（到場） | -- | 日薪 x 1.34 (前 2h) / 1.67 (3-8h) | 日薪 x 2.0 + 補假 1 日 |
| 電話待命（未啟動） | 待命津貼 (固定額) | 待命津貼 x 1.5 | 待命津貼 x 2.0 |
| 電話待命（已啟動） | 同假日值班計算 | 同假日值班計算 | 同假日值班計算 |
| 緊急召回 | 最低 4 小時加班費 | 最低 4 小時加班費 | 日薪 x 2.0 + 補假 1 日 |

```typescript
interface DutyAllowanceConfig {
  onCallBaseAllowance: number;        // 待命基本津貼（每次）
  emergencyCallbackMinHours: number;  // 緊急召回最低計算時數（預設 4）
  holidayDutyMultiplier: number;      // 假日值班倍率
}

function calculateDutyAllowance(
  duty: DutySchedule,
  monthlySalary: number,
  dayType: 'WEEKDAY' | 'REST_DAY' | 'HOLIDAY',
  config: DutyAllowanceConfig
): DutyAllowanceResult {
  const hourlyRate = monthlySalary / 30 / 8;
  let allowance = 0;
  let compensatoryLeave = 0; // 補假天數

  switch (duty.dutyType) {
    case 'HOLIDAY_DUTY':
    case 'NIGHT_PATROL':
      if (dayType === 'HOLIDAY') {
        // 例假日：2 倍日薪 + 補假 1 日
        allowance = duty.actualHours * hourlyRate * 2;
        compensatoryLeave = 1;
      } else if (dayType === 'REST_DAY') {
        // 休息日加班費率
        const h = duty.actualHours;
        allowance += Math.min(h, 2) * hourlyRate * 1.34;
        allowance += Math.max(0, Math.min(h - 2, 6)) * hourlyRate * 1.67;
        allowance += Math.max(0, h - 8) * hourlyRate * 2.67;
      }
      break;

    case 'ON_CALL':
      if (!duty.isActivated) {
        // 未啟動：固定待命津貼
        const multiplier = dayType === 'HOLIDAY' ? 2.0
                         : dayType === 'REST_DAY' ? 1.5 : 1.0;
        allowance = config.onCallBaseAllowance * multiplier;
      } else {
        // 已啟動：視同加班
        allowance = calculateOvertimePay(hourlyRate, duty.actualHours, dayType);
      }
      break;

    case 'EMERGENCY_CALLBACK':
      // 最低 4 小時
      const effectiveHours = Math.max(duty.actualHours, config.emergencyCallbackMinHours);
      if (dayType === 'HOLIDAY') {
        allowance = effectiveHours * hourlyRate * 2;
        compensatoryLeave = 1;
      } else {
        allowance = calculateOvertimePay(hourlyRate, effectiveHours, dayType);
      }
      break;
  }

  return {
    allowance: Math.round(allowance),
    compensatoryLeave,
    dutyType: duty.dutyType,
    dayType
  };
}
```

### 5.4 值班排班規則

| 編號 | 規則 | 說明 |
|:---:|:---|:---|
| D-01 | 值班間隔 | 同一員工連續值班不可超過 2 天 |
| D-02 | 值班與正班衝突 | 值班時段不可與正常排班完全重疊 |
| D-03 | 值班輪替 | 值班應在部門內公平輪替 |
| D-04 | 值班通知 | 值班排定後至少提前 3 天通知 |
| D-05 | 值班拒絕 | 員工因合理事由可申請免除值班（須主管核准） |

---

## 6. 換班規則

### 6.1 換班流程

換班是指兩位員工互換各自某日的班別。

```
                 申請人提出
                    │
                    ▼
            ┌───────────────┐
            │ PENDING_      │
            │ COUNTERPART   │ 等待對方同意
            └───────┬───────┘
                    │
           ┌───────┴───────┐
           ▼               ▼
    對方同意            對方拒絕
           │               │
           ▼               ▼
    ┌───────────┐   ┌───────────┐
    │ PENDING_  │   │ REJECTED  │
    │ APPROVAL  │   └───────────┘
    └─────┬─────┘
          │
     ┌────┴────┐
     ▼         ▼
  核准      駁回
     │         │
     ▼         ▼
┌─────────┐ ┌─────────┐
│APPROVED │ │REJECTED │
└─────────┘ └─────────┘

任何未結案狀態 → cancel() → CANCELLED
```

### 6.2 換班驗證規則

| 編號 | 驗證項目 | 規則 |
|:---:|:---|:---|
| V-SW01 | 不可自換 | 申請人 != 交換對象 |
| V-SW02 | 排班存在 | 雙方在指定日期都有排班記錄 |
| V-SW03 | 排班未鎖定 | 雙方排班狀態不可為 LOCKED |
| V-SW04 | 日期合理 | 換班日期不可早於當前日期 |
| V-SW05 | 資格檢查 | 雙方需具備對方班別的工作資格（如證照要求） |
| V-SW06 | 合規檢查 | 換班後不可違反連續工作天數、休息間隔等勞基法規定 |

### 6.3 換班執行

核准後，`ShiftSchedulingDomainService.executeSwap()` 交換雙方的 shiftId：

```java
public void executeSwap(ShiftSchedule scheduleA, ShiftSchedule scheduleB) {
    ShiftId shiftA = scheduleA.getShiftId();
    ShiftId shiftB = scheduleB.getShiftId();

    scheduleA.changeShift(shiftB);
    scheduleB.changeShift(shiftA);
}
```

---

## 7. 勞基法合規檢查

### 7.1 適用法條

| 法條 | 內容 | 系統實作 |
|:---|:---|:---|
| 第 34 條 | 輪班更換至少休息 11 小時 | 輪班間隔檢查 |
| 第 36 條 | 每 7 日應有 2 日休息（1 例假 + 1 休息日） | 連續工作天數檢查 |
| 第 37 條 | 國定假日應休假 | 假日排班警告 |
| 第 40 條 | 天災等緊急狀況始得要求例假日出勤 | 例假日值班限制 |
| 第 49 條 | 女性夜間工作限制（需工會/勞資會議同意） | 夜班性別檢查 |

### 7.2 連續工作天數檢查

```typescript
const MAX_CONSECUTIVE_WORK_DAYS = 6; // 每 7 日至少休 1 日例假

function checkConsecutiveWorkDays(
  schedules: ShiftSchedule[],
  startDate: LocalDate,
  endDate: LocalDate
): ComplianceResult {
  let consecutiveDays = 0;
  let violations: ComplianceViolation[] = [];

  for (let date = startDate; date <= endDate; date = date.plusDays(1)) {
    const schedule = findScheduleForDate(schedules, date);

    if (schedule != null && !schedule.isRestDay) {
      consecutiveDays++;
      if (consecutiveDays > MAX_CONSECUTIVE_WORK_DAYS) {
        violations.push({
          type: 'CONSECUTIVE_WORK_EXCEEDED',
          date: date,
          detail: `連續工作 ${consecutiveDays} 天，超過法定上限 ${MAX_CONSECUTIVE_WORK_DAYS} 天`,
          lawReference: '勞基法第 36 條'
        });
      }
    } else {
      consecutiveDays = 0;
    }
  }

  return {
    isCompliant: violations.length === 0,
    violations
  };
}
```

### 7.3 每週休息日檢查

```typescript
// 每 7 日至少 2 日休息（1 例假 + 1 休息日）
function checkWeeklyRest(
  schedules: ShiftSchedule[],
  weekStart: LocalDate
): ComplianceResult {
  const weekEnd = weekStart.plusDays(6);
  const restDays = countRestDays(schedules, weekStart, weekEnd);

  if (restDays < 2) {
    return {
      isCompliant: false,
      violations: [{
        type: 'INSUFFICIENT_WEEKLY_REST',
        date: weekStart,
        detail: `本週僅有 ${restDays} 天休息，未達法定 2 天`,
        lawReference: '勞基法第 36 條'
      }]
    };
  }

  return { isCompliant: true, violations: [] };
}
```

### 7.4 輪班間隔檢查

```typescript
const MIN_SHIFT_CHANGE_REST_HOURS = 11;

function checkShiftChangeInterval(
  prevSchedule: { shift: Shift, date: LocalDate },
  nextSchedule: { shift: Shift, date: LocalDate }
): ComplianceResult {
  // 計算前班結束到後班開始的間隔
  const prevEnd = combineDateAndTime(prevSchedule.date, prevSchedule.shift.workEndTime);
  const nextStart = combineDateAndTime(nextSchedule.date, nextSchedule.shift.workStartTime);

  // 處理跨日班別
  if (prevSchedule.shift.isOvernight()) {
    prevEnd = prevEnd.plusDays(1);
  }

  const gapHours = hoursBetween(prevEnd, nextStart);

  if (gapHours < MIN_SHIFT_CHANGE_REST_HOURS) {
    return {
      isCompliant: false,
      violations: [{
        type: 'INSUFFICIENT_SHIFT_CHANGE_REST',
        date: nextSchedule.date,
        detail: `班次更換間隔僅 ${gapHours} 小時，未達法定 ${MIN_SHIFT_CHANGE_REST_HOURS} 小時`,
        lawReference: '勞基法第 34 條'
      }]
    };
  }

  return { isCompliant: true, violations: [] };
}
```

### 7.5 女性夜間工作檢查

```typescript
// 勞基法第 49 條：女性勞工深夜工作（22:00-06:00）需額外條件
function checkNightWorkForFemale(
  employee: { gender: string },
  shift: Shift
): ComplianceResult {
  if (employee.gender !== 'FEMALE') {
    return { isCompliant: true, violations: [] };
  }

  const NIGHT_START = LocalTime.of(22, 0);
  const NIGHT_END = LocalTime.of(6, 0);

  const isNightShift = shift.workStartTime.isAfter(NIGHT_START) ||
                       shift.workStartTime.isBefore(NIGHT_END) ||
                       shift.workEndTime.isAfter(NIGHT_START) ||
                       shift.isOvernight();

  if (isNightShift) {
    return {
      isCompliant: false,  // 需額外審查
      violations: [{
        type: 'FEMALE_NIGHT_WORK',
        detail: '女性員工夜間工作需取得工會或勞資會議同意，並提供安全設施',
        lawReference: '勞基法第 49 條',
        severity: 'WARNING'  // 警告等級，非直接違規
      }]
    };
  }

  return { isCompliant: true, violations: [] };
}
```

### 7.6 綜合合規檢查（排班前驗證）

```typescript
function validateScheduleCompliance(
  employee: Employee,
  newSchedules: ShiftSchedule[],
  existingSchedules: ShiftSchedule[],
  shifts: Map<string, Shift>
): ComplianceResult {
  const allSchedules = [...existingSchedules, ...newSchedules];
  const violations: ComplianceViolation[] = [];

  // 1. 連續工作天數
  const consecutive = checkConsecutiveWorkDays(allSchedules, rangeStart, rangeEnd);
  violations.push(...consecutive.violations);

  // 2. 每週休息日
  for (let weekStart = rangeStart; weekStart <= rangeEnd; weekStart = weekStart.plusDays(7)) {
    const weekly = checkWeeklyRest(allSchedules, weekStart);
    violations.push(...weekly.violations);
  }

  // 3. 輪班間隔（班次變更時）
  for (let i = 1; i < allSchedules.length; i++) {
    const prev = allSchedules[i - 1];
    const curr = allSchedules[i];
    if (prev.shiftId !== curr.shiftId) {
      const interval = checkShiftChangeInterval(
        { shift: shifts.get(prev.shiftId), date: prev.scheduleDate },
        { shift: shifts.get(curr.shiftId), date: curr.scheduleDate }
      );
      violations.push(...interval.violations);
    }
  }

  // 4. 女性夜間工作
  for (const schedule of newSchedules) {
    const shift = shifts.get(schedule.shiftId);
    const nightCheck = checkNightWorkForFemale(employee, shift);
    violations.push(...nightCheck.violations);
  }

  return {
    isCompliant: violations.filter(v => v.severity !== 'WARNING').length === 0,
    violations
  };
}
```

---

## 8. Domain Model

### 8.1 聚合根一覽

| 聚合根 | 類別 | 職責 | 檔案路徑 |
|:---|:---|:---|:---|
| Shift | `Shift` | 班別定義與工時規則 | `domain/model/aggregate/Shift.java` |
| ShiftSchedule | `ShiftSchedule` | 員工每日班別指派 | `domain/model/aggregate/ShiftSchedule.java` |
| RotationPattern | `RotationPattern` | 輪班循環模式定義 | `domain/model/aggregate/RotationPattern.java` |
| ShiftSwapRequest | `ShiftSwapRequest` | 換班申請與審核流程 | `domain/model/aggregate/ShiftSwapRequest.java` |

### 8.2 值物件 (Value Object)

| 值物件 | 說明 | 檔案路徑 |
|:---|:---|:---|
| `ShiftId` | 班別 ID | `domain/model/valueobject/ShiftId.java` |
| `ScheduleId` | 排班 ID | `domain/model/valueobject/ScheduleId.java` |
| `RotationPatternId` | 輪班模式 ID | `domain/model/valueobject/RotationPatternId.java` |
| `SwapRequestId` | 換班申請 ID | `domain/model/valueobject/SwapRequestId.java` |
| `ShiftType` | 班別類型列舉 (REGULAR/FLEXIBLE/SHIFT) | `domain/model/valueobject/ShiftType.java` |
| `ScheduleStatus` | 排班狀態列舉 (DRAFT/PUBLISHED/LOCKED) | `domain/model/valueobject/ScheduleStatus.java` |
| `SwapStatus` | 換班狀態列舉 | `domain/model/valueobject/SwapStatus.java` |
| `FlexTimePolicy` | 彈性工時政策 | `domain/model/valueobject/FlexTimePolicy.java` |
| `RotationDay` | 輪班天序（內嵌於 RotationPattern） | RotationPattern 內部類別 |

### 8.3 Domain Service

| 服務 | 類別 | 職責 | 方法 |
|:---|:---|:---|:---|
| 排班 Domain Service | `ShiftSchedulingDomainService` | 自動排班與換班執行 | `generateSchedules()`, `executeSwap()` |
| 出勤計算 Domain Service | `AttendanceCalculationDomainService` | 工時計算 | `calculateWorkingHours()` |

### 8.4 Repository 介面

| Repository | 介面 | 主要方法 |
|:---|:---|:---|
| 班別 | `IShiftRepository` | `save`, `findById`, `findAll` |
| 排班 | `IShiftScheduleRepository` | `save`, `findById`, `findByEmployeeIdAndDateRange`, `findByDate` |
| 輪班模式 | `IRotationPatternRepository` | `save`, `findById`, `findAll` |
| 換班申請 | `IShiftSwapRequestRepository` | `save`, `findById`, `findByRequesterId`, `findByCounterpartId` |

### 8.5 資料庫 Schema

```sql
-- 班別表
CREATE TABLE shifts (
    id           VARCHAR(50) PRIMARY KEY,
    organization_id VARCHAR(50),
    code         VARCHAR(50) NOT NULL,
    name         VARCHAR(100) NOT NULL,
    type         VARCHAR(50) NOT NULL,           -- REGULAR / FLEXIBLE / SHIFT
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    break_start_time TIME,
    break_end_time   TIME,
    late_tolerance_minutes      INTEGER DEFAULT 0,
    early_leave_tolerance_minutes INTEGER DEFAULT 0,
    late_check_enabled          BOOLEAN DEFAULT TRUE,
    late_salary_deduction       BOOLEAN DEFAULT TRUE,
    is_active    BOOLEAN DEFAULT TRUE,
    is_deleted   BOOLEAN DEFAULT FALSE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 排班表
CREATE TABLE shift_schedules (
    id               VARCHAR(50) PRIMARY KEY,
    employee_id      VARCHAR(50) NOT NULL,
    shift_id         VARCHAR(50) NOT NULL,
    schedule_date    DATE NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT / PUBLISHED / LOCKED
    rotation_pattern_id VARCHAR(50),
    note             TEXT,
    is_deleted       BOOLEAN DEFAULT FALSE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_schedule_emp_date UNIQUE (employee_id, schedule_date)
);

-- 輪班模式表
CREATE TABLE rotation_patterns (
    id               VARCHAR(50) PRIMARY KEY,
    organization_id  VARCHAR(50) NOT NULL,
    name             VARCHAR(100) NOT NULL,
    code             VARCHAR(50) NOT NULL,
    cycle_days       INTEGER NOT NULL,
    is_active        BOOLEAN DEFAULT TRUE,
    is_deleted       BOOLEAN DEFAULT FALSE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 輪班天序表
CREATE TABLE rotation_days (
    id           VARCHAR(50) PRIMARY KEY,
    pattern_id   VARCHAR(50) NOT NULL,
    day_order    INTEGER NOT NULL,
    shift_id     VARCHAR(50),
    is_rest_day  BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_rotation_day_order UNIQUE (pattern_id, day_order)
);

-- 換班申請表
CREATE TABLE shift_swap_requests (
    id               VARCHAR(50) PRIMARY KEY,
    requester_id     VARCHAR(50) NOT NULL,
    counterpart_id   VARCHAR(50) NOT NULL,
    requester_date   DATE NOT NULL,
    counterpart_date DATE NOT NULL,
    requester_shift_id   VARCHAR(50),
    counterpart_shift_id VARCHAR(50),
    status           VARCHAR(30) NOT NULL DEFAULT 'PENDING_COUNTERPART',
    reason           TEXT,
    rejection_reason TEXT,
    approver_id      VARCHAR(50),
    is_deleted       BOOLEAN DEFAULT FALSE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 9. 排班演算法

### 9.1 自動排班演算法

`ShiftSchedulingDomainService.generateSchedules()` 核心邏輯：

```
輸入：
  - employeeId:    員工 ID
  - pattern:       輪班模式（含天序）
  - startDate:     排班起始日
  - endDate:       排班結束日
  - rotationStart: 員工輪班循環起算日

輸出：
  - List<ShiftSchedule>: 產生的排班列表（不含休息日）

演算法：
  1. 驗證輪班模式已設定天序
  2. 計算日期範圍天數 totalDays = endDate - startDate + 1
  3. 遍歷每一天：
     a. 計算 dayOffset = (currentDate - rotationStart) mod cycleDays
     b. 處理負 mod: dayOffset = ((dayOffset % cycleDays) + cycleDays) % cycleDays
     c. 取得 RotationDay = pattern.getDayForIndex(dayOffset)
     d. 若非休息日：
        - 建立 ShiftSchedule（DRAFT 狀態）
        - 設定 rotationPatternId 關聯
     e. 若為休息日：跳過（不建立記錄）
  4. 回傳排班列表
```

**Java 實作（已存在）：**

```java
public List<ShiftSchedule> generateSchedules(String employeeId, RotationPattern pattern,
        LocalDate startDate, LocalDate endDate, LocalDate rotationStart) {

    if (pattern.getRotationDays().isEmpty()) {
        throw new IllegalStateException("輪班模式尚未設定天序");
    }

    List<ShiftSchedule> schedules = new ArrayList<>();
    long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

    for (int i = 0; i < totalDays; i++) {
        LocalDate currentDate = startDate.plusDays(i);
        int dayOffset = (int) ChronoUnit.DAYS.between(rotationStart, currentDate);
        dayOffset = ((dayOffset % pattern.getCycleDays()) + pattern.getCycleDays()) % pattern.getCycleDays();

        RotationDay rotationDay = pattern.getDayForIndex(dayOffset);

        if (!rotationDay.isRestDay()) {
            ShiftSchedule schedule = new ShiftSchedule(
                    ScheduleId.generate(), employeeId, rotationDay.getShiftId(), currentDate);
            schedule.setRotationPatternId(pattern.getId().getValue());
            schedules.add(schedule);
        }
    }

    return schedules;
}
```

### 9.2 公平排班演算法（值班輪替）

確保部門內值班由員工公平分擔：

```typescript
// 公平分配值班
function distributeDuties(
  employees: Employee[],
  dutyDates: LocalDate[],
  dutyHistory: Map<string, number>  // employeeId → 歷史值班次數
): DutyAssignment[] {
  const assignments: DutyAssignment[] = [];

  // 依歷史值班次數排序（次數少的優先）
  const sorted = [...employees].sort((a, b) => {
    const countA = dutyHistory.get(a.id) || 0;
    const countB = dutyHistory.get(b.id) || 0;
    return countA - countB;
  });

  let idx = 0;
  for (const date of dutyDates) {
    const employee = sorted[idx % sorted.length];

    // 檢查是否可排班（排除請假、連續值班等）
    if (isAvailableForDuty(employee, date)) {
      assignments.push({
        employeeId: employee.id,
        dutyDate: date,
        status: 'SCHEDULED'
      });
      // 更新計數
      dutyHistory.set(employee.id, (dutyHistory.get(employee.id) || 0) + 1);
    }
    idx++;
  }

  return assignments;
}
```

### 9.3 排班衝突檢測

```typescript
interface ScheduleConflict {
  type: 'DUPLICATE_SCHEDULE' | 'LEAVE_CONFLICT' | 'DUTY_CONFLICT' | 'COMPLIANCE_VIOLATION';
  date: LocalDate;
  detail: string;
}

function detectConflicts(
  newSchedules: ShiftSchedule[],
  existingSchedules: ShiftSchedule[],
  leaveApplications: LeaveApplication[],
  dutySchedules: DutySchedule[]
): ScheduleConflict[] {
  const conflicts: ScheduleConflict[] = [];

  for (const schedule of newSchedules) {
    // 1. 重複排班
    const dup = existingSchedules.find(
      s => s.employeeId === schedule.employeeId && s.scheduleDate === schedule.scheduleDate
    );
    if (dup) {
      conflicts.push({
        type: 'DUPLICATE_SCHEDULE',
        date: schedule.scheduleDate,
        detail: `員工在 ${schedule.scheduleDate} 已有排班`
      });
    }

    // 2. 請假衝突
    const leave = leaveApplications.find(
      l => l.employeeId === schedule.employeeId &&
           l.status === 'APPROVED' &&
           schedule.scheduleDate >= l.startDate &&
           schedule.scheduleDate <= l.endDate
    );
    if (leave) {
      conflicts.push({
        type: 'LEAVE_CONFLICT',
        date: schedule.scheduleDate,
        detail: `員工在 ${schedule.scheduleDate} 已有核准的請假`
      });
    }
  }

  return conflicts;
}
```

---

## 10. Domain Event 列表

### 10.1 排班相關事件

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 說明 |
|:---|:---|:---|:---|:---|
| `SchedulePublishedEvent` | 排班發佈 | Attendance | Notification | 通知員工排班已公佈 |
| `ScheduleChangedEvent` | 排班班別變更 | Attendance | Notification | 通知員工班別異動 |
| `ScheduleLockedEvent` | 排班鎖定 | Attendance | Payroll | 月結觸發，供薪資計算 |

### 10.2 輪班相關事件

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 說明 |
|:---|:---|:---|:---|:---|
| `RotationPatternCreatedEvent` | 輪班模式建立 | Attendance | -- | 內部審計用 |
| `RotationScheduleGeneratedEvent` | 自動排班完成 | Attendance | Notification | 通知相關員工 |

### 10.3 換班相關事件

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 說明 |
|:---|:---|:---|:---|:---|
| `ShiftSwapRequestedEvent` | 換班申請提交 | Attendance | Notification | 通知交換對象 |
| `ShiftSwapAcceptedEvent` | 對方同意換班 | Attendance | Notification | 通知申請人，並轉交主管審核 |
| `ShiftSwapApprovedEvent` | 主管核准換班 | Attendance | Notification | 通知雙方換班成功 |
| `ShiftSwapRejectedEvent` | 換班被拒絕/駁回 | Attendance | Notification | 通知申請人 |

### 10.4 值班相關事件

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 說明 |
|:---|:---|:---|:---|:---|
| `DutyScheduledEvent` | 值班排定 | Attendance | Notification | 通知值班員工 |
| `DutyActivatedEvent` | 待命被啟動 | Attendance | Notification, Payroll | ON_CALL 轉為實際值班 |
| `DutyCompletedEvent` | 值班結束 | Attendance | Payroll | 觸發津貼計算 |

### 10.5 合規相關事件

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 說明 |
|:---|:---|:---|:---|:---|
| `ComplianceViolationDetectedEvent` | 排班違反勞基法 | Attendance | Notification, Reporting | 通知 HR 並記錄 |
| `ShiftChangeRestViolationEvent` | 輪班間隔不足 | Attendance | Notification | 勞基法第 34 條違規 |
| `ConsecutiveWorkExceededEvent` | 連續工作超限 | Attendance | Notification | 勞基法第 36 條違規 |

---

## 11. 相關 API 端點對照

### 11.1 班別管理 API

| 操作 | 方法 | 端點 | Controller | Service |
|:---|:---|:---|:---|:---|
| 建立班別 | POST | `/api/v1/shifts` | `HR03ShiftCmdController` | `CreateShiftServiceImpl` |
| 查詢班別列表 | GET | `/api/v1/shifts` | `HR03ShiftQryController` | `GetShiftListServiceImpl` |
| 更新班別 | PUT | `/api/v1/shifts/{shiftId}` | `HR03ShiftCmdController` | `UpdateShiftServiceImpl` |
| 停用班別 | PUT | `/api/v1/shifts/{shiftId}/deactivate` | `HR03ShiftCmdController` | `DeactivateShiftServiceImpl` |

### 11.2 排班管理 API（待實作）

| 操作 | 方法 | 端點 | Controller | Service |
|:---|:---|:---|:---|:---|
| 建立排班（手動） | POST | `/api/v1/schedules` | `HR03ScheduleCmdController` | `CreateScheduleServiceImpl` |
| 批次排班 | POST | `/api/v1/schedules/batch` | `HR03ScheduleCmdController` | `BatchCreateScheduleServiceImpl` |
| 自動排班（輪班） | POST | `/api/v1/schedules/generate` | `HR03ScheduleCmdController` | `GenerateScheduleServiceImpl` |
| 查詢員工排班 | GET | `/api/v1/schedules` | `HR03ScheduleQryController` | `GetScheduleListServiceImpl` |
| 變更排班班別 | PUT | `/api/v1/schedules/{scheduleId}/shift` | `HR03ScheduleCmdController` | `ChangeScheduleShiftServiceImpl` |
| 發佈排班 | PUT | `/api/v1/schedules/publish` | `HR03ScheduleCmdController` | `PublishScheduleServiceImpl` |
| 鎖定排班 | PUT | `/api/v1/schedules/lock` | `HR03ScheduleCmdController` | `LockScheduleServiceImpl` |

### 11.3 輪班模式管理 API（待實作）

| 操作 | 方法 | 端點 | Controller | Service |
|:---|:---|:---|:---|:---|
| 建立輪班模式 | POST | `/api/v1/rotation-patterns` | `HR03RotationCmdController` | `CreateRotationPatternServiceImpl` |
| 查詢輪班模式 | GET | `/api/v1/rotation-patterns` | `HR03RotationQryController` | `GetRotationPatternListServiceImpl` |
| 更新輪班模式 | PUT | `/api/v1/rotation-patterns/{patternId}` | `HR03RotationCmdController` | `UpdateRotationPatternServiceImpl` |
| 停用輪班模式 | PUT | `/api/v1/rotation-patterns/{patternId}/deactivate` | `HR03RotationCmdController` | `DeactivateRotationPatternServiceImpl` |

### 11.4 換班管理 API（待實作）

| 操作 | 方法 | 端點 | Controller | Service |
|:---|:---|:---|:---|:---|
| 申請換班 | POST | `/api/v1/shift-swaps` | `HR03SwapCmdController` | `CreateShiftSwapServiceImpl` |
| 對方回應 | PUT | `/api/v1/shift-swaps/{swapId}/respond` | `HR03SwapCmdController` | `RespondShiftSwapServiceImpl` |
| 主管審核 | PUT | `/api/v1/shift-swaps/{swapId}/approve` | `HR03SwapCmdController` | `ApproveShiftSwapServiceImpl` |
| 取消換班 | PUT | `/api/v1/shift-swaps/{swapId}/cancel` | `HR03SwapCmdController` | `CancelShiftSwapServiceImpl` |
| 查詢換班申請 | GET | `/api/v1/shift-swaps` | `HR03SwapQryController` | `GetShiftSwapListServiceImpl` |

### 11.5 值班管理 API（待實作）

| 操作 | 方法 | 端點 | Controller | Service |
|:---|:---|:---|:---|:---|
| 建立值班排程 | POST | `/api/v1/duties` | `HR03DutyCmdController` | `CreateDutyScheduleServiceImpl` |
| 查詢值班排程 | GET | `/api/v1/duties` | `HR03DutyQryController` | `GetDutyScheduleListServiceImpl` |
| 啟動值班 | PUT | `/api/v1/duties/{dutyId}/activate` | `HR03DutyCmdController` | `ActivateDutyServiceImpl` |
| 完成值班 | PUT | `/api/v1/duties/{dutyId}/complete` | `HR03DutyCmdController` | `CompleteDutyServiceImpl` |
| 計算值班津貼 | GET | `/api/v1/duties/{dutyId}/allowance` | `HR03DutyQryController` | `CalculateDutyAllowanceServiceImpl` |

---

**文件結束**
