package com.company.hrms.training.domain.model.valueobject;

public enum DeliveryMode {
    ONLINE("線上課程"),
    OFFLINE("實體課程"),
    HYBRID("混合式");

    private final String label;

    DeliveryMode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
