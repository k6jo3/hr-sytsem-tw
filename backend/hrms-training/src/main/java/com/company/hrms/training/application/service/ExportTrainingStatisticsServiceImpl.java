package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.training.api.request.TrainingStatisticsQuery;
import com.company.hrms.training.api.response.ExportResponse;
import com.company.hrms.training.application.service.context.statistics.ExportTrainingStatisticsContext;
import com.company.hrms.training.application.service.task.statistics.CalculateDateRangeTask;
import com.company.hrms.training.application.service.task.statistics.ConstructExportResponseTask;
import com.company.hrms.training.application.service.task.statistics.FetchTrainingEnrollmentsTask;
import com.company.hrms.training.application.service.task.statistics.GenerateTrainingStatisticsExcelTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 匯出訓練統計報表服務
 */
@Service("exportTrainingStatisticsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ExportTrainingStatisticsServiceImpl implements QueryApiService<TrainingStatisticsQuery, ExportResponse> {

    private final CalculateDateRangeTask calculateDateRangeTask;
    private final FetchTrainingEnrollmentsTask fetchTrainingEnrollmentsTask;
    private final GenerateTrainingStatisticsExcelTask generateTrainingStatisticsExcelTask;
    private final ConstructExportResponseTask constructExportResponseTask;

    @Override
    public ExportResponse getResponse(TrainingStatisticsQuery query, JWTModel currentUser, String... args) {
        ExportTrainingStatisticsContext context = new ExportTrainingStatisticsContext(query, currentUser);

        BusinessPipeline.start(context)
                .next(calculateDateRangeTask)
                .next(fetchTrainingEnrollmentsTask)
                .next(generateTrainingStatisticsExcelTask)
                .next(constructExportResponseTask)
                .execute();

        return context.getResponse();
    }
}
