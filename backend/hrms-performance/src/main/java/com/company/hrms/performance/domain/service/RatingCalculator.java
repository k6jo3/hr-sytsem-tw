package com.company.hrms.performance.domain.service;

import java.math.BigDecimal;

import com.company.hrms.performance.domain.model.valueobject.ScoringSystem;

/**
 * 評等計算服務
 * 
 * 根據不同的評分制度計算評等
 */
public class RatingCalculator {

    /**
     * 計算評等
     * 
     * @param score         分數
     * @param scoringSystem 評分制度
     * @return 評等 (A/B/C/D/E)
     */
    public String calculateRating(BigDecimal score, ScoringSystem scoringSystem) {
        if (score == null) {
            throw new IllegalArgumentException("分數不可為 null");
        }
        if (scoringSystem == null) {
            throw new IllegalArgumentException("評分制度不可為 null");
        }

        switch (scoringSystem) {
            case FIVE_POINT:
                return calculateRatingForFivePoint(score);
            case HUNDRED:
                return calculateRatingForHundred(score);
            case FIVE_GRADE:
                throw new IllegalArgumentException("五等第制由使用者直接選擇，不需計算");
            default:
                throw new IllegalArgumentException("不支援的評分制度: " + scoringSystem);
        }
    }

    /**
     * 五分制評等計算
     * >= 4.5 = A (優秀)
     * >= 3.5 = B (良好)
     * >= 2.5 = C (達標)
     * >= 1.5 = D (待改進)
     * < 1.5 = E (不合格)
     */
    private String calculateRatingForFivePoint(BigDecimal score) {
        if (score.compareTo(new BigDecimal("4.5")) >= 0) {
            return "A";
        } else if (score.compareTo(new BigDecimal("3.5")) >= 0) {
            return "B";
        } else if (score.compareTo(new BigDecimal("2.5")) >= 0) {
            return "C";
        } else if (score.compareTo(new BigDecimal("1.5")) >= 0) {
            return "D";
        } else {
            return "E";
        }
    }

    /**
     * 百分制評等計算
     * >= 90 = A (優秀)
     * >= 70 = B (良好)
     * >= 60 = C (達標)
     * >= 50 = D (待改進)
     * < 50 = E (不合格)
     */
    private String calculateRatingForHundred(BigDecimal score) {
        if (score.compareTo(new BigDecimal("90")) >= 0) {
            return "A";
        } else if (score.compareTo(new BigDecimal("70")) >= 0) {
            return "B";
        } else if (score.compareTo(new BigDecimal("60")) >= 0) {
            return "C";
        } else if (score.compareTo(new BigDecimal("50")) >= 0) {
            return "D";
        } else {
            return "E";
        }
    }
}
