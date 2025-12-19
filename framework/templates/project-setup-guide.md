# 專案初始化指南

**版本:** 1.0
**目的:** 指導如何將架構框架套用到新專案

---

## 1. 前置準備

### 1.1 技術需求

| 類別 | 技術 | 版本 |
|:---|:---|:---|
| JDK | OpenJDK | 17+ |
| Build Tool | Maven | 3.8+ |
| Node.js | LTS | 18+ |
| IDE | IntelliJ IDEA | 推薦 |

### 1.2 複製框架

```bash
# 從參考專案複製框架目錄
cp -r reference-project/framework your-project/

# 複製基礎設施代碼 (可選)
cp -r reference-project/backend/hrms-common your-project/backend/common
```

---

## 2. 後端專案設定

### 2.1 建立專案結構

```
your-project/
├── backend/
│   ├── pom.xml                    # Parent POM
│   ├── common/                    # 共用模組
│   │   ├── pom.xml
│   │   └── src/main/java/
│   │       └── com/company/project/common/
│   │           ├── base/          # 泛型基類
│   │           ├── query/         # 查詢引擎
│   │           ├── event/         # 事件系統
│   │           └── test/          # 測試基類
│   │
│   └── {module}/                  # 業務模組
│       ├── pom.xml
│       └── src/
│           ├── main/java/
│           │   └── com/company/project/{module}/
│           │       ├── api/           # Interface Layer
│           │       ├── application/   # Application Layer
│           │       ├── domain/        # Domain Layer
│           │       └── infrastructure/# Infrastructure Layer
│           └── test/
│               ├── java/
│               └── resources/snapshots/
```

### 2.2 Parent POM 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <groupId>com.company</groupId>
    <artifactId>your-project</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <modules>
        <module>common</module>
        <module>{module}</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <querydsl.version>5.0.0</querydsl.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Querydsl -->
            <dependency>
                <groupId>com.querydsl</groupId>
                <artifactId>querydsl-jpa</artifactId>
                <version>${querydsl.version}</version>
                <classifier>jakarta</classifier>
            </dependency>

            <!-- MapStruct -->
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 2.3 複製基礎設施類別

從 `framework/infrastructure/backend/` 複製以下類別到 `common` 模組：

| 類別 | 說明 |
|:---|:---|
| `BaseEntity` | 基礎實體 (審計欄位) |
| `AggregateRoot` | 聚合根基類 |
| `BaseCommandService` | Command 服務基類 |
| `BaseQueryService` | Query 服務基類 |
| `QueryGroup` | 查詢條件群組 |
| `QueryBuilder` | 查詢建構器 |
| `BaseRepository` | 泛型倉儲 |
| `DomainEvent` | 領域事件基類 |

---

## 3. 前端專案設定

### 3.1 建立專案結構

```
your-project/
└── frontend/
    ├── package.json
    ├── tsconfig.json
    ├── vite.config.ts
    └── src/
        ├── features/                # 功能模組
        │   └── {feature}/
        │       ├── api/
        │       ├── components/
        │       ├── hooks/
        │       ├── factory/
        │       └── model/
        ├── pages/                   # 頁面元件
        ├── shared/                  # 共用元件
        │   ├── api/
        │   ├── hooks/
        │   └── factory/
        └── store/                   # 狀態管理
```

### 3.2 安裝依賴

```bash
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install @tanstack/react-query axios antd
npm install -D vitest @testing-library/react
```

### 3.3 複製共用 Hooks

從 `framework/infrastructure/frontend/` 複製：

| 檔案 | 說明 |
|:---|:---|
| `createApi.ts` | API 工廠函數 |
| `useTableQuery.ts` | 表格查詢 Hook |
| `useCrudMutation.ts` | CRUD 操作 Hook |
| `BaseFactory.ts` | 泛型 Factory 基類 |

---

## 4. 定義 Domain 代號

根據專案需求定義 Domain 代號對照表：

```markdown
| 代號 | Domain | 說明 |
|:---:|:---|:---|
| `01` | XXX | ... |
| `02` | YYY | ... |
| ...
```

將此表格加入專案的 `README.md` 或 `CLAUDE.md`。

---

## 5. 建立第一個功能

### 5.1 定義合約

在 `spec/contracts/` 建立合約規格：

```markdown
# {Entity} 服務合約

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| XXX-001 | 查詢... | tenantId = :tenantId |
```

### 5.2 撰寫失敗測試 (RED)

```java
@Test
void search_ShouldMatchContract() throws Exception {
    String contract = loadContractSpec("{entity}");
    verifyApiContract("/api/v1/{entities}/search", req, contract, "XXX-001");
}
```

### 5.3 實作 Domain

```java
public class {Entity} extends AggregateRoot<{Entity}Id> {
    // Domain 物件
}
```

### 5.4 實作 Service

```java
@Service("{action}{Entity}ServiceImpl")
public class {Action}{Entity}ServiceImpl extends BaseCommandService<...> {
    @Override
    protected Response doExecute(Request req, JWTModel user) {
        // 編排邏輯
    }
}
```

### 5.5 實作 Controller

```java
@RestController
@RequestMapping("/api/v1/{entities}")
public class {Domain}{Entity}CmdController extends CommandBaseController {
    // API 端點
}
```

### 5.6 執行測試 (GREEN)

```bash
mvn test
```

---

## 6. 設定 CI/CD

### 6.1 GitHub Actions

```yaml
# .github/workflows/ci.yml
name: CI

on: [push, pull_request]

jobs:
  backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: cd backend && mvn test

  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: cd frontend && npm ci && npm test
```

---

## 7. 開發檢查清單

### 專案初始化

- [ ] 建立專案結構
- [ ] 複製基礎設施類別
- [ ] 定義 Domain 代號
- [ ] 設定 CI/CD

### 開發新功能

- [ ] 定義合約規格
- [ ] 撰寫失敗測試
- [ ] 實作 Domain 物件
- [ ] 實作 Service
- [ ] 實作 Controller
- [ ] 執行測試通過
- [ ] 更新快照
- [ ] Code Review

---

## 8. 常見問題

### Q: 如何選擇 MyBatis 還是 Querydsl?

| 情境 | 選擇 |
|:---|:---|
| 新功能列表查詢 | Querydsl |
| 既有功能維護 | MyBatis (不改動) |
| 複雜報表/CTE | MyBatis |

### Q: 測試快照衝突怎麼辦?

1. 確認是否為預期變更
2. 若是預期變更，執行 `mvn test -DupdateSnapshots=true`
3. Review 更新後的快照
4. 提交快照

### Q: Service 內可以寫業務邏輯嗎?

**不可以**。Service 只做編排，業務邏輯應在 Domain 層。

---

**文件版本:** 1.0
**建立日期:** 2025-12-19
