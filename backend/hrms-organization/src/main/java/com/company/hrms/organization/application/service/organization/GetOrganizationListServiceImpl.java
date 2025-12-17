package com.company.hrms.organization.application.service.organization;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.organization.OrganizationListItemResponse;
import com.company.hrms.organization.api.response.organization.OrganizationListResponse;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 取得組織清單服務實作
 */
@Service("getOrganizationListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetOrganizationListServiceImpl
        implements QueryApiService<Void, OrganizationListResponse> {

    private final IOrganizationRepository organizationRepository;

    @Override
    public OrganizationListResponse getResponse(Void request,
                                                JWTModel currentUser,
                                                String... args) throws Exception {
        log.info("Getting organization list");

        List<Organization> organizations = organizationRepository.findAll();

        List<OrganizationListItemResponse> items = organizations.stream()
                .map(this::toListItemResponse)
                .collect(Collectors.toList());

        return OrganizationListResponse.builder()
                .items(items)
                .totalCount(items.size())
                .build();
    }

    private OrganizationListItemResponse toListItemResponse(Organization organization) {
        return OrganizationListItemResponse.builder()
                .organizationId(organization.getId().getValue())
                .code(organization.getCode())
                .name(organization.getName())
                .type(organization.getType().name())
                .typeDisplay(organization.getType().getDisplayName())
                .status(organization.getStatus().name())
                .parentId(organization.getParentId() != null ? organization.getParentId().getValue() : null)
                .build();
    }
}
