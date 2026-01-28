package com.company.hrms.iam.application.service.role;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.response.role.RoleDetailResponse;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.application.service.role.task.LoadRolePermissionsTask;
import com.company.hrms.iam.application.service.role.task.LoadRoleTask;
import com.company.hrms.iam.domain.model.entity.Permission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.company.hrms.iam.domain.model.aggregate.Role;

/**
 * 查詢單一角色 Application Service (Pipeline 模式)
 */
@Service("getRoleServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetRoleServiceImpl
                implements QueryApiService<Object, RoleDetailResponse> {

        private final LoadRoleTask loadRoleTask;
        private final LoadRolePermissionsTask loadRolePermissionsTask;

        @Override
        @SuppressWarnings("unchecked")
        public RoleDetailResponse getResponse(Object request, JWTModel currentUser, String... args)
                        throws Exception {

                String roleId = args[0];
                log.info("查詢角色: roleId={}", roleId);

                RoleContext context = new RoleContext(roleId);

                BusinessPipeline.start(context)
                                .next(loadRoleTask)
                                .next(loadRolePermissionsTask)
                                .execute();

                var role = context.getRole();
                List<Permission> permissions = (List<Permission>) context.getAttribute("permissions");

                return buildDetailResponse(role, permissions);
        }

        private RoleDetailResponse buildDetailResponse(
                        Role role,
                        List<Permission> permissions) {

                List<RoleDetailResponse.PermissionItem> permissionItems = permissions.stream()
                                .map(p -> RoleDetailResponse.PermissionItem.builder()
                                                .permissionId(p.getId().getValue())
                                                .permissionCode(p.getPermissionCode())
                                                .permissionName(p.getPermissionName())
                                                .build())
                                .collect(Collectors.toList());

                return RoleDetailResponse.builder()
                                .roleId(role.getId().getValue())
                                .roleName(role.getRoleName())
                                .roleCode(role.getRoleCode())
                                .description(role.getDescription())
                                .tenantId(role.getTenantId())
                                .systemRole(role.isSystemRole())
                                .status(role.getStatus().name())
                                .permissions(permissionItems)
                                .createdAt(role.getCreatedAt())
                                .updatedAt(role.getUpdatedAt())
                                .build();
        }
}
