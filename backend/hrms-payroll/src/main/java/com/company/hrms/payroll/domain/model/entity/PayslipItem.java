package com.company.hrms.payroll.domain.model.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.company.hrms.payroll.domain.model.valueobject.ItemType;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 薪資單項目實體
 * 代表薪資單中的一個計算結果項目
 * 
 * <p>
 * 與 SalaryItem 不同：
 * <ul>
 * <li>SalaryItem: 薪資結構中的設定 (模板)</li>
 * <li>PayslipItem: 薪資單中的實際計算結果</li>
 * </ul>
 * </p>
 */
@Getter
@EqualsAndHashCode(of = "itemId")
@Builder
public class PayslipItem {

    /**
     * 項目 ID
     */
    private final String itemId;

    /**
     * 項目代碼
     */
    private final String itemCode;

    /**
     * 項目名稱
     */
    private final String itemName;

    /**
     * 項目類型 (收入或扣除)
     */
    private final ItemType itemType;

    /**
     * 計算金額
     */
    private final BigDecimal amount;

    /**
     * 來源說明 (如: 基於薪資結構設定 / 依出勤計算)
     */
    private final String source;

    /**
     * 是否課稅
     */
    @Builder.Default
    private final boolean taxable = true;

    /**
     * 是否納入投保薪資
     */
    @Builder.Default
    private final boolean insurable = true;

    /**
     * 排序順序
     */
    @Builder.Default
    private final int displayOrder = 0;

    /**
     * 從 SalaryItem 建立 PayslipItem
     * 
     * @param salaryItem 薪資項目
     * @return 薪資單項目
     */
    public static PayslipItem fromSalaryItem(SalaryItem salaryItem) {
        return PayslipItem.builder()
                .itemId(UUID.randomUUID().toString())
                .itemCode(salaryItem.getItemCode())
                .itemName(salaryItem.getItemName())
                .itemType(salaryItem.getItemType())
                .amount(salaryItem.getAmount())
                .source("薪資結構設定")
                .taxable(salaryItem.isTaxable())
                .insurable(salaryItem.isInsurable())
                .displayOrder(salaryItem.getDisplayOrder())
                .build();
    }

    /**
     * 建立收入項
     * 
     * @param itemCode 項目代碼
     * @param itemName 項目名稱
     * @param amount   金額
     * @param source   來源說明
     * @return 薪資單項目
     */
    public static PayslipItem createEarning(String itemCode, String itemName,
            BigDecimal amount, String source) {
        return PayslipItem.builder()
                .itemId(UUID.randomUUID().toString())
                .itemCode(itemCode)
                .itemName(itemName)
                .itemType(ItemType.EARNING)
                .amount(amount)
                .source(source)
                .build();
    }

    /**
     * 建立扣除項
     * 
     * @param itemCode 項目代碼
     * @param itemName 項目名稱
     * @param amount   金額
     * @param source   來源說明
     * @return 薪資單項目
     */
    public static PayslipItem createDeduction(String itemCode, String itemName,
            BigDecimal amount, String source) {
        return PayslipItem.builder()
                .itemId(UUID.randomUUID().toString())
                .itemCode(itemCode)
                .itemName(itemName)
                .itemType(ItemType.DEDUCTION)
                .amount(amount)
                .source(source)
                .build();
    }

    /**
     * 建立加班費項目
     * 
     * @param amount      金額
     * @param description 加班描述 (如: 平日加班 8 小時)
     * @return 薪資單項目
     */
    public static PayslipItem createOvertimePay(BigDecimal amount, String description) {
        return createEarning("OVERTIME_PAY", "加班費", amount, description);
    }

    /**
     * 建立勞保費項目
     * 
     * @param amount 金額
     * @return 薪資單項目
     */
    public static PayslipItem createLaborInsurance(BigDecimal amount) {
        return createDeduction("LABOR_INS", "勞工保險費", amount, "依投保薪資計算");
    }

    /**
     * 建立健保費項目
     * 
     * @param amount 金額
     * @return 薪資單項目
     */
    public static PayslipItem createHealthInsurance(BigDecimal amount) {
        return createDeduction("HEALTH_INS", "全民健康保險費", amount, "依投保薪資計算");
    }

    /**
     * 建立所得稅項目
     * 
     * @param amount 金額
     * @return 薪資單項目
     */
    public static PayslipItem createIncomeTax(BigDecimal amount) {
        return createDeduction("INCOME_TAX", "所得稅", amount, "依薪資所得計算");
    }

    /**
     * 檢查是否為收入項
     * 
     * @return 是否為收入項
     */
    public boolean isEarning() {
        return itemType == ItemType.EARNING;
    }

    /**
     * 檢查是否為扣除項
     * 
     * @return 是否為扣除項
     */
    public boolean isDeduction() {
        return itemType == ItemType.DEDUCTION;
    }

    /**
     * 重建 Entity (Persistence 用)
     */
    public static PayslipItem reconstruct(String itemId,
            String itemCode,
            String itemName,
            ItemType itemType,
            BigDecimal amount,
            String source,
            boolean taxable,
            boolean insurable,
            int displayOrder) {
        return PayslipItem.builder()
                .itemId(itemId)
                .itemCode(itemCode)
                .itemName(itemName)
                .itemType(itemType)
                .amount(amount)
                .source(source)
                .taxable(taxable)
                .insurable(insurable)
                .displayOrder(displayOrder)
                .build();
    }
}
