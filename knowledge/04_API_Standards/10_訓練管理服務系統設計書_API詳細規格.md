# HR10 訓練管理服務 API 詳細規格

**版本:** 1.0
**建立日期:** 2025-12-30
**最後更新:** 2025-12-30
**Domain代號:** 10 (TRN)
**服務名稱:** hrms-training

---

## 目錄

1. [API 總覽](#1-api-總覽)
2. [課程管理 API](#2-課程管理-api)
3. [報名管理 API](#3-報名管理-api)
4. [證照管理 API](#4-證照管理-api)
5. [訓練統計 API](#5-訓練統計-api)
6. [錯誤碼定義](#6-錯誤碼定義)
7. [領域事件](#7-領域事件)

---

## 1. API 總覽

### 1.1 端點清單

| # | 端點 | 方法 | 說明 | Controller |
|:---:|:---|:---:|:---|:---|
| 1 | `/api/v1/training/courses` | POST | 建立課程 | HR10CourseCmdController |
| 2 | `/api/v1/training/courses` | GET | 查詢課程列表 | HR10CourseQryController |
| 3 | `/api/v1/training/courses/{id}` | GET | 查詢課程詳情 | HR10CourseQryController |
| 4 | `/api/v1/training/courses/{id}` | PUT | 更新課程 | HR10CourseCmdController |
| 5 | `/api/v1/training/courses/{id}/publish` | PUT | 發布課程 | HR10CourseCmdController |
| 6 | `/api/v1/training/courses/{id}/close` | PUT | 關閉報名 | HR10CourseCmdController |
| 7 | `/api/v1/training/courses/{id}/complete` | PUT | 完成課程 | HR10CourseCmdController |
| 8 | `/api/v1/training/enrollments` | POST | 報名課程 | HR10EnrollmentCmdController |
| 9 | `/api/v1/training/enrollments` | GET | 查詢報名列表 | HR10EnrollmentQryController |
| 10 | `/api/v1/training/enrollments/{id}/approve` | PUT | 審核通過 | HR10EnrollmentCmdController |
| 11 | `/api/v1/training/enrollments/{id}/reject` | PUT | 審核拒絕 | HR10EnrollmentCmdController |
| 12 | `/api/v1/training/enrollments/{id}/cancel` | PUT | 取消報名 | HR10EnrollmentCmdController |
| 13 | `/api/v1/training/enrollments/{id}/attendance` | PUT | 確認出席 | HR10EnrollmentCmdController |
| 14 | `/api/v1/training/enrollments/{id}/complete` | PUT | 完成訓練 | HR10EnrollmentCmdController |
| 15 | `/api/v1/training/my` | GET | 我的訓練 | HR10EnrollmentQryController |
| 16 | `/api/v1/training/certificates` | POST | 新增證照 | HR10CertificateCmdController |
| 17 | `/api/v1/training/certificates` | GET | 查詢證照列表 | HR10CertificateQryController |
| 18 | `/api/v1/training/certificates/{id}` | GET | 查詢證照詳情 | HR10CertificateQryController |
| 19 | `/api/v1/training/certificates/{id}` | PUT | 更新證照 | HR10CertificateCmdController |
| 20 | `/api/v1/training/certificates/{id}` | DELETE | 刪除證照 | HR10CertificateCmdController |
| 21 | `/api/v1/training/certificates/expiring` | GET | 即將到期證照 | HR10CertificateQryController |
| 22 | `/api/v1/training/my/hours` | GET | 我的訓練時數 | HR10ReportQryController |
| 23 | `/api/v1/training/statistics` | GET | 訓練統計報表 | HR10ReportQryController |
| 24 | `/api/v1/training/statistics/export` | GET | 匯出統計報表 | HR10ReportQryController |

### 1.2 報名狀態流程

```
REGISTERED (已報名)
     ↓
  ┌──┴──┐
  ↓     ↓
APPROVED  REJECTED (已拒絕)
(已核准)
  ↓
ATTENDED (已出席)
  ↓
  ├── COMPLETED (已完成)
  └── NO_SHOW (未到)
```

### 1.3 課程狀態流程

```
DRAFT (草稿) → OPEN (開放報名) → CLOSED (報名截止) → COMPLETED (已結束)
                                       ↓
                               CANCELLED (已取消)
```

### 1.4 通用 Headers

| Header | 必填 | 說明 |
|:---|:---:|:---|
| `Authorization` | ✅ | Bearer Token |
| `X-Tenant-Id` | ✅ | 租戶識別碼 |
| `Content-Type` | ✅ | application/json |

---

## 2. 課程管理 API

### 2.1 建立課程

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/training/courses` |
| **方法** | POST |
| **Controller** | HR10CourseCmdController |
| **Service** | CreateCourseServiceImpl |
| **權限** | `training:course:create` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 或訓練單位建立新的訓練課程 |
| **使用者** | HR 人員、訓練管理員 |
| **前置條件** | 已取得年度訓練計畫核准 |

#### 業務邏輯

```
1. 驗證規則
   ├── 課程名稱必填且不超過 255 字元
   ├── 課程代碼唯一
   ├── 訓練時數 > 0
   ├── 開始日期 <= 結束日期
   └── 最大人數 > 0（若設定）

2. 處理步驟
   ├── 產生課程代碼（若未指定）
   ├── 建立課程記錄，狀態為 DRAFT
   └── 發布 CourseCreatedEvent

3. 課程代碼規則
   └── TRN-{YYYYMM}-{序號} (e.g., TRN-202512-001)
```

#### Request Body

```json
{
  "courseCode": "TRN-202512-001",
  "courseName": "React 進階開發實戰",
  "courseType": "INTERNAL",
  "deliveryMode": "OFFLINE",
  "category": "TECHNICAL",
  "description": "本課程將深入介紹 React Hooks、Redux Toolkit 等進階技術...",
  "instructor": "李講師",
  "instructorInfo": {
    "name": "李講師",
    "title": "資深前端工程師",
    "company": "內部講師"
  },
  "durationHours": 8,
  "maxParticipants": 30,
  "minParticipants": 10,
  "startDate": "2025-12-15",
  "endDate": "2025-12-15",
  "startTime": "09:00",
  "endTime": "18:00",
  "location": "台北辦公室 5F 訓練教室",
  "cost": 0,
  "isMandatory": false,
  "targetAudience": ["RD", "QA"],
  "prerequisites": "具備 React 基礎知識",
  "enrollmentDeadline": "2025-12-10"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `courseCode` | String | ❌ | 唯一，最大 50 字 | 課程代碼（可自動產生） |
| `courseName` | String | ✅ | 長度 1-255 | 課程名稱 |
| `courseType` | Enum | ✅ | INTERNAL/EXTERNAL | 課程類型 |
| `deliveryMode` | Enum | ✅ | ONLINE/OFFLINE/HYBRID | 授課方式 |
| `category` | Enum | ❌ | 見類別列表 | 課程類別 |
| `description` | String | ❌ | 最大 2000 字 | 課程說明 |
| `instructor` | String | ❌ | 最大 100 字 | 講師姓名 |
| `instructorInfo` | Object | ❌ | - | 講師詳細資訊 |
| `durationHours` | Decimal | ✅ | > 0 | 訓練時數 |
| `maxParticipants` | Integer | ❌ | > 0 | 最大人數 |
| `minParticipants` | Integer | ❌ | > 0 | 最小開課人數 |
| `startDate` | Date | ✅ | 不早於今天 | 開始日期 |
| `endDate` | Date | ✅ | >= startDate | 結束日期 |
| `startTime` | Time | ❌ | HH:mm | 開始時間 |
| `endTime` | Time | ❌ | HH:mm | 結束時間 |
| `location` | String | ❌ | 最大 255 字 | 上課地點 |
| `cost` | Decimal | ❌ | >= 0 | 課程費用 |
| `isMandatory` | Boolean | ❌ | - | 是否必修 |
| `targetAudience` | Array | ❌ | - | 目標對象（部門代碼） |
| `prerequisites` | String | ❌ | 最大 500 字 | 先修條件 |
| `enrollmentDeadline` | Date | ❌ | < startDate | 報名截止日 |

**課程類型 (courseType):**
- `INTERNAL` - 內訓（公司內部講師）
- `EXTERNAL` - 外訓（外部機構）

**授課方式 (deliveryMode):**
- `ONLINE` - 線上課程
- `OFFLINE` - 實體課程
- `HYBRID` - 混合式

**課程類別 (category):**
- `TECHNICAL` - 技術類
- `MANAGEMENT` - 管理類
- `SOFT_SKILL` - 軟技能
- `COMPLIANCE` - 法規遵循
- `ORIENTATION` - 新人訓練
- `SAFETY` - 安全衛生
- `OTHER` - 其他

#### Response Body

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "課程建立成功",
  "data": {
    "courseId": "course-uuid-001",
    "courseCode": "TRN-202512-001",
    "courseName": "React 進階開發實戰",
    "status": "DRAFT",
    "createdAt": "2025-12-01T10:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `TRN_COURSE_NAME_REQUIRED` | 課程名稱必填 | 請填寫課程名稱 |
| 400 | `TRN_COURSE_CODE_EXISTS` | 課程代碼已存在 | 請使用其他代碼 |
| 400 | `TRN_INVALID_DATE_RANGE` | 日期範圍無效 | 結束日期需 >= 開始日期 |
| 400 | `TRN_INVALID_HOURS` | 訓練時數無效 | 時數需 > 0 |

#### 領域事件

**CourseCreatedEvent**

```json
{
  "eventType": "CourseCreated",
  "aggregateId": "course-uuid-001",
  "aggregateType": "TrainingCourse",
  "payload": {
    "courseId": "course-uuid-001",
    "courseCode": "TRN-202512-001",
    "courseName": "React 進階開發實戰",
    "courseType": "INTERNAL",
    "durationHours": 8,
    "startDate": "2025-12-15",
    "createdBy": "user-uuid"
  }
}
```

| Topic | 訂閱服務 |
|:---|:---|
| `training.course.created` | - |

---

### 2.2 查詢課程列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/courses` |
| **方法** | GET |
| **Controller** | HR10CourseQryController |
| **Service** | GetCoursesServiceImpl |
| **權限** | `training:course:read` |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `status` | Enum | ❌ | 課程狀態篩選 |
| `courseType` | Enum | ❌ | 課程類型 |
| `category` | Enum | ❌ | 課程類別 |
| `keyword` | String | ❌ | 關鍵字搜尋（名稱、代碼） |
| `dateFrom` | Date | ❌ | 開始日期起 |
| `dateTo` | Date | ❌ | 開始日期迄 |
| `enrollable` | Boolean | ❌ | 僅顯示可報名課程 |
| `page` | Integer | ❌ | 頁碼（預設 1） |
| `size` | Integer | ❌ | 每頁筆數（預設 20） |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "items": [
      {
        "courseId": "course-uuid-001",
        "courseCode": "TRN-202512-001",
        "courseName": "React 進階開發實戰",
        "courseType": "INTERNAL",
        "courseTypeLabel": "內訓",
        "deliveryMode": "OFFLINE",
        "deliveryModeLabel": "實體課程",
        "category": "TECHNICAL",
        "categoryLabel": "技術類",
        "instructor": "李講師",
        "durationHours": 8,
        "maxParticipants": 30,
        "currentEnrollments": 15,
        "availableSeats": 15,
        "startDate": "2025-12-15",
        "endDate": "2025-12-15",
        "location": "台北辦公室 5F",
        "status": "OPEN",
        "statusLabel": "報名中",
        "isMandatory": false,
        "enrollmentDeadline": "2025-12-10",
        "canEnroll": true
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "totalItems": 25,
      "totalPages": 2
    }
  }
}
```

---

### 2.3 查詢課程詳情

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/courses/{id}` |
| **方法** | GET |
| **Controller** | HR10CourseQryController |
| **Service** | GetCourseDetailServiceImpl |
| **權限** | `training:course:read` |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "courseId": "course-uuid-001",
    "courseCode": "TRN-202512-001",
    "courseName": "React 進階開發實戰",
    "courseType": "INTERNAL",
    "courseTypeLabel": "內訓",
    "deliveryMode": "OFFLINE",
    "deliveryModeLabel": "實體課程",
    "category": "TECHNICAL",
    "categoryLabel": "技術類",
    "description": "本課程將深入介紹...",
    "instructorInfo": {
      "name": "李講師",
      "title": "資深前端工程師",
      "company": "內部講師"
    },
    "durationHours": 8,
    "maxParticipants": 30,
    "minParticipants": 10,
    "currentEnrollments": 15,
    "startDate": "2025-12-15",
    "endDate": "2025-12-15",
    "startTime": "09:00",
    "endTime": "18:00",
    "location": "台北辦公室 5F 訓練教室",
    "cost": 0,
    "isMandatory": false,
    "targetAudience": ["RD", "QA"],
    "prerequisites": "具備 React 基礎知識",
    "enrollmentDeadline": "2025-12-10",
    "status": "OPEN",
    "statusLabel": "報名中",
    "enrollmentStats": {
      "registered": 5,
      "approved": 10,
      "rejected": 2,
      "cancelled": 1
    },
    "myEnrollment": {
      "enrollmentId": "enroll-uuid-001",
      "status": "APPROVED",
      "enrolledAt": "2025-12-02T14:00:00Z"
    },
    "createdBy": "user-uuid",
    "createdByName": "HR Admin",
    "createdAt": "2025-12-01T10:00:00Z",
    "updatedAt": "2025-12-02T09:00:00Z"
  }
}
```

---

### 2.4 更新課程

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/courses/{id}` |
| **方法** | PUT |
| **Controller** | HR10CourseCmdController |
| **Service** | UpdateCourseServiceImpl |
| **權限** | `training:course:update` |

#### 業務邏輯

```
1. 驗證規則
   ├── 課程必須存在
   ├── 只有 DRAFT 或 OPEN 狀態可編輯
   └── 若已有報名，部分欄位受限制

2. 受限制欄位（已有報名時）
   ├── 開始日期僅可延後
   ├── 最大人數不可少於目前報名人數
   └── 課程時數不可減少
```

#### Request Body

```json
{
  "courseName": "React 進階開發實戰（更新版）",
  "description": "更新課程說明...",
  "maxParticipants": 35,
  "location": "台北辦公室 6F 大會議室"
}
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "課程更新成功",
  "data": {
    "courseId": "course-uuid-001",
    "courseName": "React 進階開發實戰（更新版）",
    "updatedAt": "2025-12-03T10:00:00Z"
  }
}
```

---

### 2.5 發布課程

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/courses/{id}/publish` |
| **方法** | PUT |
| **Controller** | HR10CourseCmdController |
| **Service** | PublishCourseServiceImpl |
| **權限** | `training:course:publish` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 將草稿課程發布為開放報名狀態 |
| **使用者** | HR 人員、訓練管理員 |
| **後續動作** | 員工可開始報名 |

#### 業務邏輯

```
1. 驗證規則
   ├── 課程必須存在
   ├── 課程狀態必須為 DRAFT
   ├── 必填欄位完整（日期、時數）
   └── 開始日期需在未來

2. 處理步驟
   ├── 更新課程狀態為 OPEN
   ├── 發布 CoursePublishedEvent
   └── 若為必修，發送通知給目標對象
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "課程已發布，開放報名",
  "data": {
    "courseId": "course-uuid-001",
    "status": "OPEN",
    "publishedAt": "2025-12-03T10:00:00Z"
  }
}
```

#### 領域事件

**CoursePublishedEvent**

```json
{
  "eventType": "CoursePublished",
  "aggregateId": "course-uuid-001",
  "payload": {
    "courseId": "course-uuid-001",
    "courseName": "React 進階開發實戰",
    "isMandatory": false,
    "targetAudience": ["RD", "QA"],
    "startDate": "2025-12-15",
    "enrollmentDeadline": "2025-12-10"
  }
}
```

| Topic | 訂閱服務 |
|:---|:---|
| `training.course.published` | Notification（若為必修） |

---

### 2.6 關閉報名

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/courses/{id}/close` |
| **方法** | PUT |
| **Controller** | HR10CourseCmdController |
| **Service** | CloseCourseEnrollmentServiceImpl |
| **權限** | `training:course:close` |

#### 業務邏輯

```
1. 驗證規則
   ├── 課程必須存在
   └── 課程狀態必須為 OPEN

2. 處理步驟
   ├── 更新課程狀態為 CLOSED
   ├── 拒絕所有待審核的報名
   └── 發布 CourseClosedEvent
```

#### Request Body

```json
{
  "reason": "報名截止"
}
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "課程已關閉報名",
  "data": {
    "courseId": "course-uuid-001",
    "status": "CLOSED",
    "closedAt": "2025-12-10T18:00:00Z"
  }
}
```

---

### 2.7 完成課程

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/courses/{id}/complete` |
| **方法** | PUT |
| **Controller** | HR10CourseCmdController |
| **Service** | CompleteCourseServiceImpl |
| **權限** | `training:course:complete` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 課程結束後，結算訓練結果 |
| **使用者** | HR 人員、訓練管理員 |
| **後續動作** | 更新學員訓練時數 |

#### 業務邏輯

```
1. 驗證規則
   ├── 課程必須存在
   ├── 課程狀態必須為 CLOSED
   └── 課程結束日期已過

2. 處理步驟
   ├── 更新課程狀態為 COMPLETED
   ├── 將已出席學員狀態更新為 COMPLETED
   ├── 將未出席學員狀態更新為 NO_SHOW
   ├── 發布 CourseCompletedEvent
   └── 發布 TrainingCompletedEvent（每位完成學員）
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "課程已結束",
  "data": {
    "courseId": "course-uuid-001",
    "status": "COMPLETED",
    "completedCount": 25,
    "noShowCount": 3,
    "completedAt": "2025-12-15T18:00:00Z"
  }
}
```

---

## 3. 報名管理 API

### 3.1 報名課程

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/training/enrollments` |
| **方法** | POST |
| **Controller** | HR10EnrollmentCmdController |
| **Service** | EnrollCourseServiceImpl |
| **權限** | `training:enrollment:create` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 員工自行報名或 HR 代為報名課程 |
| **使用者** | 所有員工、HR 人員 |
| **後續動作** | 通知主管審核 |

#### 業務邏輯

```
1. 驗證規則
   ├── 課程必須存在且狀態為 OPEN
   ├── 未超過報名截止日
   ├── 課程名額未滿
   ├── 員工未重複報名
   └── 員工符合先修條件（若有）

2. 處理步驟
   ├── 建立報名記錄，狀態為 REGISTERED
   ├── 發布 EnrollmentCreatedEvent
   └── Notification Service 通知主管審核

3. 自動核准規則
   ├── 免費內訓且非管理職 → 自動核准
   └── 其他 → 需主管審核
```

#### Request Body

```json
{
  "courseId": "course-uuid-001",
  "employeeId": "emp-uuid-001",
  "reason": "希望提升前端技術能力",
  "remarks": "已完成 React 基礎課程"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `courseId` | UUID | ✅ | 課程 ID |
| `employeeId` | UUID | ❌ | 員工 ID（HR 代報時填寫，否則使用當前用戶） |
| `reason` | String | ❌ | 報名理由 |
| `remarks` | String | ❌ | 備註 |

#### Response Body

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "報名成功，待主管審核",
  "data": {
    "enrollmentId": "enroll-uuid-001",
    "courseId": "course-uuid-001",
    "courseName": "React 進階開發實戰",
    "employeeId": "emp-uuid-001",
    "status": "REGISTERED",
    "enrolledAt": "2025-12-02T14:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `TRN_COURSE_NOT_OPEN` | 課程未開放報名 | 請選擇開放中的課程 |
| 400 | `TRN_ENROLLMENT_DEADLINE_PASSED` | 報名截止 | 報名截止日已過 |
| 400 | `TRN_COURSE_FULL` | 課程已額滿 | 可申請候補 |
| 400 | `TRN_ALREADY_ENROLLED` | 已報名此課程 | 請勿重複報名 |

#### 領域事件

**EnrollmentCreatedEvent**

```json
{
  "eventType": "EnrollmentCreated",
  "aggregateId": "enroll-uuid-001",
  "aggregateType": "TrainingEnrollment",
  "payload": {
    "enrollmentId": "enroll-uuid-001",
    "courseId": "course-uuid-001",
    "courseName": "React 進階開發實戰",
    "employeeId": "emp-uuid-001",
    "employeeName": "張三",
    "managerId": "mgr-uuid-001",
    "managerName": "李組長",
    "trainingHours": 8,
    "cost": 0,
    "reason": "希望提升前端技術能力"
  }
}
```

| Topic | 訂閱服務 |
|:---|:---|
| `training.enrollment.created` | Notification Service |

---

### 3.2 查詢報名列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/enrollments` |
| **方法** | GET |
| **Controller** | HR10EnrollmentQryController |
| **Service** | GetEnrollmentsServiceImpl |
| **權限** | `training:enrollment:read` |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `courseId` | UUID | ❌ | 課程篩選 |
| `employeeId` | UUID | ❌ | 員工篩選 |
| `status` | Enum | ❌ | 報名狀態 |
| `pendingApproval` | Boolean | ❌ | 待審核（主管視角） |
| `page` | Integer | ❌ | 頁碼 |
| `size` | Integer | ❌ | 每頁筆數 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "items": [
      {
        "enrollmentId": "enroll-uuid-001",
        "course": {
          "courseId": "course-uuid-001",
          "courseCode": "TRN-202512-001",
          "courseName": "React 進階開發實戰",
          "startDate": "2025-12-15",
          "durationHours": 8
        },
        "employee": {
          "employeeId": "emp-uuid-001",
          "employeeNo": "EMP001",
          "employeeName": "張三",
          "departmentName": "研發部"
        },
        "status": "REGISTERED",
        "statusLabel": "待審核",
        "reason": "希望提升前端技術能力",
        "enrolledAt": "2025-12-02T14:00:00Z",
        "approvedAt": null,
        "approvedBy": null
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "totalItems": 15,
      "totalPages": 1
    }
  }
}
```

---

### 3.3 審核通過

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/enrollments/{id}/approve` |
| **方法** | PUT |
| **Controller** | HR10EnrollmentCmdController |
| **Service** | ApproveEnrollmentServiceImpl |
| **權限** | `training:enrollment:approve` |

#### 業務邏輯

```
1. 驗證規則
   ├── 報名必須存在
   ├── 報名狀態必須為 REGISTERED
   ├── 審核者必須是員工主管或 HR
   └── 課程名額未滿

2. 處理步驟
   ├── 更新報名狀態為 APPROVED
   ├── 記錄審核者與審核時間
   ├── 發布 EnrollmentApprovedEvent
   └── 通知員工報名成功
```

#### Request Body

```json
{
  "remarks": "同意參訓"
}
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "報名審核通過",
  "data": {
    "enrollmentId": "enroll-uuid-001",
    "status": "APPROVED",
    "approvedBy": "mgr-uuid-001",
    "approvedByName": "李組長",
    "approvedAt": "2025-12-03T09:00:00Z"
  }
}
```

#### 領域事件

**EnrollmentApprovedEvent**

```json
{
  "eventType": "EnrollmentApproved",
  "aggregateId": "enroll-uuid-001",
  "payload": {
    "enrollmentId": "enroll-uuid-001",
    "courseId": "course-uuid-001",
    "courseName": "React 進階開發實戰",
    "employeeId": "emp-uuid-001",
    "employeeName": "張三",
    "employeeEmail": "zhang@company.com",
    "startDate": "2025-12-15",
    "location": "台北辦公室 5F",
    "approvedBy": "mgr-uuid-001"
  }
}
```

| Topic | 訂閱服務 |
|:---|:---|
| `training.enrollment.approved` | Notification Service |

---

### 3.4 審核拒絕

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/enrollments/{id}/reject` |
| **方法** | PUT |
| **Controller** | HR10EnrollmentCmdController |
| **Service** | RejectEnrollmentServiceImpl |
| **權限** | `training:enrollment:reject` |

#### Request Body

```json
{
  "reason": "目前專案進度緊迫，建議下期再報名"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `reason` | String | ✅ | 拒絕原因 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "報名已拒絕",
  "data": {
    "enrollmentId": "enroll-uuid-001",
    "status": "REJECTED",
    "rejectedBy": "mgr-uuid-001",
    "rejectedAt": "2025-12-03T09:00:00Z"
  }
}
```

---

### 3.5 取消報名

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/enrollments/{id}/cancel` |
| **方法** | PUT |
| **Controller** | HR10EnrollmentCmdController |
| **Service** | CancelEnrollmentServiceImpl |
| **權限** | `training:enrollment:cancel` |

#### 業務邏輯

```
1. 驗證規則
   ├── 報名必須存在
   ├── 報名狀態必須為 REGISTERED 或 APPROVED
   ├── 課程尚未開始
   └── 取消者必須是本人或 HR

2. 處理步驟
   ├── 更新報名狀態為 CANCELLED
   └── 發布 EnrollmentCancelledEvent
```

#### Request Body

```json
{
  "reason": "臨時有重要會議無法參加"
}
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "報名已取消",
  "data": {
    "enrollmentId": "enroll-uuid-001",
    "status": "CANCELLED",
    "cancelledAt": "2025-12-05T10:00:00Z"
  }
}
```

---

### 3.6 確認出席

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/enrollments/{id}/attendance` |
| **方法** | PUT |
| **Controller** | HR10EnrollmentCmdController |
| **Service** | ConfirmAttendanceServiceImpl |
| **權限** | `training:enrollment:attendance` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 課程當天確認學員出席狀態 |
| **使用者** | 講師、HR 人員 |
| **可批次操作** | 支援批次確認多位學員出席 |

#### Request Body

```json
{
  "attended": true,
  "attendedHours": 8,
  "remarks": "全程參與"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `attended` | Boolean | ✅ | 是否出席 |
| `attendedHours` | Decimal | ❌ | 實際出席時數（預設課程時數） |
| `remarks` | String | ❌ | 備註 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "出席已確認",
  "data": {
    "enrollmentId": "enroll-uuid-001",
    "status": "ATTENDED",
    "attendedHours": 8,
    "attendedAt": "2025-12-15T18:00:00Z"
  }
}
```

---

### 3.7 完成訓練

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/enrollments/{id}/complete` |
| **方法** | PUT |
| **Controller** | HR10EnrollmentCmdController |
| **Service** | CompleteTrainingServiceImpl |
| **權限** | `training:enrollment:complete` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 記錄學員完成訓練並登錄成績 |
| **使用者** | 講師、HR 人員 |
| **後續動作** | 更新員工訓練時數統計 |

#### 業務邏輯

```
1. 驗證規則
   ├── 報名必須存在
   ├── 報名狀態必須為 ATTENDED
   └── 成績介於 0-100（若填寫）

2. 處理步驟
   ├── 更新報名狀態為 COMPLETED
   ├── 記錄成績與評語
   ├── 發布 TrainingCompletedEvent
   └── 更新員工訓練時數

3. 副作用
   └── Reporting Service 更新訓練統計
```

#### Request Body

```json
{
  "completedHours": 8,
  "score": 85,
  "passed": true,
  "feedback": "表現優異，課堂互動積極"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `completedHours` | Decimal | ❌ | 完成時數（預設出席時數） |
| `score` | Decimal | ❌ | 成績（0-100） |
| `passed` | Boolean | ❌ | 是否通過（預設 true） |
| `feedback` | String | ❌ | 講師評語 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "訓練已完成",
  "data": {
    "enrollmentId": "enroll-uuid-001",
    "status": "COMPLETED",
    "completedHours": 8,
    "score": 85,
    "passed": true,
    "completedAt": "2025-12-15T18:00:00Z"
  }
}
```

#### 領域事件

**TrainingCompletedEvent**

```json
{
  "eventType": "TrainingCompleted",
  "aggregateId": "enroll-uuid-001",
  "aggregateType": "TrainingEnrollment",
  "payload": {
    "enrollmentId": "enroll-uuid-001",
    "employeeId": "emp-uuid-001",
    "employeeName": "張三",
    "courseId": "course-uuid-001",
    "courseName": "React 進階開發實戰",
    "courseCategory": "TECHNICAL",
    "completedHours": 8,
    "completedDate": "2025-12-15",
    "score": 85,
    "passed": true
  }
}
```

| Topic | 訂閱服務 |
|:---|:---|
| `training.completed` | Reporting Service |

---

### 3.8 我的訓練

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/my` |
| **方法** | GET |
| **Controller** | HR10EnrollmentQryController |
| **Service** | GetMyTrainingsServiceImpl |
| **權限** | `training:my:read` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 員工查看自己的訓練記錄與進度 |
| **使用者** | 所有員工（ESS） |
| **頁面代碼** | HR10-P03 |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `year` | Integer | ❌ | 年度（預設當年） |
| `status` | Enum | ❌ | 報名狀態 |
| `category` | Enum | ❌ | 課程類別 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "summary": {
      "year": 2025,
      "totalHours": 24,
      "requiredHours": 20,
      "completedCourses": 3,
      "upcomingCourses": 1,
      "isCompliant": true
    },
    "upcoming": [
      {
        "enrollmentId": "enroll-uuid-001",
        "courseId": "course-uuid-001",
        "courseName": "React 進階開發實戰",
        "startDate": "2025-12-15",
        "startTime": "09:00",
        "location": "台北辦公室 5F",
        "durationHours": 8,
        "status": "APPROVED"
      }
    ],
    "completed": [
      {
        "enrollmentId": "enroll-uuid-002",
        "courseId": "course-uuid-002",
        "courseName": "敏捷開發基礎",
        "completedDate": "2025-11-20",
        "completedHours": 8,
        "score": 90,
        "category": "MANAGEMENT"
      }
    ],
    "cancelled": [
      {
        "enrollmentId": "enroll-uuid-003",
        "courseName": "AWS 基礎課程",
        "cancelledDate": "2025-10-15",
        "reason": "課程取消"
      }
    ]
  }
}
```

---

## 4. 證照管理 API

### 4.1 新增證照

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/training/certificates` |
| **方法** | POST |
| **Controller** | HR10CertificateCmdController |
| **Service** | AddCertificateServiceImpl |
| **權限** | `training:certificate:create` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 員工登錄取得的專業證照 |
| **使用者** | 所有員工、HR 人員 |
| **後續動作** | 到期前自動發送提醒 |

#### 業務邏輯

```
1. 驗證規則
   ├── 證照名稱必填
   ├── 發證日期必填
   ├── 到期日（若有）需晚於發證日
   └── 附件大小限制 10MB

2. 處理步驟
   ├── 建立證照記錄
   ├── 上傳附件至 Document Service
   └── 發布 CertificateAddedEvent

3. 到期提醒規則
   ├── 到期前 90 天：第一次提醒
   ├── 到期前 30 天：第二次提醒
   └── 到期前 7 天：緊急提醒
```

#### Request Body

```json
{
  "employeeId": "emp-uuid-001",
  "certificateName": "AWS Solutions Architect - Associate",
  "issuingOrganization": "Amazon Web Services",
  "certificateNumber": "AWS-SAA-123456",
  "issueDate": "2023-12-15",
  "expiryDate": "2026-12-15",
  "category": "TECHNICAL",
  "isRequired": true,
  "attachmentUrl": "https://storage.company.com/certs/aws-saa.pdf",
  "remarks": "公司補助考照費用"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `employeeId` | UUID | ❌ | 員工 ID（預設當前用戶） |
| `certificateName` | String | ✅ | 證照名稱 |
| `issuingOrganization` | String | ❌ | 發證機構 |
| `certificateNumber` | String | ❌ | 證照編號 |
| `issueDate` | Date | ✅ | 發證日期 |
| `expiryDate` | Date | ❌ | 到期日期（永久有效則不填） |
| `category` | Enum | ❌ | 證照類別 |
| `isRequired` | Boolean | ❌ | 是否為必備證照 |
| `attachmentUrl` | String | ❌ | 證照附件 |
| `remarks` | String | ❌ | 備註 |

#### Response Body

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "證照新增成功",
  "data": {
    "certificateId": "cert-uuid-001",
    "certificateName": "AWS Solutions Architect - Associate",
    "employeeId": "emp-uuid-001",
    "issueDate": "2023-12-15",
    "expiryDate": "2026-12-15",
    "daysUntilExpiry": 350,
    "isVerified": false,
    "createdAt": "2025-12-30T10:00:00Z"
  }
}
```

---

### 4.2 查詢證照列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/certificates` |
| **方法** | GET |
| **Controller** | HR10CertificateQryController |
| **Service** | GetCertificatesServiceImpl |
| **權限** | `training:certificate:read` |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `employeeId` | UUID | ❌ | 員工篩選 |
| `category` | Enum | ❌ | 類別篩選 |
| `isExpired` | Boolean | ❌ | 是否已過期 |
| `isRequired` | Boolean | ❌ | 是否必備 |
| `keyword` | String | ❌ | 關鍵字搜尋 |
| `page` | Integer | ❌ | 頁碼 |
| `size` | Integer | ❌ | 每頁筆數 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "items": [
      {
        "certificateId": "cert-uuid-001",
        "employee": {
          "employeeId": "emp-uuid-001",
          "employeeNo": "EMP001",
          "employeeName": "張三",
          "departmentName": "研發部"
        },
        "certificateName": "AWS Solutions Architect - Associate",
        "issuingOrganization": "Amazon Web Services",
        "certificateNumber": "AWS-SAA-123456",
        "issueDate": "2023-12-15",
        "expiryDate": "2026-12-15",
        "daysUntilExpiry": 350,
        "status": "VALID",
        "statusLabel": "有效",
        "category": "TECHNICAL",
        "categoryLabel": "技術類",
        "isRequired": true,
        "isVerified": true,
        "attachmentUrl": "https://storage.company.com/certs/aws-saa.pdf"
      }
    ],
    "summary": {
      "total": 45,
      "valid": 40,
      "expiring": 3,
      "expired": 2
    },
    "pagination": {
      "page": 1,
      "size": 20,
      "totalItems": 45,
      "totalPages": 3
    }
  }
}
```

---

### 4.3 查詢證照詳情

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/certificates/{id}` |
| **方法** | GET |
| **Controller** | HR10CertificateQryController |
| **Service** | GetCertificateDetailServiceImpl |
| **權限** | `training:certificate:read` |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "certificateId": "cert-uuid-001",
    "employee": {
      "employeeId": "emp-uuid-001",
      "employeeNo": "EMP001",
      "employeeName": "張三",
      "departmentName": "研發部"
    },
    "certificateName": "AWS Solutions Architect - Associate",
    "issuingOrganization": "Amazon Web Services",
    "certificateNumber": "AWS-SAA-123456",
    "issueDate": "2023-12-15",
    "expiryDate": "2026-12-15",
    "daysUntilExpiry": 350,
    "status": "VALID",
    "category": "TECHNICAL",
    "isRequired": true,
    "isVerified": true,
    "verifiedBy": "HR Admin",
    "verifiedAt": "2024-01-10T10:00:00Z",
    "attachmentUrl": "https://storage.company.com/certs/aws-saa.pdf",
    "remarks": "公司補助考照費用",
    "history": [
      {
        "date": "2023-12-15",
        "action": "CREATED",
        "description": "證照建立"
      },
      {
        "date": "2024-01-10",
        "action": "VERIFIED",
        "description": "HR 驗證通過"
      }
    ],
    "createdAt": "2023-12-20T10:00:00Z",
    "updatedAt": "2024-01-10T10:00:00Z"
  }
}
```

---

### 4.4 更新證照

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/training/certificates/{id}` |
| **方法** | PUT |
| **Controller** | HR10CertificateCmdController |
| **Service** | UpdateCertificateServiceImpl |
| **權限** | `training:certificate:update` |

#### Request Body

```json
{
  "certificateNumber": "AWS-SAA-123456-RENEWED",
  "issueDate": "2025-12-15",
  "expiryDate": "2028-12-15",
  "attachmentUrl": "https://storage.company.com/certs/aws-saa-renewed.pdf",
  "remarks": "已續證"
}
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "證照更新成功",
  "data": {
    "certificateId": "cert-uuid-001",
    "certificateName": "AWS Solutions Architect - Associate",
    "expiryDate": "2028-12-15",
    "updatedAt": "2025-12-30T10:00:00Z"
  }
}
```

---

### 4.5 刪除證照

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `DELETE /api/v1/training/certificates/{id}` |
| **方法** | DELETE |
| **Controller** | HR10CertificateCmdController |
| **Service** | DeleteCertificateServiceImpl |
| **權限** | `training:certificate:delete` |

#### 業務邏輯

```
1. 驗證規則
   ├── 證照必須存在
   ├── 刪除者必須是本人或 HR
   └── 必備證照需 HR 審核後才能刪除

2. 處理步驟
   ├── 軟刪除證照記錄
   └── 發布 CertificateDeletedEvent
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "證照已刪除"
}
```

---

### 4.6 即將到期證照

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/certificates/expiring` |
| **方法** | GET |
| **Controller** | HR10CertificateQryController |
| **Service** | GetExpiringCertificatesServiceImpl |
| **權限** | `training:certificate:read` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 監控即將到期的證照，提醒員工續證 |
| **使用者** | HR 人員、主管 |
| **排程任務** | 每日自動檢查並發送提醒 |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `days` | Integer | ❌ | 到期天數內（預設 30） |
| `departmentId` | UUID | ❌ | 部門篩選 |
| `isRequired` | Boolean | ❌ | 僅顯示必備證照 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "items": [
      {
        "certificateId": "cert-uuid-002",
        "employee": {
          "employeeId": "emp-uuid-002",
          "employeeName": "李四",
          "email": "li@company.com",
          "departmentName": "研發部"
        },
        "certificateName": "PMP 專案管理師",
        "issuingOrganization": "PMI",
        "expiryDate": "2026-01-15",
        "daysUntilExpiry": 16,
        "isRequired": true,
        "urgencyLevel": "HIGH"
      }
    ],
    "summary": {
      "within7Days": 2,
      "within30Days": 5,
      "within90Days": 8
    }
  }
}
```

#### 領域事件

**CertificateExpiringEvent**（排程任務發布）

```json
{
  "eventType": "CertificateExpiring",
  "aggregateId": "cert-uuid-002",
  "aggregateType": "Certificate",
  "payload": {
    "certificateId": "cert-uuid-002",
    "employeeId": "emp-uuid-002",
    "employeeName": "李四",
    "employeeEmail": "li@company.com",
    "certificateName": "PMP 專案管理師",
    "issuingOrganization": "PMI",
    "expiryDate": "2026-01-15",
    "daysUntilExpiry": 16,
    "isRequired": true
  }
}
```

| Topic | 訂閱服務 |
|:---|:---|
| `training.certificate.expiring` | Notification Service |

---

## 5. 訓練統計 API

### 5.1 我的訓練時數

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/my/hours` |
| **方法** | GET |
| **Controller** | HR10ReportQryController |
| **Service** | GetMyTrainingHoursServiceImpl |
| **權限** | `training:my:read` |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `year` | Integer | ❌ | 年度（預設當年） |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "year": 2025,
    "employee": {
      "employeeId": "emp-uuid-001",
      "employeeName": "張三",
      "departmentName": "研發部"
    },
    "summary": {
      "totalHours": 24,
      "requiredHours": 20,
      "remainingHours": 0,
      "isCompliant": true,
      "complianceRate": 120
    },
    "byCategory": [
      {
        "category": "TECHNICAL",
        "categoryLabel": "技術類",
        "hours": 16,
        "courseCount": 2
      },
      {
        "category": "MANAGEMENT",
        "categoryLabel": "管理類",
        "hours": 8,
        "courseCount": 1
      }
    ],
    "byMonth": [
      {"month": "2025-01", "hours": 0},
      {"month": "2025-02", "hours": 0},
      {"month": "2025-03", "hours": 8},
      {"month": "2025-04", "hours": 0},
      {"month": "2025-05", "hours": 0},
      {"month": "2025-06", "hours": 0},
      {"month": "2025-07", "hours": 8},
      {"month": "2025-08", "hours": 0},
      {"month": "2025-09", "hours": 0},
      {"month": "2025-10", "hours": 0},
      {"month": "2025-11", "hours": 8},
      {"month": "2025-12", "hours": 0}
    ],
    "recentTrainings": [
      {
        "courseName": "React 進階開發實戰",
        "completedDate": "2025-11-20",
        "hours": 8,
        "category": "TECHNICAL"
      }
    ]
  }
}
```

---

### 5.2 訓練統計報表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/statistics` |
| **方法** | GET |
| **Controller** | HR10ReportQryController |
| **Service** | GetTrainingStatisticsServiceImpl |
| **權限** | `training:report:read` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 主管查看整體訓練成效 |
| **使用者** | HR 主管、人資長 |
| **頁面代碼** | HR10-P06 |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `year` | Integer | ❌ | 年度（預設當年） |
| `departmentId` | UUID | ❌ | 部門篩選 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "year": 2025,
    "overview": {
      "totalCourses": 45,
      "completedCourses": 38,
      "totalEnrollments": 520,
      "totalCompletions": 485,
      "completionRate": 93.3,
      "totalTrainingHours": 3200,
      "totalBudget": 500000,
      "usedBudget": 380000,
      "budgetUtilization": 76
    },
    "complianceStatus": {
      "totalEmployees": 150,
      "compliantEmployees": 142,
      "complianceRate": 94.7,
      "nonCompliantList": [
        {
          "employeeId": "emp-uuid-003",
          "employeeName": "王五",
          "departmentName": "業務部",
          "completedHours": 12,
          "requiredHours": 20,
          "shortfall": 8
        }
      ]
    },
    "byDepartment": [
      {
        "departmentId": "dept-uuid-001",
        "departmentName": "研發部",
        "employeeCount": 50,
        "totalHours": 1200,
        "avgHoursPerEmployee": 24,
        "complianceRate": 98
      }
    ],
    "byCategory": [
      {
        "category": "TECHNICAL",
        "categoryLabel": "技術類",
        "courseCount": 20,
        "totalHours": 1500,
        "participantCount": 280
      }
    ],
    "topCourses": [
      {
        "courseId": "course-uuid-001",
        "courseName": "React 進階開發實戰",
        "enrollmentCount": 45,
        "completionRate": 95,
        "avgScore": 88
      }
    ],
    "monthlyTrend": [
      {"month": "2025-01", "courses": 3, "hours": 240},
      {"month": "2025-02", "courses": 4, "hours": 320}
    ]
  }
}
```

---

### 5.3 匯出統計報表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/training/statistics/export` |
| **方法** | GET |
| **Controller** | HR10ReportQryController |
| **Service** | ExportTrainingStatisticsServiceImpl |
| **權限** | `training:report:export` |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `year` | Integer | ❌ | 年度 |
| `departmentId` | UUID | ❌ | 部門篩選 |
| `format` | Enum | ❌ | 格式：EXCEL / PDF（預設 EXCEL） |
| `reportType` | Enum | ❌ | SUMMARY / DETAIL / COMPLIANCE |

#### Response

**成功回應 (200 OK)**

```
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="training_report_2025.xlsx"
```

---

## 6. 錯誤碼定義

### 6.1 錯誤碼列表

| 錯誤碼 | HTTP 狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| **課程相關** |
| `TRN_COURSE_NOT_FOUND` | 404 | 課程不存在 | 請確認課程 ID |
| `TRN_COURSE_NOT_OPEN` | 400 | 課程未開放報名 | 請選擇開放中的課程 |
| `TRN_COURSE_NOT_EDITABLE` | 400 | 課程不可編輯 | 只有草稿或開放狀態可編輯 |
| `TRN_COURSE_NAME_REQUIRED` | 400 | 課程名稱必填 | 請填寫課程名稱 |
| `TRN_COURSE_CODE_EXISTS` | 400 | 課程代碼已存在 | 請使用其他代碼 |
| `TRN_INVALID_DATE_RANGE` | 400 | 日期範圍無效 | 結束日期需 >= 開始日期 |
| `TRN_INVALID_HOURS` | 400 | 訓練時數無效 | 時數需 > 0 |
| `TRN_COURSE_FULL` | 400 | 課程已額滿 | 可申請候補 |
| **報名相關** |
| `TRN_ENROLLMENT_NOT_FOUND` | 404 | 報名不存在 | 請確認報名 ID |
| `TRN_ENROLLMENT_DEADLINE_PASSED` | 400 | 報名截止 | 報名截止日已過 |
| `TRN_ALREADY_ENROLLED` | 400 | 已報名此課程 | 請勿重複報名 |
| `TRN_INVALID_ENROLLMENT_STATUS` | 400 | 報名狀態不符 | 無法執行此操作 |
| `TRN_NOT_APPROVER` | 403 | 非審核者 | 您無權審核此報名 |
| `TRN_CANCEL_NOT_ALLOWED` | 400 | 無法取消報名 | 課程已開始 |
| **證照相關** |
| `TRN_CERTIFICATE_NOT_FOUND` | 404 | 證照不存在 | 請確認證照 ID |
| `TRN_CERTIFICATE_NAME_REQUIRED` | 400 | 證照名稱必填 | 請填寫證照名稱 |
| `TRN_INVALID_EXPIRY_DATE` | 400 | 到期日無效 | 到期日需晚於發證日 |
| `TRN_DELETE_REQUIRED_CERT` | 400 | 無法刪除必備證照 | 請聯繫 HR |
| **權限相關** |
| `TRN_NO_PERMISSION` | 403 | 無權限 | 請聯繫管理員 |

---

## 7. 領域事件

### 7.1 事件清單

| 事件名稱 | Kafka Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| `CourseCreated` | `training.course.created` | 建立課程 | - |
| `CoursePublished` | `training.course.published` | 發布課程 | Notification（必修通知） |
| `CourseClosed` | `training.course.closed` | 關閉報名 | - |
| `CourseCompleted` | `training.course.completed` | 課程結束 | - |
| `EnrollmentCreated` | `training.enrollment.created` | 報名課程 | Notification |
| `EnrollmentApproved` | `training.enrollment.approved` | 審核通過 | Notification |
| `EnrollmentRejected` | `training.enrollment.rejected` | 審核拒絕 | Notification |
| `EnrollmentCancelled` | `training.enrollment.cancelled` | 取消報名 | - |
| `TrainingCompleted` | `training.completed` | 完成訓練 | Reporting |
| `CertificateAdded` | `training.certificate.added` | 新增證照 | - |
| `CertificateExpiring` | `training.certificate.expiring` | 證照即將到期 | Notification |

### 7.2 證照到期檢查排程

```
排程任務: CertificateExpiryCheckScheduler
執行頻率: 每日 08:00
處理邏輯:
  1. 查詢 90 天內到期的證照
  2. 依據到期天數分類（7/30/90 天）
  3. 發布 CertificateExpiringEvent
  4. Notification Service 發送提醒郵件
```

---

**文件完成日期:** 2025-12-30
**API 端點總數:** 24 個
