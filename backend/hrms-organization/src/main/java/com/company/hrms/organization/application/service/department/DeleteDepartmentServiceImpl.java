package com.company.hrms.organization.application.service.department;

import java.util.List;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 刪除部門 Application Service
 * 
 * <p>
 * 對應 API: DELETE /api/v1/departments/{id}
 * </p>
 * 
 * <p>
 * 業務邏輯:
 * </p>
 * <ol>
 * <li>驗證部門存在</li>
 * <li>檢查部門下是否有在職員工</li>
 * <li>檢查部門下是否有子部門</li>
 * <li>刪除部門（軟刪除）</li>
 * </ol>
 */
@Service("deleteDepartmentServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteDepartmentServiceImpl implements CommandApiService<Object, Void> {

    private final IDepartmentRepository departmentRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public Void execCommand(Object request, JWTModel currentUser, String... args)
            throws Exception {

        String departmentId = args[0];
        log.info("刪除部門: departmentId={}", departmentId);

        // 查詢部門
        DepartmentId deptId = new DepartmentId(departmentId);
        departmentRepository.findById(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("DEPT_NOT_FOUND", "部門不存在: " + departmentId));

        // 檢查是否有員工
        List<Employee> employees = employeeRepository.findByDepartmentId(deptId);
        if (!employees.isEmpty()) {
            throw new DomainException("CANNOT_DELETE_DEPT",
                    "無法刪除部門，尚有 " + employees.size() + " 位員工");
        }

        // 檢查是否有子部門
        int childCount = departmentRepository.countByParentId(deptId);
        if (childCount > 0) {
            throw new DomainException("CANNOT_DELETE_DEPT",
                    "無法刪除部門，尚有 " + childCount + " 個子部門");
        }

        // 刪除部門（軟刪除）
        departmentRepository.delete(deptId);

        log.info("部門刪除成功: departmentId={}", departmentId);

        return null;
    }
}
