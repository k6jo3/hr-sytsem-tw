# HR09 招募管理服務 API 詳細規格

**版本:** 1.0
**建立日期:** 2025-12-30
**最後更新:** 2025-12-30
**Domain代號:** 09 (REC)
**服務名稱:** hrms-recruitment

---

## 目錄

1. [API 總覽](#1-api-總覽)
2. [職缺管理 API](#2-職缺管理-api)
3. [應徵者管理 API](#3-應徵者管理-api)
4. [面試管理 API](#4-面試管理-api)
5. [Offer 管理 API](#5-offer-管理-api)
6. [招募報表 API](#6-招募報表-api)
7. [錯誤碼定義](#7-錯誤碼定義)
8. [領域事件](#8-領域事件)

---

## 1. API 總覽

### 1.1 端點清單

| # | 端點 | 方法 | 說明 | Controller |
|:---:|:---|:---:|:---|:---|
| 1 | `/api/v1/recruitment/job-openings` | POST | 建立職缺 | HR09JobCmdController |
| 2 | `/api/v1/recruitment/job-openings` | GET | 查詢職缺列表 | HR09JobQryController |
| 3 | `/api/v1/recruitment/job-openings/{id}` | GET | 查詢職缺詳情 | HR09JobQryController |
| 4 | `/api/v1/recruitment/job-openings/{id}` | PUT | 更新職缺 | HR09JobCmdController |
| 5 | `/api/v1/recruitment/job-openings/{id}/close` | PUT | 關閉職缺 | HR09JobCmdController |
| 6 | `/api/v1/recruitment/candidates` | POST | 新增應徵者 | HR09CandidateCmdController |
| 7 | `/api/v1/recruitment/candidates` | GET | 查詢應徵者列表 | HR09CandidateQryController |
| 8 | `/api/v1/recruitment/candidates/{id}` | GET | 查詢應徵者詳情 | HR09CandidateQryController |
| 9 | `/api/v1/recruitment/candidates/{id}/status` | PUT | 更新應徵者狀態 | HR09CandidateCmdController |
| 10 | `/api/v1/recruitment/candidates/{id}/reject` | PUT | 拒絕應徵者 | HR09CandidateCmdController |
| 11 | `/api/v1/recruitment/candidates/{id}/hire` | PUT | 錄取應徵者 | HR09CandidateCmdController |
| 12 | `/api/v1/recruitment/interviews` | POST | 安排面試 | HR09InterviewCmdController |
| 13 | `/api/v1/recruitment/interviews` | GET | 查詢面試列表 | HR09InterviewQryController |
| 14 | `/api/v1/recruitment/interviews/{id}` | GET | 查詢面試詳情 | HR09InterviewQryController |
| 15 | `/api/v1/recruitment/interviews/{id}/reschedule` | PUT | 面試重新排程 | HR09InterviewCmdController |
| 16 | `/api/v1/recruitment/interviews/{id}/evaluations` | POST | 提交面試評估 | HR09InterviewCmdController |
| 17 | `/api/v1/recruitment/interviews/{id}/cancel` | POST | 取消面試 | HR09InterviewCmdController |
| 18 | `/api/v1/recruitment/offers` | POST | 發送Offer | HR09OfferCmdController |
| 19 | `/api/v1/recruitment/offers` | GET | 查詢Offer列表 | HR09OfferQryController |
| 20 | `/api/v1/recruitment/offers/{id}` | GET | 查詢Offer詳情 | HR09OfferQryController |
| 21 | `/api/v1/recruitment/offers/{id}/accept` | POST | 接受Offer | HR09OfferCmdController |
| 22 | `/api/v1/recruitment/offers/{id}/reject` | POST | 拒絕Offer | HR09OfferCmdController |
| 23 | `/api/v1/recruitment/offers/{id}/withdraw` | POST | 撤回Offer | HR09OfferCmdController |
| 24 | `/api/v1/recruitment/offers/{id}/extend` | PUT | 延長Offer到期日 | HR09OfferCmdController |
| 25 | `/api/v1/recruitment/dashboard` | GET | 招募儀表板 | HR09ReportQryController |
| 26 | `/api/v1/recruitment/dashboard/export` | GET | 匯出招募報表 | HR09ReportQryController |


### 1.2 應徵者狀態流程

```
NEW (新投遞)
  ↓
SCREENING (篩選中)
  ↓ (通過篩選)                    ↘ (不符合)
INTERVIEWING (面試中)              REJECTED (已拒絕)
  ↓ (面試通過)    ↘ (面試未通過)       ↑
OFFERED (已發Offer) ─────────────────┘
  ↓ (接受)        ↘ (拒絕)
HIRED (已錄取)      REJECTED
```

### 1.3 通用 Headers

| Header | 必填 | 說明 |
|:---|:---:|:---|
| `Authorization` | ✅ | Bearer Token |
| `X-Tenant-Id` | ✅ | 租戶識別碼 |
| `Content-Type` | ✅ | application/json |
| `Accept-Language` | ❌ | 語系（預設 zh-TW） |

---

## 2. 職缺管理 API

### 2.1 建立職缺

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/recruitment/job-openings` |
| **方法** | POST |
| **Controller** | HR09JobCmdController |
| **Service** | CreateJobOpeningServiceImpl |
| **權限** | `recruitment:job:create` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 為部門建立新的職缺需求 |
| **使用者** | HR 人員、用人主管 |
| **前置條件** | 已取得用人主管核准 |

#### 業務邏輯

```
1. 驗證規則
   ├── 職位名稱必填且不超過 100 字元
   ├── 部門 ID 必須存在
   ├── 需求人數 >= 1
   └── 薪資範圍格式正確

2. 處理步驟
   ├── 驗證部門是否存在（呼叫 Organization Service）
   ├── 建立職缺記錄，狀態為 DRAFT
   └── 發布 JobOpeningCreatedEvent

3. 狀態轉換
   └── → DRAFT（新建為草稿）
```

#### Request Body

```json
{
  "jobTitle": "前端工程師",
  "departmentId": "dept-uuid-001",
  "numberOfPositions": 2,
  "salaryRange": {
    "min": 60000,
    "max": 80000,
    "currency": "TWD"
  },
  "requirements": "- React 3年以上經驗\n- TypeScript 熟悉\n- 具團隊協作能力",
  "responsibilities": "- 開發前端功能\n- 維護現有系統\n- 參與程式碼審查",
  "employmentType": "FULL_TIME",
  "workLocation": "台北市信義區",
  "openDate": "2025-12-30"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `jobTitle` | String | ✅ | 長度 1-100 | 職位名稱 |
| `departmentId` | UUID | ✅ | 必須存在 | 所屬部門 |
| `numberOfPositions` | Integer | ✅ | >= 1 | 需求人數 |
| `salaryRange` | Object | ❌ | - | 薪資範圍 |
| `salaryRange.min` | Decimal | ❌ | >= 0 | 最低薪資 |
| `salaryRange.max` | Decimal | ❌ | >= min | 最高薪資 |
| `salaryRange.currency` | String | ❌ | ISO 4217 | 幣別（預設 TWD） |
| `requirements` | String | ❌ | 最大 2000 字 | 職位要求 |
| `responsibilities` | String | ❌ | 最大 2000 字 | 工作職責 |
| `employmentType` | Enum | ❌ | FULL_TIME/PART_TIME/CONTRACT | 雇用類型 |
| `workLocation` | String | ❌ | 最大 200 字 | 工作地點 |
| `openDate` | Date | ❌ | 不早於今天 | 開放日期 |

#### Response Body

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "職缺建立成功",
  "data": {
    "openingId": "opening-uuid-001",
    "jobTitle": "前端工程師",
    "departmentId": "dept-uuid-001",
    "departmentName": "研發部",
    "status": "DRAFT",
    "createdAt": "2025-12-30T10:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `REC_JOB_TITLE_REQUIRED` | 職位名稱必填 | 請填寫職位名稱 |
| 400 | `REC_JOB_TITLE_TOO_LONG` | 職位名稱過長 | 限制 100 字元內 |
| 400 | `REC_DEPARTMENT_NOT_FOUND` | 部門不存在 | 請選擇有效部門 |
| 400 | `REC_INVALID_SALARY_RANGE` | 薪資範圍無效 | 最高薪資需 >= 最低薪資 |
| 403 | `REC_NO_PERMISSION` | 無建立職缺權限 | 請聯繫管理員 |

#### 領域事件

**JobOpeningCreatedEvent**

```json
{
  "eventType": "JobOpeningCreated",
  "aggregateId": "opening-uuid-001",
  "aggregateType": "JobOpening",
  "payload": {
    "openingId": "opening-uuid-001",
    "jobTitle": "前端工程師",
    "departmentId": "dept-uuid-001",
    "departmentName": "研發部",
    "numberOfPositions": 2,
    "createdBy": "user-uuid",
    "createdAt": "2025-12-30T10:00:00Z"
  }
}
```

| Topic | 訂閱服務 |
|:---|:---|
| `recruitment.job-opening.created` | - |

---

### 2.2 查詢職缺列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/recruitment/job-openings` |
| **方法** | GET |
| **Controller** | HR09JobQryController |
| **Service** | GetJobOpeningsServiceImpl |
| **權限** | `recruitment:job:read` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 查看所有職缺狀態與招募進度 |
| **使用者** | HR 人員、用人主管 |
| **前置條件** | 已登入系統 |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `status` | Enum | ❌ | 職缺狀態篩選（DRAFT/OPEN/CLOSED/FILLED） |
| `departmentId` | UUID | ❌ | 部門篩選 |
| `keyword` | String | ❌ | 關鍵字搜尋（職位名稱） |
| `page` | Integer | ❌ | 頁碼（預設 1） |
| `size` | Integer | ❌ | 每頁筆數（預設 20，最大 100） |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "items": [
      {
        "openingId": "opening-uuid-001",
        "jobTitle": "前端工程師",
        "departmentId": "dept-uuid-001",
        "departmentName": "研發部",
        "numberOfPositions": 2,
        "filledPositions": 0,
        "salaryRange": "60K-80K",
        "status": "OPEN",
        "candidateCount": 15,
        "openDate": "2025-12-01",
        "createdAt": "2025-11-25T10:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "totalItems": 12,
      "totalPages": 1
    }
  }
}
```

---

### 2.3 查詢職缺詳情

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/recruitment/job-openings/{id}` |
| **方法** | GET |
| **Controller** | HR09JobQryController |
| **Service** | GetJobOpeningDetailServiceImpl |
| **權限** | `recruitment:job:read` |

#### Path Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `id` | UUID | ✅ | 職缺 ID |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "openingId": "opening-uuid-001",
    "jobTitle": "前端工程師",
    "departmentId": "dept-uuid-001",
    "departmentName": "研發部",
    "numberOfPositions": 2,
    "filledPositions": 0,
    "salaryRange": {
      "min": 60000,
      "max": 80000,
      "currency": "TWD"
    },
    "requirements": "- React 3年以上經驗\n- TypeScript 熟悉",
    "responsibilities": "- 開發前端功能\n- 維護現有系統",
    "employmentType": "FULL_TIME",
    "workLocation": "台北市信義區",
    "status": "OPEN",
    "openDate": "2025-12-01",
    "closeDate": null,
    "candidateStats": {
      "total": 15,
      "new": 5,
      "screening": 4,
      "interviewing": 3,
      "offered": 2,
      "hired": 0,
      "rejected": 1
    },
    "createdBy": "user-uuid",
    "createdByName": "HR Admin",
    "createdAt": "2025-11-25T10:00:00Z",
    "updatedAt": "2025-12-01T09:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 404 | `REC_JOB_NOT_FOUND` | 職缺不存在 |

---

### 2.4 更新職缺

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/recruitment/job-openings/{id}` |
| **方法** | PUT |
| **Controller** | HR09JobCmdController |
| **Service** | UpdateJobOpeningServiceImpl |
| **權限** | `recruitment:job:update` |

#### 業務邏輯

```
1. 驗證規則
   ├── 職缺必須存在
   ├── 只有 DRAFT 或 OPEN 狀態可編輯
   └── 需求人數不可少於已錄取人數

2. 處理步驟
   ├── 載入現有職缺
   ├── 更新允許修改的欄位
   └── 發布 JobOpeningUpdatedEvent
```

#### Request Body

```json
{
  "jobTitle": "資深前端工程師",
  "numberOfPositions": 3,
  "salaryRange": {
    "min": 70000,
    "max": 100000,
    "currency": "TWD"
  },
  "requirements": "- React 5年以上經驗\n- 具帶人經驗",
  "responsibilities": "- 帶領前端團隊\n- 技術架構設計"
}
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "職缺更新成功",
  "data": {
    "openingId": "opening-uuid-001",
    "jobTitle": "資深前端工程師",
    "status": "OPEN",
    "updatedAt": "2025-12-30T11:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | `REC_JOB_NOT_EDITABLE` | 該職缺狀態不可編輯 |
| 400 | `REC_POSITIONS_LESS_THAN_HIRED` | 需求人數不可少於已錄取人數 |

---

### 2.5 關閉職缺

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/recruitment/job-openings/{id}/close` |
| **方法** | PUT |
| **Controller** | HR09JobCmdController |
| **Service** | CloseJobOpeningServiceImpl |
| **權限** | `recruitment:job:close` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 職缺招募完成或取消招募時關閉職缺 |
| **使用者** | HR 人員 |
| **後續動作** | 該職缺不再接受新的應徵者 |

#### 業務邏輯

```
1. 驗證規則
   ├── 職缺必須存在
   └── 只有 OPEN 狀態可關閉

2. 處理步驟
   ├── 更新職缺狀態為 CLOSED 或 FILLED
   ├── 設定關閉日期
   └── 發布 JobOpeningClosedEvent

3. 狀態判斷
   ├── 已錄取人數 = 需求人數 → FILLED
   └── 其他情況 → CLOSED
```

#### Request Body

```json
{
  "reason": "已招募足額",
  "closeType": "FILLED"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `reason` | String | ❌ | 關閉原因 |
| `closeType` | Enum | ❌ | FILLED（已滿額）/ CANCELLED（取消招募） |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "職缺已關閉",
  "data": {
    "openingId": "opening-uuid-001",
    "status": "FILLED",
    "closeDate": "2025-12-30",
    "closedAt": "2025-12-30T12:00:00Z"
  }
}
```

---

## 3. 應徵者管理 API

### 3.1 新增應徵者

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/recruitment/candidates` |
| **方法** | POST |
| **Controller** | HR09CandidateCmdController |
| **Service** | CreateCandidateServiceImpl |
| **權限** | `recruitment:candidate:create` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 收到履歷後建立應徵者資料 |
| **使用者** | HR 人員 |
| **前置條件** | 對應職缺存在且為開放狀態 |

#### 業務邏輯

```
1. 驗證規則
   ├── 職缺必須存在且狀態為 OPEN
   ├── Email 格式正確
   ├── 同一職缺不可重複投遞（相同 Email）
   └── 履歷檔案大小限制 10MB

2. 處理步驟
   ├── 驗證職缺狀態
   ├── 檢查重複投遞
   ├── 建立應徵者記錄，狀態為 NEW
   └── 發布 CandidateAppliedEvent
```

#### Request Body

```json
{
  "openingId": "opening-uuid-001",
  "fullName": "王小明",
  "email": "wang@email.com",
  "phoneNumber": "0912-345-678",
  "source": "JOB_BANK",
  "referrerId": null,
  "resumeUrl": "https://storage.company.com/resumes/wang-resume.pdf",
  "coverLetter": "您好，我對貴公司的前端工程師職位非常有興趣...",
  "expectedSalary": 70000,
  "availableDate": "2026-01-01"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `openingId` | UUID | ✅ | 必須存在且 OPEN | 應徵職缺 |
| `fullName` | String | ✅ | 長度 1-100 | 姓名 |
| `email` | String | ✅ | Email 格式 | 電子郵件 |
| `phoneNumber` | String | ❌ | 電話格式 | 聯絡電話 |
| `source` | Enum | ✅ | 見來源列表 | 履歷來源 |
| `referrerId` | UUID | ❌ | 員工必須存在 | 推薦人（員工推薦時） |
| `resumeUrl` | String | ❌ | URL 格式 | 履歷檔案連結 |
| `coverLetter` | String | ❌ | 最大 2000 字 | 求職信 |
| `expectedSalary` | Decimal | ❌ | >= 0 | 期望薪資 |
| `availableDate` | Date | ❌ | 不早於今天 | 可到職日 |

**履歷來源 (source):**
- `JOB_BANK` - 人力銀行（104、1111等）
- `REFERRAL` - 員工推薦
- `WEBSITE` - 公司官網
- `LINKEDIN` - LinkedIn
- `HEADHUNTER` - 獵頭
- `OTHER` - 其他

#### Response Body

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "應徵者資料建立成功",
  "data": {
    "candidateId": "candidate-uuid-001",
    "fullName": "王小明",
    "openingId": "opening-uuid-001",
    "jobTitle": "前端工程師",
    "status": "NEW",
    "applicationDate": "2025-12-30",
    "createdAt": "2025-12-30T10:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `REC_JOB_NOT_OPEN` | 職缺未開放 | 請確認職缺狀態 |
| 400 | `REC_DUPLICATE_APPLICATION` | 重複投遞 | 該應徵者已投遞此職缺 |
| 400 | `REC_INVALID_EMAIL` | Email 格式錯誤 | 請輸入正確 Email |
| 400 | `REC_REFERRER_NOT_FOUND` | 推薦人不存在 | 請確認推薦人員工編號 |

#### 領域事件

**CandidateAppliedEvent**

```json
{
  "eventType": "CandidateApplied",
  "aggregateId": "candidate-uuid-001",
  "aggregateType": "Candidate",
  "payload": {
    "candidateId": "candidate-uuid-001",
    "fullName": "王小明",
    "email": "wang@email.com",
    "openingId": "opening-uuid-001",
    "jobTitle": "前端工程師",
    "source": "JOB_BANK",
    "applicationDate": "2025-12-30"
  }
}
```

| Topic | 訂閱服務 |
|:---|:---|
| `recruitment.candidate.applied` | - |

---

### 3.2 查詢應徵者列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/recruitment/candidates` |
| **方法** | GET |
| **Controller** | HR09CandidateQryController |
| **Service** | GetCandidatesServiceImpl |
| **權限** | `recruitment:candidate:read` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 使用看板視圖管理應徵者流程 |
| **使用者** | HR 人員、用人主管 |
| **顯示方式** | 支援看板（Kanban）與列表兩種視圖 |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `openingId` | UUID | ❌ | 職缺篩選 |
| `status` | Enum | ❌ | 狀態篩選（多選，逗號分隔） |
| `source` | Enum | ❌ | 來源篩選 |
| `keyword` | String | ❌ | 姓名關鍵字搜尋 |
| `view` | Enum | ❌ | 視圖模式：KANBAN / LIST（預設 LIST） |
| `page` | Integer | ❌ | 頁碼（LIST 模式用） |
| `size` | Integer | ❌ | 每頁筆數 |

#### Response Body

**成功回應 - 列表視圖 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "items": [
      {
        "candidateId": "candidate-uuid-001",
        "fullName": "王小明",
        "email": "wang@email.com",
        "phoneNumber": "0912-345-678",
        "openingId": "opening-uuid-001",
        "jobTitle": "前端工程師",
        "source": "JOB_BANK",
        "sourceLabel": "人力銀行",
        "status": "NEW",
        "statusLabel": "新投遞",
        "applicationDate": "2025-12-30",
        "lastActivityDate": "2025-12-30T10:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "totalItems": 45,
      "totalPages": 3
    }
  }
}
```

**成功回應 - 看板視圖 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "kanban": {
      "NEW": {
        "count": 15,
        "items": [
          {
            "candidateId": "candidate-uuid-001",
            "fullName": "王小明",
            "jobTitle": "前端工程師",
            "source": "JOB_BANK",
            "applicationDate": "2025-12-30"
          }
        ]
      },
      "SCREENING": {
        "count": 8,
        "items": [...]
      },
      "INTERVIEWING": {
        "count": 5,
        "items": [...]
      },
      "OFFERED": {
        "count": 2,
        "items": [...]
      },
      "HIRED": {
        "count": 1,
        "items": [...]
      },
      "REJECTED": {
        "count": 3,
        "items": [...]
      }
    },
    "totalCount": 34
  }
}
```

---

### 3.3 查詢應徵者詳情

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/recruitment/candidates/{id}` |
| **方法** | GET |
| **Controller** | HR09CandidateQryController |
| **Service** | GetCandidateDetailServiceImpl |
| **權限** | `recruitment:candidate:read` |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "candidateId": "candidate-uuid-001",
    "fullName": "王小明",
    "email": "wang@email.com",
    "phoneNumber": "0912-345-678",
    "resumeUrl": "https://storage.company.com/resumes/wang-resume.pdf",
    "coverLetter": "您好，我對貴公司...",
    "expectedSalary": 70000,
    "availableDate": "2026-01-01",
    "source": "JOB_BANK",
    "sourceLabel": "人力銀行",
    "referrer": null,
    "opening": {
      "openingId": "opening-uuid-001",
      "jobTitle": "前端工程師",
      "departmentName": "研發部"
    },
    "status": "INTERVIEWING",
    "statusLabel": "面試中",
    "applicationDate": "2025-12-15",
    "timeline": [
      {
        "date": "2025-12-15T10:00:00Z",
        "action": "APPLIED",
        "description": "投遞履歷",
        "actor": "System"
      },
      {
        "date": "2025-12-18T14:00:00Z",
        "action": "STATUS_CHANGED",
        "description": "狀態更新為「篩選中」",
        "actor": "HR Admin"
      },
      {
        "date": "2025-12-20T10:00:00Z",
        "action": "INTERVIEW_SCHEDULED",
        "description": "安排第一輪面試",
        "actor": "HR Admin"
      }
    ],
    "interviews": [
      {
        "interviewId": "interview-uuid-001",
        "round": 1,
        "type": "TECHNICAL",
        "typeLabel": "技術面試",
        "scheduledAt": "2025-12-25T14:00:00Z",
        "location": "會議室 A",
        "status": "COMPLETED",
        "interviewers": [
          {
            "employeeId": "emp-uuid-001",
            "employeeName": "李組長"
          }
        ],
        "evaluations": [
          {
            "interviewerName": "李組長",
            "overallRating": "HIRE",
            "technicalScore": 4,
            "communicationScore": 4,
            "cultureFitScore": 5
          }
        ]
      }
    ],
    "offer": null,
    "createdAt": "2025-12-15T10:00:00Z",
    "updatedAt": "2025-12-25T16:00:00Z"
  }
}
```

---

### 3.4 更新應徵者狀態

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/recruitment/candidates/{id}/status` |
| **方法** | PUT |
| **Controller** | HR09CandidateCmdController |
| **Service** | UpdateCandidateStatusServiceImpl |
| **權限** | `recruitment:candidate:update` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 在看板上拖曳應徵者卡片更新狀態 |
| **使用者** | HR 人員 |
| **前置條件** | 狀態轉換必須符合狀態機規則 |

#### 業務邏輯

```
1. 狀態轉換規則
   ├── NEW → SCREENING（履歷篩選）
   ├── SCREENING → INTERVIEWING（進入面試）
   ├── SCREENING → REJECTED（不符合）
   ├── INTERVIEWING → OFFERED（發 Offer）
   ├── INTERVIEWING → REJECTED（面試未通過）
   └── 其他轉換 → 拒絕

2. 驗證規則
   ├── 應徵者必須存在
   ├── 狀態轉換必須合法
   └── OFFERED 狀態需透過發送 Offer API

3. 處理步驟
   ├── 驗證狀態轉換合法性
   ├── 更新應徵者狀態
   └── 發布 CandidateStatusChangedEvent
```

#### Request Body

```json
{
  "newStatus": "SCREENING",
  "reason": "履歷符合基本條件，進入篩選"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `newStatus` | Enum | ✅ | 新狀態 |
| `reason` | String | ❌ | 狀態變更原因 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "狀態已更新",
  "data": {
    "candidateId": "candidate-uuid-001",
    "previousStatus": "NEW",
    "currentStatus": "SCREENING",
    "updatedAt": "2025-12-30T14:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | `REC_INVALID_STATUS_TRANSITION` | 狀態轉換不合法 |
| 400 | `REC_USE_OFFER_API` | 請使用發送 Offer API |

---

### 3.5 拒絕應徵者

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/recruitment/candidates/{id}/reject` |
| **方法** | PUT |
| **Controller** | HR09CandidateCmdController |
| **Service** | RejectCandidateServiceImpl |
| **權限** | `recruitment:candidate:reject` |

#### 業務邏輯

```
1. 驗證規則
   ├── 應徵者必須存在
   ├── 不可拒絕已錄取的應徵者
   └── 拒絕原因必填

2. 處理步驟
   ├── 更新狀態為 REJECTED
   ├── 記錄拒絕原因
   └── 發布 CandidateRejectedEvent
```

#### Request Body

```json
{
  "reason": "技術能力與職位需求不符",
  "sendNotification": true,
  "notificationTemplate": "REJECTION_EMAIL"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `reason` | String | ✅ | 拒絕原因（內部記錄） |
| `sendNotification` | Boolean | ❌ | 是否發送通知（預設 false） |
| `notificationTemplate` | String | ❌ | 通知模板 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "應徵者已被拒絕",
  "data": {
    "candidateId": "candidate-uuid-001",
    "status": "REJECTED",
    "rejectedAt": "2025-12-30T15:00:00Z"
  }
}
```

---

### 3.6 錄取應徵者

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/recruitment/candidates/{id}/hire` |
| **方法** | PUT |
| **Controller** | HR09CandidateCmdController |
| **Service** | HireCandidateServiceImpl |
| **權限** | `recruitment:candidate:hire` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 應徵者接受 Offer 後，HR 確認錄取 |
| **使用者** | HR 人員 |
| **後續動作** | 自動觸發建立員工資料流程 |

#### 業務邏輯

```
1. 驗證規則
   ├── 應徵者必須存在
   ├── 應徵者狀態必須為 OFFERED
   └── 對應 Offer 必須已被接受

2. 處理步驟
   ├── 更新應徵者狀態為 HIRED
   ├── 更新職缺已錄取人數
   ├── 發布 CandidateHiredEvent
   └── Organization Service 接收事件後自動建立員工

3. 副作用
   ├── 職缺已錄取人數 +1
   ├── 若達到需求人數，職缺自動變為 FILLED
   └── 觸發建立員工資料流程
```

#### Request Body

```json
{
  "startDate": "2026-01-02",
  "remarks": "已完成背景調查"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `startDate` | Date | ❌ | 預計到職日（覆蓋 Offer 中的日期） |
| `remarks` | String | ❌ | 備註 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "錄取成功，員工資料將自動建立",
  "data": {
    "candidateId": "candidate-uuid-001",
    "fullName": "王小明",
    "status": "HIRED",
    "startDate": "2026-01-02",
    "hiredAt": "2025-12-30T16:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | `REC_NOT_OFFERED` | 應徵者尚未收到 Offer |
| 400 | `REC_OFFER_NOT_ACCEPTED` | Offer 尚未被接受 |
| 400 | `REC_JOB_ALREADY_FILLED` | 職缺已額滿 |

#### 領域事件

**CandidateHiredEvent**（重要事件）

```json
{
  "eventType": "CandidateHired",
  "aggregateId": "candidate-uuid-001",
  "aggregateType": "Candidate",
  "payload": {
    "candidateId": "candidate-uuid-001",
    "fullName": "王小明",
    "email": "wang@email.com",
    "phoneNumber": "0912-345-678",
    "openingId": "opening-uuid-001",
    "jobTitle": "前端工程師",
    "departmentId": "dept-uuid-001",
    "departmentName": "研發部",
    "offeredSalary": 70000,
    "expectedStartDate": "2026-01-02",
    "resumeUrl": "https://storage.company.com/resumes/wang-resume.pdf",
    "referrerId": null,
    "referrerName": null,
    "hiredBy": "user-uuid",
    "hiredByName": "HR Admin",
    "hiredAt": "2025-12-30T16:00:00Z"
  }
}
```

| Topic | 訂閱服務 | 處理動作 |
|:---|:---|:---|
| `recruitment.candidate.hired` | Organization Service | 自動建立員工資料 |
| `recruitment.candidate.hired` | Notification Service | 發送錄取通知 |

---

## 4. 面試管理 API

### 4.1 安排面試

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/recruitment/interviews/schedule` |
| **方法** | POST |
| **Controller** | HR09InterviewCmdController |
| **Service** | ScheduleInterviewServiceImpl |
| **權限** | `recruitment:interview:schedule` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 為通過篩選的應徵者安排面試 |
| **使用者** | HR 人員 |
| **後續動作** | 自動通知面試官與應徵者 |

#### 業務邏輯

```
1. 驗證規則
   ├── 應徵者必須存在
   ├── 應徵者狀態必須為 SCREENING 或 INTERVIEWING
   ├── 面試時間不可早於現在
   ├── 面試官必須存在
   └── 同一應徵者同一輪次不可重複安排

2. 處理步驟
   ├── 建立面試記錄
   ├── 若應徵者狀態為 SCREENING，自動更新為 INTERVIEWING
   ├── 發布 InterviewScheduledEvent
   └── Notification Service 發送通知
```

#### Request Body

```json
{
  "candidateId": "candidate-uuid-001",
  "interviewRound": 1,
  "interviewType": "TECHNICAL",
  "interviewDate": "2025-12-25T14:00:00Z",
  "duration": 60,
  "location": "台北辦公室 3F 會議室A",
  "meetingUrl": null,
  "interviewers": [
    {
      "employeeId": "emp-uuid-001",
      "role": "MAIN"
    },
    {
      "employeeId": "emp-uuid-002",
      "role": "OBSERVER"
    }
  ],
  "notes": "請準備技術測驗題目"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `candidateId` | UUID | ✅ | 應徵者 ID |
| `interviewRound` | Integer | ✅ | 面試輪次（1, 2, 3...） |
| `interviewType` | Enum | ✅ | 面試類型 |
| `interviewDate` | DateTime | ✅ | 面試時間 |
| `duration` | Integer | ❌ | 預計時長（分鐘，預設 60） |
| `location` | String | ❌ | 面試地點 |
| `meetingUrl` | String | ❌ | 線上會議連結 |
| `interviewers` | Array | ✅ | 面試官列表（至少 1 人） |
| `interviewers[].employeeId` | UUID | ✅ | 面試官員工 ID |
| `interviewers[].role` | Enum | ❌ | 角色：MAIN / OBSERVER |
| `notes` | String | ❌ | 備註 |

**面試類型 (interviewType):**
- `PHONE` - 電話面試
- `VIDEO` - 視訊面試
- `ONSITE` - 現場面試
- `TECHNICAL` - 技術面試
- `HR` - HR 面試
- `FINAL` - 最終面試

#### Response Body

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "面試已安排，將通知面試官及應徵者",
  "data": {
    "interviewId": "interview-uuid-001",
    "candidateId": "candidate-uuid-001",
    "candidateName": "王小明",
    "interviewRound": 1,
    "interviewType": "TECHNICAL",
    "interviewDate": "2025-12-25T14:00:00Z",
    "status": "SCHEDULED",
    "interviewers": [
      {
        "employeeId": "emp-uuid-001",
        "employeeName": "李組長",
        "role": "MAIN"
      }
    ]
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | `REC_CANDIDATE_NOT_SCREENING` | 應徵者狀態不符 |
| 400 | `REC_INTERVIEW_PAST_TIME` | 面試時間不可為過去 |
| 400 | `REC_DUPLICATE_ROUND` | 該輪次面試已存在 |
| 400 | `REC_INTERVIEWER_NOT_FOUND` | 面試官不存在 |

#### 領域事件

**InterviewScheduledEvent**

```json
{
  "eventType": "InterviewScheduled",
  "aggregateId": "interview-uuid-001",
  "aggregateType": "Interview",
  "payload": {
    "interviewId": "interview-uuid-001",
    "candidateId": "candidate-uuid-001",
    "candidateName": "王小明",
    "candidateEmail": "wang@email.com",
    "openingId": "opening-uuid-001",
    "jobTitle": "前端工程師",
    "interviewRound": 1,
    "interviewType": "TECHNICAL",
    "interviewDate": "2025-12-25T14:00:00Z",
    "location": "台北辦公室 3F 會議室A",
    "interviewers": [
      {
        "employeeId": "emp-uuid-001",
        "employeeName": "李組長",
        "email": "lee@company.com"
      }
    ],
    "scheduledBy": "user-uuid"
  }
}
```

| Topic | 訂閱服務 | 處理動作 |
|:---|:---|:---|
| `recruitment.interview.scheduled` | Notification Service | 通知面試官與應徵者 |

---

### 4.2 查詢面試列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/recruitment/interviews` |
| **方法** | GET |
| **Controller** | HR09InterviewQryController |
| **Service** | GetInterviewsServiceImpl |
| **權限** | `recruitment:interview:read` |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `candidateId` | UUID | ❌ | 應徵者篩選 |
| `interviewerId` | UUID | ❌ | 面試官篩選 |
| `status` | Enum | ❌ | 狀態篩選 |
| `dateFrom` | Date | ❌ | 開始日期 |
| `dateTo` | Date | ❌ | 結束日期 |
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
        "interviewId": "interview-uuid-001",
        "candidate": {
          "candidateId": "candidate-uuid-001",
          "fullName": "王小明",
          "jobTitle": "前端工程師"
        },
        "interviewRound": 1,
        "interviewType": "TECHNICAL",
        "typeLabel": "技術面試",
        "interviewDate": "2025-12-25T14:00:00Z",
        "duration": 60,
        "location": "會議室 A",
        "status": "SCHEDULED",
        "statusLabel": "已安排",
        "interviewers": [
          {
            "employeeId": "emp-uuid-001",
            "employeeName": "李組長",
            "hasEvaluated": false
          }
        ],
        "evaluationCount": 0,
        "createdAt": "2025-12-20T10:00:00Z"
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

### 4.3 提交面試評估

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/recruitment/interviews/{id}/evaluation` |
| **方法** | POST |
| **Controller** | HR09InterviewCmdController |
| **Service** | SubmitEvaluationServiceImpl |
| **權限** | `recruitment:interview:evaluate` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 面試官在面試結束後提交評估結果 |
| **使用者** | 面試官（員工） |
| **前置條件** | 當前使用者必須是該面試的面試官 |

#### 業務邏輯

```
1. 驗證規則
   ├── 面試必須存在
   ├── 當前使用者必須是面試官之一
   ├── 該面試官尚未提交評估
   ├── 面試狀態必須為 SCHEDULED 或 COMPLETED
   └── 評分必須在 1-5 之間

2. 處理步驟
   ├── 建立評估記錄
   ├── 若所有面試官都已評估，更新面試狀態為 COMPLETED
   └── 發布 InterviewEvaluatedEvent
```

#### Request Body

```json
{
  "technicalScore": 4,
  "communicationScore": 4,
  "cultureFitScore": 5,
  "overallRating": "HIRE",
  "strengths": "- React 經驗豐富\n- 溝通能力佳",
  "concerns": "- 缺乏大型專案經驗",
  "comments": "整體表現良好，建議錄用",
  "questionAnswers": [
    {
      "question": "請說明 React Hooks 的使用經驗",
      "answer": "回答清楚，舉例恰當",
      "score": 4
    }
  ]
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| `technicalScore` | Integer | ✅ | 1-5 | 技術能力評分 |
| `communicationScore` | Integer | ✅ | 1-5 | 溝通能力評分 |
| `cultureFitScore` | Integer | ✅ | 1-5 | 文化適配評分 |
| `overallRating` | Enum | ✅ | 見評級列表 | 整體評級 |
| `strengths` | String | ❌ | 最大 1000 字 | 優勢 |
| `concerns` | String | ❌ | 最大 1000 字 | 顧慮 |
| `comments` | String | ❌ | 最大 2000 字 | 評語 |
| `questionAnswers` | Array | ❌ | - | 問答記錄 |

**整體評級 (overallRating):**
- `STRONG_HIRE` - 強烈建議錄用
- `HIRE` - 建議錄用
- `NO_HIRE` - 不建議錄用
- `STRONG_NO_HIRE` - 強烈不建議錄用

#### Response Body

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "評估已提交",
  "data": {
    "evaluationId": "eval-uuid-001",
    "interviewId": "interview-uuid-001",
    "interviewerId": "emp-uuid-001",
    "overallRating": "HIRE",
    "submittedAt": "2025-12-25T16:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | `REC_NOT_INTERVIEWER` | 您不是該面試的面試官 |
| 400 | `REC_ALREADY_EVALUATED` | 您已提交評估 |
| 400 | `REC_INVALID_SCORE` | 評分必須在 1-5 之間 |

---

### 4.4 取消面試

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/recruitment/interviews/{id}/cancel` |
| **方法** | PUT |
| **Controller** | HR09InterviewCmdController |
| **Service** | CancelInterviewServiceImpl |
| **權限** | `recruitment:interview:cancel` |

#### Request Body

```json
{
  "reason": "應徵者臨時有事",
  "reschedule": true,
  "notifyParticipants": true
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `reason` | String | ✅ | 取消原因 |
| `reschedule` | Boolean | ❌ | 是否需要重新安排 |
| `notifyParticipants` | Boolean | ❌ | 是否通知相關人員（預設 true） |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "面試已取消",
  "data": {
    "interviewId": "interview-uuid-001",
    "status": "CANCELLED",
    "cancelledAt": "2025-12-24T10:00:00Z"
  }
}
```

---

## 5. Offer 管理 API

### 5.1 發送 Offer

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/recruitment/offers` |
| **方法** | POST |
| **Controller** | HR09OfferCmdController |
| **Service** | SendOfferServiceImpl |
| **權限** | `recruitment:offer:send` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | 面試通過後，HR 發送正式 Offer 給應徵者 |
| **使用者** | HR 人員 |
| **後續動作** | 自動通知應徵者收到 Offer |

#### 業務邏輯

```
1. 驗證規則
   ├── 應徵者必須存在
   ├── 應徵者狀態必須為 INTERVIEWING
   ├── 至少完成一輪面試
   ├── Offer 有效期限不可早於今天
   └── 薪資必須在職缺範圍內（警告，不阻擋）

2. 處理步驟
   ├── 建立 Offer 記錄，狀態為 PENDING
   ├── 更新應徵者狀態為 OFFERED
   ├── 發布 OfferSentEvent
   └── Notification Service 發送 Offer 通知
```

#### Request Body

```json
{
  "candidateId": "candidate-uuid-001",
  "offeredPosition": "前端工程師",
  "offeredSalary": 70000,
  "salaryType": "MONTHLY",
  "offeredStartDate": "2026-01-02",
  "expiryDate": "2025-12-31",
  "benefits": "- 年終獎金 2 個月\n- 員工旅遊\n- 彈性工時",
  "probationPeriod": 3,
  "otherTerms": "試用期滿後調薪 5%",
  "attachments": [
    {
      "name": "正式聘書.pdf",
      "url": "https://storage.company.com/offers/offer-letter.pdf"
    }
  ]
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `candidateId` | UUID | ✅ | 應徵者 ID |
| `offeredPosition` | String | ✅ | 職位名稱 |
| `offeredSalary` | Decimal | ✅ | 薪資金額 |
| `salaryType` | Enum | ❌ | MONTHLY / ANNUAL（預設 MONTHLY） |
| `offeredStartDate` | Date | ✅ | 預計到職日 |
| `expiryDate` | Date | ✅ | Offer 有效期限 |
| `benefits` | String | ❌ | 福利說明 |
| `probationPeriod` | Integer | ❌ | 試用期（月） |
| `otherTerms` | String | ❌ | 其他條款 |
| `attachments` | Array | ❌ | 附件（聘書等） |

#### Response Body

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "Offer 已發送，將通知應徵者",
  "data": {
    "offerId": "offer-uuid-001",
    "candidateId": "candidate-uuid-001",
    "candidateName": "王小明",
    "offeredPosition": "前端工程師",
    "offeredSalary": 70000,
    "status": "PENDING",
    "expiryDate": "2025-12-31",
    "sentAt": "2025-12-26T10:00:00Z"
  }
}
```

#### 領域事件

**OfferSentEvent**

```json
{
  "eventType": "OfferSent",
  "aggregateId": "offer-uuid-001",
  "aggregateType": "Offer",
  "payload": {
    "offerId": "offer-uuid-001",
    "candidateId": "candidate-uuid-001",
    "candidateName": "王小明",
    "candidateEmail": "wang@email.com",
    "openingId": "opening-uuid-001",
    "offeredPosition": "前端工程師",
    "offeredSalary": 70000,
    "offeredStartDate": "2026-01-02",
    "expiryDate": "2025-12-31"
  }
}
```

| Topic | 訂閱服務 |
|:---|:---|
| `recruitment.offer.sent` | Notification Service |

---

### 5.2 查詢 Offer 列表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/recruitment/offers` |
| **方法** | GET |
| **Controller** | HR09OfferQryController |
| **Service** | GetOffersServiceImpl |
| **權限** | `recruitment:offer:read` |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `status` | Enum | ❌ | 狀態篩選 |
| `candidateId` | UUID | ❌ | 應徵者篩選 |
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
        "offerId": "offer-uuid-001",
        "candidate": {
          "candidateId": "candidate-uuid-001",
          "fullName": "王小明",
          "email": "wang@email.com"
        },
        "opening": {
          "openingId": "opening-uuid-001",
          "jobTitle": "前端工程師"
        },
        "offeredPosition": "前端工程師",
        "offeredSalary": 70000,
        "offeredStartDate": "2026-01-02",
        "status": "PENDING",
        "statusLabel": "待回覆",
        "expiryDate": "2025-12-31",
        "daysRemaining": 5,
        "sentAt": "2025-12-26T10:00:00Z",
        "responseDate": null
      }
    ],
    "summary": {
      "pending": 3,
      "accepted": 5,
      "rejected": 2,
      "expired": 1
    },
    "pagination": {
      "page": 1,
      "size": 20,
      "totalItems": 11,
      "totalPages": 1
    }
  }
}
```

---

### 5.3 接受 Offer

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/recruitment/offers/{id}/accept` |
| **方法** | PUT |
| **Controller** | HR09OfferCmdController |
| **Service** | AcceptOfferServiceImpl |
| **權限** | `recruitment:offer:respond` |

#### 業務邏輯

```
1. 驗證規則
   ├── Offer 必須存在
   ├── Offer 狀態必須為 PENDING
   └── Offer 未過期

2. 處理步驟
   ├── 更新 Offer 狀態為 ACCEPTED
   ├── 記錄回覆日期
   └── 發布 OfferAcceptedEvent
```

#### Request Body

```json
{
  "confirmedStartDate": "2026-01-02",
  "remarks": "期待加入團隊"
}
```

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "Offer 已接受",
  "data": {
    "offerId": "offer-uuid-001",
    "status": "ACCEPTED",
    "confirmedStartDate": "2026-01-02",
    "acceptedAt": "2025-12-28T10:00:00Z"
  }
}
```

#### 錯誤碼

| HTTP 狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | `REC_OFFER_NOT_PENDING` | Offer 狀態不正確 |
| 400 | `REC_OFFER_EXPIRED` | Offer 已過期 |

---

### 5.4 拒絕 Offer

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/recruitment/offers/{id}/reject` |
| **方法** | PUT |
| **Controller** | HR09OfferCmdController |
| **Service** | RejectOfferServiceImpl |
| **權限** | `recruitment:offer:respond` |

#### Request Body

```json
{
  "reason": "已接受其他公司的 Offer",
  "feedback": "貴公司面試過程專業，但薪資與期望有落差"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `reason` | String | ✅ | 拒絕原因 |
| `feedback` | String | ❌ | 回饋意見 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "Offer 已拒絕",
  "data": {
    "offerId": "offer-uuid-001",
    "status": "REJECTED",
    "rejectedAt": "2025-12-28T10:00:00Z"
  }
}
```

---

## 6. 招募報表 API

### 6.1 招募儀表板

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/recruitment/dashboard` |
| **方法** | GET |
| **Controller** | HR09ReportQryController |
| **Service** | GetDashboardServiceImpl |
| **權限** | `recruitment:dashboard:read` |

#### 用途說明

| 項目 | 說明 |
|:---|:---|
| **業務場景** | HR 主管查看整體招募成效與 KPI |
| **使用者** | HR 主管、人資長 |
| **數據範圍** | 可依日期範圍篩選 |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `dateFrom` | Date | ❌ | 開始日期（預設當月 1 日） |
| `dateTo` | Date | ❌ | 結束日期（預設今天） |
| `departmentId` | UUID | ❌ | 部門篩選 |

#### Response Body

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "period": {
      "from": "2025-12-01",
      "to": "2025-12-30"
    },
    "kpis": {
      "openJobsCount": 12,
      "totalApplications": 85,
      "interviewsScheduled": 23,
      "offersExtended": 8,
      "hiredCount": 5,
      "avgTimeToHire": 28,
      "offerAcceptanceRate": 62.5
    },
    "sourceAnalytics": [
      {
        "source": "JOB_BANK",
        "sourceLabel": "人力銀行",
        "count": 38,
        "percentage": 44.7,
        "hiredCount": 2,
        "conversionRate": 5.3
      },
      {
        "source": "REFERRAL",
        "sourceLabel": "員工推薦",
        "count": 21,
        "percentage": 24.7,
        "hiredCount": 2,
        "conversionRate": 9.5
      },
      {
        "source": "WEBSITE",
        "sourceLabel": "公司官網",
        "count": 17,
        "percentage": 20.0,
        "hiredCount": 1,
        "conversionRate": 5.9
      },
      {
        "source": "LINKEDIN",
        "sourceLabel": "LinkedIn",
        "count": 9,
        "percentage": 10.6,
        "hiredCount": 0,
        "conversionRate": 0
      }
    ],
    "conversionFunnel": {
      "applied": 85,
      "screened": 45,
      "interviewed": 23,
      "offered": 8,
      "hired": 5,
      "rates": {
        "screeningRate": 52.9,
        "interviewRate": 51.1,
        "offerRate": 34.8,
        "acceptRate": 62.5
      }
    },
    "openingsByDepartment": [
      {
        "departmentId": "dept-uuid-001",
        "departmentName": "研發部",
        "openJobs": 5,
        "candidates": 35,
        "hired": 2
      },
      {
        "departmentId": "dept-uuid-002",
        "departmentName": "業務部",
        "openJobs": 3,
        "candidates": 25,
        "hired": 1
      }
    ],
    "monthlyTrend": [
      {
        "month": "2025-10",
        "applications": 65,
        "hired": 3
      },
      {
        "month": "2025-11",
        "applications": 72,
        "hired": 4
      },
      {
        "month": "2025-12",
        "applications": 85,
        "hired": 5
      }
    ]
  }
}
```

---

### 6.2 匯出招募報表

#### 基本資訊

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/recruitment/dashboard/export` |
| **方法** | GET |
| **Controller** | HR09ReportQryController |
| **Service** | ExportDashboardServiceImpl |
| **權限** | `recruitment:dashboard:export` |

#### Query Parameters

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| `dateFrom` | Date | ❌ | 開始日期 |
| `dateTo` | Date | ❌ | 結束日期 |
| `format` | Enum | ❌ | 匯出格式：EXCEL / PDF（預設 EXCEL） |
| `reportType` | Enum | ❌ | 報表類型：SUMMARY / DETAIL（預設 SUMMARY） |

#### Response

**成功回應 (200 OK)**

```
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="recruitment_report_2025-12.xlsx"
```

---

## 7. 錯誤碼定義

### 7.1 錯誤碼列表

| 錯誤碼 | HTTP 狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| **職缺相關** |
| `REC_JOB_NOT_FOUND` | 404 | 職缺不存在 | 請確認職缺 ID |
| `REC_JOB_NOT_OPEN` | 400 | 職缺未開放 | 請選擇開放中的職缺 |
| `REC_JOB_NOT_EDITABLE` | 400 | 職缺不可編輯 | 只有草稿或開放狀態可編輯 |
| `REC_JOB_TITLE_REQUIRED` | 400 | 職位名稱必填 | 請填寫職位名稱 |
| `REC_JOB_TITLE_TOO_LONG` | 400 | 職位名稱過長 | 限制 100 字元 |
| `REC_DEPARTMENT_NOT_FOUND` | 400 | 部門不存在 | 請選擇有效部門 |
| `REC_INVALID_SALARY_RANGE` | 400 | 薪資範圍無效 | 最高薪資需 >= 最低薪資 |
| `REC_POSITIONS_LESS_THAN_HIRED` | 400 | 需求人數不足 | 不可少於已錄取人數 |
| **應徵者相關** |
| `REC_CANDIDATE_NOT_FOUND` | 404 | 應徵者不存在 | 請確認應徵者 ID |
| `REC_DUPLICATE_APPLICATION` | 400 | 重複投遞 | 該應徵者已投遞此職缺 |
| `REC_INVALID_EMAIL` | 400 | Email 格式錯誤 | 請輸入正確 Email |
| `REC_REFERRER_NOT_FOUND` | 400 | 推薦人不存在 | 請確認推薦人員工編號 |
| `REC_INVALID_STATUS_TRANSITION` | 400 | 狀態轉換不合法 | 請依照狀態流程操作 |
| `REC_CANDIDATE_NOT_SCREENING` | 400 | 應徵者狀態不符 | 需為篩選中或面試中 |
| `REC_NOT_OFFERED` | 400 | 尚未發送 Offer | 請先發送 Offer |
| `REC_OFFER_NOT_ACCEPTED` | 400 | Offer 未被接受 | 請等待應徵者回覆 |
| `REC_JOB_ALREADY_FILLED` | 400 | 職缺已額滿 | 該職缺已招募足額 |
| **面試相關** |
| `REC_INTERVIEW_NOT_FOUND` | 404 | 面試不存在 | 請確認面試 ID |
| `REC_INTERVIEW_PAST_TIME` | 400 | 面試時間不可為過去 | 請選擇未來時間 |
| `REC_DUPLICATE_ROUND` | 400 | 輪次已存在 | 該輪次面試已安排 |
| `REC_INTERVIEWER_NOT_FOUND` | 400 | 面試官不存在 | 請選擇有效員工 |
| `REC_NOT_INTERVIEWER` | 403 | 非面試官 | 您不是該面試的面試官 |
| `REC_ALREADY_EVALUATED` | 400 | 已提交評估 | 每位面試官只能評估一次 |
| `REC_INVALID_SCORE` | 400 | 評分無效 | 評分需在 1-5 之間 |
| **Offer 相關** |
| `REC_OFFER_NOT_FOUND` | 404 | Offer 不存在 | 請確認 Offer ID |
| `REC_OFFER_NOT_PENDING` | 400 | Offer 狀態不正確 | Offer 需為待回覆狀態 |
| `REC_OFFER_EXPIRED` | 400 | Offer 已過期 | 請重新發送 Offer |
| `REC_USE_OFFER_API` | 400 | 請使用 Offer API | 發送 Offer 請使用專用 API |
| **權限相關** |
| `REC_NO_PERMISSION` | 403 | 無權限 | 請聯繫管理員 |

---

## 8. 領域事件

### 8.1 事件清單

| 事件名稱 | Kafka Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| `JobOpeningCreated` | `recruitment.job-opening.created` | 建立職缺 | - |
| `JobOpeningUpdated` | `recruitment.job-opening.updated` | 更新職缺 | - |
| `JobOpeningClosed` | `recruitment.job-opening.closed` | 關閉職缺 | - |
| `CandidateApplied` | `recruitment.candidate.applied` | 新增應徵者 | - |
| `CandidateStatusChanged` | `recruitment.candidate.status-changed` | 狀態變更 | - |
| `CandidateRejected` | `recruitment.candidate.rejected` | 拒絕應徵者 | Notification |
| `CandidateHired` | `recruitment.candidate.hired` | 錄取應徵者 | Organization, Notification |
| `InterviewScheduled` | `recruitment.interview.scheduled` | 安排面試 | Notification |
| `InterviewEvaluated` | `recruitment.interview.evaluated` | 提交評估 | - |
| `InterviewCancelled` | `recruitment.interview.cancelled` | 取消面試 | Notification |
| `OfferSent` | `recruitment.offer.sent` | 發送 Offer | Notification |
| `OfferAccepted` | `recruitment.offer.accepted` | 接受 Offer | Notification |
| `OfferRejected` | `recruitment.offer.rejected` | 拒絕 Offer | - |

### 8.2 CandidateHiredEvent 處理流程

```
Recruitment Service
        │
        ▼
┌───────────────────┐
│ CandidateHired    │
│ Event Published   │
└───────────────────┘
        │
        ▼
     Kafka
        │
   ┌────┴────┐
   ▼         ▼
┌─────────┐ ┌─────────────┐
│ Org Svc │ │Notification │
│ (建員工) │ │   Service   │
└─────────┘ └─────────────┘
   │              │
   ▼              ▼
建立員工資料    發送錄取通知
```

---

**文件完成日期:** 2025-12-30
**API 端點總數:** 21 個
