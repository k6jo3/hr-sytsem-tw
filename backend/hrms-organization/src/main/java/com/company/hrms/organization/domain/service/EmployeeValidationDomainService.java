package com.company.hrms.organization.domain.service;

import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.Email;
import com.company.hrms.organization.domain.model.valueobject.NationalId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * 員工資料驗證 Domain Service
 * 負責驗證員工資料的完整性和合規性
 */
@Service
@RequiredArgsConstructor
public class EmployeeValidationDomainService {

    private final IEmployeeRepository employeeRepository;

    /**
     * 最小工作年齡 (勞基法規定)
     */
    private static final int MIN_WORKING_AGE = 15;

    /**
     * 最大工作年齡
     */
    private static final int MAX_WORKING_AGE = 70;

    /**
     * 驗證結果
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, new ArrayList<>());
        }

        public static ValidationResult failure(List<String> errors) {
            return new ValidationResult(false, errors);
        }
    }

    /**
     * 驗證新進員工資料
     * @param employee 員工
     * @return 驗證結果
     */
    public ValidationResult validateNewEmployee(Employee employee) {
        List<String> errors = new ArrayList<>();

        // 驗證身分證字號唯一性
        if (isNationalIdExists(employee.getNationalId())) {
            errors.add("身分證字號已存在");
        }

        // 驗證電子郵件唯一性
        if (isEmailExists(employee.getEmail())) {
            errors.add("電子郵件已存在");
        }

        // 驗證員工編號唯一性
        if (isEmployeeNumberExists(employee.getEmployeeNumber())) {
            errors.add("員工編號已存在");
        }

        // 驗證年齡
        if (!isValidWorkingAge(employee.getBirthDate())) {
            errors.add("員工年齡不符合勞基法規定");
        }

        // 驗證入職日期
        if (!isValidHireDate(employee.getHireDate())) {
            errors.add("入職日期無效");
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }

    /**
     * 驗證員工更新資料
     * @param employee 員工
     * @param originalEmployee 原始員工資料
     * @return 驗證結果
     */
    public ValidationResult validateEmployeeUpdate(Employee employee, Employee originalEmployee) {
        List<String> errors = new ArrayList<>();

        // 如果電子郵件有變更，檢查唯一性
        if (!employee.getEmail().equals(originalEmployee.getEmail())) {
            if (isEmailExists(employee.getEmail())) {
                errors.add("電子郵件已存在");
            }
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }

    /**
     * 檢查身分證字號是否已存在
     * @param nationalId 身分證字號
     * @return 是否已存在
     */
    public boolean isNationalIdExists(NationalId nationalId) {
        return employeeRepository.existsByNationalId(nationalId);
    }

    /**
     * 檢查電子郵件是否已存在
     * @param email 電子郵件
     * @return 是否已存在
     */
    public boolean isEmailExists(Email email) {
        return employeeRepository.existsByEmail(email);
    }

    /**
     * 檢查員工編號是否已存在
     * @param employeeNumber 員工編號
     * @return 是否已存在
     */
    public boolean isEmployeeNumberExists(String employeeNumber) {
        return employeeRepository.existsByEmployeeNumber(employeeNumber);
    }

    /**
     * 驗證年齡是否符合工作規定
     * @param birthDate 出生日期
     * @return 是否符合
     */
    public boolean isValidWorkingAge(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return age >= MIN_WORKING_AGE && age <= MAX_WORKING_AGE;
    }

    /**
     * 驗證入職日期
     * @param hireDate 入職日期
     * @return 是否有效
     */
    public boolean isValidHireDate(LocalDate hireDate) {
        if (hireDate == null) {
            return false;
        }

        // 入職日期不能早於一年前
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        // 入職日期不能晚於三個月後
        LocalDate threeMonthsLater = LocalDate.now().plusMonths(3);

        return !hireDate.isBefore(oneYearAgo) && !hireDate.isAfter(threeMonthsLater);
    }

    /**
     * 驗證試用期長度
     * @param probationMonths 試用期月數
     * @return 是否有效 (通常為0-6個月)
     */
    public boolean isValidProbationPeriod(int probationMonths) {
        return probationMonths >= 0 && probationMonths <= 6;
    }

    /**
     * 計算員工年齡
     * @param birthDate 出生日期
     * @return 年齡
     */
    public int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
