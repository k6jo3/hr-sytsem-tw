package com.company.hrms.organization.application.service.organization;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.organization.CreateOrganizationRequest;
import com.company.hrms.organization.api.response.organization.CreateOrganizationResponse;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationType;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 新增組織服務實作
 */
@Service("createOrganizationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateOrganizationServiceImpl
        implements CommandApiService<CreateOrganizationRequest, CreateOrganizationResponse> {

    private final IOrganizationRepository organizationRepository;

    @Override
    public CreateOrganizationResponse execCommand(CreateOrganizationRequest request,
                                                   JWTModel currentUser,
                                                   String... args) throws Exception {
        log.info("Creating organization: {}", request.getCode());

        // 驗證組織代碼唯一性
        if (organizationRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("組織代碼已存在: " + request.getCode());
        }

        // 驗證母組織存在 (如果有指定)
        OrganizationId parentId = null;
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            parentId = new OrganizationId(request.getParentId());
            if (!organizationRepository.existsById(parentId)) {
                throw new IllegalArgumentException("母組織不存在: " + request.getParentId());
            }
        }

        // 建立組織
        Organization organization = Organization.create(
                request.getCode(),
                request.getName(),
                request.getNameEn(),
                OrganizationType.valueOf(request.getType()),
                parentId,
                request.getTaxId(),
                request.getPhone(),
                request.getFax(),
                request.getEmail(),
                request.getAddress(),
                request.getEstablishedDate(),
                request.getDescription()
        );

        // 儲存組織
        organizationRepository.save(organization);

        log.info("Organization created successfully: {}", organization.getId().getValue());

        return CreateOrganizationResponse.success(
                organization.getId().getValue(),
                organization.getCode(),
                organization.getName()
        );
    }
}
