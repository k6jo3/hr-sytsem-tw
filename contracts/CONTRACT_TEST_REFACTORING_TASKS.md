# 合約測試修正工作項目文件

**建立日期:** 2026-02-06
**狀態:** 進行中
**目的:** 將 HR03、HR04、HR05、HR07 的錯誤 Repository 測試改為正確的業務合約測試

---

## 📋 總覽

### 問題摘要

目前 HR03、HR04、HR05、HR07 模組的「合約測試」**不是真正的業務合約測試**，而是錯誤實作的 Repository 層測試：

❌ **錯誤的測試方式：**
- 繼承 `BaseContractTest`（Assembler 單元測試用的基類）
- 直接調用 `repository.findAll(query)`
- 沒有測試完整的 API 流程 (Controller → Service → Repository)
- 沒有角色權限驗證
- 手動組裝 QueryGroup，跳過 Service 層邏輯

✅ **正確的業務合約測試應該：**
- 繼承 `BaseApiContractTest`（API 合約測試基類）
- 使用 `@SpringBootTest` + `@AutoConfigureMockMvc`
- 使用 `MockMvc` 調用實際 API 端點
- 使用 `ArgumentCaptor` 攔截傳給 Repository 的 QueryGroup
- 驗證完整流程：`Controller → Service → QueryBuilder → QueryGroup → Repository`
- 支援角色權限驗證（`@WithMockUser`）

---

## 🎯 修正目標

| 模組代碼 | 服務名稱 | 測試檔案 | 狀態 | 負責人 |
|:---:|:---|:---|:---:|:---|
| HR01 | IAM 服務 | `IamContractTest.java` → `IamApiContractTest.java` | ✅ 已完成 | Claude |
| HR03 | 考勤服務 | `AttendanceContractTest.java` → `AttendanceApiContractTest.java` | ⏳ 待修正 | - |
| HR04 | 薪資服務 | `PayrollContractTest.java` → `PayrollApiContractTest.java` | ⏳ 待修正 | - |
| HR05 | 保險服務 | `InsuranceContractTest.java` → `InsuranceApiContractTest.java` | ⏳ 待修正 | - |
| HR07 | 工時表服務 | `TimesheetContractTest.java` → `TimesheetApiContractTest.java` | ⏳ 待修正 | - |

---

## 📝 修正步驟

### Step 1: 檢查合約文件是否存在

**位置:** `contracts/{service}_contracts_v2.md`

**檢查項目:**
- [ ] 檔案是否存在
- [ ] 是否使用新版 v2 雙層結構
- [ ] 機器可讀表格是否包含 `API 端點` 欄位
- [ ] 是否移除了不存在的欄位（如 `is_deleted`）
- [ ] 合約規格是否與系統設計書、API 規格一致

**若不存在或不一致：**
1. 參考 `contracts/iam_contracts_v2.md` 的格式
2. 對照 `knowledge/02_System_Design/{NN}_{Service}服務系統設計書.md`
3. 對照 `knowledge/04_API_Specifications/{NN}_{Service}_API詳細規格.md`
4. 重新建立或更新合約文件

---

### Step 2: 檢查 pom.xml 依賴

**檔案:** `backend/hrms-{service}/pom.xml`

**必要依賴:**

```xml
<!-- Spring Security Test (用於 @WithMockUser) -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- MockMvc -->
<!-- 已包含在 spring-boot-starter-test 中 -->
```

**檢查清單:**
- [ ] `spring-security-test` 已加入
- [ ] Maven 依賴已更新 (`mvn clean install`)

---

### Step 3: 建立正確的 API 合約測試

**新檔案位置:** `src/test/java/com/company/hrms/{service}/api/contract/{Service}ApiContractTest.java`

#### 3.1 建立測試類別架構

```java
package com.company.hrms.{service}.api.contract;

import com.company.hrms.common.test.contract.BaseApiContractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {Service} 服務業務合約測試
 *
 * 測試範圍：完整 API 流程 (Controller → Service → QueryGroup → Repository)
 * 測試方法：攔截 QueryGroup，驗證是否符合 contracts/{service}_contracts_v2.md
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("{Service} 服務業務合約測試")
class {Service}ApiContractTest extends BaseApiContractTest {

    @MockBean
    private {Entity}Repository repository;

    // 測試方法...
}
```

#### 3.2 實作查詢操作測試

**範例：測試場景 XXX_QRY_001**

```java
@Test
@DisplayName("XXX_QRY_001: [測試描述]")
@WithMockUser(roles = "ADMIN") // 根據合約定義的角色
void testScenario_XXX_QRY_001() throws Exception {
    // 1. 載入合約規格
    String contractSpec = loadContractSpec("{service}");

    // 2. 準備請求
    Get{Entity}ListRequest request = Get{Entity}ListRequest.builder()
        .{field}({value})
        .build();

    // 3. 建立 QueryGroup 攔截器
    ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
    when(repository.findPage(queryCaptor.capture(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.emptyList()));

    // 4. 執行 API 調用
    mockMvc.perform(get("/api/v1/{endpoint}")
            .param("{field}", "{value}")
            .requestAttr("currentUser", mockAdminUser)
            .contentType("application/json"))
        .andExpect(status().isOk());

    // 5. 驗證合約
    QueryGroup capturedQuery = queryCaptor.getValue();
    assertContract(capturedQuery, contractSpec, "XXX_QRY_001");
}
```

#### 3.3 實作命令操作測試（如適用）

**範例：測試場景 XXX_CMD_001**

```java
@Test
@DisplayName("XXX_CMD_001: [測試描述]")
@WithMockUser(roles = "ADMIN")
void testScenario_XXX_CMD_001() throws Exception {
    // 1. 準備請求
    Create{Entity}Request request = Create{Entity}Request.builder()
        .{field}({value})
        .build();

    // 2. 執行 API 調用
    mockMvc.perform(post("/api/v1/{endpoint}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // 3. 驗證 Domain Event 發布（如合約有定義）
    verify(eventPublisher).publish(argThat(event ->
        event instanceof {Entity}CreatedEvent
    ));
}
```

---

### Step 4: 處理舊測試檔案

**決策點：是否保留舊的 `{Service}ContractTest.java`？**

#### 方案 A：刪除舊測試（建議）

**理由：**
- 舊測試是錯誤的實作，沒有測試價值
- 避免混淆，確保只有正確的測試存在

**步驟：**
1. 刪除 `src/test/java/com/company/hrms/{service}/application/service/contract/{Service}ContractTest.java`
2. 在修正報告中記錄刪除原因

#### 方案 B：重構為 Assembler 單元測試

**理由：**
- 想保留測試覆蓋率
- Assembler 單元測試執行速度更快

**步驟：**
1. 重新命名：`{Service}ContractTest.java` → `{Entity}QueryAssemblerTest.java`
2. 修改繼承：`extends BaseContractTest`（保持不變）
3. 修改測試目的：從「合約測試」改為「Assembler 單元測試」
4. 移除合約斷言，改用快照比對

**重構範例：**

```java
/**
 * {Entity}QueryAssembler 單元測試
 *
 * 測試範圍：僅測試 Assembler.toQueryGroup() 方法
 * 測試方法：快照比對
 */
class {Entity}QueryAssemblerTest extends BaseContractTest {

    @Test
    @DisplayName("組裝查詢條件：依狀態過濾")
    void assembleQuery_ByStatus_ShouldMatchSnapshot() {
        // 1. 準備 Request
        Get{Entity}ListRequest request = Get{Entity}ListRequest.builder()
            .status("ACTIVE")
            .build();

        // 2. 組裝 QueryGroup
        QueryGroup query = {Entity}QueryAssembler.toQueryGroup(request);

        // 3. 快照比對
        assertMatchesSnapshot("query/{entity}/by_status.json", query);
    }
}
```

---

### Step 5: 執行測試並修正

#### 5.1 執行測試

```bash
cd backend/hrms-{service}
mvn clean test -Dtest={Service}ApiContractTest
```

#### 5.2 分析測試結果

**可能的失敗原因：**

| 失敗原因 | 判斷方式 | 修正方向 |
|:---|:---|:---|
| **合約規格錯誤** | 合約定義的欄位在 Entity 中不存在 | 修正合約文件 |
| **API 實作缺少過濾條件** | Service 沒有加入合約要求的過濾條件 | 修正 Service 實作 |
| **參數映射錯誤** | Request DTO 缺少 `@QueryFilter` 註解 | 修正 Request DTO |
| **權限控制未實作** | 缺少租戶隔離或角色過濾 | 修正 Service 權限邏輯 |

#### 5.3 修正策略

**原則：先判斷是「合約錯誤」還是「實作錯誤」**

1. **若合約錯誤：**
   - 在測試方法中加入 `@Disabled("TODO: 合約規格錯誤 - {原因}")`
   - 在合約文件中加入 `<!-- TODO: 待修正 - {原因} -->`
   - 記錄在修正報告中

2. **若實作錯誤：**
   - 修正 Service/Domain 層邏輯
   - 重新執行測試直到通過

---

### Step 6: 建立修正報告

**檔案位置:** `backend/hrms-{service}/HR{NN}_CONTRACT_TEST_FIX_REPORT.md`

**報告範本：**

```markdown
# HR{NN} {Service} 服務業務合約測試修正報告

**日期:** {date}
**修正人員:** {name}
**狀態:** ✅ 完成 / ⏳ 進行中 / ❌ 待解決

---

## 📋 修正摘要

### 問題描述
[描述原有測試的問題]

### 修正內容
✅ [修正項目 1]
✅ [修正項目 2]
...

---

## 📊 測試結果

### 執行結果
```
Tests run: {total}, Failures: {failures}, Errors: {errors}, Skipped: {skipped}
```

### 通過的測試 ({passed}/{total})
- ✅ {scenario_id}: {description}
- ...

### 失敗的測試 ({failed}/{total})

#### ❌ {scenario_id}: {description}
**失敗原因:** {reason}
**根本原因:** {root_cause}
**修正方案:** {solution}

---

## 🔧 待解決問題

[列出待解決的問題]

---

## 📁 相關檔案

### 新增檔案
- ✅ `src/test/java/.../api/contract/{Service}ApiContractTest.java`

### 修改檔案
- ✅ `pom.xml` - 加入 spring-security-test

### 待處理檔案
- ⏳ `contracts/{service}_contracts_v2.md` - 可能需要修正

---

## ✅ 結論

[總結修正結果與下一步行動]
```

---

## ⚠️ 重要注意事項

### 🔴 必須遵守的原則

1. **欄位存在性檢查**
   - ❌ **絕對不要**在合約中定義資料庫或 Entity 中不存在的欄位
   - ✅ **必須確認**合約中的每個欄位都在 Entity/DTO 中實際存在
   - 📋 **檢查清單:**
     - 對照 Entity 類別定義
     - 對照 DTO 類別定義
     - 對照資料庫 Schema

2. **合約與設計書一致性**
   - 合約規格必須與系統設計書、API 規格一致
   - 若發現不一致，優先以系統設計書為準
   - 更新合約後需通知 SA 確認

3. **測試層級區分**
   - **API 合約測試** = 完整流程測試（Controller → Service → Repository）
   - **Assembler 單元測試** = 僅測試 Assembler.toQueryGroup()
   - **不要混淆**這兩種測試的目的與實作方式

4. **角色權限驗證**
   - 使用 `@WithMockUser(roles = "ROLE_NAME")` 模擬不同角色
   - 驗證多租戶隔離：`tenant_id = '{currentUserTenantId}'`
   - 驗證軟刪除過濾：`status != 'DELETED'`

5. **合約測試不是 SQL 測試**
   - **重點是業務案例**，而不是 SQL 生成
   - 驗證「必須包含的過濾條件」是否存在
   - 不需要驗證 SQL 語法的正確性（那是 QueryEngine 的責任）

### 🟡 建議注意事項

6. **測試獨立性**
   - 每個測試應該獨立，不依賴其他測試的執行順序
   - 使用 `@MockBean` 模擬 Repository，避免依賴資料庫

7. **合約文件維護**
   - 合約文件由 SA 維護
   - 發現合約錯誤時，標記 TODO 並通知 SA
   - 不要擅自修改合約規格

8. **測試資料準備**
   - 使用 Builder Pattern 建立 Request
   - Mock Repository 回傳空清單或固定資料
   - 不需要準備完整的測試資料集

9. **錯誤訊息清晰度**
   - 使用 `@DisplayName` 提供清晰的測試名稱
   - 測試失敗時，錯誤訊息應明確指出哪個合約條件未滿足

10. **版本控制**
    - 新舊測試檔案的變更應該在同一個 commit
    - Commit message 格式：`test(hrms-{service}): 修正業務合約測試`

### 🟢 效能優化建議

11. **測試執行速度**
    - API 合約測試會啟動 Spring Context，執行較慢
    - 考慮使用 `@TestConfiguration` 只載入必要的 Bean
    - 避免重複啟動 Spring Context（將測試放在同一個測試類別）

12. **Mock 最小化**
    - 只 Mock Repository 層
    - Controller 和 Service 使用實際實例
    - 避免過度 Mock，降低測試的真實性

---

## 📋 修正檢查清單

使用此檢查清單確保修正完整：

### 合約文件檢查

- [ ] `contracts/{service}_contracts_v2.md` 存在
- [ ] 使用新版雙層結構（機器可讀 + 人類可讀）
- [ ] 機器可讀表格包含 `API 端點` 欄位
- [ ] 移除不存在的欄位（如 `is_deleted`）
- [ ] 合約與系統設計書一致
- [ ] 合約與 API 規格一致

### 測試檔案檢查

- [ ] 建立 `{Service}ApiContractTest.java`
- [ ] 繼承 `BaseApiContractTest`
- [ ] 使用 `@SpringBootTest` + `@AutoConfigureMockMvc`
- [ ] 使用 `@MockBean` 模擬 Repository
- [ ] 使用 `MockMvc` 調用 API
- [ ] 使用 `ArgumentCaptor` 攔截 QueryGroup
- [ ] 使用 `@WithMockUser` 模擬角色
- [ ] 處理舊測試檔案（刪除或重構）

### 依賴檢查

- [ ] `pom.xml` 加入 `spring-security-test`
- [ ] Maven 依賴已更新

### 測試執行檢查

- [ ] 測試可以執行
- [ ] 分析測試失敗原因
- [ ] 區分「合約錯誤」vs「實作錯誤」
- [ ] 標記 TODO 或修正實作
- [ ] 所有測試通過或有明確的 TODO 標記

### 文件檢查

- [ ] 建立修正報告 `HR{NN}_CONTRACT_TEST_FIX_REPORT.md`
- [ ] 記錄修正摘要
- [ ] 記錄測試結果
- [ ] 記錄待解決問題
- [ ] 記錄相關檔案

---

## 🔗 參考資源

### 文件

- [contracts/README.md](README.md) - 合約文件結構說明
- [framework/testing/04_合約驅動測試.md](../framework/testing/04_合約驅動測試.md) - 合約驅動測試方法論
- [framework/testing/測試架構規範.md](../framework/testing/測試架構規範.md) - 完整測試架構規範
- [backend/hrms-iam/HR01_CONTRACT_TEST_FIX_REPORT.md](../backend/hrms-iam/HR01_CONTRACT_TEST_FIX_REPORT.md) - HR01 修正範例

### 範例程式碼

- [IamApiContractTest.java](../backend/hrms-iam/src/test/java/com/company/hrms/iam/api/contract/IamApiContractTest.java) - HR01 正確的 API 合約測試
- [iam_contracts_v2.md](iam_contracts_v2.md) - HR01 合約文件範例

---

## 📅 修正時程規劃

| 模組 | 預估時間 | 優先級 | 備註 |
|:---:|:---:|:---:|:---|
| HR03 考勤服務 | 4 小時 | P1 | 業務邏輯較複雜（請假、加班） |
| HR04 薪資服務 | 6 小時 | P1 | 計算邏輯複雜，需仔細驗證 |
| HR05 保險服務 | 3 小時 | P2 | 規則較明確，相對簡單 |
| HR07 工時表服務 | 4 小時 | P1 | 與專案整合，需確認關聯邏輯 |

**總預估時間:** 17 小時

---

## 🚀 未來正確的開發流程（避免重蹈覆轍）

### ⚠️ 為什麼會出現這次的錯誤

**根本原因：開發流程順序錯誤**

```
❌ 錯誤的流程（目前的問題）：
   1. 先開發功能實作
   2. 後來補寫合約文件
   3. 再補寫測試
   4. 測試只測 Repository 層（最容易寫的部分）
   5. 結果：測試與合約脫節，沒有驗證完整流程

✅ 正確的流程（Contract-First + TDD）：
   1. SA 定義業務合約文件
   2. 開發者依合約建立測試（測試先行）
   3. 開發者實作功能（讓測試通過）
   4. 結果：測試驗證完整 API 流程，符合業務合約
```

### 📋 正確的開發流程（10 個階段）

#### 階段 1-4: 需求與合約（開發前準備）

```
階段 1: SA 完成需求分析書
階段 2: 架構師完成系統設計書
階段 3: 架構師完成 API 規格文件
階段 4: SA + 開發團隊共同撰寫業務合約文件 ⭐

🔒 Checkpoint: Contract Review & Freeze
   - SA 確認業務場景完整
   - 開發團隊確認可測試性
   - 合約與設計書、API 規格一致
   - 🔒 合約凍結（不再修改）
```

#### 階段 5: 測試先行（TDD Red）⭐⭐⭐

**重點：先寫測試，測試一定會失敗！**

```java
// Step 1: 依據合約建立測試類別
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AttendanceApiContractTest extends BaseApiContractTest {

    @MockBean
    private LeaveApplicationRepository repository;

    // Step 2: 依據合約場景撰寫測試方法
    @Test
    @DisplayName("ATT_QRY_001: 查詢員工請假記錄")
    @WithMockUser(roles = "EMPLOYEE")
    void scenario_ATT_QRY_001() throws Exception {
        // 1. 載入合約
        String contractSpec = loadContractSpec("attendance");

        // 2. 準備請求
        GetLeaveApplicationsRequest request = GetLeaveApplicationsRequest.builder()
            .employeeId("E001")
            .build();

        // 3. 建立 QueryGroup 攔截器
        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
        when(repository.findPage(queryCaptor.capture(), any()))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        // 4. 執行 API
        mockMvc.perform(get("/api/v1/leave-applications")
                .param("employeeId", "E001")
                .contentType("application/json"))
            .andExpect(status().isOk());

        // 5. 驗證合約
        QueryGroup query = queryCaptor.getValue();
        assertContract(query, contractSpec, "ATT_QRY_001");
    }
}
```

**執行測試：**
```bash
mvn test -Dtest=AttendanceApiContractTest
```

**預期結果：❌ 全部失敗**
```
Tests run: 10, Failures: 10, Errors: 0, Skipped: 0

原因：Controller、Service、Repository 都還沒實作
這是正常的！TDD 就是要先寫測試，讓測試失敗
```

**🔒 Checkpoint: Test Code Review**
- Code Review 測試程式
- 確認測試覆蓋所有合約場景
- 確認測試程式正確（雖然會失敗）
- Commit 測試程式（允許測試失敗）

#### 階段 6-7: 功能開發（TDD Green → Refactor）

**階段 6: 實作功能（讓測試通過）**

```
實作順序（由內而外）：

1️⃣ Domain Layer
   - LeaveApplication (Aggregate Root)
   - LeaveType (Value Object)
   - LeaveAppliedEvent (Domain Event)

2️⃣ Application Layer
   - GetLeaveApplicationsServiceImpl (Query Service)
   - ApplyLeaveServiceImpl (Command Service)

3️⃣ Infrastructure Layer
   - LeaveApplicationRepositoryImpl
   - LeaveApplicationMapper (MyBatis)

4️⃣ Interface Layer
   - HR03LeaveQryController
   - HR03LeaveCmdController
   - Request/Response DTOs
```

**每完成一個場景，執行對應測試：**
```bash
# 測試 ATT_QRY_001
mvn test -Dtest=AttendanceApiContractTest#scenario_ATT_QRY_001

# 結果：❌ → ✅（從失敗變成通過）
```

**階段 7: 重構（在測試保護下）**

```
持續執行測試，確保重構不破壞功能：
mvn test -Dtest=AttendanceApiContractTest

結果：✅ 全部通過（重構沒有破壞功能）
```

#### 階段 8-10: 驗證與交付

```
階段 8: 執行完整合約測試套件
   mvn test -Dtest=*ApiContractTest
   ✅ 全部通過

階段 9: Code Review & PR
   - Tech Lead Review
   - 確認合約測試通過
   - Merge to main branch

階段 10: CI/CD 自動驗證
   - 自動執行合約測試
   - 部署到測試環境
```

### 🎯 關鍵差異對照表

| 項目 | ❌ 錯誤流程（本次問題） | ✅ 正確流程（Contract-First + TDD） |
|:---|:---|:---|
| **開發順序** | 實作 → 測試 → 合約 | 合約 → 測試 → 實作 |
| **測試類型** | Repository 測試 | API 合約測試 |
| **測試基類** | `BaseContractTest` | `BaseApiContractTest` |
| **測試範圍** | 只測 Repository 層 | 測試完整 API 流程 |
| **測試時機** | 功能完成後補測試 | 功能開發前先寫測試 |
| **測試狀態** | 一開始就通過（測試太簡單） | 一開始必定失敗（預期行為） |
| **合約驗證** | 手動檢查 | 自動驗證（assertContract） |
| **驗證點** | 僅驗證 QueryGroup 組裝 | 驗證 Controller → Service → Repository |

### 📌 未來開發檢查清單

**開始新功能開發前，確認：**

- [ ] ✅ 業務合約文件已完成（`contracts/{service}_contracts_v2.md`）
- [ ] ✅ 合約已經過 SA + Tech Lead Review
- [ ] ✅ 合約已凍結（Contract Freeze）
- [ ] ✅ 確認合約與系統設計書、API 規格一致

**開始寫程式前，確認：**

- [ ] ✅ 先建立測試類別（`{Service}ApiContractTest.java`）
- [ ] ✅ 依據合約撰寫所有測試方法
- [ ] ✅ 執行測試，確認全部失敗（❌ Red）
- [ ] ✅ 測試程式已通過 Code Review
- [ ] ❌ **此時還不能寫功能實作！**

**寫功能實作時：**

- [ ] ✅ 先寫 Domain Layer（業務邏輯）
- [ ] ✅ 再寫 Application Layer（Service）
- [ ] ✅ 再寫 Infrastructure Layer（Repository）
- [ ] ✅ 最後寫 Interface Layer（Controller）
- [ ] ✅ 每完成一個場景，執行對應測試（❌ → ✅）

**功能完成後：**

- [ ] ✅ 所有合約測試通過（全綠 ✅）
- [ ] ✅ 執行完整測試套件
- [ ] ✅ Code Review
- [ ] ✅ Merge to main

### 🚫 禁止事項（避免重蹈覆轍）

1. **🚫 禁止：功能先行，測試後補**
   - ❌ 先寫完 Controller/Service，再補測試
   - ✅ 先寫測試，再寫實作

2. **🚫 禁止：只測 Repository 層**
   - ❌ 直接調用 `repository.findAll(query)`
   - ✅ 使用 `MockMvc` 調用完整 API

3. **🚫 禁止：手動組裝 QueryGroup**
   - ❌ 測試中手動建立 `QueryBuilder.where()...`
   - ✅ 攔截 Service 產出的 QueryGroup

4. **🚫 禁止：跳過合約定義**
   - ❌ 沒有合約就開始寫程式
   - ✅ 必須先有合約，才能開發

5. **🚫 禁止：合約凍結後隨意修改**
   - ❌ 開發中途發現不對，就直接改合約
   - ✅ 提出變更申請，經 SA Review 後才能改

---

**最後更新:** 2026-02-06
**維護者:** 開發團隊
