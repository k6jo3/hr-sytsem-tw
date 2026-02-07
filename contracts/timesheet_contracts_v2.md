# 工時管理服務業務合約 (Timesheet Service Contract)

> **服務代碼:** HR07
> **版本:** 2.0
> **建立日期:** 2026-02-06
> **維護者:** SA Team

---

## 查詢操作合約 (Query Contracts)

### 1.1 工時單查詢

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TMS_QRY_T001 | 查詢員工週工時單 | PM | `GET /api/v1/timesheets` | `{"employeeId":"E001","weekStart":"2025-01-06"}` | `employee_id = 'E001'`, `week_start = '2025-01-06'` |
| TMS_QRY_T002 | 查詢待審核工時單 | PM | `GET /api/v1/timesheets` | `{"status":"PENDING"}` | `status = 'PENDING'` |
| TMS_QRY_T003 | 查詢已核准工時單 | PM | `GET /api/v1/timesheets` | `{"status":"APPROVED"}` | `status = 'APPROVED'` |
| TMS_QRY_T004 | 員工查詢自己工時單 | EMPLOYEE | `GET /api/v1/timesheets/my` | `{}` | `employee_id = '{currentUserId}'` |
| TMS_QRY_T005 | PM 查詢專案工時單 | PM | `GET /api/v1/timesheets` | `{"projectId":"P001"}` | `project_id = 'P001'` |

### 1.2 工時明細查詢

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TMS_QRY_E001 | 查詢工時單明細 | PM | `GET /api/v1/timesheet-entries` | `{"timesheetId":"TS001"}` | `timesheet_id = 'TS001'` |
| TMS_QRY_E002 | 依專案查詢明細 | PM | `GET /api/v1/timesheet-entries` | `{"projectId":"P001"}` | `project_id = 'P001'` |

---

## 命令操作合約 (Command Contracts)

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| TMS_CMD_001 | 建立週工時單 | EMPLOYEE | `POST /api/v1/timesheets` | `{"weekStart":"2025-01-06"}` | 週期重複檢查, 工作日檢查 | `TimesheetCreated` |
| TMS_CMD_002 | 填報工時 | EMPLOYEE | `POST /api/v1/timesheet-entries` | `{"timesheetId":"TS001","projectId":"P001","hours":8}` | 專案分配檢查, 工時合理性檢查 | `TimesheetEntryAdded` |
| TMS_CMD_003 | 提交工時單 | EMPLOYEE | `POST /api/v1/timesheets/{id}/submit` | `{}` | 狀態檢查（必須為 DRAFT）, 完整性檢查 | `TimesheetSubmitted` |
| TMS_CMD_004 | PM 核准工時單 | PM | `POST /api/v1/timesheets/{id}/approve` | `{}` | 狀態檢查（必須為 PENDING）, PM 權限檢查 | `TimesheetApproved` |
| TMS_CMD_005 | PM 駁回工時單 | PM | `POST /api/v1/timesheets/{id}/reject` | `{"reason":"工時不合理"}` | 狀態檢查, 駁回原因必填 | `TimesheetRejected` |

---

## Domain Events 定義

| 事件名稱 | 觸發時機 | 訂閱服務 |
|:---|:---|:---|
| `TimesheetCreated` | 建立工時單 | - |
| `TimesheetSubmitted` | 提交工時單 | Notification |
| `TimesheetApproved` | 核准工時單 | Payroll, Project |
| `TimesheetRejected` | 駁回工時單 | Notification |
| `TimesheetEntryAdded` | 填報工時 | Project (更新實際工時) |

---

## 補充說明

**軟刪除策略:**
- 工時單使用 `status` 欄位（'DRAFT', 'PENDING', 'APPROVED', 'REJECTED'）
- **不使用 `is_deleted` 欄位**

**工時單狀態流程:**
```
DRAFT (草稿) → PENDING (待審核) → APPROVED (已核准) / REJECTED (已駁回)
```

**版本紀錄**

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-06 | 移除 is_deleted，新增 API 端點、Command 操作、Domain Events |
| 1.0 | 2025-12-19 | 初版建立 |
