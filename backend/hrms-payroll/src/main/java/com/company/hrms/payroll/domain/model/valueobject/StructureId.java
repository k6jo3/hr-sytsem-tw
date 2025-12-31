package com.company.hrms.payroll.domain.model.valueobject;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 薪資結構 ID 值物件
 * 封裝薪資結構的唯一識別碼
 */
@Getter
@EqualsAndHashCode
public class StructureId {

    private final String value;

    /**
     * 建構 StructureId 值物件
     * 
     * @param value 薪資結構 ID
     * @throws IllegalArgumentException 當 value 為 null 或空白時
     */
    public StructureId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("StructureId cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * 產生新的 StructureId
     * 
     * @return 新的 StructureId
     */
    public static StructureId generate() {
        return new StructureId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
