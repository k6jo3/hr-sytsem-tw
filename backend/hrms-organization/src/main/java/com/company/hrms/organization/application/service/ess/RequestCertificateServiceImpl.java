package com.company.hrms.organization.application.service.ess;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.ess.RequestCertificateRequest;
import com.company.hrms.organization.api.response.ess.CertificateRequestResponse;
import com.company.hrms.organization.domain.event.CertificateRequestedEvent;
import com.company.hrms.organization.domain.model.entity.CertificateRequest;
import com.company.hrms.organization.domain.model.valueobject.CertificateType;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.ICertificateRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 申請證明文件服務實作
 */
@Service("requestCertificateServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RequestCertificateServiceImpl
        implements CommandApiService<RequestCertificateRequest, CertificateRequestResponse> {

    private final ICertificateRequestRepository certificateRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public CertificateRequestResponse execCommand(RequestCertificateRequest request,
                                                  JWTModel currentUser,
                                                  String... args) throws Exception {
        log.info("Requesting certificate for user: {}", currentUser.getUserId());

        String employeeId = currentUser.getEmployeeId();
        if (employeeId == null) {
            throw new IllegalStateException("使用者未關聯員工資料");
        }

        // 建立申請
        CertificateRequest certRequest = CertificateRequest.create(
                new EmployeeId(employeeId),
                CertificateType.valueOf(request.getCertificateType()),
                request.getCopies(),
                request.getPurpose(),
                request.getRemarks()
        );

        // 儲存申請
        certificateRequestRepository.save(certRequest);

        // 發布領域事件
        eventPublisher.publishEvent(new CertificateRequestedEvent(
                certRequest.getId(),
                employeeId,
                request.getCertificateType(),
                request.getCopies()
        ));

        log.info("Certificate request created: {}", certRequest.getId());

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
