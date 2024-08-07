package com.readyauction.app.user.repository;

import com.readyauction.app.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);

    Optional<Member> findMemberByEmail(String email);

    // 닉네임이 존재하는지 확인하는 메소드
//    boolean existsByNickname(String nickname);
}
