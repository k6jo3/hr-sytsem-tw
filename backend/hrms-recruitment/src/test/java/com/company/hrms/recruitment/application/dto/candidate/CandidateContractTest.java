package com.company.hrms.recruitment.application.dto.candidate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.company.hrms.common.query.LogicalOp;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

public class CandidateContractTest {

    @Test
    void searchDto_ShouldGenerateCorrectQueryGroup() {
        // Arrange
        CandidateSearchDto dto = new CandidateSearchDto();
        dto.setOpeningId("opening-123");
        dto.setKeyword("John");
        dto.setStatus(Arrays.asList("NEW", "SCREENING"));
        dto.setSource("JOB_BANK");

        // Act
        QueryGroup queryGroup = QueryBuilder.fromCondition(dto);

        // Assert
        assertEquals(LogicalOp.AND, queryGroup.getJunction());
        assertEquals(4, queryGroup.getConditions().size());

        // Verify EQ openingId
        assertTrue(queryGroup.getConditions().stream()
                .anyMatch(c -> c.getField().equals("openingId") && c.getOp().equals(Operator.EQ)
                        && c.getValue().equals("opening-123")));

        // Verify LIKE fullName
        assertTrue(queryGroup.getConditions().stream()
                .anyMatch(c -> c.getField().equals("fullName") && c.getOp().equals(Operator.LIKE)
                        && c.getValue().equals("%John%")));

        // Verify IN status
        assertTrue(queryGroup.getConditions().stream()
                .anyMatch(c -> c.getField().equals("status") && c.getOp().equals(Operator.IN)
                        && Arrays.asList((Object[]) c.getValue()).contains("NEW")));

        // Verify EQ source
        assertTrue(queryGroup.getConditions().stream()
                .anyMatch(c -> c.getField().equals("source") && c.getOp().equals(Operator.EQ)
                        && c.getValue().equals("JOB_BANK")));
    }
}
