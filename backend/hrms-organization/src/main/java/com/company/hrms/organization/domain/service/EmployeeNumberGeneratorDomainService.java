package com.company.hrms.organization.domain.service;

import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 員工編號生成 Domain Service
 * 負責生成唯一的員工編號
 *
 * 員工編號格式: EMP + YYYYMM + 4位流水號
 * 例如: EMP202412-0001
 */
@Service
@RequiredArgsConstructor
public class EmployeeNumberGeneratorDomainService {

    private final IEmployeeRepository employeeRepository;

    /**
     * 員工編號前綴
     */
    private static final String EMPLOYEE_NUMBER_PREFIX = "EMP";

    /**
     * 日期格式
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * 流水號格式 (4位數)
     */
    private static final String SEQUENCE_FORMAT = "%04d";

    /**
     * 生成新的員工編號
     * @return 新的員工編號
     */
    public String generateEmployeeNumber() {
        String yearMonth = LocalDate.now().format(DATE_FORMATTER);
        String prefix = EMPLOYEE_NUMBER_PREFIX + yearMonth + "-";

        // 查詢當月最大流水號
        int maxSequence = employeeRepository.findMaxSequenceByPrefix(prefix);
        int nextSequence = maxSequence + 1;

        return prefix + String.format(SEQUENCE_FORMAT, nextSequence);
    }

    /**
     * 驗證員工編號格式
     * @param employeeNumber 員工編號
     * @return 是否為有效格式
     */
    public boolean isValidFormat(String employeeNumber) {
        if (employeeNumber == null || employeeNumber.isEmpty()) {
            return false;
        }

        // 格式: EMP202412-0001
        return employeeNumber.matches("^EMP\\d{6}-\\d{4}$");
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
     * 生成指定前綴的員工編號 (用於特殊情況，如約聘人員)
     * @param customPrefix 自訂前綴
     * @return 新的員工編號
     */
    public String generateWithCustomPrefix(String customPrefix) {
        String yearMonth = LocalDate.now().format(DATE_FORMATTER);
        String prefix = customPrefix + yearMonth + "-";

        int maxSequence = employeeRepository.findMaxSequenceByPrefix(prefix);
        int nextSequence = maxSequence + 1;

        return prefix + String.format(SEQUENCE_FORMAT, nextSequence);
    }
}
