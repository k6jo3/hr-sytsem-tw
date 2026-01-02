package com.company.hrms.project.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

public class TaskId extends Identifier<String> {

    public TaskId(String value) {
        super(value);
    }

    public static TaskId generate() {
        return new TaskId(UUID.randomUUID().toString());
    }

    public static TaskId from(String value) {
        return new TaskId(value);
    }
}
