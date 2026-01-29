package com.company.hrms.document.application.service.generate.task;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.generate.context.GenerateDocumentContext;

import lombok.RequiredArgsConstructor;

/**
 * 獲取員工資料任務
 */
@Component
@RequiredArgsConstructor
public class FetchEmployeeDataTask implements PipelineTask<GenerateDocumentContext> {

    @Override
    public void execute(GenerateDocumentContext context) {
        String employeeId = context.getRequest().getEmployeeId();

        // TODO: 將來應呼叫 Organization Service 獲取真實資料
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("employeeId", employeeId);
        dataModel.put("employeeName", "張三"); // 模擬資料
        dataModel.put("department", "研發部");
        dataModel.put("jobTitle", "資深工程師");
        dataModel.put("onboardDate", LocalDate.now().minusYears(2).toString());
        dataModel.put("today", LocalDate.now().toString());

        // 合併請求中的參數
        if (context.getRequest().getVariables() != null) {
            dataModel.putAll(context.getRequest().getVariables());
        }

        context.setDataModel(dataModel);
    }
}
