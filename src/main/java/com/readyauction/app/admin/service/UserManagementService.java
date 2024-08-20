package com.readyauction.app.admin.service;

import com.readyauction.app.admin.dto.UserManagementDto;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.UserStatus;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final MemberRepository memberRepository;

    public Page<UserManagementDto> getUsersByStatus(UserStatus status, Pageable pageable) {
        return memberRepository.findMembersByStatus(status, pageable)
                .map(this::convertToDto);
    }

    private UserManagementDto convertToDto(Member member) {
        Timestamp statusTimestamp = member.getUserStatus() == UserStatus.active ? member.getCreatedAt() : member.getDeletedAt();
        return new UserManagementDto(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getEmail(),
                member.getGender(),
                member.getAddress(),
                member.getPhone(),
                member.getBirth(),
                statusTimestamp
        );
    }
}
