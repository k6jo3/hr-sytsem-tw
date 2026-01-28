package com.company.hrms.notification.application.service.notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.response.notification.MarkNotificationReadResponse;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.valueobject.NotificationId;
import com.company.hrms.notification.domain.repository.INotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 標記通知為已讀 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("markNotificationReadServiceImpl")
@RequiredArgsConstructor
@Transactional
public class MarkNotificationReadServiceImpl
                implements CommandApiService<String, MarkNotificationReadResponse> {

        private final INotificationRepository notificationRepository;

        @Override
        public MarkNotificationReadResponse execCommand(
                        String notificationId,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 查詢通知
                Notification notification = notificationRepository
                                .findById(NotificationId.of(notificationId))
                                .orElseThrow(() -> new EntityNotFoundException("Notification", notificationId));

                // 驗證權限：收件人必須是當前使用者
                if (!notification.getRecipientId().equals(currentUser.getEmployeeNumber())) {
                        throw new IllegalStateException("您不是此通知的收件人");
                }

                // 標記為已讀
                notification.markAsRead();

                // 儲存
                notificationRepository.save(notification);

                log.info("通知已標記為已讀 - 通知ID: {}, 使用者: {}",
                                notificationId, currentUser.getEmployeeNumber());

                return MarkNotificationReadResponse.builder()
                                .notificationId(notification.getId().getValue())
                                .status("READ")
                                .readAt(notification.getReadAt())
                                .build();
        }
}
