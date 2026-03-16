package com.company.hrms.insurance.application.service.level;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.insurance.api.request.BatchAdjustLevelsRequest;
import com.company.hrms.insurance.api.response.BatchAdjustLevelsResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.repository.IInsuranceLevelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 批量調整投保級距 Service
 * <p>
 * 當政府公告級距調整時，批量產生新版級距並停用舊版。
 * </p>
 */
@Service("batchAdjustLevelsServiceImpl")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BatchAdjustLevelsServiceImpl
        implements CommandApiService<BatchAdjustLevelsRequest, BatchAdjustLevelsResponse> {

    private final IInsuranceLevelRepository levelRepository;

    @Override
    public BatchAdjustLevelsResponse execCommand(
            BatchAdjustLevelsRequest request, JWTModel currentUser, String... args) throws Exception {

        log.info("開始批量調整投保級距，保險類型={}, 調整金額={}, 生效日={}",
                request.getInsuranceTypes(), request.getAdjustmentAmount(), request.getEffectiveDate());

        int totalDeactivated = 0;
        int totalCreated = 0;

        for (String typeStr : request.getInsuranceTypes()) {
            InsuranceType insuranceType = InsuranceType.valueOf(typeStr);

            // 1. 查詢該類型中有效且無結束日的級距
            List<InsuranceLevel> activeLevels = levelRepository.findByTypeAndEndDateIsNull(insuranceType);

            if (activeLevels.isEmpty()) {
                log.warn("保險類型 {} 沒有找到有效的級距，跳過", typeStr);
                continue;
            }

            // 2. 停用舊級距：設定結束日為生效日前一天
            for (InsuranceLevel oldLevel : activeLevels) {
                oldLevel.setEndDate(request.getEffectiveDate().minusDays(1));
                levelRepository.update(oldLevel);
            }
            totalDeactivated += activeLevels.size();

            // 3. 產生新級距
            List<InsuranceLevel> newLevels = new ArrayList<>();
            int maxLevelNumber = 0;

            for (InsuranceLevel oldLevel : activeLevels) {
                BigDecimal newSalary = oldLevel.getMonthlySalary().add(request.getAdjustmentAmount());

                // 跳過調整後金額 <= 0 的級距
                if (newSalary.compareTo(BigDecimal.ZERO) <= 0) {
                    log.debug("級距 {} 調整後金額 {} <= 0，跳過", oldLevel.getLevelNumber(), newSalary);
                    continue;
                }

                InsuranceLevel newLevel = new InsuranceLevel(
                        LevelId.generate(),
                        insuranceType,
                        oldLevel.getLevelNumber(),
                        newSalary,
                        request.getEffectiveDate());

                // 保持原本的費率
                newLevel.setLaborRates(oldLevel.getLaborEmployeeRate(), oldLevel.getLaborEmployerRate());
                newLevel.setHealthRates(oldLevel.getHealthEmployeeRate(), oldLevel.getHealthEmployerRate());
                newLevel.setPensionEmployerRate(oldLevel.getPensionEmployerRate());

                newLevels.add(newLevel);

                if (oldLevel.getLevelNumber() > maxLevelNumber) {
                    maxLevelNumber = oldLevel.getLevelNumber();
                }
            }

            // 4. 如果有指定新最高級距，新增一筆
            if (request.getNewHighestLevelSalary() != null) {
                int newHighestLevelNumber = maxLevelNumber + 1;

                // 使用最高級距的費率作為新最高級距的費率
                InsuranceLevel highestOld = activeLevels.stream()
                        .max(Comparator.comparingInt(InsuranceLevel::getLevelNumber))
                        .orElse(activeLevels.get(0));

                InsuranceLevel highestLevel = new InsuranceLevel(
                        LevelId.generate(),
                        insuranceType,
                        newHighestLevelNumber,
                        request.getNewHighestLevelSalary(),
                        request.getEffectiveDate());

                highestLevel.setLaborRates(highestOld.getLaborEmployeeRate(), highestOld.getLaborEmployerRate());
                highestLevel.setHealthRates(highestOld.getHealthEmployeeRate(), highestOld.getHealthEmployerRate());
                highestLevel.setPensionEmployerRate(highestOld.getPensionEmployerRate());

                newLevels.add(highestLevel);
            }

            // 5. 批量儲存新級距
            if (!newLevels.isEmpty()) {
                levelRepository.saveBatch(newLevels);
                totalCreated += newLevels.size();
            }
        }

        String message = String.format("批量調整完成：停用 %d 筆舊級距，建立 %d 筆新級距",
                totalDeactivated, totalCreated);
        log.info(message);

        return BatchAdjustLevelsResponse.builder()
                .oldLevelsDeactivated(totalDeactivated)
                .newLevelsCreated(totalCreated)
                .message(message)
                .build();
    }
}
