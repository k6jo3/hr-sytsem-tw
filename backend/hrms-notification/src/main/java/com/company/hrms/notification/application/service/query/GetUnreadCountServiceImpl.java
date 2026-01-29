package com.company.hrms.notification.application.service.query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.notification.api.response.notification.UnreadCountResponse;
import com.company.hrms.notification.domain.repository.INotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢未讀通知數量 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("getUnreadCountServiceImpl")
@RequiredArgsConstructor
public class GetUnreadCountServiceImpl
        implements QueryApiService<Void, UnreadCountResponse> {

    private final INotificationRepository notificationRepository;

    @Override
    public UnreadCountResponse getResponse(
            Void request,
            JWTModel currentUser,
            String... args) throws Exception {

        String employeeId = currentUser.getEmployeeNumber();

        // 查詢未讀總數
        long unreadCount = notificationRepository.countUnreadByRecipientId(employeeId);

        // 回傳結果（類型分組功能暫時使用空 Map）
        Map<String, Long> byType = new HashMap<>();

        return UnreadCountResponse.builder()
                .unreadCount(unreadCount)
                .byType(byType)
                .build();
    }
}
