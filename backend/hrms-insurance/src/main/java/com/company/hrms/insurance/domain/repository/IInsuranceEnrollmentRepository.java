package com.company.hrms.insurance.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;

/**
 * 加退保記錄Repository介面
 */
public interface IInsuranceEnrollmentRepository {

    /**
     * 新增加退保記錄（使用 persist）
     */
    InsuranceEnrollment save(InsuranceEnrollment enrollment);

    /**
     * 更新既有加退保記錄（使用 merge，適用於退保、調整等操作）
     */
    InsuranceEnrollment update(InsuranceEnrollment enrollment);

    /**
     * 根據ID查詢
     */
    Optional<InsuranceEnrollment> findById(EnrollmentId id);

    /**
     * 根據員工ID查詢所有加退保記錄
     */
    List<InsuranceEnrollment> findByEmployeeId(String employeeId);

    /**
     * 根據員工ID和保險類型查詢有效的加保記錄
     */
    Optional<InsuranceEnrollment> findActiveByEmployeeIdAndType(String employeeId, InsuranceType type);

    /**
     * 根據員工ID查詢所有有效的加保記錄
     */
    List<InsuranceEnrollment> findAllActiveByEmployeeId(String employeeId);

    /**
     * 根據日期區間查詢加退保記錄 (用於申報檔匯出)
     */
    List<InsuranceEnrollment> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查詢所有加退保記錄
     */
    List<InsuranceEnrollment> findAll();
}
