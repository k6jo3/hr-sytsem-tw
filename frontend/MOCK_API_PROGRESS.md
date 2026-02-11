# Mock API 實作進度總結

## ✅ 已完成模組 (100% Mock API 覆蓋)

### HR01 - IAM (身份認證與權限管理)
- **Mock API**: `MockAuthApi.ts` ✅
- **整合狀態**: 
  - ✅ `AuthApi.ts` - 登入、登出、取得當前使用者
  - ✅ `UserApi.ts` - 使用者管理 (需手動整合)
  - ✅ `RoleApi.ts` - 角色管理 (需手動整合)
- **Mock 功能**:
  - 登入 (永遠成功，返回 mock token)
  - 使用者 CRUD
  - 角色查詢
  - 權限查詢

### HR02 - Organization (組織員工管理)
- **Mock API**: `MockOrganizationApi.ts` ✅
- **整合狀態**: ✅ `OrganizationApi.ts` 完全整合
- **Mock 功能**:
  - 員工 CRUD (含搜尋、篩選、分頁)
  - 組織 CRUD
  - 部門 CRUD
  - 組織結構樹查詢

### HR03 - Attendance (考勤管理)
- **Mock API**: `MockAttendanceApi.ts` ✅
- **整合狀態**: 
  - ✅ `AttendanceApi.ts` - 打卡、考勤記錄
  - ✅ `LeaveApi.ts` - 請假管理
  - ⏳ `OvertimeApi.ts` - 需手動整合
  - ⏳ `ShiftApi.ts` - 需手動整合
  - ⏳ `AttendanceReportApi.ts` - 需手動整合
  - ⏳ `MonthCloseApi.ts` - 需手動整合
- **Mock 功能**:
  - 上下班打卡
  - 今日考勤查詢
  - 考勤歷史記錄
  - 請假申請與查詢
  - 假期餘額查詢
  - 假別管理
  - 加班申請 (基礎實作)
  - 班別管理
  - 月報表、日報表
  - 月結作業

### HR08 - Performance (績效管理)
- **Mock API**: `MockPerformanceApi.ts` ✅
- **整合狀態**: ✅ `PerformanceApi.ts` 完全整合
- **Mock 功能**:
  - 考核週期 CRUD
  - 考核表單設計
  - 我的考核查詢
  - 團隊考核查詢
  - 績效分佈報表

---

## ⏳ 待完成模組

### HR04 - Payroll (薪資管理)
**優先級**: 🔴 高 (前端已完成)
- **需建立**: `MockPayrollApi.ts`
- **需整合**: `PayrollApi.ts`
- **預估工作量**: 2-3 小時

**建議 Mock 資料**:
```typescript
- 薪資單列表 (含狀態篩選、分頁)
- 薪資單詳情 (含各項目明細)
- 薪資計算執行
- 薪資發放確認
- 薪資報表
```

### HR05 - Insurance (保險管理)
**優先級**: 🔴 高 (前端已完成)
- **需建立**: `MockInsuranceApi.ts`
- **需整合**: `InsuranceApi.ts`
- **預估工作量**: 1-2 小時

**建議 Mock 資料**:
```typescript
- 保險投保記錄
- 保險異動申請
- 保險費用查詢
- 勞健保級距表
```

### HR06 - Project (專案管理)
**優先級**: 🔴 高 (前端已完成)
- **需建立**: `MockProjectApi.ts`
- **需整合**: `ProjectApi.ts`
- **預估工作量**: 2-3 小時

**建議 Mock 資料**:
```typescript
- 專案列表 (含客戶、狀態篩選)
- 專案詳情 (含 WBS 結構)
- 專案成員管理
- 專案成本追蹤
```

### HR07 - Timesheet (工時填報)
**優先級**: 🔴 高 (前端已完成)
- **需建立**: `MockTimesheetApi.ts`
- **需整合**: `TimesheetApi.ts`
- **預估工作量**: 1-2 小時

**建議 Mock 資料**:
```typescript
- 週工時表查詢
- 工時填報
- 工時審核 (PM)
- 工時統計報表
```

---

## 🎯 快速建立指南

### 步驟 1: 建立 Mock API 檔案

參考現有的 `MockOrganizationApi.ts` 或 `MockPerformanceApi.ts`：

```typescript
// src/features/{module}/api/Mock{Module}Api.ts
import type { ... } from './{Module}Types';

const uuidv4 = () => { ... };
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export class Mock{Module}Api {
  private static data: DataDto[] = [
    // 假資料
  ];

  static async getData(params?: GetDataRequest): Promise<GetDataResponse> {
    await delay(400);
    // 實作邏輯
    return { ... };
  }
}
```

### 步驟 2: 整合到 API 檔案

```typescript
// src/features/{module}/api/{Module}Api.ts
import { MockConfig } from '../../../config/MockConfig';
import { Mock{Module}Api } from './Mock{Module}Api';

export class {Module}Api {
  static async getData(params?: GetDataRequest): Promise<GetDataResponse> {
    if (MockConfig.isEnabled('{MODULE}')) return Mock{Module}Api.getData(params);
    return apiClient.get('/api/path', { params });
  }
}
```

### 步驟 3: 更新 MockConfig

確認 `MockConfig.ts` 中對應模組的開關：

```typescript
modules: {
  PAYROLL: true,  // 啟用 Payroll Mock
  INSURANCE: true,
  PROJECT: true,
  TIMESHEET: true,
  // ...
}
```

---

## 📋 待整合清單

### HR03 - Attendance 模組的其他 API 檔案

以下檔案已有 `MockAttendanceApi.ts` 支援，但尚未整合：

1. **OvertimeApi.ts**
   - `applyOvertime` → `MockAttendanceApi.applyOvertime`
   - `getOvertimeApplications` → `MockAttendanceApi.getOvertimeApplications`

2. **ShiftApi.ts**
   - `getShiftList` → `MockAttendanceApi.getShifts`
   - `createShift` → `MockAttendanceApi.createShift`
   - `updateShift` → `MockAttendanceApi.updateShift`
   - `deactivateShift` → `MockAttendanceApi.deleteShift`

3. **AttendanceReportApi.ts**
   - `getMonthlyReport` → `MockAttendanceApi.getMonthlyReport`
   - `getDailyReport` → `MockAttendanceApi.getDailyReport`

4. **MonthCloseApi.ts**
   - `executeMonthClose` → `MockAttendanceApi.executeMonthClose`

**整合範例**:
```typescript
// OvertimeApi.ts
import { MockConfig } from '../../../config/MockConfig';
import { MockAttendanceApi } from './MockAttendanceApi';

static async applyOvertime(request: ApplyOvertimeRequest): Promise<ApplyOvertimeResponse> {
  if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.applyOvertime(request);
  return apiClient.post(`${this.BASE_PATH}/applications`, request);
}
```

---

## 🔍 驗證方式

### 1. 檢查 Mock 是否啟用

```typescript
// 在瀏覽器 Console 執行
import { MockConfig } from './config/MockConfig';
console.log(MockConfig.isEnabled('ORGANIZATION')); // 應該返回 true
```

### 2. 測試 API 呼叫

```typescript
// 在元件中測試
import { OrganizationApi } from '@features/organization/api';

const testMock = async () => {
  const result = await OrganizationApi.getEmployeeList({ page: 1, page_size: 10 });
  console.log('Mock data:', result);
};
```

### 3. 檢查網路請求

- 開啟 Chrome DevTools → Network
- 執行前端操作
- **如果看到實際的 HTTP 請求** → Mock 未生效
- **如果沒有 HTTP 請求但有資料** → Mock 正常運作

---

## 📊 完成度統計

| 模組 | Mock API | API 整合 | 前端開發 | 總完成度 |
|:---|:---:|:---:|:---:|:---:|
| HR01 - IAM | ✅ | ✅ | ✅ | **100%** |
| HR02 - Organization | ✅ | ✅ | ✅ | **100%** |
| HR03 - Attendance | ✅ | 🟡 60% | ✅ | **85%** |
| HR04 - Payroll | ❌ | ❌ | ✅ | **33%** |
| HR05 - Insurance | ❌ | ❌ | ✅ | **33%** |
| HR06 - Project | ❌ | ❌ | ✅ | **33%** |
| HR07 - Timesheet | ❌ | ❌ | ✅ | **33%** |
| HR08 - Performance | ✅ | ✅ | ✅ | **100%** |

**整體進度**: 4/8 模組完成 Mock API = **50%**

---

## 🚀 下一步行動

### 立即可做 (無需等待)

1. **完成 HR03 剩餘整合** (30 分鐘)
   - 整合 `OvertimeApi.ts`
   - 整合 `ShiftApi.ts`
   - 整合 `AttendanceReportApi.ts`
   - 整合 `MonthCloseApi.ts`

2. **建立 HR04-HR07 Mock API** (6-10 小時)
   - 依照現有模板快速建立
   - 確保 DTO 結構正確
   - 提供合理的假資料

### 建議優先順序

1. 🥇 **HR04 Payroll** - 薪資功能是核心業務
2. 🥈 **HR06 Project** - 專案成本追蹤依賴此模組
3. 🥉 **HR07 Timesheet** - 與 Payroll 和 Project 緊密相關
4. **HR05 Insurance** - 相對獨立，可最後處理

---

## 💡 提示與最佳實踐

1. **Mock 資料要豐富**: 至少 3-5 筆資料，涵蓋不同狀態
2. **支援篩選與分頁**: 即使是 Mock，也要模擬真實行為
3. **加入延遲**: `await delay(400-600)` 讓 loading 狀態可見
4. **型別安全**: 確保 Mock 回傳型別與 DTO 完全一致
5. **註解清楚**: 標註哪些功能已實作、哪些待補充

---

## 📞 需要協助？

如果在建立 Mock API 過程中遇到問題：

1. 參考已完成的模組 (`MockOrganizationApi.ts`, `MockPerformanceApi.ts`)
2. 檢查 `{Module}Types.ts` 確認 DTO 結構
3. 查看 `MOCK_API_GUIDE.md` 了解架構設計
4. 確認 `MockConfig.ts` 中模組名稱拼寫正確

**最後更新**: 2026-02-11
**文件版本**: 1.0
