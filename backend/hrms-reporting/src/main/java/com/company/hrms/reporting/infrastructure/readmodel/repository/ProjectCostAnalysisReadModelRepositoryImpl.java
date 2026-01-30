package com.company.hrms.reporting.infrastructure.readmodel.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.reporting.infrastructure.readmodel.ProjectCostAnalysisReadModel;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ProjectCostAnalysisReadModelRepositoryImpl
        extends QueryBaseRepository<ProjectCostAnalysisReadModel, String> {

    public ProjectCostAnalysisReadModelRepositoryImpl(JPAQueryFactory factory) {
        super(factory, ProjectCostAnalysisReadModel.class);
    }
}
