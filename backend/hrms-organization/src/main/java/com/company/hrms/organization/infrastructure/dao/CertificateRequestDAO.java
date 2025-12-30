package com.company.hrms.organization.infrastructure.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.organization.infrastructure.mapper.CertificateRequestMapper;
import com.company.hrms.organization.infrastructure.po.CertificateRequestPO;

import lombok.RequiredArgsConstructor;

/**
 * 證明文件申請 DAO
 */
@Repository
@RequiredArgsConstructor
public class CertificateRequestDAO {

    private final CertificateRequestMapper certificateRequestMapper;

    public Optional<CertificateRequestPO> findById(String id) {
        return Optional.ofNullable(certificateRequestMapper.selectById(id));
    }

    public List<CertificateRequestPO> findByEmployeeId(String employeeId) {
        return certificateRequestMapper.selectByEmployeeId(employeeId);
    }

    public List<CertificateRequestPO> findByStatus(String status) {
        return certificateRequestMapper.selectByStatus(status);
    }

    public List<CertificateRequestPO> findByCertificateType(String certificateType) {
        return certificateRequestMapper.selectByCertificateType(certificateType);
    }

    public List<CertificateRequestPO> findPendingRequests() {
        return certificateRequestMapper.selectPendingRequests();
    }

    public void insert(CertificateRequestPO request) {
        certificateRequestMapper.insert(request);
    }

    public void update(CertificateRequestPO request) {
        certificateRequestMapper.update(request);
    }

    public void deleteById(String id) {
        certificateRequestMapper.deleteById(id);
    }

    public List<CertificateRequestPO> findByEmployeeIdAndStatus(String employeeId, String status) {
        return certificateRequestMapper.selectByEmployeeIdAndStatus(employeeId, status);
    }

    public List<CertificateRequestPO> findByEmployeeIdAndCertificateType(String employeeId, String certificateType) {
        return certificateRequestMapper.selectByEmployeeIdAndCertificateType(employeeId, certificateType);
    }

    public boolean existsById(String id) {
        return certificateRequestMapper.existsById(id);
    }
}
