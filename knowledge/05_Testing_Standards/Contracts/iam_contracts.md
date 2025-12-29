# IAM 服務業務合約 (IAM Service Contract)

> **服務代碼:** 01
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義 IAM (Identity and Access Management) 服務的業務合約，涵蓋使用者管理、角色權限、登入驗證等查詢場景。

---

## 1. 使用者查詢合約 (User Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| IAM_U001 | 查詢啟用中的使用者 | ADMIN | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| IAM_U002 | 依帳號模糊查詢 | ADMIN | `{"username":"admin"}` | `username LIKE 'admin'`, `is_deleted = 0` |
| IAM_U003 | 依角色查詢使用者 | ADMIN | `{"roleId":"R001"}` | `roles.id = 'R001'`, `is_deleted = 0` |
| IAM_U004 | 查詢鎖定帳號 | ADMIN | `{"status":"LOCKED"}` | `status = 'LOCKED'`, `is_deleted = 0` |
| IAM_U005 | 依租戶查詢使用者 | SUPER_ADMIN | `{"tenantId":"T001"}` | `tenant_id = 'T001'`, `is_deleted = 0` |
| IAM_U006 | 一般使用者查詢同部門 | USER | `{}` | `department_id = '{currentUserDeptId}'`, `is_deleted = 0` |

---

## 2. 角色查詢合約 (Role Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| IAM_R001 | 查詢所有啟用角色 | ADMIN | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| IAM_R002 | 依名稱模糊查詢角色 | ADMIN | `{"name":"管理"}` | `name LIKE '管理'`, `is_deleted = 0` |
| IAM_R003 | 查詢系統角色 | ADMIN | `{"type":"SYSTEM"}` | `type = 'SYSTEM'`, `is_deleted = 0` |
| IAM_R004 | 查詢自訂角色 | ADMIN | `{"type":"CUSTOM"}` | `type = 'CUSTOM'`, `is_deleted = 0` |
| IAM_R005 | 依租戶查詢角色 | SUPER_ADMIN | `{"tenantId":"T001"}` | `tenant_id = 'T001'`, `is_deleted = 0` |

---

## 3. 權限查詢合約 (Permission Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| IAM_P001 | 查詢所有權限 | ADMIN | `{}` | `is_deleted = 0` |
| IAM_P002 | 依模組查詢權限 | ADMIN | `{"module":"EMPLOYEE"}` | `module = 'EMPLOYEE'`, `is_deleted = 0` |
| IAM_P003 | 依類型查詢權限 | ADMIN | `{"type":"MENU"}` | `type = 'MENU'`, `is_deleted = 0` |
| IAM_P004 | 查詢角色的權限 | ADMIN | `{"roleId":"R001"}` | `roles.id = 'R001'`, `is_deleted = 0` |

---

## 4. 登入紀錄查詢合約 (Login Log Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| IAM_L001 | 查詢成功登入紀錄 | ADMIN | `{"result":"SUCCESS"}` | `result = 'SUCCESS'` |
| IAM_L002 | 查詢失敗登入紀錄 | ADMIN | `{"result":"FAILED"}` | `result = 'FAILED'` |
| IAM_L003 | 依使用者查詢登入紀錄 | ADMIN | `{"userId":"U001"}` | `user_id = 'U001'` |
| IAM_L004 | 依時間範圍查詢 | ADMIN | `{"startDate":"2025-01-01"}` | `login_time >= '2025-01-01'` |
| IAM_L005 | 一般使用者查詢自己的紀錄 | USER | `{}` | `user_id = '{currentUserId}'` |

---

## 補充說明

### 通用安全規則

1. **所有查詢都必須包含 `is_deleted = 0`** (登入紀錄除外)
2. **租戶隔離**: 非 SUPER_ADMIN 只能查詢自己租戶的資料
3. **帳號狀態過濾**: 依據查詢目的正確過濾狀態

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| SUPER_ADMIN | 全系統跨租戶 | 無限制 |
| ADMIN | 所屬租戶 | 受租戶隔離 |
| USER | 自己 + 同部門 | 僅限基本資訊 |
