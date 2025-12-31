package com.company.hrms.insurance.application.service.fee;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.insurance.api.request.CalculateSupplementaryPremiumRequest;
import com.company.hrms.insurance.api.response.SupplementaryPremiumResponse;
import com.company.hrms.insurance.domain.model.aggregate.SupplementaryPremium;
import com.company.hrms.insurance.domain.model.valueobject.IncomeType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("calculateSupplementaryPremiumServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CalculateSupplementaryPremiumServiceImpl
                implements CommandApiService<CalculateSupplementaryPremiumRequest, SupplementaryPremiumResponse> {

        @Override
        public SupplementaryPremiumResponse execCommand(CalculateSupplementaryPremiumRequest request,
                        JWTModel currentUser,
                        String... args)
                        throws Exception {

                log.info("補充保費計算請求: employeeId={}, income={}",
                                request.getEmployeeId(), request.getIncomeAmount());

                // 解析收入類型
                IncomeType incomeType = IncomeType.valueOf(request.getIncomeType());
                LocalDate incomeDate = LocalDate.parse(request.getIncomeDate());

                // 計算補充保費
                SupplementaryPremium premium = SupplementaryPremium.calculate(
                                request.getEmployeeId(),
                                incomeType,
                                incomeDate,
                                request.getIncomeAmount(),
                                request.getInsuredSalary());

                // 建構回應
                SupplementaryPremiumResponse response;
                if (premium == null) {
                        // 不需繳納補充保費
                        response = SupplementaryPremiumResponse.builder()
                                        .required(false)
                                        .premiumAmount(BigDecimal.ZERO)
                                        .build();
                } else {
                        response = SupplementaryPremiumResponse.builder()
                                        .required(true)
                                        .premiumAmount(premium.getPremiumAmount())
                                        .build();
                }

                return response;
        }
}
