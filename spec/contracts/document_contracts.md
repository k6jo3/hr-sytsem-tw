# 文件管理服務業務合約 (Document Service Contract)

> **服務代碼:** 13
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義文件管理服務的業務合約，涵蓋文件儲存、版本管理、權限控制等查詢場景。

---

## 1. 文件查詢合約 (Document Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| DOC_D001 | 查詢資料夾內文件 | EMPLOYEE | `{"folderId":"F001"}` | `folder_id = 'F001'`, `is_deleted = 0`, `visibility IN ('{userAccessibleVisibilities}')` |
| DOC_D002 | 依名稱模糊查詢 | EMPLOYEE | `{"name":"報告"}` | `name LIKE '報告'`, `is_deleted = 0`, `visibility IN ('{userAccessibleVisibilities}')` |
| DOC_D003 | 依類型查詢 | EMPLOYEE | `{"type":"PDF"}` | `type = 'PDF'`, `is_deleted = 0`, `visibility IN ('{userAccessibleVisibilities}')` |
| DOC_D004 | 查詢個人文件 | EMPLOYEE | `{}` | `owner_id = '{currentUserId}'`, `is_deleted = 0` |
| DOC_D005 | 查詢共享文件 | EMPLOYEE | `{"visibility":"SHARED"}` | `visibility = 'SHARED'`, `is_deleted = 0` |
| DOC_D006 | 查詢公開文件 | EMPLOYEE | `{"visibility":"PUBLIC"}` | `visibility = 'PUBLIC'`, `is_deleted = 0` |
| DOC_D007 | 依標籤查詢 | EMPLOYEE | `{"tag":"合約"}` | `tags LIKE '合約'`, `is_deleted = 0`, `visibility IN ('{userAccessibleVisibilities}')` |
| DOC_D008 | 查詢最近文件 | EMPLOYEE | `{"days":7}` | `owner_id = '{currentUserId}'`, `updated_at >= '{today-7days}'`, `is_deleted = 0` |
| DOC_D009 | HR 查詢全部文件 | HR | `{}` | `is_deleted = 0` |
| DOC_D010 | 查詢機密文件 | HR | `{"classification":"CONFIDENTIAL"}` | `classification = 'CONFIDENTIAL'`, `is_deleted = 0` |

---

## 2. 資料夾查詢合約 (Folder Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| DOC_F001 | 查詢根資料夾 | EMPLOYEE | `{"parentId":null}` | `parent_id IS NULL`, `is_deleted = 0`, `visibility IN ('{userAccessibleVisibilities}')` |
| DOC_F002 | 查詢子資料夾 | EMPLOYEE | `{"parentId":"F001"}` | `parent_id = 'F001'`, `is_deleted = 0` |
| DOC_F003 | 查詢個人資料夾 | EMPLOYEE | `{}` | `owner_id = '{currentUserId}'`, `is_deleted = 0` |
| DOC_F004 | 依名稱查詢 | EMPLOYEE | `{"name":"專案"}` | `name LIKE '專案'`, `is_deleted = 0`, `visibility IN ('{userAccessibleVisibilities}')` |

---

## 3. 文件版本查詢合約 (Document Version Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| DOC_V001 | 查詢文件版本 | EMPLOYEE | `{"documentId":"D001"}` | `document_id = 'D001'` |
| DOC_V002 | 查詢最新版本 | EMPLOYEE | `{"documentId":"D001","isLatest":true}` | `document_id = 'D001'`, `is_latest = 1` |
| DOC_V003 | 依版本號查詢 | EMPLOYEE | `{"documentId":"D001","version":"2.0"}` | `document_id = 'D001'`, `version = '2.0'` |
| DOC_V004 | 依上傳者查詢 | HR | `{"uploaderId":"E001"}` | `uploader_id = 'E001'` |

---

## 4. 文件範本查詢合約 (Document Template Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| DOC_T001 | 查詢啟用範本 | EMPLOYEE | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| DOC_T002 | 依類型查詢範本 | EMPLOYEE | `{"category":"CONTRACT"}` | `category = 'CONTRACT'`, `status = 'ACTIVE'`, `is_deleted = 0` |
| DOC_T003 | 依名稱模糊查詢 | EMPLOYEE | `{"name":"勞動"}` | `name LIKE '勞動'`, `status = 'ACTIVE'`, `is_deleted = 0` |
| DOC_T004 | 查詢部門範本 | EMPLOYEE | `{"deptId":"D001"}` | `department_id = 'D001'`, `status = 'ACTIVE'`, `is_deleted = 0` |
| DOC_T005 | HR 查詢全部範本 | HR | `{}` | `is_deleted = 0` |

---

## 5. 文件存取紀錄查詢合約 (Document Access Log Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| DOC_L001 | 查詢文件存取紀錄 | HR | `{"documentId":"D001"}` | `document_id = 'D001'` |
| DOC_L002 | 依使用者查詢 | HR | `{"userId":"E001"}` | `user_id = 'E001'` |
| DOC_L003 | 依操作類型查詢 | HR | `{"action":"DOWNLOAD"}` | `action = 'DOWNLOAD'` |
| DOC_L004 | 依日期範圍查詢 | HR | `{"startDate":"2025-01-01"}` | `access_time >= '2025-01-01'` |
| DOC_L005 | 員工查詢自己紀錄 | EMPLOYEE | `{}` | `user_id = '{currentUserId}'` |

---

## 補充說明

### 通用安全規則

1. **軟刪除過濾**: 文件/資料夾查詢須包含 `is_deleted = 0`
2. **權限過濾**: 依據 visibility 和權限設定過濾
3. **機密文件**: 只有授權人員可存取機密文件
4. **存取稽核**: 所有文件存取需記錄稽核日誌

### 文件可見性代碼

| 代碼 | 說明 |
|:---|:---|
| PRIVATE | 私人 (僅擁有者) |
| SHARED | 共享 (指定人員) |
| DEPARTMENT | 部門 |
| PUBLIC | 公開 |

### 文件分類代碼

| 代碼 | 說明 |
|:---|:---|
| PUBLIC | 公開 |
| INTERNAL | 內部 |
| CONFIDENTIAL | 機密 |
| RESTRICTED | 限制 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全部文件 | 完整管理權限 |
| MANAGER | 部門文件 + 共享文件 | 可管理部門文件 |
| EMPLOYEE | 個人文件 + 授權文件 | 依權限存取 |
