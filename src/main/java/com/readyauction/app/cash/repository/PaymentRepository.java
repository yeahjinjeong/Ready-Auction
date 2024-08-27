package com.readyauction.app.cash.repository;

import com.readyauction.app.cash.entity.Payment;
import com.readyauction.app.cash.entity.PaymentCategory;
import com.readyauction.app.cash.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByMemberIdAndProductIdAndCategory(Long memberId, Long productId, PaymentCategory category);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = 'ROLLBACK' WHERE p.productId = :productId AND p.category = :category AND p.status = :status")
    int updateStatusToRollbackByProductIdAndCategory(@Param("productId") Long productId, @Param("category") PaymentCategory category, @Param("status") PaymentStatus status);

    Optional<Payment> findByProductIdAndMemberIdAndCategory(Long productId,Long memberId,PaymentCategory category);
    Optional<List<Payment>> findByProductIdAndMemberIdAndCategoryNotAndStatusOrStatus(Long productId,Long memberId,PaymentCategory category, PaymentStatus status, PaymentStatus status2);
    Optional<List<Payment>> findByProductIdAndStatus(Long productId, PaymentStatus status);

    /** 지영 - memberId와 일치하는 결제 내역 조회 **/
    List<Payment> findAllByMemberId(Long memberId);

    /** 지영 - 판매자 계좌와 연관된 모든 결제 내역을 조회 **/
    @Query("SELECT p FROM Payment p WHERE p.receiverAccount.id = :accountId")
    List<Payment> findAllByReceiverAccountId(@Param("accountId") Long accountId);

    /** 지영 - 마이페이지 경매 등록 내역 조회 시 필요 **/

    // 거래 완료 (payment의 status가 COMPLETED인 경우)
    @Query("SELECT p.productId FROM Payment p WHERE p.status = :status AND p.memberId = :memberId")
    List<Long> findCompletedProductIdsByMemberId(Long memberId, PaymentStatus status);

    /** 성연 구매확정량 & 금액 필요**/
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.date BETWEEN :start AND :end")
    List<Payment> findConfirmedPaymentsInTimeRange(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end,
                                                   @Param("status") PaymentStatus status);

    Optional<Payment> findByProductIdAndMemberIdAndStatus(Long id, Long memberId, PaymentCategory paymentCategory);
}
