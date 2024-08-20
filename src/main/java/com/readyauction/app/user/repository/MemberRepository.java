package com.readyauction.app.user.repository;

import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.UserStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

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

    List<Member> findByUserStatus(UserStatus userStatus);

    @Query("SELECT m FROM Member m WHERE m.userStatus = :status")
    Page<Member> findMembersByStatus(@Param("status") UserStatus status, Pageable pageable);


    // 닉네임이 존재하는지 확인하는 메소드
//    boolean existsByNickname(String nickname);
}
