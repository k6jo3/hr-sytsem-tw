package com.company.hrms.payroll.domain.model.aggregate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;
import com.company.hrms.payroll.domain.model.valueobject.PayrollCycle;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.StructureId;

import lombok.Builder;
import lombok.Getter;

/**
 * 薪資結構聚合根
 * 定義員工薪資組成與計算規則
 * 
 * <p>
 * 核心業務邏輯：
 * </p>
 * <ul>
 * <li>支援月薪制與時薪制</li>
 * <li>管理多個薪資項目 (收入/扣除)</li>
 * <li>計算投保薪資</li>
 * <li>計算加班時薪 (月薪 ÷ 240)</li>
 * </ul>
 */
@Getter
@Builder
public class SalaryStructure {

    /**
     * 薪資結構 ID
     */
    private final StructureId id;

    /**
     * 員工 ID
     */
    private final String employeeId;

    /**
     * 薪資制度 (時薪/月薪)
     */
    private PayrollSystem payrollSystem;

    /**
     * 領薪週期
     */
    private PayrollCycle payrollCycle;

    /**
     * 時薪 (時薪制)
     */
    private BigDecimal hourlyRate;

    /**
     * 月薪 (月薪制)
     */
    private BigDecimal monthlySalary;

    /**
     * 計算出的時薪 (月薪 ÷ 240)
     */
    private BigDecimal calculatedHourlyRate;

    /**
     * 薪資項目列表
     */
    @Builder.Default
    private final List<SalaryItem> items = new ArrayList<>();

    /**
     * 生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 結束日期
     */
    private LocalDate endDate;

    /**
     * 是否生效中
     */
    @Builder.Default
    private boolean active = true;

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    // ==================== 工廠方法 ====================

    /**
     * 建立月薪制薪資結構
     * 
     * @param employeeId    員工 ID
     * @param monthlySalary 月薪
     * @param payrollCycle  領薪週期
     * @param effectiveDate 生效日期
     * @return 薪資結構
     */
    public static SalaryStructure createMonthly(String employeeId, BigDecimal monthlySalary,
            PayrollCycle payrollCycle, LocalDate effectiveDate) {
        validateEmployeeId(employeeId);
        validateMonthlySalary(monthlySalary);

        // 計算時薪 = 月薪 ÷ 240 (勞基法規定)
        BigDecimal calculatedHourlyRate = monthlySalary.divide(
                BigDecimal.valueOf(240), 4, RoundingMode.HALF_UP);

        return SalaryStructure.builder()
                .id(StructureId.generate())
                .employeeId(employeeId)
                .payrollSystem(PayrollSystem.MONTHLY)
                .payrollCycle(payrollCycle != null ? payrollCycle : PayrollCycle.MONTHLY)
                .monthlySalary(monthlySalary)
                .calculatedHourlyRate(calculatedHourlyRate)
                .effectiveDate(effectiveDate)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 建立時薪制薪資結構
     * 
     * @param employeeId    員工 ID
     * @param hourlyRate    時薪
     * @param payrollCycle  領薪週期
     * @param effectiveDate 生效日期
     * @return 薪資結構
     */
    public static SalaryStructure createHourly(String employeeId, BigDecimal hourlyRate,
            PayrollCycle payrollCycle, LocalDate effectiveDate) {
        validateEmployeeId(employeeId);
        validateHourlyRate(hourlyRate);

        return SalaryStructure.builder()
                .id(StructureId.generate())
                .employeeId(employeeId)
                .payrollSystem(PayrollSystem.HOURLY)
                .payrollCycle(payrollCycle != null ? payrollCycle : PayrollCycle.MONTHLY)
                .hourlyRate(hourlyRate)
                .calculatedHourlyRate(hourlyRate)
                .effectiveDate(effectiveDate)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 新增薪資項目
     * 
     * @param item 薪資項目
     */
    public void addSalaryItem(SalaryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("SalaryItem cannot be null");
        }
        // 檢查是否已存在相同代碼的項目
        boolean exists = items.stream()
                .anyMatch(i -> i.getItemCode().equals(item.getItemCode()));
        if (exists) {
            throw new DomainException("PAY_ITEM_DUPLICATE",
                    "薪資項目代碼已存在: " + item.getItemCode());
        }
        items.add(item);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 移除薪資項目
     * 
     * @param itemId 項目 ID
     */
    public void removeSalaryItem(String itemId) {
        boolean removed = items.removeIf(i -> i.getItemId().equals(itemId));
        if (!removed) {
            throw new DomainException("PAY_ITEM_NOT_FOUND",
                    "找不到薪資項目: " + itemId);
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 調整月薪
     * 
     * @param newSalary 新月薪
     */
    public void adjustMonthlySalary(BigDecimal newSalary) {
        if (payrollSystem != PayrollSystem.MONTHLY) {
            throw new DomainException("PAY_NOT_MONTHLY", "非月薪制無法調整月薪");
        }
        validateMonthlySalary(newSalary);
        this.monthlySalary = newSalary;
        this.calculatedHourlyRate = newSalary.divide(
                BigDecimal.valueOf(240), 4, RoundingMode.HALF_UP);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 調整時薪
     * 
     * @param newRate 新時薪
     */
    public void adjustHourlyRate(BigDecimal newRate) {
        if (payrollSystem != PayrollSystem.HOURLY) {
            throw new DomainException("PAY_NOT_HOURLY", "非時薪制無法調整時薪");
        }
        validateHourlyRate(newRate);
        this.hourlyRate = newRate;
        this.calculatedHourlyRate = newRate;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 計算月薪資總額 (底薪 + 所有收入項目)
     * 
     * @return 月薪資總額
     */
    public BigDecimal calculateMonthlyGross() {
        BigDecimal base = (payrollSystem == PayrollSystem.MONTHLY)
                ? monthlySalary
                : BigDecimal.ZERO;

        BigDecimal earnings = items.stream()
                .filter(SalaryItem::isEarning)
                .map(SalaryItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return base.add(earnings);
    }

    /**
     * 取得加班時薪
     * 
     * @return 加班時薪 (月薪 ÷ 240 或直接使用時薪)
     */
    public BigDecimal getOvertimeHourlyRate() {
        return calculatedHourlyRate;
    }

    /**
     * 計算投保薪資 (底薪 + 可投保的津貼)
     * 
     * @return 投保薪資
     */
    public BigDecimal calculateInsurableSalary() {
        BigDecimal base = (payrollSystem == PayrollSystem.MONTHLY)
                ? monthlySalary
                : BigDecimal.ZERO;

        BigDecimal insurableEarnings = items.stream()
                .filter(SalaryItem::isEarning)
                .filter(SalaryItem::isInsurable)
                .map(SalaryItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return base.add(insurableEarnings);
    }

    /**
     * 計算薪資扣除項總額
     * 
     * @return 扣除項總額
     */
    public BigDecimal calculateDeductions() {
        return items.stream()
                .filter(SalaryItem::isDeduction)
                .map(SalaryItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 取得指定代碼的薪資項目
     * 
     * @param itemCode 項目代碼
     * @return 薪資項目
     */
    public Optional<SalaryItem> getItemByCode(String itemCode) {
        return items.stream()
                .filter(i -> i.getItemCode().equals(itemCode))
                .findFirst();
    }

    /**
     * 使此薪資結構失效
     * 
     * @param endDate 結束日期
     */
    public void deactivate(LocalDate endDate) {
        this.active = false;
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== 私有驗證方法 ====================

    private static void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be null or blank");
        }
    }

    private static void validateMonthlySalary(BigDecimal monthlySalary) {
        if (monthlySalary == null || monthlySalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("PAY_INVALID_SALARY", "月薪必須大於 0");
        }
    }

    private static void validateHourlyRate(BigDecimal hourlyRate) {
        if (hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("PAY_INVALID_RATE", "時薪必須大於 0");
        }
    }

    /**
     * 重建 Aggregate (Persistence 用)
     */
    public static SalaryStructure reconstruct(StructureId id,
            String employeeId,
            BigDecimal monthlySalary,
            BigDecimal hourlyRate,
            PayrollSystem payrollSystem,
            PayrollCycle payrollCycle,
            java.util.List<com.company.hrms.payroll.domain.model.entity.SalaryItem> items,
            java.time.LocalDate effectiveDate,
            java.time.LocalDate endDate,
            boolean active) {
        return SalaryStructure.builder()
                .id(id)
                .employeeId(employeeId)
                .monthlySalary(monthlySalary)
                .hourlyRate(hourlyRate)
                .payrollSystem(payrollSystem)
                .payrollCycle(payrollCycle)
                .items(new java.util.ArrayList<>(items))
                .effectiveDate(effectiveDate)
                .endDate(endDate)
                .active(active)
                .build();
    }
}
