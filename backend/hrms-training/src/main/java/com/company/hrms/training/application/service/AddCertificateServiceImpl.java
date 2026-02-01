package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.AddCertificateRequest;
import com.company.hrms.training.api.response.CertificateResponse;
import com.company.hrms.training.application.service.context.AddCertificateContext;
import com.company.hrms.training.application.service.task.certificate.CreateCertificateTask;
import com.company.hrms.training.application.service.task.certificate.SaveCertificateActionTask;
import com.company.hrms.training.domain.model.aggregate.Certificate;

import lombok.RequiredArgsConstructor;

@Service("addCertificateServiceImpl")
@Transactional
@RequiredArgsConstructor
public class AddCertificateServiceImpl implements CommandApiService<AddCertificateRequest, CertificateResponse> {

    private final CreateCertificateTask createCertificateTask;
    private final SaveCertificateActionTask<AddCertificateContext> saveCertificateTask;

    @Override
    public CertificateResponse execCommand(AddCertificateRequest req, JWTModel currentUser, String... args)
            throws Exception {
        AddCertificateContext ctx = new AddCertificateContext(req, currentUser.getUserId());

        BusinessPipeline.start(ctx)
                .next(createCertificateTask)
                .next(saveCertificateTask)
                .execute();

        return toResponse(ctx.getCertificate());
    }

    private CertificateResponse toResponse(Certificate cert) {
        CertificateResponse res = new CertificateResponse();
        res.setCertificateId(cert.getId().toString());
        res.setEmployeeId(cert.getEmployeeId());
        res.setCertificateName(cert.getCertificateName());
        res.setIssuingOrganization(cert.getIssuingOrganization());
        res.setCertificateNumber(cert.getCertificateNumber());
        res.setIssueDate(cert.getIssueDate());
        res.setExpiryDate(cert.getExpiryDate());
        res.setCategory(cert.getCategory());
        res.setIsRequired(cert.getIsRequired());
        res.setAttachmentUrl(cert.getAttachmentUrl());
        res.setRemarks(cert.getRemarks());
        res.setIsVerified(cert.getIsVerified());
        res.setVerifiedBy(cert.getVerifiedBy());
        res.setVerifiedAt(cert.getVerifiedAt());
        res.setStatus(cert.getStatus());
        res.setCreatedAt(cert.getCreatedAt());
        res.setUpdatedAt(cert.getUpdatedAt());
        return res;
    }
}
