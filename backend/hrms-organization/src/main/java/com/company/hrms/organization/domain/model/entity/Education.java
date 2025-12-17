package com.company.hrms.organization.domain.model.entity;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.Degree;
import com.company.hrms.organization.domain.model.valueobject.EducationId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 學歷實體
 */
@Getter
@Builder
public class Education {

    /**
     * 學歷 ID
     */
    private final EducationId id;

    /**
     * 員工 ID
     */
    private UUID employeeId;

    /**
     * 學位
     */
    private Degree degree;

    /**
     * 學校名稱
     */
    private String school;

    /**
     * 科系/主修
     */
    private String major;

    /**
     * 入學日期
     */
    private LocalDate startDate;

    /**
     * 畢業日期
     */
    private LocalDate endDate;

    /**
     * 是否為最高學歷
     */
    private boolean isHighestDegree;

    // ==================== 工廠方法 ====================

    /**
     * 建立學歷記錄
     * @param employeeId 員工 ID
     * @param degree 學位
     * @param school 學校名稱
     * @param major 科系
     * @param startDate 入學日期
     * @param endDate 畢業日期
     * @return 新的學歷實例
     */
    public static Education create(
            UUID employeeId,
            Degree degree,
            String school,
            String major,
            LocalDate startDate,
            LocalDate endDate) {

        if (employeeId == null) {
            throw new DomainException("EMPLOYEE_ID_REQUIRED", "員工 ID 不可為空");
        }
        if (degree == null) {
            throw new DomainException("DEGREE_REQUIRED", "學位不可為空");
        }
        if (school == null || school.isBlank()) {
            throw new DomainException("SCHOOL_REQUIRED", "學校名稱不可為空");
        }

        return Education.builder()
                .id(EducationId.generate())
                .employeeId(employeeId)
                .degree(degree)
                .school(school)
                .major(major)
                .startDate(startDate)
                .endDate(endDate)
                .isHighestDegree(false)
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 標記為最高學歷
     */
    public void markAsHighest() {
        this.isHighestDegree = true;
    }

    /**
     * 取消最高學歷標記
     */
    public void unmarkAsHighest() {
        this.isHighestDegree = false;
    }

    /**
     * 更新學歷資訊
     * @param school 學校
     * @param major 科系
     * @param startDate 入學日期
     * @param endDate 畢業日期
     */
    public void update(String school, String major, LocalDate startDate, LocalDate endDate) {
        if (school != null && !school.isBlank()) {
            this.school = school;
        }
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 是否已畢業
     * @return 是否畢業
     */
    public boolean isGraduated() {
        return this.endDate != null && this.endDate.isBefore(LocalDate.now());
    }
}
