package com.company.hrms.training.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CertificateAddedEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String certificateId;
    private String employeeId;
    private String certificateName;
    private String issuingOrganization;
    private String certificateNumber;
    private String issueDate;
    private String expiryDate;
    private Boolean isRequired;

    public static CertificateAddedEvent create(
            String certificateId,
            String employeeId,
            String certificateName,
            String issuingOrganization,
            String certificateNumber,
            String issueDate,
            String expiryDate,
            Boolean isRequired) {

        CertificateAddedEvent event = new CertificateAddedEvent();
        event.setAggregateId(certificateId);
        event.setAggregateType("Certificate");
        // event.setEventType("CertificateAdded");

        event.certificateId = certificateId;
        event.employeeId = employeeId;
        event.certificateName = certificateName;
        event.issuingOrganization = issuingOrganization;
        event.certificateNumber = certificateNumber;
        event.issueDate = issueDate;
        event.expiryDate = expiryDate;
        event.isRequired = isRequired != null ? isRequired : false;

        return event;
    }
}
