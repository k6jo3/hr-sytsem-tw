package com.company.hrms.organization.application.service.organization;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.organization.UpdateOrganizationRequest;
import com.company.hrms.organization.api.response.organization.OrganizationDetailResponse;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 更新組織服務實作
 */
@Service("updateOrganizationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateOrganizationServiceImpl
        implements CommandApiService<UpdateOrganizationRequest, OrganizationDetailResponse> {

    private final IOrganizationRepository organizationRepository;

    @Override
    public OrganizationDetailResponse execCommand(UpdateOrganizationRequest request,
                                                   JWTModel currentUser,
                                                   String... args) throws Exception {
        String organizationId = args[0];
        log.info("Updating organization: {}", organizationId);

        // 查詢組織
        Organization organization = organizationRepository.findById(new OrganizationId(organizationId))
                .orElseThrow(() -> new IllegalArgumentException("組織不存在: " + organizationId));

        // 更新組織資訊
        organization.update(
                request.getName() != null ? request.getName() : organization.getName(),
                request.getNameEn() != null ? request.getNameEn() : organization.getNameEn(),
                request.getTaxId() != null ? request.getTaxId() : organization.getTaxId(),
                request.getPhone() != null ? request.getPhone() : organization.getPhone(),
                request.getFax() != null ? request.getFax() : organization.getFax(),
                request.getEmail() != null ? request.getEmail() : organization.getEmail(),
                request.getAddress() != null ? request.getAddress() : organization.getAddress(),
                request.getEstablishedDate() != null ? request.getEstablishedDate() : organization.getEstablishedDate(),
                request.getDescription() != null ? request.getDescription() : organization.getDescription()
        );

        // 儲存更新
        organizationRepository.save(organization);

        log.info("Organization updated successfully: {}", organizationId);

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
