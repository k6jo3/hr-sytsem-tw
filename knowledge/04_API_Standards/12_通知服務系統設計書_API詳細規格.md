# HR12 通知服務 API 詳細規格

**版本:** 1.0
**建立日期:** 2025-12-30
**服務代碼:** HR12 (NTF)
**服務名稱:** 通知服務 (Notification Service)

---

## 目錄

1. [API 總覽](#1-api-總覽)
2. [通知管理 API](#2-通知管理-api)
3. [通知範本管理 API](#3-通知範本管理-api)
4. [通知偏好設定 API](#4-通知偏好設定-api)
5. [公告管理 API](#5-公告管理-api)
6. [錯誤碼總覽](#6-錯誤碼總覽)
7. [領域事件總覽](#7-領域事件總覽)

---

## 1. API 總覽

### 1.1 端點清單

| 序號 | 端點 | 方法 | 說明 | Controller |
|:---:|:---|:---:|:---|:---|
| 1 | `/api/v1/notifications/send` | POST | 發送通知 | HR12NotificationCmdController |
| 2 | `/api/v1/notifications/send-batch` | POST | 批次發送通知 | HR12NotificationCmdController |
| 3 | `/api/v1/notifications/test` | POST | 發送測試通知 | HR12NotificationCmdController |
| 4 | `/api/v1/notifications/me` | GET | 查詢我的通知列表 | HR12NotificationQryController |
| 5 | `/api/v1/notifications/{id}` | GET | 查詢通知詳情 | HR12NotificationQryController |
| 6 | `/api/v1/notifications/{id}/read` | PUT | 標記通知為已讀 | HR12NotificationCmdController |
| 7 | `/api/v1/notifications/read-all` | PUT | 標記全部為已讀 | HR12NotificationCmdController |
| 8 | `/api/v1/notifications/{id}` | DELETE | 刪除通知 | HR12NotificationCmdController |
| 9 | `/api/v1/notifications/unread-count` | GET | 查詢未讀通知數量 | HR12NotificationQryController |
| 10 | `/api/v1/notifications/templates` | POST | 建立通知範本 | HR12TemplateCmdController |
| 11 | `/api/v1/notifications/templates` | GET | 查詢通知範本列表 | HR12TemplateQryController |
| 12 | `/api/v1/notifications/templates/{id}` | GET | 查詢通知範本詳情 | HR12TemplateQryController |
| 13 | `/api/v1/notifications/templates/{id}` | PUT | 更新通知範本 | HR12TemplateCmdController |
| 14 | `/api/v1/notifications/templates/{id}` | DELETE | 刪除通知範本 | HR12TemplateCmdController |
| 15 | `/api/v1/notifications/preferences` | GET | 查詢通知偏好設定 | HR12PreferenceQryController |
| 16 | `/api/v1/notifications/preferences` | PUT | 更新通知偏好設定 | HR12PreferenceCmdController |
| 17 | `/api/v1/notifications/announcements` | POST | 發布公告 | HR12AnnouncementCmdController |
| 18 | `/api/v1/notifications/announcements` | GET | 查詢公告列表 | HR12AnnouncementQryController |
| 19 | `/api/v1/notifications/announcements/{id}` | GET | 查詢公告詳情 | HR12AnnouncementQryController |
| 20 | `/api/v1/notifications/announcements/{id}` | PUT | 更新公告 | HR12AnnouncementCmdController |
| 21 | `/api/v1/notifications/announcements/{id}` | DELETE | 撤銷公告 | HR12AnnouncementCmdController |

### 1.2 通知渠道

| 渠道代碼 | 說明 | 技術實作 |
|:---|:---|:---|
| IN_APP | 系統內通知 | WebSocket (STOMP) |
| EMAIL | 電子郵件 | Spring Mail + SMTP |
| PUSH | 行動推播 | Firebase FCM |
| TEAMS | Microsoft Teams | Webhook |
| LINE | LINE 通知 | LINE Notify API |

### 1.3 通知類型

| 類型代碼 | 說明 | 常見場景 |
|:---|:---|:---|
| APPROVAL_REQUEST | 審核請求 | 請假/加班申請待審核 |
| APPROVAL_RESULT | 審核結果 | 請假/加班申請通過/駁回 |
| REMINDER | 提醒 | 合約到期、證照到期、生日 |
| ANNOUNCEMENT | 公告 | 系統公告、活動通知 |
| ALERT | 警示 | 異常警告、緊急通知 |

### 1.4 通知優先級

| 優先級 | 說明 | 處理方式 |
|:---|:---|:---|
| LOW | 低 | 不受靜音時段限制影響 |
| NORMAL | 一般 | 預設，遵守靜音時段 |
| HIGH | 高 | 即時發送，不等待 |
| URGENT | 緊急 | 忽略所有限制立即發送 |

---

## 2. 通知管理 API

### 2.1 發送通知

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/notifications/send` |
| **方法** | POST |
| **Controller** | HR12NotificationCmdController |
| **Service** | SendNotificationServiceImpl |
| **權限** | `NOTIFICATION:SEND` |

**用途說明**

- **業務場景:** 其他服務透過此 API 發送通知給指定員工
- **使用者:** 業務服務（透過 Feign Client）
- **注意:** 建議使用事件驅動方式發送通知，此 API 為備用方案

**業務邏輯**

1. 若指定 templateCode，載入範本並替換變數
2. 根據 recipientId 查詢通知偏好設定
3. 過濾禁用的渠道
4. 若在靜音時段且優先級非 URGENT，延後發送
5. 建立通知記錄
6. 依渠道發送通知（並行處理）
7. 更新發送狀態

**Request Body**

```json
{
  "recipientId": "emp-001",
  "title": "請假申請待審核",
  "content": "員工張三申請特休假2天（2025/12/20-2025/12/21），請您審核。",
  "notificationType": "APPROVAL_REQUEST",
  "channels": ["IN_APP", "EMAIL"],
  "priority": "NORMAL",
  "templateCode": "LEAVE_APPROVAL_REQUEST",
  "templateVariables": {
    "employeeName": "張三",
    "leaveType": "特休假",
    "totalDays": 2,
    "startDate": "2025/12/20",
    "endDate": "2025/12/21"
  },
  "businessType": "LEAVE_APPLICATION",
  "businessId": "leave-001",
  "businessUrl": "/attendance/leave/applications/leave-001"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| recipientId | String | ✅ | 有效員工 ID | 收件人員工 ID |
| title | String | ✅ | 1-200字元 | 通知標題 |
| content | String | 條件 | 最多2000字元 | 通知內容（無範本時必填） |
| notificationType | String | ✅ | 有效類型代碼 | 通知類型 |
| channels | Array | | 有效渠道代碼 | 發送渠道（預設 IN_APP） |
| priority | String | | 有效優先級 | 優先級（預設 NORMAL） |
| templateCode | String | | 存在的範本代碼 | 使用的通知範本 |
| templateVariables | Object | | | 範本變數 |
| businessType | String | | | 關聯業務類型 |
| businessId | String | | | 關聯業務 ID |
| businessUrl | String | | | 業務詳情連結 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "notificationId": "ntf-001",
    "recipientId": "emp-001",
    "title": "請假申請待審核",
    "status": "SENT",
    "channels": ["IN_APP", "EMAIL"],
    "sentAt": "2025-12-30T10:00:00Z",
    "channelResults": [
      { "channel": "IN_APP", "status": "SUCCESS" },
      { "channel": "EMAIL", "status": "SUCCESS" }
    ]
  },
  "message": "通知發送成功"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | NTF_RECIPIENT_NOT_FOUND | 收件人不存在 | 確認員工 ID 正確 |
| 400 | NTF_TEMPLATE_NOT_FOUND | 通知範本不存在 | 確認範本代碼正確 |
| 400 | NTF_CONTENT_REQUIRED | 未使用範本時必須提供內容 | 提供 content 或 templateCode |
| 400 | NTF_INVALID_CHANNEL | 無效的通知渠道 | 使用有效的渠道代碼 |

**領域事件**

| 事件名稱 | Kafka Topic | 說明 |
|:---|:---|:---|
| NotificationCreatedEvent | notification.created | 通知建立 |
| NotificationSentEvent | notification.sent | 通知發送成功 |
| NotificationFailedEvent | notification.failed | 通知發送失敗 |

---

### 2.2 批次發送通知

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/notifications/send-batch` |
| **方法** | POST |
| **Controller** | HR12NotificationCmdController |
| **Service** | SendBatchNotificationServiceImpl |
| **權限** | `NOTIFICATION:SEND_BATCH` |

**用途說明**

- **業務場景:** 發送通知給多位員工（如公告、群發提醒）
- **使用者:** 系統管理員、HR
- **注意:** 批次上限 500 人

**Request Body**

```json
{
  "recipientIds": ["emp-001", "emp-002", "emp-003"],
  "recipientFilter": {
    "departmentIds": ["dept-001"],
    "roleIds": ["role-001"]
  },
  "title": "12月份薪資單已產生",
  "content": "您的2025年12月薪資單已產生，請至系統查看。",
  "notificationType": "REMINDER",
  "channels": ["IN_APP", "EMAIL"],
  "priority": "NORMAL",
  "templateCode": "PAYSLIP_READY",
  "templateVariables": {
    "month": "2025年12月"
  }
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| recipientIds | Array | 條件 | 指定收件人 ID 列表 |
| recipientFilter | Object | 條件 | 收件人篩選條件 |
| recipientFilter.departmentIds | Array | | 部門 ID 列表 |
| recipientFilter.roleIds | Array | | 角色 ID 列表 |
| title | String | ✅ | 通知標題 |
| content | String | 條件 | 通知內容 |
| notificationType | String | ✅ | 通知類型 |
| channels | Array | | 發送渠道 |
| priority | String | | 優先級 |
| templateCode | String | | 使用的通知範本 |
| templateVariables | Object | | 範本變數 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "batchId": "batch-001",
    "totalRecipients": 150,
    "successCount": 148,
    "failedCount": 2,
    "status": "COMPLETED",
    "failedRecipients": [
      { "recipientId": "emp-099", "error": "Email 無效" },
      { "recipientId": "emp-100", "error": "員工已離職" }
    ]
  },
  "message": "批次發送完成"
}
```

---

### 2.3 查詢我的通知列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/notifications/me` |
| **方法** | GET |
| **Controller** | HR12NotificationQryController |
| **Service** | GetMyNotificationsServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 員工查看自己收到的通知
- **使用者:** 一般員工

**Query Parameters**

| 參數 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| status | String | | 狀態篩選（SENT/READ） |
| notificationType | String | | 類型篩選 |
| startDate | String | | 開始日期 |
| endDate | String | | 結束日期 |
| page | Integer | | 頁碼，預設 1 |
| pageSize | Integer | | 每頁筆數，預設 20 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "notificationId": "ntf-001",
        "title": "請假申請已核准",
        "content": "您的特休假申請（2025/12/20-2025/12/21）已核准。",
        "notificationType": "APPROVAL_RESULT",
        "priority": "NORMAL",
        "status": "SENT",
        "isRead": false,
        "businessType": "LEAVE_APPLICATION",
        "businessId": "leave-001",
        "businessUrl": "/attendance/leave/applications/leave-001",
        "createdAt": "2025-12-30T10:00:00Z"
      },
      {
        "notificationId": "ntf-002",
        "title": "12月份薪資單已產生",
        "content": "您的2025年12月薪資單已產生，請至系統查看。",
        "notificationType": "REMINDER",
        "priority": "NORMAL",
        "status": "READ",
        "isRead": true,
        "businessType": "PAYSLIP",
        "businessId": "payslip-001",
        "businessUrl": "/payroll/payslips/payslip-001",
        "createdAt": "2025-12-29T09:00:00Z",
        "readAt": "2025-12-29T10:30:00Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 25,
      "totalPages": 2
    },
    "summary": {
      "totalUnread": 5
    }
  }
}
```

---

### 2.4 查詢通知詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/notifications/{id}` |
| **方法** | GET |
| **Controller** | HR12NotificationQryController |
| **Service** | GetNotificationDetailServiceImpl |
| **權限** | 通知收件人 |

**用途說明**

- **業務場景:** 查看通知的完整內容
- **使用者:** 通知收件人

**Response Body**

```json
{
  "success": true,
  "data": {
    "notificationId": "ntf-001",
    "title": "請假申請已核准",
    "content": "您的特休假申請（2025/12/20-2025/12/21）已核准。\n\n核准人：李經理\n核准時間：2025/12/30 10:00",
    "notificationType": "APPROVAL_RESULT",
    "priority": "NORMAL",
    "status": "READ",
    "channels": ["IN_APP", "EMAIL"],
    "businessType": "LEAVE_APPLICATION",
    "businessId": "leave-001",
    "businessUrl": "/attendance/leave/applications/leave-001",
    "createdAt": "2025-12-30T10:00:00Z",
    "sentAt": "2025-12-30T10:00:05Z",
    "readAt": "2025-12-30T10:30:00Z"
  }
}
```

---

### 2.5 標記通知為已讀

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/notifications/{id}/read` |
| **方法** | PUT |
| **Controller** | HR12NotificationCmdController |
| **Service** | MarkNotificationReadServiceImpl |
| **權限** | 通知收件人 |

**用途說明**

- **業務場景:** 使用者閱讀通知後標記為已讀
- **使用者:** 通知收件人

**Response Body**

```json
{
  "success": true,
  "data": {
    "notificationId": "ntf-001",
    "status": "READ",
    "readAt": "2025-12-30T10:30:00Z"
  },
  "message": "已標記為已讀"
}
```

**領域事件**

| 事件名稱 | Kafka Topic | 說明 |
|:---|:---|:---|
| NotificationReadEvent | notification.read | 通知已讀 |

---

### 2.6 標記全部為已讀

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/notifications/read-all` |
| **方法** | PUT |
| **Controller** | HR12NotificationCmdController |
| **Service** | MarkAllNotificationsReadServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 一次標記所有未讀通知為已讀
- **使用者:** 一般員工

**Response Body**

```json
{
  "success": true,
  "data": {
    "markedCount": 5,
    "readAt": "2025-12-30T10:30:00Z"
  },
  "message": "已將5則通知標記為已讀"
}
```

---

### 2.7 刪除通知

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `DELETE /api/v1/notifications/{id}` |
| **方法** | DELETE |
| **Controller** | HR12NotificationCmdController |
| **Service** | DeleteNotificationServiceImpl |
| **權限** | 通知收件人 |

**用途說明**

- **業務場景:** 使用者刪除不需要的通知
- **使用者:** 通知收件人
- **注意:** 軟刪除，不會真正刪除記錄

**Response Body**

```json
{
  "success": true,
  "message": "通知已刪除"
}
```

---

### 2.8 查詢未讀通知數量

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/notifications/unread-count` |
| **方法** | GET |
| **Controller** | HR12NotificationQryController |
| **Service** | GetUnreadCountServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 顯示頂部導航列的通知數量徽章
- **使用者:** 一般員工
- **注意:** 此 API 會被頻繁調用，需優化效能

**Response Body**

```json
{
  "success": true,
  "data": {
    "unreadCount": 5,
    "byType": {
      "APPROVAL_REQUEST": 2,
      "APPROVAL_RESULT": 1,
      "REMINDER": 2
    }
  }
}
```

---

## 3. 通知範本管理 API

### 3.1 建立通知範本

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/notifications/templates` |
| **方法** | POST |
| **Controller** | HR12TemplateCmdController |
| **Service** | CreateNotificationTemplateServiceImpl |
| **權限** | `NOTIFICATION:TEMPLATE:CREATE` |

**用途說明**

- **業務場景:** 系統管理員建立新的通知範本
- **使用者:** 系統管理員
- **注意:** 範本支援變數替換，使用 `{{variableName}}` 語法

**Request Body**

```json
{
  "templateCode": "LEAVE_APPROVED",
  "templateName": "請假申請核准通知",
  "subject": "【請假核准】您的請假申請已核准",
  "body": "親愛的 {{employeeName}}：\n\n您的{{leaveType}}申請已核准。\n\n請假日期：{{startDate}} 至 {{endDate}}\n請假天數：{{totalDays}} 天\n核准人：{{approverName}}\n\n如有疑問，請聯繫人事部門。\n\n人力資源管理系統",
  "defaultChannels": ["IN_APP", "EMAIL"],
  "variables": [
    { "name": "employeeName", "description": "員工姓名", "required": true },
    { "name": "leaveType", "description": "請假類型", "required": true },
    { "name": "startDate", "description": "開始日期", "required": true },
    { "name": "endDate", "description": "結束日期", "required": true },
    { "name": "totalDays", "description": "請假天數", "required": true },
    { "name": "approverName", "description": "核准人姓名", "required": true }
  ]
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| templateCode | String | ✅ | 唯一，英文大寫_分隔 | 範本代碼 |
| templateName | String | ✅ | 1-100字元 | 範本名稱 |
| subject | String | | 1-200字元 | Email 主旨 |
| body | String | ✅ | 1-5000字元 | 範本內容 |
| defaultChannels | Array | | 有效渠道代碼 | 預設發送渠道 |
| variables | Array | | | 變數定義 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "templateId": "tpl-001",
    "templateCode": "LEAVE_APPROVED",
    "templateName": "請假申請核准通知",
    "isActive": true,
    "createdAt": "2025-12-30T10:00:00Z"
  },
  "message": "通知範本建立成功"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | NTF_DUPLICATE_TEMPLATE_CODE | 範本代碼已存在 |
| 400 | NTF_INVALID_TEMPLATE_CODE | 範本代碼格式不正確 |

---

### 3.2 查詢通知範本列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/notifications/templates` |
| **方法** | GET |
| **Controller** | HR12TemplateQryController |
| **Service** | GetNotificationTemplateListServiceImpl |
| **權限** | `NOTIFICATION:TEMPLATE:READ` |

**用途說明**

- **業務場景:** 管理員查看系統中的通知範本
- **使用者:** 系統管理員

**Query Parameters**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| keyword | String | 範本名稱或代碼關鍵字 |
| isActive | Boolean | 是否只顯示啟用的範本 |
| page | Integer | 頁碼 |
| pageSize | Integer | 每頁筆數 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "templateId": "tpl-001",
        "templateCode": "LEAVE_APPROVED",
        "templateName": "請假申請核准通知",
        "subject": "【請假核准】您的請假申請已核准",
        "defaultChannels": ["IN_APP", "EMAIL"],
        "isActive": true,
        "variableCount": 6,
        "createdAt": "2025-12-01T10:00:00Z",
        "updatedAt": "2025-12-15T14:30:00Z"
      },
      {
        "templateId": "tpl-002",
        "templateCode": "LEAVE_REJECTED",
        "templateName": "請假申請駁回通知",
        "subject": "【請假駁回】您的請假申請已駁回",
        "defaultChannels": ["IN_APP", "EMAIL"],
        "isActive": true,
        "variableCount": 5,
        "createdAt": "2025-12-01T10:00:00Z",
        "updatedAt": null
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 15,
      "totalPages": 1
    }
  }
}
```

---

### 3.3 查詢通知範本詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/notifications/templates/{id}` |
| **方法** | GET |
| **Controller** | HR12TemplateQryController |
| **Service** | GetNotificationTemplateDetailServiceImpl |
| **權限** | `NOTIFICATION:TEMPLATE:READ` |

**Response Body**

```json
{
  "success": true,
  "data": {
    "templateId": "tpl-001",
    "templateCode": "LEAVE_APPROVED",
    "templateName": "請假申請核准通知",
    "subject": "【請假核准】您的請假申請已核准",
    "body": "親愛的 {{employeeName}}：\n\n您的{{leaveType}}申請已核准。\n\n請假日期：{{startDate}} 至 {{endDate}}\n請假天數：{{totalDays}} 天\n核准人：{{approverName}}\n\n如有疑問，請聯繫人事部門。\n\n人力資源管理系統",
    "defaultChannels": ["IN_APP", "EMAIL"],
    "variables": [
      { "name": "employeeName", "description": "員工姓名", "required": true },
      { "name": "leaveType", "description": "請假類型", "required": true },
      { "name": "startDate", "description": "開始日期", "required": true },
      { "name": "endDate", "description": "結束日期", "required": true },
      { "name": "totalDays", "description": "請假天數", "required": true },
      { "name": "approverName", "description": "核准人姓名", "required": true }
    ],
    "isActive": true,
    "createdAt": "2025-12-01T10:00:00Z",
    "createdBy": "admin",
    "updatedAt": "2025-12-15T14:30:00Z",
    "updatedBy": "admin"
  }
}
```

---

### 3.4 更新通知範本

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/notifications/templates/{id}` |
| **方法** | PUT |
| **Controller** | HR12TemplateCmdController |
| **Service** | UpdateNotificationTemplateServiceImpl |
| **權限** | `NOTIFICATION:TEMPLATE:UPDATE` |

**Request Body**

```json
{
  "templateName": "請假申請核准通知（更新版）",
  "subject": "【請假核准】您的請假申請已核准",
  "body": "親愛的 {{employeeName}}：\n\n恭喜！您的{{leaveType}}申請已核准。\n\n請假日期：{{startDate}} 至 {{endDate}}\n請假天數：{{totalDays}} 天\n核准人：{{approverName}}\n核准時間：{{approvalTime}}\n\n祝您假期愉快！\n\n人力資源管理系統",
  "defaultChannels": ["IN_APP", "EMAIL"],
  "isActive": true
}
```

**Response Body**

```json
{
  "success": true,
  "data": {
    "templateId": "tpl-001",
    "templateCode": "LEAVE_APPROVED",
    "templateName": "請假申請核准通知（更新版）",
    "updatedAt": "2025-12-30T14:00:00Z"
  },
  "message": "通知範本更新成功"
}
```

---

### 3.5 刪除通知範本

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `DELETE /api/v1/notifications/templates/{id}` |
| **方法** | DELETE |
| **Controller** | HR12TemplateCmdController |
| **Service** | DeleteNotificationTemplateServiceImpl |
| **權限** | `NOTIFICATION:TEMPLATE:DELETE` |

**用途說明**

- **業務場景:** 刪除不再使用的通知範本
- **使用者:** 系統管理員
- **注意:** 已被使用的範本不可刪除，建議停用

**Response Body**

```json
{
  "success": true,
  "message": "通知範本已刪除"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | NTF_TEMPLATE_IN_USE | 範本已被使用，無法刪除 |

---

## 4. 通知偏好設定 API

### 4.1 查詢通知偏好設定

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/notifications/preferences` |
| **方法** | GET |
| **Controller** | HR12PreferenceQryController |
| **Service** | GetNotificationPreferenceServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 員工查看自己的通知偏好設定
- **使用者:** 一般員工
- **注意:** 若使用者未設定過偏好，回傳系統預設值

**Response Body**

```json
{
  "success": true,
  "data": {
    "preferenceId": "pref-001",
    "employeeId": "emp-001",
    "isEnabled": true,
    "language": "zh-TW",
    "channels": {
      "inAppEnabled": true,
      "emailEnabled": true,
      "pushEnabled": false,
      "teamsEnabled": false,
      "lineEnabled": false
    },
    "quietHours": {
      "enabled": true,
      "startTime": "22:00",
      "endTime": "08:00",
      "daysOfWeek": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"]
    },
    "typeSettings": [
      { "notificationType": "APPROVAL_REQUEST", "enabled": true, "channels": ["IN_APP", "EMAIL"] },
      { "notificationType": "APPROVAL_RESULT", "enabled": true, "channels": ["IN_APP", "EMAIL"] },
      { "notificationType": "REMINDER", "enabled": true, "channels": ["IN_APP"] },
      { "notificationType": "ANNOUNCEMENT", "enabled": true, "channels": ["IN_APP"] },
      { "notificationType": "ALERT", "enabled": true, "channels": ["IN_APP", "EMAIL", "PUSH"] }
    ],
    "eventSettings": [
      { "eventType": "LEAVE_APPLIED", "enabled": true, "channels": ["IN_APP", "EMAIL"] },
      { "eventType": "LEAVE_APPROVED", "enabled": true, "channels": ["IN_APP", "EMAIL", "PUSH"] },
      { "eventType": "PAYSLIP_GENERATED", "enabled": true, "channels": ["EMAIL"] },
      { "eventType": "PASSWORD_EXPIRING", "enabled": true, "channels": ["IN_APP", "EMAIL"] },
      { "eventType": "PROJECT_BUDGET_ALERT", "enabled": true, "channels": ["EMAIL", "TEAMS"] }
    ],
    "updatedAt": "2025-12-15T10:00:00Z"
  }
}
```

---

### 4.2 更新通知偏好設定

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/notifications/preferences` |
| **方法** | PUT |
| **Controller** | HR12PreferenceCmdController |
| **Service** | UpdateNotificationPreferenceServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 員工調整自己的通知偏好設定
- **使用者:** 一般員工
- **注意:** 支援部分更新，只需傳入要修改的欄位

**業務邏輯**

1. 若 preferenceId 不存在，建立新的偏好記錄
2. 驗證渠道設定與靜音時段格式
3. 合併現有設定與新設定（部分更新）
4. 儲存更新後的偏好設定

**Request Body**

```json
{
  "isEnabled": true,
  "language": "zh-TW",
  "channels": {
    "inAppEnabled": true,
    "emailEnabled": true,
    "pushEnabled": true,
    "teamsEnabled": false,
    "lineEnabled": false
  },
  "quietHours": {
    "enabled": true,
    "startTime": "23:00",
    "endTime": "07:00",
    "daysOfWeek": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"]
  },
  "typeSettings": [
    { "notificationType": "APPROVAL_REQUEST", "enabled": true, "channels": ["IN_APP", "EMAIL", "PUSH"] },
    { "notificationType": "APPROVAL_RESULT", "enabled": true, "channels": ["IN_APP", "EMAIL"] },
    { "notificationType": "REMINDER", "enabled": true, "channels": ["IN_APP"] },
    { "notificationType": "ANNOUNCEMENT", "enabled": false, "channels": [] },
    { "notificationType": "ALERT", "enabled": true, "channels": ["IN_APP", "EMAIL", "PUSH"] }
  ],
  "eventSettings": [
    { "eventType": "LEAVE_APPLIED", "enabled": true, "channels": ["IN_APP", "EMAIL"] },
    { "eventType": "LEAVE_APPROVED", "enabled": true, "channels": ["IN_APP", "EMAIL", "PUSH"] },
    { "eventType": "PAYSLIP_GENERATED", "enabled": true, "channels": ["EMAIL"] }
  ]
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| isEnabled | Boolean | | 全域通知開關 |
| language | String | | 語言偏好（zh-TW, en-US） |
| channels | Object | | 各渠道啟用狀態 |
| channels.inAppEnabled | Boolean | | 系統內通知 |
| channels.emailEnabled | Boolean | | Email 通知 |
| channels.pushEnabled | Boolean | | 推播通知 |
| channels.teamsEnabled | Boolean | | Teams 通知 |
| channels.lineEnabled | Boolean | | LINE 通知 |
| quietHours | Object | | 靜音時段設定 |
| quietHours.enabled | Boolean | | 是否啟用靜音時段 |
| quietHours.startTime | String | | 靜音開始時間 (HH:mm) |
| quietHours.endTime | String | | 靜音結束時間 (HH:mm) |
| quietHours.daysOfWeek | Array | | 適用星期（預設全週） |
| typeSettings | Array | | 按通知類型的設定 |
| eventSettings | Array | | 按業務事件的設定 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "preferenceId": "pref-001",
    "updatedAt": "2025-12-30T14:00:00Z"
  },
  "message": "通知偏好設定已更新"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | NTF_INVALID_QUIET_HOURS | 靜音時段設定不正確 | 確認時間格式 HH:mm |
| 400 | NTF_INVALID_LANGUAGE | 不支援的語言代碼 | 使用 zh-TW 或 en-US |
| 400 | NTF_INVALID_DAY_OF_WEEK | 無效的星期代碼 | 使用 MONDAY 至 SUNDAY |

---

## 5. 公告管理 API

### 5.1 發布公告

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/notifications/announcements` |
| **方法** | POST |
| **Controller** | HR12AnnouncementCmdController |
| **Service** | CreateAnnouncementServiceImpl |
| **權限** | `NOTIFICATION:ANNOUNCEMENT:CREATE` |

**用途說明**

- **業務場景:** HR 或管理員發布全員公告
- **使用者:** HR、系統管理員

**Request Body**

```json
{
  "title": "2026年春節放假公告",
  "content": "各位同仁：\n\n2026年春節假期為1/28（六）至2/5（日），共9天。\n\n1/27（五）為補班日，請同仁留意。\n\n祝大家新年快樂！\n\n人事部",
  "priority": "HIGH",
  "channels": ["IN_APP", "EMAIL"],
  "targetAudience": {
    "type": "ALL",
    "departmentIds": [],
    "roleIds": []
  },
  "publishAt": "2025-12-30T09:00:00Z",
  "expireAt": "2026-02-05T23:59:59Z"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| title | String | ✅ | 公告標題 |
| content | String | ✅ | 公告內容 |
| priority | String | | 優先級 |
| channels | Array | | 發送渠道 |
| targetAudience | Object | | 目標對象 |
| targetAudience.type | String | | ALL / DEPARTMENT / ROLE |
| publishAt | String | | 發布時間（立即或排程） |
| expireAt | String | | 過期時間 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "announcementId": "ann-001",
    "title": "2026年春節放假公告",
    "status": "PUBLISHED",
    "recipientCount": 500,
    "publishedAt": "2025-12-30T09:00:00Z",
    "expireAt": "2026-02-05T23:59:59Z"
  },
  "message": "公告發布成功"
}
```

---

### 5.2 查詢公告列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/notifications/announcements` |
| **方法** | GET |
| **Controller** | HR12AnnouncementQryController |
| **Service** | GetAnnouncementListServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 查看系統公告列表
- **使用者:** 一般員工（僅能看到與自己相關的公告）

**Query Parameters**

| 參數 | 類型 | 說明 |
|:---|:---|:---|
| includeExpired | Boolean | 是否包含已過期公告 |
| page | Integer | 頁碼 |
| pageSize | Integer | 每頁筆數 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "announcementId": "ann-001",
        "title": "2026年春節放假公告",
        "content": "各位同仁：\n\n2026年春節假期為...",
        "priority": "HIGH",
        "publishedAt": "2025-12-30T09:00:00Z",
        "expireAt": "2026-02-05T23:59:59Z",
        "isRead": false,
        "publishedBy": {
          "employeeId": "hr-001",
          "fullName": "人事部"
        }
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 5,
      "totalPages": 1
    }
  }
}
```

---

### 5.3 查詢公告詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `GET /api/v1/notifications/announcements/{id}` |
| **方法** | GET |
| **Controller** | HR12AnnouncementQryController |
| **Service** | GetAnnouncementDetailServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 查看公告的完整內容
- **使用者:** 一般員工

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| id | String | ✅ | 公告 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "announcementId": "ann-001",
    "title": "2026年春節放假公告",
    "content": "各位同仁：\n\n2026年春節假期為1/28（六）至2/5（日），共9天。\n\n1/27（五）為補班日，請同仁留意。\n\n祝大家新年快樂！\n\n人事部",
    "priority": "HIGH",
    "status": "PUBLISHED",
    "targetAudience": {
      "type": "ALL",
      "departmentIds": [],
      "roleIds": []
    },
    "attachments": [],
    "publishedAt": "2025-12-30T09:00:00Z",
    "expireAt": "2026-02-05T23:59:59Z",
    "isRead": false,
    "publishedBy": {
      "employeeId": "hr-001",
      "fullName": "人事部"
    },
    "createdAt": "2025-12-29T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 404 | NTF_ANNOUNCEMENT_NOT_FOUND | 公告不存在 |
| 403 | NTF_ANNOUNCEMENT_NOT_AUTHORIZED | 無權查看此公告 |

---

### 5.4 更新公告

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `PUT /api/v1/notifications/announcements/{id}` |
| **方法** | PUT |
| **Controller** | HR12AnnouncementCmdController |
| **Service** | UpdateAnnouncementServiceImpl |
| **權限** | `NOTIFICATION:ANNOUNCEMENT:UPDATE` |

**用途說明**

- **業務場景:** 修改已發布的公告內容或設定
- **使用者:** HR、系統管理員
- **注意:** 只能修改尚未過期的公告

**業務邏輯**

1. 檢查公告是否存在
2. 檢查公告是否已過期（已過期無法修改）
3. 若修改目標對象，需重新發送通知
4. 更新公告記錄
5. 發布 AnnouncementUpdatedEvent

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| id | String | ✅ | 公告 ID |

**Request Body**

```json
{
  "title": "2026年春節放假公告（更新版）",
  "content": "各位同仁：\n\n2026年春節假期為1/28（六）至2/5（日），共9天。\n\n1/27（五）為補班日，請同仁留意。\n\n補充：1/26（四）正常上班。\n\n祝大家新年快樂！\n\n人事部",
  "priority": "HIGH",
  "expireAt": "2026-02-05T23:59:59Z"
}
```

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| title | String | | 公告標題 |
| content | String | | 公告內容 |
| priority | String | | 優先級 |
| expireAt | String | | 過期時間 |

**Response Body**

```json
{
  "success": true,
  "data": {
    "announcementId": "ann-001",
    "title": "2026年春節放假公告（更新版）",
    "updatedAt": "2025-12-30T14:00:00Z"
  },
  "message": "公告更新成功"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 404 | NTF_ANNOUNCEMENT_NOT_FOUND | 公告不存在 |
| 400 | NTF_ANNOUNCEMENT_EXPIRED | 公告已過期，無法修改 |
| 400 | NTF_ANNOUNCEMENT_WITHDRAWN | 公告已撤銷，無法修改 |

**領域事件**

| 事件名稱 | Kafka Topic | 說明 |
|:---|:---|:---|
| AnnouncementUpdatedEvent | notification.announcement.updated | 公告更新 |

---

### 5.5 撤銷公告

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `DELETE /api/v1/notifications/announcements/{id}` |
| **方法** | DELETE |
| **Controller** | HR12AnnouncementCmdController |
| **Service** | WithdrawAnnouncementServiceImpl |
| **權限** | `NOTIFICATION:ANNOUNCEMENT:DELETE` |

**用途說明**

- **業務場景:** 撤銷錯誤發布的公告
- **使用者:** HR、系統管理員
- **注意:** 軟刪除，公告將標記為已撤銷狀態

**業務邏輯**

1. 檢查公告是否存在
2. 檢查公告是否已撤銷
3. 標記公告為已撤銷狀態
4. 發布 AnnouncementWithdrawnEvent

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| id | String | ✅ | 公告 ID |

**Response Body**

```json
{
  "success": true,
  "data": {
    "announcementId": "ann-001",
    "status": "WITHDRAWN",
    "withdrawnAt": "2025-12-30T15:00:00Z"
  },
  "message": "公告已撤銷"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 404 | NTF_ANNOUNCEMENT_NOT_FOUND | 公告不存在 |
| 400 | NTF_ANNOUNCEMENT_ALREADY_WITHDRAWN | 公告已撤銷 |

**領域事件**

| 事件名稱 | Kafka Topic | 說明 |
|:---|:---|:---|
| AnnouncementWithdrawnEvent | notification.announcement.withdrawn | 公告撤銷 |

---

## 6. 測試通知 API

### 6.1 發送測試通知

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| **端點** | `POST /api/v1/notifications/test` |
| **方法** | POST |
| **Controller** | HR12NotificationCmdController |
| **Service** | SendTestNotificationServiceImpl |
| **權限** | 登入使用者 |

**用途說明**

- **業務場景:** 員工測試自己的通知設定是否正確（驗證 Email、推播等是否能正常接收）
- **使用者:** 一般員工
- **注意:** 每人每小時最多發送 5 次測試通知

**業務邏輯**

1. 檢查頻率限制（每小時最多 5 次）
2. 根據指定渠道發送測試通知
3. 記錄發送結果
4. 回傳各渠道發送狀態

**Request Body**

```json
{
  "channels": ["IN_APP", "EMAIL", "PUSH"],
  "message": "這是一則測試通知"
}
```

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---:|:---|:---|
| channels | Array | | 有效渠道代碼 | 測試渠道（預設所有已啟用渠道） |
| message | String | | 最多 200 字元 | 測試訊息內容（預設「這是測試通知」） |

**Response Body**

```json
{
  "success": true,
  "data": {
    "testId": "test-001",
    "sentAt": "2025-12-30T10:00:00Z",
    "results": [
      { "channel": "IN_APP", "status": "SUCCESS", "message": null },
      { "channel": "EMAIL", "status": "SUCCESS", "message": null },
      { "channel": "PUSH", "status": "FAILED", "message": "裝置未註冊推播服務" }
    ],
    "remainingTests": 4
  },
  "message": "測試通知已發送"
}
```

**錯誤碼**

| HTTP狀態碼 | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 429 | NTF_TEST_RATE_LIMIT | 測試通知頻率超過限制 | 請等待一小時後再試 |
| 400 | NTF_NO_CHANNEL_ENABLED | 沒有啟用任何通知渠道 | 請先啟用至少一個渠道 |

---

## 7. 錯誤碼總覽

### 7.1 通知錯誤 (NTF_*)

| 錯誤碼 | HTTP狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| NTF_NOT_FOUND | 404 | 通知不存在 | 確認通知 ID 正確 |
| NTF_RECIPIENT_NOT_FOUND | 400 | 收件人不存在 | 確認員工 ID 正確 |
| NTF_NOT_RECIPIENT | 403 | 您不是此通知的收件人 | 只能操作自己的通知 |
| NTF_CONTENT_REQUIRED | 400 | 未使用範本時必須提供內容 | 提供 content 或 templateCode |
| NTF_INVALID_CHANNEL | 400 | 無效的通知渠道 | 使用有效的渠道代碼 |
| NTF_SEND_FAILED | 500 | 通知發送失敗 | 稍後重試或聯繫管理員 |

### 7.2 範本錯誤 (NTF_TPL_*)

| 錯誤碼 | HTTP狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| NTF_TEMPLATE_NOT_FOUND | 404 | 通知範本不存在 | 確認範本代碼正確 |
| NTF_DUPLICATE_TEMPLATE_CODE | 400 | 範本代碼已存在 | 使用不同的代碼 |
| NTF_INVALID_TEMPLATE_CODE | 400 | 範本代碼格式不正確 | 使用英文大寫_分隔 |
| NTF_TEMPLATE_IN_USE | 400 | 範本已被使用，無法刪除 | 建議停用而非刪除 |
| NTF_MISSING_VARIABLE | 400 | 缺少必要的範本變數 | 提供所有必要變數 |

### 7.3 偏好設定錯誤 (NTF_PREF_*)

| 錯誤碼 | HTTP狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| NTF_INVALID_QUIET_HOURS | 400 | 靜音時段設定不正確 | 確認時間格式 HH:mm |

### 7.4 公告錯誤 (NTF_ANN_*)

| 錯誤碼 | HTTP狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| NTF_ANNOUNCEMENT_NOT_FOUND | 404 | 公告不存在 | 確認公告 ID 正確 |
| NTF_ANNOUNCEMENT_EXPIRED | 400 | 公告已過期 | 無法操作已過期的公告 |
| NTF_ANNOUNCEMENT_NOT_AUTHORIZED | 403 | 無權查看此公告 | 確認公告是否對您開放 |
| NTF_ANNOUNCEMENT_ALREADY_WITHDRAWN | 400 | 公告已撤銷 | 無法操作已撤銷的公告 |
| NTF_ANNOUNCEMENT_WITHDRAWN | 400 | 公告已撤銷，無法修改 | 無法操作已撤銷的公告 |

### 7.5 測試通知錯誤 (NTF_TEST_*)

| 錯誤碼 | HTTP狀態碼 | 說明 | 處理建議 |
|:---|:---:|:---|:---|
| NTF_TEST_RATE_LIMIT | 429 | 測試通知頻率超過限制 | 請等待一小時後再試 |
| NTF_NO_CHANNEL_ENABLED | 400 | 沒有啟用任何通知渠道 | 請先啟用至少一個渠道 |

---

## 8. 領域事件總覽

### 8.1 發布的事件

| 事件名稱 | Kafka Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| NotificationCreatedEvent | notification.created | 通知建立 | - |
| NotificationSentEvent | notification.sent | 通知發送成功 | - |
| NotificationFailedEvent | notification.failed | 通知發送失敗 | Monitoring |
| NotificationReadEvent | notification.read | 通知已讀 | - |
| AnnouncementPublishedEvent | notification.announcement.published | 公告發布 | - |

### 8.2 訂閱的事件

通知服務主要作為**事件訂閱者**，訂閱其他業務服務的事件並發送通知：

| 業務事件 | 來源服務 | 通知對象 | 範本代碼 |
|:---|:---|:---|:---|
| LeaveAppliedEvent | Attendance | 直屬主管 | LEAVE_APPROVAL_REQUEST |
| LeaveApprovedEvent | Attendance | 申請人 | LEAVE_APPROVED |
| LeaveRejectedEvent | Attendance | 申請人 | LEAVE_REJECTED |
| OvertimeAppliedEvent | Attendance | 直屬主管 | OVERTIME_APPROVAL_REQUEST |
| PayslipGeneratedEvent | Payroll | 員工 | PAYSLIP_READY |
| TaskAssignedEvent | Workflow | 審核人 | APPROVAL_REQUEST |
| TaskOverdueEvent | Workflow | 審核人 | TASK_OVERDUE |
| ApprovalCompletedEvent | Workflow | 申請人 | APPROVAL_COMPLETED |
| ApprovalRejectedEvent | Workflow | 申請人 | APPROVAL_REJECTED |
| EmployeeCreatedEvent | Organization | 新員工 | WELCOME_MESSAGE |
| ContractExpiringEvent | Organization | HR | CONTRACT_EXPIRY |
| CertificateExpiringEvent | Training | 員工 | CERTIFICATE_EXPIRY |

### 8.3 事件 Payload 結構

#### NotificationSentEvent

```json
{
  "eventId": "evt-ntf-001",
  "eventType": "NotificationSent",
  "timestamp": "2025-12-30T10:00:00Z",
  "payload": {
    "notificationId": "ntf-001",
    "recipientId": "emp-001",
    "title": "請假申請已核准",
    "notificationType": "APPROVAL_RESULT",
    "channels": ["IN_APP", "EMAIL"],
    "sentAt": "2025-12-30T10:00:00Z"
  }
}
```

#### NotificationFailedEvent

```json
{
  "eventId": "evt-ntf-002",
  "eventType": "NotificationFailed",
  "timestamp": "2025-12-30T10:05:00Z",
  "payload": {
    "notificationId": "ntf-002",
    "recipientId": "emp-002",
    "channel": "EMAIL",
    "errorCode": "SMTP_CONNECTION_FAILED",
    "errorMessage": "SMTP伺服器連線逾時",
    "retryCount": 3,
    "willRetry": false
  }
}
```

### 8.4 自動提醒 Job

| Job 名稱 | 執行頻率 | 說明 | 範本代碼 |
|:---|:---|:---|:---|
| BirthdayReminderJob | 每日 08:00 | 發送當日生日祝福 | BIRTHDAY_GREETING |
| ContractExpiryJob | 每日 09:00 | 合約30天內到期提醒HR | CONTRACT_EXPIRY |
| CertificateExpiryJob | 每週一 | 證照30天內到期提醒員工 | CERTIFICATE_EXPIRY |
| AnnualLeaveExpiryJob | 每週一 | 特休30天內到期提醒 | ANNUAL_LEAVE_EXPIRY |
| TimesheetReminderJob | 每日 18:00 | 未回報工時提醒 | TIMESHEET_REMINDER |
| PerformanceReminderJob | 依考核週期 | 考核到期提醒 | PERFORMANCE_REMINDER |

---

**文件完成日期:** 2025-12-30
**最後更新日期:** 2025-12-30
**API 端點數量:** 21 個
