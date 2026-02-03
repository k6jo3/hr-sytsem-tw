package com.company.hrms.payroll.domain.model.aggregate;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayrollRunStatus;
import com.company.hrms.payroll.domain.model.valueobject.PayrollStatistics;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

import lombok.Builder;
import lombok.Getter;

/**
 * 薪資批次聚合根
 * 管理薪資計算批次生命週期
 * 
 * <p>
 * 狀態流轉：
 * </p>
 * 
 * <pre>
 * DRAFT → CALCULATING → COMPLETED → SUBMITTED → APPROVED → PAID
 *                ↓            ↓           ↓
 *             FAILED      CANCELLED   CANCELLED
 * </pre>
 */
@Getter
@Builder
public class PayrollRun {

    /**
     * 批次 ID
     */
    private final RunId id;

    /**
     * 批次名稱
     */
    private final String name;

    /**
     * 組織 ID
     */
    private final String organizationId;

    /**
     * 薪資制度
     */
    private final PayrollSystem payrollSystem;

    /**
     * 計薪期間
     */
    private final PayPeriod payPeriod;

    /**
     * 發薪日
     */
    private final LocalDate payDate;

    /**
     * 批次狀態
     */
    private PayrollRunStatus status;

    /**
     * 統計數據
     */
    private PayrollStatistics statistics;

    /**
     * 執行者 ID
     */
    private String executedBy;

    /**
     * 執行時間
     */
    private LocalDateTime executedAt;

    /**
     * 完成時間
     */
    private LocalDateTime completedAt;

    /**
     * 送審者 ID
     */
    private String submittedBy;

    /**
     * 送審時間
     */
    private LocalDateTime submittedAt;

    /**
     * 核准者 ID
     */
    private String approvedBy;

    /**
     * 核准時間
     */
    private LocalDateTime approvedAt;

    /**
     * 發放時間
     */
    private LocalDateTime paidAt;

    /**
     * 銀行薪轉檔 URL
     */
    private String bankFileUrl;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    /**
     * 建立者 ID
     */
    private final String createdBy;

    // ==================== 工廠方法 ====================

    /**
     * 建立薪資計算批次
     * 
     * @param id             批次 ID
     * @param name           批次名稱
     * @param organizationId 組織 ID
     * @param payPeriod      計薪期間
     * @param payrollSystem  薪資制度
     * @param payDate        發薪日
     * @param createdBy      建立者 ID
     * @return 薪資批次
     */
    public static PayrollRun create(RunId id, String name, String organizationId, PayPeriod payPeriod,
            PayrollSystem payrollSystem,
            LocalDate payDate, String createdBy) {
        validateCreate(organizationId, payPeriod, payDate);

        return PayrollRun.builder()
                .id(id)
                .name(name)
                .organizationId(organizationId)
                .payPeriod(payPeriod)
                .payrollSystem(payrollSystem)
                .payDate(payDate)
                .status(PayrollRunStatus.DRAFT)
                .statistics(PayrollStatistics.empty())
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 開始執行計算
     * 
     * @param executorId     執行者 ID
     * @param totalEmployees 需計算的員工總數
     */
    public void startExecution(String executorId, int totalEmployees) {
        status.validateTransition(PayrollRunStatus.CALCULATING);

        if (totalEmployees < 0) {
            throw new DomainException("PAY_RUN_INVALID_EMPLOYEE_COUNT", "員工總數不可為負數");
        }

        this.status = PayrollRunStatus.CALCULATING;
        this.executedBy = executorId;
        this.executedAt = LocalDateTime.now();
        this.statistics = PayrollStatistics.initial(totalEmployees);
    }

    /**
     * 更新計算進度
     * 
     * @param increment 本次增加的處理數據
     */
    public void updateProgress(PayrollStatistics increment) {
        if (status != PayrollRunStatus.CALCULATING) {
            throw new DomainException("PAY_RUN_NOT_CALCULATING", "批次非計算中狀態");
        }
        this.statistics = this.statistics.merge(increment);
    }

    /**
     * 完成計算
     * 
     * @param finalStatistics 最終統計
     */
    public void complete(PayrollStatistics finalStatistics) {
        status.validateTransition(PayrollRunStatus.COMPLETED);

        this.status = PayrollRunStatus.COMPLETED;
        this.statistics = finalStatistics;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 標記計算失敗
     * 
     * @param reason 失敗原因
     */
    public void fail(String reason) {
        status.validateTransition(PayrollRunStatus.FAILED);

        this.status = PayrollRunStatus.FAILED;
        this.cancelReason = reason;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 重置為草稿狀態 (失敗後重試)
     */
    public void reset() {
        if (status != PayrollRunStatus.FAILED) {
            throw new DomainException("PAY_RUN_CANNOT_RESET", "只有失敗的批次可以重置");
        }
        this.status = PayrollRunStatus.DRAFT;
        this.statistics = PayrollStatistics.empty();
        this.executedBy = null;
        this.executedAt = null;
        this.completedAt = null;
        this.cancelReason = null;
    }

    /**
     * 送審
     * 
     * @param submitterId 送審者 ID
     */
    public void submit(String submitterId) {
        status.validateTransition(PayrollRunStatus.SUBMITTED);

        this.status = PayrollRunStatus.SUBMITTED;
        this.submittedBy = submitterId;
        this.submittedAt = LocalDateTime.now();
    }

    /**
     * 退回 (重新計算)
     * 
     * @param reason 退回原因
     */
    public void reject(String reason) {
        if (status != PayrollRunStatus.SUBMITTED) {
            throw new DomainException("PAY_RUN_NOT_SUBMITTED", "批次未在送審狀態");
        }
        this.status = PayrollRunStatus.COMPLETED;
        this.cancelReason = reason;
        this.submittedBy = null;
        this.submittedAt = null;
    }

    /**
     * 核准
     * 
     * @param approverId 核准者 ID
     */
    public void approve(String approverId) {
        status.validateTransition(PayrollRunStatus.APPROVED);

        this.status = PayrollRunStatus.APPROVED;
        this.approvedBy = approverId;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * 標記已發放
     * 
     * @param bankFileUrl 銀行薪轉檔 URL
     */
    public void markAsPaid(String bankFileUrl) {
        status.validateTransition(PayrollRunStatus.PAID);

        this.status = PayrollRunStatus.PAID;
        this.bankFileUrl = bankFileUrl;
        this.paidAt = LocalDateTime.now();
    }

    /**
     * 取消批次
     * 
     * @param reason 取消原因
     */
    public void cancel(String reason) {
        status.validateTransition(PayrollRunStatus.CANCELLED);

        this.status = PayrollRunStatus.CANCELLED;
        this.cancelReason = reason;
    }

    // ==================== 查詢方法 ====================

    /**
     * 檢查是否可以執行計算
     * 
     * @return 是否可執行
     */
    public boolean canExecute() {
        return status.canExecute();
    }

    /**
     * 檢查是否可以送審
     * 
     * @return 是否可送審
     */
    public boolean canSubmit() {
        return status.canSubmit();
    }

    /**
     * 檢查是否可以核准
     * 
     * @return 是否可核准
     */
    public boolean canApprove() {
        return status.canApprove();
    }

    /**
     * 檢查是否為終態
     * 
     * @return 是否為終態
     */
    public boolean isFinal() {
        return status.isFinal();
    }

    /**
     * 取得批次標題
     * 
     * @return 例如: "2025年12月薪資計算"
     */
    public String getTitle() {
        return String.format("%d年%d月薪資計算",
                payPeriod.getYear(), payPeriod.getMonth());
    }

    // ==================== 私有驗證方法 ====================

    private static void validateCreate(String organizationId, PayPeriod payPeriod, LocalDate payDate) {
        if (organizationId == null || organizationId.isBlank()) {
            throw new IllegalArgumentException("Organization ID cannot be null or blank");
        }
        if (payPeriod == null) {
            throw new IllegalArgumentException("PayPeriod cannot be null");
        }
        if (payDate == null) {
            throw new IllegalArgumentException("PayDate cannot be null");
        }
        if (payDate.isBefore(payPeriod.getEndDate())) {
            throw new DomainException("PAY_INVALID_PAY_DATE",
                    "發薪日必須在計薪期間結束後");
        }
    }

    /**
     * 重建 Aggregate (Persistence 用)
     */
    public static PayrollRun reconstruct(RunId id,
            String name,
            String organizationId,
            PayPeriod payPeriod,
            PayrollSystem payrollSystem,
            LocalDate payDate,
            PayrollRunStatus status,
            PayrollStatistics statistics,
            String executedBy,
            LocalDateTime executedAt,
            LocalDateTime completedAt,
            String submittedBy,
            LocalDateTime submittedAt,
            String approvedBy,
            LocalDateTime approvedAt,
            LocalDateTime paidAt,
            String bankFileUrl,
            String cancelReason,
            String createdBy,
            LocalDateTime createdAt) {
        return PayrollRun.builder()
                .id(id)
                .name(name)
                .organizationId(organizationId)
                .payPeriod(payPeriod)
                .payrollSystem(payrollSystem)
                .payDate(payDate)
                .status(status)
                .statistics(statistics)
                .executedBy(executedBy)
                .executedAt(executedAt)
                .completedAt(completedAt)
                .submittedBy(submittedBy)
                .submittedAt(submittedAt)
                .approvedBy(approvedBy)
                .approvedAt(approvedAt)
                .paidAt(paidAt)
                .bankFileUrl(bankFileUrl)
                .cancelReason(cancelReason)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .build();
    }
}
