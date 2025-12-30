package com.company.hrms.iam.application.service.profile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.response.profile.ProfileResponse;
import com.company.hrms.iam.application.service.profile.context.ProfileContext;
import com.company.hrms.iam.application.service.profile.task.LoadUserByIdTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢個人資料 Application Service (Pipeline 模式)
 */
@Service("getProfileServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetProfileServiceImpl
        implements QueryApiService<Void, ProfileResponse> {

    private final LoadUserByIdTask loadUserByIdTask;

    @Override
    public ProfileResponse getResponse(Void request, JWTModel currentUser, String... args)
            throws Exception {

        String userId = currentUser.getUserId();
        log.info("查詢個人資料: userId={}", userId);

        ProfileContext context = new ProfileContext(userId);

        BusinessPipeline.start(context)
                .next(loadUserByIdTask)
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
                .lastLoginAt(user.getLastLoginAt())
                .mustChangePassword(user.isMustChangePassword())
                .build();
    }
}
