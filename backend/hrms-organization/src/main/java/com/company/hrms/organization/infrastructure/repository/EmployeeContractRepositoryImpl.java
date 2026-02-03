package com.company.hrms.organization.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.organization.domain.model.aggregate.EmployeeContract;
import com.company.hrms.organization.domain.model.valueobject.ContractId;
import com.company.hrms.organization.domain.model.valueobject.ContractStatus;
import com.company.hrms.organization.domain.model.valueobject.ContractType;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeContractRepository;
import com.company.hrms.organization.infrastructure.dao.EmployeeContractDAO;
import com.company.hrms.organization.infrastructure.po.EmployeeContractPO;

import lombok.RequiredArgsConstructor;

/**
 * 員工合約 Repository 實作
 */
@Repository
@RequiredArgsConstructor
public class EmployeeContractRepositoryImpl implements IEmployeeContractRepository {

    private final EmployeeContractDAO contractDAO;

    @Override
    public Optional<EmployeeContract> findById(ContractId id) {
        return findById(id.getValue());
    }

    @Override
    public Optional<EmployeeContract> findById(UUID id) {
        return contractDAO.findById(id.toString()).map(this::toAggregate);
    }

    @Override
    public List<EmployeeContract> findByEmployeeId(EmployeeId employeeId) {
        return findByEmployeeId(employeeId.getValue());
    }

    @Override
    public List<EmployeeContract> findByEmployeeId(UUID employeeId) {
        return contractDAO.findByEmployeeId(employeeId.toString()).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeContract> findByEmployeeIdAndStatus(UUID employeeId, ContractStatus status) {
        return contractDAO.findByEmployeeIdAndStatus(employeeId.toString(), status.name()).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EmployeeContract> findActiveByEmployeeId(EmployeeId employeeId) {
        return findActiveByEmployeeId(employeeId.getValue());
    }

    @Override
    public Optional<EmployeeContract> findActiveByEmployeeId(UUID employeeId) {
        return contractDAO.findActiveByEmployeeId(employeeId.toString()).map(this::toAggregate);
    }

    @Override
    public List<EmployeeContract> findExpiringContracts(LocalDate startDate, LocalDate endDate) {
        return contractDAO.findExpiringContracts(startDate, endDate).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeContract> findExpiringContracts(LocalDate endDateBefore, ContractStatus status) {
        // 這裡可以使用 Query Engine 或在 DAO 增加方法，目前先暫時以此實作
        return contractDAO.findExpiringContracts(LocalDate.now(), endDateBefore).stream()
                .map(this::toAggregate)
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeContract> findExpiringBefore(LocalDate expiryDate) {
        return contractDAO.findExpiringContracts(LocalDate.now(), expiryDate).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeContract> findContractsExpiringSoon() {
        LocalDate thirtyDaysLater = LocalDate.now().plusDays(30);
        return findExpiringContracts(LocalDate.now(), thirtyDaysLater);
    }

    @Override
    public List<EmployeeContract> findByStatus(ContractStatus status) {
        return contractDAO.findByStatus(status.name()).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public void save(EmployeeContract contract) {
        EmployeeContractPO po = toPO(contract);
        if (contractDAO.existsById(po.getId())) {
            contractDAO.update(po);
        } else {
            contractDAO.insert(po);
        }
    }

    @Override
    public void delete(ContractId id) {
        contractDAO.deleteById(id.getValue().toString());
    }

    @Override
    public boolean existsById(ContractId id) {
        return contractDAO.existsById(id.getValue().toString());
    }

    @Override
    public boolean existsByContractNumber(String contractNumber) {
        return contractDAO.existsByContractNumber(contractNumber);
    }

    // ==================== Mappings ====================

    private EmployeeContract toAggregate(EmployeeContractPO po) {
        return EmployeeContract.reconstitute(
                new ContractId(po.getId()),
                new EmployeeId(po.getEmployeeId()),
                ContractType.valueOf(po.getContractType()),
                po.getStartDate(),
                po.getEndDate(),
                ContractStatus.valueOf(po.getStatus()),
                po.getProbationMonths(),
                po.getRenewalCount(),
                po.getNotes());
    }

    private EmployeeContractPO toPO(EmployeeContract contract) {
        EmployeeContractPO po = new EmployeeContractPO();
        po.setId(contract.getId().getValue().toString());
        po.setEmployeeId(contract.getEmployeeId().getValue().toString());
        po.setContractType(contract.getContractType().name());
        po.setStartDate(contract.getStartDate());
        po.setEndDate(contract.getEndDate());
        po.setStatus(contract.getStatus().name());
        po.setProbationMonths(contract.getProbationMonths());
        po.setRenewalCount(contract.getRenewalCount());
        po.setNotes(contract.getNotes());
        po.setCreatedAt(contract.getCreatedAt());
        po.setUpdatedAt(contract.getUpdatedAt());
        return po;
    }
}
