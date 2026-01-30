package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class EmployeeRosterReadModelRepositoryImpl extends QueryBaseRepository<EmployeeRosterReadModel, String> {

    public EmployeeRosterReadModelRepositoryImpl(JPAQueryFactory factory) {
        super(factory, EmployeeRosterReadModel.class);
    }
}
