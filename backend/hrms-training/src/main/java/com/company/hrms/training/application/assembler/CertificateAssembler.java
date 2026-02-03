package com.company.hrms.training.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.training.api.response.CertificateResponse;
import com.company.hrms.training.domain.model.aggregate.Certificate;

/**
 * 霅 Assembler
 * 鞎痊 Aggregate ??DTO 銋????? */
@Component
public class CertificateAssembler {

    public static CertificateResponse toResponse(Certificate cert) {
        if (cert == null) {
            return null;
        }

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
