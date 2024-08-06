package com.readyauction.app.user.repository;

import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
}
