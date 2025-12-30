package com.company.hrms.iam.application.service.auth.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查找使用者 by Email Task (安全模式：不拋出異常)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FindUserByEmailTask implements PipelineTask<AuthContext> {

    private final IUserRepository userRepository;

    @Override
    public void execute(AuthContext context) throws Exception {
        var request = context.getForgotPasswordRequest();
        String email = request.getEmail();

        // 安全模式：不拋出異常，防止 Email 枚舉攻擊
        var user = userRepository.findByEmail(new Email(email)).orElse(null);

        if (user != null && user.isActive()) {
            context.setUser(user);
            log.debug("找到使用者: username={}", user.getUsername());
        } else {
            log.info("Email 不存在或使用者已停用: {}", email);
        }
    }

    @Override
    public String getName() {
        return "查找使用者 by Email";
    }
}
