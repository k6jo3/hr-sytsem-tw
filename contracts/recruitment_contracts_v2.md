# 招募管理服務業務合約 (Recruitment Service Contract)

> **服務代碼:** HR09
> **版本:** 2.0
> **建立日期:** 2026-02-06

---

## 查詢操作合約

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RCT_QRY_001 | 查詢開放職缺 | HR | `GET /api/v1/job-postings` | `{"status":"OPEN"}` | `status = 'OPEN'` |
| RCT_QRY_002 | 查詢已關閉職缺 | HR | `GET /api/v1/job-postings` | `{"status":"CLOSED"}` | `status = 'CLOSED'` |
| RCT_QRY_003 | 查詢應徵者 | HR | `GET /api/v1/candidates` | `{"jobPostingId":"JP001"}` | `job_posting_id = 'JP001'` |
| RCT_QRY_004 | 查詢面試安排 | HR | `GET /api/v1/interviews` | `{"date":"2025-01-15"}` | `interview_date = '2025-01-15'` |

---

## 命令操作合約

| 場景 ID | API 端點 | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- |
| RCT_CMD_001 | `POST /api/v1/job-postings` | 職位編制檢查 | `JobPostingCreated` |
| RCT_CMD_002 | `POST /api/v1/candidates` | 重複應徵檢查 | `CandidateApplied` |
| RCT_CMD_003 | `POST /api/v1/interviews` | 面試官可用性檢查 | `InterviewScheduled` |
| RCT_CMD_004 | `POST /api/v1/candidates/{id}/hire` | 職缺名額檢查 | `CandidateHired` |

---

## Domain Events

| 事件名稱 | 觸發時機 | 訂閱服務 |
|:---|:---|:---|
| `JobPostingCreated` | 建立職缺 | Notification |
| `CandidateApplied` | 應徵者投遞 | Notification |
| `InterviewScheduled` | 安排面試 | Notification |
| `CandidateHired` | 錄取應徵者 | Organization (建立員工) |

---

**軟刪除:** 使用 `status` 欄位，不使用 `is_deleted`

**版本:** 2.0 | 2026-02-06
