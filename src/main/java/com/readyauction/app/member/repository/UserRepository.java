package com.readyauction.app.member.repository;

import com.readyauction.app.member.entity.Member;
import com.readyauction.app.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
