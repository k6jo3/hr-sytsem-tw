package com.company.hrms.workflow.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.workflow.api.response.PendingTaskResponse;
import com.company.hrms.workflow.domain.repository.IApprovalTaskRepository;
import com.company.hrms.workflow.infrastructure.entity.ApprovalTaskEntity;
import com.company.hrms.workflow.infrastructure.entity.QWorkflowInstanceEntity;
import com.company.hrms.workflow.infrastructure.entity.WorkflowInstanceEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ApprovalTaskRepositoryImpl extends QueryBaseRepository<ApprovalTaskEntity, String>
        implements IApprovalTaskRepository {

    public ApprovalTaskRepositoryImpl(JPAQueryFactory factory) {
        super(factory, ApprovalTaskEntity.class);
    }

    @Override
    public Page<PendingTaskResponse> searchPendingTasks(QueryGroup queryGroup, Pageable pageable) {
        // 1. 初始化 Engine
        UltimateQueryEngine<ApprovalTaskEntity> engine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression predicate = engine.parse(queryGroup);

        // 2. 獲取與 Engine 一致的路徑定義，確保 Alias 統一
        PathBuilder<ApprovalTaskEntity> qTask = engine.getEntityPath();
        QWorkflowInstanceEntity qInst = QWorkflowInstanceEntity.workflowInstanceEntity;

        // 3. 建立基礎查詢並加入必要的 Join
        JPAQuery<ApprovalTaskEntity> query = engine.getQuery();
        // 使用 PathBuilder 進行 join，保證與 Engine 內部邏輯相容
        query.join(qTask.get("workflowInstance", WorkflowInstanceEntity.class), qInst);

        if (predicate != null) {
            query.where(predicate);
        }

        // 4. 計算總數
        UltimateQueryEngine<ApprovalTaskEntity> countEngine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression countPredicate = countEngine.parse(queryGroup);
        JPAQuery<ApprovalTaskEntity> countQuery = countEngine.getQuery();
        countQuery.join(countEngine.getEntityPath().get("workflowInstance", WorkflowInstanceEntity.class), qInst);

        if (countPredicate != null) {
            countQuery.where(countPredicate);
        }

        Long totalCount = countQuery.select(countEngine.getEntityPath().count()).fetchOne();
        long total = totalCount != null ? totalCount : 0L;

        if (total == 0) {
            return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
        }

        // 5. 執行投影查詢 (Projection)
        List<PendingTaskResponse> results = query
                .select(Projections.fields(PendingTaskResponse.class,
                        qTask.get("taskId", String.class).as("taskId"),
                        qInst.instanceId,
                        qTask.get("nodeName", String.class).as("taskName"),
                        qInst.applicantName,
                        qInst.summary,
                        qTask.get("createdAt", java.time.LocalDateTime.class).as("createdAt"),
                        qTask.get("dueDate", java.time.LocalDateTime.class).as("dueDate"),
                        qInst.businessUrl))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qTask.getComparable("createdAt", java.time.LocalDateTime.class).desc())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }
}
