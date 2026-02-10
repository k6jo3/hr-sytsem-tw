package com.company.hrms.common.test.contract;

import java.util.List;
import java.util.Map;

/**
 * 預期回應結果
 */
public class ExpectedResponse {
    private Integer statusCode;
    private String dataPath;
    private Integer minRecords;
    private Integer maxRecords;
    private Integer exactRecords;
    private List<RequiredField> requiredFields;
    private OrderBy orderBy;
    private Pagination pagination;
    private List<FieldAssertion> assertions;
    private Map<String, Object> responseStructure;

    // Getters and Setters
    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public Integer getMinRecords() {
        return minRecords;
    }

    public void setMinRecords(Integer minRecords) {
        this.minRecords = minRecords;
    }

    public Integer getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(Integer maxRecords) {
        this.maxRecords = maxRecords;
    }

    public Integer getExactRecords() {
        return exactRecords;
    }

    public void setExactRecords(Integer exactRecords) {
        this.exactRecords = exactRecords;
    }

    public List<RequiredField> getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(List<RequiredField> requiredFields) {
        this.requiredFields = requiredFields;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<FieldAssertion> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<FieldAssertion> assertions) {
        this.assertions = assertions;
    }

    public Map<String, Object> getResponseStructure() {
        return responseStructure;
    }

    public void setResponseStructure(Map<String, Object> responseStructure) {
        this.responseStructure = responseStructure;
    }

    /**
     * 排序規則
     */
    public static class OrderBy {
        private String field;
        private String direction; // ASC or DESC

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }

    /**
     * 分頁資訊
     */
    public static class Pagination {
        private Boolean required;

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }
    }
}
