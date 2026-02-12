package com.company.hrms.iam.application.service.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.user.AssignUserRolesRequest;
import com.company.hrms.iam.api.response.user.AssignUserRolesResponse;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * 指派角色給使用者 Application Service
 * 
 * <p>
 * 對應 API: PUT /api/v1/users/{userId}/roles
 * </p>
 */
@Service("assignUserRolesServiceImpl")
@Transactional
public class AssignUserRolesServiceImpl implements CommandApiService<AssignUserRolesRequest, AssignUserRolesResponse> {

        private final IUserRepository userRepository;
        private final IRoleRepository roleRepository;
        private final com.company.hrms.common.domain.event.EventPublisher eventPublisher;

        public AssignUserRolesServiceImpl(IUserRepository userRepository,
                        IRoleRepository roleRepository,
                        com.company.hrms.common.domain.event.EventPublisher eventPublisher) {
                this.userRepository = userRepository;
                this.roleRepository = roleRepository;
                this.eventPublisher = eventPublisher;
        }

        @Override
        public AssignUserRolesResponse execCommand(AssignUserRolesRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {
                String userId = args[0];

                // 1. 載入使用者
                userRepository.findById(new UserId(userId))
                                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND",
                                                "使用者不存在: " + userId));

                // 2. 驗證並載入角色
                List<Role> roles = new ArrayList<>();
                for (String roleId : request.getRoleIds()) {
                        Role role = roleRepository.findById(RoleId.of(roleId))
                                        .orElseThrow(() -> new ResourceNotFoundException("ROLE_NOT_FOUND",
                                                        "角色不存在: " + roleId));
                        roles.add(role);
                }

                // 3. 更新使用者角色 (透過 Repository)
                userRepository.updateUserRoles(new UserId(userId), request.getRoleIds());

                // 發布領域事件
                eventPublisher.publish(new com.company.hrms.iam.domain.event.UserRolesAssignedEvent(userId,
                                request.getRoleIds()));

                // 4. 回傳結果
                List<AssignUserRolesResponse.RoleInfo> roleInfos = new ArrayList<>();
                for (Role role : roles) {
                        roleInfos.add(AssignUserRolesResponse.RoleInfo.builder()
                                        .roleId(role.getId().getValue())
                                        .roleName(role.getRoleCode())
                                        .displayName(role.getRoleName())
                                        .build());
                }

                return AssignUserRolesResponse.builder()
                                .userId(userId)
                                .roles(roleInfos)
                                .build();
        }
}
