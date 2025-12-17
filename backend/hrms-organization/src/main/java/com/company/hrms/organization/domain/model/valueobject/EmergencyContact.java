package com.company.hrms.organization.domain.model.valueobject;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 緊急聯絡人值對象
 */
@Getter
@Builder
@EqualsAndHashCode
public class EmergencyContact {

    /**
     * 聯絡人姓名
     */
    private final String name;

    /**
     * 與員工的關係
     */
    private final String relationship;

    /**
     * 聯絡電話
     */
    private final String phoneNumber;

    /**
     * 檢查是否有有效的緊急聯絡人資料
     * @return 是否有效
     */
    public boolean isValid() {
        return name != null && !name.isBlank() &&
               phoneNumber != null && !phoneNumber.isBlank();
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "未設定";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (relationship != null && !relationship.isBlank()) {
            sb.append(" (").append(relationship).append(")");
        }
        sb.append(" - ").append(phoneNumber);
        return sb.toString();
    }

    /**
     * 建立空的緊急聯絡人
     * @return 空實例
     */
    public static EmergencyContact empty() {
        return EmergencyContact.builder().build();
    }
}
