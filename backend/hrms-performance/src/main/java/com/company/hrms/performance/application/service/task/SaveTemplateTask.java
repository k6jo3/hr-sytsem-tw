package com.company.hrms.performance.application.service.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.performance.application.service.context.SaveTemplateContext;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;
import com.company.hrms.performance.domain.model.valueobject.EvaluationTemplate;

/**
 * 儲存考核範本 Task
 */
@Component
public class SaveTemplateTask implements PipelineTask<SaveTemplateContext> {

    @Override
    public void execute(SaveTemplateContext context) {
        var req = context.getRequest();

        // 建立評估項目列表
        List<EvaluationItem> items = req.getItems().stream()
                .map(itemReq -> EvaluationItem.createDefinition(
                        itemReq.getItemName(),
                        itemReq.getWeight(),
                        itemReq.getDescription(),
                        itemReq.getCriteria()))
                .toList();

        // 建立範本
        // Request Field: enableDistribution, Domain arg name: forcedDistribution
        EvaluationTemplate template = EvaluationTemplate.create(
                req.getTemplateName(),
                req.getScoringSystem(),
                req.getEnableDistribution());

        // Set items
        template.setEvaluationItems(items);

        // 儲存到 Cycle
        context.getCycle().saveTemplate(template);
    }
}
