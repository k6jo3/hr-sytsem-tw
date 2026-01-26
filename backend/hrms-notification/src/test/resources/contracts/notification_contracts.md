# 通知服務業務合約 (Notification Service Contract)

> **服務代碼:** 12
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義通知服務的業務合約，涵蓋通知發送、通知紀錄、訂閱管理等查詢場景。

---

## 1. 通知訊息查詢合約 (Notification Message Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| NTF_M001 | 查詢個人通知 | EMPLOYEE | `{}` | `recipient_id = '{currentUserId}'` |
| NTF_M002 | 查詢未讀通知 | EMPLOYEE | `{"isRead":false}` | `recipient_id = '{currentUserId}'`, `is_read = 0` |
| NTF_M003 | 查詢已讀通知 | EMPLOYEE | `{"isRead":true}` | `recipient_id = '{currentUserId}'`, `is_read = 1` |
| NTF_M004 | 依類型查詢通知 | EMPLOYEE | `{"type":"SYSTEM"}` | `recipient_id = '{currentUserId}'`, `type = 'SYSTEM'` |
| NTF_M005 | 依優先級查詢 | EMPLOYEE | `{"priority":"HIGH"}` | `recipient_id = '{currentUserId}'`, `priority = 'HIGH'` |
| NTF_M006 | 查詢最近通知 | EMPLOYEE | `{"days":7}` | `recipient_id = '{currentUserId}'`, `created_at >= '{today-7days}'` |
| NTF_M007 | HR 查詢全部通知 | HR | `{}` | `1 = 1` |
| NTF_M008 | 依發送狀態查詢 | HR | `{"status":"SENT"}` | `status = 'SENT'` |
| NTF_M009 | 依發送失敗查詢 | HR | `{"status":"FAILED"}` | `status = 'FAILED'` |

---

## 2. 通知範本查詢合約 (Notification Template Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| NTF_T001 | 查詢啟用範本 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| NTF_T002 | 依類型查詢範本 | HR | `{"type":"EMAIL"}` | `type = 'EMAIL'`, `is_deleted = 0` |
| NTF_T003 | 依事件類型查詢 | HR | `{"eventType":"LEAVE_APPROVED"}` | `event_type = 'LEAVE_APPROVED'`, `is_deleted = 0` |
| NTF_T004 | 依名稱模糊查詢 | HR | `{"name":"請假"}` | `name LIKE '請假'`, `is_deleted = 0` |
| NTF_T005 | 查詢停用範本 | HR | `{"status":"INACTIVE"}` | `status = 'INACTIVE'`, `is_deleted = 0` |

---

## 3. 通知訂閱查詢合約 (Notification Subscription Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| NTF_S001 | 查詢個人訂閱 | EMPLOYEE | `{}` | `user_id = '{currentUserId}'`, `is_deleted = 0` |
| NTF_S002 | 查詢啟用訂閱 | EMPLOYEE | `{"enabled":true}` | `user_id = '{currentUserId}'`, `enabled = 1`, `is_deleted = 0` |
| NTF_S003 | 依通知類型查詢 | EMPLOYEE | `{"notificationType":"EMAIL"}` | `user_id = '{currentUserId}'`, `notification_type = 'EMAIL'`, `is_deleted = 0` |
| NTF_S004 | HR 查詢全部訂閱 | HR | `{}` | `is_deleted = 0` |
| NTF_S005 | 依事件類型查詢 | HR | `{"eventType":"LEAVE"}` | `event_type = 'LEAVE'`, `is_deleted = 0` |

---

## 4. 通知發送紀錄查詢合約 (Notification Log Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| NTF_L001 | 查詢發送成功紀錄 | HR | `{"status":"SUCCESS"}` | `status = 'SUCCESS'` |
| NTF_L002 | 查詢發送失敗紀錄 | HR | `{"status":"FAILED"}` | `status = 'FAILED'` |
| NTF_L003 | 依通知管道查詢 | HR | `{"channel":"EMAIL"}` | `channel = 'EMAIL'` |
| NTF_L004 | 依日期範圍查詢 | HR | `{"startDate":"2025-01-01"}` | `sent_at >= '2025-01-01'` |
| NTF_L005 | 依收件人查詢 | HR | `{"recipientId":"E001"}` | `recipient_id = 'E001'` |

---

## 補充說明

### 通用安全規則

1. **個人隔離**: 員工只能查詢自己的通知
2. **範本管理**: 只有 HR 可管理通知範本
3. **隱私保護**: 通知內容可能包含敏感資料

### 通知管道代碼

| 代碼 | 說明 |
|:---|:---|
| EMAIL | 電子郵件 |
| PUSH | 推播通知 |
| SMS | 簡訊 |
| TEAMS | Microsoft Teams |
| LINE | LINE 通知 |
| IN_APP | 系統內通知 |

### 通知類型代碼

| 代碼 | 說明 |
|:---|:---|
| SYSTEM | 系統通知 |
| WORKFLOW | 流程通知 |
| REMINDER | 提醒通知 |
| ANNOUNCEMENT | 公告通知 |
| ALERT | 警示通知 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全部通知與範本 | 完整管理權限 |
| EMPLOYEE | 僅自己的通知 | 可管理自己的訂閱 |
