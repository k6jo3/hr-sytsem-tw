package com.company.hrms.organization.application.service.organization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.organization.UpdateOrganizationRequest;
import com.company.hrms.organization.api.response.organization.OrganizationDetailResponse;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.application.service.organization.task.LoadOrgStatsTask;
import com.company.hrms.organization.application.service.organization.task.LoadOrgTask;
import com.company.hrms.organization.application.service.organization.task.SaveOrgTask;
import com.company.hrms.organization.application.service.organization.task.UpdateOrgAggregateTask;
import com.company.hrms.organization.domain.model.aggregate.Organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新組織 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: PUT /api/v1/organizations/{id}
 * </p>
 * 
 * <p>
 * Pipeline 步驟：
 * </p>
 * <ol>
 * <li>LoadOrgTask - 載入組織</li>
 * <li>UpdateOrgAggregateTask - 更新組織聚合根</li>
 * <li>SaveOrgTask - 儲存組織</li>
 * <li>LoadOrgStatsTask - 載入統計資訊</li>
 * </ol>
 */
@Service("updateOrganizationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateOrganizationServiceImpl
        implements CommandApiService<UpdateOrganizationRequest, OrganizationDetailResponse> {

    private final LoadOrgTask loadOrgTask;
    private final UpdateOrgAggregateTask updateOrgAggregateTask;
    private final SaveOrgTask saveOrgTask;
    private final LoadOrgStatsTask loadOrgStatsTask;

    @Override
    public OrganizationDetailResponse execCommand(UpdateOrganizationRequest request,
            JWTModel currentUser, String... args) throws Exception {

        String organizationId = args[0];
        log.info("更新組織: id={}", organizationId);

        // 1. 建立 Context
        OrganizationContext context = new OrganizationContext(organizationId, request);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadOrgTask)
                .next(updateOrgAggregateTask)
                .next(saveOrgTask)
                .next(loadOrgStatsTask)
                .execute();

        // 3. 建立回應
        log.info("組織更新成功: id={}", organizationId);
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
                .parentId(org.getParentId() != null ? org.getParentId().getValue().toString() : null)
                .taxId(org.getTaxId())
                .phone(org.getPhone())
                .address(org.getAddress())
                .establishedDate(org.getEstablishedDate())
                .description(org.getDescription())
                .departmentCount(context.getDepartmentCount())
                .employeeCount(context.getEmployeeCount())
                .build();
    }
}
