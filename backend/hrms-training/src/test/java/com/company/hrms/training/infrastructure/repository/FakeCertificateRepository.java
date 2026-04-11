package com.company.hrms.training.infrastructure.repository;

import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.training.domain.model.aggregate.Certificate;
import com.company.hrms.training.domain.model.valueobject.CertificateId;
import com.company.hrms.training.domain.repository.ICertificateRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ICertificateRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeCertificateRepository repo = new FakeCertificateRepository();
 * repo.seed(Certificate.create(...));
 *
 * List&lt;Certificate&gt; expiring = repo.findExpiringCertificates(LocalDate.now().plusDays(30));
 * assertThat(expiring).isNotEmpty();
 * </pre>
 */
public class FakeCertificateRepository
        extends InMemoryBaseRepository<Certificate, CertificateId>
        implements ICertificateRepository {

    public FakeCertificateRepository() {
        super(Certificate.class, Certificate::getId);
    }

    /**
     * 儲存證照（介面回傳實體）
     */
    @Override
    public Certificate save(Certificate certificate) {
        return doSave(certificate);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<Certificate> findById(CertificateId id) {
        return super.findById(id);
    }

    /**
     * 刪除證照
     */
    @Override
    public void delete(Certificate certificate) {
        super.delete(certificate);
    }

    /**
     * 查詢即將到期的證照
     * 篩選到期日在 thresholdDate 之前（含）且尚未過期的證照
     */
    @Override
    public List<Certificate> findExpiringCertificates(LocalDate thresholdDate) {
        return findAll().stream()
                .filter(cert -> cert.getExpiryDate() != null
                        && !cert.getExpiryDate().isAfter(thresholdDate)
                        && !cert.getExpiryDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }
}
