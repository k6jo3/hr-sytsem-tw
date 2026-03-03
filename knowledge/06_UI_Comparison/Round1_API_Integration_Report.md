# Round 1 API 串接測試報告

**日期：** 2026-03-03
**範圍：** HR01 IAM + HR02 Organization

---

## 1. 後端服務啟動狀態

| 服務 | Port | Profile | 狀態 |
|:---|:---:|:---|:---:|
| hrms-iam | 8081 | local (H2 + embedded Redis) | ✅ 啟動成功 |
| hrms-organization | 8082 | local (H2) | ✅ 啟動成功（修復後） |

---

## 2. API 端點測試結果

### 2.1 IAM 服務 (port 8081)

| API | Method | Path | 狀態 | 說明 |
|:---|:---|:---|:---:|:---|
| 登入 | POST | `/api/v1/auth/login` | ✅ | 返回 JWT + user info |
| 使用者列表 | GET | `/api/v1/users` | ❌ | QueryEngine bug: `eq(null) is not allowed` |
| 角色列表 | GET | `/api/v1/roles` | ⚠️ | 未測試（依賴 users 同樣的 QueryEngine） |
| 權限列表 | GET | `/api/v1/permissions` | ⚠️ | 未測試 |

**IAM Bug：** `UltimateQueryEngine` 處理 null 值時呼叫 `.eq(null)` 而非 `.isNull()`，導致 500 錯誤。

### 2.2 Organization 服務 (port 8082)

| API | Method | Path | 狀態 | 說明 |
|:---|:---|:---|:---:|:---|
| 員工列表 | GET | `/api/v1/employees` | ✅ | 返回 4 名員工 |
| 員工詳情 | GET | `/api/v1/employees/:id` | ✅ | 含部門、身分證遮罩 |
| 部門列表 | GET | `/api/v1/departments` | ✅ | 返回 5 個部門 |

---

## 3. 修復的後端問題

### 3.1 data-local.sql UUID 格式 (已修復)

**問題：** Domain Value Object (`EmployeeId`, `DepartmentId`, `OrganizationId`) 內部使用 `UUID.fromString()`，但 seed data 用字串 ID 如 `emp-0001`。

**修復：** 將所有 ID 改為標準 UUID 格式：
- `emp-0001` → `00000000-0000-0000-0000-000000000001`
- `org-0001` → `10000000-0000-0000-0000-000000000001`
- `dept-0001` → `20000000-0000-0000-0000-000000000001`

### 3.2 NationalId 校驗碼 (已修復)

**問題：** `NationalId` 值物件有完整的台灣身分證校驗邏輯，測試資料 `B234567890`、`C345678901`、`D456789012` 不通過。

**修復：** 計算有效的測試用身分證號：
- `B234567890` → `B200000004` (校驗通過)
- `C345678901` → `C100000003` (格式+校驗通過)
- `D456789012` → `D100000022` (格式+校驗通過)

---

## 4. 前後端格式不匹配分析（關鍵發現）

### 4.1 命名風格差異

前端 DTO 使用 **snake_case**（為 Mock API 設計），後端回應使用 **camelCase**（Spring Boot 預設）。

#### Auth 模組

| 前端欄位 (snake_case) | 後端欄位 (camelCase) | 說明 |
|:---|:---|:---|
| `access_token` | `accessToken` | 登入回應 |
| `refresh_token` | `refreshToken` | 登入回應 |
| `expires_in` | `expiresIn` | 登入回應 |
| `user.id` | `user.userId` | 使用者 ID 名稱不同 |
| `user.display_name` | `user.displayName` | 顯示名稱 |
| `user.role_list` | `user.roles` | 角色欄位名不同 |
| `user.must_change_password` | 不存在 | 後端未返回 |

#### Organization 模組 — Employee

| 前端 EmployeeDto | 後端 Response | 說明 |
|:---|:---|:---|
| `id` | `employeeId` | ID 名稱不同 |
| `employee_number` | `employeeNumber` | snake vs camel |
| `first_name` + `last_name` | `fullName` | 後端只有全名，無拆分 |
| `department_id` | `departmentId` | snake vs camel |
| `department_name` | `departmentName` | snake vs camel |
| `position` | 不存在 (`jobTitle` in detail) | 欄位缺失 |
| `status` (ACTIVE/INACTIVE/ON_LEAVE/TERMINATED) | `employmentStatus` (ACTIVE/PROBATION/...) | 枚舉值不同 |
| `hire_date` | `hireDate` | snake vs camel |
| `created_at` / `updated_at` | 不存在 (列表) | 列表不返回審計欄位 |

#### Organization 模組 — Department ✅ 匹配

前端 DepartmentDto 已使用 camelCase，與後端格式一致。

### 4.2 回應結構差異

#### Employee List Response

```
前端期望:                    後端實際返回:
{                            {
  employees: [...],            items: [...],        // ❌ 欄位名不同
  total: 10,                   total: 4,
  page: 1,                     page: 1,
  page_size: 20                size: 20             // ❌ 欄位名不同
}                              totalPages: 1        // 前端無此欄位
                             }
```

---

## 5. 需要的修改才能完成串接

### 5.1 高優先級

1. **前端 Response Adapter** — 在 `OrganizationApi.ts` 的真實 API 分支中，將 camelCase 回應轉換為 snake_case DTO
2. **前端 Auth Adapter** — 在 `AuthApi.ts` 將 `accessToken` → `access_token` 等
3. **修復 IAM QueryEngine** — `UltimateQueryEngine` 需處理 null 值用 `.isNull()` 而非 `.eq(null)`

### 5.2 中優先級

4. **Employee 欄位對齊** — 後端 employee list response 需加入 `firstName`, `lastName`, `jobTitle` 等欄位
5. **Status 枚舉對齊** — 前端 `ON_LEAVE`/`TERMINATED` vs 後端 `PARENTAL_LEAVE`/`UNPAID_LEAVE`/`TERMINATED`

### 5.3 建議方案

推薦在前端 API 層建立 **Response Adapter** 模式：

```typescript
// OrganizationApi.ts
getEmployeeList: async (params) => {
  if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getEmployeeList(params);

  // 真實 API 回傳 camelCase → 轉換為前端 snake_case DTO
  const response = await apiClient.get('/employees', { params });
  return {
    employees: response.items.map(adaptEmployeeResponse),
    total: response.total,
    page: response.page,
    page_size: response.size,
  };
}
```

---

## 6. 總結

| 項目 | 結果 |
|:---|:---:|
| 後端 IAM 登入 | ✅ 可用 |
| 後端 IAM 使用者管理 | ❌ QueryEngine bug |
| 後端 ORG 員工管理 | ✅ 可用 |
| 後端 ORG 部門管理 | ✅ 可用 |
| 前後端格式匹配 | ❌ 需要 Response Adapter |
| Seed Data 品質 | ⚠️ 已修復（UUID + NationalId） |

**結論：** 後端 API 基本功能正常，但前端直接切換為真實 API 需要先實作 Response Adapter 層來處理命名風格差異。建議先在前端 API 模組中建立適配器，再逐模組關閉 Mock。
