package com.company.hrms.organization.application.service.employee;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 匯出員工清單服務實作
 * <p>
 * 目前實作 CSV 格式匯出
 * </p>
 */
@Service("exportEmployeesServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExportEmployeesServiceImpl
        implements QueryApiService<Void, byte[]> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public byte[] getResponse(Void request,
            JWTModel currentUser,
            String... args) throws Exception {
        log.info("Exporting employees to CSV");
        // TODO: 不符合business pipeline的設計，應該要拆成多個task
        // 1. 查詢所有員工
        List<Employee> employees = employeeRepository.findAll();

        // 2. 建立 CSV 內容
        StringBuilder csv = new StringBuilder();
        // Header
        csv.append("員工編號,姓名,Email,部門,職稱,到職日,狀態\n");

        // Data
        for (Employee emp : employees) {
            csv.append(escape(emp.getEmployeeNumber())).append(",");
            csv.append(escape(emp.getFullName())).append(",");
            csv.append(escape(emp.getEmail().getValue())).append(",");
            csv.append(escape(emp.getDepartmentId().toString())).append(",");
            csv.append(escape(emp.getJobTitle())).append(",");
            csv.append(emp.getHireDate()).append(",");
            csv.append(emp.getEmploymentStatus().toString()).append("\n");
        }

        // 3. 回傳 byte[] (UTF-8 with BOM for Excel compatibility)
        byte[] content = csv.toString().getBytes(StandardCharsets.UTF_8);
        byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        byte[] result = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, result, 0, bom.length);
        System.arraycopy(content, 0, result, bom.length, content.length);

        return result;
    }

    private String escape(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
