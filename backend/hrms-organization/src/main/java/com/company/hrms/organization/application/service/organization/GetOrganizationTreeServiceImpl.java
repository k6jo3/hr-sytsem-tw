package com.company.hrms.organization.application.service.organization;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.organization.OrganizationTreeResponse;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 取得組織樹狀結構服務實作
 */
@Service("getOrganizationTreeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetOrganizationTreeServiceImpl
        implements QueryApiService<Void, OrganizationTreeResponse> {

    private final IOrganizationRepository organizationRepository;
    private final IDepartmentRepository departmentRepository;

    @Override
    public OrganizationTreeResponse getResponse(Void request,
                                                JWTModel currentUser,
                                                String... args) throws Exception {
        String organizationId = args[0];
        log.info("Getting organization tree: {}", organizationId);

        Organization organization = organizationRepository.findById(new OrganizationId(organizationId))
                .orElseThrow(() -> new IllegalArgumentException("組織不存在: " + organizationId));

        // 取得子組織
        List<Organization> childOrganizations = organizationRepository.findByParentId(organization.getId());

        // 取得部門
        List<Department> departments = departmentRepository.findByOrganizationId(organization.getId());

        // 建立組織樹
        return buildOrganizationTree(organization, childOrganizations, departments);
    }

    private OrganizationTreeResponse buildOrganizationTree(Organization organization,
                                                            List<Organization> childOrganizations,
                                                            List<Department> departments) {
        // 建立子組織節點
        List<OrganizationTreeResponse> childNodes = childOrganizations.stream()
                .map(child -> {
                    List<Organization> grandChildren = organizationRepository.findByParentId(child.getId());
                    List<Department> childDepts = departmentRepository.findByOrganizationId(child.getId());
                    return buildOrganizationTree(child, grandChildren, childDepts);
                })
                .collect(Collectors.toList());

        // 建立部門樹
        List<OrganizationTreeResponse.DepartmentTreeNode> departmentTree = buildDepartmentTree(departments);

        return OrganizationTreeResponse.builder()
                .organizationId(organization.getId().getValue())
                .code(organization.getCode())
                .name(organization.getName())
                .type(organization.getType().name())
                .status(organization.getStatus().name())
                .children(childNodes)
                .departments(departmentTree)
                .build();
    }

    private List<OrganizationTreeResponse.DepartmentTreeNode> buildDepartmentTree(List<Department> departments) {
        // 按父部門分組
        Map<String, List<Department>> childrenMap = departments.stream()
                .filter(d -> d.getParentId() != null)
                .collect(Collectors.groupingBy(d -> d.getParentId().getValue()));

        // 找出根部門並建立樹狀結構
        return departments.stream()
                .filter(d -> d.getParentId() == null)
                .map(d -> buildDepartmentNode(d, childrenMap))
                .collect(Collectors.toList());
    }

    private OrganizationTreeResponse.DepartmentTreeNode buildDepartmentNode(
            Department department,
            Map<String, List<Department>> childrenMap) {

        List<OrganizationTreeResponse.DepartmentTreeNode> children =
                childrenMap.getOrDefault(department.getId().getValue(), new ArrayList<>())
                        .stream()
                        .map(child -> buildDepartmentNode(child, childrenMap))
                        .collect(Collectors.toList());

        return OrganizationTreeResponse.DepartmentTreeNode.builder()
                .departmentId(department.getId().getValue())
                .code(department.getCode())
                .name(department.getName())
                .level(department.getLevel())
                .managerId(department.getManagerId() != null ? department.getManagerId().getValue() : null)
                .children(children)
                .build();
    }
}
