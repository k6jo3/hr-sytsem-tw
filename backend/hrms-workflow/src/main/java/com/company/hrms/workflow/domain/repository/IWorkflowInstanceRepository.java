package com.company.hrms.workflow.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowInstanceId;

public interface IWorkflowInstanceRepository {
        WorkflowInstance save(WorkflowInstance instance);

        Optional<WorkflowInstance> findById(WorkflowInstanceId id);

        // Add other domain methods used by existing code if any. e.g. saveAll
        void saveAll(List<WorkflowInstance> instances);

        boolean existsByBusinessIdAndType(String businessId, String businessType);

        /**
         * 分頁查詢流程實例
         */
        Page<WorkflowInstance> search(QueryGroup queryGroup, Pageable pageable);
}
