package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 換班申請 ID 值物件
 */
public class SwapRequestId extends Identifier<String> {

    public SwapRequestId(String value) {
        super(value);
    }

    public static SwapRequestId generate() {
        return new SwapRequestId(generateUUID());
    }
}
