package com.company.hrms.payroll.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 薪資預借 ID 值物件
 */
public class AdvanceId extends Identifier<String> {

    public AdvanceId(String value) {
        super(value);
    }

    public static AdvanceId generate() {
        return new AdvanceId(generateUUID());
    }
}
