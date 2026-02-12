package com.company.hrms.iam.application.service.role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.role.AssignPermissionsRequest;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.application.service.role.task.AssignPermissionsTask;
import com.company.hrms.iam.application.service.role.task.LoadRoleTask;
import com.company.hrms.iam.application.service.role.task.PublishRolePermissionsUpdatedTask;
import com.company.hrms.iam.application.service.role.task.UpdateRoleTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 指派權限 Application Service (Pipeline 模式)
 */
@Service("assignPermissionsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssignPermissionsServiceImpl
        implements CommandApiService<AssignPermissionsRequest, Void> {

    private final LoadRoleTask loadRoleTask;
    private final AssignPermissionsTask assignPermissionsTask;
    private final UpdateRoleTask updateRoleTask;
    private final PublishRolePermissionsUpdatedTask publishRolePermissionsUpdatedTask;

    @Override
    public Void execCommand(AssignPermissionsRequest request, JWTModel currentUser, String... args)
            throws Exception {

        String roleId = args[0];
        log.info("指派權限: roleId={}", roleId);

        RoleContext context = new RoleContext(roleId, request);

        BusinessPipeline.start(context)
                .next(loadRoleTask)
                .next(assignPermissionsTask)
                .next(updateRoleTask)
                .next(publishRolePermissionsUpdatedTask)
                .execute();

        log.info("權限指派成功: roleId={}", roleId);
        return null;
    }
}
