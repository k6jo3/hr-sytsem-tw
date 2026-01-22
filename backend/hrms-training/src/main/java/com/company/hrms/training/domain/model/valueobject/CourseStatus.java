package com.company.hrms.training.domain.model.valueobject;

public enum CourseStatus {
    DRAFT("草稿"),
    OPEN("報名中"),
    CLOSED("報名截止"),
    COMPLETED("已結束"),
    CANCELLED("已取消");

    private final String label;

    CourseStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
