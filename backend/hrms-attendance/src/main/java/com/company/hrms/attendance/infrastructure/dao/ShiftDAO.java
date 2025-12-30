package com.company.hrms.attendance.infrastructure.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.infrastructure.mapper.ShiftMapper;
import com.company.hrms.attendance.infrastructure.po.ShiftPO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShiftDAO {

    private final ShiftMapper shiftMapper;

    public Optional<ShiftPO> findById(String id) {
        return Optional.ofNullable(shiftMapper.selectById(id));
    }

    public List<ShiftPO> findAll() {
        return shiftMapper.selectAll();
    }

    public void insert(ShiftPO shift) {
        shiftMapper.insert(shift);
    }

    public void update(ShiftPO shift) {
        shiftMapper.update(shift);
    }

    public void deleteById(String id) {
        shiftMapper.deleteById(id);
    }
}
