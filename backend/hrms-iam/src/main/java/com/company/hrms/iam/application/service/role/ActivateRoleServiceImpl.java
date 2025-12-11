package com.company.hrms.iam.application.service.role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.repository.IRoleRepository;

/**
 * 啟用角色 Application Service
 *
 * <p>
 * 命名規範：{動詞}{名詞}ServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：activateRole
 * </p>
 */
@Service("activateRoleServiceImpl")
@Transactional
public class ActivateRoleServiceImpl implements CommandApiService<Void, Void> {

    private final IRoleRepository roleRepository;

    public ActivateRoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Void execCommand(Void request, JWTModel currentUser, String... args) throws Exception {
        String roleId = args[0];

        // 查詢角色
        Role role = roleRepository.findById(RoleId.of(roleId))
                .orElseThrow(() -> new DomainException("ROLE_NOT_FOUND", "角色不存在"));

        // 啟用角色
        role.activate();

        // 儲存更新
        roleRepository.update(role);

        return null;
    }
}
