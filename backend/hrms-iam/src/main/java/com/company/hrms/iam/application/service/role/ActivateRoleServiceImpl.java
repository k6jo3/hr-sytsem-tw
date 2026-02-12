package com.company.hrms.iam.application.service.role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.response.role.RoleDetailResponse;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.application.service.role.task.ActivateRoleTask;
import com.company.hrms.iam.application.service.role.task.LoadRoleTask;
import com.company.hrms.iam.application.service.role.task.SaveRoleTask;
import com.company.hrms.iam.domain.model.aggregate.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 啟用角色 Application Service (Pipeline 模式)
 */
@Service("activateRoleServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivateRoleServiceImpl
                implements CommandApiService<Object, RoleDetailResponse> {

        private final LoadRoleTask loadRoleTask;
        private final ActivateRoleTask activateRoleTask;
        private final SaveRoleTask saveRoleTask;

        @Override
        public RoleDetailResponse execCommand(Object request, JWTModel currentUser, String... args)
                        throws Exception {

                String roleId = args[0];
                log.info("啟用角色: roleId={}", roleId);

                RoleContext context = new RoleContext(roleId);

                BusinessPipeline.start(context)
                                .next(loadRoleTask)
                                .next(activateRoleTask)
                                .next(saveRoleTask)
                                .execute();

                Role role = context.getRole();
                log.info("角色啟用成功: roleId={}", roleId);

                return RoleDetailResponse.builder()
                                .roleId(role.getId().getValue())
                                .roleCode(role.getRoleCode())
                                .roleName(role.getRoleName())
                                .status(role.getStatus().name())
                                .build();
        }
}
