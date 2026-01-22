package com.company.hrms.training.domain.model.valueobject;

public enum CourseCategory {
    TECHNICAL("技術類"),
    MANAGEMENT("管理類"),
    SOFT_SKILL("軟技能"),
    COMPLIANCE("法規遵循"),
    ORIENTATION("新人訓練"),
    SAFETY("安全衛生"),
    OTHER("其他");

    private final String label;

    CourseCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
