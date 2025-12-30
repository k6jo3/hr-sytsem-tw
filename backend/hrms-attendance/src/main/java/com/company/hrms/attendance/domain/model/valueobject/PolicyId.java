package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 政策 ID
 */
public class PolicyId extends Identifier<String> {
    public PolicyId(String value) {
        super(value);
    }

    public static PolicyId next() {
        return new PolicyId(generateUUID());
    }
}
