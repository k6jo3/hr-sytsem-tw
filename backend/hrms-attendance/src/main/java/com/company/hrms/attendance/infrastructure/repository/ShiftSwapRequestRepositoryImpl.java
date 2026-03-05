package com.company.hrms.attendance.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.attendance.domain.model.aggregate.ShiftSwapRequest;
import com.company.hrms.attendance.domain.model.valueobject.SwapRequestId;
import com.company.hrms.attendance.domain.model.valueobject.SwapStatus;
import com.company.hrms.attendance.domain.repository.IShiftSwapRequestRepository;
import com.company.hrms.attendance.infrastructure.po.ShiftSwapRequestPO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 換班申請 Repository 實作
 */
@Repository
public class ShiftSwapRequestRepositoryImpl extends BaseRepository<ShiftSwapRequestPO, String>
        implements IShiftSwapRequestRepository {

    public ShiftSwapRequestRepositoryImpl(JPAQueryFactory queryFactory) {
        super(queryFactory, ShiftSwapRequestPO.class);
    }

    @Override
    public Optional<ShiftSwapRequest> findById(SwapRequestId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<ShiftSwapRequest> findByRequesterId(String requesterId) {
        QueryGroup query = QueryBuilder.where()
                .and("requesterId", Operator.EQ, requesterId)
                .and("isDeleted", Operator.EQ, 0)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<ShiftSwapRequest> findByCounterpartId(String counterpartId) {
        QueryGroup query = QueryBuilder.where()
                .and("counterpartId", Operator.EQ, counterpartId)
                .and("isDeleted", Operator.EQ, 0)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<ShiftSwapRequest> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(ShiftSwapRequest request) {
        ShiftSwapRequestPO po = toPO(request);
        Optional<ShiftSwapRequestPO> existing = super.findById(po.getId());

        if (existing.isPresent()) {
            po.setCreatedAt(existing.get().getCreatedAt());
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            super.save(po);
        }
    }

    private ShiftSwapRequest toDomain(ShiftSwapRequestPO po) {
        return ShiftSwapRequest.reconstitute(
                new SwapRequestId(po.getId()),
                po.getRequesterId(),
                po.getCounterpartId(),
                po.getRequesterDate(),
                po.getCounterpartDate(),
                po.getRequesterShiftId(),
                po.getCounterpartShiftId(),
                SwapStatus.valueOf(po.getStatus()),
                po.getReason(),
                po.getRejectionReason(),
                po.getApproverId(),
                po.getIsDeleted() != null && po.getIsDeleted() == 1);
    }

    private ShiftSwapRequestPO toPO(ShiftSwapRequest request) {
        return ShiftSwapRequestPO.builder()
                .id(request.getId().getValue())
                .requesterId(request.getRequesterId())
                .counterpartId(request.getCounterpartId())
                .requesterDate(request.getRequesterDate())
                .counterpartDate(request.getCounterpartDate())
                .requesterShiftId(request.getRequesterShiftId())
                .counterpartShiftId(request.getCounterpartShiftId())
                .status(request.getStatus().name())
                .reason(request.getReason())
                .rejectionReason(request.getRejectionReason())
                .approverId(request.getApproverId())
                .isDeleted(request.isDeleted() ? 1 : 0)
                .build();
    }
}
