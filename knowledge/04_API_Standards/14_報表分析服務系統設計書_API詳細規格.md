# 報表分析服務 API 詳細規格

**版本:** 1.0
**日期:** 2025-12-30
**Domain代號:** 14 (RPT)
**對應系統設計書:** `knowledge/03_System_Architecture/Detailed_Design/14_報表分析服務系統設計書.md`

---

## 目錄

1. [Controller 命名對照](#1-controller-命名對照)
2. [API 總覽](#2-api-總覽-18個端點)
3. [人力資源報表 API](#3-人力資源報表-api)
4. [專案管理報表 API](#4-專案管理報表-api)
5. [財務報表 API](#5-財務報表-api)
6. [儀表板 API](#6-儀表板-api)
7. [報表匯出 API](#7-報表匯出-api)
8. [錯誤碼定義](#8-錯誤碼定義)
9. [領域事件](#9-領域事件)

---

## 1. Controller 命名對照

| Controller | 負責功能 | 類型 |
|:---|:---|:---:|
| `HR14HrQryController` | 人力資源報表查詢 | Query |
| `HR14ProjectQryController` | 專案管理報表查詢 | Query |
| `HR14FinanceQryController` | 財務報表查詢 | Query |
| `HR14DashboardCmdController` | 儀表板建立、更新、刪除 | Command |
| `HR14DashboardQryController` | 儀表板查詢 | Query |
| `HR14ExportCmdController` | 報表匯出 | Command |
| `HR14ExportQryController` | 匯出檔案下載 | Query |

---

## 2. API 總覽 (18個端點)

### 2.1 人力資源報表 API (4個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 1 | `/api/v1/reports/hr/employee-roster` | GET | 員工花名冊 | HR14HrQryController | `report:hr:read` |
| 2 | `/api/v1/reports/hr/headcount` | GET | 人力盤點報表 | HR14HrQryController | `report:hr:read` |
| 3 | `/api/v1/reports/hr/attendance-summary` | GET | 差勤統計報表 | HR14HrQryController | `report:hr:read` |
| 4 | `/api/v1/reports/hr/turnover` | GET | 離職率分析 | HR14HrQryController | `report:hr:read` |

### 2.2 專案管理報表 API (2個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 5 | `/api/v1/reports/project/cost-analysis` | GET | 專案成本分析 | HR14ProjectQryController | `report:project:read` |
| 6 | `/api/v1/reports/project/utilization-rate` | GET | 稼動率分析 | HR14ProjectQryController | `report:project:read` |

### 2.3 財務報表 API (3個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 7 | `/api/v1/reports/finance/labor-cost` | GET | 人力成本分析 | HR14FinanceQryController | `report:finance:read` |
| 8 | `/api/v1/reports/finance/labor-cost-by-department` | GET | 部門人力成本分析 | HR14FinanceQryController | `report:finance:read` |
| 9 | `/api/v1/reports/finance/payroll-summary` | GET | 薪資總表 | HR14FinanceQryController | `report:finance:read` |

### 2.4 儀表板 API (5個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 10 | `/api/v1/dashboards` | POST | 建立儀表板 | HR14DashboardCmdController | `dashboard:create` |
| 11 | `/api/v1/dashboards` | GET | 儀表板列表 | HR14DashboardQryController | `dashboard:read` |
| 12 | `/api/v1/dashboards/{id}` | GET | 儀表板詳情 | HR14DashboardQryController | `dashboard:read` |
| 13 | `/api/v1/dashboards/{id}/widgets` | PUT | 更新 Widget 配置 | HR14DashboardCmdController | `dashboard:update` |
| 14 | `/api/v1/dashboards/{id}` | DELETE | 刪除儀表板 | HR14DashboardCmdController | `dashboard:delete` |

### 2.5 報表匯出 API (4個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 15 | `/api/v1/reports/export/excel` | POST | 匯出 Excel | HR14ExportCmdController | `report:export` |
| 16 | `/api/v1/reports/export/pdf` | POST | 匯出 PDF | HR14ExportCmdController | `report:export` |
| 17 | `/api/v1/reports/export/government` | POST | 政府申報格式匯出 | HR14ExportCmdController | `report:export:government` |
| 18 | `/api/v1/reports/export/{id}/download` | GET | 下載匯出檔案 | HR14ExportQryController | `report:export` |

---

## 3. 人力資源報表 API

### 3.1 員工花名冊

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/hr/employee-roster` |
| Controller | `HR14HrQryController` |
| Service | `GetEmployeeRosterServiceImpl` |
| 權限 | `report:hr:read` |
| 版本 | v1 |

**用途說明**

查詢員工花名冊報表，包含員工基本資料、部門、職稱、到職日、年資等資訊。資料來源為 CQRS 讀模型（Materialized View），查詢效能優於直接查詢正規化資料表。

**業務邏輯**

1. **查詢讀模型**
   - 從 `employee_report_view` Materialized View 查詢
   - 支援多條件篩選與排序

2. **計算年資**
   - 年資 = (查詢日 - 到職日) / 365
   - 取到小數點後一位

3. **資料脫敏**
   - 身分證字號顯示遮蔽格式（如 A123****89）

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| organizationId | UUID | ⬚ | - | 組織 ID | `org-uuid-001` |
| departmentId | UUID | ⬚ | - | 部門 ID | `dept-uuid-001` |
| status | Enum | ⬚ | ACTIVE | 在職狀態 | `ACTIVE` |
| employmentType | Enum | ⬚ | - | 聘僱類型 | `FULL_TIME` |
| search | String | ⬚ | - | 關鍵字搜尋（姓名、工號） | `張` |
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 50 | 每頁筆數（最大 500） | `100` |
| sort | String | ⬚ | employeeNumber,asc | 排序 | `hireDate,desc` |

**status 列舉值**

| 值 | 說明 |
|:---|:---|
| `ACTIVE` | 在職 |
| `ON_LEAVE` | 留職停薪 |
| `TERMINATED` | 離職 |
| `ALL` | 全部 |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| content | Employee[] | 員工列表 |
| page | Integer | 當前頁碼 |
| size | Integer | 每頁筆數 |
| totalElements | Long | 總筆數 |
| totalPages | Integer | 總頁數 |

**Employee 物件結構**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| employeeId | UUID | 員工 ID |
| employeeNumber | String | 工號 |
| fullName | String | 姓名 |
| nationalIdMasked | String | 身分證（遮蔽） |
| email | String | Email |
| departmentName | String | 部門名稱 |
| departmentPath | String | 部門完整路徑 |
| jobTitle | String | 職稱 |
| managerName | String | 主管姓名 |
| hireDate | Date | 到職日 |
| serviceYears | Decimal | 年資 |
| employmentStatus | Enum | 在職狀態 |
| employmentType | Enum | 聘僱類型 |
| payrollSystem | Enum | 薪資制度 |

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "employeeId": "emp-uuid-001",
        "employeeNumber": "E001",
        "fullName": "張三",
        "nationalIdMasked": "A123****89",
        "email": "zhangsan@company.com",
        "departmentName": "研發部",
        "departmentPath": "OO科技 > 技術中心 > 研發部",
        "jobTitle": "資深前端工程師",
        "managerName": "李四",
        "hireDate": "2023-05-01",
        "serviceYears": 1.7,
        "employmentStatus": "ACTIVE",
        "employmentType": "FULL_TIME",
        "payrollSystem": "MONTHLY"
      }
    ],
    "page": 1,
    "size": 50,
    "totalElements": 150,
    "totalPages": 3
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_INVALID_DATE_RANGE | 日期區間無效 | 確認日期格式 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無報表查詢權限 | 聯繫管理員授權 |

---

### 3.2 人力盤點報表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/hr/headcount` |
| Controller | `HR14HrQryController` |
| Service | `GetHeadcountReportServiceImpl` |
| 權限 | `report:hr:read` |
| 版本 | v1 |

**用途說明**

查詢人力盤點報表，提供組織人力概況，包含在職人數、本月到職/離職、離職率、部門分布、聘僱類型分布等統計資料。

**業務邏輯**

1. **人數統計**
   - 在職人數：status = ACTIVE
   - 留停人數：status = ON_LEAVE
   - 本月離職：本月 termination_date 不為空

2. **離職率計算**
   - 月離職率 = 本月離職人數 / 期初在職人數 × 100%
   - 季離職率 = 季度離職人數 / 季初在職人數 × 100%
   - 年離職率 = 年度離職人數 / 年初在職人數 × 100%

3. **部門分布**
   - 計算各部門人數佔比

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| asOfDate | Date | ⬚ | 今日 | 統計基準日 | `2025-12-30` |
| organizationId | UUID | ⬚ | - | 組織 ID | `org-uuid-001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "asOfDate": "2025-12-30",
    "organizationName": "OO科技股份有限公司",
    "summary": {
      "totalEmployees": 150,
      "activeEmployees": 145,
      "onLeaveEmployees": 3,
      "terminatedThisMonth": 2,
      "newHiresThisMonth": 5,
      "netChange": 3
    },
    "byDepartment": [
      {
        "departmentId": "dept-uuid-001",
        "departmentName": "研發部",
        "headcount": 50,
        "percentage": 33.33,
        "avgSalary": 60000,
        "avgServiceYears": 3.5
      },
      {
        "departmentId": "dept-uuid-002",
        "departmentName": "業務部",
        "headcount": 30,
        "percentage": 20.0,
        "avgSalary": 55000,
        "avgServiceYears": 4.2
      }
    ],
    "byEmploymentType": {
      "FULL_TIME": 140,
      "CONTRACT": 8,
      "INTERN": 2
    },
    "turnoverRate": {
      "monthly": 1.33,
      "quarterly": 4.0,
      "yearly": 15.0
    },
    "generatedAt": "2025-12-30T08:30:00Z"
  }
}
```

---

### 3.3 差勤統計報表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/hr/attendance-summary` |
| Controller | `HR14HrQryController` |
| Service | `GetAttendanceSummaryServiceImpl` |
| 權限 | `report:hr:read` |
| 版本 | v1 |

**用途說明**

查詢差勤統計報表，彙整組織或部門的出勤狀況，包含總工時、加班時數、請假時數、異常打卡等統計。

**業務邏輯**

1. **工時統計**
   - 總工時：所有員工實際出勤時數總和
   - 平均工時：總工時 / 員工人數

2. **加班統計**
   - 平日加班、假日加班分開統計
   - 計算加班費預估

3. **請假統計**
   - 依假別分類統計

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| organizationId | UUID | ⬚ | - | 組織 ID | `org-uuid-001` |
| departmentId | UUID | ⬚ | - | 部門 ID | `dept-uuid-001` |
| month | String | ✅ | - | 統計月份 (YYYY-MM) | `2025-12` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "month": "2025-12",
    "organizationName": "OO科技股份有限公司",
    "totalEmployees": 150,
    "workingDays": 22,
    "workingHours": {
      "totalHours": 25200,
      "averageHoursPerEmployee": 168,
      "standardHours": 176
    },
    "overtime": {
      "totalOvertimeHours": 1200,
      "weekdayOvertimeHours": 800,
      "weekendOvertimeHours": 400,
      "averageOvertimePerEmployee": 8,
      "estimatedOvertimePay": 360000
    },
    "leave": {
      "totalLeaveHours": 500,
      "byLeaveType": [
        { "leaveType": "ANNUAL", "hours": 300 },
        { "leaveType": "SICK", "hours": 100 },
        { "leaveType": "PERSONAL", "hours": 80 },
        { "leaveType": "OTHER", "hours": 20 }
      ]
    },
    "anomalies": {
      "lateCount": 15,
      "earlyLeaveCount": 8,
      "missingPunchCount": 5,
      "absentCount": 0
    },
    "byDepartment": [
      {
        "departmentName": "研發部",
        "headcount": 50,
        "totalHours": 8400,
        "overtimeHours": 500,
        "leaveHours": 150
      }
    ],
    "generatedAt": "2025-12-30T08:30:00Z"
  }
}
```

---

### 3.4 離職率分析

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/hr/turnover` |
| Controller | `HR14HrQryController` |
| Service | `GetTurnoverAnalysisServiceImpl` |
| 權限 | `report:hr:read` |
| 版本 | v1 |

**用途說明**

查詢離職率分析報表，提供離職趨勢、離職原因分布、部門離職率比較、年資分布等分析資料。

**業務邏輯**

1. **離職率計算**
   - 離職率 = 離職人數 / 期初人數 × 100%

2. **離職原因分析**
   - 統計各離職原因佔比

3. **趨勢分析**
   - 提供過去 12 個月的離職率趨勢

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| organizationId | UUID | ⬚ | - | 組織 ID | `org-uuid-001` |
| year | Integer | ⬚ | 今年 | 統計年度 | `2025` |
| startMonth | String | ⬚ | 年初 | 開始月份 | `2025-01` |
| endMonth | String | ⬚ | 當月 | 結束月份 | `2025-12` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "period": "2025-01 ~ 2025-12",
    "organizationName": "OO科技股份有限公司",
    "summary": {
      "totalTerminations": 20,
      "averageHeadcount": 145,
      "annualTurnoverRate": 13.79,
      "industryBenchmark": 15.0,
      "status": "GOOD"
    },
    "monthlyTrend": [
      { "month": "2025-01", "terminations": 2, "headcount": 140, "rate": 1.43 },
      { "month": "2025-02", "terminations": 1, "headcount": 143, "rate": 0.70 },
      { "month": "2025-03", "terminations": 3, "headcount": 145, "rate": 2.07 }
    ],
    "byReason": [
      { "reason": "PERSONAL", "count": 8, "percentage": 40.0 },
      { "reason": "CAREER_DEVELOPMENT", "count": 5, "percentage": 25.0 },
      { "reason": "COMPENSATION", "count": 4, "percentage": 20.0 },
      { "reason": "RELOCATION", "count": 2, "percentage": 10.0 },
      { "reason": "OTHER", "count": 1, "percentage": 5.0 }
    ],
    "byDepartment": [
      { "departmentName": "業務部", "terminations": 8, "headcount": 30, "rate": 26.67 },
      { "departmentName": "研發部", "terminations": 5, "headcount": 50, "rate": 10.0 }
    ],
    "byServiceYears": [
      { "range": "0-1年", "count": 10, "percentage": 50.0 },
      { "range": "1-3年", "count": 6, "percentage": 30.0 },
      { "range": "3-5年", "count": 3, "percentage": 15.0 },
      { "range": "5年以上", "count": 1, "percentage": 5.0 }
    ],
    "generatedAt": "2025-12-30T08:30:00Z"
  }
}
```

---

## 4. 專案管理報表 API

### 4.1 專案成本分析

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/project/cost-analysis` |
| Controller | `HR14ProjectQryController` |
| Service | `GetProjectCostAnalysisServiceImpl` |
| 權限 | `report:project:read` |
| 版本 | v1 |

**用途說明**

查詢專案成本分析報表，提供專案預算使用情況、實際成本、成員成本分布、月度趨勢、獲利率預估等分析。

**業務邏輯**

1. **成本計算**
   - 實際成本 = Σ(員工工時 × 員工時薪成本)
   - 時薪成本 = (月薪 + 勞健保雇主負擔) / 月工時

2. **預算使用率**
   - 成本使用率 = 實際成本 / 預算金額 × 100%
   - 工時使用率 = 實際工時 / 預算工時 × 100%

3. **獲利率計算**
   - 獲利率 = (合約金額 - 實際成本) / 合約金額 × 100%

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| projectId | UUID | ✅ | - | 專案 ID | `prj-uuid-001` |
| startMonth | String | ⬚ | 專案開始月 | 開始月份 | `2025-01` |
| endMonth | String | ⬚ | 當月 | 結束月份 | `2025-12` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "projectId": "prj-uuid-001",
    "projectCode": "PRJ-2025-001",
    "projectName": "XX銀行核心系統升級案",
    "customerName": "XX銀行",
    "period": "2025-01 ~ 2025-12",
    "budget": {
      "budgetType": "FIXED_PRICE",
      "contractAmount": 10000000,
      "budgetAmount": 7000000,
      "budgetHours": 5000
    },
    "actual": {
      "totalHours": 3500,
      "totalCost": 3500000,
      "costUtilization": 50.0,
      "hourUtilization": 70.0
    },
    "profitAnalysis": {
      "estimatedRevenue": 10000000,
      "estimatedCost": 7000000,
      "estimatedProfit": 3000000,
      "profitMargin": 30.0,
      "status": "ON_TRACK"
    },
    "monthlyTrend": [
      { "month": "2025-01", "hours": 400, "cost": 400000 },
      { "month": "2025-02", "hours": 350, "cost": 350000 },
      { "month": "2025-03", "hours": 420, "cost": 420000 }
    ],
    "memberCosts": [
      {
        "employeeId": "emp-uuid-001",
        "employeeName": "張三",
        "role": "Tech Lead",
        "hours": 500,
        "hourlyRate": 350,
        "cost": 175000,
        "percentage": 5.0
      }
    ],
    "generatedAt": "2025-12-30T08:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_PROJECT_NOT_FOUND | 專案不存在 | 確認專案 ID |

---

### 4.2 稼動率分析

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/project/utilization-rate` |
| Controller | `HR14ProjectQryController` |
| Service | `GetUtilizationRateServiceImpl` |
| 權限 | `report:project:read` |
| 版本 | v1 |

**用途說明**

查詢稼動率（Utilization Rate）分析報表，計算員工的計費工時比例，用於評估人力資源運用效率。

**業務邏輯**

1. **稼動率計算**
   - 稼動率 = 計費工時 / 總可用工時 × 100%
   - 計費工時 = 外部專案工時（排除內部專案、訓練、行政）
   - 總可用工時 = 工作天數 × 8 × 員工人數

2. **分級標準**
   - 優良：≥ 80%
   - 正常：60% ~ 80%
   - 待改善：< 60%

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| departmentId | UUID | ⬚ | - | 部門 ID | `dept-uuid-001` |
| month | String | ✅ | - | 統計月份 | `2025-12` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "month": "2025-12",
    "departmentName": "研發部",
    "summary": {
      "totalEmployees": 50,
      "workingDays": 22,
      "totalAvailableHours": 8800,
      "billableHours": 7040,
      "nonBillableHours": 1760,
      "utilizationRate": 80.0,
      "status": "EXCELLENT"
    },
    "byEmployee": [
      {
        "employeeId": "emp-uuid-001",
        "employeeName": "張三",
        "availableHours": 176,
        "billableHours": 160,
        "nonBillableHours": 16,
        "utilizationRate": 90.91,
        "status": "EXCELLENT"
      }
    ],
    "byProject": [
      {
        "projectId": "prj-uuid-001",
        "projectName": "XX銀行專案",
        "totalHours": 3500,
        "percentage": 49.72
      }
    ],
    "trend": [
      { "month": "2025-10", "rate": 75.0 },
      { "month": "2025-11", "rate": 78.0 },
      { "month": "2025-12", "rate": 80.0 }
    ],
    "generatedAt": "2025-12-30T08:30:00Z"
  }
}
```

---

## 5. 財務報表 API

### 5.1 人力成本分析

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/finance/labor-cost` |
| Controller | `HR14FinanceQryController` |
| Service | `GetLaborCostAnalysisServiceImpl` |
| 權限 | `report:finance:read` |
| 版本 | v1 |

**用途說明**

查詢年度人力成本分析報表，彙整薪資、勞保、健保、勞退等人力成本，並提供月度趨勢分析。

**業務邏輯**

1. **成本組成**
   - 薪資：底薪 + 津貼 + 加班費 + 獎金
   - 勞保：公司負擔部分
   - 健保：公司負擔部分
   - 勞退：公司提繳部分

2. **人均成本計算**
   - 人均成本 = 總成本 / 平均員工人數

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| organizationId | UUID | ⬚ | - | 組織 ID | `org-uuid-001` |
| year | Integer | ✅ | - | 統計年度 | `2025` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "year": 2025,
    "organizationName": "OO科技股份有限公司",
    "summary": {
      "totalLaborCost": 72000000,
      "grossWage": 60000000,
      "laborInsurance": 5000000,
      "healthInsurance": 3000000,
      "pension": 3600000,
      "otherBenefits": 400000,
      "averageHeadcount": 145,
      "avgCostPerEmployee": 496552
    },
    "byMonth": [
      {
        "month": "2025-01",
        "headcount": 140,
        "grossWage": 5000000,
        "laborInsurance": 420000,
        "healthInsurance": 250000,
        "pension": 300000,
        "totalCost": 5970000
      }
    ],
    "byCategory": [
      { "category": "薪資", "amount": 60000000, "percentage": 83.33 },
      { "category": "勞保", "amount": 5000000, "percentage": 6.94 },
      { "category": "健保", "amount": 3000000, "percentage": 4.17 },
      { "category": "勞退", "amount": 3600000, "percentage": 5.0 },
      { "category": "其他福利", "amount": 400000, "percentage": 0.56 }
    ],
    "yearOverYear": {
      "previousYear": 65000000,
      "currentYear": 72000000,
      "growthRate": 10.77,
      "growthAmount": 7000000
    },
    "generatedAt": "2025-12-30T08:30:00Z"
  }
}
```

---

### 5.2 部門人力成本分析

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/finance/labor-cost-by-department` |
| Controller | `HR14FinanceQryController` |
| Service | `GetLaborCostByDepartmentServiceImpl` |
| 權限 | `report:finance:read` |
| 版本 | v1 |

**用途說明**

查詢按部門分類的人力成本分析報表，比較各部門人力成本、人均成本，用於預算規劃與成本控制。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| organizationId | UUID | ⬚ | - | 組織 ID | `org-uuid-001` |
| year | Integer | ✅ | - | 統計年度 | `2025` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "year": 2025,
    "organizationName": "OO科技股份有限公司",
    "totalLaborCost": 72000000,
    "departments": [
      {
        "departmentId": "dept-uuid-001",
        "departmentName": "研發部",
        "headcount": 50,
        "totalSalary": 36000000,
        "totalInsurance": 5400000,
        "totalPension": 2160000,
        "totalCost": 43560000,
        "percentage": 60.5,
        "avgCostPerEmployee": 871200
      },
      {
        "departmentId": "dept-uuid-002",
        "departmentName": "業務部",
        "headcount": 30,
        "totalSalary": 18000000,
        "totalInsurance": 2700000,
        "totalPension": 1080000,
        "totalCost": 21780000,
        "percentage": 30.25,
        "avgCostPerEmployee": 726000
      }
    ],
    "monthlyTrend": [
      {
        "month": "2025-01",
        "研發部": 3630000,
        "業務部": 1815000,
        "管理部": 525000
      }
    ],
    "generatedAt": "2025-12-30T08:30:00Z"
  }
}
```

---

### 5.3 薪資總表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/finance/payroll-summary` |
| Controller | `HR14FinanceQryController` |
| Service | `GetPayrollSummaryServiceImpl` |
| 權限 | `report:finance:read` |
| 版本 | v1 |

**用途說明**

查詢月度薪資總表，彙整當月全體員工薪資發放明細，供財務審核與撥款使用。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| organizationId | UUID | ⬚ | - | 組織 ID | `org-uuid-001` |
| month | String | ✅ | - | 薪資月份 | `2025-12` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "month": "2025-12",
    "organizationName": "OO科技股份有限公司",
    "payrollRunId": "payroll-uuid-001",
    "status": "APPROVED",
    "summary": {
      "totalEmployees": 150,
      "totalGrossPay": 6500000,
      "totalDeductions": 850000,
      "totalNetPay": 5650000,
      "totalEmployerCost": 980000
    },
    "breakdown": {
      "earnings": {
        "baseSalary": 5800000,
        "allowances": 400000,
        "overtimePay": 200000,
        "bonus": 100000
      },
      "deductions": {
        "laborInsurance": 300000,
        "healthInsurance": 180000,
        "pension": 260000,
        "incomeTax": 110000
      },
      "employerContributions": {
        "laborInsurance": 420000,
        "healthInsurance": 250000,
        "pension": 310000
      }
    },
    "byDepartment": [
      {
        "departmentName": "研發部",
        "employeeCount": 50,
        "grossPay": 3000000,
        "netPay": 2610000
      }
    ],
    "generatedAt": "2025-12-30T08:30:00Z"
  }
}
```

---

## 6. 儀表板 API

### 6.1 建立儀表板

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/dashboards` |
| Controller | `HR14DashboardCmdController` |
| Service | `CreateDashboardServiceImpl` |
| 權限 | `dashboard:create` |
| 版本 | v1 |

**用途說明**

建立客製化儀表板，使用者可自訂 Widget 配置，包含 KPI 卡片、折線圖、圓餅圖、表格等元件。

**業務邏輯**

1. **驗證 Widget 配置**
   - 檢查 widgetType 有效性
   - 檢查 dataSource 有效性
   - 檢查 position 不重疊

2. **建立儀表板**
   - 產生 UUID
   - 設定擁有者為當前使用者
   - 儲存 Widget 配置（JSONB）

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| dashboardName | String | ✅ | 1~100 字元 | 儀表板名稱 | `"高階主管儀表板"` |
| description | String | ⬚ | 最長 500 字元 | 說明 | `"CEO每日查看"` |
| isPublic | Boolean | ⬚ | - | 是否公開（預設 false） | `false` |
| widgets | Widget[] | ✅ | 至少 1 個 | Widget 配置 | `[...]` |

**Widget 物件結構**

| 欄位 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| widgetType | Enum | ✅ | Widget 類型 | `"KPI_CARD"` |
| title | String | ✅ | 標題 | `"在職人數"` |
| dataSource | String | ✅ | 資料來源 | `"employee_count"` |
| position | Position | ✅ | 位置配置 | `{"x":0,"y":0,"w":3,"h":2}` |
| refreshInterval | Integer | ⬚ | 刷新間隔（秒） | `3600` |
| style | Object | ⬚ | 樣式配置 | `{"icon":"users"}` |
| chartConfig | Object | ⬚ | 圖表配置 | `{"xAxis":"month"}` |

**widgetType 列舉值**

| 值 | 說明 |
|:---|:---|
| `KPI_CARD` | KPI 指標卡片 |
| `LINE_CHART` | 折線圖 |
| `BAR_CHART` | 長條圖 |
| `PIE_CHART` | 圓餅圖 |
| `TABLE` | 表格 |
| `GAUGE` | 儀表板 |

**dataSource 可用值**

| 值 | 說明 |
|:---|:---|
| `employee_count` | 在職人數 |
| `monthly_turnover_rate` | 月離職率 |
| `monthly_labor_cost` | 月度人力成本 |
| `headcount_by_department` | 部門人數分布 |
| `top_projects_by_cost` | 專案成本 TOP N |
| `utilization_rate` | 稼動率 |
| `attendance_summary` | 差勤摘要 |

**範例：**
```json
{
  "dashboardName": "高階主管儀表板",
  "description": "CEO每日經營數據",
  "isPublic": false,
  "widgets": [
    {
      "widgetType": "KPI_CARD",
      "title": "在職人數",
      "dataSource": "employee_count",
      "position": {"x": 0, "y": 0, "w": 3, "h": 2},
      "refreshInterval": 3600,
      "style": {"icon": "users", "color": "#0078D7"}
    },
    {
      "widgetType": "LINE_CHART",
      "title": "月度人力成本趨勢",
      "dataSource": "monthly_labor_cost",
      "position": {"x": 3, "y": 0, "w": 9, "h": 4},
      "refreshInterval": 86400,
      "chartConfig": {
        "xAxis": "month",
        "yAxis": "cost",
        "period": "last_12_months"
      }
    }
  ]
}
```

**Response**

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "儀表板建立成功",
  "data": {
    "dashboardId": "dashboard-uuid-001",
    "dashboardName": "高階主管儀表板",
    "widgetCount": 2,
    "createdAt": "2025-12-30T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_INVALID_WIDGET_TYPE | 無效的 Widget 類型 | 使用有效的 widgetType |
| 400 | VALIDATION_INVALID_DATA_SOURCE | 無效的資料來源 | 使用有效的 dataSource |
| 400 | VALIDATION_WIDGET_POSITION_OVERLAP | Widget 位置重疊 | 調整 position 配置 |

---

### 6.2 儀表板列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/dashboards` |
| Controller | `HR14DashboardQryController` |
| Service | `GetDashboardListServiceImpl` |
| 權限 | `dashboard:read` |
| 版本 | v1 |

**用途說明**

查詢使用者可存取的儀表板列表，包含自己建立的及公開的儀表板。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| includePublic | Boolean | ⬚ | true | 是否包含公開儀表板 | `true` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "dashboards": [
      {
        "dashboardId": "dashboard-uuid-001",
        "dashboardName": "高階主管儀表板",
        "description": "CEO每日經營數據",
        "isPublic": false,
        "isDefault": true,
        "widgetCount": 5,
        "ownerName": "系統管理員",
        "createdAt": "2025-01-15T10:00:00Z",
        "updatedAt": "2025-12-30T08:00:00Z"
      },
      {
        "dashboardId": "dashboard-uuid-002",
        "dashboardName": "HR營運儀表板",
        "description": "人資部門使用",
        "isPublic": true,
        "isDefault": false,
        "widgetCount": 8,
        "ownerName": "HR主管",
        "createdAt": "2025-03-01T10:00:00Z",
        "updatedAt": "2025-12-28T14:30:00Z"
      }
    ]
  }
}
```

---

### 6.3 儀表板詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/dashboards/{id}` |
| Controller | `HR14DashboardQryController` |
| Service | `GetDashboardDetailServiceImpl` |
| 權限 | `dashboard:read` |
| 版本 | v1 |

**用途說明**

查詢儀表板詳情，包含完整 Widget 配置及即時資料。系統會依據各 Widget 的 dataSource 查詢對應資料並返回。

**業務邏輯**

1. **權限檢查**
   - 確認使用者為擁有者或儀表板為公開

2. **查詢 Widget 資料**
   - 依據各 Widget 的 dataSource 查詢對應 API
   - 優先從 Redis 快取讀取
   - 若快取過期則重新查詢並更新快取

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 儀表板 ID | `dashboard-uuid-001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "dashboardId": "dashboard-uuid-001",
    "dashboardName": "高階主管儀表板",
    "description": "CEO每日經營數據",
    "layout": {
      "columns": 12,
      "rowHeight": 60
    },
    "widgets": [
      {
        "widgetId": "w1",
        "widgetType": "KPI_CARD",
        "title": "在職人數",
        "dataSource": "employee_count",
        "position": {"x": 0, "y": 0, "w": 3, "h": 2},
        "data": {
          "value": 150,
          "previousValue": 145,
          "change": 5,
          "changePercent": 3.45,
          "trend": "UP"
        },
        "lastRefreshed": "2025-12-30T08:00:00Z"
      },
      {
        "widgetId": "w2",
        "widgetType": "KPI_CARD",
        "title": "月離職率",
        "dataSource": "monthly_turnover_rate",
        "position": {"x": 3, "y": 0, "w": 3, "h": 2},
        "data": {
          "value": 1.33,
          "unit": "%",
          "threshold": {"warning": 2.0, "danger": 5.0},
          "status": "NORMAL"
        },
        "lastRefreshed": "2025-12-30T08:00:00Z"
      },
      {
        "widgetId": "w3",
        "widgetType": "LINE_CHART",
        "title": "月度人力成本趨勢",
        "dataSource": "monthly_labor_cost",
        "position": {"x": 0, "y": 2, "w": 6, "h": 4},
        "data": {
          "series": [
            {"month": "2025-01", "value": 5970000},
            {"month": "2025-02", "value": 6020000},
            {"month": "2025-03", "value": 6100000}
          ],
          "unit": "元"
        },
        "lastRefreshed": "2025-12-30T00:00:00Z"
      }
    ],
    "generatedAt": "2025-12-30T08:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 403 | AUTHZ_ACCESS_DENIED | 無權存取此儀表板 | 聯繫儀表板擁有者 |
| 404 | RESOURCE_DASHBOARD_NOT_FOUND | 儀表板不存在 | 確認儀表板 ID |

---

### 6.4 更新 Widget 配置

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/dashboards/{id}/widgets` |
| Controller | `HR14DashboardCmdController` |
| Service | `UpdateDashboardWidgetsServiceImpl` |
| 權限 | `dashboard:update` |
| 版本 | v1 |

**用途說明**

更新儀表板的 Widget 配置，支援新增、修改、刪除、調整位置等操作。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 儀表板 ID | `dashboard-uuid-001` |

**Request Body**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| widgets | Widget[] | ✅ | 完整的 Widget 配置（會取代現有配置） |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "Widget 配置更新成功",
  "data": {
    "dashboardId": "dashboard-uuid-001",
    "widgetCount": 5,
    "updatedAt": "2025-12-30T10:30:00Z"
  }
}
```

---

### 6.5 刪除儀表板

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `DELETE /api/v1/dashboards/{id}` |
| Controller | `HR14DashboardCmdController` |
| Service | `DeleteDashboardServiceImpl` |
| 權限 | `dashboard:delete` |
| 版本 | v1 |

**用途說明**

刪除儀表板。僅儀表板擁有者可刪除，預設儀表板不可刪除。

**業務邏輯**

1. **權限檢查**
   - 確認使用者為儀表板擁有者

2. **刪除限制**
   - isDefault = true 的儀表板不可刪除

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 儀表板 ID | `dashboard-uuid-001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "儀表板刪除成功",
  "data": {
    "dashboardId": "dashboard-uuid-001",
    "deletedAt": "2025-12-30T11:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 403 | AUTHZ_NOT_OWNER | 非儀表板擁有者 | 僅擁有者可刪除 |
| 404 | RESOURCE_DASHBOARD_NOT_FOUND | 儀表板不存在 | 確認儀表板 ID |
| 422 | BUSINESS_CANNOT_DELETE_DEFAULT | 預設儀表板不可刪除 | - |

---

## 7. 報表匯出 API

### 7.1 匯出 Excel

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/reports/export/excel` |
| Controller | `HR14ExportCmdController` |
| Service | `ExportExcelServiceImpl` |
| 權限 | `report:export` |
| 版本 | v1 |

**用途說明**

將報表資料匯出為 Excel 格式。匯出為非同步作業，系統會產生檔案後提供下載連結。

**業務邏輯**

1. **查詢報表資料**
   - 依據 reportType 和 filters 查詢對應資料

2. **產生 Excel**
   - 使用 Apache POI 產生 .xlsx 檔案
   - 自動套用格式（日期、金額、百分比）

3. **儲存檔案**
   - 儲存至暫存目錄
   - 設定過期時間（預設 24 小時）

4. **返回下載資訊**
   - 返回下載 URL 和過期時間

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| reportType | Enum | ✅ | 有效的報表類型 | 報表類型 | `"EMPLOYEE_ROSTER"` |
| filters | Object | ⬚ | - | 篩選條件 | `{"status":"ACTIVE"}` |
| columns | String[] | ⬚ | - | 指定匯出欄位 | `["employeeNumber","fullName"]` |
| fileName | String | ⬚ | 最長 100 字元 | 自訂檔名 | `"員工花名冊"` |

**reportType 列舉值**

| 值 | 說明 |
|:---|:---|
| `EMPLOYEE_ROSTER` | 員工花名冊 |
| `HEADCOUNT` | 人力盤點報表 |
| `ATTENDANCE_SUMMARY` | 差勤統計報表 |
| `TURNOVER_ANALYSIS` | 離職率分析 |
| `PROJECT_COST` | 專案成本分析 |
| `UTILIZATION_RATE` | 稼動率分析 |
| `LABOR_COST` | 人力成本分析 |
| `PAYROLL_SUMMARY` | 薪資總表 |

**範例：**
```json
{
  "reportType": "EMPLOYEE_ROSTER",
  "filters": {
    "organizationId": "org-uuid-001",
    "status": "ACTIVE"
  },
  "columns": ["employeeNumber", "fullName", "departmentName", "jobTitle", "hireDate"],
  "fileName": "在職員工花名冊_202512"
}
```

**Response**

**成功回應 (202 Accepted)**

```json
{
  "code": "SUCCESS",
  "message": "匯出作業已排程",
  "data": {
    "exportId": "export-uuid-001",
    "status": "PROCESSING",
    "estimatedTime": 30,
    "checkStatusUrl": "/api/v1/reports/export/export-uuid-001/status"
  }
}
```

**完成後回應**

```json
{
  "code": "SUCCESS",
  "message": "匯出完成",
  "data": {
    "exportId": "export-uuid-001",
    "status": "COMPLETED",
    "fileName": "在職員工花名冊_202512.xlsx",
    "fileSize": 102400,
    "downloadUrl": "/api/v1/reports/export/export-uuid-001/download",
    "expiresAt": "2025-12-31T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_INVALID_REPORT_TYPE | 無效的報表類型 | 使用有效的 reportType |
| 400 | VALIDATION_INVALID_FILTERS | 無效的篩選條件 | 檢查 filters 參數 |
| 422 | BUSINESS_DATA_TOO_LARGE | 資料量過大 | 縮小篩選範圍 |

---

### 7.2 匯出 PDF

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/reports/export/pdf` |
| Controller | `HR14ExportCmdController` |
| Service | `ExportPdfServiceImpl` |
| 權限 | `report:export` |
| 版本 | v1 |

**用途說明**

將報表資料匯出為 PDF 格式，適用於列印或正式文件用途。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| reportType | Enum | ✅ | 報表類型 | `"PAYROLL_SUMMARY"` |
| filters | Object | ⬚ | 篩選條件 | `{"month":"2025-12"}` |
| orientation | Enum | ⬚ | 紙張方向（預設 PORTRAIT） | `"LANDSCAPE"` |
| includeChart | Boolean | ⬚ | 是否包含圖表（預設 true） | `true` |

**Response**

**成功回應 (202 Accepted)**

與 Excel 匯出相同格式。

---

### 7.3 政府申報格式匯出

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/reports/export/government` |
| Controller | `HR14ExportCmdController` |
| Service | `ExportGovernmentFormatServiceImpl` |
| 權限 | `report:export:government` |
| 版本 | v1 |

**用途說明**

匯出符合政府機關要求的申報格式檔案，如勞動部加保申報、健保署投保申報等。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| formatType | Enum | ✅ | 申報格式類型 | `"LABOR_BUREAU_ENROLLMENT"` |
| month | String | ✅ | 申報月份 | `"2025-12"` |
| organizationId | UUID | ⬚ | 組織 ID | `"org-uuid-001"` |

**formatType 列舉值**

| 值 | 說明 | 格式 |
|:---|:---|:---|
| `LABOR_BUREAU_ENROLLMENT` | 勞保加退保申報 | CSV |
| `HEALTH_INSURANCE_ENROLLMENT` | 健保加退保申報 | CSV |
| `PENSION_CONTRIBUTION` | 勞退提繳申報 | CSV |
| `TAX_WITHHOLDING` | 扣繳憑單申報 | XML |

**Response**

**成功回應 (202 Accepted)**

```json
{
  "code": "SUCCESS",
  "message": "匯出作業已排程",
  "data": {
    "exportId": "export-uuid-002",
    "formatType": "LABOR_BUREAU_ENROLLMENT",
    "status": "PROCESSING"
  }
}
```

---

### 7.4 下載匯出檔案

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/reports/export/{id}/download` |
| Controller | `HR14ExportQryController` |
| Service | `DownloadExportFileServiceImpl` |
| 權限 | `report:export` |
| 版本 | v1 |

**用途說明**

下載已產生的匯出檔案。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 匯出作業 ID | `export-uuid-001` |

**Response**

**成功回應 (200 OK)**

```
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="員工花名冊_202512.xlsx"

(Binary file stream)
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_EXPORT_NOT_FOUND | 匯出作業不存在 | 確認 exportId |
| 410 | RESOURCE_EXPORT_EXPIRED | 匯出檔案已過期 | 重新匯出 |
| 422 | BUSINESS_EXPORT_NOT_READY | 匯出尚未完成 | 稍後再試 |

---

## 8. 錯誤碼定義

### 8.1 驗證錯誤 (4xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | VALIDATION_INVALID_DATE_RANGE | 日期區間無效 |
| 400 | VALIDATION_INVALID_REPORT_TYPE | 無效的報表類型 |
| 400 | VALIDATION_INVALID_FILTERS | 無效的篩選條件 |
| 400 | VALIDATION_INVALID_WIDGET_TYPE | 無效的 Widget 類型 |
| 400 | VALIDATION_INVALID_DATA_SOURCE | 無效的資料來源 |
| 400 | VALIDATION_WIDGET_POSITION_OVERLAP | Widget 位置重疊 |

### 8.2 授權錯誤 (4xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 |
| 403 | AUTHZ_PERMISSION_DENIED | 無報表查詢權限 |
| 403 | AUTHZ_ACCESS_DENIED | 無權存取此儀表板 |
| 403 | AUTHZ_NOT_OWNER | 非儀表板擁有者 |

### 8.3 資源錯誤 (4xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 404 | RESOURCE_PROJECT_NOT_FOUND | 專案不存在 |
| 404 | RESOURCE_DASHBOARD_NOT_FOUND | 儀表板不存在 |
| 404 | RESOURCE_EXPORT_NOT_FOUND | 匯出作業不存在 |
| 410 | RESOURCE_EXPORT_EXPIRED | 匯出檔案已過期 |

### 8.4 業務錯誤 (4xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 422 | BUSINESS_DATA_TOO_LARGE | 資料量過大 |
| 422 | BUSINESS_EXPORT_NOT_READY | 匯出尚未完成 |
| 422 | BUSINESS_CANNOT_DELETE_DEFAULT | 預設儀表板不可刪除 |

### 8.5 系統錯誤 (5xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 500 | SYSTEM_REPORT_GENERATION_ERROR | 報表產生失敗 |
| 500 | SYSTEM_EXPORT_ERROR | 匯出處理失敗 |
| 500 | SYSTEM_READMODEL_ERROR | 讀模型查詢失敗 |

---

## 9. 領域事件

### 9.1 事件訂閱（來源事件）

報表服務訂閱其他服務的領域事件，用於更新讀模型（ReadModel）：

| 來源事件 | 來源服務 | 更新 ReadModel |
|:---|:---|:---|
| EmployeeCreatedEvent | Organization | employee_report_view |
| EmployeeTerminatedEvent | Organization | employee_report_view, monthly_hr_stats |
| TimesheetApprovedEvent | Timesheet | project_cost_snapshots |
| PayrollRunCompletedEvent | Payroll | monthly_hr_stats |
| AttendanceRecordCreatedEvent | Attendance | monthly_hr_stats |

### 9.2 發布事件

| 事件名稱 | Topic | 觸發時機 | 說明 |
|:---|:---|:---|:---|
| ReportExportedEvent | `report.exported` | 報表匯出完成 | 通知使用者下載 |
| DashboardCreatedEvent | `report.dashboard.created` | 儀表板建立 | - |
| ReadModelRefreshedEvent | `report.readmodel.refreshed` | 讀模型刷新完成 | 內部使用 |

### 9.3 事件 Payload 範例

**ReportExportedEvent**

```json
{
  "eventId": "evt-rpt-001",
  "eventType": "ReportExportedEvent",
  "occurredAt": "2025-12-30T10:30:00Z",
  "aggregateId": "export-uuid-001",
  "aggregateType": "ReportExport",
  "payload": {
    "exportId": "export-uuid-001",
    "reportType": "EMPLOYEE_ROSTER",
    "format": "EXCEL",
    "requestedBy": "user-uuid-001",
    "fileName": "員工花名冊_202512.xlsx",
    "fileSize": 102400,
    "downloadUrl": "/api/v1/reports/export/export-uuid-001/download",
    "expiresAt": "2025-12-31T10:00:00Z"
  }
}
```

---

## 附錄 A：關鍵指標計算公式

| 指標 | 計算公式 | 說明 |
|:---|:---|:---|
| 離職率 | 離職人數 / 期初人數 × 100% | 衡量人員流動 |
| 稼動率 | 計費工時 / 總可用工時 × 100% | 衡量人力運用效率 |
| 專案獲利率 | (合約金額 - 實際成本) / 合約金額 × 100% | 衡量專案獲利能力 |
| 人均成本 | 總人力成本 / 員工人數 | 衡量人力成本效率 |
| 預算使用率 | 實際支出 / 預算金額 × 100% | 衡量預算控制 |

---

## 附錄 B：ReadModel 刷新排程

| Job 名稱 | 執行頻率 | 更新目標 |
|:---|:---|:---|
| RefreshEmployeeViewJob | 每小時 | employee_report_view |
| CalculateProjectCostJob | 每日 00:30 | project_cost_snapshots |
| CalculateMonthlyStatsJob | 每月 1 日 02:00 | monthly_hr_stats |
| RefreshUtilizationRateJob | 每日 01:00 | utilization_snapshots |

---

**文件建立日期:** 2025-12-30
**版本:** 1.0
