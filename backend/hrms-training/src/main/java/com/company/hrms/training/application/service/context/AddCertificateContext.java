package com.company.hrms.training.application.service.context;

import com.company.hrms.training.api.request.AddCertificateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddCertificateContext extends CertificateContext {
    private AddCertificateRequest request;

    public AddCertificateContext(AddCertificateRequest request, String operatorId) {
        super(operatorId);
        this.request = request;
    }
}
