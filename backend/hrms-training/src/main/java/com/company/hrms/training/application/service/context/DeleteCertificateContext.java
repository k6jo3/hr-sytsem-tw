package com.company.hrms.training.application.service.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteCertificateContext extends CertificateIdContext {

    public DeleteCertificateContext(String certificateId, String operatorId) {
        super(certificateId, operatorId);
    }
}
