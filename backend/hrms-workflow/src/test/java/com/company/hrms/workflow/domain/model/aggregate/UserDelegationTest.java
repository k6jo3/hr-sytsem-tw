package com.company.hrms.workflow.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.company.hrms.workflow.domain.model.valueobject.UserDelegationId;

public class UserDelegationTest {

    @Test
    public void testIsActiveNow() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        // Case 1: Active range including today
        UserDelegation d1 = UserDelegation.builder()
                .delegationId(new UserDelegationId(UUID.randomUUID().toString()))
                .delegatorId("user1")
                .delegateId("delegate1")
                .startDate(yesterday)
                .endDate(tomorrow)
                .isActive(true)
                .build();
        assertTrue(d1.isActiveNow(), "Should be active when today is within range");

        // Case 2: Inactive flag
        d1.setActive(false);
        assertFalse(d1.isActiveNow(), "Should be inactive when flag is false");

        // Case 3: Future range
        UserDelegation d2 = UserDelegation.builder()
                .delegationId(new UserDelegationId(UUID.randomUUID().toString()))
                .startDate(today.plusDays(2))
                .endDate(today.plusDays(5))
                .isActive(true)
                .build();
        assertFalse(d2.isActiveNow(), "Should be inactive when range is in future");

        // Case 4: Past range
        UserDelegation d3 = UserDelegation.builder()
                .delegationId(new UserDelegationId(UUID.randomUUID().toString()))
                .startDate(today.minusDays(5))
                .endDate(today.minusDays(2))
                .isActive(true)
                .build();
        assertFalse(d3.isActiveNow(), "Should be inactive when range is in past");

        // Case 5: Open ended (start only)
        UserDelegation d4 = UserDelegation.builder()
                .delegationId(new UserDelegationId(UUID.randomUUID().toString()))
                .startDate(today.minusDays(1))
                .endDate(null)
                .isActive(true)
                .build();
        assertTrue(d4.isActiveNow(), "Should be active when start is past and end is null");
    }
}
