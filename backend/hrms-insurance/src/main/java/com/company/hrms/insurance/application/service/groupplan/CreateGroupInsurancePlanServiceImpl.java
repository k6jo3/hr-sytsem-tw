package com.company.hrms.insurance.application.service.groupplan;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.insurance.api.request.CreateGroupInsurancePlanRequest;
import com.company.hrms.insurance.api.response.GroupInsurancePlanDetailResponse;
import com.company.hrms.insurance.application.factory.GroupInsurancePlanDtoFactory;
import com.company.hrms.insurance.application.service.groupplan.context.CreateGroupPlanContext;
import com.company.hrms.insurance.application.service.groupplan.task.CreateAndSaveGroupPlanTask;
import com.company.hrms.insurance.application.service.groupplan.task.ValidatePlanCodeUniqueTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立團體保險方案服務實作
 */
@Service("createGroupInsurancePlanServiceImpl")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CreateGroupInsurancePlanServiceImpl
        implements CommandApiService<CreateGroupInsurancePlanRequest, GroupInsurancePlanDetailResponse> {

    private final ValidatePlanCodeUniqueTask validatePlanCodeUniqueTask;
    private final CreateAndSaveGroupPlanTask createAndSaveGroupPlanTask;
    private final GroupInsurancePlanDtoFactory dtoFactory;

    @Override
    public GroupInsurancePlanDetailResponse execCommand(
            CreateGroupInsurancePlanRequest request, JWTModel currentUser, String... args) throws Exception {

        log.info("建立團體保險方案: planCode={}, type={}",
                request.getPlanCode(), request.getInsuranceType());

        // 建立 Context
        CreateGroupPlanContext context = new CreateGroupPlanContext(request);

        // 執行 Pipeline
        BusinessPipeline.start(context)
                .next(validatePlanCodeUniqueTask)
                .next(createAndSaveGroupPlanTask)
                .execute();

        // 回傳結果
        return dtoFactory.toDetailResponse(context.getPlan());
    }
}
