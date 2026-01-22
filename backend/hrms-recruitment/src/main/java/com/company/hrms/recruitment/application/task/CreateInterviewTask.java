package com.company.hrms.recruitment.application.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.ScheduleInterviewContext;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.repository.IInterviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 建立面試 Task
 */
@Component
@RequiredArgsConstructor
public class CreateInterviewTask implements PipelineTask<ScheduleInterviewContext> {

    private final IInterviewRepository interviewRepository;

    @Override
    public void execute(ScheduleInterviewContext ctx) {
        // 使用 Domain Model 建立面試
        Interview interview = Interview.schedule(
                ctx.getCandidateId(),
                ctx.getCandidate().getFullName(),
                ctx.getInterviewRound(),
                ctx.getInterviewType(),
                ctx.getInterviewDate(),
                ctx.getLocation(),
                ctx.getInterviewerIds());

        // 儲存
        Interview saved = interviewRepository.save(interview);
        ctx.setInterview(saved);
    }
}
