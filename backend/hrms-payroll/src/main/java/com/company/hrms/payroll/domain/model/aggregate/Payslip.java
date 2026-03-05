package com.company.hrms.payroll.domain.model.aggregate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.entity.PayslipItem;
import com.company.hrms.payroll.domain.model.valueobject.BankAccount;
import com.company.hrms.payroll.domain.model.valueobject.InsuranceDeductions;
import com.company.hrms.payroll.domain.model.valueobject.OvertimePayDetail;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayslipId;
import com.company.hrms.payroll.domain.model.valueobject.PayslipStatus;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

import lombok.Builder;
import lombok.Getter;

/**
 * 薪資單聚合根
 * 個人薪資明細與計算結果
 * 
 * <p>
 * 薪資計算公式：
 * </p>
 * 
 * <pre>
 * 應發薪資 = 底薪 + Σ(收入項目) + 加班費 - 請假扣款
 * 應扣項目 = 勞保費 + 健保費 + 勞退自提 + 所得稅 + 二代健保補充保費
 * 實發薪資 = 應發薪資 - 應扣項目
 * </pre>
 */
@Getter
@Builder
public class Payslip {

    /**
     * 薪資單 ID
     */
    private final PayslipId id;

    /**
     * 批次 ID
     */
    private final RunId payrollRunId;

    /**
     * 員工 ID
     */
    private final String employeeId;

    /**
     * 員工編號
     */
    private final String employeeNumber;

    /**
     * 員工姓名
     */
    private final String employeeName;

    /**
     * 計薪期間
     */
    private final PayPeriod payPeriod;

    /**
     * 發薪日
     */
    private final LocalDate payDate;

    /**
     * 底薪
     */
    private BigDecimal baseSalary;

    /**
     * 收入項目列表
     */
    @Builder.Default
    private final List<PayslipItem> earningItems = new ArrayList<>();

    /**
     * 扣除項目列表
     */
    @Builder.Default
    private final List<PayslipItem> deductionItems = new ArrayList<>();

    /**
     * 加班費明細
     */
    private OvertimePayDetail overtimePay;

    /**
     * 請假扣款
     */
    @Builder.Default
    private BigDecimal leaveDeduction = BigDecimal.ZERO;

    /**
     * 保險扣除
     */
    private InsuranceDeductions insuranceDeductions;

    /**
     * 所得稅
     */
    @Builder.Default
    private BigDecimal incomeTax = BigDecimal.ZERO;

    /**
     * 應發薪資
     */
    private BigDecimal grossWage;

    /**
     * 實發薪資
     */
    private BigDecimal netWage;

    /**
     * 銀行帳戶
     */
    private BankAccount bankAccount;

    /**
     * 薪資單狀態
     */
    private PayslipStatus status;

    /**
     * PDF URL
     */
    private String pdfUrl;

    /**
     * Email 發送時間
     */
    private LocalDateTime emailSentAt;

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    // ==================== 工廠方法 ====================

    /**
     * 建立薪資單 (草稿)
     * 
     * @param payrollRunId   批次 ID
     * @param employeeId     員工 ID
     * @param employeeNumber 員工編號
     * @param employeeName   員工姓名
     * @param payPeriod      計薪期間
     * @param payDate        發薪日
     * @return 薪資單
     */
    public static Payslip create(RunId payrollRunId, String employeeId,
            String employeeNumber, String employeeName,
            PayPeriod payPeriod, LocalDate payDate) {
        return Payslip.builder()
                .id(PayslipId.generate())
                .payrollRunId(payrollRunId)
                .employeeId(employeeId)
                .employeeNumber(employeeNumber)
                .employeeName(employeeName)
                .payPeriod(payPeriod)
                .payDate(payDate)
                .baseSalary(BigDecimal.ZERO)
                .overtimePay(OvertimePayDetail.empty())
                .leaveDeduction(BigDecimal.ZERO)
                .insuranceDeductions(InsuranceDeductions.empty())
                .incomeTax(BigDecimal.ZERO)
                .grossWage(BigDecimal.ZERO)
                .netWage(BigDecimal.ZERO)
                .status(PayslipStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 設定底薪
     * 
     * @param baseSalary 底薪
     */
    public void setBaseSalary(BigDecimal baseSalary) {
        validateDraft();
        this.baseSalary = baseSalary != null ? baseSalary : BigDecimal.ZERO;
    }

    /**
     * 新增收入項目
     * 
     * @param item 收入項目
     */
    public void addEarningItem(PayslipItem item) {
        validateDraft();
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (!item.isEarning()) {
            throw new DomainException("PAY_INVALID_ITEM_TYPE", "非收入項目");
        }
        earningItems.add(item);
    }

    /**
     * 新增扣除項目
     * 
     * @param item 扣除項目
     */
    public void addDeductionItem(PayslipItem item) {
        validateDraft();
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (!item.isDeduction()) {
            throw new DomainException("PAY_INVALID_ITEM_TYPE", "非扣除項目");
        }
        deductionItems.add(item);
    }

    /**
     * 設定加班費
     * 
     * @param overtimePay 加班費明細
     */
    public void setOvertimePay(OvertimePayDetail overtimePay) {
        validateDraft();
        this.overtimePay = overtimePay != null ? overtimePay : OvertimePayDetail.empty();
    }

    /**
     * 設定請假扣款
     * 
     * @param leaveDeduction 請假扣款
     */
    public void setLeaveDeduction(BigDecimal leaveDeduction) {
        validateDraft();
        this.leaveDeduction = leaveDeduction != null ? leaveDeduction : BigDecimal.ZERO;
    }

    /**
     * 設定保險扣除
     * 
     * @param insuranceDeductions 保險扣除
     */
    public void setInsuranceDeductions(InsuranceDeductions insuranceDeductions) {
        validateDraft();
        this.insuranceDeductions = insuranceDeductions != null
                ? insuranceDeductions
                : InsuranceDeductions.empty();
    }

    /**
     * 設定所得稅
     * 
     * @param incomeTax 所得稅
     */
    public void setIncomeTax(BigDecimal incomeTax) {
        validateDraft();
        this.incomeTax = incomeTax != null ? incomeTax : BigDecimal.ZERO;
    }

    /**
     * 設定銀行帳戶
     * 
     * @param bankAccount 銀行帳戶
     */
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * 計算薪資 (總計算)
     * 重新計算應發與實發薪資
     */
    public void calculate() {
        validateDraft();

        // 計算應發薪資
        BigDecimal totalEarnings = earningItems.stream()
                .map(PayslipItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.grossWage = baseSalary
                .add(totalEarnings)
                .add(overtimePay.getTotal())
                .subtract(leaveDeduction)
                .setScale(0, RoundingMode.HALF_UP);

        // 計算應扣項目
        BigDecimal totalDeductions = deductionItems.stream()
                .map(PayslipItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal insuranceTotal = insuranceDeductions.getTotal();

        // 計算實發薪資
        this.netWage = grossWage
                .subtract(totalDeductions)
                .subtract(insuranceTotal)
                .subtract(incomeTax)
                .setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 定案薪資單
     * 定案後不可再修改
     */
    public void finalize() {
        if (status != PayslipStatus.DRAFT) {
            throw new DomainException("PAY_PAYSLIP_NOT_DRAFT", "薪資單非草稿狀態");
        }

        // 確保已計算
        if (grossWage == null || grossWage.compareTo(BigDecimal.ZERO) == 0) {
            throw new DomainException("PAY_PAYSLIP_NOT_CALCULATED", "請先執行薪資計算");
        }

        this.status = PayslipStatus.FINALIZED;
    }

    /**
     * 設定 PDF URL
     * 
     * @param pdfUrl PDF URL
     */
    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    /**
     * 標記已發送
     */
    public void markAsSent() {
        if (status != PayslipStatus.FINALIZED) {
            throw new DomainException("PAY_PAYSLIP_NOT_FINALIZED", "薪資單未定案");
        }
        this.status = PayslipStatus.SENT;
        this.emailSentAt = LocalDateTime.now();
    }

    /**
     * 作廢薪資單（產生沖正時呼叫）
     */
    public void voidPayslip() {
        if (this.status != PayslipStatus.FINALIZED && this.status != PayslipStatus.SENT) {
            throw new DomainException("PAY_CANNOT_VOID", "僅已定案或已發送的薪資單可作廢");
        }
        this.status = PayslipStatus.VOIDED;
    }

    // ==================== 查詢方法 ====================

    /**
     * 計算收入項目總額
     * 
     * @return 收入總額
     */
    public BigDecimal getTotalEarnings() {
        return earningItems.stream()
                .map(PayslipItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 計算扣除項目總額
     * 
     * @return 扣除總額
     */
    public BigDecimal getTotalDeductions() {
        BigDecimal itemDeductions = deductionItems.stream()
                .map(PayslipItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return itemDeductions
                .add(insuranceDeductions.getTotal())
                .add(incomeTax);
    }

    /**
     * 檢查是否為草稿狀態
     * 
     * @return 是否為草稿
     */
    public boolean isDraft() {
        return status == PayslipStatus.DRAFT;
    }

    /**
     * 檢查是否已發送
     * 
     * @return 是否已發送
     */
    public boolean isSent() {
        return status == PayslipStatus.SENT;
    }

    // ==================== 私有方法 ====================

    private void validateDraft() {
        if (status != PayslipStatus.DRAFT) {
            throw new DomainException("PAY_PAYSLIP_LOCKED", "已定案的薪資單不可修改");
        }
    }

    /**
     * 重建 Aggregate (Persistence 用)
     */
    public static Payslip reconstruct(PayslipId id,
            RunId payrollRunId,
            String employeeId,
            String employeeNumber,
            String employeeName,
            PayPeriod payPeriod,
            LocalDate payDate,
            BigDecimal baseSalary,
            List<PayslipItem> earningItems,
            List<PayslipItem> deductionItems,
            OvertimePayDetail overtimePay,
            BigDecimal leaveDeduction,
            InsuranceDeductions insuranceDeductions,
            BigDecimal incomeTax,
            BigDecimal grossWage,
            BigDecimal netWage,
            BankAccount bankAccount,
            PayslipStatus status,
            String pdfUrl,
            LocalDateTime emailSentAt,
            LocalDateTime createdAt) {
        return Payslip.builder()
                .id(id)
                .payrollRunId(payrollRunId)
                .employeeId(employeeId)
                .employeeNumber(employeeNumber)
                .employeeName(employeeName)
                .payPeriod(payPeriod)
                .payDate(payDate)
                .baseSalary(baseSalary)
                .earningItems(earningItems)
                .deductionItems(deductionItems)
                .overtimePay(overtimePay)
                .leaveDeduction(leaveDeduction)
                .insuranceDeductions(insuranceDeductions)
                .incomeTax(incomeTax)
                .grossWage(grossWage)
                .netWage(netWage)
                .bankAccount(bankAccount)
                .status(status)
                .pdfUrl(pdfUrl)
                .emailSentAt(emailSentAt)
                .createdAt(createdAt)
                .build();
    }
}
