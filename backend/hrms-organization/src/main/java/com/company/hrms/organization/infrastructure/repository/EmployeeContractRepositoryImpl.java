package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.aggregate.EmployeeContract;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IEmployeeContractRepository;
import com.company.hrms.organization.infrastructure.dao.EmployeeContractDAO;
import com.company.hrms.organization.infrastructure.po.EmployeeContractPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 員工合約倉儲實作
 */
@Repository
@RequiredArgsConstructor
public class EmployeeContractRepositoryImpl implements IEmployeeContractRepository {

    private final EmployeeContractDAO contractDAO;

    @Override
    public Optional<EmployeeContract> findById(ContractId id) {
        return contractDAO.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<EmployeeContract> findByEmployeeId(EmployeeId employeeId) {
        return contractDAO.findByEmployeeId(employeeId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EmployeeContract> findActiveByEmployeeId(EmployeeId employeeId) {
        return contractDAO.findActiveByEmployeeId(employeeId.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<EmployeeContract> findExpiringContracts(LocalDate startDate, LocalDate endDate) {
        return contractDAO.findExpiringContracts(startDate, endDate).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(EmployeeContract contract) {
        EmployeeContractPO po = toPO(contract);
        if (contractDAO.existsById(contract.getId().getValue())) {
            po.setUpdatedAt(LocalDateTime.now());
            contractDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            contractDAO.insert(po);
        }
    }

    @Override
    public void delete(ContractId id) {
        contractDAO.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(ContractId id) {
        return contractDAO.existsById(id.getValue());
    }

    private EmployeeContract toDomain(EmployeeContractPO po) {
        return EmployeeContract.reconstitute(
                new ContractId(po.getId()),
                new EmployeeId(po.getEmployeeId()),
                ContractType.valueOf(po.getContractType()),
                po.getStartDate(),
                po.getEndDate(),
                ContractStatus.valueOf(po.getStatus()),
                po.getProbationMonths(),
                po.getRenewalCount(),
                po.getNotes()
        );
    }

    private EmployeeContractPO toPO(EmployeeContract contract) {
        EmployeeContractPO po = new EmployeeContractPO();
        po.setId(contract.getId().getValue());
        po.setEmployeeId(contract.getEmployeeId().getValue());
        po.setContractType(contract.getContractType().name());
        po.setStartDate(contract.getStartDate());
        po.setEndDate(contract.getEndDate());
        po.setStatus(contract.getStatus().name());
        po.setProbationMonths(contract.getProbationMonths());
        po.setRenewalCount(contract.getRenewalCount());
        po.setNotes(contract.getNotes());
        return po;
    }
}
