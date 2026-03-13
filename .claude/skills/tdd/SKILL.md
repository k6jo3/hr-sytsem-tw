---
name: tdd
description: TDD 開發流程 — 所有功能開發、修改、邏輯調整必須遵循的測試驅動開發規範，強制引用合約測試
user_invocable: true
---

# TDD 開發流程 Skill

**名稱：** Test-Driven Development 開發流程
**版本：** 2.0
**適用範圍：** 所有後端/前端功能開發、修改、邏輯調整

---

## 強制規則

> **所有功能開發、修改功能、調整邏輯，都必須走 TDD 流程。**
> **合約測試是 TDD 的起點 — 先確認合約，再寫測試，再寫實作。**

---

## TDD 三步驟

```
Red → Green → Refactor
失敗測試 → 通過實作 → 重構優化
```

---

## 完整開發流程

### 第〇步：確認合約與規格（開發前必做）

在寫任何程式碼之前，**必須**先確認：

1. **讀取合約文件**
   ```
   contracts/{service_name}_contracts.md
   ```
   - 找到對應的場景 ID（如 `ORG_CMD_E001`）
   - 確認 request/response 格式、業務規則、預期事件

2. **讀取 API 規格文件**
   ```
   knowledge/04_API_Specifications/{NN}_*.md
   ```

3. **讀取系統設計書**
   ```
   knowledge/02_System_Design/{NN}_*.md
   ```

4. **確認 framework 規範**
   ```
   framework/architecture/03_Business_Pipeline.md    # Service 實作
   framework/architecture/Fluent-Query-Engine.md     # Repository 查詢
   framework/testing/04_合約驅動測試.md              # 測試架構
   ```

**如果合約文件中缺少對應場景：**
→ 先執行 `/contract-driven-test` skill 補充合約，再繼續 TDD

---

### 第一步：Red — 寫失敗的測試

根據合約定義，先寫出預期會失敗的測試：

#### 後端測試分層

| 測試類型 | 測試對象 | 基類 | 位置 |
|:---|:---|:---|:---|
| 合約測試 | API 端點 + 合約規格 | `BaseContractTest` | `api/contract/` |
| Domain 單元測試 | Aggregate、Entity、VO、Domain Service | 無 | `domain/` |
| Pipeline 單元測試 | Task、Business Pipeline | 無 | `application/` |
| API 整合測試 | Controller → Service → DB | `BaseApiIntegrationTest` | `api/controller/` |

**優先順序：合約測試 > Domain 單元測試 > Pipeline 單元測試 > 整合測試**

#### 後端 — 合約測試（必須）

```java
@Test
@DisplayName("{SCENARIO_ID}: {場景描述}")
void testMethod_{SCENARIO_ID}() throws Exception {
    ContractSpec contract = loadContractFromMarkdown(contractSpec, "{SCENARIO_ID}");

    // 建立請求
    Map<String, Object> request = new HashMap<>();
    request.put("field", "value");

    // 擷取前快照
    var beforeSnapshot = captureDataSnapshot("table_name");

    // 執行 API
    var result = mockMvc.perform(post("/api/v1/{resource}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andReturn();

    // 擷取後快照 + 事件
    var afterSnapshot = captureDataSnapshot("table_name");
    var events = convertDomainEventsToMaps(eventCaptor.getCapturedEvents());

    // 驗證合約
    verifyCommandContract(beforeSnapshot, afterSnapshot, events, contract);
}
```

#### 後端 — Domain 單元測試（必須）

```java
@Test
@DisplayName("建立員工 - 工號格式驗證")
void createEmployee_invalidNumber_shouldThrow() {
    assertThrows(DomainException.class, () ->
        Employee.create("INVALID", "張三", ...));
}
```

#### 前端測試

| 測試類型 | 測試對象 | 必要性 |
|:---|:---|:---:|
| Adapter 測試 | API Response → DTO 轉換 | **必要** |
| Factory 測試 | DTO → ViewModel 轉換 | 必要 |
| Hook 測試 | 自定義 Hook 邏輯 | 必要 |
| Component 測試 | 組件渲染與互動 | 必要 |

**優先順序：Adapter 測試 > Factory 測試 > Hook 測試 > Component 測試**

```typescript
// Adapter 測試（必須）— 驗證 API 回傳與前端 DTO 的映射
describe('adaptEmployeeItem', () => {
    it('should map all fields correctly from API response', () => {
        const apiResponse = { employeeId: '001', employeeName: '張三', status: 'ACTIVE' };
        const result = adaptEmployeeItem(apiResponse);
        expect(result.id).toBe('001');
        expect(result.name).toBe('張三');
    });

    it('should throw or warn when status is unknown enum value', () => {
        const apiResponse = { employeeId: '001', status: 'PROBATION' };
        // ❌ 禁止: dto.status || 'ACTIVE'（靜默 fallback 隱藏問題）
        // ✅ 正確: 明確處理未知值，console.warn 或拋錯
        expect(() => adaptEmployeeItem(apiResponse)).not.toThrow();
        expect(consoleSpy).toHaveBeenCalledWith(expect.stringContaining('PROBATION'));
    });

    it('should handle null/missing fields gracefully', () => {
        const apiResponse = { employeeId: '001' }; // 缺少多數欄位
        const result = adaptEmployeeItem(apiResponse);
        expect(result.name).toBe(''); // 明確空值，非 undefined
    });
});

// Factory 測試
it('should transform API DTO to ViewModel correctly', () => {
    const dto: EmployeeDto = { /* 合約定義的 response 格式 */ };
    const vm = EmployeeViewModelFactory.createFromDTO(dto);
    expect(vm.fullName).toBe('...');
});
```

---

### 第二步：Green — 寫最少的實作讓測試通過

1. **後端實作順序**
   - Request/Response DTO
   - Controller method（method name 決定 Service bean name）
   - Application Service（`@Service("{methodName}ServiceImpl")`）
   - Business Pipeline Tasks（步驟 ≥ 2 步就建立 Task）
   - Domain 邏輯（純 POJO，驗證在 Domain 層）
   - Repository/Mapper

2. **前端實作順序**
   - API Types（DTO 定義 — 欄位名必須與合約 `requiredFields` 一致）
   - API Module（API 呼叫 + Adapter）
   - **Adapter 函式**（API Response → 前端 DTO，禁止靜默 fallback）
   - Factory（DTO → ViewModel）
   - Hook（業務邏輯封裝）
   - Component（UI 組件）
   - Page（頁面組合）

3. **Service 只做編排，不做決策**（架構師原則三）

4. **每完成一個實作單元，就執行對應測試**
   ```bash
   # 後端
   mvn test -Dtest="{TestClass}" -pl hrms-{service}

   # 前端
   npx vitest run {test-file}
   ```

---

### 第三步：Refactor — 重構

測試通過後：

1. **檢查 SOLID 原則**
   - Single Responsibility：每個類別只負責一件事
   - Open/Closed：對擴展開放，對修改封閉
   - Interface Segregation：介面不強迫實作不需要的方法

2. **檢查 Clean Code**
   - 方法不超過 20 行
   - 命名清晰（參考 CLAUDE.md 命名規範）
   - 註解用繁體中文

3. **確認重構後測試仍通過**
   ```bash
   mvn test -pl hrms-{service}
   ```

---

## 修改既有功能的 TDD 流程

修改功能不是「改完再測」，而是：

1. **先確認現有測試是否覆蓋要修改的行為**
   - 有 → 修改測試預期值（Red），再修改實作（Green）
   - 沒有 → 先補測試（Red），再修改實作（Green）

2. **確認合約文件是否需要更新**
   - HTTP 狀態碼變更 → 更新合約 + 測試
   - 回應欄位變更 → 更新合約的 `requiredFields` + 測試
   - 業務規則變更 → 更新合約的 `businessRules` + 測試

3. **合約變更的連鎖反應**
   ```
   修改實作 → 更新合約 → 更新測試 → 確認所有測試通過
   ```

---

## 邏輯調整的 TDD 流程

調整業務邏輯（如狀態機、計算規則、驗證規則）：

1. **寫出新邏輯的測試案例**（包含邊界條件）
2. **確認舊測試是否需要調整**（行為改變 → 調整預期值）
3. **修改 Domain 邏輯**
4. **確認所有相關測試通過**

---

## 檢查清單

開發完成前，逐一確認：

### 後端
- [ ] 合約文件有對應的場景定義（含正常 + 錯誤流程）
- [ ] 合約測試程式存在且通過
- [ ] Domain 單元測試覆蓋 100%
- [ ] Pipeline/Task 有單元測試
- [ ] API 整合測試存在
- [ ] GlobalExceptionHandler 錯誤碼映射有測試覆蓋
- [ ] H2 測試 schema 與 Mapper SQL 同步
- [ ] 測試資料反映真實場景（中文名、各種 status、邊界值）
- [ ] H2 不支援的 PostgreSQL 語法已標記 TODO

### 前端
- [ ] Adapter 函式有單元測試（含缺失欄位、null、未知 enum 值）
- [ ] Adapter 無靜默 fallback（`|| 'DEFAULT'` 禁止）
- [ ] 前端 DTO type 欄位名與合約 `requiredFields` 一致
- [ ] 前端 Factory 測試通過
- [ ] 前端 Hook 測試通過
- [ ] 前端 Component 測試通過
- [ ] API 錯誤處理覆蓋 400/401/403/404/409/500

### 整合（串接驗證閘門 — 未通過不可標記完成）
- [ ] 所有既有測試仍通過（無 regression）
- [ ] MockConfig 對應模組設為 `false`（使用真實 API）
- [ ] **串接煙霧測試**：啟動後端 + 前端，實際操作一次核心流程，確認資料正確顯示
- [ ] **Console 無警告**：瀏覽器 Console 無 `[Adapter]` 或 `[MockConfig]` 警告
- [ ] **Network 確認**：瀏覽器 Network tab 確認 API 呼叫走真實後端（非 Mock）

---

## 常用測試指令

```bash
# 後端 — 單一測試類別
JAVA_HOME="..." mvn test -Dtest="{TestClass}" -pl hrms-{service}

# 後端 — 合約測試
JAVA_HOME="..." mvn test -Dtest="*ContractTest" -pl hrms-{service}

# 後端 — 整個模組
JAVA_HOME="..." mvn test -pl hrms-{service}

# 後端 — 全部模組
JAVA_HOME="..." mvn test

# 前端 — 單一測試檔
cd frontend && npx vitest run src/features/{feature}/**/*.test.ts

# 前端 — 全部測試
cd frontend && npx vitest run
```

---

## 參考文件

| 文件 | 何時查閱 |
|:---|:---|
| `contracts/{service}_contracts.md` | TDD 第〇步：確認合約 |
| `framework/testing/04_合約驅動測試.md` | 合約測試架構設計 |
| `framework/testing/測試架構規範.md` | 測試規範總覽 |
| `framework/architecture/03_Business_Pipeline.md` | Service/Task 實作 |
| `knowledge/02_System_Design/{NN}_*.md` | 系統設計 |
| `knowledge/04_API_Specifications/{NN}_*.md` | API 規格 |
