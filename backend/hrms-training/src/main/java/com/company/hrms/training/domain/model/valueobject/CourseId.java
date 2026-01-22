package com.company.hrms.training.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseId extends Identifier<String> {

    private CourseId(String id) {
        super(id);
    }

    public static CourseId create() {
        return new CourseId(UUID.randomUUID().toString());
    }

    public static CourseId from(String id) {
        if (id == null) {
            throw new IllegalArgumentException("CourseId cannot be null");
        }
        return new CourseId(id);
    }

    public String toString() {
        return value;
    }
}
