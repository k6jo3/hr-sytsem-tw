package com.company.hrms.organization.application.service.organization;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.organization.OrganizationDetailResponse;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 取得組織詳情服務實作
 */
@Service("getOrganizationDetailServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetOrganizationDetailServiceImpl
        implements QueryApiService<Void, OrganizationDetailResponse> {

    private final IOrganizationRepository organizationRepository;

    @Override
    public OrganizationDetailResponse getResponse(Void request,
                                                  JWTModel currentUser,
                                                  String... args) throws Exception {
        String organizationId = args[0];
        log.info("Getting organization detail: {}", organizationId);

        Organization organization = organizationRepository.findById(new OrganizationId(organizationId))
                .orElseThrow(() -> new IllegalArgumentException("組織不存在: " + organizationId));

        // 取得母組織名稱
        String parentName = null;
        if (organization.getParentId() != null) {
            parentName = organizationRepository.findById(organization.getParentId())
                    .map(Organization::getName)
                    .orElse(null);
        }

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
                .parentName(parentName)
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
