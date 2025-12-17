package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.entity.CertificateRequest;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.ICertificateRequestRepository;
import com.company.hrms.organization.infrastructure.dao.CertificateRequestDAO;
import com.company.hrms.organization.infrastructure.po.CertificateRequestPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 證明文件申請倉儲實作
 */
@Repository
@RequiredArgsConstructor
public class CertificateRequestRepositoryImpl implements ICertificateRequestRepository {

    private final CertificateRequestDAO certificateRequestDAO;

    @Override
    public Optional<CertificateRequest> findById(String id) {
        return certificateRequestDAO.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<CertificateRequest> findByEmployeeId(EmployeeId employeeId) {
        return certificateRequestDAO.findByEmployeeId(employeeId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificateRequest> findPendingRequests() {
        return certificateRequestDAO.findPendingRequests().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(CertificateRequest request) {
        CertificateRequestPO po = toPO(request);
        if (certificateRequestDAO.existsById(request.getId())) {
            po.setUpdatedAt(LocalDateTime.now());
            certificateRequestDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            certificateRequestDAO.insert(po);
        }
    }

    @Override
    public void delete(String id) {
        certificateRequestDAO.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return certificateRequestDAO.existsById(id);
    }

    private CertificateRequest toDomain(CertificateRequestPO po) {
        return CertificateRequest.reconstitute(
                po.getId(),
                new EmployeeId(po.getEmployeeId()),
                CertificateType.valueOf(po.getCertificateType()),
                po.getCopies(),
                po.getPurpose(),
                CertificateRequestStatus.valueOf(po.getStatus()),
                po.getRequestDate(),
                po.getCompletedDate(),
                po.getRemarks()
        );
    }

    private CertificateRequestPO toPO(CertificateRequest request) {
        CertificateRequestPO po = new CertificateRequestPO();
        po.setId(request.getId());
        po.setEmployeeId(request.getEmployeeId().getValue());
        po.setCertificateType(request.getCertificateType().name());
        po.setCopies(request.getCopies());
        po.setPurpose(request.getPurpose());
        po.setStatus(request.getStatus().name());
        po.setRequestDate(request.getRequestDate());
        po.setCompletedDate(request.getCompletedDate());
        po.setRemarks(request.getRemarks());
        return po;
    }
}
