package com.company.hrms.notification.application.service.query;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.response.notification.NotificationDetailResponse;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.valueobject.NotificationId;
import com.company.hrms.notification.domain.repository.INotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢通知詳情 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("getNotificationDetailServiceImpl")
@RequiredArgsConstructor
public class GetNotificationDetailServiceImpl
                implements QueryApiService<Void, NotificationDetailResponse> {

        private final INotificationRepository notificationRepository;

        @Override
        public NotificationDetailResponse getResponse(
                        Void request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 取得通知 ID
                String notificationId = args[0];

                // 查詢通知
                Notification notification = notificationRepository
                                .findById(NotificationId.of(notificationId))
                                .orElseThrow(() -> new EntityNotFoundException("Notification", notificationId));

                // 驗證權限：收件人必須是當前使用者
                if (!notification.getRecipientId().equals(currentUser.getEmployeeNumber())) {
                        throw new IllegalStateException("您不是此通知的收件人");
                }

                // 組裝回應
                return NotificationDetailResponse.builder()
                                .notificationId(notification.getId().getValue())
                                .title(notification.getTitle())
                                .content(notification.getContent())
                                .notificationType(notification.getNotificationType().name())
                                .priority(notification.getPriority().name())
                                .status(notification.getStatus().name())
                                .channels(notification.getChannels() != null
                                                ? notification.getChannels().stream()
                                                                .map(Enum::name)
                                                                .collect(Collectors.toList())
                                                : null)
                                .businessType(notification.getRelatedBusinessType())
                                .businessId(notification.getRelatedBusinessId())
                                .businessUrl(notification.getRelatedBusinessUrl())
                                .createdAt(notification.getCreatedAt())
                                .sentAt(notification.getSentAt())
                                .readAt(notification.getReadAt())
                                .build();
        }
}
