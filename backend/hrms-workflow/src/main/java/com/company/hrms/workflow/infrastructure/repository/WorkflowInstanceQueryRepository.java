package com.company.hrms.workflow.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.workflow.infrastructure.entity.WorkflowInstanceEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class WorkflowInstanceQueryRepository extends QueryBaseRepository<WorkflowInstanceEntity, String> {

    public WorkflowInstanceQueryRepository(JPAQueryFactory factory) {
        super(factory, WorkflowInstanceEntity.class);
    }
}
