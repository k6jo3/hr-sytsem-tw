# 前端啟動與 API 切換指南

**日期**: 2026-02-11 11:17  
**版本**: 1.0

---

## ✅ 是的！前端已可獨立啟動

### 當前狀態

✅ **所有功能都已準備就緒**:
- 49 個頁面元件已建立
- 14 個模組的 Mock API 已完成
- 可以完全獨立於後端運行

---

## 🚀 啟動前端

### 1. 安裝依賴

```bash
cd frontend
npm install
```

### 2. 啟動開發伺服器

```bash
npm run dev
```

前端會在 `http://localhost:5173` 啟動（Vite 預設端口）

### 3. 查看所有頁面

所有 49 個頁面都可以訪問：

#### 核心模組頁面
- **HR01 - IAM**
  - `/login` - 登入頁面
  - `/password-change` - 密碼變更
  - `/role-management` - 角色管理
  - `/user-management` - 使用者管理

- **HR02 - Organization**
  - `/employees` - 員工列表
  - `/employees/:id` - 員工詳情
  - `/organization-tree` - 組織樹

- **HR03 - Attendance**
  - `/attendance/check-in` - 打卡
  - `/attendance/my-list` - 我的考勤
  - `/attendance/leave` - 請假管理
  - `/attendance/leave-balance` - 假期餘額
  - `/attendance/overtime` - 加班管理
  - `/attendance/approval` - 審批列表
  - `/attendance/shift` - 班表管理
  - `/attendance/leave-types` - 假別管理
  - `/attendance/report` - 考勤報表
  - `/attendance/month-close` - 月結

- **HR04 - Payroll**
  - `/payroll/my-payslips` - 我的薪資單
  - `/payroll/payslip/:id` - 薪資單詳情
  - `/payroll/list` - 薪資列表
  - `/payroll/batch/:id` - 批次詳情
  - `/payroll/approval` - 薪資審核
  - `/payroll/history` - 歷史記錄
  - `/payroll/salary-structure` - 薪資結構
  - `/payroll/items` - 薪資項目
  - `/payroll/bank-transfer` - 銀行轉帳

- **HR05 - Insurance**
  - `/insurance/my` - 我的保險
  - `/insurance/enrollment` - 加退保管理
  - `/insurance/calculator` - 保費計算器

- **HR06 - Project**
  - `/projects` - 專案列表
  - `/projects/:id` - 專案詳情
  - `/projects/:id/edit` - 編輯專案
  - `/projects/:id/tasks` - 專案任務
  - `/customers` - 客戶管理

- **HR07 - Timesheet**
  - `/timesheet` - 工時填報
  - `/timesheet/approval` - 工時審核
  - `/timesheet/report` - 工時報表

- **HR08 - Performance**
  - `/performance/my` - 我的績效
  - `/performance/team` - 團隊績效
  - `/performance/cycles` - 週期管理
  - `/performance/templates` - 範本設計
  - `/performance/report` - 績效報表

- **HR09 - Recruitment**
  - `/recruitment` - 招募看板

#### 支援模組頁面
- **HR10 - Training**
  - `/training` - 訓練列表

- **HR11 - Workflow**
  - `/workflow` - 流程列表

- **HR12 - Notification**
  - `/notifications` - 通知中心

- **HR13 - Document**
  - `/documents` - 文件列表

- **HR14 - Reporting**
  - `/reports/dashboard` - 報表儀表板

---

## 🔄 API 切換機制

### 當前設定 (使用 Mock API)

檔案位置: `frontend/src/config/MockConfig.ts`

```typescript
export const MockConfig = {
  // 主開關：true = 使用 Mock API
  USE_MOCK_ALL: true,  // ✅ 目前設定為 true
  
  // 個別模組開關
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
};
```

### 切換到真實後端 API

#### 方式 1: 全部切換 (建議)

```typescript
export const MockConfig = {
  USE_MOCK_ALL: false,  // ❌ 改為 false
  modules: { ... }
};
```

**一行改動，所有模組都切換到真實 API！**

#### 方式 2: 個別模組切換

```typescript
export const MockConfig = {
  USE_MOCK_ALL: false,
  modules: {
    AUTH: false,           // ❌ 使用真實 API
    ORGANIZATION: false,   // ❌ 使用真實 API
    ATTENDANCE: true,      // ✅ 仍使用 Mock API
    PAYROLL: true,         // ✅ 仍使用 Mock API
    // ...
  },
};
```

**可以逐步切換，方便測試！**

---

## 🔧 後端 API 設定

### API Base URL 設定

檔案位置: `frontend/src/shared/api/apiClient.ts`

```typescript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
```

### 環境變數設定

建立 `.env.development` 檔案：

```bash
# 開發環境 - 本地後端
VITE_API_BASE_URL=http://localhost:8080/api/v1

# 或使用遠端後端
# VITE_API_BASE_URL=https://api.example.com/api/v1
```

建立 `.env.production` 檔案：

```bash
# 生產環境
VITE_API_BASE_URL=https://api.production.com/api/v1
```

---

## 📋 切換流程

### 當後端準備好時

1. **確認後端 API 已啟動**
   ```bash
   # 後端應該在 http://localhost:8080 運行
   curl http://localhost:8080/api/v1/health
   ```

2. **設定環境變數** (可選)
   ```bash
   # .env.development
   VITE_API_BASE_URL=http://localhost:8080/api/v1
   ```

3. **切換 Mock 設定**
   ```typescript
   // frontend/src/config/MockConfig.ts
   export const MockConfig = {
     USE_MOCK_ALL: false,  // 改為 false
     // ...
   };
   ```

4. **重新啟動前端**
   ```bash
   # 停止前端 (Ctrl+C)
   # 重新啟動
   npm run dev
   ```

5. **驗證切換成功**
   - 打開瀏覽器開發者工具 (F12)
   - 查看 Network 標籤
   - 應該看到真實的 API 請求到 `http://localhost:8080/api/v1/*`

---

## 🎯 混合模式 (部分 Mock + 部分真實)

### 使用場景

當後端只完成部分模組時，可以混合使用：

```typescript
export const MockConfig = {
  USE_MOCK_ALL: false,  // 關閉全域 Mock
  modules: {
    AUTH: false,           // ✅ 後端已完成，使用真實 API
    ORGANIZATION: false,   // ✅ 後端已完成，使用真實 API
    ATTENDANCE: true,      // ❌ 後端未完成，使用 Mock API
    PAYROLL: true,         // ❌ 後端未完成，使用 Mock API
    INSURANCE: true,       // ❌ 後端未完成，使用 Mock API
    PROJECT: true,         // ❌ 後端未完成，使用 Mock API
    TIMESHEET: true,       // ❌ 後端未完成，使用 Mock API
    PERFORMANCE: true,     // ❌ 後端未完成，使用 Mock API
    RECRUITMENT: true,     // ❌ 後端未完成，使用 Mock API
    TRAINING: true,        // ❌ 後端未完成，使用 Mock API
    WORKFLOW: true,        // ❌ 後端未完成，使用 Mock API
    NOTIFICATION: true,    // ❌ 後端未完成，使用 Mock API
    DOCUMENT: true,        // ❌ 後端未完成，使用 Mock API
    REPORT: true,          // ❌ 後端未完成，使用 Mock API
  },
};
```

**這樣可以逐步整合後端 API，降低風險！**

---

## 🐛 除錯技巧

### 確認當前使用的 API

在瀏覽器 Console 中執行：

```javascript
// 檢查 Mock 設定
console.log('Mock Config:', MockConfig);

// 檢查 API Base URL
console.log('API Base URL:', import.meta.env.VITE_API_BASE_URL);
```

### 查看 API 請求

1. 打開瀏覽器開發者工具 (F12)
2. 切換到 Network 標籤
3. 執行操作 (例如登入)
4. 查看請求：
   - **Mock API**: 不會有真實的網路請求
   - **真實 API**: 會看到 `http://localhost:8080/api/v1/*` 的請求

### 常見問題

#### 問題 1: CORS 錯誤

**症狀**: 瀏覽器 Console 顯示 CORS 錯誤

**解決方案**: 後端需要設定 CORS

```java
// Spring Boot 後端設定
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true);
            }
        };
    }
}
```

#### 問題 2: 401 未授權

**症狀**: API 回傳 401 錯誤

**解決方案**: 確認 JWT Token 設定

```typescript
// 檢查 apiClient.ts 是否正確設定 Authorization header
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

---

## 📊 總結

### ✅ 當前狀態

- **前端**: 可獨立啟動，使用 Mock API
- **所有頁面**: 49 個頁面都可訪問
- **所有功能**: 14 個模組都可使用

### 🔄 切換步驟

1. 後端啟動
2. 修改 `MockConfig.ts` 中的 `USE_MOCK_ALL` 為 `false`
3. 重新啟動前端
4. 完成！

### 🎯 優勢

- ✅ **前端不受阻**: 可以獨立開發
- ✅ **一鍵切換**: 只需改一行設定
- ✅ **混合模式**: 支援部分 Mock + 部分真實
- ✅ **易於除錯**: 清楚知道使用哪種 API
- ✅ **降低風險**: 逐步整合後端

---

**最後更新**: 2026-02-11 11:17  
**作者**: AI Assistant  
**狀態**: ✅ 前端已可獨立啟動
