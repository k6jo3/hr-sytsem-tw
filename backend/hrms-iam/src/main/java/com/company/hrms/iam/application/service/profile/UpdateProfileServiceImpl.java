package com.company.hrms.iam.application.service.profile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.profile.UpdateProfileRequest;
import com.company.hrms.iam.api.response.profile.ProfileResponse;
import com.company.hrms.iam.application.service.profile.context.ProfileContext;
import com.company.hrms.iam.application.service.profile.task.LoadPermissionsTask;
import com.company.hrms.iam.application.service.profile.task.LoadUserByIdTask;
import com.company.hrms.iam.application.service.profile.task.UpdateProfileTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新個人資料 Application Service (Pipeline 模式)
 */
@Service("updateProfileServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateProfileServiceImpl
                implements CommandApiService<UpdateProfileRequest, ProfileResponse> {

        private final LoadUserByIdTask loadUserByIdTask;
        private final UpdateProfileTask updateProfileTask;
        private final LoadPermissionsTask loadPermissionsTask;

        @Override
        public ProfileResponse execCommand(UpdateProfileRequest request, JWTModel currentUser, String... args)
                        throws Exception {

                String userId = currentUser.getUserId();
                log.info("更新個人資料: userId={}", userId);

                ProfileContext context = new ProfileContext(userId, request);

                BusinessPipeline.start(context)
                                .next(loadUserByIdTask)
                                .next(updateProfileTask)
                                .next(loadPermissionsTask)
                                .execute();

                var user = context.getUser();

                return ProfileResponse.builder()
                                .userId(user.getId().getValue())
                                .username(user.getUsername())
                                .email(user.getEmail().getValue())
                                .displayName(user.getDisplayName())
                                .employeeId(user.getEmployeeId())
                                .tenantId(user.getTenantId())
                                .roles(user.getRoles())
                                .permissions(context.getPermissions())
                                .preferredLanguage(user.getPreferredLanguage())
                                .timezone(user.getTimezone())
                                .lastLoginAt(user.getLastLoginAt())
                                .passwordChangedAt(user.getPasswordChangedAt())
                                .createdAt(user.getCreatedAt())
                                .mustChangePassword(user.isMustChangePassword())
                                .build();
        }
}
