package com.company.hrms.performance.domain.model.valueobject;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 評估項目
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationItem {
    /**
     * 項目 ID
     */
    private UUID itemId;

    /**
     * 項目名稱（如：工作品質、專業能力）
     */
    private String itemName;

    /**
     * 權重（百分比，總和需為 100）
     */
    private Integer weight;

    /**
     * 項目說明
     */
    private String description;

    /**
     * 評分標準說明
     */
    private String scoringCriteria;

    /**
     * 分數（實際評分時填寫）
     */
    private Integer score;

    /**
     * 自評說明
     */
    private String selfComment;

    /**
     * 主管評說明
     */
    private String managerComment;

    /**
     * 建立評估項目定義（用於表單範本）
     */
    public static EvaluationItem createDefinition(
            String itemName,
            Integer weight,
            String description,
            String scoringCriteria) {

        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("項目名稱不可為空");
        }
        if (weight == null || weight <= 0 || weight > 100) {
            throw new IllegalArgumentException("權重必須在 1-100 之間");
        }

        EvaluationItem item = new EvaluationItem();
        item.itemId = UUID.randomUUID();
        item.itemName = itemName;
        item.weight = weight;
        item.description = description;
        item.scoringCriteria = scoringCriteria;
        return item;
    }

    /**
     * 建立評分項目（用於實際評估）
     */
    public static EvaluationItem createWithScore(
            UUID itemId,
            String itemName,
            Integer weight,
            Integer score,
            String comment) {

        if (score == null || score < 1 || score > 5) {
            throw new IllegalArgumentException("分數必須在 1-5 之間");
        }

        EvaluationItem item = new EvaluationItem();
        item.itemId = itemId;
        item.itemName = itemName;
        item.weight = weight;
        item.score = score;
        return item;
    }

    /**
     * 計算加權分數
     */
    public BigDecimal calculateWeightedScore() {
        if (score == null || weight == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(score)
                .multiply(new BigDecimal(weight))
                .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
    }
}
