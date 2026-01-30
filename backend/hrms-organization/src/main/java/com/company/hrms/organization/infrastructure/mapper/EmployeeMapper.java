package com.company.hrms.organization.infrastructure.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.company.hrms.organization.infrastructure.po.EmployeePO;

/**
 * 員工 MyBatis Mapper
 */
@Mapper
public interface EmployeeMapper {

        /**
         * 根據 ID 查詢員工
         */
        EmployeePO selectById(@Param("id") String id);

        /**
         * 根據員工編號查詢
         */
        EmployeePO selectByEmployeeNumber(@Param("employeeNumber") String employeeNumber);

        /**
         * 根據 Email 查詢
         */
        EmployeePO selectByEmail(@Param("email") String email);

        /**
         * 根據身分證號查詢
         */
        EmployeePO selectByNationalId(@Param("nationalId") String nationalId);

        /**
         * 根據部門 ID 查詢員工
         */
        List<EmployeePO> selectByDepartmentId(@Param("departmentId") String departmentId);

        /**
         * 根據主管 ID 查詢下屬
         */
        List<EmployeePO> selectBySupervisorId(@Param("supervisorId") String supervisorId);

        /**
         * 根據任職狀態查詢
         */
        List<EmployeePO> selectByEmploymentStatus(@Param("employmentStatus") String employmentStatus);

        /**
         * 條件查詢員工清單
         */
        List<EmployeePO> selectByCriteria(
                        @Param("keyword") String keyword,
                        @Param("departmentId") String departmentId,
                        @Param("employmentStatus") String employmentStatus,
                        @Param("employmentType") String employmentType,
                        @Param("hireDateFrom") LocalDate hireDateFrom,
                        @Param("hireDateTo") LocalDate hireDateTo,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

        /**
         * 條件查詢員工數量
         */
        long countByCriteria(
                        @Param("keyword") String keyword,
                        @Param("departmentId") String departmentId,
                        @Param("employmentStatus") String employmentStatus,
                        @Param("employmentType") String employmentType,
                        @Param("hireDateFrom") LocalDate hireDateFrom,
                        @Param("hireDateTo") LocalDate hireDateTo);

        /**
         * 新增員工
         */
        int insert(EmployeePO employee);

        /**
         * 更新員工
         */
        int update(EmployeePO employee);

        /**
         * 刪除員工 (軟刪除)
         */
        int deleteById(@Param("id") String id);

        /**
         * 檢查員工編號是否存在
         */
        boolean existsByEmployeeNumber(@Param("employeeNumber") String employeeNumber);

        /**
         * 檢查 Email 是否存在
         */
        boolean existsByEmail(@Param("email") String email);

        /**
         * 檢查身分證號是否存在
         */
        boolean existsByNationalId(@Param("nationalId") String nationalId);

        /**
         * 檢查員工 ID 是否存在
         */
        boolean existsById(@Param("id") String id);

        /**
         * 計算部門員工數量
         */
        int countByDepartmentId(@Param("departmentId") String departmentId);

        /**
         * 查詢特定前綴的最大流水號
         * 用於生成員工編號
         * 
         * @param prefix 員工編號前綴 (例如: EMP202412-)
         * @return 最大流水號
         */
        Integer findMaxSequenceByPrefix(@Param("prefix") String prefix);

        /**
         * 計算組織下的員工數量
         */
        int countByOrganizationId(@Param("organizationId") String organizationId);

        List<EmployeePO> selectAll();
}
