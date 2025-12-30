package com.company.hrms.payroll.domain.model.valueobject;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * 所得稅級距值物件
 */
@Getter
@Builder
public class TaxBracket {

    /**
     * 級距下限
     */
    private final BigDecimal minIncome;

    /**
     * 級距上限 (null 表示無上限)
     */
    private final BigDecimal maxIncome;

    /**
     * 稅率 (如 0.05 代表 5%)
     */
    private final BigDecimal taxRate;

    /**
     * 累進差額
     */
    private final BigDecimal progressiveDeduction;

    /**
     * 檢查收入是否落在此級距
     *
     * @param income 收入
     * @return 是否符合
     */
    public boolean contains(BigDecimal income) {
        if (income == null)
            return false;
        if (income.compareTo(minIncome) < 0)
            return false;
        return maxIncome == null || income.compareTo(maxIncome) <= 0;
    }
}
