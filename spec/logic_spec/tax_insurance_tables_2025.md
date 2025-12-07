# 2025年度稅務保險級距表

**版本:** 1.0  
**日期:** 2025-12-07  
**適用年度:** 2025年 (民國114年)

---

## 1. 勞工保險費率

### 1.1 2025年勞保費率

| 項目 | 費率 | 說明 |
|:---|:---:|:---|
| 普通事故保險 | 10.5% | |
| 就業保險 | 1% | |
| **合計** | **11.5%** | |

### 1.2 負擔比例

| 負擔方 | 比例 |
|:---|:---:|
| 雇主 | 70% |
| 勞工 | 20% |
| 政府 | 10% |

### 1.3 勞保投保薪資級距表 (2025年)

```typescript
const LABOR_INSURANCE_BRACKETS_2025 = [
  { level: 1,  minSalary: 0,      maxSalary: 27470,  insuredSalary: 27470 },
  { level: 2,  minSalary: 27471,  maxSalary: 28800,  insuredSalary: 28800 },
  { level: 3,  minSalary: 28801,  maxSalary: 30300,  insuredSalary: 30300 },
  { level: 4,  minSalary: 30301,  maxSalary: 31800,  insuredSalary: 31800 },
  { level: 5,  minSalary: 31801,  maxSalary: 33300,  insuredSalary: 33300 },
  { level: 6,  minSalary: 33301,  maxSalary: 34800,  insuredSalary: 34800 },
  { level: 7,  minSalary: 34801,  maxSalary: 36300,  insuredSalary: 36300 },
  { level: 8,  minSalary: 36301,  maxSalary: 38200,  insuredSalary: 38200 },
  { level: 9,  minSalary: 38201,  maxSalary: 40100,  insuredSalary: 40100 },
  { level: 10, minSalary: 40101,  maxSalary: 42000,  insuredSalary: 42000 },
  { level: 11, minSalary: 42001,  maxSalary: 43900,  insuredSalary: 43900 },
  { level: 12, minSalary: 43901,  maxSalary: 45800,  insuredSalary: 45800 },
  { level: 13, minSalary: 45801,  maxSalary: 48200,  insuredSalary: 48200 },
  { level: 14, minSalary: 48201,  maxSalary: 50600,  insuredSalary: 50600 },
  { level: 15, minSalary: 50601,  maxSalary: 53000,  insuredSalary: 53000 },
  { level: 16, minSalary: 53001,  maxSalary: 55400,  insuredSalary: 55400 },
  { level: 17, minSalary: 55401,  maxSalary: 57800,  insuredSalary: 57800 },
  { level: 18, minSalary: 57801,  maxSalary: 60800,  insuredSalary: 60800 },
  { level: 19, minSalary: 60801,  maxSalary: 63800,  insuredSalary: 63800 },
  { level: 20, minSalary: 63801,  maxSalary: 66800,  insuredSalary: 66800 },
  { level: 21, minSalary: 66801,  maxSalary: 69800,  insuredSalary: 69800 },
  { level: 22, minSalary: 69801,  maxSalary: 72800,  insuredSalary: 72800 },
  { level: 23, minSalary: 72801,  maxSalary: 76500,  insuredSalary: 76500 },
  { level: 24, minSalary: 76501,  maxSalary: 80200,  insuredSalary: 80200 },
  { level: 25, minSalary: 80201,  maxSalary: 83900,  insuredSalary: 83900 },
  { level: 26, minSalary: 83901,  maxSalary: 87600,  insuredSalary: 87600 },
  { level: 27, minSalary: 87601,  maxSalary: Infinity, insuredSalary: 87600 }, // 上限
];
```

### 1.4 勞保費計算

```typescript
function calculateLaborInsurance(monthlySalary: number): LaborInsuranceFee {
  const bracket = LABOR_INSURANCE_BRACKETS_2025.find(
    b => monthlySalary >= b.minSalary && monthlySalary <= b.maxSalary
  );
  
  const insuredSalary = bracket?.insuredSalary || 87600;
  const totalFee = Math.round(insuredSalary * 0.115); // 11.5%
  
  return {
    insuredSalary,
    totalFee,
    employerFee: Math.round(totalFee * 0.70),  // 70%
    employeeFee: Math.round(totalFee * 0.20),  // 20%
    governmentFee: Math.round(totalFee * 0.10) // 10%
  };
}
```

---

## 2. 健康保險費率

### 2.1 2025年健保費率

| 項目 | 費率 |
|:---|:---:|
| 一般保險費 | 5.17% |
| 平均眷屬人數 | 0.57人 |

### 2.2 負擔比例 (一般受僱者)

| 負擔方 | 比例 |
|:---|:---:|
| 雇主 | 60% |
| 勞工 | 30% |
| 政府 | 10% |

### 2.3 健保投保薪資級距表 (2025年)

```typescript
const HEALTH_INSURANCE_BRACKETS_2025 = [
  { level: 1,  minSalary: 0,      maxSalary: 27470,  insuredSalary: 27470 },
  { level: 2,  minSalary: 27471,  maxSalary: 28800,  insuredSalary: 28800 },
  { level: 3,  minSalary: 28801,  maxSalary: 30300,  insuredSalary: 30300 },
  // ... (同勞保級距，但上限不同)
  { level: 50, minSalary: 175601, maxSalary: 182000, insuredSalary: 182000 },
  { level: 51, minSalary: 182001, maxSalary: Infinity, insuredSalary: 219500 }, // 上限
];
```

### 2.4 健保費計算

```typescript
function calculateHealthInsurance(
  monthlySalary: number, 
  dependents: number
): HealthInsuranceFee {
  const bracket = HEALTH_INSURANCE_BRACKETS_2025.find(
    b => monthlySalary >= b.minSalary && monthlySalary <= b.maxSalary
  );
  
  const insuredSalary = bracket?.insuredSalary || 219500;
  const rate = 0.0517; // 5.17%
  
  // 本人保費
  const selfFee = insuredSalary * rate;
  
  // 眷屬保費 (最多3人)
  const actualDependents = Math.min(dependents, 3);
  const dependentFee = selfFee * actualDependents;
  
  // 總保費
  const totalFee = selfFee + dependentFee;
  
  return {
    insuredSalary,
    dependents: actualDependents,
    totalFee: Math.round(totalFee),
    employerFee: Math.round(totalFee * 0.60),  // 60%
    employeeFee: Math.round(totalFee * 0.30),  // 30%
    governmentFee: Math.round(totalFee * 0.10) // 10%
  };
}
```

---

## 3. 二代健保補充保費

### 3.1 補充保費費率

| 項目 | 費率 |
|:---|:---:|
| 補充保費費率 | 2.11% |

### 3.2 應計收補充保費項目

| 項目 | 門檻 | 扣費對象 |
|:---|:---|:---|
| 獎金 | 單次超過4個月投保金額 | 雇主代扣 |
| 兼職所得 | 單次≥基本工資 | 雇主代扣 |
| 執行業務收入 | 單次≥基本工資 | 扣繳義務人代扣 |
| 股利所得 | 單次≥基本工資 | 扣繳義務人代扣 |
| 利息所得 | 單次≥基本工資 | 扣繳義務人代扣 |
| 租金收入 | 單次≥基本工資 | 扣繳義務人代扣 |

### 3.3 補充保費計算

```typescript
const BASIC_WAGE_2025 = 27470; // 2025年基本工資
const SUPPLEMENTARY_RATE = 0.0211; // 2.11%

function calculateSupplementaryPremium(
  bonusAmount: number,
  monthlyInsuredSalary: number
): SupplementaryPremium {
  // 計算門檻 (4個月投保金額)
  const threshold = monthlyInsuredSalary * 4;
  
  // 超過門檻的部分需繳補充保費
  if (bonusAmount > threshold) {
    const taxableAmount = bonusAmount - threshold;
    const premium = Math.round(taxableAmount * SUPPLEMENTARY_RATE);
    
    return {
      bonusAmount,
      threshold,
      taxableAmount,
      premium,
      isApplicable: true
    };
  }
  
  return {
    bonusAmount,
    threshold,
    taxableAmount: 0,
    premium: 0,
    isApplicable: false
  };
}
```

---

## 4. 勞工退休金

### 4.1 2025年勞退提繳

| 項目 | 費率 |
|:---|:---:|
| 雇主強制提繳 | 6% (最低) |
| 勞工自願提繳 | 0-6% |

### 4.2 勞退月提繳工資分級表

```typescript
const PENSION_BRACKETS_2025 = [
  { level: 1,  minSalary: 0,      maxSalary: 1500,   contributionWage: 1500 },
  { level: 2,  minSalary: 1501,   maxSalary: 3000,   contributionWage: 3000 },
  // ... 共62級
  { level: 61, minSalary: 150001, maxSalary: 176000, contributionWage: 176000 },
  { level: 62, minSalary: 176001, maxSalary: Infinity, contributionWage: 176000 }, // 上限
];

function calculatePensionContribution(
  monthlySalary: number,
  employeeVoluntaryRate: number = 0
): PensionContribution {
  const bracket = PENSION_BRACKETS_2025.find(
    b => monthlySalary >= b.minSalary && monthlySalary <= b.maxSalary
  );
  
  const contributionWage = bracket?.contributionWage || 176000;
  
  return {
    contributionWage,
    employerContribution: Math.round(contributionWage * 0.06),
    employeeContribution: Math.round(contributionWage * employeeVoluntaryRate),
    totalContribution: Math.round(contributionWage * (0.06 + employeeVoluntaryRate))
  };
}
```

---

## 5. 所得稅扣繳

### 5.1 2025年薪資所得扣繳稅額表

```typescript
const TAX_BRACKETS_2025 = [
  { minIncome: 0,       maxIncome: 590000,   rate: 0.05, deduction: 0 },
  { minIncome: 590001,  maxIncome: 1330000,  rate: 0.12, deduction: 41300 },
  { minIncome: 1330001, maxIncome: 2660000,  rate: 0.20, deduction: 147700 },
  { minIncome: 2660001, maxIncome: 4980000,  rate: 0.30, deduction: 413700 },
  { minIncome: 4980001, maxIncome: Infinity, rate: 0.40, deduction: 911700 },
];

// 每月扣繳門檻
const MONTHLY_TAX_THRESHOLD = 88501; // 月薪超過此金額需扣繳

function calculateMonthlyWithholding(
  monthlySalary: number,
  dependents: number = 0
): MonthlyWithholding {
  // 免稅額 (本人 + 眷屬)
  const personalExemption = 97000; // 年
  const dependentExemption = 97000 * dependents; // 年
  const totalExemption = (personalExemption + dependentExemption) / 12; // 月
  
  // 標準扣除額
  const standardDeduction = 131000 / 12; // 月
  
  // 薪資所得特別扣除額
  const salaryDeduction = 218000 / 12; // 月
  
  // 課稅所得
  const taxableIncome = Math.max(0, 
    monthlySalary - totalExemption - standardDeduction - salaryDeduction
  );
  
  // 計算應扣繳稅額 (簡化版，實際應參照國稅局公布表格)
  let withholdingTax = 0;
  if (monthlySalary > MONTHLY_TAX_THRESHOLD) {
    withholdingTax = Math.round(taxableIncome * 0.05);
  }
  
  return {
    monthlySalary,
    exemptions: Math.round(totalExemption),
    taxableIncome: Math.round(taxableIncome),
    withholdingTax
  };
}
```

---

## 6. 系統初始化資料

### 6.1 DDL & 初始資料

```sql
-- 保險費率設定表
CREATE TABLE insurance_rate_settings (
    rate_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    effective_year INTEGER NOT NULL,
    insurance_type VARCHAR(30) NOT NULL,
    rate DECIMAL(6,4) NOT NULL,
    employer_ratio DECIMAL(4,2) NOT NULL,
    employee_ratio DECIMAL(4,2) NOT NULL,
    government_ratio DECIMAL(4,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_rate UNIQUE (effective_year, insurance_type)
);

-- 2025年初始資料
INSERT INTO insurance_rate_settings VALUES
(gen_random_uuid(), 2025, 'LABOR_INSURANCE', 0.1150, 0.70, 0.20, 0.10, NOW()),
(gen_random_uuid(), 2025, 'HEALTH_INSURANCE', 0.0517, 0.60, 0.30, 0.10, NOW()),
(gen_random_uuid(), 2025, 'EMPLOYMENT_INSURANCE', 0.0100, 0.70, 0.20, 0.10, NOW()),
(gen_random_uuid(), 2025, 'PENSION', 0.0600, 1.00, 0.00, 0.00, NOW()),
(gen_random_uuid(), 2025, 'SUPPLEMENTARY', 0.0211, 0.00, 1.00, 0.00, NOW());
```

---

**文件結束**
