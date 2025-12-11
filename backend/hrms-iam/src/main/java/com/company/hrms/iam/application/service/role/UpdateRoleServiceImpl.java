package com.company.hrms.iam.application.service.role;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.role.UpdateRoleRequest;
import com.company.hrms.iam.api.response.role.RoleDetailResponse;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.repository.IPermissionRepository;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 更新角色 Application Service
 *
 * <p>命名規範：{動詞}{名詞}ServiceImpl</p>
 * <p>對應 Controller 方法：updateRole</p>
 */
@Service("updateRoleServiceImpl")
@Transactional
public class UpdateRoleServiceImpl implements CommandApiService<UpdateRoleRequest, RoleDetailResponse> {

    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;

    @Autowired
    public UpdateRoleServiceImpl(IRoleRepository roleRepository, IPermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public RoleDetailResponse execCommand(UpdateRoleRequest request, JWTModel currentUser, String... args) throws Exception {
        String roleId = args[0];

        // 查詢角色
        Role role = roleRepository.findById(RoleId.of(roleId))
                .orElseThrow(() -> new DomainException("ROLE_NOT_FOUND", "角色不存在"));

        // 更新角色資訊
        role.update(request.getRoleName(), request.getDescription());

        // 更新權限 (若有提供)
        if (request.getPermissionIds() != null) {
            role.clearPermissions();
            for (String permissionId : request.getPermissionIds()) {
                role.assignPermission(PermissionId.of(permissionId));
            }
        }

        // 儲存更新
        roleRepository.update(role);

        // 查詢權限詳情
        List<Permission> permissions = permissionRepository.findByIds(role.getPermissionIds());

        return toDetailResponse(role, permissions);
    }

    private RoleDetailResponse toDetailResponse(Role role, List<Permission> permissions) {
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
