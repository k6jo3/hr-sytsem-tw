# Business Pipeline 架構

**版本:** 1.0
**目的:** 定義宣告式業務流水線，解決 Service 層複雜邏輯的可維護性問題

---

## 1. 設計背景

### 1.1 問題：Service 層的複雜性

傳統 Service 實作常見問題：

```java
// ❌ 傳統寫法：邏輯糾纏、難以測試、難以維護
@Service
public class CalculateSalaryServiceImpl {
    public SalaryResponse calculate(String empId) {
        Employee emp = employeeRepo.findById(empId);
        Attendance att = attendanceRepo.findByEmpId(empId);
        // ... 50 行業務邏輯
        BigDecimal tax = calculateTax(base);
        // ... 又 30 行計算
        return response;
    }
}
```

**痛點：**
- 邏輯混雜：資料存取與計算混在一起
- 難以測試：必須 Mock 大量依賴
- 難以維護：「接手即重寫」
- 難以觀測：出錯時無法定位步驟

### 1.2 解決方案：宣告式流水線

將 Service 重構為透明的傳送帶：

```java
// ✅ Pipeline 寫法：步驟清晰、可測試、可觀測
@Service
public class CalculateSalaryServiceImpl {
    public SalaryResponse calculate(String empId) {
        return BusinessPipeline.start(new SalaryContext(empId))
            .next(loadEmployeeTask)      // 步驟一：載入員工
            .next(loadAttendanceTask)    // 步驟二：載入出勤
            .next(calculateBaseTask)     // 步驟三：計算基本薪
            .next(calculateTaxTask)      // 步驟四：計算稅額
            .nextIf(ctx -> ctx.isFullTime(), bonusTask)  // 條件分支
            .execute();
    }
}
```

---

## 2. 核心組件

### 2.1 組件總覽

```
┌─────────────────────────────────────────────────────────────────┐
│                    Business Pipeline 架構                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │   Context    │    │    Task      │    │   Pipeline   │      │
│  │  (數據載體)   │ ←→ │  (執行單元)   │ ←─ │   (編排器)    │      │
│  └──────────────┘    └──────────────┘    └──────────────┘      │
│                                                                 │
│  Context: 攜帶輸入、中間結果、輸出                               │
│  Task: 單一職責的執行步驟                                        │
│  Pipeline: 連接並執行 Task 序列                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Context (上下文)

**職責：** 在 Pipeline 中傳遞數據

```java
@Data
public class SalaryContext {
    // === 輸入 ===
    private String employeeId;
    private YearMonth targetMonth;

    // === 中間數據 ===
    private Employee employee;
    private AttendanceRecord attendance;
    private OvertimeRecord overtime;
    private SalaryPolicy policy;

    // === 輸出 ===
    private CalculationResult<BigDecimal> result;

    // === 建構子 ===
    public SalaryContext(String employeeId, YearMonth month) {
        this.employeeId = employeeId;
        this.targetMonth = month;
    }
}
```

**設計原則：**
- 清晰分區：輸入 / 中間數據 / 輸出
- 不可變輸入：輸入欄位在建構後不應修改
- 完整追蹤：所有中間步驟結果都可取得

### 2.3 Task (任務)

**職責：** 執行單一步驟的邏輯

```java
public interface Task<C> {
    void execute(C context);
}
```

**Task 分類：**

| 類型 | 職責 | 命名格式 | 範例 |
|:---|:---|:---|:---|
| **Infrastructure Task** | 資料存取 | `Load{Entity}Task` | `LoadEmployeeTask` |
| **Domain Task** | 業務計算 | `{動詞}{業務}Task` | `CalculateTaxTask` |
| **Integration Task** | 外部服務 | `{動詞}{Service}Task` | `SendNotificationTask` |

**Infrastructure Task 範例：**

```java
@Component
@RequiredArgsConstructor
public class LoadEmployeeTask implements Task<SalaryContext> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(SalaryContext ctx) {
        Employee emp = employeeRepository.findById(ctx.getEmployeeId())
            .orElseThrow(() -> new EmployeeNotFoundException(ctx.getEmployeeId()));
        ctx.setEmployee(emp);
    }
}
```

**Domain Task 範例：**

```java
@Component
@RequiredArgsConstructor
public class CalculateTaxTask implements Task<SalaryContext> {

    private final TaxCalculationDomainService taxService;

    @Override
    public void execute(SalaryContext ctx) {
        BigDecimal tax = taxService.calculate(
            ctx.getEmployee(),
            ctx.getResult().getFinalValue()
        );
        ctx.getResult().setTaxAmount(tax);
    }
}
```

### 2.4 Pipeline (流水線)

**職責：** 連接並依序執行 Task

```java
public class BusinessPipeline<C> {

    private final C context;
    private final List<Task<C>> tasks = new ArrayList<>();

    public static <C> BusinessPipeline<C> start(C context) {
        return new BusinessPipeline<>(context);
    }

    public BusinessPipeline<C> next(Task<C> task) {
        tasks.add(task);
        return this;
    }

    public BusinessPipeline<C> nextIf(Predicate<C> condition, Task<C> task) {
        if (condition.test(context)) {
            tasks.add(task);
        }
        return this;
    }

    public C execute() {
        for (Task<C> task : tasks) {
            task.execute(context);
        }
        return context;
    }
}
```

---

## 3. 與現有架構的整合

### 3.1 層級對接

Pipeline 定位在 **Application Layer** 與 **Domain Layer** 之間：

```
┌─────────────────────────────────────────────────────────────────┐
│ Interface Layer                                                 │
│ ─────────────────────                                          │
│ Controller: 保持不變，繼續使用 execCommand                       │
├─────────────────────────────────────────────────────────────────┤
│ Application Layer                                               │
│ ─────────────────────                                          │
│ Service: 扮演 Pipeline 編排者 (Orchestrator)                    │
│         只負責建立 Context、組裝 Pipeline、回傳結果              │
├─────────────────────────────────────────────────────────────────┤
│ Domain / Application Layer                                      │
│ ─────────────────────                                          │
│ Task: 扮演邏輯執行者 (Processor)                                │
│       - Infrastructure Task → 依賴 Repository                   │
│       - Domain Task → 依賴 Domain Service                       │
├─────────────────────────────────────────────────────────────────┤
│ Domain Layer                                                    │
│ ─────────────────────                                          │
│ Domain Service: 保持純淨，不變                                  │
│ Aggregate/Entity: 保持純淨，不變                                │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 完整實作範例

**Service 實作：**

```java
@Service("calculateMonthlySalaryServiceImpl")
@Transactional
@RequiredArgsConstructor
public class CalculateMonthlySalaryServiceImpl
        extends BaseCommandService<CalculateSalaryRequest, SalaryCalculationResponse> {

    // 注入預定義的 Task
    private final LoadEmployeeTask loadEmployeeTask;
    private final LoadAttendanceTask loadAttendanceTask;
    private final LoadOvertimeTask loadOvertimeTask;
    private final CalculateBaseSalaryTask calculateBaseSalaryTask;
    private final CalculateOvertimePayTask calculateOvertimePayTask;
    private final CalculateTaxTask calculateTaxTask;
    private final SalaryMapper mapper;

    @Override
    protected SalaryCalculationResponse doExecute(
            CalculateSalaryRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 建立 Context
        SalaryContext ctx = new SalaryContext(
            request.getEmployeeId(),
            request.getTargetMonth()
        );

        // 2. 執行 Pipeline
        BusinessPipeline.start(ctx)
            .next(loadEmployeeTask)
            .next(loadAttendanceTask)
            .next(loadOvertimeTask)
            .next(calculateBaseSalaryTask)
            .nextIf(c -> c.getOvertime() != null, calculateOvertimePayTask)
            .next(calculateTaxTask)
            .execute();

        // 3. 轉換回應
        return mapper.toResponse(ctx.getResult());
    }
}
```

---

## 4. 目錄結構

### 4.1 標準結構

```
application/
├── service/
│   └── {domain}/
│       ├── {動詞}{名詞}ServiceImpl.java      # Service (Pipeline 編排)
│       ├── context/                          # Context 類別
│       │   └── {UseCase}Context.java
│       └── task/                             # Task 類別
│           ├── Load{Entity}Task.java         # Infrastructure Task
│           ├── {動詞}{業務}Task.java          # Domain Task
│           └── ...
```

### 4.2 複雜領域範例 (薪資計算)

```
application/
├── service/
│   └── pay/
│       ├── CalculateMonthlySalaryServiceImpl.java
│       ├── context/
│       │   └── SalaryContext.java
│       └── task/
│           ├── LoadEmployeeTask.java
│           ├── LoadAttendanceTask.java
│           ├── LoadOvertimeTask.java
│           ├── CalculateBaseSalaryTask.java
│           ├── CalculateOvertimePayTask.java
│           ├── CalculateTaxTask.java
│           └── DeductInsuranceTask.java
```

---

## 5. 進階功能

### 5.1 觀測與除錯

在任意步驟間插入日誌快照：

```java
BusinessPipeline.start(ctx)
    .next(loadEmployeeTask)
    .next(ctx -> log.debug("After load: {}", ctx))  // Lambda 快照
    .next(calculateBaseSalaryTask)
    .next(ctx -> log.debug("After calc: {}", ctx.getResult()))
    .execute();
```

### 5.2 錯誤處理

```java
public class BusinessPipeline<C> {

    public C execute() {
        for (int i = 0; i < tasks.size(); i++) {
            try {
                tasks.get(i).execute(context);
            } catch (Exception e) {
                throw new PipelineException(
                    String.format("Pipeline failed at step %d: %s",
                        i, tasks.get(i).getClass().getSimpleName()),
                    e
                );
            }
        }
        return context;
    }
}
```

### 5.3 條件分支

```java
BusinessPipeline.start(ctx)
    .next(loadEmployeeTask)
    .nextIf(c -> c.getEmployee().isFullTime(), fullTimeTask)
    .nextIf(c -> c.getEmployee().isPartTime(), partTimeTask)
    .nextIf(c -> c.getEmployee().isContractor(), contractorTask)
    .execute();
```

### 5.4 並行執行 (進階)

```java
// 當多個 Task 互不依賴時，可並行執行
BusinessPipeline.start(ctx)
    .next(loadEmployeeTask)
    .parallel(
        loadAttendanceTask,
        loadOvertimeTask,
        loadPolicyTask
    )
    .next(calculateSalaryTask)
    .execute();
```

---

## 6. 測試策略

### 6.1 Task 單元測試

```java
class CalculateTaxTaskTest {

    @Test
    void shouldCalculateTaxCorrectly() {
        // Given
        SalaryContext ctx = new SalaryContext("EMP001", YearMonth.now());
        ctx.setEmployee(createEmployee());
        ctx.setResult(CalculationResult.of(BigDecimal.valueOf(50000)));

        CalculateTaxTask task = new CalculateTaxTask(taxService);

        // When
        task.execute(ctx);

        // Then
        assertThat(ctx.getResult().getTaxAmount())
            .isEqualTo(BigDecimal.valueOf(5000));
    }
}
```

### 6.2 Pipeline 整合測試

```java
class SalaryPipelineIntegrationTest {

    @Test
    void shouldExecuteFullPipeline() {
        // Given
        SalaryContext ctx = new SalaryContext("EMP001", YearMonth.of(2025, 1));

        // When
        BusinessPipeline.start(ctx)
            .next(loadEmployeeTask)
            .next(loadAttendanceTask)
            .next(calculateBaseSalaryTask)
            .next(calculateTaxTask)
            .execute();

        // Then
        assertThat(ctx.getResult()).isNotNull();
        assertThat(ctx.getResult().getFinalValue()).isPositive();
    }
}
```

### 6.3 快照測試

```java
class SalaryCalculationSnapshotTest extends BaseServiceTest {

    @Test
    void calculateSalary_ShouldMatchSnapshot() {
        CalculateSalaryRequest req = new CalculateSalaryRequest();
        req.setEmployeeId("EMP001");
        req.setTargetMonth(YearMonth.of(2025, 1));

        service.execCommand(req, mockUser);

        // 驗證計算結果快照
        FluentAssert.assertMatchesSnapshot(
            "salary_calculation_emp001.json",
            service.getLastContext().getResult()
        );
    }
}
```

---

## 7. 架構優點

### 7.1 對比傳統寫法

| 面向 | 傳統 Service | Pipeline 模式 |
|:---|:---|:---|
| **可讀性** | 邏輯糾纏 | 步驟清晰 |
| **可測試性** | 需 Mock 整個 Service | 獨立測試每個 Task |
| **可維護性** | 修改風險高 | 只改特定 Task |
| **可觀測性** | 難以追蹤執行過程 | 步驟間可插入日誌 |
| **可擴展性** | 修改 Service 代碼 | 新增或調整 Task |

### 7.2 符合架構原則

- **符合 DDD**: Task 可封裝為 Domain Service，保持 Application Service 純粹
- **符合 SOLID**:
  - **S** - 每個 Task 只做一件事
  - **O** - Pipeline 可擴展新 Task
  - **D** - Task 依賴介面而非實作
- **與 AOP 相容**: ApiServiceAspect 正常運作，Pipeline 是 Service 內部實作

---

## 8. 使用時機

### 8.1 適合使用 Pipeline

- ✅ 複雜業務流程 (如薪資計算、訂單處理)
- ✅ 多步驟資料載入與計算
- ✅ 需要高可測試性的核心邏輯
- ✅ 需要清晰追蹤執行過程

### 8.2 不需要 Pipeline

- ❌ 簡單 CRUD 操作
- ❌ 只有 1-2 個步驟的邏輯
- ❌ 單純的資料查詢

---

## 9. 檢查清單

### 新增 Pipeline 時

- [ ] Context 是否清晰分區 (輸入/中間/輸出)？
- [ ] 每個 Task 是否只做一件事？
- [ ] Task 命名是否符合規範？
- [ ] 是否有對應的單元測試？

### Code Review 時

- [ ] Service 是否只做編排？
- [ ] Task 依賴是否正確注入？
- [ ] 條件分支邏輯是否清晰？
- [ ] 錯誤處理是否完善？

---

**文件版本:** 1.0
**建立日期:** 2025-12-19
