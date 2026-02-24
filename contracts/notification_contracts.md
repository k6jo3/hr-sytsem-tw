# HR12 通知服務業務合約

> **服務代碼:** HR12
> **服務名稱:** 通知服務 (Notification Service)
> **版本:** 1.0
> **更新日期:** 2026-02-24

---

## 概述

通知服務負責多渠道通知發送（IN_APP、Email、Push、Teams、LINE）、
通知範本管理、個人偏好設定，以及全員公告管理。
支援靜音時段、優先級控制、自動重試等完整通知生命週期。

---

## API 端點概覽

### 通知管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/notifications/send` | POST | NTF_CMD_001 | 發送通知 | ✅ 已實作 |
| 2 | `GET /api/v1/notifications/me` | GET | NTF_M001 | 查詢我的通知列表 | ✅ 已實作 |
| 3 | `PUT /api/v1/notifications/{id}/read` | PUT | NTF_CMD_002 | 標記通知為已讀 | ✅ 已實作 |
| 4 | `PUT /api/v1/notifications/read-all` | PUT | NTF_CMD_003 | 標記全部已讀 | ✅ 已實作 |
| 5 | `DELETE /api/v1/notifications/{id}` | DELETE | NTF_CMD_004 | 刪除通知（軟刪除）| ✅ 已實作 |

### 通知範本 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/notifications/templates` | GET | NTF_T001 | 查詢啟用範本 | ✅ 已實作 |
| 2 | `POST /api/v1/notifications/templates` | POST | NTF_CMD_005 | 建立通知範本 | ✅ 已實作 |

### 通知偏好 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/notifications/preferences` | GET | NTF_S001 | 查詢偏好設定 | ✅ 已實作 |
| 2 | `PUT /api/v1/notifications/preferences` | PUT | NTF_CMD_006 | 更新偏好設定 | ✅ 已實作 |

### 公告管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/notifications/announcements` | GET | NTF_A001 | 查詢公告列表 | ✅ 已實作 |
| 2 | `POST /api/v1/notifications/announcements` | POST | NTF_CMD_007 | 發布公告 | ✅ 已實作 |

---

## 1. Query 操作業務合約

### 1.0 Query 場景快速索引（合約驗證用表格）

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| NTF_T001 | 查詢啟用通知範本清單 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| NTF_T005 | 查詢停用通知範本清單 | HR | `{"status":"INACTIVE"}` | `status = 'INACTIVE'`, `is_deleted = 0` |
| NTF_M001 | 員工查詢自己的通知列表 | EMPLOYEE | `{}` | `recipient_id = '{currentUserEmployeeId}'`, `is_deleted = 0` |
| NTF_A001 | 查詢已發布公告 | EMPLOYEE | `{}` | `status = 'PUBLISHED'`, `is_deleted = 0` |

---

### 1.1 通知範本查詢

#### NTF_T001: 查詢啟用通知範本清單

**API 端點：** `GET /api/v1/notifications/templates`

**業務場景描述：**

HR 管理員查詢所有啟用中的通知範本，以便查看可用的範本清單。
系統自動過濾軟刪除記錄（is_deleted = 0）及啟用狀態（status = ACTIVE）。

**測試合約：**

```json
{
  "scenarioId": "NTF_T001",
  "apiEndpoint": "GET /api/v1/notifications/templates",
  "controller": "HR12TemplateQryController",
  "service": "GetNotificationTemplateListServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {"status": "ACTIVE"},
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "ACTIVE"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "templateId", "type": "string", "notNull": true},
      {"name": "templateCode", "type": "string", "notNull": true},
      {"name": "templateName", "type": "string", "notNull": true},
      {"name": "isActive", "type": "boolean", "notNull": true}
    ]
  }
}
```

---

#### NTF_T005: 查詢停用通知範本清單

**API 端點：** `GET /api/v1/notifications/templates`

**業務場景描述：**

HR 管理員查詢所有已停用的通知範本，用於稽核或重新啟用。
系統過濾軟刪除記錄及停用狀態（status = INACTIVE）。

**測試合約：**

```json
{
  "scenarioId": "NTF_T005",
  "apiEndpoint": "GET /api/v1/notifications/templates",
  "controller": "HR12TemplateQryController",
  "service": "GetNotificationTemplateListServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {"status": "INACTIVE"},
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "INACTIVE"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "templateId", "type": "string", "notNull": true},
      {"name": "isActive", "type": "boolean", "notNull": true}
    ]
  }
}
```

---

### 1.2 個人通知查詢

#### NTF_M001: 員工查詢自己的通知列表

**API 端點：** `GET /api/v1/notifications/me`

**業務場景描述：**

員工登入後查看自己收到的所有通知（請假核准通知、薪資通知等）。
系統依當前登入使用者的員工 ID 自動過濾，確保員工只能看到自己的通知。
過濾軟刪除記錄（is_deleted = 0）。

**測試合約：**

```json
{
  "scenarioId": "NTF_M001",
  "apiEndpoint": "GET /api/v1/notifications/me",
  "controller": "HR12NotificationQryController",
  "service": "GetMyNotificationsServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {},
  "expectedQueryFilters": [
    {"field": "recipient_id", "operator": "=", "value": "{currentUserEmployeeId}"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "notificationId", "type": "string", "notNull": true},
      {"name": "title", "type": "string", "notNull": true}
    ]
  }
}
```

---

### 1.3 公告查詢

#### NTF_A001: 查詢已發布公告

**API 端點：** `GET /api/v1/notifications/announcements`

**業務場景描述：**

員工查詢所有已發布且未撤銷的公告（放假通知、制度更新等）。
系統過濾已撤銷狀態及軟刪除記錄。

**測試合約：**

```json
{
  "scenarioId": "NTF_A001",
  "apiEndpoint": "GET /api/v1/notifications/announcements",
  "controller": "HR12AnnouncementQryController",
  "service": "GetAnnouncementListServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {},
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "PUBLISHED"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data.items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "announcementId", "type": "string", "notNull": true},
      {"name": "title", "type": "string", "notNull": true}
    ]
  }
}
```

---

## 2. Command 操作業務合約

### 2.1 發送通知

#### NTF_CMD_001: 系統發送通知給員工

**API 端點：** `POST /api/v1/notifications/send`

**業務場景描述：**

業務服務（考勤、薪資、簽核等）呼叫此 API 發送通知給員工。
系統根據收件人的偏好設定過濾渠道，檢查靜音時段，渲染範本內容後發送。

**業務規則：**
1. 依收件人 employeeId 載入偏好設定
2. 若指定 templateCode，載入範本並渲染內容
3. 依偏好設定過濾渠道（如員工關閉 Email，就不走 Email 渠道）
4. 若 priority != URGENT，檢查靜音時段
5. 建立 Notification 記錄並儲存
6. 依各渠道發送通知
7. 更新通知狀態（SENT / FAILED）

**測試合約：**

```json
{
  "scenarioId": "NTF_CMD_001",
  "apiEndpoint": "POST /api/v1/notifications/send",
  "controller": "HR12NotificationCmdController",
  "service": "SendNotificationServiceImpl",
  "permission": "NOTIFICATION:SEND",
  "request": {
    "recipientId": "EMP001",
    "title": "請假申請已核准",
    "content": "您的 2026/03/01 特休假申請已核准",
    "notificationType": "APPROVAL_RESULT",
    "channels": ["IN_APP", "EMAIL"],
    "priority": "NORMAL"
  },
  "businessRules": [
    "載入收件人偏好設定以過濾渠道",
    "建立 Notification 聚合根並呼叫 markAsSent()",
    "儲存 Notification 至 Repository",
    "依渠道發送通知"
  ],
  "expectedDataChanges": {
    "tables": ["notifications"],
    "operations": [
      {
        "type": "INSERT",
        "table": "notifications",
        "expectedFields": {
          "recipient_id": "EMP001",
          "notification_type": "APPROVAL_RESULT",
          "status": "SENT"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "NotificationSentEvent",
      "expectedFields": {
        "recipientId": "EMP001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "notificationId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

---

### 2.2 標記通知已讀

#### NTF_CMD_002: 員工標記通知為已讀

**API 端點：** `PUT /api/v1/notifications/{id}/read`

**業務場景描述：**

員工點擊通知後，系統將通知標記為已讀（更新 readAt 時間戳）。
僅能標記自己的通知，不能標記他人的通知。

**業務規則：**
1. 載入指定 notificationId 的通知
2. 驗證通知的 recipientId 等於當前使用者的 employeeId
3. 呼叫 `notification.markAsRead()` 更新狀態
4. 儲存通知

**測試合約：**

```json
{
  "scenarioId": "NTF_CMD_002",
  "apiEndpoint": "PUT /api/v1/notifications/{id}/read",
  "controller": "HR12NotificationCmdController",
  "service": "MarkNotificationReadServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {
    "notificationId": "NTF-001"
  },
  "businessRules": [
    "通知的 recipientId 必須等於當前使用者 employeeId",
    "呼叫 notification.markAsRead() 設定 readAt 時間戳",
    "儲存更新後的通知"
  ],
  "expectedDataChanges": {
    "tables": ["notifications"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "notifications",
        "expectedFields": {
          "id": "NTF-001",
          "read_at": "NOT_NULL"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "NotificationReadEvent",
      "expectedFields": {
        "notificationId": "NTF-001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "notificationId", "type": "string", "notNull": true}
    ]
  }
}
```

---

### 2.3 標記全部已讀

#### NTF_CMD_003: 員工一鍵標記全部通知為已讀

**API 端點：** `PUT /api/v1/notifications/read-all`

**業務場景描述：**

員工點擊「全部標記已讀」按鈕，系統批次更新所有未讀通知。
系統僅處理當前使用者的未讀通知，不影響其他使用者。

**業務規則：**
1. 查詢當前使用者所有未讀通知
2. 批次呼叫 `notification.markAsRead()`
3. 批次儲存

**測試合約：**

```json
{
  "scenarioId": "NTF_CMD_003",
  "apiEndpoint": "PUT /api/v1/notifications/read-all",
  "controller": "HR12NotificationCmdController",
  "service": "MarkAllNotificationsReadServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {},
  "businessRules": [
    "僅處理當前使用者（recipientId = currentUser.employeeId）的未讀通知",
    "批次呼叫 markAsRead() 設定 readAt 時間戳",
    "批次儲存所有更新"
  ],
  "expectedDataChanges": {
    "tables": ["notifications"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "notifications",
        "expectedFields": {
          "read_at": "NOT_NULL"
        }
      }
    ]
  },
  "expectedDomainEvents": [],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "updatedCount", "type": "integer", "notNull": true}
    ]
  }
}
```

---

### 2.4 刪除通知

#### NTF_CMD_004: 員工軟刪除通知

**API 端點：** `DELETE /api/v1/notifications/{id}`

**業務場景描述：**

員工刪除不需要的通知（軟刪除，設定 isDeleted = true）。
只能刪除自己的通知。

**業務規則：**
1. 載入指定通知
2. 驗證 recipientId 為當前使用者
3. 執行軟刪除（isDeleted = true）
4. 儲存更新

**測試合約：**

```json
{
  "scenarioId": "NTF_CMD_004",
  "apiEndpoint": "DELETE /api/v1/notifications/{id}",
  "controller": "HR12NotificationCmdController",
  "service": "DeleteNotificationServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {
    "notificationId": "NTF-001"
  },
  "businessRules": [
    "通知的 recipientId 必須等於當前使用者",
    "執行軟刪除（isDeleted = true）",
    "儲存更新"
  ],
  "expectedDataChanges": {
    "tables": ["notifications"],
    "operations": [
      {
        "type": "SOFT_DELETE",
        "table": "notifications",
        "expectedFields": {
          "is_deleted": true
        }
      }
    ]
  },
  "expectedDomainEvents": [],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": []
  }
}
```

---

### 2.5 建立通知範本

#### NTF_CMD_005: HR 建立新通知範本

**API 端點：** `POST /api/v1/notifications/templates`

**業務場景描述：**

HR 管理員建立新的通知範本，定義標題、內容模板（含變數）、預設渠道等。
建立後範本狀態為 ACTIVE，可立即使用。

**業務規則：**
1. 建立 NotificationTemplate 聚合根
2. 儲存至 Repository
3. 回傳 templateId

**測試合約：**

```json
{
  "scenarioId": "NTF_CMD_005",
  "apiEndpoint": "POST /api/v1/notifications/templates",
  "controller": "HR12TemplateCmdController",
  "service": "CreateNotificationTemplateServiceImpl",
  "permission": "NOTIFICATION:TEMPLATE:MANAGE",
  "request": {
    "templateCode": "CUSTOM_REMINDER",
    "templateName": "自訂提醒範本",
    "subject": "重要提醒",
    "bodyTemplate": "親愛的 {{employeeName}}，請注意：{{message}}",
    "notificationType": "REMINDER",
    "defaultChannels": ["IN_APP", "EMAIL"]
  },
  "businessRules": [
    "建立 NotificationTemplate，初始狀態為 ACTIVE",
    "範本代碼必須唯一",
    "儲存範本至 Repository"
  ],
  "expectedDataChanges": {
    "tables": ["notification_templates"],
    "operations": [
      {
        "type": "INSERT",
        "table": "notification_templates",
        "expectedFields": {
          "template_code": "CUSTOM_REMINDER",
          "is_active": true,
          "is_deleted": false
        }
      }
    ]
  },
  "expectedDomainEvents": [],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "templateId", "type": "string", "notNull": true}
    ]
  }
}
```

---

### 2.6 更新通知偏好設定

#### NTF_CMD_006: 員工更新個人通知偏好

**API 端點：** `PUT /api/v1/notifications/preferences`

**業務場景描述：**

員工設定自己的通知偏好（啟用/停用各渠道、設定靜音時段等）。
若員工尚無偏好設定記錄，系統自動建立預設值後再更新。

**業務規則：**
1. 依當前使用者 employeeId 查詢偏好設定
2. 若不存在，建立預設偏好設定
3. 更新偏好設定欄位
4. 儲存

**測試合約：**

```json
{
  "scenarioId": "NTF_CMD_006",
  "apiEndpoint": "PUT /api/v1/notifications/preferences",
  "controller": "HR12PreferenceCmdController",
  "service": "UpdateNotificationPreferencesServiceImpl",
  "permission": "AUTHENTICATED",
  "request": {
    "emailEnabled": true,
    "pushEnabled": false,
    "inAppEnabled": true,
    "quietHoursStart": "22:00",
    "quietHoursEnd": "08:00"
  },
  "businessRules": [
    "依當前使用者 employeeId 查詢偏好設定",
    "若不存在則建立預設偏好設定",
    "更新並儲存偏好設定"
  ],
  "expectedDataChanges": {
    "tables": ["notification_preferences"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "notification_preferences",
        "expectedFields": {
          "email_enabled": true,
          "push_enabled": false
        }
      }
    ]
  },
  "expectedDomainEvents": [],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "preferenceId", "type": "string", "notNull": true }
    ]
  }
}
```

---

### 2.7 發布公告

#### NTF_CMD_007: HR 發布全員公告

**API 端點：** `POST /api/v1/notifications/announcements`

**業務場景描述：**

HR 發布重要公告（放假通知、制度更新等），可設定目標對象（全員/特定部門/特定角色）、
發布時間、過期時間。發布後通知服務會自動發送通知給目標對象。

**業務規則：**
1. 建立 Announcement 聚合根
2. 計算目標受衆人數
3. 儲存公告
4. 發布 AnnouncementPublishedEvent

**測試合約：**

```json
{
  "scenarioId": "NTF_CMD_007",
  "apiEndpoint": "POST /api/v1/notifications/announcements",
  "controller": "HR12AnnouncementCmdController",
  "service": "CreateAnnouncementServiceImpl",
  "permission": "ANNOUNCEMENT:CREATE",
  "request": {
    "title": "2026 年端午節放假公告",
    "content": "依政府規定，2026/06/19（五）至 06/21（日）共 3 天放假，敬請周知。",
    "priority": "NORMAL",
    "channels": ["IN_APP", "EMAIL"],
    "targetAudience": "ALL"
  },
  "businessRules": [
    "建立 Announcement，publishedBy 設為當前使用者 userId",
    "status 設為 PUBLISHED",
    "儲存公告至 Repository",
    "發布 AnnouncementPublishedEvent"
  ],
  "expectedDataChanges": {
    "tables": ["announcements"],
    "operations": [
      {
        "type": "INSERT",
        "table": "announcements",
        "expectedFields": {
          "title": "2026 年端午節放假公告",
          "status": "PUBLISHED",
          "is_deleted": false
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "AnnouncementPublishedEvent",
      "expectedFields": {
        "title": "2026 年端午節放假公告"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "announcementId", "type": "string", "notNull": true}
    ]
  }
}
```

---

**文件完成日期:** 2026-02-24
**合約場景總數:** 11 個（4 Query + 7 Command）
