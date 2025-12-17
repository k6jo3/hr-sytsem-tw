package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.util.UUID;

/**
 * 證明文件申請事件
 * 觸發時機: 證明文件申請
 * 訂閱服務: Notification
 */
@Getter
public class CertificateRequestedEvent extends DomainEvent {

    private final UUID requestId;
    private final UUID employeeId;
    private final String employeeName;
    private final String certificateType;
    private final String purpose;
    private final Integer quantity;

    public CertificateRequestedEvent(UUID requestId, UUID employeeId, String employeeName,
                                      String certificateType, String purpose, Integer quantity) {
        super();
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.certificateType = certificateType;
        this.purpose = purpose;
        this.quantity = quantity;
    }
}
