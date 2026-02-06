# 組織員工服務業務合約 v2.0 (Organization Service Contract)

**服務代碼:** HR02
**版本:** 2.0
**建立日期:** 2026-02-06
**維護者:** SA Team + Development Team
**狀態:** ✅ 生效中

---

## 📋 目錄

1. [Query 操作業務合約](#1-query-操作業務合約)
   - 1.1 [員工查詢合約](#11-員工查詢合約)
   - 1.2 [部門查詢合約](#12-部門查詢合約)
   - 1.3 [組織查詢合約](#13-組織查詢合約)
   - 1.4 [員工自助查詢合約](#14-員工自助查詢合約ess)
   - 1.5 [合約查詢合約](#15-合約查詢合約)
2. [Command 操作業務合約](#2-command-操作業務合約)
   - 2.1 [員工命令合約](#21-員工命令合約)
   - 2.2 [部門命令合約](#22-部門命令合約)
   - 2.3 [組織命令合約](#23-組織命令合約)
   - 2.4 [員工自助命令合約](#24-員工自助命令合約ess)
   - 2.5 [合約命令合約](#25-合約命令合約)
3. [領域事件合約](#3-領域事件合約)

---

## 🔴 重要說明

### 軟刪除實作方式

**本服務不使用 `is_deleted` 欄位！**

✅ **正確的過濾方式：**

| Entity | 欄位 | 在職/啟用狀態 | 離職/停用狀態 |
|:---|:---|:---|:---|
| **Employee** | `employment_status` | `'PROBATION'`, `'ACTIVE'`, `'PARENTAL_LEAVE'`, `'UNPAID_LEAVE'` | `'TERMINATED'` |
| **Department** | `status` | `'ACTIVE'` | `'INACTIVE'` |
| **Organization** | `status` | `'ACTIVE'` | `'INACTIVE'` |

**範例：**
```sql
-- ❌ 錯誤
WHERE is_deleted = 0

-- ✅ 正確（員工）
WHERE employment_status != 'TERMINATED'
-- 或
WHERE employment_status IN ('PROBATION', 'ACTIVE', 'PARENTAL_LEAVE', 'UNPAID_LEAVE')

-- ✅ 正確（部門/組織）
WHERE status = 'ACTIVE'
```

---

## 1. Query 操作業務合約

### 1.1 員工查詢合約

#### 1.1.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ORG_QRY_E001 | 查詢在職員工 | HR | `GET /api/v1/employees` | `{"employmentStatus":"ACTIVE"}` | `employment_status = 'ACTIVE'` |
| ORG_QRY_E002 | 查詢離職員工 | HR | `GET /api/v1/employees` | `{"employmentStatus":"TERMINATED"}` | `employment_status = 'TERMINATED'` |
| ORG_QRY_E003 | 查詢試用期員工 | HR | `GET /api/v1/employees` | `{"employmentStatus":"PROBATION"}` | `employment_status = 'PROBATION'` |
| ORG_QRY_E004 | 查詢留職停薪員工 | HR | `GET /api/v1/employees` | `{"employmentStatus":"UNPAID_LEAVE"}` | `employment_status = 'UNPAID_LEAVE'` |
| ORG_QRY_E005 | 依部門查詢員工 | HR | `GET /api/v1/employees` | `{"departmentId":"DEPT-001"}` | `department_id = 'DEPT-001'`, `employment_status != 'TERMINATED'` |
| ORG_QRY_E006 | 依姓名模糊查詢 | HR | `GET /api/v1/employees` | `{"name":"王"}` | `full_name LIKE '%王%'`, `employment_status != 'TERMINATED'` |
| ORG_QRY_E007 | 依工號查詢 | HR | `GET /api/v1/employees` | `{"employeeNumber":"EMP001"}` | `employee_number = 'EMP001'` |
| ORG_QRY_E008 | 主管查詢下屬 | MANAGER | `GET /api/v1/employees` | `{}` | `department_id IN ('{managedDeptIds}')`, `employment_status = 'ACTIVE'` |
| ORG_QRY_E009 | 員工查詢同部門 | EMPLOYEE | `GET /api/v1/employees` | `{}` | `department_id = '{currentUserDeptId}'`, `employment_status = 'ACTIVE'` |
| ORG_QRY_E010 | 依到職日期範圍查詢 | HR | `GET /api/v1/employees` | `{"hireDateFrom":"2025-01-01","hireDateTo":"2025-12-31"}` | `hire_date >= '2025-01-01'`, `hire_date <= '2025-12-31'`, `employment_status != 'TERMINATED'` |
| ORG_QRY_E011 | 查詢員工詳情 | HR | `GET /api/v1/employees/{id}` | `{"employeeId":"EMP-001"}` | `employee_id = 'EMP-001'` |
| ORG_QRY_E012 | 查詢員工異動歷程 | HR | `GET /api/v1/employees/{id}/history` | `{"employeeId":"EMP-001"}` | `employee_id = 'EMP-001'` |

#### 1.1.2 詳細業務場景說明

##### ORG_QRY_E001: 查詢在職員工

**業務場景描述：**
HR 查詢所有在職狀態的員工列表（不含離職）。

**必須包含的過濾條件：**
- `employment_status = 'ACTIVE'`

**權限檢查：**
- 需要 `employee:view` 權限
- HR 角色可查詢全公司

**測試範例：**
```java
@Test
@DisplayName("ORG_QRY_E001: 查詢在職員工")
@WithMockUser(roles = "HR")
void scenario_ORG_QRY_E001() throws Exception {
    String contractSpec = loadContractSpec("organization");
    GetEmployeeListRequest request = GetEmployeeListRequest.builder()
        .employmentStatus("ACTIVE")
        .build();

    verifyApiContract("/api/v1/employees", request, contractSpec, "ORG_QRY_E001");
}
```

##### ORG_QRY_E005: 依部門查詢員工

**業務場景描述：**
HR 查詢特定部門的所有在職員工。

**必須包含的過濾條件：**
- `department_id = '{deptId}'`
- `employment_status != 'TERMINATED'`（排除離職員工）

**業務規則：**
1. 必須過濾掉離職員工
2. 支援查詢子部門（可選參數 `includeSubDepts`）

**測試範例：**
```java
@Test
@DisplayName("ORG_QRY_E005: 依部門查詢員工")
@WithMockUser(roles = "HR")
void scenario_ORG_QRY_E005() throws Exception {
    String contractSpec = loadContractSpec("organization");
    GetEmployeeListRequest request = GetEmployeeListRequest.builder()
        .departmentId("DEPT-001")
        .build();

    ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
    when(employeeRepository.findPage(queryCaptor.capture(), any()))
        .thenReturn(new PageImpl<>(Collections.emptyList()));

    mockMvc.perform(get("/api/v1/employees")
            .param("departmentId", "DEPT-001")
            .contentType("application/json"))
        .andExpect(status().isOk());

    assertContract(queryCaptor.getValue(), contractSpec, "ORG_QRY_E005");
}
```

##### ORG_QRY_E008: 主管查詢下屬

**業務場景描述：**
部門主管查詢所管轄部門的所有在職員工。

**必須包含的過濾條件：**
- `department_id IN ('{managedDeptIds}')` - 所管轄的部門 ID 列表
- `employment_status = 'ACTIVE'`

**權限檢查：**
- MANAGER 角色只能查詢自己管轄的部門
- 自動從當前使用者取得 `managedDeptIds`

**業務規則：**
1. 查詢當前使用者擔任主管的所有部門
2. 只返回在職員工

---

### 1.2 部門查詢合約

#### 1.2.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ORG_QRY_D001 | 查詢所有啟用部門 | HR | `GET /api/v1/departments` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| ORG_QRY_D002 | 查詢頂層部門 | HR | `GET /api/v1/departments` | `{"parentId":null}` | `parent_department_id IS NULL`, `status = 'ACTIVE'` |
| ORG_QRY_D003 | 查詢子部門 | HR | `GET /api/v1/departments/{id}/sub-departments` | `{"parentId":"DEPT-001"}` | `parent_department_id = 'DEPT-001'`, `status = 'ACTIVE'` |
| ORG_QRY_D004 | 依名稱模糊查詢 | HR | `GET /api/v1/departments` | `{"name":"研發"}` | `department_name LIKE '%研發%'`, `status = 'ACTIVE'` |
| ORG_QRY_D005 | 依代碼查詢 | HR | `GET /api/v1/departments` | `{"code":"RD"}` | `department_code = 'RD'`, `status = 'ACTIVE'` |
| ORG_QRY_D006 | 查詢已停用部門 | ADMIN | `GET /api/v1/departments` | `{"status":"INACTIVE"}` | `status = 'INACTIVE'` |
| ORG_QRY_D007 | 查詢部門詳情 | HR | `GET /api/v1/departments/{id}` | `{"departmentId":"DEPT-001"}` | `department_id = 'DEPT-001'` |

#### 1.2.2 詳細業務場景說明

##### ORG_QRY_D002: 查詢頂層部門

**業務場景描述：**
查詢組織架構的最上層部門（沒有父部門的部門）。

**必須包含的過濾條件：**
- `parent_department_id IS NULL`
- `status = 'ACTIVE'`

**業務規則：**
1. 只返回啟用的部門
2. 依 `display_order` 排序

---

### 1.3 組織查詢合約

#### 1.3.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ORG_QRY_O001 | 查詢所有啟用組織 | ADMIN | `GET /api/v1/organizations` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| ORG_QRY_O002 | 查詢母公司 | ADMIN | `GET /api/v1/organizations` | `{"type":"PARENT"}` | `organization_type = 'PARENT'`, `status = 'ACTIVE'` |
| ORG_QRY_O003 | 查詢子公司 | ADMIN | `GET /api/v1/organizations` | `{"type":"SUBSIDIARY","parentId":"ORG-001"}` | `organization_type = 'SUBSIDIARY'`, `parent_organization_id = 'ORG-001'`, `status = 'ACTIVE'` |
| ORG_QRY_O004 | 查詢組織詳情 | ADMIN | `GET /api/v1/organizations/{id}` | `{"organizationId":"ORG-001"}` | `organization_id = 'ORG-001'` |

---

### 1.4 員工自助查詢合約（ESS）

#### 1.4.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ORG_QRY_ESS001 | 查詢個人資料 | EMPLOYEE | `GET /api/v1/employees/me` | `{}` | `employee_id = '{currentUserId}'` |
| ORG_QRY_ESS002 | 查詢證明文件申請記錄 | EMPLOYEE | `GET /api/v1/employees/me/certificate-requests` | `{}` | `employee_id = '{currentUserId}'` |

---

### 1.5 合約查詢合約

#### 1.5.1 機器可讀合約表格 (For Test Automation)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| ORG_QRY_C001 | 查詢員工合約列表 | HR | `GET /api/v1/employees/{employeeId}/contracts` | `{"employeeId":"EMP-001"}` | `employee_id = 'EMP-001'` |
| ORG_QRY_C002 | 查詢生效中的合約 | HR | `GET /api/v1/employees/{employeeId}/contracts` | `{"employeeId":"EMP-001","status":"ACTIVE"}` | `employee_id = 'EMP-001'`, `status = 'ACTIVE'` |

---

## 2. Command 操作業務合約

### 2.1 員工命令合約

#### ORG_CMD_E001: 新增員工（到職）

**業務場景描述：**
HR 建立新員工檔案，完成到職程序。

**API 端點：** `POST /api/v1/employees`

**前置條件：**
- 執行者必須擁有 `employee:create` 權限
- `employee_number` 必須唯一
- `company_email` 必須唯一
- `national_id` 必須唯一（如有提供）
- `department_id` 必須存在於 departments 表

**業務規則驗證：**
1. ✅ 員工編號唯一性檢查
2. ✅ Email 唯一性檢查
3. ✅ 身分證號唯一性檢查
4. ✅ 到職日期不可為未來日期（除非特殊情況）
5. ✅ 預設 `employment_status = 'PROBATION'`（試用期）
6. ✅ 計算 `probation_end_date`（到職日 + 3個月）

**必須發布的領域事件：**
- `EmployeeCreatedEvent`

**Event Payload 必須包含：**
```json
{
  "employeeId": "EMP-001",
  "employeeNumber": "20260001",
  "fullName": "王小明",
  "companyEmail": "wang.xiaoming@company.com",
  "organizationId": "ORG-001",
  "departmentId": "DEPT-001",
  "managerId": "EMP-100",
  "hireDate": "2026-02-06",
  "employmentStatus": "PROBATION",
  "jobTitle": "軟體工程師",
  "jobLevel": "E2"
}
```

**測試驗證點：**
- [ ] 資料庫驗證：`employees` 表有新記錄
- [ ] 狀態驗證：`employment_status = 'PROBATION'`
- [ ] 試用期驗證：`probation_end_date = hire_date + 3 months`
- [ ] 事件驗證：`EmployeeCreatedEvent` 已發布

**測試範例：**
```java
@Test
@DisplayName("ORG_CMD_E001: 新增員工（到職）")
@WithMockUser(roles = "HR")
void scenario_ORG_CMD_E001() throws Exception {
    CreateEmployeeRequest request = CreateEmployeeRequest.builder()
        .employeeNumber("20260001")
        .firstName("小明")
        .lastName("王")
        .companyEmail("wang.xiaoming@company.com")
        .departmentId("DEPT-001")
        .hireDate(LocalDate.of(2026, 2, 6))
        .jobTitle("軟體工程師")
        .jobLevel("E2")
        .build();

    mockMvc.perform(post("/api/v1/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // 驗證 Domain Event 發布
    verify(eventPublisher).publish(argThat(event ->
        event instanceof EmployeeCreatedEvent &&
        ((EmployeeCreatedEvent) event).getEmployeeNumber().equals("20260001")
    ));
}
```

---

#### ORG_CMD_E002: 員工離職

**業務場景描述：**
HR 辦理員工離職程序。

**API 端點：** `POST /api/v1/employees/{id}/terminate`

**前置條件：**
- 執行者必須擁有 `employee:terminate` 權限
- 員工狀態必須不是 `TERMINATED`

**業務規則驗證：**
1. ✅ 離職日期不可早於到職日期
2. ✅ 必須提供離職原因
3. ✅ 狀態更新為 `TERMINATED`
4. ✅ 記錄 `termination_date` 和 `termination_reason`

**必須發布的領域事件：**
- `EmployeeTerminatedEvent`

**Event Payload 必須包含：**
```json
{
  "employeeId": "EMP-001",
  "employeeNumber": "20260001",
  "terminationDate": "2026-12-31",
  "terminationReason": "自願離職",
  "lastWorkingDay": "2026-12-31"
}
```

**下游消費者：**
- IAM Service: 停用使用者帳號
- Attendance Service: 結算未休假天數
- Insurance Service: 停保
- Payroll Service: 計算最後薪資
- Project Service: 移除專案成員

**測試驗證點：**
- [ ] 狀態驗證：`employment_status = 'TERMINATED'`
- [ ] 資料驗證：`termination_date` 已設定
- [ ] 事件驗證：`EmployeeTerminatedEvent` 已發布

---

#### ORG_CMD_E003: 部門調動

**業務場景描述：**
HR 辦理員工部門調動。

**API 端點：** `POST /api/v1/employees/{id}/transfer`

**業務規則驗證：**
1. ✅ 新部門必須存在且啟用
2. ✅ 生效日期不可為過去
3. ✅ 更新 `department_id`
4. ✅ 更新 `manager_id`（新部門主管）
5. ✅ 記錄異動歷程

**必須發布的領域事件：**
- `EmployeeDepartmentChangedEvent`

**Event Payload 必須包含：**
```json
{
  "employeeId": "EMP-001",
  "oldDepartmentId": "DEPT-001",
  "newDepartmentId": "DEPT-002",
  "oldManagerId": "EMP-100",
  "newManagerId": "EMP-200",
  "effectiveDate": "2026-03-01",
  "reason": "組織調整"
}
```

---

#### ORG_CMD_E004: 員工升遷

**業務場景描述：**
HR 辦理員工升遷。

**API 端點：** `POST /api/v1/employees/{id}/promote`

**業務規則驗證：**
1. ✅ 新職等必須高於現有職等
2. ✅ 生效日期不可為過去
3. ✅ 更新 `job_title` 和 `job_level`
4. ✅ 記錄異動歷程

**必須發布的領域事件：**
- `EmployeePromotedEvent`

**Event Payload 必須包含：**
```json
{
  "employeeId": "EMP-001",
  "oldJobTitle": "軟體工程師",
  "newJobTitle": "資深軟體工程師",
  "oldJobLevel": "E2",
  "newJobLevel": "E3",
  "effectiveDate": "2026-04-01",
  "reason": "年度晉升"
}
```

---

#### ORG_CMD_E005: 試用期轉正

**業務場景描述：**
HR 辦理員工試用期通過，轉為正式員工。

**API 端點：** `POST /api/v1/employees/{id}/regularize`

**業務規則驗證：**
1. ✅ 員工狀態必須是 `PROBATION`
2. ✅ 已過試用期結束日（`probation_end_date`）
3. ✅ 狀態更新為 `ACTIVE`

**必須發布的領域事件：**
- `EmployeeProbationPassedEvent`

**Event Payload 必須包含：**
```json
{
  "employeeId": "EMP-001",
  "effectiveDate": "2026-05-06",
  "probationStartDate": "2026-02-06",
  "probationEndDate": "2026-05-06"
}
```

---

### 2.2 部門命令合約

#### ORG_CMD_D001: 新增部門

**業務場景描述：**
ADMIN 建立新部門。

**API 端點：** `POST /api/v1/departments`

**業務規則驗證：**
1. ✅ 部門代碼唯一性檢查（同組織內）
2. ✅ 如有父部門，父部門必須存在且啟用
3. ✅ 自動計算 `level`（父部門 level + 1）
4. ✅ `level` 不可超過 5
5. ✅ 預設 `status = 'ACTIVE'`

**必須發布的領域事件：**
- `DepartmentCreatedEvent`

---

#### ORG_CMD_D002: 指派部門主管

**業務場景描述：**
ADMIN 指派或變更部門主管。

**API 端點：** `POST /api/v1/departments/{id}/assign-manager`

**業務規則驗證：**
1. ✅ 新主管必須是該部門的員工
2. ✅ 新主管狀態必須是 `ACTIVE`
3. ✅ 更新 `manager_id`

**必須發布的領域事件：**
- `DepartmentManagerChangedEvent`

---

#### ORG_CMD_D003: 停用部門

**業務場景描述：**
ADMIN 停用部門（組織調整）。

**API 端點：** `POST /api/v1/departments/{id}/deactivate`

**業務規則驗證：**
1. ✅ 部門下不可有在職員工
2. ✅ 部門下不可有啟用的子部門
3. ✅ 狀態更新為 `INACTIVE`

---

### 2.3 組織命令合約

#### ORG_CMD_O001: 新增組織

**業務場景描述：**
ADMIN 建立新組織（公司）。

**API 端點：** `POST /api/v1/organizations`

**業務規則驗證：**
1. ✅ 組織代碼唯一性檢查
2. ✅ 統一編號唯一性檢查
3. ✅ 如為子公司，母公司必須存在
4. ✅ 預設 `status = 'ACTIVE'`

---

### 2.4 員工自助命令合約（ESS）

#### ORG_CMD_ESS001: 更新個人資料

**業務場景描述：**
員工更新自己的個人資料（限定欄位）。

**API 端點：** `PUT /api/v1/employees/me`

**可更新欄位：**
- `personal_email`, `mobile_phone`, `address`, `emergency_contact`

**不可更新欄位：**
- `employee_number`, `company_email`, `department_id`, `employment_status` 等

---

#### ORG_CMD_ESS002: 申請證明文件

**業務場景描述：**
員工申請在職證明、薪資證明等文件。

**API 端點：** `POST /api/v1/employees/me/certificate-requests`

**業務規則驗證：**
1. ✅ 員工狀態必須是 `ACTIVE`
2. ✅ 證明類型必須有效
3. ✅ 預設 `status = 'PENDING'`

**必須發布的領域事件：**
- `CertificateRequestedEvent`

---

### 2.5 合約命令合約

#### ORG_CMD_C001: 新增員工合約

**業務場景描述：**
HR 建立員工勞動合約。

**API 端點：** `POST /api/v1/employees/{employeeId}/contracts`

**業務規則驗證：**
1. ✅ 員工必須存在
2. ✅ 合約編號唯一性檢查
3. ✅ 合約起始日不可早於到職日
4. ✅ 定期契約必須有結束日期
5. ✅ 不定期契約不應有結束日期

---

## 3. 領域事件合約

### 3.1 領域事件清單

| 事件名稱 | 觸發場景 | Schema 必須包含 | 訂閱服務 |
|:---|:---|:---|:---|
| `EmployeeCreatedEvent` | ORG_CMD_E001 新員工到職 | employeeId, employeeNumber, companyEmail, departmentId, hireDate | IAM, Insurance, Payroll |
| `EmployeeProbationPassedEvent` | ORG_CMD_E005 試用期轉正 | employeeId, effectiveDate | Payroll |
| `EmployeeTerminatedEvent` | ORG_CMD_E002 員工離職 | employeeId, employeeNumber, terminationDate, reason | IAM, Attendance, Insurance, Payroll, Project |
| `EmployeeDepartmentChangedEvent` | ORG_CMD_E003 部門調動 | employeeId, oldDepartmentId, newDepartmentId, effectiveDate | Attendance, Payroll |
| `EmployeePromotedEvent` | ORG_CMD_E004 員工升遷 | employeeId, oldJobTitle, newJobTitle, oldJobLevel, newJobLevel, effectiveDate | Payroll, Performance |
| `DepartmentCreatedEvent` | ORG_CMD_D001 新增部門 | departmentId, departmentCode, departmentName | - |
| `DepartmentManagerChangedEvent` | ORG_CMD_D002 主管異動 | departmentId, oldManagerId, newManagerId, effectiveDate | Attendance |
| `CertificateRequestedEvent` | ORG_CMD_ESS002 證明文件申請 | requestId, employeeId, certificateType | Notification |
| `CertificateCompletedEvent` | 證明文件完成 | requestId, employeeId, documentUrl | Notification |

---

## 4. 通用業務規則

### 4.1 權限控制

| 角色 | 可查詢範圍 | 可執行操作 |
|:---|:---|:---|
| **HR** | 全公司員工 | 員工到職、離職、調動、升遷 |
| **MANAGER** | 所管轄部門員工 | 查詢下屬資料 |
| **EMPLOYEE** | 自己 + 同部門基本資訊 | 更新個人資料、申請證明文件 |
| **ADMIN** | 全部 | 組織、部門管理 |

### 4.2 資料遮蔽規則

**敏感欄位（需遮蔽）：**
- `national_id` - 身分證號
- `bank_account` - 銀行帳戶
- `personal_email` - 個人 Email
- `mobile_phone` - 手機號碼
- `address` - 地址

**遮蔽規則：**
- EMPLOYEE 查詢同部門時，敏感欄位遮蔽
- MANAGER 查詢下屬時，可查看部分敏感欄位
- HR 可查看完整資訊

---

## 5. 測試案例模板

### 5.1 Query 測試模板

```java
@Test
@DisplayName("{場景ID}: {測試描述}")
@WithMockUser(roles = "{角色}")
void scenario_{場景ID}() throws Exception {
    // 1. 載入合約
    String contractSpec = loadContractSpec("organization");

    // 2. 準備請求
    GetEmployeeListRequest request = GetEmployeeListRequest.builder()
        .{field}({value})
        .build();

    // 3. 建立 QueryGroup 攔截器
    ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
    when(employeeRepository.findPage(queryCaptor.capture(), any()))
        .thenReturn(new PageImpl<>(Collections.emptyList()));

    // 4. 執行 API
    mockMvc.perform(get("/api/v1/employees")
            .param("{field}", "{value}")
            .contentType("application/json"))
        .andExpect(status().isOk());

    // 5. 驗證合約
    assertContract(queryCaptor.getValue(), contractSpec, "{場景ID}");
}
```

### 5.2 Command 測試模板

```java
@Test
@DisplayName("{場景ID}: {測試描述}")
@WithMockUser(roles = "{角色}")
void scenario_{場景ID}() throws Exception {
    // 1. 準備請求
    CreateEmployeeRequest request = CreateEmployeeRequest.builder()
        .{field}({value})
        .build();

    // 2. 執行 API
    mockMvc.perform(post("/api/v1/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // 3. 驗證 Domain Event 發布
    verify(eventPublisher).publish(argThat(event ->
        event instanceof {EventName} &&
        (({EventName}) event).get{Field}().equals({expectedValue})
    ));
}
```

---

## 6. 注意事項

### ⚠️ 開發者注意事項

1. **欄位存在性：** 所有過濾條件中的欄位都已確認存在於資料表
2. **狀態欄位：** Employee 使用 `employment_status`，Department/Organization 使用 `status`
3. **軟刪除：** 不使用 `is_deleted`，使用狀態欄位區分
4. **異動歷程：** 所有員工異動都必須記錄到 `employee_history` 表
5. **事件發布：** 所有 Command 操作必須發布對應的 Domain Event

### ⚠️ 測試注意事項

1. **完整流程測試：** 必須測試 Controller → Service → Repository 完整流程
2. **權限測試：** 使用 `@WithMockUser` 模擬不同角色
3. **事件驗證：** Command 操作必須驗證事件是否正確發布
4. **合約載入：** 測試程式自動載入 `contracts/organization_contracts_v2.md`

---

**變更歷史**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-06 | 重建合約文件：移除 is_deleted、新增雙層結構、新增 Command 合約、新增領域事件 |
| 1.0 | 2025-12-19 | 初版（已廢棄） |

---

**維護責任：** SA Team + Development Team
**審核責任：** Tech Lead
**問題回報：** 請在專案 Issue Tracker 建立工單
