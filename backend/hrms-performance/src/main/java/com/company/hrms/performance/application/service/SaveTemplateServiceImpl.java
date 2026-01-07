package com.company.hrms.performance.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.SaveTemplateRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.application.service.context.StartCycleContext;
import com.company.hrms.performance.application.service.task.LoadCycleTask;
import com.company.hrms.performance.application.service.task.PublishCycleEventsTask;
import com.company.hrms.performance.application.service.task.SaveCycleTask;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;

import lombok.RequiredArgsConstructor;

/**
 * 儲存考核範本 Service (Business Pipeline 架構)
 */
@Service("saveTemplateServiceImpl")
@RequiredArgsConstructor
@Transactional
public class SaveTemplateServiceImpl implements CommandApiService<SaveTemplateRequest, SuccessResponse> {

        private final LoadCycleTask loadCycleTask;
        private final SaveCycleTask saveCycleTask;
        private final PublishCycleEventsTask publishEventsTask;

        @Override
        public SuccessResponse execCommand(SaveTemplateRequest req, JWTModel currentUser, String... args)
                        throws Exception {

                StartCycleContext ctx = new StartCycleContext(req.getCycleId());

                BusinessPipeline.start(ctx)
                                .next(loadCycleTask)
                                        // 建立並儲存範本 (使用 Domain 方法)
                                        List<EvaluationItem> items = req.getItems().stream()
                                                .map(itemReq -> EvaluationItem.createDefinition(
                                                        itemReq.getItemName(),
                                                        itemReq.getWeight(),
                                                        itemReq.getDescription(),
                                                        itemReq.getCriteria()))
                                                .toList();

                                        EvaluationTemplate template = EvaluationTemplate.create(
                                                        req.getTemplateName(),
                                                        req.getScoringSystem(),
                                                        req.getEnableDistribution()); // Request Field: enableDistribution, Domain arg name: forcedDistribution
                                        
                                        // Set items
                                        template.setEvaluationItems(items);
                                        
                                        context.getCycle().saveTemplate(template);
                                }).next(saveCycleTask).next(publishEventsTask).execute();

        return SuccessResponse.of("考核範本已儲存");
}}
