package com.company.hrms.organization.infrastructure.dao;

import com.company.hrms.organization.infrastructure.mapper.EmployeeContractMapper;
import com.company.hrms.organization.infrastructure.po.EmployeeContractPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 員工合約 DAO
 */
@Repository
@RequiredArgsConstructor
public class EmployeeContractDAO {

    private final EmployeeContractMapper contractMapper;

    public Optional<EmployeeContractPO> findById(String id) {
        return Optional.ofNullable(contractMapper.selectById(id));
    }

    public List<EmployeeContractPO> findByEmployeeId(String employeeId) {
        return contractMapper.selectByEmployeeId(employeeId);
    }

    public Optional<EmployeeContractPO> findActiveByEmployeeId(String employeeId) {
        return Optional.ofNullable(contractMapper.selectActiveByEmployeeId(employeeId));
    }

    public List<EmployeeContractPO> findExpiringContracts(LocalDate startDate, LocalDate endDate) {
        return contractMapper.selectExpiringContracts(startDate, endDate);
    }

    public List<EmployeeContractPO> findByStatus(String status) {
        return contractMapper.selectByStatus(status);
    }

    public void insert(EmployeeContractPO contract) {
        contractMapper.insert(contract);
    }

    public void update(EmployeeContractPO contract) {
        contractMapper.update(contract);
    }

    public void deleteById(String id) {
        contractMapper.deleteById(id);
    }

    public boolean existsById(String id) {
        return contractMapper.existsById(id);
    }

    public List<EmployeeContractPO> findByEmployeeIdAndStatus(String employeeId, String status) {
        return contractMapper.selectByEmployeeIdAndStatus(employeeId, status);
    }

    public boolean existsByContractNumber(String contractNumber) {
        return contractMapper.existsByContractNumber(contractNumber);
    }
}
