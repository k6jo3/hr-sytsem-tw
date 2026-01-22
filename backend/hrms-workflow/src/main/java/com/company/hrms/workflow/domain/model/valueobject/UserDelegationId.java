package com.company.hrms.workflow.domain.model.valueobject;

import com.company.hrms.common.domain.model.Identifier;

/**
 * 代理 ID
 */
public class UserDelegationId extends Identifier<String> {

    public UserDelegationId(String value) {
        super(value);
    }

    public static UserDelegationId create() {
        return new UserDelegationId(generateUUID());
    }

    public static UserDelegationId from(String id) {
        return new UserDelegationId(id);
    }
}
