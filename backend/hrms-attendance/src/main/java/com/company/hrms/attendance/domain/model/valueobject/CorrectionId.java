package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 補卡申請 ID
 */
public class CorrectionId extends Identifier<String> {
    public CorrectionId(String value) {
        super(value);
    }

    public static CorrectionId next() {
        return new CorrectionId(generateUUID());
    }
}
