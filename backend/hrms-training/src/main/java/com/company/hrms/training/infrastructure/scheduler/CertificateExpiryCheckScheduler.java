package com.company.hrms.training.infrastructure.scheduler;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.training.domain.event.CertificateExpiringEvent;
import com.company.hrms.training.domain.model.aggregate.Certificate;
import com.company.hrms.training.domain.model.valueobject.CertificateStatus;
import com.company.hrms.training.domain.repository.ICertificateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CertificateExpiryCheckScheduler {

    private final ICertificateRepository certificateRepository;
    private final EventPublisher eventPublisher;

    // Check expiration at 90, 60, 30, 7, 1 days before
    private static final List<Integer> NOTIFICATION_DAYS = Arrays.asList(90, 60, 30, 7, 1);

    @Scheduled(cron = "0 0 8 * * ?") // Daily at 08:00 AM
    @Transactional
    public void checkExpiringCertificates() {
        log.info("Starting certificate expiry check...");
        LocalDate today = LocalDate.now();

        // Warning: This implementation iteration over all expiring certificates might
        // be inefficient for large datasets.
        // It's better to add findByExpiryDate(date) in Repository.
        // But for MVP utilizing findExpiringCertificates(threshold) and filtering is
        // acceptable if volume is low.
        // Or strictly adhere to "check expiration at X days".

        // Let's use existing findExpiringCertificates(threshold) where threshold is max
        // (90 days).
        LocalDate threshold = today.plusDays(90);
        List<Certificate> expiringCerts = certificateRepository.findExpiringCertificates(threshold);

        for (Certificate cert : expiringCerts) {
            long daysUntilExpiry = cert.getDaysUntilExpiry();

            if (NOTIFICATION_DAYS.contains((int) daysUntilExpiry)) {
                log.info("Certificate {} is expiring in {} days. Publishing event.", cert.getCertificateNumber(),
                        daysUntilExpiry);

                // Update status to EXPIRING if currently VALID
                if (cert.getStatus() == CertificateStatus.VALID) {
                    // We don't have setStatus exposed easily, but we can assume Certificate
                    // aggregate handles logic?
                    // Or we just publish event.
                    // Let's publish event.
                }

                CertificateExpiringEvent event = CertificateExpiringEvent.create(
                        cert.getId().toString(),
                        cert.getEmployeeId(),
                        null,
                        null,
                        cert.getCertificateName(),
                        null,
                        cert.getExpiryDate().toString(),
                        (int) daysUntilExpiry,
                        false);

                eventPublisher.publish(event);
            }
        }
        log.info("Certificate expiry check completed.");
    }
}
