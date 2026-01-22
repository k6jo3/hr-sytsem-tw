package com.company.hrms.training.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.training.infrastructure.entity.CertificateEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class CertificateQueryRepository extends QueryBaseRepository<CertificateEntity, String> {

    public CertificateQueryRepository(JPAQueryFactory factory) {
        super(factory, CertificateEntity.class);
    }
}
