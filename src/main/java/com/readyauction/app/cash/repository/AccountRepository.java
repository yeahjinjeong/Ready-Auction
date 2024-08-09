package com.readyauction.app.cash.repository;

import com.readyauction.app.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Member, Long> {
}
