## 7. Domain設計

### 7.1 聚合根 (Aggregate Root)

#### 7.1.1 Shift聚合根 (班別)

**職責:** 定義工作班別與工時規則

**Java實作:**
```java
@Entity
@Table(name = "shifts")
public class Shift {
    @EmbeddedId
    private ShiftId id;
    
    @Column(name = "shift_code", unique = true, nullable = false)
    private String shiftCode;
    
    @Column(name = "shift_name", nullable = false)
    private String shiftName;
    
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", nullable = false)
    private ShiftType shiftType;
    
    @Embedded
    private WorkingTime workingTime;
    
    @Column(name = "late_tolerance_minutes")
    private Integer lateToleranceMinutes;
    
    @Column(name = "early_leave_tolerance_minutes")
    private Integer earlyLeaveToleranceMinutes;
    
    @Column(name = "is_active")
    private boolean isActive;
    
    // ========== Domain行為 ==========
    
    /**
     * 檢查打卡時間是否遲到
     */
    public LateResult checkLate(LocalTime checkInTime) {
        LocalTime tolerance = workingTime.getWorkStartTime()
            .plusMinutes(lateToleranceMinutes);
        
        if (checkInTime.isAfter(tolerance)) {
            int lateMinutes = (int) Duration.between(
                workingTime.getWorkStartTime(), checkInTime).toMinutes();
            return new LateResult(true, lateMinutes);
        }
        return new LateResult(false, 0);
    }
    
    /**
     * 檢查打卡時間是否早退
     */
    public EarlyLeaveResult checkEarlyLeave(LocalTime checkOutTime) {
        LocalTime tolerance = workingTime.getWorkEndTime()
            .minusMinutes(earlyLeaveToleranceMinutes);
        
        if (checkOutTime.isBefore(tolerance)) {
            int earlyMinutes = (int) Duration.between(
                checkOutTime, workingTime.getWorkEndTime()).toMinutes();
            return new EarlyLeaveResult(true, earlyMinutes);
        }
        return new EarlyLeaveResult(false, 0);
    }
    
    /**
     * 計算實際工時
     */
    public BigDecimal calculateWorkingHours(LocalTime checkIn, LocalTime checkOut) {
        Duration workDuration = Duration.between(checkIn, checkOut);
        
        // 扣除休息時間
        if (hasBreakTime()) {
            Duration breakDuration = Duration.between(
                workingTime.getBreakStartTime(), 
                workingTime.getBreakEndTime());
            workDuration = workDuration.minus(breakDuration);
        }
        
        return BigDecimal.valueOf(workDuration.toMinutes())
            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}

// 值對象
@Embeddable
public class WorkingTime {
    @Column(name = "work_start_time")
    private LocalTime workStartTime;
    
    @Column(name = "work_end_time")
    private LocalTime workEndTime;
    
    @Column(name = "break_start_time")
    private LocalTime breakStartTime;
    
    @Column(name = "break_end_time")
    private LocalTime breakEndTime;
}
```

#### 7.1.2 AttendanceRecord聚合根 (打卡記錄)

**職責:** 記錄員工打卡與計算出勤狀態

**Java實作:**
```java
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {
    @EmbeddedId
    private RecordId id;
    
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;
    
    @Column(name = "shift_id")
    private UUID shiftId;
    
    @Embedded
    private CheckInInfo checkInInfo;
    
    @Embedded
    private CheckOutInfo checkOutInfo;
    
    @Column(name = "working_hours")
    private BigDecimal workingHours;
    
    @Column(name = "is_late")
    private boolean isLate;
    
    @Column(name = "late_minutes")
    private int lateMinutes;
    
    @Column(name = "is_early_leave")
    private boolean isEarlyLeave;
    
    @Column(name = "early_leave_minutes")
    private int earlyLeaveMinutes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "anomaly_type")
    private AnomalyType anomalyType;
    
    @Column(name = "is_corrected")
    private boolean isCorrected;
    
    // ========== Factory Method ==========
    
    /**
     * 上班打卡
     */
    public static AttendanceRecord checkIn(
            UUID employeeId, 
            LocalDate date,
            Shift shift,
            CheckInRequest request) {
        
        AttendanceRecord record = new AttendanceRecord();
        record.id = RecordId.generate();
        record.employeeId = employeeId;
        record.recordDate = date;
        record.shiftId = shift.getId().getValue();
        
        // 設定打卡資訊
        record.checkInInfo = new CheckInInfo(
            request.getCheckInTime(),
            request.getLatitude(),
            request.getLongitude(),
            request.getIpAddress()
        );
        
        // 檢查遲到
        LateResult lateResult = shift.checkLate(
            request.getCheckInTime().toLocalTime());
        record.isLate = lateResult.isLate();
        record.lateMinutes = lateResult.getLateMinutes();
        
        if (record.isLate) {
            record.anomalyType = AnomalyType.LATE;
            
            // 發布異常事件
            DomainEventPublisher.publish(new AttendanceAnomalyDetectedEvent(
                record.employeeId,
                record.recordDate,
                AnomalyType.LATE,
                record.lateMinutes
            ));
        }
        
        // 發布打卡事件
        DomainEventPublisher.publish(new AttendanceRecordedEvent(
            record.id.getValue(),
            record.employeeId,
            record.recordDate,
            record.checkInInfo.getCheckInTime(),
            null
        ));
        
        return record;
    }
    
    /**
     * 下班打卡
     */
    public void checkOut(Shift shift, CheckOutRequest request) {
        if (this.checkOutInfo != null && this.checkOutInfo.getCheckOutTime() != null) {
            throw new DomainException("今日已完成下班打卡");
        }
        
        this.checkOutInfo = new CheckOutInfo(
            request.getCheckOutTime(),
            request.getLatitude(),
            request.getLongitude(),
            request.getIpAddress()
        );
        
        // 檢查早退
        EarlyLeaveResult result = shift.checkEarlyLeave(
            request.getCheckOutTime().toLocalTime());
        this.isEarlyLeave = result.isEarlyLeave();
        this.earlyLeaveMinutes = result.getEarlyMinutes();
        
        if (this.isEarlyLeave) {
            this.anomalyType = AnomalyType.EARLY_LEAVE;
        }
        
        // 計算工時
        this.workingHours = shift.calculateWorkingHours(
            this.checkInInfo.getCheckInTime().toLocalTime(),
            request.getCheckOutTime().toLocalTime()
        );
    }
    
    /**
     * 補卡
     */
    public void correct(LocalDateTime correctedTime, CorrectionType type) {
        if (type == CorrectionType.CHECK_IN) {
            this.checkInInfo = new CheckInInfo(
                correctedTime, null, null, "CORRECTED");
            this.isLate = false;
            this.lateMinutes = 0;
        } else {
            this.checkOutInfo = new CheckOutInfo(
                correctedTime, null, null, "CORRECTED");
            this.isEarlyLeave = false;
            this.earlyLeaveMinutes = 0;
        }
        this.isCorrected = true;
        this.anomalyType = null;
    }
}

// 值對象
@Embeddable
public class CheckInInfo {
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_in_latitude")
    private BigDecimal latitude;
    
    @Column(name = "check_in_longitude")
    private BigDecimal longitude;
    
    @Column(name = "check_in_ip")
    private String ipAddress;
}
```

#### 7.1.3 LeaveApplication聚合根 (請假申請)

**職責:** 管理員工請假申請與審核

**Java實作:**
```java
@Entity
@Table(name = "leave_applications")
public class LeaveApplication {
    @EmbeddedId
    private ApplicationId id;
    
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    @Column(name = "leave_type_id", nullable = false)
    private UUID leaveTypeId;
    
    @Embedded
    private LeavePeriod leavePeriod;
    
    @Column(name = "total_days", nullable = false)
    private BigDecimal totalDays;
    
    @Column(name = "reason", nullable = false)
    private String reason;
    
    @Column(name = "proof_attachment_url")
    private String proofAttachmentUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status;
    
    @Column(name = "approver_id")
    private UUID approverId;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    // ========== Factory Method ==========
    
    /**
     * 建立請假申請
     */
    public static LeaveApplication create(
            UUID employeeId,
            LeaveType leaveType,
            LeaveBalance balance,
            CreateLeaveRequest request) {
        
        // 計算請假天數
        BigDecimal days = calculateLeaveDays(
            request.getStartDate(), 
            request.getEndDate(),
            request.getStartPeriod(),
            request.getEndPeriod()
        );
        
        // 驗證餘額
        if (balance.getRemainingDays().compareTo(days) < 0) {
            throw new DomainException(
                String.format("假期餘額不足，剩餘 %s 天，申請 %s 天",
                    balance.getRemainingDays(), days));
        }
        
        // 驗證是否需要證明文件
        if (leaveType.isRequiresProof() && 
            StringUtils.isBlank(request.getProofAttachmentUrl())) {
            throw new DomainException("此假別需要上傳證明文件");
        }
        
        LeaveApplication app = new LeaveApplication();
        app.id = ApplicationId.generate();
        app.employeeId = employeeId;
        app.leaveTypeId = leaveType.getId().getValue();
        app.leavePeriod = new LeavePeriod(
            request.getStartDate(),
            request.getEndDate(),
            request.getStartPeriod(),
            request.getEndPeriod()
        );
        app.totalDays = days;
        app.reason = request.getReason();
        app.proofAttachmentUrl = request.getProofAttachmentUrl();
        app.status = ApplicationStatus.PENDING;
        
        // 發布事件
        DomainEventPublisher.publish(new LeaveAppliedEvent(
            app.id.getValue(),
            app.employeeId,
            app.leaveTypeId,
            app.totalDays
        ));
        
        return app;
    }
    
    /**
     * 核准
     */
    public void approve(UUID approverId, LeaveBalance balance) {
        if (this.status != ApplicationStatus.PENDING) {
            throw new DomainException("只能核准待審核的申請");
        }
        
        this.status = ApplicationStatus.APPROVED;
        this.approverId = approverId;
        this.approvedAt = LocalDateTime.now();
        
        // 扣除餘額
        balance.deduct(this.totalDays);
        
        // 發布事件
        DomainEventPublisher.publish(new LeaveApprovedEvent(
            this.id.getValue(),
            this.employeeId,
            this.leaveTypeId,
            this.totalDays
        ));
    }
    
    /**
     * 駁回
     */
    public void reject(UUID approverId, String reason) {
        if (this.status != ApplicationStatus.PENDING) {
            throw new DomainException("只能駁回待審核的申請");
        }
        
        this.status = ApplicationStatus.REJECTED;
        this.approverId = approverId;
        this.rejectionReason = reason;
        
        DomainEventPublisher.publish(new LeaveRejectedEvent(
            this.id.getValue(),
            this.employeeId,
            reason
        ));
    }
    
    /**
     * 取消
     */
    public void cancel(LeaveBalance balance) {
        if (this.status == ApplicationStatus.CANCELLED) {
            throw new DomainException("申請已取消");
        }
        
        // 若已核准，退回餘額
        if (this.status == ApplicationStatus.APPROVED) {
            balance.refund(this.totalDays);
        }
        
        this.status = ApplicationStatus.CANCELLED;
    }
}
```

#### 7.1.4 OvertimeApplication聚合根 (加班申請)

**職責:** 管理員工加班申請，包含勞基法時數管控

**Java實作:**
```java
@Entity
@Table(name = "overtime_applications")
public class OvertimeApplication {
    @EmbeddedId
    private OvertimeId id;
    
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    @Column(name = "overtime_date", nullable = false)
    private LocalDate overtimeDate;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "overtime_hours", nullable = false)
    private BigDecimal overtimeHours;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "overtime_type", nullable = false)
    private OvertimeType overtimeType;
    
    @Column(name = "reason", nullable = false)
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "compensation_type")
    private CompensationType compensationType;
    
    @Column(name = "is_compensated")
    private boolean isCompensated;
    
    // 勞基法限制常數
    private static final BigDecimal MONTHLY_LIMIT = new BigDecimal("46");
    private static final BigDecimal QUARTERLY_LIMIT = new BigDecimal("138");
    
    // ========== Factory Method ==========
    
    /**
     * 建立加班申請
     */
    public static OvertimeApplication create(
            UUID employeeId,
            OvertimeStatistics statistics,
            CreateOvertimeRequest request) {
        
        // 計算加班時數
        BigDecimal hours = calculateHours(request.getStartTime(), request.getEndTime());
        
        // 檢查月加班上限 (46小時)
        BigDecimal newMonthly = statistics.getMonthlyHours().add(hours);
        if (newMonthly.compareTo(MONTHLY_LIMIT) > 0) {
            DomainEventPublisher.publish(new OvertimeLimitExceededEvent(
                employeeId,
                newMonthly,
                MONTHLY_LIMIT,
                "MONTHLY"
            ));
            throw new DomainException(
                String.format("超過月加班上限，目前累計 %s 小時 + 本次 %s 小時 > 46小時",
                    statistics.getMonthlyHours(), hours));
        }
        
        // 檢查季加班上限 (138小時)
        BigDecimal newQuarterly = statistics.getQuarterlyHours().add(hours);
        if (newQuarterly.compareTo(QUARTERLY_LIMIT) > 0) {
            DomainEventPublisher.publish(new OvertimeLimitExceededEvent(
                employeeId,
                newQuarterly,
                QUARTERLY_LIMIT,
                "QUARTERLY"
            ));
            throw new DomainException("超過季加班上限 (138小時)");
        }
        
        OvertimeApplication app = new OvertimeApplication();
        app.id = OvertimeId.generate();
        app.employeeId = employeeId;
        app.overtimeDate = request.getOvertimeDate();
        app.startTime = request.getStartTime();
        app.endTime = request.getEndTime();
        app.overtimeHours = hours;
        app.overtimeType = request.getOvertimeType();
        app.reason = request.getReason();
        app.compensationType = request.getCompensationType();
        app.status = ApplicationStatus.PENDING;
        
        DomainEventPublisher.publish(new OvertimeAppliedEvent(
            app.id.getValue(),
            app.employeeId,
            app.overtimeHours,
            app.overtimeType
        ));
        
        return app;
    }
    
    /**
     * 核准
     */
    public void approve(UUID approverId) {
        if (this.status != ApplicationStatus.PENDING) {
            throw new DomainException("只能核准待審核的申請");
        }
        
        this.status = ApplicationStatus.APPROVED;
        
        DomainEventPublisher.publish(new OvertimeApprovedEvent(
            this.id.getValue(),
            this.employeeId,
            this.overtimeHours,
            this.overtimeType,
            this.compensationType
        ));
    }
}
```

#### 7.1.5 LeaveBalance聚合根 (假期餘額)

**職責:** 管理員工各類假別的餘額

**Java實作:**
```java
@Entity
@Table(name = "leave_balances")
public class LeaveBalance {
    @EmbeddedId
    private BalanceId id;
    
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;
    
    @Column(name = "leave_type_id", nullable = false)
    private UUID leaveTypeId;
    
    @Column(name = "year", nullable = false)
    private int year;
    
    @Column(name = "total_days", nullable = false)
    private BigDecimal totalDays;
    
    @Column(name = "used_days", nullable = false)
    private BigDecimal usedDays;
    
    @Column(name = "remaining_days", nullable = false)
    private BigDecimal remainingDays;
    
    @Column(name = "is_annual_leave")
    private boolean isAnnualLeave;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    // ========== Domain行為 ==========
    
    /**
     * 扣除假期
     */
    public void deduct(BigDecimal days) {
        if (this.remainingDays.compareTo(days) < 0) {
            throw new DomainException("假期餘額不足");
        }
        
        this.usedDays = this.usedDays.add(days);
        this.remainingDays = this.totalDays.subtract(this.usedDays);
    }
    
    /**
     * 退回假期
     */
    public void refund(BigDecimal days) {
        this.usedDays = this.usedDays.subtract(days);
        this.remainingDays = this.totalDays.subtract(this.usedDays);
    }
    
    /**
     * 檢查是否即將到期
     */
    public boolean isExpiringSoon(int daysThreshold) {
        if (this.expiryDate == null) return false;
        
        LocalDate threshold = LocalDate.now().plusDays(daysThreshold);
        return this.expiryDate.isBefore(threshold) || 
               this.expiryDate.isEqual(threshold);
    }
}
```

#### 7.1.6 AnnualLeavePolicy聚合根 (特休假政策)

**職責:** 依勞基法計算特休天數

**Java實作:**
```java
@Entity
@Table(name = "annual_leave_policies")
public class AnnualLeavePolicy {
    @EmbeddedId
    private PolicyId id;
    
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "policy_id")
    private List<AnnualLeaveRule> rules;
    
    @Column(name = "expiry_months")
    private int expiryMonths;
    
    @Column(name = "compensation_enabled")
    private boolean compensationEnabled;
    
    /**
     * 計算特休天數 (依勞基法)
     */
    public int calculateAnnualLeaveDays(LocalDate hireDate, LocalDate currentDate) {
        BigDecimal serviceYears = calculateServiceYears(hireDate, currentDate);
        
        // 未滿6個月，無特休
        if (serviceYears.compareTo(new BigDecimal("0.5")) < 0) {
            return 0;
        }
        
        // 找到對應規則
        for (AnnualLeaveRule rule : rules) {
            if (serviceYears.compareTo(rule.getMinServiceYears()) >= 0) {
                if (rule.getMaxServiceYears() == null ||
                    serviceYears.compareTo(rule.getMaxServiceYears()) < 0) {
                    
                    // 10年以上特殊計算
                    if (serviceYears.compareTo(new BigDecimal("10")) >= 0) {
                        int extraYears = serviceYears.intValue() - 10;
                        int extraDays = Math.min(extraYears, 15); // 最多加15天
                        return rule.getLeaveDays() + extraDays;  // 15 + extra, 上限30
                    }
                    
                    return rule.getLeaveDays();
                }
            }
        }
        
        return 0;
    }
    
    /**
     * 計算未休工資補償
     */
    public BigDecimal calculateUnusedCompensation(
            LeaveBalance balance, 
            BigDecimal dailySalary) {
        if (!compensationEnabled) return BigDecimal.ZERO;
        
        return balance.getRemainingDays().multiply(dailySalary);
    }
}
```

### 7.2 Repository介面

```java
// AttendanceRecord Repository
public interface IAttendanceRecordRepository {
    AttendanceRecord findById(RecordId id);
    AttendanceRecord findByEmployeeAndDate(UUID employeeId, LocalDate date);
    List<AttendanceRecord> findByEmployeeAndMonth(UUID employeeId, YearMonth month);
    List<AttendanceRecord> findAnomalies(UUID employeeId, LocalDate from, LocalDate to);
    void save(AttendanceRecord record);
}

// LeaveApplication Repository
public interface ILeaveApplicationRepository {
    LeaveApplication findById(ApplicationId id);
    List<LeaveApplication> findByEmployeeId(UUID employeeId);
    List<LeaveApplication> findPendingByApprover(UUID approverId);
    List<LeaveApplication> findOverlapping(UUID employeeId, LocalDate start, LocalDate end);
    void save(LeaveApplication application);
}

// OvertimeApplication Repository
public interface IOvertimeApplicationRepository {
    OvertimeApplication findById(OvertimeId id);
    List<OvertimeApplication> findByEmployeeId(UUID employeeId);
    OvertimeStatistics getStatistics(UUID employeeId, YearMonth month);
    void save(OvertimeApplication application);
}

// LeaveBalance Repository
public interface ILeaveBalanceRepository {
    LeaveBalance findByEmployeeAndTypeAndYear(UUID employeeId, UUID leaveTypeId, int year);
    List<LeaveBalance> findByEmployeeId(UUID employeeId);
    List<LeaveBalance> findExpiringBefore(LocalDate date);
    void save(LeaveBalance balance);
}
```

---

## 8. 領域事件設計

### 8.1 事件清單

| 事件名稱 | 觸發時機 | 發布服務 | 訂閱服務 |
|:---|:---|:---|:---|
| `AttendanceRecorded` | 員工打卡 | Attendance | - |
| `AttendanceAnomalyDetected` | 偵測到打卡異常 | Attendance | Notification |
| `LeaveApplied` | 請假申請提交 | Attendance | Workflow |
| `LeaveApproved` | 請假審核通過 | Attendance | Payroll, Notification |
| `LeaveRejected` | 請假審核駁回 | Attendance | Notification |
| `LeaveCancelled` | 請假取消 | Attendance | - |
| `OvertimeApplied` | 加班申請提交 | Attendance | Workflow |
| `OvertimeApproved` | 加班審核通過 | Attendance | Payroll |
| `OvertimeLimitExceeded` | 加班時數超過上限 | Attendance | Notification |
| `AnnualLeaveExpiring` | 特休即將到期 | Attendance | Notification |
| `AttendanceMonthClosed` | 月度差勤結算 | Attendance | Payroll, Report |

### 8.2 事件Schema與範例

#### 8.2.1 AttendanceRecordedEvent

```json
{
  "eventId": "evt-att-001",
  "eventType": "AttendanceRecorded",
  "timestamp": "2025-12-06T09:05:00Z",
  "payload": {
    "recordId": "550e8400-e29b-41d4-a716-446655440000",
    "employeeId": "emp-001",
    "recordDate": "2025-12-06",
    "checkInTime": "2025-12-06T09:05:00",
    "checkOutTime": null,
    "isLate": true,
    "lateMinutes": 5
  }
}
```

#### 8.2.2 LeaveApprovedEvent

```json
{
  "eventId": "evt-lv-002",
  "eventType": "LeaveApproved",
  "timestamp": "2025-12-06T10:30:00Z",
  "payload": {
    "applicationId": "app-leave-001",
    "employeeId": "emp-001",
    "leaveTypeId": "lt-annual",
    "leaveTypeName": "特休假",
    "startDate": "2025-12-09",
    "endDate": "2025-12-10",
    "totalDays": 2,
    "isPaid": true,
    "payRate": 1.0,
    "approverId": "mgr-001",
    "approvedAt": "2025-12-06T10:30:00"
  }
}
```

#### 8.2.3 OvertimeApprovedEvent

```json
{
  "eventId": "evt-ot-003",
  "eventType": "OvertimeApproved",
  "timestamp": "2025-12-06T11:00:00Z",
  "payload": {
    "overtimeId": "ot-001",
    "employeeId": "emp-001",
    "overtimeDate": "2025-12-05",
    "overtimeHours": 2.5,
    "overtimeType": "WEEKDAY",
    "compensationType": "PAY",
    "approverId": "mgr-001"
  }
}
```

#### 8.2.4 AttendanceMonthClosedEvent (關鍵事件)

```json
{
  "eventId": "evt-close-004",
  "eventType": "AttendanceMonthClosed",
  "timestamp": "2025-12-01T00:00:00Z",
  "payload": {
    "month": "2025-11",
    "employeeId": "emp-001",
    "summary": {
      "totalWorkDays": 22,
      "actualWorkDays": 20,
      "totalLeaveDays": 2,
      "totalOvertimeHours": 15.5,
      "lateCount": 3,
      "lateTotalMinutes": 25,
      "earlyLeaveCount": 1,
      "earlyLeaveTotalMinutes": 10
    },
    "leaveDetails": [
      {"leaveType": "ANNUAL", "days": 1.5, "isPaid": true},
      {"leaveType": "SICK", "days": 0.5, "isPaid": false}
    ],
    "overtimeDetails": [
      {"type": "WEEKDAY", "hours": 10.5, "compensation": "PAY"},
      {"type": "REST_DAY", "hours": 5, "compensation": "PAY"}
    ]
  }
}
```

---

## 9. API設計

### 9.1 API總覽

| 模組 | API數量 | 說明 |
|:---|:---:|:---|
| 打卡管理 | 6 | 打卡、查詢記錄、補卡申請 |
| 請假管理 | 8 | 餘額、申請、審核、取消 |
| 加班管理 | 6 | 申請、審核、統計 |
| 班別管理 | 4 | CRUD |
| 假別管理 | 4 | CRUD |
| 報表結算 | 3 | 月報、結算 |
| **合計** | **31** | |

### 9.2 Controller命名對照 (符合命名規範)

| Controller | 說明 |
|:---|:---|
| `HR03CheckInCmdController` | 打卡Command操作 |
| `HR03CheckInQryController` | 打卡記錄Query操作 |
| `HR03LeaveCmdController` | 請假Command操作 |
| `HR03LeaveQryController` | 請假Query操作 |
| `HR03OvertimeCmdController` | 加班Command操作 |
| `HR03OvertimeQryController` | 加班Query操作 |
| `HR03ShiftCmdController` | 班別管理Command操作 |
| `HR03ShiftQryController` | 班別Query操作 |
| `HR03ReportQryController` | 報表Query操作 |
| `HR03MonthCloseCmdController` | 月結Command操作 |

### 9.3 打卡管理API

#### 9.3.1 上班打卡

**端點:** `POST /api/v1/attendance/check-in`

**Controller:** `HR03CheckInCmdController`

**Service:** `CheckInServiceImpl`

**Request:**
```json
{
  "employeeId": "550e8400-e29b-41d4-a716-446655440000",
  "checkInTime": "2025-12-06T09:05:00",
  "location": {
    "latitude": 25.0330,
    "longitude": 121.5654
  },
  "ipAddress": "192.168.1.100"
}
```

**Response 200:**
```json
{
  "recordId": "550e8400-e29b-41d4-a716-446655440001",
  "checkInTime": "2025-12-06T09:05:00",
  "isLate": true,
  "lateMinutes": 5,
  "shift": {
    "shiftName": "標準班",
    "workStartTime": "09:00",
    "lateToleranceMinutes": 0
  }
}
```

**錯誤碼:**
| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | ALREADY_CHECKED_IN | 今日已完成上班打卡 |
| 400 | LOCATION_OUT_OF_RANGE | 位置不在允許範圍內 |
| 404 | NO_SHIFT_ASSIGNED | 員工未設定班別 |

#### 9.3.2 下班打卡

**端點:** `POST /api/v1/attendance/check-out`

**Service:** `CheckOutServiceImpl`

**Request:**
```json
{
  "employeeId": "550e8400-e29b-41d4-a716-446655440000",
  "checkOutTime": "2025-12-06T18:30:00",
  "location": {
    "latitude": 25.0330,
    "longitude": 121.5654
  },
  "ipAddress": "192.168.1.100"
}
```

**Response 200:**
```json
{
  "recordId": "550e8400-e29b-41d4-a716-446655440001",
  "checkInTime": "2025-12-06T09:05:00",
  "checkOutTime": "2025-12-06T18:30:00",
  "workingHours": 8.42,
  "isEarlyLeave": false
}
```

### 9.4 請假管理API

#### 9.4.1 查詢假期餘額

**端點:** `GET /api/v1/leave/balances?employeeId={id}`

**Controller:** `HR03LeaveQryController`

**Service:** `GetLeaveBalancesServiceImpl`

**Response 200:**
```json
{
  "employeeId": "emp-001",
  "balances": [
    {
      "leaveTypeId": "lt-annual",
      "leaveTypeName": "特休假",
      "year": 2025,
      "totalDays": 10,
      "usedDays": 3.5,
      "remainingDays": 6.5,
      "expiryDate": "2026-12-31"
    },
    {
      "leaveTypeId": "lt-sick",
      "leaveTypeName": "病假",
      "year": 2025,
      "totalDays": 30,
      "usedDays": 2,
      "remainingDays": 28,
      "expiryDate": null
    }
  ]
}
```

#### 9.4.2 提交請假申請

**端點:** `POST /api/v1/leave/applications`

**Controller:** `HR03LeaveCmdController`

**Service:** `CreateLeaveApplicationServiceImpl`

**Request:**
```json
{
  "employeeId": "emp-001",
  "leaveTypeId": "lt-annual",
  "startDate": "2025-12-09",
  "endDate": "2025-12-10",
  "startPeriod": "FULL_DAY",
  "endPeriod": "FULL_DAY",
  "reason": "家庭事務",
  "proofAttachmentUrl": null
}
```

**Response 201:**
```json
{
  "applicationId": "app-001",
  "totalDays": 2,
  "remainingBalance": 4.5,
  "status": "PENDING",
  "workflowInstanceId": "wf-001"
}
```

**錯誤碼:**
| HTTP狀態碼 | 錯誤碼 | 說明 |
|:---:|:---|:---|
| 400 | INSUFFICIENT_BALANCE | 假期餘額不足 |
| 400 | DATE_OVERLAP | 日期與其他申請重疊 |
| 400 | PROOF_REQUIRED | 需要上傳證明文件 |

#### 9.4.3 審核請假 (核准)

**端點:** `PUT /api/v1/leave/applications/{id}/approve`

**Controller:** `HR03LeaveCmdController`

**Service:** `ApproveLeaveServiceImpl`

**權限:** `attendance:leave:approve`

**Response 200:**
```json
{
  "applicationId": "app-001",
  "status": "APPROVED",
  "approvedBy": "李經理",
  "approvedAt": "2025-12-06T10:30:00Z"
}
```

### 9.5 加班管理API

#### 9.5.1 提交加班申請

**端點:** `POST /api/v1/overtime/applications`

**Controller:** `HR03OvertimeCmdController`

**Service:** `CreateOvertimeApplicationServiceImpl`

**Request:**
```json
{
  "employeeId": "emp-001",
  "overtimeDate": "2025-12-06",
  "startTime": "18:00",
  "endTime": "20:30",
  "overtimeType": "WEEKDAY",
  "reason": "專案趕工",
  "compensationType": "PAY"
}
```

**Response 201:**
```json
{
  "overtimeId": "ot-001",
  "overtimeHours": 2.5,
  "status": "PENDING",
  "monthlyStatistics": {
    "accumulatedHours": 15.5,
    "monthlyLimit": 46,
    "quarterlyAccumulatedHours": 45.5,
    "quarterlyLimit": 138
  }
}
```

#### 9.5.2 查詢加班統計

**端點:** `GET /api/v1/overtime/statistics?employeeId={id}&month={month}`

**Controller:** `HR03OvertimeQryController`

**Service:** `GetOvertimeStatisticsServiceImpl`

**Response 200:**
```json
{
  "employeeId": "emp-001",
  "month": "2025-12",
  "totalHours": 15.5,
  "byType": {
    "WEEKDAY": 12.0,
    "REST_DAY": 3.5,
    "HOLIDAY": 0
  },
  "monthlyLimit": 46,
  "quarterlyAccumulatedHours": 45.5,
  "quarterlyLimit": 138,
  "warnings": []
}
```

### 9.6 月度結算API

#### 9.6.1 執行月度結算

**端點:** `POST /api/v1/attendance/monthly-close`

**Controller:** `HR03MonthCloseCmdController`

**Service:** `MonthlyCloseServiceImpl`

**權限:** `attendance:close`

**Request:**
```json
{
  "month": "2025-11",
  "organizationId": "org-001"
}
```

**Response 200:**
```json
{
  "month": "2025-11",
  "processedEmployees": 150,
  "closedAt": "2025-12-01T00:00:00Z"
}
```

**後續事件:**
- ✅ 為每位員工發布 `AttendanceMonthClosed` 事件
- ✅ Payroll Service訂閱事件進行薪資計算

---

## 10. 工項清單摘要

### 前端開發工項
1. HR03-P01 打卡頁面 (GPS定位、即時時間)
2. HR03-P02 差勤記錄頁面 (行事曆、統計)
3. HR03-P03 請假申請頁面 (餘額顯示、表單)
4. HR03-P04 假期餘額頁面
5. HR03-P05 加班申請頁面 (時數預警)
6. HR03-P06 差勤審核頁面 (主管)
7. HR03-P07 班別管理頁面
8. HR03-P08 假別管理頁面
9. HR03-P09 差勤報表頁面
10. HR03-P10 月度結算頁面

### 後端開發工項
1. 班別聚合根與Repository
2. 打卡記錄聚合根與Repository
3. 請假申請聚合根與Repository
4. 加班申請聚合根與Repository
5. 假期餘額聚合根與Repository
6. 特休假政策聚合根
7. 打卡管理API (6端點)
8. 請假管理API (8端點)
9. 加班管理API (6端點)
10. 班別/假別管理API (8端點)
11. 報表結算API (3端點)
12. Workflow整合 (請假/加班審核)
13. 排程任務 (特休到期提醒、月結算Job)

### 資料庫開發工項
1. 建立8個資料表DDL
2. 建立索引
3. 初始化法定假別與預設班別
4. 初始化特休假規則 (依勞基法)

---

**文件完成日期:** 2025-12-06  
**版本:** 1.0
