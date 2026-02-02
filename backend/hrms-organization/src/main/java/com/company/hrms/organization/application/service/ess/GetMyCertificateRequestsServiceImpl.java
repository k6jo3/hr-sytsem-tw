package com.company.hrms.organization.application.service.ess;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.ess.CertificateRequestResponse;
import com.company.hrms.organization.domain.model.entity.CertificateRequest;
import com.company.hrms.organization.domain.repository.ICertificateRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢個人證明文件申請記錄服務實作 (員工自助)
 */
@Service("getMyCertificateRequestsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetMyCertificateRequestsServiceImpl
        implements QueryApiService<Void, CertificateRequestResponse.ListResponse> {

    private final ICertificateRequestRepository certificateRequestRepository;

    @Override
    public CertificateRequestResponse.ListResponse getResponse(Void request, JWTModel currentUser, String... args)
            throws Exception {
        UUID employeeId = UUID.fromString(currentUser.getUserId());
        log.info("查詢個人證明文件申請記錄: userId={}", employeeId);

        // 1. 查詢申請列表
        List<CertificateRequest> requests = certificateRequestRepository.findByEmployeeId(employeeId);

        // 2. 轉換為 Response
        List<CertificateRequestResponse> items = requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return CertificateRequestResponse.ListResponse.builder()
                .items(items)
                .totalCount(items.size())
                .build();
    }

    private CertificateRequestResponse mapToResponse(CertificateRequest request) {
        return CertificateRequestResponse.builder()
                .requestId(request.getId().toString())
                .certificateType(request.getCertificateType().name())
                .certificateTypeDisplay(request.getCertificateType().getDisplayName())
                .copies(request.getQuantity() != null ? request.getQuantity() : 0)
                .purpose(request.getPurpose())
                .status(request.getStatus().name())
                .statusDisplay(request.getStatus().getDisplayName())
                .requestDate(request.getRequestDate() != null ? request.getRequestDate().toLocalDate() : null)
                .completedDate(request.getProcessedAt() != null ? request.getProcessedAt().toLocalDate() : null)
                .remarks("") // CertificateRequest entity currently doesn't have a remarks field
                .build();
    }
}
