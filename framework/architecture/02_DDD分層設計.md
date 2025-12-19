# DDD 分層設計

**版本:** 1.0
**目的:** 定義領域驅動設計的四層架構標準

---

## 1. 四層架構總覽

```
┌─────────────────────────────────────────────────────────────────┐
│ Interface Layer (介面層)                                        │
│ ─────────────────────────────────────────────────               │
│ ● REST Controller (CQRS 分離)                                   │
│ ● Request/Response DTO                                          │
│ ● 輸入驗證 (@Valid)                                              │
│ ● Swagger 文檔                                                   │
├─────────────────────────────────────────────────────────────────┤
│ Application Layer (應用層)                                      │
│ ─────────────────────────────────────────────────               │
│ ● Use Case 編排 (Service)                                       │
│ ● 事務管理 (@Transactional)                                      │
│ ● 領域事件發布                                                   │
│ ● ⚠️ 禁止包含核心業務規則                                        │
├─────────────────────────────────────────────────────────────────┤
│ Domain Layer (領域層 - 核心)                                    │
│ ─────────────────────────────────────────────────               │
│ ● Aggregate Root (聚合根)                                       │
│ ● Entity (實體)                                                  │
│ ● Value Object (值對象)                                         │
│ ● Domain Service (領域服務)                                     │
│ ● Domain Event (領域事件)                                       │
│ ● Repository Interface (倉儲介面)                               │
│ ● ✅ 純淨 Java POJO，無框架依賴                                  │
├─────────────────────────────────────────────────────────────────┤
│ Infrastructure Layer (基礎設施層)                               │
│ ─────────────────────────────────────────────────               │
│ ● Repository 實作                                               │
│ ● DAO / Mapper                                                   │
│ ● 外部服務適配器                                                 │
│ ● 快取實作                                                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 各層詳細說明

### 2.1 Interface Layer (介面層)

**職責:** 處理 HTTP 請求，轉換外部格式

**組成元件:**

| 元件 | 命名格式 | 說明 |
|:---|:---|:---|
| Command Controller | `{Domain}CmdController` | 處理寫入操作 |
| Query Controller | `{Domain}QryController` | 處理讀取操作 |
| Request DTO | `{動詞}{名詞}Request` | 請求資料結構 |
| Response DTO | `{名詞}{Type}Response` | 回應資料結構 |

**目錄結構:**

```
api/
├── controller/
│   ├── {entity}/
│   │   ├── {Domain}{Entity}CmdController.java
│   │   └── {Domain}{Entity}QryController.java
├── request/
│   └── {entity}/
│       ├── Create{Entity}Request.java
│       └── Update{Entity}Request.java
└── response/
    └── {entity}/
        ├── {Entity}DetailResponse.java
        └── {Entity}ListResponse.java
```

### 2.2 Application Layer (應用層)

**職責:** 編排 Use Case，協調領域物件

**組成元件:**

| 元件 | 命名格式 | 說明 |
|:---|:---|:---|
| Command Service | `{動詞}{名詞}ServiceImpl` | 處理寫入邏輯 |
| Query Service | `Get{名詞}{Type}ServiceImpl` | 處理讀取邏輯 |
| Saga | `{流程名稱}Saga` | 分散式事務編排 |

**禁止事項:**

```java
// ❌ 錯誤：在 Service 內寫業務邏輯
@Service
public class CreateOrderServiceImpl {
    public OrderResponse execCommand(CreateOrderRequest req) {
        if (req.getAmount() > 10000) {
            // 這是業務邏輯，應該在 Domain 層
            discount = calculateVIPDiscount();
        }
    }
}

// ✅ 正確：Service 只編排
@Service
public class CreateOrderServiceImpl extends BaseCommandService<...> {
    @Override
    protected OrderResponse doExecute(CreateOrderRequest req, JWTModel user) {
        // 1. 載入/建立領域物件
        Order order = Order.create(req.getItems(), req.getCustomerId());

        // 2. 持久化
        orderRepository.save(order);

        // 3. 發布事件
        publishEvents(order);

        // 4. 回傳
        return mapper.toResponse(order);
    }
}
```

**目錄結構:**

```
application/
├── service/
│   └── {entity}/
│       ├── Create{Entity}ServiceImpl.java
│       ├── Update{Entity}ServiceImpl.java
│       └── Get{Entity}ListServiceImpl.java
├── saga/
│   └── {ProcessName}Saga.java
└── factory/
    ├── CommandApiServiceFactory.java
    └── QueryApiServiceFactory.java
```

### 2.3 Domain Layer (領域層)

**職責:** 封裝核心業務規則

**組成元件:**

| 元件 | 說明 | 特性 |
|:---|:---|:---|
| **Aggregate Root** | 聚合根，業務邊界 | 控制內部實體一致性 |
| **Entity** | 有識別的領域物件 | 有唯一 ID |
| **Value Object** | 無識別的值物件 | 不可變，透過值比較 |
| **Domain Service** | 跨聚合的業務邏輯 | 無狀態 |
| **Domain Event** | 領域事件 | 通知其他聚合 |
| **Repository Interface** | 倉儲介面 | 只定義介面 |

**核心原則:**

1. **純淨 POJO** - 不依賴任何框架（Spring、JPA 等）
2. **自封裝** - 業務規則在物件內部
3. **不可變值對象** - Value Object 建立後不可修改

**範例:**

```java
// Aggregate Root
public class Order extends AggregateRoot<OrderId> {

    private CustomerId customerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private Money totalAmount;

    // Factory Method - 封裝建立規則
    public static Order create(List<OrderItemData> itemsData, CustomerId customerId) {
        Order order = new Order();
        order.customerId = customerId;
        order.items = itemsData.stream()
            .map(OrderItem::from)
            .collect(toList());
        order.status = OrderStatus.PENDING;
        order.totalAmount = order.calculateTotal();

        // 註冊領域事件
        order.registerEvent(new OrderCreatedEvent(order));

        return order;
    }

    // 業務方法
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待處理訂單可確認");
        }
        this.status = OrderStatus.CONFIRMED;
        this.registerEvent(new OrderConfirmedEvent(this));
    }

    // 私有計算方法
    private Money calculateTotal() {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(Money.ZERO, Money::add);
    }
}

// Value Object
public class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("幣別不同無法相加");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

**目錄結構:**

```
domain/
├── model/
│   ├── aggregate/
│   │   └── Order.java
│   ├── entity/
│   │   └── OrderItem.java
│   └── valueobject/
│       ├── Money.java
│       └── OrderStatus.java
├── repository/
│   └── IOrderRepository.java
├── service/
│   └── PricingDomainService.java
└── event/
    ├── OrderCreatedEvent.java
    └── OrderConfirmedEvent.java
```

### 2.4 Infrastructure Layer (基礎設施層)

**職責:** 實作技術細節

**組成元件:**

| 元件 | 命名格式 | 說明 |
|:---|:---|:---|
| Repository Impl | `{Entity}RepositoryImpl` | 倉儲實作 |
| DAO | `{Entity}DAO` | 資料存取 (MyBatis) |
| PO | `{Entity}PO` | 持久化物件 |
| Mapper | `{Entity}Mapper` | ORM 映射 |
| Adapter | `{External}Adapter` | 外部服務適配器 |

**目錄結構:**

```
infrastructure/
├── persistence/
│   ├── mybatis/           # MyBatis 專用
│   │   ├── mapper/
│   │   └── xml/
│   └── querydsl/          # Querydsl 專用
│       └── repository/
├── repository/
│   └── OrderRepositoryImpl.java
├── po/
│   └── OrderPO.java
├── adapter/
│   └── PaymentGatewayAdapter.java
└── config/
    └── PersistenceConfig.java
```

---

## 3. 層間依賴規則

```
┌─────────────────────────────────────────────────────────────────┐
│                      依賴方向 (單向)                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Interface ──────────────────────────────────────▶ Application │
│       │                                                 │       │
│       │                                                 │       │
│       │                                                 ▼       │
│       └─────────────────────────────────────────────▶ Domain    │
│                                                         ▲       │
│                                                         │       │
│   Infrastructure ───────────────────────────────────────┘       │
│                                                                 │
│   ⚠️ Domain 層不依賴任何其他層                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**規則:**

1. **Domain 層獨立** - 不依賴其他任何層
2. **單向依賴** - 上層依賴下層，反之不可
3. **介面隔離** - Domain 定義介面，Infrastructure 實作

---

## 4. 架構檢查清單

### 新增功能時

- [ ] Controller 是否分離 Command/Query？
- [ ] Service 是否只做編排，無業務邏輯？
- [ ] Domain 物件是否純淨 POJO？
- [ ] Repository 是否定義在 Domain 層？
- [ ] 持久化邏輯是否在 Infrastructure 層？

### Code Review 時

- [ ] 是否有業務邏輯洩漏到 Service？
- [ ] Domain 物件是否依賴框架？
- [ ] 是否違反依賴方向？

---

**文件版本:** 1.0
**建立日期:** 2025-12-19
