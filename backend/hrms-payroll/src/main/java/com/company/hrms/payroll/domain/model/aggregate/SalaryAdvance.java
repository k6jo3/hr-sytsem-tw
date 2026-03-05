package com.company.hrms.payroll.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceStatus;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

/**
 * 薪資預借聚合根
 *
 * 預借上限公式：可預借金額 <= (應發薪資 - 法定扣除 - 法扣款) × 90%
 * 分期扣回：每月薪資自動扣除 installmentAmount，直到 remainingBalance = 0
 */
@Getter
public class SalaryAdvance extends AggregateRoot<AdvanceId> {

    private String employeeId;
    private BigDecimal requestedAmount;   // 申請金額
    private BigDecimal approvedAmount;    // 核准金額（可能低於申請金額）
    private int installmentMonths;        // 分期月數
    private BigDecimal installmentAmount; // 每期扣回金額
    private BigDecimal repaidAmount;      // 已扣回金額
    private BigDecimal remainingBalance;  // 剩餘未扣回
    private LocalDate applicationDate;
    private LocalDate disbursementDate;   // 撥款日
    private AdvanceStatus status;
    private String reason;
    private String rejectionReason;
    private String approverId;

    /**
     * 新建預借申請
     */
    public SalaryAdvance(AdvanceId id, String employeeId, BigDecimal requestedAmount,
            int installmentMonths, String reason) {
        super(id);
        validate(employeeId, requestedAmount, installmentMonths);
        this.employeeId = employeeId;
        this.requestedAmount = requestedAmount;
        this.installmentMonths = installmentMonths;
        this.reason = reason;
        this.applicationDate = LocalDate.now();
        this.status = AdvanceStatus.PENDING;
        this.repaidAmount = BigDecimal.ZERO;
    }

    /**
     * 核准預借（可調整核准金額）
     */
    public void approve(String approverId, BigDecimal approvedAmount) {
        if (this.status != AdvanceStatus.PENDING) {
            throw new IllegalStateException("僅待審核狀態可核准");
        }
        if (approvedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("核准金額必須 > 0");
        }
        if (approvedAmount.compareTo(this.requestedAmount) > 0) {
            throw new IllegalArgumentException("核准金額不可超過申請金額");
        }
        this.approverId = approverId;
        this.approvedAmount = approvedAmount;
        this.remainingBalance = approvedAmount;
        this.installmentAmount = calculateInstallmentAmount(approvedAmount, installmentMonths);
        this.status = AdvanceStatus.APPROVED;
    }

    /**
     * 駁回
     */
    public void reject(String approverId, String reason) {
        if (this.status != AdvanceStatus.PENDING) {
            throw new IllegalStateException("僅待審核狀態可駁回");
        }
        this.approverId = approverId;
        this.rejectionReason = reason;
        this.status = AdvanceStatus.REJECTED;
    }

    /**
     * 撥款
     */
    public void disburse(LocalDate disbursementDate) {
        if (this.status != AdvanceStatus.APPROVED) {
            throw new IllegalStateException("僅已核准狀態可撥款");
        }
        this.disbursementDate = disbursementDate;
        this.status = AdvanceStatus.DISBURSED;
    }

    /**
     * 開始扣回（撥款後首次薪資扣回時觸發）
     */
    public void startRepayment() {
        if (this.status != AdvanceStatus.DISBURSED) {
            throw new IllegalStateException("僅已撥款狀態可開始扣回");
        }
        this.status = AdvanceStatus.REPAYING;
    }

    /**
     * 每期扣回（薪資計算時呼叫）
     * @return 本期實際扣回金額
     */
    public BigDecimal repay(BigDecimal amount) {
        if (this.status != AdvanceStatus.REPAYING && this.status != AdvanceStatus.DISBURSED) {
            throw new IllegalStateException("不可在此狀態進行扣回");
        }
        if (this.status == AdvanceStatus.DISBURSED) {
            this.status = AdvanceStatus.REPAYING;
        }

        // 實際扣回不超過剩餘餘額
        BigDecimal actualRepay = amount.min(this.remainingBalance);
        this.repaidAmount = this.repaidAmount.add(actualRepay);
        this.remainingBalance = this.remainingBalance.subtract(actualRepay);

        if (this.remainingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            this.remainingBalance = BigDecimal.ZERO;
            this.status = AdvanceStatus.FULLY_REPAID;
        }

        return actualRepay;
    }

    /**
     * 取消（未撥款前可取消）
     */
    public void cancel() {
        if (this.status == AdvanceStatus.DISBURSED || this.status == AdvanceStatus.REPAYING
                || this.status == AdvanceStatus.FULLY_REPAID) {
            throw new IllegalStateException("已撥款/扣回中的預借不可取消");
        }
        this.status = AdvanceStatus.CANCELLED;
    }

    /**
     * 檢查預借金額是否在上限內
     * 可預借金額 <= (應發薪資 - 法定扣除 - 法扣款) × 90%
     */
    public static BigDecimal calculateMaxAdvance(BigDecimal grossSalary, BigDecimal statutoryDeductions,
            BigDecimal garnishments) {
        BigDecimal available = grossSalary.subtract(statutoryDeductions).subtract(garnishments);
        if (available.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return available.multiply(new BigDecimal("0.9"));
    }

    private BigDecimal calculateInstallmentAmount(BigDecimal total, int months) {
        if (months <= 0) {
            return total;
        }
        // 向上取整（最後一期少扣）
        return total.divide(BigDecimal.valueOf(months), 0, java.math.RoundingMode.CEILING);
    }

    private void validate(String employeeId, BigDecimal requestedAmount, int installmentMonths) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("員工 ID 不可為空");
        }
        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("申請金額必須 > 0");
        }
        if (installmentMonths < 1) {
            throw new IllegalArgumentException("分期月數必須 >= 1");
        }
    }

    /**
     * 從持久層重建
     */
    private SalaryAdvance(AdvanceId id, String employeeId, BigDecimal requestedAmount,
            BigDecimal approvedAmount, int installmentMonths, BigDecimal installmentAmount,
            BigDecimal repaidAmount, BigDecimal remainingBalance,
            LocalDate applicationDate, LocalDate disbursementDate,
            AdvanceStatus status, String reason, String rejectionReason, String approverId) {
        super(id);
        this.employeeId = employeeId;
        this.requestedAmount = requestedAmount;
        this.approvedAmount = approvedAmount;
        this.installmentMonths = installmentMonths;
        this.installmentAmount = installmentAmount;
        this.repaidAmount = repaidAmount;
        this.remainingBalance = remainingBalance;
        this.applicationDate = applicationDate;
        this.disbursementDate = disbursementDate;
        this.status = status;
        this.reason = reason;
        this.rejectionReason = rejectionReason;
        this.approverId = approverId;
    }

    public static SalaryAdvance reconstitute(AdvanceId id, String employeeId, BigDecimal requestedAmount,
            BigDecimal approvedAmount, int installmentMonths, BigDecimal installmentAmount,
            BigDecimal repaidAmount, BigDecimal remainingBalance,
            LocalDate applicationDate, LocalDate disbursementDate,
            AdvanceStatus status, String reason, String rejectionReason, String approverId) {
        return new SalaryAdvance(id, employeeId, requestedAmount, approvedAmount,
                installmentMonths, installmentAmount, repaidAmount, remainingBalance,
                applicationDate, disbursementDate, status, reason, rejectionReason, approverId);
    }
}
