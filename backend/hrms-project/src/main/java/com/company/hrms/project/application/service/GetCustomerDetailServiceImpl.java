package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetCustomerDetailRequest;
import com.company.hrms.project.api.response.GetCustomerDetailResponse;
import com.company.hrms.project.application.service.context.CustomerDetailContext;
import com.company.hrms.project.application.service.task.BuildCustomerDetailResponseTask;
import com.company.hrms.project.application.service.task.LoadCustomerTask;

import lombok.RequiredArgsConstructor;

/**
 * 客戶詳情查詢服務 - Business Pipeline 版本
 */
@Service("getCustomerDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCustomerDetailServiceImpl
                implements QueryApiService<GetCustomerDetailRequest, GetCustomerDetailResponse> {

        private final LoadCustomerTask loadCustomerTask;
        private final BuildCustomerDetailResponseTask buildCustomerDetailResponseTask;

        @Override
        public GetCustomerDetailResponse getResponse(GetCustomerDetailRequest req, JWTModel currentUser, String... args)
                        throws Exception {

                // 1. 建立 Context
                CustomerDetailContext context = new CustomerDetailContext(req.getCustomerId());

                // 2. 執行 Pipeline
                BusinessPipeline.start(context)
                                .next(loadCustomerTask) // Infrastructure Task: 載入客戶
                                .next(buildCustomerDetailResponseTask) // Domain Task: 建構回應
                                .execute();

                // 3. 回傳結果
                return context.getResponse();
        }
}
