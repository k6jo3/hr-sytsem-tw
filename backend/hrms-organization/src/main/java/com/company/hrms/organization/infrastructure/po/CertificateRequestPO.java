package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 證明文件申請持久化對象
 */
@Data
@Entity
@Table(name = "certificate_requests")
public class CertificateRequestPO {

    @Id
    private String id;
    private String employeeId;
    private String certificateType;
    private Integer copies;
    private String purpose;
    private String status;
    private LocalDateTime requestDate;
    private LocalDateTime completedDate;
    private String remarks;

    // 審計欄位
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
