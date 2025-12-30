package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 記錄登入 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecordLoginTask implements PipelineTask<AuthContext> {

    private final IUserRepository userRepository;

    @Override
    public void execute(AuthContext context) throws Exception {
        var user = context.getUser();

        user.recordLogin();
        userRepository.update(user);

        log.info("使用者登入成功: username={}", user.getUsername());
    }

    @Override
    public String getName() {
        return "記錄登入";
    }
}
