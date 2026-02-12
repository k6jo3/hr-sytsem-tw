package com.company.hrms.iam.application.service.role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.role.CreateRoleRequest;
import com.company.hrms.iam.api.response.role.CreateRoleResponse;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.application.service.role.task.CheckRoleCodeExistenceTask;
import com.company.hrms.iam.application.service.role.task.CreateRoleAggregateTask;
import com.company.hrms.iam.application.service.role.task.PublishRoleCreatedEventTask;
import com.company.hrms.iam.application.service.role.task.SaveRoleTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立角色 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: POST /api/v1/roles
 * </p>
 */
@Service("createRoleServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateRoleServiceImpl
                implements CommandApiService<CreateRoleRequest, CreateRoleResponse> {

        private final CheckRoleCodeExistenceTask checkRoleCodeExistenceTask;
        private final CreateRoleAggregateTask createRoleAggregateTask;
        private final SaveRoleTask saveRoleTask;
        private final PublishRoleCreatedEventTask publishRoleCreatedEventTask;

        @Override
        public CreateRoleResponse execCommand(CreateRoleRequest request, JWTModel currentUser, String... args)
                        throws Exception {

                String tenantId = currentUser != null ? currentUser.getTenantId() : null;
                log.info("建立角色: code={}", request.getRoleCode());

                RoleContext context = new RoleContext(request, tenantId);

                BusinessPipeline.start(context)
                                .next(checkRoleCodeExistenceTask)
                                .next(createRoleAggregateTask)
                                .next(saveRoleTask)
                                .next(publishRoleCreatedEventTask)
                                .execute();

                var role = context.getRole();
                log.info("角色建立成功: id={}, code={}", role.getId().getValue(), role.getRoleCode());

                return CreateRoleResponse.builder()
                                .roleId(role.getId().getValue())
                                .roleCode(role.getRoleCode())
                                .roleName(role.getRoleName())
                                .build();
        }
}
