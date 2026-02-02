package com.company.hrms.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.project.api.request.AddProjectMemberRequest;
import com.company.hrms.project.api.response.AddProjectMemberResponse;
import com.company.hrms.project.application.service.context.AddProjectMemberContext;
import com.company.hrms.project.application.service.task.AddMemberToProjectTask;
import com.company.hrms.project.application.service.task.LoadProjectTask;
import com.company.hrms.project.application.service.task.SaveProjectTask;

import lombok.RequiredArgsConstructor;

@Service("addProjectMemberServiceImpl")
@RequiredArgsConstructor
@Transactional
public class AddProjectMemberServiceImpl
        implements CommandApiService<AddProjectMemberRequest, AddProjectMemberResponse> {

    private final LoadProjectTask loadProjectTask;
    private final AddMemberToProjectTask addMemberToProjectTask;
    private final SaveProjectTask saveProjectTask;

    @Override
    public AddProjectMemberResponse execCommand(AddProjectMemberRequest req, JWTModel currentUser, String... args)
            throws Exception {

        AddProjectMemberContext context = new AddProjectMemberContext(req);

        BusinessPipeline.start(context)
                .next(loadProjectTask)
                .next(addMemberToProjectTask)
                .next(saveProjectTask)
                .execute();

        return new AddProjectMemberResponse(true);
    }
}
