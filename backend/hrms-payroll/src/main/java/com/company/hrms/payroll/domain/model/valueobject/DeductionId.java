package com.company.hrms.payroll.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 法扣款 ID 值物件
 */
public class DeductionId extends Identifier<String> {

    public DeductionId(String value) {
        super(value);
    }

    public static DeductionId generate() {
        return new DeductionId(generateUUID());
    }
}
