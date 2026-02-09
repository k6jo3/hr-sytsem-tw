# 招募管理服務業務合約 (Recruitment Service Business Contract)

> **服務代碼:** HR09
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/09_招募管理服務系統設計書.md`
> - `knowledge/04_API_Specifications/09_招募管理服務系統設計書_API詳細規格.md`

---

## 📋 概述

本合約文件定義招募管理服務的**完整業務場景**，包括：
1. **Command 操作場景**（建立、更新、完成） - 驗證業務規則與領域事件
2. **Query 操作場景**（查詢） - 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景
- ✅ 新增 5 個領域事件的詳細定義
- ✅ 對應到實際的 API 端點
- ✅ 使用實際存在的欄位（移除 is_deleted，使用 status）
- ✅ 包含完整的業務規則驗證

**服務定位：**
招募管理服務負責職缺發布、應徵者追蹤、面試排程、Offer 管理等功能。本服務必須確保**完善的招募流程管理**，支援看板式應徵者管理、多輪面試評分、自動建立員工資料等功能。

**資料軟刪除策略：**
- **職缺**: 使用 `status` 欄位，'OPEN' 開放中、'CLOSED' 已關閉、'CANCELLED' 已取消、'FILLED' 已招滿
- **應徵者**: 使用 `status` 欄位，'ACTIVE' 進行中、'HIRED' 已錄取、'REJECTED' 已拒絕、'WITHDRAWN' 已撤回
- **面試**: 使用 `status` 欄位，'SCHEDULED' 已排程、'COMPLETED' 已完成、'CANCELLED' 已取消
- **Offer**: 使用 `status` 欄位，'PENDING_APPROVAL' 待簽核、'SENT' 已發送、'ACCEPTED' 已接受、'DECLINED' 已拒絕、'EXPIRED' 已過期

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [職缺管理 Command](#11-職缺管理-command)
   - 1.2 [應徵者管理 Command](#12-應徵者管理-command)
   - 1.3 [面試和 Offer 管理 Command](#13-面試和-offer-管理-command)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [職缺查詢](#21-職缺查詢)
   - 2.2 [應徵者查詢](#22-應徵者查詢)
   - 2.3 [面試查詢](#23-面試查詢)
   - 2.4 [Offer 查詢](#24-offer-查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 職缺管理 Command

#### RCT_CMD_001: 建立職缺

**業務場景描述：**
HR 或部門主管建立新職缺，定義職位、薪資範圍、需求、招募優先級等，系統將職缺發布到公開職缺頁面。

**API 端點：**
```
POST /api/v1/recruitment/job-openings
```

**前置條件：**
- 執行者必須擁有 `recruitment:job:manage` 權限
- 部門必須存在且為 ACTIVE 狀態
- 職位必須存在於組織結構

**輸入 (Request)：**
```json
{
  "jobTitle": "資深前端工程師",
  "departmentId": "D001",
  "positionId": "P001",
  "numberOfPositions": 2,
  "salaryMin": 60000,
  "salaryMax": 80000,
  "salaryNegotiable": true,
  "employmentType": "FULL_TIME",
  "workLocation": "台北市信義區",
  "jobDescription": "主要負責 React 應用開發與維護...",
  "requirements": "3年以上 React 開發經驗，熟悉 TypeScript...",
  "qualifications": "資訊相關科系畢業優先",
  "priority": "HIGH",
  "openDate": "2026-02-01",
  "targetCloseDate": "2026-03-31",
  "visibility": "PUBLIC",
  "isUrgent": false,
  "recruiterId": "HR001"
}
```

**業務規則驗證：**

1. ✅ **部門存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Department 存在且為 ACTIVE 狀態

2. ✅ **職位存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Position 存在於該部門

3. ✅ **薪資範圍合理性檢查**
   - 規則：salaryMin <= salaryMax，且在合理範圍內（台灣基本工資以上）
   - 預期結果：salaryMin >= 27,470（2025年台灣基本工資）

4. ✅ **錄取人數合理性檢查**
   - 規則：numberOfPositions >= 1 且 <= 10
   - 預期結果：人數在合理範圍

5. ✅ **日期合理性檢查**
   - 規則：openDate <= targetCloseDate，openDate 不為過去日期
   - 預期結果：日期邏輯正確

6. ✅ **招募人員權限檢查**
   - 查詢條件：recruiterId 必須是有效的 HR 使用者
   - 預期結果：招募人員存在且為 ACTIVE 狀態

**必須發布的領域事件：**
```json
{
  "eventId": "evt-rct-job-001",
  "eventType": "JobOpeningCreatedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "job-001",
  "aggregateType": "JobOpening",
  "payload": {
    "openingId": "job-001",
    "jobTitle": "資深前端工程師",
    "departmentId": "D001",
    "departmentName": "研發部",
    "positionId": "P001",
    "positionName": "資深工程師",
    "numberOfPositions": 2,
    "salaryRange": "60K-80K",
    "requirements": "3年以上 React 開發經驗，熟悉 TypeScript",
    "openDate": "2026-02-01",
    "targetCloseDate": "2026-03-31",
    "visibility": "PUBLIC",
    "recruiterId": "HR001",
    "recruiterName": "李小姐",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "openingId": "job-001",
    "jobTitle": "資深前端工程師",
    "departmentId": "D001",
    "numberOfPositions": 2,
    "status": "OPEN",
    "salaryRange": "60K-80K",
    "openDate": "2026-02-01",
    "createdAt": "2026-02-09T09:00:00Z"
  }
}
```

---

### 1.2 應徵者管理 Command

#### RCT_CMD_002: 新增應徵者

**業務場景描述：**
應徵者線上提交履歷、或 HR 代為上傳候選人資料，系統建立應徵者記錄，將狀態設為 'NEW'。

**API 端點：**
```
POST /api/v1/recruitment/candidates
```

**前置條件：**
- 職缺必須存在且狀態為 'OPEN'
- 應徵者尚未對該職缺提交

**輸入 (Request)：**
```json
{
  "openingId": "job-001",
  "fullName": "王小明",
  "email": "wang.xiaoming@example.com",
  "phoneNumber": "0912-345-678",
  "appliedDate": "2026-02-10",
  "source": "104",
  "sourceDetail": "104 人力銀行",
  "education": "MASTER",
  "major": "資訊工程",
  "university": "國立台灣大學",
  "yearsOfExperience": 5,
  "currentCompany": "某科技公司",
  "currentPosition": "前端工程師",
  "expectedSalary": 75000,
  "resumeUrl": "https://storage.company.com/resumes/wang_xiaoming_20260210.pdf",
  "linkedinUrl": "https://linkedin.com/in/wangxiaoming",
  "portfolioUrl": "https://github.com/wangxiaoming",
  "inTalentPool": false,
  "referrerId": "E001",
  "notes": "技術背景強，GitHub 專案實踐豐富"
}
```

**業務規則驗證：**

1. ✅ **職缺存在性檢查**
   - 查詢條件：`opening_id = ? AND status = 'OPEN'`
   - 預期結果：職缺存在且為開放狀態

2. ✅ **重複應徵檢查**
   - 查詢條件：`opening_id = ? AND email = ? AND status != 'WITHDRAWN'`
   - 預期結果：不存在該應徵者的重複應徵記錄

3. ✅ **聯絡資訊驗證**
   - 規則：email 格式合法，phoneNumber 為有效台灣電話號碼
   - 預期結果：聯絡資訊通過驗證

4. ✅ **經驗年數合理性檢查**
   - 規則：yearsOfExperience >= 0 且 <= 60
   - 預期結果：經驗年數邏輯合理

5. ✅ **期望薪資合理性檢查**
   - 規則：expectedSalary 在職缺薪資範圍附近（±20%）
   - 預期結果：薪資期望值合理

**必須發布的領域事件：**
```json
{
  "eventId": "evt-rct-candidate-apply-001",
  "eventType": "CandidateAppliedEvent",
  "timestamp": "2026-02-10T10:30:00Z",
  "aggregateId": "candidate-001",
  "aggregateType": "Candidate",
  "payload": {
    "candidateId": "candidate-001",
    "openingId": "job-001",
    "jobTitle": "資深前端工程師",
    "fullName": "王小明",
    "email": "wang.xiaoming@example.com",
    "phoneNumber": "0912-345-678",
    "source": "104",
    "education": "MASTER",
    "yearsOfExperience": 5,
    "expectedSalary": 75000,
    "appliedAt": "2026-02-10T10:30:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "candidateId": "candidate-001",
    "openingId": "job-001",
    "fullName": "王小明",
    "email": "wang.xiaoming@example.com",
    "stage": "NEW",
    "status": "ACTIVE",
    "appliedDate": "2026-02-10",
    "createdAt": "2026-02-10T10:30:00Z"
  }
}
```

---

### 1.3 面試和 Offer 管理 Command

#### RCT_CMD_003: 安排面試

**業務場景描述：**
HR 為通過篩選的應徵者安排面試，指定面試官、日期、時間、地點等，系統發送通知給面試官和應徵者。

**API 端點：**
```
POST /api/v1/recruitment/interviews/schedule
```

**前置條件：**
- 執行者必須擁有 `recruitment:interview:manage` 權限
- 應徵者必須存在且狀態為 'SCREENING' 或 'INTERVIEWING'
- 面試官必須存在於 Organization Service

**輸入 (Request)：**
```json
{
  "candidateId": "candidate-001",
  "openingId": "job-001",
  "interviewRound": 1,
  "interviewType": "TECHNICAL",
  "interviewDate": "2026-02-20T14:00:00Z",
  "location": "台北辦公室 3F 會議室A",
  "duration": 60,
  "interviewers": [
    {
      "employeeId": "E001",
      "employeeName": "李組長"
    },
    {
      "employeeId": "E002",
      "employeeName": "張經理"
    }
  ],
  "description": "第一輪技術面試，主要評估 React 和系統設計能力"
}
```

**業務規則驗證：**

1. ✅ **應徵者存在性檢查**
   - 查詢條件：`candidate_id = ? AND status = 'ACTIVE'`
   - 預期結果：應徵者存在

2. ✅ **應徵者狀態檢查**
   - 規則：應徵者狀態必須為 'SCREENING' 或 'INTERVIEWING'
   - 預期結果：允許進行面試

3. ✅ **面試官存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：所有面試官都存在且為 ACTIVE

4. ✅ **面試日期合理性檢查**
   - 規則：interviewDate > now，不與其他面試衝突
   - 預期結果：日期邏輯正確，沒有時間衝突

5. ✅ **面試輪數合理性檢查**
   - 規則：同一應徵者同一職缺最多 3 輪面試
   - 預期結果：面試輪數不超限

**必須發布的領域事件：**
```json
{
  "eventId": "evt-rct-interview-sched-001",
  "eventType": "InterviewScheduledEvent",
  "timestamp": "2026-02-15T09:00:00Z",
  "aggregateId": "interview-001",
  "aggregateType": "Interview",
  "payload": {
    "interviewId": "interview-001",
    "candidateId": "candidate-001",
    "candidateName": "王小明",
    "candidateEmail": "wang.xiaoming@example.com",
    "openingId": "job-001",
    "jobTitle": "資深前端工程師",
    "interviewRound": 1,
    "interviewType": "TECHNICAL",
    "interviewDate": "2026-02-20T14:00:00Z",
    "location": "台北辦公室 3F 會議室A",
    "duration": 60,
    "interviewers": [
      {
        "employeeId": "E001",
        "employeeName": "李組長",
        "email": "lee@company.com"
      },
      {
        "employeeId": "E002",
        "employeeName": "張經理",
        "email": "zhang@company.com"
      }
    ],
    "scheduledBy": "HR001",
    "scheduledAt": "2026-02-15T09:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "interviewId": "interview-001",
    "candidateId": "candidate-001",
    "interviewDate": "2026-02-20T14:00:00Z",
    "interviewers": [
      {
        "employeeId": "E001",
        "employeeName": "李組長"
      },
      {
        "employeeId": "E002",
        "employeeName": "張經理"
      }
    ],
    "status": "SCHEDULED",
    "createdAt": "2026-02-15T09:00:00Z"
  }
}
```

---

#### RCT_CMD_004: 發送 Offer

**業務場景描述：**
面試全部完成且評分通過後，HR 生成並發送 Offer 給應徵者，設定期限與起薪。

**API 端點：**
```
POST /api/v1/recruitment/offers
```

**前置條件：**
- 執行者必須擁有 `recruitment:offer:manage` 權限
- 應徵者必須存在且面試都已完成
- 該職缺尚未招滿

**輸入 (Request)：**
```json
{
  "candidateId": "candidate-001",
  "openingId": "job-001",
  "offeredPosition": "資深前端工程師",
  "offeredSalary": 75000,
  "salaryFrequency": "MONTHLY",
  "offeredStartDate": "2026-03-01",
  "offerValidityDays": 14,
  "benefits": "健保、勞保、年假20天、年終獎金",
  "jobDescription": "主要負責 React 應用開發與維護",
  "employmentType": "FULL_TIME",
  "reportingTo": "李組長",
  "probationPeriod": 3,
  "notes": "根據面試評分，建議起薪 75K"
}
```

**業務規則驗證：**

1. ✅ **應徵者面試完成檢查**
   - 查詢條件：所有面試都已 COMPLETED 且評分通過
   - 預期結果：應徵者已完成足夠的面試輪數

2. ✅ **Offer 重複檢查**
   - 查詢條件：`candidate_id = ? AND opening_id = ? AND status NOT IN ('DECLINED', 'EXPIRED')`
   - 預期結果：不存在有效的同職缺 Offer

3. ✅ **薪資合理性檢查**
   - 規則：offeredSalary 在職缺薪資範圍內（salaryMin ~ salaryMax）
   - 預期結果：薪資在合理範圍

4. ✅ **起薪日期合理性檢查**
   - 規則：offeredStartDate > now + 7 days（預留 1 週處理期）
   - 預期結果：起薪日期邏輯合理

5. ✅ **職缺招募人數檢查**
   - 查詢條件：`opening_id = ? AND status = 'HIRED'`
   - 預期結果：已錄取人數 < numberOfPositions

**必須發布的領域事件：**
```json
{
  "eventId": "evt-rct-offer-001",
  "eventType": "OfferSentEvent",
  "timestamp": "2026-02-25T10:00:00Z",
  "aggregateId": "offer-001",
  "aggregateType": "Offer",
  "payload": {
    "offerId": "offer-001",
    "candidateId": "candidate-001",
    "candidateName": "王小明",
    "candidateEmail": "wang.xiaoming@example.com",
    "openingId": "job-001",
    "offeredPosition": "資深前端工程師",
    "offeredSalary": 75000,
    "offeredStartDate": "2026-03-01",
    "offerDate": "2026-02-25",
    "expiryDate": "2026-03-11",
    "offerDocumentUrl": "https://storage.company.com/offers/offer_candidate_001_20260225.pdf",
    "sentAt": "2026-02-25T10:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "offerId": "offer-001",
    "candidateId": "candidate-001",
    "offeredSalary": 75000,
    "offeredStartDate": "2026-03-01",
    "status": "SENT",
    "expiryDate": "2026-03-11",
    "sentAt": "2026-02-25T10:00:00Z"
  }
}
```

---

#### RCT_CMD_005: 確認錄取

**業務場景描述：**
應徵者接受 Offer 後，HR 確認錄取，系統自動在 Organization Service 建立員工資料、更新應徵者狀態。

**API 端點：**
```
PUT /api/v1/recruitment/candidates/{id}/hire
```

**前置條件：**
- 執行者必須擁有 `recruitment:candidate:hire` 權限
- 應徵者必須存在且 Offer 已被接受
- 該職缺尚未招滿

**輸入 (Request)：**
```json
{
  "offerId": "offer-001",
  "hireDate": "2026-02-25",
  "employeeNumber": "E0025",
  "startDate": "2026-03-01",
  "employmentType": "FULL_TIME",
  "probationEndDate": "2026-06-01",
  "initialBaseSalary": 75000,
  "notes": "技術背景強，重點培養"
}
```

**業務規則驗證：**

1. ✅ **Offer 狀態檢查**
   - 查詢條件：`offer_id = ? AND status = 'ACCEPTED'`
   - 預期結果：Offer 已被應徵者接受

2. ✅ **職缺招募狀態檢查**
   - 查詢條件：已錄取人數是否達到 numberOfPositions
   - 預期結果：職缺還有位置

3. ✅ **員工編號唯一性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：employeeNumber 不重複

4. ✅ **起薪日期合理性檢查**
   - 規則：startDate 與 Offer 的 offeredStartDate 一致或晚於
   - 預期結果：日期邏輯合理

**必須發布的領域事件：**
```json
{
  "eventId": "evt-rct-hired-001",
  "eventType": "CandidateHiredEvent",
  "timestamp": "2026-02-25T15:00:00Z",
  "aggregateId": "candidate-001",
  "aggregateType": "Candidate",
  "payload": {
    "candidateId": "candidate-001",
    "fullName": "王小明",
    "email": "wang.xiaoming@example.com",
    "phoneNumber": "0912-345-678",
    "openingId": "job-001",
    "jobTitle": "資深前端工程師",
    "departmentId": "D001",
    "departmentName": "研發部",
    "offeredSalary": 75000,
    "expectedStartDate": "2026-03-01",
    "resumeUrl": "https://storage.company.com/resumes/wang_xiaoming_20260210.pdf",
    "referrerId": null,
    "referrerName": null,
    "hiredBy": "HR001",
    "hiredByName": "李小姐",
    "hiredAt": "2026-02-25T15:00:00Z",
    "employeeNumber": "E0025"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "candidateId": "candidate-001",
    "stage": "HIRED",
    "status": "ACTIVE",
    "employeeNumber": "E0025",
    "startDate": "2026-03-01",
    "hiredAt": "2026-02-25T15:00:00Z"
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 職缺查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RCT_QRY_J001 | 查詢開放職缺 | HR | `GET /api/v1/recruitment/job-openings` | `{"status":"OPEN"}` | `status = 'OPEN'` |
| RCT_QRY_J002 | 查詢已關閉職缺 | HR | `GET /api/v1/recruitment/job-openings` | `{"status":"CLOSED"}` | `status = 'CLOSED'` |
| RCT_QRY_J003 | 依部門查詢職缺 | HR | `GET /api/v1/recruitment/job-openings` | `{"departmentId":"D001"}` | `department_id = 'D001'` |
| RCT_QRY_J004 | 依職位查詢 | HR | `GET /api/v1/recruitment/job-openings` | `{"positionId":"P001"}` | `position_id = 'P001'` |
| RCT_QRY_J005 | 查詢急聘職缺 | HR | `GET /api/v1/recruitment/job-openings` | `{"priority":"URGENT"}` | `priority = 'URGENT'`, `status = 'OPEN'` |
| RCT_QRY_J006 | 依名稱模糊查詢 | HR | `GET /api/v1/recruitment/job-openings` | `{"title":"工程師"}` | `job_title LIKE '%工程師%'` |
| RCT_QRY_J007 | 查詢公開職缺 | PUBLIC | `GET /api/v1/recruitment/job-openings/public` | `{}` | `visibility = 'PUBLIC'`, `status = 'OPEN'` |
| RCT_QRY_J008 | 主管查詢部門職缺 | MANAGER | `GET /api/v1/recruitment/job-openings/my-department` | `{}` | `department_id IN ('{managedDeptIds}')`，`status = 'OPEN'` |
| RCT_QRY_J009 | 依招募負責人查詢 | HR | `GET /api/v1/recruitment/job-openings` | `{"recruiterId":"E001"}` | `recruiter_id = 'E001'` |

---

### 2.2 應徵者查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RCT_QRY_C001 | 查詢職缺應徵者 | HR | `GET /api/v1/recruitment/candidates` | `{"jobId":"J001"}` | `opening_id = 'J001'`, `status = 'ACTIVE'` |
| RCT_QRY_C002 | 查詢新進應徵者 | HR | `GET /api/v1/recruitment/candidates` | `{"stage":"NEW"}` | `stage = 'NEW'`, `status = 'ACTIVE'` |
| RCT_QRY_C003 | 查詢面試中應徵者 | HR | `GET /api/v1/recruitment/candidates` | `{"stage":"INTERVIEWING"}` | `stage = 'INTERVIEWING'`, `status = 'ACTIVE'` |
| RCT_QRY_C004 | 查詢已錄取應徵者 | HR | `GET /api/v1/recruitment/candidates` | `{"stage":"HIRED"}` | `stage = 'HIRED'` |
| RCT_QRY_C005 | 查詢已拒絕應徵者 | HR | `GET /api/v1/recruitment/candidates` | `{"stage":"REJECTED"}` | `stage = 'REJECTED'` |
| RCT_QRY_C006 | 依姓名模糊查詢 | HR | `GET /api/v1/recruitment/candidates` | `{"name":"王"}` | `full_name LIKE '%王%'`, `status != 'WITHDRAWN'` |
| RCT_QRY_C007 | 依來源查詢 | HR | `GET /api/v1/recruitment/candidates` | `{"source":"104"}` | `source = '104'`, `status = 'ACTIVE'` |
| RCT_QRY_C008 | 依學歷查詢 | HR | `GET /api/v1/recruitment/candidates` | `{"education":"MASTER"}` | `education = 'MASTER'`, `status = 'ACTIVE'` |
| RCT_QRY_C009 | 查詢人才庫 | HR | `GET /api/v1/recruitment/candidates` | `{"inTalentPool":true}` | `in_talent_pool = 1`, `status = 'ACTIVE'` |
| RCT_QRY_C010 | 主管查詢部門應徵者 | MANAGER | `GET /api/v1/recruitment/candidates/my-department` | `{}` | `opening.department_id IN ('{managedDeptIds}')`，`status = 'ACTIVE'` |

---

### 2.3 面試查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RCT_QRY_I001 | 查詢應徵者面試 | HR | `GET /api/v1/recruitment/interviews` | `{"candidateId":"C001"}` | `candidate_id = 'C001'` |
| RCT_QRY_I002 | 查詢今日面試 | HR | `GET /api/v1/recruitment/interviews` | `{"interviewDate":"2026-02-20"}` | `DATE(interview_date) = '2026-02-20'`, `status = 'SCHEDULED'` |
| RCT_QRY_I003 | 查詢面試官的面試 | INTERVIEWER | `GET /api/v1/recruitment/interviews/my` | `{}` | `interviewers.employee_id = '{currentUserId}'`, `status IN ('SCHEDULED', 'COMPLETED')` |
| RCT_QRY_I004 | 查詢待評分面試 | HR | `GET /api/v1/recruitment/interviews` | `{"status":"PENDING_EVALUATION"}` | `status = 'PENDING_EVALUATION'` |
| RCT_QRY_I005 | 查詢已完成面試 | HR | `GET /api/v1/recruitment/interviews` | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| RCT_QRY_I006 | 依面試類型查詢 | HR | `GET /api/v1/recruitment/interviews` | `{"type":"TECHNICAL"}` | `interview_type = 'TECHNICAL'` |
| RCT_QRY_I007 | 依日期範圍查詢 | HR | `GET /api/v1/recruitment/interviews` | `{"startDate":"2026-02-01","endDate":"2026-02-28"}` | `interview_date >= '2026-02-01'`, `interview_date <= '2026-02-28'` |

---

### 2.4 Offer 查詢

#### 2.4.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RCT_QRY_O001 | 查詢應徵者 Offer | HR | `GET /api/v1/recruitment/offers` | `{"candidateId":"C001"}` | `candidate_id = 'C001'` |
| RCT_QRY_O002 | 查詢待簽核 Offer | HR | `GET /api/v1/recruitment/offers` | `{"status":"PENDING_APPROVAL"}` | `status = 'PENDING_APPROVAL'` |
| RCT_QRY_O003 | 查詢已發送 Offer | HR | `GET /api/v1/recruitment/offers` | `{"status":"SENT"}` | `status = 'SENT'`, `expiry_date >= CURDATE()` |
| RCT_QRY_O004 | 查詢已接受 Offer | HR | `GET /api/v1/recruitment/offers` | `{"status":"ACCEPTED"}` | `status = 'ACCEPTED'` |
| RCT_QRY_O005 | 查詢已拒絕 Offer | HR | `GET /api/v1/recruitment/offers` | `{"status":"DECLINED"}` | `status = 'DECLINED'` |

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `JobOpeningCreatedEvent` | 建立職缺 | Recruitment | - | 記錄職缺到招募日誌 |
| `CandidateAppliedEvent` | 應徵者提交履歷 | Recruitment | - | 增加應徵者統計 |
| `InterviewScheduledEvent` | 安排面試 | Recruitment | Notification | 通知面試官和應徵者 |
| `OfferSentEvent` | 發送 Offer | Recruitment | Notification | 通知應徵者收到 Offer |
| `CandidateHiredEvent` | 確認錄取 | Recruitment | Organization, Notification | 自動建立員工資料、通知相關人員 |

---

### 3.2 InterviewScheduledEvent (面試排程事件)

**觸發時機：**
HR 為應徵者安排面試後發布此事件，通知面試官和應徵者。

**Event Payload:**
```json
{
  "eventId": "evt-rct-interview-sched-001",
  "eventType": "InterviewScheduledEvent",
  "timestamp": "2026-02-15T09:00:00Z",
  "aggregateId": "interview-001",
  "aggregateType": "Interview",
  "payload": {
    "interviewId": "interview-001",
    "candidateId": "candidate-001",
    "candidateName": "王小明",
    "candidateEmail": "wang.xiaoming@example.com",
    "candidatePhoneNumber": "0912-345-678",
    "openingId": "job-001",
    "jobTitle": "資深前端工程師",
    "departmentName": "研發部",
    "interviewRound": 1,
    "interviewType": "TECHNICAL",
    "interviewDate": "2026-02-20T14:00:00Z",
    "duration": 60,
    "location": "台北辦公室 3F 會議室A",
    "interviewers": [
      {
        "employeeId": "E001",
        "employeeName": "李組長",
        "email": "lee@company.com",
        "department": "研發部"
      },
      {
        "employeeId": "E002",
        "employeeName": "張經理",
        "email": "zhang@company.com",
        "department": "研發部"
      }
    ],
    "description": "第一輪技術面試，主要評估 React 和系統設計能力",
    "scheduledBy": "HR001",
    "scheduledByName": "李小姐",
    "scheduledAt": "2026-02-15T09:00:00Z"
  },
  "metadata": {
    "userId": "HR001",
    "userName": "李小姐",
    "source": "Recruitment Service",
    "version": "1.0"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送 Email 給應徵者：面試邀請、時間、地點、面試官資訊
  - 發送 Email 給每位面試官：面試日程、應徵者簡介、評估重點
  - 可選：發送日曆邀請至面試官的公司郵件

---

### 3.3 OfferSentEvent (Offer 發送事件)

**觸發時機：**
HR 生成並發送 Offer 給應徵者後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-rct-offer-001",
  "eventType": "OfferSentEvent",
  "timestamp": "2026-02-25T10:00:00Z",
  "aggregateId": "offer-001",
  "aggregateType": "Offer",
  "payload": {
    "offerId": "offer-001",
    "candidateId": "candidate-001",
    "candidateName": "王小明",
    "candidateEmail": "wang.xiaoming@example.com",
    "candidatePhoneNumber": "0912-345-678",
    "openingId": "job-001",
    "offeredPosition": "資深前端工程師",
    "departmentName": "研發部",
    "offeredSalary": 75000,
    "salaryFrequency": "MONTHLY",
    "offeredStartDate": "2026-03-01",
    "employmentType": "FULL_TIME",
    "probationPeriod": 3,
    "benefits": "健保、勞保、年假20天、年終獎金",
    "reportingTo": "李組長",
    "offerDate": "2026-02-25",
    "expiryDate": "2026-03-11",
    "validityDays": 14,
    "offerDocumentUrl": "https://storage.company.com/offers/offer_candidate_001_20260225.pdf",
    "sentBy": "HR001",
    "sentByName": "李小姐",
    "sentAt": "2026-02-25T10:00:00Z"
  },
  "metadata": {
    "userId": "HR001",
    "userName": "李小姐",
    "source": "Recruitment Service",
    "version": "1.0"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送 Email 給應徵者：Offer 內容、起薪、福利、回覆截止日
  - 包含 Offer 文件 PDF 連結
  - 提供線上簽核或回覆連結

---

### 3.4 CandidateHiredEvent (錄取事件 - 重要)

**觸發時機：**
HR 確認應徵者接受 Offer 並完成錄取流程後發布此事件，觸發員工資料自動建立。

**Event Payload:**
```json
{
  "eventId": "evt-rct-hired-001",
  "eventType": "CandidateHiredEvent",
  "timestamp": "2026-02-25T15:00:00Z",
  "aggregateId": "candidate-001",
  "aggregateType": "Candidate",
  "payload": {
    "candidateId": "candidate-001",
    "fullName": "王小明",
    "email": "wang.xiaoming@example.com",
    "personalEmail": "wang.xiaoming@gmail.com",
    "phoneNumber": "0912-345-678",
    "openingId": "job-001",
    "jobTitle": "資深前端工程師",
    "departmentId": "D001",
    "departmentName": "研發部",
    "positionId": "P001",
    "positionName": "資深工程師",
    "offeredSalary": 75000,
    "expectedStartDate": "2026-03-01",
    "employmentType": "FULL_TIME",
    "probationEndDate": "2026-06-01",
    "resumeUrl": "https://storage.company.com/resumes/wang_xiaoming_20260210.pdf",
    "linkedinUrl": "https://linkedin.com/in/wangxiaoming",
    "portfolioUrl": "https://github.com/wangxiaoming",
    "referrerId": null,
    "referrerName": null,
    "education": "MASTER",
    "major": "資訊工程",
    "university": "國立台灣大學",
    "yearsOfExperience": 5,
    "previousCompany": "某科技公司",
    "previousPosition": "前端工程師",
    "hiredBy": "HR001",
    "hiredByName": "李小姐",
    "hiredAt": "2026-02-25T15:00:00Z",
    "offerId": "offer-001",
    "employeeNumber": "E0025"
  },
  "metadata": {
    "userId": "HR001",
    "userName": "李小姐",
    "source": "Recruitment Service",
    "version": "1.0"
  }
}
```

**訂閱服務處理：**

- **Organization Service（重要）:**
  - 自動建立員工記錄
  - 設置員工編號：E0025
  - 初始狀態：PROBATION（試用期）
  - 設置主管指派（reportingTo 資訊）
  - 設置薪資結構（baseSalary: 75000）
  - 建立員工檔案（履歷、推薦人等）

- **Notification Service:**
  - 發送 Email 歡迎新員工，提供入職須知
  - 通知直屬主管新員工已錄取
  - 通知 HR 部門更新員工名冊

- **IAM Service:**
  - 建立員工帳號（username: wang.xiaoming）
  - 設置初始密碼並發送登入資訊

- **Insurance Service（如適用）:**
  - 根據起薪日期自動準備保險加保資料

---

## 4. 測試斷言規格

### 4.1 Command 操作測試斷言

**測試目標：** 驗證 Command 操作是否正確執行業務規則並發布領域事件。

**範例：RCT_CMD_001 建立職缺測試**

```java
@Test
@DisplayName("RCT_CMD_001: 建立職缺 - 應驗證薪資範圍並發布事件")
void createJobOpening_ShouldValidateSalaryAndPublishEvent() {
    // Given
    var request = CreateJobOpeningRequest.builder()
        .jobTitle("資深前端工程師")
        .departmentId("D001")
        .numberOfPositions(2)
        .salaryMin(60000)
        .salaryMax(80000)
        .openDate(LocalDate.now())
        .targetCloseDate(LocalDate.now().plusMonths(1))
        .build();

    // Mock department exists
    when(organizationService.departmentExists("D001")).thenReturn(true);

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify job opening saved
    var captor = ArgumentCaptor.forClass(JobOpening.class);
    verify(jobOpeningRepository).save(captor.capture());

    var savedJob = captor.getValue();
    assertThat(savedJob.getStatus()).isEqualTo("OPEN");
    assertThat(savedJob.getSalaryMin()).isEqualTo(60000);

    // Then - Verify event published
    var eventCaptor = ArgumentCaptor.forClass(JobOpeningCreatedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("JobOpeningCreatedEvent");
}
```

---

### 4.2 Query 操作測試斷言

**測試目標：** 驗證 Query 操作是否正確套用過濾條件與權限控制。

**範例：RCT_QRY_C001 查詢職缺應徵者測試**

```java
@Test
@DisplayName("RCT_QRY_C001: 查詢職缺應徵者 - 應過濾指定職缺和有效狀態")
void searchCandidates_ByOpening_ShouldIncludeRequiredFilters() {
    // Given
    String contractSpec = loadContractSpec("recruitment");

    var request = CandidateSearchRequest.builder()
        .jobId("J001")
        .build();

    // When
    var captor = ArgumentCaptor.forClass(QueryGroup.class);
    service.getResponse(request, currentUser);

    // Then
    verify(candidateRepository).findPage(captor.capture(), any());

    var queryGroup = captor.getValue();
    assertContract(queryGroup, contractSpec, "RCT_QRY_C001");

    // Additional assertions
    assertThat(queryGroup).containsFilter("opening_id", Operator.EQUAL, "J001");
    assertThat(queryGroup).containsFilter("status", Operator.EQUAL, "ACTIVE");
}
```

---

### 4.3 Integration Test 斷言

**測試目標：** 驗證完整的 API → Service → Repository 流程。

**範例：RCT_CMD_005 確認錄取整合測試**

```java
@Test
@DisplayName("RCT_CMD_005: 確認錄取整合測試 - 應建立員工資料並發布事件")
void hireCandidate_Integration_ShouldCreateEmployeeAndPublishEvent() throws Exception {
    // Given
    var request = HireCandidateRequest.builder()
        .offerId("offer-001")
        .employeeNumber("E0025")
        .startDate(LocalDate.of(2026, 3, 1))
        .build();

    // When
    var result = mockMvc.perform(put("/api/v1/recruitment/candidates/{id}/hire", "candidate-001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.stage").value("HIRED"))
        .andReturn();

    // Then - Verify database
    var candidate = candidateRepository.findById("candidate-001");
    assertThat(candidate).isPresent();
    assertThat(candidate.get().getStage()).isEqualTo("HIRED");
    assertThat(candidate.get().getEmployeeNumber()).isEqualTo("E0025");

    // Then - Verify event published (Organization Service will be called)
    var eventCaptor = ArgumentCaptor.forClass(CandidateHiredEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getPayload().getEmployeeNumber()).isEqualTo("E0025");
}
```

---

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 職缺使用 `status` 欄位（'OPEN', 'CLOSED', 'CANCELLED', 'FILLED'）
   - 應徵者使用 `status` 欄位（'ACTIVE', 'HIRED', 'REJECTED', 'WITHDRAWN'）
   - 面試使用 `status` 欄位（'SCHEDULED', 'COMPLETED', 'CANCELLED'）
   - Offer 使用 `status` 欄位（'PENDING_APPROVAL', 'SENT', 'ACCEPTED', 'DECLINED', 'EXPIRED'）
   - **不使用 `is_deleted` 欄位**

2. **個人資料保護:**
   - 應徵者個資依法規保護，只有授權人員可查看
   - 面試評分不向應徵者公開
   - 拒絕理由應依公司政策保密

3. **租戶隔離:**
   - 所有查詢自動加上 `tenant_id = ?` 過濾條件

4. **招募隱私:**
   - 薪資資訊應保密，避免洩露到應徵者數據中

---

### 5.2 應徵者狀態機

```
NEW
  ↓
SCREENING（篩選中）
  ├→ REJECTED（已拒絕）
  └→ INTERVIEWING（面試中）
      ├→ REJECTED（已拒絕）
      └→ OFFER（Offer 階段）
          ├→ REJECTED（已拒絕）
          └→ HIRED（已錄取）

任何狀態 → WITHDRAWN（已撤回）
```

---

### 5.3 角色權限說明

| 角色 | 可執行操作 | 可查詢範圍 |
|:---|:---|:---|
| **HR** | 建立職缺、安排面試、發送 Offer、確認錄取、管理應徵者 | 全公司招募資訊 |
| **MANAGER** | 查詢部門職缺、參與面試評分 | 所管轄部門職缺和應徵者 |
| **INTERVIEWER** | 查詢自己的面試、提交評分 | 被指派的面試資訊 |
| **PUBLIC** | 查詢公開職缺、線上投遞 | 公開職缺資訊 |

---

### 5.4 應徵來源代碼

| 代碼 | 說明 |
|:---|:---|
| **104** | 104 人力銀行 |
| **1111** | 1111 人力銀行 |
| **LINKEDIN** | LinkedIn 招募 |
| **INTERNAL** | 內部推薦 |
| **EMPLOYEE_REFERRAL** | 員工推薦 |
| **RECRUITMENT_AGENCY** | 人力仲介 |
| **CAREER_FAIR** | 校園徵才活動 |
| **COMPANY_WEBSITE** | 公司官網 |

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景、業務規則驗證、Domain Events Payload 定義、測試斷言規格 |
| 1.0 | 2025-12-19 | 精簡版建立 |
