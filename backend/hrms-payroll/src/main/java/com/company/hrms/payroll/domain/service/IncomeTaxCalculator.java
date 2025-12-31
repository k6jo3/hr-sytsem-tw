package com.company.hrms.payroll.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.hrms.payroll.domain.model.valueobject.TaxBracket;

/**
 * 所得稅計算器 Domain Service
 * 負責計算薪資所得稅
 */
@Service
public class IncomeTaxCalculator {

    /**
     * 計算所得稅
     * 公式：(應稅薪資 × 稅率) - 累進差額
     *
     * @param taxableIncome 應稅薪資
     * @param brackets      稅率級距表 (需按金額排序)
     * @return 應扣所得稅
     */
    public BigDecimal calculate(BigDecimal taxableIncome, List<TaxBracket> brackets) {
        if (taxableIncome == null || taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (brackets == null || brackets.isEmpty()) {
            return BigDecimal.ZERO; // 若無級距設定，預設不扣稅? 或拋出異常
        }

        // 尋找對應級距
        TaxBracket bracket = brackets.stream()
                .filter(b -> b.contains(taxableIncome))
                .findFirst()
                .orElse(null);

        if (bracket == null) {
            // 若超過最高級距，通常取最後一個 (假設最後一個是無上限)
            bracket = brackets.get(brackets.size() - 1);
        }

        // 計算
        BigDecimal tax = taxableIncome.multiply(bracket.getTaxRate())
                .subtract(bracket.getProgressiveDeduction());

        return tax.max(BigDecimal.ZERO).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 預設 2025 年所得稅級距 (參考用，實際應從資料庫讀取)
     * 
     * @return 2025 級距表
     */
    public List<TaxBracket> getDefault2025Brackets() {
        // 範例數據 (需依實際法規調整)
        return List.of(
                TaxBracket.builder().minIncome(BigDecimal.ZERO).maxIncome(new BigDecimal("88501"))
                        .taxRate(BigDecimal.ZERO).progressiveDeduction(BigDecimal.ZERO).build(),
                TaxBracket.builder().minIncome(new BigDecimal("88502")).maxIncome(null).taxRate(new BigDecimal("0.05"))
                        .progressiveDeduction(BigDecimal.ZERO).build()
        // 更多級距...
        );
    }
}
