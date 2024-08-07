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
//    Optional<Member> findByEmail(String email);

    Member findMemberByEmail(String email);
}
