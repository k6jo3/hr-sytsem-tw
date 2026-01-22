package com.company.hrms.training.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CertificateExpiringEvent extends DomainEvent {

    private String aggregateId;
    private String aggregateType;

    private String certificateId;
    private String employeeId;
    private String employeeName;
    private String employeeEmail;
    private String certificateName;
    private String issuingOrganization;
    private String expiryDate;
    private Integer daysUntilExpiry;
    private Boolean isRequired;

    public static CertificateExpiringEvent create(
            String certificateId,
            String employeeId,
            String employeeName,
            String employeeEmail,
            String certificateName,
            String issuingOrganization,
            String expiryDate,
            Integer daysUntilExpiry,
            Boolean isRequired) {

        CertificateExpiringEvent event = new CertificateExpiringEvent();
        event.setAggregateId(certificateId);
        event.setAggregateType("Certificate");
        // event.setEventType("CertificateExpiring");

        event.certificateId = certificateId;
        event.employeeId = employeeId;
        event.employeeName = employeeName;
        event.employeeEmail = employeeEmail;
        event.certificateName = certificateName;
        event.issuingOrganization = issuingOrganization;
        event.expiryDate = expiryDate;
        event.daysUntilExpiry = daysUntilExpiry;
        event.isRequired = isRequired != null ? isRequired : false;

        return event;
    }
}
