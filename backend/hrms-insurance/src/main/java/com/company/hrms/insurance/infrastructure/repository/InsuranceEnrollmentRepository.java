package com.company.hrms.insurance.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBatchBaseRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;
import com.company.hrms.insurance.infrastructure.entity.InsuranceEnrollmentEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 加退保記錄Repository實作
 * 使用 Fluent-Query-Engine (BaseRepository)
 */
@Repository
public class InsuranceEnrollmentRepository
                extends CommandBatchBaseRepository<InsuranceEnrollmentEntity, UUID>
                implements IInsuranceEnrollmentRepository {

        public InsuranceEnrollmentRepository(JPAQueryFactory factory) {
                super(factory, InsuranceEnrollmentEntity.class);
        }

        @Override
        public InsuranceEnrollment save(InsuranceEnrollment enrollment) {
                InsuranceEnrollmentEntity entity = toEntity(enrollment);
                // 新建記錄使用 persist，而非 merge（修正 500 Internal Server Error）
                super.save(entity);
                return enrollment;
        }

        @Override
        public InsuranceEnrollment update(InsuranceEnrollment enrollment) {
                InsuranceEnrollmentEntity entity = toEntity(enrollment);
                // 更新既有記錄使用 merge（適用於退保、調整等操作）
                super.update(entity);
                return enrollment;
        }

        @Override
        public Optional<InsuranceEnrollment> findById(EnrollmentId id) {
                return super.findById(UUID.fromString(id.getValue()))
                                .map(this::toDomain);
        }

        @Override
        public List<InsuranceEnrollment> findByEmployeeId(String employeeId) {
                QueryGroup query = QueryGroup.and().eq("employeeId", employeeId);
                return super.findAll(query).stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public Optional<InsuranceEnrollment> findActiveByEmployeeIdAndType(String employeeId, InsuranceType type) {
                QueryGroup query = QueryGroup.and()
                                .eq("employeeId", employeeId)
                                .eq("insuranceType", type)
                                .eq("status", EnrollmentStatus.ACTIVE);
                return super.findOne(query).map(this::toDomain);
        }

        @Override
        public List<InsuranceEnrollment> findAllActiveByEmployeeId(String employeeId) {
                QueryGroup query = QueryGroup.and()
                                .eq("employeeId", employeeId)
                                .eq("status", EnrollmentStatus.ACTIVE);
                return super.findAll(query).stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<InsuranceEnrollment> findAll() {
                return super.findAll(QueryGroup.and()).stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<InsuranceEnrollment> findByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
                // 查詢加保日期在區間內 或 退保日期在區間內的記錄
                QueryGroup query = QueryGroup.and()
                                .gte("enrollDate", startDate)
                                .lte("enrollDate", endDate);

                return super.findAll(query).stream()
                                .map(this::toDomain)
                                .collect(Collectors.toList());
        }

        // ==================== Entity <-> Domain 轉換 ====================

        private InsuranceEnrollmentEntity toEntity(InsuranceEnrollment domain) {
                return InsuranceEnrollmentEntity.builder()
                                .enrollmentId(UUID.fromString(domain.getId().getValue()))
                                .employeeId(domain.getEmployeeId())
                                .insuranceUnitId(UUID.fromString(domain.getInsuranceUnitId().getValue()))
                                .insuranceType(domain.getInsuranceType())
                                .enrollDate(domain.getEnrollDate())
                                .withdrawDate(domain.getWithdrawDate())
                                .insuranceLevelId(domain.getInsuranceLevelId() != null
                                                ? UUID.fromString(domain.getInsuranceLevelId().getValue())
                                                : null)
                                .monthlySalary(domain.getMonthlySalary())
                                .status(domain.getStatus())
                                .isReported(domain.isReported())
                                .build();
        }

        private InsuranceEnrollment toDomain(InsuranceEnrollmentEntity entity) {
                return new InsuranceEnrollment(
                                new EnrollmentId(entity.getEnrollmentId().toString()),
                                entity.getEmployeeId(),
                                new UnitId(entity.getInsuranceUnitId().toString()),
                                entity.getInsuranceType(),
                                entity.getEnrollDate(),
                                entity.getWithdrawDate(),
                                entity.getInsuranceLevelId() != null
                                                ? new LevelId(entity.getInsuranceLevelId().toString())
                                                : null,
                                entity.getMonthlySalary(),
                                entity.getStatus(),
                                entity.getIsReported() != null ? entity.getIsReported() : false);
        }
}
