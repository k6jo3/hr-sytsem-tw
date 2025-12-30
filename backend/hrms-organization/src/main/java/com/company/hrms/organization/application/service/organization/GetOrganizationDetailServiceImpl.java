package com.company.hrms.organization.application.service.organization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.organization.OrganizationDetailResponse;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.application.service.organization.task.LoadOrgStatsTask;
import com.company.hrms.organization.application.service.organization.task.LoadOrgTask;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢組織詳情 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: GET /api/v1/organizations/{id}
 * </p>
 * 
 * <p>
 * Pipeline 步驟：
 * </p>
 * <ol>
 * <li>LoadOrgTask - 載入組織</li>
 * <li>LoadOrgStatsTask - 載入統計資訊</li>
 * </ol>
 */
@Service("getOrganizationDetailServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetOrganizationDetailServiceImpl
        implements QueryApiService<Object, OrganizationDetailResponse> {

    private final LoadOrgTask loadOrgTask;
    private final LoadOrgStatsTask loadOrgStatsTask;
    private final IOrganizationRepository organizationRepository;

    @Override
    public OrganizationDetailResponse getResponse(Object request, JWTModel currentUser, String... args)
            throws Exception {

        String organizationId = args[0];
        log.info("查詢組織詳情: id={}", organizationId);

        // 1. 建立 Context
        OrganizationContext context = new OrganizationContext(organizationId);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadOrgTask)
                .next(loadOrgStatsTask)
                .execute();

        // 3. 建立回應
        return buildResponse(context);
    }

    private OrganizationDetailResponse buildResponse(OrganizationContext context) {
        Organization org = context.getOrganization();

        // 查詢母組織名稱
        String parentName = null;
        String parentId = null;
        if (org.getParentId() != null) {
            parentId = org.getParentId().getValue().toString();
            organizationRepository.findById(org.getParentId())
                    .ifPresent(parent -> {
                        // 使用 Builder 設定
                    });
        }

        return OrganizationDetailResponse.builder()
                .organizationId(org.getId().getValue().toString())
                .code(org.getCode())
                .name(org.getName())
                .nameEn(org.getNameEn())
                .type(org.getType().name())
                .typeDisplay(org.getType().getDisplayName())
                .status(org.getStatus().name())
                .statusDisplay(org.getStatus().getDisplayName())
                .parentId(parentId)
                .parentName(parentName)
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
