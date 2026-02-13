package com.company.hrms.project.domain.model.aggregate;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.project.domain.event.CustomerCreatedEvent;
import com.company.hrms.project.domain.model.command.CreateCustomerCommand;
import com.company.hrms.project.domain.model.command.UpdateCustomerCommand;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.model.valueobject.CustomerStatus;

import lombok.Getter;

@Getter
public class Customer extends AggregateRoot<CustomerId> {

    private String customerCode;
    private String customerName;
    private String taxId;
    private String industry;
    private String email;
    private String phoneNumber;
    private CustomerStatus status;
    private long version;
    private Integer isDeleted;
    private Integer projectCount;

    // Domain Constructor
    private Customer(CustomerId id) {
        super(id);
    }

    public long getVersion() {
        return version;
    }

    /**
     * 重建 Aggregate Root (由 Repository 調用)
     */
    public static Customer reconstitute(
            CustomerId id,
            String customerCode,
            String customerName,
            String taxId,
            String industry,
            String email,
            String phoneNumber,
            CustomerStatus status,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime updatedAt,
            long version,
            Integer isDeleted,
            Integer projectCount) {

        Customer customer = new Customer(id);
        customer.customerCode = customerCode;
        customer.customerName = customerName;
        customer.taxId = taxId;
        customer.industry = industry;
        customer.email = email;
        customer.phoneNumber = phoneNumber;
        customer.status = status;
        customer.version = version;
        customer.isDeleted = isDeleted;
        customer.projectCount = projectCount;

        return customer;
    }

    public static Customer create(CreateCustomerCommand cmd) {
        Customer customer = new Customer(CustomerId.generate());
        customer.customerCode = cmd.getCustomerCode();
        customer.customerName = cmd.getCustomerName();
        customer.taxId = cmd.getTaxId();
        customer.industry = cmd.getIndustry();
        customer.email = cmd.getEmail();
        customer.phoneNumber = cmd.getPhoneNumber();
        customer.status = CustomerStatus.ACTIVE;

        // 註冊領域事件
        customer.registerEvent(new CustomerCreatedEvent(
                customer.getId().getValue(),
                customer.customerCode,
                customer.customerName));

        return customer;
    }

    public void update(UpdateCustomerCommand cmd) {
        this.customerName = cmd.getCustomerName();
        this.taxId = cmd.getTaxId();
        this.industry = cmd.getIndustry();
        this.email = cmd.getEmail();
        this.phoneNumber = cmd.getPhoneNumber();
        // Assuming status update is separate or not allowed via general update
    }
}
