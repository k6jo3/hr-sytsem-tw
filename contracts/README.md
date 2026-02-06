# 業務合約文件說明 (Business Contract Documentation Guide)

**版本:** 2.0
**最後更新:** 2026-02-06
**狀態:** 生效中

---

## 📋 目錄

1. [什麼是業務合約文件](#1-什麼是業務合約文件)
2. [合約文件的構成](#2-合約文件的構成)
3. [合約文件的作用](#3-合約文件的作用)
4. [開發順序](#4-開發順序)
5. [如何使用合約文件](#5-如何使用合約文件)
6. [注意事項](#6-注意事項)
7. [文件清單](#7-文件清單)

---

## 1. 什麼是業務合約文件

### 1.1 定義

**業務合約文件**是連接**業務需求**與**技術實作**的橋樑，它定義了：
- ✅ **業務場景**：系統應該做什麼（What）
- ✅ **業務規則**：必須遵守的規則（Rules）
- ✅ **領域事件**：系統會發生什麼事件（Events）
- ✅ **測試斷言**：如何驗證實作是否正確（Assertions）

### 1.2 在開發流程中的定位

```
需求分析書 (SA 產出)
    ↓ 定義業務需求
系統設計書 (設計師產出)
    ↓ 定義技術設計
API 規格文件 (設計師產出)
    ↓ 定義 API 介面
業務合約文件 ⭐ (SA + 開發團隊產出)
    ↓ 定義業務場景與測試規格
開發實作 (開發人員產出)
    ↓ 寫程式
合約測試 (開發人員產出)
    ↓ 驗證是否符合業務合約
```

### 1.3 為什麼需要業務合約文件

**沒有業務合約文件的問題：**
- ❌ 開發人員不知道如何驗證功能是否正確
- ❌ 需求、設計、實作三者容易脫節
- ❌ 測試只能驗證「程式不會壞」，無法驗證「符合業務需求」
- ❌ 回歸測試困難，無法確認修改是否影響既有功能

**有業務合約文件的好處：**
- ✅ 明確的驗收標準
- ✅ 自動化驗證業務規則
- ✅ 需求、設計、測試三者一致
- ✅ 回歸測試自動化
- ✅ 新人可以快速理解業務邏輯

---

## 2. 合約文件的構成

### 2.1 文件結構

每個服務的合約文件包含以下部分：

```markdown
# {服務名稱} 業務合約

## 1. Command 操作業務合約
   ### 1.1 {功能模組} Command
       #### CMD_001: {場景名稱}
       - 業務場景描述
       - API 端點
       - 前置條件
       - 業務規則驗證
       - 必須發布的領域事件
       - 測試驗證點

## 2. Query 操作業務合約
   ### 2.1 {功能模組}查詢合約表（Machine-Readable）
       [結構化表格 - 給測試程式用]

   ### 2.2 {功能模組}查詢場景詳細說明
       #### QRY_001: {場景名稱}
       - 業務場景描述
       - API 端點
       - 權限要求
       - 必須包含的過濾條件
       - 測試斷言

## 3. 領域事件合約
   [事件清單表格]

## 4. 測試斷言規格
   [測試模板]
```

### 2.2 核心組成部分

#### A. Command 操作合約（新增、修改、刪除）

**必須包含：**
- **場景 ID**：唯一識別碼（例如：IAM_CMD_001）
- **業務場景描述**：這個操作的業務目的
- **API 端點**：對應的 API 端點
- **前置條件**：執行前必須滿足的條件
- **業務規則驗證**：必須驗證的業務規則清單
- **必須發布的領域事件**：操作完成後必須發布的事件
- **後置條件**：執行後系統的狀態
- **測試驗證點**：如何驗證操作成功

**範例：**
```markdown
#### IAM_CMD_001: 建立使用者

**業務場景描述：**
HR 管理員建立新的系統使用者帳號。

**API 端點：** POST /api/v1/users

**前置條件：**
- 執行者必須擁有 `user:create` 權限
- employeeId 必須存在於 Organization Service

**業務規則驗證：**
1. ✅ username 唯一性檢查
2. ✅ 初始密碼生成（至少 8 字元）
3. ✅ 至少指派一個角色

**必須發布的領域事件：**
UserCreatedEvent

**測試驗證點：**
- [ ] 資料庫驗證：users 表有新記錄
- [ ] 事件驗證：UserCreatedEvent 已發布
```

#### B. Query 操作合約（查詢）

**必須包含兩部分：**

**B1. 結構化合約表格（給測試程式用）**

```markdown
| 場景 ID | 測試描述 | 模擬角色 | API 端點 | 輸入 | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| IAM_QRY_001 | 查詢啟用中的使用者 | ADMIN | GET /api/v1/users | {"status":"ACTIVE"} | status = 'ACTIVE', tenant_id = '{currentUserTenantId}' |
```

**重要：** 此表格可被 `MarkdownContractEngine` 自動解析驗證

**B2. 詳細場景說明（給開發人員看）**

```markdown
#### IAM_QRY_001: 查詢啟用中的使用者

**業務場景描述：**
ADMIN 查詢所有啟用中的使用者列表。

**必須包含的過濾條件：**
- status = 'ACTIVE'
- tenant_id = '{currentUserTenantId}'（自動加上）

**不應包含的過濾條件：**
- ❌ is_deleted = 0（欄位不存在）
```

#### C. 領域事件合約

**必須包含：**
- **事件名稱**
- **觸發場景**（對應到哪個 Command）
- **Schema 必須包含**（事件 Payload 必須有的欄位）
- **訂閱服務**（哪些服務會訂閱此事件）

**範例：**
```markdown
| 事件名稱 | 觸發場景 | Schema 必須包含 | 訂閱服務 |
|:---|:---|:---|:---|
| UserCreatedEvent | IAM_CMD_001 建立使用者 | userId, username, email, employeeId, roleIds | Organization, Notification |
```

---

## 3. 合約文件的作用

### 3.1 對開發人員

- ✅ **明確的實作規格**：知道要做什麼、怎麼驗證
- ✅ **測試指引**：知道要寫哪些測試案例
- ✅ **業務理解**：理解每個功能的業務目的

### 3.2 對測試程式

- ✅ **自動化驗證**：`assertContract()` 自動驗證 QueryGroup
- ✅ **回歸測試**：確保修改不破壞既有功能
- ✅ **清晰的錯誤訊息**：測試失敗時顯示缺失的過濾條件

### 3.3 對 SA/PM

- ✅ **需求追溯**：確認實作符合需求
- ✅ **驗收標準**：明確的驗收標準
- ✅ **文件一致性**：確保需求、設計、實作一致

### 3.4 對系統架構

- ✅ **Event-Driven 架構驗證**：確認領域事件正確發布
- ✅ **租戶隔離驗證**：確認多租戶資料隔離
- ✅ **權限控制驗證**：確認 RBAC 正確實施

---

## 4. 開發順序（Contract-First + TDD）

### 4.1 正確的開發流程（重要！）

**核心原則：先合約 → 再測試 → 最後實作**

```
階段 1: 需求分析（SA 主導）
────────────────────────────
└─ SA 撰寫需求分析書
   └─ 定義業務需求
   └─ 定義使用者故事

階段 2: 系統設計（架構師主導）
────────────────────────────
└─ 架構師撰寫系統設計書
   └─ 定義領域模型（Entity, Value Object, Aggregate）
   └─ 定義領域事件（Domain Events）
   └─ 定義 API 架構
   └─ 定義資料表結構（Schema）

階段 3: API 規格（架構師主導）
────────────────────────────
└─ 架構師撰寫 API 規格文件
   └─ 定義每個 API 的 Request/Response
   └─ 定義業務邏輯流程
   └─ 定義錯誤碼與異常處理

🔒 Checkpoint: Design Review
   ├─ PM 確認需求正確
   ├─ Tech Lead 確認設計可行
   └─ 文件凍結（Freeze）

階段 4: 業務合約 ⭐⭐⭐（SA + 開發團隊共同）
──────────────────────────────────────
└─ SA + 開發團隊共同撰寫業務合約文件
   │
   ├─ 從需求分析書提取業務場景
   ├─ 從系統設計書提取領域模型與事件
   ├─ 從 API 規格文件提取 API 端點
   ├─ 定義測試斷言（Assertions）
   ├─ 定義領域事件驗證規格
   └─ 定義業務規則檢查點

   產出：contracts/{service}_contracts_v2.md

🔒 Checkpoint: Contract Review
   ├─ SA 確認業務場景完整
   ├─ 開發團隊確認可測試性
   ├─ 確認合約與設計書、API 規格一致
   └─ 合約凍結（Contract Freeze）

階段 5: 測試先行 ⭐⭐（開發者 - TDD Red）
────────────────────────────────────
└─ 開發人員依據業務合約撰寫測試程式
   │
   ├─ 建立 {Service}ApiContractTest.java
   ├─ 實作所有合約場景的測試方法
   ├─ 配置 MockMvc、MockBean、ArgumentCaptor
   └─ 執行測試 → ❌ 全部失敗（預期行為！）

   產出：*ApiContractTest.java（測試全紅）

   ⚠️ 重要：此時功能尚未實作，測試失敗是正常的！
   ⚠️ 這一步的目的是「確認測試本身寫對」

🔒 Checkpoint: Test Review
   ├─ Code Review 測試程式
   ├─ 確認測試覆蓋所有合約場景
   └─ Commit 測試程式（允許測試失敗）

階段 6: 功能開發 ⭐（開發者 - TDD Green）
────────────────────────────────────
└─ 開發人員實作功能（讓測試通過）
   │
   │  實作順序（由內而外）：
   │  ├─ 1️⃣ Domain Layer - 領域邏輯、業務規則
   │  ├─ 2️⃣ Application Layer - Service、業務流程編排
   │  ├─ 3️⃣ Infrastructure Layer - Repository、外部服務呼叫
   │  └─ 4️⃣ Interface Layer - Controller、DTO
   │
   └─ 每完成一個場景，執行對應的測試
      ├─ 測試從 ❌ 變成 ✅
      └─ 持續 Commit

   產出：完整功能實作 + 測試全綠 ✅

階段 7: 重構（開發者 - TDD Refactor）
────────────────────────────────────
└─ 在測試保護下進行程式碼重構
   ├─ 消除重複程式碼
   ├─ 優化命名與結構
   ├─ 提升可讀性與可維護性
   └─ 執行測試確保功能不變

階段 8: 合約驗證（QA + 開發者）
────────────────────────────────────
└─ 執行完整的合約測試套件
   ├─ 驗證 Query 操作的過濾條件
   ├─ 驗證 Command 操作的領域事件
   ├─ 驗證業務規則執行
   ├─ 驗證權限控制（多租戶隔離、RBAC）
   └─ 驗證異常處理

   ✅ 所有合約測試通過 → 進入 Code Review

階段 9: Code Review & 合併
────────────────────────────────────
└─ Tech Lead / Senior Developer Review
   ├─ 確認程式碼品質
   ├─ 確認合約測試通過
   ├─ 確認沒有安全漏洞
   └─ 合併到主分支

階段 10: CI/CD 驗證
────────────────────────────────────
└─ 自動執行合約測試
   ├─ 單元測試
   ├─ 合約測試
   ├─ 整合測試
   └─ 部署到測試環境
```

### 4.2 開發順序的關鍵原則

#### 🔴 原則 1: 合約先行 (Contract-First)

**合約文件是開發的起點，不是終點！**

```
❌ 錯誤順序（傳統瀑布式）：
   寫實作 → 寫測試 → 檢查是否符合需求

✅ 正確順序（Contract-First + TDD）：
   寫合約 → 寫測試（失敗） → 寫實作（通過）
```

**為什麼要這樣做？**
- ✅ 避免「做了才發現理解錯誤」
- ✅ 確保測試真正驗證需求，而非「驗證實作能跑」
- ✅ 提早發現需求不明確的問題

#### 🔴 原則 2: 測試驅動開發 (Test-Driven Development)

**先寫測試，再寫實作！**

```
TDD 循環：
   Red (寫測試，必定失敗)
    ↓
   Green (寫實作，讓測試通過)
    ↓
   Refactor (在測試保護下重構)
    ↓
   回到 Red (下一個場景)
```

**重要：測試失敗是好事！**
- ❌ 如果測試一開始就通過 → 測試可能寫錯了
- ✅ 測試失敗 → 代表測試有效，實作還沒完成

#### 🔴 原則 3: 合約必須基於正式文件

**不要憑空想像合約！**

```
❌ 錯誤：開發者自己想「應該」要有什麼過濾條件
✅ 正確：從系統設計書和 API 規格文件提取

合約的來源：
   需求分析書 → 業務場景
   系統設計書 → 領域模型、事件、資料表結構
   API 規格文件 → API 端點、Request/Response
```

**檢查清單：**
- [ ] 合約中的每個欄位都存在於資料表 Schema
- [ ] 合約中的每個 API 端點都定義於 API 規格
- [ ] 合約中的每個領域事件都定義於系統設計書

#### 🔴 原則 4: 合約與測試必須連動

**測試程式自動解析合約文件！**

```
❌ 錯誤：合約文件與測試程式各寫各的
   → 合約改了，測試沒改
   → 測試通過，但不符合合約

✅ 正確：測試程式載入合約文件並自動驗證
   → assertContract(query, contractSpec, "SCENE_001")
   → 合約改了，測試自動跟著改
```

#### 🔴 原則 5: 外部驗證 (Non-Self-Validation)

**避免「自己測試自己」！**

```
❌ 錯誤：開發者寫實作，開發者寫測試，開發者定義驗證規則
   → 三者容易「串通」，測試失去意義

✅ 正確：SA 定義合約（外部規範），開發者實作與測試
   → 測試失敗 = 不符合 SA 定義的業務需求
   → 測試成為「外部法規」的自動化驗證
```

#### 🟡 原則 6: 合約凍結 (Contract Freeze)

**合約定義後不應隨意修改！**

```
合約凍結時機：
   1. SA 確認業務場景完整
   2. 開發團隊確認可測試性
   3. 確認與設計書、API 規格一致
   ↓
   🔒 合約凍結 (Contract Freeze)
   ↓
   開始撰寫測試與實作

如需修改合約：
   1. 提出變更申請
   2. SA + Tech Lead Review
   3. 評估影響範圍
   4. 更新合約 → 更新測試 → 更新實作
```

### 4.3 檢查點

在每個階段結束時，必須檢查：

| 階段 | 檢查項目 | 負責人 |
|:---|:---|:---|
| 階段 3 完成 | API 規格文件是否與系統設計書一致？ | 架構師 |
| 階段 4 完成 | 業務合約是否涵蓋所有 API？ | SA + Tech Lead |
| 階段 4 完成 | 業務合約是否涵蓋所有領域事件？ | SA + Tech Lead |
| 階段 4 完成 | 合約表格格式是否正確？ | Tech Lead |
| 階段 5 完成 | 測試是否基於業務合約？ | 開發人員 |
| 階段 6 完成 | 合約測試是否全部通過？ | QA + 開發人員 |

---

## 5. 如何使用合約文件

### 5.1 開發人員使用合約文件

**Step 1: 閱讀業務場景**

找到要實作的功能對應的合約場景，例如 `IAM_CMD_001: 建立使用者`

**Step 2: 理解業務規則**

閱讀「業務規則驗證」部分，了解必須驗證什麼。

**Step 3: 寫測試（TDD）**

基於「測試驗證點」寫測試：
```java
@Test
void createUser_ShouldPublishEvent() {
    // 基於合約的測試驗證點
    // - [ ] 資料庫驗證：users 表有新記錄
    // - [ ] 事件驗證：UserCreatedEvent 已發布
}
```

**Step 4: 寫實作**

讓測試通過。

**Step 5: 執行合約測試**

確認符合業務合約。

### 5.2 測試人員使用合約文件

**Step 1: 載入合約規格**
```java
contractSpec = loadContractSpec("iam");
```

**Step 2: 寫合約測試**
```java
@Test
void searchActiveUsers_ShouldMatchContract() {
    // Act
    mockMvc.perform(get("/api/v1/users?status=ACTIVE")...);

    // Assert - 自動驗證合約
    assertContract(query, contractSpec, "IAM_QRY_001");
}
```

**Step 3: 執行並檢查結果**

如果測試失敗，會顯示詳細的錯誤訊息：
```
╔══════════════════════════════════════════════════════════════╗
║                    合約驗證失敗                               ║
╠══════════════════════════════════════════════════════════════╣
║ 場景 ID: IAM_QRY_001                                          ║
╠══════════════════════════════════════════════════════════════╣
║ 缺失的過濾條件:                                               ║
║   ❌ tenant_id = '{currentUserTenantId}'                      ║
╚══════════════════════════════════════════════════════════════╝
```

### 5.3 SA/PM 使用合約文件

**驗收時檢查：**
- [ ] 所有業務場景都有對應的合約定義
- [ ] 所有合約測試都通過
- [ ] 領域事件正確發布
- [ ] 業務規則正確驗證

---

## 6. 注意事項

### 6.1 合約文件撰寫注意事項

#### ⚠️ 注意事項 1: 合約必須基於實際的資料模型

**❌ 錯誤：**
```markdown
必須包含的過濾條件: is_deleted = 0
```
→ 但實際資料表沒有 `is_deleted` 欄位

**✅ 正確：**
先檢查系統設計書中的資料表結構，確認欄位存在。

#### ⚠️ 注意事項 2: 不要憑空產生欄位

**檢查清單：**
- [ ] 合約中的每個欄位都存在於資料表
- [ ] 合約中的每個過濾條件都可以實作
- [ ] 合約中的每個事件都定義於系統設計書

#### ⚠️ 注意事項 3: Command 和 Query 分開定義

**Command 操作：**
- 重點在**業務規則驗證**和**領域事件發布**
- 測試重點：資料庫 + 事件

**Query 操作：**
- 重點在**過濾條件**和**權限控制**
- 測試重點：QueryGroup 驗證

#### ⚠️ 注意事項 4: 保持結構化表格與詳細說明一致

**結構化表格（給測試程式）：**
```markdown
| IAM_QRY_001 | ... | status = 'ACTIVE', tenant_id = '{currentUserTenantId}' |
```

**詳細說明（給開發人員）：**
```markdown
必須包含的過濾條件：
- status = 'ACTIVE'
- tenant_id = '{currentUserTenantId}'
```

→ **兩者必須完全一致！**

#### ⚠️ 注意事項 5: 明確標註 TODO

如果某些實作細節尚未確定，明確標註：

```markdown
#### IAM_QRY_006: 查詢特定部門的使用者

⚠️ TODO: 需要與架構師確認實作方案
- 方案 A: 跨服務查詢（呼叫 Organization Service）
- 方案 B: 冗餘資料（在 User 表新增 department_id）
```

### 6.2 合約測試注意事項

#### ⚠️ 注意事項 1: 測試要測完整流程

**❌ 錯誤：**
```java
// 只測試 Repository
QueryGroup query = QueryBuilder.where().eq("status", "ACTIVE").build();
List<User> result = userRepository.findAll(query);
```

**✅ 正確：**
```java
// 測試完整的 API → Service → Repository 流程
mockMvc.perform(get("/api/v1/users?status=ACTIVE")...)
    .andExpect(status().isOk());

// 驗證傳給 Repository 的 QueryGroup
assertContract(query, contractSpec, "IAM_QRY_001");
```

#### ⚠️ 注意事項 2: 使用 @MockBean 模擬 Repository

```java
@MockBean
private IUserRepository userRepository;

@Test
void test() {
    ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
    when(userRepository.findPage(queryCaptor.capture(), any()))
        .thenReturn(new PageImpl<>(Collections.emptyList()));

    // ... 執行 API

    // 驗證 QueryGroup
    QueryGroup query = queryCaptor.getValue();
    assertContract(query, contractSpec, "IAM_QRY_001");
}
```

#### ⚠️ 注意事項 3: Command 操作要驗證事件

```java
@Test
void createUser_ShouldPublishEvent() {
    // Arrange
    ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);

    // Act
    mockMvc.perform(post("/api/v1/users")...)
        .andExpect(status().isOk());

    // Assert - 驗證事件
    verify(eventPublisher).publish(eventCaptor.capture());
    DomainEvent event = eventCaptor.getValue();
    assertThat(event.getEventType()).isEqualTo("UserCreatedEvent");
}
```

#### ⚠️ 注意事項 4: 測試失敗時，先確認是哪邊錯

**測試失敗的可能原因：**
1. **合約文件定義錯誤** - 定義了不存在的欄位
2. **實作邏輯錯誤** - 程式沒有加上必要的過濾條件
3. **測試程式錯誤** - 測試本身寫錯

**排查順序：**
```
1. 檢查合約文件 - 欄位是否存在於資料表？
2. 檢查實作邏輯 - Service 是否正確組裝 QueryGroup？
3. 檢查測試程式 - Mock 是否正確設定？
```

### 6.3 維護注意事項

#### ⚠️ 注意事項 1: 需求變更時，同步更新合約

```
需求變更 → 更新需求分析書 → 更新系統設計書 → 更新 API 規格 → 更新業務合約 → 更新測試
```

#### ⚠️ 注意事項 2: 定期檢查合約與文件的一致性

建議每個 Sprint 結束時檢查：
- [ ] 業務合約是否與系統設計書一致？
- [ ] 業務合約是否涵蓋所有 API？
- [ ] 所有合約測試是否通過？

#### ⚠️ 注意事項 3: 合約文件的版本控制

- 重大變更時，更新版本號（例如：v2.0）
- 在變更歷史中記錄變更原因
- 保留舊版本作為參考（例如：iam_contracts_v1_deprecated.md）

---

## 7. 文件清單

### 7.1 現有合約文件

| 服務代碼 | 服務名稱 | 合約文件 | 版本 | 狀態 |
|:---:|:---|:---|:---:|:---|
| 01 | IAM | `iam_contracts_v2.md` | 2.0 | ✅ 已重建 |
| 02 | Organization | `organization_contracts.md` | 1.0 | ⚠️ 待重建 |
| 03 | Attendance | `attendance_contracts.md` | 1.0 | ⚠️ 待重建 |
| 04 | Payroll | `payroll_contracts.md` | 1.0 | ⚠️ 待重建 |
| 05 | Insurance | `insurance_contracts.md` | 1.0 | ⚠️ 待重建 |
| 06 | Project | `project_contracts.md` | 1.0 | ⚠️ 待重建 |
| 07 | Timesheet | `timesheet_contracts.md` | 1.0 | ⚠️ 待重建 |
| 08 | Performance | `performance_contracts.md` | 1.0 | ⏳ 待檢查 |
| 09 | Recruitment | `recruitment_contracts.md` | 1.0 | ⏳ 待檢查 |
| 10 | Training | `training_contracts.md` | 1.0 | ⏳ 待檢查 |
| 11 | Workflow | `workflow_contracts.md` | 1.0 | ⏳ 待檢查 |
| 12 | Notification | `notification_contracts.md` | 1.0 | ⏳ 待檢查 |
| 13 | Document | `document_contracts.md` | 1.0 | ⏳ 待檢查 |
| 14 | Reporting | `reporting_contracts.md` | 1.0 | ⏳ 待檢查 |

### 7.2 相關文件

| 文件 | 路徑 | 說明 |
|:---|:---|:---|
| 合約測試修正工作項目 | `contracts/CONTRACT_TEST_REFACTORING_TASKS.md` | 合約測試修正的工作清單 |
| 文件一致性分析報告 | `HR01_DOCUMENT_CONSISTENCY_ANALYSIS.md` | HR01 三份文件的一致性分析 |
| 合約測試基類 | `backend/hrms-common/.../BaseContractTest.java` | 合約測試的基類 |
| Markdown 合約引擎 | `backend/hrms-common/.../MarkdownContractEngine.java` | 解析合約 Markdown 的引擎 |

---

## 8. 常見問題 (FAQ)

### Q1: 合約文件由誰負責撰寫？

**A:** SA（系統分析師）+ 開發團隊共同撰寫
- SA 負責：業務場景描述、業務規則定義
- 開發團隊負責：技術實作細節、測試斷言規格

### Q2: 合約文件什麼時候寫？

**A:** 在開發實作之前寫
- 理想時間點：API 規格文件完成後、開發實作開始前
- 遵循 TDD：先有合約 → 寫測試 → 寫實作

### Q3: 如果發現合約文件定義錯誤怎麼辦？

**A:** 立即修正，並同步更新測試
1. 確認錯誤原因（是合約錯還是實作錯）
2. 修正合約文件
3. 更新測試程式
4. 重新執行測試

### Q4: 合約表格和詳細說明不一致怎麼辦？

**A:** 以合約表格為準（因為測試程式解析表格）
- 修正詳細說明以符合表格
- 或修正表格並更新測試

### Q5: 舊版合約文件怎麼處理？

**A:** 備份後廢棄
```bash
mv iam_contracts.md iam_contracts_v1_deprecated.md
```
- 保留作為歷史參考
- 在文件開頭標註「已廢棄，請使用 v2.0」

---

## 9. 變更歷史

| 版本 | 日期 | 變更內容 | 負責人 |
|:---|:---|:---|:---|
| 2.0 | 2026-02-06 | 建立新版合約文件說明，包含雙層結構設計 | Development Team |
| 1.0 | 2025-12-19 | 初版（已廢棄） | SA Team |

---

## 10. 參考資料

- `framework/testing/04_合約驅動測試.md` - 合約驅動測試方法論
- `framework/testing/測試架構規範.md` - 測試架構總覽
- `knowledge/02_System_Design/` - 系統設計書
- `knowledge/04_API_Specifications/` - API 規格文件

---

**維護責任：** Development Team
**審核責任：** Tech Lead + SA
**問題回報：** 請在專案 Issue Tracker 建立工單
