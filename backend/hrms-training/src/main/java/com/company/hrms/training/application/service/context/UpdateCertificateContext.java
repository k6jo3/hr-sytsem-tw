package com.company.hrms.training.application.service.context;

import com.company.hrms.training.api.request.UpdateCertificateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateCertificateContext extends CertificateIdContext {
    private UpdateCertificateRequest request;

    public UpdateCertificateContext(String certificateId, UpdateCertificateRequest request, String operatorId) {
        super(certificateId, operatorId);
        this.request = request;
    }
}
