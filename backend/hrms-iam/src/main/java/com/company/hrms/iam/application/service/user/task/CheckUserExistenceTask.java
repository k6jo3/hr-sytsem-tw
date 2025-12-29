package com.company.hrms.iam.application.service.user.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.user.context.UserPipelineContext;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.repository.IUserRepository;

@Component
public class CheckUserExistenceTask implements PipelineTask<UserPipelineContext> {

    private final IUserRepository userRepository;

    public CheckUserExistenceTask(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(UserPipelineContext context) throws Exception {
        var request = context.getCreateRequest();

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DomainException("USERNAME_EXISTS", "使用者名稱已存在: " + request.getUsername());
        }

        if (userRepository.existsByEmail(new Email(request.getEmail()))) {
            throw new DomainException("EMAIL_EXISTS", "Email 已存在: " + request.getEmail());
        }
    }
}
