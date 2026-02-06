# HR01 IAM 服務業務合約測試修正報告

**日期:** 2026-02-06
**修正人員:** Claude
**狀態:** ✅ 初步完成 (待解決合約規格不一致問題)

---

## 📋 修正摘要

### 問題描述
原有的 `IamContractTest.java` **不是真正的業務合約測試**，而是 **Repository 層查詢測試**：

❌ **錯誤的測試方式：**
- 繼承 `BaseContractTest`（Assembler 單元測試用的基類）
- 直接調用 `userRepository.findAll(query)`
- 沒有測試完整的 API 流程 (Controller → Service → Repository)
- 沒有角色權限驗證
- 手動組裝 QueryGroup，跳過 Service 層邏輯

### 修正內容

✅ **建立正確的業務合約測試 `IamApiContractTest.java`：**
1. 繼承 `BaseApiContractTest`（API 合約測試基類）
2. 使用 `@SpringBootTest` + `@AutoConfigureMockMvc` 啟動完整測試環境
3. 使用 `@MockBean` 模擬 Repository
4. 使用 `MockMvc` 調用實際 API 端點
5. 使用 `ArgumentCaptor` 攔截傳給 Repository 的 QueryGroup
6. 驗證完整流程：`Controller → Service → QueryBuilder → QueryGroup → Repository`
7. 支援角色權限驗證（`@WithMockUser`）

✅ **加入缺少的依賴：**
- 在 `pom.xml` 中加入 `spring-security-test` 依賴

---

## 📊 測試結果

### 執行結果
```
Tests run: 20, Failures: 3, Errors: 0, Skipped: 0
```

### 通過的測試 (17/20)
- ✅ 角色查詢 API 合約測試 (5 個 TODO - 待 API 實作)
- ✅ 權限查詢 API 合約測試 (4 個 TODO - 待 API 實作)
- ✅ 登入紀錄查詢 API 合約測試 (5 個 TODO - 待 API 實作)
- ✅ 使用者查詢：依角色查詢 (TODO - 待 API 實作)
- ✅ 使用者查詢：依租戶查詢 (TODO - 待 API 實作)
- ✅ 使用者查詢：同部門查詢 (TODO - 待權限控制實作)

### 失敗的測試 (3/20)

#### ❌ IAM_U001: 查詢啟用中的使用者
**失敗原因：** 合約規格與實作不一致
- **合約要求:** `is_deleted = 0`
- **實作產出:** `status != 'DELETED'`
- **根本原因:** IAM 模組使用 `status` 欄位表示刪除狀態，而非獨立的 `is_deleted` 欄位

#### ❌ IAM_U002: 依帳號模糊查詢
**失敗原因：** 合約規格與實作不一致
- **合約要求:** `username LIKE 'admin'` + `is_deleted = 0`
- **實作產出:** `status != 'DELETED'` (缺少 username LIKE)
- **根本原因：**
  1. API 使用 `keyword` 參數，但未轉換成 `username LIKE`
  2. `GetUserListRequest.keyword` 欄位沒有 `@QueryFilter` 註解
  3. Service 未處理 keyword 的查詢邏輯

#### ❌ IAM_U004: 查詢鎖定帳號
**失敗原因：** 同 IAM_U001，缺少 `is_deleted = 0`

---

## 🔧 待解決問題

### 1. is_deleted 欄位不一致

**問題：** 合約要求所有查詢都要包含 `is_deleted = 0`，但 IAM 模組使用 `status != 'DELETED'`

**可能的解決方案：**

#### 方案 A：修改合約規格（建議）
- 將合約中的 `is_deleted = 0` 改為 `status != 'DELETED'`
- 理由：IAM 使用 status 欄位統一管理使用者狀態（ACTIVE, LOCKED, DELETED）更符合業務語意
- 影響：需更新 `contracts/iam_contracts.md`

#### 方案 B：修改 Domain 層
- 在 User Entity 加入 `is_deleted` 欄位
- 在資料庫加入 `is_deleted` 欄位
- 修改 Service 加入 `is_deleted = 0` 過濾
- 理由：符合統一的軟刪除規範
- 影響：需要資料庫遷移、Entity 修改、Service 修改

### 2. keyword 參數未轉換成 username LIKE

**問題：** API 使用 `keyword` 參數，但未自動轉換成 `username LIKE` 查詢

**可能的解決方案：**

#### 方案 A：在 Request DTO 加入 @QueryFilter
```java
@QueryFilter(property = "username", operator = Operator.LIKE)
private String keyword;
```

#### 方案 B：在 Service 手動處理
```java
@Override
protected QueryGroup buildQuery(GetUserListRequest request, JWTModel currentUser) {
    QueryBuilder builder = QueryBuilder.where()
            .fromDto(request)
            .ne("status", "DELETED");

    // 手動處理 keyword
    if (request.getKeyword() != null) {
        builder.like("username", request.getKeyword());
    }

    return builder.build();
}
```

#### 方案 C：修改合約規格
- 將合約中的 `username LIKE 'admin'` 改為使用 keyword 參數的語意描述
- 理由：API 設計使用通用的 keyword 搜尋，而非僅限 username

---

## 📝 建議行動

### 優先級 P0
1. **與 SA 確認合約規格**
   - 確認 `is_deleted` vs `status != 'DELETED'` 的規範
   - 確認 `keyword` 參數的查詢語意
   - 統一修改合約規格或實作邏輯

### 優先級 P1
2. **修正 keyword 查詢邏輯**
   - 根據確認結果實作 keyword → username LIKE 轉換

3. **實作待開發的 API**
   - 角色查詢 API (5 個場景)
   - 權限查詢 API (4 個場景)
   - 登入紀錄查詢 API (5 個場景)

### 優先級 P2
4. **處理舊測試檔案**
   - 決定是否保留 `IamContractTest.java`
   - 如果保留，重構為 Assembler 單元測試
   - 如果刪除，建立遷移說明文件

---

## 📁 相關檔案

### 新增檔案
- ✅ `src/test/java/com/company/hrms/iam/api/contract/IamApiContractTest.java` - 正確的業務合約測試

### 修改檔案
- ✅ `pom.xml` - 加入 `spring-security-test` 依賴

### 待處理檔案
- ⏳ `src/test/java/com/company/hrms/iam/application/service/contract/IamContractTest.java` - 舊測試（待決定處理方式）
- ⏳ `contracts/iam_contracts.md` - 合約規格（可能需要修正）

---

## 🎯 測試覆蓋率

### 使用者查詢 (User Query)
- ✅ IAM_U001: 查詢啟用中的使用者 (測試存在，待修正合約)
- ✅ IAM_U002: 依帳號模糊查詢 (測試存在，待修正實作)
- ⏳ IAM_U003: 依角色查詢使用者 (待 API 實作)
- ✅ IAM_U004: 查詢鎖定帳號 (測試存在，待修正合約)
- ⏳ IAM_U005: 依租戶查詢使用者 (待 API 參數支援)
- ⏳ IAM_U006: 一般使用者查詢同部門 (待權限控制實作)

### 角色查詢 (Role Query)
- ⏳ IAM_R001 ~ IAM_R005 (待 API 實作)

### 權限查詢 (Permission Query)
- ⏳ IAM_P001 ~ IAM_P004 (待 API 實作)

### 登入紀錄查詢 (Login Log Query)
- ⏳ IAM_L001 ~ IAM_L005 (待 API 實作)

---

## ✅ 結論

HR01 的業務合約測試框架已建立完成，測試可以正常執行並攔截 QueryGroup 進行驗證。目前的失敗是由於**合約規格與實作不一致**，而非測試本身的問題。

**下一步：**
1. 與 SA 確認合約規格修正方向
2. 根據確認結果修正合約或實作
3. 參考 HR01 的修正方式，修正其他模組（HR03、HR04、HR05、HR07）
