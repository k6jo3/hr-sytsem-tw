package com.company.hrms.iam.application.service.auth;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.auth.AdminResetPasswordRequest;
import com.company.hrms.iam.api.response.auth.ResetPasswordResponse;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.application.service.auth.task.AdminResetPasswordTask;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 管理員重設使用者密碼 Application Service (Pipeline 模式)
 */
@Service("adminResetPasswordServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminResetPasswordServiceImpl
        implements CommandApiService<AdminResetPasswordRequest, ResetPasswordResponse> {

    private final IUserRepository userRepository;
    private final AdminResetPasswordTask adminResetPasswordTask;

    @Override
    public ResetPasswordResponse execCommand(AdminResetPasswordRequest request, JWTModel currentUser, String... args)
            throws Exception {

        String targetUserId = args[0];
        log.info("管理員重設密碼: targetUserId={}", targetUserId);

        // 建立 Context 並載入使用者
        AuthContext context = new AuthContext(request, targetUserId);
        var user = userRepository.findById(new UserId(targetUserId))
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "使用者不存在"));
        context.setUser(user);

        BusinessPipeline.start(context)
                .next(adminResetPasswordTask)
                .execute();

        String temporaryPassword = (String) context.getAttribute("temporaryPassword");

        return ResetPasswordResponse.builder()
                .success(true)
                .message("使用者密碼已重設")
                .temporaryPassword(temporaryPassword)
                .build();
    }
}
