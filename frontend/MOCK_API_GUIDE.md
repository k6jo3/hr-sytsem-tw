# Mock API 架構說明

## 📋 概述

本系統已全面導入 **Mock API 機制**，讓前端開發完全不受後端進度影響。透過統一的 `MockConfig` 設定檔，可以輕鬆切換真實 API 與 Mock API。

## 🎯 設計理念

### 防腐層 (Anti-Corruption Layer)

```
UI Components
    ↓
Hooks (useCycles, useUsers...)
    ↓
API Layer (PerformanceApi, AuthApi...)
    ↓
MockConfig.isEnabled('MODULE') ?
    ├─ TRUE  → MockPerformanceApi (假資料)
    └─ FALSE → apiClient (真實後端)
    ↓
ViewModelFactory (DTO → ViewModel)
    ↓
UI Components
```

### 核心優勢

1. **前後端解耦**：前端開發不需等待後端 API 完成
2. **合約測試**：Mock 資料即為前端對後端的「契約 (Contract)」
3. **無痛切換**：後端完成後，只需修改 `MockConfig` 開關
4. **易於維護**：若後端欄位變更，只需調整 `ViewModelFactory`，UI 元件完全不受影響

## 📁 檔案結構

```
frontend/src/
├── config/
│   └── MockConfig.ts          # 統一的 Mock 開關設定
│
└── features/
    ├── auth/                   # IAM 模組 (HR01)
    │   └── api/
    │       ├── AuthTypes.ts    # DTO 定義
    │       ├── AuthApi.ts      # API 類別 (整合 Mock)
    │       └── MockAuthApi.ts  # Mock 實作
    │
    ├── performance/            # 績效模組 (HR08)
    │   └── api/
    │       ├── PerformanceTypes.ts
    │       ├── PerformanceApi.ts
    │       └── MockPerformanceApi.ts
    │
    └── ... (其他模組)
```

## 🔧 使用方式

### 1. 統一開關設定 (`src/config/MockConfig.ts`)

```typescript
export const MockConfig = {
  // 主開關：若為 true，所有模組預設使用 Mock
  USE_MOCK_ALL: true, 

  // 個別模組開關 (可覆寫主開關)
  modules: {
    AUTH: true,
    ORGANIZATION: true,
    ATTENDANCE: true,
    PAYROLL: true,
    INSURANCE: true,
    PROJECT: true,
    TIMESHEET: true,
    PERFORMANCE: true,
    RECRUITMENT: true,
    TRAINING: true,
    WORKFLOW: true,
    NOTIFICATION: true,
    DOCUMENT: true,
    REPORT: true,
  },

  isEnabled(moduleName: string): boolean {
    return this.USE_MOCK_ALL || (this.modules as any)[moduleName];
  }
};
```

### 2. API 類別整合 Mock

```typescript
// src/features/performance/api/PerformanceApi.ts
import { MockConfig } from '../../../config/MockConfig';
import { MockPerformanceApi } from './MockPerformanceApi';

export class PerformanceApi {
  static async getCycles(params: GetCyclesRequest): Promise<GetCyclesResponse> {
    if (MockConfig.isEnabled('PERFORMANCE')) {
      return MockPerformanceApi.getCycles(params);
    }
    return apiClient.get<GetCyclesResponse>(`/performance/cycles`, { params });
  }
}
```

### 3. Mock API 實作

```typescript
// src/features/performance/api/MockPerformanceApi.ts
export class MockPerformanceApi {
  private static cycles: PerformanceCycleDto[] = [
    {
      cycle_id: 'c001',
      cycle_name: '2024年度績效考核',
      cycle_type: 'ANNUAL',
      status: 'IN_PROGRESS',
      // ... 完整的假資料
    }
  ];

  static async getCycles(params: GetCyclesRequest): Promise<GetCyclesResponse> {
    await delay(500); // 模擬網路延遲
    return {
      cycles: this.cycles,
      total: this.cycles.length
    };
  }
}
```

## ✅ 已完成模組

| 模組 | Mock API | 整合狀態 | 說明 |
|:---|:---:|:---:|:---|
| **Performance (HR08)** | ✅ | ✅ | 完整實作週期管理、表單設計、團隊考核、報表 |
| **Auth (HR01)** | ✅ | ✅ | 登入、使用者管理、角色權限 |

## 🔄 切換至真實後端

當後端 API 完成後，只需修改 `MockConfig.ts`：

```typescript
export const MockConfig = {
  USE_MOCK_ALL: false,  // 改為 false
  
  // 或針對特定模組關閉
  modules: {
    PERFORMANCE: false,  // 績效模組已完成，使用真實 API
    AUTH: true,          // IAM 模組尚未完成，繼續使用 Mock
    // ...
  }
};
```

## 🛠️ 後端對接注意事項

### 1. DTO 結構必須一致

後端回傳的 JSON 結構必須符合 `{Feature}Types.ts` 定義的 DTO：

```typescript
// PerformanceTypes.ts
export interface PerformanceCycleDto {
  cycle_id: string;
  cycle_name: string;
  cycle_type: CycleType;
  status: CycleStatus;
  start_date: string;
  end_date: string;
  // ...
}
```

### 2. 若後端欄位不同

若後端實際回傳的欄位名稱與前端定義不同，**只需修改 `ViewModelFactory`**：

```typescript
// PerformanceViewModelFactory.ts
export class PerformanceViewModelFactory {
  static createCycleViewModel(dto: PerformanceCycleDto): PerformanceCycleViewModel {
    return {
      cycleId: dto.cycle_id,           // 後端: cycle_id
      cycleName: dto.cycle_name,       // 後端: cycle_name
      // 若後端改為 cycleStatus，只需改這裡：
      status: dto.status,              // 後端: status
      // ...
    };
  }
}
```

**UI 元件完全不需修改**，因為它們只依賴 `ViewModel`。

## 📝 待辦事項

- [ ] Organization (HR02) - Mock API
- [ ] Attendance (HR03) - Mock API
- [ ] Payroll (HR04) - Mock API
- [ ] Insurance (HR05) - Mock API
- [ ] Project (HR06) - Mock API
- [ ] Timesheet (HR07) - Mock API
- [ ] Recruitment (HR09) - Mock API
- [ ] Training (HR10) - Mock API
- [ ] Workflow (HR11) - Mock API
- [ ] Notification (HR12) - Mock API
- [ ] Document (HR13) - Mock API
- [ ] Report (HR14) - Mock API

## 🎓 最佳實踐

1. **Mock 資料要完整**：包含所有必填欄位，模擬真實場景
2. **加入延遲**：使用 `delay()` 模擬網路延遲，讓 loading 狀態可見
3. **支援 CRUD**：Mock API 應支援增刪改查，讓前端功能完整測試
4. **型別安全**：Mock API 回傳型別必須與 `{Feature}Types.ts` 一致
5. **合理的假資料**：使用符合業務邏輯的假資料，方便測試各種狀態

## 🔗 相關文件

- `framework/architecture/03_Business_Pipeline.md` - 業務流水線設計
- `framework/development/04_Frontend_Guide.md` - 前端開發指南
- `frontend/TEST_SUMMARY.md` - TDD 測試摘要
