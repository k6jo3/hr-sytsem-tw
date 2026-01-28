package com.company.hrms.notification.application.service.announcement;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.notification.api.response.announcement.WithdrawAnnouncementResponse;
import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.model.valueobject.AnnouncementId;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 撤銷公告 Application Service
 *
 * @author Claude
 * @since 2026-01-28
 */
@Slf4j
@Service("withdrawAnnouncementServiceImpl")
@RequiredArgsConstructor
@Transactional
public class WithdrawAnnouncementServiceImpl
        implements CommandApiService<String, WithdrawAnnouncementResponse> {

    private final IAnnouncementRepository announcementRepository;

    @Override
    public WithdrawAnnouncementResponse execCommand(
            String announcementIdStr,
            JWTModel currentUser,
            String... args) throws Exception {

        // 查詢公告
        Announcement announcement = announcementRepository
                .findById(AnnouncementId.of(announcementIdStr))
                .orElseThrow(() -> new EntityNotFoundException("Announcement", announcementIdStr));

        // 撤銷公告
        announcement.withdraw();

        // 儲存
        announcementRepository.save(announcement);

        log.info("公告已撤銷 - 公告ID: {}", announcementIdStr);

        return WithdrawAnnouncementResponse.builder()
                .announcementId(announcementIdStr)
                .status("WITHDRAWN")
                .withdrawnAt(LocalDateTime.now())
                .build();
    }
}
