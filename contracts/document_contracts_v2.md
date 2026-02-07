# 文件管理服務業務合約 (Document Service Contract)

> **服務代碼:** HR13
> **版本:** 2.0
> **建立日期:** 2026-02-06

---

## 查詢操作合約

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| DOC_QRY_001 | 查詢有效文件 | EMPLOYEE | `GET /api/v1/documents` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| DOC_QRY_002 | 依類型查詢文件 | HR | `GET /api/v1/documents` | `{"type":"CONTRACT"}` | `document_type = 'CONTRACT'` |
| DOC_QRY_003 | 查詢員工文件 | EMPLOYEE | `GET /api/v1/documents/my` | `{}` | `owner_id = '{currentUserId}'` |
| DOC_QRY_004 | 查詢文件版本 | HR | `GET /api/v1/documents/{id}/versions` | `{}` | `document_id = '{id}'` |

---

## 命令操作合約

| 場景 ID | API 端點 | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- |
| DOC_CMD_001 | `POST /api/v1/documents/upload` | 檔案大小限制, 檔案類型檢查 | `DocumentUploaded` |
| DOC_CMD_002 | `POST /api/v1/documents/{id}/new-version` | 版本號遞增檢查 | `DocumentVersionCreated` |
| DOC_CMD_003 | `DELETE /api/v1/documents/{id}` | 所有權檢查 | `DocumentDeleted` |
| DOC_CMD_004 | `POST /api/v1/documents/{id}/share` | 權限檢查 | `DocumentShared` |

---

## Domain Events

| 事件名稱 | 觸發時機 | 訂閱服務 |
|:---|:---|:---|
| `DocumentUploaded` | 上傳文件 | Notification |
| `DocumentVersionCreated` | 建立新版本 | Notification |
| `DocumentShared` | 分享文件 | Notification |

---

**軟刪除:** 使用 `status` 欄位，不使用 `is_deleted`

**版本:** 2.0 | 2026-02-06
