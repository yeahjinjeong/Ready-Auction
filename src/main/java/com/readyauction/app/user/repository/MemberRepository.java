package com.readyauction.app.user.repository;

import com.readyauction.app.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

//    @Query("""
//       select
//            m
//       from
//            Member m join fetch m.authorities
//       where
//            m.email = :email
//       """)

    Member findByEmail(String email);

    Optional<Member> findMemberByEmail(String email);

    // 닉네임이 존재하는지 확인하는 메소드
//    boolean existsByNickname(String nickname);
}
