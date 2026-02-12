package com.company.hrms.iam.application.service.role;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.request.role.GetRoleListRequest;
import com.company.hrms.iam.api.response.role.RoleListResponse;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢角色列表 Application Service
 */
@Service("getRoleListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetRoleListServiceImpl
        implements QueryApiService<GetRoleListRequest, List<RoleListResponse>> {

    private final IRoleRepository roleRepository;

    @Override
    public List<RoleListResponse> getResponse(GetRoleListRequest request, JWTModel currentUser, String... args)
            throws Exception {

        log.info("查詢角色列表: {}", request);

        QueryBuilder builder = QueryBuilder.where();

        // 1. 租戶隔離邏輯
        boolean isSuperAdmin = currentUser.getRoles() != null && currentUser.getRoles().contains("SUPER_ADMIN");
        String tenantId = currentUser.getTenantId();

        if (!isSuperAdmin) {
            builder.orGroup(sub -> sub
                    .eq("tenantId", tenantId)
                    .isNull("tenantId"));
        }

        // 2. 名稱模糊查詢
        if (request.getName() != null && !request.getName().isEmpty()) {
            String searchPattern = "%" + request.getName() + "%";
            builder.orGroup(sub -> sub
                    .like("roleName", searchPattern)
                    .like("roleCode", searchPattern));
        }

        // 3. 狀態過濾
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            builder.eq("status", request.getStatus());
        }

        // 4. 系統角色過濾
        if (request.getIsSystemRole() != null) {
            builder.eq("isSystemRole", request.getIsSystemRole());
        }

        QueryGroup query = builder.build();
        List<Role> roles = roleRepository.findAll(query);

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
                .isSystemRole(role.isSystemRole())
                .status(role.getStatus().name())
                .permissionCount(role.getPermissionCount())
                .permissions(role.getPermissionIds().stream()
                        .map(id -> id.getValue())
                        .collect(Collectors.toList()))
                .createdAt(role.getCreatedAt())
                .build();
    }
}
