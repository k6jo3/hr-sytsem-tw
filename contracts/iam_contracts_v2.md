# IAM 服務業務合約 (IAM Service Business Contract)

> **服務代碼:** 01
> **版本:** 2.0（重建版）
> **重建日期:** 2026-02-06
> **維護者:** Development Team
> **參考文件:**
> - `knowledge/02_System_Design/01_IAM服務系統設計書.md`
> - `knowledge/04_API_Specifications/01_IAM服務系統設計書_API詳細規格.md`
> - `knowledge/02_Requirements_Analysis/01_IAM服務需求分析書.md`

---

## 📋 概述

本合約文件定義 IAM 服務的**完整業務場景**，包括：
1. **Command 操作場景**（建立、更新、刪除）- 驗證業務規則與領域事件
2. **Query 操作場景**（查詢）- 驗證過濾條件與權限控制
3. **領域事件規格** - 驗證 Event-Driven 架構

**與舊版差異：**
- ✅ 新增 Command 操作的業務場景
- ✅ 新增 15 個領域事件定義
- ✅ 對應到實際的 API 端點
- ✅ 使用實際存在的欄位（移除 is_deleted、department_id）
- ✅ 包含完整的業務規則驗證

---

## 目錄

1. [Command 操作業務合約](#1-command-操作業務合約)
   - 1.1 [使用者管理 Command](#11-使用者管理-command)
   - 1.2 [角色管理 Command](#12-角色管理-command)
2. [Query 操作業務合約](#2-query-操作業務合約)
   - 2.1 [使用者查詢 Query](#21-使用者查詢-query)
   - 2.2 [角色查詢 Query](#22-角色查詢-query)
   - 2.3 [權限查詢 Query](#23-權限查詢-query)
3. [領域事件合約](#3-領域事件合約)
4. [測試斷言規格](#4-測試斷言規格)

---

## 1. Command 操作業務合約

### 1.1 使用者管理 Command

#### IAM_CMD_001: 建立使用者

**業務場景描述：**
HR 管理員建立新的系統使用者帳號，關聯到已存在的員工，並指派初始角色。

**API 端點：**
```
POST /api/v1/users
```

**前置條件：**
- 執行者必須擁有 `user:create` 權限
- employeeId 必須存在於 Organization Service
- username 必須唯一（同租戶內）

**輸入 (Request)：**
```json
{
  "username": "jane.doe@company.com",
  "email": "jane.doe@company.com",
  "employeeId": "employee-uuid",
  "roleIds": ["role-uuid-1", "role-uuid-2"],
  "sendWelcomeEmail": true
}
```

**業務規則驗證：**
1. ✅ **username 唯一性檢查**
   - 查詢條件：`username = ? AND tenant_id = ?`
   - 預期結果：不存在重複

2. ✅ **employeeId 存在性檢查**
   - 呼叫 Organization Service 驗證
   - 預期結果：Employee 存在且為 ACTIVE 狀態

3. ✅ **初始密碼生成**
   - 規則：至少 8 字元，含大小寫字母、數字
   - 儲存：BCrypt 加密

4. ✅ **角色指派**
   - 至少指派一個角色
   - 所有 roleId 必須存在

5. ✅ **租戶隔離**
   - 自動設定 `tenant_id` 為當前使用者的租戶

**必須發布的領域事件：**
```
UserCreatedEvent {
  eventType: "UserCreatedEvent",
  aggregateId: "user-uuid",
  payload: {
    userId: "uuid",
    username: "jane.doe@company.com",
    email: "jane.doe@company.com",
    employeeId: "employee-uuid",
    tenantId: "tenant-uuid",
    roleIds: ["role-uuid-1", "role-uuid-2"]
  }
}
```

**輸出 (Response)：**
```json
{
  "code": "SUCCESS",
  "message": "使用者建立成功",
  "data": {
    "userId": "new-user-uuid"
  }
}
```

**後置條件：**
- users 表新增一筆記錄
- user_roles 表新增角色關聯
- UserCreatedEvent 已發布至 Kafka

**測試驗證點：**
- [ ] 資料庫驗證：users 表有新記錄，status = 'ACTIVE'
- [ ] 資料庫驗證：user_roles 表有角色關聯
- [ ] 事件驗證：UserCreatedEvent 已發布
- [ ] 郵件驗證：若 sendWelcomeEmail = true，應發送歡迎郵件

---

#### IAM_CMD_002: 停用使用者

**業務場景描述：**
HR 管理員停用離職員工的系統帳號，帳號進入 INACTIVE 狀態，無法登入。

**API 端點：**
```
PUT /api/v1/users/{userId}/deactivate
```

**前置條件：**
- 執行者必須擁有 `user:deactivate` 權限
- userId 必須存在
- 使用者當前狀態為 ACTIVE 或 LOCKED

**輸入 (Request)：**
```json
{
  "reason": "員工離職"
}
```

**業務規則驗證：**
1. ✅ **使用者存在性檢查**
   - 查詢條件：`user_id = ? AND tenant_id = ?`
   - 預期結果：使用者存在

2. ✅ **狀態轉換檢查**
   - 當前狀態：ACTIVE 或 LOCKED
   - 目標狀態：INACTIVE
   - 規則：INACTIVE 狀態的使用者不可再停用

3. ✅ **清除快取**
   - Redis 中的 Principal 快取
   - Redis 中的 Session

**必須發布的領域事件：**
```
UserDeactivatedEvent {
  eventType: "UserDeactivatedEvent",
  aggregateId: "user-uuid",
  payload: {
    userId: "uuid",
    username: "john.doe@company.com",
    deactivatedAt: "2026-02-06T10:00:00Z",
    reason: "員工離職"
  }
}
```

**輸出 (Response)：**
```json
{
  "code": "SUCCESS",
  "message": "使用者已停用"
}
```

**後置條件：**
- users 表：`status = 'INACTIVE'`, `updated_at` 更新
- Redis：清除相關快取
- UserDeactivatedEvent 已發布至 Kafka

**測試驗證點：**
- [ ] 資料庫驗證：status = 'INACTIVE'
- [ ] 快取驗證：Redis 中無該使用者的 Principal
- [ ] 事件驗證：UserDeactivatedEvent 已發布
- [ ] 登入驗證：該使用者無法登入（返回 ACCOUNT_INACTIVE 錯誤）

---

#### IAM_CMD_003: 啟用使用者

**業務場景描述：**
HR 管理員重新啟用已停用的使用者帳號。

**API 端點：**
```
PUT /api/v1/users/{userId}/activate
```

**前置條件：**
- 執行者必須擁有 `user:deactivate` 權限
- userId 必須存在
- 使用者當前狀態為 INACTIVE

**業務規則驗證：**
1. ✅ **狀態轉換檢查**
   - 當前狀態：INACTIVE
   - 目標狀態：ACTIVE

**必須發布的領域事件：**
```
UserActivatedEvent {
  eventType: "UserActivatedEvent",
  aggregateId: "user-uuid",
  payload: {
    userId: "uuid",
    activatedAt: "2026-02-06T10:00:00Z"
  }
}
```

**測試驗證點：**
- [ ] 資料庫驗證：status = 'ACTIVE'
- [ ] 事件驗證：UserActivatedEvent 已發布
- [ ] 登入驗證：該使用者可以正常登入

---

#### IAM_CMD_004: 管理員重置密碼

**API 端點：**
```
PUT /api/v1/users/{userId}/reset-password
```

**業務規則驗證：**
1. ✅ **密碼強度檢查**（同建立使用者）
2. ✅ **記錄密碼變更時間**：`password_changed_at`
3. ✅ **清除 Session**：強制使用者重新登入

**必須發布的領域事件：**
```
PasswordResetEvent {
  eventType: "PasswordResetEvent",
  aggregateId: "user-uuid",
  payload: {
    userId: "uuid",
    resetBy: "admin-user-id",
    resetAt: "2026-02-06T10:00:00Z"
  }
}
```

**測試驗證點：**
- [ ] 資料庫驗證：password_hash 已更新
- [ ] 資料庫驗證：password_changed_at 已更新
- [ ] 快取驗證：Session 已清除
- [ ] 事件驗證：PasswordResetEvent 已發布

---

#### IAM_CMD_005: 指派角色

**API 端點：**
```
PUT /api/v1/users/{userId}/roles
```

**輸入 (Request)：**
```json
{
  "roleIds": ["role-uuid-1", "role-uuid-2", "role-uuid-3"]
}
```

**業務規則驗證：**
1. ✅ **角色存在性檢查**：所有 roleId 必須存在
2. ✅ **租戶隔離**：只能指派同租戶的角色
3. ✅ **覆蓋式更新**：刪除舊角色，新增新角色

**必須發布的領域事件：**
```
UserRoleAssignedEvent {
  eventType: "UserRoleAssignedEvent",
  aggregateId: "user-uuid",
  payload: {
    userId: "uuid",
    roleIds: ["role-uuid-1", "role-uuid-2", "role-uuid-3"]
  }
}
```

**測試驗證點：**
- [ ] 資料庫驗證：user_roles 表記錄正確
- [ ] 快取驗證：Principal 快取已更新
- [ ] 事件驗證：UserRoleAssignedEvent 已發布

---

#### IAM_CMD_006: 批次停用使用者

**API 端點：**
```
PUT /api/v1/users/batch-deactivate
```

**輸入 (Request)：**
```json
{
  "userIds": ["user-uuid-1", "user-uuid-2"],
  "reason": "批次停用"
}
```

**業務規則驗證：**
1. ✅ **批次處理**：逐一驗證每個使用者
2. ✅ **部分成功處理**：某些失敗不影響其他使用者

**必須發布的領域事件：**
```
為每個成功停用的使用者發布 UserDeactivatedEvent
```

**輸出 (Response)：**
```json
{
  "code": "SUCCESS",
  "message": "批次停用完成",
  "data": {
    "successCount": 2,
    "failedCount": 0,
    "results": [
      {"userId": "user-uuid-1", "status": "SUCCESS"},
      {"userId": "user-uuid-2", "status": "SUCCESS"}
    ]
  }
}
```

**測試驗證點：**
- [ ] 資料庫驗證：所有使用者 status = 'INACTIVE'
- [ ] 事件驗證：每個使用者都有對應的 UserDeactivatedEvent

---

### 1.2 角色管理 Command

#### IAM_CMD_101: 建立角色

**API 端點：**
```
POST /api/v1/roles
```

**輸入 (Request)：**
```json
{
  "roleName": "PROJECT_MANAGER",
  "displayName": "專案經理",
  "description": "負責專案管理",
  "permissionCodes": ["project:read", "project:write", "timesheet:approve"]
}
```

**業務規則驗證：**
1. ✅ **角色名稱唯一性**：`role_name` 在同租戶內唯一
2. ✅ **權限有效性**：所有 permissionCode 必須存在
3. ✅ **租戶隔離**：自動設定 `tenant_id`

**必須發布的領域事件：**
```
RoleCreatedEvent {
  eventType: "RoleCreatedEvent",
  aggregateId: "role-uuid",
  payload: {
    roleId: "uuid",
    roleName: "PROJECT_MANAGER",
    displayName: "專案經理",
    tenantId: "tenant-uuid"
  }
}
```

**測試驗證點：**
- [ ] 資料庫驗證：roles 表有新記錄
- [ ] 資料庫驗證：role_permissions 表有權限關聯
- [ ] 事件驗證：RoleCreatedEvent 已發布

---

#### IAM_CMD_102: 更新角色權限

**API 端點：**
```
PUT /api/v1/roles/{roleId}/permissions
```

**輸入 (Request)：**
```json
{
  "permissionCodes": ["project:read", "project:write", "timesheet:approve", "report:read"]
}
```

**業務規則驗證：**
1. ✅ **系統角色保護**：`is_system_role = true` 的角色不可修改
2. ✅ **覆蓋式更新**：刪除舊權限，新增新權限

**必須發布的領域事件：**
```
RolePermissionChangedEvent {
  eventType: "RolePermissionChangedEvent",
  aggregateId: "role-uuid",
  payload: {
    roleId: "uuid",
    addedPermissions: ["report:read"],
    removedPermissions: []
  }
}
```

**測試驗證點：**
- [ ] 資料庫驗證：role_permissions 表記錄正確
- [ ] 快取驗證：擁有此角色的使用者 Principal 快取已清除
- [ ] 事件驗證：RolePermissionChangedEvent 已發布

---

#### IAM_CMD_103: 刪除角色

**API 端點：**
```
DELETE /api/v1/roles/{roleId}
```

**業務規則驗證：**
1. ✅ **系統角色保護**：`is_system_role = true` 的角色不可刪除
2. ✅ **使用中檢查**：有使用者使用的角色不可刪除

**必須發布的領域事件：**
```
RoleDeletedEvent {
  eventType: "RoleDeletedEvent",
  aggregateId: "role-uuid",
  payload: {
    roleId: "uuid",
    roleName: "PROJECT_MANAGER"
  }
}
```

**測試驗證點：**
- [ ] 資料庫驗證：roles 表記錄已刪除（或標記刪除）
- [ ] 資料庫驗證：role_permissions 表關聯已刪除
- [ ] 事件驗證：RoleDeletedEvent 已發布

---

## 2. Query 操作業務合約

### 2.1 使用者查詢合約表（Machine-Readable）

**重要：** 此表格可被 `MarkdownContractEngine` 自動解析驗證

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| IAM_QRY_001 | 查詢啟用中的使用者 | ADMIN | `GET /api/v1/users` | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `tenant_id = '{currentUserTenantId}'` |
| IAM_QRY_002 | 依關鍵字模糊查詢 | ADMIN | `GET /api/v1/users` | `{"search":"john"}` | `(username LIKE '%john%' OR email LIKE '%john%')`, `tenant_id = '{currentUserTenantId}'` |
| IAM_QRY_003 | 依角色查詢使用者 | ADMIN | `GET /api/v1/users` | `{"roleId":"R001"}` | `user_id IN (SELECT user_id FROM user_roles WHERE role_id = 'R001')`, `tenant_id = '{currentUserTenantId}'` |
| IAM_QRY_004 | 查詢鎖定的使用者 | ADMIN | `GET /api/v1/users` | `{"status":"LOCKED"}` | `status = 'LOCKED'`, `tenant_id = '{currentUserTenantId}'` |
| IAM_QRY_005 | 依租戶查詢使用者 | SUPER_ADMIN | `GET /api/v1/users` | `{"tenantId":"T001"}` | `tenant_id = 'T001'` |
| IAM_QRY_006 | 查詢特定部門的使用者 | ADMIN | `GET /api/v1/users` | `{"departmentId":"D001"}` | `employee_id IN (SELECT employee_id FROM employees WHERE department_id = 'D001')`, `tenant_id = '{currentUserTenantId}'` |

**重要說明：**
- ❌ **移除了 `is_deleted = 0`** - 此欄位不存在
- ✅ **所有查詢都自動加上 `tenant_id` 過濾** - 租戶隔離
- ⚠️ **IAM_QRY_006 需要跨服務查詢或冗餘資料** - 待架構師確認

---

### 2.2 使用者查詢場景詳細說明

#### IAM_QRY_001: 查詢啟用中的使用者

**業務場景描述：**
ADMIN 查詢所有啟用中（ACTIVE）的使用者列表。

**API 端點：**
```
GET /api/v1/users?status=ACTIVE
```

**權限要求：**
- 必須擁有 `user:read` 權限

**輸入 (Query Parameters)：**
```
status=ACTIVE
page=1
size=20
```

**必須包含的過濾條件：**
```sql
WHERE status = 'ACTIVE'
  AND tenant_id = '{currentUserTenantId}'  -- 自動加上租戶隔離
```

**不應包含的過濾條件：**
```sql
-- ❌ 錯誤：沒有 is_deleted 欄位
-- is_deleted = 0

-- ✅ 正確：使用 status 欄位管理狀態
-- status != 'DELETED'  也不需要，因為沒有 DELETED 狀態值
```

**輸出驗證：**
- 所有返回的使用者 `status = 'ACTIVE'`
- 所有返回的使用者 `tenant_id = currentUserTenantId`

**測試斷言：**
```java
QueryGroup query = capturedQuery.getValue();

// 必須有的過濾條件
assertThat(query).hasFilterForField("status", "ACTIVE");
assertThat(query).hasFilterForField("tenant_id", currentUser.getTenantId());

// 不應該有的過濾條件
assertThat(query).doesNotHaveFilterForField("is_deleted");
```

---

#### IAM_QRY_002: 依關鍵字模糊查詢使用者

**業務場景描述：**
ADMIN 透過關鍵字搜尋使用者（搜尋 username 或 email）。

**API 端點：**
```
GET /api/v1/users?search=john
```

**輸入 (Query Parameters)：**
```
search=john
```

**必須包含的過濾條件：**
```sql
WHERE (username LIKE '%john%' OR email LIKE '%john%')
  AND tenant_id = '{currentUserTenantId}'
```

**注意事項：**
- `search` 參數應該轉換成對 `username` 或 `email` 的 LIKE 查詢
- 不是只查詢 `username`，而是 `username OR email`

**測試斷言：**
```java
QueryGroup query = capturedQuery.getValue();

// 應該有 OR 條件組
assertThat(query).hasOrCondition(
    likeFilter("username", "john"),
    likeFilter("email", "john")
);

// 必須有租戶隔離
assertThat(query).hasFilterForField("tenant_id", currentUser.getTenantId());
```

---

#### IAM_QRY_003: 依角色查詢使用者

**業務場景描述：**
ADMIN 查詢擁有特定角色的所有使用者。

**API 端點：**
```
GET /api/v1/users?roleId=R001
```

**輸入 (Query Parameters)：**
```
roleId=R001
```

**必須包含的過濾條件：**
```sql
-- 需要 JOIN user_roles 表
SELECT u.* FROM users u
INNER JOIN user_roles ur ON u.user_id = ur.user_id
WHERE ur.role_id = 'R001'
  AND u.tenant_id = '{currentUserTenantId}'
```

**注意事項：**
- 需要 JOIN 查詢，不是 User 表的直接欄位
- QueryBuilder 應該支援 JOIN 條件

**測試斷言：**
```java
QueryGroup query = capturedQuery.getValue();

// 應該有 JOIN 條件或子查詢
assertThat(query).hasJoinCondition("user_roles", "role_id", "R001");
// 或
assertThat(query).hasSubQuery("user_id IN (SELECT user_id FROM user_roles WHERE role_id = 'R001')");
```

---

#### IAM_QRY_004: 查詢鎖定的使用者

**API 端點：**
```
GET /api/v1/users?status=LOCKED
```

**必須包含的過濾條件：**
```sql
WHERE status = 'LOCKED'
  AND tenant_id = '{currentUserTenantId}'
```

**測試斷言：**
```java
assertThat(query).hasFilterForField("status", "LOCKED");
assertThat(query).hasFilterForField("tenant_id", currentUser.getTenantId());
```

---

#### IAM_QRY_005: 依租戶查詢使用者（SUPER_ADMIN）

**業務場景描述：**
SUPER_ADMIN 跨租戶查詢特定租戶的使用者。

**API 端點：**
```
GET /api/v1/users?tenantId=T001
```

**權限要求：**
- 必須擁有 `SUPER_ADMIN` 角色

**必須包含的過濾條件：**
```sql
WHERE tenant_id = 'T001'
```

**注意事項：**
- 只有 SUPER_ADMIN 可以查詢其他租戶
- 一般 ADMIN 會自動過濾自己的租戶，不允許指定 tenantId

**測試斷言：**
```java
// 模擬 SUPER_ADMIN
@WithMockUser(roles = "SUPER_ADMIN")
void searchByTenant_AsSuperAdmin() {
    // ...
    assertThat(query).hasFilterForField("tenant_id", "T001");
}

// 模擬一般 ADMIN
@WithMockUser(roles = "ADMIN")
void searchByTenant_AsAdmin_ShouldForbidden() {
    // ...
    // 應該返回 403 Forbidden
}
```

---

#### IAM_QRY_006: 查詢特定部門的使用者

**業務場景描述：**
ADMIN 查詢特定部門的使用者。

**API 端點：**
```
GET /api/v1/users?departmentId=D001
```

**輸入 (Query Parameters)：**
```
departmentId=D001
```

**必須包含的過濾條件：**
```sql
-- 需要 JOIN organization.employees 表
SELECT u.* FROM users u
INNER JOIN organization.employees e ON u.employee_id = e.employee_id
WHERE e.department_id = 'D001'
  AND u.tenant_id = '{currentUserTenantId}'
```

**注意事項：**
- ⚠️ **User 表沒有 department_id 欄位**
- 需要透過 `employee_id` JOIN 到 Organization Service 的 Employee 表
- 這可能需要跨服務查詢或在 IAM Service 維護員工部門的冗餘資料

**實作建議：**
1. **方案 A**：跨服務查詢
   - 先呼叫 Organization Service 查詢 department_id = 'D001' 的所有 employeeId
   - 再用 `employee_id IN (...)` 查詢 users 表

2. **方案 B**：冗餘資料
   - 在 users 表新增 `department_id` 欄位（冗餘）
   - 監聽 EmployeeDepartmentChangedEvent 同步更新

**測試斷言：**
```java
// 如果採用方案 A
assertThat(query).hasFilterForField("employee_id", in(employeeIds));

// 如果採用方案 B
assertThat(query).hasFilterForField("department_id", "D001");
```

**TODO: 需要與架構師確認實作方案**

---

### 2.3 角色查詢合約表（Machine-Readable）

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| IAM_QRY_101 | 查詢所有啟用角色 | ADMIN | `GET /api/v1/roles` | `{}` | `(tenant_id = '{currentUserTenantId}' OR tenant_id IS NULL)` |
| IAM_QRY_102 | 依名稱模糊查詢角色 | ADMIN | `GET /api/v1/roles` | `{"name":"管理"}` | `(display_name LIKE '%管理%' OR role_name LIKE '%管理%')`, `(tenant_id = '{currentUserTenantId}' OR tenant_id IS NULL)` |
| IAM_QRY_103 | 查詢系統預設角色 | ADMIN | `GET /api/v1/roles` | `{"isSystemRole":"true"}` | `is_system_role = true` |
| IAM_QRY_104 | 查詢自訂角色 | ADMIN | `GET /api/v1/roles` | `{"isSystemRole":"false"}` | `is_system_role = false`, `tenant_id = '{currentUserTenantId}'` |

---

### 2.4 角色查詢場景詳細說明

#### IAM_QRY_101: 查詢所有啟用角色

**API 端點：**
```
GET /api/v1/roles
```

**必須包含的過濾條件：**
```sql
WHERE tenant_id = '{currentUserTenantId}' OR tenant_id IS NULL
-- tenant_id IS NULL 表示系統預設角色
```

**測試斷言：**
```java
assertThat(query).hasOrCondition(
    equalFilter("tenant_id", currentUser.getTenantId()),
    nullFilter("tenant_id")
);
```

---

#### IAM_QRY_102: 依名稱模糊查詢角色

**API 端點：**
```
GET /api/v1/roles?name=管理
```

**必須包含的過濾條件：**
```sql
WHERE (display_name LIKE '%管理%' OR role_name LIKE '%管理%')
  AND (tenant_id = '{currentUserTenantId}' OR tenant_id IS NULL)
```

---

#### IAM_QRY_103: 查詢系統預設角色

**API 端點：**
```
GET /api/v1/roles?isSystemRole=true
```

**必須包含的過濾條件：**
```sql
WHERE is_system_role = true
```

---

#### IAM_QRY_104: 查詢自訂角色

**API 端點：**
```
GET /api/v1/roles?isSystemRole=false
```

**必須包含的過濾條件：**
```sql
WHERE is_system_role = false
  AND tenant_id = '{currentUserTenantId}'
```

---

### 2.5 權限查詢合約表（Machine-Readable）

| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| IAM_QRY_201 | 查詢所有權限 | ADMIN | `GET /api/v1/permissions` | `{}` | (無特殊過濾，權限是全域的) |
| IAM_QRY_202 | 依模組查詢權限 | ADMIN | `GET /api/v1/permissions` | `{"resource":"employee"}` | `resource = 'employee'` |
| IAM_QRY_203 | 查詢角色的權限 | ADMIN | `GET /api/v1/permissions` | `{"roleId":"R001"}` | `permission_id IN (SELECT permission_id FROM role_permissions WHERE role_id = 'R001')` |

---

### 2.6 權限查詢場景詳細說明

#### IAM_QRY_201: 查詢所有權限

**API 端點：**
```
GET /api/v1/permissions
```

**必須包含的過濾條件：**
```sql
-- 權限是全域的，不需要租戶隔離
WHERE 1=1
```

**測試斷言：**
```java
// 不應該有租戶過濾
assertThat(query).doesNotHaveFilterForField("tenant_id");
```

---

#### IAM_QRY_202: 依模組查詢權限

**API 端點：**
```
GET /api/v1/permissions?resource=employee
```

**必須包含的過濾條件：**
```sql
WHERE resource = 'employee'
```

**注意事項：**
- Permission 表有 `resource` 和 `action` 欄位
- 不是 `module` 和 `type`（舊合約的錯誤）

---

#### IAM_QRY_203: 查詢角色的權限

**API 端點：**
```
GET /api/v1/permissions?roleId=R001
```

**必須包含的過濾條件：**
```sql
SELECT p.* FROM permissions p
INNER JOIN role_permissions rp ON p.permission_id = rp.permission_id
WHERE rp.role_id = 'R001'
```

---

## 3. 領域事件合約

### 3.1 使用者相關事件

| 事件名稱 | 觸發場景 | Schema 必須包含 | 訂閱服務 |
|:---|:---|:---|:---|
| **UserCreatedEvent** | IAM_CMD_001 建立使用者 | userId, username, email, employeeId, tenantId, roleIds | Organization, Notification |
| **UserUpdatedEvent** | 更新使用者資料 | userId, updatedFields | - |
| **UserDeactivatedEvent** | IAM_CMD_002 停用使用者 | userId, username, deactivatedAt, reason | Notification |
| **UserActivatedEvent** | IAM_CMD_003 啟用使用者 | userId, activatedAt | - |
| **UserLoggedInEvent** | 登入成功 | userId, loginTime, ipAddress | Notification |
| **AccountLockedEvent** | 登入失敗超過 5 次 | userId, lockedUntil | Notification |
| **AccountUnlockedEvent** | 解鎖帳號 | userId, unlockedAt | - |
| **PasswordChangedEvent** | 使用者修改密碼 | userId, changedAt | Notification |
| **PasswordResetEvent** | IAM_CMD_004 管理員重置密碼 | userId, resetBy, resetAt | Notification |

### 3.2 角色相關事件

| 事件名稱 | 觸發場景 | Schema 必須包含 | 訂閱服務 |
|:---|:---|:---|:---|
| **RoleCreatedEvent** | IAM_CMD_101 建立角色 | roleId, roleName, displayName, tenantId | - |
| **RoleUpdatedEvent** | 更新角色 | roleId, updatedFields | - |
| **RoleDeletedEvent** | IAM_CMD_103 刪除角色 | roleId, roleName | - |
| **RolePermissionChangedEvent** | IAM_CMD_102 更新角色權限 | roleId, addedPermissions, removedPermissions | - |
| **UserRoleAssignedEvent** | IAM_CMD_005 指派角色 | userId, roleIds | - |
| **UserRoleRevokedEvent** | 移除角色 | userId, roleIds | - |

### 3.3 事件驗證規格

**所有領域事件必須包含的基本欄位：**
```json
{
  "eventId": "uuid",
  "eventType": "UserCreatedEvent",
  "occurredAt": "2026-02-06T10:00:00Z",
  "aggregateId": "aggregate-uuid",
  "aggregateType": "User",
  "payload": {
    // 具體事件資料
  }
}
```

**測試驗證方式：**
```java
// 1. 驗證事件已發布
ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
verify(eventPublisher).publish(eventCaptor.capture());

DomainEvent event = eventCaptor.getValue();

// 2. 驗證事件類型
assertThat(event.getEventType()).isEqualTo("UserCreatedEvent");

// 3. 驗證 Payload
UserCreatedEvent payload = (UserCreatedEvent) event.getPayload();
assertThat(payload.getUserId()).isNotNull();
assertThat(payload.getUsername()).isEqualTo("jane.doe@company.com");
```

---

## 4. 測試斷言規格

### 4.1 Query 操作測試模板

```java
@Test
@WithMockUser(roles = "ADMIN")
@DisplayName("IAM_QRY_001: 查詢啟用中的使用者")
void searchActiveUsers_AsAdmin_ShouldIncludeCorrectFilters() throws Exception {
    // Arrange
    ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
    when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

    // Act - 調用實際 API
    mockMvc.perform(get("/api/v1/users?status=ACTIVE")
            .requestAttr("currentUser", mockAdminUser)
            .contentType("application/json"))
            .andExpect(status().isOk());

    // Assert - 驗證 QueryGroup
    QueryGroup query = queryCaptor.getValue();

    // 必須有的過濾條件
    assertThat(query).hasFilter("status", Operator.EQ, "ACTIVE");
    assertThat(query).hasFilter("tenant_id", Operator.EQ, mockAdminUser.getTenantId());

    // 不應該有的過濾條件
    assertThat(query).doesNotHaveFilterForField("is_deleted");
}
```

### 4.2 Command 操作測試模板

```java
@Test
@WithMockUser(roles = "ADMIN")
@DisplayName("IAM_CMD_001: 建立使用者")
void createUser_AsAdmin_ShouldPublishEvent() throws Exception {
    // Arrange
    CreateUserRequest request = CreateUserRequest.builder()
            .username("jane.doe@company.com")
            .email("jane.doe@company.com")
            .employeeId("employee-uuid")
            .roleIds(Arrays.asList("role-uuid-1"))
            .build();

    ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);

    // Act
    mockMvc.perform(post("/api/v1/users")
            .requestAttr("currentUser", mockAdminUser)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

    // Assert - 驗證資料庫
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertThat(savedUser.getUsername()).isEqualTo("jane.doe@company.com");
    assertThat(savedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);

    // Assert - 驗證事件
    verify(eventPublisher).publish(eventCaptor.capture());

    DomainEvent event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("UserCreatedEvent");
}
```

---

## 附錄 A: 欄位對照表

### User 模型欄位（實際存在）

| 欄位名稱 | 資料型別 | 說明 | 在哪些查詢中使用 |
|:---|:---|:---|:---|
| user_id | UUID | 主鍵 | - |
| username | VARCHAR(255) | 登入帳號 | IAM_QRY_002 |
| password_hash | VARCHAR(255) | 加密密碼 | - |
| email | VARCHAR(255) | Email | IAM_QRY_002 |
| employee_id | UUID | 關聯員工 | IAM_QRY_006（JOIN） |
| tenant_id | UUID | 所屬租戶 | 所有查詢（自動加上） |
| status | VARCHAR(20) | 帳號狀態 | IAM_QRY_001, IAM_QRY_004 |
| last_login_at | TIMESTAMP | 最後登入時間 | - |
| password_changed_at | TIMESTAMP | 密碼變更時間 | - |
| failed_login_attempts | INTEGER | 失敗登入次數 | - |
| locked_until | TIMESTAMP | 鎖定至何時 | - |
| created_at | TIMESTAMP | 建立時間 | - |
| updated_at | TIMESTAMP | 更新時間 | - |

### 不存在的欄位（舊合約的錯誤）

| 欄位名稱 | 為何不存在 | 正確做法 |
|:---|:---|:---|
| ❌ is_deleted | IAM 使用 status 欄位管理狀態，沒有軟刪除機制 | 使用 status = 'INACTIVE' 表示停用 |
| ❌ department_id（在 User 表） | User 透過 employee_id 關聯 Employee，department_id 在 Employee 表 | 需要 JOIN Employee 表查詢 |
| ❌ module（在 Permission 表） | Permission 表使用 resource 和 action 欄位 | 使用 resource = 'employee' |
| ❌ type（在 Permission 表） | 沒有此欄位 | 使用 resource 和 action 組合 |

---

## 附錄 B: 自動化測試使用指南

### B.1 如何使用合約表格進行自動化驗證

**Step 1: 載入合約規格**
```java
@BeforeEach
void setUp() throws Exception {
    contractSpec = loadContractSpec("iam");  // 載入 iam_contracts_v2.md
}
```

**Step 2: 執行 API 並攔截 QueryGroup**
```java
@Test
void searchActiveUsers_AsAdmin_ShouldMatchContract() throws Exception {
    // Arrange
    ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
    when(userRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

    // Act - 調用實際 API
    mockMvc.perform(get("/api/v1/users?status=ACTIVE")
            .requestAttr("currentUser", mockAdminUser)
            .contentType("application/json"))
            .andExpect(status().isOk());

    // Assert - 自動驗證合約
    QueryGroup query = queryCaptor.getValue();
    assertContract(query, contractSpec, "IAM_QRY_001");  // ✅ 自動解析表格並驗證
}
```

**Step 3: MarkdownContractEngine 自動驗證**

`assertContract()` 方法會：
1. 解析 Markdown 表格，找到 `IAM_QRY_001` 的定義
2. 提取「必須包含的過濾條件」欄位
3. 驗證 `QueryGroup` 是否包含所有必需的過濾條件
4. 如果不符合，拋出詳細的錯誤訊息

**輸出範例（測試失敗時）：**
```
╔══════════════════════════════════════════════════════════════╗
║                    合約驗證失敗                               ║
╠══════════════════════════════════════════════════════════════╣
║ 場景 ID: IAM_QRY_001                                          ║
╠══════════════════════════════════════════════════════════════╣
║ 缺失的過濾條件:                                               ║
║   ❌ tenant_id = '{currentUserTenantId}'                      ║
╠══════════════════════════════════════════════════════════════╣
║ 實際產出的過濾條件:                                           ║
║   ✓ status = 'ACTIVE'                                        ║
╚══════════════════════════════════════════════════════════════╝
```

### B.2 合約表格格式說明

**必須包含的欄位：**
- `場景 ID`: 唯一識別碼（例如：IAM_QRY_001）
- `測試描述`: 場景簡短說明
- `模擬角色`: 執行者角色（ADMIN, SUPER_ADMIN, USER）
- `API 端點`: 對應的 API 端點
- `輸入 (Request)`: JSON 格式的輸入參數
- `必須包含的過濾條件 (Required Filters)`: SQL 風格的過濾條件，用逗號分隔

**過濾條件格式：**
```
單一條件: status = 'ACTIVE'
多個條件: status = 'ACTIVE', tenant_id = '{currentUserTenantId}'
OR 條件: (username LIKE '%john%' OR email LIKE '%john%')
IN 條件: user_id IN (SELECT user_id FROM user_roles WHERE role_id = 'R001')
```

### B.3 合約表格 vs 詳細說明的關係

| 部分 | 用途 | 讀者 |
|:---|:---|:---|
| **合約表格** | 機器可讀，自動化驗證 | 測試程式 |
| **詳細說明** | 人類可讀，業務理解 | 開發人員、SA |

**兩者關係：**
- 合約表格提供**精確的測試斷言**
- 詳細說明提供**業務背景和實作指引**
- 兩者內容必須**完全一致**

### B.4 變數替換規則

**合約中的變數：**
- `{currentUserTenantId}` - 當前使用者的租戶 ID
- `{currentUserId}` - 當前使用者的 ID
- `{currentUserDeptId}` - 當前使用者的部門 ID

**測試時處理：**
```java
// 方案 A: 在驗證前替換變數
String processedSpec = contractSpec
    .replace("{currentUserTenantId}", mockUser.getTenantId())
    .replace("{currentUserId}", mockUser.getUserId());
assertContract(query, processedSpec, "IAM_QRY_001");

// 方案 B: MarkdownContractEngine 自動替換（推薦）
assertContract(query, contractSpec, "IAM_QRY_001", mockUser);
```

---

## 附錄 C: 與舊版合約的差異

| 項目 | 舊版 (v1.0) | 新版 (v2.0) |
|:---|:---|:---|
| **Command 場景** | ❌ 無 | ✅ 6 個使用者場景 + 3 個角色場景 |
| **Query 場景** | ✅ 6 個 | ✅ 6 個（修正過濾條件） |
| **領域事件** | ❌ 無 | ✅ 15 個事件定義 |
| **API 端點** | ❌ 無 | ✅ 完整對應 |
| **is_deleted 欄位** | ✅ 要求必須有 | ❌ 已移除（不存在） |
| **department_id 欄位** | ✅ 要求 User 表有 | ❌ 已修正（需 JOIN） |
| **測試斷言** | ⬚ 簡單 | ✅ 完整的測試模板 |

---

## 變更歷史

| 版本 | 日期 | 變更內容 |
|:---|:---|:---|
| 2.0 | 2026-02-06 | 完全重建，基於系統設計書和 API 規格文件 |
| 1.0 | 2025-12-19 | 初版（已廢棄，存在多處錯誤） |

---

**注意事項：**
1. 本合約文件與系統設計書、API 規格文件保持一致
2. 所有欄位都是實際存在的
3. 所有業務場景都有對應的 API 端點
4. 所有領域事件都有完整的定義
5. 測試應該驗證完整的業務流程，而不僅僅是查詢條件
