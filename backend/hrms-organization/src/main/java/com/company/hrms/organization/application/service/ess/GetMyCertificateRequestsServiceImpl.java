package com.company.hrms.organization.application.service.ess;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.ess.CertificateRequestResponse;
import com.company.hrms.organization.domain.model.entity.CertificateRequest;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.ICertificateRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 取得我的證明文件申請記錄服務實作
 */
@Service("getMyCertificateRequestsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetMyCertificateRequestsServiceImpl
        implements QueryApiService<Void, CertificateRequestResponse.ListResponse> {

    private final ICertificateRequestRepository certificateRequestRepository;

    @Override
    public CertificateRequestResponse.ListResponse getResponse(Void request,
                                                               JWTModel currentUser,
                                                               String... args) throws Exception {
        log.info("Getting my certificate requests for user: {}", currentUser.getUserId());

        String employeeId = currentUser.getEmployeeId();
        if (employeeId == null) {
            throw new IllegalStateException("使用者未關聯員工資料");
        }

        List<CertificateRequest> requests = certificateRequestRepository
                .findByEmployeeId(new EmployeeId(employeeId));

        List<CertificateRequestResponse> items = requests.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return CertificateRequestResponse.ListResponse.builder()
                .items(items)
                .totalCount(items.size())
                .build();
    }

    private CertificateRequestResponse toResponse(CertificateRequest certRequest) {
        return CertificateRequestResponse.builder()
                .requestId(certRequest.getId())
                .certificateType(certRequest.getCertificateType().name())
                .certificateTypeDisplay(certRequest.getCertificateType().getDisplayName())
                .copies(certRequest.getCopies())
                .purpose(certRequest.getPurpose())
                .status(certRequest.getStatus().name())
                .statusDisplay(certRequest.getStatus().getDisplayName())
                .requestDate(certRequest.getRequestDate())
                .completedDate(certRequest.getCompletedDate())
                .remarks(certRequest.getRemarks())
                .build();
    }
}
