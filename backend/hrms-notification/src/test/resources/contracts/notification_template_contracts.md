# HR12 通知範本管理 API 業務合約

## Phase 2: 通知範本管理 API 合約規格

---

### 2.1 範本查詢業務合約 (Template Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| TPL_Q001 | 查詢所有範本（排除已刪除） | HR | `{}` | `isDeleted = false` |
| TPL_Q002 | 根據範本代碼查詢 | HR | `{"templateCode":"LEAVE_APPROVED"}` | `templateCode = 'LEAVE_APPROVED'`, `isDeleted = false` |
| TPL_Q003 | 根據通知類型查詢啟用範本 | HR | `{"notificationType":"APPROVAL_REQUEST"}` | `notificationType = 'APPROVAL_REQUEST'`, `status = 'ACTIVE'`, `isDeleted = false` |
| TPL_Q004 | 查詢啟用的範本 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `isDeleted = false` |
| TPL_Q005 | 搜尋範本（模糊查詢） | HR | `{"templateName":"請假"}` | `templateName LIKE '%請假%'`, `isDeleted = false` |

---

### 2.2 範本建立業務合約 (Template Creation Contract)

| 場景 ID | 測試描述 | 測試目標 | 輸入 (Request) | 預期結果 |
| :--- | :--- | :--- | :--- | :--- |
| TPL_C001 | 建立範本成功 | 驗證範本建立流程 | `{"templateCode":"LEAVE_APPROVED", "templateName":"請假核准", "body":"您的請假已核准"}` | 回傳範本 ID，狀態為 ACTIVE |
| TPL_C002 | 範本代碼重複驗證 | 驗證唯一性約束 | 重複的 `templateCode` | 拋出 `DuplicateKeyException` |
| TPL_C003 | 範本代碼格式驗證 | 驗證格式規則 | `{"templateCode":"leave_approved"}` (小寫) | 拋出 `ValidationException` - 格式錯誤 |
| TPL_C004 | 必填欄位驗證 | 驗證必填欄位 | `{"templateCode":"", "body":""}` | 拋出 `ValidationException` - 必填欄位為空 |
| TPL_C005 | 範本變數自動提取 | 驗證變數提取邏輯 | `{"body":"您好 {{employeeName}}，{{leaveType}}"}` | 自動提取 `employeeName`, `leaveType` 變數 |

---

### 2.3 範本更新業務合約 (Template Update Contract)

| 場景 ID | 測試描述 | 測試目標 | 輸入 (Request) | 預期結果 |
| :--- | :--- | :--- | :--- | :--- |
| TPL_U001 | 更新範本內容成功 | 驗證範本更新流程 | `{"templateId":"tpl-001", "body":"新的內容"}` | 範本內容更新，`updatedAt` 更新 |
| TPL_U002 | 更新不存在的範本 | 驗證範本存在性 | `{"templateId":"non-existent"}` | 拋出 `NotFoundException` |
| TPL_U003 | 啟用/停用範本 | 驗證範本狀態切換 | `{"templateId":"tpl-001", "isActive":false}` | 範本狀態變更為 INACTIVE |
| TPL_U004 | 樂觀鎖衝突處理 | 驗證並行更新控制 | 同時更新同一範本 | 第二次更新拋出 `OptimisticLockException` |

---

### 2.4 範本刪除業務合約 (Template Deletion Contract)

| 場景 ID | 測試描述 | 測試目標 | 輸入 (Request) | 預期結果 |
| :--- | :--- | :--- | :--- | :--- |
| TPL_D001 | 軟刪除範本成功 | 驗證軟刪除流程 | `{"templateId":"tpl-001"}` | `isDeleted` 設為 true，範本不再出現在查詢結果 |
| TPL_D002 | 刪除不存在的範本 | 驗證範本存在性 | `{"templateId":"non-existent"}` | 拋出 `NotFoundException` |
| TPL_D003 | 刪除使用中的範本 | 驗證業務規則 | 範本仍有關聯通知 | 允許刪除（軟刪除不影響已發送通知） |

---

### 2.5 Assembler 查詢組裝合約 (Assembler Contract)

| 場景 ID | 測試描述 | 測試目標 | 呼叫方法 | 預期 QueryGroup 結構 |
| :--- | :--- | :--- | :--- | :--- |
| TPL_A001 | 查詢所有範本 Assembler | 驗證基本過濾條件 | `queryAllTemplates()` | `[isDeleted = false]` |
| TPL_A002 | 根據範本代碼查詢 Assembler | 驗證精確查詢 | `queryByTemplateCode("LEAVE_APPROVED")` | `[templateCode = 'LEAVE_APPROVED', isDeleted = false]` |
| TPL_A003 | 根據通知類型查詢 Assembler | 驗證多條件查詢 | `queryByNotificationType("APPROVAL_REQUEST")` | `[notificationType = 'APPROVAL_REQUEST', status = 'ACTIVE', isDeleted = false]` |
| TPL_A004 | 查詢啟用範本 Assembler | 驗證狀態過濾 | `queryAllActive()` | `[status = 'ACTIVE', isDeleted = false]` |

---

### 合約規格說明

1. **測試層級說明：**
   - **Query Contract (查詢合約):** 測試 Controller → Service → Assembler → QueryGroup 完整流程
   - **Command Contract (命令合約):** 測試 Controller → Service → Domain → Repository 完整流程
   - **Assembler Contract (組裝合約):** 單元測試 Assembler 的查詢條件組裝邏輯

2. **過濾條件說明：**
   - `isDeleted = false` - 所有查詢都必須排除已軟刪除的資料
   - `status = 'ACTIVE'` - 查詢啟用狀態的範本
   - `LIKE` - 支援模糊查詢

3. **測試基類使用：**
   - Query 合約測試繼承 `BaseApiContractTest`
   - Command 合約測試使用完整流程測試（需啟動資料庫）
   - Assembler 合約測試繼承 `BaseContractTest`（純單元測試）

4. **驗證重點：**
   - ✅ 所有查詢都必須包含 `isDeleted = false`
   - ✅ 狀態過濾必須正確（ACTIVE, INACTIVE）
   - ✅ 唯一性約束驗證（templateCode 不可重複）
   - ✅ 格式驗證（templateCode 必須大寫英文與底線）
   - ✅ 樂觀鎖並行控制

---

### 相關檔案

- **API 規格：** `spec/12_通知服務系統設計書_API詳細規格.md`
- **Assembler：** `TemplateQueryAssembler.java`
- **Repository：** `NotificationTemplateRepositoryImpl.java`
- **Domain Model：** `NotificationTemplate.java`
