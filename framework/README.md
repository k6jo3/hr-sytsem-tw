# 低成本高效能企業級開發架構框架

**版本:** 1.1
**最後更新:** 2025-12-19
**目標:** 提供一套可重用的企業級系統架構，實現「低開發成本、高測試性、易維護」

---

## 架構核心價值

```
┌─────────────────────────────────────────────────────────────────┐
│                    架構價值金字塔                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│                        ┌─────────┐                              │
│                        │  效率   │                              │
│                       /│  提升   │\                             │
│                      / └─────────┘ \                            │
│                     /   測試即文檔   \                           │
│                    /─────────────────\                          │
│                   /   零 Mock 測試    \                         │
│                  /─────────────────────\                        │
│                 /   結構化意圖物件      \                        │
│                /─────────────────────────\                      │
│               /    宣告式設計優先         \                      │
│              /───────────────────────────────\                  │
│             /      DDD + CQRS + 事件驅動       \                 │
│            └─────────────────────────────────────┘              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 架構設計三原則

> 這三個原則是本架構的核心哲學，所有設計決策都基於此。

### 原則一：能用「宣告」的，就不要用「程式碼」

**範例:** 使用 `@QueryFilter` 註解自動生成查詢條件，而非手寫程式碼

```java
// ❌ 傳統做法：手寫程式碼
public QueryGroup buildQuery(Request req) {
    QueryGroup group = new QueryGroup();
    if (req.getName() != null) {
        group.add(new FilterUnit("name", Operator.LIKE, req.getName()));
    }
    if (req.getStatus() != null) {
        group.add(new FilterUnit("status", Operator.EQ, req.getStatus()));
    }
    return group;
}

// ✅ 宣告式做法：一行搞定
public class Request {
    @QueryFilter(operator = Operator.LIKE)
    private String name;

    @QueryFilter(operator = Operator.EQ)
    private String status;
}

QueryGroup group = QueryBuilder.where().fromDto(request).build();
```

### 原則二：能回傳「結構化物件」的，就不要回傳「基本型別」

**範例:** 計算結果物件化，方便快照測試與追蹤

```java
// ❌ 傳統做法：只回傳數字，無法追蹤計算過程
public BigDecimal calculateBonus(Employee emp) {
    return new BigDecimal("50000");
}

// ✅ 結構化做法：回傳計算證明物件
public CalculationResult<BigDecimal> calculateBonus(Employee emp) {
    return CalculationResult.<BigDecimal>builder()
        .finalValue(new BigDecimal("50000"))
        .addRule("BASE_SALARY_RATE", "月薪 x 1.5")
        .addRule("PERFORMANCE_BONUS", "績效加成 10%")
        .breakdown("baseSalary", 40000)
        .breakdown("performanceBonus", 10000)
        .build();
}
```

### 原則三：Service 只做「編排」，不做「決策」

**範例:** 業務邏輯在 Domain 層，Service 只負責組裝

```java
// ❌ 錯誤做法：在 Service 寫業務邏輯
@Service
public class CreateOrderServiceImpl {
    public OrderResponse execCommand(CreateOrderRequest req) {
        // 業務邏輯散落在 Service
        if (req.getAmount() > 10000) {
            // 複雜判斷...
        }
    }
}

// ✅ 正確做法：Service 只編排，邏輯在 Domain
@Service
public class CreateOrderServiceImpl {
    public OrderResponse execCommand(CreateOrderRequest req) {
        // 1. 建立領域物件（邏輯在 Domain 內）
        Order order = Order.create(req);

        // 2. 持久化
        orderRepository.save(order);

        // 3. 發布事件
        publishEvents(order);

        // 4. 回傳
        return mapper.toResponse(order);
    }
}
```

---

## 框架結構

```
framework/
├── README.md                           # 本文件 - 架構總覽
│
├── architecture/                       # 架構設計規範
│   ├── 01_核心架構原則.md              # 設計哲學與原則
│   ├── 02_DDD分層設計.md               # 四層架構詳細說明
│   ├── 03_Business_Pipeline.md         # 宣告式業務流水線 ⭐
│   ├── 04_CQRS模式.md                  # 命令查詢分離
│   ├── 05_Service_Factory模式.md       # 動態 Service 注入
│   ├── 06_事件驅動架構.md              # 領域事件設計
│   └── 07_Fluent_Query_Engine.md       # 宣告式查詢引擎
│
├── development/                        # 開發流程規範
│   ├── 01_開發流程.md                  # 五階段開發流程
│   ├── 02_命名規範.md                  # 統一命名標準
│   ├── 03_後端開發規範.md              # 後端實作指南
│   └── 04_前端開發規範.md              # 前端實作指南
│
├── testing/                            # 測試架構規範
│   ├── 01_測試架構總覽.md              # 測試策略與金字塔
│   ├── 02_三階測試法.md                # 引擎契約/業務組裝/資料工廠
│   ├── 03_快照測試指南.md              # JSON 快照比對方法
│   └── 04_合約驅動測試.md              # Markdown 合約斷言
│
├── infrastructure/                     # 基礎設施程式碼
│   ├── backend/                        # 後端泛型基類
│   │   ├── base/                       # BaseEntity, AggregateRoot
│   │   ├── service/                    # BaseCommandService, BaseQueryService
│   │   ├── repository/                 # BaseRepository
│   │   ├── query/                      # QueryGroup, QueryBuilder
│   │   └── test/                       # 測試基類
│   │
│   └── frontend/                       # 前端泛型基類
│       ├── api/                        # createApi 工廠
│       ├── hooks/                      # useTableQuery, useCrudMutation
│       └── factory/                    # BaseFactory
│
└── templates/                          # 專案模板
    ├── project-setup-guide.md          # 專案初始化指南
    ├── backend-module-template/        # 後端模組模板
    └── frontend-feature-template/      # 前端功能模板
```

---

## 快速導覽

### 我是架構師/技術主管

1. 閱讀 [01_核心架構原則.md](architecture/01_核心架構原則.md) 了解設計哲學
2. 閱讀 [03_Business_Pipeline.md](architecture/03_Business_Pipeline.md) 了解 Service 層流水線模式
3. 閱讀 [project-setup-guide.md](templates/project-setup-guide.md) 了解如何套用到新專案

### 我是後端工程師

1. 閱讀 [01_開發流程.md](development/01_開發流程.md) 了解開發步驟
2. 閱讀 [02_命名規範.md](development/02_命名規範.md) 了解命名標準
3. 閱讀 [03_Business_Pipeline.md](architecture/03_Business_Pipeline.md) 了解複雜業務邏輯寫法
4. 閱讀 [03_後端開發規範.md](development/03_後端開發規範.md) 開始實作

### 我是前端工程師

1. 閱讀 [04_前端開發規範.md](development/04_前端開發規範.md) 了解前端架構
2. 參考 `infrastructure/frontend/` 使用泛型基類

### 我是 QA/測試工程師

1. 閱讀 [01_測試架構總覽.md](testing/01_測試架構總覽.md) 了解測試策略
2. 閱讀 [04_合約驅動測試.md](testing/04_合約驅動測試.md) 了解合約測試

---

## 架構效益量化

| 指標 | 傳統架構 | 本架構 | 改善幅度 |
|:---|:---:|:---:|:---:|
| 新增 CRUD Service | ~150 行 | ~30 行 | **80%** |
| 新增 API 端點 | 2-3 小時 | 30 分鐘 | **75%** |
| DTO 轉換程式碼 | 手寫 80 行 | 0 行 (MapStruct) | **100%** |
| 前端表格頁面 | ~250 行 | ~80 行 | **68%** |
| 單元測試代碼 | 比業務碼長 | 3-5 行 | **90%** |
| 測試執行時間 | 需啟動 DB | 0.1 秒 | **100x** |

---

## 技術堆疊

### 後端

| 類別 | 技術 | 說明 |
|:---|:---|:---|
| 框架 | Spring Boot 3.x | 核心框架 |
| 微服務 | Spring Cloud | 服務發現、配置中心 |
| ORM | Querydsl + JPA | 宣告式查詢引擎 |
| 快取 | Redis | 分散式快取 |
| 訊息佇列 | Kafka | 事件驅動通訊 |
| 映射 | MapStruct | 自動 DTO 轉換 |

### 前端

| 類別 | 技術 | 說明 |
|:---|:---|:---|
| 框架 | React 18 | UI 框架 |
| 語言 | TypeScript | 型別安全 |
| 狀態 | Redux Toolkit | 全局狀態管理 |
| 查詢 | TanStack Query | 伺服器狀態管理 |
| UI | Ant Design | 元件庫 |

---

## 相關文件

| 文件 | 說明 |
|:---|:---|
| [架構師交接手冊](../架構師交接手冊：合約驅動與全鏈路自動化體系.md) | 架構核心概念與維護策略 |
| [開發流程](../開發流程.md) | 五階段開發流程詳細說明 |

---

## 版本紀錄

| 版本 | 日期 | 變更 |
|:---|:---|:---|
| 1.1 | 2025-12-19 | 新增 Business Pipeline 架構文件 |
| 1.0 | 2025-12-19 | 初版建立 |
