# IAM服務 API 詳細規格

**版本:** 1.0
**日期:** 2025-12-29
**服務代碼:** HR01
**API 總數:** 29 個端點

---

## 目錄

1. [認證 API (Auth)](#1-認證-api-auth) - 9 個端點
2. [使用者管理 API (User)](#2-使用者管理-api-user) - 9 個端點
3. [角色管理 API (Role)](#3-角色管理-api-role) - 6 個端點
4. [權限管理 API (Permission)](#4-權限管理-api-permission) - 2 個端點
5. [個人資料 API (Profile)](#5-個人資料-api-profile) - 3 個端點

---

## 1. 認證 API (Auth)

### 1.1 使用者登入

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/auth/login` |
| Controller | `HR01AuthCmdController` |
| Service | `LoginServiceImpl` |
| 權限 | 無（公開 API） |
| 版本 | v1 |

**用途說明**

使用者透過帳號密碼進行身份驗證，成功後取得 JWT Token 用於後續 API 呼叫。支援多租戶情境。

**業務邏輯**

1. **驗證請求資料**
   - username 不可為空
   - password 不可為空
   - tenantId 為選填（單租戶情境可省略）

2. **查詢使用者**
   - 根據 username + tenantId 查詢使用者
   - 若使用者不存在，回傳「帳號或密碼錯誤」（不透露帳號是否存在）

3. **檢查帳號狀態**
   - INACTIVE：回傳「帳號已停用」
   - LOCKED：檢查 lockedUntil，若已過期則自動解鎖，否則回傳「帳號已鎖定」

4. **驗證密碼**
   - 使用 BCrypt 比對密碼
   - 密碼錯誤：failedLoginAttempts + 1
   - 連續錯誤 5 次：鎖定帳號 30 分鐘

5. **登入成功處理**
   - 重置 failedLoginAttempts = 0
   - 更新 lastLoginAt
   - 產生 AccessToken（有效期 1 小時）
   - 產生 RefreshToken（有效期 7 天）
   - 儲存 RefreshToken 至資料庫
   - 記錄登入日誌（IP、User-Agent）

6. **發布事件**
   - 發布 UserLoggedInEvent 至 Kafka

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| username | String | ✅ | 最長255字元 | 登入帳號（通常為Email） | `"john.doe@company.com"` |
| password | String | ✅ | 最長128字元 | 密碼 | `"Password123!"` |
| tenantId | UUID | ⬚ | 有效的租戶ID | 租戶識別碼（多租戶情境） | `"tenant-uuid"` |
| rememberMe | Boolean | ⬚ | - | 記住我（延長Token有效期） | `false` |

**範例：**
```json
{
  "username": "john.doe@company.com",
  "password": "Password123!",
  "tenantId": "550e8400-e29b-41d4-a716-446655440000",
  "rememberMe": false
}
```

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| accessToken | String | JWT Access Token |
| refreshToken | String | JWT Refresh Token |
| expiresIn | Integer | Access Token 有效秒數 |
| tokenType | String | Token 類型（固定為 "Bearer"） |
| user | Object | 使用者資訊 |
| user.userId | UUID | 使用者ID |
| user.username | String | 登入帳號 |
| user.displayName | String | 顯示名稱 |
| user.email | String | Email |
| user.employeeId | UUID | 關聯員工ID |
| user.roles | String[] | 角色代碼列表 |
| user.permissions | String[] | 權限代碼列表 |

```json
{
  "code": "SUCCESS",
  "message": "登入成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "tokenType": "Bearer",
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440001",
      "username": "john.doe@company.com",
      "displayName": "John Doe",
      "email": "john.doe@company.com",
      "employeeId": "550e8400-e29b-41d4-a716-446655440002",
      "roles": ["HR_ADMIN", "EMPLOYEE"],
      "permissions": ["user:read", "user:write", "employee:profile:read"]
    }
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_USERNAME_REQUIRED | 帳號不可為空 | 填入帳號 |
| 400 | VALIDATION_PASSWORD_REQUIRED | 密碼不可為空 | 填入密碼 |
| 401 | AUTH_INVALID_CREDENTIALS | 帳號或密碼錯誤 | 確認帳號密碼 |
| 401 | AUTH_ACCOUNT_INACTIVE | 帳號已停用 | 聯繫管理員啟用 |
| 401 | AUTH_ACCOUNT_LOCKED | 帳號已被鎖定 | 等待解鎖或聯繫管理員 |
| 404 | RESOURCE_TENANT_NOT_FOUND | 租戶不存在 | 確認租戶ID |

**錯誤回應範例（帳號鎖定）：**
```json
{
  "code": "AUTH_ACCOUNT_LOCKED",
  "message": "帳號已被鎖定，請30分鐘後再試",
  "data": {
    "lockedUntil": "2025-12-29T11:30:00Z",
    "remainingMinutes": 25
  }
}
```

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| UserLoggedInEvent | `iam.user.logged-in` | 登入成功時發布 |

---

### 1.2 使用者登出

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/auth/logout` |
| Controller | `HR01AuthCmdController` |
| Service | `LogoutServiceImpl` |
| 權限 | 需登入 |
| 版本 | v1 |

**用途說明**

使用者登出系統，撤銷當前的 Refresh Token，使其失效。

**業務邏輯**

1. **驗證 Token**
   - 從 Header 取得 Access Token
   - 驗證 Token 有效性

2. **撤銷 Refresh Token**
   - 從資料庫刪除該使用者的 Refresh Token

3. **清除快取**
   - 從 Redis 清除使用者 Principal 快取

4. **記錄日誌**
   - 記錄登出日誌

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Request Body**

| 欄位 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| refreshToken | String | ⬚ | 要撤銷的 Refresh Token | `"eyJhbGci..."` |
| logoutAll | Boolean | ⬚ | 是否登出所有裝置（預設false） | `false` |

**範例：**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "logoutAll": false
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "登出成功"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 401 | AUTH_TOKEN_EXPIRED | Token 已過期 | 重新登入 |

---

### 1.3 刷新 Token

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/auth/refresh-token` |
| Controller | `HR01AuthCmdController` |
| Service | `RefreshTokenServiceImpl` |
| 權限 | 無（使用 Refresh Token） |
| 版本 | v1 |

**用途說明**

當 Access Token 過期時，使用 Refresh Token 取得新的 Access Token，無需重新登入。

**業務邏輯**

1. **驗證 Refresh Token**
   - 驗證 Token 簽章
   - 驗證 Token 未過期
   - 驗證 Token 存在於資料庫（未被撤銷）

2. **檢查使用者狀態**
   - 使用者必須仍為 ACTIVE 狀態

3. **產生新 Token**
   - 產生新的 Access Token
   - 產生新的 Refresh Token（Token Rotation）
   - 撤銷舊的 Refresh Token

4. **更新資料庫**
   - 儲存新的 Refresh Token
   - 刪除舊的 Refresh Token

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| refreshToken | String | ✅ | Refresh Token | `"eyJhbGci..."` |

**範例：**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| accessToken | String | 新的 Access Token |
| refreshToken | String | 新的 Refresh Token |
| expiresIn | Integer | Access Token 有效秒數 |
| tokenType | String | Token 類型 |

```json
{
  "code": "SUCCESS",
  "message": "Token 刷新成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "tokenType": "Bearer"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_REFRESH_TOKEN_REQUIRED | Refresh Token 不可為空 | 提供 Refresh Token |
| 401 | AUTH_REFRESH_TOKEN_INVALID | Refresh Token 無效 | 重新登入 |
| 401 | AUTH_REFRESH_TOKEN_EXPIRED | Refresh Token 已過期 | 重新登入 |
| 401 | AUTH_REFRESH_TOKEN_REVOKED | Refresh Token 已被撤銷 | 重新登入 |
| 401 | AUTH_ACCOUNT_INACTIVE | 帳號已停用 | 聯繫管理員 |

---

### 1.4 忘記密碼

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/auth/forgot-password` |
| Controller | `HR01AuthCmdController` |
| Service | `ForgotPasswordServiceImpl` |
| 權限 | 無（公開 API） |
| 版本 | v1 |

**用途說明**

使用者忘記密碼時，輸入註冊 Email，系統發送密碼重置連結。

**業務邏輯**

1. **驗證 Email**
   - Email 格式驗證
   - 查詢使用者是否存在

2. **安全考量**
   - 無論使用者是否存在，都回傳成功訊息（避免帳號列舉攻擊）

3. **產生重置 Token**
   - 產生隨機 Token（有效期 24 小時）
   - 儲存 Token 與使用者關聯

4. **發送郵件**
   - 發送包含重置連結的郵件
   - 連結格式：`{frontendUrl}/reset-password?token={token}`

5. **發布事件**
   - 發布 PasswordResetRequestedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| email | String | ✅ | Email格式 | 註冊時使用的 Email | `"john@company.com"` |
| tenantId | UUID | ⬚ | - | 租戶ID | `"tenant-uuid"` |

**範例：**
```json
{
  "email": "john.doe@company.com",
  "tenantId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "若此 Email 已註冊，您將收到密碼重置郵件"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_EMAIL_REQUIRED | Email 不可為空 | 填入 Email |
| 400 | VALIDATION_EMAIL_FORMAT | Email 格式不正確 | 確認 Email 格式 |
| 429 | RATE_LIMIT_EXCEEDED | 請求過於頻繁 | 稍後再試（限制：每分鐘1次） |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| PasswordResetRequestedEvent | `iam.password.reset-requested` | 發送重置郵件 |

---

### 1.5 重置密碼

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/auth/reset-password` |
| Controller | `HR01AuthCmdController` |
| Service | `ResetPasswordServiceImpl` |
| 權限 | 無（使用重置 Token） |
| 版本 | v1 |

**用途說明**

使用者點擊重置連結後，設定新密碼完成密碼重置。

**業務邏輯**

1. **驗證重置 Token**
   - Token 必須存在
   - Token 未過期（24小時內）
   - Token 未被使用過

2. **驗證新密碼**
   - 長度 8~128 字元
   - 包含大小寫字母和數字
   - 不可與舊密碼相同

3. **更新密碼**
   - 使用 BCrypt 加密新密碼
   - 更新 passwordChangedAt
   - 標記重置 Token 為已使用

4. **安全處理**
   - 撤銷所有 Refresh Token（強制重新登入）
   - 解鎖帳號（若被鎖定）

5. **發送通知**
   - 發送密碼已變更通知郵件

6. **發布事件**
   - 發布 PasswordChangedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| token | String | ✅ | - | 重置 Token | `"abc123..."` |
| newPassword | String | ✅ | 8~128字元，含大小寫+數字 | 新密碼 | `"NewPassword123!"` |
| confirmPassword | String | ✅ | 必須與 newPassword 相同 | 確認新密碼 | `"NewPassword123!"` |

**範例：**
```json
{
  "token": "abc123def456ghi789",
  "newPassword": "NewPassword123!",
  "confirmPassword": "NewPassword123!"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "密碼重置成功，請使用新密碼登入"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_TOKEN_REQUIRED | Token 不可為空 | 使用郵件中的連結 |
| 400 | VALIDATION_PASSWORD_REQUIRED | 密碼不可為空 | 填入新密碼 |
| 400 | VALIDATION_PASSWORD_WEAK | 密碼強度不足 | 密碼需含大小寫+數字，至少8字元 |
| 400 | VALIDATION_PASSWORD_MISMATCH | 兩次密碼不一致 | 確認密碼輸入相同 |
| 400 | AUTH_RESET_TOKEN_INVALID | 重置 Token 無效 | 重新申請密碼重置 |
| 400 | AUTH_RESET_TOKEN_EXPIRED | 重置 Token 已過期 | 重新申請密碼重置 |
| 400 | AUTH_RESET_TOKEN_USED | 重置 Token 已使用 | 重新申請密碼重置 |
| 422 | BUSINESS_PASSWORD_SAME_AS_OLD | 新密碼不可與舊密碼相同 | 使用不同的密碼 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| PasswordChangedEvent | `iam.password.changed` | 密碼變更完成 |

---

### 1.6 Google OAuth 登入

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/auth/oauth/google` |
| Controller | `HR01AuthCmdController` |
| Service | `GoogleOAuthServiceImpl` |
| 權限 | 無（公開 API） |
| 版本 | v1 |

**用途說明**

重導向至 Google OAuth 授權頁面，開始 Google 單一登入流程。

**業務邏輯**

1. **產生 State Token**
   - 產生隨機 state 參數防止 CSRF 攻擊
   - 暫存 state 至 Redis（有效期 10 分鐘）

2. **組合授權 URL**
   - client_id: Google OAuth Client ID
   - redirect_uri: 回調 URL
   - scope: email, profile, openid
   - state: CSRF Token

3. **重導向**
   - 302 重導向至 Google 授權頁面

**Request**

**Query Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| tenantId | UUID | ⬚ | 租戶ID | `"tenant-uuid"` |
| returnUrl | String | ⬚ | 登入成功後跳轉URL | `"/dashboard"` |

**Response**

**成功回應 (302 Found)**

重導向至 Google 授權頁面

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 500 | SYSTEM_OAUTH_CONFIG_ERROR | OAuth 設定錯誤 | 聯繫系統管理員 |

---

### 1.7 Google OAuth 回調

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/auth/oauth/google/callback` |
| Controller | `HR01AuthCmdController` |
| Service | `GoogleOAuthCallbackServiceImpl` |
| 權限 | 無（OAuth 回調） |
| 版本 | v1 |

**用途說明**

接收 Google OAuth 授權回調，驗證授權碼並完成登入或帳號連結。

**業務邏輯**

1. **驗證 State**
   - 比對 state 參數與 Redis 中的值
   - 驗證失敗則拒絕請求

2. **交換授權碼**
   - 使用 code 向 Google 換取 Access Token
   - 取得使用者資訊（email, name, picture）

3. **查詢/建立使用者**
   - 查詢是否已有帳號連結此 Google 帳號
   - 若無，檢查 email 是否已存在系統使用者
     - 若存在：進行帳號連結
     - 若不存在：依設定決定是否自動建立帳號

4. **產生 Token**
   - 產生 Access Token 和 Refresh Token
   - 記錄登入日誌

5. **重導向**
   - 重導向至前端，帶入 Token

**Request**

**Query Parameters**

| 參數名 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| code | String | ✅ | Google 授權碼 |
| state | String | ✅ | CSRF 防護 Token |
| error | String | ⬚ | 錯誤碼（使用者拒絕授權時） |

**Response**

**成功回應 (302 Found)**

重導向至前端，URL 帶入 Token：
```
{frontendUrl}/oauth/callback?accessToken={token}&refreshToken={token}
```

**錯誤回應 (302 Found)**

重導向至前端錯誤頁面：
```
{frontendUrl}/oauth/error?code={errorCode}&message={message}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | AUTH_OAUTH_STATE_INVALID | State 驗證失敗 | 重新發起 OAuth 流程 |
| 400 | AUTH_OAUTH_CODE_INVALID | 授權碼無效 | 重新發起 OAuth 流程 |
| 400 | AUTH_OAUTH_USER_DENIED | 使用者拒絕授權 | 使用帳號密碼登入 |
| 409 | AUTH_OAUTH_EMAIL_EXISTS | Email 已被其他帳號使用 | 使用該帳號登入後連結 |
| 422 | BUSINESS_AUTO_CREATE_DISABLED | 不允許自動建立帳號 | 聯繫管理員建立帳號 |

---

### 1.8 Microsoft OAuth 登入

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/auth/oauth/microsoft` |
| Controller | `HR01AuthCmdController` |
| Service | `MicrosoftOAuthServiceImpl` |
| 權限 | 無（公開 API） |
| 版本 | v1 |

**用途說明**

重導向至 Microsoft OAuth 授權頁面，開始 Microsoft 單一登入流程。

**業務邏輯**

與 Google OAuth 登入類似，參考 [1.6 Google OAuth 登入](#16-google-oauth-登入)

**Request**

**Query Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| tenantId | UUID | ⬚ | 租戶ID | `"tenant-uuid"` |
| returnUrl | String | ⬚ | 登入成功後跳轉URL | `"/dashboard"` |

**Response**

**成功回應 (302 Found)**

重導向至 Microsoft 授權頁面

---

### 1.9 Microsoft OAuth 回調

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/auth/oauth/microsoft/callback` |
| Controller | `HR01AuthCmdController` |
| Service | `MicrosoftOAuthCallbackServiceImpl` |
| 權限 | 無（OAuth 回調） |
| 版本 | v1 |

**用途說明**

接收 Microsoft OAuth 授權回調，驗證授權碼並完成登入或帳號連結。

**業務邏輯**

與 Google OAuth 回調類似，參考 [1.7 Google OAuth 回調](#17-google-oauth-回調)

**Request**

**Query Parameters**

| 參數名 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| code | String | ✅ | Microsoft 授權碼 |
| state | String | ✅ | CSRF 防護 Token |
| error | String | ⬚ | 錯誤碼 |
| error_description | String | ⬚ | 錯誤描述 |

**Response**

與 Google OAuth 回調相同

---

## 2. 使用者管理 API (User)

### 2.1 查詢使用者列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/users` |
| Controller | `HR01UserQryController` |
| Service | `GetUserListServiceImpl` |
| 權限 | `user:read` |
| 版本 | v1 |

**用途說明**

HR 管理員查詢系統使用者列表，支援分頁、排序、關鍵字搜尋與多條件篩選。

**業務邏輯**

1. **權限檢查**
   - 需具備 user:read 權限

2. **查詢條件處理**
   - search：模糊比對 username、email、displayName
   - status：精確比對帳號狀態
   - roleId：篩選具有特定角色的使用者
   - departmentId：篩選特定部門的使用者（需跨服務查詢）

3. **分頁與排序**
   - 預設每頁 10 筆
   - 預設按 createdAt 降冪排序

4. **多租戶隔離**
   - 自動加入 tenantId 條件，只能查詢同租戶的使用者

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| page | Integer | ⬚ | 1 | 頁碼（從1開始） | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數（1~100） | `20` |
| sort | String | ⬚ | createdAt,desc | 排序欄位,方向 | `username,asc` |
| search | String | ⬚ | - | 關鍵字搜尋 | `john` |
| status | Enum | ⬚ | - | 狀態篩選 | `ACTIVE` |
| roleId | UUID | ⬚ | - | 角色篩選 | `"role-uuid"` |
| departmentId | UUID | ⬚ | - | 部門篩選 | `"dept-uuid"` |

**可排序欄位**

| 欄位 | 說明 |
|:---|:---|
| username | 帳號 |
| displayName | 顯示名稱 |
| status | 狀態 |
| lastLoginAt | 最後登入時間 |
| createdAt | 建立時間 |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| content | User[] | 使用者列表 |
| page | Integer | 當前頁碼 |
| size | Integer | 每頁筆數 |
| totalElements | Long | 總筆數 |
| totalPages | Integer | 總頁數 |

**User 物件結構**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| userId | UUID | 使用者ID |
| username | String | 登入帳號 |
| email | String | Email |
| displayName | String | 顯示名稱（從員工資料取得） |
| employeeId | UUID | 關聯員工ID |
| departmentName | String | 部門名稱 |
| status | Enum | 帳號狀態 |
| roles | Role[] | 角色列表 |
| lastLoginAt | DateTime | 最後登入時間 |
| createdAt | DateTime | 建立時間 |
| avatar | String | 頭像 URL |

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "userId": "550e8400-e29b-41d4-a716-446655440001",
        "username": "john.doe@company.com",
        "email": "john.doe@company.com",
        "displayName": "John Doe",
        "employeeId": "550e8400-e29b-41d4-a716-446655440002",
        "departmentName": "人力資源部",
        "status": "ACTIVE",
        "roles": [
          {
            "roleId": "00000000-0000-0000-0000-000000000002",
            "roleName": "HR_ADMIN",
            "displayName": "人資管理員"
          }
        ],
        "lastLoginAt": "2025-12-29T08:30:00Z",
        "createdAt": "2025-01-15T10:00:00Z",
        "avatar": "https://storage.example.com/avatars/john.jpg"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 156,
    "totalPages": 16
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_PAGE_INVALID | 頁碼必須大於0 | 頁碼從1開始 |
| 400 | VALIDATION_SIZE_INVALID | 每頁筆數必須在1~100之間 | 調整 size 參數 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 user:read 權限 | 聯繫管理員授權 |

---

### 2.2 查詢使用者詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/users/{id}` |
| Controller | `HR01UserQryController` |
| Service | `GetUserDetailServiceImpl` |
| 權限 | `user:read` |
| 版本 | v1 |

**用途說明**

查詢單一使用者的詳細資訊，包含角色權限、登入歷史等。

**業務邏輯**

1. **權限檢查**
   - 需具備 user:read 權限

2. **查詢使用者**
   - 根據 userId 查詢使用者
   - 同時載入角色、權限資訊

3. **查詢關聯資料**
   - 從 Organization 服務取得員工資訊

4. **多租戶隔離**
   - 只能查詢同租戶的使用者

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 使用者ID | `"550e8400-e29b-41d4-a716-446655440001"` |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| userId | UUID | 使用者ID |
| username | String | 登入帳號 |
| email | String | Email |
| displayName | String | 顯示名稱 |
| employeeId | UUID | 關聯員工ID |
| employeeNo | String | 員工編號 |
| departmentId | UUID | 部門ID |
| departmentName | String | 部門名稱 |
| status | Enum | 帳號狀態 |
| roles | Role[] | 角色列表（含權限） |
| lastLoginAt | DateTime | 最後登入時間 |
| lastLoginIp | String | 最後登入IP |
| passwordChangedAt | DateTime | 密碼變更時間 |
| failedLoginAttempts | Integer | 連續失敗次數 |
| lockedUntil | DateTime | 鎖定至（若被鎖定） |
| createdAt | DateTime | 建立時間 |
| createdBy | String | 建立者 |
| updatedAt | DateTime | 更新時間 |
| updatedBy | String | 更新者 |

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "username": "john.doe@company.com",
    "email": "john.doe@company.com",
    "displayName": "John Doe",
    "employeeId": "550e8400-e29b-41d4-a716-446655440002",
    "employeeNo": "EMP001",
    "departmentId": "550e8400-e29b-41d4-a716-446655440003",
    "departmentName": "人力資源部",
    "status": "ACTIVE",
    "roles": [
      {
        "roleId": "00000000-0000-0000-0000-000000000002",
        "roleName": "HR_ADMIN",
        "displayName": "人資管理員",
        "permissions": ["user:read", "user:write", "employee:profile:read"]
      }
    ],
    "lastLoginAt": "2025-12-29T08:30:00Z",
    "lastLoginIp": "192.168.1.100",
    "passwordChangedAt": "2025-06-15T10:00:00Z",
    "failedLoginAttempts": 0,
    "lockedUntil": null,
    "createdAt": "2025-01-15T10:00:00Z",
    "createdBy": "admin",
    "updatedAt": "2025-12-01T14:30:00Z",
    "updatedBy": "hr.manager"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 user:read 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_USER_NOT_FOUND | 使用者不存在 | 確認使用者ID |

---

### 2.3 建立使用者

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

1. **權限檢查**
   - 需具備 user:create 權限

2. **驗證請求資料**
   - username 必須為有效 Email 格式
   - username 在同一 tenant 內必須唯一
   - employeeId 必須存在於 Organization 服務
   - employeeId 尚未關聯其他使用者帳號
   - roleIds 至少指定一個角色，所有角色必須存在

3. **建立使用者**
   - 產生 UUID 作為 userId
   - 產生 8 位隨機臨時密碼（含大小寫+數字+特殊字元）
   - 密碼使用 BCrypt 加密後儲存
   - 設定 status = ACTIVE
   - 設定 passwordChangedAt = null（強制首次登入改密碼）

4. **建立角色關聯**
   - 在 user_roles 表建立使用者與角色的關聯

5. **發送通知**
   - 若 sendWelcomeEmail = true，發送歡迎郵件含臨時密碼

6. **發布事件**
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
| username | String | ✅ | Email格式，最長255字元，tenant內唯一 | 登入帳號 | `"jane.doe@company.com"` |
| email | String | ✅ | Email格式，最長255字元 | 使用者電子郵件 | `"jane.doe@company.com"` |
| employeeId | UUID | ✅ | 必須存在且未關聯其他帳號 | 關聯員工ID | `"emp-uuid-001"` |
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
| status | Enum | 帳號狀態 |
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
| 400 | VALIDATION_USERNAME_REQUIRED | 帳號不可為空 | 填入帳號 |
| 400 | VALIDATION_EMAIL_FORMAT | Email格式不正確 | 確認Email格式 |
| 400 | VALIDATION_ROLE_REQUIRED | 至少需指派一個角色 | 在roleIds中加入角色ID |
| 400 | VALIDATION_ROLE_LIMIT_EXCEEDED | 最多指派10個角色 | 減少角色數量 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 user:create 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認 employeeId |
| 404 | RESOURCE_ROLE_NOT_FOUND | 角色不存在 | 確認 roleIds |
| 409 | RESOURCE_USER_EXISTS | 使用者帳號已存在 | 使用其他帳號名稱 |
| 409 | RESOURCE_EMPLOYEE_HAS_USER | 該員工已有帳號 | 一個員工只能有一個帳號 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| UserCreatedEvent | `iam.user.created` | 使用者建立完成 |

---

### 2.4 更新使用者

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/users/{id}` |
| Controller | `HR01UserCmdController` |
| Service | `UpdateUserServiceImpl` |
| 權限 | `user:write` |
| 版本 | v1 |

**用途說明**

更新使用者的基本資訊，如 Email、關聯員工等。不包含密碼與角色的變更。

**業務邏輯**

1. **權限檢查**
   - 需具備 user:write 權限

2. **查詢使用者**
   - 確認使用者存在

3. **驗證更新資料**
   - email 必須為有效 Email 格式
   - employeeId 若有變更，必須存在且未關聯其他帳號

4. **更新使用者**
   - 更新允許修改的欄位
   - 更新 updatedAt、updatedBy

5. **發布事件**
   - 發布 UserUpdatedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 使用者ID | `"550e8400-..."` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| email | String | ⬚ | Email格式 | 電子郵件 | `"john.new@company.com"` |
| employeeId | UUID | ⬚ | 必須存在且未關聯其他帳號 | 關聯員工ID | `"emp-uuid-002"` |

**範例：**
```json
{
  "email": "john.new@company.com"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "使用者更新成功",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "username": "john.doe@company.com",
    "email": "john.new@company.com",
    "updatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_EMAIL_FORMAT | Email格式不正確 | 確認Email格式 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 user:write 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_USER_NOT_FOUND | 使用者不存在 | 確認使用者ID |
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認 employeeId |
| 409 | RESOURCE_EMPLOYEE_HAS_USER | 該員工已有帳號 | 一個員工只能有一個帳號 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| UserUpdatedEvent | `iam.user.updated` | 使用者資料更新 |

---

### 2.5 停用使用者

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/users/{id}/deactivate` |
| Controller | `HR01UserCmdController` |
| Service | `DeactivateUserServiceImpl` |
| 權限 | `user:deactivate` |
| 版本 | v1 |

**用途說明**

停用使用者帳號，使其無法登入系統。適用於員工離職或暫時停權的情境。

**業務邏輯**

1. **權限檢查**
   - 需具備 user:deactivate 權限

2. **驗證使用者**
   - 確認使用者存在
   - 不可停用自己的帳號
   - 不可停用系統管理員帳號（需額外確認）

3. **停用帳號**
   - 設定 status = INACTIVE
   - 更新 updatedAt、updatedBy

4. **撤銷 Token**
   - 刪除該使用者所有 Refresh Token
   - 清除 Redis 中的 Principal 快取

5. **發布事件**
   - 發布 UserDeactivatedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 使用者ID | `"550e8400-..."` |

**Request Body**

| 欄位 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| reason | String | ⬚ | 停用原因 | `"員工離職"` |

**範例：**
```json
{
  "reason": "員工離職"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "使用者已停用",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "status": "INACTIVE",
    "deactivatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | BUSINESS_CANNOT_DEACTIVATE_SELF | 不可停用自己的帳號 | 請其他管理員操作 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 user:deactivate 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_USER_NOT_FOUND | 使用者不存在 | 確認使用者ID |
| 409 | RESOURCE_USER_ALREADY_INACTIVE | 使用者已是停用狀態 | 無需重複操作 |
| 422 | BUSINESS_CANNOT_DEACTIVATE_ADMIN | 不可停用系統管理員 | 需先移除系統管理員角色 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| UserDeactivatedEvent | `iam.user.deactivated` | 使用者已停用 |

---

### 2.6 啟用使用者

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/users/{id}/activate` |
| Controller | `HR01UserCmdController` |
| Service | `ActivateUserServiceImpl` |
| 權限 | `user:deactivate` |
| 版本 | v1 |

**用途說明**

重新啟用已停用的使用者帳號，或解鎖被鎖定的帳號。

**業務邏輯**

1. **權限檢查**
   - 需具備 user:deactivate 權限（與停用共用）

2. **驗證使用者**
   - 確認使用者存在
   - 確認使用者目前為 INACTIVE 或 LOCKED 狀態

3. **啟用帳號**
   - 設定 status = ACTIVE
   - 重置 failedLoginAttempts = 0
   - 清除 lockedUntil
   - 更新 updatedAt、updatedBy

4. **發布事件**
   - 發布 UserActivatedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 使用者ID | `"550e8400-..."` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "使用者已啟用",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "status": "ACTIVE",
    "activatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無權限 | 聯繫管理員授權 |
| 404 | RESOURCE_USER_NOT_FOUND | 使用者不存在 | 確認使用者ID |
| 409 | RESOURCE_USER_ALREADY_ACTIVE | 使用者已是啟用狀態 | 無需重複操作 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| UserActivatedEvent | `iam.user.activated` | 使用者已啟用 |

---

### 2.7 管理員重置密碼

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/users/{id}/reset-password` |
| Controller | `HR01UserCmdController` |
| Service | `AdminResetPasswordServiceImpl` |
| 權限 | `user:reset-password` |
| 版本 | v1 |

**用途說明**

管理員為使用者重置密碼，產生新的臨時密碼並發送通知郵件。

**業務邏輯**

1. **權限檢查**
   - 需具備 user:reset-password 權限

2. **驗證使用者**
   - 確認使用者存在

3. **重置密碼**
   - 產生 8 位隨機臨時密碼
   - 使用 BCrypt 加密後儲存
   - 設定 passwordChangedAt = null（強制首次登入改密碼）

4. **安全處理**
   - 撤銷所有 Refresh Token
   - 解鎖帳號（若被鎖定）
   - 重置 failedLoginAttempts

5. **發送通知**
   - 發送密碼重置通知郵件，含臨時密碼

6. **發布事件**
   - 發布 PasswordResetEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 使用者ID | `"550e8400-..."` |

**Request Body**

| 欄位 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| sendEmail | Boolean | ⬚ | 是否發送郵件通知（預設true） | `true` |

**範例：**
```json
{
  "sendEmail": true
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "密碼已重置，通知郵件已發送",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "emailSent": true
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | BUSINESS_CANNOT_RESET_OWN_PASSWORD | 不可重置自己的密碼 | 使用修改密碼功能 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 user:reset-password 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_USER_NOT_FOUND | 使用者不存在 | 確認使用者ID |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| PasswordResetEvent | `iam.password.reset` | 管理員重置密碼 |

---

### 2.8 指派角色

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/users/{id}/roles` |
| Controller | `HR01UserCmdController` |
| Service | `AssignRolesServiceImpl` |
| 權限 | `user:assign-role` |
| 版本 | v1 |

**用途說明**

為使用者指派或更新角色，完全覆蓋原有的角色設定。

**業務邏輯**

1. **權限檢查**
   - 需具備 user:assign-role 權限
   - 不可指派比自己權限更高的角色（如非 SYSTEM_ADMIN 不可指派 SYSTEM_ADMIN）

2. **驗證請求**
   - 確認使用者存在
   - 確認所有角色存在
   - 至少指派一個角色

3. **更新角色**
   - 刪除原有的 user_roles 記錄
   - 建立新的 user_roles 記錄
   - 記錄 assigned_by 和 assigned_at

4. **清除快取**
   - 清除該使用者的權限快取

5. **發布事件**
   - 發布 UserRolesChangedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 使用者ID | `"550e8400-..."` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| roleIds | UUID[] | ✅ | 1~10個，所有ID必須存在 | 角色ID列表 | `["role-1", "role-2"]` |

**範例：**
```json
{
  "roleIds": [
    "00000000-0000-0000-0000-000000000002",
    "00000000-0000-0000-0000-000000000007"
  ]
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "角色指派成功",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "roles": [
      {
        "roleId": "00000000-0000-0000-0000-000000000002",
        "roleName": "HR_ADMIN",
        "displayName": "人資管理員"
      },
      {
        "roleId": "00000000-0000-0000-0000-000000000007",
        "roleName": "EMPLOYEE",
        "displayName": "一般員工"
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_ROLE_REQUIRED | 至少需指派一個角色 | 加入角色ID |
| 400 | VALIDATION_ROLE_LIMIT_EXCEEDED | 最多指派10個角色 | 減少角色數量 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 user:assign-role 權限 | 聯繫管理員授權 |
| 403 | AUTHZ_CANNOT_ASSIGN_HIGHER_ROLE | 不可指派更高權限的角色 | 聯繫系統管理員 |
| 404 | RESOURCE_USER_NOT_FOUND | 使用者不存在 | 確認使用者ID |
| 404 | RESOURCE_ROLE_NOT_FOUND | 角色不存在 | 確認角色ID |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| UserRolesChangedEvent | `iam.user.roles-changed` | 使用者角色變更 |

---

### 2.9 批次停用使用者

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/users/batch-deactivate` |
| Controller | `HR01UserCmdController` |
| Service | `BatchDeactivateUsersServiceImpl` |
| 權限 | `user:deactivate` |
| 版本 | v1 |

**用途說明**

批次停用多個使用者帳號，適用於組織調整或批次處理離職員工的情境。

**業務邏輯**

1. **權限檢查**
   - 需具備 user:deactivate 權限

2. **驗證請求**
   - userIds 不可為空，最多 100 個
   - 過濾掉不存在的使用者
   - 過濾掉操作者自己的帳號
   - 過濾掉系統管理員帳號

3. **批次處理**
   - 逐一停用使用者
   - 記錄成功與失敗的項目

4. **撤銷 Token**
   - 批次刪除 Refresh Token
   - 批次清除快取

5. **發布事件**
   - 為每個成功停用的使用者發布 UserDeactivatedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| userIds | UUID[] | ✅ | 1~100個 | 使用者ID列表 | `["user-1", "user-2"]` |
| reason | String | ⬚ | 最長500字元 | 停用原因 | `"批次離職處理"` |

**範例：**
```json
{
  "userIds": [
    "550e8400-e29b-41d4-a716-446655440001",
    "550e8400-e29b-41d4-a716-446655440002",
    "550e8400-e29b-41d4-a716-446655440003"
  ],
  "reason": "批次離職處理"
}
```

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| successCount | Integer | 成功停用數量 |
| failedCount | Integer | 失敗數量 |
| successIds | UUID[] | 成功停用的使用者ID |
| failures | Object[] | 失敗項目詳情 |
| failures[].userId | UUID | 使用者ID |
| failures[].reason | String | 失敗原因 |

```json
{
  "code": "SUCCESS",
  "message": "批次停用完成",
  "data": {
    "successCount": 2,
    "failedCount": 1,
    "successIds": [
      "550e8400-e29b-41d4-a716-446655440001",
      "550e8400-e29b-41d4-a716-446655440002"
    ],
    "failures": [
      {
        "userId": "550e8400-e29b-41d4-a716-446655440003",
        "reason": "不可停用系統管理員"
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_USER_IDS_REQUIRED | 使用者ID列表不可為空 | 提供至少一個使用者ID |
| 400 | VALIDATION_USER_IDS_LIMIT_EXCEEDED | 最多處理100個使用者 | 分批處理 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 user:deactivate 權限 | 聯繫管理員授權 |

---

## 3. 角色管理 API (Role)

### 3.1 查詢角色列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/roles` |
| Controller | `HR01RoleQryController` |
| Service | `GetRoleListServiceImpl` |
| 權限 | `role:read` |
| 版本 | v1 |

**用途說明**

查詢系統中所有角色列表，包含系統預設角色與自訂角色。

**業務邏輯**

1. **權限檢查**
   - 需具備 role:read 權限

2. **查詢角色**
   - 查詢系統角色（isSystemRole = true）
   - 查詢同租戶的自訂角色

3. **統計使用者數量**
   - 計算每個角色的使用者數量

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| search | String | ⬚ | - | 角色名稱搜尋 | `"admin"` |
| isSystemRole | Boolean | ⬚ | - | 篩選系統/自訂角色 | `true` |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| roles | Role[] | 角色列表 |

**Role 物件結構**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| roleId | UUID | 角色ID |
| roleName | String | 角色代碼 |
| displayName | String | 角色顯示名稱 |
| description | String | 角色描述 |
| isSystemRole | Boolean | 是否為系統角色 |
| userCount | Integer | 使用此角色的使用者數量 |
| createdAt | DateTime | 建立時間 |

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "roles": [
      {
        "roleId": "00000000-0000-0000-0000-000000000001",
        "roleName": "SYSTEM_ADMIN",
        "displayName": "系統管理員",
        "description": "最高權限，可管理所有功能",
        "isSystemRole": true,
        "userCount": 2,
        "createdAt": "2025-01-01T00:00:00Z"
      },
      {
        "roleId": "00000000-0000-0000-0000-000000000002",
        "roleName": "HR_ADMIN",
        "displayName": "人資管理員",
        "description": "人資全功能權限",
        "isSystemRole": true,
        "userCount": 5,
        "createdAt": "2025-01-01T00:00:00Z"
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 role:read 權限 | 聯繫管理員授權 |

---

### 3.2 查詢角色詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/roles/{id}` |
| Controller | `HR01RoleQryController` |
| Service | `GetRoleDetailServiceImpl` |
| 權限 | `role:read` |
| 版本 | v1 |

**用途說明**

查詢單一角色的詳細資訊，包含完整的權限列表。

**業務邏輯**

1. **權限檢查**
   - 需具備 role:read 權限

2. **查詢角色**
   - 載入角色基本資訊
   - 載入關聯的權限列表
   - 載入擁有此角色的使用者列表

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 角色ID | `"00000000-0000-0000-0000-000000000002"` |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| roleId | UUID | 角色ID |
| roleName | String | 角色代碼 |
| displayName | String | 角色顯示名稱 |
| description | String | 角色描述 |
| isSystemRole | Boolean | 是否為系統角色 |
| permissions | Permission[] | 權限列表 |
| users | User[] | 擁有此角色的使用者（簡要資訊） |
| createdAt | DateTime | 建立時間 |
| updatedAt | DateTime | 更新時間 |

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "roleId": "00000000-0000-0000-0000-000000000002",
    "roleName": "HR_ADMIN",
    "displayName": "人資管理員",
    "description": "人資全功能權限",
    "isSystemRole": true,
    "permissions": [
      {
        "permissionId": "perm-001",
        "permissionCode": "user:read",
        "resource": "user",
        "action": "read",
        "description": "查看使用者資料"
      },
      {
        "permissionId": "perm-002",
        "permissionCode": "user:write",
        "resource": "user",
        "action": "write",
        "description": "編輯使用者"
      }
    ],
    "users": [
      {
        "userId": "user-001",
        "username": "hr.manager@company.com",
        "displayName": "HR Manager"
      }
    ],
    "createdAt": "2025-01-01T00:00:00Z",
    "updatedAt": "2025-06-15T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 role:read 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_ROLE_NOT_FOUND | 角色不存在 | 確認角色ID |

---

### 3.3 建立角色

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/roles` |
| Controller | `HR01RoleCmdController` |
| Service | `CreateRoleServiceImpl` |
| 權限 | `role:create` |
| 版本 | v1 |

**用途說明**

建立自訂角色，可指定權限範圍。

**業務邏輯**

1. **權限檢查**
   - 需具備 role:create 權限

2. **驗證請求**
   - roleName 在同一 tenant 內必須唯一
   - roleName 符合命名規則（大寫字母、底線、數字）
   - permissionIds 皆必須存在

3. **建立角色**
   - 產生 UUID 作為 roleId
   - 設定 isSystemRole = false
   - 建立角色與權限的關聯

4. **發布事件**
   - 發布 RoleCreatedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| roleName | String | ✅ | 大寫+底線+數字，最長100字元，唯一 | 角色代碼 | `"CUSTOM_ROLE"` |
| displayName | String | ✅ | 最長255字元 | 角色顯示名稱 | `"自訂角色"` |
| description | String | ⬚ | 最長1000字元 | 角色描述 | `"這是自訂角色"` |
| permissionIds | UUID[] | ⬚ | 最多500個 | 權限ID列表 | `["perm-1", "perm-2"]` |

**範例：**
```json
{
  "roleName": "CUSTOM_REVIEWER",
  "displayName": "審核人員",
  "description": "負責審核各類申請",
  "permissionIds": [
    "perm-uuid-001",
    "perm-uuid-002"
  ]
}
```

**Response**

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "角色建立成功",
  "data": {
    "roleId": "550e8400-e29b-41d4-a716-446655440099",
    "roleName": "CUSTOM_REVIEWER",
    "displayName": "審核人員",
    "createdAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_ROLE_NAME_REQUIRED | 角色代碼不可為空 | 填入角色代碼 |
| 400 | VALIDATION_ROLE_NAME_FORMAT | 角色代碼格式錯誤 | 使用大寫+底線+數字 |
| 400 | VALIDATION_DISPLAY_NAME_REQUIRED | 顯示名稱不可為空 | 填入顯示名稱 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 role:create 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_PERMISSION_NOT_FOUND | 權限不存在 | 確認權限ID |
| 409 | RESOURCE_ROLE_EXISTS | 角色代碼已存在 | 使用其他代碼 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| RoleCreatedEvent | `iam.role.created` | 角色建立完成 |

---

### 3.4 更新角色

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/roles/{id}` |
| Controller | `HR01RoleCmdController` |
| Service | `UpdateRoleServiceImpl` |
| 權限 | `role:write` |
| 版本 | v1 |

**用途說明**

更新角色的基本資訊（顯示名稱、描述）。系統角色不可修改。

**業務邏輯**

1. **權限檢查**
   - 需具備 role:write 權限

2. **驗證角色**
   - 確認角色存在
   - 確認非系統角色（isSystemRole = false）

3. **更新角色**
   - 更新 displayName、description
   - 更新 updatedAt、updatedBy

4. **發布事件**
   - 發布 RoleUpdatedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 角色ID | `"550e8400-..."` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| displayName | String | ⬚ | 最長255字元 | 角色顯示名稱 | `"新名稱"` |
| description | String | ⬚ | 最長1000字元 | 角色描述 | `"新描述"` |

**範例：**
```json
{
  "displayName": "進階審核人員",
  "description": "負責審核重要申請"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "角色更新成功",
  "data": {
    "roleId": "550e8400-e29b-41d4-a716-446655440099",
    "roleName": "CUSTOM_REVIEWER",
    "displayName": "進階審核人員",
    "updatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 role:write 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_ROLE_NOT_FOUND | 角色不存在 | 確認角色ID |
| 422 | BUSINESS_CANNOT_MODIFY_SYSTEM_ROLE | 系統角色不可修改 | 只能修改自訂角色 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| RoleUpdatedEvent | `iam.role.updated` | 角色資料更新 |

---

### 3.5 刪除角色

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `DELETE /api/v1/roles/{id}` |
| Controller | `HR01RoleCmdController` |
| Service | `DeleteRoleServiceImpl` |
| 權限 | `role:delete` |
| 版本 | v1 |

**用途說明**

刪除自訂角色。系統角色與仍有使用者使用的角色不可刪除。

**業務邏輯**

1. **權限檢查**
   - 需具備 role:delete 權限

2. **驗證角色**
   - 確認角色存在
   - 確認非系統角色
   - 確認無使用者使用此角色

3. **刪除角色**
   - 刪除 role_permissions 關聯
   - 刪除角色記錄

4. **發布事件**
   - 發布 RoleDeletedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 角色ID | `"550e8400-..."` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "角色刪除成功"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 role:delete 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_ROLE_NOT_FOUND | 角色不存在 | 確認角色ID |
| 422 | BUSINESS_CANNOT_DELETE_SYSTEM_ROLE | 系統角色不可刪除 | 只能刪除自訂角色 |
| 422 | BUSINESS_ROLE_IN_USE | 角色仍有使用者使用 | 先移除使用者的此角色 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| RoleDeletedEvent | `iam.role.deleted` | 角色已刪除 |

---

### 3.6 更新角色權限

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/roles/{id}/permissions` |
| Controller | `HR01RoleCmdController` |
| Service | `UpdateRolePermissionsServiceImpl` |
| 權限 | `role:manage-permission` |
| 版本 | v1 |

**用途說明**

更新角色的權限配置，完全覆蓋原有的權限設定。

**業務邏輯**

1. **權限檢查**
   - 需具備 role:manage-permission 權限

2. **驗證請求**
   - 確認角色存在
   - 確認所有權限存在
   - 系統角色也可修改權限

3. **更新權限**
   - 刪除原有的 role_permissions 記錄
   - 建立新的 role_permissions 記錄

4. **清除快取**
   - 清除所有擁有此角色的使用者的權限快取

5. **發布事件**
   - 發布 RolePermissionChangedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 角色ID | `"550e8400-..."` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| permissionIds | UUID[] | ✅ | 最多500個，所有ID必須存在 | 權限ID列表 | `["perm-1", "perm-2"]` |

**範例：**
```json
{
  "permissionIds": [
    "perm-uuid-001",
    "perm-uuid-002",
    "perm-uuid-003"
  ]
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "角色權限更新成功",
  "data": {
    "roleId": "550e8400-e29b-41d4-a716-446655440099",
    "permissionCount": 3,
    "affectedUserCount": 15
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_PERMISSION_IDS_REQUIRED | 權限ID列表不可為空 | 至少提供一個權限 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 role:manage-permission 權限 | 聯繫管理員授權 |
| 404 | RESOURCE_ROLE_NOT_FOUND | 角色不存在 | 確認角色ID |
| 404 | RESOURCE_PERMISSION_NOT_FOUND | 權限不存在 | 確認權限ID |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| RolePermissionChangedEvent | `iam.role.permission-changed` | 角色權限變更 |

---

## 4. 權限管理 API (Permission)

### 4.1 查詢權限列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/permissions` |
| Controller | `HR01PermissionQryController` |
| Service | `GetPermissionListServiceImpl` |
| 權限 | `permission:read` |
| 版本 | v1 |

**用途說明**

查詢系統中所有的權限列表，用於角色權限配置時的選項來源。

**業務邏輯**

1. **權限檢查**
   - 需具備 permission:read 權限

2. **查詢權限**
   - 查詢所有權限
   - 可依資源分類篩選

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| resource | String | ⬚ | - | 資源名稱篩選 | `"user"` |
| search | String | ⬚ | - | 關鍵字搜尋 | `"read"` |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| permissions | Permission[] | 權限列表 |

**Permission 物件結構**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| permissionId | UUID | 權限ID |
| permissionCode | String | 權限代碼 |
| resource | String | 資源名稱 |
| action | String | 操作名稱 |
| description | String | 權限描述 |

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "permissions": [
      {
        "permissionId": "perm-001",
        "permissionCode": "user:read",
        "resource": "user",
        "action": "read",
        "description": "查看使用者資料"
      },
      {
        "permissionId": "perm-002",
        "permissionCode": "user:create",
        "resource": "user",
        "action": "create",
        "description": "建立使用者"
      },
      {
        "permissionId": "perm-003",
        "permissionCode": "user:write",
        "resource": "user",
        "action": "write",
        "description": "編輯使用者"
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 permission:read 權限 | 聯繫管理員授權 |

---

### 4.2 查詢權限樹

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/permissions/tree` |
| Controller | `HR01PermissionQryController` |
| Service | `GetPermissionTreeServiceImpl` |
| 權限 | `permission:read` |
| 版本 | v1 |

**用途說明**

以樹狀結構回傳權限列表，方便在角色權限設定頁面以樹狀核取方塊顯示。

**業務邏輯**

1. **權限檢查**
   - 需具備 permission:read 權限

2. **組織樹狀結構**
   - 第一層：資源（如 user, employee, attendance）
   - 第二層：操作（如 read, create, write, delete）

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| tree | PermissionGroup[] | 權限樹狀結構 |

**PermissionGroup 物件結構**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| resource | String | 資源名稱 |
| displayName | String | 資源顯示名稱 |
| permissions | Permission[] | 該資源下的權限列表 |

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "tree": [
      {
        "resource": "user",
        "displayName": "使用者管理",
        "permissions": [
          {
            "permissionId": "perm-001",
            "permissionCode": "user:read",
            "action": "read",
            "description": "查看使用者資料"
          },
          {
            "permissionId": "perm-002",
            "permissionCode": "user:create",
            "action": "create",
            "description": "建立使用者"
          },
          {
            "permissionId": "perm-003",
            "permissionCode": "user:write",
            "action": "write",
            "description": "編輯使用者"
          },
          {
            "permissionId": "perm-004",
            "permissionCode": "user:delete",
            "action": "delete",
            "description": "刪除使用者"
          }
        ]
      },
      {
        "resource": "employee",
        "displayName": "員工管理",
        "permissions": [
          {
            "permissionId": "perm-010",
            "permissionCode": "employee:profile:read",
            "action": "profile:read",
            "description": "查看員工資料"
          },
          {
            "permissionId": "perm-011",
            "permissionCode": "employee:profile:write",
            "action": "profile:write",
            "description": "編輯員工資料"
          }
        ]
      },
      {
        "resource": "attendance",
        "displayName": "考勤管理",
        "permissions": [
          {
            "permissionId": "perm-020",
            "permissionCode": "attendance:leave:read",
            "action": "leave:read",
            "description": "查看請假記錄"
          },
          {
            "permissionId": "perm-021",
            "permissionCode": "attendance:leave:approve",
            "action": "leave:approve",
            "description": "審核請假"
          }
        ]
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 permission:read 權限 | 聯繫管理員授權 |

---

## 5. 個人資料 API (Profile)

### 5.1 查詢個人資料

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/profile` |
| Controller | `HR01ProfileQryController` |
| Service | `GetProfileServiceImpl` |
| 權限 | 需登入（查詢自己的資料） |
| 版本 | v1 |

**用途說明**

查詢當前登入使用者的個人資料與帳號資訊。

**業務邏輯**

1. **取得當前使用者**
   - 從 Token 取得 userId

2. **查詢使用者資訊**
   - 載入使用者基本資料
   - 載入角色與權限

3. **查詢員工資訊**
   - 從 Organization 服務取得關聯員工資料

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| userId | UUID | 使用者ID |
| username | String | 登入帳號 |
| email | String | Email |
| displayName | String | 顯示名稱 |
| employeeId | UUID | 關聯員工ID |
| employeeNo | String | 員工編號 |
| departmentName | String | 部門名稱 |
| jobTitle | String | 職稱 |
| roles | Role[] | 角色列表 |
| permissions | String[] | 權限代碼列表 |
| avatar | String | 頭像 URL |
| lastLoginAt | DateTime | 最後登入時間 |
| passwordChangedAt | DateTime | 密碼變更時間 |
| mustChangePassword | Boolean | 是否需要變更密碼 |

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "username": "john.doe@company.com",
    "email": "john.doe@company.com",
    "displayName": "John Doe",
    "employeeId": "550e8400-e29b-41d4-a716-446655440002",
    "employeeNo": "EMP001",
    "departmentName": "人力資源部",
    "jobTitle": "人資經理",
    "roles": [
      {
        "roleId": "00000000-0000-0000-0000-000000000002",
        "roleName": "HR_ADMIN",
        "displayName": "人資管理員"
      }
    ],
    "permissions": ["user:read", "user:write", "employee:profile:read"],
    "avatar": "https://storage.example.com/avatars/john.jpg",
    "lastLoginAt": "2025-12-29T08:30:00Z",
    "passwordChangedAt": "2025-06-15T10:00:00Z",
    "mustChangePassword": false
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |

---

### 5.2 更新個人資料

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/profile` |
| Controller | `HR01ProfileCmdController` |
| Service | `UpdateProfileServiceImpl` |
| 權限 | 需登入（更新自己的資料） |
| 版本 | v1 |

**用途說明**

更新當前登入使用者的個人資料，僅限可自行修改的欄位（如 Email、頭像）。

**業務邏輯**

1. **取得當前使用者**
   - 從 Token 取得 userId

2. **驗證請求**
   - email 格式驗證
   - avatar URL 格式驗證

3. **更新資料**
   - 更新允許自行修改的欄位
   - 更新 updatedAt

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| email | String | ⬚ | Email格式 | Email | `"john.new@company.com"` |
| avatar | String | ⬚ | URL格式 | 頭像URL | `"https://..."` |

**範例：**
```json
{
  "email": "john.new@company.com",
  "avatar": "https://storage.example.com/avatars/new-avatar.jpg"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "個人資料更新成功",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "email": "john.new@company.com",
    "avatar": "https://storage.example.com/avatars/new-avatar.jpg",
    "updatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_EMAIL_FORMAT | Email格式不正確 | 確認Email格式 |
| 400 | VALIDATION_AVATAR_URL_FORMAT | 頭像URL格式不正確 | 確認URL格式 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |

---

### 5.3 修改密碼

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/profile/change-password` |
| Controller | `HR01ProfileCmdController` |
| Service | `ChangePasswordServiceImpl` |
| 權限 | 需登入 |
| 版本 | v1 |

**用途說明**

使用者自行修改密碼，需提供現有密碼驗證身份。

**業務邏輯**

1. **取得當前使用者**
   - 從 Token 取得 userId

2. **驗證現有密碼**
   - 使用 BCrypt 比對現有密碼

3. **驗證新密碼**
   - 長度 8~128 字元
   - 包含大小寫字母和數字
   - 不可與現有密碼相同
   - 兩次輸入一致

4. **更新密碼**
   - 使用 BCrypt 加密新密碼
   - 更新 passwordChangedAt

5. **安全處理**
   - 撤銷其他裝置的 Refresh Token（可選）

6. **發送通知**
   - 發送密碼變更通知郵件

7. **發布事件**
   - 發布 PasswordChangedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| currentPassword | String | ✅ | - | 現有密碼 | `"OldPassword123!"` |
| newPassword | String | ✅ | 8~128字元，含大小寫+數字 | 新密碼 | `"NewPassword123!"` |
| confirmPassword | String | ✅ | 必須與 newPassword 相同 | 確認新密碼 | `"NewPassword123!"` |
| logoutOtherDevices | Boolean | ⬚ | - | 是否登出其他裝置（預設false） | `true` |

**範例：**
```json
{
  "currentPassword": "OldPassword123!",
  "newPassword": "NewPassword123!",
  "confirmPassword": "NewPassword123!",
  "logoutOtherDevices": true
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "密碼修改成功",
  "data": {
    "passwordChangedAt": "2025-12-29T10:30:00Z",
    "otherDevicesLoggedOut": true
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_CURRENT_PASSWORD_REQUIRED | 現有密碼不可為空 | 填入現有密碼 |
| 400 | VALIDATION_NEW_PASSWORD_REQUIRED | 新密碼不可為空 | 填入新密碼 |
| 400 | VALIDATION_PASSWORD_WEAK | 密碼強度不足 | 密碼需含大小寫+數字，至少8字元 |
| 400 | VALIDATION_PASSWORD_MISMATCH | 兩次密碼不一致 | 確認密碼輸入相同 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 401 | AUTH_CURRENT_PASSWORD_WRONG | 現有密碼錯誤 | 確認現有密碼 |
| 422 | BUSINESS_PASSWORD_SAME_AS_OLD | 新密碼不可與現有密碼相同 | 使用不同的密碼 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| PasswordChangedEvent | `iam.password.changed` | 密碼變更完成 |

---

## 附錄：API 端點總表

| # | 端點 | 方法 | 說明 | 權限 |
|:---:|:---|:---:|:---|:---|
| 1 | `/api/v1/auth/login` | POST | 使用者登入 | - |
| 2 | `/api/v1/auth/logout` | POST | 使用者登出 | 需登入 |
| 3 | `/api/v1/auth/refresh-token` | POST | 刷新Token | - |
| 4 | `/api/v1/auth/forgot-password` | POST | 忘記密碼 | - |
| 5 | `/api/v1/auth/reset-password` | POST | 重置密碼 | - |
| 6 | `/api/v1/auth/oauth/google` | GET | Google OAuth登入 | - |
| 7 | `/api/v1/auth/oauth/google/callback` | GET | Google OAuth回調 | - |
| 8 | `/api/v1/auth/oauth/microsoft` | GET | Microsoft OAuth登入 | - |
| 9 | `/api/v1/auth/oauth/microsoft/callback` | GET | Microsoft OAuth回調 | - |
| 10 | `/api/v1/users` | GET | 查詢使用者列表 | user:read |
| 11 | `/api/v1/users/{id}` | GET | 查詢使用者詳情 | user:read |
| 12 | `/api/v1/users` | POST | 建立使用者 | user:create |
| 13 | `/api/v1/users/{id}` | PUT | 更新使用者 | user:write |
| 14 | `/api/v1/users/{id}/deactivate` | PUT | 停用使用者 | user:deactivate |
| 15 | `/api/v1/users/{id}/activate` | PUT | 啟用使用者 | user:deactivate |
| 16 | `/api/v1/users/{id}/reset-password` | PUT | 管理員重置密碼 | user:reset-password |
| 17 | `/api/v1/users/{id}/roles` | PUT | 指派角色 | user:assign-role |
| 18 | `/api/v1/users/batch-deactivate` | PUT | 批次停用使用者 | user:deactivate |
| 19 | `/api/v1/roles` | GET | 查詢角色列表 | role:read |
| 20 | `/api/v1/roles/{id}` | GET | 查詢角色詳情 | role:read |
| 21 | `/api/v1/roles` | POST | 建立角色 | role:create |
| 22 | `/api/v1/roles/{id}` | PUT | 更新角色 | role:write |
| 23 | `/api/v1/roles/{id}` | DELETE | 刪除角色 | role:delete |
| 24 | `/api/v1/roles/{id}/permissions` | PUT | 更新角色權限 | role:manage-permission |
| 25 | `/api/v1/permissions` | GET | 查詢權限列表 | permission:read |
| 26 | `/api/v1/permissions/tree` | GET | 查詢權限樹 | permission:read |
| 27 | `/api/v1/profile` | GET | 查詢個人資料 | 需登入 |
| 28 | `/api/v1/profile` | PUT | 更新個人資料 | 需登入 |
| 29 | `/api/v1/profile/change-password` | PUT | 修改密碼 | 需登入 |

---

**文件建立日期:** 2025-12-29
**版本:** 1.0
