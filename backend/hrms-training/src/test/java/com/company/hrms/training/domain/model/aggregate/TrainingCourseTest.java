package com.company.hrms.training.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.company.hrms.training.domain.model.valueobject.CourseType;
import com.company.hrms.training.domain.model.valueobject.DeliveryMode;

class TrainingCourseTest {

    private TrainingCourse course;

    @BeforeEach
    void setUp() {
        course = TrainingCourse.create(
                "C001",
                "Java Programming",
                CourseType.INTERNAL,
                DeliveryMode.ONLINE,
                new BigDecimal("10"),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                "admin");
        course.publish(); // Status becomes OPEN
    }

    @Test
    void testIncrementEnrollmentCount() {
        course.incrementEnrollmentCount();
        assertEquals(1, course.getCurrentEnrollments());
    }

    @Test
    void testDecrementEnrollmentCount() {
        course.incrementEnrollmentCount();
        course.decrementEnrollmentCount();
        assertEquals(0, course.getCurrentEnrollments());
    }

    @Test
    void testDecrementWhenZero() {
        course.decrementEnrollmentCount();
        assertEquals(0, course.getCurrentEnrollments());
    }

    @Test
    void testIncrementWhenFull() {
        course.setMaxParticipants(1);
        course.incrementEnrollmentCount();
        assertThrows(IllegalStateException.class, () -> course.incrementEnrollmentCount());
    }
}
