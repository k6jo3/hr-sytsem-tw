package com.company.hrms.recruitment.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.recruitment.domain.event.CandidateHiredEvent;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.model.valueobject.RecruitmentSource;

/**
 * 應徵者聚合根
 * 
 * 管理應徵者的完整生命週期，包含狀態轉換和領域事件發布。
 * 
 * 狀態流程：
 * NEW → SCREENING → INTERVIEWING → OFFERED → HIRED
 * ↓ ↓ ↓
 * REJECTED REJECTED REJECTED
 */
public class Candidate extends AggregateRoot<CandidateId> {

    /**
     * 應徵職缺 ID
     */
    private OpeningId openingId;

    /**
     * 應徵者姓名
     */
    private String fullName;

    /**
     * 電子郵件
     */
    private String email;

    /**
     * 電話號碼
     */
    private String phoneNumber;

    /**
     * 履歷檔案 URL
     */
    private String resumeUrl;

    /**
     * 履歷來源
     */
    private RecruitmentSource source;

    /**
     * 推薦人 ID（員工推薦時）
     */
    private UUID referrerId;

    /**
     * 應徵日期
     */
    private LocalDate applicationDate;

    /**
     * 應徵者狀態
     */
    private CandidateStatus status;

    /**
     * 拒絕原因
     */
    private String rejectionReason;

    /**
     * 求職信
     */
    private String coverLetter;

    /**
     * 期望薪資
     */
    private BigDecimal expectedSalary;

    /**
     * 可到職日
     */
    private LocalDate availableDate;

    /**
     * Domain 建構子
     */
    private Candidate(CandidateId id) {
        super(id);
    }

    /**
     * 建立新應徵者
     */
    public static Candidate create(
            OpeningId openingId,
            String fullName,
            String email,
            String phoneNumber,
            RecruitmentSource source) {

        validateOpeningId(openingId);
        validateFullName(fullName);
        validateEmail(email);

        CandidateId candidateId = CandidateId.create();
        Candidate candidate = new Candidate(candidateId);
        candidate.openingId = openingId;
        candidate.fullName = fullName;
        candidate.email = email;
        candidate.phoneNumber = phoneNumber;
        candidate.source = source != null ? source : RecruitmentSource.OTHER;
        candidate.status = CandidateStatus.NEW;
        candidate.applicationDate = LocalDate.now();

        return candidate;
    }

    /**
     * 重建應徵者（用於從資料庫載入）
     */
    public static Candidate reconstitute(
            CandidateId candidateId,
            OpeningId openingId,
            String fullName,
            String email,
            String phoneNumber,
            String resumeUrl,
            RecruitmentSource source,
            UUID referrerId,
            LocalDate applicationDate,
            CandidateStatus status,
            String rejectionReason,
            String coverLetter,
            BigDecimal expectedSalary,
            LocalDate availableDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        Candidate candidate = new Candidate(candidateId);
        candidate.openingId = openingId;
        candidate.fullName = fullName;
        candidate.email = email;
        candidate.phoneNumber = phoneNumber;
        candidate.resumeUrl = resumeUrl;
        candidate.source = source;
        candidate.referrerId = referrerId;
        candidate.applicationDate = applicationDate;
        candidate.status = status;
        candidate.rejectionReason = rejectionReason;
        candidate.coverLetter = coverLetter;
        candidate.expectedSalary = expectedSalary;
        candidate.availableDate = availableDate;
        candidate.createdAt = createdAt;
        candidate.updatedAt = updatedAt;

        return candidate;
    }

    // === 狀態轉換方法 ===

    /**
     * 履歷篩選通過，進入篩選中狀態
     */
    public void passScreening() {
        if (this.status != CandidateStatus.NEW) {
            throw new IllegalStateException("只有新投遞狀態可以進行篩選，當前狀態：" + this.status.getDisplayName());
        }
        this.status = CandidateStatus.SCREENING;
        touch();
    }

    /**
     * 進入面試階段
     */
    public void moveToInterview() {
        if (this.status != CandidateStatus.SCREENING) {
            throw new IllegalStateException("只有篩選中狀態可以進入面試，當前狀態：" + this.status.getDisplayName());
        }
        this.status = CandidateStatus.INTERVIEWING;
        touch();
    }

    /**
     * 發送 Offer
     */
    public void sendOffer() {
        if (this.status != CandidateStatus.INTERVIEWING) {
            throw new IllegalStateException("只有面試中狀態可以發送 Offer，當前狀態：" + this.status.getDisplayName());
        }
        this.status = CandidateStatus.OFFERED;
        touch();
    }

    /**
     * 錄取應徵者
     * 
     * 發布 CandidateHiredEvent 供 Organization Service 自動建立員工資料
     */
    public void hire() {
        if (this.status != CandidateStatus.OFFERED) {
            throw new IllegalStateException("只有已發 Offer 狀態可以錄取，當前狀態：" + this.status.getDisplayName());
        }
        this.status = CandidateStatus.HIRED;
        touch();

        // 發布錄取事件
        registerEvent(CandidateHiredEvent.create(
                this.getId(),
                this.fullName,
                this.email,
                this.phoneNumber,
                this.openingId));
    }

    /**
     * 拒絕應徵者
     */
    public void reject(String reason) {
        if (this.status == CandidateStatus.HIRED || this.status == CandidateStatus.REJECTED) {
            throw new IllegalStateException("終態狀態無法被拒絕，當前狀態：" + this.status.getDisplayName());
        }
        this.status = CandidateStatus.REJECTED;
        this.rejectionReason = reason;
        touch();
    }

    // === 其他業務方法 ===

    /**
     * 更新履歷資訊
     */
    public void updateResume(String resumeUrl, String coverLetter) {
        this.resumeUrl = resumeUrl;
        this.coverLetter = coverLetter;
        touch();
    }

    /**
     * 設定期望薪資和可到職日
     */
    public void updateExpectations(BigDecimal expectedSalary, LocalDate availableDate) {
        this.expectedSalary = expectedSalary;
        this.availableDate = availableDate;
        touch();
    }

    /**
     * 設定推薦人
     */
    public void setReferrer(UUID referrerId) {
        this.referrerId = referrerId;
        this.source = RecruitmentSource.REFERRAL;
        touch();
    }

    // === 驗證方法 ===

    private static void validateOpeningId(OpeningId openingId) {
        if (openingId == null) {
            throw new IllegalArgumentException("職缺 ID 不可為空");
        }
    }

    private static void validateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("姓名不可為空");
        }
        if (fullName.length() > 100) {
            throw new IllegalArgumentException("姓名長度不可超過 100 字元");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email 不可為空");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email 格式不正確");
        }
    }

    // === Getters ===

    public OpeningId getOpeningId() {
        return openingId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public RecruitmentSource getSource() {
        return source;
    }

    public UUID getReferrerId() {
        return referrerId;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public BigDecimal getExpectedSalary() {
        return expectedSalary;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }
}
