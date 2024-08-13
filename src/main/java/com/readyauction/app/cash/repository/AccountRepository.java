package com.readyauction.app.cash.repository;

import com.readyauction.app.cash.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByMemberId(Long memberId);
}
