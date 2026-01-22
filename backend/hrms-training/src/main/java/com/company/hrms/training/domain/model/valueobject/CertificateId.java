package com.company.hrms.training.domain.model.valueobject;

import java.util.UUID;

import com.company.hrms.common.domain.model.Identifier;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CertificateId extends Identifier<String> {

    private CertificateId(String id) {
        super(id);
    }

    public static CertificateId create() {
        return new CertificateId(UUID.randomUUID().toString());
    }

    public static CertificateId from(String id) {
        if (id == null) {
            throw new IllegalArgumentException("CertificateId cannot be null");
        }
        return new CertificateId(id);
    }

    public String toString() {
        return value;
    }
}
