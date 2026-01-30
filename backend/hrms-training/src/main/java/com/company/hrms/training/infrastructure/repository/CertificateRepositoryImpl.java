package com.company.hrms.training.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBaseRepository;
import com.company.hrms.training.domain.model.aggregate.Certificate;
import com.company.hrms.training.domain.model.valueobject.CertificateId;
import com.company.hrms.training.domain.model.valueobject.CertificateStatus;
import com.company.hrms.training.domain.repository.ICertificateRepository;
import com.company.hrms.training.infrastructure.entity.CertificateEntity;
import com.company.hrms.training.infrastructure.entity.QCertificateEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CertificateRepositoryImpl
        extends CommandBaseRepository<CertificateEntity, String>
        implements ICertificateRepository {

    private final EventPublisher eventPublisher;

    public CertificateRepositoryImpl(JPAQueryFactory factory, EventPublisher eventPublisher) {
        super(factory, CertificateEntity.class);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Certificate save(Certificate certificate) {
        CertificateEntity entity = toEntity(certificate);
        super.save(entity);

        certificate.getDomainEvents().forEach(eventPublisher::publish);
        certificate.clearDomainEvents();

        return certificate;
    }

    @Override
    public Optional<Certificate> findById(CertificateId id) {
        return super.findById(id.toString()).map(this::toDomain);
    }

    @Override
    public void delete(Certificate certificate) {
        CertificateEntity entity = toEntity(certificate);
        super.delete(entity);
    }

    @Override
    public List<Certificate> findExpiringCertificates(LocalDate thresholdDate) {
        QCertificateEntity qCert = QCertificateEntity.certificateEntity;

        List<CertificateEntity> entities = factory.selectFrom(qCert)
                .where(qCert.expiryDate.loe(thresholdDate)
                        .and(qCert.status.ne(CertificateStatus.EXPIRED))
                        .and(qCert.status.ne(CertificateStatus.REVOKED)))
                .fetch();

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private CertificateEntity toEntity(Certificate domain) {
        CertificateEntity entity = new CertificateEntity();
        entity.setCertificateId(domain.getId().toString());
        entity.setEmployee_id(domain.getEmployeeId());
        entity.setCertificateName(domain.getCertificateName());
        entity.setIssuingOrganization(domain.getIssuingOrganization());
        entity.setCertificateNumber(domain.getCertificateNumber());
        entity.setIssueDate(domain.getIssueDate());
        entity.setExpiryDate(domain.getExpiryDate());
        entity.setCategory(domain.getCategory());
        entity.setIsRequired(domain.getIsRequired());
        entity.setAttachmentUrl(domain.getAttachmentUrl());
        entity.setRemarks(domain.getRemarks());
        entity.setIsVerified(domain.getIsVerified());
        entity.setVerifiedBy(domain.getVerifiedBy());
        entity.setVerifiedAt(domain.getVerifiedAt());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Certificate toDomain(CertificateEntity entity) {
        return Certificate.reconstitute(
                CertificateId.from(entity.getCertificateId()),
                entity.getEmployee_id(),
                entity.getCertificateName(),
                entity.getIssuingOrganization(),
                entity.getCertificateNumber(),
                entity.getIssueDate(),
                entity.getExpiryDate(),
                entity.getCategory(),
                entity.getIsRequired(),
                entity.getAttachmentUrl(),
                entity.getRemarks(),
                entity.getIsVerified(),
                entity.getVerifiedBy(),
                entity.getVerifiedAt(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
