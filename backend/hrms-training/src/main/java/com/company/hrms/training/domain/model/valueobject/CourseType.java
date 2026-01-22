package com.company.hrms.training.domain.model.valueobject;

public enum CourseType {
    INTERNAL("內訓"),
    EXTERNAL("外訓");

    private final String label;

    CourseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
