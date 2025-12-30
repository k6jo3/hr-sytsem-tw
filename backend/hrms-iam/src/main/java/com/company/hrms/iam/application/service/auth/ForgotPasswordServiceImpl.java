package com.company.hrms.iam.application.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.auth.ForgotPasswordRequest;
import com.company.hrms.iam.api.response.auth.ForgotPasswordResponse;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.application.service.auth.task.FindUserByEmailTask;
import com.company.hrms.iam.application.service.auth.task.GenerateAndSendResetEmailTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 忘記密碼 Application Service (Pipeline 模式)
 * 
 * <p>
 * 安全考量: 無論 Email 是否存在，都返回相同訊息，防止 Email 枚舉攻擊
 * </p>
 */
@Service("forgotPasswordServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ForgotPasswordServiceImpl
        implements CommandApiService<ForgotPasswordRequest, ForgotPasswordResponse> {

    private final FindUserByEmailTask findUserByEmailTask;
    private final GenerateAndSendResetEmailTask generateAndSendResetEmailTask;

    @Override
    public ForgotPasswordResponse execCommand(ForgotPasswordRequest request, JWTModel currentUser, String... args)
            throws Exception {

        log.info("收到忘記密碼請求，Email: {}", request.getEmail());

        AuthContext context = new AuthContext(request);

        BusinessPipeline.start(context)
                .next(findUserByEmailTask)
                .next(generateAndSendResetEmailTask)
                .execute();

        // 無論成功或失敗，都返回相同訊息（防止 Email 枚舉攻擊）
        return ForgotPasswordResponse.success();
    }
}
