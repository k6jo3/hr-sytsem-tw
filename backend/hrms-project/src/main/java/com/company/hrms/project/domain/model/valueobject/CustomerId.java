package com.company.hrms.project.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

public class CustomerId extends Identifier<String> {

    public CustomerId(String value) {
        super(value);
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID().toString());
    }

    public static CustomerId from(String value) {
        return new CustomerId(value);
    }
}
