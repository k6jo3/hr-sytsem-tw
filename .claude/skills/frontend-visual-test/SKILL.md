# Frontend Visual Test Skill

## 使用時機
當使用者想要透過 MCP Playwright 進行前端視覺測試時，使用此 Skill。
觸發關鍵字：`/frontend-visual-test`、「前端視覺測試」、「截圖測試」、「UI 驗證」

## 前置條件

1. **確認 Dev Server 已啟動**
   ```bash
   cd frontend && npm run dev
   ```
   預設位址：`http://localhost:5173`

2. **確認 MCP Playwright 已安裝**
   在 `.mcp.json` 中應有 playwright MCP 伺服器設定。
   重啟 Claude Code 後 Playwright MCP 工具會自動載入。

## 執行流程

### Step 1: 開啟瀏覽器並導覽
使用 Playwright MCP 工具：
- `browser_navigate` → 導覽至目標頁面
- `browser_screenshot` → 擷取截圖
- `browser_click` → 點擊元素
- `browser_type` → 輸入文字

### Step 2: 登入流程
```
1. browser_navigate → http://localhost:5173/login
2. browser_type → 帳號欄位輸入 admin
3. browser_type → 密碼欄位輸入 password
4. browser_click → 點擊「登入」按鈕
5. browser_screenshot → 確認登入成功
```

### Step 3: 選單驗證
登入後驗證：
- 側邊欄選單是否完整顯示（15 個模組）
- 圖示是否正確（特別是 HR12 通知應為鈴鐺圖示）
- 點擊選單項後高亮是否正確
- 動態路由（如 `/admin/employees/123`）是否正確高亮

### Step 4: 角色權限驗證
根據不同角色登入：
- **ADMIN**: 應看到所有選單項
- **HR**: 應看到組織、考勤、薪資、保險、績效、招募
- **EMPLOYEE**: 只應看到打卡、請假、薪資單、保險資料、工時、績效等個人功能
- **PM**: 應看到專案管理相關選單
- **FINANCE**: 應看到薪資核算相關選單

### Step 5: 路由守衛驗證
直接存取限制路由：
- EMPLOYEE 嘗試存取 `/admin/users` → 應被重導至 `/dashboard`
- 未登入嘗試存取任何受保護路由 → 應被重導至 `/login`

## 常見問題

### Q: Playwright MCP 工具沒出現？
A: 確認已重啟 Claude Code，且 `.mcp.json` 設定正確。

### Q: Dev Server 無法啟動？
A: 確認已安裝 node_modules：`cd frontend && npm install`

### Q: 截圖中文亂碼？
A: Windows 環境通常不會有此問題，若有請檢查系統字型設定。

## 驗證清單

- [ ] 登入頁面正常顯示
- [ ] 登入成功後跳轉至首頁
- [ ] 側邊欄 15 個模組選單完整
- [ ] HR12 通知圖示為 BellOutlined（鈴鐺）
- [ ] 動態路由高亮正確（例如 `/admin/employees/123`）
- [ ] 子選單自動展開
- [ ] ADMIN 角色看到全部選單
- [ ] EMPLOYEE 角色只看到個人功能
- [ ] `/profile` 頁面正常顯示個人資料
- [ ] 未授權路由正確重導
