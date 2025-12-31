package com.company.hrms.payroll.domain.model.valueobject;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 薪資單 ID 值物件
 * 封裝薪資單的唯一識別碼
 */
@Getter
@EqualsAndHashCode
public class PayslipId {

    private final String value;

    /**
     * 建構 PayslipId 值物件
     * 
     * @param value 薪資單 ID
     * @throws IllegalArgumentException 當 value 為 null 或空白時
     */
    public PayslipId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PayslipId cannot be null or blank");
        }
        this.value = value;
    }

    /**
     * 產生新的 PayslipId
     * 
     * @return 新的 PayslipId
     */
    public static PayslipId generate() {
        return new PayslipId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
