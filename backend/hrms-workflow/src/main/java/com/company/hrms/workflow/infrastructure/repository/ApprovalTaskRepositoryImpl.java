package com.company.hrms.workflow.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.workflow.api.response.PendingTaskResponse;
import com.company.hrms.workflow.domain.repository.IApprovalTaskRepository;
import com.company.hrms.workflow.infrastructure.entity.ApprovalTaskEntity;
import com.company.hrms.workflow.infrastructure.entity.QApprovalTaskEntity;
import com.company.hrms.workflow.infrastructure.entity.QWorkflowInstanceEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ApprovalTaskRepositoryImpl implements IApprovalTaskRepository {

    private final JPAQueryFactory factory;

    @Override
    public Page<PendingTaskResponse> searchPendingTasks(QueryGroup queryGroup, Pageable pageable) {

        // Setup Engine
        UltimateQueryEngine<ApprovalTaskEntity> engine = new UltimateQueryEngine<>(factory, ApprovalTaskEntity.class);

        // Parse QueryGroup to Predicate
        BooleanExpression predicate = engine.parse(queryGroup);

        // Get Base Query (managed by Engine)
        JPAQuery<ApprovalTaskEntity> query = engine.getQuery();

        // Manual Join for Projection data (WorkflowInstance)
        QApprovalTaskEntity qTask = QApprovalTaskEntity.approvalTaskEntity;
        QWorkflowInstanceEntity qInst = QWorkflowInstanceEntity.workflowInstanceEntity;

        // Careful: engine uses PathBuilder, we use Q-Class. They should alias to same
        // if default naming used.
        // QApprovalTaskEntity variable is "approvalTaskEntity".
        // UltimateQueryEngine (Introspector.decapitalize) -> "approvalTaskEntity".
        // So mixing them *should* work if we join on the right alias.
        // However, safest is to use the query from engine but add join using Q-types
        // IF they match the alias in engine.

        // Engine's root path:
        // PathBuilder<ApprovalTaskEntity> entityPath = new
        // PathBuilder<>(ApprovalTaskEntity.class, "approvalTaskEntity");

        // QApprovalTaskEntity.approvalTaskEntity variable name is "approvalTaskEntity".
        // So we can assume qTask refers to the same root.

        query.join(qTask.workflowInstance, qInst);

        // Apply Where
        if (predicate != null) {
            query.where(predicate);
        }

        // Calculate Total
        // Calculate Total
        // Use a separate execution for count to avoid deprecated fetchCount() and state
        // mutation
        UltimateQueryEngine<ApprovalTaskEntity> countEngine = new UltimateQueryEngine<>(factory,
                ApprovalTaskEntity.class);
        BooleanExpression countPredicate = countEngine.parse(queryGroup);
        JPAQuery<ApprovalTaskEntity> countQuery = countEngine.getQuery();

        // Re-apply manual join
        countQuery.join(qTask.workflowInstance, qInst);

        if (countPredicate != null) {
            countQuery.where(countPredicate);
        }

        Long totalCount = countQuery.select(qTask.count()).fetchOne();
        long total = totalCount != null ? totalCount : 0L;

        // Fetch Data with Projection
        List<PendingTaskResponse> results = query
                .select(Projections.fields(PendingTaskResponse.class,
                        qTask.taskId,
                        qInst.instanceId,
                        qTask.nodeName.as("taskName"),
                        qInst.applicantName,
                        qInst.summary,
                        qTask.createdAt,
                        qTask.dueDate,
                        qInst.businessUrl))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qTask.createdAt.desc())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }
}
