package com.company.hrms.common.test.contract;

import java.util.List;

/**
 * 預期資料異動
 */
public class ExpectedDataChange {
    private String action; // INSERT, UPDATE, DELETE, SOFT_DELETE
    private String table;
    private Integer count;
    private List<FieldAssertion> assertions;

    // Getters and Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<FieldAssertion> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<FieldAssertion> assertions) {
        this.assertions = assertions;
    }
}
