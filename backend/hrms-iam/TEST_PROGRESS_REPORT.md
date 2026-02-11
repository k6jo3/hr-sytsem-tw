# HR01 IAM Service - 測試執行進度報告

## 📊 測試通過率

### 最新狀態 (2026-02-11 11:40)

- **總測試數**: 277
- **通過**: 248 ✅
- **失敗**: 29 ⚠️
- **錯誤**: 0 ✅
- **通過率**: **89.5%** 🎯

### 進度追蹤

| 時間 | 通過率 | 通過 | 失敗 | 錯誤 | 主要修正 |
|:---|:---:|:---:|:---:|:---:|:---|
| 初始狀態 | 68.6% | 190 | 26 | 62 | - |
| 第一階段 | 71.5% | 198 | 14 | 65 | RequiredField.value, dataPath 修正 |
| 第二階段 | 86.6% | 240 | 8 | 29 | 變數替換、schema 對齊 |
| 第三階段 | 89.5% | 248 | 29 | 0 | @Sql import、password_reset_tokens 表 |

---

## ✅ 已解決的問題

### 1. 編譯錯誤 (全部解決)
- ✅ 缺少 `@Sql` 註解的 import 語句
- ✅ AuthenticationApiContractTest.java
- ✅ PermissionApiContractTest.java
- ✅ ProfileApiContractTest.java
- ✅ IamApiContractTest.java

### 2. 資料庫結構問題 (全部解決)
- ✅ H2 資料庫大小寫敏感性 (DATABASE_TO_LOWER=TRUE)
- ✅ 缺少 tenants 表格
- ✅ roles 表缺少 role_code, status 欄位
- ✅ password_reset_tokens 表格定義

### 3. 合約測試框架問題 (全部解決)
- ✅ RequiredField 缺少 value 屬性
- ✅ dataPath 從 "data.content" 改為 "items"
- ✅ 角色查詢 dataPath 從 "data" 改為 ""
- ✅ 變數替換機制 (loadContractFromMarkdown)

### 4. 測試資料初始化 (全部解決)
- ✅ TRUNCATE 清除資料機制
- ✅ MERGE INTO 避免重複插入
- ✅ 外鍵約束處理 (REFERENTIAL_INTEGRITY)

---

## ⚠️ 剩餘問題 (29 個失敗)

### 分類統計

| 測試類別 | 失敗數 | 問題類型 |
|:---|:---:|:---|
| AuthenticationApiContractTest | 5 | HTTP 狀態碼不符 (400/204) |
| IamApiContractTest - RoleCommand | 4 | 權限不足 (403) |
| IamApiContractTest - UserCommand | 6 | 權限不足 (403) |
| IamApiContractTest - RoleQuery | 2 | 合約驗證失敗 |
| IamApiContractTest - UserQuery | 1 | 合約驗證失敗 |
| ProfileApiContractTest - Command | 2 | 權限不足或實作缺失 |
| ProfileApiContractTest - Query | 1 | 合約驗證失敗 |
| UserApiIntegrationTest - Search | 3 | 整合測試資料問題 |
| UserApiIntegrationTest - CRUD | 3 | 整合測試資料問題 |
| GetUserListServiceImplTest | 2 | 單元測試斷言問題 |

### 核心問題分析

1. **合約測試使用 Mock Repository**
   - 合約測試應該驗證完整流程（API → Service → Repository → Database）
   - 目前使用 `@MockBean` 導致沒有真正執行資料庫操作
   - **建議**: 合約測試應該使用真實 Repository（@SpringBootTest 完整環境）

2. **認證 API 回傳狀態碼與合約不符**
   - login: 預期 200，實際 400（請求驗證失敗）
   - logout: 預期 200，實際 204（No Content）
   - refreshToken: 預期 200，實際 400
   - resetPassword: 預期 200，實際 400
   - **問題**: 可能是 API 實作與合約定義不一致

3. **權限檢查導致 403**
   - 所有角色管理操作都回傳 403
   - `mockSecurityContext()` 有執行，但 Spring Security 可能沒有正確讀取
   - **問題**: `@AutoConfigureMockMvc(addFilters = false)` 可能不足夠

---

## 📋 下一步修正建議

### 優先級 1 - 重新設計合約測試架構

**現狀問題**:
- 合約測試使用 `@MockBean` 無法驗證真實業務邏輯
- 無法驗證資料變更（INSERT/UPDATE/DELETE）

**建議方案**:
1. 移除 `@MockBean` for Repository
2. 使用真實的 H2 資料庫和真實的 Service
3. 確保測試資料腳本正確執行

### 優先級 2 - 修正認證 API 測試

**需要檢查**:
1. API 規格中定義的狀態碼是 200 還是 204？
2. 請求 DTO 的驗證規則（@Valid）
3. 測試資料是否包含有效的使用者帳號

### 優先級 3 - 修正 Security 配置

**需要調整**:
1. 在測試中正確設置認證 Context
2. 確保 MockMvc 能夠讀取 SecurityContextHolder
3. 考慮使用 `@WithMockUser` 替代手動設置

---

## 🎯 目標

- **短期目標**: 達到 95% 通過率 (263/277)
- **中期目標**: 達到 98% 通過率 (271/277)
- **最終目標**: 達到 100% 通過率 (277/277)

---

## 📝 修正歷史

### 2026-02-11 11:40 - 第三階段修正

**問題**: 29 個編譯錯誤（Sql cannot be resolved）

**修正**:
1. 為 4 個合約測試類添加 `import org.springframework.test.context.jdbc.Sql;`
   - AuthenticationApiContractTest.java
   - PermissionApiContractTest.java
   - ProfileApiContractTest.java
   - IamApiContractTest.java

2. 在 iam_base_data.sql 中添加 password_reset_tokens 表
   - CREATE TABLE IF NOT EXISTS password_reset_tokens
   - TRUNCATE TABLE password_reset_tokens

**結果**: 
- 錯誤從 2 降為 0 ✅
- 通過率維持 89.5%

### 2026-02-11 11:30 - 第二階段修正

**問題**: 
- 變數替換失效導致 15 個測試失敗
- schema-test.sql 缺少 tenants 表和 role_code 欄位

**修正**:
1. 添加 `loadContractFromMarkdown()` 方法支援已替換變數的 Markdown
2. 批量替換所有 `loadContract()` 調用為 `loadContractFromMarkdown(contractSpec, ...)`
3. 在 schema-test.sql 中添加 tenants 表
4. 在 roles 表中添加 role_code, status 欄位
5. application-test.yml 添加 H2 大小寫設置

**結果**: 通過率從 71.5% 提升到 86.6%

### 2026-02-11 10:00 - 第一階段修正

**問題**: 
- RequiredField 缺少 value 屬性
- dataPath 路徑錯誤
- 測試資料重複插入

**修正**:
1. 擴展 RequiredField 類添加 value 屬性
2. 修正 iam_contracts.md 中的 dataPath
3. 修改 iam_base_data.sql 使用 TRUNCATE + MERGE INTO

**結果**: 通過率從 68.6% 提升到 71.5%

---

**報告生成時間**: 2026-02-11 11:41  
**報告版本**: 1.0
