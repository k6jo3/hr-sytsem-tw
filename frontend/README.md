# HR System 3.0 - Frontend Architecture

## 📋 Project Structure

```
frontend/
├── src/
│   ├── features/              # 功能模組 (14個領域)
│   │   ├── auth/              # HR01 - 身份認證
│   │   ├── organization/      # HR02 - 組織員工
│   │   ├── attendance/        # HR03 - 考勤管理
│   │   ├── payroll/           # HR04 - 薪資管理
│   │   ├── insurance/         # HR05 - 保險管理
│   │   ├── project/           # HR06 - 專案管理
│   │   ├── timesheet/         # HR07 - 工時管理
│   │   ├── performance/       # HR08 - 績效管理
│   │   ├── recruitment/       # HR09 - 招募管理
│   │   ├── training/          # HR10 - 訓練管理
│   │   ├── workflow/          # HR11 - 簽核流程
│   │   ├── notification/      # HR12 - 通知服務
│   │   ├── document/          # HR13 - 文件管理
│   │   └── report/            # HR14 - 報表分析
│   │
│   ├── pages/                 # 頁面元件 (HR{DD}-P{XX})
│   │   ├── HR01LoginPage.tsx
│   │   ├── HR02EmployeeListPage.tsx
│   │   └── ...
│   │
│   ├── shared/                # 共用模組
│   │   ├── api/               # API Client
│   │   ├── components/        # 共用元件
│   │   ├── hooks/             # 共用 Hooks
│   │   ├── utils/             # 工具函式
│   │   └── constants/         # 常數定義
│   │
│   ├── store/                 # Redux Store
│   │   ├── authSlice.ts
│   │   ├── organizationSlice.ts
│   │   └── ...
│   │
│   ├── App.tsx                # 主應用程式
│   └── main.tsx               # 入口點
│
├── .env.development           # 開發環境變數
├── .env.production            # 生產環境變數
├── .env.example               # 環境變數範例
├── package.json
├── tsconfig.json              # TypeScript 配置
├── vite.config.ts             # Vite 配置
└── README.md                  # 本文件
```

## 🏗️ Feature Module Structure

每個 Feature 模組遵循相同的結構：

```
features/{feature}/
├── api/                       # API 呼叫
│   ├── {Feature}Api.ts        # API 方法
│   ├── {Feature}Types.ts      # DTO 類型定義
│   └── index.ts
├── factory/                   # ViewModel Factory
│   └── {Feature}ViewModelFactory.ts
├── hooks/                     # 自訂 Hooks
│   ├── use{Feature}.ts
│   └── index.ts
├── components/                # 元件
│   └── index.ts
├── model/                     # 前端 Domain Model
│   └── {Feature}ViewModel.ts
└── index.ts                   # Feature 匯出
```

## 🎯 Key Concepts

### 1. Factory Pattern (MANDATORY)

**Never use raw API data directly in components!**

```typescript
// ❌ WRONG
const { data } = useQuery(...);
return <div>{data.first_name}</div>;

// ✅ CORRECT
const { data } = useQuery(...);
const viewModel = UserViewModelFactory.createFromDTO(data);
return <div>{viewModel.fullName}</div>;
```

### 2. Feature-Based Architecture

- Each domain has its own feature module
- Self-contained: API, Factory, Hooks, Components, Models
- Clear separation of concerns

### 3. Path Aliases

```typescript
import { AuthApi } from '@features/auth';
import { PageLayout } from '@shared/components';
import { useDebounce } from '@shared/hooks';
import { HR01LoginPage } from '@pages/HR01LoginPage';
import { store } from '@store/index';
```

## 🚀 Getting Started

### Installation

```bash
npm install
```

### Development

```bash
npm run dev
```

### Build

```bash
npm run build
```

### Test

```bash
npm test
```

## 📝 Naming Conventions

### Pages

- Format: `HR{DD}{PageName}Page.tsx`
- Examples:
  - `HR01LoginPage.tsx` - Login page
  - `HR02EmployeeListPage.tsx` - Employee list page
  - `HR06ProjectDetailPage.tsx` - Project detail page

### Components

- Format: `PascalCase`
- Examples: `EmployeeCard.tsx`, `LoginForm.tsx`

### Hooks

- Format: `use{Name}.ts`
- Examples: `useLogin.ts`, `useEmployees.ts`

### Factories

- Format: `{Name}ViewModelFactory.ts`
- Examples: `UserViewModelFactory.ts`, `EmployeeViewModelFactory.ts`

## 🗺️ Routes

| Path | Page | Feature |
|:---|:---|:---|
| `/login` | HR01LoginPage | auth |
| `/employees` | HR02EmployeeListPage | organization |
| `/attendance/check-in` | HR03CheckInPage | attendance |
| `/attendance/leaves` | HR03LeaveListPage | attendance |
| `/payroll` | HR04PayrollListPage | payroll |
| `/insurance` | HR05InsuranceListPage | insurance |
| `/projects` | HR06ProjectListPage | project |
| `/timesheet` | HR07TimesheetPage | timesheet |
| `/performance` | HR08PerformanceListPage | performance |
| `/recruitment` | HR09RecruitmentPage | recruitment |
| `/training` | HR10TrainingListPage | training |
| `/workflow` | HR11WorkflowListPage | workflow |
| `/notifications` | HR12NotificationPage | notification |
| `/documents` | HR13DocumentListPage | document |
| `/reports` | HR14ReportDashboardPage | report |

## 🧩 Shared Components

### PageLayout

Provides consistent layout with sidebar navigation:

```typescript
import { PageLayout } from '@shared/components';

export const MyPage: React.FC = () => {
  return (
    <PageLayout>
      <div>Page content here</div>
    </PageLayout>
  );
};
```

### Loading

Unified loading indicator:

```typescript
import { Loading } from '@shared/components';

<Loading tip="載入中..." size="large" />
```

### ErrorMessage

Consistent error display:

```typescript
import { ErrorMessage } from '@shared/components';

<ErrorMessage message="發生錯誤" description="詳細錯誤訊息" />
```

## 🪝 Shared Hooks

### useDebounce

Debounce value changes:

```typescript
import { useDebounce } from '@shared/hooks';

const [searchTerm, setSearchTerm] = useState('');
const debouncedSearchTerm = useDebounce(searchTerm, 500);
```

### useLocalStorage

Sync state with localStorage:

```typescript
import { useLocalStorage } from '@shared/hooks';

const [theme, setTheme] = useLocalStorage('theme', 'light');
```

### useAsync

Handle async operations:

```typescript
import { useAsync } from '@shared/hooks';

const { loading, error, data, execute } = useAsync<User>();

const loadUser = async () => {
  await execute(() => UserApi.getUser(id));
};
```

## 🔧 Technology Stack

- **React** 18.x - UI Library
- **TypeScript** 5.x - Type Safety
- **Vite** 5.x - Build Tool
- **Redux Toolkit** - State Management
- **Ant Design** 5.x - UI Components
- **React Router** 6.x - Routing
- **Axios** - HTTP Client
- **Vitest** - Testing

## 📚 Documentation

For complete architectural documentation, see:
- `架構說明與開發規範.md` - Development guidelines
- `../CLAUDE.md` - AI assistant guide
- `../spec/系統架構設計文件.md` - System architecture

## 🎓 Development Guidelines

1. **Always use Factory Pattern** for API data transformation
2. **Follow naming conventions** strictly
3. **Use path aliases** for imports
4. **Write tests** for factories and hooks
5. **Use TypeScript** - avoid `any` type
6. **Document with TSDoc** for exported functions
7. **Keep components small** and focused

---

**Version:** 3.0
**Last Updated:** 2025-12-08
