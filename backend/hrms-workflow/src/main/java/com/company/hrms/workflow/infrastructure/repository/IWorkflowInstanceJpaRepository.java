package com.company.hrms.workflow.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.company.hrms.workflow.infrastructure.entity.WorkflowInstanceEntity;

public interface IWorkflowInstanceJpaRepository
        extends JpaRepository<WorkflowInstanceEntity, String>, QuerydslPredicateExecutor<WorkflowInstanceEntity> {
}
