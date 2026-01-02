package com.company.hrms.project.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetMyProjectsRequest;
import com.company.hrms.project.api.response.GetMyProjectsResponse;
import com.company.hrms.project.application.service.context.MyProjectsContext;
import com.company.hrms.project.application.service.task.BuildMyProjectsResponseTask;
import com.company.hrms.project.application.service.task.LoadMyProjectsTask;

import lombok.RequiredArgsConstructor;

/**
 * 我的專案查詢服務 (ESS) - Business Pipeline 版本
 * 
 * 查詢當前使用者參與的專案列表
 * 使用 Business Pipeline 模式進行流程編排
 */
@Service("getMyProjectsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyProjectsServiceImpl implements QueryApiService<GetMyProjectsRequest, GetMyProjectsResponse> {

    private final LoadMyProjectsTask loadMyProjectsTask;
    private final BuildMyProjectsResponseTask buildMyProjectsResponseTask;

    @Override
    public GetMyProjectsResponse getResponse(GetMyProjectsRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 1. 建立 Context
        UUID employeeId = UUID.fromString(currentUser.getUserId());
        int page = req.getPage() != null ? req.getPage() : 0;
        int size = req.getSize() != null ? req.getSize() : 10;

        MyProjectsContext context = new MyProjectsContext(employeeId, page, size);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadMyProjectsTask) // Infrastructure Task: 載入專案
                .next(buildMyProjectsResponseTask) // Domain Task: 建構回應
                .execute();

        // 3. 回傳結果
        return context.getResponse();
    }
}
