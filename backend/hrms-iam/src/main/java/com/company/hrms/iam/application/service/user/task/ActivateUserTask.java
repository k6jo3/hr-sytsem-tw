package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.model.aggregate.User;

/**
 * 啟用使用者 Task
 * 
 * <p>
 * 呼叫 User 聚合根的 activate() 方法
 * </p>
 */
@Component
public class ActivateUserTask implements PipelineTask<UserPipelineContext> {

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        User user = context.getUser();
        user.activate();
    }
}
