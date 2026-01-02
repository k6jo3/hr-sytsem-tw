package com.company.hrms.project.domain.model.valueobject;

public enum ProjectType {
    DEVELOPMENT("新開發專案"),
    MAINTENANCE("維護專案"),
    CONSULTING("顧問專案");

    private final String displayName;

    ProjectType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
