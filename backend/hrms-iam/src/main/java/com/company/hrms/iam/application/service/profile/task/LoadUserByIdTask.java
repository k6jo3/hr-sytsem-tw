package com.company.hrms.iam.application.service.profile.task;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.profile.context.ProfileContext;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入使用者 by ID Task
 */
@Component
@RequiredArgsConstructor
public class LoadUserByIdTask implements PipelineTask<ProfileContext> {

    private final IUserRepository userRepository;

    @Override
    public void execute(ProfileContext context) throws Exception {
        String userId = context.getUserId();

        var user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND",
                        "使用者不存在: " + userId));

        context.setUser(user);
    }

    @Override
    public String getName() {
        return "載入使用者";
    }
}
