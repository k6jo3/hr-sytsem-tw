package com.company.hrms.organization.domain.model.entity;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.ExperienceId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * 工作經歷實體
 */
@Getter
@Builder
public class WorkExperience {

    /**
     * 經歷 ID
     */
    private final ExperienceId id;

    /**
     * 員工 ID
     */
    private UUID employeeId;

    /**
     * 公司名稱
     */
    private String company;

    /**
     * 職稱
     */
    private String jobTitle;

    /**
     * 入職日期
     */
    private LocalDate startDate;

    /**
     * 離職日期 (null 表示目前在職)
     */
    private LocalDate endDate;

    /**
     * 工作描述
     */
    private String description;

    // ==================== 工廠方法 ====================

    /**
     * 建立工作經歷記錄
     * @param employeeId 員工 ID
     * @param company 公司名稱
     * @param jobTitle 職稱
     * @param startDate 入職日期
     * @param endDate 離職日期
     * @param description 工作描述
     * @return 新的工作經歷實例
     */
    public static WorkExperience create(
            UUID employeeId,
            String company,
            String jobTitle,
            LocalDate startDate,
            LocalDate endDate,
            String description) {

        if (employeeId == null) {
            throw new DomainException("EMPLOYEE_ID_REQUIRED", "員工 ID 不可為空");
        }
        if (company == null || company.isBlank()) {
            throw new DomainException("COMPANY_REQUIRED", "公司名稱不可為空");
        }
        if (jobTitle == null || jobTitle.isBlank()) {
            throw new DomainException("JOB_TITLE_REQUIRED", "職稱不可為空");
        }
        if (startDate == null) {
            throw new DomainException("START_DATE_REQUIRED", "入職日期不可為空");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new DomainException("INVALID_DATE_RANGE", "離職日期不可早於入職日期");
        }

        return WorkExperience.builder()
                .id(ExperienceId.generate())
                .employeeId(employeeId)
                .company(company)
                .jobTitle(jobTitle)
                .startDate(startDate)
                .endDate(endDate)
                .description(description)
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 更新工作經歷
     * @param company 公司
     * @param jobTitle 職稱
     * @param startDate 入職日期
     * @param endDate 離職日期
     * @param description 描述
     */
    public void update(String company, String jobTitle, LocalDate startDate,
                       LocalDate endDate, String description) {
        if (company != null && !company.isBlank()) {
            this.company = company;
        }
        if (jobTitle != null && !jobTitle.isBlank()) {
            this.jobTitle = jobTitle;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        this.endDate = endDate;
        this.description = description;
    }

    /**
     * 是否為目前工作
     * @return 是否為目前工作
     */
    public boolean isCurrent() {
        return this.endDate == null;
    }

    /**
     * 計算年資 (月)
     * @return 年資月數
     */
    public long getMonthsWorked() {
        LocalDate end = this.endDate != null ? this.endDate : LocalDate.now();
        return ChronoUnit.MONTHS.between(this.startDate, end);
    }

    /**
     * 取得期間描述
     * @return 期間描述
     */
    public String getPeriodDescription() {
        String start = this.startDate.toString();
        String end = this.endDate != null ? this.endDate.toString() : "迄今";
        return start + " ~ " + end;
    }
}
