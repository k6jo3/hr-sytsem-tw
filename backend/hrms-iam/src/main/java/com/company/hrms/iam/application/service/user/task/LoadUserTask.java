package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;

@Component
public class LoadUserTask implements PipelineTask<UserPipelineContext> {

    private final IUserRepository userRepository;

    public LoadUserTask(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        // 從 context 中獲取 userId，這個 userId 應該在 Controller 層傳入
        String userId = context.getAttribute("userId");
        if (userId == null) {
            throw new IllegalArgumentException("UserId not found in context");
        }

        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "使用者不存在: " + userId));

        context.setUser(user);
    }
}
