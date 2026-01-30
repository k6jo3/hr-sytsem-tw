package com.company.hrms.training.application.task.statistics;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.statistics.ExportTrainingStatisticsContext;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;
import com.company.hrms.training.infrastructure.repository.TrainingEnrollmentQueryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FetchTrainingEnrollmentsTask implements PipelineTask<ExportTrainingStatisticsContext> {

    private final TrainingEnrollmentQueryRepository enrollmentRepository;

    @Override
    public void execute(ExportTrainingStatisticsContext context) {
        List<TrainingEnrollmentEntity> enrollments = enrollmentRepository.findCompletedInPeriod(context.getStartDate(),
                context.getEndDate());
        context.setEnrollments(enrollments);
    }
}
