package com.company.hrms.iam.application.service.role;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.controller.role.HR01RoleQryController.GetRoleRequest;
import com.company.hrms.iam.api.response.role.RoleDetailResponse;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.repository.IPermissionRepository;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 查詢單一角色 Application Service
 *
 * <p>命名規範：{動詞}{名詞}ServiceImpl</p>
 * <p>對應 Controller 方法：getRole</p>
 */
@Service("getRoleServiceImpl")
@Transactional(readOnly = true)
public class GetRoleServiceImpl implements QueryApiService<GetRoleRequest, RoleDetailResponse> {

    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;

    @Autowired
    public GetRoleServiceImpl(IRoleRepository roleRepository, IPermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public RoleDetailResponse getResponse(GetRoleRequest request, JWTModel currentUser, String... args) throws Exception {
        String roleId = args[0];

        // 查詢角色
        Role role = roleRepository.findById(RoleId.of(roleId))
                .orElseThrow(() -> new DomainException("ROLE_NOT_FOUND", "角色不存在"));

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
