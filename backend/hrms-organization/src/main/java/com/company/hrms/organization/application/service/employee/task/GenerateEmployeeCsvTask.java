package com.company.hrms.organization.application.service.employee.task;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeExportContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;

/**
 * 產生員工 CSV Task
 */
@Component
public class GenerateEmployeeCsvTask implements PipelineTask<EmployeeExportContext> {

    @Override
    public void execute(EmployeeExportContext context) throws Exception {
        List<Employee> employees = context.getEmployees();

        StringBuilder csv = new StringBuilder();
        // Header
        csv.append("員工編號,姓名,Email,部門,職稱,到職日,狀態\n");

        // Data
        for (Employee emp : employees) {
            csv.append(escape(emp.getEmployeeNumber())).append(",");
            csv.append(escape(emp.getFullName())).append(",");
            csv.append(escape(emp.getEmail() != null ? emp.getEmail().getValue() : "")).append(",");
            csv.append(escape(emp.getDepartmentId() != null ? emp.getDepartmentId().toString() : "")).append(",");
            csv.append(escape(emp.getJobTitle())).append(",");
            csv.append(emp.getHireDate()).append(",");
            csv.append(emp.getEmploymentStatus() != null ? emp.getEmploymentStatus().toString() : "").append("\n");
        }

        // 回傳 byte[] (UTF-8 with BOM for Excel compatibility)
        byte[] content = csv.toString().getBytes(StandardCharsets.UTF_8);
        byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        byte[] result = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, result, 0, bom.length);
        System.arraycopy(content, 0, result, bom.length, content.length);

        context.setResult(result);
    }

    @Override
    public String getName() {
        return "產生員工 CSV";
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
