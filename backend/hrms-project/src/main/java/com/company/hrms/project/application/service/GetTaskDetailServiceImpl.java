package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetTaskDetailRequest;
import com.company.hrms.project.api.response.GetTaskDetailResponse;
import com.company.hrms.project.application.service.context.TaskDetailContext;
import com.company.hrms.project.application.service.task.BuildTaskDetailResponseTask;
import com.company.hrms.project.application.service.task.LoadTaskTask;

import lombok.RequiredArgsConstructor;

/**
 * 工項詳情查詢服務 - Business Pipeline 版本
 */
@Service("getTaskDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTaskDetailServiceImpl implements QueryApiService<GetTaskDetailRequest, GetTaskDetailResponse> {

    private final LoadTaskTask loadTaskTask;
    private final BuildTaskDetailResponseTask buildTaskDetailResponseTask;

    @Override
    public GetTaskDetailResponse getResponse(GetTaskDetailRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 建立 Context
        TaskDetailContext context = new TaskDetailContext(req.getTaskId());

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadTaskTask) // Infrastructure Task: 載入工項
                .next(buildTaskDetailResponseTask) // Domain Task: 建構回應
                .execute();

        // 3. 回傳結果
        return context.getResponse();
    }
}
