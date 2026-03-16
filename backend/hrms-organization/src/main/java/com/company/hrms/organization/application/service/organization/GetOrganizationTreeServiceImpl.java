package com.company.hrms.organization.application.service.organization;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.organization.OrganizationTreeResponse;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.application.service.organization.task.LoadDepartmentTreeTask;
import com.company.hrms.organization.application.service.organization.task.LoadOrgTask;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢組織樹 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: GET /api/v1/organizations/{id}/tree
 * </p>
 * 
 * <p>
 * Pipeline 步驟：
 * </p>
 * <ol>
 * <li>LoadOrgTask - 載入組織</li>
 * <li>LoadDepartmentTreeTask - 載入部門樹</li>
 * </ol>
 */
@Service("getOrganizationTreeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetOrganizationTreeServiceImpl
        implements QueryApiService<Object, OrganizationTreeResponse> {

    private final LoadOrgTask loadOrgTask;
    private final LoadDepartmentTreeTask loadDepartmentTreeTask;
    private final IEmployeeRepository employeeRepository;

    @Override
    @SuppressWarnings("unchecked")
    public OrganizationTreeResponse getResponse(Object request, JWTModel currentUser, String... args)
            throws Exception {

        String organizationId = args[0];
        log.info("查詢組織樹: id={}", organizationId);

        // 1. 建立 Context
        OrganizationContext context = new OrganizationContext(organizationId);

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(loadOrgTask)
                .next(loadDepartmentTreeTask)
                .execute();

        // 3. 建立回應
        Organization org = context.getOrganization();
        List<OrganizationTreeResponse.DepartmentTreeNode> departmentNodes = (List<OrganizationTreeResponse.DepartmentTreeNode>) context
                .getAttribute("departmentNodes");

        // 查詢組織總員工數
        int employeeCount = employeeRepository.countByOrganizationId(org.getId());

        return OrganizationTreeResponse.builder()
                .organizationId(org.getId().getValue().toString())
                .code(org.getCode())
                .name(org.getName())
                .type(org.getType().name())
                .status(org.getStatus().name())
                .employeeCount(employeeCount)
                .departments(departmentNodes)
                .build();
    }
}
