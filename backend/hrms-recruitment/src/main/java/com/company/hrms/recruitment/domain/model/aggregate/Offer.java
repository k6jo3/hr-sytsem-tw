package com.company.hrms.recruitment.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.recruitment.domain.event.OfferSentEvent;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.OfferId;
import com.company.hrms.recruitment.domain.model.valueobject.OfferStatus;

/**
 * Offer 聚合根
 * 
 * 管理 Offer 的生命週期：建立、接受、拒絕、過期。
 */
public class Offer extends AggregateRoot<OfferId> {

    /**
     * 應徵者 ID
     */
    private CandidateId candidateId;

    /**
     * 應徵者姓名（快照）
     */
    private String candidateName;

    /**
     * 錄取職位
     */
    private String offeredPosition;

    /**
     * 錄取薪資
     */
    private BigDecimal offeredSalary;

    /**
     * 預計到職日
     */
    private LocalDate offeredStartDate;

    /**
     * Offer 發送日期
     */
    private LocalDate offerDate;

    /**
     * Offer 到期日
     */
    private LocalDate expiryDate;

    /**
     * Offer 狀態
     */
    private OfferStatus status;

    /**
     * 回覆日期
     */
    private LocalDate responseDate;

    /**
     * 拒絕原因
     */
    private String rejectionReason;

    /**
     * Domain 建構子
     */
    private Offer(OfferId id) {
        super(id);
    }

    /**
     * 建立 Offer
     */
    public static Offer create(
            CandidateId candidateId,
            String candidateName,
            String offeredPosition,
            BigDecimal offeredSalary,
            LocalDate offeredStartDate,
            LocalDate expiryDate) {

        validateCandidateId(candidateId);
        validateOfferedPosition(offeredPosition);
        validateOfferedSalary(offeredSalary);
        validateExpiryDate(expiryDate);

        OfferId offerId = OfferId.create();
        Offer offer = new Offer(offerId);
        offer.candidateId = candidateId;
        offer.candidateName = candidateName;
        offer.offeredPosition = offeredPosition;
        offer.offeredSalary = offeredSalary;
        offer.offeredStartDate = offeredStartDate;
        offer.offerDate = LocalDate.now();
        offer.expiryDate = expiryDate;
        offer.status = OfferStatus.PENDING;

        // 發布 Offer 發送事件
        offer.registerEvent(OfferSentEvent.create(
                offerId,
                candidateId,
                candidateName,
                offeredPosition,
                offeredSalary,
                expiryDate));

        return offer;
    }

    /**
     * 重建 Offer（從資料庫載入）
     */
    public static Offer reconstitute(
            OfferId offerId,
            CandidateId candidateId,
            String candidateName,
            String offeredPosition,
            BigDecimal offeredSalary,
            LocalDate offeredStartDate,
            LocalDate offerDate,
            LocalDate expiryDate,
            OfferStatus status,
            LocalDate responseDate,
            String rejectionReason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        Offer offer = new Offer(offerId);
        offer.candidateId = candidateId;
        offer.candidateName = candidateName;
        offer.offeredPosition = offeredPosition;
        offer.offeredSalary = offeredSalary;
        offer.offeredStartDate = offeredStartDate;
        offer.offerDate = offerDate;
        offer.expiryDate = expiryDate;
        offer.status = status;
        offer.responseDate = responseDate;
        offer.rejectionReason = rejectionReason;
        offer.createdAt = createdAt;
        offer.updatedAt = updatedAt;

        return offer;
    }

    // === 狀態轉換方法 ===

    /**
     * 接受 Offer
     */
    public void accept() {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("只有待處理狀態可以接受，當前狀態：" + this.status.getDisplayName());
        }
        checkNotExpired();

        this.status = OfferStatus.ACCEPTED;
        this.responseDate = LocalDate.now();
        touch();
    }

    /**
     * 拒絕 Offer
     */
    public void reject(String reason) {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("只有待處理狀態可以拒絕，當前狀態：" + this.status.getDisplayName());
        }

        this.status = OfferStatus.REJECTED;
        this.rejectionReason = reason;
        this.responseDate = LocalDate.now();
        touch();
    }

    /**
     * 標記為過期
     */
    public void expire() {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("只有待處理狀態可以過期，當前狀態：" + this.status.getDisplayName());
        }

        this.status = OfferStatus.EXPIRED;
        touch();
    }

    /**
     * 撤回 Offer
     */
    public void withdraw() {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("只有待處理狀態可以撤回，當前狀態：" + this.status.getDisplayName());
        }

        this.status = OfferStatus.WITHDRAWN;
        touch();
    }

    /**
     * 檢查 Offer 是否已過期
     */
    public boolean isExpired() {
        return this.expiryDate != null && LocalDate.now().isAfter(this.expiryDate);
    }

    /**
     * 延長到期日
     */
    public void extendExpiryDate(LocalDate newExpiryDate) {
        if (this.status != OfferStatus.PENDING) {
            throw new IllegalStateException("只有待處理狀態可以延長到期日");
        }
        if (newExpiryDate == null || !newExpiryDate.isAfter(this.expiryDate)) {
            throw new IllegalArgumentException("新到期日必須晚於原到期日");
        }
        this.expiryDate = newExpiryDate;
        touch();
    }

    // === 驗證方法 ===

    private void checkNotExpired() {
        if (isExpired()) {
            throw new IllegalStateException("Offer 已過期");
        }
    }

    private static void validateCandidateId(CandidateId candidateId) {
        if (candidateId == null) {
            throw new IllegalArgumentException("應徵者 ID 不可為空");
        }
    }

    private static void validateOfferedPosition(String position) {
        if (position == null || position.isBlank()) {
            throw new IllegalArgumentException("錄取職位不可為空");
        }
    }

    private static void validateOfferedSalary(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("錄取薪資必須大於 0");
        }
    }

    private static void validateExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null) {
            throw new IllegalArgumentException("到期日不可為空");
        }
        if (!expiryDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("到期日必須晚於今日");
        }
    }

    // === Getters ===

    public CandidateId getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getOfferedPosition() {
        return offeredPosition;
    }

    public BigDecimal getOfferedSalary() {
        return offeredSalary;
    }

    public LocalDate getOfferedStartDate() {
        return offeredStartDate;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public LocalDate getResponseDate() {
        return responseDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}
