package com.company.hrms.training.application.task.certificate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.api.request.UpdateCertificateRequest;
import com.company.hrms.training.application.service.context.UpdateCertificateContext;

/**
 * 更新證照 Task
 */
@Component
public class UpdateCertificateTask implements PipelineTask<UpdateCertificateContext> {

    @Override
    public void execute(UpdateCertificateContext context) {
        UpdateCertificateRequest req = context.getRequest();
        context.getCertificate().update(
                req.getCertificateNumber(),
                req.getIssueDate(),
                req.getExpiryDate(),
                req.getAttachmentUrl(),
                req.getRemarks());
    }
}
