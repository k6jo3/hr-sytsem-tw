package com.company.hrms.iam.application.service.profile.task;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.profile.context.ProfileContext;
import com.company.hrms.iam.infrastructure.dao.PermissionDAO;
import com.company.hrms.iam.infrastructure.po.PermissionPO;

import lombok.RequiredArgsConstructor;

/**
 * 載入使用者權限 Task
 */
@Component
@RequiredArgsConstructor
public class LoadPermissionsTask implements PipelineTask<ProfileContext> {

    private final PermissionDAO permissionDAO;

    @Override
    public void execute(ProfileContext context) throws Exception {
        var permissions = permissionDAO.selectByUserId(context.getUserId());
        context.setPermissions(permissions.stream()
                .map(PermissionPO::getPermissionCode)
                .collect(Collectors.toList()));
    }

    @Override
    public String getName() {
        return "載入權限";
    }
}
