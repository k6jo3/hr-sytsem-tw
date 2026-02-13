package com.company.hrms.iam.application.service.profile.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.iam.api.request.profile.ChangePasswordRequest;
import com.company.hrms.iam.api.request.profile.UpdateProfileRequest;
import com.company.hrms.iam.domain.model.aggregate.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Profile Pipeline Context
 */
@Getter
@Setter
@NoArgsConstructor
public class ProfileContext extends PipelineContext {

    // === 輸入 ===
    private String userId;
    private ChangePasswordRequest changePasswordRequest;
    private UpdateProfileRequest updateProfileRequest;

    // === 中間數據 ===
    private User user;
    private String newPasswordHash;
    private java.util.List<String> permissions;

    // === 建構子 ===

    public ProfileContext(String userId) {
        this.userId = userId;
    }

    public ProfileContext(String userId, ChangePasswordRequest request) {
        this.userId = userId;
        this.changePasswordRequest = request;
    }

    public ProfileContext(String userId, UpdateProfileRequest request) {
        this.userId = userId;
        this.updateProfileRequest = request;
    }
}
