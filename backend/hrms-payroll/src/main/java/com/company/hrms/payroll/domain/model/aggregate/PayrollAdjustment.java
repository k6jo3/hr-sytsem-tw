package com.company.hrms.payroll.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.company.hrms.payroll.domain.model.valueobject.AdjustmentId;
import com.company.hrms.payroll.domain.model.valueobject.AdjustmentStatus;
import com.company.hrms.payroll.domain.model.valueobject.AdjustmentType;

import lombok.Getter;

/**
 * 薪資調整單聚合根
 *
 * <p>支援三種調整類型：
 * <ul>
 *   <li>SUPPLEMENTARY：追溯補發（正數金額）</li>
 *   <li>DEDUCTION：追溯扣回（正數金額，實際扣除）</li>
 *   <li>REVERSAL：沖正（原薪資單作廢，產生負數沖正單）</li>
 * </ul>
 */
@Getter
public class PayrollAdjustment {

    private final AdjustmentId id;
    private final String employeeId;
    private final String originalPayslipId;
    private final AdjustmentType adjustmentType;
    private final BigDecimal amount;
    private final String reason;
    private AdjustmentStatus status;

    /** 調整生效日期（決定併入哪期薪資） */
    private final LocalDate effectiveDate;

    /** 核准人 */
    private String approvedBy;
    private LocalDateTime approvedAt;

    /** 審計軌跡 */
    private final List<AuditEntry> auditTrail;

    private final String createdBy;
    private final LocalDateTime createdAt;

    private PayrollAdjustment(AdjustmentId id, String employeeId, String originalPayslipId,
            AdjustmentType adjustmentType, BigDecimal amount, String reason,
            LocalDate effectiveDate, String createdBy) {
        this.id = id;
        this.employeeId = employeeId;
        this.originalPayslipId = originalPayslipId;
        this.adjustmentType = adjustmentType;
        this.amount = amount;
        this.reason = reason;
        this.effectiveDate = effectiveDate;
        this.status = AdjustmentStatus.PENDING;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.auditTrail = new ArrayList<>();

        // 初始審計記錄
        addAudit("CREATE", createdBy, "建立薪資調整單: " + adjustmentType.name());
    }

    /**
     * 建立補發調整單
     */
    public static PayrollAdjustment createSupplementary(
            String employeeId, String originalPayslipId,
            BigDecimal amount, String reason, LocalDate effectiveDate, String createdBy) {

        validateAmount(amount);
        return new PayrollAdjustment(
                AdjustmentId.generate(), employeeId, originalPayslipId,
                AdjustmentType.SUPPLEMENTARY, amount, reason, effectiveDate, createdBy);
    }

    /**
     * 建立扣回調整單
     */
    public static PayrollAdjustment createDeduction(
            String employeeId, String originalPayslipId,
            BigDecimal amount, String reason, LocalDate effectiveDate, String createdBy) {

        validateAmount(amount);
        return new PayrollAdjustment(
                AdjustmentId.generate(), employeeId, originalPayslipId,
                AdjustmentType.DEDUCTION, amount, reason, effectiveDate, createdBy);
    }

    /**
     * 建立沖正調整單（作廢原薪資單）
     */
    public static PayrollAdjustment createReversal(
            String employeeId, String originalPayslipId,
            BigDecimal originalAmount, String reason, String createdBy) {

        return new PayrollAdjustment(
                AdjustmentId.generate(), employeeId, originalPayslipId,
                AdjustmentType.REVERSAL, originalAmount, reason, LocalDate.now(), createdBy);
    }

    /**
     * 核准
     */
    public void approve(String approver) {
        if (this.status != AdjustmentStatus.PENDING) {
            throw new IllegalStateException("僅待審核狀態可核准");
        }
        this.status = AdjustmentStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        addAudit("APPROVE", approver, "核准薪資調整單");
    }

    /**
     * 駁回
     */
    public void reject(String rejector, String rejectionReason) {
        if (this.status != AdjustmentStatus.PENDING) {
            throw new IllegalStateException("僅待審核狀態可駁回");
        }
        this.status = AdjustmentStatus.REJECTED;
        addAudit("REJECT", rejector, "駁回: " + rejectionReason);
    }

    /**
     * 標記為已執行
     */
    public void markExecuted(String executor) {
        if (this.status != AdjustmentStatus.APPROVED) {
            throw new IllegalStateException("僅已核准狀態可執行");
        }
        this.status = AdjustmentStatus.EXECUTED;
        addAudit("EXECUTE", executor, "薪資調整已執行");
    }

    /**
     * 取消
     */
    public void cancel(String canceller, String cancelReason) {
        if (this.status == AdjustmentStatus.EXECUTED) {
            throw new IllegalStateException("已執行的調整單不可取消");
        }
        this.status = AdjustmentStatus.CANCELLED;
        addAudit("CANCEL", canceller, "取消: " + cancelReason);
    }

    /**
     * 取得實際調整金額（補發為正、扣回/沖正為負）
     */
    public BigDecimal getEffectiveAmount() {
        return switch (adjustmentType) {
            case SUPPLEMENTARY -> amount;
            case DEDUCTION, REVERSAL -> amount.negate();
        };
    }

    public List<AuditEntry> getAuditTrail() {
        return Collections.unmodifiableList(auditTrail);
    }

    private void addAudit(String action, String operator, String description) {
        this.auditTrail.add(new AuditEntry(action, operator, description, LocalDateTime.now()));
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("調整金額必須為正數");
        }
    }

    /**
     * 審計軌跡項目
     */
    @Getter
    public static class AuditEntry {
        private final String action;
        private final String operator;
        private final String description;
        private final LocalDateTime timestamp;

        public AuditEntry(String action, String operator, String description, LocalDateTime timestamp) {
            this.action = action;
            this.operator = operator;
            this.description = description;
            this.timestamp = timestamp;
        }
    }

    /**
     * Repository 重建用
     */
    public static PayrollAdjustment reconstitute(
            AdjustmentId id, String employeeId, String originalPayslipId,
            AdjustmentType adjustmentType, BigDecimal amount, String reason,
            LocalDate effectiveDate, AdjustmentStatus status,
            String approvedBy, LocalDateTime approvedAt,
            String createdBy, LocalDateTime createdAt,
            List<AuditEntry> auditTrail) {

        PayrollAdjustment adj = new PayrollAdjustment(
                id, employeeId, originalPayslipId,
                adjustmentType, amount, reason, effectiveDate, createdBy);
        adj.status = status;
        adj.approvedBy = approvedBy;
        adj.approvedAt = approvedAt;
        adj.auditTrail.clear();
        if (auditTrail != null) {
            adj.auditTrail.addAll(auditTrail);
        }
        return adj;
    }
}
