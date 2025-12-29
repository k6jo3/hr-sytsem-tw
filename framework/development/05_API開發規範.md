# API 開發規範

**版本:** 1.0
**日期:** 2025-12-29
**目的:** 定義 HR 系統 API 文件的標準格式與開發規範，確保所有 API 規格完整且一致

---

## 1. API 文件結構標準

每個服務的 API 設計章節應包含以下結構：

```
## 9. API設計

### 9.1 Controller命名對照
### 9.2 API總覽 (N個端點)
### 9.3 {模組名稱}API
    #### 9.3.1 {API名稱}
    #### 9.3.2 {API名稱}
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

---

## 3. Request 規格說明

### 3.1 Headers

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | Bearer Token，格式：`Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

### 3.2 驗證規則標記 (Bean Validation)

| 標記 | 說明 | 範例 |
|:---|:---|:---|
| `@NotNull` | 不可為 null | 必填欄位 |
| `@NotBlank` | 不可為空字串 | 字串必填 |
| `@Size(min, max)` | 長度限制 | `@Size(1, 100)` |
| `@Min` / `@Max` | 數值範圍 | `@Min(0)` |
| `@Email` | Email格式 | - |
| `@Pattern(regex)` | 正規表示式 | 手機號碼格式 |

---

## 4. Response 規格說明

### 4.1 統一回應格式 (ApiResponse)

所有 API (除檔案下載外) 必須回傳統一的 JSON 結構：

**成功回應 (Success):**
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": { ... },
  "timestamp": "2025-12-29T10:30:00Z"
}
```

**分頁回應 (PageResponse):**
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

**錯誤回應 (Error):**
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

---

## 5. 錯誤處理規格

### 5.1 HTTP 狀態碼對照

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

### 5.2 錯誤碼命名規則

格式：`{CATEGORY}_{SPECIFIC_ERROR}`

| 分類 | 說明 | 範例 |
|:---|:---|:---|
| `AUTH_` | 認證相關 | `AUTH_TOKEN_EXPIRED` |
| `AUTHZ_` | 授權相關 | `AUTHZ_PERMISSION_DENIED` |
| `VALIDATION_` | 驗證相關 | `VALIDATION_EMAIL_FORMAT` |
| `RESOURCE_` | 資源相關 | `RESOURCE_NOT_FOUND` |
| `BUSINESS_` | 業務邏輯 | `BUSINESS_INSUFFICIENT_LEAVE` |
| `SYSTEM_` | 系統錯誤 | `SYSTEM_DATABASE_ERROR` |

---

## 6. 詳細 API 規格範本

撰寫 API 文件時，請依據以下範本：

```markdown
#### 9.X.Y {API名稱}

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `{METHOD} /api/v1/{resource}` |
| Controller | `HR{DD}{Screen}{Cmd/Qry}Controller` |
| Service | `{Verb}{Noun}ServiceImpl` |
| 權限 | `{resource}:{action}` |

**用途說明**

{說明這個 API 的業務用途}

**業務邏輯**

1. {步驟1}
2. {步驟2}

**Request Body**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| username | String | ✅ | 登入帳號 |

**Response Body**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| userId | UUID | 使用者ID |

**領域事件**

| 事件名稱 | Topic |
|:---|:---|
| UserCreatedEvent | `iam.user.created` |
```

---

**文件版本:** 1.0
**建立日期:** 2025-12-29
