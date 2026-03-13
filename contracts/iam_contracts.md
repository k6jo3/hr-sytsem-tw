# HR01 IAM 服務業務合約

> **服務代碼:** HR01
> **服務名稱:** IAM 服務 (Identity & Access Management)
> **版本:** 2.1
> **更新日期:** 2026-03-13

---

## 📋 概述

IAM 服務負責系統的身份認證與授權管理，包含使用者管理、角色管理、權限管理及個人資料管理等功能。

### Gateway 路由合約

IAM 服務在 Spring Cloud Gateway 中註冊的路由 predicates 包含 `/api/v1/system/**`，使系統管理相關請求（功能開關、系統參數、排程管理）可透過此路徑路由至 IAM 服務，與既有的 `/api/v1/iam/system/**` 路徑並行運作。

---

## API 端點概覽

### 認證 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/auth/login` | POST | AUTH_CMD_001 | 登入 | ⏳ 待實作 |
| 2 | `POST /api/v1/auth/logout` | POST | AUTH_CMD_002 | 登出 | ⏳ 待實作 |
| 3 | `POST /api/v1/auth/refresh-token` | POST | AUTH_CMD_003 | 刷新Token | ⏳ 待實作 |
| 4 | `POST /api/v1/auth/forgot-password` | POST | AUTH_CMD_004 | 忘記密碼 | ⏳ 待實作 |
| 5 | `POST /api/v1/auth/reset-password` | POST | AUTH_CMD_005 | 重置密碼 | ⏳ 待實作 |

### 使用者管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/users` | GET | IAM_QRY_001 | 查詢啟用的使用者 | ✅ 已實作 |
| 2 | `GET /api/v1/users/{id}` | GET | IAM_QRY_002 | 查詢使用者詳情 | ✅ 已實作 |
| 3 | `POST /api/v1/users` | POST | IAM_CMD_001 | 建立使用者 | ✅ 已實作 |
| 4 | `PUT /api/v1/users/{id}` | PUT | IAM_CMD_002 | 更新使用者 | ✅ 已實作 |
| 5 | `PUT /api/v1/users/{id}/deactivate` | PUT | IAM_CMD_003 | 停用使用者 | ✅ 已實作 |
| 6 | `PUT /api/v1/users/{id}/activate` | PUT | IAM_CMD_004 | 啟用使用者 | ✅ 已實作 |
| 7 | `PUT /api/v1/users/{id}/roles` | PUT | IAM_CMD_005 | 指派角色給使用者 | ✅ 已實作 |
| 8 | `PUT /api/v1/users/batch-deactivate` | PUT | IAM_CMD_006 | 批次停用使用者 | ✅ 已實作 |

### 角色管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/roles` | GET | IAM_QRY_101 | 查詢所有角色 | ✅ 已實作 |
| 2 | `GET /api/v1/roles?name={keyword}` | GET | IAM_QRY_102 | 按名稱查詢角色 | ✅ 已實作 |
| 3 | `GET /api/v1/roles?isSystemRole=true` | GET | IAM_QRY_103 | 查詢系統角色 | ✅ 已實作 |
| 4 | `GET /api/v1/roles?isSystemRole=false` | GET | IAM_QRY_104 | 查詢自訂角色 | ✅ 已實作 |
| 5 | `GET /api/v1/roles/{id}` | GET | IAM_QRY_105 | 查詢角色詳情 | ✅ 已實作 |
| 6 | `POST /api/v1/roles` | POST | IAM_CMD_101 | 建立角色 | ✅ 已實作 |
| 7 | `PUT /api/v1/roles/{id}` | PUT | IAM_CMD_102 | 更新角色 | ✅ 已實作 |
| 8 | `DELETE /api/v1/roles/{id}` | DELETE | IAM_CMD_103 | 刪除角色（軟刪除） | ✅ 已實作 |
| 9 | `PUT /api/v1/roles/{id}/permissions` | PUT | IAM_CMD_104 | 更新角色權限 | ✅ 已實作 |

### 權限管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/permissions` | GET | IAM_QRY_201 | 查詢權限列表 | ⏳ 待實作 |
| 2 | `GET /api/v1/permissions/tree` | GET | IAM_QRY_202 | 查詢權限樹 | ⏳ 待實作 |

### 個人資料 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/profile` | GET | IAM_QRY_301 | 查詢個人資料 | ⏳ 待實作 |
| 2 | `PUT /api/v1/profile` | PUT | IAM_CMD_301 | 更新個人資料 | ⏳ 待實作 |
| 3 | `PUT /api/v1/profile/change-password` | PUT | IAM_CMD_302 | 修改密碼 | ⏳ 待實作 |

**總計：29 個場景（15 個已實作，14 個待實作）**

**場景分類：**
- **Command 操作：** 13 個（6 使用者 + 4 角色 + 3 認證 + 2 個人資料）
- **Query 操作：** 16 個（3 使用者 + 5 角色 + 2 權限 + 1 個人資料）

**實作狀態：**
- ✅ **已實作（15個）：** 使用者管理全部、角色管理全部
- ⏳ **待實作（14個）：** 認證全部、權限管理全部、個人資料管理全部

---

## 1. Command 操作業務合約

### 1.1 使用者管理

#### IAM_CMD_001: 建立使用者

**API 端點：** `POST /api/v1/users`

**業務場景描述：**

HR管理員建立新的系統使用者帳號。新使用者會收到一封包含臨時密碼的歡迎郵件，首次登入時必須修改密碼。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_001",
  "apiEndpoint": "POST /api/v1/users",
  "controller": "HR01UserCmdController",
  "service": "CreateUserServiceImpl",
  "permission": "user:create",
  "request": {
    "username": "jane.doe@company.com",
    "email": "jane.doe@company.com",
    "employeeId": "550e8400-e29b-41d4-a716-446655440001",
    "roleIds": ["00000000-0000-0000-0000-000000000007"],
    "sendWelcomeEmail": true
  },
  "businessRules": [
    {"rule": "username 必須為有效 Email 格式"},
    {"rule": "username 在同一 tenant 內必須唯一"},
    {"rule": "employeeId 必須存在於 Organization 服務"},
    {"rule": "roleIds 至少指定一個角色，所有角色必須存在"},
    {"rule": "一個員工只能有一個使用者帳號"},
    {"rule": "產生 8 位隨機臨時密碼（含大小寫+數字）"},
    {"rule": "密碼使用 BCrypt 加密"},
    {"rule": "設定 status = ACTIVE"},
    {"rule": "設定 passwordChangedAt = null（強制首次登入改密碼）"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "users",
      "count": 1,
      "assertions": [
        {"field": "user_id", "operator": "notNull"},
        {"field": "username", "operator": "equals", "value": "jane.doe@company.com"},
        {"field": "email", "operator": "equals", "value": "jane.doe@company.com"},
        {"field": "employee_id", "operator": "equals", "value": "550e8400-e29b-41d4-a716-446655440001"},
        {"field": "status", "operator": "equals", "value": "ACTIVE"},
        {"field": "password_changed_at", "operator": "null"},
        {"field": "tenant_id", "operator": "notNull"},
        {"field": "is_deleted", "operator": "equals", "value": false}
      ]
    },
    {
      "action": "INSERT",
      "table": "user_roles",
      "count": 1,
      "assertions": [
        {"field": "user_id", "operator": "notNull"},
        {"field": "role_id", "operator": "equals", "value": "00000000-0000-0000-0000-000000000007"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "UserCreatedEvent",
      "payload": [
        {"field": "userId", "operator": "notNull"},
        {"field": "username", "operator": "equals", "value": "jane.doe@company.com"},
        {"field": "employeeId", "operator": "equals", "value": "550e8400-e29b-41d4-a716-446655440001"}
      ]
    }
  ]
}
```

---

#### IAM_CMD_002: 更新使用者

**API 端點：** `PUT /api/v1/users/{id}`

**業務場景描述：**

更新使用者的基本資料（Email）。帳號名稱（username）建立後不可修改。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_002",
  "apiEndpoint": "PUT /api/v1/users/{id}",
  "controller": "HR01UserCmdController",
  "service": "UpdateUserServiceImpl",
  "permission": "user:write",
  "request": {
    "email": "jane.new@company.com"
  },
  "businessRules": [
    {"rule": "email 必須為有效 Email 格式"},
    {"rule": "username 不可修改"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "users",
      "count": 1,
      "assertions": [
        {"field": "email", "operator": "equals", "value": "jane.new@company.com"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "UserUpdatedEvent",
      "payload": [
        {"field": "userId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### IAM_CMD_003: 停用使用者

**API 端點：** `PUT /api/v1/users/{id}/deactivate`

**業務場景描述：**

管理員停用使用者帳號。停用後的使用者無法登入系統，但保留所有歷史資料。可透過啟用功能恢復。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_003",
  "apiEndpoint": "PUT /api/v1/users/{id}/deactivate",
  "controller": "HR01UserCmdController",
  "service": "DeactivateUserServiceImpl",
  "permission": "user:deactivate",
  "request": {},
  "businessRules": [
    {"rule": "使用者必須存在"},
    {"rule": "當前狀態必須為 ACTIVE"},
    {"rule": "不可停用自己的帳號"},
    {"rule": "停用時撤銷所有有效的 Token"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "users",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "INACTIVE"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "UserDeactivatedEvent",
      "payload": [
        {"field": "userId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### IAM_CMD_004: 啟用使用者

**API 端點：** `PUT /api/v1/users/{id}/activate`

**業務場景描述：**

管理員啟用被停用的使用者帳號，恢復其登入權限。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_004",
  "apiEndpoint": "PUT /api/v1/users/{id}/activate",
  "controller": "HR01UserCmdController",
  "service": "ActivateUserServiceImpl",
  "permission": "user:deactivate",
  "request": {},
  "businessRules": [
    {"rule": "使用者必須存在"},
    {"rule": "當前狀態必須為 INACTIVE"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "users",
      "count": 1,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "ACTIVE"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "UserActivatedEvent",
      "payload": [
        {"field": "userId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### IAM_CMD_005: 指派角色給使用者

**API 端點：** `PUT /api/v1/users/{id}/roles`

**業務場景描述：**

管理員為使用者指派一個或多個角色。指派時會先移除所有現有角色，再指派新角色（全量替換）。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_005",
  "apiEndpoint": "PUT /api/v1/users/{id}/roles",
  "controller": "HR01UserCmdController",
  "service": "AssignUserRolesServiceImpl",
  "permission": "user:assign-roles",
  "request": {
    "roleIds": ["role-uuid-1", "role-uuid-2"]
  },
  "businessRules": [
    {"rule": "使用者必須存在"},
    {"rule": "所有 roleIds 必須存在且未刪除"},
    {"rule": "roleIds 至少包含一個角色"},
    {"rule": "先刪除現有所有角色關聯，再建立新的關聯（全量替換）"}
  ],
  "expectedDataChanges": [
    {
      "action": "DELETE",
      "table": "user_roles",
      "assertions": [
        {"field": "user_id", "operator": "equals", "value": "{userId}"}
      ]
    },
    {
      "action": "INSERT",
      "table": "user_roles",
      "count": 2,
      "assertions": [
        {"field": "user_id", "operator": "equals", "value": "{userId}"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "UserRolesAssignedEvent",
      "payload": [
        {"field": "userId", "operator": "notNull"},
        {"field": "roleIds", "operator": "size", "value": 2}
      ]
    }
  ]
}
```

---

#### IAM_CMD_006: 批次停用使用者

**API 端點：** `PUT /api/v1/users/batch-deactivate`

**業務場景描述：**

管理員一次停用多個使用者帳號。常用於員工批次離職的場景。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_006",
  "apiEndpoint": "PUT /api/v1/users/batch-deactivate",
  "controller": "HR01UserCmdController",
  "service": "BatchDeactivateUsersServiceImpl",
  "permission": "user:deactivate",
  "request": {
    "userIds": ["user-uuid-1", "user-uuid-2", "user-uuid-3"]
  },
  "businessRules": [
    {"rule": "所有 userIds 必須存在"},
    {"rule": "不可包含當前登入使用者的 ID"},
    {"rule": "只停用狀態為 ACTIVE 的使用者"},
    {"rule": "撤銷所有被停用使用者的有效 Token"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "users",
      "count": 3,
      "assertions": [
        {"field": "status", "operator": "equals", "value": "INACTIVE"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "UsersBatchDeactivatedEvent",
      "payload": [
        {"field": "userIds", "operator": "size", "value": 3}
      ]
    }
  ]
}
```

---

### 1.2 角色管理

#### IAM_CMD_101: 建立角色

**API 端點：** `POST /api/v1/roles`

**業務場景描述：**

管理員建立新的自訂角色，並指派權限。自訂角色可完全由管理員自由編輯和刪除。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_101",
  "apiEndpoint": "POST /api/v1/roles",
  "controller": "HR01RoleCmdController",
  "service": "CreateRoleServiceImpl",
  "permission": "role:create",
  "request": {
    "roleCode": "PROJECT_MANAGER",
    "roleName": "專案經理",
    "description": "負責專案管理與團隊協調",
    "permissionIds": ["perm-uuid-1", "perm-uuid-2"]
  },
  "businessRules": [
    {"rule": "roleCode 在同一 tenant 內必須唯一"},
    {"rule": "roleCode 只能包含英文字母、數字和底線"},
    {"rule": "roleName 必填"},
    {"rule": "permissionIds 中的所有 ID 必須存在"},
    {"rule": "新建角色 isSystemRole = false"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "roles",
      "count": 1,
      "assertions": [
        {"field": "role_id", "operator": "notNull"},
        {"field": "role_code", "operator": "equals", "value": "PROJECT_MANAGER"},
        {"field": "role_name", "operator": "equals", "value": "專案經理"},
        {"field": "is_system_role", "operator": "equals", "value": false},
        {"field": "tenant_id", "operator": "notNull"},
        {"field": "is_deleted", "operator": "equals", "value": false}
      ]
    },
    {
      "action": "INSERT",
      "table": "role_permissions",
      "count": 2,
      "assertions": [
        {"field": "role_id", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "RoleCreatedEvent",
      "payload": [
        {"field": "roleId", "operator": "notNull"},
        {"field": "roleCode", "operator": "equals", "value": "PROJECT_MANAGER"}
      ]
    }
  ]
}
```

---

#### IAM_CMD_102: 更新角色

**API 端點：** `PUT /api/v1/roles/{id}`

**業務場景描述：**

更新角色的基本資訊（名稱、描述）。系統角色不可修改 roleCode。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_102",
  "apiEndpoint": "PUT /api/v1/roles/{id}",
  "controller": "HR01RoleCmdController",
  "service": "UpdateRoleServiceImpl",
  "permission": "role:write",
  "request": {
    "roleName": "專案經理（高級）",
    "description": "負責大型專案管理與跨部門協調"
  },
  "businessRules": [
    {"rule": "角色必須存在"},
    {"rule": "roleCode 不可修改"},
    {"rule": "系統角色可修改名稱和描述"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "roles",
      "count": 1,
      "assertions": [
        {"field": "role_name", "operator": "equals", "value": "專案經理（高級）"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "RoleUpdatedEvent",
      "payload": [
        {"field": "roleId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### IAM_CMD_103: 刪除角色（軟刪除）

**API 端點：** `DELETE /api/v1/roles/{id}`

**業務場景描述：**

刪除自訂角色。系統角色不可刪除。刪除前須檢查是否有使用者正在使用此角色。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_103",
  "apiEndpoint": "DELETE /api/v1/roles/{id}",
  "controller": "HR01RoleCmdController",
  "service": "DeleteRoleServiceImpl",
  "permission": "role:delete",
  "request": {},
  "businessRules": [
    {"rule": "角色必須存在"},
    {"rule": "isSystemRole = false（系統角色不可刪除）"},
    {"rule": "該角色未被任何使用者使用"},
    {"rule": "使用軟刪除機制"}
  ],
  "expectedDataChanges": [
    {
      "action": "SOFT_DELETE",
      "table": "roles",
      "count": 1,
      "assertions": [
        {"field": "is_deleted", "operator": "equals", "value": true},
        {"field": "deleted_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "RoleDeletedEvent",
      "payload": [
        {"field": "roleId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

#### IAM_CMD_104: 更新角色權限

**API 端點：** `PUT /api/v1/roles/{id}/permissions`

**業務場景描述：**

管理員更新角色的權限配置。權限指派採用全量替換方式，會先刪除所有現有權限，再指派新權限。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_104",
  "apiEndpoint": "PUT /api/v1/roles/{id}/permissions",
  "controller": "HR01RoleCmdController",
  "service": "AssignPermissionsServiceImpl",
  "permission": "role:assign-permissions",
  "request": {
    "permissionIds": ["perm-uuid-3", "perm-uuid-4", "perm-uuid-5"]
  },
  "businessRules": [
    {"rule": "角色必須存在"},
    {"rule": "所有 permissionIds 必須存在"},
    {"rule": "先刪除現有所有權限關聯，再建立新的關聯（全量替換）"}
  ],
  "expectedDataChanges": [
    {
      "action": "DELETE",
      "table": "role_permissions",
      "assertions": [
        {"field": "role_id", "operator": "equals", "value": "{roleId}"}
      ]
    },
    {
      "action": "INSERT",
      "table": "role_permissions",
      "count": 3,
      "assertions": [
        {"field": "role_id", "operator": "equals", "value": "{roleId}"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "RolePermissionsUpdatedEvent",
      "payload": [
        {"field": "roleId", "operator": "notNull"},
        {"field": "permissionIds", "operator": "size", "value": 3}
      ]
    }
  ]
}
```

---

### 1.3 認證管理

#### AUTH_CMD_001: 使用者登入

**API 端點：** `POST /api/v1/auth/login`

**業務場景描述：**

使用者使用帳號密碼登入系統。登入成功後返回 JWT Token（包含 AccessToken 和 RefreshToken）。連續登入失敗會導致帳號鎖定。

**測試合約：**

```json
{
  "scenarioId": "AUTH_CMD_001",
  "apiEndpoint": "POST /api/v1/auth/login",
  "controller": "HR01AuthCmdController",
  "service": "LoginServiceImpl",
  "permission": "public",
  "request": {
    "username": "john.doe@company.com",
    "password": "SecurePass123"
  },
  "businessRules": [
    {"rule": "username 必須存在"},
    {"rule": "使用者狀態必須為 ACTIVE"},
    {"rule": "密碼驗證使用 BCrypt"},
    {"rule": "密碼錯誤累計 5 次會鎖定帳號 30 分鐘"},
    {"rule": "登入成功重置失敗次數"},
    {"rule": "返回 AccessToken（15分鐘有效）和 RefreshToken（7天有效）"},
    {"rule": "記錄登入時間和 IP"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "users",
      "count": 1,
      "assertions": [
        {"field": "last_login_at", "operator": "notNull"},
        {"field": "last_login_ip", "operator": "notNull"},
        {"field": "failed_login_attempts", "operator": "equals", "value": 0}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "UserLoggedInEvent",
      "payload": [
        {"field": "userId", "operator": "notNull"},
        {"field": "loginTime", "operator": "notNull"}
      ]
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "accessToken", "type": "string", "notNull": true},
      {"name": "refreshToken", "type": "string", "notNull": true},
      {"name": "expiresIn", "type": "integer", "notNull": true},
      {"name": "tokenType", "type": "string", "value": "Bearer"}
    ]
  },
  "errorScenarios": [
    {
      "scenario": "LOGIN_FAILED - 帳號密碼錯誤",
      "errorCode": "AUTH_INVALID_CREDENTIALS",
      "expectedStatusCode": 401,
      "description": "LOGIN_FAILED DomainException 經 GlobalExceptionHandler 處理後返回 HTTP 401"
    },
    {
      "scenario": "ACCOUNT_LOCKED - 帳號已被鎖定",
      "errorCode": "AUTH_ACCOUNT_LOCKED",
      "expectedStatusCode": 401,
      "description": "ACCOUNT_LOCKED DomainException 經 GlobalExceptionHandler 處理後返回 HTTP 401（原為 423）"
    },
    {
      "scenario": "PipelineExecutionException 包裝 LOGIN_FAILED",
      "errorCode": "AUTH_INVALID_CREDENTIALS",
      "expectedStatusCode": 401,
      "description": "Business Pipeline 內拋出 LOGIN_FAILED 時，PipelineExecutionException handler 同樣返回 HTTP 401"
    },
    {
      "scenario": "PipelineExecutionException 包裝 ACCOUNT_LOCKED",
      "errorCode": "AUTH_ACCOUNT_LOCKED",
      "expectedStatusCode": 401,
      "description": "Business Pipeline 內拋出 ACCOUNT_LOCKED 時，PipelineExecutionException handler 同樣返回 HTTP 401"
    }
  ]
}
```

> **前端 apiClient 行為：** 401 攔截器會跳過登入 API（`/auth/login`）的自動導向登入頁邏輯，改為提取 `error.response.data.message` 作為 Error message 直接顯示給使用者。

---

#### AUTH_CMD_002: 使用者登出

**API 端點：** `POST /api/v1/auth/logout`

**業務場景描述：**

使用者登出系統，撤銷當前的 AccessToken 和 RefreshToken。

**測試合約：**

```json
{
  "scenarioId": "AUTH_CMD_002",
  "apiEndpoint": "POST /api/v1/auth/logout",
  "controller": "HR01AuthCmdController",
  "service": "LogoutServiceImpl",
  "permission": "authenticated",
  "request": {},
  "businessRules": [
    {"rule": "撤銷當前使用者的 AccessToken"},
    {"rule": "撤銷當前使用者的 RefreshToken"},
    {"rule": "Token 加入黑名單（Redis）直到過期"}
  ],
  "expectedDataChanges": [],
  "expectedEvents": []
}
```

---

#### AUTH_CMD_003: 刷新 Token

**API 端點：** `POST /api/v1/auth/refresh-token`

**業務場景描述：**

使用 RefreshToken 取得新的 AccessToken，延長登入狀態。

**測試合約：**

```json
{
  "scenarioId": "AUTH_CMD_003",
  "apiEndpoint": "POST /api/v1/auth/refresh-token",
  "controller": "HR01AuthCmdController",
  "service": "RefreshTokenServiceImpl",
  "permission": "public",
  "request": {
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "businessRules": [
    {"rule": "RefreshToken 必須有效且未過期"},
    {"rule": "RefreshToken 未被撤銷（不在黑名單）"},
    {"rule": "使用者狀態必須為 ACTIVE"},
    {"rule": "返回新的 AccessToken（15分鐘有效）"},
    {"rule": "RefreshToken 保持不變"}
  ],
  "expectedDataChanges": [],
  "expectedEvents": [],
  "expectedResponse": {
    "statusCode": 200,
    "requiredFields": [
      {"name": "accessToken", "type": "string", "notNull": true},
      {"name": "expiresIn", "type": "integer", "notNull": true},
      {"name": "tokenType", "type": "string", "value": "Bearer"}
    ]
  }
}
```

---

#### AUTH_CMD_004: 忘記密碼

**API 端點：** `POST /api/v1/auth/forgot-password`

**業務場景描述：**

使用者忘記密碼時，系統發送密碼重置郵件到使用者的註冊信箱。

**測試合約：**

```json
{
  "scenarioId": "AUTH_CMD_004",
  "apiEndpoint": "POST /api/v1/auth/forgot-password",
  "controller": "HR01AuthCmdController",
  "service": "ForgotPasswordServiceImpl",
  "permission": "public",
  "request": {
    "email": "john.doe@company.com"
  },
  "businessRules": [
    {"rule": "Email 必須存在於系統中"},
    {"rule": "產生隨機重置 Token（UUID）"},
    {"rule": "重置 Token 有效期 1 小時"},
    {"rule": "發送密碼重置郵件"},
    {"rule": "即使 Email 不存在也返回成功（安全考量）"}
  ],
  "expectedDataChanges": [
    {
      "action": "INSERT",
      "table": "password_reset_tokens",
      "count": 1,
      "assertions": [
        {"field": "user_id", "operator": "notNull"},
        {"field": "token", "operator": "notNull"},
        {"field": "expires_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "PasswordResetRequestedEvent",
      "payload": [
        {"field": "userId", "operator": "notNull"},
        {"field": "email", "operator": "equals", "value": "john.doe@company.com"}
      ]
    }
  ]
}
```

---

#### AUTH_CMD_005: 重置密碼

**API 端點：** `POST /api/v1/auth/reset-password`

**業務場景描述：**

使用者透過郵件中的重置連結設定新密碼。

**測試合約：**

```json
{
  "scenarioId": "AUTH_CMD_005",
  "apiEndpoint": "POST /api/v1/auth/reset-password",
  "controller": "HR01AuthCmdController",
  "service": "ResetPasswordServiceImpl",
  "permission": "public",
  "request": {
    "token": "550e8400-e29b-41d4-a716-446655440000",
    "newPassword": "NewSecurePass456"
  },
  "businessRules": [
    {"rule": "Token 必須存在且未過期"},
    {"rule": "Token 只能使用一次"},
    {"rule": "新密碼必須符合密碼強度要求（8-20位，含大小寫+數字）"},
    {"rule": "密碼使用 BCrypt 加密"},
    {"rule": "設定 passwordChangedAt 為當前時間"},
    {"rule": "撤銷所有使用者的有效 Token"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "users",
      "count": 1,
      "assertions": [
        {"field": "password", "operator": "notNull"},
        {"field": "password_changed_at", "operator": "notNull"}
      ]
    },
    {
      "action": "UPDATE",
      "table": "password_reset_tokens",
      "count": 1,
      "assertions": [
        {"field": "used_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": [
    {
      "eventType": "PasswordResetCompletedEvent",
      "payload": [
        {"field": "userId", "operator": "notNull"}
      ]
    }
  ]
}
```

---

### 1.4 個人資料管理

#### IAM_CMD_301: 更新個人資料

**API 端點：** `PUT /api/v1/profile`

**業務場景描述：**

使用者更新自己的個人資料（顯示名稱、偏好設定等）。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_301",
  "apiEndpoint": "PUT /api/v1/profile",
  "controller": "HR01ProfileCmdController",
  "service": "UpdateProfileServiceImpl",
  "permission": "authenticated",
  "request": {
    "displayName": "John Doe",
    "preferredLanguage": "zh-TW",
    "timezone": "Asia/Taipei"
  },
  "businessRules": [
    {"rule": "只能修改自己的資料"},
    {"rule": "displayName 最多 100 字元"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "users",
      "count": 1,
      "assertions": [
        {"field": "display_name", "operator": "equals", "value": "John Doe"},
        {"field": "preferred_language", "operator": "equals", "value": "zh-TW"},
        {"field": "timezone", "operator": "equals", "value": "Asia/Taipei"},
        {"field": "updated_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

#### IAM_CMD_302: 修改密碼

**API 端點：** `PUT /api/v1/profile/change-password`

**業務場景描述：**

使用者修改自己的登入密碼。需要提供舊密碼驗證身份。

**測試合約：**

```json
{
  "scenarioId": "IAM_CMD_302",
  "apiEndpoint": "PUT /api/v1/profile/change-password",
  "controller": "HR01ProfileCmdController",
  "service": "ChangePasswordServiceImpl",
  "permission": "authenticated",
  "request": {
    "oldPassword": "OldSecurePass123",
    "newPassword": "NewSecurePass456"
  },
  "businessRules": [
    {"rule": "舊密碼必須正確"},
    {"rule": "新密碼不可與舊密碼相同"},
    {"rule": "新密碼必須符合密碼強度要求（8-20位，含大小寫+數字）"},
    {"rule": "密碼使用 BCrypt 加密"},
    {"rule": "設定 passwordChangedAt 為當前時間"},
    {"rule": "撤銷所有當前使用者的有效 Token（強制重新登入）"}
  ],
  "expectedDataChanges": [
    {
      "action": "UPDATE",
      "table": "users",
      "count": 1,
      "assertions": [
        {"field": "password", "operator": "notNull"},
        {"field": "password_changed_at", "operator": "notNull"}
      ]
    }
  ],
  "expectedEvents": []
}
```

---

## 2. Query 操作業務合約

### 2.1 使用者查詢

#### IAM_QRY_001: 查詢啟用的使用者

**API 端點：** `GET /api/v1/users?status=ACTIVE`

**業務場景描述：**

管理員查詢組織內所有啟用狀態的使用者。系統應自動過濾當前租戶的使用者，並排除已刪除的記錄。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_001",
  "apiEndpoint": "GET /api/v1/users",
  "controller": "HR01UserQryController",
  "service": "GetUserListServiceImpl",
  "permission": "user:read",
  "request": {
    "status": "ACTIVE"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "ACTIVE"},
    {"field": "tenant_id", "operator": "=", "value": "{currentUserTenantId}"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "userId", "type": "uuid", "notNull": true},
      {"name": "username", "type": "string", "notNull": true},
      {"name": "displayName", "type": "string"},
      {"name": "email", "type": "email"},
      {"name": "status", "type": "string"},
      {"name": "roles", "type": "array"},
      {"name": "lastLoginAt", "type": "datetime"},
      {"name": "createdAt", "type": "datetime", "notNull": true}
    ],
    "pagination": {
      "required": true
    },
    "assertions": [
      {"field": "status", "operator": "equals", "value": "ACTIVE"}
    ]
  }
}
```

---

#### IAM_QRY_002: 查詢使用者詳情

**API 端點：** `GET /api/v1/users/{id}`

**業務場景描述：**

管理員查詢單一使用者的完整資料，包含基本資訊、角色、權限、登入記錄等。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_002",
  "apiEndpoint": "GET /api/v1/users/{id}",
  "controller": "HR01UserQryController",
  "service": "GetUserServiceImpl",
  "permission": "user:read",
  "request": {
    "userId": "user-uuid-001"
  },
  "expectedQueryFilters": [
    {"field": "user_id", "operator": "=", "value": "user-uuid-001"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "userId", "type": "uuid", "notNull": true},
      {"name": "username", "type": "string", "notNull": true},
      {"name": "displayName", "type": "string"},
      {"name": "email", "type": "email", "notNull": true},
      {"name": "employeeId", "type": "string"},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "roles", "type": "array", "notNull": true},
      {"name": "lastLoginAt", "type": "datetime"},
      {"name": "lastLoginIp", "type": "string"},
      {"name": "failedLoginAttempts", "type": "integer"},
      {"name": "passwordChangedAt", "type": "datetime"},
      {"name": "createdAt", "type": "datetime", "notNull": true},
      {"name": "updatedAt", "type": "datetime"}
    ]
  }
}
```

---

#### IAM_QRY_004: 查詢鎖定的使用者

**API 端點：** `GET /api/v1/users?status=LOCKED`

**業務場景描述：**

管理員查詢因連續登入失敗而被鎖定的使用者帳號，用於手動解鎖或監控異常登入行為。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_004",
  "apiEndpoint": "GET /api/v1/users",
  "controller": "HR01UserQryController",
  "service": "GetUserListServiceImpl",
  "permission": "user:read",
  "request": {
    "status": "LOCKED"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "LOCKED"},
    {"field": "tenant_id", "operator": "=", "value": "{currentUserTenantId}"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "userId", "type": "uuid", "notNull": true},
      {"name": "username", "type": "string", "notNull": true},
      {"name": "status", "type": "string"},
      {"name": "lockedUntil", "type": "datetime"},
      {"name": "failedLoginAttempts", "type": "integer"}
    ],
    "pagination": {
      "required": true
    },
    "assertions": [
      {"field": "status", "operator": "equals", "value": "LOCKED"}
    ]
  }
}
```

---

#### IAM_QRY_005: 按租戶查詢使用者

**API 端點：** `GET /api/v1/users?tenantId={tenantId}`

**業務場景描述：**

超級管理員跨租戶查詢使用者，用於多租戶管理場景。一般管理員無此權限，只能查詢自己租戶的使用者。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_005",
  "apiEndpoint": "GET /api/v1/users",
  "controller": "HR01UserQryController",
  "service": "GetUserListServiceImpl",
  "permission": "user:read",
  "request": {
    "tenantId": "T001"
  },
  "expectedQueryFilters": [
    {"field": "tenant_id", "operator": "=", "value": "T001"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "items",
    "minRecords": 0,
    "requiredFields": [
      {"name": "userId", "type": "uuid", "notNull": true},
      {"name": "username", "type": "string", "notNull": true},
      {"name": "tenantId", "type": "string", "notNull": true}
    ],
    "pagination": {
      "required": true
    },
    "assertions": [
      {"field": "tenantId", "operator": "equals", "value": "T001"}
    ]
  }
}
```

---

### 2.2 角色查詢

#### IAM_QRY_101: 查詢所有角色

**API 端點：** `GET /api/v1/roles`

**業務場景描述：**

管理員查詢當前租戶的所有角色（包含系統角色和自訂角色），用於角色管理和使用者角色指派。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_101",
  "apiEndpoint": "GET /api/v1/roles",
  "controller": "HR01RoleQryController",
  "service": "GetRoleListServiceImpl",
  "permission": "role:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "tenant_id", "operator": "=", "value": "{currentUserTenantId}"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "",
    "minRecords": 0,
    "requiredFields": [
      {"name": "roleId", "type": "uuid", "notNull": true},
      {"name": "roleCode", "type": "string", "notNull": true},
      {"name": "roleName", "type": "string", "notNull": true},
      {"name": "description", "type": "string"},
      {"name": "isSystemRole", "type": "boolean", "notNull": true},
      {"name": "permissions", "type": "array"},
      {"name": "createdAt", "type": "datetime", "notNull": true}
    ]
  }
}
```

---

#### IAM_QRY_102: 按名稱查詢角色

**API 端點：** `GET /api/v1/roles?name={keyword}`

**業務場景描述：**

管理員使用關鍵字搜尋角色，支援模糊查詢。常用於角色管理頁面的搜尋功能。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_102",
  "apiEndpoint": "GET /api/v1/roles",
  "controller": "HR01RoleQryController",
  "service": "GetRoleListServiceImpl",
  "permission": "role:read",
  "request": {
    "name": "管理"
  },
  "expectedQueryFilters": [
    {"field": "role_name", "operator": "LIKE", "value": "%管理%"},
    {"field": "tenant_id", "operator": "=", "value": "{currentUserTenantId}"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "",
    "minRecords": 0,
    "requiredFields": [
      {"name": "roleId", "type": "uuid", "notNull": true},
      {"name": "roleCode", "type": "string", "notNull": true},
      {"name": "roleName", "type": "string", "notNull": true}
    ],
    "assertions": [
      {"field": "roleName", "operator": "contains", "value": "管理"}
    ]
  }
}
```

---

#### IAM_QRY_103: 查詢系統角色

**API 端點：** `GET /api/v1/roles?isSystemRole=true`

**業務場景描述：**

管理員查詢系統預設角色。系統角色不可刪除或修改核心設定，但可調整權限配置。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_103",
  "apiEndpoint": "GET /api/v1/roles",
  "controller": "HR01RoleQryController",
  "service": "GetRoleListServiceImpl",
  "permission": "role:read",
  "request": {
    "isSystemRole": true
  },
  "expectedQueryFilters": [
    {"field": "is_system_role", "operator": "=", "value": true},
    {"field": "tenant_id", "operator": "=", "value": "{currentUserTenantId}"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "",
    "minRecords": 1,
    "requiredFields": [
      {"name": "roleId", "type": "uuid", "notNull": true},
      {"name": "roleCode", "type": "string", "notNull": true},
      {"name": "roleName", "type": "string", "notNull": true},
      {"name": "isSystemRole", "type": "boolean", "notNull": true}
    ],
    "assertions": [
      {"field": "isSystemRole", "operator": "equals", "value": true}
    ]
  }
}
```

---

#### IAM_QRY_104: 查詢自訂角色

**API 端點：** `GET /api/v1/roles?isSystemRole=false`

**業務場景描述：**

管理員查詢租戶自行建立的自訂角色。自訂角色可完全由管理員自由編輯和刪除。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_104",
  "apiEndpoint": "GET /api/v1/roles",
  "controller": "HR01RoleQryController",
  "service": "GetRoleListServiceImpl",
  "permission": "role:read",
  "request": {
    "isSystemRole": false
  },
  "expectedQueryFilters": [
    {"field": "is_system_role", "operator": "=", "value": false},
    {"field": "tenant_id", "operator": "=", "value": "{currentUserTenantId}"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "",
    "minRecords": 0,
    "requiredFields": [
      {"name": "roleId", "type": "uuid", "notNull": true},
      {"name": "roleCode", "type": "string", "notNull": true},
      {"name": "roleName", "type": "string", "notNull": true},
      {"name": "isSystemRole", "type": "boolean", "notNull": true}
    ],
    "assertions": [
      {"field": "isSystemRole", "operator": "equals", "value": false}
    ]
  }
}
```

---

#### IAM_QRY_105: 查詢角色詳情

**API 端點：** `GET /api/v1/roles/{id}`

**業務場景描述：**

管理員查詢單一角色的完整資料，包含角色資訊、關聯的權限列表等。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_105",
  "apiEndpoint": "GET /api/v1/roles/{id}",
  "controller": "HR01RoleQryController",
  "service": "GetRoleServiceImpl",
  "permission": "role:read",
  "request": {
    "roleId": "role-uuid-001"
  },
  "expectedQueryFilters": [
    {"field": "role_id", "operator": "=", "value": "role-uuid-001"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "roleId", "type": "uuid", "notNull": true},
      {"name": "roleCode", "type": "string", "notNull": true},
      {"name": "roleName", "type": "string", "notNull": true},
      {"name": "description", "type": "string"},
      {"name": "isSystemRole", "type": "boolean", "notNull": true},
      {"name": "permissions", "type": "array", "notNull": true},
      {"name": "userCount", "type": "integer"},
      {"name": "createdAt", "type": "datetime", "notNull": true},
      {"name": "updatedAt", "type": "datetime"}
    ]
  }
}
```

---

### 2.3 權限查詢

#### IAM_QRY_201: 查詢權限列表

**API 端點：** `GET /api/v1/permissions`

**業務場景描述：**

管理員查詢系統所有可用的權限，用於角色權限配置時選擇。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_201",
  "apiEndpoint": "GET /api/v1/permissions",
  "controller": "HR01PermissionQryController",
  "service": "GetPermissionListServiceImpl",
  "permission": "permission:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "is_active", "operator": "=", "value": true}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "minRecords": 1,
    "requiredFields": [
      {"name": "permissionId", "type": "uuid", "notNull": true},
      {"name": "permissionCode", "type": "string", "notNull": true},
      {"name": "permissionName", "type": "string", "notNull": true},
      {"name": "description", "type": "string"},
      {"name": "category", "type": "string"},
      {"name": "createdAt", "type": "datetime", "notNull": true}
    ]
  }
}
```

---

#### IAM_QRY_202: 查詢權限樹

**API 端點：** `GET /api/v1/permissions/tree`

**業務場景描述：**

管理員查詢權限的樹狀結構，按模組和分類層級組織，用於角色權限配置時以樹狀選擇器呈現。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_202",
  "apiEndpoint": "GET /api/v1/permissions/tree",
  "controller": "HR01PermissionQryController",
  "service": "GetPermissionTreeServiceImpl",
  "permission": "permission:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "is_active", "operator": "=", "value": true}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "minRecords": 1,
    "requiredFields": [
      {"name": "category", "type": "string", "notNull": true},
      {"name": "categoryName", "type": "string", "notNull": true},
      {"name": "permissions", "type": "array", "notNull": true}
    ],
    "assertions": [
      {"field": "permissions[0].permissionId", "operator": "notNull"},
      {"field": "permissions[0].permissionCode", "operator": "notNull"}
    ]
  }
}
```

---

### 2.4 個人資料查詢

#### IAM_QRY_301: 查詢個人資料

**API 端點：** `GET /api/v1/profile`

**業務場景描述：**

使用者查詢自己的個人資料，包含基本資訊、角色、權限、偏好設定等。

**測試合約：**

```json
{
  "scenarioId": "IAM_QRY_301",
  "apiEndpoint": "GET /api/v1/profile",
  "controller": "HR01ProfileQryController",
  "service": "GetProfileServiceImpl",
  "permission": "authenticated",
  "request": {},
  "expectedQueryFilters": [
    {"field": "user_id", "operator": "=", "value": "{currentUserId}"},
    {"field": "is_deleted", "operator": "=", "value": false}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "userId", "type": "uuid", "notNull": true},
      {"name": "username", "type": "string", "notNull": true},
      {"name": "displayName", "type": "string"},
      {"name": "email", "type": "email", "notNull": true},
      {"name": "employeeId", "type": "uuid"},
      {"name": "roles", "type": "array", "notNull": true},
      {"name": "permissions", "type": "array", "notNull": true},
      {"name": "preferredLanguage", "type": "string"},
      {"name": "timezone", "type": "string"},
      {"name": "lastLoginAt", "type": "datetime"},
      {"name": "passwordChangedAt", "type": "datetime"},
      {"name": "createdAt", "type": "datetime", "notNull": true}
    ]
  }
}
```

---

## 附註

### 變數替換規則

測試執行時，合約中的變數會自動替換為實際值：

- `{currentUserTenantId}` - 當前登入使用者的租戶 ID
- `{currentUserId}` - 當前登入使用者的 ID
- `{userId}` - 測試資料中的使用者 ID
- `{roleId}` - 測試資料中的角色 ID

### 測試範圍

本合約文件涵蓋 HR01 IAM 服務的完整 API 測試場景：

- ✅ **使用者管理：** 6 個 Command + 3 個 Query（已實作）
- ✅ **角色管理：** 4 個 Command + 5 個 Query（已實作）
- ⏳ **認證管理：** 5 個 Command（待實作）
- ⏳ **權限管理：** 2 個 Query（待實作）
- ⏳ **個人資料：** 2 個 Command + 1 個 Query（待實作）

### 權限說明

**已實作權限：**
- `user:read` - 查詢使用者
- `user:create` - 建立使用者
- `user:write` - 更新使用者
- `user:deactivate` - 停用/啟用使用者
- `user:assign-roles` - 指派角色
- `role:read` - 查詢角色
- `role:create` - 建立角色
- `role:write` - 更新角色
- `role:delete` - 刪除角色
- `role:assign-permissions` - 指派權限

**待實作權限：**
- `permission:read` - 查詢權限
- `authenticated` - 已登入使用者
- `public` - 公開存取（不需認證）

**特殊角色：**
- 超級管理員（SUPER_ADMIN）擁有所有權限，可跨租戶操作

### 資料異動操作類型

- `INSERT` - 新增記錄
- `UPDATE` - 更新記錄
- `DELETE` - 實體刪除記錄
- `SOFT_DELETE` - 軟刪除（設定 is_deleted = true）

### 測試資料庫表

**使用者相關：**
- `users` - 使用者主表
- `user_roles` - 使用者角色關聯表
- `password_reset_tokens` - 密碼重置 Token 表

**角色權限相關：**
- `roles` - 角色主表
- `role_permissions` - 角色權限關聯表
- `permissions` - 權限主表

**系統管理相關：**
- `feature_toggles` - 功能開關表
- `system_parameters` - 系統參數表
- `parameter_change_logs` - 參數異動記錄表
- `scheduled_job_configs` - 排程任務配置表

---

## 擴充功能合約（2026-03-05 新增）

### LDAP/AD 企業登入整合

#### IAM_LDAP_001 — LDAP 登入認證

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | IAM_LDAP_001 |
| **場景名稱** | LDAP/AD 使用者登入 |
| **前置條件** | ldap.enabled=true, LDAP Server 可連線 |
| **輸入** | username, password |
| **預期行為** | 1. LDAP bind 驗證帳密 → 2. JIT Provisioning（首次自動建立本地帳號）→ 3. 同步角色（groupRoleMapping）→ 4. 發放 JWT Token |
| **輸出** | accessToken, refreshToken, user info |
| **副作用** | users 表 INSERT/UPDATE（authSource='LDAP', ldapDn=DN）, user_roles 表 INSERT |
| **安全規則** | LDAP Filter 需跳脫特殊字元（防 LDAP Injection）, 連線逾時 5 秒, LDAPS 優先 |

#### IAM_LDAP_002 — LDAP 使用者資料同步

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | IAM_LDAP_002 |
| **場景名稱** | LDAP 使用者每次登入同步資料 |
| **前置條件** | 使用者已存在且 authSource='LDAP' |
| **輸入** | LDAP 使用者屬性（displayName, email, employeeId） |
| **預期行為** | 同步 displayName/email 至本地帳號, 不覆蓋 passwordHash |
| **輸出** | 更新後的使用者資料 |
| **副作用** | users 表 UPDATE |

#### IAM_LDAP_003 — LDAP 群組角色對應

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | IAM_LDAP_003 |
| **場景名稱** | LDAP 群組自動對應本地角色 |
| **前置條件** | groupRoleMapping 已配置 |
| **輸入** | LDAP memberOf 群組列表 |
| **預期行為** | 精確 DN 比對 + CN 模糊比對 → 對應至本地 role_code |
| **輸出** | 對應的角色代碼清單 |
| **副作用** | 無直接副作用（由 LdapLoginService 觸發 user_roles 更新） |

---

### 系統管理模組

#### IAM_SYS_001 — 功能開關管理

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | IAM_SYS_001 |
| **場景名稱** | 啟用/停用功能開關 |
| **前置條件** | 使用者具 ADMIN 角色 |
| **輸入** | featureCode, enabled (boolean) |
| **預期行為** | 切換功能開關狀態, 記錄操作者與時間 |
| **輸出** | 更新後的 FeatureToggle |
| **副作用** | feature_toggles 表 UPDATE |
| **業務規則** | toggle() 切換 enabled 反轉, 更新 updatedAt/updatedBy |

#### IAM_SYS_002 — 系統參數管理

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | IAM_SYS_002 |
| **場景名稱** | 更新系統參數 |
| **前置條件** | 使用者具 ADMIN 角色, 參數存在 |
| **輸入** | paramCode, newValue, operator |
| **預期行為** | 驗證 paramType 合法性 → 更新值 → 記錄異動軌跡 |
| **輸出** | ParameterChange（oldValue, newValue） |
| **副作用** | system_parameters 表 UPDATE, parameter_change_logs 表 INSERT |
| **業務規則** | paramType: STRING/INTEGER/DECIMAL/BOOLEAN/JSON, category: SECURITY/BUSINESS/UI/SYSTEM |

#### IAM_SYS_003 — 排程任務配置管理

| 欄位 | 值 |
|:---|:---|
| **場景 ID** | IAM_SYS_003 |
| **場景名稱** | 更新排程任務 Cron 表達式 / 啟停排程 |
| **前置條件** | 使用者具 ADMIN 角色, 排程任務存在 |
| **輸入** | jobCode, cronExpression / enabled |
| **預期行為** | 更新 cronExpression 或 enabled 狀態 |
| **輸出** | 更新後的 ScheduledJobConfig |
| **副作用** | scheduled_job_configs 表 UPDATE |
| **業務規則** | recordSuccess() 清零 consecutiveFailures, recordFailure() 累加, needsAlert() 當 ≥3 次 |
