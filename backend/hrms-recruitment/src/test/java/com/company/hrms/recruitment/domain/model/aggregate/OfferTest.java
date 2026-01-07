package com.company.hrms.recruitment.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.OfferStatus;

/**
 * Offer 聚合根測試
 */
@DisplayName("Offer 聚合根測試")
class OfferTest {

    private static final CandidateId CANDIDATE_ID = CandidateId.create();
    private static final String CANDIDATE_NAME = "王小明";
    private static final String POSITION = "前端工程師";
    private static final BigDecimal SALARY = new BigDecimal("70000");
    private static final LocalDate START_DATE = LocalDate.now().plusMonths(1);
    private static final LocalDate EXPIRY_DATE = LocalDate.now().plusDays(7);

    @Nested
    @DisplayName("建立 Offer")
    class CreateOfferTests {

        @Test
        @DisplayName("應成功建立 Offer，狀態為 PENDING")
        void shouldCreateOfferWithPendingStatus() {
            // When
            Offer offer = Offer.create(CANDIDATE_ID, CANDIDATE_NAME, POSITION,
                    SALARY, START_DATE, EXPIRY_DATE);

            // Then
            assertNotNull(offer.getId());
            assertEquals(CANDIDATE_ID, offer.getCandidateId());
            assertEquals(CANDIDATE_NAME, offer.getCandidateName());
            assertEquals(POSITION, offer.getOfferedPosition());
            assertEquals(SALARY, offer.getOfferedSalary());
            assertEquals(OfferStatus.PENDING, offer.getStatus());
            assertTrue(offer.getDomainEvents().size() > 0);
        }

        @Test
        @DisplayName("薪資為0時應拋出例外")
        void shouldThrowExceptionWhenSalaryIsZero() {
            assertThrows(IllegalArgumentException.class, () -> Offer.create(CANDIDATE_ID, CANDIDATE_NAME, POSITION,
                    BigDecimal.ZERO, START_DATE, EXPIRY_DATE));
        }

        @Test
        @DisplayName("到期日早於今日應拋出例外")
        void shouldThrowExceptionWhenExpiryDateInPast() {
            assertThrows(IllegalArgumentException.class, () -> Offer.create(CANDIDATE_ID, CANDIDATE_NAME, POSITION,
                    SALARY, START_DATE, LocalDate.now().minusDays(1)));
        }

        @Test
        @DisplayName("職位為空時應拋出例外")
        void shouldThrowExceptionWhenPositionIsEmpty() {
            assertThrows(IllegalArgumentException.class, () -> Offer.create(CANDIDATE_ID, CANDIDATE_NAME, "",
                    SALARY, START_DATE, EXPIRY_DATE));
        }
    }

    @Nested
    @DisplayName("接受 Offer")
    class AcceptOfferTests {

        @Test
        @DisplayName("PENDING 狀態可以接受")
        void shouldAcceptPendingOffer() {
            // Given
            Offer offer = createPendingOffer();
            offer.clearDomainEvents();

            // When
            offer.accept();

            // Then
            assertEquals(OfferStatus.ACCEPTED, offer.getStatus());
            assertNotNull(offer.getResponseDate());
        }

        @Test
        @DisplayName("已拒絕的 Offer 無法接受")
        void shouldNotAcceptRejectedOffer() {
            // Given
            Offer offer = createPendingOffer();
            offer.reject("有更好的選擇");

            // When & Then
            assertThrows(IllegalStateException.class, () -> offer.accept());
        }
    }

    @Nested
    @DisplayName("拒絕 Offer")
    class RejectOfferTests {

        @Test
        @DisplayName("PENDING 狀態可以拒絕")
        void shouldRejectPendingOffer() {
            // Given
            Offer offer = createPendingOffer();
            offer.clearDomainEvents();

            // When
            offer.reject("薪資不符合期望");

            // Then
            assertEquals(OfferStatus.REJECTED, offer.getStatus());
            assertEquals("薪資不符合期望", offer.getRejectionReason());
        }
    }

    @Nested
    @DisplayName("Offer 到期")
    class ExpireOfferTests {

        @Test
        @DisplayName("可以標記 Offer 為過期")
        void shouldExpireOffer() {
            // Given
            Offer offer = createPendingOffer();
            offer.clearDomainEvents();

            // When
            offer.expire();

            // Then
            assertEquals(OfferStatus.EXPIRED, offer.getStatus());
        }

        @Test
        @DisplayName("已接受的 Offer 無法過期")
        void shouldNotExpireAcceptedOffer() {
            // Given
            Offer offer = createPendingOffer();
            offer.accept();

            // When & Then
            assertThrows(IllegalStateException.class, () -> offer.expire());
        }
    }

    @Nested
    @DisplayName("撤回和延期")
    class WithdrawAndExtendTests {

        @Test
        @DisplayName("可以撤回 Offer")
        void shouldWithdrawOffer() {
            // Given
            Offer offer = createPendingOffer();
            offer.clearDomainEvents();

            // When
            offer.withdraw();

            // Then
            assertEquals(OfferStatus.WITHDRAWN, offer.getStatus());
        }

        @Test
        @DisplayName("可以延長到期日")
        void shouldExtendExpiryDate() {
            // Given
            Offer offer = createPendingOffer();
            LocalDate newExpiry = EXPIRY_DATE.plusDays(7);

            // When
            offer.extendExpiryDate(newExpiry);

            // Then
            assertEquals(newExpiry, offer.getExpiryDate());
        }

        @Test
        @DisplayName("新到期日必須晚於原到期日")
        void shouldNotExtendToEarlierDate() {
            // Given
            Offer offer = createPendingOffer();
            LocalDate earlierDate = EXPIRY_DATE.minusDays(1);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> offer.extendExpiryDate(earlierDate));
        }
    }

    // === 測試輔助方法 ===

    private Offer createPendingOffer() {
        return Offer.create(CANDIDATE_ID, CANDIDATE_NAME, POSITION,
                SALARY, START_DATE, EXPIRY_DATE);
    }
}
