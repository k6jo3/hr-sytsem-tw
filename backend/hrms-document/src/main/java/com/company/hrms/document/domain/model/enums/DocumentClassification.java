package com.company.hrms.document.domain.model.enums;

/**
 * 文件分類
 */
public enum DocumentClassification {
    PUBLIC("公開"),
    INTERNAL("內部"),
    CONFIDENTIAL("機密"),
    RESTRICTED("限制");

    private final String description;

    DocumentClassification(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
