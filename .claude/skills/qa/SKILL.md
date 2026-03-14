# QA（品質保證）Skill

## 使用時機
進行品質保證驗證、測試計畫制定、Bug 追蹤與回歸測試時使用。
觸發關鍵字：`/qa`、「品質保證」、「QA 驗證」、「測試計畫」

> 最後更新：2026-03-14

---

## 執行流程

### Step 1: 確認 QA 範圍

```
1. 驗證目標：[ ] 單一功能 / [ ] 整個模組 / [ ] 跨模組流程
2. 驗證類型：[ ] 新功能驗收 / [ ] Bug 修復確認 / [ ] 回歸測試 / [ ] 上線前檢查
3. 涉及模組：{列出模組代碼，如 HR01, HR02}
```

### Step 2: 測試層級驗證

依照三階測試法，由下而上逐層驗證：

```
第三層：E2E / 整合測試（API 端點）
   ↑
第二層：合約測試 + Pipeline 單元測試
   ↑
第一層：Domain 單元測試 + Factory 單元測試
```

### Step 3: 執行驗證

---

## QA 驗證 Checklist

### 一、後端測試驗證

#### 1.1 單元測試
```bash
# 執行全後端測試
cd backend && export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.10.7-hotspot"
mvn clean test
```

- [ ] 所有測試通過（0 failures, 0 errors）
- [ ] Domain 邏輯 100% 覆蓋
- [ ] Task 單元測試覆蓋所有路徑（成功 + 失敗）

#### 1.2 合約測試
- [ ] 每個 API 端點都有合約測試
- [ ] 合約場景 ID 對應 `contracts/{service}_contracts.md`
- [ ] 合約文件 ↔ 測試程式 ↔ 實作三者一致
- [ ] HTTP 狀態碼正確（200/400/401/403/404/409/500）

#### 1.3 整合測試
- [ ] API 端點整合測試通過（MockMvc）
- [ ] 錯誤場景有測試（無效輸入、資源不存在、權限不足）
- [ ] GlobalExceptionHandler 每個 ErrorCode 都有測試

### 二、前端測試驗證

```bash
# 執行前端測試
cd frontend && npx vitest run
```

#### 2.1 Adapter 測試
- [ ] 每個 API 模組都有 adapter test
- [ ] 測試涵蓋：正常值、null、缺失欄位、未知 enum
- [ ] guardEnum 是否正確觸發 console.warn

#### 2.2 Factory 測試
- [ ] 每個 Factory 都有單元測試
- [ ] enum → 中文標籤映射完整
- [ ] 邊界值處理正確

#### 2.3 元件 / Hook 測試
- [ ] 有元件的模組都有元件測試
- [ ] Hook 測試覆蓋成功/失敗/loading 狀態

#### 2.4 Build 驗證
```bash
npx tsc --noEmit   # TypeScript 型別檢查
npx vite build     # 建構
```
- [ ] 0 TypeScript 錯誤
- [ ] Build 成功

### 三、三方一致性驗證

| 項目 | 檢查重點 |
|:---|:---|
| 後端 Response DTO | 欄位名稱、型別 |
| 合約 requiredFields | 名稱一致、必填欄位正確 |
| 前端 API Types | 欄位名稱、型別映射 |
| 前端 Adapter | 欄位 fallback chain 正確 |

- [ ] 三方欄位名稱一致（或有明確的 `frontendAdapterMapping`）
- [ ] 必填欄位確實有值（非 undefined）
- [ ] enum 值域三方一致

### 四、跨模組流程驗證

常見跨模組流程：

| 流程 | 涉及模組 | 驗證重點 |
|:---|:---|:---|
| 員工入職 | HR01 + HR02 + HR03 | 帳號建立 → 員工建立 → 考勤設定 |
| 請假審核 | HR03 + HR11 + HR12 | 申請 → 簽核 → 通知 |
| 薪資計算 | HR03 + HR04 + HR05 | 考勤 → 薪資 → 保險 |
| 考核週期 | HR08 + HR02 | 建立週期 → 指派員工 → 評分 |
| 專案工時 | HR06 + HR07 | 建立專案 → 填報工時 → 審核 |

- [ ] Domain Event 正確發布與接收
- [ ] 跨服務資料一致性

### 五、前後端串接驗證（E2E）

> 此階段需實際啟動後端服務 + 前端 Dev Server，用瀏覽器或 Playwright 操作驗證。
> 詳細操作流程參見 `/ui-api-roundtest` 和 `/env-setup`。

#### 5.1 環境準備
```bash
# 後端：啟動目標服務（參見 /env-setup 的服務啟動章節）
cd backend
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.10.7-hotspot"
mvn install -pl hrms-common -DskipTests
mvn spring-boot:run -pl hrms-{service} -Plocal -Dspring-boot.run.profiles=local

# 前端：啟動 Dev Server
cd frontend && npm run dev
```

- [ ] 後端服務 health check 回應 200
- [ ] 前端 http://localhost:5173 可存取
- [ ] MockConfig 目標模組設為 `false`（使用真實 API）
- [ ] Vite Proxy 設定正確（指向對應後端 port）

#### 5.2 基本功能驗證

| 操作 | 驗證重點 |
|:---|:---|
| 登入 | 帳密驗證、Token 取得、頁面導向 |
| 列表查詢 | 分頁、排序、篩選、資料正確性 |
| 新增 | 表單驗證、成功提示、列表刷新 |
| 編輯 | 帶入現有資料、更新成功、不可改欄位鎖定 |
| 刪除/停用 | 二次確認、成功提示、列表更新 |
| 詳情查看 | 欄位完整、格式正確 |

- [ ] CRUD 流程可正常操作
- [ ] 頁面資料來自真實 API（非 Mock）
- [ ] 中文顯示正確（含 enum 標籤、日期格式）

#### 5.3 API 回應欄位驗證

使用瀏覽器 Network tab 或 Playwright `browser_network_requests`：

- [ ] API 回應欄位名與前端 Adapter 映射一致
- [ ] 無 `undefined` 或空值欄位（應有值的欄位）
- [ ] enum 值在前端 guardEnum 的允許範圍內（無 console.warn）
- [ ] 分頁 total / page / size 正確

#### 5.4 錯誤場景驗證

| HTTP | 觸發方式 | 預期前端行為 |
|:---:|:---|:---|
| 400 | 送出空白必填欄位 | 顯示欄位驗證錯誤 |
| 401 | Token 過期或無效 | 導向登入頁 |
| 403 | 無權限操作 | 顯示「權限不足」提示 |
| 404 | 存取不存在的資源 | 顯示「找不到資源」提示 |
| 409 | 建立重複資料（如同名組織） | 顯示「已存在」提示 |
| 500 | 後端未預期錯誤 | 顯示「伺服器錯誤」提示 |

- [ ] 所有錯誤狀態碼都有對應的前端提示
- [ ] 錯誤訊息為中文（非 raw error message）
- [ ] 錯誤後頁面不卡死、可繼續操作

#### 5.5 Adapter 品質驗證

```bash
# 搜尋靜默 fallback（應使用 guardEnum 取代）
grep -r "|| '" frontend/src/features/{feature}/api/
grep -r "|| \"" frontend/src/features/{feature}/api/
```

- [ ] 無 `|| 'DEFAULT'` 靜默 fallback
- [ ] Adapter 處理所有後端可能的 enum 值
- [ ] null/undefined 欄位有明確處理

#### 5.6 串接驗證 Round 對照

| Round | 服務 | 前端頁面 |
|:---:|:---|:---|
| 1 | IAM + Organization | 登入、員工列表、使用者管理、組織架構 |
| 2 | Attendance + Payroll | 考勤打卡、請假、薪資 |
| 3 | Project + Timesheet | 專案管理、工時填報 |
| 4 | Insurance + Performance | 保險、績效考核 |
| 5 | Recruitment + Training | 招募、訓練 |
| 6 | Workflow + Notification + Document + Reporting | 簽核、通知、文件、報表 |

### 六、非功能性驗證

- [ ] **資安**：無 SQL Injection / XSS / CSRF 風險
- [ ] **效能**：API 回應時間 < 2s（正常場景）
- [ ] **錯誤處理**：所有 HTTP 狀態碼（400-500）都有對應處理
- [ ] **國際化**：中文顯示正確，無亂碼

---

## QA 報告格式

```markdown
## QA 驗證報告

**驗證範圍**：{描述}
**驗證日期**：{日期}
**驗證環境**：Local / Test / Staging

### 測試執行結果

| 類別 | 總數 | 通過 | 失敗 | 跳過 |
|:---|:---:|:---:|:---:|:---:|
| 後端單元測試 | | | | |
| 後端合約測試 | | | | |
| 後端整合測試 | | | | |
| 前端 Adapter 測試 | | | | |
| 前端 Factory 測試 | | | | |
| 前端元件測試 | | | | |
| 前端 Build | ✅ / ❌ | | | |
| 前後端串接（E2E） | | | | |

### 發現問題

| # | 嚴重度 | 模組 | 問題描述 | 狀態 |
|:---|:---|:---|:---|:---|
| 1 | P0 | HR03 | ... | 待修復 |

### 結論
[ ] ✅ 通過 — 可上線
[ ] ⚠️ 有條件通過 — 修復 P0 後可上線
[ ] ❌ 不通過 — 需修復後重新驗證
```

---

## Bug 追蹤格式

```markdown
### BUG-{模組代碼}-{序號}

**嚴重度**：P0 / P1 / P2
**模組**：HR{DD}
**重現步驟**：
1. ...
2. ...
3. ...

**預期結果**：...
**實際結果**：...
**根因分析**：...
**修復方式**：...
**影響範圍**：...
**回歸測試**：[ ] 已驗證
```
