package com.company.hrms.common.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * GROUP BY 子句定義
 * 包含分組欄位、聚合欄位與 HAVING 條件
 *
 * <p>使用範例:</p>
 * <pre>
 * GroupByClause clause = GroupByClause.builder()
 *     .groupBy("project.id", "project.name")
 *     .aggregate(AggregateField.sum("hours", "totalHours"))
 *     .aggregate(AggregateField.countDistinct("employeeId", "headCount"))
 *     .having(QueryBuilder.where().gt("SUM(hours)", 100).build())
 *     .build();
 * </pre>
 */
public class GroupByClause implements Serializable {

    private static final long serialVersionUID = 1L;

    /** GROUP BY 欄位 */
    private final List<String> groupByFields;

    /** 聚合欄位 */
    private final List<AggregateField> aggregates;

    /** HAVING 條件 (可選) */
    private final QueryGroup having;

    private GroupByClause(Builder builder) {
        this.groupByFields = Collections.unmodifiableList(new ArrayList<>(builder.groupByFields));
        this.aggregates = Collections.unmodifiableList(new ArrayList<>(builder.aggregates));
        this.having = builder.having;
    }

    // ========== 靜態工廠方法 ==========

    /**
     * 建立 Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 建立簡單的 GROUP BY (無聚合、無 HAVING)
     */
    public static GroupByClause of(String... groupByFields) {
        return builder().groupBy(groupByFields).build();
    }

    // ========== Getters ==========

    public List<String> getGroupByFields() {
        return groupByFields;
    }

    public List<AggregateField> getAggregates() {
        return aggregates;
    }

    public QueryGroup getHaving() {
        return having;
    }

    public boolean hasHaving() {
        return having != null && !having.isEmpty();
    }

    public boolean hasAggregates() {
        return !aggregates.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupByClause that = (GroupByClause) o;
        return Objects.equals(groupByFields, that.groupByFields) &&
               Objects.equals(aggregates, that.aggregates) &&
               Objects.equals(having, that.having);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupByFields, aggregates, having);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GroupByClause{");
        sb.append("groupBy=").append(groupByFields);
        if (!aggregates.isEmpty()) {
            sb.append(", aggregates=").append(aggregates);
        }
        if (having != null) {
            sb.append(", having=").append(having);
        }
        sb.append("}");
        return sb.toString();
    }

    // ========== Builder ==========

    public static class Builder {
        private final List<String> groupByFields = new ArrayList<>();
        private final List<AggregateField> aggregates = new ArrayList<>();
        private QueryGroup having;

        private Builder() {
        }

        /**
         * 加入 GROUP BY 欄位
         */
        public Builder groupBy(String... fields) {
            groupByFields.addAll(Arrays.asList(fields));
            return this;
        }

        /**
         * 加入聚合欄位
         */
        public Builder aggregate(AggregateField aggregate) {
            Objects.requireNonNull(aggregate, "aggregate cannot be null");
            aggregates.add(aggregate);
            return this;
        }

        /**
         * 加入多個聚合欄位
         */
        public Builder aggregates(AggregateField... aggregateFields) {
            for (AggregateField agg : aggregateFields) {
                aggregate(agg);
            }
            return this;
        }

        /**
         * 加入 COUNT 聚合
         */
        public Builder count(String field, String alias) {
            return aggregate(AggregateField.count(field, alias));
        }

        /**
         * 加入 COUNT DISTINCT 聚合
         */
        public Builder countDistinct(String field, String alias) {
            return aggregate(AggregateField.countDistinct(field, alias));
        }

        /**
         * 加入 SUM 聚合
         */
        public Builder sum(String field, String alias) {
            return aggregate(AggregateField.sum(field, alias));
        }

        /**
         * 加入 AVG 聚合
         */
        public Builder avg(String field, String alias) {
            return aggregate(AggregateField.avg(field, alias));
        }

        /**
         * 加入 MAX 聚合
         */
        public Builder max(String field, String alias) {
            return aggregate(AggregateField.max(field, alias));
        }

        /**
         * 加入 MIN 聚合
         */
        public Builder min(String field, String alias) {
            return aggregate(AggregateField.min(field, alias));
        }

        /**
         * 設定 HAVING 條件
         */
        public Builder having(QueryGroup having) {
            this.having = having;
            return this;
        }

        /**
         * 建構 GroupByClause
         */
        public GroupByClause build() {
            return new GroupByClause(this);
        }
    }
}
