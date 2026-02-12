package com.company.hrms.iam.application.service.user.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.request.user.CreateUserRequest;
import com.company.hrms.iam.api.request.user.UpdateUserRequest;
import com.company.hrms.iam.domain.model.aggregate.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPipelineContext extends PipelineContext {
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;
    private User user;
    private String passwordHash;
    private String generatedPassword; // 系統自動產生的明文密碼（用於發送歡迎郵件）
    private JWTModel currentUser; // 當前執行操作的使用者（用於取得 tenantId 等資訊）

    public UserPipelineContext(CreateUserRequest createRequest) {
        this.createRequest = createRequest;
    }

    public UserPipelineContext(String userId) {
        this.setAttribute("userId", userId);
    }

    public UserPipelineContext(String userId, UpdateUserRequest updateRequest) {
        this.setAttribute("userId", userId);
        this.updateRequest = updateRequest;
    }
}
