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

4. **記錄格式不匹配**
   - 若發現前端 DTO (snake_case) 與後端回應 (camelCase) 不匹配
   - 在對應的 API 檔案中建立 Response Adapter
   - 更新 Factory 和測試

5. **恢復 MockConfig**
   - 驗證完成後將 MockConfig 恢復為全開
   - 確保不影響其他開發者

---

## Round 對照表

| Round | 服務 | 前端頁面 | 後端 Port |
|:---:|:---|:---|:---:|
| 1 | IAM + Organization | 登入、員工列表、組織管理 | 8081, 8082 |
| 2 | Attendance + Payroll | 考勤、薪資 | 8083, 8084 |
| 3 | Insurance + Project | 保險、專案 | 8085, 8086 |
| 4 | Timesheet + Performance | 工時、績效 | 8087, 8088 |
| 5 | Recruitment + Training | 招募、訓練 | 8089, 8090 |
| 6 | Workflow + Notification | 簽核、通知 | 8091, 8092 |
| 7 | Document + Reporting | 文件、報表 | 8093, 8094 |

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
