package com.company.hrms.iam.application.service.role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.controller.role.HR01RoleCmdController.AssignPermissionsRequest;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.repository.IRoleRepository;

/**
 * 指派權限 Application Service
 *
 * <p>
 * 命名規範：{動詞}{名詞}ServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：assignPermissions
 * </p>
 */
@Service("assignPermissionsServiceImpl")
@Transactional
public class AssignPermissionsServiceImpl implements CommandApiService<AssignPermissionsRequest, Void> {

    private final IRoleRepository roleRepository;

    public AssignPermissionsServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Void execCommand(AssignPermissionsRequest request, JWTModel currentUser, String... args) throws Exception {
        String roleId = args[0];

        // 查詢角色
        Role role = roleRepository.findById(RoleId.of(roleId))
                .orElseThrow(() -> new DomainException("ROLE_NOT_FOUND", "角色不存在"));

        // 清除現有權限並指派新權限
        role.clearPermissions();
        if (request.permissionIds() != null) {
            for (String permissionId : request.permissionIds()) {
                role.assignPermission(PermissionId.of(permissionId));
            }
        }

        // 儲存更新
        roleRepository.update(role);

        return null;
    }
}
