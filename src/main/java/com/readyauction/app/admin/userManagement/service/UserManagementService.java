package com.readyauction.app.admin.userManagement.service;

import com.readyauction.app.admin.userManagement.dto.UserManagementDto;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.UserStatus;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final MemberRepository memberRepository;

    public Page<UserManagementDto> getUsersByStatus(UserStatus status, Pageable pageable) {
        return memberRepository.findMembersByStatus(status, pageable)
                .map(this::convertToDto);
    }

    private UserManagementDto convertToDto(Member member) {
        return new UserManagementDto(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getEmail(),
                member.getGender(),
                member.getAddress(),
                member.getPhone(),
                member.getBirth(),
                member.getUserStatus() == UserStatus.active ? member.getCreatedAt() : member.getDeletedAt(),
                member.getUserStatus().toString(),
                member.getCreatedAt(), // 가입일
                member.getDeletedAt(), // 탈퇴일
                member.getMannerScore()
        );
    }

    public Member getUserById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserManagementDto getUserDtoById(Long id) {
        Member member = getUserById(id);
        return convertToDto(member); // 서비스 내에서 DTO로 변환
    }
}

