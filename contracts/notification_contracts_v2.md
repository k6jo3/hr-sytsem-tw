# 通知服務業務合約 (Notification Service Contract)

> **服務代碼:** HR12
> **版本:** 2.0
> **建立日期:** 2026-02-06

---

## 查詢操作合約

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| NTF_QRY_001 | 查詢未讀通知 | EMPLOYEE | `GET /api/v1/notifications/my` | `{"isRead":false}` | `user_id = '{currentUserId}'`, `is_read = FALSE` |
| NTF_QRY_002 | 查詢已讀通知 | EMPLOYEE | `GET /api/v1/notifications/my` | `{"isRead":true}` | `user_id = '{currentUserId}'`, `is_read = TRUE` |
| NTF_QRY_003 | 查詢通知歷史 | EMPLOYEE | `GET /api/v1/notifications/my/history` | `{}` | `user_id = '{currentUserId}'` |
| NTF_QRY_004 | 查詢發送記錄 | HR | `GET /api/v1/notification-logs` | `{"channel":"EMAIL"}` | `channel = 'EMAIL'` |

---

## 命令操作合約

| 場景 ID | API 端點 | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- |
| NTF_CMD_001 | `POST /api/v1/notifications/send` | 收件人存在檢查, 範本存在檢查 | `NotificationSent` |
| NTF_CMD_002 | `PUT /api/v1/notifications/{id}/mark-read` | 通知所有權檢查 | `NotificationRead` |
| NTF_CMD_003 | `POST /api/v1/notifications/broadcast` | 廣播權限檢查 | `NotificationBroadcasted` |

---

## Domain Events

| 事件名稱 | 觸發時機 | 訂閱服務 |
|:---|:---|:---|
| `NotificationSent` | 發送通知 | - |
| `NotificationDeliveryFailed` | 發送失敗 | 重試機制 |

---

**軟刪除:** 通知不進行軟刪除，保留所有歷史記錄

**版本:** 2.0 | 2026-02-06
