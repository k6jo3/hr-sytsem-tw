package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 證明文件申請持久化對象
 */
@Data
public class CertificateRequestPO {

    private UUID requestId;
    private UUID employeeId;
    private String certificateType;
    private String purpose;
    private Integer quantity;
    private LocalDateTime requestDate;
    private String status;
    private UUID processedBy;
    private LocalDateTime processedAt;
    private String documentUrl;
}
