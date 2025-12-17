package com.company.hrms.organization.domain.repository;

import com.company.hrms.organization.domain.model.entity.CertificateRequest;
import com.company.hrms.organization.domain.model.valueobject.CertificateRequestStatus;
import com.company.hrms.organization.domain.model.valueobject.CertificateType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 證明文件申請 Repository 介面
 */
public interface ICertificateRequestRepository {

    /**
     * 依 ID 查詢
     * @param id 申請 ID
     * @return 申請
     */
    Optional<CertificateRequest> findById(UUID id);

    /**
     * 依員工 ID 查詢申請
     * @param employeeId 員工 ID
     * @return 申請列表
     */
    List<CertificateRequest> findByEmployeeId(UUID employeeId);

    /**
     * 依員工 ID 和狀態查詢
     * @param employeeId 員工 ID
     * @param status 狀態
     * @return 申請列表
     */
    List<CertificateRequest> findByEmployeeIdAndStatus(UUID employeeId, CertificateRequestStatus status);

    /**
     * 依狀態查詢所有申請
     * @param status 狀態
     * @return 申請列表
     */
    List<CertificateRequest> findByStatus(CertificateRequestStatus status);

    /**
     * 依證明文件類型查詢
     * @param employeeId 員工 ID
     * @param certificateType 證明文件類型
     * @return 申請列表
     */
    List<CertificateRequest> findByEmployeeIdAndCertificateType(UUID employeeId, CertificateType certificateType);

    /**
     * 儲存申請
     * @param request 申請
     */
    void save(CertificateRequest request);
}
