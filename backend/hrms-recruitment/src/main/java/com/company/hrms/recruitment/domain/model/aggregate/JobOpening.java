package com.company.hrms.recruitment.domain.model.aggregate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.recruitment.domain.event.JobOpeningCreatedEvent;
import com.company.hrms.recruitment.domain.model.valueobject.EmploymentType;
import com.company.hrms.recruitment.domain.model.valueobject.JobStatus;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.model.valueobject.SalaryRange;

/**
 * 職缺聚合根
 * 
 * 管理職缺的完整生命週期，包含狀態轉換和招募進度追蹤。
 * 
 * 狀態流程：
 * DRAFT → OPEN → CLOSED / FILLED
 */
public class JobOpening extends AggregateRoot<OpeningId> {

    /**
     * 職位名稱
     */
    private String jobTitle;

    /**
     * 部門 ID
     */
    private UUID departmentId;

    /**
     * 需求人數
     */
    private int numberOfPositions;

    /**
     * 已錄取人數
     */
    private int filledPositions;

    /**
     * 薪資範圍
     */
    private SalaryRange salaryRange;

    /**
     * 職位要求
     */
    private String requirements;

    /**
     * 工作職責
     */
    private String responsibilities;

    /**
     * 雇用類型
     */
    private EmploymentType employmentType;

    /**
     * 工作地點
     */
    private String workLocation;

    /**
     * 職缺狀態
     */
    private JobStatus status;

    /**
     * 開放日期
     */
    private LocalDate openDate;

    /**
     * 關閉日期
     */
    private LocalDate closeDate;

    /**
     * 關閉原因
     */
    private String closeReason;

    /**
     * Domain 建構子
     */
    private JobOpening(OpeningId id) {
        super(id);
    }

    /**
     * 建立新職缺
     */
    public static JobOpening create(String jobTitle, UUID departmentId, int numberOfPositions) {
        validateJobTitle(jobTitle);
        validateDepartmentId(departmentId);
        validateNumberOfPositions(numberOfPositions);

        OpeningId openingId = OpeningId.create();
        JobOpening job = new JobOpening(openingId);
        job.jobTitle = jobTitle;
        job.departmentId = departmentId;
        job.numberOfPositions = numberOfPositions;
        job.filledPositions = 0;
        job.status = JobStatus.DRAFT;
        job.employmentType = EmploymentType.FULL_TIME;

        return job;
    }

    /**
     * 重建職缺（從資料庫載入）
     */
    public static JobOpening reconstitute(
            OpeningId openingId,
            String jobTitle,
            UUID departmentId,
            int numberOfPositions,
            int filledPositions,
            SalaryRange salaryRange,
            String requirements,
            String responsibilities,
            EmploymentType employmentType,
            String workLocation,
            JobStatus status,
            LocalDate openDate,
            LocalDate closeDate,
            String closeReason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        JobOpening job = new JobOpening(openingId);
        job.jobTitle = jobTitle;
        job.departmentId = departmentId;
        job.numberOfPositions = numberOfPositions;
        job.filledPositions = filledPositions;
        job.salaryRange = salaryRange;
        job.requirements = requirements;
        job.responsibilities = responsibilities;
        job.employmentType = employmentType;
        job.workLocation = workLocation;
        job.status = status;
        job.openDate = openDate;
        job.closeDate = closeDate;
        job.closeReason = closeReason;
        job.createdAt = createdAt;
        job.updatedAt = updatedAt;

        return job;
    }

    // === 狀態轉換方法 ===

    /**
     * 發布職缺
     */
    public void publish() {
        if (this.status != JobStatus.DRAFT) {
            throw new IllegalStateException("只有草稿狀態可以發布，當前狀態：" + this.status.getDisplayName());
        }
        this.status = JobStatus.OPEN;
        this.openDate = LocalDate.now();
        touch();

        registerEvent(JobOpeningCreatedEvent.create(
                this.getId(),
                this.jobTitle,
                this.departmentId,
                this.numberOfPositions));
    }

    /**
     * 關閉職缺
     */
    public void close(String reason) {
        if (this.status != JobStatus.OPEN) {
            throw new IllegalStateException("只有開放中狀態可以關閉，當前狀態：" + this.status.getDisplayName());
        }
        this.status = JobStatus.CLOSED;
        this.closeDate = LocalDate.now();
        this.closeReason = reason;
        touch();
    }

    /**
     * 增加已錄取人數
     */
    public void incrementFilledPositions() {
        if (this.status != JobStatus.OPEN) {
            throw new IllegalStateException("只有開放中狀態可以增加錄取人數，當前狀態：" + this.status.getDisplayName());
        }
        this.filledPositions++;
        touch();

        // 檢查是否已滿額
        if (this.filledPositions >= this.numberOfPositions) {
            this.status = JobStatus.FILLED;
            this.closeDate = LocalDate.now();
        }
    }

    // === 更新方法 ===

    /**
     * 設定薪資範圍
     */
    public void setSalaryRange(SalaryRange salaryRange) {
        validateEditable();
        this.salaryRange = salaryRange;
        touch();
    }

    /**
     * 設定職位要求
     */
    public void setRequirements(String requirements) {
        validateEditable();
        this.requirements = requirements;
        touch();
    }

    /**
     * 設定工作職責
     */
    public void setResponsibilities(String responsibilities) {
        validateEditable();
        this.responsibilities = responsibilities;
        touch();
    }

    /**
     * 設定雇用類型
     */
    public void setEmploymentType(EmploymentType employmentType) {
        validateEditable();
        this.employmentType = employmentType;
        touch();
    }

    /**
     * 設定工作地點
     */
    public void setWorkLocation(String workLocation) {
        validateEditable();
        this.workLocation = workLocation;
        touch();
    }

    /**
     * 更新需求人數
     */
    public void updateNumberOfPositions(int numberOfPositions) {
        validateEditable();
        if (numberOfPositions < this.filledPositions) {
            throw new IllegalArgumentException("需求人數不可少於已錄取人數");
        }
        validateNumberOfPositions(numberOfPositions);
        this.numberOfPositions = numberOfPositions;
        touch();
    }

    // === 驗證方法 ===

    private void validateEditable() {
        if (this.status == JobStatus.CLOSED || this.status == JobStatus.FILLED) {
            throw new IllegalStateException("已關閉或已滿額的職缺無法編輯");
        }
    }

    private static void validateJobTitle(String jobTitle) {
        if (jobTitle == null || jobTitle.isBlank()) {
            throw new IllegalArgumentException("職位名稱不可為空");
        }
        if (jobTitle.length() > 100) {
            throw new IllegalArgumentException("職位名稱長度不可超過 100 字元");
        }
    }

    private static void validateDepartmentId(UUID departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("部門 ID 不可為空");
        }
    }

    private static void validateNumberOfPositions(int numberOfPositions) {
        if (numberOfPositions < 1) {
            throw new IllegalArgumentException("需求人數必須大於等於 1");
        }
    }

    // === Getters ===

    public String getJobTitle() {
        return jobTitle;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public int getNumberOfPositions() {
        return numberOfPositions;
    }

    public int getFilledPositions() {
        return filledPositions;
    }

    public SalaryRange getSalaryRange() {
        return salaryRange;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public JobStatus getStatus() {
        return status;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public String getCloseReason() {
        return closeReason;
    }
}
