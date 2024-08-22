package com.readyauction.app.cash.repository;

import com.readyauction.app.cash.entity.Cash;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CashRepository extends JpaRepository<Cash, Long> {
    Optional<Cash> findCashByMemberId(Long memberId);

    List<Cash> findAllByAccountId(Long accountId);
}
