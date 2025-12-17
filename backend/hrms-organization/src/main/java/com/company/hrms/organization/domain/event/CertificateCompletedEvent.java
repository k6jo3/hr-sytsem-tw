package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.util.UUID;

/**
 * 證明文件完成事件
 * 觸發時機: 證明文件完成
 * 訂閱服務: Notification
 */
@Getter
public class CertificateCompletedEvent extends DomainEvent {

    private final UUID requestId;
    private final UUID employeeId;
    private final String employeeName;
    private final String certificateType;
    private final String documentUrl;

    public CertificateCompletedEvent(UUID requestId, UUID employeeId, String employeeName,
                                      String certificateType, String documentUrl) {
        super();
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.certificateType = certificateType;
        this.documentUrl = documentUrl;
    }
}
