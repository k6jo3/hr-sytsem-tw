package com.company.hrms.organization.application.service.organization;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.organization.OrganizationListItemResponse;
import com.company.hrms.organization.api.response.organization.OrganizationListResponse;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        private final IDepartmentRepository departmentRepository;
        private final IEmployeeRepository employeeRepository;

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
                // 計算組織下的部門數與員工數
                int deptCount = departmentRepository.countByOrganizationId(organization.getId());
                int empCount = employeeRepository.countByOrganizationId(organization.getId());

                return OrganizationListItemResponse.builder()
                                .organizationId(organization.getId().getValue().toString())
                                .code(organization.getCode())
                                .name(organization.getName())
                                .type(organization.getType().name())
                                .typeDisplay(organization.getType().getDisplayName())
                                .status(organization.getStatus().name())
                                .parentId(organization.getParentId() != null
                                                ? organization.getParentId().getValue().toString()
                                                : null)
                                .departmentCount(deptCount)
                                .employeeCount(empCount)
                                .build();
        }
}
