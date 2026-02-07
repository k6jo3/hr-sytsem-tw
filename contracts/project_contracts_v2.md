# 專案管理服務業務合約 (Project Service Contract)

> **服務代碼:** HR06
> **版本:** 2.0
> **建立日期:** 2026-02-06
> **維護者:** SA Team

---

## 查詢操作合約 (Query Contracts)

### 1.1 專案查詢

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PRJ_QRY_P001 | 查詢進行中專案 | PM | `GET /api/v1/projects` | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'` |
| PRJ_QRY_P002 | 查詢已完成專案 | PM | `GET /api/v1/projects` | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| PRJ_QRY_P003 | 依客戶查詢專案 | PM | `GET /api/v1/projects` | `{"customerId":"C001"}` | `customer_id = 'C001'` |
| PRJ_QRY_P004 | 依 PM 查詢專案 | PM | `GET /api/v1/projects` | `{"pmId":"E001"}` | `pm_id = 'E001'` |
| PRJ_QRY_P005 | 員工查詢參與專案 | EMPLOYEE | `GET /api/v1/projects/my` | `{}` | `team_member_ids CONTAINS '{currentUserId}'` |

### 1.2 客戶查詢

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PRJ_QRY_C001 | 查詢有效客戶 | PM | `GET /api/v1/customers` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| PRJ_QRY_C002 | 依名稱模糊查詢 | PM | `GET /api/v1/customers` | `{"keyword":"科技"}` | `name LIKE '%科技%'` |

### 1.3 WBS 查詢

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| PRJ_QRY_W001 | 查詢專案 WBS | PM | `GET /api/v1/wbs` | `{"projectId":"P001"}` | `project_id = 'P001'` |
| PRJ_QRY_W002 | 查詢頂層工作包 | PM | `GET /api/v1/wbs` | `{"projectId":"P001","parentId":null}` | `project_id = 'P001'`, `parent_id IS NULL` |

---

## 命令操作合約 (Command Contracts)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| PRJ_CMD_001 | 建立專案 | PM | `POST /api/v1/projects` | `{"name":"系統開發","customerId":"C001","pmId":"E001"}` | 客戶存在檢查, PM 權限檢查 | `ProjectCreated` |
| PRJ_CMD_002 | 更新專案狀態 | PM | `PUT /api/v1/projects/{id}/status` | `{"status":"COMPLETED"}` | 狀態轉換檢查 | `ProjectStatusChanged` |
| PRJ_CMD_003 | 建立 WBS | PM | `POST /api/v1/wbs` | `{"projectId":"P001","name":"需求分析"}` | 專案存在檢查, 層級深度限制 | `WBSCreated` |
| PRJ_CMD_004 | 分配成員 | PM | `POST /api/v1/projects/{id}/members` | `{"employeeId":"E002","role":"DEVELOPER"}` | 員工存在檢查, 重複分配檢查 | `ProjectMemberAssigned` |

---

## Domain Events 定義

| 事件名稱 | 觸發時機 | 訂閱服務 |
|:---|:---|:---|
| `ProjectCreated` | 建立專案 | Notification |
| `ProjectStatusChanged` | 專案狀態變更 | Notification, Report |
| `ProjectMemberAssigned` | 分配專案成員 | Notification |
| `WBSCreated` | 建立工作包 | Timesheet |

---

## 補充說明

**軟刪除策略:**
- 專案、客戶、WBS 使用 `status` 欄位（'ACTIVE', 'INACTIVE', 'COMPLETED', 'CANCELLED'）
- **不使用 `is_deleted` 欄位**

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-06 | 移除 is_deleted，新增 API 端點、Command 操作、Domain Events |
| 1.0 | 2025-12-19 | 初版建立 |
