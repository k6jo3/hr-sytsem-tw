# 招募管理服務業務合約 (Recruitment Service Contract)

> **服務代碼:** 09
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義招募管理服務的業務合約，涵蓋職缺管理、應徵者追蹤、面試排程等查詢場景。

---

## 1. 職缺查詢合約 (Job Opening Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| RCT_J001 | 查詢開放職缺 | HR | `{"status":"OPEN"}` | `status = 'OPEN'`, `is_deleted = 0` |
| RCT_J002 | 查詢已關閉職缺 | HR | `{"status":"CLOSED"}` | `status = 'CLOSED'`, `is_deleted = 0` |
| RCT_J003 | 依部門查詢職缺 | HR | `{"deptId":"D001"}` | `department_id = 'D001'`, `is_deleted = 0` |
| RCT_J004 | 依職位查詢 | HR | `{"positionId":"P001"}` | `position_id = 'P001'`, `is_deleted = 0` |
| RCT_J005 | 查詢急聘職缺 | HR | `{"priority":"URGENT"}` | `priority = 'URGENT'`, `is_deleted = 0` |
| RCT_J006 | 依名稱模糊查詢 | HR | `{"title":"工程師"}` | `title LIKE '%工程師%'`, `is_deleted = 0` |
| RCT_J007 | 查詢公開職缺 | PUBLIC | `{}` | `visibility = 'PUBLIC'`, `status = 'OPEN'`, `is_deleted = 0` |
| RCT_J008 | 主管查詢部門職缺 | MANAGER | `{}` | `department_id IN ('{managedDeptIds}')`, `is_deleted = 0` |
| RCT_J009 | 依招募負責人查詢 | HR | `{"recruiterId":"E001"}` | `recruiter_id = 'E001'`, `is_deleted = 0` |

---

## 2. 應徵者查詢合約 (Candidate Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| RCT_C001 | 查詢職缺應徵者 | HR | `{"jobId":"J001"}` | `job_id = 'J001'`, `is_deleted = 0` |
| RCT_C002 | 查詢新進應徵者 | HR | `{"stage":"NEW"}` | `stage = 'NEW'`, `is_deleted = 0` |
| RCT_C003 | 查詢面試中應徵者 | HR | `{"stage":"INTERVIEWING"}` | `stage = 'INTERVIEWING'`, `is_deleted = 0` |
| RCT_C004 | 查詢已錄取應徵者 | HR | `{"stage":"HIRED"}` | `stage = 'HIRED'`, `is_deleted = 0` |
| RCT_C005 | 查詢已拒絕應徵者 | HR | `{"stage":"REJECTED"}` | `stage = 'REJECTED'`, `is_deleted = 0` |
| RCT_C006 | 依姓名模糊查詢 | HR | `{"name":"王"}` | `name LIKE '%王%'`, `is_deleted = 0` |
| RCT_C007 | 依來源查詢 | HR | `{"source":"104"}` | `source = '104'`, `is_deleted = 0` |
| RCT_C008 | 依學歷查詢 | HR | `{"education":"MASTER"}` | `education = 'MASTER'`, `is_deleted = 0` |
| RCT_C009 | 查詢人才庫 | HR | `{"inTalentPool":true}` | `in_talent_pool = 1`, `is_deleted = 0` |
| RCT_C010 | 主管查詢部門應徵者 | MANAGER | `{}` | `job.department_id IN ('{managedDeptIds}')`, `is_deleted = 0` |

---

## 3. 面試排程查詢合約 (Interview Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| RCT_I001 | 查詢應徵者面試 | HR | `{"candidateId":"C001"}` | `candidate_id = 'C001'` |
| RCT_I002 | 查詢今日面試 | HR | `{"interviewDate":"2025-01-15"}` | `interview_date = '2025-01-15'` |
| RCT_I003 | 查詢面試官的面試 | INTERVIEWER | `{}` | `interviewers.employee_id = '{currentUserId}'` |
| RCT_I004 | 查詢待評分面試 | HR | `{"status":"PENDING_FEEDBACK"}` | `status = 'PENDING_FEEDBACK'` |
| RCT_I005 | 查詢已完成面試 | HR | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| RCT_I006 | 依面試類型查詢 | HR | `{"type":"TECHNICAL"}` | `type = 'TECHNICAL'` |
| RCT_I007 | 依日期範圍查詢 | HR | `{"startDate":"2025-01-01","endDate":"2025-01-31"}` | `interview_date >= '2025-01-01'`, `interview_date <= '2025-01-31'` |

---

## 4. 錄取通知查詢合約 (Offer Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| RCT_O001 | 查詢應徵者 Offer | HR | `{"candidateId":"C001"}` | `candidate_id = 'C001'`, `is_deleted = 0` |
| RCT_O002 | 查詢待簽核 Offer | HR | `{"status":"PENDING_APPROVAL"}` | `status = 'PENDING_APPROVAL'`, `is_deleted = 0` |
| RCT_O003 | 查詢已發送 Offer | HR | `{"status":"SENT"}` | `status = 'SENT'`, `is_deleted = 0` |
| RCT_O004 | 查詢已接受 Offer | HR | `{"status":"ACCEPTED"}` | `status = 'ACCEPTED'`, `is_deleted = 0` |
| RCT_O005 | 查詢已拒絕 Offer | HR | `{"status":"DECLINED"}` | `status = 'DECLINED'`, `is_deleted = 0` |

---

## 補充說明

### 通用安全規則

1. **軟刪除過濾**: 主檔查詢須包含 `is_deleted = 0`
2. **部門隔離**: 主管只能查詢所管轄部門的招募資訊
3. **隱私保護**: 應徵者個資需依法保護

### 應徵者階段代碼

| 代碼 | 說明 |
|:---|:---|
| NEW | 新進 |
| SCREENING | 篩選中 |
| INTERVIEWING | 面試中 |
| OFFER | Offer 階段 |
| HIRED | 已錄取 |
| REJECTED | 已拒絕 |
| WITHDRAWN | 已撤回 |

### 面試類型代碼

| 代碼 | 說明 |
|:---|:---|
| PHONE | 電話面試 |
| VIDEO | 視訊面試 |
| ONSITE | 現場面試 |
| TECHNICAL | 技術面試 |
| HR | HR 面試 |
| FINAL | 最終面試 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全公司招募 | 完整管理權限 |
| MANAGER | 所管轄部門 | 可審核職缺、參與面試 |
| INTERVIEWER | 被指派的面試 | 只能填寫面試評分 |
| PUBLIC | 公開職缺 | 僅能查看公開資訊 |
