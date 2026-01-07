package com.company.hrms.insurance.application.service.level;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.repository.IInsuranceLevelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("getLevelsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GetLevelsServiceImpl implements QueryApiService<Void, List<InsuranceLevel>> {

    private final IInsuranceLevelRepository levelRepository;

    @Override
    public List<InsuranceLevel> getResponse(Void request, JWTModel currentUser, String... args) throws Exception {

        log.debug("查詢有效投保級距");
        return levelRepository.findAllActive(LocalDate.now());
    }
}
