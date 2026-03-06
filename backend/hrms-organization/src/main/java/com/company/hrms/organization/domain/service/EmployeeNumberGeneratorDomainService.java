package com.company.hrms.organization.domain.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.infrastructure.config.EmployeeNumberProperties;

import lombok.RequiredArgsConstructor;

/**
 * 員工編號生成 Domain Service
 * 負責生成唯一的員工編號
 *
 * 支援兩種格式（由系統參數設定）：
 * - YYYYMM-NNNN: 前綴 + 年月 + 流水號（如 EMP202412-0001）
 * - NNNN: 前綴 + 流水號（如 EMP0001）
 */
@Service
@RequiredArgsConstructor
public class EmployeeNumberGeneratorDomainService {

    private final IEmployeeRepository employeeRepository;
    private final EmployeeNumberProperties config;

    /** 日期格式 */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * 生成新的員工編號（依系統參數設定）
     */
    public String generateEmployeeNumber() {
        String prefix = buildPrefix(config.getPrefix());
        String seqFormat = "%" + String.format("0%dd", config.getSeqDigits());

        int maxSequence = employeeRepository.findMaxSequenceByPrefix(prefix);
        int nextSequence = maxSequence + 1;

        return prefix + String.format(seqFormat, nextSequence);
    }

    /**
     * 驗證員工編號格式
     */
    public boolean isValidFormat(String employeeNumber) {
        if (employeeNumber == null || employeeNumber.isEmpty()) {
            return false;
        }

        String formatType = config.getFormat();
        String prefix = config.getPrefix();
        int digits = config.getSeqDigits();

        if ("NNNN".equals(formatType)) {
            // 格式: EMP0001
            return employeeNumber.matches("^" + prefix + "\\d{" + digits + "}$");
        }
        // 預設 YYYYMM-NNNN 格式: EMP202412-0001
        return employeeNumber.matches("^" + prefix + "\\d{6}-\\d{" + digits + "}$");
    }

    /**
     * 檢查員工編號是否已存在
     */
    public boolean isEmployeeNumberExists(String employeeNumber) {
        return employeeRepository.existsByEmployeeNumber(employeeNumber);
    }

    /**
     * 生成指定前綴的員工編號（用於特殊情況，如約聘人員）
     */
    public String generateWithCustomPrefix(String customPrefix) {
        String prefix = buildPrefix(customPrefix);
        String seqFormat = "%" + String.format("0%dd", config.getSeqDigits());

        int maxSequence = employeeRepository.findMaxSequenceByPrefix(prefix);
        int nextSequence = maxSequence + 1;

        return prefix + String.format(seqFormat, nextSequence);
    }

    /**
     * 根據格式設定組合前綴
     * YYYYMM-NNNN → EMP202412-
     * NNNN → EMP
     */
    private String buildPrefix(String basePrefix) {
        if ("NNNN".equals(config.getFormat())) {
            return basePrefix;
        }
        // YYYYMM-NNNN
        String yearMonth = LocalDate.now().format(DATE_FORMATTER);
        return basePrefix + yearMonth + "-";
    }
}
