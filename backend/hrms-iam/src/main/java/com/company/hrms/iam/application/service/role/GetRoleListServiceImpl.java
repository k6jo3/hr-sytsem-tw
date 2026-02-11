package com.company.hrms.iam.application.service.role;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.controller.role.HR01RoleQryController.RoleQueryRequest;
import com.company.hrms.iam.api.response.role.RoleListResponse;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢角色列表 Application Service
 * 符合系統開發規範，使用 QueryBuilder 構建查詢
 */
@Service("getRoleListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetRoleListServiceImpl
        implements QueryApiService<RoleQueryRequest, List<RoleListResponse>> {

    private final IRoleRepository roleRepository;

    @Override
    public List<RoleListResponse> getResponse(RoleQueryRequest request, JWTModel currentUser, String... args)
            throws Exception {

        log.info("查詢角色列表: {}", request);

        QueryBuilder builder = QueryBuilder.where();

        // 0. 安全性：軟刪除過濾
        builder.eq("is_deleted", false);

        // 1. 租戶隔離邏輯
        // 管理員只能看到自己租戶的角色 + 系統角色 (tenant_id IS NULL)
        // 超級管理員可以看到所有角色
        boolean isSuperAdmin = currentUser.getRoles() != null && currentUser.getRoles().contains("SUPER_ADMIN");
        String tenantId = currentUser.getTenantId();

        if (!isSuperAdmin) {
            builder.orGroup(sub -> sub
                    .eq("tenant_id", tenantId)
                    .isNull("tenant_id"));
        }

        // 2. 名稱模糊查詢 (符合合約 IAM_QRY_102)
        if (request.name() != null && !request.name().isEmpty()) {
            String searchPattern = "%" + request.name() + "%";
            builder.orGroup(sub -> sub
                    .like("display_name", searchPattern)
                    .like("role_name", searchPattern));
        }

        // 3. 狀態過濾
        if (request.status() != null && !request.status().isEmpty()) {
            builder.eq("status", request.status());
        }

        // 4. 系統角色過濾 (符合合約 IAM_QRY_103/104)
        if (request.isSystemRole() != null) {
            builder.eq("is_system_role", request.isSystemRole());
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
                .systemRole(role.isSystemRole())
                .status(role.getStatus().name())
                .permissionCount(role.getPermissionCount())
                .build();
    }
}
