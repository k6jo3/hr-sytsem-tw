# HR13 文件管理服務業務合約

> **服務代碼:** HR13
> **服務名稱:** 文件管理服務 (Document Management)
> **版本:** 1.0
> **更新日期:** 2026-02-24

---

## 概述

文件管理服務負責文件上傳、版本控制、資料夾管理、文件範本、存取紀錄等功能。
支援完整的文件生命週期：建立 → 上傳版本 → 共享存取 → 版本管理 → 軟刪除。

---

## API 端點概覽

### 文件管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/documents` | GET | DOC_D001~D010 | 查詢文件列表 | ✅ 已實作 |
| 2 | `GET /api/v1/documents/{id}` | GET | - | 查詢文件詳情 | ✅ 已實作 |

### 資料夾管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/documents/folders` | GET | DOC_F001~F004 | 查詢資料夾列表 | ✅ 已實作 |

### 文件版本管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/documents/{id}/versions` | GET | DOC_V001~V004 | 查詢版本列表 | ✅ 已實作 |

### 文件範本管理 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/document-templates` | GET | DOC_T001~T005 | 查詢範本列表 | ✅ 已實作 |

### 文件存取紀錄 API

| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/documents/access-logs` | GET | DOC_L001~L005 | 查詢存取紀錄 | ✅ 已實作 |

**總計：24 個查詢場景**

---

## 1. Query 操作業務合約

### 1.1 文件查詢合約 (Document Query Contract)

**說明：** 驗證 `DocumentListQueryAssembler` 正確組裝文件查詢條件。
所有場景均須包含 `isDeleted = false` 軟刪除過濾條件。

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| DOC_D001 | 查詢資料夾內文件 | EMPLOYEE | GET /api/v1/documents | `folderId = F001`, `visibility IN [PUBLIC, SHARED, DEPARTMENT]`, `isDeleted = false` |
| DOC_D002 | 依名稱模糊查詢 | EMPLOYEE | GET /api/v1/documents | `fileName LIKE 報告`, `visibility IN [PUBLIC, SHARED, DEPARTMENT]`, `isDeleted = false` |
| DOC_D003 | 依類型查詢 | EMPLOYEE | GET /api/v1/documents | `documentType = PDF`, `visibility IN [PUBLIC, SHARED, DEPARTMENT]`, `isDeleted = false` |
| DOC_D004 | 查詢個人文件 | EMPLOYEE | GET /api/v1/documents | `ownerId = currentUserId`, `isDeleted = false` |
| DOC_D005 | 查詢共享文件 | EMPLOYEE | GET /api/v1/documents | `visibility = SHARED`, `isDeleted = false` |
| DOC_D006 | 查詢公開文件 | EMPLOYEE | GET /api/v1/documents | `visibility = PUBLIC`, `isDeleted = false` |
| DOC_D007 | 依標籤查詢 | EMPLOYEE | GET /api/v1/documents | `tags LIKE 合約`, `visibility IN [PUBLIC, SHARED, DEPARTMENT]`, `isDeleted = false` |
| DOC_D008 | 查詢最近文件 | EMPLOYEE | GET /api/v1/documents | `ownerId = currentUserId`, `updatedAt >= 2025-01-01`, `visibility IN [PUBLIC, SHARED, DEPARTMENT]`, `isDeleted = false` |
| DOC_D009 | HR 查詢全部文件 | HR | GET /api/v1/documents | `isDeleted = false` |
| DOC_D010 | 查詢機密文件 | HR | GET /api/v1/documents | `classification = CONFIDENTIAL`, `isDeleted = false` |

### 1.2 資料夾查詢合約 (Folder Query Contract)

**說明：** 驗證 `DocumentListQueryAssembler` 正確處理資料夾查詢。
`NULL_MARKER` 用於查詢根資料夾（parentId IS NULL）。

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| DOC_F001 | 查詢根資料夾 | EMPLOYEE | GET /api/v1/documents/folders | `folderId IS NULL`, `visibility IN [PUBLIC, SHARED, DEPARTMENT]`, `isDeleted = false` |
| DOC_F002 | 查詢子資料夾 | EMPLOYEE | GET /api/v1/documents/folders | `folderId = F001`, `isDeleted = false` |
| DOC_F003 | 查詢個人資料夾 | EMPLOYEE | GET /api/v1/documents/folders | `ownerId = currentUserId`, `isDeleted = false` |
| DOC_F004 | 依名稱查詢資料夾 | EMPLOYEE | GET /api/v1/documents/folders | `fileName LIKE 專案`, `visibility IN [PUBLIC, SHARED, DEPARTMENT]`, `isDeleted = false` |

### 1.3 文件版本查詢合約 (Document Version Query Contract)

**說明：** 驗證 `DocumentVersionListQueryAssembler` 正確組裝版本查詢條件。
版本記錄為歷史資料，不使用軟刪除過濾。

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| DOC_V001 | 查詢文件所有版本 | EMPLOYEE | GET /api/v1/documents/{id}/versions | `document_id = D001` |
| DOC_V002 | 查詢最新版本 | EMPLOYEE | GET /api/v1/documents/{id}/versions | `document_id = D001`, `is_latest = true` |
| DOC_V003 | 依版本號查詢 | EMPLOYEE | GET /api/v1/documents/{id}/versions | `document_id = D001`, `version = 2.0` |
| DOC_V004 | 依上傳者查詢 | MANAGER | GET /api/v1/documents/{id}/versions | `uploader_id = E001` |

### 1.4 文件範本查詢合約 (Document Template Query Contract)

**說明：** 驗證 `DocumentTemplateListQueryAssembler` 正確組裝範本查詢條件。
所有場景均須包含 `is_deleted = 0` 軟刪除過濾條件。

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| DOC_T001 | 查詢啟用範本 | EMPLOYEE | GET /api/v1/document-templates | `status = ACTIVE`, `is_deleted = 0` |
| DOC_T002 | 依類型查詢範本 | EMPLOYEE | GET /api/v1/document-templates | `category = CONTRACT`, `status = ACTIVE`, `is_deleted = 0` |
| DOC_T003 | 依名稱模糊查詢範本 | EMPLOYEE | GET /api/v1/document-templates | `name LIKE 勞動`, `status = ACTIVE`, `is_deleted = 0` |
| DOC_T004 | 查詢部門範本 | MANAGER | GET /api/v1/document-templates | `department_id = D001`, `status = ACTIVE`, `is_deleted = 0` |
| DOC_T005 | HR 查詢全部範本 | HR | GET /api/v1/document-templates | `is_deleted = 0` |

### 1.5 文件存取紀錄查詢合約 (Document Access Log Query Contract)

**說明：** 驗證 `DocumentAccessLogListQueryAssembler` 正確組裝存取紀錄查詢條件。
存取紀錄為稽核資料，不使用軟刪除過濾。

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| DOC_L001 | 查詢文件存取紀錄 | MANAGER | GET /api/v1/documents/access-logs | `documentId = D001` |
| DOC_L002 | 依使用者查詢紀錄 | MANAGER | GET /api/v1/documents/access-logs | `userId = E001` |
| DOC_L003 | 依操作類型查詢 | MANAGER | GET /api/v1/documents/access-logs | `action = DOWNLOAD` |
| DOC_L004 | 依日期範圍查詢 | MANAGER | GET /api/v1/documents/access-logs | `accessedAt >= 2025-01-01` |
| DOC_L005 | 員工查詢自己的紀錄 | EMPLOYEE | GET /api/v1/documents/access-logs | `userId = currentUserId` |

---

## 場景分類說明

| 分類 | 場景 ID | 數量 | Assembler |
|:---|:---|:---:|:---|
| 文件查詢 | DOC_D001~D010 | 10 | `DocumentListQueryAssembler` |
| 資料夾查詢 | DOC_F001~F004 | 4 | `DocumentListQueryAssembler` |
| 版本查詢 | DOC_V001~V004 | 4 | `DocumentVersionListQueryAssembler` |
| 範本查詢 | DOC_T001~T005 | 5 | `DocumentTemplateListQueryAssembler` |
| 存取紀錄查詢 | DOC_L001~L005 | 5 | `DocumentAccessLogListQueryAssembler` |
| **合計** | | **28** | |
