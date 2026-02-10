package com.company.hrms.common.test.contract;

import java.util.List;

/**
 * 合約規格物件 - 從 JSON 解析而來
 */
public class ContractSpec {
    private String scenarioId;
    private String apiEndpoint;
    private String controller;
    private String service;
    private String permission;
    private Object request;
    private List<ExpectedFilter> expectedQueryFilters;
    private ExpectedResponse expectedResponse;
    private List<BusinessRule> businessRules;
    private List<ExpectedDataChange> expectedDataChanges;
    private List<ExpectedEvent> expectedEvents;

    // Getters and Setters
    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public List<ExpectedFilter> getExpectedQueryFilters() {
        return expectedQueryFilters;
    }

    public void setExpectedQueryFilters(List<ExpectedFilter> expectedQueryFilters) {
        this.expectedQueryFilters = expectedQueryFilters;
    }

    public ExpectedResponse getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(ExpectedResponse expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public List<BusinessRule> getBusinessRules() {
        return businessRules;
    }

    public void setBusinessRules(List<BusinessRule> businessRules) {
        this.businessRules = businessRules;
    }

    public List<ExpectedDataChange> getExpectedDataChanges() {
        return expectedDataChanges;
    }

    public void setExpectedDataChanges(List<ExpectedDataChange> expectedDataChanges) {
        this.expectedDataChanges = expectedDataChanges;
    }

    public List<ExpectedEvent> getExpectedEvents() {
        return expectedEvents;
    }

    public void setExpectedEvents(List<ExpectedEvent> expectedEvents) {
        this.expectedEvents = expectedEvents;
    }

    /**
     * 預期過濾條件
     */
    public static class ExpectedFilter {
        private String field;
        private String operator;
        private Object value;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    /**
     * 業務規則
     */
    public static class BusinessRule {
        private String rule;
        private String description;

        public String getRule() {
            return rule;
        }

        public void setRule(String rule) {
            this.rule = rule;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
