package com.company.hrms.training.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.training.api.response.MyTrainingHoursResponse;
import com.company.hrms.training.domain.repository.ITrainingEnrollmentRepository;

import lombok.RequiredArgsConstructor;

@Service("getMyTrainingHoursServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMyTrainingHoursServiceImpl implements QueryApiService<QueryGroup, MyTrainingHoursResponse> {

    private final ITrainingEnrollmentRepository enrollmentRepository;

    @Override
    public MyTrainingHoursResponse getResponse(QueryGroup query, JWTModel currentUser, String... args) {
        String employeeId = currentUser.getUserId();

        // Total All Time
        BigDecimal total = enrollmentRepository.sumCompletedHours(employeeId, null, null);

        // Year to Date
        LocalDate startOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        BigDecimal ytd = enrollmentRepository.sumCompletedHours(employeeId, startOfYear, LocalDate.now());

        MyTrainingHoursResponse res = new MyTrainingHoursResponse();
        res.setEmployeeId(employeeId);
        res.setTotalHours(total);
        res.setYearToDateHours(ytd);

        return res;
    }
}
