package com.company.hrms.payroll.domain.model.aggregate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentStatus;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

/**
 * 法扣款聚合根
 *
 * 法律依據（強制執行法）：
 * - §115-1：扣薪上限不得超過薪資的 1/3
 * - §122：需保留債務人居住地最低生活費 × 1.2 倍 + 扶養費用
 * - 可扣金額 = min(薪資淨額 × 1/3, 薪資淨額 - 最低生活費×1.2 - 扶養費)
 *
 * 扣款優先順序：法扣 > 勞健保 > 所得稅 > 預借扣回 > 其他
 */
@Getter
public class LegalDeduction extends AggregateRoot<DeductionId> {

    private String employeeId;
    private String courtOrderNumber;    // 法院扣押令編號
    private GarnishmentType garnishmentType;
    private BigDecimal totalAmount;     // 扣押總額
    private BigDecimal deductedAmount;  // 已扣款金額
    private BigDecimal remainingAmount; // 剩餘應扣
    private int priority;              // 優先順序（多筆扣押令時）
    private LocalDate effectiveDate;   // 生效日
    private LocalDate expiryDate;      // 到期日（可為 null，直到扣完）
    private GarnishmentStatus status;
    private String issuingAuthority;   // 執行機關（法院/國稅局/健保署）
    private String caseNumber;         // 案號
    private String note;

    public LegalDeduction(DeductionId id, String employeeId, String courtOrderNumber,
            GarnishmentType garnishmentType, BigDecimal totalAmount,
            int priority, LocalDate effectiveDate, String issuingAuthority) {
        super(id);
        validate(employeeId, courtOrderNumber, totalAmount, effectiveDate);
        this.employeeId = employeeId;
        this.courtOrderNumber = courtOrderNumber;
        this.garnishmentType = garnishmentType;
        this.totalAmount = totalAmount;
        this.deductedAmount = BigDecimal.ZERO;
        this.remainingAmount = totalAmount;
        this.priority = priority;
        this.effectiveDate = effectiveDate;
        this.issuingAuthority = issuingAuthority;
        this.status = GarnishmentStatus.ACTIVE;
    }

    /**
     * 執行扣款（薪資計算時呼叫）
     * @param maxDeductible 本期最大可扣金額（已依法律上限計算）
     * @return 本期實際扣款金額
     */
    public BigDecimal deduct(BigDecimal maxDeductible) {
        if (this.status != GarnishmentStatus.ACTIVE) {
            return BigDecimal.ZERO;
        }

        BigDecimal actualDeduct = maxDeductible.min(this.remainingAmount);
        this.deductedAmount = this.deductedAmount.add(actualDeduct);
        this.remainingAmount = this.remainingAmount.subtract(actualDeduct);

        if (this.remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            this.remainingAmount = BigDecimal.ZERO;
            this.status = GarnishmentStatus.COMPLETED;
        }

        return actualDeduct;
    }

    /**
     * 暫停扣款（法院暫緩執行）
     */
    public void suspend() {
        if (this.status != GarnishmentStatus.ACTIVE) {
            throw new IllegalStateException("僅執行中的法扣可暫停");
        }
        this.status = GarnishmentStatus.SUSPENDED;
    }

    /**
     * 恢復扣款
     */
    public void resume() {
        if (this.status != GarnishmentStatus.SUSPENDED) {
            throw new IllegalStateException("僅暫停中的法扣可恢復");
        }
        this.status = GarnishmentStatus.ACTIVE;
    }

    /**
     * 終止（法院撤銷扣押令）
     */
    public void terminate() {
        this.status = GarnishmentStatus.TERMINATED;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /**
     * 計算法定可扣上限
     *
     * 可扣金額 = min(薪資淨額 × 1/3, 薪資淨額 - 最低生活費×1.2 - 扶養費)
     *
     * @param netSalary 薪資淨額（應發 - 勞保 - 健保 - 所得稅）
     * @param minimumLivingCost 居住地最低生活費
     * @param dependentCost 扶養費用
     * @return 法定可扣上限
     */
    public static BigDecimal calculateMaxGarnishment(BigDecimal netSalary,
            BigDecimal minimumLivingCost, BigDecimal dependentCost) {
        if (netSalary.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 規則一：不超過淨額 1/3
        BigDecimal oneThird = netSalary.divide(BigDecimal.valueOf(3), 0, RoundingMode.FLOOR);

        // 規則二：需保留最低生活費 × 1.2 + 扶養費
        BigDecimal protectedAmount = minimumLivingCost
                .multiply(new BigDecimal("1.2"))
                .add(dependentCost != null ? dependentCost : BigDecimal.ZERO);
        BigDecimal afterProtection = netSalary.subtract(protectedAmount);

        if (afterProtection.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return oneThird.min(afterProtection);
    }

    private void validate(String employeeId, String courtOrderNumber,
            BigDecimal totalAmount, LocalDate effectiveDate) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("員工 ID 不可為空");
        }
        if (courtOrderNumber == null || courtOrderNumber.isBlank()) {
            throw new IllegalArgumentException("扣押令編號不可為空");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣押總額必須 > 0");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("生效日不可為空");
        }
    }

    /**
     * 從持久層重建
     */
    private LegalDeduction(DeductionId id, String employeeId, String courtOrderNumber,
            GarnishmentType garnishmentType, BigDecimal totalAmount,
            BigDecimal deductedAmount, BigDecimal remainingAmount,
            int priority, LocalDate effectiveDate, LocalDate expiryDate,
            GarnishmentStatus status, String issuingAuthority, String caseNumber, String note) {
        super(id);
        this.employeeId = employeeId;
        this.courtOrderNumber = courtOrderNumber;
        this.garnishmentType = garnishmentType;
        this.totalAmount = totalAmount;
        this.deductedAmount = deductedAmount;
        this.remainingAmount = remainingAmount;
        this.priority = priority;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.issuingAuthority = issuingAuthority;
        this.caseNumber = caseNumber;
        this.note = note;
    }

    public static LegalDeduction reconstitute(DeductionId id, String employeeId, String courtOrderNumber,
            GarnishmentType garnishmentType, BigDecimal totalAmount,
            BigDecimal deductedAmount, BigDecimal remainingAmount,
            int priority, LocalDate effectiveDate, LocalDate expiryDate,
            GarnishmentStatus status, String issuingAuthority, String caseNumber, String note) {
        return new LegalDeduction(id, employeeId, courtOrderNumber, garnishmentType,
                totalAmount, deductedAmount, remainingAmount, priority,
                effectiveDate, expiryDate, status, issuingAuthority, caseNumber, note);
    }
}
