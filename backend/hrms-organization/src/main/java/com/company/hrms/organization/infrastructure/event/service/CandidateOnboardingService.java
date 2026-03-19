package com.company.hrms.organization.infrastructure.event.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmploymentType;
import com.company.hrms.organization.domain.model.valueobject.Gender;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 候選人入職服務
 *
 * <p>負責將錄取的候選人資料轉換為員工檔案。
 * 由 {@link com.company.hrms.organization.infrastructure.event.CandidateHiredEventListener} 呼叫。
 *
 * <p>預設行為：
 * <ul>
 *   <li>員工狀態設為 PROBATION（試用期），試用期 3 個月</li>
 *   <li>自動產生員工編號（格式: EMP{yyyyMM}-{sequence}）</li>
 *   <li>若已有相同 Email 的員工則跳過（冪等性保護）</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateOnboardingService {

    private final IEmployeeRepository employeeRepository;

    /** 預設試用期月數 */
    private static final int DEFAULT_PROBATION_MONTHS = 3;

    /**
     * 從候選人資料建立員工檔案
     *
     * @param candidateId       候選人 ID
     * @param fullName          全名
     * @param email             電子郵件（將作為公司 Email）
     * @param phoneNumber       手機號碼
     * @param jobTitle          職稱
     * @param departmentId      部門 ID（字串格式的 UUID）
     * @param departmentName    部門名稱
     * @param offeredSalary     錄取薪資（字串格式）
     * @param expectedStartDate 預計到職日期（字串格式 yyyy-MM-dd）
     */
    @Transactional
    public void createEmployeeFromCandidate(
            String candidateId,
            String fullName,
            String email,
            String phoneNumber,
            String jobTitle,
            String departmentId,
            String departmentName,
            String offeredSalary,
            String expectedStartDate) {

        // 冪等性檢查：若已有相同 Email 的員工則跳過
        if (employeeRepository.existsByEmail(email)) {
            log.warn("[CandidateOnboardingService] 已存在相同 Email 的員工，跳過建立 - email={}, candidateId={}",
                    email, candidateId);
            return;
        }

        // 解析到職日期，預設為今天
        LocalDate hireDate = parseDate(expectedStartDate);
        if (hireDate == null) {
            hireDate = LocalDate.now();
            log.info("[CandidateOnboardingService] 未提供到職日期，使用今天 - candidateId={}", candidateId);
        }

        // 解析部門 ID
        UUID deptUuid = parseUUID(departmentId);

        // 產生員工編號
        String employeeNumber = generateEmployeeNumber(hireDate);

        // 拆分姓名（中文姓名：第一個字為姓，其餘為名）
        String lastName = fullName.length() > 0 ? fullName.substring(0, 1) : fullName;
        String firstName = fullName.length() > 1 ? fullName.substring(1) : "";

        try {
            // 使用 Employee 聚合根的工廠方法建立員工
            // 預設狀態為 PROBATION（試用期），試用期 3 個月
            Employee employee = Employee.onboard(
                    employeeNumber,
                    firstName,
                    lastName,
                    "A000000000",           // TODO: 候選人事件中無身分證號，使用預設值，待入職後由 HR 補填
                    LocalDate.of(1990, 1, 1), // TODO: 候選人事件中無出生日期，使用預設值，待入職後由 HR 補填
                    Gender.MALE,              // TODO: 候選人事件中無性別，使用預設值，待入職後由 HR 補填
                    email,
                    phoneNumber != null ? phoneNumber : "",
                    UUID.randomUUID(),        // TODO: 組織 ID 應從系統設定或事件中取得
                    deptUuid != null ? deptUuid : UUID.randomUUID(), // 若無部門 ID 則暫時產生
                    jobTitle != null ? jobTitle : "待定",
                    EmploymentType.FULL_TIME,
                    hireDate,
                    DEFAULT_PROBATION_MONTHS
            );

            employeeRepository.save(employee);

            log.info("[CandidateOnboardingService] 員工檔案建立成功 - employeeNumber={}, fullName={}, jobTitle={}, status=PROBATION, hireDate={}",
                    employeeNumber, fullName, jobTitle, hireDate);

        } catch (Exception e) {
            log.error("[CandidateOnboardingService] 建立員工檔案失敗 - candidateId={}, fullName={}, 原因: {}",
                    candidateId, fullName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 產生員工編號
     * 格式: EMP{yyyyMM}-{sequence}
     *
     * @param hireDate 到職日期
     * @return 員工編號
     */
    private String generateEmployeeNumber(LocalDate hireDate) {
        String prefix = "EMP" + hireDate.format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        int maxSeq = employeeRepository.findMaxSequenceByPrefix(prefix);
        return prefix + String.format("%03d", maxSeq + 1);
    }

    /**
     * 安全解析日期字串
     *
     * @param dateStr 日期字串（yyyy-MM-dd 格式）
     * @return 解析後的 LocalDate，解析失敗時回傳 null
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            log.warn("[CandidateOnboardingService] 日期格式解析失敗: {}", dateStr);
            return null;
        }
    }

    /**
     * 安全解析 UUID 字串
     *
     * @param uuidStr UUID 字串
     * @return 解析後的 UUID，解析失敗時回傳 null
     */
    private UUID parseUUID(String uuidStr) {
        if (uuidStr == null || uuidStr.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            log.warn("[CandidateOnboardingService] UUID 格式解析失敗: {}", uuidStr);
            return null;
        }
    }
}
