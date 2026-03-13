# Skill: UI + API Round Test

## 說明
封裝「UI 比對 + 後端啟動 + API 串接測試」三階段流程，用於逐 Round 驗證前後端整合。

## 使用方式
```
/ui-api-roundtest [round] [services...]
```

範例：
- `/ui-api-roundtest round1 iam organization` — 驗證 HR01 + HR02
- `/ui-api-roundtest round2 attendance payroll` — 驗證 HR03 + HR04

---

## Phase A：UI 比對

### 步驟
1. **確認 Dev Server**
   - 檢查 `http://localhost:5173` 是否可用
   - 若未啟動，提示使用者執行：`cd frontend && npm run dev`

2. **Playwright 頁面截圖**
   - 使用 MCP Playwright 登入系統（帳號 `admin` / 密碼 `admin123`）
   - 依據 Round 涵蓋的服務，逐頁導航並截圖
   - 截圖存放至 `knowledge/06_UI_Comparison/`

3. **設計書比對**
   - 讀取對應的設計書：`knowledge/02_System_Design/{NN}_*服務系統設計書.md`
   - 找出 Chapter 3 的 UX Flow / wireframe 說明
   - 逐元素比對：選單項目、表格欄位、按鈕、表單欄位、篩選條件

4. **產出比對報告**
   - 報告格式：`knowledge/06_UI_Comparison/round{N}_ui_comparison.md`
   - 包含：符合項目、缺漏項目、多餘項目、建議修正

---

## Phase B：後端啟動

### 步驟
1. **安裝 hrms-common**
   ```bash
   cd backend && mvn install -pl hrms-common -DskipTests
   ```

2. **啟動目標服務**（依 Round 決定）
   - IAM (8081): `mvn spring-boot:run -pl hrms-iam -Plocal -Dspring-boot.run.profiles=local`
   - Organization (8082): `mvn spring-boot:run -pl hrms-organization -Plocal -Dspring-boot.run.profiles=local`
   - Attendance (8083): `mvn spring-boot:run -pl hrms-attendance -Plocal -Dspring-boot.run.profiles=local`
   - 其他服務依此類推，port = 8080 + service number

3. **健康檢查**
   ```bash
   curl -s http://localhost:{port}/actuator/health
   ```
   若服務無 actuator，改用實際 API endpoint 測試：
   ```bash
   curl -s http://localhost:{port}/api/v1/{resource}
   ```

---

## Phase C：API 串接測試

### 步驟
1. **切換 MockConfig**
   - 編輯 `frontend/src/config/MockConfig.ts`
   - 將目標模組設為 `false`（使用真實 API）
   - 其他模組保持 `true`

2. **確認 Vite Proxy**
   - 檢查 `frontend/vite.config.ts` 的 `server.proxy` 是否包含目標服務的路由
   - 確認 `.env.development` 的 `VITE_API_BASE_URL` 為相對路徑（`/api/v1`）

3. **Playwright 操作驗證**
   - 使用 Playwright 操作頁面
   - 檢查 Network Requests（`browser_network_requests`）確認 API 呼叫目標
   - 檢查 Console Messages（`browser_console_messages`）是否有錯誤
   - 確認頁面資料來自真實 API（非 Mock）

4. **Adapter 品質驗證（必做）**
   - 檢查 Adapter 函式是否有靜默 fallback（`|| 'DEFAULT'`、`?? 'ACTIVE'`）
   - 確認 Adapter 處理所有後端可能的 enum 值（如 PROBATION、LOCKED、PENDING）
   - 確認 Adapter 對 null/undefined 欄位有明確處理（非靜默忽略）
   - 搜尋模式：`grep -r "|| '" features/{feature}/api/`

5. **欄位名一致性驗證（必做）**
   - 比對 Network tab 中 API 回應的實際欄位名
   - 與前端 DTO type 定義的欄位名逐一對照
   - 與合約 `requiredFields` 的欄位名逐一對照
   - 發現不一致時：修正合約 → 修正 Adapter → 修正測試

6. **錯誤場景驗證**
   - 測試各種錯誤情境的前端表現：
     - 401（未授權）→ 應跳轉登入頁
     - 403（無權限）→ 應顯示權限不足提示
     - 400（驗證失敗）→ 應顯示欄位錯誤訊息
     - 409（資源衝突）→ 應顯示重複提示
     - 500（伺服器錯誤）→ 應顯示通用錯誤訊息
   - 確認前端 API 層的 `.catch()` 處理了上述所有狀態碼

7. **確認 MockConfig**
   - 驗證完成後確認 MockConfig 目標模組為 `false`（使用真實 API）
   - **MockConfig 預設值應為 `false`**，只有開發中的模組才設為 `true`
   - 截至 2026-03-13，全部 14 模組均應為 `false`

---

## Round 對照表

| Round | 服務 | 前端頁面 | 後端 Port | 狀態 |
|:---:|:---|:---|:---:|:---:|
| 1 | IAM + Organization | 登入、員工列表、使用者管理、角色管理 | 8081, 8082 | ✅ |
| 2 | Attendance + Payroll | 考勤、薪資 | 8083, 8084 | ✅ |
| 3 | Project + Timesheet | 專案（列表/客戶/WBS）、工時（填報/審核） | 8086, 8087 | ✅ |
| 4 | Insurance + Performance | 保險、績效（週期/表單/考核/報表） | 8085, 8088 | ✅ |
| 5 | Recruitment + Training | 招募（Kanban）、訓練（課程/證照） | 8089, 8090 | ✅ |
| 6 | Workflow + Notification + Document + Reporting | 簽核、通知、文件、報表 | 8091-8094 | ✅ |

---

## 報告產出位置

- UI 比對報告：`knowledge/06_UI_Comparison/round{N}_ui_comparison.md`
- API 串接報告：`knowledge/06_UI_Comparison/round{N}_api_integration.md`

---

## 常見問題

### 後端啟動失敗
- 確認 `mvn install -pl hrms-common -DskipTests` 已執行
- 確認 Maven `local` profile 啟用（`-Plocal`）
- 確認 `application-local.yml` 存在於目標服務

### API 回傳 401
- 確認 hrms-common 的 `LocalSecurityAutoConfig` 已安裝
- 確認 `MockJwtAuthenticationFilter` 正確注入模擬使用者

### 前端格式不匹配
- 在 API 檔案中建立 `adapt*Response()` 函數
- 將後端 camelCase 轉換為前端 snake_case DTO
- 更新對應的 Factory 測試

### 頁面顯示正常但資料值異常
- 檢查 Adapter 是否有靜默 fallback（如 `status || 'ACTIVE'`）
- 確認後端實際回傳的 enum 值
- 修正 Adapter 使其明確處理所有可能值

### 錯誤情境無回饋
- 檢查前端 API 層的 error handling
- 確認 `GlobalExceptionHandler` 的 ErrorCode → HTTP Status 映射
- 確認前端 `.catch()` 覆蓋所有 HTTP 狀態碼

### 測試通過但手動操作失敗
- 優先檢查 MockConfig 是否為 `false`
- 檢查 Adapter 靜默 fallback
- 檢查前後端欄位名是否一致
- 參考 `/test-fix` skill 的「前後端串接問題診斷」章節
