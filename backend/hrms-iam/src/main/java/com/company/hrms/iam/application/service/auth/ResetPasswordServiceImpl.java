package com.company.hrms.iam.application.service.auth;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.auth.ResetPasswordRequest;
import com.company.hrms.iam.api.response.auth.ResetPasswordResponse;
import com.company.hrms.iam.application.service.auth.context.AuthContext;
import com.company.hrms.iam.application.service.auth.task.ResetPasswordTask;
import com.company.hrms.iam.application.service.auth.task.ValidateResetPasswordTask;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 使用者自行重設密碼 Application Service (Pipeline 模式)
 */
@Service("resetPasswordServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResetPasswordServiceImpl
                implements CommandApiService<ResetPasswordRequest, ResetPasswordResponse> {

        private final IUserRepository userRepository;
        private final ValidateResetPasswordTask validateResetPasswordTask;
        private final ResetPasswordTask resetPasswordTask;
        private final com.company.hrms.iam.domain.service.PasswordResetTokenDomainService passwordResetTokenService;
        private final com.company.hrms.common.domain.event.EventPublisher eventPublisher;

        @Override
        public ResetPasswordResponse execCommand(ResetPasswordRequest request, JWTModel currentUser, String... args)
                        throws Exception {

                AuthContext context = new AuthContext(request);

                if (request.getToken() != null && !request.getToken().isBlank()) {
                        // Token-based reset
                        String userId = passwordResetTokenService.validateToken(request.getToken());
                        if (userId == null) {
                                throw new DomainException("INVALID_TOKEN", "Token 無效或已過期");
                        }
                        var user = userRepository.findById(new UserId(userId))
                                        .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "使用者不存在"));
                        context.setUser(user);
                } else {
                        // Authenticated reset
                        if (currentUser == null || currentUser.getUserId() == null) {
                                throw new DomainException("UNAUTHORIZED", "請先登入");
                        }

                        log.info("重設密碼: userId={}", currentUser.getUserId());

                        // 載入使用者
                        var user = userRepository.findById(new UserId(currentUser.getUserId()))
                                        .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "使用者不存在"));
                        context.setUser(user);
                }

                BusinessPipeline.start(context)
                                .next(validateResetPasswordTask)
                                .next(resetPasswordTask)
                                .execute();

                // Use token if present (invalidate it)
                if (request.getToken() != null && !request.getToken().isBlank()) {
                        passwordResetTokenService.invalidateToken(request.getToken());
                }

                // Publish event
                eventPublisher.publish(new com.company.hrms.iam.domain.event.PasswordResetCompletedEvent(
                                context.getUser().getId().getValue()));

                return ResetPasswordResponse.builder()
                                .success(true)
                                .message("密碼已成功更新")
                                .build();
        }
}
