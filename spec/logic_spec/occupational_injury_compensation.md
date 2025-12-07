# 職災補償邏輯規格書

**版本:** 1.0  
**日期:** 2025-12-07  
**適用法規:** 勞動基準法第59條、職業災害勞工保護法

---

## 1. 職災補償概述

依據勞基法第59條，勞工因遭遇職業災害而致死亡、失能、傷害或疾病時，雇主應依規定予以補償。

### 1.1 補償類型

| 補償類型 | 適用情境 | 法條依據 |
|:---|:---|:---|
| 醫療補償 | 職災醫療費用 | 勞基法§59第1款 |
| 工資補償 | 醫療期間無法工作 | 勞基法§59第2款 |
| 失能補償 | 治療終止後遺留失能 | 勞基法§59第3款 |
| 死亡補償 | 勞工因職災死亡 | 勞基法§59第4款 |

---

## 2. 醫療補償

### 2.1 補償範圍
```
雇主應補償必需之醫療費用：
- 掛號費、診察費
- 藥品費、手術費
- 住院費、護理費
- 義肢、義眼、輪椅等輔具費用
- 復健治療費用
```

### 2.2 計算邏輯

```typescript
interface MedicalCompensation {
  employeeId: string;
  injuryDate: Date;
  medicalExpenses: MedicalExpense[];
  totalAmount: number;
}

interface MedicalExpense {
  expenseType: 'REGISTRATION' | 'MEDICINE' | 'SURGERY' | 'HOSPITALIZATION' | 'PROSTHESIS' | 'REHABILITATION';
  amount: number;
  receiptDate: Date;
  receiptNumber: string;
  laborInsuranceCovered: number; // 勞保已給付金額
}

function calculateMedicalCompensation(expenses: MedicalExpense[]): number {
  // 雇主補償 = 實際醫療費用 - 勞保已給付
  return expenses.reduce((total, expense) => {
    const employerPays = expense.amount - expense.laborInsuranceCovered;
    return total + Math.max(0, employerPays);
  }, 0);
}
```

---

## 3. 工資補償 (原領工資)

### 3.1 法規規定
```
勞工在醫療中不能工作時，雇主應按其「原領工資」數額予以補償。
- 補償期限: 最長2年
- 超過2年: 經指定醫院診斷確定為無法治癒，雇主得一次給付40個月平均工資
```

### 3.2 原領工資定義

```typescript
interface OriginalWage {
  basicSalary: number;      // 本薪
  fixedAllowances: number;  // 固定津貼 (伙食津貼、交通津貼等)
  regularBonus: number;     // 經常性給與 (業績獎金月平均)
}

/**
 * 計算原領工資
 * 原領工資 = 事故發生前最近一個月正常工作時間所得工資
 * 如該月工資不正常，取前6個月平均
 */
function calculateOriginalWage(
  lastMonthWage: number,
  last6MonthsWages: number[]
): number {
  // 判斷是否為正常月份 (如有請假、停工等情況則不正常)
  const average6Months = last6MonthsWages.reduce((a, b) => a + b, 0) / 6;
  
  // 如果上月工資與6個月平均差異超過20%，視為不正常月份
  if (Math.abs(lastMonthWage - average6Months) / average6Months > 0.2) {
    return Math.round(average6Months);
  }
  
  return lastMonthWage;
}
```

### 3.3 補償計算範例

**情境:** 員工月薪60,000元，因職災住院3個月

| 月份 | 原領工資 | 勞保給付 (70%) | 雇主補償 |
|:---:|:---:|:---:|:---:|
| 1 | 60,000 | 42,000 | 18,000 |
| 2 | 60,000 | 42,000 | 18,000 |
| 3 | 60,000 | 42,000 | 18,000 |
| **合計** | | | **54,000** |

```typescript
function calculateWageCompensation(
  originalWage: number,
  medicalDays: number,
  laborInsurancePayment: number
): WageCompensation {
  // 日薪 = 原領工資 / 30
  const dailyWage = originalWage / 30;
  
  // 應補償金額 = 日薪 × 醫療天數
  const totalCompensation = dailyWage * medicalDays;
  
  // 扣除勞保已給付
  const employerPays = totalCompensation - laborInsurancePayment;
  
  return {
    originalWage,
    medicalDays,
    totalCompensation: Math.round(totalCompensation),
    laborInsuranceCovered: laborInsurancePayment,
    employerCompensation: Math.max(0, Math.round(employerPays))
  };
}
```

---

## 4. 失能補償

### 4.1 失能等級與給付

| 失能等級 | 給付標準 | 計算方式 |
|:---|:---|:---|
| 第1級 | 1,800日 | 平均工資 × 1,800 |
| 第2級 | 1,500日 | 平均工資 × 1,500 |
| 第3級 | 1,260日 | 平均工資 × 1,260 |
| 第4級 | 1,110日 | 平均工資 × 1,110 |
| 第5級 | 960日 | 平均工資 × 960 |
| 第6級 | 810日 | 平均工資 × 810 |
| 第7級 | 660日 | 平均工資 × 660 |
| 第8級 | 540日 | 平均工資 × 540 |
| 第9級 | 420日 | 平均工資 × 420 |
| 第10級 | 330日 | 平均工資 × 330 |
| 第11級 | 240日 | 平均工資 × 240 |
| 第12級 | 150日 | 平均工資 × 150 |
| 第13級 | 90日 | 平均工資 × 90 |
| 第14級 | 60日 | 平均工資 × 60 |
| 第15級 | 45日 | 平均工資 × 45 |

### 4.2 計算邏輯

```typescript
const DISABILITY_DAYS: Record<number, number> = {
  1: 1800, 2: 1500, 3: 1260, 4: 1110, 5: 960,
  6: 810, 7: 660, 8: 540, 9: 420, 10: 330,
  11: 240, 12: 150, 13: 90, 14: 60, 15: 45
};

function calculateDisabilityCompensation(
  averageDailyWage: number,
  disabilityLevel: number,
  laborInsurancePayment: number
): DisabilityCompensation {
  const days = DISABILITY_DAYS[disabilityLevel];
  if (!days) {
    throw new Error(`無效的失能等級: ${disabilityLevel}`);
  }
  
  const totalCompensation = averageDailyWage * days;
  const employerPays = totalCompensation - laborInsurancePayment;
  
  return {
    disabilityLevel,
    compensationDays: days,
    totalCompensation: Math.round(totalCompensation),
    laborInsuranceCovered: laborInsurancePayment,
    employerCompensation: Math.max(0, Math.round(employerPays))
  };
}
```

---

## 5. 死亡補償

### 5.1 法規規定
```
勞工遭遇職業災害死亡，雇主應給付：
1. 喪葬費: 5個月平均工資
2. 死亡補償: 40個月平均工資
總計: 45個月平均工資
```

### 5.2 計算邏輯

```typescript
function calculateDeathCompensation(
  averageMonthlyWage: number,
  laborInsurancePayment: number
): DeathCompensation {
  const funeralExpense = averageMonthlyWage * 5;
  const deathBenefit = averageMonthlyWage * 40;
  const totalCompensation = funeralExpense + deathBenefit;
  
  const employerPays = totalCompensation - laborInsurancePayment;
  
  return {
    funeralExpense: Math.round(funeralExpense),
    deathBenefit: Math.round(deathBenefit),
    totalCompensation: Math.round(totalCompensation),
    laborInsuranceCovered: laborInsurancePayment,
    employerCompensation: Math.max(0, Math.round(employerPays))
  };
}
```

---

## 6. 系統實作

### 6.1 資料模型

```sql
-- 職災記錄表
CREATE TABLE occupational_injuries (
    injury_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    injury_date DATE NOT NULL,
    injury_type VARCHAR(50) NOT NULL 
        CHECK (injury_type IN ('INJURY', 'DISEASE', 'DISABILITY', 'DEATH')),
    injury_description TEXT,
    is_recognized BOOLEAN DEFAULT FALSE,
    recognized_date DATE,
    medical_end_date DATE,
    disability_level INTEGER CHECK (disability_level BETWEEN 1 AND 15),
    status VARCHAR(20) DEFAULT 'PENDING' 
        CHECK (status IN ('PENDING', 'RECOGNIZED', 'REJECTED', 'CLOSED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 職災補償記錄表
CREATE TABLE injury_compensations (
    compensation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    injury_id UUID NOT NULL REFERENCES occupational_injuries(injury_id),
    compensation_type VARCHAR(30) NOT NULL 
        CHECK (compensation_type IN ('MEDICAL', 'WAGE', 'DISABILITY', 'DEATH')),
    period_start DATE,
    period_end DATE,
    original_wage DECIMAL(12,2),
    total_amount DECIMAL(12,2) NOT NULL,
    labor_insurance_covered DECIMAL(12,2) DEFAULT 0,
    employer_compensation DECIMAL(12,2) NOT NULL,
    paid_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

**文件結束**
