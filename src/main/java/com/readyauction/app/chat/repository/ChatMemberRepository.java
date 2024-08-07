package com.readyauction.app.chat.repository;

import com.readyauction.app.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMemberRepository extends JpaRepository<Member, Long> {
    Member findMemberById(Long userId);
}
