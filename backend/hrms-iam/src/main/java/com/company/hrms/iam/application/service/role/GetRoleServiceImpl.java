package com.company.hrms.iam.application.service.role;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.controller.role.HR01RoleQryController.GetRoleRequest;
import com.company.hrms.iam.api.response.role.RoleDetailResponse;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.application.service.role.task.CountRoleUsersTask;
import com.company.hrms.iam.application.service.role.task.LoadRolePermissionsTask;
import com.company.hrms.iam.application.service.role.task.LoadRoleTask;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.entity.Permission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢單一角色 Application Service (Pipeline 模式)
 */
@Service("getRoleServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetRoleServiceImpl
                implements QueryApiService<GetRoleRequest, RoleDetailResponse> {

        private final LoadRoleTask loadRoleTask;
        private final LoadRolePermissionsTask loadRolePermissionsTask;
        private final CountRoleUsersTask countRoleUsersTask;

        @Override
        @SuppressWarnings("unchecked")
        public RoleDetailResponse getResponse(GetRoleRequest request, JWTModel currentUser, String... args)
                        throws Exception {

                String roleId = args[0];
                log.info("查詢角色: roleId={}", roleId);

                RoleContext context = new RoleContext(roleId);

                BusinessPipeline.start(context)
                                .next(loadRoleTask)
                                .next(loadRolePermissionsTask)
                                .next(countRoleUsersTask)
                                .execute();

                Role role = context.getRole();
                List<Permission> permissions = (List<Permission>) context.getAttribute("permissions");
                Integer userCount = (Integer) context.getAttribute("userCount");

                return buildDetailResponse(role, permissions, userCount);
        }

        private RoleDetailResponse buildDetailResponse(
                        Role role,
                        List<Permission> permissions,
                        Integer userCount) {

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
                                .isSystemRole(role.isSystemRole())
                                .status(role.getStatus().name())
                                .userCount(userCount != null ? userCount : 0)
                                .permissionDetails(permissionItems)
                                .permissions(permissions.stream().map(Permission::getPermissionCode)
                                                .collect(Collectors.toList()))
                                .createdAt(role.getCreatedAt())
                                .updatedAt(role.getUpdatedAt())
                                .build();
        }
}
