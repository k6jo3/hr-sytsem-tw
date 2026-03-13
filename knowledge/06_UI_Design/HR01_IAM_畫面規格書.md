# HR01 IAM 服務畫面規格書

**文件編號：** UI-HR01
**版本：** 1.1
**更新日期：** 2026-03-13
**參照文件：**
- `knowledge/02_System_Design/01_IAM服務系統設計書.md`
- `knowledge/04_API_Specifications/01_IAM服務系統設計書_API詳細規格.md`
- `00_UI設計總覽與風格規範.md`

---

## 1. 模組總覽

| 項目 | 說明 |
|:---|:---|
| **服務代碼** | HR01 |
| **服務名稱** | IAM（Identity & Access Management） |
| **功能範疇** | 身分認證、使用者管理、角色權限、密碼管理 |
| **使用角色** | 全部角色（登入）、ADMIN（使用者與角色管理） |
| **頁面數量** | 4 頁 + 2 Modal |
| **前端 Feature** | `frontend/src/features/auth/` |

---

## 2. 頁面清單

| 代碼 | 頁面名稱 | 路由 | 頁面類型 | 使用角色 |
|:---|:---|:---|:---|:---|
| HR01-P01 | 登入頁 | `/login` | 表單頁 | ALL |
| HR01-P02 | 使用者管理 | `/admin/users` | 列表頁 | ADMIN |
| HR01-P03 | 角色權限分配 | `/admin/roles` | 列表頁 | ADMIN |
| HR01-P04 | 修改密碼 | `/profile/password` | 表單頁 | ALL（已登入） |
| HR01-M01 | 使用者新增/編輯 | Modal | 表單 Modal | ADMIN |
| HR01-M02 | 角色新增/編輯 | Modal | 表單 Modal | ADMIN |

---

## 3. HR01-P01 登入頁

### 3.1 頁面概述

| 項目 | 說明 |
|:---|:---|
| **用途** | 使用者身分認證入口，支援帳密登入與 SSO |
| **使用角色** | 所有未登入使用者 |
| **進入方式** | 直接存取 `/login`、未登入被重導 |
| **頁面類型** | 獨立全螢幕表單頁（不含 PageLayout） |

### 3.2 Wireframe

```
┌──────────────────────────────────────────────────────┐
│                                                      │
│        漸層背景 (#667eea → #764ba2)                   │
│                                                      │
│           ┌────────────────────────┐                  │
│           │                        │                  │
│           │    HR System 3.0       │                  │
│           │    ─────────────       │                  │
│           │                        │                  │
│           │  帳號                   │                  │
│           │  ┌──────────────────┐  │                  │
│           │  │ 請輸入帳號       │  │                  │
│           │  └──────────────────┘  │                  │
│           │                        │                  │
│           │  密碼                   │                  │
│           │  ┌──────────────────┐  │                  │
│           │  │ 請輸入密碼     👁 │  │                  │
│           │  └──────────────────┘  │                  │
│           │                        │                  │
│           │  ☐ 記住我              │                  │
│           │                        │                  │
│           │  ┌──────────────────┐  │                  │
│           │  │     登  入       │  │                  │
│           │  └──────────────────┘  │                  │
│           │                        │                  │
│           │  忘記密碼？            │                  │
│           │                        │                  │
│           │  ── 或 ──             │                  │
│           │                        │                  │
│           │  [Google SSO] [Azure]  │                  │
│           │                        │                  │
│           └────────────────────────┘                  │
│                                                      │
└──────────────────────────────────────────────────────┘
```

### 3.3 區域說明

| 區域 | 說明 | 元件 |
|:---|:---|:---|
| 背景 | 全螢幕漸層 `#667eea → #764ba2` | CSS linear-gradient |
| 登入卡片 | 置中白色卡片，圓角 12px，陰影-3 | `Card` |
| 標題 | 系統名稱 `HR System 3.0`，主色 | `Typography.Title` |
| 表單 | 帳號、密碼、記住我、登入按鈕 | `Form` + `Input` + `Checkbox` + `Button` |
| 輔助連結 | 忘記密碼 | `Button type="link"` |
| SSO 區塊 | 第三方登入按鈕 | `Divider` + `Button` |

### 3.4 資料欄位規格

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---|:---|:---|
| 帳號 (username) | `Input` | ✅ | 不可為空 | Email 或員工編號 |
| 密碼 (password) | `Input.Password` | ✅ | 不可為空，最少 8 字元 | 支援顯示/隱藏切換 |
| 記住我 (rememberMe) | `Checkbox` | — | — | 延長 Token 有效期 |

### 3.5 操作事件規格

| 事件 ID | 觸發元素 | 行為 | 對應 API | UI 反應 |
|:---|:---|:---|:---|:---|
| E-LOGIN-01 | 登入按鈕 | 提交帳密驗證 | `POST /api/v1/auth/login` | 成功：導向 `/dashboard`；失敗：顯示錯誤訊息 |
| E-LOGIN-02 | 忘記密碼連結 | 開啟忘記密碼 Modal | — | 彈出 ForgotPasswordModal |
| E-LOGIN-03 | Google SSO 按鈕 | OAuth2 導向 | `GET /api/v1/auth/oauth2/google` | 跳轉 Google 授權頁 |
| E-LOGIN-04 | Azure SSO 按鈕 | OAuth2 導向 | `GET /api/v1/auth/oauth2/azure` | 跳轉 Azure 授權頁 |

### 3.6 狀態與互動

| 狀態 | 顯示方式 |
|:---|:---|
| 初始 | 帳號欄位 autofocus |
| 驗證中 | 登入按鈕 `loading` 態 |
| 驗證失敗 | `message.error('帳號或密碼錯誤')` |
| 帳號鎖定 | `message.error('帳號已鎖定，請30分鐘後重試')` |
| 連線失敗 | `message.error('網路連線異常，請稍後重試')` |

### 3.7 前端元件結構

| 元件 | 檔案路徑 |
|:---|:---|
| Page | `frontend/src/pages/HR01LoginPage.tsx` |
| LoginForm | `frontend/src/features/auth/components/LoginForm.tsx` |
| ForgotPasswordModal | `frontend/src/features/auth/components/ForgotPasswordModal.tsx` |
| useLogin Hook | `frontend/src/features/auth/hooks/useLogin.ts` |
| AuthApi | `frontend/src/features/auth/api/AuthApi.ts` |
| AuthTypes | `frontend/src/features/auth/api/AuthTypes.ts` |

---

## 4. HR01-P02 使用者管理

### 4.1 頁面概述

| 項目 | 說明 |
|:---|:---|
| **用途** | 管理系統使用者帳號：查詢、新增、編輯、啟用/停用 |
| **使用角色** | ADMIN |
| **進入方式** | 側邊選單 → 帳號與權限 → 使用者管理 |
| **頁面類型** | 列表頁（SearchForm + Table + Pagination） |

### 4.2 Wireframe

```
┌──────────────────────────────────────────────────────┐
│ 使用者管理                            [+ 新增使用者]  │
├──────────────────────────────────────────────────────┤
│ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐  │
│ │ 使用者名稱   │ │ Email        │ │ 狀態     [▼] │  │
│ │ [________]   │ │ [________]   │ │ [全部    ]   │  │
│ └──────────────┘ └──────────────┘ └──────────────┘  │
│                              [搜尋] [重置]           │
├──────────────────────────────────────────────────────┤
│ ┌────┬──────┬─────────┬──────┬──────┬──────┬─────┐  │
│ │ #  │ 帳號 │ Email   │ 角色 │ 狀態 │ 建立 │ 操作│  │
│ ├────┼──────┼─────────┼──────┼──────┼──────┼─────┤  │
│ │ 1  │admin │a@co.com │ADMIN │ 啟用 │01-01 │編輯 │  │
│ │ 2  │user1 │u@co.com │USER  │ 啟用 │01-05 │停用 │  │
│ │ 3  │user2 │u2@co.com│HR    │ 停用 │01-10 │啟用 │  │
│ └────┴──────┴─────────┴──────┴──────┴──────┴─────┘  │
├──────────────────────────────────────────────────────┤
│                              第 1-20 筆，共 50 筆    │
└──────────────────────────────────────────────────────┘
```

### 4.3 搜尋/篩選條件

| 條件 | 欄位類型 | 選項來源 | 預設值 | 對應 API 參數 |
|:---|:---|:---|:---|:---|
| 使用者名稱 | `Input` | — | — | `username` (LIKE) |
| Email | `Input` | — | — | `email` (LIKE) |
| 狀態 | `Select` | ACTIVE/INACTIVE | 全部 | `status` (EQ) |

### 4.4 資料欄位規格

| 欄位 | API 來源 | 顯示格式 | 寬度 | 排序 |
|:---|:---|:---|:---|:---|
| 序號 | — | 自動編號 | 60px | — |
| 帳號 | `username` | 原始值 | 150px | ✅ |
| Email | `email` | 原始值 | 200px | ✅ |
| 角色 | `roles[]` | Tag 列表 | 200px | — |
| 狀態 | `status` | `StatusTag type="active"` | 100px | ✅ |
| 建立時間 | `createdAt` | `YYYY-MM-DD` | 120px | ✅ |
| 操作 | — | 按鈕群組 | 150px | — |

### 4.5 操作事件規格

| 事件 ID | 觸發元素 | 行為 | 對應 API | UI 反應 |
|:---|:---|:---|:---|:---|
| E-USER-01 | 新增使用者按鈕 | 開啟 HR01-M01 (create) | — | 彈出 Modal |
| E-USER-02 | 搜尋按鈕 | 查詢使用者列表 | `GET /api/v1/users` | 重新載入表格 |
| E-USER-03 | 重置按鈕 | 清空搜尋條件 | — | 清空表單，重新查詢 |
| E-USER-04 | 編輯按鈕 | 開啟 HR01-M01 (edit) | `GET /api/v1/users/{id}` | 彈出 Modal，載入資料 |
| E-USER-05 | 停用按鈕 | 停用使用者 | `PUT /api/v1/users/{id}/deactivate` | Modal.confirm → 成功 → 重新載入 |
| E-USER-06 | 啟用按鈕 | 啟用使用者 | `PUT /api/v1/users/{id}/activate` | Modal.confirm → 成功 → 重新載入 |

### 4.6 前端元件結構

| 元件 | 檔案路徑 |
|:---|:---|
| Page | `frontend/src/pages/HR01UserManagementPage.tsx` |
| UserTable | `frontend/src/features/auth/components/UserTable.tsx` |
| UserFormModal | `frontend/src/features/auth/components/UserFormModal.tsx` |
| useUserManagement Hook | `frontend/src/features/auth/hooks/useUserManagement.ts` |
| UserApi | `frontend/src/features/auth/api/UserApi.ts` |
| UserViewModelFactory | `frontend/src/features/auth/factory/UserViewModelFactory.ts` |

---

## 5. HR01-P03 角色權限分配

### 5.1 頁面概述

| 項目 | 說明 |
|:---|:---|
| **用途** | 管理系統角色定義、分配功能權限 |
| **使用角色** | ADMIN |
| **進入方式** | 側邊選單 → 帳號與權限 → 角色權限分配 |
| **頁面類型** | 列表頁 + 權限樹配置 |

### 5.2 Wireframe

```
┌──────────────────────────────────────────────────────┐
│ 角色權限分配                          [+ 新增角色]    │
├──────────────────────────────────────────────────────┤
│ ┌──────────────────────────┐ ┌──────────────────────┐│
│ │ 角色列表                 │ │ 權限配置             ││
│ │ ┌──────────────────────┐ │ │                      ││
│ │ │ 🔒 系統管理員 (3人)  │ │ │ ☑ 帳號與權限        ││
│ │ │ ADMIN                │ │ │   ☑ 使用者管理      ││
│ │ └──────────────────────┘ │ │   ☑ 角色管理        ││
│ │ ┌──────────────────────┐ │ │ ☑ 組織與員工        ││
│ │ │ 👤 HR 管理員 (5人)   │ │ │   ☑ 部門管理        ││
│ │ │ HR                   │ │ │   ☑ 員工管理        ││
│ │ └──────────────────────┘ │ │ ☐ 薪資核算          ││
│ │ ┌──────────────────────┐ │ │ ☐ 保險管理          ││
│ │ │ 👤 一般員工 (50人)   │ │ │                      ││
│ │ │ EMPLOYEE             │ │ │        [儲存變更]    ││
│ │ └──────────────────────┘ │ │                      ││
│ └──────────────────────────┘ └──────────────────────┘│
└──────────────────────────────────────────────────────┘
```

### 5.3 區域說明

| 區域 | 說明 | 元件 |
|:---|:---|:---|
| 角色列表（左側） | 卡片式角色列表，點選切換 | `Card` + `List` |
| 權限配置（右側） | 功能權限樹狀勾選 | `Tree` (checkable) |

### 5.4 操作事件規格

| 事件 ID | 觸發元素 | 行為 | 對應 API | UI 反應 |
|:---|:---|:---|:---|:---|
| E-ROLE-01 | 新增角色按鈕 | 開啟 HR01-M02 (create) | — | 彈出 Modal |
| E-ROLE-02 | 角色卡片點擊 | 載入該角色權限 | `GET /api/v1/roles/{id}/permissions` | 右側樹狀更新 |
| E-ROLE-03 | 編輯按鈕 | 開啟 HR01-M02 (edit) | `GET /api/v1/roles/{id}` | 彈出 Modal |
| E-ROLE-04 | 刪除按鈕 | 刪除角色 | `DELETE /api/v1/roles/{id}` | Modal.confirm |
| E-ROLE-05 | 權限勾選 | 選取/取消權限 | — | 前端即時更新 |
| E-ROLE-06 | 儲存變更按鈕 | 儲存權限配置 | `PUT /api/v1/roles/{id}/permissions` | message.success |

### 5.5 前端元件結構

| 元件 | 檔案路徑 |
|:---|:---|
| Page | `frontend/src/pages/HR01RoleManagementPage.tsx` |
| RoleCard | `frontend/src/features/auth/components/RoleCard.tsx` |
| RoleFormModal | `frontend/src/features/auth/components/RoleFormModal.tsx` |
| PermissionTree | `frontend/src/features/auth/components/PermissionTree.tsx` |
| useRoleManagement Hook | `frontend/src/features/auth/hooks/useRoleManagement.ts` |
| RoleApi | `frontend/src/features/auth/api/RoleApi.ts` |

---

## 6. HR01-P04 修改密碼

### 6.1 頁面概述

| 項目 | 說明 |
|:---|:---|
| **用途** | 已登入使用者修改自身密碼 |
| **使用角色** | ALL（已登入） |
| **進入方式** | Header 使用者 Dropdown → 修改密碼 |
| **頁面類型** | 簡單表單頁 |

### 6.2 Wireframe

```
┌──────────────────────────────────────────────────────┐
│ 修改密碼                                              │
├──────────────────────────────────────────────────────┤
│                                                      │
│   ┌────────────────────────────────────┐              │
│   │  目前密碼                          │              │
│   │  ┌────────────────────────────┐    │              │
│   │  │ ••••••••              👁  │    │              │
│   │  └────────────────────────────┘    │              │
│   │                                    │              │
│   │  新密碼                            │              │
│   │  ┌────────────────────────────┐    │              │
│   │  │                        👁  │    │              │
│   │  └────────────────────────────┘    │              │
│   │  密碼強度：████░░░░ 中等            │              │
│   │                                    │              │
│   │  確認新密碼                        │              │
│   │  ┌────────────────────────────┐    │              │
│   │  │                        👁  │    │              │
│   │  └────────────────────────────┘    │              │
│   │                                    │              │
│   │              [取消]  [確認修改]     │              │
│   └────────────────────────────────────┘              │
│                                                      │
└──────────────────────────────────────────────────────┘
```

### 6.3 資料欄位規格

| 欄位 | 類型 | 必填 | 驗證規則 |
|:---|:---|:---|:---|
| 目前密碼 | `Input.Password` | ✅ | 不可為空 |
| 新密碼 | `Input.Password` | ✅ | 至少 8 字元，含大小寫英文和數字 |
| 確認新密碼 | `Input.Password` | ✅ | 必須與新密碼一致 |

### 6.4 操作事件規格

| 事件 ID | 觸發元素 | 行為 | 對應 API | UI 反應 |
|:---|:---|:---|:---|:---|
| E-PWD-01 | 確認修改按鈕 | 提交密碼修改 | `PUT /api/v1/users/me/password` | 成功：message.success + 登出重新登入 |
| E-PWD-02 | 取消按鈕 | 返回上一頁 | — | `navigate(-1)` |

### 6.5 前端元件結構

| 元件 | 檔案路徑 |
|:---|:---|
| Page | `frontend/src/pages/HR01PasswordChangePage.tsx` |
| usePasswordChange Hook | `frontend/src/features/auth/hooks/usePasswordChange.ts` |
| AuthApi | `frontend/src/features/auth/api/AuthApi.ts` |

---

## 7. HR01-M01 使用者新增/編輯 Modal

### 7.1 Wireframe

```
┌──────────────────────────────────────┐
│ 新增使用者 / 編輯使用者          [✕]  │
├──────────────────────────────────────┤
│                                      │
│  使用者名稱 *                        │
│  ┌──────────────────────────────┐    │
│  │                              │    │
│  └──────────────────────────────┘    │
│                                      │
│  Email *                             │
│  ┌──────────────────────────────┐    │
│  │                              │    │
│  └──────────────────────────────┘    │
│                                      │
│  關聯員工                            │
│  ┌──────────────────────────────┐    │
│  │ 搜尋員工...              ▼  │    │
│  └──────────────────────────────┘    │
│                                      │
│  指派角色 *                          │
│  ┌──────────────────────────────┐    │
│  │ ☑ ADMIN  ☐ HR  ☐ EMPLOYEE  │    │
│  └──────────────────────────────┘    │
│                                      │
├──────────────────────────────────────┤
│                    [取消]  [確認]     │
└──────────────────────────────────────┘
```

### 7.2 資料欄位規格

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 |
|:---|:---|:---|:---|:---|
| 使用者名稱 | `Input` | ✅ | 不可為空，唯一性 | 登入帳號 |
| Email | `Input` | ✅ | Email 格式驗證 | 通知信箱 |
| 關聯員工 | `Select` (搜尋) | — | 需存在於 Organization 服務 | 下拉搜尋員工 |
| 指派角色 | `Checkbox.Group` | ✅ | 至少選一個 | 多選角色 |

### 7.3 操作事件規格

| 事件 ID | 觸發元素 | 行為 | 對應 API |
|:---|:---|:---|:---|
| E-USER-M-01 | 確認按鈕 (新增) | 建立使用者 | `POST /api/v1/users` |
| E-USER-M-02 | 確認按鈕 (編輯) | 更新使用者 | `PUT /api/v1/users/{id}` |
| E-USER-M-03 | 員工搜尋 | 搜尋員工下拉 | `GET /api/v1/employees?keyword={keyword}` |

---

## 8. HR01-M02 角色新增/編輯 Modal

### 8.1 Wireframe

```
┌──────────────────────────────────────┐
│ 新增角色 / 編輯角色              [✕]  │
├──────────────────────────────────────┤
│                                      │
│  角色代碼 *                          │
│  ┌──────────────────────────────┐    │
│  │ 例：ADMIN                    │    │
│  └──────────────────────────────┘    │
│                                      │
│  角色名稱 *                          │
│  ┌──────────────────────────────┐    │
│  │ 例：系統管理員               │    │
│  └──────────────────────────────┘    │
│                                      │
│  說明                                │
│  ┌──────────────────────────────┐    │
│  │                              │    │
│  │                              │    │
│  └──────────────────────────────┘    │
│                                      │
├──────────────────────────────────────┤
│                    [取消]  [確認]     │
└──────────────────────────────────────┘
```

### 8.2 操作事件規格

| 事件 ID | 觸發元素 | 行為 | 對應 API |
|:---|:---|:---|:---|
| E-ROLE-M-01 | 確認按鈕 (新增) | 建立角色 | `POST /api/v1/roles` |
| E-ROLE-M-02 | 確認按鈕 (編輯) | 更新角色 | `PUT /api/v1/roles/{id}` |

---

## 9. 系統管理 - 排程管理 Tab（ScheduledJobTab）

### 9.1 Tab 概述

| 項目 | 說明 |
|:---|:---|
| **用途** | 管理排程任務的啟停控制、查看執行狀態與錯誤詳情 |
| **使用角色** | ADMIN |
| **進入方式** | 側邊選單 → 系統管理 → 排程管理 Tab |
| **頁面類型** | 列表頁（Table + Modal） |

### 9.2 資料欄位規格

| 欄位 | API 來源 | 顯示格式 | 說明 |
|:---|:---|:---|:---|
| 任務代碼 | `jobCode` | 原始值 | — |
| 任務名稱 | `jobName` | 原始值 | — |
| 所屬模組 | `module` | Tag | — |
| Cron 表達式 | `cronExpression` | 原始值 | — |
| 啟用狀態 | `enabled` | `StatusTag` | — |
| 最近執行時間 | `lastExecutedAt` | `YYYY-MM-DD HH:mm:ss` | — |
| 執行狀態 | `lastExecutionStatus` | Tag（SUCCESS=綠/FAILED=紅/RUNNING=藍） | — |
| 需關注 | `consecutiveFailures` | `Tag color="warning"`：顯示「需關注」 | 當 `consecutiveFailures > 0` 時顯示警告標籤 |
| 查看錯誤 | `lastErrorMessage` | `Tag` 可點擊：顯示「查看錯誤」 | 當 `lastErrorMessage` 存在時顯示，點擊開啟錯誤詳情 Modal |
| **操作** | — | 啟用/停用 Toggle | 見下方操作欄位規格 |

### 9.3 操作欄位規格

「操作」欄位提供排程任務的啟用/停用切換功能：

| 項目 | 說明 |
|:---|:---|
| **元件** | `Switch` 或 `Button`，搭配 `Popconfirm` 二次確認 |
| **啟用 → 停用** | Popconfirm 提示：`確定要停用此排程嗎？`，確認後呼叫 `PUT /api/v1/admin/scheduled-jobs/{code}/disable` |
| **停用 → 啟用** | Popconfirm 提示：`確定要啟用此排程嗎？`，確認後呼叫 `PUT /api/v1/admin/scheduled-jobs/{code}/enable` |
| **操作完成後** | 重新載入排程列表，顯示 `message.success` |

### 9.4 錯誤詳情 Modal

當使用者點擊「查看錯誤」標籤時，彈出錯誤詳情 Modal：

```
┌──────────────────────────────────────┐
│ 排程錯誤詳情                      [✕]  │
├──────────────────────────────────────┤
│                                      │
│  任務名稱：曠職自動判定              │
│  任務代碼：ABSENT_DETECTION          │
│                                      │
│  連續失敗次數：3                      │
│  ┌──────────────────────────────┐    │
│  │  3                           │    │
│  └──────────────────────────────┘    │
│                                      │
│  最近錯誤訊息：                      │
│  ┌──────────────────────────────┐    │
│  │ Connection refused: HR03     │    │
│  │ Attendance Service           │    │
│  │ unavailable                  │    │
│  └──────────────────────────────┘    │
│                                      │
├──────────────────────────────────────┤
│                           [關閉]     │
└──────────────────────────────────────┘
```

| 欄位 | 來源 | 說明 |
|:---|:---|:---|
| 任務名稱 | `jobName` | 排程任務顯示名稱 |
| 任務代碼 | `jobCode` | 排程任務唯一識別碼 |
| 連續失敗次數 | `consecutiveFailures` | 目前累計連續失敗次數 |
| 最近錯誤訊息 | `lastErrorMessage` | 最後一次執行失敗的完整錯誤訊息 |

### 9.5 操作事件規格

| 事件 ID | 觸發元素 | 行為 | 對應 API | UI 反應 |
|:---|:---|:---|:---|:---|
| E-SJOB-01 | 啟用/停用 Toggle | 切換排程啟停狀態 | `PUT /api/v1/admin/scheduled-jobs/{code}/enable` 或 `disable` | Popconfirm 確認 → 成功 → 重新載入列表 |
| E-SJOB-02 | 「需關注」Tag | 視覺提示 | — | 當 `consecutiveFailures > 0` 時以 warning 色標籤提示 |
| E-SJOB-03 | 「查看錯誤」Tag 點擊 | 開啟錯誤詳情 Modal | — | 彈出 Modal 顯示 `consecutiveFailures` 與 `lastErrorMessage` |

---

## 10. API 端點對照表

| 方法 | 端點 | 用途 | 對應畫面事件 |
|:---|:---|:---|:---|
| `POST` | `/api/v1/auth/login` | 使用者登入 | E-LOGIN-01 |
| `POST` | `/api/v1/auth/logout` | 使用者登出 | Header Dropdown |
| `POST` | `/api/v1/auth/refresh` | Token 更新 | 自動（Interceptor） |
| `GET` | `/api/v1/auth/oauth2/{provider}` | SSO 登入導向 | E-LOGIN-03/04 |
| `GET` | `/api/v1/users` | 查詢使用者列表 | E-USER-02 |
| `GET` | `/api/v1/users/{id}` | 取得使用者詳情 | E-USER-04 |
| `POST` | `/api/v1/users` | 新增使用者 | E-USER-M-01 |
| `PUT` | `/api/v1/users/{id}` | 更新使用者 | E-USER-M-02 |
| `PUT` | `/api/v1/users/{id}/activate` | 啟用使用者 | E-USER-06 |
| `PUT` | `/api/v1/users/{id}/deactivate` | 停用使用者 | E-USER-05 |
| `PUT` | `/api/v1/users/me/password` | 修改密碼 | E-PWD-01 |
| `GET` | `/api/v1/roles` | 查詢角色列表 | E-ROLE-02 |
| `GET` | `/api/v1/roles/{id}` | 取得角色詳情 | E-ROLE-03 |
| `POST` | `/api/v1/roles` | 新增角色 | E-ROLE-M-01 |
| `PUT` | `/api/v1/roles/{id}` | 更新角色 | E-ROLE-M-02 |
| `DELETE` | `/api/v1/roles/{id}` | 刪除角色 | E-ROLE-04 |
| `GET` | `/api/v1/roles/{id}/permissions` | 取得角色權限 | E-ROLE-02 |
| `PUT` | `/api/v1/roles/{id}/permissions` | 更新角色權限 | E-ROLE-06 |
