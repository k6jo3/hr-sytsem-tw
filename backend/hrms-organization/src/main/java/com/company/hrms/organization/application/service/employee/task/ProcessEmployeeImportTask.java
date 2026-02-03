package com.company.hrms.organization.application.service.employee.task;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.CreateEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeImportContext;
import com.company.hrms.organization.domain.event.EmployeeCreatedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.EmploymentType;
import com.company.hrms.organization.domain.model.valueobject.Gender;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 處理員工匯入 Task
 * 負責解析檔案、驗證資料、批次建立員工聚合根與儲存
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessEmployeeImportTask implements PipelineTask<EmployeeImportContext> {

    private final IEmployeeRepository employeeRepository;
    private final IDepartmentRepository departmentRepository;
    private final IOrganizationRepository organizationRepository;
    private final IEmployeeHistoryRepository historyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(EmployeeImportContext context) throws Exception {
        if (context.getFileData() == null || context.getFileData().length == 0) {
            context.getErrors().add("匯入檔案資料為空");
            return;
        }

        log.info("開始處理員工匯入: {}", context.getFileName());

        List<CreateEmployeeRequest> requests = parseExcel(context.getFileData(), context.getErrors());
        context.setTotalCount(requests.size());

        for (CreateEmployeeRequest request : requests) {
            try {
                // 1. 資料驗證
                validateRequest(request);

                // 2. 建立員工 Aggregate
                Employee employee = Employee.onboard(
                        request.getEmployeeNumber(),
                        request.getFirstName(),
                        request.getLastName(),
                        request.getNationalId(),
                        request.getDateOfBirth(),
                        Gender.valueOf(request.getGender()),
                        request.getCompanyEmail(),
                        request.getMobilePhone(),
                        UUID.fromString(request.getOrganizationId()),
                        UUID.fromString(request.getDepartmentId()),
                        request.getJobTitle(),
                        EmploymentType.valueOf(request.getEmploymentType()),
                        request.getHireDate(),
                        request.getProbationMonths());

                // 3. 儲存
                employeeRepository.save(employee);

                // 4. 記錄人事歷程
                EmployeeHistory history = EmployeeHistory.recordOnboarding(
                        employee.getId().getValue(),
                        employee.getHireDate(),
                        Map.of(
                                "employeeNumber", employee.getEmployeeNumber(),
                                "fullName", employee.getFullName(),
                                "departmentId", employee.getDepartmentId().toString()));
                historyRepository.save(history);

                // 5. 發布領域事件
                eventPublisher.publishEvent(new EmployeeCreatedEvent(
                        employee.getId().getValue(),
                        employee.getEmployeeNumber(),
                        employee.getFullName(),
                        employee.getCompanyEmail().getValue(),
                        employee.getOrganizationId(), // UUID 不需要 getValue()
                        employee.getDepartmentId(), // UUID 不需要 getValue()
                        employee.getJobTitle(),
                        employee.getHireDate()));

                context.setSuccessCount(context.getSuccessCount() + 1);
                log.debug("員工匯入成功: {}", request.getEmployeeNumber());

            } catch (Exception e) {
                log.error("員工匯入失敗: {}", request.getEmployeeNumber(), e);
                context.setFailureCount(context.getFailureCount() + 1);
                context.getErrors()
                        .add("員工 " + (request.getEmployeeNumber() != null ? request.getEmployeeNumber() : "未知")
                                + " 匯入失敗: " + e.getMessage());
            }
        }

        log.info("員工匯入完成. 成功: {}, 失敗: {}", context.getSuccessCount(), context.getFailureCount());
    }

    private void validateRequest(CreateEmployeeRequest request) {
        // 唯一性檢查
        if (employeeRepository.existsByEmployeeNumber(request.getEmployeeNumber())) {
            throw new IllegalArgumentException("員工編號已存在: " + request.getEmployeeNumber());
        }
        if (employeeRepository.existsByEmail(request.getCompanyEmail())) {
            throw new IllegalArgumentException("公司 Email 已存在: " + request.getCompanyEmail());
        }
        if (employeeRepository.existsByNationalId(request.getNationalId())) {
            throw new IllegalArgumentException("身分證號已存在: " + request.getNationalId());
        }

        // 關聯性檢查
        if (!organizationRepository.existsById(new OrganizationId(request.getOrganizationId()))) {
            throw new IllegalArgumentException("組織 ID 不存在: " + request.getOrganizationId());
        }
        if (!departmentRepository.existsById(new DepartmentId(request.getDepartmentId()))) {
            throw new IllegalArgumentException("部門 ID 不存在: " + request.getDepartmentId());
        }
    }

    private List<CreateEmployeeRequest> parseExcel(byte[] data, List<String> errors) {
        List<CreateEmployeeRequest> requests = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(data))) {
            Sheet sheet = workbook.getSheetAt(0);
            // 假設第一列為標題
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    CreateEmployeeRequest req = new CreateEmployeeRequest();
                    // 欄位映射需與 Excel 模板一致
                    req.setEmployeeNumber(getCellValueAsString(row, 0));
                    req.setLastName(getCellValueAsString(row, 1));
                    req.setFirstName(getCellValueAsString(row, 2));
                    req.setNationalId(getCellValueAsString(row, 3));
                    req.setOrganizationId(getCellValueAsString(row, 4));
                    req.setDepartmentId(getCellValueAsString(row, 5));

                    // 核心必填欄位
                    req.setGender(getCellValueAsString(row, 6)); // MALE/FEMALE
                    req.setCompanyEmail(getCellValueAsString(row, 7));
                    req.setMobilePhone(getCellValueAsString(row, 8));
                    req.setJobTitle(getCellValueAsString(row, 9));
                    req.setEmploymentType(getCellValueAsString(row, 10)); // FULL_TIME/CONTRACT...

                    String hireDateStr = getCellValueAsString(row, 11);
                    req.setHireDate(hireDateStr.isBlank() ? LocalDate.now() : LocalDate.parse(hireDateStr));

                    String birthDateStr = getCellValueAsString(row, 12);
                    req.setDateOfBirth(
                            birthDateStr.isBlank() ? LocalDate.of(1990, 1, 1) : LocalDate.parse(birthDateStr));

                    req.setProbationMonths(3);

                    requests.add(req);
                } catch (Exception e) {
                    errors.add("第 " + (i + 1) + " 列資料解析失敗: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Excel 解析錯誤", e);
            errors.add("檔案格式錯誤，無法解析 Excel: " + e.getMessage());
        }
        return requests;
    }

    private String getCellValueAsString(Row row, int cellIndex) {
        if (row.getCell(cellIndex) == null)
            return "";
        return row.getCell(cellIndex).toString().trim();
    }

    @Override
    public String getName() {
        return "處理員工匯入";
    }
}
