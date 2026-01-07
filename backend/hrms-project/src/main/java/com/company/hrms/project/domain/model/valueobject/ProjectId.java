package com.company.hrms.project.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

public class ProjectId extends Identifier<String> {

    public ProjectId(String value) {
        super(value);
    }

    public static ProjectId generate() {
        return new ProjectId(UUID.randomUUID().toString());
    }

    public static ProjectId from(String value) {
        return new ProjectId(value);
    }
}
