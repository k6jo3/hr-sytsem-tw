package com.company.hrms.organization.application.service.ess;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.ess.RequestCertificateRequest;
import com.company.hrms.organization.domain.model.entity.CertificateRequest;
import com.company.hrms.organization.domain.model.valueobject.CertificateType;
import com.company.hrms.organization.domain.repository.ICertificateRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 申請證明文件服務實作 (員工自助)
 */
@Service("requestCertificateServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RequestCertificateServiceImpl implements CommandApiService<RequestCertificateRequest, Void> {

    private final ICertificateRequestRepository certificateRequestRepository;

    @Override
    public Void execCommand(RequestCertificateRequest request, JWTModel currentUser, String... args) throws Exception {
        log.info("Requesting certificate: type={}, user={}", request.getCertificateType(), currentUser.getUserId());

        // 1. 轉換類型 (處理 DTO 與 Enum 的差異)
        CertificateType type;
        switch (request.getCertificateType()) {
            case "EMPLOYMENT":
                type = CertificateType.EMPLOYMENT_CERTIFICATE;
                break;
            case "SALARY":
                type = CertificateType.SALARY_CERTIFICATE;
                break;
            default:
                // 嘗試直接轉換
                try {
                    type = CertificateType.valueOf(request.getCertificateType());
                } catch (IllegalArgumentException e) {
                    type = CertificateType.EMPLOYMENT_CERTIFICATE; // Default fallback or throw error
                }
        }

        // 2. 建立領域物件
        CertificateRequest domainRequest = CertificateRequest.create(
                UUID.fromString(currentUser.getUserId()),
                type,
                request.getPurpose(),
                request.getCopies());

        // 3. 儲存
        certificateRequestRepository.save(domainRequest);

        log.info("Certificate request created: id={}", domainRequest.getId());

        return null;
    }
}
