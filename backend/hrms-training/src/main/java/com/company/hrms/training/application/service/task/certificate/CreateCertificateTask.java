package com.company.hrms.training.application.task.certificate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.api.request.AddCertificateRequest;
import com.company.hrms.training.application.service.context.AddCertificateContext;
import com.company.hrms.training.domain.model.aggregate.Certificate;

/**
 * 建立證照 Task
 */
@Component
public class CreateCertificateTask implements PipelineTask<AddCertificateContext> {

    @Override
    public void execute(AddCertificateContext context) {
        AddCertificateRequest req = context.getRequest();
        Certificate certificate = Certificate.create(
                req.getEmployeeId(),
                req.getCertificateName(),
                req.getIssuingOrganization(),
                req.getCertificateNumber(),
                req.getIssueDate(),
                req.getExpiryDate(),
                req.getCategory(),
                req.getIsRequired(),
                req.getAttachmentUrl(),
                req.getRemarks());
        context.setCertificate(certificate);
    }
}
