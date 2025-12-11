package com.company.hrms.iam.application.service.role;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.role.CreateRoleRequest;
import com.company.hrms.iam.api.response.role.CreateRoleResponse;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 建立角色 Application Service
 *
 * <p>命名規範：{動詞}{名詞}ServiceImpl</p>
 * <p>對應 Controller 方法：createRole</p>
 */
@Service("createRoleServiceImpl")
@Transactional
public class CreateRoleServiceImpl implements CommandApiService<CreateRoleRequest, CreateRoleResponse> {

    private final IRoleRepository roleRepository;

    @Autowired
    public CreateRoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public CreateRoleResponse execCommand(CreateRoleRequest request, JWTModel currentUser, String... args) throws Exception {
        // 檢查角色代碼是否已存在
        String tenantId = currentUser != null ? currentUser.getTenantId() : null;
        if (roleRepository.existsByRoleCodeAndTenantId(request.getRoleCode(), tenantId)) {
            throw new DomainException("ROLE_CODE_EXISTS", "角色代碼已存在");
        }

        // 建立角色
        Role role = Role.create(
                request.getRoleName(),
                request.getRoleCode(),
                request.getDescription(),
                tenantId
        );

        // 指派權限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            for (String permissionId : request.getPermissionIds()) {
                role.assignPermission(PermissionId.of(permissionId));
            }
        }

        // 儲存角色
        roleRepository.save(role);

        return CreateRoleResponse.builder()
                .roleId(role.getId().getValue())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .build();
    }
}
