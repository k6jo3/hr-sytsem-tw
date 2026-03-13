# 前端系統架構與開發規範書 (Strict Specification)

> **重要:** 本文件為開發人員的快速參考指南。
> 完整架構設計請參閱 [系統架構設計文件](../architecture/系統架構設計文件.md)
> 命名規範詳細說明請參閱 [系統架構設計文件_命名規範](../architecture/系統架構設計文件_命名規範.md)

---

## 1. 系統概述 (System Overview)

本專案前端採用 **React 18 + TypeScript + Vite**，架構設計採用 **Feature-Based Architecture**。
核心目標：**元件即文件**、**類型即合約**。

---

## 2. 技術堆疊 (Technology Stack)

| 類別 | 技術 | 版本 |
|:---|:---|:---|
| **框架** | React | 18.x |
| **語言** | TypeScript | 5.x |
| **建構工具** | Vite | 5.x |
| **狀態管理** | Redux Toolkit | - |
| **UI元件庫** | Ant Design | 5.x |
| **路由** | React Router | 6.x |
| **HTTP Client** | Axios | - |
| **測試** | Vitest + RTL | - |

---

## 3. Domain代號對照表

| 代號 | Domain | 前端 Feature | 說明 |
|:---:|:---|:---|:---|
| `01` | IAM | `auth` | 身份認證與授權 |
| `02` | ORG | `organization` | 組織員工管理 |
| `03` | ATT | `attendance` | 考勤管理 |
| `04` | PAY | `payroll` | 薪資管理 |
| `05` | INS | `insurance` | 保險管理 |
| `06` | PRJ | `project` | 專案管理 |
| `07` | TMS | `timesheet` | 工時管理 |
| `08` | PFM | `performance` | 績效管理 |
| `09` | RCT | `recruitment` | 招募管理 |
| `10` | TRN | `training` | 訓練發展 |
| `11` | WFL | `workflow` | 簽核流程 |
| `12` | NTF | `notification` | 通知服務 |
| `13` | DOC | `document` | 文件管理 |
| `14` | RPT | `report` | 報表分析 |

---

## 4. 頁面代碼規範

### 4.1 頁面代碼格式

| 類型 | 格式 | 範例 |
|:---|:---|:---|
| **頁面** | `HR{DD}-P{XX}` | `HR01-P01` (登入頁) |
| **Modal** | `HR{DD}-M{XX}` | `HR01-M01` (使用者編輯對話框) |

### 4.2 頁面檔案命名

```
src/pages/
├── HR01LoginPage.tsx           # HR01-P01 登入頁
├── HR02EmployeeListPage.tsx    # HR02-P01 員工列表頁
├── HR02EmployeeDetailPage.tsx  # HR02-P02 員工詳情頁
├── HR03CheckInPage.tsx         # HR03-P01 打卡頁
└── HR04PayrollRunPage.tsx      # HR04-P03 薪資計算批次頁
```

---

## 5. Feature-Based 目錄結構

```
frontend/src/
├── features/                   # 按 Domain 分類
│   ├── auth/                   # Feature: IAM (HR01)
│   │   ├── api/
│   │   │   ├── AuthApi.ts      # API 呼叫封裝
│   │   │   └── AuthTypes.ts    # Request/Response DTOs
│   │   ├── components/
│   │   │   └── LoginForm.tsx
│   │   ├── factory/            # ★ Factory Pattern
│   │   │   └── AuthViewModelFactory.ts
│   │   ├── hooks/
│   │   │   └── useLogin.ts
│   │   └── model/
│   │       └── UserProfile.ts  # 前端 Domain Model
│   │
│   ├── organization/           # Feature: ORG (HR02)
│   ├── attendance/             # Feature: ATT (HR03)
│   ├── payroll/                # Feature: PAY (HR04)
│   └── ...
│
├── pages/                      # 頁面入口 (Route entries)
│   ├── HR01LoginPage.tsx
│   ├── HR02EmployeeListPage.tsx
│   └── ...
│
├── shared/                     # Shared Kernel
│   ├── components/             # 共用 UI 元件
│   ├── factory/                # 共用 Factories
│   ├── hooks/                  # 共用 Hooks
│   └── utils/                  # 工具函式
│
├── App.tsx
└── main.tsx
```

---

## 6. 命名規範

### 6.1 檔案命名

| 類型 | 命名格式 | 範例 |
|:---|:---|:---|
| **頁面** | `HR{DD}{PageName}Page.tsx` | `HR01LoginPage.tsx` |
| **元件** | `PascalCase` | `EmployeeCard.tsx` |
| **Hook** | `use{Name}.ts` | `useLogin.ts` |
| **Factory** | `{Name}Factory.ts` | `UserViewModelFactory.ts` |
| **API** | `{Feature}Api.ts` | `AuthApi.ts` |
| **Types** | `{Feature}Types.ts` | `AuthTypes.ts` |

### 6.2 元件與函式命名

| 類型 | 命名格式 | 範例 |
|:---|:---|:---|
| **React Component** | PascalCase | `EmployeeCard` |
| **Hook** | camelCase + use prefix | `useEmployeeList` |
| **Factory Method** | static + createFromDTO | `UserFactory.createFromDTO()` |
| **Event Handler** | handle + Action | `handleSubmit` |
| **Boolean** | is/has/can prefix | `isLoading`, `hasError` |

---

## 7. 設計模式應用

### 7.1 Factory Pattern (強制)

**禁止** 在 Component 內直接使用 API 回傳的原始資料，**必須** 透過 Factory 轉換。

```typescript
// ❌ 錯誤：直接使用 API 資料
const UserList = () => {
  const { data } = useQuery(...);
  return <div>{data.first_name} {data.last_name}</div>;
}

// ✅ 正確：透過 Factory 轉換
const UserList = () => {
  const { data } = useQuery(...);
  const viewModel = UserViewModelFactory.createFromDTO(data);
  return <div>{viewModel.fullName}</div>;
}
```

### 7.2 Factory 範例

```typescript
/**
 * Factory to create UserViewModel from API response.
 */
export class UserViewModelFactory {
  static createFromDTO(dto: UserDto): UserViewModel {
    return {
      id: dto.id,
      fullName: `${dto.first_name} ${dto.last_name}`,
      isAdmin: dto.role_list.includes('ADMIN'),
      displayStatus: dto.status === 'ACTIVE' ? '在職' : '離職',
    };
  }
}
```

---

## 8. API Client 規範 (apiClient / Axios)

### 8.1 401 攔截器

Axios 回應攔截器在收到 **401 Unauthorized** 時應自動導向登入頁，但登入 API 本身必須排除此行為，以便前端正確顯示「帳號或密碼錯誤」等伺服器端訊息：

```typescript
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 登入 API 本身不做重導向，讓呼叫端處理錯誤訊息
      if (!error.config.url?.includes('/auth/login')) {
        store.dispatch(logout());
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
```

### 8.2 伺服器錯誤訊息擷取

後端回傳的錯誤訊息統一放在 `error.response.data.message`，前端應從此路徑擷取並顯示給使用者：

```typescript
try {
  await AuthApi.login(credentials);
} catch (error) {
  if (axios.isAxiosError(error)) {
    const serverMessage = error.response?.data?.message || '系統錯誤，請稍後再試';
    message.error(serverMessage);
  }
}
```

---

## 9. API 轉接層模式 (Adapter Pattern)

### 9.1 後端短欄位名 vs 前端語義化欄位名

後端 API 回傳的欄位通常使用短名稱（如 `code`、`name`、`type`），而前端 ViewModel 則使用帶有前綴的語義化名稱（如 `organizationCode`、`organizationName`、`organizationType`）。**必須** 透過 Adapter 函式進行雙向映射：

```typescript
// API DTO（後端格式）
interface OrganizationDTO {
  id: string;
  code: string;
  name: string;
  type: string;
}

// 前端 ViewModel（語義化格式）
interface OrganizationViewModel {
  id: string;
  organizationCode: string;
  organizationName: string;
  organizationType: string;
}

// Adapter：DTO → ViewModel
function toOrganizationViewModel(dto: OrganizationDTO): OrganizationViewModel {
  return {
    id: dto.id,
    organizationCode: dto.code,
    organizationName: dto.name,
    organizationType: dto.type,
  };
}

// Adapter：ViewModel → Request DTO（送回後端時）
function toOrganizationRequest(vm: OrganizationViewModel): Partial<OrganizationDTO> {
  return {
    code: vm.organizationCode,
    name: vm.organizationName,
    type: vm.organizationType,
  };
}
```

### 9.2 Adapter 放置位置

Adapter 函式應放在各 Feature 的 `factory/` 目錄或 `api/` 目錄中，與 Factory 模式搭配使用：

```
features/{domain}/
├── api/
│   ├── {Feature}Api.ts         # API 呼叫
│   └── {Feature}Adapter.ts     # ★ 欄位映射 Adapter
├── factory/
│   └── {Name}ViewModelFactory.ts  # 可在此整合 Adapter
```

---

## 10. 測試規範

### 10.1 測試類型

| 類型 | 工具 | 強制測試範圍 |
|:---|:---|:---|
| **Unit Test** | Vitest | `factory/`, `utils/`, `hooks/` |
| **Component Test** | RTL | 關鍵 UI 互動 |

### 10.2 TDD 範例

**1. 測試 (UserViewModelFactory.test.ts):**
```typescript
import { describe, it, expect } from 'vitest';
import { UserViewModelFactory } from './UserViewModelFactory';

describe('UserViewModelFactory', () => {
  it('should transform API DTO to ViewModel correctly', () => {
    const dto = { 
      id: '1', 
      first_name: 'John', 
      last_name: 'Doe', 
      role_list: ['ADMIN'] 
    };
    const viewModel = UserViewModelFactory.createFromDTO(dto);
    
    expect(viewModel.fullName).toBe('John Doe');
    expect(viewModel.isAdmin).toBe(true);
  });
});
```

**2. 實作 (UserViewModelFactory.ts):**
```typescript
export class UserViewModelFactory {
  static createFromDTO(dto: UserDto): UserViewModel {
    return {
      id: dto.id,
      fullName: `${dto.first_name} ${dto.last_name}`,
      isAdmin: dto.role_list.includes('ADMIN'),
    };
  }
}
```

---

## 11. 狀態管理 (Redux)

### 11.1 State 結構

```typescript
interface RootState {
  auth: AuthState;
  organization: OrganizationState;
  attendance: AttendanceState;
  payroll: PayrollState;
  // ... 按 Feature 組織
}
```

### 11.2 Slice 命名

```
src/store/
├── authSlice.ts
├── organizationSlice.ts
├── attendanceSlice.ts
└── ...
```

---

## 12. TypeScript 規範

### 12.1 Type 定義

```typescript
// ✅ 正確：明確定義 Interface
interface EmployeeCardProps {
  employee: EmployeeViewModel;
  onEdit: (id: string) => void;
  isEditable?: boolean;
}

// ❌ 錯誤：使用 any
const EmployeeCard = (props: any) => { ... }
```

### 12.2 TSDoc 規範

所有 Export 的 Function、Factory、Interface **必須** 包含 TSDoc：

```typescript
/**
 * 將 API 回傳的使用者 DTO 轉換為前端 ViewModel
 * @param dto - API 回傳的原始資料
 * @returns 前端顯示用的 ViewModel
 */
export function createUserViewModel(dto: UserDto): UserViewModel {
  // ...
}
```

---

**文件版本:** 2.1
**最後更新:** 2026-03-13
