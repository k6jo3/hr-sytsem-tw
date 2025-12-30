package com.company.hrms.payroll.domain.model.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 薪資批次統計值物件
 * 封裝薪資計算批次的統計數據
 */
@Getter
@EqualsAndHashCode
@Builder
public class PayrollStatistics {

    /**
     * 總員工數
     */
    @Builder.Default
    private final int totalEmployees = 0;

    /**
     * 已處理員工數
     */
    @Builder.Default
    private final int processedEmployees = 0;

    /**
     * 處理失敗員工數
     */
    @Builder.Default
    private final int failedEmployees = 0;

    /**
     * 應發薪資總額
     */
    @Builder.Default
    private final BigDecimal totalGrossAmount = BigDecimal.ZERO;

    /**
     * 實發薪資總額
     */
    @Builder.Default
    private final BigDecimal totalNetAmount = BigDecimal.ZERO;

    /**
     * 扣除項總額
     */
    @Builder.Default
    private final BigDecimal totalDeductions = BigDecimal.ZERO;

    /**
     * 加班費總額
     */
    @Builder.Default
    private final BigDecimal totalOvertimePay = BigDecimal.ZERO;

    /**
     * 建立空的統計
     * 
     * @return 空的統計
     */
    public static PayrollStatistics empty() {
        return PayrollStatistics.builder().build();
    }

    /**
     * 建立初始統計 (只有員工總數)
     * 
     * @param totalEmployees 員工總數
     * @return 初始統計
     */
    public static PayrollStatistics initial(int totalEmployees) {
        return PayrollStatistics.builder()
                .totalEmployees(totalEmployees)
                .build();
    }

    /**
     * 計算平均應發薪資
     * 
     * @return 平均應發薪資
     */
    public BigDecimal getAverageGrossWage() {
        if (processedEmployees == 0) {
            return BigDecimal.ZERO;
        }
        return totalGrossAmount.divide(
                BigDecimal.valueOf(processedEmployees),
                0,
                RoundingMode.HALF_UP);
    }

    /**
     * 計算平均實發薪資
     * 
     * @return 平均實發薪資
     */
    public BigDecimal getAverageNetWage() {
        if (processedEmployees == 0) {
            return BigDecimal.ZERO;
        }
        return totalNetAmount.divide(
                BigDecimal.valueOf(processedEmployees),
                0,
                RoundingMode.HALF_UP);
    }

    /**
     * 計算成功率
     * 
     * @return 成功率 (0-100)
     */
    public BigDecimal getSuccessRate() {
        if (totalEmployees == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(processedEmployees)
                .divide(BigDecimal.valueOf(totalEmployees), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 檢查是否全部處理完成
     * 
     * @return 是否全部處理完成
     */
    public boolean isComplete() {
        return processedEmployees + failedEmployees >= totalEmployees;
    }

    /**
     * 合併另一個統計 (用於批次更新)
     * 
     * @param other 另一個統計
     * @return 合併後的統計
     */
    public PayrollStatistics merge(PayrollStatistics other) {
        return PayrollStatistics.builder()
                .totalEmployees(this.totalEmployees)
                .processedEmployees(this.processedEmployees + other.processedEmployees)
                .failedEmployees(this.failedEmployees + other.failedEmployees)
                .totalGrossAmount(this.totalGrossAmount.add(other.totalGrossAmount))
                .totalNetAmount(this.totalNetAmount.add(other.totalNetAmount))
                .totalDeductions(this.totalDeductions.add(other.totalDeductions))
                .totalOvertimePay(this.totalOvertimePay.add(other.totalOvertimePay))
                .build();
    }
}
