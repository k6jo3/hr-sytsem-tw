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
 * 查詢證照列表服務
 * 使用 QueryBuilder.fromDto() 進行宣告式查詢
 */
@Service("getCertificatesServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCertificatesServiceImpl implements QueryApiService<GetCertificatesRequest, Page<CertificateResponse>> {

    private final CertificateQueryRepository certificateRepository;

    @Override
    public Page<CertificateResponse> getResponse(GetCertificatesRequest request, JWTModel currentUser, String... args) {
        // 使用 QueryBuilder 從 Request DTO 自動建構查詢條件 (宣告式查詢)
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // 取得分頁資訊 (從 PageRequest 轉換)
        Pageable pageable = request.toPageable();

        // 執行查詢
        Page<CertificateEntity> page = certificateRepository.findPage(query, pageable);

        // 轉換為 DTO
        List<CertificateResponse> responseList = new ArrayList<>();
        for (CertificateEntity cert : page.getContent()) {
            responseList.add(toResponse(cert));
        }

        return new PageImpl<>(responseList, pageable, page.getTotalElements());
    }

    private CertificateResponse toResponse(CertificateEntity cert) {
        CertificateResponse res = new CertificateResponse();
        res.setCertificateId(cert.getCertificateId());
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
