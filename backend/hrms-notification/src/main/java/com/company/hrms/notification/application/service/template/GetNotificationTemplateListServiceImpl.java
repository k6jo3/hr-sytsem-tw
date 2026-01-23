package com.company.hrms.notification.application.service.template;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.querydsl.model.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.request.template.SearchTemplateRequest;
import com.company.hrms.notification.api.response.template.TemplateListResponse;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import com.company.hrms.notification.infrastructure.persistence.assembler.TemplateQueryAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 查詢通知範本列表 Application Service
 *
 * @author Claude
 * @since 2025-01-23
 */
@Service("getNotificationTemplateListServiceImpl")
public class GetNotificationTemplateListServiceImpl
        implements QueryApiService<SearchTemplateRequest, TemplateListResponse> {

    private final INotificationTemplateRepository templateRepository;
    private final TemplateQueryAssembler assembler;

    public GetNotificationTemplateListServiceImpl(
            INotificationTemplateRepository templateRepository,
            TemplateQueryAssembler assembler) {
        this.templateRepository = templateRepository;
        this.assembler = assembler;
    }

    @Override
    public TemplateListResponse getResponse(
            SearchTemplateRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 1. 組裝查詢條件
        QueryGroup queryGroup = assembler.toQueryGroup(request);

        // 2. 建立分頁參數
        int page = request.getPage() != null ? request.getPage() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        Pageable pageable = PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // 3. 執行查詢
        // TODO: 需要在 Repository 增加分頁查詢方法
        List<NotificationTemplate> templates = templateRepository.findAllActive();

        // 4. 組裝回應
        List<TemplateListResponse.TemplateItem> items = templates.stream()
                .map(this::toTemplateItem)
                .collect(Collectors.toList());

        TemplateListResponse.PaginationInfo pagination = TemplateListResponse.PaginationInfo.builder()
                .currentPage(page)
                .pageSize(pageSize)
                .totalItems((long) templates.size())
                .totalPages((int) Math.ceil((double) templates.size() / pageSize))
                .build();

        return TemplateListResponse.builder()
                .items(items)
                .pagination(pagination)
                .build();
    }

    /**
     * 轉換為範本項目
     */
    private TemplateListResponse.TemplateItem toTemplateItem(NotificationTemplate template) {
        List<String> channelNames = template.getDefaultChannels().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return TemplateListResponse.TemplateItem.builder()
                .templateId(template.getId().getValue())
                .templateCode(template.getTemplateCode())
                .templateName(template.getName())
                .subject(template.getSubject())
                .notificationType(template.getNotificationType().name())
                .defaultChannels(channelNames)
                .isActive(template.isActive())
                .variableCount(template.getVariables() != null ? template.getVariables().size() : 0)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
