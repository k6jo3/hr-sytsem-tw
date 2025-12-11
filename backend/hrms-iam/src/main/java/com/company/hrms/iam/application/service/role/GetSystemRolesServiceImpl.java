package com.company.hrms.iam.application.service.role;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.controller.role.HR01RoleQryController.GetSystemRolesRequest;
import com.company.hrms.iam.api.response.role.RoleListResponse;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 查詢系統角色列表 Application Service
 *
 * <p>命名規範：{動詞}{名詞}ServiceImpl</p>
 * <p>對應 Controller 方法：getSystemRoles</p>
 */
@Service("getSystemRolesServiceImpl")
@Transactional(readOnly = true)
public class GetSystemRolesServiceImpl implements QueryApiService<GetSystemRolesRequest, List<RoleListResponse>> {

    private final IRoleRepository roleRepository;

    @Autowired
    public GetSystemRolesServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleListResponse> getResponse(GetSystemRolesRequest request, JWTModel currentUser, String... args) throws Exception {
        List<Role> systemRoles = roleRepository.findSystemRoles();

        return systemRoles.stream()
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
