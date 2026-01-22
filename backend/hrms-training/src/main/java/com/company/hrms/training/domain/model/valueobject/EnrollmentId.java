package com.company.hrms.training.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EnrollmentId extends Identifier<String> {

    private EnrollmentId(String id) {
        super(id);
    }

    public static EnrollmentId create() {
        return new EnrollmentId(UUID.randomUUID().toString());
    }

    public static EnrollmentId from(String id) {
        if (id == null) {
            throw new IllegalArgumentException("EnrollmentId cannot be null");
        }
        return new EnrollmentId(id);
    }

    public String toString() {
        return value;
    }
}
