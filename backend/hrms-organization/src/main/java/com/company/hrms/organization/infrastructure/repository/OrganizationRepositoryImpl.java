package com.company.hrms.organization.infrastructure.repository;

import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;
import com.company.hrms.organization.infrastructure.dao.OrganizationDAO;
import com.company.hrms.organization.infrastructure.po.OrganizationPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 組織倉儲實作
 */
@Repository
@RequiredArgsConstructor
public class OrganizationRepositoryImpl implements IOrganizationRepository {

    private final OrganizationDAO organizationDAO;

    @Override
    public Optional<Organization> findById(OrganizationId id) {
        return organizationDAO.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public Optional<Organization> findByCode(String code) {
        return organizationDAO.findByCode(code)
                .map(this::toDomain);
    }

    @Override
    public List<Organization> findAll() {
        return organizationDAO.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Organization> findByParentId(OrganizationId parentId) {
        return organizationDAO.findByParentId(parentId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Organization organization) {
        OrganizationPO po = toPO(organization);
        if (organizationDAO.existsById(organization.getId().getValue())) {
            po.setUpdatedAt(LocalDateTime.now());
            organizationDAO.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            organizationDAO.insert(po);
        }
    }

    @Override
    public void delete(OrganizationId id) {
        organizationDAO.deleteById(id.getValue());
    }

    @Override
    public boolean existsByCode(String code) {
        return organizationDAO.existsByCode(code);
    }

    @Override
    public boolean existsById(OrganizationId id) {
        return organizationDAO.existsById(id.getValue());
    }

    private Organization toDomain(OrganizationPO po) {
        return Organization.reconstitute(
                new OrganizationId(po.getId()),
                po.getCode(),
                po.getName(),
                po.getNameEn(),
                OrganizationType.valueOf(po.getType()),
                OrganizationStatus.valueOf(po.getStatus()),
                po.getParentId() != null ? new OrganizationId(po.getParentId()) : null,
                po.getTaxId(),
                po.getPhone(),
                po.getFax(),
                po.getEmail(),
                po.getAddress(),
                po.getEstablishedDate(),
                po.getDescription()
        );
    }

    private OrganizationPO toPO(Organization organization) {
        OrganizationPO po = new OrganizationPO();
        po.setId(organization.getId().getValue());
        po.setCode(organization.getCode());
        po.setName(organization.getName());
        po.setNameEn(organization.getNameEn());
        po.setType(organization.getType().name());
        po.setStatus(organization.getStatus().name());
        po.setParentId(organization.getParentId() != null ? organization.getParentId().getValue() : null);
        po.setTaxId(organization.getTaxId());
        po.setPhone(organization.getPhone());
        po.setFax(organization.getFax());
        po.setEmail(organization.getEmail());
        po.setAddress(organization.getAddress());
        po.setEstablishedDate(organization.getEstablishedDate());
        po.setDescription(organization.getDescription());
        return po;
    }
}
