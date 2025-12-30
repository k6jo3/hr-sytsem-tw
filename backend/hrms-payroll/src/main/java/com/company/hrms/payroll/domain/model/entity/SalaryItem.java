package com.company.hrms.payroll.domain.model.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.company.hrms.payroll.domain.model.valueobject.ItemType;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 薪資項目實體
 * 代表薪資結構中的一個收入或扣除項目
 * 
 * <p>
 * 例如：職務加給、伙食津貼、交通補助等
 * </p>
 */
@Getter
@EqualsAndHashCode(of = "itemId")
@Builder
public class SalaryItem {

    /**
     * 項目 ID
     */
    private final String itemId;

    /**
     * 項目代碼 (如: JOB_ALLOWANCE, MEAL_ALLOWANCE)
     */
    private final String itemCode;

    /**
     * 項目名稱 (如: 職務加給, 伙食津貼)
     */
    private final String itemName;

    /**
     * 項目類型 (收入或扣除)
     */
    private final ItemType itemType;

    /**
     * 金額
     */
    private BigDecimal amount;

    /**
     * 是否為固定金額 (否則可能按比例或公式計算)
     */
    @Builder.Default
    private final boolean fixedAmount = true;

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
     * 建立新的薪資項目
     * 
     * @param itemCode 項目代碼
     * @param itemName 項目名稱
     * @param itemType 項目類型
     * @param amount   金額
     * @return 新的薪資項目
     */
    public static SalaryItem create(String itemCode, String itemName,
            ItemType itemType, BigDecimal amount) {
        return SalaryItem.builder()
                .itemId(UUID.randomUUID().toString())
                .itemCode(itemCode)
                .itemName(itemName)
                .itemType(itemType)
                .amount(amount)
                .fixedAmount(true)
                .taxable(true)
                .insurable(true)
                .build();
    }

    /**
     * 建立收入項
     * 
     * @param itemCode 項目代碼
     * @param itemName 項目名稱
     * @param amount   金額
     * @return 收入項
     */
    public static SalaryItem createEarning(String itemCode, String itemName, BigDecimal amount) {
        return create(itemCode, itemName, ItemType.EARNING, amount);
    }

    /**
     * 建立扣除項
     * 
     * @param itemCode 項目代碼
     * @param itemName 項目名稱
     * @param amount   金額
     * @return 扣除項
     */
    public static SalaryItem createDeduction(String itemCode, String itemName, BigDecimal amount) {
        return create(itemCode, itemName, ItemType.DEDUCTION, amount);
    }

    /**
     * 建立不可稅的收入項 (如: 伙食津貼)
     * 
     * @param itemCode 項目代碼
     * @param itemName 項目名稱
     * @param amount   金額
     * @return 不可稅的收入項
     */
    public static SalaryItem createNonTaxableEarning(String itemCode, String itemName, BigDecimal amount) {
        return SalaryItem.builder()
                .itemId(UUID.randomUUID().toString())
                .itemCode(itemCode)
                .itemName(itemName)
                .itemType(ItemType.EARNING)
                .amount(amount)
                .fixedAmount(true)
                .taxable(false)
                .insurable(false)
                .build();
    }

    /**
     * 更新金額
     * 
     * @param newAmount 新金額
     */
    public void updateAmount(BigDecimal newAmount) {
        if (newAmount == null || newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }
        this.amount = newAmount;
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
    public static SalaryItem reconstruct(String itemId,
            String itemCode,
            String itemName,
            ItemType type,
            BigDecimal amount,
            boolean fixedAmount,
            boolean taxable,
            boolean insurable) {
        return SalaryItem.builder()
                .itemId(itemId)
                .itemCode(itemCode)
                .itemName(itemName)
                .itemType(type)
                .amount(amount)
                .fixedAmount(fixedAmount)
                .taxable(taxable)
                .insurable(insurable)
                .build();
    }
}
