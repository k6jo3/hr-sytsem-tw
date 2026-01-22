package com.company.hrms.training.application.service.context.statistics;

import java.time.LocalDate;
import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.training.api.request.TrainingStatisticsQuery;
import com.company.hrms.training.api.response.ExportResponse;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExportTrainingStatisticsContext extends PipelineContext {
    private TrainingStatisticsQuery query;
    private JWTModel currentUser;

    private LocalDate startDate;
    private LocalDate endDate;
    private List<TrainingEnrollmentEntity> enrollments;
    private byte[] excelData;
    private ExportResponse response;

    public ExportTrainingStatisticsContext(TrainingStatisticsQuery query, JWTModel currentUser) {
        this.query = query;
        this.currentUser = currentUser;
    }
}
