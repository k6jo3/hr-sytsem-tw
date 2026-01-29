package com.company.hrms.document.domain.model.enums;

public enum DocumentTemplateStatus {
    DRAFT("草稿"),
    ACTIVE("啟用"),
    INACTIVE("停用");

    private final String description;

    DocumentTemplateStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
