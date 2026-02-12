package com.company.hrms.iam.application.service.role;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.role.UpdateRoleRequest;
import com.company.hrms.iam.api.response.role.RoleDetailResponse;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.application.service.role.task.LoadRoleTask;
import com.company.hrms.iam.application.service.role.task.PublishRoleUpdatedEventTask;
import com.company.hrms.iam.application.service.role.task.UpdateRoleAggregateTask;
import com.company.hrms.iam.application.service.role.task.UpdateRoleTask;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.domain.repository.IPermissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新角色 Application Service (Pipeline 模式)
 */
@Service("updateRoleServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateRoleServiceImpl
                implements CommandApiService<UpdateRoleRequest, RoleDetailResponse> {

        private final LoadRoleTask loadRoleTask;
        private final UpdateRoleAggregateTask updateRoleAggregateTask;
        private final UpdateRoleTask updateRoleTask;
        private final PublishRoleUpdatedEventTask publishRoleUpdatedEventTask;
        private final IPermissionRepository permissionRepository;

        @Override
        public RoleDetailResponse execCommand(UpdateRoleRequest request, JWTModel currentUser, String... args)
                        throws Exception {

                String roleId = args[0];
                log.info("更新角色: roleId={}", roleId);

                RoleContext context = new RoleContext(roleId, request);

                BusinessPipeline.start(context)
                                .next(loadRoleTask)
                                .next(updateRoleAggregateTask)
                                .next(updateRoleTask)
                                .next(publishRoleUpdatedEventTask)
                                .execute();

                Role role = context.getRole();
                List<Permission> permissions = permissionRepository.findByIds(role.getPermissionIds());

                log.info("角色更新成功: roleId={}", roleId);
                return toDetailResponse(role, permissions);
        }

        private RoleDetailResponse toDetailResponse(
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
                                .isSystemRole(role.isSystemRole())
                                .status(role.getStatus().name())
                                .permissionDetails(permissionItems)
                                .permissions(permissions.stream().map(Permission::getPermissionCode)
                                                .collect(Collectors.toList()))
                                .createdAt(role.getCreatedAt())
                                .updatedAt(role.getUpdatedAt())
                                .build();
        }
}
