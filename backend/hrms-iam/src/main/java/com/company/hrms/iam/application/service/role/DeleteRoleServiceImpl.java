package com.company.hrms.iam.application.service.role;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.application.service.role.task.DeleteRoleTask;
import com.company.hrms.iam.application.service.role.task.LoadRoleTask;
import com.company.hrms.iam.application.service.role.task.PublishRoleDeletedEventTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 刪除角色 Application Service (Pipeline 模式)
 */
@Service("deleteRoleServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteRoleServiceImpl
        implements CommandApiService<Object, Void> {

    private final LoadRoleTask loadRoleTask;
    private final DeleteRoleTask deleteRoleTask;
    private final PublishRoleDeletedEventTask publishRoleDeletedEventTask;

    @Override
    public Void execCommand(Object request, JWTModel currentUser, String... args)
            throws Exception {

        String roleId = args[0];
        log.info("刪除角色: roleId={}", roleId);

        RoleContext context = new RoleContext(roleId);

        BusinessPipeline.start(context)
                .next(loadRoleTask)
                .next(deleteRoleTask)
                .next(publishRoleDeletedEventTask)
                .execute();

        log.info("角色刪除成功: roleId={}", roleId);
        return null;
    }
}
