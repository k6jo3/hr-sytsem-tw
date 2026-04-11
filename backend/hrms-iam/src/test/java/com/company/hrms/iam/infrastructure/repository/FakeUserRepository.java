package com.company.hrms.iam.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;
import com.company.hrms.iam.domain.repository.IUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * IUserRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeUserRepository repo = new FakeUserRepository();
 * repo.seed(
 *     User.create("admin", new Email("admin@test.com"), "hash", "Admin"),
 *     User.create("user1", new Email("user1@test.com"), "hash", "User1")
 * );
 *
 * Optional&lt;User&gt; found = repo.findByUsername("admin");
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakeUserRepository
        extends InMemoryBaseRepository<User, UserId>
        implements IUserRepository {

    public FakeUserRepository() {
        super(User.class, User::getId);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return super.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findOneByField("username", username);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return findOneByField("email", email.getValue());
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        return findByField("status", status);
    }

    @Override
    public List<User> findAll() {
        return super.findAll();
    }

    @Override
    public Page<User> findPage(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    @Override
    public List<User> findAll(QueryGroup query) {
        return super.findAll(query);
    }

    @Override
    public long count(QueryGroup query) {
        return super.count(query);
    }

    @Override
    public void save(User user) {
        doSave(user);
    }

    @Override
    public void update(User user) {
        doSave(user);
    }

    @Override
    public void deleteById(UserId id) {
        super.deleteById(id);
    }

    @Override
    public Optional<User> findByEmployeeId(String employeeId) {
        return findOneByField("employeeId", employeeId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return existsByField("username", username);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return existsByField("email", email.getValue());
    }

    @Override
    public void updateUserRoles(UserId userId, List<String> roleIds) {
        findById(userId).ifPresent(user -> user.setRoles(roleIds));
    }
}
