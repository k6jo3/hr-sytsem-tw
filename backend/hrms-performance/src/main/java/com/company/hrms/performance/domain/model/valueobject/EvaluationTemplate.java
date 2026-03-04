package com.company.hrms.performance.domain.model.valueobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考核表單範本
 */
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

    public EvaluationTemplate() {
    }

    public EvaluationTemplate(String formName, ScoringSystem scoringSystem, Boolean forcedDistribution,
            Map<String, Integer> distributionRules, List<EvaluationItem> evaluationItems, Boolean isPublished) {
        this.formName = formName;
        this.scoringSystem = scoringSystem;
        this.forcedDistribution = forcedDistribution;
        this.distributionRules = distributionRules;
        this.evaluationItems = evaluationItems;
        this.isPublished = isPublished;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public ScoringSystem getScoringSystem() {
        return scoringSystem;
    }

    public void setScoringSystem(ScoringSystem scoringSystem) {
        this.scoringSystem = scoringSystem;
    }

    public Boolean getForcedDistribution() {
        return forcedDistribution;
    }

    public void setForcedDistribution(Boolean forcedDistribution) {
        this.forcedDistribution = forcedDistribution;
    }

    public Map<String, Integer> getDistributionRules() {
        return distributionRules;
    }

    public List<EvaluationItem> getEvaluationItems() {
        return evaluationItems;
    }

    public void setEvaluationItems(List<EvaluationItem> evaluationItems) {
        this.evaluationItems = evaluationItems;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

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
        if (Boolean.TRUE.equals(isPublished)) {
            throw new IllegalStateException("表單已發布，無法修改");
        }
        if (item == null) {
            throw new IllegalArgumentException("評估項目不可為 null");
        }
        this.evaluationItems.add(item);
    }

    /**
     * 設定強制分配規則（JSON 反序列化用，不做業務驗證）
     */
    public void setDistributionRules(Map<String, Integer> rules) {
        this.distributionRules = rules != null ? new HashMap<>(rules) : null;
    }

    /**
     * 業務操作：更新強制分配規則（含業務驗證）
     */
    public void updateDistributionRules(Map<String, Integer> rules) {
        if (Boolean.TRUE.equals(isPublished)) {
            throw new IllegalStateException("表單已發布，無法修改");
        }
        if (!Boolean.TRUE.equals(forcedDistribution)) {
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
