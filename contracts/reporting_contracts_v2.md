# 報表服務業務合約 (Reporting Service Contract)

> **服務代碼:** HR14
> **版本:** 2.0
> **建立日期:** 2026-02-06

---

## 查詢操作合約

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| RPT_QRY_001 | 查詢組織報表 | HR | `GET /api/v1/reports/organization` | `{"reportType":"HEADCOUNT"}` | `organization_id = '{orgId}'`, `report_type = 'HEADCOUNT'` |
| RPT_QRY_002 | 查詢薪資報表 | HR_PAYROLL | `GET /api/v1/reports/payroll` | `{"yearMonth":"2025-01"}` | `year_month = '2025-01'` |
| RPT_QRY_003 | 查詢差勤報表 | HR | `GET /api/v1/reports/attendance` | `{"yearMonth":"2025-01"}` | `year_month = '2025-01'` |
| RPT_QRY_004 | 查詢專案成本報表 | PM | `GET /api/v1/reports/project-cost` | `{"projectId":"P001"}` | `project_id = 'P001'` |

---

## 命令操作合約

| 場景 ID | API 端點 | 必須檢查的業務規則 | 預期發布的事件 |
| :--- | :--- | :--- | :--- |
| RPT_CMD_001 | `POST /api/v1/reports/generate` | 報表權限檢查, 資料來源檢查 | `ReportGenerated` |
| RPT_CMD_002 | `POST /api/v1/reports/{id}/export` | 匯出格式檢查 | `ReportExported` |
| RPT_CMD_003 | `POST /api/v1/dashboards` | 儀表板定義驗證 | `DashboardCreated` |

---

## Domain Events

| 事件名稱 | 觸發時機 | 訂閱服務 |
|:---|:---|:---|
| `ReportGenerated` | 產生報表 | Notification |
| `ReportExported` | 匯出報表 | Document |

---

**軟刪除:** 使用 `status` 欄位，不使用 `is_deleted`

**CQRS Read Model:** 報表服務使用 CQRS 讀取模型，訂閱所有服務的 Domain Events 更新統計數據

**版本:** 2.0 | 2026-02-06
