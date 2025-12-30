package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 餘額 ID
 */
public class BalanceId extends Identifier<String> {
    public BalanceId(String value) {
        super(value);
    }

    public static BalanceId next() {
        return new BalanceId(generateUUID());
    }
}
