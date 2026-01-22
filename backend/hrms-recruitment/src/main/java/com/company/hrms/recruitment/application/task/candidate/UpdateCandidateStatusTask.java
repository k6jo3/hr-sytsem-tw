package com.company.hrms.recruitment.application.task.candidate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.context.UpdateCandidateStatusContext;
import com.company.hrms.recruitment.application.dto.candidate.UpdateCandidateStatusRequest;
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;

@Component
public class UpdateCandidateStatusTask implements PipelineTask<UpdateCandidateStatusContext> {

    @Override
    public void execute(UpdateCandidateStatusContext context) {
        Candidate candidate = context.getCandidate();
        UpdateCandidateStatusRequest request = context.getRequest();

        CandidateStatus newStatus;
        try {
            newStatus = CandidateStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的狀態: " + request.getStatus());
        }

        // 根據新狀態呼叫對應的 domain method
        switch (newStatus) {
            case SCREENING:
                candidate.passScreening();
                break;
            case INTERVIEWING:
                candidate.moveToInterview();
                break;
            case OFFERED:
                candidate.sendOffer();
                break;
            case HIRED:
                candidate.hire();
                break;
            case REJECTED:
                if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
                    throw new IllegalArgumentException("拒絕狀態必須提供拒絕原因");
                }
                candidate.reject(request.getRejectionReason());
                break;
            default:
                throw new IllegalArgumentException("不允許直接更新至此狀態: " + newStatus);
        }
    }
}
