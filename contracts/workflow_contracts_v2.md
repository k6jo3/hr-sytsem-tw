# 流程引擎服務業務合約 (Workflow Service Contract)

> **服務代碼:** HR11
> **版本:** 2.0
> **建立日期:** 2026-02-06

---

## 查詢操作合約

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| WFL_QRY_001 | 查詢啟用流程定義 | HR | `GET /api/v1/workflow-definitions` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| WFL_QRY_002 | 查詢流程實例 | HR | `GET /api/v1/workflow-instances` | `{"definitionId":"WF001"}` | `workflow_definition_id = 'WF001'` |
| WFL_QRY_003 | 查詢待辦任務 | EMPLOYEE | `GET /api/v1/tasks/my` | `{}` | `assignee_id = '{currentUserId}'`, `status = 'PENDING'` |
| WFL_QRY_004 | 查詢已完成任務 | EMPLOYEE | `GET /api/v1/tasks/my` | `{"status":"COMPLETED"}` | `assignee_id = '{currentUserId}'`, `status = 'COMPLETED'` |

---

## 命令操作合約

| 場景 ID | API 端點 | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- |
| WFL_CMD_001 | `POST /api/v1/workflow-definitions` | 流程定義驗證 | `WorkflowDefinitionCreated` |
| WFL_CMD_002 | `POST /api/v1/workflow-instances` | 流程定義存在檢查 | `WorkflowInstanceStarted` |
| WFL_CMD_003 | `POST /api/v1/tasks/{id}/complete` | 任務權限檢查 | `TaskCompleted`, `WorkflowInstanceCompleted` |

---

## Domain Events

| 事件名稱 | 觸發時機 | 訂閱服務 |
|:---|:---|:---|
| `WorkflowInstanceStarted` | 啟動流程實例 | Notification |
| `TaskAssigned` | 分配任務 | Notification |
| `TaskCompleted` | 完成任務 | 觸發下一節點 |
| `WorkflowInstanceCompleted` | 流程完成 | 原始申請服務 |

---

**軟刪除:** 使用 `status` 欄位，不使用 `is_deleted`

**版本:** 2.0 | 2026-02-06
