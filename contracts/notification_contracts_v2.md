# 通知服務業務合約 (Notification Service Business Contract)

> **服務代碼:** HR12
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/12_通知服務系統設計書.md`
> - `knowledge/04_API_Specifications/12_通知服務系統設計書_API詳細規格.md`

---

## 📋 概述

本合約文件定義通知服務的**完整業務場景**，包括：
1. **Command 操作場景**（發送、標記、廣播）- 驗證業務規則與領域事件
2. **Query 操作場景**（查詢）- 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景
- ✅ 新增領域事件的詳細定義
- ✅ 對應到實際的 API 端點
- ✅ 使用實際存在的欄位（使用 status 欄位）
- ✅ 包含完整的業務規則驗證

**服務定位：**
通知服務負責多通道通知（Email、系統內通知、推播、Teams、LINE）的發送、範本管理、用戶偏好設定等功能。本服務基於事件驅動架構，訂閱其他業務服務的域事件並發送相應通知。

**資料軟刪除策略：**
- **通知記錄**: 使用 `status` 欄位，'PENDING' 為待發送，'SENT' 為已發送，'FAILED' 為發送失敗，'READ' 為已讀
- **範本資料**: 使用 `is_active` 欄位，true 為有效，false 為停用
- **偏好設定**: 不進行軟刪除，只保留最新的有效設定

**角色權限說明：**
- **EMPLOYEE**: 查詢自己的通知、更新偏好設定
- **HR**: 查詢所有員工通知、發送廣播通知、管理範本
- **ADMIN**: 全部操作

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [發送通知](#11-發送通知)
   - 1.2 [標記已讀](#12-標記已讀)
   - 1.3 [廣播通知](#13-廣播通知)
   - 1.4 [範本管理](#14-範本管理)
   - 1.5 [偏好設定](#15-偏好設定)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [我的通知查詢](#21-我的通知查詢)
   - 2.2 [通知日誌查詢](#22-通知日誌查詢)
   - 2.3 [未讀計數查詢](#23-未讀計數查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 發送通知

#### NTF_CMD_001: 發送單一通知

**業務場景描述：**
當業務服務發生事件時（如請假核准、薪資單產生），通知服務訂閱事件並發送通知給相關人員。例如請假核准後，系統自動發送通知給申請人。

**API 端點：**
```
POST /api/v1/notifications/send
```

**前置條件：**
- 執行者必須擁有 `notification:send` 權限或為系統服務
- recipientId 必須存在於 Organization Service
- templateCode 對應的範本必須存在且為活跃状態

**輸入 (Request)：**
```json
{
  "recipientId": "E001",
  "templateCode": "LEAVE_APPROVED",
  "variables": {
    "employeeName": "王小華",
    "leaveType": "特休",
    "startDate": "2026-02-10",
    "endDate": "2026-02-12",
    "totalDays": 3
  },
  "channels": ["IN_APP", "EMAIL"],
  "priority": "NORMAL",
  "relatedBusinessType": "LEAVE_APPLICATION",
  "relatedBusinessId": "leave-001"
}
```

**業務規則驗證：**

1. ✅ **收件人存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

2. ✅ **範本存在性檢查**
   - 查詢條件：`template_code = ? AND is_active = true`
   - 預期結果：範本存在且為 ACTIVE

3. ✅ **範本變數驗證**
   - 規則：請求提供的變數必須包含範本所需的所有必填變數
   - 範本範例：LEAVE_APPROVED 需要 `employeeName, leaveType, startDate, endDate, totalDays`

4. ✅ **通知渠道有效性檢查**
   - 規則：只能選擇 ['IN_APP', 'EMAIL', 'PUSH', 'TEAMS', 'LINE'] 其中之一或多個
   - 預期結果：至少選擇一個通道

5. ✅ **優先級有效性檢查**
   - 規則：priority 必須為 ['LOW', 'NORMAL', 'HIGH', 'URGENT'] 其中之一

**必須發布的領域事件：**
```json
{
  "eventId": "evt-ntf-send-001",
  "eventType": "NotificationSentEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "notification-001",
  "aggregateType": "Notification",
  "payload": {
    "notificationId": "notification-001",
    "recipientId": "E001",
    "recipientName": "王小華",
    "title": "請假申請已核准",
    "content": "您的特休請假申請已獲得核准，2026-02-10 至 2026-02-12，共 3 天。",
    "notificationType": "APPROVAL_RESULT",
    "channels": ["IN_APP", "EMAIL"],
    "priority": "NORMAL",
    "status": "SENT",
    "sentAt": "2026-02-09T09:00:00Z",
    "relatedBusinessType": "LEAVE_APPLICATION",
    "relatedBusinessId": "leave-001"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "notificationId": "notification-001",
    "recipientId": "E001",
    "status": "SENT",
    "channels": ["IN_APP", "EMAIL"],
    "sentAt": "2026-02-09T09:00:00Z"
  }
}
```

---

### 1.2 標記已讀

#### NTF_CMD_002: 標記單一通知為已讀

**業務場景描述：**
員工在應用程序中查看通知後，點擊通知以標記為已讀，系統更新通知狀態並記錄查閱時間。

**API 端點：**
```
PUT /api/v1/notifications/{id}/read
```

**前置條件：**
- 執行者必須是該通知的收件人或 HR/ADMIN
- 通知必須存在且狀態為 'SENT'

**輸入 (Request)：**
```json
{
  "notificationId": "notification-001"
}
```

**業務規則驗證：**

1. ✅ **通知所有權檢查**
   - 查詢條件：`notification_id = ? AND recipient_id = ?`
   - 預期結果：通知屬於當前用戶或用戶為 HR/ADMIN

2. ✅ **通知狀態檢查**
   - 查詢條件：`notification_id = ? AND status = 'SENT'`
   - 預期結果：通知狀態為 'SENT'，只有已發送的通知才能標記為已讀

3. ✅ **重複標記防止**
   - 規則：如果通知已為 'READ' 狀態，不再重複發布事件

**必須發布的領域事件：**
```json
{
  "eventId": "evt-ntf-read-001",
  "eventType": "NotificationReadEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "notification-001",
  "aggregateType": "Notification",
  "payload": {
    "notificationId": "notification-001",
    "recipientId": "E001",
    "readAt": "2026-02-09T10:00:00Z",
    "previousStatus": "SENT"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "notificationId": "notification-001",
    "status": "READ",
    "readAt": "2026-02-09T10:00:00Z"
  }
}
```

---

#### NTF_CMD_003: 標記所有通知為已讀

**業務場景描述：**
員工在通知中心點擊「全部標記為已讀」按鈕，系統批量更新所有未讀通知為已讀狀態。

**API 端點：**
```
PUT /api/v1/notifications/read-all
```

**前置條件：**
- 執行者必須擁有基本權限（所有用戶都可調用）
- 至少存在一條未讀通知

**輸入 (Request)：**
```json
{}
```

**業務規則驗證：**

1. ✅ **未讀通知查詢**
   - 查詢條件：`recipient_id = ? AND status = 'SENT'`
   - 預期結果：查詢當前用戶的所有未讀通知

2. ✅ **批量更新檢查**
   - 規則：至少要有一條通知才能執行批量標記

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "totalUpdated": 5,
    "updatedAt": "2026-02-09T10:05:00Z"
  }
}
```

---

### 1.3 廣播通知

#### NTF_CMD_004: 發送廣播通知

**業務場景描述：**
HR 管理員發送系統公告或重要通知給所有員工或特定部門的員工。

**API 端點：**
```
POST /api/v1/notifications/broadcast
```

**前置條件：**
- 執行者必須擁有 `notification:broadcast` 權限（僅 HR/ADMIN）
- 目標受眾集合不為空

**輸入 (Request)：**
```json
{
  "title": "系統升級通知",
  "content": "系統將於 2026-02-15 晚上 10 點進行升級維護，預期停機時間 2 小時。",
  "notificationType": "ANNOUNCEMENT",
  "channels": ["IN_APP", "EMAIL"],
  "priority": "HIGH",
  "targetAudience": {
    "type": "DEPARTMENT",
    "departmentIds": ["D001", "D002"]
  }
}
```

**業務規則驗證：**

1. ✅ **廣播權限檢查**
   - 規則：只有 HR/ADMIN 可以發送廣播
   - 預期結果：用戶角色為 HR 或 ADMIN

2. ✅ **目標受眾驗證**
   - 規則：targetAudience.type 必須為 ['ALL', 'DEPARTMENT', 'ROLE']
   - 如果 type 為 'DEPARTMENT'，必須提供至少一個有效的 departmentId

3. ✅ **內容長度檢查**
   - 規則：title 最多 255 字，content 最多 5000 字

4. ✅ **接收者計數**
   - 規則：基於目標受眾查詢實際接收人數，不可為 0

**必須發布的領域事件：**
```json
{
  "eventId": "evt-ntf-broadcast-001",
  "eventType": "BroadcastNotificationSentEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "broadcast-001",
  "aggregateType": "BroadcastNotification",
  "payload": {
    "broadcastId": "broadcast-001",
    "senderId": "admin-001",
    "senderName": "系統管理員",
    "title": "系統升級通知",
    "content": "系統將於 2026-02-15 晚上 10 點進行升級維護，預期停機時間 2 小時。",
    "notificationType": "ANNOUNCEMENT",
    "channels": ["IN_APP", "EMAIL"],
    "priority": "HIGH",
    "targetAudience": {
      "type": "DEPARTMENT",
      "departmentIds": ["D001", "D002"]
    },
    "totalRecipients": 85,
    "sentAt": "2026-02-09T11:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "broadcastId": "broadcast-001",
    "totalRecipients": 85,
    "sentAt": "2026-02-09T11:00:00Z"
  }
}
```

---

### 1.4 範本管理

#### NTF_CMD_005: 建立通知範本

**業務場景描述：**
HR 管理員建立新的通知範本，定義通知的標題、內容、預設通道等。範本可以包含變數，在發送時進行替換。

**API 端點：**
```
POST /api/v1/notifications/templates
```

**前置條件：**
- 執行者必須擁有 `notification:template:manage` 權限（僅 HR/ADMIN）
- templateCode 必須唯一

**輸入 (Request)：**
```json
{
  "templateCode": "LEAVE_APPROVED",
  "templateName": "請假核准通知",
  "subject": "您的請假申請已獲得核准",
  "body": "親愛的 {{employeeName}}，\n\n您的 {{leaveType}} 請假申請已獲得核准。\n請假期間：{{startDate}} 至 {{endDate}}，共 {{totalDays}} 天。\n\n祝好！\n人資部",
  "defaultChannels": ["IN_APP", "EMAIL"],
  "variables": [
    {
      "name": "employeeName",
      "type": "STRING",
      "required": true,
      "description": "員工名稱"
    },
    {
      "name": "leaveType",
      "type": "STRING",
      "required": true,
      "description": "請假類型"
    },
    {
      "name": "startDate",
      "type": "DATE",
      "required": true,
      "description": "開始日期"
    },
    {
      "name": "endDate",
      "type": "DATE",
      "required": true,
      "description": "結束日期"
    },
    {
      "name": "totalDays",
      "type": "INTEGER",
      "required": true,
      "description": "請假天數"
    }
  ]
}
```

**業務規則驗證：**

1. ✅ **範本代碼唯一性檢查**
   - 查詢條件：`template_code = ?`
   - 預期結果：不存在重複的範本代碼

2. ✅ **範本代碼格式檢查**
   - 規則：只允許大寫字母、數字和下劃線，最多 100 字符

3. ✅ **模板內容驗證**
   - 規則：subject 和 body 必須包含 {{variable}} 格式的變數引用
   - 實際提供的變數必須匹配模板中使用的變數

4. ✅ **預設通道驗證**
   - 規則：defaultChannels 必須是 ['IN_APP', 'EMAIL', 'PUSH', 'TEAMS', 'LINE'] 的子集

**必須發布的領域事件：**
```json
{
  "eventId": "evt-ntf-tmpl-001",
  "eventType": "NotificationTemplateCreatedEvent",
  "timestamp": "2026-02-09T12:00:00Z",
  "aggregateId": "template-001",
  "aggregateType": "NotificationTemplate",
  "payload": {
    "templateId": "template-001",
    "templateCode": "LEAVE_APPROVED",
    "templateName": "請假核准通知",
    "defaultChannels": ["IN_APP", "EMAIL"],
    "variables": ["employeeName", "leaveType", "startDate", "endDate", "totalDays"],
    "createdAt": "2026-02-09T12:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "templateId": "template-001",
    "templateCode": "LEAVE_APPROVED",
    "templateName": "請假核准通知",
    "createdAt": "2026-02-09T12:00:00Z"
  }
}
```

---

#### NTF_CMD_006: 更新通知範本

**業務場景描述：**
HR 管理員更新現有的通知範本內容，例如修改郵件模板的文本或預設通道。

**API 端點：**
```
PUT /api/v1/notifications/templates/{id}
```

**前置條件：**
- 執行者必須擁有 `notification:template:manage` 權限
- 範本必須存在

**輸入 (Request)：**
```json
{
  "templateName": "請假核准通知 (更新)",
  "subject": "您的請假申請已核准",
  "body": "親愛的 {{employeeName}}，\n\n您的 {{leaveType}} 請假已核准。\n期間：{{startDate}} - {{endDate}}（共{{totalDays}}天）。",
  "defaultChannels": ["IN_APP", "EMAIL", "PUSH"]
}
```

**業務規則驗證：**

1. ✅ **範本存在性檢查**
   - 查詢條件：`template_id = ?`
   - 預期結果：範本存在

2. ✅ **變數一致性檢查**
   - 規則：更新後的模板變數必須與原始範本保持一致或擴充（不可移除必填變數）

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "templateId": "template-001",
    "templateCode": "LEAVE_APPROVED",
    "updatedAt": "2026-02-09T13:00:00Z"
  }
}
```

---

### 1.5 偏好設定

#### NTF_CMD_007: 更新通知偏好設定

**業務場景描述：**
員工在個人設定頁面配置通知偏好，例如選擇接收哪些通道的通知、設置免擾時間。

**API 端點：**
```
PUT /api/v1/notifications/preferences
```

**前置條件：**
- 執行者可以是任何認證用戶
- 用戶偏好設定記錄可能不存在（首次創建）

**輸入 (Request)：**
```json
{
  "emailEnabled": true,
  "pushEnabled": true,
  "inAppEnabled": true,
  "teamsEnabled": false,
  "lineEnabled": false,
  "quietHoursStart": "22:00",
  "quietHoursEnd": "08:00"
}
```

**業務規則驗證：**

1. ✅ **至少啟用一個通道**
   - 規則：至少有一個通道必須為 true
   - 預期結果：emailEnabled, pushEnabled, inAppEnabled, teamsEnabled, lineEnabled 中至少一個為 true

2. ✅ **免擾時間有效性檢查**
   - 規則：如果設置了免擾時間，quietHoursStart 和 quietHoursEnd 必須符合 HH:mm 格式
   - 預期結果：時間格式有效且 start 時間 < end 時間（允許跨越午夜）

3. ✅ **用戶存在性檢查**
   - 呼叫 Organization Service 驗證用戶存在

**必須發布的領域事件：**
```json
{
  "eventId": "evt-ntf-pref-001",
  "eventType": "NotificationPreferenceUpdatedEvent",
  "timestamp": "2026-02-09T14:00:00Z",
  "aggregateId": "preference-E001",
  "aggregateType": "NotificationPreference",
  "payload": {
    "preferenceId": "preference-E001",
    "employeeId": "E001",
    "emailEnabled": true,
    "pushEnabled": true,
    "inAppEnabled": true,
    "teamsEnabled": false,
    "lineEnabled": false,
    "quietHoursStart": "22:00",
    "quietHoursEnd": "08:00",
    "updatedAt": "2026-02-09T14:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "preferenceId": "preference-E001",
    "employeeId": "E001",
    "emailEnabled": true,
    "pushEnabled": true,
    "inAppEnabled": true,
    "updatedAt": "2026-02-09T14:00:00Z"
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 我的通知查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| NTF_QRY_001 | 查詢未讀通知 | EMPLOYEE | `GET /api/v1/notifications/my` | `{"status":"SENT"}` | `recipient_id = '{currentUserId}'`, `status = 'SENT'` |
| NTF_QRY_002 | 查詢已讀通知 | EMPLOYEE | `GET /api/v1/notifications/my` | `{"status":"READ"}` | `recipient_id = '{currentUserId}'`, `status = 'READ'` |
| NTF_QRY_003 | 查詢通知歷史 | EMPLOYEE | `GET /api/v1/notifications/my/history` | `{"pageSize":20,"pageNo":1}` | `recipient_id = '{currentUserId}'` |
| NTF_QRY_004 | 依優先級查詢 | EMPLOYEE | `GET /api/v1/notifications/my` | `{"priority":"HIGH"}` | `recipient_id = '{currentUserId}'`, `priority = 'HIGH'` |
| NTF_QRY_005 | 依業務類型查詢 | EMPLOYEE | `GET /api/v1/notifications/my` | `{"relatedBusinessType":"LEAVE_APPLICATION"}` | `recipient_id = '{currentUserId}'`, `related_business_type = 'LEAVE_APPLICATION'` |

#### 2.1.2 業務場景說明

**NTF_QRY_001: 查詢未讀通知**

- **使用者：** 員工自助查詢
- **業務目的：** 在通知中心顯示未讀通知列表
- **權限控制：** 無需特殊權限，但只能查詢自己的通知
- **過濾邏輯：**
  ```sql
  WHERE recipient_id = '{currentUserId}'
    AND status = 'SENT'
  ORDER BY created_at DESC
  ```

**NTF_QRY_003: 查詢通知歷史**

- **使用者：** 員工自助查詢
- **業務目的：** 查看已讀和未讀的所有歷史通知
- **權限控制：** 無需特殊權限，但只能查詢自己
- **過濾邏輯：**
  ```sql
  WHERE recipient_id = '{currentUserId}'
  ORDER BY created_at DESC
  LIMIT {pageSize} OFFSET {(pageNo-1)*pageSize}
  ```

---

### 2.2 通知日誌查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| NTF_QRY_006 | 查詢通知發送日誌 | HR | `GET /api/v1/notification-logs` | `{"channel":"EMAIL"}` | `channel = 'EMAIL'` |
| NTF_QRY_007 | 查詢失敗的通知 | HR | `GET /api/v1/notification-logs` | `{"status":"FAILED"}` | `status = 'FAILED'` |
| NTF_QRY_008 | 依範本查詢 | HR | `GET /api/v1/notification-logs` | `{"templateCode":"LEAVE_APPROVED"}` | `template_code = 'LEAVE_APPROVED'` |
| NTF_QRY_009 | 查詢特定時間範圍的通知 | HR | `GET /api/v1/notification-logs` | `{"startDate":"2026-02-01","endDate":"2026-02-28"}` | `created_at >= '2026-02-01' AND created_at < '2026-03-01'` |

#### 2.2.2 業務場景說明

**NTF_QRY_006: 查詢通知發送日誌**

- **使用者：** HR 管理員
- **業務目的：** 查看通知發送日誌，審計通知發送情況
- **權限控制：** `notification:log:read`
- **過濾邏輯：**
  ```sql
  WHERE channel = 'EMAIL'
  ORDER BY created_at DESC
  ```

---

### 2.3 未讀計數查詢

#### 2.3.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| NTF_QRY_010 | 查詢未讀計數 | EMPLOYEE | `GET /api/v1/notifications/unread-count` | `{}` | `recipient_id = '{currentUserId}'`, `status = 'SENT'` |
| NTF_QRY_011 | 查詢優先級未讀計數 | EMPLOYEE | `GET /api/v1/notifications/unread-count` | `{"priority":"HIGH"}` | `recipient_id = '{currentUserId}'`, `status = 'SENT'`, `priority = 'HIGH'` |

#### 2.3.2 業務場景說明

**NTF_QRY_010: 查詢未讀計數**

- **使用者：** 員工查看頂部通知鈴鐺顯示的未讀數
- **業務目的：** 快速取得未讀計數用於 UI 顯示
- **權限控制：** 無需特殊權限，但只能查詢自己
- **過濾邏輯：**
  ```sql
  SELECT COUNT(*) as unread_count
  FROM notifications
  WHERE recipient_id = '{currentUserId}'
    AND status = 'SENT'
  ```

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `NotificationSentEvent` | 通知發送成功 | Notification | - | - |
| `NotificationReadEvent` | 通知被標記為已讀 | Notification | - | - |
| `BroadcastNotificationSentEvent` | 廣播通知發送 | Notification | Monitoring | 監控廣播覆蓋率 |
| `NotificationFailedEvent` | 通知發送失敗 | Notification | Monitoring | 記錄失敗日誌、觸發告警 |
| `NotificationTemplateCreatedEvent` | 建立新範本 | Notification | - | - |
| `NotificationPreferenceUpdatedEvent` | 用戶偏好變更 | Notification | - | - |

---

### 3.2 NotificationSentEvent (通知發送成功事件)

**觸發時機：**
通知通過所有指定通道成功發送後，發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-ntf-send-001",
  "eventType": "NotificationSentEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "notification-001",
  "aggregateType": "Notification",
  "payload": {
    "notificationId": "notification-001",
    "recipientId": "E001",
    "recipientName": "王小華",
    "title": "請假申請已核准",
    "content": "您的特休請假申請已獲得核准，2026-02-10 至 2026-02-12，共 3 天。",
    "notificationType": "APPROVAL_RESULT",
    "channels": ["IN_APP", "EMAIL"],
    "priority": "NORMAL",
    "status": "SENT",
    "sentAt": "2026-02-09T09:00:00Z",
    "relatedBusinessType": "LEAVE_APPLICATION",
    "relatedBusinessId": "leave-001",
    "templateCode": "LEAVE_APPROVED"
  }
}
```

**訂閱服務處理：**
- 此事件主要用於日誌記錄和審計追蹤，無特殊訂閱服務

---

### 3.3 NotificationFailedEvent (通知發送失敗事件)

**觸發時機：**
通知在某個或所有通道上發送失敗時，發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-ntf-fail-001",
  "eventType": "NotificationFailedEvent",
  "timestamp": "2026-02-09T09:05:00Z",
  "aggregateId": "notification-001",
  "aggregateType": "Notification",
  "payload": {
    "notificationId": "notification-001",
    "recipientId": "E001",
    "recipientName": "王小華",
    "failedChannels": ["EMAIL"],
    "successfulChannels": ["IN_APP"],
    "errorMessage": "SMTP 伺服器連線逾時",
    "errorCode": "SMTP_TIMEOUT",
    "retryCount": 1,
    "maxRetries": 3,
    "willRetry": true,
    "failedAt": "2026-02-09T09:05:00Z"
  }
}
```

**訂閱服務處理：**

- **Monitoring Service:**
  - 記錄失敗日誌
  - 如果 failedChannels 包含所有指定通道，發送告警
  - 跟蹤重試次數，達到 maxRetries 後升級為嚴重告警

---

### 3.4 BroadcastNotificationSentEvent (廣播通知發送事件)

**觸發時機：**
廣播通知成功發送給所有目標受眾時發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-ntf-broadcast-001",
  "eventType": "BroadcastNotificationSentEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "broadcast-001",
  "aggregateType": "BroadcastNotification",
  "payload": {
    "broadcastId": "broadcast-001",
    "senderId": "admin-001",
    "senderName": "系統管理員",
    "title": "系統升級通知",
    "content": "系統將於 2026-02-15 晚上 10 點進行升級維護，預期停機時間 2 小時。",
    "notificationType": "ANNOUNCEMENT",
    "channels": ["IN_APP", "EMAIL"],
    "priority": "HIGH",
    "targetAudience": {
      "type": "DEPARTMENT",
      "departmentIds": ["D001", "D002"]
    },
    "totalRecipients": 85,
    "sentAt": "2026-02-09T11:00:00Z"
  }
}
```

**訂閱服務處理：**

- **Monitoring Service:**
  - 記錄廣播通知的發送統計
  - 跟蹤覆蓋率（實際收到 / 目標受眾數）

---

## 4. 測試斷言規格

### 4.1 Command 操作測試斷言

**測試目標：** 驗證 Command 操作是否正確執行業務規則並發布領域事件。

**範例：NTF_CMD_001 發送通知測試**

```java
@Test
@DisplayName("NTF_CMD_001: 發送單一通知 - 應正確發送並發布事件")
void sendNotification_ShouldSendAndPublishEvent() {
    // Given
    var request = SendNotificationRequest.builder()
        .recipientId("E001")
        .templateCode("LEAVE_APPROVED")
        .variables(Map.of(
            "employeeName", "王小華",
            "leaveType", "特休",
            "startDate", "2026-02-10",
            "endDate", "2026-02-12",
            "totalDays", 3
        ))
        .channels(List.of(NotificationChannel.IN_APP, NotificationChannel.EMAIL))
        .priority(Priority.NORMAL)
        .build();

    // Mock employee exists
    when(organizationService.employeeExists("E001")).thenReturn(true);

    // Mock template exists
    var template = new NotificationTemplate();
    template.setTemplateCode("LEAVE_APPROVED");
    template.setBody("親愛的 {{employeeName}}，您的 {{leaveType}} 已核准...");
    when(templateRepository.findByCode("LEAVE_APPROVED")).thenReturn(Optional.of(template));

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify notification saved
    var captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(captor.capture());

    var savedNotification = captor.getValue();
    assertThat(savedNotification.getRecipientId()).isEqualTo("E001");
    assertThat(savedNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
    assertThat(savedNotification.getChannels()).containsExactlyInAnyOrder(
        NotificationChannel.IN_APP, NotificationChannel.EMAIL);

    // Then - Verify event published
    var eventCaptor = ArgumentCaptor.forClass(NotificationSentEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("NotificationSentEvent");
    assertThat(event.getPayload().getRecipientId()).isEqualTo("E001");
}
```

---

### 4.2 Query 操作測試斷言

**測試目標：** 驗證 Query 操作是否正確套用過濾條件與權限控制。

**範例：NTF_QRY_001 查詢未讀通知測試**

```java
@Test
@DisplayName("NTF_QRY_001: 查詢未讀通知 - 應包含收件人ID與狀態過濾")
void queryUnreadNotifications_ShouldIncludeRequiredFilters() {
    // Given
    var request = QueryNotificationRequest.builder()
        .status(NotificationStatus.SENT)
        .build();

    JWTModel currentUser = JWTModel.builder()
        .userId("E001")
        .build();

    // When
    var captor = ArgumentCaptor.forClass(QueryGroup.class);
    service.getResponse(request, currentUser);

    // Then
    verify(notificationRepository).findPage(captor.capture(), any());

    var queryGroup = captor.getValue();

    // Verify required filters
    assertThat(queryGroup).containsFilter("recipient_id", Operator.EQUAL, "E001");
    assertThat(queryGroup).containsFilter("status", Operator.EQUAL, NotificationStatus.SENT);
}
```

---

### 4.3 Integration Test 斷言

**測試目標：** 驗證完整的 API → Service → Repository 流程。

**範例：NTF_CMD_001 整合測試**

```java
@Test
@DisplayName("NTF_CMD_001: 發送通知整合測試 - 應建立記錄並返回正確回應")
void sendNotification_Integration_ShouldCreateRecordAndReturnResponse() throws Exception {
    // Given
    var request = SendNotificationRequest.builder()
        .recipientId("E001")
        .templateCode("LEAVE_APPROVED")
        .variables(Map.of(
            "employeeName", "王小華",
            "leaveType", "特休",
            "startDate", "2026-02-10",
            "endDate", "2026-02-12",
            "totalDays", 3
        ))
        .channels(List.of("IN_APP", "EMAIL"))
        .priority("NORMAL")
        .build();

    // When
    var result = mockMvc.perform(post("/api/v1/notifications/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.notificationId").isNotEmpty())
        .andExpect(jsonPath("$.data.status").value("SENT"))
        .andReturn();

    // Then - Verify database
    var notification = notificationRepository.findByRecipientId("E001").getFirst();
    assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
    assertThat(notification.getChannels()).containsExactlyInAnyOrder("IN_APP", "EMAIL");
}
```

---

## 補充說明

### 5.1 通用安全規則

1. **軟刪除過濾:**
   - 通知記錄使用 `status` 欄位，不使用 `is_deleted` 欄位
   - 範本使用 `is_active` 欄位

2. **個人資料保護:**
   - 員工只能查詢自己的通知
   - 廣播通知的收件人名單不向普通員工公開

3. **租戶隔離:**
   - 所有查詢自動加上 `tenant_id = ?` 過濾條件

### 5.2 多通道發送邏輯

通知可同時指定多個通道：
- **IN_APP**: WebSocket 即時推送到系統內通知中心
- **EMAIL**: 發送 HTML 郵件至員工郵箱
- **PUSH**: 推送至 Firebase FCM（iOS/Android）
- **TEAMS**: 發送至 Microsoft Teams
- **LINE**: 發送至 LINE Notify

如果某個通道發送失敗，系統會：
1. 記錄失敗信息
2. 發布 `NotificationFailedEvent`
3. 根據重試策略決定是否重試
4. 最後更新通知狀態為 FAILED（如果所有通道都失敗）

### 5.3 免擾時間說明

如果員工設置了免擾時間（quietHoursStart 到 quietHoursEnd），系統將：
- 在免擾時間內不發送 PUSH/EMAIL 通知
- 仍然發送 IN_APP 通知（用戶可自行查看）
- 對於 URGENT 優先級通知，忽略免擾時間，必須發送

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景、業務規則驗證、Domain Events Payload 定義、測試斷言規格 |
| 1.0 | 2026-02-06 | 精簡版建立 |
