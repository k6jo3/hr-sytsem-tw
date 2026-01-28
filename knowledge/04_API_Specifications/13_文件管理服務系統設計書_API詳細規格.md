# 文件管理服務 API 詳細規格

**版本:** 1.0
**日期:** 2025-12-30
**Domain代號:** 13 (DOC)
**對應系統設計書:** `knowledge/03_System_Architecture/Detailed_Design/13_文件管理服務系統設計書.md`

---

## 目錄

1. [Controller 命名對照](#1-controller-命名對照)
2. [API 總覽](#2-api-總覽-17個端點)
3. [文件管理 API](#3-文件管理-api)
4. [範本管理 API](#4-範本管理-api)
5. [文件申請 API](#5-文件申請-api)
6. [稽核查詢 API](#6-稽核查詢-api)
7. [錯誤碼定義](#7-錯誤碼定義)
8. [領域事件](#8-領域事件)

---

## 1. Controller 命名對照

| Controller | 負責功能 | 類型 |
|:---|:---|:---:|
| `HR13DocumentCmdController` | 文件上傳、刪除、產生 | Command |
| `HR13DocumentQryController` | 文件查詢、下載、版本歷史 | Query |
| `HR13TemplateCmdController` | 範本建立、更新、刪除 | Command |
| `HR13TemplateQryController` | 範本列表、詳情查詢 | Query |
| `HR13RequestCmdController` | 文件申請 | Command |
| `HR13RequestQryController` | 申請歷史、申請類型查詢 | Query |

---

## 2. API 總覽 (17個端點)

### 2.1 文件管理 API (7個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 1 | `/api/v1/documents/upload` | POST | 上傳文件 | HR13DocumentCmdController | `document:upload` |
| 2 | `/api/v1/documents/{id}/download` | GET | 下載文件 | HR13DocumentQryController | `document:download` |
| 3 | `/api/v1/documents` | GET | 文件列表（管理端） | HR13DocumentQryController | `document:read` |
| 4 | `/api/v1/documents/{id}` | GET | 文件詳情 | HR13DocumentQryController | `document:read` |
| 5 | `/api/v1/documents/{id}` | DELETE | 刪除文件 | HR13DocumentCmdController | `document:delete` |
| 6 | `/api/v1/documents/{id}/versions` | GET | 文件版本歷史 | HR13DocumentQryController | `document:read` |
| 7 | `/api/v1/documents/my` | GET | 我的文件（ESS） | HR13DocumentQryController | - |

### 2.2 文件產生 API (1個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 8 | `/api/v1/documents/generate` | POST | 從範本產生文件 | HR13DocumentCmdController | `document:generate` |

### 2.3 範本管理 API (5個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 9 | `/api/v1/documents/templates` | POST | 建立文件範本 | HR13TemplateCmdController | `template:create` |
| 10 | `/api/v1/documents/templates` | GET | 範本列表 | HR13TemplateQryController | `template:read` |
| 11 | `/api/v1/documents/templates/{id}` | GET | 範本詳情 | HR13TemplateQryController | `template:read` |
| 12 | `/api/v1/documents/templates/{id}` | PUT | 更新範本 | HR13TemplateCmdController | `template:update` |
| 13 | `/api/v1/documents/templates/{id}` | DELETE | 刪除範本 | HR13TemplateCmdController | `template:delete` |

### 2.4 文件申請 API (3個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 14 | `/api/v1/documents/request-types` | GET | 可申請文件類型 | HR13RequestQryController | - |
| 15 | `/api/v1/documents/request` | POST | 申請文件 | HR13RequestCmdController | - |
| 16 | `/api/v1/documents/requests` | GET | 申請歷史 | HR13RequestQryController | - |

### 2.5 稽核查詢 API (1個)

| # | 端點 | 方法 | 說明 | Controller | 權限 |
|:---:|:---|:---:|:---|:---|:---|
| 17 | `/api/v1/documents/download-logs` | GET | 下載記錄查詢 | HR13DocumentQryController | `document:audit` |

---

## 3. 文件管理 API

### 3.1 上傳文件

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/documents/upload` |
| Controller | `HR13DocumentCmdController` |
| Service | `UploadDocumentServiceImpl` |
| 權限 | `document:upload` |
| 版本 | v1 |

**用途說明**

HR 人員或員工上傳各類文件（合約、履歷、證照等）至系統。上傳前會進行檔案驗證（大小、格式）及病毒掃描，確保系統安全。

**業務邏輯**

1. **檔案驗證**
   - 檢查檔案大小：單檔不超過 10MB，圖片不超過 2MB
   - 檢查檔案格式：禁止上傳 .exe, .sh, .bat 等可執行檔
   - 支援格式：PDF, DOC, DOCX, JPG, PNG, XLS, XLSX

2. **病毒掃描**
   - 使用 ClamAV 進行病毒掃描
   - 發現病毒則拒絕上傳並記錄 Log

3. **儲存處理**
   - 產生 UUID 作為系統檔名
   - 依 documentType 儲存至對應資料夾
   - 若為 PAYSLIP 類型，使用 AES-256 加密

4. **建立 Metadata**
   - 記錄原始檔名、大小、MIME 類型、上傳者、上傳時間
   - 設定文件可見性（PRIVATE/DEPARTMENT/PUBLIC）

5. **發布事件**
   - 發布 `DocumentUploadedEvent`

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `multipart/form-data` |

**Request Body (multipart/form-data)**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| file | Binary | ✅ | 最大 10MB，禁止可執行檔 | 檔案內容 | - |
| documentType | Enum | ✅ | 必須為有效類型 | 文件類型 | `"EMPLOYEE_CONTRACT"` |
| businessType | String | ✅ | 最長 50 字元 | 業務類型 | `"employee"` |
| businessId | UUID | ✅ | - | 業務關聯 ID | `"550e8400-..."` |
| visibility | Enum | ⬚ | PRIVATE/DEPARTMENT/PUBLIC | 可見性，預設 PRIVATE | `"PRIVATE"` |
| description | String | ⬚ | 最長 500 字元 | 文件說明 | `"2025年員工合約"` |

**documentType 列舉值**

| 值 | 說明 | 是否加密 |
|:---|:---|:---:|
| `EMPLOYEE_CONTRACT` | 員工合約 | ❌ |
| `EMPLOYEE_RESUME` | 履歷 | ❌ |
| `EMPLOYEE_PHOTO` | 員工照片 | ❌ |
| `CERTIFICATE` | 證照 | ❌ |
| `PAYSLIP` | 薪資單 | ✅ AES-256 |
| `GENERATED_DOCUMENT` | 系統產生文件 | ❌ |

**Response**

**成功回應 (201 Created)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| documentId | UUID | 文件唯一識別碼 |
| fileName | String | 系統檔名 |
| originalFileName | String | 原始檔名 |
| fileSize | Long | 檔案大小（bytes） |
| mimeType | String | MIME 類型 |
| storagePath | String | 儲存路徑 |
| isEncrypted | Boolean | 是否已加密 |
| uploadedAt | DateTime | 上傳時間 |

```json
{
  "code": "SUCCESS",
  "message": "文件上傳成功",
  "data": {
    "documentId": "doc-uuid-001",
    "fileName": "doc-uuid-001.pdf",
    "originalFileName": "員工合約_張三.pdf",
    "fileSize": 204800,
    "mimeType": "application/pdf",
    "storagePath": "/storage/contracts/doc-uuid-001.pdf",
    "isEncrypted": false,
    "uploadedAt": "2025-12-30T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_FILE_TOO_LARGE | 檔案大小超過限制 | 檔案不得超過 10MB |
| 400 | VALIDATION_FILE_TYPE_NOT_ALLOWED | 不允許的檔案類型 | 不支援可執行檔 |
| 400 | VALIDATION_INVALID_DOCUMENT_TYPE | 無效的文件類型 | 使用有效的 documentType |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無上傳權限 | 聯繫管理員授權 |
| 422 | BUSINESS_VIRUS_DETECTED | 檔案包含病毒 | 請使用安全的檔案 |
| 500 | SYSTEM_STORAGE_ERROR | 儲存失敗 | 聯繫系統管理員 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| DocumentUploadedEvent | `document.uploaded` | 文件上傳完成 |

---

### 3.2 下載文件

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents/{id}/download` |
| Controller | `HR13DocumentQryController` |
| Service | `DownloadDocumentServiceImpl` |
| 權限 | `document:download` 或文件擁有者 |
| 版本 | v1 |

**用途說明**

下載指定文件。系統會進行權限檢查，確保使用者有權存取該文件。若為加密文件（如薪資單），需提供密碼驗證。

**業務邏輯**

1. **權限檢查**
   - 檢查使用者是否為文件擁有者（ownerId）
   - 或具有 `document:read:all` 權限（HR）
   - 或文件 visibility 為 PUBLIC/DEPARTMENT 且使用者符合條件

2. **加密文件處理**
   - 若為 PAYSLIP 類型，檢查 password 參數
   - 密碼為員工身分證後 4 碼
   - 使用 AES-256 解密後返回

3. **記錄下載 Log**
   - 記錄下載者、下載時間、IP 位址
   - 用於稽核追蹤

4. **返回檔案串流**
   - 設定 Content-Disposition 為 attachment
   - 設定正確的 Content-Type

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 文件 ID | `doc-uuid-001` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| password | String | ⬚ | 加密文件密碼（薪資單必填） | `6789` |

**Response**

**成功回應 (200 OK)**

```
Content-Type: application/pdf
Content-Disposition: attachment; filename="員工合約_張三.pdf"

(Binary file stream)
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_PASSWORD_REQUIRED | 加密文件需提供密碼 | 請提供密碼參數 |
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_ACCESS_DENIED | 無權存取此文件 | 聯繫文件擁有者或 HR |
| 403 | BUSINESS_WRONG_PASSWORD | 密碼錯誤 | 確認密碼是否正確 |
| 404 | RESOURCE_DOCUMENT_NOT_FOUND | 文件不存在 | 確認文件 ID |
| 500 | SYSTEM_STORAGE_ERROR | 讀取檔案失敗 | 聯繫系統管理員 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| DocumentDownloadedEvent | `document.downloaded` | 文件下載完成（稽核用） |

---

### 3.3 文件列表（管理端）

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents` |
| Controller | `HR13DocumentQryController` |
| Service | `GetDocumentListServiceImpl` |
| 權限 | `document:read` |
| 版本 | v1 |

**用途說明**

HR 管理人員查詢文件列表，支援依文件類型、業務類型、擁有者等條件篩選，並提供分頁功能。

**業務邏輯**

1. **查詢條件處理**
   - 依 documentType、businessType、ownerId 篩選
   - 支援關鍵字搜尋（檔名、說明）
   - 支援日期區間篩選

2. **排序與分頁**
   - 預設依上傳時間降序排列
   - 支援自訂排序欄位

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `20` |
| sort | String | ⬚ | uploadedAt,desc | 排序 | `fileName,asc` |
| documentType | Enum | ⬚ | - | 文件類型 | `EMPLOYEE_CONTRACT` |
| businessType | String | ⬚ | - | 業務類型 | `employee` |
| ownerId | UUID | ⬚ | - | 擁有者 ID | `emp-uuid-001` |
| search | String | ⬚ | - | 關鍵字搜尋 | `合約` |
| startDate | Date | ⬚ | - | 上傳開始日期 | `2025-01-01` |
| endDate | Date | ⬚ | - | 上傳結束日期 | `2025-12-31` |

**Response**

**成功回應 (200 OK)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| content | Document[] | 文件列表 |
| page | Integer | 當前頁碼 |
| size | Integer | 每頁筆數 |
| totalElements | Long | 總筆數 |
| totalPages | Integer | 總頁數 |

**Document 物件結構**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| documentId | UUID | 文件 ID |
| documentType | Enum | 文件類型 |
| originalFileName | String | 原始檔名 |
| fileSize | Long | 檔案大小 |
| mimeType | String | MIME 類型 |
| visibility | Enum | 可見性 |
| isEncrypted | Boolean | 是否加密 |
| ownerName | String | 擁有者姓名 |
| uploadedByName | String | 上傳者姓名 |
| uploadedAt | DateTime | 上傳時間 |

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "documentId": "doc-uuid-001",
        "documentType": "EMPLOYEE_CONTRACT",
        "originalFileName": "員工合約_張三.pdf",
        "fileSize": 204800,
        "mimeType": "application/pdf",
        "visibility": "PRIVATE",
        "isEncrypted": false,
        "ownerName": "張三",
        "uploadedByName": "李四（HR）",
        "uploadedAt": "2025-12-30T10:00:00Z"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 156,
    "totalPages": 16
  }
}
```

---

### 3.4 文件詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents/{id}` |
| Controller | `HR13DocumentQryController` |
| Service | `GetDocumentDetailServiceImpl` |
| 權限 | `document:read` 或文件擁有者 |
| 版本 | v1 |

**用途說明**

查詢單一文件的詳細資訊，包含 metadata、版本資訊、關聯業務資料等。

**業務邏輯**

1. **權限檢查**
   - 確認使用者有權檢視此文件

2. **組裝詳情資料**
   - 基本 metadata
   - 當前版本資訊
   - 業務關聯資訊

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 文件 ID | `doc-uuid-001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "documentId": "doc-uuid-001",
    "documentType": "EMPLOYEE_CONTRACT",
    "documentTypeName": "員工合約",
    "businessType": "employee",
    "businessId": "emp-uuid-001",
    "fileName": "doc-uuid-001.pdf",
    "originalFileName": "員工合約_張三.pdf",
    "fileSize": 204800,
    "fileSizeDisplay": "200 KB",
    "mimeType": "application/pdf",
    "storagePath": "/storage/contracts/doc-uuid-001.pdf",
    "isEncrypted": false,
    "visibility": "PRIVATE",
    "visibilityName": "僅本人與HR可見",
    "description": "2025年員工合約",
    "version": 1,
    "owner": {
      "employeeId": "emp-uuid-001",
      "employeeName": "張三"
    },
    "uploadedBy": {
      "employeeId": "emp-uuid-002",
      "employeeName": "李四"
    },
    "uploadedAt": "2025-12-30T10:00:00Z",
    "downloadCount": 3
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 403 | AUTHZ_ACCESS_DENIED | 無權存取此文件 | 聯繫文件擁有者或 HR |
| 404 | RESOURCE_DOCUMENT_NOT_FOUND | 文件不存在 | 確認文件 ID |

---

### 3.5 刪除文件

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `DELETE /api/v1/documents/{id}` |
| Controller | `HR13DocumentCmdController` |
| Service | `DeleteDocumentServiceImpl` |
| 權限 | `document:delete` |
| 版本 | v1 |

**用途說明**

刪除指定文件。刪除前會檢查文件是否可被刪除（如薪資單通常不可刪除）。

**業務邏輯**

1. **刪除限制檢查**
   - PAYSLIP 類型文件不可刪除（需保留稽核）
   - 需為文件擁有者或具有 `document:delete:all` 權限

2. **刪除處理**
   - 從資料庫標記刪除（軟刪除）
   - 實體檔案依保留政策處理

3. **發布事件**
   - 發布 `DocumentDeletedEvent`

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 文件 ID | `doc-uuid-001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "文件刪除成功",
  "data": {
    "documentId": "doc-uuid-001",
    "deletedAt": "2025-12-30T15:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 403 | AUTHZ_PERMISSION_DENIED | 無刪除權限 | 聯繫管理員 |
| 404 | RESOURCE_DOCUMENT_NOT_FOUND | 文件不存在 | 確認文件 ID |
| 422 | BUSINESS_CANNOT_DELETE_PAYSLIP | 薪資單不可刪除 | 薪資單需保留稽核 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| DocumentDeletedEvent | `document.deleted` | 文件已刪除 |

---

### 3.6 文件版本歷史

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents/{id}/versions` |
| Controller | `HR13DocumentQryController` |
| Service | `GetDocumentVersionsServiceImpl` |
| 權限 | `document:read` 或文件擁有者 |
| 版本 | v1 |

**用途說明**

查詢文件的版本歷史記錄，用於追蹤文件變更。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 文件 ID | `doc-uuid-001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "documentId": "doc-uuid-001",
    "currentVersion": 3,
    "versions": [
      {
        "version": 3,
        "fileName": "員工合約_張三_v3.pdf",
        "fileSize": 215000,
        "uploadedBy": "李四",
        "uploadedAt": "2025-12-30T10:00:00Z",
        "changeNote": "更新薪資條款",
        "isCurrent": true
      },
      {
        "version": 2,
        "fileName": "員工合約_張三_v2.pdf",
        "fileSize": 210000,
        "uploadedBy": "李四",
        "uploadedAt": "2025-06-15T14:30:00Z",
        "changeNote": "更新職稱",
        "isCurrent": false
      },
      {
        "version": 1,
        "fileName": "員工合約_張三.pdf",
        "fileSize": 204800,
        "uploadedBy": "王五",
        "uploadedAt": "2025-01-10T09:00:00Z",
        "changeNote": "初始版本",
        "isCurrent": false
      }
    ]
  }
}
```

---

### 3.7 我的文件（ESS）

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents/my` |
| Controller | `HR13DocumentQryController` |
| Service | `GetMyDocumentsServiceImpl` |
| 權限 | - （登入員工皆可） |
| 版本 | v1 |

**用途說明**

員工在 ESS（Employee Self Service）查詢自己的文件，包含合約、證照、薪資單等。

**業務邏輯**

1. **查詢範圍**
   - 自動以當前登入使用者的 employeeId 為 ownerId 篩選
   - 僅返回使用者本人的文件

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `20` |
| documentType | Enum | ⬚ | - | 文件類型篩選 | `PAYSLIP` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "documentId": "doc-uuid-001",
        "documentType": "EMPLOYEE_CONTRACT",
        "documentTypeName": "員工合約",
        "originalFileName": "員工合約_張三.pdf",
        "fileSize": 204800,
        "fileSizeDisplay": "200 KB",
        "isEncrypted": false,
        "uploadedAt": "2025-01-10T09:00:00Z"
      },
      {
        "documentId": "doc-uuid-002",
        "documentType": "PAYSLIP",
        "documentTypeName": "薪資單",
        "originalFileName": "202512_薪資單.pdf",
        "fileSize": 102400,
        "fileSizeDisplay": "100 KB",
        "isEncrypted": true,
        "uploadedAt": "2025-12-05T10:00:00Z"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 24,
    "totalPages": 3
  }
}
```

---

## 4. 範本管理 API

### 4.1 從範本產生文件

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/documents/generate` |
| Controller | `HR13DocumentCmdController` |
| Service | `GenerateDocumentServiceImpl` |
| 權限 | `document:generate` 或文件申請者 |
| 版本 | v1 |

**用途說明**

使用預設範本產生文件（如在職證明、薪資證明等），系統會自動填入員工資料並轉換為 PDF。

**業務邏輯**

1. **載入範本**
   - 依 templateCode 查詢對應範本
   - 確認範本為啟用狀態

2. **查詢變數資料**
   - 向 Organization Service 查詢員工資料
   - 向 Payroll Service 查詢薪資資料（如需要）

3. **替換變數**
   - 將範本中的 `{{variableName}}` 替換為實際值
   - 處理日期格式、金額格式等

4. **產生 PDF**
   - 使用 Apache POI 處理 Word 範本
   - 轉換為 PDF 格式

5. **儲存文件**
   - 儲存至 Document 資料表
   - documentType = GENERATED_DOCUMENT

6. **發布事件**
   - 發布 `DocumentGeneratedEvent`
   - 觸發通知服務通知申請者

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| templateCode | String | ✅ | 必須存在且啟用 | 範本代碼 | `"EMPLOYMENT_CERTIFICATE"` |
| employeeId | UUID | ✅ | 必須存在 | 目標員工 ID | `"emp-uuid-001"` |
| purpose | String | ⬚ | 最長 200 字元 | 用途說明 | `"申請房屋貸款"` |
| variables | Object | ⬚ | - | 額外變數 | `{"customField": "value"}` |

**範例：**
```json
{
  "templateCode": "EMPLOYMENT_CERTIFICATE",
  "employeeId": "550e8400-e29b-41d4-a716-446655440001",
  "purpose": "申請房屋貸款",
  "variables": {}
}
```

**Response**

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "文件產生成功",
  "data": {
    "documentId": "doc-uuid-gen-001",
    "fileName": "在職證明_張三_20251230.pdf",
    "downloadUrl": "/api/v1/documents/doc-uuid-gen-001/download",
    "generatedAt": "2025-12-30T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_TEMPLATE_NOT_FOUND | 範本不存在 | 確認 templateCode |
| 400 | VALIDATION_TEMPLATE_DISABLED | 範本已停用 | 聯繫管理員 |
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認 employeeId |
| 422 | BUSINESS_EMPLOYEE_NOT_ACTIVE | 員工非在職狀態 | 離職員工無法產生在職證明 |
| 500 | SYSTEM_PDF_GENERATION_ERROR | PDF 產生失敗 | 聯繫系統管理員 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| DocumentGeneratedEvent | `document.generated` | 文件產生完成，通知申請者 |

---

### 4.2 建立文件範本

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/documents/templates` |
| Controller | `HR13TemplateCmdController` |
| Service | `CreateTemplateServiceImpl` |
| 權限 | `template:create` |
| 版本 | v1 |

**用途說明**

HR 管理員建立新的文件範本，範本可包含變數標記，供系統自動填入員工資料。

**業務邏輯**

1. **驗證範本**
   - templateCode 必須唯一
   - 上傳的範本檔案必須為 Word 格式（.docx）
   - 驗證範本中的變數標記格式

2. **解析變數**
   - 掃描範本內容，提取所有 `{{variableName}}` 標記
   - 建立變數清單供後續產生文件時使用

3. **儲存範本**
   - 儲存範本檔案至指定目錄
   - 建立範本 metadata

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `multipart/form-data` |

**Request Body (multipart/form-data)**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| templateFile | Binary | ✅ | .docx 格式，最大 5MB | 範本檔案 | - |
| templateCode | String | ✅ | 唯一，2~50 字元，英數底線 | 範本代碼 | `"EMPLOYMENT_CERT"` |
| templateName | String | ✅ | 1~100 字元 | 範本名稱 | `"在職證明書範本"` |
| templateType | Enum | ✅ | 有效的範本類型 | 範本類型 | `"EMPLOYMENT_CERT"` |
| description | String | ⬚ | 最長 500 字元 | 說明 | `"用於產生員工在職證明"` |

**templateType 列舉值**

| 值 | 說明 |
|:---|:---|
| `EMPLOYMENT_CERT` | 在職證明 |
| `SALARY_CERT` | 薪資證明 |
| `SEPARATION_CERT` | 離職證明 |
| `TAX_WITHHOLDING` | 扣繳憑單 |

**Response**

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "範本建立成功",
  "data": {
    "templateId": "tpl-uuid-001",
    "templateCode": "EMPLOYMENT_CERT",
    "templateName": "在職證明書範本",
    "templateType": "EMPLOYMENT_CERT",
    "variables": [
      {
        "variableName": "employeeName",
        "description": "員工姓名"
      },
      {
        "variableName": "nationalId",
        "description": "身分證字號（遮蔽）"
      },
      {
        "variableName": "hireDate",
        "description": "到職日期"
      },
      {
        "variableName": "jobTitle",
        "description": "職稱"
      },
      {
        "variableName": "department",
        "description": "部門"
      },
      {
        "variableName": "purpose",
        "description": "用途"
      },
      {
        "variableName": "issueDate",
        "description": "開立日期"
      }
    ],
    "isActive": true,
    "createdAt": "2025-12-30T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_FILE_TYPE_INVALID | 檔案格式錯誤 | 請使用 .docx 格式 |
| 400 | VALIDATION_TEMPLATE_CODE_FORMAT | 範本代碼格式錯誤 | 使用英數底線，2~50字元 |
| 409 | RESOURCE_TEMPLATE_CODE_EXISTS | 範本代碼已存在 | 使用其他代碼 |

---

### 4.3 範本列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents/templates` |
| Controller | `HR13TemplateQryController` |
| Service | `GetTemplateListServiceImpl` |
| 權限 | `template:read` |
| 版本 | v1 |

**用途說明**

查詢文件範本列表，用於管理介面或選擇產生文件時的範本。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `20` |
| templateType | Enum | ⬚ | - | 範本類型篩選 | `EMPLOYMENT_CERT` |
| isActive | Boolean | ⬚ | - | 是否啟用 | `true` |
| search | String | ⬚ | - | 關鍵字搜尋 | `在職` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "templateId": "tpl-uuid-001",
        "templateCode": "EMPLOYMENT_CERT",
        "templateName": "在職證明書範本",
        "templateType": "EMPLOYMENT_CERT",
        "templateTypeName": "在職證明",
        "variableCount": 7,
        "isActive": true,
        "createdAt": "2025-01-15T10:00:00Z",
        "updatedAt": "2025-06-20T14:30:00Z"
      },
      {
        "templateId": "tpl-uuid-002",
        "templateCode": "SALARY_CERT",
        "templateName": "薪資證明書範本",
        "templateType": "SALARY_CERT",
        "templateTypeName": "薪資證明",
        "variableCount": 10,
        "isActive": true,
        "createdAt": "2025-01-16T10:00:00Z",
        "updatedAt": null
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 4,
    "totalPages": 1
  }
}
```

---

### 4.4 範本詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents/templates/{id}` |
| Controller | `HR13TemplateQryController` |
| Service | `GetTemplateDetailServiceImpl` |
| 權限 | `template:read` |
| 版本 | v1 |

**用途說明**

查詢單一範本的詳細資訊，包含完整變數清單及說明。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 範本 ID | `tpl-uuid-001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "templateId": "tpl-uuid-001",
    "templateCode": "EMPLOYMENT_CERT",
    "templateName": "在職證明書範本",
    "templateType": "EMPLOYMENT_CERT",
    "templateTypeName": "在職證明",
    "description": "用於產生員工在職證明",
    "templateFilePath": "/templates/EMPLOYMENT_CERT.docx",
    "variables": [
      {
        "variableName": "employeeName",
        "description": "員工姓名",
        "source": "Organization Service",
        "example": "張三"
      },
      {
        "variableName": "nationalId",
        "description": "身分證字號（遮蔽）",
        "source": "Organization Service",
        "example": "A123****89"
      },
      {
        "variableName": "hireDate",
        "description": "到職日期",
        "source": "Organization Service",
        "example": "2023-05-01"
      },
      {
        "variableName": "jobTitle",
        "description": "職稱",
        "source": "Organization Service",
        "example": "資深工程師"
      },
      {
        "variableName": "department",
        "description": "部門",
        "source": "Organization Service",
        "example": "研發部"
      },
      {
        "variableName": "purpose",
        "description": "用途",
        "source": "使用者輸入",
        "example": "申請房屋貸款"
      },
      {
        "variableName": "issueDate",
        "description": "開立日期",
        "source": "系統產生",
        "example": "2025-12-30"
      }
    ],
    "isActive": true,
    "createdBy": "系統管理員",
    "createdAt": "2025-01-15T10:00:00Z",
    "updatedBy": "系統管理員",
    "updatedAt": "2025-06-20T14:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_TEMPLATE_NOT_FOUND | 範本不存在 | 確認範本 ID |

---

### 4.5 更新範本

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/documents/templates/{id}` |
| Controller | `HR13TemplateCmdController` |
| Service | `UpdateTemplateServiceImpl` |
| 權限 | `template:update` |
| 版本 | v1 |

**用途說明**

更新現有文件範本，可更新範本名稱、說明、啟用狀態，或上傳新版本範本檔案。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `multipart/form-data` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 範本 ID | `tpl-uuid-001` |

**Request Body (multipart/form-data)**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| templateFile | Binary | ⬚ | .docx 格式 | 新版範本檔案 | - |
| templateName | String | ⬚ | 1~100 字元 | 範本名稱 | `"在職證明書範本 v2"` |
| description | String | ⬚ | 最長 500 字元 | 說明 | `"更新版"` |
| isActive | Boolean | ⬚ | - | 是否啟用 | `true` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "範本更新成功",
  "data": {
    "templateId": "tpl-uuid-001",
    "templateCode": "EMPLOYMENT_CERT",
    "templateName": "在職證明書範本 v2",
    "isActive": true,
    "updatedAt": "2025-12-30T15:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_FILE_TYPE_INVALID | 檔案格式錯誤 | 請使用 .docx 格式 |
| 404 | RESOURCE_TEMPLATE_NOT_FOUND | 範本不存在 | 確認範本 ID |

---

### 4.6 刪除範本

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `DELETE /api/v1/documents/templates/{id}` |
| Controller | `HR13TemplateCmdController` |
| Service | `DeleteTemplateServiceImpl` |
| 權限 | `template:delete` |
| 版本 | v1 |

**用途說明**

刪除文件範本。若該範本曾被使用產生文件，則無法刪除，只能停用。

**業務邏輯**

1. **使用檢查**
   - 檢查是否有文件使用此範本產生
   - 若有則拒絕刪除，建議改為停用

2. **刪除處理**
   - 從資料庫刪除範本 metadata
   - 刪除範本檔案

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 範本 ID | `tpl-uuid-001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "範本刪除成功",
  "data": {
    "templateId": "tpl-uuid-001",
    "deletedAt": "2025-12-30T16:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_TEMPLATE_NOT_FOUND | 範本不存在 | 確認範本 ID |
| 422 | BUSINESS_TEMPLATE_IN_USE | 範本已被使用 | 請改為停用範本 |

---

## 5. 文件申請 API

### 5.1 可申請文件類型

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents/request-types` |
| Controller | `HR13RequestQryController` |
| Service | `GetRequestTypesServiceImpl` |
| 權限 | - （登入員工皆可） |
| 版本 | v1 |

**用途說明**

查詢員工可申請的文件類型清單，顯示於 ESS 文件申請頁面。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "requestTypes": [
      {
        "templateCode": "EMPLOYMENT_CERT",
        "name": "在職證明",
        "description": "證明您目前在職狀態",
        "requireApproval": false,
        "autoGenerate": true,
        "icon": "certificate"
      },
      {
        "templateCode": "SALARY_CERT",
        "name": "薪資證明",
        "description": "證明您的薪資資料",
        "requireApproval": false,
        "autoGenerate": true,
        "icon": "money"
      },
      {
        "templateCode": "SEPARATION_CERT",
        "name": "離職證明",
        "description": "證明您的離職資料",
        "requireApproval": true,
        "autoGenerate": false,
        "icon": "document"
      },
      {
        "templateCode": "TAX_WITHHOLDING",
        "name": "扣繳憑單",
        "description": "年度扣繳憑單",
        "requireApproval": false,
        "autoGenerate": true,
        "icon": "tax"
      }
    ]
  }
}
```

---

### 5.2 申請文件

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/documents/request` |
| Controller | `HR13RequestCmdController` |
| Service | `RequestDocumentServiceImpl` |
| 權限 | - （登入員工皆可） |
| 版本 | v1 |

**用途說明**

員工申請產生文件（如在職證明、薪資證明等）。若文件類型需審核，則建立申請單並啟動簽核流程；若不需審核，則直接產生文件。

**業務邏輯**

1. **驗證申請資格**
   - 離職證明：僅離職員工可申請
   - 在職證明：僅在職員工可申請
   - 薪資證明：僅在職員工可申請

2. **判斷流程**
   - 若 requireApproval = false，直接產生文件
   - 若 requireApproval = true，建立申請單並啟動簽核

3. **產生文件（不需審核時）**
   - 呼叫文件產生服務
   - 儲存至 Document

4. **發布事件**
   - 發布 `DocumentRequestedEvent`

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| templateCode | String | ✅ | 必須為可申請類型 | 範本代碼 | `"EMPLOYMENT_CERT"` |
| purpose | String | ⬚ | 最長 200 字元 | 用途說明 | `"申請房屋貸款"` |
| additionalInfo | Object | ⬚ | - | 額外資訊 | `{}` |

**範例：**
```json
{
  "templateCode": "EMPLOYMENT_CERT",
  "purpose": "申請房屋貸款"
}
```

**Response**

**成功回應 (201 Created) - 直接產生**

```json
{
  "code": "SUCCESS",
  "message": "文件產生成功",
  "data": {
    "requestId": "req-uuid-001",
    "status": "COMPLETED",
    "documentId": "doc-uuid-gen-001",
    "downloadUrl": "/api/v1/documents/doc-uuid-gen-001/download",
    "generatedAt": "2025-12-30T10:00:00Z"
  }
}
```

**成功回應 (201 Created) - 需審核**

```json
{
  "code": "SUCCESS",
  "message": "申請已提交，待 HR 審核",
  "data": {
    "requestId": "req-uuid-002",
    "status": "PENDING_APPROVAL",
    "documentId": null,
    "downloadUrl": null,
    "submittedAt": "2025-12-30T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_INVALID_TEMPLATE | 無效的範本代碼 | 使用可申請的範本類型 |
| 422 | BUSINESS_NOT_ELIGIBLE_EMPLOYMENT | 非在職員工不可申請在職證明 | - |
| 422 | BUSINESS_NOT_ELIGIBLE_SEPARATION | 非離職員工不可申請離職證明 | - |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| DocumentRequestedEvent | `document.requested` | 文件申請已提交 |

---

### 5.3 申請歷史

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents/requests` |
| Controller | `HR13RequestQryController` |
| Service | `GetRequestHistoryServiceImpl` |
| 權限 | - （登入員工皆可） |
| 版本 | v1 |

**用途說明**

查詢員工的文件申請歷史記錄，包含申請狀態、審核結果、產生的文件等。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `20` |
| status | Enum | ⬚ | - | 狀態篩選 | `COMPLETED` |
| startDate | Date | ⬚ | - | 開始日期 | `2025-01-01` |
| endDate | Date | ⬚ | - | 結束日期 | `2025-12-31` |

**status 列舉值**

| 值 | 說明 |
|:---|:---|
| `PENDING_APPROVAL` | 待審核 |
| `APPROVED` | 已核准 |
| `REJECTED` | 已駁回 |
| `COMPLETED` | 已完成（文件已產生） |
| `CANCELLED` | 已取消 |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "requestId": "req-uuid-001",
        "templateCode": "EMPLOYMENT_CERT",
        "documentTypeName": "在職證明",
        "purpose": "申請房屋貸款",
        "status": "COMPLETED",
        "statusName": "已完成",
        "documentId": "doc-uuid-gen-001",
        "downloadUrl": "/api/v1/documents/doc-uuid-gen-001/download",
        "submittedAt": "2025-12-01T10:00:00Z",
        "completedAt": "2025-12-01T10:00:30Z"
      },
      {
        "requestId": "req-uuid-002",
        "templateCode": "SEPARATION_CERT",
        "documentTypeName": "離職證明",
        "purpose": "求職使用",
        "status": "PENDING_APPROVAL",
        "statusName": "待審核",
        "documentId": null,
        "downloadUrl": null,
        "submittedAt": "2025-12-30T09:00:00Z",
        "completedAt": null
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 5,
    "totalPages": 1
  }
}
```

---

## 6. 稽核查詢 API

### 6.1 下載記錄查詢

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/documents/download-logs` |
| Controller | `HR13DocumentQryController` |
| Service | `GetDownloadLogsServiceImpl` |
| 權限 | `document:audit` |
| 版本 | v1 |

**用途說明**

HR 或稽核人員查詢文件下載記錄，用於追蹤敏感文件的存取情況。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `50` |
| documentId | UUID | ⬚ | - | 指定文件 ID | `doc-uuid-001` |
| documentType | Enum | ⬚ | - | 文件類型 | `PAYSLIP` |
| downloadedBy | UUID | ⬚ | - | 下載者 ID | `emp-uuid-001` |
| startDate | DateTime | ⬚ | - | 開始時間 | `2025-12-01T00:00:00Z` |
| endDate | DateTime | ⬚ | - | 結束時間 | `2025-12-31T23:59:59Z` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "logId": "log-uuid-001",
        "documentId": "doc-uuid-001",
        "documentName": "202512_薪資單.pdf",
        "documentType": "PAYSLIP",
        "downloadedBy": {
          "employeeId": "emp-uuid-001",
          "employeeName": "張三"
        },
        "downloadedAt": "2025-12-30T10:15:00Z",
        "ipAddress": "192.168.1.100"
      },
      {
        "logId": "log-uuid-002",
        "documentId": "doc-uuid-002",
        "documentName": "員工合約_張三.pdf",
        "documentType": "EMPLOYEE_CONTRACT",
        "downloadedBy": {
          "employeeId": "emp-uuid-002",
          "employeeName": "李四（HR）"
        },
        "downloadedAt": "2025-12-30T11:20:00Z",
        "ipAddress": "192.168.1.50"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 256,
    "totalPages": 26
  }
}
```

---

## 7. 錯誤碼定義

### 7.1 驗證錯誤 (4xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | VALIDATION_FILE_TOO_LARGE | 檔案大小超過限制 |
| 400 | VALIDATION_FILE_TYPE_NOT_ALLOWED | 不允許的檔案類型 |
| 400 | VALIDATION_FILE_TYPE_INVALID | 檔案格式錯誤 |
| 400 | VALIDATION_INVALID_DOCUMENT_TYPE | 無效的文件類型 |
| 400 | VALIDATION_INVALID_TEMPLATE | 無效的範本代碼 |
| 400 | VALIDATION_TEMPLATE_CODE_FORMAT | 範本代碼格式錯誤 |
| 400 | VALIDATION_PASSWORD_REQUIRED | 加密文件需提供密碼 |

### 7.2 授權錯誤 (4xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 |
| 403 | AUTHZ_PERMISSION_DENIED | 無操作權限 |
| 403 | AUTHZ_ACCESS_DENIED | 無權存取此文件 |
| 403 | BUSINESS_WRONG_PASSWORD | 密碼錯誤 |

### 7.3 資源錯誤 (4xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 404 | RESOURCE_DOCUMENT_NOT_FOUND | 文件不存在 |
| 404 | RESOURCE_TEMPLATE_NOT_FOUND | 範本不存在 |
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 |
| 409 | RESOURCE_TEMPLATE_CODE_EXISTS | 範本代碼已存在 |

### 7.4 業務錯誤 (4xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 422 | BUSINESS_VIRUS_DETECTED | 檔案包含病毒 |
| 422 | BUSINESS_CANNOT_DELETE_PAYSLIP | 薪資單不可刪除 |
| 422 | BUSINESS_TEMPLATE_IN_USE | 範本已被使用 |
| 422 | BUSINESS_TEMPLATE_DISABLED | 範本已停用 |
| 422 | BUSINESS_EMPLOYEE_NOT_ACTIVE | 員工非在職狀態 |
| 422 | BUSINESS_NOT_ELIGIBLE_EMPLOYMENT | 非在職員工不可申請在職證明 |
| 422 | BUSINESS_NOT_ELIGIBLE_SEPARATION | 非離職員工不可申請離職證明 |

### 7.5 系統錯誤 (5xx)

| HTTP | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 500 | SYSTEM_STORAGE_ERROR | 儲存/讀取檔案失敗 |
| 500 | SYSTEM_PDF_GENERATION_ERROR | PDF 產生失敗 |
| 500 | SYSTEM_VIRUS_SCAN_ERROR | 病毒掃描服務異常 |

---

## 8. 領域事件

### 8.1 事件總覽

| 事件名稱 | Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| DocumentUploadedEvent | `document.uploaded` | 文件上傳完成 | Document 內部 |
| DocumentDownloadedEvent | `document.downloaded` | 文件下載完成 | Document 內部（稽核） |
| DocumentGeneratedEvent | `document.generated` | 文件產生完成 | Notification |
| DocumentDeletedEvent | `document.deleted` | 文件刪除 | Document 內部 |
| DocumentRequestedEvent | `document.requested` | 文件申請提交 | Workflow、Notification |

### 8.2 DocumentUploadedEvent

```json
{
  "eventId": "evt-doc-001",
  "eventType": "DocumentUploadedEvent",
  "occurredAt": "2025-12-30T10:00:00Z",
  "aggregateId": "doc-uuid-001",
  "aggregateType": "Document",
  "payload": {
    "documentId": "doc-uuid-001",
    "documentType": "EMPLOYEE_CONTRACT",
    "ownerId": "emp-uuid-001",
    "fileName": "員工合約_張三.pdf",
    "fileSize": 204800,
    "uploadedBy": "emp-uuid-002",
    "isEncrypted": false
  }
}
```

### 8.3 DocumentGeneratedEvent

```json
{
  "eventId": "evt-doc-002",
  "eventType": "DocumentGeneratedEvent",
  "occurredAt": "2025-12-30T10:00:30Z",
  "aggregateId": "doc-uuid-gen-001",
  "aggregateType": "Document",
  "payload": {
    "documentId": "doc-uuid-gen-001",
    "templateCode": "EMPLOYMENT_CERT",
    "employeeId": "emp-uuid-001",
    "employeeName": "張三",
    "fileName": "在職證明_張三_20251230.pdf",
    "downloadUrl": "/api/v1/documents/doc-uuid-gen-001/download"
  }
}
```

### 8.4 DocumentDownloadedEvent

```json
{
  "eventId": "evt-doc-003",
  "eventType": "DocumentDownloadedEvent",
  "occurredAt": "2025-12-30T10:15:00Z",
  "aggregateId": "doc-uuid-001",
  "aggregateType": "Document",
  "payload": {
    "documentId": "doc-uuid-001",
    "downloadedBy": "emp-uuid-001",
    "ipAddress": "192.168.1.100",
    "documentType": "PAYSLIP"
  }
}
```

---

## 附錄 A：文件類型與權限對照

| 文件類型 | 擁有者可見 | HR 可見 | 財務可見 | 全公司可見 | 可刪除 |
|:---|:---:|:---:|:---:|:---:|:---:|
| EMPLOYEE_CONTRACT | ✅ | ✅ | ❌ | ❌ | HR |
| EMPLOYEE_RESUME | ❌ | ✅ | ❌ | ❌ | HR |
| EMPLOYEE_PHOTO | ✅ | ✅ | ❌ | ✅ | 擁有者+HR |
| CERTIFICATE | ✅ | ✅ | ❌ | ❌ | 擁有者+HR |
| PAYSLIP | ✅ | ✅ | ✅ | ❌ | ❌ |
| GENERATED_DOCUMENT | ✅ | ✅ | ❌ | ❌ | HR |

---

## 附錄 B：範本變數清單

### B.1 在職證明 (EMPLOYMENT_CERT)

| 變數名 | 說明 | 資料來源 |
|:---|:---|:---|
| `{{employeeName}}` | 員工姓名 | Organization Service |
| `{{nationalId}}` | 身分證字號（遮蔽） | Organization Service |
| `{{hireDate}}` | 到職日期 | Organization Service |
| `{{jobTitle}}` | 職稱 | Organization Service |
| `{{department}}` | 部門 | Organization Service |
| `{{companyName}}` | 公司名稱 | 系統設定 |
| `{{purpose}}` | 用途 | 使用者輸入 |
| `{{issueDate}}` | 開立日期 | 系統產生 |

### B.2 薪資證明 (SALARY_CERT)

| 變數名 | 說明 | 資料來源 |
|:---|:---|:---|
| `{{employeeName}}` | 員工姓名 | Organization Service |
| `{{nationalId}}` | 身分證字號（遮蔽） | Organization Service |
| `{{hireDate}}` | 到職日期 | Organization Service |
| `{{jobTitle}}` | 職稱 | Organization Service |
| `{{department}}` | 部門 | Organization Service |
| `{{baseSalary}}` | 底薪 | Payroll Service |
| `{{totalSalary}}` | 總薪資（含津貼） | Payroll Service |
| `{{salaryPeriod}}` | 薪資期間 | 使用者輸入/系統 |
| `{{companyName}}` | 公司名稱 | 系統設定 |
| `{{issueDate}}` | 開立日期 | 系統產生 |

---

**文件建立日期:** 2025-12-30
**版本:** 1.0
