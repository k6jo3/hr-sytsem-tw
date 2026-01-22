package com.company.hrms.training.domain.model.aggregate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.training.domain.event.CertificateAddedEvent;
import com.company.hrms.training.domain.model.valueobject.CertificateId;
import com.company.hrms.training.domain.model.valueobject.CertificateStatus;
import com.company.hrms.training.domain.model.valueobject.CourseCategory;

import lombok.Getter;

@Getter
public class Certificate extends AggregateRoot<CertificateId> {

    private String employeeId;
    private String certificateName;
    private String issuingOrganization;
    private String certificateNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private CourseCategory category;
    private Boolean isRequired;
    private String attachmentUrl;
    private String remarks;

    private Boolean isVerified;
    private String verifiedBy;
    private LocalDateTime verifiedAt;

    private CertificateStatus status;

    private Certificate(CertificateId id) {
        super(id);
    }

    public static Certificate create(
            String employeeId,
            String certificateName,
            String issuingOrganization,
            String certificateNumber,
            LocalDate issueDate,
            LocalDate expiryDate,
            CourseCategory category,
            Boolean isRequired,
            String attachmentUrl,
            String remarks) {

        validateCertificateName(certificateName);
        validateIssueDate(issueDate);
        validateDates(issueDate, expiryDate);

        Certificate cert = new Certificate(CertificateId.create());
        cert.employeeId = employeeId;
        cert.certificateName = certificateName;
        cert.issuingOrganization = issuingOrganization;
        cert.certificateNumber = certificateNumber;
        cert.issueDate = issueDate;
        cert.expiryDate = expiryDate;
        cert.category = category;
        cert.isRequired = isRequired != null ? isRequired : false;
        cert.attachmentUrl = attachmentUrl;
        cert.remarks = remarks;

        cert.isVerified = false;
        cert.status = determineStatus(expiryDate);
        cert.createdAt = LocalDateTime.now();

        cert.registerEvent(CertificateAddedEvent.create(
                cert.getId().toString(),
                employeeId,
                certificateName,
                issuingOrganization,
                certificateNumber,
                issueDate.toString(),
                expiryDate != null ? expiryDate.toString() : null,
                isRequired));

        return cert;
    }

    public static Certificate reconstitute(
            CertificateId id,
            String employeeId,
            String certificateName,
            String issuingOrganization,
            String certificateNumber,
            LocalDate issueDate,
            LocalDate expiryDate,
            CourseCategory category,
            Boolean isRequired,
            String attachmentUrl,
            String remarks,
            Boolean isVerified,
            String verifiedBy,
            LocalDateTime verifiedAt,
            CertificateStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        Certificate cert = new Certificate(id);
        cert.employeeId = employeeId;
        cert.certificateName = certificateName;
        cert.issuingOrganization = issuingOrganization;
        cert.certificateNumber = certificateNumber;
        cert.issueDate = issueDate;
        cert.expiryDate = expiryDate;
        cert.category = category;
        cert.isRequired = isRequired;
        cert.attachmentUrl = attachmentUrl;
        cert.remarks = remarks;
        cert.isVerified = isVerified;
        cert.verifiedBy = verifiedBy;
        cert.verifiedAt = verifiedAt;
        cert.status = status;
        cert.createdAt = createdAt;
        cert.updatedAt = updatedAt;

        return cert;
    }

    public void update(
            String certificateNumber,
            LocalDate issueDate,
            LocalDate expiryDate,
            String attachmentUrl,
            String remarks) {

        if (issueDate != null) {
            this.issueDate = issueDate;
        }

        if (expiryDate != null) {
            this.expiryDate = expiryDate;
        }

        validateDates(this.issueDate, this.expiryDate);

        if (certificateNumber != null) {
            this.certificateNumber = certificateNumber;
        }

        if (attachmentUrl != null) {
            this.attachmentUrl = attachmentUrl;
        }

        if (remarks != null) {
            this.remarks = remarks;
        }

        this.status = determineStatus(this.expiryDate);
        this.touch();

        // 重置驗證狀態? 規格未詳述，通常更新後需重新驗證，這裡暫時不重置
    }

    public void verify(String verifierId) {
        this.isVerified = true;
        this.verifiedBy = verifierId;
        this.verifiedAt = LocalDateTime.now();
        this.touch();
    }

    public long getDaysUntilExpiry() {
        if (this.expiryDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), this.expiryDate);
    }

    // === Private Methods ===

    private static void validateCertificateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("證照名稱不能為空");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("證照名稱過長");
        }
    }

    private static void validateIssueDate(LocalDate issueDate) {
        if (issueDate == null) {
            throw new IllegalArgumentException("發證日期不能為空");
        }
    }

    private static void validateDates(LocalDate issueDate, LocalDate expiryDate) {
        if (expiryDate != null && issueDate != null && expiryDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("到期日必須晚於發證日");
        }
    }

    private static CertificateStatus determineStatus(LocalDate expiryDate) {
        if (expiryDate == null) {
            return CertificateStatus.VALID;
        }

        LocalDate now = LocalDate.now();
        if (expiryDate.isBefore(now)) {
            return CertificateStatus.EXPIRED;
        }

        if (ChronoUnit.DAYS.between(now, expiryDate) <= 90) {
            return CertificateStatus.EXPIRING;
        }

        return CertificateStatus.VALID;
    }
}
