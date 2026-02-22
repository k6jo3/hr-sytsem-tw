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
