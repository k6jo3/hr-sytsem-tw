package com.company.hrms.organization.infrastructure.dao;

import com.company.hrms.organization.infrastructure.mapper.OrganizationMapper;
import com.company.hrms.organization.infrastructure.po.OrganizationPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 組織 DAO
 */
@Repository
@RequiredArgsConstructor
public class OrganizationDAO {

    private final OrganizationMapper organizationMapper;

    public Optional<OrganizationPO> findById(String id) {
        return Optional.ofNullable(organizationMapper.selectById(id));
    }

    public Optional<OrganizationPO> findByCode(String code) {
        return Optional.ofNullable(organizationMapper.selectByCode(code));
    }

    public List<OrganizationPO> findAll() {
        return organizationMapper.selectAll();
    }

    public List<OrganizationPO> findByParentId(String parentId) {
        return organizationMapper.selectByParentId(parentId);
    }

    public List<OrganizationPO> findByStatus(String status) {
        return organizationMapper.selectByStatus(status);
    }

    public void insert(OrganizationPO organization) {
        organizationMapper.insert(organization);
    }

    public void update(OrganizationPO organization) {
        organizationMapper.update(organization);
    }

    public void deleteById(String id) {
        organizationMapper.deleteById(id);
    }

    public boolean existsByCode(String code) {
        return organizationMapper.existsByCode(code);
    }

    public boolean existsById(String id) {
        return organizationMapper.existsById(id);
    }
}
