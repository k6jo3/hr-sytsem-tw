package com.company.hrms.iam.application.service.user.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
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
