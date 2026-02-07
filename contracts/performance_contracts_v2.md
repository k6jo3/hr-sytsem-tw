# 績效管理服務業務合約 (Performance Service Contract)

> **服務代碼:** HR08
> **版本:** 2.0
> **建立日期:** 2026-02-06

---

## 查詢操作合約

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PFM_QRY_001 | 查詢進行中評核 | HR | `GET /api/v1/reviews` | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'` |
| PFM_QRY_002 | 查詢已完成評核 | HR | `GET /api/v1/reviews` | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| PFM_QRY_003 | 員工查詢自己評核 | EMPLOYEE | `GET /api/v1/reviews/my` | `{}` | `employee_id = '{currentUserId}'` |
| PFM_QRY_004 | 主管查詢下屬評核 | MANAGER | `GET /api/v1/reviews` | `{}` | `reviewer_id = '{currentUserId}'` |

---

## 命令操作合約

| 場景 ID | API 端點 | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- |
| PFM_CMD_001 | `POST /api/v1/reviews` | 評核週期檢查, 員工存在檢查 | `ReviewCreated` |
| PFM_CMD_002 | `POST /api/v1/reviews/{id}/submit` | 狀態檢查, 完整性檢查 | `ReviewSubmitted` |
| PFM_CMD_003 | `POST /api/v1/reviews/{id}/approve` | 權限檢查 | `ReviewApproved` |

---

## Domain Events

| 事件名稱 | 觸發時機 | 訂閱服務 |
|:---|:---|:---|
| `ReviewCreated` | 建立評核 | Notification |
| `ReviewSubmitted` | 提交評核 | Notification |
| `ReviewApproved` | 核准評核 | Payroll (影響績效獎金) |

---

**軟刪除:** 使用 `status` 欄位，不使用 `is_deleted`

**版本:** 2.0 | 2026-02-06
