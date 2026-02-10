# HR01 IAM 服務文件一致性分析報告

**分析日期:** 2026-02-06
**分析範圍:** 需求分析書、系統設計書、API 規格文件、合約文件
**結論:** 系統設計書與需求分析書高度一致，但合約文件完全脫節

---

## 📋 摘要

| 對比項目 | 需求分析書 vs 設計書 | 設計書 vs API 規格 | 合約文件 vs 其他 |
|:---|:---:|:---:|:---:|
| **一致性** | ✅ 高度一致 | ✅ 高度一致 | ❌ 完全脫節 |
| **資料模型** | ✅ 對應 | ✅ 對應 | ❌ 不一致 |
| **API 定義** | ✅ 對應 | ✅ 對應 | ❌ 無 API 定義 |
| **領域事件** | ✅ 對應 | ✅ 對應 | ❌ 完全沒有 |
| **業務場景** | ✅ 有定義 | ✅ 有流程圖 | ❌ 只有查詢條件 |

---

## 1️⃣ User 資料模型對比

### 需求分析書定義

```
User {
  userId: UUID (PK)
  username: String (unique, 登入帳號)
  passwordHash: String (BCrypt加密)
  email: String
  employeeId: UUID (FK to Organization.Employee)
  tenantId: UUID (所屬租戶)
  status: UserStatus (ACTIVE, INACTIVE, LOCKED)  ← 使用 status 欄位
  lastLoginAt: DateTime
  passwordChangedAt: DateTime
  failedLoginAttempts: Integer
  createdAt: DateTime
  updatedAt: DateTime
}

enum UserStatus {
  ACTIVE       // 啟用
  INACTIVE     // 停用
  LOCKED       // 鎖定
}
```

### 系統設計書定義

```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    employee_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  ← 使用 status 欄位
    last_login_at TIMESTAMP,
    password_changed_at TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,  ← 新增技術實作細節
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED'))
);
```

### 合約文件定義

```markdown
IAM_U001: 查詢啟用中的使用者
必須包含: status = 'ACTIVE', is_deleted = 0  ← 憑空產生的欄位！

IAM_U006: 一般使用者查詢同部門
必須包含: department_id = '{currentUserDeptId}', is_deleted = 0  ← 憑空產生的欄位！
```

### 🔍 對比結論

| 欄位 | 需求分析書 | 系統設計書 | 合約文件 | 狀態 |
|:---|:---:|:---:|:---:|:---|
| `user_id` | ✅ | ✅ | - | 一致 |
| `username` | ✅ | ✅ | ✅ | 一致 |
| `email` | ✅ | ✅ | - | 一致 |
| `employee_id` | ✅ | ✅ | - | 一致 |
| `tenant_id` | ✅ | ✅ | ✅ | 一致 |
| `status` | ✅ ACTIVE/INACTIVE/LOCKED | ✅ 同左 | ✅ 同左 | **一致** |
| `locked_until` | ❌ | ✅ | - | 設計書新增（合理） |
| **`is_deleted`** | ❌ **不存在** | ❌ **不存在** | ✅ **要求必須有** | **🔴 合約錯誤** |
| **`department_id`** | ❌ **不存在** | ❌ **不存在** | ✅ **用於 IAM_U006** | **🔴 合約錯誤** |

**結論：**
- ✅ **需求分析書與系統設計書高度一致**（僅新增 locked_until 技術細節）
- ❌ **合約文件定義了兩個不存在的欄位**（is_deleted, department_id）

---

## 2️⃣ API 端點對比

### 需求分析書定義

```
5.2 使用者管理API

5.2.1 建立使用者
POST /api/v1/users

5.2.2 更新使用者資料
PUT /api/v1/users/{userId}

5.2.3 查詢使用者列表
GET /api/v1/users

5.2.4 查詢單一使用者
GET /api/v1/users/{userId}

5.2.5 停用使用者
PUT /api/v1/users/{userId}/deactivate

5.2.6 重置密碼
PUT /api/v1/users/{userId}/reset-password

5.2.7 指派角色
PUT /api/v1/users/{userId}/roles
```

### 系統設計書定義

```
9.2 API總覽 (24個端點)

| 端點 | 方法 | Controller |
|:---|:---:|:---|
| /api/v1/users | GET | HR01UserQryController |
| /api/v1/users/{id} | GET | HR01UserQryController |
| /api/v1/users | POST | HR01UserCmdController |
| /api/v1/users/{id} | PUT | HR01UserCmdController |
| /api/v1/users/{id}/deactivate | PUT | HR01UserCmdController |
| /api/v1/users/{id}/activate | PUT | HR01UserCmdController |
| /api/v1/users/{id}/reset-password | PUT | HR01UserCmdController |
| /api/v1/users/{id}/roles | PUT | HR01UserCmdController |
| /api/v1/users/batch-deactivate | PUT | HR01UserCmdController |  ← 新增
```

### API 規格文件定義

```
1.2 API總覽 (29個端點)

| 端點 | 方法 | Controller | 權限 |
|:---|:---:|:---|:---|
| /api/v1/users | GET | HR01UserQryController | user:read |
| /api/v1/users/{id} | GET | HR01UserQryController | user:read |
| /api/v1/users | POST | HR01UserCmdController | user:create |
| /api/v1/users/{id} | PUT | HR01UserCmdController | user:write |
| /api/v1/users/{id}/deactivate | PUT | HR01UserCmdController | user:deactivate |
| /api/v1/users/{id}/activate | PUT | HR01UserCmdController | user:deactivate |
| /api/v1/users/{id}/reset-password | PUT | HR01UserCmdController | user:reset-password |
| /api/v1/users/{id}/roles | PUT | HR01UserCmdController | user:assign-role |
| /api/v1/users/batch-deactivate | PUT | HR01UserCmdController | user:deactivate |
| /api/v1/permissions | GET | HR01PermissionQryController | permission:read |  ← 新增
| /api/v1/permissions/tree | GET | HR01PermissionQryController | permission:read |  ← 新增
```

### 合約文件定義

```
❌ 完全沒有 API 端點定義！

只有查詢場景的過濾條件：
- IAM_U001: 查詢啟用中的使用者
- IAM_U002: 依帳號模糊查詢
- ...
```

### 🔍 對比結論

| 項目 | 需求分析書 | 系統設計書 | API 規格文件 | 合約文件 |
|:---|:---:|:---:|:---:|:---:|
| API 端點數量 | ~20 個 | 24 個 | 29 個 | ❌ 0 個 |
| 基本 CRUD | ✅ | ✅ | ✅ | ❌ |
| 批次操作 | ⬚ | ✅ | ✅ | ❌ |
| 權限定義 | ✅ | ⬚ | ✅ | ❌ |
| Request/Response | ✅ | ✅ | ✅ 最完整 | ❌ |

**結論：**
- ✅ **需求分析書 → 設計書 → API 規格文件是漸進式完善，高度一致**
- ❌ **合約文件完全沒有定義 API 端點**

---

## 3️⃣ 領域事件對比

### 需求分析書定義

```
7.2 事件驅動整合 (Event-Driven)

發布的事件：
- UserCreatedEvent: 使用者建立完成
- UserDeactivatedEvent: 使用者停用
- UserRoleChangedEvent: 使用者角色變更
- AccountLockedEvent: 帳號鎖定
- PasswordChangedEvent: 密碼變更

訂閱的事件：
- EmployeeCreatedEvent (from Organization)
- EmployeeTerminatedEvent (from Organization)
```

### 系統設計書定義

```
8.1 事件清單

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 |
|:---|:---|:---|:---|
| UserCreatedEvent | 建立使用者 | IAM | Organization, Notification |
| UserUpdatedEvent | 更新使用者資料 | IAM | - |
| UserDeactivatedEvent | 停用使用者 | IAM | Notification |
| UserActivatedEvent | 啟用使用者 | IAM | - |
| UserLoggedInEvent | 登入成功 | IAM | Notification |
| AccountLockedEvent | 帳號鎖定 | IAM | Notification |
| AccountUnlockedEvent | 帳號解鎖 | IAM | - |
| PasswordChangedEvent | 修改密碼 | IAM | Notification |
| PasswordResetEvent | 重置密碼 | IAM | Notification |
| RoleCreatedEvent | 建立角色 | IAM | - |
| RoleUpdatedEvent | 更新角色 | IAM | - |
| RoleDeletedEvent | 刪除角色 | IAM | - |
| RolePermissionChangedEvent | 角色權限變更 | IAM | - |
| UserRoleAssignedEvent | 指派角色給使用者 | IAM | - |
| UserRoleRevokedEvent | 移除使用者角色 | IAM | - |

8.2 事件Schema (完整定義，含 JSON Schema)
```

### API 規格文件定義

```
8. 領域事件總覽

8.1 事件清單 (與系統設計書一致)

每個 API 的業務邏輯中都有說明：
- 建立使用者 API → 發布 UserCreatedEvent
- 停用使用者 API → 發布 UserDeactivatedEvent
- ...
```

### 合約文件定義

```
❌ 完全沒有領域事件定義！
❌ 沒有 Command 操作的業務場景！
❌ 只有 Query 操作的過濾條件！
```

### 🔍 對比結論

| 項目 | 需求分析書 | 系統設計書 | API 規格文件 | 合約文件 |
|:---|:---:|:---:|:---:|:---:|
| 領域事件清單 | ✅ 5 個 | ✅ 15 個（完善） | ✅ 15 個 | ❌ **0 個** |
| 事件 Schema | ⬚ 概念性 | ✅ 完整 JSON | ✅ 有 | ❌ **無** |
| 觸發時機 | ✅ | ✅ | ✅ | ❌ **無** |
| 訂閱者定義 | ✅ | ✅ | ✅ | ❌ **無** |

**結論：**
- ✅ **需求分析書定義核心事件，設計書完善細節，API 規格對應實作**
- ❌ **合約文件完全沒有領域事件，無法驗證 Command 操作**

---

## 4️⃣ 業務場景對比

### 需求分析書定義

```
4. 核心業務邏輯

4.1 使用者認證流程
4.2 密碼策略
4.3 帳號鎖定機制
4.4 Token管理
4.5 角色權限檢查
4.6 多租戶隔離
...
```

### 系統設計書定義

```
5. 業務流程設計 (UX Flow)

5.1 登入流程 (含 Mermaid 時序圖)
5.2 使用者管理流程
5.3 角色權限管理流程
5.4 個人資料管理流程
5.5 OAuth 整合流程
...
```

### API 規格文件定義

```
每個 API 都有完整的業務邏輯說明

3.1 查詢使用者列表
業務邏輯：
1. 權限檢查 - 必須擁有 user:read 權限
2. 資料篩選 - 只能查詢同租戶的使用者
3. 回傳分頁資料

3.2 建立使用者
業務邏輯：
1. 驗證請求資料
2. 查詢 employeeId 是否存在
3. 檢查 username 唯一性
4. 產生初始密碼
5. 指派角色
6. 發布 UserCreatedEvent
```

### 合約文件定義

```
只有查詢場景的過濾條件定義：

IAM_U001: 查詢啟用中的使用者
輸入: {"status":"ACTIVE"}
必須包含: status = 'ACTIVE', is_deleted = 0

IAM_U002: 依帳號模糊查詢
輸入: {"username":"admin"}
必須包含: username LIKE 'admin', is_deleted = 0

...

❌ 沒有 Command 操作的業務場景
❌ 沒有建立使用者的場景
❌ 沒有停用使用者的場景
❌ 沒有登入流程的場景
```

### 🔍 對比結論

| 業務場景類型 | 需求分析書 | 系統設計書 | API 規格文件 | 合約文件 |
|:---|:---:|:---:|:---:|:---:|
| **Command 操作** | ✅ 有定義 | ✅ 有流程圖 | ✅ 有業務邏輯 | ❌ **完全沒有** |
| **Query 操作** | ✅ 有定義 | ✅ 有流程圖 | ✅ 有業務邏輯 | ✅ 只有過濾條件 |
| **業務規則** | ✅ 完整 | ✅ 完整 | ✅ 完整 | ⬚ 部分（查詢） |
| **錯誤處理** | ✅ 有 | ✅ 有 | ✅ 完整 | ❌ **無** |
| **權限檢查** | ✅ 有 | ✅ 有 | ✅ 完整 | ⬚ 部分（角色） |

**結論：**
- ✅ **需求分析書、設計書、API 規格文件都有完整的業務場景定義**
- ❌ **合約文件只定義了 Query 操作的過濾條件，完全缺少 Command 操作**

---

## 5️⃣ 查詢過濾條件對比

### API 規格文件定義

```
3.1 查詢使用者列表

Query Parameters:
- search: 搜尋關鍵字 (username/email)
- status: 狀態篩選 (ACTIVE/INACTIVE/LOCKED)
- roleId: 角色篩選
- departmentId: 部門篩選

業務邏輯:
1. 權限檢查 - 必須擁有 user:read 權限
2. 資料篩選 - 只能查詢同租戶的使用者（自動加上 tenant_id 過濾）
3. 依搜尋條件篩選
```

### 合約文件定義

```
IAM_U001: 查詢啟用中的使用者
輸入: {"status":"ACTIVE"}
必須包含: status = 'ACTIVE', is_deleted = 0  ← is_deleted 不存在！

IAM_U002: 依帳號模糊查詢
輸入: {"username":"admin"}
必須包含: username LIKE 'admin', is_deleted = 0  ← is_deleted 不存在！

IAM_U006: 一般使用者查詢同部門
輸入: {}
必須包含: department_id = '{currentUserDeptId}', is_deleted = 0  ← department_id 不存在！
```

### 🔍 對比結論

| 過濾條件 | API 規格文件 | 實際資料表 | 合約文件 | 狀態 |
|:---|:---:|:---:|:---:|:---|
| `status` | ✅ 有 | ✅ 有 | ✅ 有 | 一致 |
| `tenant_id` | ✅ 自動加上 | ✅ 有 | ⬚ 未提 | API 規格正確 |
| `search` (username/email) | ✅ 有 | ✅ 有 | ⬚ 部分 | 合約用 username |
| `roleId` | ✅ 有 | ✅ JOIN | ⬚ 未實作 | - |
| `departmentId` | ✅ 有參數 | ❌ **User 表無此欄位** | ✅ 要求有 | **🔴 兩者都錯** |
| **`is_deleted`** | ❌ **未提及** | ❌ **資料表無此欄位** | ✅ **要求必須有** | **🔴 合約憑空產生** |

**重大發現：**

1. **`is_deleted` 欄位問題**：
   - 需求分析書：❌ 無
   - 系統設計書：❌ 無
   - API 規格文件：❌ 無
   - 實際資料表：❌ 無
   - 合約文件：✅ **要求必須有**（錯誤！）

2. **`department_id` 欄位問題**：
   - User 模型設計：User 透過 `employeeId` 關聯到 Organization.Employee，Employee 才有 department_id
   - API 規格文件：有 `departmentId` 查詢參數（需要 JOIN 查詢）
   - 合約文件：要求 User 表直接有 `department_id`（錯誤！）

---

## 📊 總體一致性評分

| 對比維度 | 需求 vs 設計 | 設計 vs API 規格 | 合約 vs 其他 |
|:---|:---:|:---:|:---:|
| 資料模型 | 95% ✅ | 100% ✅ | 30% ❌ |
| API 定義 | 90% ✅ | 95% ✅ | 0% ❌ |
| 領域事件 | 85% ✅ | 100% ✅ | 0% ❌ |
| 業務場景 | 90% ✅ | 95% ✅ | 20% ❌ |
| 查詢條件 | - | 90% ✅ | 40% ❌ |
| **總體評分** | **90% ✅ 高度一致** | **96% ✅ 高度一致** | **18% ❌ 嚴重脫節** |

---

## ✅ 結論

### 1️⃣ 需求分析書與系統設計書

**✅ 高度一致（90%）**

- 資料模型完全對應
- API 定義漸進式完善
- 領域事件從概念到實作一致
- 業務場景完整對應

**差異主要是：**
- 系統設計書補充了技術實作細節（如 locked_until 欄位）
- 系統設計書補充了更多的領域事件（從 5 個擴充到 15 個）
- **這些差異都是合理的設計細化，沒有偏離需求**

### 2️⃣ 系統設計書與 API 規格文件

**✅ 高度一致（96%）**

- API 端點完全對應
- Request/Response Schema 一致
- 領域事件定義一致
- 業務邏輯完整對應

**差異主要是：**
- API 規格文件更詳細（錯誤碼、範例、完整說明）
- **這是預期的文件深化，完全一致**

### 3️⃣ 合約文件與其他文件

**❌ 嚴重脫節（18%）**

**問題清單：**

1. ❌ **定義了不存在的欄位**：
   - `is_deleted = 0`（需求、設計、實作都沒有）
   - `department_id` in User（User 模型不直接有此欄位）

2. ❌ **完全沒有 Command 操作的業務場景**：
   - 沒有建立使用者場景
   - 沒有停用使用者場景
   - 沒有修改密碼場景
   - ...

3. ❌ **完全沒有領域事件定義**：
   - 15 個領域事件完全沒提
   - 無法驗證 Event-Driven 架構

4. ❌ **沒有 API 端點對應**：
   - 無法追溯到具體的 API
   - 測試無法對應到實際端點

5. ❌ **只有查詢過濾條件**：
   - 只定義了 Query 操作的必須過濾條件
   - 缺少完整的業務場景描述

---

## 🎯 核心問題

**合約文件的定位錯誤：**

- ❌ **現狀**：合約文件只是「查詢過濾條件的檢查清單」
- ✅ **應該**：合約文件應該是「業務場景與領域事件的完整定義」

**正確的合約文件應該包含：**

1. **Command 操作的業務場景**：
   - 建立使用者：輸入 → 驗證 → 領域事件 → 輸出
   - 停用使用者：前置條件 → 業務規則 → 領域事件
   - ...

2. **Query 操作的業務場景**：
   - 查詢使用者列表：權限 → 過濾條件 → 排序 → 分頁
   - ...

3. **領域事件定義**：
   - UserCreatedEvent: 何時觸發、Schema、訂閱者
   - ...

4. **API 端點對應**：
   - 每個場景對應到具體的 API 端點
   - ...

---

## 💡 建議

### ✅ 需求分析書與系統設計書

**無需修正**，兩者高度一致且合理。

### ⚠️ 合約文件

**需要完全重建**，建議：

1. **從系統設計書提取業務場景**
2. **從系統設計書提取領域事件**
3. **從 API 規格文件對應 API 端點**
4. **移除不存在的欄位要求**（is_deleted, department_id）
5. **補充 Command 操作的業務場景**
6. **補充領域事件的驗證規格**

### 📋 下一步行動

1. ✅ **確認**：系統設計書與需求分析書一致，可以作為開發依據
2. ❌ **暫停**：所有基於現有合約文件的測試修正工作
3. 🔄 **重建**：根據系統設計書和 API 規格文件重建合約文件
4. ✅ **重新測試**：基於新的合約文件重新建立合約測試
