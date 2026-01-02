package com.company.hrms.performance.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 強制分配驗證服務
 * 
 * 驗證評等分布是否符合強制分配規則
 */
public class DistributionValidator {

    /**
     * 驗證評等分布是否符合強制分配規則
     * 
     * @param actualRatings     實際評等列表
     * @param distributionRules 強制分配規則 (評等 -> 百分比)
     * @param tolerance         容許誤差百分比（例如：5 表示 ±5%）
     * @return 驗證結果
     */
    public ValidationResult validate(
            List<String> actualRatings,
            Map<String, Integer> distributionRules,
            int tolerance) {

        if (actualRatings == null || actualRatings.isEmpty()) {
            throw new IllegalArgumentException("實際評等列表不可為空");
        }
        if (distributionRules == null || distributionRules.isEmpty()) {
            throw new IllegalArgumentException("強制分配規則不可為空");
        }

        // 計算實際分布
        Map<String, Integer> actualDistribution = calculateDistribution(actualRatings);

        // 驗證每個評等的分布
        ValidationResult result = new ValidationResult();
        result.setCompliant(true);

        for (Map.Entry<String, Integer> rule : distributionRules.entrySet()) {
            String rating = rule.getKey();
            int expectedPercentage = rule.getValue();
            int actualPercentage = actualDistribution.getOrDefault(rating, 0);

            // 計算差異
            int difference = Math.abs(actualPercentage - expectedPercentage);

            if (difference > tolerance) {
                result.setCompliant(false);
                result.addViolation(
                        rating,
                        expectedPercentage,
                        actualPercentage,
                        "評等 " + rating + " 的比例為 " + actualPercentage + "%，" +
                                "應為 " + expectedPercentage + "% (±" + tolerance + "%)");
            }
        }

        return result;
    }

    /**
     * 計算實際評等分布
     */
    private Map<String, Integer> calculateDistribution(List<String> ratings) {
        int total = ratings.size();

        // 計算每個評等的數量
        Map<String, Long> counts = ratings.stream()
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()));

        // 轉換為百分比
        Map<String, Integer> distribution = new HashMap<>();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            int percentage = (int) Math.round((entry.getValue() * 100.0) / total);
            distribution.put(entry.getKey(), percentage);
        }

        return distribution;
    }

    /**
     * 驗證結果
     */
    public static class ValidationResult {
        private boolean compliant;
        private Map<String, Violation> violations = new HashMap<>();

        public boolean isCompliant() {
            return compliant;
        }

        public void setCompliant(boolean compliant) {
            this.compliant = compliant;
        }

        public Map<String, Violation> getViolations() {
            return violations;
        }

        public void addViolation(String rating, int expected, int actual, String message) {
            violations.put(rating, new Violation(rating, expected, actual, message));
        }

        public boolean hasViolations() {
            return !violations.isEmpty();
        }
    }

    /**
     * 違規項目
     */
    public static class Violation {
        private final String rating;
        private final int expectedPercentage;
        private final int actualPercentage;
        private final String message;

        public Violation(String rating, int expectedPercentage, int actualPercentage, String message) {
            this.rating = rating;
            this.expectedPercentage = expectedPercentage;
            this.actualPercentage = actualPercentage;
            this.message = message;
        }

        public String getRating() {
            return rating;
        }

        public int getExpectedPercentage() {
            return expectedPercentage;
        }

        public int getActualPercentage() {
            return actualPercentage;
        }

        public String getMessage() {
            return message;
        }
    }
}
