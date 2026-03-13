package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.model.aggregate.User;

@Component
public class CreateUserAggregateTask implements PipelineTask<UserPipelineContext> {

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        var request = context.getCreateRequest();

        // 若未提供 displayName，則使用 username 作為預設值
        String displayName = StringUtils.hasText(request.getDisplayName())
                ? request.getDisplayName()
                : request.getUsername();

        // 從 Context 取得 tenantId（由 Security Context 或請求提供）
        String tenantId = context.getCurrentUser() != null
                ? context.getCurrentUser().getTenantId()
                : null;

        User user = User.createWithTenant(
                request.getUsername(),
                request.getEmail(),
                context.getPasswordHash(),
                displayName,
                request.getEmployeeId(),
                tenantId);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.activate();
        context.setUser(user);
    }
}
