package com.company.hrms.project.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.project.domain.model.aggregate.Customer;
import com.company.hrms.project.domain.model.valueobject.CustomerId;
import com.company.hrms.project.domain.repository.ICustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * ICustomerRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeCustomerRepository repo = new FakeCustomerRepository();
 * repo.seed(customer);
 *
 * boolean exists = repo.existsByCustomerCode("CUST001");
 * assertThat(exists).isTrue();
 * </pre>
 */
public class FakeCustomerRepository
        extends InMemoryBaseRepository<Customer, CustomerId>
        implements ICustomerRepository {

    public FakeCustomerRepository() {
        super(Customer.class, Customer::getId);
    }

    /**
     * 儲存客戶（介面回傳實體）
     */
    @Override
    public Customer save(Customer customer) {
        return doSave(customer);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<Customer> findById(CustomerId id) {
        return super.findById(id);
    }

    /**
     * 查詢所有客戶
     */
    @Override
    public List<Customer> findAll() {
        return super.findAll();
    }

    /**
     * 分頁查詢客戶
     */
    @Override
    public Page<Customer> findCustomers(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    /**
     * 檢查客戶代碼是否存在
     */
    @Override
    public boolean existsByCustomerCode(String customerCode) {
        return existsByField("customerCode", customerCode);
    }

    /**
     * 檢查統一編號是否存在
     */
    @Override
    public boolean existsByTaxId(String taxId) {
        return existsByField("taxId", taxId);
    }
}
