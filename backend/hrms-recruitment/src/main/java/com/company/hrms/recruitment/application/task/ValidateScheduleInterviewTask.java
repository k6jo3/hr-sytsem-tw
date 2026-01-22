package com.company.hrms.recruitment.application.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.ScheduleInterviewContext;
import com.company.hrms.recruitment.application.dto.interview.ScheduleInterviewRequest;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

import lombok.RequiredArgsConstructor;

/**
 * 驗證排程面試請求 Task
 */
@Component
@RequiredArgsConstructor
public class ValidateScheduleInterviewTask implements PipelineTask<ScheduleInterviewContext> {

    private final ICandidateRepository candidateRepository;

    @Override
    public void execute(ScheduleInterviewContext ctx) {
        ScheduleInterviewRequest req = ctx.getRequest();

        // 驗證應徵者存在
        CandidateId candidateId = CandidateId.of(req.getCandidateId());
        var candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("應徵者不存在：" + req.getCandidateId()));

        // 設定 Context
        ctx.setCandidate(candidate);
        ctx.setCandidateId(candidateId);
        ctx.setInterviewRound(req.getInterviewRound());
        ctx.setInterviewType(req.getInterviewType());
        ctx.setInterviewDate(req.getInterviewDate());
        ctx.setLocation(req.getLocation());
        ctx.setInterviewerIds(req.getInterviewerIds());
    }
}
