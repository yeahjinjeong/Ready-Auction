package com.readyauction.app.cash.repository;

import com.readyauction.app.cash.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
    Optional<Charge> findChargeByMemberId(Long memberId);
}
