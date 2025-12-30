package com.company.hrms.organization.application.service.organization.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.organization.api.request.organization.CreateOrganizationRequest;
import com.company.hrms.organization.api.request.organization.UpdateOrganizationRequest;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 組織 Pipeline Context
 * 
 * <p>
 * 在 Pipeline 中傳遞組織相關數據
 * </p>
 * 
 * <p>
 * 結構：
 * </p>
 * <ul>
 * <li>輸入：Request DTO、ID 參數</li>
 * <li>中間數據：Organization 聚合根、統計資訊</li>
 * <li>輸出：由 Service 根據 Context 建立 Response</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
public class OrganizationContext extends PipelineContext {

    // === 輸入 ===
    private CreateOrganizationRequest createRequest;
    private UpdateOrganizationRequest updateRequest;
    private String organizationId;

    // === 中間數據 ===
    private Organization organization;
    private Organization parentOrganization;
    private OrganizationId parentId;

    // === 統計資訊 ===
    private int departmentCount;
    private int employeeCount;

    // === 建構子 ===

    /**
     * 建立組織用
     */
    public OrganizationContext(CreateOrganizationRequest request) {
        this.createRequest = request;
    }

    /**
     * 查詢/更新/停用組織用
     */
    public OrganizationContext(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 更新組織用
     */
    public OrganizationContext(String organizationId, UpdateOrganizationRequest request) {
        this.organizationId = organizationId;
        this.updateRequest = request;
    }
}
