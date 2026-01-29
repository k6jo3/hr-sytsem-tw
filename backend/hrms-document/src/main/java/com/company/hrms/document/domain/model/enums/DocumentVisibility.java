package com.company.hrms.document.domain.model.enums;

/**
 * 文件可見性
 */
public enum DocumentVisibility {
    PRIVATE("私人"),
    SHARED("共享"),
    DEPARTMENT("部門"),
    PUBLIC("公開");

    private final String description;

    DocumentVisibility(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
