package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 申請單 ID
 */
public class ApplicationId extends Identifier<String> {
    public ApplicationId(String value) {
        super(value);
    }

    public static ApplicationId next() {
        return new ApplicationId(generateUUID());
    }
}
