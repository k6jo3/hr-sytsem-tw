# 工作流程服務業務合約 (Workflow Service Contract)

> **服務代碼:** 11
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義工作流程服務的業務合約，涵蓋流程定義、流程實例、待辦事項等查詢場景。

---

## 1. 流程定義查詢合約 (Process Definition Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| WFL_D001 | 查詢啟用流程定義 | HR | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| WFL_D002 | 依類型查詢流程 | HR | `{"type":"LEAVE"}` | `type = 'LEAVE'`, `is_deleted = 0` |
| WFL_D003 | 查詢最新版本流程 | HR | `{"isLatest":true}` | `is_latest = 1`, `is_deleted = 0` |
| WFL_D004 | 依名稱模糊查詢 | HR | `{"name":"請假"}` | `name LIKE '請假'`, `is_deleted = 0` |
| WFL_D005 | 查詢草稿流程 | HR | `{"status":"DRAFT"}` | `status = 'DRAFT'`, `is_deleted = 0` |
| WFL_D006 | 依部門查詢流程 | HR | `{"deptId":"D001"}` | `department_id = 'D001'`, `is_deleted = 0` |

---

## 2. 流程實例查詢合約 (Process Instance Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| WFL_I001 | 查詢進行中實例 | HR | `{"status":"RUNNING"}` | `status = 'RUNNING'` |
| WFL_I002 | 查詢已完成實例 | HR | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| WFL_I003 | 查詢已取消實例 | HR | `{"status":"CANCELLED"}` | `status = 'CANCELLED'` |
| WFL_I004 | 依申請人查詢 | HR | `{"initiatorId":"E001"}` | `initiator_id = 'E001'` |
| WFL_I005 | 員工查詢自己發起的 | EMPLOYEE | `{}` | `initiator_id = '{currentUserId}'` |
| WFL_I006 | 依流程類型查詢 | HR | `{"processType":"LEAVE"}` | `process_type = 'LEAVE'` |
| WFL_I007 | 依業務單號查詢 | HR | `{"businessKey":"LV-2025-001"}` | `business_key = 'LV-2025-001'` |

---

## 3. 待辦事項查詢合約 (Task Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| WFL_T001 | 查詢個人待辦 | EMPLOYEE | `{}` | `assignee_id = '{currentUserId}'`, `status = 'PENDING'` |
| WFL_T002 | 查詢群組待辦 | EMPLOYEE | `{}` | `candidate_groups LIKE '{currentUserGroups}'`, `status = 'PENDING'` |
| WFL_T003 | 查詢已完成任務 | EMPLOYEE | `{"status":"COMPLETED"}` | `assignee_id = '{currentUserId}'`, `status = 'COMPLETED'` |
| WFL_T004 | 依流程實例查詢 | HR | `{"instanceId":"PI001"}` | `process_instance_id = 'PI001'` |
| WFL_T005 | 查詢逾期任務 | HR | `{"isOverdue":true}` | `due_date < '{now}'`, `status = 'PENDING'` |
| WFL_T006 | 依任務類型查詢 | HR | `{"taskType":"APPROVE"}` | `task_type = 'APPROVE'`, `status = 'PENDING'` |
| WFL_T007 | 依優先級查詢 | EMPLOYEE | `{"priority":"HIGH"}` | `assignee_id = '{currentUserId}'`, `priority = 'HIGH'`, `status = 'PENDING'` |

---

## 4. 審核紀錄查詢合約 (Approval Log Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| WFL_L001 | 查詢流程審核紀錄 | HR | `{"instanceId":"PI001"}` | `process_instance_id = 'PI001'` |
| WFL_L002 | 查詢審核人紀錄 | HR | `{"approverId":"E001"}` | `approver_id = 'E001'` |
| WFL_L003 | 查詢核准紀錄 | HR | `{"action":"APPROVED"}` | `action = 'APPROVED'` |
| WFL_L004 | 查詢駁回紀錄 | HR | `{"action":"REJECTED"}` | `action = 'REJECTED'` |
| WFL_L005 | 員工查詢自己審核紀錄 | EMPLOYEE | `{}` | `approver_id = '{currentUserId}'` |

---

## 補充說明

### 通用安全規則

1. **待辦隔離**: 員工只能看到指派給自己的待辦
2. **流程紀錄**: 只有相關人員可查詢流程紀錄
3. **流程定義**: 只有 HR 可管理流程定義

### 流程類型代碼

| 代碼 | 說明 |
|:---|:---|
| LEAVE | 請假 |
| OVERTIME | 加班 |
| EXPENSE | 費用報銷 |
| PURCHASE | 採購 |
| RECRUITMENT | 招募 |
| PERFORMANCE | 績效 |
| TRAINING | 訓練 |
| TRANSFER | 調動 |
| RESIGNATION | 離職 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全部流程定義與實例 | 完整管理權限 |
| MANAGER | 下屬發起的流程 | 可審核下屬申請 |
| EMPLOYEE | 自己發起的流程 + 待辦 | 只能操作自己的 |
