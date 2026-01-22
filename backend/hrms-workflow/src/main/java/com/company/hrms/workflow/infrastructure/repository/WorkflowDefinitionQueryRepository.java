package com.company.hrms.workflow.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.workflow.infrastructure.entity.WorkflowDefinitionEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class WorkflowDefinitionQueryRepository extends QueryBaseRepository<WorkflowDefinitionEntity, String> {

    public WorkflowDefinitionQueryRepository(JPAQueryFactory factory) {
        super(factory, WorkflowDefinitionEntity.class);
    }
}
