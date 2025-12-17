package com.company.hrms.organization.application.service.organization;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.response.organization.OrganizationDetailResponse;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 停用組織服務實作
 */
@Service("deactivateOrganizationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeactivateOrganizationServiceImpl
        implements CommandApiService<Void, OrganizationDetailResponse> {

    private final IOrganizationRepository organizationRepository;

    @Override
    public OrganizationDetailResponse execCommand(Void request,
                                                   JWTModel currentUser,
                                                   String... args) throws Exception {
        String organizationId = args[0];
        log.info("Deactivating organization: {}", organizationId);

        // 查詢組織
        Organization organization = organizationRepository.findById(new OrganizationId(organizationId))
                .orElseThrow(() -> new IllegalArgumentException("組織不存在: " + organizationId));

        // TODO: 檢查組織下是否有在職員工

        // 停用組織
        organization.deactivate();

        // 儲存更新
        organizationRepository.save(organization);

        log.info("Organization deactivated successfully: {}", organizationId);

        return buildOrganizationDetailResponse(organization);
    }

    private OrganizationDetailResponse buildOrganizationDetailResponse(Organization organization) {
        return OrganizationDetailResponse.builder()
                .organizationId(organization.getId().getValue())
                .code(organization.getCode())
                .name(organization.getName())
                .nameEn(organization.getNameEn())
                .type(organization.getType().name())
                .typeDisplay(organization.getType().getDisplayName())
                .status(organization.getStatus().name())
                .statusDisplay(organization.getStatus().getDisplayName())
                .parentId(organization.getParentId() != null ? organization.getParentId().getValue() : null)
                .taxId(organization.getTaxId())
                .phone(organization.getPhone())
                .fax(organization.getFax())
                .email(organization.getEmail())
                .address(organization.getAddress())
                .establishedDate(organization.getEstablishedDate())
                .description(organization.getDescription())
                .build();
    }
}
