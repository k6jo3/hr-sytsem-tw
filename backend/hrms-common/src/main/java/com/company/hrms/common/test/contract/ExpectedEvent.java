package com.company.hrms.common.test.contract;

import java.util.List;

/**
 * 預期領域事件
 */
public class ExpectedEvent {
    private String eventType;
    private List<FieldAssertion> payload;

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public List<FieldAssertion> getPayload() {
        return payload;
    }

    public void setPayload(List<FieldAssertion> payload) {
        this.payload = payload;
    }
}
