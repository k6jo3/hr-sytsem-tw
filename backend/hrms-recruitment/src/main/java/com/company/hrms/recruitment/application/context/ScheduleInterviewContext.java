package com.company.hrms.recruitment.application.context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.application.dto.interview.ScheduleInterviewRequest;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 安排面試 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleInterviewContext extends PipelineContext {

    // === 輸入 ===
    private ScheduleInterviewRequest request;
    private String operatorId;

    // === 中間資料 ===
    private Candidate candidate;
    private CandidateId candidateId;
    private int interviewRound;
    private InterviewType interviewType;
    private LocalDateTime interviewDate;
    private String location;
    private List<UUID> interviewerIds;

    // === 輸出 ===
    private Interview interview;
    private InterviewResponse response;

    public static ScheduleInterviewContext of(ScheduleInterviewRequest request, String operatorId) {
        ScheduleInterviewContext ctx = new ScheduleInterviewContext();
        ctx.request = request;
        ctx.operatorId = operatorId;
        return ctx;
    }
}
