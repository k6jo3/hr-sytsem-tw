package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.application.service.context.DeleteCertificateContext;
import com.company.hrms.training.application.task.certificate.DeleteCertificateActionTask;
import com.company.hrms.training.application.task.certificate.LoadCertificateActionTask;

import lombok.RequiredArgsConstructor;

@Service("deleteCertificateServiceImpl")
@Transactional
@RequiredArgsConstructor
public class DeleteCertificateServiceImpl implements CommandApiService<Void, Void> {

    private final LoadCertificateActionTask<DeleteCertificateContext> loadCertificateTask;
    private final DeleteCertificateActionTask<DeleteCertificateContext> deleteCertificateTask;

    @Override
    public Void execCommand(Void req, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Certificate ID is required");
        }
        String certId = args[0];

        DeleteCertificateContext ctx = new DeleteCertificateContext(certId, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(loadCertificateTask)
                .next(deleteCertificateTask)
                .execute();

        return null;
    }
}
