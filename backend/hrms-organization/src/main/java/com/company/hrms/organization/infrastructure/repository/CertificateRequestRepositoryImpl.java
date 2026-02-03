package com.company.hrms.organization.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.organization.domain.model.entity.CertificateRequest;
import com.company.hrms.organization.domain.model.valueobject.CertificateRequestStatus;
import com.company.hrms.organization.domain.model.valueobject.CertificateType;
import com.company.hrms.organization.domain.repository.ICertificateRequestRepository;
import com.company.hrms.organization.infrastructure.dao.CertificateRequestDAO;
import com.company.hrms.organization.infrastructure.po.CertificateRequestPO;

import lombok.RequiredArgsConstructor;

/**
 * 證明文件申請倉儲實作
 */
@Repository
@RequiredArgsConstructor
public class CertificateRequestRepositoryImpl implements ICertificateRequestRepository {

    private final CertificateRequestDAO certificateRequestDAO;

    @Override
    public Optional<CertificateRequest> findById(UUID id) {
        return certificateRequestDAO.findById(id.toString())
                .map(this::toDomain);
    }

    @Override
    public List<CertificateRequest> findByEmployeeId(UUID employeeId) {
        return certificateRequestDAO.findByEmployeeId(employeeId.toString()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificateRequest> findByEmployeeIdAndStatus(UUID employeeId, CertificateRequestStatus status) {
        return certificateRequestDAO.findByEmployeeIdAndStatus(employeeId.toString(), status.name()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificateRequest> findByStatus(CertificateRequestStatus status) {
        return certificateRequestDAO.findByStatus(status.name()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificateRequest> findByEmployeeIdAndCertificateType(UUID employeeId,
            CertificateType certificateType) {
        return certificateRequestDAO.findByEmployeeIdAndCertificateType(employeeId.toString(), certificateType.name())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(CertificateRequest request) {
        CertificateRequestPO po = toPO(request);
        if (certificateRequestDAO.existsById(request.getId().toString())) {
            certificateRequestDAO.update(po);
        } else {
            certificateRequestDAO.insert(po);
        }
    }

    private CertificateRequest toDomain(CertificateRequestPO po) {
        return CertificateRequest.builder()
                .id(UUID.fromString(po.getId()))
                .employeeId(UUID.fromString(po.getEmployeeId()))
                .certificateType(CertificateType.valueOf(po.getCertificateType()))
                .purpose(po.getPurpose())
                .quantity(po.getCopies())
                .requestDate(po.getRequestDate())
                .status(CertificateRequestStatus.valueOf(po.getStatus()))
                .build();
    }

    private CertificateRequestPO toPO(CertificateRequest request) {
        CertificateRequestPO po = new CertificateRequestPO();
        po.setId(request.getId().toString());
        po.setEmployeeId(request.getEmployeeId().toString());
        po.setCertificateType(request.getCertificateType().name());
        po.setPurpose(request.getPurpose());
        po.setCopies(request.getQuantity());
        po.setRequestDate(request.getRequestDate());
        po.setStatus(request.getStatus().name());
        return po;
    }
}
