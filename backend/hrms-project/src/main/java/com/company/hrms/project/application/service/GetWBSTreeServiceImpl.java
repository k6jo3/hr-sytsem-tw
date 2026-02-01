package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetWBSTreeRequest;
import com.company.hrms.project.api.response.GetWBSTreeResponse;
import com.company.hrms.project.application.service.context.WBSTreeContext;
import com.company.hrms.project.application.service.task.BuildWBSTreeTask;
import com.company.hrms.project.application.service.task.LoadProjectTasksTask;

import lombok.RequiredArgsConstructor;

@Service("getWBSTreeServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetWBSTreeServiceImpl implements QueryApiService<GetWBSTreeRequest, GetWBSTreeResponse> {

    private final LoadProjectTasksTask loadProjectTasksTask;
    private final BuildWBSTreeTask buildWBSTreeTask;

    @Override
    public GetWBSTreeResponse getResponse(GetWBSTreeRequest req, JWTModel currentUser, String... args)
            throws Exception {

        String projectIdStr = (args.length > 0 && args[0] != null) ? args[0] : req.getProjectId();

        WBSTreeContext context = new WBSTreeContext(req, projectIdStr);

        BusinessPipeline.start(context)
                .next(loadProjectTasksTask)
                .next(buildWBSTreeTask)
                .execute();

        return context.getResponse();
    }
}
