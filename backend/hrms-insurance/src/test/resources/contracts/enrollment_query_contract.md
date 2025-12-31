# HR05 Insurance Enrollment API Contract

## Overview
保險加退保查詢 API 合約定義

---

## API: GET /api/v1/insurance/enrollments/active

### 說明
查詢員工目前有效的加保記錄（狀態為 ACTIVE）

### 前端使用
- **頁面:** HR05-P07 我的保險資訊頁面
- **事件ID:** E-MY-01
- **觸發時機:** 頁面載入 (onMount)

### Request
```
GET /api/v1/insurance/enrollments/active?employeeId={employeeId}
```

#### Query Parameters
| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| employeeId | String | ✅ | 員工ID |

### Response
```json
[
  {
    "enrollmentId": "uuid-string",
    "employeeId": "EMP001",
    "insuranceType": "LABOR",
    "insuranceTypeDisplay": "勞保",
    "status": "ACTIVE",
    "statusDisplay": "已加保",
    "enrollDate": "2025-01-01",
    "withdrawDate": null,
    "monthlySalary": 48200.00
  },
  {
    "enrollmentId": "uuid-string",
    "employeeId": "EMP001",
    "insuranceType": "HEALTH",
    "insuranceTypeDisplay": "健保",
    "status": "ACTIVE",
    "statusDisplay": "已加保",
    "enrollDate": "2025-01-01",
    "withdrawDate": null,
    "monthlySalary": 48200.00
  },
  {
    "enrollmentId": "uuid-string",
    "employeeId": "EMP001",
    "insuranceType": "PENSION",
    "insuranceTypeDisplay": "勞退",
    "status": "ACTIVE",
    "statusDisplay": "已加保",
    "enrollDate": "2025-01-01",
    "withdrawDate": null,
    "monthlySalary": 48200.00
  }
]
```

---

## Query Contract Tests

### EnrollmentQueryContract

#### searchByEmployeeId_ShouldIncludeEmployeeIdFilter
- **Given:** employeeId = "EMP001"
- **Expected Filter:** `employeeId EQ EMP001`

#### searchActive_ShouldIncludeStatusFilter
- **Given:** status = "ACTIVE"
- **Expected Filter:** `status EQ ACTIVE`

#### searchByEmployeeIdAndActive_ShouldIncludeBothFilters
- **Given:** employeeId = "EMP001", status = "ACTIVE"
- **Expected Filters:** 
  - `employeeId EQ EMP001`
  - `status EQ ACTIVE`
