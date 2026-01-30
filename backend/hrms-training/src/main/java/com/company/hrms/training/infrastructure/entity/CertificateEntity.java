package com.company.hrms.training.infrastructure.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.training.domain.model.valueobject.CertificateStatus;
import com.company.hrms.training.domain.model.valueobject.CourseCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "certificates")
@Getter
@Setter
public class CertificateEntity {

    @Id
    @Column(name = "certificate_id")
    private String certificateId;

    @Column(name = "employee_id")
    private String employee_id;

    @Column(name = "certificate_name")
    private String certificateName;

    @Column(name = "issuing_organization")
    private String issuingOrganization;

    @Column(name = "certificate_number")
    private String certificateNumber;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private CourseCategory category;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CertificateStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Integer is_deleted = 0;
}
