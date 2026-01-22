package com.company.hrms.training.domain.model.valueobject;

public enum EnrollmentStatus {
    REGISTERED("已報名"),
    APPROVED("已審核"),
    REJECTED("已拒絕"),
    ATTENDED("已出席"),
    COMPLETED("已完成"),
    CANCELLED("已取消"),
    NO_SHOW("未出席");

    private final String label;

    EnrollmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
