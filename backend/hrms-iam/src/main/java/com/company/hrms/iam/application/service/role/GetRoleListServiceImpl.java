package com.company.hrms.iam.application.service.role;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.response.role.RoleListResponse;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢角色列表 Application Service (Pipeline 模式)
 * 
 * <p>
 * 注意：簡單查詢可選擇不使用 Pipeline，但為保持一致性仍使用標準結構
 * </p>
 */
@Service("getRoleListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetRoleListServiceImpl
        implements QueryApiService<Object, List<RoleListResponse>> {

    private final IRoleRepository roleRepository;

    @Override
    public List<RoleListResponse> getResponse(Object request, JWTModel currentUser, String... args)
            throws Exception {

        log.info("查詢角色列表");

        // 取得所有角色（簡單查詢，不需要 Pipeline）
        String tenantId = currentUser != null ? currentUser.getTenantId() : null;
        List<Role> roles;

        if (tenantId != null) {
            roles = roleRepository.findByTenantId(tenantId);
            roles.addAll(roleRepository.findSystemRoles());
        } else {
            roles = roleRepository.findAll();
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
