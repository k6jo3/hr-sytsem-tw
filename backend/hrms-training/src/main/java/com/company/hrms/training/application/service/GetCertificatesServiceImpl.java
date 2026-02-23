package com.company.hrms.training.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.training.api.request.GetCertificatesRequest;
import com.company.hrms.training.api.response.CertificateResponse;
import com.company.hrms.training.infrastructure.entity.CertificateEntity;
import com.company.hrms.training.infrastructure.repository.CertificateQueryRepository;

import lombok.RequiredArgsConstructor;

/**
 * ?亥岷霅?”??
 * 雿輻 QueryBuilder.fromDto() ?脰?摰??撘閰?
 */
@Service("getCertificatesServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class GetCertificatesServiceImpl implements QueryApiService<GetCertificatesRequest, Page<CertificateResponse>> {

    private final CertificateQueryRepository certificateRepository;

    @Override
    public Page<CertificateResponse> getResponse(GetCertificatesRequest request, JWTModel currentUser, String... args) {
        // 雿輻 QueryBuilder 敺?Request DTO ?芸?撱箸??亥岷璇辣 (摰??撘閰?
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // ????鞈? (敺?PageRequest 頧?)
        Pageable pageable = request.toPageable();

        // ?瑁??亥岷
        Page<CertificateEntity> page = certificateRepository.findPage(query, pageable);

        // 頧???DTO
        List<CertificateResponse> responseList = new ArrayList<>();
        for (CertificateEntity cert : page.getContent()) {
            responseList.add(toResponse(cert));
        }

        return new PageImpl<>(responseList, pageable, page.getTotalElements());
    }

    private CertificateResponse toResponse(CertificateEntity cert) {
        CertificateResponse res = new CertificateResponse();
        res.setCertificateId(cert.getCertificateId());
        res.setEmployeeId(cert.getEmployee_id());
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
