package com.company.hrms.iam.application.service.user.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * 指派角色給使用者 Task
 * 在建立使用者時，將 roleIds 指派給新建的使用者
 */
@Component
public class AssignUserRolesTask implements PipelineTask<UserPipelineContext> {

    private final IUserRepository userRepository;

    public AssignUserRolesTask(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        var request = context.getCreateRequest();

        // 只在建立使用者時執行（更新使用者有專門的 API）
        if (request == null || request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            return;
        }

        List<String> roleIds = request.getRoleIds();

        // 指派角色給使用者（使用 User 的 UserId）
        userRepository.updateUserRoles(context.getUser().getId(), roleIds);
    }
}
