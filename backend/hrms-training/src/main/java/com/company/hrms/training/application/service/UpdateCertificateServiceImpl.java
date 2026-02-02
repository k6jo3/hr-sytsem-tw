package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.UpdateCertificateRequest;
import com.company.hrms.training.api.response.CertificateResponse;
import com.company.hrms.training.application.service.context.UpdateCertificateContext;
import com.company.hrms.training.application.service.task.certificate.LoadCertificateActionTask;
import com.company.hrms.training.application.service.task.certificate.SaveCertificateActionTask;
import com.company.hrms.training.application.service.task.certificate.UpdateCertificateTask;
import com.company.hrms.training.domain.model.aggregate.Certificate;

import lombok.RequiredArgsConstructor;

@Service("updateCertificateServiceImpl")
@Transactional
@RequiredArgsConstructor
public class UpdateCertificateServiceImpl implements CommandApiService<UpdateCertificateRequest, CertificateResponse> {

    private final LoadCertificateActionTask<UpdateCertificateContext> loadCertificateTask;
    private final UpdateCertificateTask updateCertificateTask;
    private final SaveCertificateActionTask<UpdateCertificateContext> saveCertificateTask;

    @Override
    public CertificateResponse execCommand(UpdateCertificateRequest req, JWTModel currentUser, String... args)
            throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Certificate ID is required");
        }
        String certId = args[0];

        UpdateCertificateContext ctx = new UpdateCertificateContext(certId, req, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadCertificateTask)
                .next(updateCertificateTask)
                .next(saveCertificateTask)
                .execute();

        return toResponse(ctx.getCertificate());
    }

    private CertificateResponse toResponse(Certificate cert) {
        return com.company.hrms.training.application.assembler.CertificateAssembler.toResponse(cert);
    }
}

