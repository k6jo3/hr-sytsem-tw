# 績效管理服務業務合約 (Performance Service Business Contract)

> **服務代碼:** HR08
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/08_績效管理服務系統設計書.md`
> - `knowledge/04_API_Specifications/08_績效管理服務系統設計書_API詳細規格.md`

---

## 📋 概述

本合約文件定義績效管理服務的**完整業務場景**，包括：
1. **Command 操作場景**（建立、更新、完成） - 驗證業務規則與領域事件
2. **Query 操作場景**（查詢） - 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景
- ✅ 新增 3 個領域事件的詳細定義
- ✅ 對應到實際的 API 端點
- ✅ 使用實際存在的欄位（移除 is_deleted，使用 status）
- ✅ 包含完整的業務規則驗證

**服務定位：**
績效管理服務負責考核週期管理、考核表單設計、績效評等記錄、目標管理等功能。本服務必須確保**公正透明的績效評估流程**，支援多層級評核（自評、主管評核、HR確認）。

**資料軟刪除策略：**
- **考核週期**: 使用 `status` 欄位，'PLANNING' 規劃中、'IN_PROGRESS' 進行中、'COMPLETED' 已完成、'CANCELLED' 已取消
- **考核記錄**: 使用 `status` 欄位，'DRAFT' 草稿、'IN_REVIEW' 評核中、'COMPLETED' 已完成、'WITHDRAWN' 已撤回
- **目標記錄**: 使用 `status` 欄位，'ACTIVE' 進行中、'ACHIEVED' 已達成、'CANCELLED' 已取消

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [考核週期管理 Command](#11-考核週期管理-command)
   - 1.2 [考核記錄管理 Command](#12-考核記錄管理-command)
   - 1.3 [目標管理 Command](#13-目標管理-command)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [考核週期查詢](#21-考核週期查詢)
   - 2.2 [考核記錄查詢](#22-考核記錄查詢)
   - 2.3 [目標查詢](#23-目標查詢)
   - 2.4 [考核表單查詢](#24-考核表單查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 考核週期管理 Command

#### PFM_CMD_001: 建立考核週期

**業務場景描述：**
HR 管理員為新年度建立考核週期，設定自評截止日、主管評核截止日、評分規則等，系統根據員工狀態自動判斷納入考核人員。

**API 端點：**
```
POST /api/v1/performance/cycles
```

**前置條件：**
- 執行者必須擁有 `performance:cycle:manage` 權限
- 年度不可已存在進行中的考核週期

**輸入 (Request)：**
```json
{
  "cycleName": "2026年度考核",
  "cycleType": "ANNUAL",
  "year": 2026,
  "startDate": "2026-01-01",
  "endDate": "2026-12-31",
  "selfEvalDeadline": "2026-01-15",
  "managerEvalDeadline": "2026-01-31",
  "hrReviewDeadline": "2026-02-14",
  "targetFormId": "form-001",
  "reviewFormId": "form-002",
  "hasDistributionRule": true,
  "distributionRule": {
    "gradeA": 0.10,
    "gradeB": 0.25,
    "gradeC": 0.50,
    "gradeD": 0.10,
    "gradeE": 0.05
  },
  "description": "2026 年度全員績效考核"
}
```

**業務規則驗證：**

1. ✅ **年度唯一性檢查**
   - 查詢條件：`year = ? AND status IN ('PLANNING', 'IN_PROGRESS')`
   - 預期結果：不存在進行中或規劃中的同年考核週期

2. ✅ **日期合理性檢查**
   - 規則：startDate < endDate，且自評截止日 < 主管評核截止日 < HR確認截止日
   - 預期結果：日期遞增且不為過去日期

3. ✅ **表單存在性檢查**
   - 查詢條件：`form_id = ? AND status = 'ACTIVE'`
   - 預期結果：目標表單和考核表單都存在

4. ✅ **強制分配規則驗證**
   - 規則：所有等第比例和 = 100%
   - 預期結果：gradeA + gradeB + gradeC + gradeD + gradeE = 1.0

**必須發布的領域事件：**
```json
{
  "eventId": "evt-pfm-cycle-001",
  "eventType": "PerformanceCycleStartedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "cycle-001",
  "aggregateType": "PerformanceCycle",
  "payload": {
    "cycleId": "cycle-001",
    "cycleName": "2026年度考核",
    "cycleType": "ANNUAL",
    "year": 2026,
    "startDate": "2026-01-01",
    "endDate": "2026-12-31",
    "selfEvalDeadline": "2026-01-15",
    "managerEvalDeadline": "2026-01-31",
    "hrReviewDeadline": "2026-02-14",
    "affectedEmployeeIds": ["E001", "E002", "E003"],
    "totalEmployees": 150,
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "cycleId": "cycle-001",
    "cycleName": "2026年度考核",
    "cycleType": "ANNUAL",
    "year": 2026,
    "status": "PLANNING",
    "startDate": "2026-01-01",
    "endDate": "2026-12-31",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

---

#### PFM_CMD_002: 啟動考核週期

**業務場景描述：**
HR 確認所有設定完成後啟動考核週期，系統通知所有員工開始進行自評，將週期狀態變更為進行中。

**API 端點：**
```
PUT /api/v1/performance/cycles/{id}/start
```

**前置條件：**
- 執行者必須擁有 `performance:cycle:manage` 權限
- 考核週期必須存在且狀態為 'PLANNING'

**輸入 (Request)：**
```json
{
  "startRemark": "開始2026年度績效考核"
}
```

**業務規則驗證：**

1. ✅ **週期狀態檢查**
   - 查詢條件：`cycle_id = ? AND status = 'PLANNING'`
   - 預期結果：週期存在且為規劃狀態

2. ✅ **表單和規則完整性檢查**
   - 查詢條件：檢驗目標表單、考核表單、分配規則是否已設定

**必須發布的領域事件：**
```json
{
  "eventId": "evt-pfm-cycle-start-001",
  "eventType": "PerformanceCycleStartedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "cycle-001",
  "aggregateType": "PerformanceCycle",
  "payload": {
    "cycleId": "cycle-001",
    "cycleName": "2026年度考核",
    "status": "IN_PROGRESS",
    "affectedEmployeeIds": ["E001", "E002", "E003"],
    "totalEmployees": 150,
    "selfEvalDeadline": "2026-01-15",
    "startedAt": "2026-02-09T10:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "cycleId": "cycle-001",
    "status": "IN_PROGRESS",
    "startedAt": "2026-02-09T10:00:00Z"
  }
}
```

---

### 1.2 考核記錄管理 Command

#### PFM_CMD_003: 提交考核記錄

**業務場景描述：**
員工或主管提交自評、主管評核或 HR 評等，系統驗證評分邏輯、更新考核狀態、並判斷是否進入下一階段。

**API 端點：**
```
PUT /api/v1/performance/reviews/{id}/submit
```

**前置條件：**
- 執行者必須擁有 `performance:review:manage` 權限或為評核人員
- 考核記錄必須存在且狀態為 'IN_REVIEW'
- 對應的考核週期必須為進行中

**輸入 (Request)：**
```json
{
  "reviewType": "MANAGER_REVIEW",
  "reviewerId": "M001",
  "reviewerName": "李組長",
  "overallScore": 3.9,
  "overallRating": "B",
  "evaluationItems": [
    {
      "itemId": "item-001",
      "itemName": "技術能力",
      "score": 4.0,
      "weight": 0.30,
      "comment": "React 能力佳，TypeScript 基礎扎實"
    },
    {
      "itemId": "item-002",
      "itemName": "團隊合作",
      "score": 3.8,
      "weight": 0.25,
      "comment": "積極參與團隊活動"
    }
  ],
  "strengths": ["技術能力強", "學習意願高"],
  "improvements": ["溝通表達需加強"],
  "submitDate": "2026-01-20"
}
```

**業務規則驗證：**

1. ✅ **考核記錄狀態檢查**
   - 查詢條件：`review_id = ? AND status IN ('DRAFT', 'IN_REVIEW')`
   - 預期結果：考核記錄存在且可編輯

2. ✅ **評分項目完整性檢查**
   - 規則：所有必填項目都需有評分
   - 預期結果：評估項目數 = 表單定義項目數

3. ✅ **評分範圍驗證**
   - 規則：每個項目評分 1.0 ~ 5.0
   - 預期結果：score >= 1.0 AND score <= 5.0

4. ✅ **加權平均計算驗證**
   - 規則：overallScore = SUM(score × weight)
   - 預期結果：計算結果與提交值一致

5. ✅ **評等轉換驗證**
   - 規則：根據 overallScore 自動轉換評等 (A/B/C/D/E)
   - overallScore >= 4.5 → 'A'
   - overallScore >= 4.0 → 'B'
   - overallScore >= 3.0 → 'C'
   - overallScore >= 2.0 → 'D'
   - overallScore < 2.0 → 'E'

6. ✅ **週期截止日檢查**
   - 查詢條件：根據 reviewType 檢查對應的截止日
   - 預期結果：submitDate <= 該評核階段的截止日

**必須發布的領域事件：**
```json
{
  "eventId": "evt-pfm-review-submit-001",
  "eventType": "PerformanceReviewSubmittedEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "review-001",
  "aggregateType": "PerformanceReview",
  "payload": {
    "reviewId": "review-001",
    "cycleId": "cycle-001",
    "cycleName": "2026年度考核",
    "employeeId": "E001",
    "employeeName": "王小華",
    "reviewerId": "M001",
    "reviewerName": "李組長",
    "reviewType": "MANAGER_REVIEW",
    "overallScore": 3.9,
    "overallRating": "B",
    "submittedAt": "2026-01-20T14:30:00Z",
    "nextStep": "HR_REVIEW"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "reviewId": "review-001",
    "status": "IN_REVIEW",
    "overallScore": 3.9,
    "overallRating": "B",
    "nextStep": "HR_REVIEW",
    "submittedAt": "2026-01-20T14:30:00Z"
  }
}
```

---

#### PFM_CMD_004: 確認最終評等

**業務場景描述：**
HR 管理員確認最終績效評等，系統計算調薪建議、發布完成事件通知 Payroll Service 進行調薪、通知員工評考結果。

**API 端點：**
```
PUT /api/v1/performance/reviews/{id}/finalize
```

**前置條件：**
- 執行者必須擁有 `performance:review:finalize` 權限
- 考核記錄必須已完成所有評核階段

**輸入 (Request)：**
```json
{
  "hrFinalScore": 3.9,
  "hrFinalRating": "B",
  "recommendedAdjustmentRate": 0.05,
  "hrComment": "綜合評核結果，建議5%調薪",
  "approvalDate": "2026-02-10"
}
```

**業務規則驗證：**

1. ✅ **考核完成度檢查**
   - 查詢條件：自評、主管評核、HR審核都已提交
   - 預期結果：所有必需評核階段都已完成

2. ✅ **評等合理性檢查**
   - 規則：HR最終評等不能超過主管評等 2 個等級以上
   - 預期結果：等級差異在合理範圍內

3. ✅ **強制分配規則應用**
   - 規則：如啟用強制分配，檢查是否符合分配比例
   - 預期結果：全公司評等分布符合設定比例（需統計驗證）

4. ✅ **調薪建議計算**
   - 規則：根據最終評等計算調薪百分比
   - A 級 10%、B 級 5%、C 級 2%、D 級 0%、E 級 -3%

**必須發布的領域事件：**
```json
{
  "eventId": "evt-pfm-completed-001",
  "eventType": "PerformanceReviewCompletedEvent",
  "timestamp": "2026-02-09T12:00:00Z",
  "aggregateId": "review-001",
  "aggregateType": "PerformanceReview",
  "payload": {
    "reviewId": "review-001",
    "cycleId": "cycle-001",
    "cycleName": "2026年度考核",
    "employeeId": "E001",
    "employeeName": "王小華",
    "employeeNumber": "E0001",
    "departmentId": "D001",
    "departmentName": "研發部",
    "selfEvalScore": 4.1,
    "managerEvalScore": 3.9,
    "finalScore": 3.9,
    "finalRating": "B",
    "evaluationPeriod": {
      "startDate": "2026-01-01",
      "endDate": "2026-12-31"
    },
    "recommendedAdjustmentRate": 0.05,
    "completedAt": "2026-02-10T09:00:00Z",
    "reviewSummary": {
      "strengths": ["技術能力強", "學習意願高"],
      "improvements": ["溝通表達需加強"]
    }
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "reviewId": "review-001",
    "status": "COMPLETED",
    "finalScore": 3.9,
    "finalRating": "B",
    "recommendedAdjustmentRate": 0.05,
    "finalizedAt": "2026-02-10T09:00:00Z"
  }
}
```

---

### 1.3 目標管理 Command

#### PFM_CMD_005: 建立員工目標

**業務場景描述：**
主管為直屬員工建立年度目標，定義目標內容、權重、達成條件，員工確認後進度追蹤。

**API 端點：**
```
POST /api/v1/performance/goals
```

**前置條件：**
- 執行者必須擁有 `performance:goal:manage` 權限或為員工主管
- 員工必須存在且狀態為 ACTIVE
- 考核週期必須為進行中

**輸入 (Request)：**
```json
{
  "cycleId": "cycle-001",
  "employeeId": "E001",
  "departmentId": "D001",
  "goalName": "完成 React 升級專案",
  "goalDescription": "將現有系統升級至 React 18.x 並優化性能",
  "goalCategory": "PROJECT",
  "targetValue": 100,
  "unit": "%",
  "weight": 0.30,
  "dueDate": "2026-06-30",
  "successCriteria": "升級完成，性能提升 20% 以上",
  "keyResults": [
    {
      "resultId": "kr-001",
      "description": "完成依賴升級",
      "weight": 0.3,
      "targetValue": 100
    },
    {
      "resultId": "kr-002",
      "description": "測試覆蓋率達 80%",
      "weight": 0.4,
      "targetValue": 80
    },
    {
      "resultId": "kr-003",
      "description": "性能指標改善",
      "weight": 0.3,
      "targetValue": 20
    }
  ]
}
```

**業務規則驗證：**

1. ✅ **員工存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

2. ✅ **主管權限檢查**
   - 查詢條件：檢查執行者是否為該員工的直屬主管
   - 預期結果：有權限設定該員工目標

3. ✅ **週期有效性檢查**
   - 查詢條件：`cycle_id = ? AND status = 'IN_PROGRESS'`
   - 預期結果：考核週期為進行中

4. ✅ **權重驗證**
   - 規則：所有目標權重和 <= 100%
   - 預期結果：weight <= 1.0

5. ✅ **期限合理性檢查**
   - 規則：dueDate 不早於當日，不晚於週期結束日
   - 預期結果：today <= dueDate <= cycleEndDate

**必須發布的領域事件：**
```json
{
  "eventId": "evt-pfm-goal-001",
  "eventType": "PerformanceGoalCreatedEvent",
  "timestamp": "2026-02-09T13:00:00Z",
  "aggregateId": "goal-001",
  "aggregateType": "PerformanceGoal",
  "payload": {
    "goalId": "goal-001",
    "cycleId": "cycle-001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "goalName": "完成 React 升級專案",
    "goalCategory": "PROJECT",
    "weight": 0.30,
    "dueDate": "2026-06-30",
    "createdAt": "2026-02-09T13:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "goalId": "goal-001",
    "employeeId": "E001",
    "goalName": "完成 React 升級專案",
    "status": "ACTIVE",
    "weight": 0.30,
    "createdAt": "2026-02-09T13:00:00Z"
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 考核週期查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PFM_QRY_C001 | 查詢進行中週期 | HR | `GET /api/v1/performance/cycles` | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'` |
| PFM_QRY_C002 | 查詢已完成週期 | HR | `GET /api/v1/performance/cycles` | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| PFM_QRY_C003 | 依年度查詢週期 | HR | `GET /api/v1/performance/cycles` | `{"year":2026}` | `year = 2026` |
| PFM_QRY_C004 | 依類型查詢週期 | HR | `GET /api/v1/performance/cycles` | `{"cycleType":"ANNUAL"}` | `cycle_type = 'ANNUAL'` |
| PFM_QRY_C005 | 查詢規劃中週期 | HR | `GET /api/v1/performance/cycles` | `{"status":"PLANNING"}` | `status = 'PLANNING'` |

#### 2.1.2 業務場景說明

**PFM_QRY_C001: 查詢進行中週期**

- **使用者：** HR 專員
- **業務目的：** 查看目前正在進行的考核週期，了解進度
- **權限控制：** `performance:cycle:read`
- **過濾邏輯：**
  ```sql
  WHERE status = 'IN_PROGRESS'
  ORDER BY start_date DESC
  ```

---

### 2.2 考核記錄查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PFM_QRY_R001 | 查詢員工考核紀錄 | HR | `GET /api/v1/performance/reviews` | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| PFM_QRY_R002 | 查詢週期考核紀錄 | HR | `GET /api/v1/performance/reviews` | `{"cycleId":"C001"}` | `cycle_id = 'C001'` |
| PFM_QRY_R003 | 查詢待自評紀錄 | HR | `GET /api/v1/performance/reviews` | `{"stage":"SELF_REVIEW"}` | `stage = 'SELF_REVIEW'`, `status = 'DRAFT'` |
| PFM_QRY_R004 | 查詢待主管評核 | MANAGER | `GET /api/v1/performance/reviews` | `{"stage":"MANAGER_REVIEW"}` | `stage = 'MANAGER_REVIEW'`, `reviewer_id = '{currentUserId}'`, `status = 'IN_REVIEW'` |
| PFM_QRY_R005 | 員工查詢自己考核 | EMPLOYEE | `GET /api/v1/performance/my` | `{}` | `employee_id = '{currentUserId}'` |
| PFM_QRY_R006 | 主管查詢下屬考核 | MANAGER | `GET /api/v1/performance/team` | `{}` | `employee.department_id IN ('{managedDeptIds}')` |
| PFM_QRY_R007 | 查詢已完成考核 | HR | `GET /api/v1/performance/reviews` | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| PFM_QRY_R008 | 依等第查詢 | HR | `GET /api/v1/performance/reviews` | `{"rating":"A"}` | `final_rating = 'A'` |

#### 2.2.2 業務場景說明

**PFM_QRY_R005: 員工查詢自己考核（ESS）**

- **使用者：** 一般員工
- **業務目的：** 員工自助查詢自己的績效考核結果
- **權限控制：** 無需特殊權限，但只能查詢自己
- **過濾邏輯：**
  ```sql
  WHERE employee_id = '{currentUserId}'
  ORDER BY cycle_id DESC
  ```

**PFM_QRY_R006: 主管查詢下屬考核**

- **使用者：** 部門主管
- **業務目的：** 主管查看所管轄部門員工的考核進度
- **權限控制：** 自動過濾為直屬下屬
- **過濾邏輯：**
  ```sql
  WHERE employee.department_id IN (managedDeptIds)
  ORDER BY employee_id, cycle_id DESC
  ```

---

### 2.3 目標查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PFM_QRY_G001 | 查詢員工目標 | MANAGER | `GET /api/v1/performance/goals` | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `status = 'ACTIVE'` |
| PFM_QRY_G002 | 查詢進行中目標 | MANAGER | `GET /api/v1/performance/goals` | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'` |
| PFM_QRY_G003 | 查詢已達成目標 | MANAGER | `GET /api/v1/performance/goals` | `{"status":"ACHIEVED"}` | `status = 'ACHIEVED'` |
| PFM_QRY_G004 | 員工查詢自己目標 | EMPLOYEE | `GET /api/v1/performance/my/goals` | `{}` | `employee_id = '{currentUserId}'`, `status != 'CANCELLED'` |
| PFM_QRY_G005 | 依週期查詢目標 | HR | `GET /api/v1/performance/goals` | `{"cycleId":"C001"}` | `cycle_id = 'C001'` |
| PFM_QRY_G006 | 查詢部門目標 | MANAGER | `GET /api/v1/performance/goals` | `{"deptId":"D001"}` | `department_id = 'D001'`, `status != 'CANCELLED'` |

---

### 2.4 考核表單查詢

#### 2.4.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PFM_QRY_F001 | 查詢啟用的表單 | HR | `GET /api/v1/performance/forms` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| PFM_QRY_F002 | 依類型查詢表單 | HR | `GET /api/v1/performance/forms` | `{"formType":"ANNUAL"}` | `form_type = 'ANNUAL'` |
| PFM_QRY_F003 | 依職等查詢表單 | HR | `GET /api/v1/performance/forms` | `{"applicableGrades":"M1"}` | `applicable_grades LIKE '%M1%'` |
| PFM_QRY_F004 | 查詢預設表單 | HR | `GET /api/v1/performance/forms` | `{"isDefault":true}` | `is_default = 1` |

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `PerformanceCycleStartedEvent` | 啟動考核週期 | Performance | Notification | 通知所有員工開始自評 |
| `PerformanceReviewSubmittedEvent` | 提交考核評估 | Performance | Notification | 通知下一階段評核者 |
| `PerformanceReviewCompletedEvent` | 確認最終評等 | Performance | Payroll, Notification, Reporting | 觸發調薪計算、發送通知、更新統計 |

---

### 3.2 PerformanceCycleStartedEvent (考核週期啟動事件)

**觸發時機：**
HR 管理員確認所有設定完成後啟動考核週期，系統生成此事件通知所有相關員工。

**Event Payload:**
```json
{
  "eventId": "evt-pfm-cycle-start-001",
  "eventType": "PerformanceCycleStartedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "cycle-001",
  "aggregateType": "PerformanceCycle",
  "payload": {
    "cycleId": "cycle-001",
    "cycleName": "2026年度考核",
    "cycleType": "ANNUAL",
    "year": 2026,
    "startDate": "2026-01-01",
    "endDate": "2026-12-31",
    "selfEvalDeadline": "2026-01-15",
    "managerEvalDeadline": "2026-01-31",
    "hrReviewDeadline": "2026-02-14",
    "affectedEmployeeIds": ["E001", "E002", "E003", "E004", "E005"],
    "totalEmployees": 150,
    "startedAt": "2026-02-09T10:00:00Z"
  },
  "metadata": {
    "userId": "hr-admin-001",
    "userName": "HR Administrator",
    "source": "Performance Service",
    "version": "1.0"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 批次發送 Email 通知所有員工開始自評
  - 範本：PERFORMANCE_CYCLE_STARTED
  - 包含：週期名稱、自評截止日、提交位置

---

### 3.3 PerformanceReviewSubmittedEvent (考核提交事件)

**觸發時機：**
員工或主管提交考核評估後發布此事件，通知下一階段的評核者。

**Event Payload:**
```json
{
  "eventId": "evt-pfm-review-submit-001",
  "eventType": "PerformanceReviewSubmittedEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "review-001",
  "aggregateType": "PerformanceReview",
  "payload": {
    "reviewId": "review-001",
    "cycleId": "cycle-001",
    "cycleName": "2026年度考核",
    "employeeId": "E001",
    "employeeName": "王小華",
    "reviewerId": "M001",
    "reviewerName": "李組長",
    "reviewType": "MANAGER_REVIEW",
    "overallScore": 3.9,
    "overallRating": "B",
    "submittedAt": "2026-01-20T14:30:00Z",
    "nextStep": "HR_REVIEW",
    "nextStepDeadline": "2026-02-14"
  },
  "metadata": {
    "userId": "reviewer-001",
    "userName": "李組長",
    "source": "Performance Service",
    "version": "1.0"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 根據 nextStep 發送通知給下一階段評核者（HR）
  - 範本：PERFORMANCE_REVIEW_AWAITING_NEXT_STEP
  - 包含：員工名稱、當前評等、截止日期

---

### 3.4 PerformanceReviewCompletedEvent (考核完成事件)

**觸發時機：**
HR 管理員確認最終績效評等後發布此事件，觸發調薪計算、通知、統計等後續作業。

**Event Payload:**
```json
{
  "eventId": "evt-pfm-completed-001",
  "eventType": "PerformanceReviewCompletedEvent",
  "timestamp": "2026-02-09T12:00:00Z",
  "aggregateId": "review-001",
  "aggregateType": "PerformanceReview",
  "payload": {
    "reviewId": "review-001",
    "cycleId": "cycle-001",
    "cycleName": "2026年度考核",
    "employeeId": "E001",
    "employeeName": "王小華",
    "employeeNumber": "E0001",
    "departmentId": "D001",
    "departmentName": "研發部",
    "jobTitle": "資深前端工程師",
    "selfEvalScore": 4.1,
    "selfEvalRating": "B",
    "managerEvalScore": 3.9,
    "managerEvalRating": "B",
    "finalScore": 3.9,
    "finalRating": "B",
    "evaluationPeriod": {
      "startDate": "2026-01-01",
      "endDate": "2026-12-31"
    },
    "recommendedAdjustmentRate": 0.05,
    "completedAt": "2026-02-10T09:00:00Z",
    "reviewSummary": {
      "strengths": ["技術能力強", "學習意願高", "團隊協作佳"],
      "improvements": ["溝通表達需加強", "主動承擔任務可再加強"],
      "futureGrowthAreas": ["架構設計", "技術領導力"]
    }
  },
  "metadata": {
    "userId": "hr-admin-001",
    "userName": "HR Administrator",
    "source": "Performance Service",
    "version": "1.0"
  }
}
```

**訂閱服務處理：**

- **Payroll Service:**
  - 根據 recommendedAdjustmentRate 建立薪資調整建議
  - 計算生效日期（通常為下月或次月 1 日）
  - 記錄調薪原因：「依據 2026年度考核績效評等」

- **Notification Service:**
  - 發送 Email 通知員工績效評考結果
  - 範本：PERFORMANCE_REVIEW_COMPLETED
  - 包含：最終評等、調薪建議、評核摘要

- **Reporting Service:**
  - 更新績效統計報表（全公司評等分布、部門評等分布）
  - 記錄進度指標（完成率、平均評分、評等分布）

---

## 4. 測試斷言規格

### 4.1 Command 操作測試斷言

**測試目標：** 驗證 Command 操作是否正確執行業務規則並發布領域事件。

**範例：PFM_CMD_001 建立考核週期測試**

```java
@Test
@DisplayName("PFM_CMD_001: 建立考核週期 - 應驗證年度唯一性並正確設定狀態")
void createCycle_ShouldValidateYearUniquenessAndCreateWithPlanningStatus() {
    // Given
    var request = CreateCycleRequest.builder()
        .cycleName("2026年度考核")
        .cycleType("ANNUAL")
        .year(2026)
        .startDate(LocalDate.of(2026, 1, 1))
        .endDate(LocalDate.of(2026, 12, 31))
        .selfEvalDeadline(LocalDate.of(2026, 1, 15))
        .managerEvalDeadline(LocalDate.of(2026, 1, 31))
        .hrReviewDeadline(LocalDate.of(2026, 2, 14))
        .targetFormId("form-001")
        .reviewFormId("form-002")
        .build();

    // Mock no existing cycle for year 2026
    when(cycleRepository.findByYearAndStatus(2026, List.of("PLANNING", "IN_PROGRESS")))
        .thenReturn(List.of());

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify cycle saved
    var captor = ArgumentCaptor.forClass(PerformanceCycle.class);
    verify(cycleRepository).save(captor.capture());

    var savedCycle = captor.getValue();
    assertThat(savedCycle.getStatus()).isEqualTo("PLANNING");
    assertThat(savedCycle.getYear()).isEqualTo(2026);
    assertThat(savedCycle.getCycleName()).isEqualTo("2026年度考核");

    // Then - Verify event published
    var eventCaptor = ArgumentCaptor.forClass(PerformanceCycleStartedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("PerformanceCycleStartedEvent");
    assertThat(event.getPayload().getYear()).isEqualTo(2026);
}
```

---

### 4.2 Query 操作測試斷言

**測試目標：** 驗證 Query 操作是否正確套用過濾條件與權限控制。

**範例：PFM_QRY_R005 員工查詢自己考核測試**

```java
@Test
@DisplayName("PFM_QRY_R005: 員工查詢自己考核 - 應自動過濾為當前員工")
void getMyReviews_ShouldAutoFilterByCurrentUser() {
    // Given
    String contractSpec = loadContractSpec("performance");
    JWTModel currentUser = createJWTModel("E001", "EMPLOYEE");

    var request = MyReviewsRequest.builder()
        .build();

    // When
    var captor = ArgumentCaptor.forClass(QueryGroup.class);
    service.getResponse(request, currentUser);

    // Then
    verify(reviewRepository).findPage(captor.capture(), any());

    var queryGroup = captor.getValue();
    assertContract(queryGroup, contractSpec, "PFM_QRY_R005");

    // Additional assertions
    assertThat(queryGroup).containsFilter("employee_id", Operator.EQUAL, "E001");
}
```

---

### 4.3 Integration Test 斷言

**測試目標：** 驗證完整的 API → Service → Repository 流程。

**範例：PFM_CMD_003 提交考核整合測試**

```java
@Test
@DisplayName("PFM_CMD_003: 提交考核記錄整合測試 - 應更新評分並返回正確回應")
void submitReview_Integration_ShouldUpdateReviewAndReturnResponse() throws Exception {
    // Given
    var request = SubmitReviewRequest.builder()
        .reviewType("MANAGER_REVIEW")
        .reviewerId("M001")
        .overallScore(3.9)
        .overallRating("B")
        .evaluationItems(List.of(
            EvaluationItem.builder()
                .itemId("item-001")
                .score(4.0)
                .weight(0.30)
                .build()
        ))
        .submitDate(LocalDate.now())
        .build();

    // When
    var result = mockMvc.perform(put("/api/v1/performance/reviews/{id}/submit", "review-001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.overallRating").value("B"))
        .andReturn();

    // Then - Verify database
    var review = reviewRepository.findById("review-001");
    assertThat(review).isPresent();
    assertThat(review.get().getOverallScore()).isEqualTo(3.9);
    assertThat(review.get().getOverallRating()).isEqualTo("B");
}
```

---

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 考核週期使用 `status` 欄位（'PLANNING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'）
   - 考核記錄使用 `status` 欄位（'DRAFT', 'IN_REVIEW', 'COMPLETED', 'WITHDRAWN'）
   - 目標使用 `status` 欄位（'ACTIVE', 'ACHIEVED', 'CANCELLED'）
   - **不使用 `is_deleted` 欄位**

2. **個人資料保護:**
   - 員工只能查詢自己的考核資訊
   - 主管只能查詢下屬的考核資訊
   - HR 可查詢全公司考核資訊

3. **租戶隔離:**
   - 所有查詢自動加上 `tenant_id = ?` 過濾條件

---

### 5.2 評分計算規則

**加權平均計算：**
```
overallScore = SUM(評估項目評分 × 該項目權重) / SUM(所有項目權重)
```

**評等轉換標準：**
- overallScore >= 4.5 → 'A' (傑出)
- overallScore >= 4.0 → 'B' (優良)
- overallScore >= 3.0 → 'C' (稱職)
- overallScore >= 2.0 → 'D' (待改進)
- overallScore < 2.0 → 'E' (不適任)

**調薪建議百分比：**
- A 級 → 10% 調薪
- B 級 → 5% 調薪
- C 級 → 2% 調薪
- D 級 → 0% 不調薪
- E 級 → -3% 凍結或減薪

---

### 5.3 角色權限說明

| 角色 | 可執行操作 | 可查詢範圍 |
|:---|:---|:---|
| **HR** | 建立週期、啟動週期、確認最終評等、管理表單 | 全公司考核資訊 |
| **MANAGER** | 提交主管評核、建立員工目標、查詢評核進度 | 直屬下屬考核資訊 |
| **EMPLOYEE** | 提交自評、查詢自己結果、查詢自己目標 | 僅限自己的考核資訊 |

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景、業務規則驗證、Domain Events Payload 定義、測試斷言規格 |
| 1.0 | 2025-12-19 | 精簡版建立 |
