package com.company.hrms.organization.application.service.organization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.response.organization.OrganizationDetailResponse;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.application.service.organization.task.CheckNoEmployeesTask;
import com.company.hrms.organization.application.service.organization.task.DeactivateOrgTask;
import com.company.hrms.organization.application.service.organization.task.LoadOrgTask;
import com.company.hrms.organization.application.service.organization.task.SaveOrgTask;
import com.company.hrms.organization.domain.model.aggregate.Organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 停用組織 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: PUT /api/v1/organizations/{id}/deactivate
 * </p>
 * 
 * <p>
 * Pipeline 步驟：
 * </p>
 * <ol>
 * <li>LoadOrgTask - 載入組織</li>
 * <li>CheckNoEmployeesTask - 檢查無員工</li>
 * <li>DeactivateOrgTask - 停用組織</li>
 * <li>SaveOrgTask - 儲存組織</li>
 * </ol>
 */
@Service("deactivateOrganizationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeactivateOrganizationServiceImpl
        implements CommandApiService<Object, OrganizationDetailResponse> {

    private final LoadOrgTask loadOrgTask;
    private final CheckNoEmployeesTask checkNoEmployeesTask;
    private final DeactivateOrgTask deactivateOrgTask;
    private final SaveOrgTask saveOrgTask;

    @Override
    public OrganizationDetailResponse execCommand(Object request,
            JWTModel currentUser, String... args) throws Exception {

        String organizationId = args[0];
        log.info("停用組織: id={}", organizationId);

        // 1. 建立 Context
        OrganizationContext context = new OrganizationContext(organizationId);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadOrgTask)
                .next(checkNoEmployeesTask)
                .next(deactivateOrgTask)
                .next(saveOrgTask)
                .execute();

        // 3. 建立回應
        log.info("組織停用成功: id={}", organizationId);
        return buildResponse(context);
    }

    private OrganizationDetailResponse buildResponse(OrganizationContext context) {
        Organization org = context.getOrganization();

        return OrganizationDetailResponse.builder()
                .organizationId(org.getId().getValue().toString())
                .code(org.getCode())
                .name(org.getName())
                .nameEn(org.getNameEn())
                .type(org.getType().name())
                .typeDisplay(org.getType().getDisplayName())
                .status(org.getStatus().name())
                .statusDisplay(org.getStatus().getDisplayName())
                .taxId(org.getTaxId())
                .phone(org.getPhone())
                .address(org.getAddress())
                .build();
    }
}
