package com.company.hrms.training.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.training.api.request.TrainingStatisticsQuery;
import com.company.hrms.training.api.response.TrainingStatisticsResponse;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;
import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.training.domain.repository.ITrainingEnrollmentRepository;

import lombok.RequiredArgsConstructor;

@Service("getTrainingStatisticsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetTrainingStatisticsServiceImpl
                implements QueryApiService<TrainingStatisticsQuery, TrainingStatisticsResponse> {

        private final ITrainingEnrollmentRepository enrollmentRepository;

        @Override
        public TrainingStatisticsResponse getResponse(TrainingStatisticsQuery query, JWTModel currentUser,
                        String... args) {
                // Assume query contains date range or use defaults
                // TODO: 不符合business pipeline設計
                LocalDate startDate = query != null && query.getStartDate() != null ? query.getStartDate()
                                : LocalDate.of(LocalDate.now().getYear(), 1, 1);
                LocalDate endDate = query != null && query.getEndDate() != null ? query.getEndDate() : LocalDate.now();

                List<TrainingEnrollment> enrollments = enrollmentRepository.findByDateRange(startDate, endDate);

                int totalEnrollments = enrollments.size();
                long completedCount = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                                .count();
                double completionRate = totalEnrollments > 0 ? (double) completedCount / totalEnrollments : 0.0;

                BigDecimal totalHours = enrollments.stream()
                                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED
                                                && e.getCompletedHours() != null)
                                .map(TrainingEnrollment::getCompletedHours)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Map<String, Integer> coursesByCategory = ... requires join with Course, skip
                // for now or do if CourseId->Category map available

                TrainingStatisticsResponse res = new TrainingStatisticsResponse();
                res.setTotalCourses(0); // Need Course Repository count
                res.setTotalEnrollments(totalEnrollments);
                res.setCompletionRate(completionRate);
                res.setTotalTrainingHours(totalHours);
                res.setCoursesByCategory(new HashMap<>());
                res.setHoursByDepartment(new HashMap<>()); // Cannot group by dept yet

                return res;
        }
}
