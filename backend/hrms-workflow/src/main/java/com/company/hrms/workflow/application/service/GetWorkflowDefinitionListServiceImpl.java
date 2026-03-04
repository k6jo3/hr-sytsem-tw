package com.company.hrms.workflow.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetWorkflowDefinitionListRequest;
import com.company.hrms.workflow.api.response.WorkflowDefinitionResponse;
import com.company.hrms.workflow.application.service.context.GetWorkflowDefinitionListContext;
import com.company.hrms.workflow.application.service.task.definition.FetchWorkflowDefinitionListTask;
import com.company.hrms.workflow.application.service.task.definition.TransformWorkflowDefinitionListResponseTask;

import lombok.RequiredArgsConstructor;

@Service("getDefinitionsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetWorkflowDefinitionListServiceImpl
                implements QueryApiService<GetWorkflowDefinitionListRequest, Page<WorkflowDefinitionResponse>> {

        private final FetchWorkflowDefinitionListTask fetchTask;
        private final TransformWorkflowDefinitionListResponseTask transformTask;

        @Override
        public Page<WorkflowDefinitionResponse> getResponse(GetWorkflowDefinitionListRequest req, JWTModel currentUser,
                        String... args) throws Exception {

                // 1. Prepare Pagination (Default sort by createdAt desc)
                Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
                if (args != null && args.length >= 2) {
                        try {
                                int page = Integer.parseInt(args[0]);
                                int size = Integer.parseInt(args[1]);
                                pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                        } catch (NumberFormatException e) {
                                // Fallback to default if args are not numbers
                        }
                }

                // 2. Setup Context
                GetWorkflowDefinitionListContext context = new GetWorkflowDefinitionListContext(req, currentUser,
                                pageable);

                // 3. Execute Pipeline
                BusinessPipeline.start(context)
                                .next(fetchTask)
                                .next(transformTask)
                                .execute();

                return context.getResponse();
        }
}
