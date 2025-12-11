package com.company.hrms.iam.application.service.role;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.controller.role.HR01RoleQryController.RoleQueryRequest;
import com.company.hrms.iam.api.response.role.RoleListResponse;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.RoleStatus;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 查詢角色列表 Application Service
 *
 * <p>命名規範：{動詞}{名詞}ServiceImpl</p>
 * <p>對應 Controller 方法：getRoleList</p>
 */
@Service("getRoleListServiceImpl")
@Transactional(readOnly = true)
public class GetRoleListServiceImpl implements QueryApiService<RoleQueryRequest, List<RoleListResponse>> {

    private final IRoleRepository roleRepository;

    @Autowired
    public GetRoleListServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleListResponse> getResponse(RoleQueryRequest request, JWTModel currentUser, String... args) throws Exception {
        List<Role> roles;

        // 根據查詢條件取得角色列表
        if (request.status() != null && !request.status().isBlank()) {
            roles = roleRepository.findByStatus(RoleStatus.valueOf(request.status().toUpperCase()));
        } else if (Boolean.TRUE.equals(request.systemRole())) {
            roles = roleRepository.findSystemRoles();
        } else {
            String tenantId = currentUser != null ? currentUser.getTenantId() : null;
            if (tenantId != null) {
                roles = roleRepository.findByTenantId(tenantId);
                // 加入系統角色
                roles.addAll(roleRepository.findSystemRoles());
            } else {
                roles = roleRepository.findAll();
            }
        }

        return roles.stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    private RoleListResponse toListResponse(Role role) {
        return RoleListResponse.builder()
                .roleId(role.getId().getValue())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .description(role.getDescription())
                .systemRole(role.isSystemRole())
                .status(role.getStatus().name())
                .permissionCount(role.getPermissionCount())
                .build();
    }
}
