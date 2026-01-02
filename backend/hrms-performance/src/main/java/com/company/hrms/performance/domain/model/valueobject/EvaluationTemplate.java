package com.company.hrms.performance.domain.model.valueobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 考核表單範本
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationTemplate {
    /**
     * 表單名稱
     */
    private String formName;

    /**
     * 評分制度
     */
    private ScoringSystem scoringSystem;

    /**
     * 是否啟用強制分配
     */
    private Boolean forcedDistribution;

    /**
     * 強制分配規則（評等 -> 百分比）
     * 例如：{"A": 10, "B": 30, "C": 50, "D": 8, "E": 2}
     */
    private Map<String, Integer> distributionRules;

    /**
     * 評估項目列表
     */
    private List<EvaluationItem> evaluationItems;

    /**
     * 是否已發布
     */
    private Boolean isPublished;

    /**
     * 建立表單範本
     */
    public static EvaluationTemplate create(
            String formName,
            ScoringSystem scoringSystem,
            Boolean forcedDistribution) {

        if (formName == null || formName.isBlank()) {
            throw new IllegalArgumentException("表單名稱不可為空");
        }
        if (scoringSystem == null) {
            throw new IllegalArgumentException("評分制度不可為空");
        }

        EvaluationTemplate template = new EvaluationTemplate();
        template.formName = formName;
        template.scoringSystem = scoringSystem;
        template.forcedDistribution = forcedDistribution != null && forcedDistribution;
        template.distributionRules = new HashMap<>();
        template.evaluationItems = new ArrayList<>();
        template.isPublished = false;
        return template;
    }

    /**
     * 新增評估項目
     */
    public void addEvaluationItem(EvaluationItem item) {
        if (isPublished) {
            throw new IllegalStateException("表單已發布，無法修改");
        }
        if (item == null) {
            throw new IllegalArgumentException("評估項目不可為 null");
        }
        this.evaluationItems.add(item);
    }

    /**
     * 設定強制分配規則
     */
    public void setDistributionRules(Map<String, Integer> rules) {
        if (isPublished) {
            throw new IllegalStateException("表單已發布，無法修改");
        }
        if (!forcedDistribution) {
            throw new IllegalStateException("未啟用強制分配");
        }

        // 驗證比例總和為 100
        int sum = rules.values().stream().mapToInt(Integer::intValue).sum();
        if (sum != 100) {
            throw new IllegalArgumentException("分配比例總和必須為 100%，目前為 " + sum + "%");
        }

        this.distributionRules = new HashMap<>(rules);
    }

    /**
     * 驗證表單完整性
     */
    public void validate() {
        if (evaluationItems == null || evaluationItems.isEmpty()) {
            throw new IllegalStateException("至少需要一個評估項目");
        }

        // 驗證權重總和為 100
        int totalWeight = evaluationItems.stream()
                .mapToInt(EvaluationItem::getWeight)
                .sum();
        if (totalWeight != 100) {
            throw new IllegalStateException("評估項目權重總和必須為 100%，目前為 " + totalWeight + "%");
        }

        // 如果啟用強制分配，驗證規則已設定
        if (forcedDistribution && (distributionRules == null || distributionRules.isEmpty())) {
            throw new IllegalStateException("啟用強制分配時必須設定分配規則");
        }
    }

    /**
     * 發布表單
     */
    public void publish() {
        validate();
        this.isPublished = true;
    }
}
