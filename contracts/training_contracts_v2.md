# 訓練發展服務業務合約 (Training Service Business Contract)

> **服務代碼:** HR10
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/10_訓練管理服務系統設計書.md`

---

## 📋 概述

本合約文件定義訓練發展服務的**完整業務場景**，包括：
1. **Command 操作場景**（建立、更新、完成）- 驗證業務規則與領域事件
2. **Query 操作場景**（查詢）- 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景
- ✅ 新增 4 個領域事件的詳細定義
- ✅ 對應到實際的 API 端點
- ✅ 使用實際存在的欄位（status 欄位）
- ✅ 包含完整的業務規則驗證

**服務定位：**
訓練發展服務負責課程管理、員工報名、訓練完成記錄、證照管理、訓練時數統計等功能。本服務支援內訓與外訓的完整生命週期管理，並與 Organization Service、Notification Service 整合。

**資料軟刪除策略：**
- **課程**: 使用 `status` 欄位，'OPEN' 為可報名，'CLOSED' 為已結束，'CANCELLED' 為取消
- **報名記錄**: 使用 `status` 欄位，'PENDING' 為待審核，'APPROVED' 為已核准，'ATTENDED' 為已出席，'COMPLETED' 為已完成
- **證照**: 使用 `status` 欄位，'ACTIVE' 為有效，'EXPIRED' 為已過期，'REVOKED' 為已撤銷
- **歷史記錄**: 不進行軟刪除，保留所有歷史記錄（用於訓練時數統計）

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [課程管理 Command](#11-課程管理-command)
   - 1.2 [報名管理 Command](#12-報名管理-command)
   - 1.3 [證照管理 Command](#13-證照管理-command)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [課程查詢](#21-課程查詢)
   - 2.2 [報名記錄查詢](#22-報名記錄查詢)
   - 2.3 [證照查詢](#23-證照查詢)
   - 2.4 [訓練時數查詢](#24-訓練時數查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 課程管理 Command

#### TRN_CMD_001: 建立課程

**業務場景描述：**
HR 專員或訓練負責人建立新的訓練課程，設定課程名稱、講師、日期、名額等資訊。

**API 端點：**
```
POST /api/v1/training/courses
```

**前置條件：**
- 執行者必須擁有 `training:course:manage` 權限
- 講師（instructorId）必須存在於 Organization Service

**輸入 (Request)：**
```json
{
  "courseName": "React 進階開發",
  "courseCode": "TRN-2026-001",
  "category": "TECHNICAL",
  "instructorId": "emp-001",
  "trainingType": "INTERNAL",
  "startDate": "2026-03-01",
  "endDate": "2026-03-02",
  "hours": 16,
  "maxParticipants": 30,
  "cost": 5000,
  "description": "深入學習 React Hooks、Redux、效能優化",
  "location": "訓練教室 A"
}
```

**業務規則驗證：**

1. ✅ **課程代碼唯一性檢查**
   - 查詢條件：`course_code = ?`
   - 預期結果：不存在重複

2. ✅ **講師存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

3. ✅ **日期合理性檢查**
   - 規則：結束日期不可早於開始日期
   - 規則：開始日期不可為過去日期

4. ✅ **訓練時數合理性檢查**
   - 規則：訓練時數必須 > 0

5. ✅ **名額合理性檢查**
   - 規則：最大名額必須 >= 1

**必須發布的領域事件：**
```json
{
  "eventType": "CourseCreatedEvent",
  "aggregateId": "course-001",
  "timestamp": "2026-02-09T09:00:00Z",
  "payload": {
    "courseId": "course-001",
    "courseName": "React 進階開發",
    "courseCode": "TRN-2026-001",
    "instructorId": "emp-001",
    "instructorName": "李老師",
    "startDate": "2026-03-01",
    "endDate": "2026-03-02",
    "hours": 16,
    "maxParticipants": 30,
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "courseId": "course-001",
    "courseName": "React 進階開發",
    "courseCode": "TRN-2026-001",
    "status": "OPEN",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

---

#### TRN_CMD_002: 關閉課程

**業務場景描述：**
課程結束後，HR 專員關閉課程，不再接受新的報名。

**API 端點：**
```
PUT /api/v1/training/courses/{id}/close
```

**前置條件：**
- 執行者必須擁有 `training:course:manage` 權限
- 課程必須存在且狀態為 OPEN

**輸入 (Request)：**
```json
{
  "reason": "課程已結束"
}
```

**業務規則驗證：**

1. ✅ **課程存在性檢查**
   - 查詢條件：`course_id = ? AND status = 'OPEN'`
   - 預期結果：課程存在且為 OPEN 狀態

2. ✅ **關閉原因必填檢查**
   - 規則：reason 不可為空

**必須發布的領域事件：**
```json
{
  "eventType": "CourseClosedEvent",
  "aggregateId": "course-001",
  "timestamp": "2026-03-03T18:00:00Z",
  "payload": {
    "courseId": "course-001",
    "courseName": "React 進階開發",
    "closedAt": "2026-03-03T18:00:00Z",
    "reason": "課程已結束",
    "totalEnrollments": 25
  }
}
```

---

### 1.2 報名管理 Command

#### TRN_CMD_003: 員工報名課程

**業務場景描述：**
員工自行報名訓練課程，系統建立報名記錄並通知主管審核。

**API 端點：**
```
POST /api/v1/training/enrollments
```

**前置條件：**
- 執行者為一般員工（可報名自己）
- 課程必須存在且為 OPEN 狀態

**輸入 (Request)：**
```json
{
  "courseId": "course-001",
  "employeeId": "emp-002",
  "reason": "提升前端技術能力"
}
```

**業務規則驗證：**

1. ✅ **課程存在性與狀態檢查**
   - 查詢條件：`course_id = ? AND status = 'OPEN'`
   - 預期結果：課程存在且為 OPEN 狀態

2. ✅ **報名截止日期檢查**
   - 查詢條件：`course_id = ? AND enroll_end_date >= CURRENT_DATE`
   - 預期結果：尚未超過報名截止日

3. ✅ **重複報名檢查**
   - 查詢條件：`course_id = ? AND employee_id = ? AND status IN ('PENDING', 'APPROVED', 'ATTENDED', 'COMPLETED')`
   - 預期結果：不存在有效的報名記錄

4. ✅ **名額檢查**
   - 查詢條件：`SELECT COUNT(*) FROM training_enrollments WHERE course_id = ? AND status IN ('APPROVED', 'ATTENDED', 'COMPLETED')`
   - 規則：已核准人數 < 最大名額

5. ✅ **員工存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

**必須發布的領域事件：**
```json
{
  "eventType": "EnrollmentCreatedEvent",
  "aggregateId": "enroll-001",
  "timestamp": "2026-02-10T10:00:00Z",
  "payload": {
    "enrollmentId": "enroll-001",
    "courseId": "course-001",
    "courseName": "React 進階開發",
    "employeeId": "emp-002",
    "employeeName": "張三",
    "managerId": "emp-003",
    "managerName": "李組長",
    "trainingHours": 16,
    "cost": 5000,
    "reason": "提升前端技術能力",
    "status": "PENDING",
    "createdAt": "2026-02-10T10:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "enrollmentId": "enroll-001",
    "courseId": "course-001",
    "status": "PENDING",
    "createdAt": "2026-02-10T10:00:00Z"
  }
}
```

---

#### TRN_CMD_004: 審核報名

**業務場景描述：**
主管審核員工的訓練報名申請，決定核准或駁回。

**API 端點：**
```
PUT /api/v1/training/enrollments/{id}/approve
```

**前置條件：**
- 執行者必須為申請人的主管或擁有 `training:enrollment:approve` 權限
- 報名記錄必須存在且狀態為 PENDING

**輸入 (Request)：**
```json
{
  "approved": true,
  "comment": "同意參加訓練"
}
```

**業務規則驗證：**

1. ✅ **報名記錄狀態檢查**
   - 查詢條件：`enrollment_id = ? AND status = 'PENDING'`
   - 預期結果：報名記錄存在且為 PENDING 狀態

2. ✅ **審核權限檢查**
   - 規則：執行者為申請人的主管或 HR
   - 查詢條件：呼叫 Organization Service 驗證主管關係

3. ✅ **名額再次檢查（核准時）**
   - 查詢條件：`SELECT COUNT(*) FROM training_enrollments WHERE course_id = ? AND status IN ('APPROVED', 'ATTENDED', 'COMPLETED')`
   - 規則：已核准人數 < 最大名額

**必須發布的領域事件：**
```json
{
  "eventType": "EnrollmentApprovedEvent",
  "aggregateId": "enroll-001",
  "timestamp": "2026-02-10T14:00:00Z",
  "payload": {
    "enrollmentId": "enroll-001",
    "courseId": "course-001",
    "courseName": "React 進階開發",
    "employeeId": "emp-002",
    "employeeName": "張三",
    "employeeEmail": "zhang@company.com",
    "approverId": "emp-003",
    "approverName": "李組長",
    "approved": true,
    "comment": "同意參加訓練",
    "approvedAt": "2026-02-10T14:00:00Z"
  }
}
```

---

#### TRN_CMD_005: 完成訓練

**業務場景描述：**
訓練結束後，講師或 HR 專員標記員工完成訓練，系統記錄訓練時數並發布事件供 Reporting Service 更新統計。

**API 端點：**
```
PUT /api/v1/training/enrollments/{id}/complete
```

**前置條件：**
- 執行者必須擁有 `training:enrollment:manage` 權限
- 報名記錄必須存在且狀態為 APPROVED 或 ATTENDED

**輸入 (Request)：**
```json
{
  "completedDate": "2026-03-02",
  "completedHours": 16,
  "score": 85,
  "passed": true,
  "comment": "表現優異"
}
```

**業務規則驗證：**

1. ✅ **報名記錄狀態檢查**
   - 查詢條件：`enrollment_id = ? AND status IN ('APPROVED', 'ATTENDED')`
   - 預期結果：報名記錄存在且為 APPROVED 或 ATTENDED 狀態

2. ✅ **完成日期合理性檢查**
   - 規則：完成日期不可早於課程開始日期
   - 規則：完成日期不可晚於課程結束日期後 7 天

3. ✅ **訓練時數合理性檢查**
   - 規則：完成時數必須 > 0 且 <= 課程總時數

4. ✅ **成績合理性檢查**
   - 規則：成績範圍 0 ~ 100

**必須發布的領域事件：**
```json
{
  "eventType": "TrainingCompletedEvent",
  "aggregateId": "enroll-001",
  "timestamp": "2026-03-02T17:00:00Z",
  "payload": {
    "enrollmentId": "enroll-001",
    "employeeId": "emp-002",
    "employeeName": "張三",
    "courseId": "course-001",
    "courseName": "React 進階開發",
    "completedHours": 16,
    "completedDate": "2026-03-02",
    "score": 85,
    "passed": true,
    "comment": "表現優異"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "enrollmentId": "enroll-001",
    "status": "COMPLETED",
    "completedHours": 16,
    "score": 85,
    "updatedAt": "2026-03-02T17:00:00Z"
  }
}
```

---

### 1.3 證照管理 Command

#### TRN_CMD_006: 新增證照

**業務場景描述：**
員工或 HR 專員新增員工取得的證照記錄。

**API 端點：**
```
POST /api/v1/training/certificates
```

**前置條件：**
- 執行者為證照持有人本人或擁有 `training:certificate:manage` 權限
- employeeId 必須存在於 Organization Service

**輸入 (Request)：**
```json
{
  "employeeId": "emp-002",
  "certificateName": "AWS 架構師證照",
  "issuingOrganization": "Amazon",
  "certificateNumber": "AWS-2026-12345",
  "issueDate": "2026-01-15",
  "expiryDate": "2029-01-15",
  "isRequired": true,
  "attachmentUrl": "https://s3.amazonaws.com/certificates/aws-cert.pdf"
}
```

**業務規則驗證：**

1. ✅ **員工存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

2. ✅ **證照編號唯一性檢查**
   - 查詢條件：`certificate_number = ?`
   - 預期結果：不存在重複

3. ✅ **日期合理性檢查**
   - 規則：到期日必須晚於發證日
   - 規則：發證日不可為未來日期

4. ✅ **證照名稱必填檢查**
   - 規則：certificateName 不可為空

**必須發布的領域事件：**
```json
{
  "eventType": "CertificateAddedEvent",
  "aggregateId": "cert-001",
  "timestamp": "2026-02-09T11:00:00Z",
  "payload": {
    "certificateId": "cert-001",
    "employeeId": "emp-002",
    "employeeName": "張三",
    "certificateName": "AWS 架構師證照",
    "issuingOrganization": "Amazon",
    "issueDate": "2026-01-15",
    "expiryDate": "2029-01-15",
    "isRequired": true,
    "createdAt": "2026-02-09T11:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "certificateId": "cert-001",
    "certificateName": "AWS 架構師證照",
    "status": "ACTIVE",
    "createdAt": "2026-02-09T11:00:00Z"
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 課程查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TRN_QRY_001 | 查詢可報名課程 | EMPLOYEE | `GET /api/v1/training/courses` | `{"status":"OPEN"}` | `status = 'OPEN'`, `enroll_end_date >= CURRENT_DATE` |
| TRN_QRY_002 | 查詢已結束課程 | HR | `GET /api/v1/training/courses` | `{"status":"CLOSED"}` | `status = 'CLOSED'` |
| TRN_QRY_003 | 依類別查詢課程 | EMPLOYEE | `GET /api/v1/training/courses` | `{"category":"TECHNICAL"}` | `category = 'TECHNICAL'`, `status = 'OPEN'` |
| TRN_QRY_004 | 依講師查詢課程 | HR | `GET /api/v1/training/courses` | `{"instructorId":"emp-001"}` | `instructor_id = 'emp-001'` |
| TRN_QRY_005 | 依日期範圍查詢課程 | HR | `GET /api/v1/training/courses` | `{"startDate":"2026-01-01","endDate":"2026-12-31"}` | `start_date >= '2026-01-01'`, `end_date <= '2026-12-31'` |

#### 2.1.2 業務場景說明

**TRN_QRY_001: 查詢可報名課程**

- **使用者：** 一般員工
- **業務目的：** 查詢目前開放報名的訓練課程
- **權限控制：** 無需特殊權限
- **過濾邏輯：**
  ```sql
  WHERE status = 'OPEN'
    AND enroll_end_date >= CURRENT_DATE
  ORDER BY start_date ASC
  ```

**TRN_QRY_003: 依類別查詢課程**

- **使用者：** 一般員工
- **業務目的：** 查詢特定類別（技術、管理、語言等）的課程
- **權限控制：** 無需特殊權限
- **過濾邏輯：**
  ```sql
  WHERE category = 'TECHNICAL'
    AND status = 'OPEN'
  ORDER BY start_date ASC
  ```

---

### 2.2 報名記錄查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TRN_QRY_006 | 員工查詢已報名課程 | EMPLOYEE | `GET /api/v1/training/enrollments/my` | `{}` | `employee_id = '{currentUserId}'` |
| TRN_QRY_007 | 查詢課程學員 | HR | `GET /api/v1/training/enrollments` | `{"courseId":"course-001"}` | `course_id = 'course-001'` |
| TRN_QRY_008 | 查詢待審核報名 | MANAGER | `GET /api/v1/training/enrollments/pending` | `{}` | `approver_id = '{currentUserId}'`, `status = 'PENDING'` |
| TRN_QRY_009 | 查詢已完成訓練 | EMPLOYEE | `GET /api/v1/training/enrollments/my` | `{"status":"COMPLETED"}` | `employee_id = '{currentUserId}'`, `status = 'COMPLETED'` |

#### 2.2.2 業務場景說明

**TRN_QRY_006: 員工查詢已報名課程**

- **使用者：** 一般員工
- **業務目的：** 員工自助查詢自己的報名記錄
- **權限控制：** 無需特殊權限，但只能查詢自己
- **過濾邏輯：**
  ```sql
  WHERE employee_id = '{currentUserId}'
  ORDER BY created_at DESC
  ```

**TRN_QRY_008: 查詢待審核報名**

- **使用者：** 主管
- **業務目的：** 查詢需要自己審核的報名申請
- **權限控制：** 只能查詢自己為審核者的記錄
- **過濾邏輯：**
  ```sql
  WHERE approver_id = '{currentUserId}'
    AND status = 'PENDING'
  ORDER BY created_at ASC
  ```

---

### 2.3 證照查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TRN_QRY_010 | 查詢員工證照 | HR | `GET /api/v1/training/certificates` | `{"employeeId":"emp-002"}` | `employee_id = 'emp-002'`, `status = 'ACTIVE'` |
| TRN_QRY_011 | 查詢即將到期證照 | HR | `GET /api/v1/training/certificates/expiring` | `{"daysBeforeExpiry":30}` | `status = 'ACTIVE'`, `expiry_date <= DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY)`, `expiry_date >= CURRENT_DATE` |
| TRN_QRY_012 | 員工查詢自己證照 | EMPLOYEE | `GET /api/v1/training/certificates/my` | `{}` | `employee_id = '{currentUserId}'`, `status = 'ACTIVE'` |
| TRN_QRY_013 | 查詢必要證照 | HR | `GET /api/v1/training/certificates` | `{"isRequired":true}` | `is_required = TRUE`, `status = 'ACTIVE'` |

---

### 2.4 訓練時數查詢

#### 2.4.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TRN_QRY_014 | 查詢員工訓練時數 | EMPLOYEE | `GET /api/v1/training/my/hours` | `{"year":"2026"}` | `employee_id = '{currentUserId}'`, `YEAR(completed_date) = 2026`, `status = 'COMPLETED'` |
| TRN_QRY_015 | 查詢部門訓練時數 | HR | `GET /api/v1/training/hours/department` | `{"departmentId":"dept-001","year":"2026"}` | `department_id = 'dept-001'`, `YEAR(completed_date) = 2026`, `status = 'COMPLETED'` |
| TRN_QRY_016 | 查詢組織訓練統計 | HR | `GET /api/v1/training/hours/statistics` | `{"year":"2026"}` | `YEAR(completed_date) = 2026`, `status = 'COMPLETED'` |

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `EnrollmentCreatedEvent` | 員工報名課程 | Training | Notification | 通知主管審核 |
| `EnrollmentApprovedEvent` | 主管審核通過 | Training | Notification | 通知員工報名成功 |
| `TrainingCompletedEvent` | 員工完成訓練 | Training | Reporting | 更新訓練時數統計 |
| `CertificateExpiringEvent` | 證照即將到期 | Training | Notification | 提醒員工更新證照 |

---

### 3.2 EnrollmentCreatedEvent (報名建立事件)

**觸發時機：**
員工報名課程後，系統建立報名記錄並發布此事件，通知主管審核。

**Event Payload:**
```json
{
  "eventId": "evt-trn-enroll-001",
  "eventType": "EnrollmentCreatedEvent",
  "timestamp": "2026-02-10T10:00:00Z",
  "aggregateId": "enroll-001",
  "aggregateType": "TrainingEnrollment",
  "payload": {
    "enrollmentId": "enroll-001",
    "courseId": "course-001",
    "courseName": "React 進階開發",
    "employeeId": "emp-002",
    "employeeName": "張三",
    "employeeEmail": "zhang@company.com",
    "managerId": "emp-003",
    "managerName": "李組長",
    "managerEmail": "lee@company.com",
    "trainingHours": 16,
    "cost": 5000,
    "reason": "提升前端技術能力",
    "status": "PENDING",
    "createdAt": "2026-02-10T10:00:00Z"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送 Email 給主管：「您有一筆訓練報名需要審核」
  - 發送站內通知給主管

---

### 3.3 EnrollmentApprovedEvent (報名核准事件)

**觸發時機：**
主管審核報名申請通過後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-trn-approve-001",
  "eventType": "EnrollmentApprovedEvent",
  "timestamp": "2026-02-10T14:00:00Z",
  "aggregateId": "enroll-001",
  "aggregateType": "TrainingEnrollment",
  "payload": {
    "enrollmentId": "enroll-001",
    "courseId": "course-001",
    "courseName": "React 進階開發",
    "employeeId": "emp-002",
    "employeeName": "張三",
    "employeeEmail": "zhang@company.com",
    "approverId": "emp-003",
    "approverName": "李組長",
    "approved": true,
    "comment": "同意參加訓練",
    "approvedAt": "2026-02-10T14:00:00Z",
    "courseStartDate": "2026-03-01",
    "location": "訓練教室 A"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送 Email 給員工：「您的訓練報名已核准」
  - 發送站內通知給員工
  - 在課程開始前 3 天發送提醒通知

---

### 3.4 TrainingCompletedEvent (訓練完成事件)

**觸發時機：**
訓練結束後，講師或 HR 專員標記員工完成訓練時發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-trn-complete-001",
  "eventType": "TrainingCompletedEvent",
  "timestamp": "2026-03-02T17:00:00Z",
  "aggregateId": "enroll-001",
  "aggregateType": "TrainingEnrollment",
  "payload": {
    "enrollmentId": "enroll-001",
    "employeeId": "emp-002",
    "employeeName": "張三",
    "departmentId": "dept-001",
    "departmentName": "研發部",
    "courseId": "course-001",
    "courseName": "React 進階開發",
    "category": "TECHNICAL",
    "completedHours": 16,
    "completedDate": "2026-03-02",
    "score": 85,
    "passed": true,
    "comment": "表現優異"
  }
}
```

**訂閱服務處理：**

- **Reporting Service:**
  - 更新員工訓練時數統計
  - 更新部門訓練時數統計
  - 更新組織整體訓練統計

---

### 3.5 CertificateExpiringEvent (證照到期提醒事件)

**觸發時機：**
系統排程每日檢查證照，發現距離到期日少於 30 天時發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-trn-cert-expiring-001",
  "eventType": "CertificateExpiringEvent",
  "timestamp": "2026-02-09T08:00:00Z",
  "aggregateId": "cert-002",
  "aggregateType": "Certificate",
  "payload": {
    "certificateId": "cert-002",
    "employeeId": "emp-002",
    "employeeName": "張三",
    "employeeEmail": "zhang@company.com",
    "certificateName": "AWS 架構師證照",
    "issuingOrganization": "Amazon",
    "issueDate": "2023-01-15",
    "expiryDate": "2026-03-10",
    "daysUntilExpiry": 30,
    "isRequired": true
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送 Email 給員工：「您的證照即將到期，請儘快更新」
  - 如果是必要證照，同時通知主管和 HR

---

## 4. 測試斷言規格

### 4.1 Command 操作測試斷言

**測試目標：** 驗證 Command 操作是否正確執行業務規則並發布領域事件。

**測試方法：**

1. **業務規則驗證**
   - 使用 Mock Repository 驗證查詢條件
   - 使用 ArgumentCaptor 捕獲儲存的 Entity
   - 斷言 Entity 狀態符合業務規則

2. **領域事件驗證**
   - 使用 Mock EventPublisher 驗證事件發布
   - 斷言事件類型、Payload 內容正確
   - 驗證事件時序（先儲存後發布）

**範例：TRN_CMD_003 報名測試**

```java
@Test
@DisplayName("TRN_CMD_003: 員工報名課程 - 應建立報名記錄並發布事件")
void enrollCourse_ShouldCreateEnrollmentAndPublishEvent() {
    // Given
    var request = EnrollCourseRequest.builder()
        .courseId("course-001")
        .employeeId("emp-002")
        .reason("提升前端技術能力")
        .build();

    // Mock course exists and open
    when(courseRepository.findById("course-001"))
        .thenReturn(Optional.of(createOpenCourse()));

    // Mock no duplicate enrollment
    when(enrollmentRepository.findByEmployeeAndCourse("emp-002", "course-001"))
        .thenReturn(Optional.empty());

    // Mock available slots
    when(enrollmentRepository.countApprovedEnrollments("course-001"))
        .thenReturn(20);

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify enrollment saved
    var captor = ArgumentCaptor.forClass(TrainingEnrollment.class);
    verify(enrollmentRepository).save(captor.capture());

    var enrollment = captor.getValue();
    assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
    assertThat(enrollment.getReason()).isEqualTo("提升前端技術能力");

    // Then - Verify event published
    var eventCaptor = ArgumentCaptor.forClass(EnrollmentCreatedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("EnrollmentCreatedEvent");
    assertThat(event.getPayload().getEmployeeId()).isEqualTo("emp-002");
    assertThat(event.getPayload().getStatus()).isEqualTo("PENDING");
}
```

---

### 4.2 Query 操作測試斷言

**測試目標：** 驗證 Query 操作是否正確套用過濾條件與權限控制。

**範例：TRN_QRY_001 查詢測試**

```java
@Test
@DisplayName("TRN_QRY_001: 查詢可報名課程 - 應包含狀態與截止日期過濾")
void searchCourses_OpenStatus_ShouldIncludeRequiredFilters() {
    // Given
    String contractSpec = loadContractSpec("training");

    var request = CourseSearchRequest.builder()
        .status("OPEN")
        .build();

    // When
    var captor = ArgumentCaptor.forClass(QueryGroup.class);
    service.getResponse(request, currentUser);

    // Then
    verify(courseRepository).findPage(captor.capture(), any());

    var queryGroup = captor.getValue();
    assertContract(queryGroup, contractSpec, "TRN_QRY_001");

    // Additional assertions
    assertThat(queryGroup).containsFilter("status", Operator.EQUAL, "OPEN");
    assertThat(queryGroup).containsFilter("enroll_end_date", Operator.GREATER_THAN_OR_EQUAL, LocalDate.now());
}
```

---

### 4.3 Integration Test 斷言

**範例：TRN_CMD_003 整合測試**

```java
@Test
@DisplayName("TRN_CMD_003: 員工報名課程整合測試")
void enrollCourse_Integration_ShouldCreateRecordAndReturnResponse() throws Exception {
    // Given
    var request = EnrollCourseRequest.builder()
        .courseId("course-001")
        .employeeId("emp-002")
        .reason("提升前端技術能力")
        .build();

    // When
    var result = mockMvc.perform(post("/api/v1/training/enrollments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.status").value("PENDING"))
        .andReturn();

    // Then - Verify database
    var enrollment = enrollmentRepository.findById(extractId(result));
    assertThat(enrollment).isPresent();
    assertThat(enrollment.get().getStatus()).isEqualTo(EnrollmentStatus.PENDING);
}
```

---

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 課程使用 `status` 欄位（'OPEN', 'CLOSED', 'CANCELLED'）
   - 報名記錄使用 `status` 欄位（'PENDING', 'APPROVED', 'ATTENDED', 'COMPLETED', 'REJECTED'）
   - 證照使用 `status` 欄位（'ACTIVE', 'EXPIRED', 'REVOKED'）
   - **不使用 `is_deleted` 欄位**

2. **個人資料保護:**
   - 員工只能查詢自己的報名記錄和證照
   - 主管只能查詢需要自己審核的報名

3. **租戶隔離:**
   - 所有查詢自動加上 `tenant_id = ?` 過濾條件

---

### 5.2 證照到期檢查排程

- **執行頻率：** 每日 08:00 執行
- **檢查範圍：** 距離到期日 30 天內的有效證照
- **通知對象：** 證照持有人、主管（如果是必要證照）、HR

---

### 5.3 訓練時數計算規則

- **有效訓練時數：** 只計算狀態為 COMPLETED 的報名記錄
- **統計維度：** 員工、部門、組織、年度、課程類別
- **使用 CQRS Read Model：** Reporting Service 訂閱 TrainingCompletedEvent 更新統計

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景、業務規則驗證、Domain Events Payload 定義、測試斷言規格 |
| 1.0 | 2026-02-06 | 精簡版建立 |
