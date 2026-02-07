# 薪資管理服務業務合約 (Payroll Service Contract)

> **服務代碼:** HR04
> **版本:** 2.0
> **建立日期:** 2026-02-06
> **維護者:** SA Team
> **變更說明:** 移除不存在的 is_deleted 欄位，新增 API 端點、Command 操作、Domain Events，採用雙層結構設計

---

## 📋 目錄

1. [合約概述](#合約概述)
2. [查詢操作合約 (Query Contracts)](#查詢操作合約-query-contracts)
   - [2.1 薪資結構查詢](#21-薪資結構查詢)
   - [2.2 薪資單查詢](#22-薪資單查詢)
   - [2.3 獎金查詢](#23-獎金查詢)
   - [2.4 扣款項目查詢](#24-扣款項目查詢)
   - [2.5 加班費計算查詢](#25-加班費計算查詢)
   - [2.6 薪資批次查詢](#26-薪資批次查詢)
3. [命令操作合約 (Command Contracts)](#命令操作合約-command-contracts)
   - [3.1 薪資結構操作](#31-薪資結構操作)
   - [3.2 薪資批次操作](#32-薪資批次操作)
   - [3.3 薪資單操作](#33-薪資單操作)
4. [Domain Events 定義](#domain-events-定義)
5. [補充說明](#補充說明)

---

## 合約概述

### 服務定位
薪資管理服務負責員工薪資結構設定、薪資計算、薪資發放等核心功能。本服務處理**高度敏感的財務資料**，需嚴格控管存取權限並記錄完整稽核軌跡。

### 資料軟刪除策略

**⚠️ 重要：本服務不使用 `is_deleted` 欄位進行軟刪除**

- **薪資結構、項目定義**: 使用 `is_active` 欄位，FALSE 代表已停用
- **薪資批次**: 使用 `status` 欄位，CANCELLED 代表已取消
- **薪資單**: 不進行軟刪除，保留所有歷史記錄（status 為 DRAFT/FINALIZED/SENT）

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊權限 |
|:---|:---|:---|
| `HR_PAYROLL` | 全公司薪資資料 | 可建立/修改薪資結構、執行薪資計算、核准發放 |
| `HR` | 基本薪資結構（唯讀） | 不可查詢薪資明細、不可執行薪資計算 |
| `MANAGER` | 無 | 不可查詢下屬薪資（勞基法規定） |
| `EMPLOYEE` | 僅自己 | 只能查詢自己的薪資資料 |

### 獎金類型代碼

| 代碼 | 說明 | 發放時機 |
|:---|:---|:---|
| `PERFORMANCE` | 績效獎金 | 季度/年度 |
| `YEAR_END` | 年終獎金 | 農曆年前 |
| `PROJECT` | 專案獎金 | 專案完成 |
| `REFERRAL` | 推薦獎金 | 成功錄用 |
| `ATTENDANCE` | 全勤獎金 | 每月 |
| `OTHER` | 其他獎金 | 不定期 |

### 薪資計算批次狀態流程

```
DRAFT (草稿)
  ↓ [執行計算]
CALCULATING (計算中)
  ↓ [計算完成]
COMPLETED (已完成)
  ↓ [送審]
SUBMITTED (送審中)
  ↓ [核准 / 駁回]
APPROVED (已核准) → [標記已發放] → PAID (已發放)
  ↓ [取消]
CANCELLED (已取消)
```

---

## 查詢操作合約 (Query Contracts)

### 2.1 薪資結構查詢

#### 2.1.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_S001 | 查詢員工薪資結構 | HR_PAYROLL | `GET /api/v1/salary-structures` | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_active = TRUE` |
| PAY_QRY_S002 | 查詢有效薪資結構 | HR_PAYROLL | `GET /api/v1/salary-structures` | `{"isActive":true}` | `is_active = TRUE` |
| PAY_QRY_S003 | 查詢月薪制結構 | HR_PAYROLL | `GET /api/v1/salary-structures` | `{"payrollSystem":"MONTHLY"}` | `payroll_system = 'MONTHLY'`, `is_active = TRUE` |
| PAY_QRY_S004 | 查詢時薪制結構 | HR_PAYROLL | `GET /api/v1/salary-structures` | `{"payrollSystem":"HOURLY"}` | `payroll_system = 'HOURLY'`, `is_active = TRUE` |
| PAY_QRY_S005 | 組合條件查詢 | HR_PAYROLL | `GET /api/v1/salary-structures` | `{"employeeId":"E001","isActive":true}` | `employee_id = 'E001'`, `is_active = TRUE` |
| PAY_QRY_S006 | 查詢停用的薪資結構 | HR_PAYROLL | `GET /api/v1/salary-structures/inactive` | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_active = FALSE` |
| PAY_QRY_S007 | 查詢特定生效日期結構 | HR_PAYROLL | `GET /api/v1/salary-structures` | `{"employeeId":"E001","effectiveDate":"2025-01-01"}` | `employee_id = 'E001'`, `effective_date <= '2025-01-01'`, `(end_date IS NULL OR end_date > '2025-01-01')` |

#### 2.1.2 詳細業務描述

**場景 PAY_QRY_S001: 查詢員工薪資結構**

- **業務規則:**
  1. HR_PAYROLL 可查詢任意員工的薪資結構
  2. 僅返回啟用狀態的薪資結構（is_active = TRUE）
  3. 包含底薪、加給、津貼等所有薪資項目

- **權限檢查:**
  - 需要 `payroll:structure:view` 權限
  - HR_PAYROLL 角色不受部門限制

- **軟刪除策略:**
  - **不使用 is_deleted 欄位**
  - 使用 `is_active = TRUE` 過濾啟用的薪資結構
  - 停用的薪資結構不會被查詢到（除非使用 inactive API）

- **測試範例:**
```java
@Test
@DisplayName("PAY_QRY_S001: 查詢員工薪資結構")
void getEmployeeSalaryStructure_AsPayroll_ShouldFilterByEmployeeAndActive() throws Exception {
    String contractSpec = loadContractSpec("payroll");

    GetSalaryStructuresRequest request = GetSalaryStructuresRequest.builder()
        .employeeId("E001")
        .build();

    verifyApiContract("/api/v1/salary-structures", request, contractSpec, "PAY_QRY_S001");
}
```

**場景 PAY_QRY_S007: 查詢特定生效日期結構**

- **業務規則:**
  1. 查詢在特定日期有效的薪資結構
  2. 有效條件: effective_date <= 查詢日期 AND (end_date IS NULL OR end_date > 查詢日期)
  3. 用於計算歷史薪資或薪資調整回溯

- **查詢邏輯:**
  - `effective_date <= '2025-01-01'` - 生效日期在查詢日期之前
  - `(end_date IS NULL OR end_date > '2025-01-01')` - 尚未結束或結束日期在查詢日期之後

---

### 2.2 薪資單查詢

#### 2.2.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_P001 | 查詢批次下薪資單 | HR_PAYROLL | `GET /api/v1/payslips` | `{"runId":"RUN001"}` | `payroll_run_id = 'RUN001'` |
| PAY_QRY_P002 | 員工查詢自己薪資單 | EMPLOYEE | `GET /api/v1/payslips/my` | `{}` | `employee_id = '{currentUserId}'` |
| PAY_QRY_P003 | HR 查詢特定員工薪資單 | HR_PAYROLL | `GET /api/v1/payslips` | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| PAY_QRY_P004 | 組合條件查詢薪資單 | HR_PAYROLL | `GET /api/v1/payslips` | `{"runId":"RUN001","employeeId":"E001"}` | `payroll_run_id = 'RUN001'`, `employee_id = 'E001'` |
| PAY_QRY_P005 | 員工查詢特定月份薪資單 | EMPLOYEE | `GET /api/v1/payslips/my` | `{"yearMonth":"2025-01"}` | `employee_id = '{currentUserId}'`, `pay_period_start >= '2025-01-01'`, `pay_period_end <= '2025-01-31'` |
| PAY_QRY_P006 | 員工查詢歷史薪資單 | EMPLOYEE | `GET /api/v1/payslips/my/history` | `{}` | `employee_id = '{currentUserId}'`, `status = 'SENT'` |
| PAY_QRY_P007 | 依發放日期查詢 | HR_PAYROLL | `GET /api/v1/payslips` | `{"payDate":"2025-01-05"}` | `pay_date = '2025-01-05'` |
| PAY_QRY_P008 | 查詢草稿狀態薪資單 | HR_PAYROLL | `GET /api/v1/payslips` | `{"status":"DRAFT"}` | `status = 'DRAFT'` |
| PAY_QRY_P009 | 查詢已寄送薪資單 | HR_PAYROLL | `GET /api/v1/payslips` | `{"status":"SENT"}` | `status = 'SENT'` |

#### 2.2.2 詳細業務描述

**場景 PAY_QRY_P002: 員工查詢自己薪資單**

- **業務規則:**
  1. 員工只能查詢自己的薪資單
  2. 自動套用 `employee_id = {currentUserId}` 過濾
  3. 返回所有狀態的薪資單（DRAFT, FINALIZED, SENT）

- **權限檢查:**
  - 不需要特殊權限（所有員工可用）
  - 自動限制為當前登入使用者

- **測試範例:**
```java
@Test
@DisplayName("PAY_QRY_P002: 員工查詢自己薪資單")
@WithMockUser(username = "E001", roles = "EMPLOYEE")
void getMyPayslips_AsEmployee_ShouldFilterByCurrentUser() throws Exception {
    String contractSpec = loadContractSpec("payroll");

    GetMyPayslipsRequest request = new GetMyPayslipsRequest();

    verifyApiContract("/api/v1/payslips/my", request, contractSpec, "PAY_QRY_P002");
}
```

**場景 PAY_QRY_P006: 員工查詢歷史薪資單**

- **業務規則:**
  1. 只返回已寄送的薪資單（status = 'SENT'）
  2. 草稿狀態（DRAFT）和定稿狀態（FINALIZED）不會出現在歷史記錄中
  3. 按發放日期倒序排列

- **查詢邏輯:**
  - `employee_id = '{currentUserId}'` - 僅查詢自己
  - `status = 'SENT'` - 只查詢已寄送的薪資單

---

### 2.3 獎金查詢

#### 2.3.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_B001 | 查詢員工獎金 | HR_PAYROLL | `GET /api/v1/bonuses` | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| PAY_QRY_B002 | 依獎金類型查詢 | HR_PAYROLL | `GET /api/v1/bonuses` | `{"bonusType":"PERFORMANCE"}` | `bonus_type = 'PERFORMANCE'` |
| PAY_QRY_B003 | 查詢年終獎金 | HR_PAYROLL | `GET /api/v1/bonuses` | `{"bonusType":"YEAR_END","year":"2025"}` | `bonus_type = 'YEAR_END'`, `EXTRACT(YEAR FROM pay_date) = 2025` |
| PAY_QRY_B004 | 依發放狀態查詢 | HR_PAYROLL | `GET /api/v1/bonuses` | `{"status":"PAID"}` | `status = 'PAID'` |
| PAY_QRY_B005 | 員工查詢自己獎金 | EMPLOYEE | `GET /api/v1/bonuses/my` | `{}` | `employee_id = '{currentUserId}'` |
| PAY_QRY_B006 | 查詢待發放獎金 | HR_PAYROLL | `GET /api/v1/bonuses` | `{"status":"APPROVED"}` | `status = 'APPROVED'` |

#### 2.3.2 詳細業務描述

**場景 PAY_QRY_B003: 查詢年終獎金**

- **業務規則:**
  1. 查詢特定年度的年終獎金
  2. 年終獎金通常在農曆年前發放
  3. 包含已發放和待發放的獎金

- **查詢邏輯:**
  - `bonus_type = 'YEAR_END'` - 獎金類型為年終獎金
  - `EXTRACT(YEAR FROM pay_date) = 2025` - 發放年度為 2025

---

### 2.4 扣款項目查詢

#### 2.4.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_D001 | 查詢員工扣款項目 | HR_PAYROLL | `GET /api/v1/deductions` | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| PAY_QRY_D002 | 依扣款類型查詢 | HR_PAYROLL | `GET /api/v1/deductions` | `{"deductionType":"LOAN"}` | `deduction_type = 'LOAN'` |
| PAY_QRY_D003 | 查詢進行中的扣款 | HR_PAYROLL | `GET /api/v1/deductions` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| PAY_QRY_D004 | 查詢已結清的扣款 | HR_PAYROLL | `GET /api/v1/deductions` | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| PAY_QRY_D005 | 員工查詢自己扣款 | EMPLOYEE | `GET /api/v1/deductions/my` | `{}` | `employee_id = '{currentUserId}'` |

---

### 2.5 加班費計算查詢

#### 2.5.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_O001 | 查詢員工加班費 | HR_PAYROLL | `GET /api/v1/overtime-pay` | `{"employeeId":"E001","yearMonth":"2025-01"}` | `employee_id = 'E001'`, `year_month = '2025-01'` |
| PAY_QRY_O002 | 查詢部門加班費 | HR_PAYROLL | `GET /api/v1/overtime-pay` | `{"deptId":"D001","yearMonth":"2025-01"}` | `employee_id IN (SELECT employee_id FROM employees WHERE department_id = 'D001')`, `year_month = '2025-01'` |
| PAY_QRY_O003 | 員工查詢自己加班費 | EMPLOYEE | `GET /api/v1/overtime-pay/my` | `{"yearMonth":"2025-01"}` | `employee_id = '{currentUserId}'`, `year_month = '2025-01'` |

#### 2.5.2 詳細業務描述

**場景 PAY_QRY_O001: 查詢員工加班費**

- **業務規則:**
  1. 計算員工當月的加班費總額
  2. 依勞基法計算平日、休息日、假日加班費率
  3. 包含加班時數明細

- **加班費計算公式:**
  - 平日加班: 時薪 × 1.34 倍 × 小時數（前 2 小時）
  - 平日加班: 時薪 × 1.67 倍 × 小時數（第 3 小時起）
  - 休息日加班: 時薪 × 1.34 倍（前 2 小時）、1.67 倍（第 3-8 小時）、2.67 倍（第 9 小時起）
  - 假日加班: 時薪 × 2 倍

---

### 2.6 薪資批次查詢

#### 2.6.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PAY_QRY_R001 | HR 查詢特定組織薪資批次 | HR_PAYROLL | `GET /api/v1/payroll-runs` | `{"organizationId":"ORG001"}` | `organization_id = 'ORG001'` |
| PAY_QRY_R002 | HR 查詢特定狀態薪資批次 | HR_PAYROLL | `GET /api/v1/payroll-runs` | `{"status":"SUBMITTED"}` | `status = 'SUBMITTED'` |
| PAY_QRY_R003 | HR 查詢日期範圍內批次 | HR_PAYROLL | `GET /api/v1/payroll-runs` | `{"startDate":"2025-01-01","endDate":"2025-01-31"}` | `pay_period_start >= '2025-01-01'`, `pay_period_end <= '2025-01-31'` |
| PAY_QRY_R004 | 查詢草稿狀態批次 | HR_PAYROLL | `GET /api/v1/payroll-runs` | `{"status":"DRAFT"}` | `status = 'DRAFT'` |
| PAY_QRY_R005 | 查詢已核准批次 | HR_PAYROLL | `GET /api/v1/payroll-runs` | `{"status":"APPROVED"}` | `status = 'APPROVED'` |
| PAY_QRY_R006 | 查詢已發薪批次 | HR_PAYROLL | `GET /api/v1/payroll-runs` | `{"status":"PAID"}` | `status = 'PAID'` |
| PAY_QRY_R007 | 排除已取消的批次 | HR_PAYROLL | `GET /api/v1/payroll-runs` | `{"excludeCancelled":true}` | `status != 'CANCELLED'` |
| PAY_QRY_R008 | 查詢計算完成的批次 | HR_PAYROLL | `GET /api/v1/payroll-runs` | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |

#### 2.6.2 詳細業務描述

**場景 PAY_QRY_R002: HR 查詢特定狀態薪資批次**

- **業務規則:**
  1. 查詢處於送審狀態的薪資批次
  2. 送審狀態（SUBMITTED）代表已完成計算，等待核准
  3. 用於主管審核頁面

- **查詢邏輯:**
  - `status = 'SUBMITTED'` - 送審狀態
  - 不包含已取消（CANCELLED）的批次

**場景 PAY_QRY_R007: 排除已取消的批次**

- **業務規則:**
  1. 查詢有效的薪資批次（排除已取消）
  2. 已取消的批次不應出現在一般查詢結果中
  3. 使用 `status != 'CANCELLED'` 而非 `is_deleted = 0`

- **軟刪除策略:**
  - **不使用 is_deleted 欄位**
  - 使用 `status != 'CANCELLED'` 過濾有效批次
  - 已取消的批次仍保留在資料庫中（用於稽核）

---

## 命令操作合約 (Command Contracts)

### 3.1 薪資結構操作

#### 3.1.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| PAY_CMD_001 | 建立月薪制薪資結構 | HR_PAYROLL | `POST /api/v1/salary-structures` | `{"employeeId":"E001","payrollSystem":"MONTHLY","monthlySalary":50000}` | 員工存在檢查, 薪資項目驗證, 時薪計算（月薪÷240） | `SalaryStructureCreated` |
| PAY_CMD_002 | 建立時薪制薪資結構 | HR_PAYROLL | `POST /api/v1/salary-structures` | `{"employeeId":"E002","payrollSystem":"HOURLY","hourlyRate":200}` | 員工存在檢查, 時薪必填檢查 | `SalaryStructureCreated` |
| PAY_CMD_003 | 更新薪資結構（調薪） | HR_PAYROLL | `PUT /api/v1/salary-structures/{id}` | `{"monthlySalary":55000,"effectiveDate":"2025-02-01"}` | 舊結構結束檢查, 新結構生效檢查, 薪資不可降低檢查 | `SalaryStructureChanged` |
| PAY_CMD_004 | 停用薪資結構 | HR_PAYROLL | `PUT /api/v1/salary-structures/{id}/deactivate` | `{}` | 狀態檢查（必須為 ACTIVE） | `SalaryStructureDeactivated` |

#### 3.1.2 詳細業務描述

**場景 PAY_CMD_001: 建立月薪制薪資結構**

- **業務規則:**
  1. 檢查員工是否存在（employeeId 有效性）
  2. 月薪制必須提供月薪（monthlySalary NOT NULL）
  3. 自動計算時薪（calculated_hourly_rate = monthly_salary / 240）
  4. 驗證薪資項目（item_code 有效性、金額合理性）
  5. 新結構預設為啟用狀態（is_active = TRUE）

- **Domain Logic:**
  ```java
  SalaryStructure structure = SalaryStructure.createMonthly(
      employeeId,
      monthlySalary,
      payrollCycle,
      salaryItems,
      effectiveDate
  );

  // Domain 方法內部會：
  // 1. 驗證員工存在
  // 2. 計算時薪（用於加班費）
  // 3. 驗證薪資項目
  // 4. 發布 SalaryStructureCreated 事件
  ```

- **Domain Event:**
  - 成功建立後發布 `SalaryStructureCreated` 事件
  - Insurance Service 訂閱此事件，計算保險費用

- **測試範例:**
```java
@Test
@DisplayName("PAY_CMD_001: 建立月薪制薪資結構")
void createMonthlySalaryStructure_AsPayroll_ShouldPublishEvent() throws Exception {
    CreateSalaryStructureRequest request = CreateSalaryStructureRequest.builder()
        .employeeId("E001")
        .payrollSystem(PayrollSystem.MONTHLY)
        .monthlySalary(new BigDecimal("50000"))
        .salaryItems(List.of(
            SalaryItem.builder()
                .itemCode("JOB_ALLOWANCE")
                .itemName("職務加給")
                .itemType(ItemType.EARNING)
                .amount(new BigDecimal("5000"))
                .build()
        ))
        .effectiveDate(LocalDate.of(2025, 1, 1))
        .build();

    mockMvc.perform(post("/api/v1/salary-structures")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // 驗證 Domain Event 發布
    verify(eventPublisher).publish(argThat(event ->
        event instanceof SalaryStructureCreatedEvent
    ));
}
```

**場景 PAY_CMD_003: 更新薪資結構（調薪）**

- **業務規則:**
  1. 調薪時自動結束舊薪資結構（設定 end_date）
  2. 建立新薪資結構（effective_date = 調薪生效日）
  3. 薪資不可降低（除非特殊情況）
  4. 新結構繼承舊結構的薪資項目

- **Domain Logic:**
  ```java
  salaryStructure.adjustSalary(
      newMonthlySalary,
      newEffectiveDate
  );

  // Domain 方法內部會：
  // 1. 驗證薪資不可降低
  // 2. 設定舊結構 end_date = new_effective_date - 1 天
  // 3. 建立新結構（複製舊項目）
  // 4. 發布 SalaryStructureChanged 事件
  ```

- **Domain Event:**
  - 發布 `SalaryStructureChanged` 事件
  - Insurance Service 訂閱此事件，重新計算保險費用

---

### 3.2 薪資批次操作

#### 3.2.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| PAY_CMD_005 | 建立薪資批次 | HR_PAYROLL | `POST /api/v1/payroll-runs` | `{"organizationId":"ORG001","payPeriodStart":"2025-01-01","payPeriodEnd":"2025-01-31","payDate":"2025-02-05"}` | 期間重疊檢查, 發放日期合理性檢查 | `PayrollRunCreated` |
| PAY_CMD_006 | 執行薪資計算 | HR_PAYROLL | `POST /api/v1/payroll-runs/{id}/calculate` | `{}` | 狀態檢查（必須為 DRAFT）, Saga 流程啟動 | `PayrollRunStarted`, `PayrollRunCompleted` |
| PAY_CMD_007 | 送審薪資批次 | HR_PAYROLL | `POST /api/v1/payroll-runs/{id}/submit` | `{}` | 狀態檢查（必須為 COMPLETED）, 所有薪資單已產生檢查 | `PayrollRunSubmitted` |
| PAY_CMD_008 | 核准薪資批次 | HR_MANAGER | `POST /api/v1/payroll-runs/{id}/approve` | `{}` | 狀態檢查（必須為 SUBMITTED）, 金額合理性檢查 | `PayrollRunApproved` |
| PAY_CMD_009 | 駁回薪資批次 | HR_MANAGER | `POST /api/v1/payroll-runs/{id}/reject` | `{"reason":"數據異常"}` | 狀態檢查（必須為 SUBMITTED）, 駁回原因必填 | `PayrollRunRejected` |
| PAY_CMD_010 | 標記已發放 | HR_PAYROLL | `POST /api/v1/payroll-runs/{id}/mark-paid` | `{}` | 狀態檢查（必須為 APPROVED）, 銀行檔案已產生檢查 | `PayrollPaid` |
| PAY_CMD_011 | 取消薪資批次 | HR_PAYROLL | `POST /api/v1/payroll-runs/{id}/cancel` | `{}` | 狀態檢查（不可為 PAID） | `PayrollRunCancelled` |

#### 3.2.2 詳細業務描述

**場景 PAY_CMD_006: 執行薪資計算**

- **業務規則:**
  1. 只能對草稿狀態（DRAFT）的批次執行計算
  2. 使用 Saga 模式協調跨服務資料收集
  3. 計算過程需記錄完整稽核軌跡
  4. 計算失敗的員工需記錄錯誤原因

- **Saga 流程:**
  ```
  1. PayrollRunSaga.start()
  2. → Attendance Service: 取得差勤數據（工時、請假、遲到）
  3. → Insurance Service: 取得保險費用（勞保、健保、勞退）
  4. → 本地計算: 加班費、請假扣款
  5. → 本地計算: 所得稅、二代健保
  6. → 產生 Payslip
  7. → 更新批次狀態為 COMPLETED
  8. → 發布 PayrollRunCompleted 事件
  ```

- **Domain Logic:**
  ```java
  payrollRun.calculate(sagaOrchestrator);

  // Domain 方法內部會：
  // 1. 檢查狀態（status == DRAFT）
  // 2. 更新狀態為 CALCULATING
  // 3. 啟動 Saga 協調器
  // 4. 發布 PayrollRunStarted 事件
  // ... Saga 完成後 ...
  // 5. 更新統計數據
  // 6. 更新狀態為 COMPLETED
  // 7. 發布 PayrollRunCompleted 事件
  ```

- **Domain Events:**
  - `PayrollRunStarted`: 開始計算時發布
  - `PayrollRunCompleted`: 計算完成時發布
  - Notification Service 訂閱 Completed 事件，發送通知給 HR

**場景 PAY_CMD_008: 核准薪資批次**

- **業務規則:**
  1. 只能核准送審狀態（SUBMITTED）的批次
  2. 需檢查薪資總額合理性（與上月比較，異常值警告）
  3. 記錄核准人 ID 和核准時間
  4. 核准後即可產生銀行轉帳檔案

- **Domain Logic:**
  ```java
  payrollRun.approve(approverId);

  // Domain 方法內部會：
  // 1. 檢查狀態（status == SUBMITTED）
  // 2. 驗證薪資總額合理性
  // 3. 更新狀態為 APPROVED
  // 4. 記錄 approved_by, approved_at
  // 5. 發布 PayrollRunApproved 事件
  ```

- **Domain Event:**
  - 發布 `PayrollRunApproved` 事件
  - 觸發銀行轉帳檔案產生作業

---

### 3.3 薪資單操作

#### 3.3.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| PAY_CMD_012 | 產生薪資單 PDF | HR_PAYROLL | `POST /api/v1/payslips/{id}/generate-pdf` | `{}` | 狀態檢查（必須為 FINALIZED）, 薪資單數據完整性檢查 | `PayslipPDFGenerated` |
| PAY_CMD_013 | 寄送薪資單 Email | HR_PAYROLL | `POST /api/v1/payslips/{id}/send-email` | `{}` | PDF 已產生檢查, 員工 Email 有效性檢查 | `PayslipSent` |
| PAY_CMD_014 | 批次寄送薪資單 | HR_PAYROLL | `POST /api/v1/payroll-runs/{id}/send-all-payslips` | `{}` | 批次狀態檢查（必須為 PAID）, 所有 PDF 已產生檢查 | `PayslipSent` (多個) |

#### 3.3.2 詳細業務描述

**場景 PAY_CMD_013: 寄送薪資單 Email**

- **業務規則:**
  1. 薪資單 PDF 必須已產生（pdf_url NOT NULL）
  2. 員工 Email 必須有效
  3. Email 內容需加密或使用安全連結
  4. 記錄寄送時間（email_sent_at）

- **Domain Logic:**
  ```java
  payslip.sendEmail(employeeEmail, pdfUrl);

  // Domain 方法內部會：
  // 1. 檢查 PDF 已產生
  // 2. 驗證 Email 有效性
  // 3. 更新狀態為 SENT
  // 4. 記錄 email_sent_at
  // 5. 發布 PayslipSent 事件
  ```

- **Domain Event:**
  - 發布 `PayslipSent` 事件
  - Notification Service 訂閱此事件，實際執行 Email 寄送

---

## Domain Events 定義

### 4.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `SalaryStructureCreated` | 建立薪資結構 | Payroll | Insurance | 計算保險費用 |
| `SalaryStructureChanged` | 薪資調整 | Payroll | Insurance | 重新計算保險費用 |
| `SalaryStructureDeactivated` | 停用薪資結構 | Payroll | - | 記錄稽核 |
| `PayrollRunCreated` | 建立薪資批次 | Payroll | - | 記錄稽核 |
| `PayrollRunStarted` | 開始薪資計算 | Payroll | - | Saga 流程啟動 |
| `PayrollRunCompleted` | 薪資計算完成 | Payroll | Notification, Report | 發送通知、更新報表 |
| `PayslipGenerated` | 產生薪資單 | Payroll | Document | 產生 PDF |
| `PayslipPDFGenerated` | PDF 產生完成 | Payroll | Notification | 準備寄送 |
| `PayrollRunSubmitted` | 薪資批次送審 | Payroll | Notification | 通知主管審核 |
| `PayrollRunApproved` | 薪資核准 | Payroll | - | 產生銀行檔案 |
| `PayrollRunRejected` | 薪資駁回 | Payroll | Notification | 通知 HR |
| `PayrollPaid` | 薪資已發放 | Payroll | Report | 更新財務報表 |
| `PayslipSent` | 薪資單已寄送 | Payroll | - | 記錄稽核 |

---

### 4.2 SalaryStructureCreatedEvent (薪資結構建立事件)

**觸發時機:** HR 建立員工薪資結構

**Event Payload:**
```json
{
  "eventId": "evt-salary-created-001",
  "eventType": "SalaryStructureCreatedEvent",
  "timestamp": "2026-02-06T10:00:00Z",
  "aggregateId": "structure-550e8400-e29b-41d4-a716-446655440001",
  "payload": {
    "structureId": "structure-550e8400-e29b-41d4-a716-446655440001",
    "employeeId": "E001",
    "employeeNumber": "EMP001",
    "employeeName": "王小華",
    "payrollSystem": "MONTHLY",
    "monthlySalary": 50000,
    "calculatedHourlyRate": 208.33,
    "salaryItems": [
      {
        "itemCode": "JOB_ALLOWANCE",
        "itemName": "職務加給",
        "itemType": "EARNING",
        "amount": 5000,
        "isTaxable": true,
        "isInsurable": true
      }
    ],
    "effectiveDate": "2026-02-01",
    "createdAt": "2026-02-06T10:00:00"
  }
}
```

**下游消費者:**
- **Insurance Service**: 計算勞保、健保、勞退費用（依投保薪資）

---

### 4.3 SalaryStructureChangedEvent (薪資調整事件)

**觸發時機:** HR 調整員工薪資

**Event Payload:**
```json
{
  "eventId": "evt-salary-changed-001",
  "eventType": "SalaryStructureChangedEvent",
  "timestamp": "2026-02-06T11:00:00Z",
  "aggregateId": "structure-550e8400-e29b-41d4-a716-446655440002",
  "payload": {
    "newStructureId": "structure-550e8400-e29b-41d4-a716-446655440002",
    "oldStructureId": "structure-550e8400-e29b-41d4-a716-446655440001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "oldMonthlySalary": 50000,
    "newMonthlySalary": 55000,
    "adjustmentAmount": 5000,
    "adjustmentPercentage": 10.0,
    "effectiveDate": "2026-03-01",
    "reason": "年度調薪",
    "changedAt": "2026-02-06T11:00:00"
  }
}
```

**下游消費者:**
- **Insurance Service**: 重新計算保險費用（投保薪資可能調整）

---

### 4.4 PayrollRunStartedEvent (薪資計算開始事件)

**觸發時機:** HR 執行薪資計算作業

**Event Payload:**
```json
{
  "eventId": "evt-payroll-started-001",
  "eventType": "PayrollRunStartedEvent",
  "timestamp": "2026-02-01T09:00:00Z",
  "aggregateId": "run-202601",
  "payload": {
    "runId": "run-202601",
    "organizationId": "ORG001",
    "payPeriodStart": "2026-01-01",
    "payPeriodEnd": "2026-01-31",
    "payDate": "2026-02-05",
    "totalEmployees": 150,
    "executedBy": "HR001",
    "executedAt": "2026-02-01T09:00:00"
  }
}
```

**下游消費者:**
- 無（內部 Saga 流程使用）

---

### 4.5 PayrollRunCompletedEvent (薪資計算完成事件)

**觸發時機:** 薪資批次計算完成

**Event Payload:**
```json
{
  "eventId": "evt-payroll-completed-001",
  "eventType": "PayrollRunCompletedEvent",
  "timestamp": "2026-02-01T10:30:00Z",
  "aggregateId": "run-202601",
  "payload": {
    "runId": "run-202601",
    "organizationId": "ORG001",
    "payPeriod": "2026-01-01 ~ 2026-01-31",
    "payDate": "2026-02-05",
    "statistics": {
      "totalEmployees": 150,
      "processedEmployees": 148,
      "failedEmployees": 2,
      "totalGrossAmount": 8500000,
      "totalNetAmount": 7200000,
      "totalDeductions": 1300000
    },
    "completedAt": "2026-02-01T10:30:00"
  }
}
```

**下游消費者:**
- **Notification Service**: 發送計算完成通知給 HR
- **Report Service**: 更新薪資報表數據

---

### 4.6 PayslipGeneratedEvent (薪資單產生事件)

**觸發時機:** 薪資計算過程中為每位員工產生薪資單

**Event Payload:**
```json
{
  "eventId": "evt-payslip-generated-001",
  "eventType": "PayslipGeneratedEvent",
  "timestamp": "2026-02-01T10:15:00Z",
  "aggregateId": "payslip-550e8400-e29b-41d4-a716-446655440003",
  "payload": {
    "payslipId": "payslip-550e8400-e29b-41d4-a716-446655440003",
    "payrollRunId": "run-202601",
    "employeeId": "E001",
    "employeeNumber": "EMP001",
    "employeeName": "王小華",
    "payPeriod": "2026-01",
    "baseSalary": 50000,
    "totalEarnings": 55000,
    "totalOvertimePay": 5000,
    "leaveDeduction": 2000,
    "grossWage": 58000,
    "totalDeductions": 8500,
    "netWage": 49500,
    "bankAccount": {
      "bankCode": "012",
      "accountNumber": "****5678"
    },
    "generatedAt": "2026-02-01T10:15:00"
  }
}
```

**下游消費者:**
- **Document Service**: 產生薪資單 PDF

---

### 4.7 PayslipPDFGeneratedEvent (薪資單 PDF 產生事件)

**觸發時機:** Document Service 完成薪資單 PDF 產生

**Event Payload:**
```json
{
  "eventId": "evt-pdf-generated-001",
  "eventType": "PayslipPDFGeneratedEvent",
  "timestamp": "2026-02-01T10:20:00Z",
  "aggregateId": "payslip-550e8400-e29b-41d4-a716-446655440003",
  "payload": {
    "payslipId": "payslip-550e8400-e29b-41d4-a716-446655440003",
    "employeeId": "E001",
    "employeeName": "王小華",
    "pdfUrl": "https://storage.example.com/payslips/202601/E001.pdf",
    "fileSize": 245678,
    "generatedAt": "2026-02-01T10:20:00"
  }
}
```

**下游消費者:**
- **Notification Service**: 準備寄送薪資單 Email

---

### 4.8 PayrollRunApprovedEvent (薪資核准事件)

**觸發時機:** 主管或 HR 核准薪資批次

**Event Payload:**
```json
{
  "eventId": "evt-payroll-approved-001",
  "eventType": "PayrollRunApprovedEvent",
  "timestamp": "2026-02-03T14:00:00Z",
  "aggregateId": "run-202601",
  "payload": {
    "runId": "run-202601",
    "organizationId": "ORG001",
    "payPeriod": "2026-01-01 ~ 2026-01-31",
    "payDate": "2026-02-05",
    "totalNetAmount": 7200000,
    "approvedBy": "MGR001",
    "approverName": "李主管",
    "approvedAt": "2026-02-03T14:00:00"
  }
}
```

**下游消費者:**
- 無（內部觸發銀行檔案產生）

---

### 4.9 PayrollPaidEvent (薪資發放事件)

**觸發時機:** HR 標記薪資批次已發放

**Event Payload:**
```json
{
  "eventId": "evt-payroll-paid-001",
  "eventType": "PayrollPaidEvent",
  "timestamp": "2026-02-05T10:00:00Z",
  "aggregateId": "run-202601",
  "payload": {
    "runId": "run-202601",
    "organizationId": "ORG001",
    "payDate": "2026-02-05",
    "totalNetAmount": 7200000,
    "totalEmployees": 148,
    "bankFileUrl": "https://storage.example.com/bank-transfer/202601.txt",
    "paidAt": "2026-02-05T10:00:00"
  }
}
```

**下游消費者:**
- **Report Service**: 更新財務報表

---

### 4.10 PayslipSentEvent (薪資單寄送事件)

**觸發時機:** 薪資單 Email 已寄送給員工

**Event Payload:**
```json
{
  "eventId": "evt-payslip-sent-001",
  "eventType": "PayslipSentEvent",
  "timestamp": "2026-02-05T11:00:00Z",
  "aggregateId": "payslip-550e8400-e29b-41d4-a716-446655440003",
  "payload": {
    "payslipId": "payslip-550e8400-e29b-41d4-a716-446655440003",
    "employeeId": "E001",
    "employeeName": "王小華",
    "employeeEmail": "wang@example.com",
    "pdfUrl": "https://storage.example.com/payslips/202601/E001.pdf",
    "sentAt": "2026-02-05T11:00:00"
  }
}
```

**下游消費者:**
- 無（純記錄事件）

---

## 補充說明

### 5.1 通用安全規則

1. **高度敏感資料:**
   - 薪資資料需嚴格控管，只有 HR_PAYROLL 可查詢他人
   - 所有薪資查詢需記錄稽核日誌
   - 薪資單 PDF 需加密儲存

2. **軟刪除過濾:**
   - 薪資結構使用 `is_active = TRUE` 過濾
   - 薪資批次使用 `status != 'CANCELLED'` 過濾
   - **不使用 `is_deleted` 欄位**

3. **個人資料保護:**
   - 員工只能查詢自己的薪資資料
   - 主管不可查詢下屬薪資（勞基法規定）
   - 自動套用 `employee_id = '{currentUserId}'` 過濾

4. **稽核軌跡:**
   - 所有薪資計算需記錄完整計算過程（payroll_audit_logs）
   - 記錄來源數據快照（差勤、保險等）
   - 記錄執行人、執行時間

### 5.2 測試注意事項

1. **合約測試重點:** 驗證業務規則執行，而非 SQL 生成
2. **欄位存在性:** 所有合約中的欄位必須在資料表中實際存在
3. **API 端點一致性:** 合約中的 API 端點必須與實際 Controller 路徑一致
4. **Domain Events:** 命令操作必須驗證 Domain Event 是否正確發布
5. **Saga 流程:** 薪資計算需驗證 Saga 協調器正確執行

### 5.3 Saga 模式說明

薪資計算使用 **Saga 模式** 協調跨服務資料收集：

**Saga Orchestrator:**
```java
public class PayrollCalculationSaga {
    public SagaResult execute(PayrollRun run) {
        // Step 1: 獲取差勤數據
        AttendanceData attendance = attendanceService.getMonthlyData(...);

        // Step 2: 獲取保險費用
        InsuranceFees insurance = insuranceService.calculateFees(...);

        // Step 3: 計算薪資單
        Payslip payslip = Payslip.calculate(...);

        // Step 4: 儲存結果
        payslipRepository.save(payslip);

        return SagaResult.success(payslip);
    }
}
```

**Saga Compensation (補償):**
- 若計算失敗，不回滾已完成的步驟
- 記錄錯誤原因（error_message）
- 標記員工為計算失敗（has_error = TRUE）
- 允許 HR 手動修正後重新計算

### 5.4 勞基法合規性

本服務所有業務規則必須符合「勞動基準法」規定：

- **時薪計算:** 月薪 ÷ 240 小時（勞基法基準）
- **加班費率:** 平日 1.34/1.67 倍、休息日 1.34/1.67/2.67 倍、假日 2 倍
- **投保薪資:** 勞保/健保/勞退投保薪資級距（依勞動部公告）
- **所得稅:** 依財政部扣繳稅額表計算
- **二代健保:** 單次獎金超過 4 倍底薪需扣補充保費

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-06 | 移除不存在的 is_deleted 欄位，新增 API 端點、Command 操作、Domain Events、Saga 模式說明，採用雙層結構設計 |
| 1.0 | 2025-12-19 | 初版建立 |
