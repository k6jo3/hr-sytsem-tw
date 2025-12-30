package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入使用者 by ID (for Auth) Task
 */
@Component("loadUserByIdForAuthTask")
@RequiredArgsConstructor
public class LoadUserByIdForAuthTask implements PipelineTask<AuthContext> {

    private final IUserRepository userRepository;

    @Override
    public void execute(AuthContext context) throws Exception {
        String userId = context.getUserId();

        var user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "使用者不存在"));

        if (!user.isActive()) {
            throw new DomainException("USER_INACTIVE", "使用者帳號已停用");
        }

        context.setUser(user);
    }

    @Override
    public String getName() {
        return "載入使用者";
    }
}
