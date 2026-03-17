package com.company.hrms.insurance.domain.model.valueobject;

/**
 * 眷屬類型
 * 依據全民健康保險法第2條規定：被保險人之眷屬包含配偶、子女、父母、祖父母、孫子女
 * [2026-03-17 更新] 移除 SIBLING（不符合全民健康保險法），新增 GRANDPARENT、GRANDCHILD
 */
public enum DependentType {

    SPOUSE("配偶"),
    CHILD("子女"),
    PARENT("父母"),
    GRANDPARENT("祖父母"),
    GRANDCHILD("孫子女"),
    OTHER("其他");

    private final String displayName;

    DependentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
