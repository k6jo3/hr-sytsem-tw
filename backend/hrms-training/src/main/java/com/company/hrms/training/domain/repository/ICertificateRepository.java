package com.company.hrms.training.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.company.hrms.training.domain.model.aggregate.Certificate;
import com.company.hrms.training.domain.model.valueobject.CertificateId;

public interface ICertificateRepository {

    Certificate save(Certificate certificate);

    Optional<Certificate> findById(CertificateId id);

    void delete(Certificate certificate);

    List<Certificate> findExpiringCertificates(LocalDate thresholdDate);
}
