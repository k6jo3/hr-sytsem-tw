package com.company.hrms.training.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.training.api.request.ExportReportRequest;
import com.company.hrms.training.api.response.FileResponse;

import lombok.RequiredArgsConstructor;

@Service("exportTrainingReportServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExportTrainingReportServiceImpl implements CommandApiService<ExportReportRequest, FileResponse> {

    @Override
    public FileResponse execCommand(ExportReportRequest req, JWTModel currentUser, String... args) throws Exception {
        // Mock generation
        // In real implementation, use Apache POI or iText to generate file

        FileResponse res = new FileResponse();
        res.setFileName("TrainingReport_" + System.currentTimeMillis() + ".xlsx");
        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        res.setContent("UEsDBBQACAgIAAAAAAA="); // Mock Base64 (empty ZIP header)

        return res;
    }
}
