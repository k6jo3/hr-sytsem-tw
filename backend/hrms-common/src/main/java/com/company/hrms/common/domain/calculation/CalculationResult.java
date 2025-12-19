package com.company.hrms.common.domain.calculation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 結構化計算結果物件
 * 封裝計算結果、套用規則與明細，支援快照測試
 *
 * <p>設計理念：
 * <ul>
 *   <li>結構化輸出便於快照比對</li>
 *   <li>計算過程完整可追溯</li>
 *   <li>符合「回傳結構化物件」原則</li>
 * </ul>
 *
 * <p>使用範例：
 * <pre>
 * CalculationResult&lt;BigDecimal&gt; result = CalculationResult.&lt;BigDecimal&gt;builder()
 *     .value(new BigDecimal("50000"))
 *     .addRule("基本薪資", "底薪 × 1.0")
 *     .addBreakdown("底薪", new BigDecimal("45000"))
 *     .addBreakdown("伙食津貼", new BigDecimal("2400"))
 *     .addBreakdown("交通津貼", new BigDecimal("2600"))
 *     .build();
 * </pre>
 *
 * @param <T> 計算結果型別
 */
public class CalculationResult<T> {

    /** 最終計算結果 */
    private final T value;

    /** 套用的業務規則 */
    private final List<AppliedRule> appliedRules;

    /** 計算明細 */
    private final List<BreakdownItem> breakdown;

    /** 計算時間戳 */
    private final LocalDateTime calculatedAt;

    /** 額外元資料 */
    private final java.util.Map<String, Object> metadata;

    private CalculationResult(Builder<T> builder) {
        this.value = builder.value;
        this.appliedRules = Collections.unmodifiableList(new ArrayList<>(builder.appliedRules));
        this.breakdown = Collections.unmodifiableList(new ArrayList<>(builder.breakdown));
        this.calculatedAt = builder.calculatedAt != null ? builder.calculatedAt : LocalDateTime.now();
        this.metadata = Collections.unmodifiableMap(new java.util.HashMap<>(builder.metadata));
    }

    /**
     * 建立 Builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * 建立簡單結果（無規則明細）
     */
    public static <T> CalculationResult<T> of(T value) {
        return CalculationResult.<T>builder().value(value).build();
    }

    /**
     * 建立帶規則的結果
     */
    public static <T> CalculationResult<T> of(T value, String ruleName, String ruleDescription) {
        return CalculationResult.<T>builder()
            .value(value)
            .addRule(ruleName, ruleDescription)
            .build();
    }

    // Getters

    public T getValue() {
        return value;
    }

    public List<AppliedRule> getAppliedRules() {
        return appliedRules;
    }

    public List<BreakdownItem> getBreakdown() {
        return breakdown;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public java.util.Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * 取得元資料
     */
    @SuppressWarnings("unchecked")
    public <V> V getMetadata(String key) {
        return (V) metadata.get(key);
    }

    /**
     * 檢查是否有套用特定規則
     */
    public boolean hasRule(String ruleName) {
        return appliedRules.stream()
            .anyMatch(r -> r.getName().equals(ruleName));
    }

    /**
     * 取得明細項目值
     */
    @SuppressWarnings("unchecked")
    public <V> V getBreakdownValue(String itemName) {
        return breakdown.stream()
            .filter(b -> b.getName().equals(itemName))
            .findFirst()
            .map(b -> (V) b.getValue())
            .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculationResult<?> that = (CalculationResult<?>) o;
        return Objects.equals(value, that.value) &&
               Objects.equals(appliedRules, that.appliedRules) &&
               Objects.equals(breakdown, that.breakdown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, appliedRules, breakdown);
    }

    @Override
    public String toString() {
        return "CalculationResult{" +
               "value=" + value +
               ", appliedRules=" + appliedRules +
               ", breakdown=" + breakdown +
               ", calculatedAt=" + calculatedAt +
               '}';
    }

    /**
     * Builder 模式
     */
    public static class Builder<T> {
        private T value;
        private final List<AppliedRule> appliedRules = new ArrayList<>();
        private final List<BreakdownItem> breakdown = new ArrayList<>();
        private LocalDateTime calculatedAt;
        private final java.util.Map<String, Object> metadata = new java.util.HashMap<>();

        public Builder<T> value(T value) {
            this.value = value;
            return this;
        }

        public Builder<T> addRule(String name, String description) {
            this.appliedRules.add(new AppliedRule(name, description));
            return this;
        }

        public Builder<T> addRule(AppliedRule rule) {
            this.appliedRules.add(rule);
            return this;
        }

        public Builder<T> addBreakdown(String name, Object value) {
            this.breakdown.add(new BreakdownItem(name, value));
            return this;
        }

        public Builder<T> addBreakdown(String name, Object value, String description) {
            this.breakdown.add(new BreakdownItem(name, value, description));
            return this;
        }

        public Builder<T> addBreakdown(BreakdownItem item) {
            this.breakdown.add(item);
            return this;
        }

        public Builder<T> calculatedAt(LocalDateTime calculatedAt) {
            this.calculatedAt = calculatedAt;
            return this;
        }

        public Builder<T> metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public CalculationResult<T> build() {
            return new CalculationResult<>(this);
        }
    }

    /**
     * 套用的規則記錄
     */
    public static class AppliedRule {
        private final String name;
        private final String description;
        private final java.util.Map<String, Object> parameters;

        public AppliedRule(String name, String description) {
            this(name, description, Collections.emptyMap());
        }

        public AppliedRule(String name, String description, java.util.Map<String, Object> parameters) {
            this.name = Objects.requireNonNull(name, "規則名稱不可為空");
            this.description = description;
            this.parameters = Collections.unmodifiableMap(new java.util.HashMap<>(parameters));
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public java.util.Map<String, Object> getParameters() {
            return parameters;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AppliedRule that = (AppliedRule) o;
            return Objects.equals(name, that.name) &&
                   Objects.equals(description, that.description) &&
                   Objects.equals(parameters, that.parameters);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description, parameters);
        }

        @Override
        public String toString() {
            return "AppliedRule{name='" + name + "', description='" + description + "'}";
        }
    }

    /**
     * 計算明細項目
     */
    public static class BreakdownItem {
        private final String name;
        private final Object value;
        private final String description;

        public BreakdownItem(String name, Object value) {
            this(name, value, null);
        }

        public BreakdownItem(String name, Object value, String description) {
            this.name = Objects.requireNonNull(name, "明細項目名稱不可為空");
            this.value = value;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BreakdownItem that = (BreakdownItem) o;
            return Objects.equals(name, that.name) &&
                   Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        @Override
        public String toString() {
            return "BreakdownItem{name='" + name + "', value=" + value + "}";
        }
    }
}
