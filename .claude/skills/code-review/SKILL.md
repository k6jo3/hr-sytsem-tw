# Code Review Skill

## 使用時機
對前端或後端程式碼進行 Code Review 時使用。
觸發關鍵字：`/code-review`、「程式碼審查」、「CR」、「review」

> 最後更新：2026-03-14

---

## 執行流程

### Step 1: 確認審查範圍

```
請提供以下資訊：
1. 審查目標：[ ] 單一檔案 / [ ] PR / [ ] 整個模組
2. 審查層級：[ ] 後端 / [ ] 前端 / [ ] 全端
3. 變更類型：[ ] 新功能 / [ ] Bug 修復 / [ ] 重構 / [ ] 設計變更
```

### Step 2: 讀取變更檔案

- 若是 PR：`git diff main...HEAD`
- 若是單一檔案：直接讀取
- 若是模組：列出所有變更檔案

### Step 3: 逐項檢查

依照下方 Checklist 逐項檢查，產出審查報告。

---

## 後端 Checklist

### 架構層級

- [ ] **四層分離** — Controller / Service / Domain / Infrastructure 職責清晰
- [ ] **Domain 純 POJO** — Domain 層不依賴 Spring、JPA 等框架
- [ ] **Repository Interface 在 Domain 層** — 遵循依賴反轉
- [ ] **Service 只做編排** — 業務決策在 Domain 或 Task 中，Service 不含 if-else 業務邏輯

### 命名規範

- [ ] Controller：`HR{DD}{Screen}CmdController` / `QryController`
- [ ] Service：`{Verb}{Noun}ServiceImpl`，Bean 名稱與 Controller 方法名對應
- [ ] Task：`{Verb}{Business}Task` 或 `Load{Entity}Task`
- [ ] Context：清晰分區（輸入 / 中間 / 輸出）
- [ ] Request/Response DTO：`{Verb}{Noun}Request` / `{Noun}{Type}Response`
- [ ] Domain Event：`{Aggregate}{PastVerb}Event`

### Pipeline 模式

- [ ] 步驟 ≥ 2 步是否使用 Pipeline + Task？
- [ ] Task 是否單一職責？
- [ ] Context 是否有清晰的輸入/中間/輸出分區？
- [ ] 條件執行是否使用 `nextIf()` 而非 Task 內 if-else？

### Domain 設計

- [ ] Aggregate Root 是否有工廠方法（`create()` / `reconstitute()`）？
- [ ] Value Object 是否不可變且自帶驗證？
- [ ] 業務邏輯是否放在 Domain 層（非 Service 層）？
- [ ] Domain Event 是否正確命名與發布？

### 異常處理

- [ ] 業務錯誤是否拋出 `DomainException` 並帶 ErrorCode？
- [ ] ErrorCode 是否有對應的 HTTP 狀態碼？
- [ ] GlobalExceptionHandler 是否有處理新的 ErrorCode？
- [ ] 是否有對應的整合測試驗證錯誤回應？

### 測試

- [ ] 是否有合約測試？合約場景 ID 是否存在於 `contracts/` 中？
- [ ] Domain 邏輯是否有 100% 覆蓋率？
- [ ] Task 是否有單元測試（Mockito）？
- [ ] API 端點是否有整合測試？
- [ ] H2 test schema 是否與 Mapper SQL 同步？

### 資安

- [ ] 是否有 SQL Injection 風險？（禁止字串拼接 SQL）
- [ ] 是否有 XSS 風險？（驗證/轉義使用者輸入）
- [ ] 敏感資料是否加密或遮蔽？
- [ ] API 是否有適當的權限檢查？

### Query Engine

- [ ] 查詢是否使用 QueryBuilder / `@QueryFilter`？
- [ ] 是否避免直接寫 raw SQL？
- [ ] 分頁查詢是否正確使用 Pageable？

---

## 前端 Checklist

### 架構層級

- [ ] **API → Adapter → Factory → Component** 流程完整？
- [ ] 是否有直接使用後端回傳資料（未經 Adapter/Factory 轉換）？
- [ ] Mock API 是否同步更新？

### API 層

- [ ] Adapter 是否處理了欄位名稱映射（camelCase → snake_case）？
- [ ] 是否使用 `guardEnum` 而非 `|| 'DEFAULT'` 靜默 fallback？
- [ ] 欄位 fallback chain 是否覆蓋後端可能的命名變體？
- [ ] API 送出時是否正確轉換欄位名稱？

### Factory 層

- [ ] 是否有 Factory 轉換 DTO → ViewModel？
- [ ] enum → 中文標籤映射是否完整？
- [ ] enum → 顏色映射是否完整？
- [ ] 是否處理了缺失欄位的情況？

### 元件設計

- [ ] 表單 Modal 是否支援建立/編輯雙模式（`isEdit = !!editData`）？
- [ ] 建立後不可改的欄位是否設為 `disabled`？
- [ ] 刪除/停用操作是否有 `Modal.confirm` 二次確認？
- [ ] loading / error 狀態是否正確處理？

### 狀態管理

- [ ] 全域狀態是否只用 Redux（auth、token）？
- [ ] 功能狀態是否用 Hook + local state？
- [ ] 是否避免不必要的 re-render？

### 測試

- [ ] Adapter 是否有單元測試（含 null、缺失欄位、未知 enum）？
- [ ] Factory 是否有單元測試？
- [ ] 元件是否有測試？
- [ ] Hook 是否有測試？

### 三方一致性

- [ ] 後端 Response DTO 欄位名 = 合約 requiredFields = 前端 API Types？
- [ ] 不一致時是否在合約 `frontendAdapterMapping` 中記錄？

---

## 審查報告格式

```markdown
## Code Review 報告

**審查範圍**：{描述}
**審查日期**：{日期}

### 嚴重度分類

| 等級 | 定義 |
|:---|:---|
| 🔴 P0 | 功能失效 / 資安漏洞 — 必須修復 |
| 🟡 P1 | 資料錯誤 / 邏輯瑕疵 — 應該修復 |
| 🔵 P2 | 規範不符 / 可維護性 — 建議修復 |
| ⚪ P3 | 風格建議 — 選擇性修復 |

### 發現事項

#### 🔴 P0-001: {標題}
- **檔案**：`path/to/file.java:42`
- **問題**：{描述}
- **建議**：{修正方式}

#### 🟡 P1-001: {標題}
...

### 統計

| 等級 | 數量 |
|:---|:---:|
| 🔴 P0 | 0 |
| 🟡 P1 | 0 |
| 🔵 P2 | 0 |
| ⚪ P3 | 0 |

### 結論
[ ] ✅ 通過 — 可合併
[ ] ⚠️ 有條件通過 — 修復 P0/P1 後可合併
[ ] ❌ 不通過 — 需重新設計
```
