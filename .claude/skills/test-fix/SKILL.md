---
name: test-fix
description: 系統化診斷並修復 Java/Spring Boot 測試失敗的流程與經驗
user_invocable: true
---

# Test Failure Diagnosis and Fix Skill

**Purpose**: 系統化診斷並修復 Java/Spring Boot 測試失敗的流程與經驗。

---

## 測試失敗分類

### 1. 編譯錯誤 (Compilation Errors)

**症狀**: `cannot find symbol`, `package does not exist`

**診斷步驟**:
1. 檢查 import 語句是否缺失
2. 檢查依賴項是否正確配置（pom.xml）
3. 確認類別/方法命名是否正確

### 2. 資料庫結構問題 (Database Schema Issues)

**症狀**: `Column "xxx" not found`, `Table "xxx" doesn't exist`

**診斷步驟**:
1. 確認 PO 類別的欄位定義
2. 檢查資料庫 schema 定義（schema-test.sql）
3. 檢查測試資料腳本

**H2 資料庫配置**:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1
```

### 3. 快照測試失敗 (Snapshot Test Failures)

**判斷是否為預期變更**:
- 添加安全過濾條件（is_deleted, tenant_id）→ 更新快照
- 移除必要的過濾條件 → 修正邏輯

### 4. HTTP 狀態碼不符 (Status Code Mismatch)

| 狀態碼 | 可能原因 | 修復方法 |
|:---:|:---|:---|
| 500 | Runtime Exception | 修正程式邏輯錯誤 |
| 404 | Mock Repository 回傳空 | 添加 `when(...).thenReturn(...)` |
| 400 | DTO 驗證失敗 | 調整請求資料或驗證規則 |
| 403 | 權限不足 | 正確設置 Mock 使用者權限 |

### 5. 合約測試問題 (Contract Test Issues)

| 問題 | 根本原因 | 修復方法 |
|:---|:---|:---|
| INSERT 筆數為 0 | 使用 `@MockBean` 替代真實 Service | 移除 `@MockBean`，使用真實 Repository |
| 變數替換失敗 | `loadContract()` 重新載入原始文件 | 使用 `loadContractFromMarkdown(contractSpec, ...)` |
| 403 權限錯誤 | MockMvc 未讀取 SecurityContext | 確認 `@AutoConfigureMockMvc(addFilters = false)` |

---

## 通用修復流程

### 步驟 1: 執行測試並收集資訊

```bash
cd backend/hrms-{service}
mvn clean test
mvn test 2>&1 | grep "Tests run:"
mvn test -Dtest=SpecificTestClass
```

### 步驟 2: 分析失敗類型

```bash
mvn test 2>&1 | grep "<<< FAILURE!"
mvn test 2>&1 | grep "ERROR"
mvn test 2>&1 | grep "cannot find symbol"
```

### 步驟 3: 根據類型修復

參考上面各節的修復方法。按 CLAUDE.md 規則 17：先確認是測試本身寫錯還是程式邏輯錯誤，在需修正處留下 TODO。

### 步驟 4: 驗證修復

```bash
mvn clean compile test-compile
mvn test -Dtest=FixedTestClass
mvn test
```

---

## 快速診斷速查表

| 錯誤訊息 | 可能原因 | 修復方向 |
|:---|:---|:---|
| `UPDATE 筆數不符: 預期 1, 實際 0` | @MockBean / 事務管理 | 移除 Mock 或調整事務 |
| `Status 404` | 測試資料缺失 | 添加測試資料 |
| `Status 400` + 驗證錯誤 | DTO 欄位不符 | 修正 request 欄位 |
| `Status 403` | 權限不足 | 設置正確權限 |
| `PRIMARY KEY violation` | 測試資料衝突 | 使用唯一的 ID |
| `變數替換失效` | 使用 loadContract() | 改用 loadContractFromMarkdown() |
| `UnsupportedOperationException` on getDomainEvents().clear() | 不可變 List | 使用 clearDomainEvents() |
| `BaseUnitTest assert 參數不足` | 需要 message 參數 | 添加 message 參數到 assert 呼叫 |

---

## 前後端串接問題診斷（測試通過但手動測試失敗）

### 6. MockConfig 導致假陽性

**症狀**: 前端測試通過、CI 綠燈，但手動操作時功能異常或資料為空

**診斷步驟**:
1. 檢查 `frontend/src/config/MockConfig.ts`
2. 確認目標模組是否設為 `false`（使用真實 API）
3. 檢查 MockConfig 預設值是否為 `true`（會導致未定義模組自動用 Mock）

**修復方法**:
- 將目標模組明確設為 `false`
- 所有已完成串接的模組都應設為 `false`

### 7. 前端 Adapter 靜默 Fallback

**症狀**: 頁面顯示資料但值不正確（如 status 永遠顯示 ACTIVE）

**診斷步驟**:
1. 搜尋 Adapter 函式中的 `|| '` 或 `?? '` 或 `|| "` 模式
2. 確認是否存在 `dto.status || 'ACTIVE'` 等靜默 fallback
3. 檢查後端實際回傳的 enum 值是否被前端處理

**修復方法**:
- 移除靜默 fallback，改為明確檢查 + `console.warn`
- 補齊 Adapter 測試，覆蓋未知 enum 值

### 8. 前後端欄位名不一致

**症狀**: API 回傳有資料但前端顯示為空或 undefined

**診斷步驟**:
1. 瀏覽器 Network tab 檢查 API 回應的欄位名
2. 對照前端 DTO type 定義的欄位名
3. 對照合約 `requiredFields` 的欄位名

**修復方法**:
- 修正合約 `requiredFields` 使其與後端 Response DTO 一致
- 修正前端 Adapter 映射或 DTO type 定義
- 如需欄位重命名，在合約 `frontendAdapterMapping` 中明確記錄

### 9. H2 與 PostgreSQL 差異

**症狀**: 本地 H2 測試通過，部署到 PostgreSQL 後 SQL 報錯

**常見差異**:
| H2 行為 | PostgreSQL 行為 | 修復方式 |
|:---|:---|:---|
| 自動型別轉換 | 嚴格型別檢查 | 加上明確 `CAST()` |
| 無 JSON 函式 | 支援 `jsonb_*` 函式 | 標記 TODO |
| `::` 不支援 | 支援 `::text` 型別轉換 | 用 `CAST()` 替代 |
| 大小寫不敏感 | 預設大小寫敏感 | 注意表名/欄位名 |

**修復方法**:
- 涉及 PostgreSQL 專有語法時，在測試中標記 `// TODO: H2 不支援，需 Testcontainers 驗證`
- 長期方案：導入 Testcontainers 替代 H2

### 10. 錯誤處理覆蓋不足

**症狀**: 特定錯誤情境（403/409/500）前端無法正確顯示錯誤訊息

**診斷步驟**:
1. 檢查 `GlobalExceptionHandler` 的 ErrorCode → HTTP Status 映射
2. 確認每個映射是否有整合測試覆蓋
3. 檢查前端 API 層是否處理了所有 HTTP 狀態碼

**修復方法**:
- 後端：補齊 `GlobalExceptionHandler` 測試
- 前端：在 API 層補齊 `.catch()` 處理所有狀態碼

---

## 關鍵經驗

### 合約測試的變數替換機制

**問題**: `loadContract()` 會重新讀取原始檔案，丟失 `setUp()` 中的變數替換

**解決方案**:
```java
@BeforeEach
void setUp() {
    contractSpec = loadContractSpec("iam");
    contractSpec = contractSpec.replace("{currentUserId}", "user-current-id");
    contractSpec = contractSpec.replace("{currentUserTenantId}", "T001");
}

@Test
void testQuery() {
    ContractSpec contract = loadContractFromMarkdown(contractSpec, "IAM_QRY_001");
}
```

### 測試資料設計原則

- **使用描述性 ID**: `user-current-id` 而非 `user-001`
- **使用 MERGE 而非 INSERT**: 避免主鍵重複
- **隔離測試資料**: 每個測試文件使用獨立的 ID 範圍
- **密碼 Hash 必須真實對應明文密碼**

### 事務管理問題

合約測試中 `@Transactional` + MockMvc + JdbcTemplate 快照會有事務可見性問題。Service 事務未提交時擷取快照會導致 UPDATE 筆數為 0。

### BaseContractTest protected 方法存取

外部測試類別無法直接呼叫 `loadContractSpec()` 等 protected 方法時，建立內部 helper 類別：

```java
private static class ContractTestHelper extends BaseContractTest {
    public String doLoadContractSpec(String serviceName) throws IOException {
        return loadContractSpec(serviceName);
    }
    public ContractSpec doLoadContractFromMarkdown(String markdown, String scenarioId) {
        return loadContractFromMarkdown(markdown, scenarioId);
    }
}
```

### DomainEventHolder 事件擷取

Mock repository 不會自動擷取 domain events，需手動設定：

```java
private void setupEventCapture() {
    doAnswer(invocation -> {
        Timesheet ts = invocation.getArgument(0);
        DomainEventHolder.captureAll(ts.getDomainEvents());
        ts.clearDomainEvents();
        return null;
    }).when(repository).save(any(Aggregate.class));
}
```

---

## 參考資料

- `framework/testing/` - 測試架構規範
- `contracts/` - 合約測試規格
- `CLAUDE.md` - 專案開發規範（規則 17：測試失敗處理原則）
