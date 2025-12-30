package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入使用者（by username）Task
 */
@Component
@RequiredArgsConstructor
public class LoadUserByUsernameTask implements PipelineTask<AuthContext> {

    private final IUserRepository userRepository;

    @Override
    public void execute(AuthContext context) throws Exception {
        var request = context.getLoginRequest();

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new DomainException("LOGIN_FAILED", "使用者名稱或密碼錯誤"));

        context.setUser(user);
    }

    @Override
    public String getName() {
        return "載入使用者";
    }
}
