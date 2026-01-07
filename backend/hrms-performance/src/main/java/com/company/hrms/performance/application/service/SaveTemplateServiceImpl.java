package com.company.hrms.performance.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.SaveTemplateRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceCycle;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;
import com.company.hrms.performance.domain.model.valueobject.EvaluationTemplate;
import com.company.hrms.performance.domain.repository.IPerformanceCycleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 儲存考核表單範本 Service
 */
@Service("saveTemplateServiceImpl")
@RequiredArgsConstructor
@Transactional
public class SaveTemplateServiceImpl implements CommandApiService<SaveTemplateRequest, SuccessResponse> {

    private final IPerformanceCycleRepository cycleRepository;
    private final EventPublisher eventPublisher;

    @Override
    public SuccessResponse execCommand(SaveTemplateRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 查詢週期
        PerformanceCycle cycle = cycleRepository.findById(CycleId.of(req.getCycleId()))
                .orElseThrow(() -> new IllegalArgumentException("考核週期不存在"));

        // 建立評估項目列表
        List<EvaluationItem> items = req.getItems().stream()
                .map(item -> EvaluationItem.createDefinition(
                        item.getItemName(),
                        item.getWeight(),
                        item.getDescription(),
                        item.getCriteria()))
                .collect(Collectors.toList());

        // 建立範本
        EvaluationTemplate template = EvaluationTemplate.create(
                req.getTemplateName(),
                req.getScoringSystem(),
                req.getEnableDistribution() != null && req.getEnableDistribution());

        // 加入評估項目
        items.forEach(template::addEvaluationItem);

        // 儲存範本
        cycle.saveTemplate(template);
        cycleRepository.save(cycle);

        eventPublisher.publishAll(cycle.getDomainEvents());
        cycle.clearDomainEvents();

        return SuccessResponse.of("範本儲存成功");
    }
}
