package com.company.hrms.reporting.application.service.dashboard;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.reporting.api.response.DeleteDashboardResponse;
import com.company.hrms.reporting.domain.model.dashboard.DashboardId;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;

import lombok.RequiredArgsConstructor;

/**
 * 刪除儀表板 Service
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("deleteDashboardServiceImpl")
@RequiredArgsConstructor
public class DeleteDashboardServiceImpl
        implements CommandApiService<Void, DeleteDashboardResponse> {

    private final IDashboardRepository dashboardRepository;

    @Override
    @Transactional
    public DeleteDashboardResponse execCommand(
            Void request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 從 PathVariable 取得 dashboardId
        String dashboardIdStr = args[0];
        DashboardId dashboardId = DashboardId.of(UUID.fromString(dashboardIdStr));

        // 檢查是否存在
        if (!dashboardRepository.exists(dashboardId)) {
            throw new IllegalArgumentException("儀表板不存在");
        }

        // 刪除
        dashboardRepository.delete(dashboardId);

        return DeleteDashboardResponse.builder()
                .message("儀表板刪除成功")
                .build();
    }
}
