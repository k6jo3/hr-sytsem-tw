package com.company.hrms.organization.domain.model.entity;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.CertificateRequestStatus;
import com.company.hrms.organization.domain.model.valueobject.CertificateType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 證明文件申請實體
 */
@Getter
@Builder
public class CertificateRequest {

    /**
     * 申請 ID
     */
    private final UUID id;

    /**
     * 員工 ID
     */
    private UUID employeeId;

    /**
     * 證明文件類型
     */
    private CertificateType certificateType;

    /**
     * 申請目的
     */
    private String purpose;

    /**
     * 份數
     */
    private Integer quantity;

    /**
     * 申請時間
     */
    private LocalDateTime requestDate;

    /**
     * 申請狀態
     */
    private CertificateRequestStatus status;

    /**
     * 處理者 ID
     */
    private UUID processedBy;

    /**
     * 處理時間
     */
    private LocalDateTime processedAt;

    /**
     * 文件 URL
     */
    private String documentUrl;

    // ==================== 工廠方法 ====================

    /**
     * 建立證明文件申請
     * @param employeeId 員工 ID
     * @param certificateType 證明文件類型
     * @param purpose 申請目的
     * @param quantity 份數
     * @return 新的申請實例
     */
    public static CertificateRequest create(
            UUID employeeId,
            CertificateType certificateType,
            String purpose,
            Integer quantity) {

        if (employeeId == null) {
            throw new DomainException("EMPLOYEE_ID_REQUIRED", "員工 ID 不可為空");
        }
        if (certificateType == null) {
            throw new DomainException("CERTIFICATE_TYPE_REQUIRED", "證明文件類型不可為空");
        }

        return CertificateRequest.builder()
                .id(UUID.randomUUID())
                .employeeId(employeeId)
                .certificateType(certificateType)
                .purpose(purpose)
                .quantity(quantity != null && quantity > 0 ? quantity : 1)
                .requestDate(LocalDateTime.now())
                .status(CertificateRequestStatus.PENDING)
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 核准申請
     * @param processedBy 處理者 ID
     */
    public void approve(UUID processedBy) {
        if (this.status != CertificateRequestStatus.PENDING) {
            throw new DomainException("INVALID_STATUS", "只有待處理的申請可以核准");
        }
        this.status = CertificateRequestStatus.APPROVED;
        this.processedBy = processedBy;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 拒絕申請
     * @param processedBy 處理者 ID
     */
    public void reject(UUID processedBy) {
        if (this.status != CertificateRequestStatus.PENDING) {
            throw new DomainException("INVALID_STATUS", "只有待處理的申請可以拒絕");
        }
        this.status = CertificateRequestStatus.REJECTED;
        this.processedBy = processedBy;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 完成處理
     * @param documentUrl 文件 URL
     */
    public void complete(String documentUrl) {
        if (this.status != CertificateRequestStatus.APPROVED) {
            throw new DomainException("NOT_APPROVED", "只有已核准的申請可以完成");
        }
        this.status = CertificateRequestStatus.COMPLETED;
        this.documentUrl = documentUrl;
    }

    // ==================== 查詢方法 ====================

    /**
     * 是否可取消
     * @return 是否可取消
     */
    public boolean isCancellable() {
        return this.status.isCancellable();
    }

    /**
     * 是否已完成
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return this.status == CertificateRequestStatus.COMPLETED;
    }

    /**
     * 取得證明文件類型顯示名稱
     * @return 顯示名稱
     */
    public String getCertificateTypeDisplayName() {
        return this.certificateType.getDisplayName();
    }

    /**
     * 取得狀態顯示名稱
     * @return 顯示名稱
     */
    public String getStatusDisplayName() {
        return this.status.getDisplayName();
    }
}
