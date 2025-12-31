package com.company.hrms.insurance.api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.insurance.api.response.InsuranceLevelResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IInsuranceLevelRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 投保級距 Query Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/levels")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR05-Level", description = "投保級距查詢")
public class HR05LevelQryController {

    private final IInsuranceLevelRepository levelRepository;

    @GetMapping
    @Operation(summary = "查詢投保級距", operationId = "getLevels")
    public ResponseEntity<List<InsuranceLevelResponse>> getLevels(
            @RequestParam(required = false) String insuranceType,
            @RequestParam(required = false) String effectiveDate) {

        log.debug("查詢投保級距: type={}, date={}", insuranceType, effectiveDate);

        LocalDate date = effectiveDate != null ? LocalDate.parse(effectiveDate) : LocalDate.now();

        List<InsuranceLevel> levels;
        if (insuranceType != null && !insuranceType.isBlank()) {
            InsuranceType type = InsuranceType.valueOf(insuranceType);
            levels = levelRepository.findByType(type);
        } else {
            levels = levelRepository.findAllActive(date);
        }

        List<InsuranceLevelResponse> response = levels.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        log.info("投保級距查詢完成: {} 筆", response.size());

        return ResponseEntity.ok(response);
    }

    private InsuranceLevelResponse toResponse(InsuranceLevel level) {
        return InsuranceLevelResponse.builder()
                .levelId(level.getId().getValue())
                .insuranceType(level.getInsuranceType().name())
                .levelNumber(level.getLevelNumber())
                .monthlySalary(level.getMonthlySalary())
                .effectiveDate(level.getEffectiveDate().toString())
                .endDate(level.getEndDate() != null ? level.getEndDate().toString() : null)
                .isActive(level.isActive())
                .build();
    }
}
