package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.training.api.response.CertificateResponse;
import com.company.hrms.training.domain.model.aggregate.Certificate;
import com.company.hrms.training.domain.model.valueobject.CertificateId;
import com.company.hrms.training.domain.repository.ICertificateRepository;

import lombok.RequiredArgsConstructor;

@Service("getCertificateDetailServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCertificateDetailServiceImpl implements QueryApiService<String, CertificateResponse> {

    private final ICertificateRepository certificateRepository;

    @Override
    public CertificateResponse getResponse(String id, JWTModel currentUser, String... args) {
        Certificate cert = certificateRepository.findById(CertificateId.from(id))
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found: " + id));

        return toResponse(cert);
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
