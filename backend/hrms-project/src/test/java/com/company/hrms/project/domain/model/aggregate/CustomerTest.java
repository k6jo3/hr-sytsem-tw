package com.company.hrms.project.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.company.hrms.project.domain.model.command.CreateCustomerCommand;
import com.company.hrms.project.domain.model.command.UpdateCustomerCommand;
import com.company.hrms.project.domain.model.valueobject.CustomerStatus;

/**
 * TDD: Customer Aggregate Root Tests
 */
public class CustomerTest {

    @Test
    @DisplayName("Test: Successfully create a customer")
    void shouldCreateCustomerSuccessfully() {
        // Arrange
        CreateCustomerCommand cmd = CreateCustomerCommand.builder()
                .customerCode("CUST-001")
                .customerName("Acme Corp")
                .taxId("12345678")
                .industry("Technology")
                .email("contact@acme.com")
                .phoneNumber("02-1234-5678")
                .build();

        // Act
        Customer customer = Customer.create(cmd);

        // Assert
        assertNotNull(customer.getId());
        assertEquals("CUST-001", customer.getCustomerCode());
        assertEquals("Acme Corp", customer.getCustomerName());
        assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
    }

    @Test
    @DisplayName("Test: Update customer info")
    void shouldUpdateCustomerInfo() {
        // Arrange
        Customer customer = createValidCustomer();
        UpdateCustomerCommand cmd = UpdateCustomerCommand.builder()
                .customerName("New Name")
                .taxId("87654321")
                .industry("Finance")
                .email("new@acme.com")
                .phoneNumber("0912345678")
                .build();

        // Act
        customer.update(cmd);

        // Assert
        assertEquals("New Name", customer.getCustomerName());
        assertEquals("87654321", customer.getTaxId());
        assertEquals("Finance", customer.getIndustry());
        assertEquals("new@acme.com", customer.getEmail());
        assertEquals("0912345678", customer.getPhoneNumber());
    }

    private Customer createValidCustomer() {
        CreateCustomerCommand cmd = CreateCustomerCommand.builder()
                .customerCode("CUST-TEST")
                .customerName("Test Customer")
                .build();
        return Customer.create(cmd);
    }
}
