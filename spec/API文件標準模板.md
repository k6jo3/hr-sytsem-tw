# API 文件標準模板

**版本:** 1.0
**日期:** 2025-12-29
**目的:** 定義 HR 系統 API 文件的標準格式，確保所有 API 規格完整且一致

---

## 目錄

1. [文件結構概述](#1-文件結構概述)
2. [API 總覽表格式](#2-api-總覽表格式)
3. [API 詳細規格模板](#3-api-詳細規格模板)
4. [Request 規格說明](#4-request-規格說明)
5. [Response 規格說明](#5-response-規格說明)
6. [錯誤處理規格](#6-錯誤處理規格)
7. [範例：完整 API 規格](#7-範例完整-api-規格)

---

## 1. 文件結構概述

每個服務的 API 設計章節應包含以下結構：

```
## 9. API設計

### 9.1 Controller命名對照
### 9.2 API總覽 (N個端點)
### 9.3 {模組名稱}API
    #### 9.3.1 {API名稱}
    #### 9.3.2 {API名稱}
    ...
### 9.4 {模組名稱}API
    ...
```

---

## 2. API 總覽表格式

### 標準格式

```markdown
| 端點 | 方法 | Controller | 說明 | 權限 |
|:---|:---:|:---|:---|:---|
| `/api/v1/users` | GET | HR01UserQryController | 查詢使用者列表 | user:read |
| `/api/v1/users/{id}` | GET | HR01UserQryController | 查詢使用者詳情 | user:read |
| `/api/v1/users` | POST | HR01UserCmdController | 建立使用者 | user:create |
```

### 欄位說明

| 欄位 | 必填 | 說明 |
|:---|:---:|:---|
| 端點 | ✅ | API 路徑，包含路徑參數 |
| 方法 | ✅ | HTTP Method (GET/POST/PUT/DELETE/PATCH) |
| Controller | ✅ | 對應的 Controller 類別名稱 |
| 說明 | ✅ | API 用途簡述（10字以內） |
| 權限 | ✅ | 所需權限代碼（無則填 `-`） |

---

## 3. API 詳細規格模板

每個 API 必須包含以下完整規格：

```markdown
#### 9.X.Y {API名稱}

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `{METHOD} /api/v1/{resource}` |
| Controller | `HR{DD}{Screen}{Cmd/Qry}Controller` |
| Service | `{Verb}{Noun}ServiceImpl` |
| 權限 | `{resource}:{action}` |
| 版本 | v1 |

**用途說明**

{說明這個 API 的業務用途，解決什麼問題，誰會使用}

**業務邏輯**

1. {步驟1：說明處理邏輯}
2. {步驟2：說明驗證規則}
3. {步驟3：說明計算邏輯}
4. {步驟4：說明副作用（事件發布、通知等）}

**Request**

- Headers
- Path Parameters（如有）
- Query Parameters（如有）
- Request Body（如有）

**Response**

- 成功回應 (200/201)
- 錯誤回應

**領域事件**

{列出此 API 會發布的領域事件}
```

---

## 4. Request 規格說明

### 4.1 Headers

```markdown
**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | Bearer Token，格式：`Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |
| X-Tenant-Id | ⬚ | 租戶ID（多租戶情境） |
```

### 4.2 Path Parameters

```markdown
**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 資源唯一識別碼 | `550e8400-e29b-41d4-a716-446655440000` |
```

### 4.3 Query Parameters

```markdown
**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| page | Integer | ⬚ | 1 | 頁碼（從1開始） | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數（最大100） | `20` |
| sort | String | ⬚ | createdAt,desc | 排序欄位與方向 | `name,asc` |
| search | String | ⬚ | - | 關鍵字搜尋 | `john` |
| status | Enum | ⬚ | - | 狀態篩選 | `ACTIVE` |
| startDate | Date | ⬚ | - | 開始日期 (ISO 8601) | `2025-01-01` |
| endDate | Date | ⬚ | - | 結束日期 (ISO 8601) | `2025-12-31` |
```

### 4.4 Request Body

```markdown
**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| username | String | ✅ | Email格式，最長255字元 | 登入帳號 | `"john@company.com"` |
| password | String | ✅ | 8~128字元，含大小寫+數字 | 密碼 | `"Password123"` |
| employeeId | UUID | ✅ | 必須存在於Organization服務 | 關聯員工ID | `"550e8400-..."` |
| roleIds | UUID[] | ✅ | 至少1個，最多10個 | 指派角色 | `["role-1", "role-2"]` |
| isActive | Boolean | ⬚ | - | 是否啟用 | `true` |
| metadata | Object | ⬚ | - | 擴充資料 | `{"key": "value"}` |
```

**範例：**
```json
{
  "username": "john.doe@company.com",
  "password": "Password123!",
  "employeeId": "550e8400-e29b-41d4-a716-446655440000",
  "roleIds": ["role-uuid-1", "role-uuid-2"],
  "isActive": true
}
```

### 4.5 欄位類型對照

| 類型 | Java | TypeScript | 說明 |
|:---|:---|:---|:---|
| String | String | string | 字串 |
| Integer | Integer/int | number | 整數 |
| Long | Long/long | number | 長整數 |
| Decimal | BigDecimal | number | 高精度小數（金額用） |
| Boolean | Boolean | boolean | 布林值 |
| UUID | UUID | string | UUID字串 |
| Date | LocalDate | string | 日期 (YYYY-MM-DD) |
| DateTime | LocalDateTime | string | 日期時間 (ISO 8601) |
| Enum | Enum | string | 列舉值 |
| Object | Map/POJO | object | 巢狀物件 |
| Array | List | T[] | 陣列 |

### 4.6 驗證規則標記

| 標記 | 說明 | 範例 |
|:---|:---|:---|
| `@NotNull` | 不可為 null | 必填欄位 |
| `@NotBlank` | 不可為空字串 | 字串必填 |
| `@Size(min, max)` | 長度限制 | `@Size(1, 100)` |
| `@Min(value)` | 最小值 | `@Min(0)` |
| `@Max(value)` | 最大值 | `@Max(100)` |
| `@Email` | Email格式 | - |
| `@Pattern(regex)` | 正規表示式 | `@Pattern("^[A-Z]{2}\\d{4}$")` |
| `@Past` | 必須是過去時間 | 生日 |
| `@Future` | 必須是未來時間 | 到期日 |
| `@Positive` | 正數 | 金額 |
| `@PositiveOrZero` | 正數或零 | 數量 |

---

## 5. Response 規格說明

### 5.1 統一回應格式

**成功回應：**
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": { ... },
  "timestamp": "2025-12-29T10:30:00Z"
}
```

**分頁回應：**
```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [ ... ],
    "page": 1,
    "size": 10,
    "totalElements": 156,
    "totalPages": 16
  },
  "timestamp": "2025-12-29T10:30:00Z"
}
```

**錯誤回應：**
```json
{
  "code": "VALIDATION_ERROR",
  "message": "輸入資料驗證失敗",
  "errors": [
    {
      "field": "email",
      "message": "Email格式不正確"
    }
  ],
  "timestamp": "2025-12-29T10:30:00Z"
}
```

### 5.2 Response Body 規格

```markdown
**Response Body (200 OK)**

| 欄位 | 類型 | 說明 | 範例 |
|:---|:---|:---|:---|
| userId | UUID | 使用者唯一識別碼 | `"550e8400-..."` |
| username | String | 登入帳號 | `"john@company.com"` |
| displayName | String | 顯示名稱 | `"John Doe"` |
| status | Enum | 帳號狀態 | `"ACTIVE"` |
| roles | Role[] | 角色列表 | `[{...}]` |
| createdAt | DateTime | 建立時間 | `"2025-01-15T10:30:00Z"` |

**Role 物件結構**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| roleId | UUID | 角色ID |
| roleName | String | 角色代碼 |
| displayName | String | 角色顯示名稱 |
```

---

## 6. 錯誤處理規格

### 6.1 HTTP 狀態碼對照

| HTTP 狀態碼 | 使用情境 |
|:---:|:---|
| 200 | 請求成功（Query、Update、Delete） |
| 201 | 資源建立成功（Create） |
| 204 | 成功但無回傳內容 |
| 400 | 請求格式錯誤、驗證失敗 |
| 401 | 未授權（未登入、Token過期） |
| 403 | 禁止存取（無權限） |
| 404 | 資源不存在 |
| 409 | 資源衝突（重複建立等） |
| 422 | 業務邏輯錯誤 |
| 500 | 伺服器內部錯誤 |

### 6.2 錯誤碼命名規則

```
{CATEGORY}_{SPECIFIC_ERROR}
```

**Category 分類：**

| 分類 | 說明 | 範例 |
|:---|:---|:---|
| `AUTH_` | 認證相關 | `AUTH_TOKEN_EXPIRED` |
| `AUTHZ_` | 授權相關 | `AUTHZ_PERMISSION_DENIED` |
| `VALIDATION_` | 驗證相關 | `VALIDATION_EMAIL_FORMAT` |
| `RESOURCE_` | 資源相關 | `RESOURCE_NOT_FOUND` |
| `BUSINESS_` | 業務邏輯 | `BUSINESS_INSUFFICIENT_LEAVE` |
| `SYSTEM_` | 系統錯誤 | `SYSTEM_DATABASE_ERROR` |

### 6.3 API 錯誤碼表格式

```markdown
**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_EMAIL_FORMAT | Email格式不正確 | 檢查Email格式 |
| 400 | VALIDATION_PASSWORD_WEAK | 密碼強度不足 | 密碼需含大小寫+數字 |
| 401 | AUTH_TOKEN_EXPIRED | Token已過期 | 重新登入或刷新Token |
| 403 | AUTHZ_PERMISSION_DENIED | 無此操作權限 | 聯繫管理員授權 |
| 404 | RESOURCE_USER_NOT_FOUND | 使用者不存在 | 確認使用者ID |
| 409 | RESOURCE_USER_EXISTS | 使用者帳號已存在 | 使用其他帳號名稱 |
| 422 | BUSINESS_ACCOUNT_LOCKED | 帳號已被鎖定 | 等待解鎖或聯繫管理員 |
```

---

## 7. 範例：完整 API 規格

以下為一個完整的 API 規格範例，供撰寫時參考：

---

### 9.3.1 建立使用者

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/users` |
| Controller | `HR01UserCmdController` |
| Service | `CreateUserServiceImpl` |
| 權限 | `user:create` |
| 版本 | v1 |

**用途說明**

HR 管理員建立新的系統使用者帳號。新使用者會收到一封包含臨時密碼的歡迎郵件，首次登入時必須修改密碼。

**業務邏輯**

1. **驗證請求資料**
   - username 必須為有效 Email 格式
   - username 在同一 tenant 內必須唯一
   - employeeId 必須存在於 Organization 服務
   - roleIds 至少指定一個角色，所有角色必須存在

2. **建立使用者**
   - 產生 UUID 作為 userId
   - 產生 8 位隨機臨時密碼（含大小寫+數字）
   - 密碼使用 BCrypt 加密後儲存
   - 設定 status = ACTIVE
   - 設定 passwordChangedAt = null（強制首次登入改密碼）

3. **建立角色關聯**
   - 在 user_roles 表建立使用者與角色的關聯

4. **發送通知**
   - 若 sendWelcomeEmail = true，發送歡迎郵件含臨時密碼
   - 發布 UserCreatedEvent 至 Kafka

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| username | String | ✅ | Email格式，最長255字元，tenant內唯一 | 登入帳號（通常與email相同） | `"jane.doe@company.com"` |
| email | String | ✅ | Email格式，最長255字元 | 使用者電子郵件 | `"jane.doe@company.com"` |
| employeeId | UUID | ✅ | 必須存在於Organization服務 | 關聯員工ID | `"emp-uuid-001"` |
| roleIds | UUID[] | ✅ | 1~10個，所有ID必須存在 | 指派角色ID列表 | `["role-uuid-1"]` |
| sendWelcomeEmail | Boolean | ⬚ | - | 是否發送歡迎郵件（預設true） | `true` |

**範例：**
```json
{
  "username": "jane.doe@company.com",
  "email": "jane.doe@company.com",
  "employeeId": "550e8400-e29b-41d4-a716-446655440001",
  "roleIds": ["00000000-0000-0000-0000-000000000007"],
  "sendWelcomeEmail": true
}
```

**Response**

**成功回應 (201 Created)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| userId | UUID | 新建使用者ID |
| username | String | 登入帳號 |
| status | Enum | 帳號狀態（ACTIVE） |
| createdAt | DateTime | 建立時間 |

```json
{
  "code": "SUCCESS",
  "message": "使用者建立成功",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440099",
    "username": "jane.doe@company.com",
    "status": "ACTIVE",
    "createdAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_EMAIL_FORMAT | Email格式不正確 | 檢查username/email格式 |
| 400 | VALIDATION_ROLE_REQUIRED | 至少需指派一個角色 | 在roleIds中加入角色ID |
| 401 | AUTH_TOKEN_INVALID | Token無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無user:create權限 | 聯繫管理員授權 |
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認employeeId正確性 |
| 404 | RESOURCE_ROLE_NOT_FOUND | 角色不存在 | 確認roleIds正確性 |
| 409 | RESOURCE_USER_EXISTS | 使用者帳號已存在 | 使用其他帳號名稱 |
| 409 | RESOURCE_EMPLOYEE_HAS_USER | 該員工已有帳號 | 一個員工只能有一個帳號 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| UserCreatedEvent | `iam.user.created` | 使用者建立完成，通知其他服務 |

**事件 Payload：**
```json
{
  "eventId": "evt-uuid-001",
  "eventType": "UserCreatedEvent",
  "occurredAt": "2025-12-29T10:30:00Z",
  "aggregateId": "550e8400-e29b-41d4-a716-446655440099",
  "aggregateType": "User",
  "payload": {
    "userId": "550e8400-e29b-41d4-a716-446655440099",
    "username": "jane.doe@company.com",
    "email": "jane.doe@company.com",
    "employeeId": "550e8400-e29b-41d4-a716-446655440001",
    "tenantId": "tenant-uuid-001",
    "roleIds": ["00000000-0000-0000-0000-000000000007"]
  }
}
```

---

## 附錄 A：常用列舉值定義

### A.1 通用狀態

```java
public enum Status {
    ACTIVE("啟用"),
    INACTIVE("停用"),
    DELETED("已刪除");
}
```

### A.2 審核狀態

```java
public enum ApprovalStatus {
    PENDING("待審核"),
    APPROVED("已核准"),
    REJECTED("已駁回"),
    CANCELLED("已取消");
}
```

### A.3 使用者狀態

```java
public enum UserStatus {
    ACTIVE("啟用"),
    INACTIVE("停用"),
    LOCKED("鎖定");
}
```

---

## 附錄 B：API 文件檢查清單

撰寫 API 文件時，請確認以下項目皆已完成：

### 基本資訊
- [ ] 端點路徑與 HTTP Method
- [ ] Controller 類別名稱
- [ ] Service 類別名稱
- [ ] 所需權限代碼

### 用途與邏輯
- [ ] 用途說明（誰用、解決什麼問題）
- [ ] 業務邏輯步驟（驗證→處理→副作用）
- [ ] 計算公式（如有）

### Request
- [ ] Headers 說明
- [ ] Path Parameters（類型、說明、範例）
- [ ] Query Parameters（類型、必填、預設值、說明）
- [ ] Request Body（每個欄位的類型、必填、驗證規則、說明、範例）
- [ ] 完整 JSON 範例

### Response
- [ ] 成功回應（狀態碼、欄位說明、JSON範例）
- [ ] 巢狀物件結構說明
- [ ] 分頁資訊（如適用）

### 錯誤處理
- [ ] 所有可能的錯誤碼
- [ ] 每個錯誤的 HTTP 狀態碼
- [ ] 錯誤說明與處理建議

### 領域事件
- [ ] 會發布的事件名稱
- [ ] Kafka Topic
- [ ] 事件 Payload 結構

---

**文件建立日期:** 2025-12-29
**版本:** 1.0
