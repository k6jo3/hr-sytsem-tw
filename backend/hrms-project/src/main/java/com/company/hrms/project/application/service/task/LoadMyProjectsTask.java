package com.company.hrms.project.application.service.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.application.service.context.MyProjectsContext;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入我的專案 Task
 * 
 * Infrastructure Task: 負責從 Repository 載入使用者參與的專案
 */
@Component
@RequiredArgsConstructor
public class LoadMyProjectsTask implements PipelineTask<MyProjectsContext> {

    private final IProjectRepository projectRepository;

    @Override
    public void execute(MyProjectsContext context) throws Exception {
        Pageable pageable = PageRequest.of(context.getPage(), context.getSize());

        // 查詢使用者參與的專案（透過 ProjectMember）
        Page<Project> projects = projectRepository.findByMemberEmployeeId(
                context.getEmployeeId(),
                pageable);

        context.setProjects(projects);
    }
}
