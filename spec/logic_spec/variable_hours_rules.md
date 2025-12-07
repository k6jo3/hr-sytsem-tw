# 變形工時邏輯規格書

**版本:** 1.0  
**日期:** 2025-12-07  
**適用法規:** 勞動基準法第30條、第30-1條

---

## 1. 變形工時概述

變形工時是依據勞基法規定，在特定條件下允許雇主彈性調整工作時間分配的制度。

### 1.1 變形工時類型

| 類型 | 法條 | 週期 | 每日上限 | 每週上限 | 適用行業 |
|:---|:---|:---:|:---:|:---:|:---|
| 二週變形 | 第30條第2項 | 2週 | 10小時 | 48小時 | 一般行業 |
| 四週變形 | 第30條第3項 | 4週 | 10小時 | 48小時 | 特定行業 |
| 八週變形 | 第30-1條 | 8週 | 8小時 | 48小時 | 勞動部指定行業 |

---

## 2. 二週變形工時 (2-Week Flexible Hours)

### 2.1 法規限制
```
- 每日正常工時上限: 10小時
- 每週正常工時上限: 48小時
- 二週總工時上限: 80小時 (原84小時減4小時)
- 每7日至少應有1日例假
```

### 2.2 計算邏輯

```typescript
interface TwoWeekFlexibleHours {
  periodStart: Date;
  periodEnd: Date; // periodStart + 13 days
  weeklyHours: [number, number]; // 第一週, 第二週
  totalHours: number;
}

function validateTwoWeekSchedule(schedule: DailySchedule[]): ValidationResult {
  const DAILY_MAX = 10;
  const WEEKLY_MAX = 48;
  const PERIOD_MAX = 80;
  
  let errors: string[] = [];
  let totalHours = 0;
  
  // 驗證每日上限
  for (let day of schedule) {
    if (day.regularHours > DAILY_MAX) {
      errors.push(`${day.date}: 每日正常工時超過${DAILY_MAX}小時`);
    }
    totalHours += day.regularHours;
  }
  
  // 驗證每週上限
  const week1Hours = sum(schedule.slice(0, 7).map(d => d.regularHours));
  const week2Hours = sum(schedule.slice(7, 14).map(d => d.regularHours));
  
  if (week1Hours > WEEKLY_MAX) {
    errors.push(`第一週正常工時超過${WEEKLY_MAX}小時`);
  }
  if (week2Hours > WEEKLY_MAX) {
    errors.push(`第二週正常工時超過${WEEKLY_MAX}小時`);
  }
  
  // 驗證二週總時數
  if (totalHours > PERIOD_MAX) {
    errors.push(`二週總工時超過${PERIOD_MAX}小時`);
  }
  
  return { isValid: errors.length === 0, errors };
}
```

### 2.3 範例情境

**情境:** 員工在2週內需完成專案，採用2週變形工時

| 日期 | 週次 | 星期 | 工時 | 說明 |
|:---|:---:|:---:|:---:|:---|
| 12/02 | 1 | 一 | 10h | 最大上限 |
| 12/03 | 1 | 二 | 10h | 最大上限 |
| 12/04 | 1 | 三 | 10h | 最大上限 |
| 12/05 | 1 | 四 | 10h | 最大上限 |
| 12/06 | 1 | 五 | 8h | |
| 12/07 | 1 | 六 | 0h | 休息日 |
| 12/08 | 1 | 日 | 0h | 例假日 |
| | | **週一小計** | **48h** | ✅ 未超過48h |
| 12/09 | 2 | 一 | 8h | |
| 12/10 | 2 | 二 | 8h | |
| 12/11 | 2 | 三 | 8h | |
| 12/12 | 2 | 四 | 8h | |
| 12/13 | 2 | 五 | 0h | 補休 |
| 12/14 | 2 | 六 | 0h | 休息日 |
| 12/15 | 2 | 日 | 0h | 例假日 |
| | | **週二小計** | **32h** | ✅ 未超過48h |
| | | **二週總計** | **80h** | ✅ 未超過80h |

---

## 3. 四週變形工時 (4-Week Flexible Hours)

### 3.1 法規限制
```
- 每日正常工時上限: 10小時
- 每週正常工時上限: 48小時 (單週不硬性限制，但4週總量控制)
- 四週總工時上限: 160小時
- 每2週內應有2日例假 (可連續或分開)
```

### 3.2 計算邏輯

```typescript
function validateFourWeekSchedule(schedule: DailySchedule[]): ValidationResult {
  const DAILY_MAX = 10;
  const PERIOD_MAX = 160;
  const MIN_HOLIDAYS_PER_2WEEKS = 2;
  
  let errors: string[] = [];
  let totalHours = 0;
  
  // 驗證每日上限
  for (let day of schedule) {
    if (day.regularHours > DAILY_MAX) {
      errors.push(`${day.date}: 每日正常工時超過${DAILY_MAX}小時`);
    }
    totalHours += day.regularHours;
  }
  
  // 驗證4週總時數
  if (totalHours > PERIOD_MAX) {
    errors.push(`四週總工時超過${PERIOD_MAX}小時`);
  }
  
  // 驗證每2週例假日
  const first2WeeksHolidays = countHolidays(schedule.slice(0, 14));
  const last2WeeksHolidays = countHolidays(schedule.slice(14, 28));
  
  if (first2WeeksHolidays < MIN_HOLIDAYS_PER_2WEEKS) {
    errors.push(`前2週例假日不足${MIN_HOLIDAYS_PER_2WEEKS}日`);
  }
  if (last2WeeksHolidays < MIN_HOLIDAYS_PER_2WEEKS) {
    errors.push(`後2週例假日不足${MIN_HOLIDAYS_PER_2WEEKS}日`);
  }
  
  return { isValid: errors.length === 0, errors };
}
```

### 3.3 適用行業 (勞動部公告)
- 製造業
- 營造業
- 運輸倉儲通信業
- 餐飲業
- 其他經勞動部指定行業

---

## 4. 八週變形工時 (8-Week Flexible Hours)

### 4.1 法規限制
```
- 每日正常工時上限: 8小時 (不可超過)
- 每週正常工時上限: 48小時
- 八週總工時上限: 320小時
- 每7日應有1日例假，可於8週內調整
```

### 4.2 計算邏輯

```typescript
function validateEightWeekSchedule(schedule: DailySchedule[]): ValidationResult {
  const DAILY_MAX = 8; // 注意: 8週變形每日上限為8小時
  const WEEKLY_MAX = 48;
  const PERIOD_MAX = 320;
  
  let errors: string[] = [];
  let totalHours = 0;
  
  // 驗證每日上限 (8小時)
  for (let day of schedule) {
    if (day.regularHours > DAILY_MAX) {
      errors.push(`${day.date}: 八週變形每日正常工時不可超過${DAILY_MAX}小時`);
    }
    totalHours += day.regularHours;
  }
  
  // 驗證每週上限
  for (let week = 0; week < 8; week++) {
    const weekStart = week * 7;
    const weekHours = sum(schedule.slice(weekStart, weekStart + 7).map(d => d.regularHours));
    if (weekHours > WEEKLY_MAX) {
      errors.push(`第${week + 1}週正常工時超過${WEEKLY_MAX}小時`);
    }
  }
  
  // 驗證8週總時數
  if (totalHours > PERIOD_MAX) {
    errors.push(`八週總工時超過${PERIOD_MAX}小時`);
  }
  
  // 驗證例假日 (8週內應有8日例假)
  const totalHolidays = countHolidays(schedule);
  if (totalHolidays < 8) {
    errors.push(`八週內例假日不足8日`);
  }
  
  return { isValid: errors.length === 0, errors };
}
```

---

## 5. 加班費計算 (變形工時)

### 5.1 加班費率

| 情境 | 費率 | 說明 |
|:---|:---:|:---|
| 超過正常工時 (前2小時) | 1.34倍 | 月薪 ÷ 30 ÷ 8 × 1.34 |
| 超過正常工時 (第3-4小時) | 1.67倍 | 月薪 ÷ 30 ÷ 8 × 1.67 |
| 休息日加班 (前2小時) | 1.34倍 | |
| 休息日加班 (第3-8小時) | 1.67倍 | |
| 休息日加班 (第9-12小時) | 2.67倍 | |
| 例假日加班 | 2倍 + 補假1日 | |

### 5.2 加班費計算範例

```typescript
function calculateOvertimePay(
  monthlySalary: number,
  overtimeHours: OvertimeBreakdown
): number {
  const hourlyRate = monthlySalary / 30 / 8;
  
  let totalPay = 0;
  
  // 平日加班
  totalPay += overtimeHours.weekdayFirst2Hours * hourlyRate * 1.34;
  totalPay += overtimeHours.weekdayAfter2Hours * hourlyRate * 1.67;
  
  // 休息日加班
  totalPay += overtimeHours.restDayFirst2Hours * hourlyRate * 1.34;
  totalPay += overtimeHours.restDayHours3To8 * hourlyRate * 1.67;
  totalPay += overtimeHours.restDayHours9To12 * hourlyRate * 2.67;
  
  // 例假日加班
  totalPay += overtimeHours.holidayHours * hourlyRate * 2;
  
  return Math.round(totalPay);
}
```

---

## 6. 系統實作要點

### 6.1 資料模型

```sql
-- 變形工時設定表
CREATE TABLE flexible_hours_settings (
    setting_id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    flex_type VARCHAR(20) NOT NULL CHECK (flex_type IN ('TWO_WEEK', 'FOUR_WEEK', 'EIGHT_WEEK')),
    period_start_date DATE NOT NULL,
    effective_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 排班表 (含變形工時標記)
CREATE TABLE work_schedules (
    schedule_id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    work_date DATE NOT NULL,
    scheduled_hours DECIMAL(4,2) NOT NULL,
    flex_period_id UUID REFERENCES flexible_hours_settings(setting_id),
    day_type VARCHAR(20) CHECK (day_type IN ('WORKDAY', 'REST_DAY', 'HOLIDAY')),
    CONSTRAINT uk_schedule UNIQUE (employee_id, work_date)
);
```

### 6.2 驗證時機
1. **排班建立時:** 即時驗證是否符合變形工時規則
2. **打卡時:** 驗證實際工時是否超過當日上限
3. **週期結束時:** 驗證整個週期總工時

---

**文件結束**
