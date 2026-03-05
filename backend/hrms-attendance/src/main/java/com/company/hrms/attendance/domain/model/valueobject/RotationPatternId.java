package com.company.hrms.attendance.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 輪班模式 ID 值物件
 */
public class RotationPatternId extends Identifier<String> {

    public RotationPatternId(String value) {
        super(value);
    }

    public static RotationPatternId generate() {
        return new RotationPatternId(generateUUID());
    }
}
