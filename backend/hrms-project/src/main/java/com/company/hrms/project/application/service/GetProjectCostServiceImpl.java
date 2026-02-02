package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetProjectCostRequest;
import com.company.hrms.project.api.response.GetProjectCostResponse;
import com.company.hrms.project.application.service.context.ProjectCostContext;
import com.company.hrms.project.application.service.task.CalculateProjectCostTask;
import com.company.hrms.project.application.service.task.LoadProjectForCostTask;

import lombok.RequiredArgsConstructor;

/**
 * 查詢專案成本分析服務
 */
@Service("getProjectCostServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProjectCostServiceImpl implements QueryApiService<GetProjectCostRequest, GetProjectCostResponse> {

        private final LoadProjectForCostTask loadProjectForCostTask;
        private final CalculateProjectCostTask calculateProjectCostTask;

        @Override
        public GetProjectCostResponse getResponse(GetProjectCostRequest req, JWTModel currentUser, String... args)
                        throws Exception {

                ProjectCostContext context = new ProjectCostContext(req);

                BusinessPipeline.start(context)
                                .next(loadProjectForCostTask)
                                .next(calculateProjectCostTask)
                                .execute();

                return context.getResponse();
        }
}
