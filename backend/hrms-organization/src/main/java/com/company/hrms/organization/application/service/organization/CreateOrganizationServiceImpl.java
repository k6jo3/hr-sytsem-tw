package com.company.hrms.organization.application.service.organization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.organization.CreateOrganizationRequest;
import com.company.hrms.organization.api.response.organization.CreateOrganizationResponse;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.application.service.organization.task.CheckOrgCodeExistenceTask;
import com.company.hrms.organization.application.service.organization.task.CreateOrgAggregateTask;
import com.company.hrms.organization.application.service.organization.task.SaveOrgTask;
import com.company.hrms.organization.application.service.organization.task.ValidateParentOrgTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立組織 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: POST /api/v1/organizations
 * </p>
 * 
 * <p>
 * Pipeline 步驟：
 * </p>
 * <ol>
 * <li>CheckOrgCodeExistenceTask - 驗證組織代碼唯一性</li>
 * <li>ValidateParentOrgTask - 驗證母組織存在（條件執行）</li>
 * <li>CreateOrgAggregateTask - 建立組織聚合根</li>
 * <li>SaveOrgTask - 儲存組織</li>
 * </ol>
 */
@Service("createOrganizationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateOrganizationServiceImpl
        implements CommandApiService<CreateOrganizationRequest, CreateOrganizationResponse> {

    private final CheckOrgCodeExistenceTask checkOrgCodeExistenceTask;
    private final ValidateParentOrgTask validateParentOrgTask;
    private final CreateOrgAggregateTask createOrgAggregateTask;
    private final SaveOrgTask saveOrgTask;

    @Override
    public CreateOrganizationResponse execCommand(CreateOrganizationRequest request,
            JWTModel currentUser, String... args) throws Exception {

        log.info("建立組織: code={}, name={}", request.getCode(), request.getName());

        // 1. 建立 Context
        OrganizationContext context = new OrganizationContext(request);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(checkOrgCodeExistenceTask)
                .next(validateParentOrgTask)
                .next(createOrgAggregateTask)
                .next(saveOrgTask)
                .execute();

        // 3. 建立回應
        var organization = context.getOrganization();

        log.info("組織建立成功: id={}, code={}",
                organization.getId().getValue(),
                organization.getCode());

        return CreateOrganizationResponse.success(
                organization.getId().getValue().toString(),
                organization.getCode(),
                organization.getName());
    }
}
