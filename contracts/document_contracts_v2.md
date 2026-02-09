# 文件管理服務業務合約 (Document Service Business Contract)

> **服務代碼:** HR13
> **版本:** 2.0（完整版）
> **重建日期:** 2026-02-09
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/13_文件管理服務系統設計書.md`
> - `knowledge/04_API_Specifications/13_文件管理服務系統設計書_API詳細規格.md`

---

## 📋 概述

本合約文件定義文件管理服務的**完整業務場景**，包括：
1. **Command 操作場景**（上傳、產生、版本管理）- 驗證業務規則與領域事件
2. **Query 操作場景**（查詢、下載）- 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景（上傳、產生、版本管理）
- ✅ 新增領域事件的詳細定義
- ✅ 對應到實際的 API 端點
- ✅ 完整的業務規則驗證（檔案大小、類型、加密等）
- ✅ 包含文件版本控制邏輯

**服務定位：**
文件管理服務負責企業文件的儲存、版本控制、訪問權限管理。支援多種文件類型（履歷、合約、證照、薪資單），提供文件產生（如在職證明）、加密存儲（薪資單）、版本歷史查詢等功能。

**資料軟刪除策略：**
- **文件記錄**: 使用 `status` 欄位，'ACTIVE' 為有效，'DELETED' 為已刪除，'ARCHIVED' 為已歸檔
- **版本記錄**: 不進行軟刪除，保留所有歷史版本用於版本控制
- **存取記錄**: 不進行軟刪除，保留完整的審計日誌

**角色權限說明：**
- **EMPLOYEE**: 上傳/下載自己的文件、申請證明文件
- **MANAGER**: 查詢直屬部門員工的文件
- **HR**: 查詢所有員工文件、管理文件範本
- **ADMIN**: 全部操作

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [文件上傳](#11-文件上傳)
   - 1.2 [文件產生](#12-文件產生)
   - 1.3 [版本管理](#13-版本管理)
   - 1.4 [範本管理](#14-範本管理)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [文件查詢](#21-文件查詢)
   - 2.2 [版本查詢](#22-版本查詢)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 文件上傳

#### DOC_CMD_001: 上傳員工文件

**業務場景描述：**
員工在 ESS 系統中上傳個人文件（如履歷、證照掃描件）。系統驗證檔案大小、類型，儲存到檔案系統並記錄版本號。

**API 端點：**
```
POST /api/v1/documents/upload
```

**前置條件：**
- 執行者必須是該文件的所有者或 HR/ADMIN
- documentType 必須是有效的類型
- 檔案大小不超過限制（一般檔案 10MB，薪資單 5MB）

**輸入 (Request)：**
```json
{
  "employeeId": "E001",
  "documentType": "EMPLOYEE_RESUME",
  "fileName": "王小華_履歷_20260209.pdf",
  "fileSize": 2048576,
  "mimeType": "application/pdf",
  "visibility": "PRIVATE",
  "tags": ["履歷", "2026年"]
}
```

**業務規則驗證：**

1. ✅ **檔案大小檢查**
   - 規則：根據 documentType 檢查檔案大小
     - PAYSLIP: 最大 5MB
     - 其他類型: 最大 10MB
   - 預期結果：檔案大小符合限制

2. ✅ **檔案類型驗證**
   - 規則：mimeType 必須符合 documentType 的允許類型
     - EMPLOYEE_RESUME: application/pdf, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document
     - PAYSLIP: application/pdf
     - CERTIFICATE: image/jpeg, image/png, application/pdf
   - 預期結果：mimeType 符合允許清單

3. ✅ **所有權檢查**
   - 規則：上傳者必須是 employeeId 本人或 HR/ADMIN
   - 預期結果：權限檢查通過

4. ✅ **可見性驗證**
   - 規則：visibility 必須為 ['PRIVATE', 'DEPARTMENT', 'PUBLIC']
   - 預期結果：visibility 值有效

5. ✅ **員工存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

**必須發布的領域事件：**
```json
{
  "eventId": "evt-doc-upload-001",
  "eventType": "DocumentUploadedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "document-001",
  "aggregateType": "Document",
  "payload": {
    "documentId": "document-001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "documentType": "EMPLOYEE_RESUME",
    "fileName": "王小華_履歷_20260209.pdf",
    "fileSize": 2048576,
    "version": 1,
    "visibility": "PRIVATE",
    "uploadedBy": "E001",
    "uploadedAt": "2026-02-09T09:00:00Z",
    "storagePath": "s3://hrms-documents/E001/resume/document-001.pdf"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "documentId": "document-001",
    "fileName": "王小華_履歷_20260209.pdf",
    "version": 1,
    "uploadedAt": "2026-02-09T09:00:00Z",
    "downloadUrl": "https://api.hrms.com/api/v1/documents/document-001/download"
  }
}
```

---

### 1.2 文件產生

#### DOC_CMD_002: 產生在職證明

**業務場景描述：**
員工申請在職證明，系統基於存儲的範本、員工資料和組織資訊動態產生 PDF 文件，並儲存為新的文件記錄。

**API 端點：**
```
POST /api/v1/documents/generate
```

**前置條件：**
- 執行者必須是申請者本人或 HR
- documentType 對應的範本必須存在
- 員工資料必須完整（組織、職位等）

**輸入 (Request)：**
```json
{
  "employeeId": "E001",
  "documentType": "EMPLOYMENT_CERT",
  "templateCode": "EMPLOYMENT_CERT_V1",
  "variables": {
    "employeeName": "王小華",
    "employeeNumber": "E001",
    "jobTitle": "資深軟體工程師",
    "department": "技術部",
    "hireDate": "2022-01-15",
    "issuanceDate": "2026-02-09"
  }
}
```

**業務規則驗證：**

1. ✅ **範本存在性檢查**
   - 查詢條件：`template_code = ? AND is_active = true`
   - 預期結果：範本存在且為有效

2. ✅ **員工資料完整性檢查**
   - 規則：必需欄位（employeeName, jobTitle, department）不可為空
   - 呼叫 Organization Service 驗證最新資料
   - 預期結果：員工資料完整

3. ✅ **範本變數驗證**
   - 規則：提供的變數必須包含範本中所有必填變數
   - 預期結果：變數檢查通過

4. ✅ **所有權檢查**
   - 規則：申請者必須是 employeeId 本人或 HR/ADMIN
   - 預期結果：權限檢查通過

**必須發布的領域事件：**
```json
{
  "eventId": "evt-doc-gen-001",
  "eventType": "DocumentGeneratedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "document-002",
  "aggregateType": "Document",
  "payload": {
    "documentId": "document-002",
    "employeeId": "E001",
    "employeeName": "王小華",
    "documentType": "EMPLOYMENT_CERT",
    "documentName": "在職證明_王小華_20260209.pdf",
    "templateCode": "EMPLOYMENT_CERT_V1",
    "version": 1,
    "fileSize": 512000,
    "generatedBy": "E001",
    "generatedAt": "2026-02-09T10:00:00Z",
    "storagePath": "s3://hrms-documents/E001/certificates/document-002.pdf"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "documentId": "document-002",
    "documentName": "在職證明_王小華_20260209.pdf",
    "documentType": "EMPLOYMENT_CERT",
    "generatedAt": "2026-02-09T10:00:00Z",
    "downloadUrl": "https://api.hrms.com/api/v1/documents/document-002/download"
  }
}
```

---

### 1.3 版本管理

#### DOC_CMD_003: 建立新版本

**業務場景描述：**
當員工修改現有文件（如更新履歷）後上傳新版本，系統自動遞增版本號並保留舊版本用於歷史追蹤。

**API 端點：**
```
POST /api/v1/documents/{id}/new-version
```

**前置條件：**
- 執行者必須是文件所有者或 HR/ADMIN
- 文件必須存在且狀態為 ACTIVE
- 新檔案必須符合大小和類型要求

**輸入 (Request)：**
```json
{
  "documentId": "document-001",
  "fileName": "王小華_履歷_20260215.pdf",
  "fileSize": 2150400,
  "mimeType": "application/pdf",
  "changeDescription": "更新工作經歷部分"
}
```

**業務規則驗證：**

1. ✅ **文件存在性檢查**
   - 查詢條件：`document_id = ? AND status = 'ACTIVE'`
   - 預期結果：文件存在且為 ACTIVE

2. ✅ **所有權檢查**
   - 查詢條件：`document_id = ? AND owner_id = ?`
   - 預期結果：文件屬於當前用戶或用戶為 HR/ADMIN

3. ✅ **檔案類型一致性檢查**
   - 規則：新版本的 mimeType 必須與原檔案相同或相容
   - 預期結果：檔案類型相容

4. ✅ **版本號遞增檢查**
   - 規則：新版本號 = 當前最高版本號 + 1
   - 預期結果：版本號正確遞增

**必須發布的領域事件：**
```json
{
  "eventId": "evt-doc-ver-001",
  "eventType": "DocumentVersionCreatedEvent",
  "timestamp": "2026-02-09T11:00:00Z",
  "aggregateId": "document-001",
  "aggregateType": "Document",
  "payload": {
    "documentId": "document-001",
    "employeeId": "E001",
    "currentVersion": 2,
    "previousVersion": 1,
    "fileName": "王小華_履歷_20260215.pdf",
    "fileSize": 2150400,
    "changeDescription": "更新工作經歷部分",
    "createdBy": "E001",
    "createdAt": "2026-02-09T11:00:00Z"
  }
}
```

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "documentId": "document-001",
    "currentVersion": 2,
    "fileName": "王小華_履歷_20260215.pdf",
    "createdAt": "2026-02-09T11:00:00Z"
  }
}
```

---

#### DOC_CMD_004: 刪除文件

**業務場景描述：**
員工或 HR 刪除不需要的文件記錄（使用軟刪除）。被刪除的文件仍保留在系統中用於審計，但不會在查詢列表中顯示。

**API 端點：**
```
DELETE /api/v1/documents/{id}
```

**前置條件：**
- 執行者必須是文件所有者或 HR/ADMIN
- 文件必須存在且狀態為 ACTIVE

**輸入 (Request)：**
```json
{
  "documentId": "document-001",
  "reason": "上傳錯誤"
}
```

**業務規則驗證：**

1. ✅ **文件存在性檢查**
   - 查詢條件：`document_id = ? AND status = 'ACTIVE'`
   - 預期結果：文件存在且為 ACTIVE

2. ✅ **所有權檢查**
   - 規則：刪除者必須是文件所有者或 HR/ADMIN
   - 預期結果：權限檢查通過

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "documentId": "document-001",
    "status": "DELETED",
    "deletedAt": "2026-02-09T12:00:00Z"
  }
}
```

---

### 1.4 範本管理

#### DOC_CMD_005: 建立文件範本

**業務場景描述：**
HR 管理員建立新的文件範本（如在職證明、薪資證明）。範本包含 HTML 或 Markdown 內容，支援變數替換和條件邏輯。

**API 端點：**
```
POST /api/v1/documents/templates
```

**前置條件：**
- 執行者必須擁有 `document:template:manage` 權限
- templateCode 必須唯一

**輸入 (Request)：**
```json
{
  "templateCode": "EMPLOYMENT_CERT_V1",
  "templateName": "在職證明",
  "templateType": "EMPLOYMENT_CERT",
  "templateContent": "<html><body>{{companyName}} 茲證明 {{employeeName}} 自 {{hireDate}} 起為本公司{{jobTitle}}...</body></html>",
  "variables": [
    {
      "name": "companyName",
      "type": "STRING",
      "required": true,
      "description": "公司名稱"
    },
    {
      "name": "employeeName",
      "type": "STRING",
      "required": true,
      "description": "員工名稱"
    },
    {
      "name": "jobTitle",
      "type": "STRING",
      "required": true,
      "description": "職位"
    },
    {
      "name": "hireDate",
      "type": "DATE",
      "required": true,
      "description": "到職日期"
    }
  ]
}
```

**業務規則驗證：**

1. ✅ **範本代碼唯一性檢查**
   - 查詢條件：`template_code = ?`
   - 預期結果：不存在重複的範本代碼

2. ✅ **範本類型驗證**
   - 規則：templateType 必須為 ['EMPLOYMENT_CERT', 'SALARY_CERT', 'SEPARATION_CERT', 'TAX_WITHHOLDING']
   - 預期結果：templateType 有效

3. ✅ **變數驗證**
   - 規則：templateContent 中的變數引用必須在 variables 陣列中定義
   - 預期結果：變數定義完整

**預期輸出：**
```json
{
  "success": true,
  "data": {
    "templateId": "template-001",
    "templateCode": "EMPLOYMENT_CERT_V1",
    "templateName": "在職證明",
    "createdAt": "2026-02-09T13:00:00Z"
  }
}
```

---

## 2. Query 操作業務合約

### 2.1 文件查詢

#### 2.1.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| DOC_QRY_001 | 查詢有效文件 | EMPLOYEE | `GET /api/v1/documents` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| DOC_QRY_002 | 依文件類型查詢 | HR | `GET /api/v1/documents` | `{"documentType":"PAYSLIP"}` | `document_type = 'PAYSLIP'` |
| DOC_QRY_003 | 查詢自己的文件 | EMPLOYEE | `GET /api/v1/documents/my` | `{}` | `owner_id = '{currentUserId}'`, `status = 'ACTIVE'` |
| DOC_QRY_004 | 查詢員工所有文件 | HR | `GET /api/v1/documents` | `{"employeeId":"E001"}` | `owner_id = 'E001'`, `status = 'ACTIVE'` |
| DOC_QRY_005 | 依可見性查詢 | EMPLOYEE | `GET /api/v1/documents` | `{"visibility":"PUBLIC"}` | `visibility = 'PUBLIC'`, `status = 'ACTIVE'` |

#### 2.1.2 業務場景說明

**DOC_QRY_003: 查詢自己的文件（ESS）**

- **使用者：** 員工在 ESS 系統中查詢自己的文件
- **業務目的：** 讓員工下載自己的文件副本（如薪資單、證明）
- **權限控制：** 無需特殊權限，但只能查詢自己的文件
- **過濾邏輯：**
  ```sql
  WHERE owner_id = '{currentUserId}'
    AND status = 'ACTIVE'
  ORDER BY uploaded_at DESC
  ```

**DOC_QRY_004: 查詢員工所有文件（HR）**

- **使用者：** HR 查詢特定員工的所有文件
- **業務目的：** 審核員工上傳的證照、合約等
- **權限控制：** `document:read`
- **過濾邏輯：**
  ```sql
  WHERE owner_id = 'E001'
    AND status = 'ACTIVE'
  ORDER BY uploaded_at DESC
  ```

---

### 2.2 版本查詢

#### 2.2.1 機器可讀合約表格

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| DOC_QRY_006 | 查詢文件版本歷史 | HR | `GET /api/v1/documents/{id}/versions` | `{}` | `document_id = '{id}'` |
| DOC_QRY_007 | 查詢特定版本 | EMPLOYEE | `GET /api/v1/documents/{id}/versions/{version}` | `{}` | `document_id = '{id}'`, `version = '{version}'` |

#### 2.2.2 業務場景說明

**DOC_QRY_006: 查詢文件版本歷史**

- **使用者：** HR 或文件所有者
- **業務目的：** 查看文件的所有版本和變更歷史
- **權限控制：** 文件所有者或 HR/ADMIN
- **過濾邏輯：**
  ```sql
  WHERE document_id = '{id}'
  ORDER BY version DESC
  ```

---

## 3. 領域事件合約

### 3.1 事件清單總覽

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 | 業務影響 |
|:---|:---|:---|:---|:---|
| `DocumentUploadedEvent` | 文件上傳 | Document | Notification | 發送上傳成功通知 |
| `DocumentGeneratedEvent` | 文件產生 | Document | Notification | 發送文件產生完成通知 |
| `DocumentVersionCreatedEvent` | 建立新版本 | Document | Notification | 發送版本更新通知 |
| `DocumentDeletedEvent` | 刪除文件 | Document | Notification | 發送刪除通知 |

---

### 3.2 DocumentUploadedEvent (文件上傳事件)

**觸發時機：**
文件成功上傳並儲存到檔案系統後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-doc-upload-001",
  "eventType": "DocumentUploadedEvent",
  "timestamp": "2026-02-09T09:00:00Z",
  "aggregateId": "document-001",
  "aggregateType": "Document",
  "payload": {
    "documentId": "document-001",
    "employeeId": "E001",
    "employeeName": "王小華",
    "documentType": "EMPLOYEE_RESUME",
    "fileName": "王小華_履歷_20260209.pdf",
    "fileSize": 2048576,
    "version": 1,
    "visibility": "PRIVATE",
    "uploadedBy": "E001",
    "uploadedAt": "2026-02-09T09:00:00Z",
    "storagePath": "s3://hrms-documents/E001/resume/document-001.pdf"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送上傳成功通知給上傳者

---

### 3.3 DocumentGeneratedEvent (文件產生事件)

**觸發時機：**
系統根據範本和數據成功產生新的 PDF 文件並儲存後發布此事件。

**Event Payload:**
```json
{
  "eventId": "evt-doc-gen-001",
  "eventType": "DocumentGeneratedEvent",
  "timestamp": "2026-02-09T10:00:00Z",
  "aggregateId": "document-002",
  "aggregateType": "Document",
  "payload": {
    "documentId": "document-002",
    "employeeId": "E001",
    "employeeName": "王小華",
    "documentType": "EMPLOYMENT_CERT",
    "documentName": "在職證明_王小華_20260209.pdf",
    "templateCode": "EMPLOYMENT_CERT_V1",
    "version": 1,
    "fileSize": 512000,
    "generatedBy": "E001",
    "generatedAt": "2026-02-09T10:00:00Z",
    "storagePath": "s3://hrms-documents/E001/certificates/document-002.pdf"
  }
}
```

**訂閱服務處理：**

- **Notification Service:**
  - 發送文件產生完成通知給申請者
  - 包含文件下載連結

---

## 4. 測試斷言規格

### 4.1 Command 操作測試斷言

**測試目標：** 驗證 Command 操作是否正確執行業務規則並發布領域事件。

**範例：DOC_CMD_001 上傳文件測試**

```java
@Test
@DisplayName("DOC_CMD_001: 上傳員工文件 - 應驗證檔案並發布事件")
void uploadDocument_ShouldValidateAndPublishEvent() {
    // Given
    var request = UploadDocumentRequest.builder()
        .employeeId("E001")
        .documentType("EMPLOYEE_RESUME")
        .fileName("王小華_履歷.pdf")
        .fileSize(2048576)
        .mimeType("application/pdf")
        .visibility("PRIVATE")
        .build();

    // Mock employee exists
    when(organizationService.employeeExists("E001")).thenReturn(true);

    // When
    var response = service.execCommand(request, currentUser);

    // Then - Verify document saved
    var captor = ArgumentCaptor.forClass(Document.class);
    verify(documentRepository).save(captor.capture());

    var savedDocument = captor.getValue();
    assertThat(savedDocument.getEmployeeId()).isEqualTo("E001");
    assertThat(savedDocument.getStatus()).isEqualTo(DocumentStatus.ACTIVE);
    assertThat(savedDocument.getVersion()).isEqualTo(1);

    // Then - Verify event published
    var eventCaptor = ArgumentCaptor.forClass(DocumentUploadedEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());

    var event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("DocumentUploadedEvent");
    assertThat(event.getPayload().getEmployeeId()).isEqualTo("E001");
}
```

---

### 4.2 Query 操作測試斷言

**測試目標：** 驗證 Query 操作是否正確套用過濾條件與權限控制。

**範例：DOC_QRY_003 查詢自己的文件測試**

```java
@Test
@DisplayName("DOC_QRY_003: 查詢自己的文件 - 應包含所有者ID與狀態過濾")
void queryMyDocuments_ShouldIncludeRequiredFilters() {
    // Given
    var request = QueryDocumentRequest.builder()
        .status("ACTIVE")
        .build();

    JWTModel currentUser = JWTModel.builder()
        .userId("E001")
        .build();

    // When
    var captor = ArgumentCaptor.forClass(QueryGroup.class);
    service.getResponse(request, currentUser);

    // Then
    verify(documentRepository).findPage(captor.capture(), any());

    var queryGroup = captor.getValue();

    // Verify required filters
    assertThat(queryGroup).containsFilter("owner_id", Operator.EQUAL, "E001");
    assertThat(queryGroup).containsFilter("status", Operator.EQUAL, DocumentStatus.ACTIVE);
}
```

---

### 4.3 Integration Test 斷言

**測試目標：** 驗證完整的 API → Service → Repository 流程。

**範例：DOC_CMD_001 整合測試**

```java
@Test
@DisplayName("DOC_CMD_001: 上傳文件整合測試 - 應儲存文件並返回正確回應")
void uploadDocument_Integration_ShouldSaveAndReturnResponse() throws Exception {
    // Given
    var request = UploadDocumentRequest.builder()
        .employeeId("E001")
        .documentType("EMPLOYEE_RESUME")
        .fileName("王小華_履歷.pdf")
        .fileSize(2048576)
        .mimeType("application/pdf")
        .visibility("PRIVATE")
        .build();

    // When
    var result = mockMvc.perform(post("/api/v1/documents/upload")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.documentId").isNotEmpty())
        .andExpect(jsonPath("$.data.version").value(1))
        .andReturn();

    // Then - Verify database
    var document = documentRepository.findByEmployeeId("E001").getFirst();
    assertThat(document.getStatus()).isEqualTo(DocumentStatus.ACTIVE);
    assertThat(document.getVersion()).isEqualTo(1);
}
```

---

## 補充說明

### 5.1 檔案存儲策略

1. **存儲位置:**
   - 文件儲存在 S3 或 MinIO（應取決於部署環境）
   - 路徑格式：`s3://bucket/{employeeId}/{documentType}/{documentId}.{extension}`

2. **加密存儲:**
   - PAYSLIP 類型文件必須加密存儲
   - 使用 AES-256 加密，密鑰儲存在密鑰管理服務中
   - 下載時自動解密

3. **版本控制:**
   - 每個版本保留一份完整副本
   - 刪除不刪除歷史版本（僅標記狀態）

### 5.2 權限控制

- **EMPLOYEE**: 只能操作自己的文件
- **MANAGER**: 可查詢直屬部門員工的文件（visibility >= DEPARTMENT）
- **HR**: 可查詢所有員工的文件
- **ADMIN**: 全部操作

### 5.3 軟刪除說明

- 刪除操作將 `status` 設為 'DELETED'
- 被刪除的文件仍保留在資料庫中用於審計
- 查詢時自動過濾掉 'DELETED' 狀態的文件
- 版本歷史不進行軟刪除，永久保留

---

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-09 | 完整版建立：新增詳細的 Command 操作業務場景（上傳、產生、版本管理）、業務規則驗證、Domain Events Payload 定義、測試斷言規格 |
| 1.0 | 2026-02-06 | 精簡版建立 |
