package com.company.hrms.training.application.service.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 包含 ID 的證照操作 Context 基類
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class CertificateIdContext extends CertificateContext {
    private String certificateId;

    public CertificateIdContext() {
        super();
    }

    public CertificateIdContext(String certificateId, String operatorId) {
        super(operatorId);
        this.certificateId = certificateId;
    }
}
