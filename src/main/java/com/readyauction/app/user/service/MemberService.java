package com.readyauction.app.user.service;

import com.readyauction.app.user.dto.MemberRegisterRequestDto;
import com.readyauction.app.user.dto.MemberUpdateRequestDto;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.User;
import com.readyauction.app.user.entity.UserStatus;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void register(MemberRegisterRequestDto dto) {
        // 1. dto -> entity 변환
        Member member = dto.toMember();
        // 기본권한 설정
        member.setDefaultAuthorities();
        // repository의 save메서드 호출 (조건. entity객체를 넘겨줘야 함)
        memberRepository.save(member);
    }

    public void update(MemberUpdateRequestDto dto) {
        Member member = memberRepository.findMemberByEmail(dto.getEmail());
        member.changeName(dto.getName());
    }

    public Member findMemberByEmail(String email){
        return memberRepository.findMemberByEmail(email);
    }

}
