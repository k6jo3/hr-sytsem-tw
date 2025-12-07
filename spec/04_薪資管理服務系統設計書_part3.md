## 7. Domain設計

### 7.1 聚合根 (Aggregate Root)

#### 7.1.1 SalaryStructure聚合根 (薪資結構)

**職責:** 定義員工的薪資組成與計算規則

**Java實作:**
```java
@Entity
@Table(name = "salary_structures")
public class SalaryStructure {
    @EmbeddedId
    private StructureId id;
    
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payroll_system", nullable = false)
    private PayrollSystem payrollSystem;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payroll_cycle", nullable = false)
    private PayrollCycle payrollCycle;
    
    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;
    
    @Column(name = "monthly_salary")
    private BigDecimal monthlySalary;
    
    @Column(name = "calculated_hourly_rate")
    private BigDecimal calculatedHourlyRate;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "structure_id")
    private List<SalaryItem> salaryItems = new ArrayList<>();
    
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "is_active")
    private boolean isActive;
    
    // 每月平均工時常數
    private static final BigDecimal MONTHLY_WORK_HOURS = new BigDecimal("240");
    
    // ========== Factory Method ==========
    
    /**
     * 建立月薪制薪資結構
     */
    public static SalaryStructure createMonthly(
            UUID employeeId,
            BigDecimal monthlySalary,
            PayrollCycle cycle,
            LocalDate effectiveDate) {
        
        SalaryStructure structure = new SalaryStructure();
        structure.id = StructureId.generate();
        structure.employeeId = employeeId;
        structure.payrollSystem = PayrollSystem.MONTHLY;
        structure.payrollCycle = cycle;
        structure.monthlySalary = monthlySalary;
        structure.calculatedHourlyRate = monthlySalary.divide(
            MONTHLY_WORK_HOURS, 4, RoundingMode.HALF_UP);
        structure.effectiveDate = effectiveDate;
        structure.isActive = true;
        
        // 發布事件
        DomainEventPublisher.publish(new SalaryStructureCreatedEvent(
            structure.id.getValue(),
            employeeId,
            monthlySalary
        ));
        
        return structure;
    }
    
    /**
     * 建立時薪制薪資結構
     */
    public static SalaryStructure createHourly(
            UUID employeeId,
            BigDecimal hourlyRate,
            LocalDate effectiveDate) {
        
        SalaryStructure structure = new SalaryStructure();
        structure.id = StructureId.generate();
        structure.employeeId = employeeId;
        structure.payrollSystem = PayrollSystem.HOURLY;
        structure.payrollCycle = PayrollCycle.DAILY;
        structure.hourlyRate = hourlyRate;
        structure.effectiveDate = effectiveDate;
        structure.isActive = true;
        
        return structure;
    }
    
    // ========== Domain行為 ==========
    
    /**
     * 新增薪資項目
     */
    public void addSalaryItem(SalaryItem item) {
        this.salaryItems.add(item);
    }
    
    /**
     * 調整月薪
     */
    public void adjustMonthlySalary(BigDecimal newSalary, LocalDate effectiveDate) {
        if (this.payrollSystem != PayrollSystem.MONTHLY) {
            throw new DomainException("只有月薪制員工可調整月薪");
        }
        
        BigDecimal oldSalary = this.monthlySalary;
        this.monthlySalary = newSalary;
        this.calculatedHourlyRate = newSalary.divide(
            MONTHLY_WORK_HOURS, 4, RoundingMode.HALF_UP);
        
        DomainEventPublisher.publish(new SalaryStructureChangedEvent(
            this.employeeId,
            oldSalary,
            newSalary,
            effectiveDate
        ));
    }
    
    /**
     * 計算月應發薪資 (固定項目)
     */
    public BigDecimal calculateMonthlyGross() {
        BigDecimal base = this.payrollSystem == PayrollSystem.MONTHLY 
            ? this.monthlySalary 
            : BigDecimal.ZERO;
        
        BigDecimal earningsTotal = this.salaryItems.stream()
            .filter(item -> item.getItemType() == ItemType.EARNING)
            .filter(SalaryItem::isFixedAmount)
            .map(SalaryItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return base.add(earningsTotal);
    }
    
    /**
     * 取得加班費時薪基準
     */
    public BigDecimal getOvertimeHourlyRate() {
        if (this.payrollSystem == PayrollSystem.HOURLY) {
            return this.hourlyRate;
        }
        return this.calculatedHourlyRate;
    }
    
    /**
     * 計算投保薪資 (勞健保計算用)
     */
    public BigDecimal calculateInsurableSalary() {
        BigDecimal base = this.payrollSystem == PayrollSystem.MONTHLY 
            ? this.monthlySalary 
            : this.hourlyRate.multiply(new BigDecimal("240"));
        
        BigDecimal insurableAllowances = this.salaryItems.stream()
            .filter(item -> item.getItemType() == ItemType.EARNING)
            .filter(SalaryItem::isInsurable)
            .map(SalaryItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return base.add(insurableAllowances);
    }
}

// 薪資項目實體
@Entity
@Table(name = "salary_structure_items")
public class SalaryItem {
    @Id
    private UUID itemId;
    
    @Column(name = "item_code", nullable = false)
    private String itemCode;
    
    @Column(name = "item_name", nullable = false)
    private String itemName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;
    
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @Column(name = "is_fixed_amount")
    private boolean isFixedAmount;
    
    @Column(name = "is_taxable")
    private boolean isTaxable;
    
    @Column(name = "is_insurable")
    private boolean isInsurable;
}

// 枚舉定義
public enum PayrollSystem { HOURLY, MONTHLY }
public enum PayrollCycle { DAILY, WEEKLY, BI_WEEKLY, MONTHLY }
public enum ItemType { EARNING, DEDUCTION }
```

#### 7.1.2 PayrollRun聚合根 (薪資計算批次) - 核心聚合根

**職責:** 執行一次薪資計算作業，管理批次生命週期

**Java實作:**
```java
@Entity
@Table(name = "payroll_runs")
public class PayrollRun {
    @EmbeddedId
    private RunId id;
    
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
    
    @Embedded
    private PayPeriod payPeriod;
    
    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PayrollRunStatus status;
    
    @Embedded
    private PayrollStatistics statistics;
    
    @Column(name = "executed_by")
    private UUID executedBy;
    
    @Column(name = "executed_at")
    private LocalDateTime executedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "approved_by")
    private UUID approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    // ========== Factory Method ==========
    
    /**
     * 建立薪資計算批次
     */
    public static PayrollRun create(
            UUID organizationId,
            LocalDate periodStart,
            LocalDate periodEnd,
            LocalDate payDate) {
        
        PayrollRun run = new PayrollRun();
        run.id = RunId.generate();
        run.organizationId = organizationId;
        run.payPeriod = new PayPeriod(periodStart, periodEnd);
        run.payDate = payDate;
        run.status = PayrollRunStatus.DRAFT;
        run.statistics = new PayrollStatistics();
        
        return run;
    }
    
    // ========== Domain行為 ==========
    
    /**
     * 開始執行薪資計算
     */
    public void startExecution(UUID executorId, int totalEmployees) {
        if (this.status != PayrollRunStatus.DRAFT) {
            throw new DomainException("只有草稿狀態的批次可以執行");
        }
        
        this.status = PayrollRunStatus.CALCULATING;
        this.executedBy = executorId;
        this.executedAt = LocalDateTime.now();
        this.statistics = new PayrollStatistics(totalEmployees);
        
        DomainEventPublisher.publish(new PayrollRunStartedEvent(
            this.id.getValue(),
            this.payPeriod.getStart(),
            this.payPeriod.getEnd(),
            totalEmployees
        ));
    }
    
    /**
     * 完成薪資計算
     */
    public void complete(PayrollStatistics finalStats) {
        if (this.status != PayrollRunStatus.CALCULATING) {
            throw new DomainException("只有計算中的批次可以完成");
        }
        
        this.status = PayrollRunStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.statistics = finalStats;
        
        DomainEventPublisher.publish(new PayrollRunCompletedEvent(
            this.id.getValue(),
            this.payPeriod.getStart().toString() + " ~ " + this.payPeriod.getEnd().toString(),
            finalStats.getTotalEmployees(),
            finalStats.getProcessedEmployees(),
            finalStats.getTotalGrossAmount(),
            finalStats.getTotalNetAmount()
        ));
    }
    
    /**
     * 送審
     */
    public void submit(UUID submitterId) {
        if (this.status != PayrollRunStatus.COMPLETED) {
            throw new DomainException("只有已完成的批次可以送審");
        }
        
        this.status = PayrollRunStatus.SUBMITTED;
    }
    
    /**
     * 核准
     */
    public void approve(UUID approverId) {
        if (this.status != PayrollRunStatus.SUBMITTED) {
            throw new DomainException("只有送審中的批次可以核准");
        }
        
        this.status = PayrollRunStatus.APPROVED;
        this.approvedBy = approverId;
        this.approvedAt = LocalDateTime.now();
        
        DomainEventPublisher.publish(new PayrollApprovedEvent(
            this.id.getValue(),
            approverId
        ));
    }
    
    /**
     * 標記為已發放
     */
    public void markAsPaid(String bankFileUrl) {
        if (this.status != PayrollRunStatus.APPROVED) {
            throw new DomainException("只有已核准的批次可以標記為已發放");
        }
        
        this.status = PayrollRunStatus.PAID;
        
        DomainEventPublisher.publish(new PayrollPaidEvent(
            this.id.getValue(),
            this.payDate,
            this.statistics.getTotalNetAmount()
        ));
    }
    
    /**
     * 取消
     */
    public void cancel() {
        if (this.status == PayrollRunStatus.PAID) {
            throw new DomainException("已發放的批次無法取消");
        }
        
        this.status = PayrollRunStatus.CANCELLED;
    }
}

// 值對象: 計薪期間
@Embeddable
public class PayPeriod {
    @Column(name = "pay_period_start")
    private LocalDate start;
    
    @Column(name = "pay_period_end")
    private LocalDate end;
    
    public int getDays() {
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }
}

// 值對象: 統計數據
@Embeddable
public class PayrollStatistics {
    @Column(name = "total_employees")
    private int totalEmployees;
    
    @Column(name = "processed_employees")
    private int processedEmployees;
    
    @Column(name = "failed_employees")
    private int failedEmployees;
    
    @Column(name = "total_gross_amount")
    private BigDecimal totalGrossAmount;
    
    @Column(name = "total_net_amount")
    private BigDecimal totalNetAmount;
    
    @Column(name = "total_deductions")
    private BigDecimal totalDeductions;
}

public enum PayrollRunStatus {
    DRAFT,       // 草稿
    CALCULATING, // 計算中
    COMPLETED,   // 已完成
    SUBMITTED,   // 送審中
    APPROVED,    // 已核准
    PAID,        // 已發放
    CANCELLED    // 已取消
}
```

#### 7.1.3 Payslip聚合根 (薪資單)

**職責:** 個人薪資明細，包含完整的薪資計算結果

**Java實作:**
```java
@Entity
@Table(name = "payslips")
public class Payslip {
    @EmbeddedId
    private PayslipId id;
    
    @Column(name = "payroll_run_id", nullable = false)
    private UUID payrollRunId;
    
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    @Column(name = "employee_number")
    private String employeeNumber;
    
    @Column(name = "employee_name")
    private String employeeName;
    
    @Embedded
    private PayPeriod payPeriod;
    
    @Column(name = "pay_date")
    private LocalDate payDate;
    
    // 薪資結構快照
    @Enumerated(EnumType.STRING)
    @Column(name = "payroll_system")
    private PayrollSystem payrollSystem;
    
    @Column(name = "base_salary")
    private BigDecimal baseSalary;
    
    // 收入項目
    @Type(JsonType.class)
    @Column(name = "earnings", columnDefinition = "jsonb")
    private List<PayslipItem> earnings;
    
    @Column(name = "total_earnings")
    private BigDecimal totalEarnings;
    
    // 加班費明細
    @Embedded
    private OvertimePayDetail overtimePay;
    
    @Column(name = "total_overtime_pay")
    private BigDecimal totalOvertimePay;
    
    // 請假扣款
    @Column(name = "leave_deduction")
    private BigDecimal leaveDeduction;
    
    // 應發薪資
    @Column(name = "gross_wage")
    private BigDecimal grossWage;
    
    // 保險費用
    @Embedded
    private InsuranceDeductions insuranceDeductions;
    
    // 稅金
    @Column(name = "income_tax")
    private BigDecimal incomeTax;
    
    @Column(name = "supplementary_premium")
    private BigDecimal supplementaryPremium;
    
    // 其他扣除項
    @Type(JsonType.class)
    @Column(name = "deductions", columnDefinition = "jsonb")
    private List<PayslipItem> deductions;
    
    @Column(name = "total_deductions")
    private BigDecimal totalDeductions;
    
    // 實發薪資
    @Column(name = "net_wage")
    private BigDecimal netWage;
    
    // 銀行帳戶
    @Embedded
    private BankAccount bankAccount;
    
    // 狀態
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PayslipStatus status;
    
    @Column(name = "pdf_url")
    private String pdfUrl;
    
    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;
    
    // ========== Factory Method ==========
    
    /**
     * 計算並建立薪資單
     */
    public static Payslip calculate(
            UUID runId,
            Employee employee,
            SalaryStructure structure,
            AttendanceMonthlyData attendanceData,
            InsuranceFees insuranceFees,
            IncomeTaxCalculator taxCalculator) {
        
        Payslip payslip = new Payslip();
        payslip.id = PayslipId.generate();
        payslip.payrollRunId = runId;
        payslip.employeeId = employee.getId();
        payslip.employeeNumber = employee.getEmployeeNumber();
        payslip.employeeName = employee.getName();
        payslip.payrollSystem = structure.getPayrollSystem();
        payslip.status = PayslipStatus.DRAFT;
        
        // 1. 計算基本薪資
        payslip.baseSalary = structure.getMonthlySalary();
        
        // 2. 計算固定收入項目
        payslip.earnings = structure.getSalaryItems().stream()
            .filter(item -> item.getItemType() == ItemType.EARNING)
            .map(item -> new PayslipItem(item.getItemCode(), item.getItemName(), item.getAmount()))
            .collect(Collectors.toList());
        
        payslip.totalEarnings = payslip.baseSalary.add(
            payslip.earnings.stream()
                .map(PayslipItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        
        // 3. 計算加班費
        payslip.overtimePay = calculateOvertimePay(
            structure.getOvertimeHourlyRate(),
            attendanceData.getOvertimeDetail()
        );
        payslip.totalOvertimePay = payslip.overtimePay.getTotal();
        
        // 4. 計算請假扣款
        payslip.leaveDeduction = calculateLeaveDeduction(
            structure.getMonthlySalary(),
            attendanceData.getLeaveDetail()
        );
        
        // 5. 應發薪資
        payslip.grossWage = payslip.totalEarnings
            .add(payslip.totalOvertimePay)
            .subtract(payslip.leaveDeduction);
        
        // 6. 保險費用
        payslip.insuranceDeductions = new InsuranceDeductions(
            insuranceFees.getLaborInsurance(),
            insuranceFees.getHealthInsurance(),
            insuranceFees.getPensionSelfContribution()
        );
        
        // 7. 所得稅
        payslip.incomeTax = taxCalculator.calculate(payslip.grossWage);
        
        // 8. 二代健保補充保費 (獎金超過投保金額4倍)
        payslip.supplementaryPremium = calculateSupplementaryPremium(
            payslip.totalOvertimePay,
            insuranceFees.getInsuredSalary()
        );
        
        // 9. 總扣除
        payslip.totalDeductions = payslip.insuranceDeductions.getTotal()
            .add(payslip.incomeTax)
            .add(payslip.supplementaryPremium);
        
        // 10. 實發薪資
        payslip.netWage = payslip.grossWage.subtract(payslip.totalDeductions);
        
        // 11. 銀行帳戶
        payslip.bankAccount = employee.getBankAccount();
        
        // 發布事件
        DomainEventPublisher.publish(new PayslipGeneratedEvent(
            payslip.id.getValue(),
            payslip.employeeId,
            payslip.grossWage,
            payslip.netWage
        ));
        
        return payslip;
    }
    
    /**
     * 計算加班費 (依勞基法)
     */
    private static OvertimePayDetail calculateOvertimePay(
            BigDecimal hourlyRate,
            OvertimeData overtime) {
        
        // 平日加班
        BigDecimal weekdayPay = BigDecimal.ZERO;
        if (overtime.getWeekdayHours().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal first2Hours = overtime.getWeekdayHours().min(new BigDecimal("2"));
            BigDecimal remaining = overtime.getWeekdayHours().subtract(first2Hours).max(BigDecimal.ZERO);
            
            weekdayPay = hourlyRate.multiply(new BigDecimal("1.34")).multiply(first2Hours)
                .add(hourlyRate.multiply(new BigDecimal("1.67")).multiply(remaining));
        }
        
        // 休息日加班
        BigDecimal restDayPay = BigDecimal.ZERO;
        if (overtime.getRestDayHours().compareTo(BigDecimal.ZERO) > 0) {
            // 簡化計算 (實際需分段)
            restDayPay = hourlyRate.multiply(new BigDecimal("1.67")).multiply(overtime.getRestDayHours());
        }
        
        // 假日加班
        BigDecimal holidayPay = hourlyRate.multiply(new BigDecimal("2.0")).multiply(overtime.getHolidayHours());
        
        return new OvertimePayDetail(
            overtime.getWeekdayHours(), weekdayPay,
            overtime.getRestDayHours(), restDayPay,
            overtime.getHolidayHours(), holidayPay
        );
    }
    
    /**
     * 定稿薪資單
     */
    public void finalize() {
        this.status = PayslipStatus.FINALIZED;
    }
    
    /**
     * 標記已發送
     */
    public void markAsSent() {
        this.status = PayslipStatus.SENT;
        this.emailSentAt = LocalDateTime.now();
    }
    
    /**
     * 設定PDF URL
     */
    public void setPdfUrl(String url) {
        this.pdfUrl = url;
    }
}

// 值對象
@Embeddable
public class OvertimePayDetail {
    private BigDecimal weekdayHours;
    private BigDecimal weekdayPay;
    private BigDecimal restDayHours;
    private BigDecimal restDayPay;
    private BigDecimal holidayHours;
    private BigDecimal holidayPay;
    
    public BigDecimal getTotal() {
        return weekdayPay.add(restDayPay).add(holidayPay);
    }
}

@Embeddable
public class InsuranceDeductions {
    @Column(name = "labor_insurance")
    private BigDecimal laborInsurance;
    
    @Column(name = "health_insurance")
    private BigDecimal healthInsurance;
    
    @Column(name = "pension_self_contribution")
    private BigDecimal pensionSelfContribution;
    
    public BigDecimal getTotal() {
        return laborInsurance.add(healthInsurance).add(pensionSelfContribution);
    }
}

public class PayslipItem {
    private String itemCode;
    private String itemName;
    private BigDecimal amount;
}

public enum PayslipStatus { DRAFT, FINALIZED, SENT }
```

### 7.2 薪資計算引擎 (Domain Service)

**職責:** 協調薪資計算Saga流程

```java
@Service
public class PayrollCalculationDomainService {
    
    private final ISalaryStructureRepository salaryStructureRepo;
    private final IPayrollRunRepository payrollRunRepo;
    private final IPayslipRepository payslipRepo;
    private final OrganizationServiceClient orgClient;
    private final AttendanceServiceClient attClient;
    private final InsuranceServiceClient insClient;
    private final IncomeTaxCalculator taxCalculator;
    
    /**
     * 執行薪資計算Saga
     */
    @Transactional
    public PayrollRunResult execute(UUID runId) {
        PayrollRun run = payrollRunRepo.findById(runId);
        
        // 1. 獲取員工清單
        List<Employee> employees = orgClient.getActiveEmployees(run.getOrganizationId());
        run.startExecution(SecurityContext.getCurrentUserId(), employees.size());
        
        PayrollStatistics stats = new PayrollStatistics(employees.size());
        List<Payslip> payslips = new ArrayList<>();
        
        // 2. 對每位員工計算薪資
        for (Employee employee : employees) {
            try {
                // 2.1 獲取薪資結構
                SalaryStructure structure = salaryStructureRepo
                    .findByEmployeeAndEffectiveDate(employee.getId(), run.getPayPeriod().getEnd());
                
                if (structure == null) {
                    stats.incrementFailed();
                    continue;
                }
                
                // 2.2 獲取差勤數據
                AttendanceMonthlyData attendance = attClient.getMonthlyData(
                    employee.getId(),
                    run.getPayPeriod().getStart(),
                    run.getPayPeriod().getEnd()
                );
                
                // 2.3 獲取保險費用
                InsuranceFees insurance = insClient.calculateFees(
                    employee.getId(),
                    structure.calculateInsurableSalary()
                );
                
                // 2.4 計算薪資單
                Payslip payslip = Payslip.calculate(
                    runId, employee, structure, attendance, insurance, taxCalculator
                );
                
                payslips.add(payslip);
                stats.addPayslip(payslip);
                stats.incrementProcessed();
                
            } catch (Exception e) {
                log.error("計算員工 {} 薪資失敗: {}", employee.getId(), e.getMessage());
                stats.incrementFailed();
            }
        }
        
        // 3. 儲存所有薪資單
        payslipRepo.saveAll(payslips);
        
        // 4. 完成批次
        run.complete(stats);
        payrollRunRepo.save(run);
        
        return new PayrollRunResult(run, payslips.size(), stats.getFailedEmployees());
    }
}
```

### 7.3 Repository介面

```java
public interface ISalaryStructureRepository {
    SalaryStructure findById(StructureId id);
    SalaryStructure findByEmployeeId(UUID employeeId);
    SalaryStructure findByEmployeeAndEffectiveDate(UUID employeeId, LocalDate date);
    List<SalaryStructure> findByOrganization(UUID orgId);
    void save(SalaryStructure structure);
}

public interface IPayrollRunRepository {
    PayrollRun findById(RunId id);
    List<PayrollRun> findByOrganization(UUID orgId, int year);
    PayrollRun findByOrganizationAndPeriod(UUID orgId, LocalDate start, LocalDate end);
    void save(PayrollRun run);
}

public interface IPayslipRepository {
    Payslip findById(PayslipId id);
    List<Payslip> findByPayrollRun(UUID runId);
    List<Payslip> findByEmployeeId(UUID employeeId);
    List<Payslip> findByEmployeeAndYear(UUID employeeId, int year);
    void save(Payslip payslip);
    void saveAll(List<Payslip> payslips);
}
```

---

## 8. 領域事件設計

### 8.1 事件清單

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 |
|:---|:---|:---|:---|
| `SalaryStructureCreated` | 建立薪資結構 | Payroll | Insurance |
| `SalaryStructureChanged` | 薪資調整 | Payroll | Insurance |
| `PayrollRunStarted` | 開始薪資計算 | Payroll | - |
| `PayrollRunCompleted` | 薪資計算完成 | Payroll | Notification, Report |
| `PayslipGenerated` | 產生薪資單 | Payroll | Notification, Document |
| `PayrollApproved` | 薪資核准 | Payroll | - |
| `PayrollPaid` | 薪資已發放 | Payroll | Report |
| `PayslipSent` | 薪資單已寄送 | Payroll | - |

### 8.2 事件Schema與範例

#### 8.2.1 PayrollRunCompletedEvent

```json
{
  "eventId": "evt-pay-001",
  "eventType": "PayrollRunCompleted",
  "timestamp": "2025-12-01T10:30:00Z",
  "payload": {
    "runId": "run-202512",
    "organizationId": "org-001",
    "payPeriod": "2025-11-01 ~ 2025-11-30",
    "payDate": "2025-12-05",
    "statistics": {
      "totalEmployees": 150,
      "processedEmployees": 148,
      "failedEmployees": 2,
      "totalGrossAmount": 8500000,
      "totalNetAmount": 7200000,
      "totalDeductions": 1300000
    }
  }
}
```

#### 8.2.2 PayslipGeneratedEvent

```json
{
  "eventId": "evt-slip-001",
  "eventType": "PayslipGenerated",
  "timestamp": "2025-12-01T10:25:00Z",
  "payload": {
    "payslipId": "slip-001",
    "payrollRunId": "run-202512",
    "employeeId": "emp-001",
    "employeeName": "張三",
    "payPeriod": "2025-11",
    "grossWage": 60600,
    "netWage": 56600,
    "bankAccount": {
      "bankCode": "012",
      "accountNumber": "****5678"
    }
  }
}
```

---

## 9. API設計

### 9.1 API總覽

| 模組 | API數量 | 說明 |
|:---|:---:|:---|
| 薪資結構管理 | 5 | CRUD + 查詢 |
| 薪資計算批次 | 8 | 建立、執行、核准、發放等 |
| 薪資單管理 | 4 | 查詢、下載PDF、發送Email |
| 薪轉檔案 | 2 | 產生、下載銀行媒體檔 |
| **合計** | **19** | |

### 9.2 Controller命名對照 (符合命名規範)

| Controller | 說明 |
|:---|:---|
| `HR04SalaryStructureCmdController` | 薪資結構Command操作 |
| `HR04SalaryStructureQryController` | 薪資結構Query操作 |
| `HR04PayrollRunCmdController` | 薪資計算批次Command操作 |
| `HR04PayrollRunQryController` | 薪資計算批次Query操作 |
| `HR04PayslipCmdController` | 薪資單Command操作 |
| `HR04PayslipQryController` | 薪資單Query操作 |
| `HR04BankTransferCmdController` | 薪轉檔案Command操作 |

### 9.3 薪資結構API

#### 9.3.1 建立薪資結構

**端點:** `POST /api/v1/salary-structures`

**Controller:** `HR04SalaryStructureCmdController`

**權限:** `payroll:structure:manage`

**Request:**
```json
{
  "employeeId": "emp-001",
  "payrollSystem": "MONTHLY",
  "payrollCycle": "MONTHLY",
  "monthlySalary": 50000,
  "salaryItems": [
    {
      "itemCode": "JOB_ALLOWANCE",
      "itemName": "職務加給",
      "itemType": "EARNING",
      "amount": 5000,
      "isFixedAmount": true,
      "isTaxable": true,
      "isInsurable": true
    },
    {
      "itemCode": "MEAL_ALLOWANCE",
      "itemName": "伙食津貼",
      "itemType": "EARNING",
      "amount": 2400,
      "isFixedAmount": true,
      "isTaxable": false,
      "isInsurable": false
    }
  ],
  "effectiveDate": "2025-01-01"
}
```

**Response 201:**
```json
{
  "structureId": "struct-001",
  "employeeId": "emp-001",
  "monthlySalary": 50000,
  "calculatedHourlyRate": 208.33,
  "totalMonthlyGross": 57400,
  "effectiveDate": "2025-01-01"
}
```

### 9.4 薪資計算批次API

#### 9.4.1 建立薪資計算批次

**端點:** `POST /api/v1/payroll-runs`

**Controller:** `HR04PayrollRunCmdController`

**權限:** `payroll:run:create`

**Request:**
```json
{
  "organizationId": "org-001",
  "payPeriodStart": "2025-11-01",
  "payPeriodEnd": "2025-11-30",
  "payDate": "2025-12-05"
}
```

**Response 201:**
```json
{
  "runId": "run-202512",
  "status": "DRAFT",
  "payPeriodStart": "2025-11-01",
  "payPeriodEnd": "2025-11-30",
  "payDate": "2025-12-05"
}
```

#### 9.4.2 執行薪資計算

**端點:** `POST /api/v1/payroll-runs/{runId}/execute`

**Controller:** `HR04PayrollRunCmdController`

**權限:** `payroll:run:execute`

**Response 202 Accepted:**
```json
{
  "runId": "run-202512",
  "status": "CALCULATING",
  "message": "薪資計算已啟動，請稍後查詢結果"
}
```

#### 9.4.3 核准薪資

**端點:** `PUT /api/v1/payroll-runs/{runId}/approve`

**Controller:** `HR04PayrollRunCmdController`

**權限:** `payroll:run:approve`

**Response 200:**
```json
{
  "runId": "run-202512",
  "status": "APPROVED",
  "approvedBy": "財務經理",
  "approvedAt": "2025-12-02T09:00:00Z"
}
```

### 9.5 薪資單API

#### 9.5.1 查詢我的薪資單 (ESS)

**端點:** `GET /api/v1/payslips/my?year={year}`

**Controller:** `HR04PayslipQryController`

**權限:** `payroll:payslip:read:self`

**Response 200:**
```json
{
  "employeeId": "emp-001",
  "year": 2025,
  "payslips": [
    {
      "payslipId": "slip-001",
      "payPeriod": "2025-11",
      "payDate": "2025-12-05",
      "grossWage": 60600,
      "netWage": 56600,
      "status": "SENT"
    }
  ]
}
```

#### 9.5.2 查詢薪資單詳情

**端點:** `GET /api/v1/payslips/{payslipId}`

**Controller:** `HR04PayslipQryController`

**Response 200:**
```json
{
  "payslipId": "slip-001",
  "employeeNumber": "E0001",
  "employeeName": "張三",
  "payPeriod": "2025-11",
  "payDate": "2025-12-05",
  "baseSalary": 50000,
  "earnings": [
    {"itemName": "職務加給", "amount": 5000},
    {"itemName": "伙食津貼", "amount": 2400}
  ],
  "totalEarnings": 57400,
  "overtimePay": {
    "weekdayHours": 8,
    "weekdayPay": 3200,
    "restDayHours": 0,
    "restDayPay": 0,
    "holidayHours": 0,
    "holidayPay": 0,
    "total": 3200
  },
  "leaveDeduction": 0,
  "grossWage": 60600,
  "deductions": {
    "laborInsurance": 1200,
    "healthInsurance": 800,
    "pensionSelfContribution": 0,
    "incomeTax": 2000,
    "supplementaryPremium": 0
  },
  "totalDeductions": 4000,
  "netWage": 56600,
  "pdfUrl": "/api/v1/payslips/slip-001/pdf"
}
```

#### 9.5.3 下載薪資單PDF

**端點:** `GET /api/v1/payslips/{payslipId}/pdf`

**Controller:** `HR04PayslipQryController`

**Response 200:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="payslip_202511.pdf"

(加密PDF檔案，密碼為身分證後4碼)
```

---

## 10. 工項清單摘要

### 前端開發工項
1. HR04-P01 薪資結構設定頁面
2. HR04-P02 薪資項目設定頁面
3. HR04-P03 薪資計算批次頁面
4. HR04-P04 薪資計算明細頁面
5. HR04-P05 薪資核准頁面
6. HR04-P06 我的薪資單頁面 (ESS)
7. HR04-P07 員工薪資查詢頁面
8. HR04-P08 薪轉檔案產生頁面
9. HR04-M01 薪資結構編輯Modal
10. HR04-M02 薪資項目編輯Modal

### 後端開發工項
1. SalaryStructure聚合根與Repository
2. PayrollRun聚合根與Repository
3. Payslip聚合根與Repository
4. PayrollCalculationDomainService (Saga引擎)
5. OvertimePayCalculator (加班費計算器)
6. IncomeTaxCalculator (所得稅計算器)
7. 薪資結構API (5端點)
8. 薪資計算批次API (8端點)
9. 薪資單API (4端點)
10. 薪轉檔案API (2端點)
11. PDF生成服務 (加密薪資單)
12. 與Organization/Attendance/Insurance服務整合

### 資料庫開發工項
1. 建立6個資料表DDL
2. 建立索引與約束
3. 初始化薪資項目定義
4. 初始化所得稅級距

---

**文件完成日期:** 2025-12-07  
**版本:** 1.0
