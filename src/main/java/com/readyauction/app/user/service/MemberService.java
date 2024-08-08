package com.readyauction.app.user.service;

import com.readyauction.app.common.handler.UserNotFoundException;
import com.readyauction.app.user.dto.MemberDto;
import com.readyauction.app.user.dto.MemberRegisterRequestDto;
import com.readyauction.app.user.dto.MemberUpdateRequestDto;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Member member = memberRepository.findByEmail(dto.getEmail());
        member.changeName(dto.getName());
    }

    public Long findMemberIdByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        MemberDto memberDTO = MemberDto.toMemberDto(member);
        System.out.println(memberDTO);
        return member.getId();
    }

    public List<MemberDto> findAll() {
        List<Member> memberEntityList = memberRepository.findAll();
        List<MemberDto> memberDtoList = new ArrayList<>();
        for (Member memberEntity: memberEntityList) {
            memberDtoList.add(MemberDto.toMemberDto(memberEntity));
            MemberDto memberDTO = MemberDto.toMemberDto(memberEntity);
            memberDtoList.add(memberDTO);
        }
        return memberDtoList;
    }

    public MemberDto findById(Long id) {
        Optional<Member> optionalMemberEntity = memberRepository.findById(id);
        if (optionalMemberEntity.isPresent()) {
//            MemberEntity memberEntity = optionalMemberEntity.get();
//            MemberDto memberDto = MemberDto.toMemberDto(memberEntity);
//            return memberDTO;
            return MemberDto.toMemberDto(optionalMemberEntity.get());
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }


    public MemberDto updateForm(String myEmail) {
        Member optionalMemberEntity = memberRepository.findByEmail(myEmail);
        if (optionalMemberEntity != null) {
            return MemberDto.toMemberDto(optionalMemberEntity);
        } else {
            return null;
        }
    }

    // Profile 데이터 수정
    @Transactional
    public void updateProfile(MemberUpdateRequestDto dto) {
//        Member member = memberRepository.findByEmail(dto.getEmail());
        Member member = memberRepository.findByEmail("ssg@gmail.com"); // 정적
        if (member == null) {
            // 예외를 던지거나 로그를 남기고 종료
            throw new UserNotFoundException("Member not found with email: " + dto.getEmail());
        }
        member.setNickname(dto.getNickname());
        member.setProfilePicture(dto.getProfilePicture());
        MemberUpdateRequestDto.toUpdateMemberDto(member);
        System.out.println(dto);
    }

    // Member 데이터 수정
//    @Transactional
//    public Member updateMember(Member member) {
//        Member existingMember = memberRepository.findById(member.getId())
//                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + member.getId()));
//        existingMember.setNickname(member.getNickname());
//        existingMember.setProfilePicture(member.getProfilePicture());
//        existingMember.setAddress(member.getAddress());
//        existingMember.setMannerScore(member.getMannerScore());
//        return memberRepository.save(existingMember);
//    }

    @Transactional
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    public String emailCheck(String memberEmail) {
        Member byMemberEmail = memberRepository.findByEmail(memberEmail);
        if (byMemberEmail != null) {
            // 조회결과가 있다 -> 사용할 수 없다.
            return null;
        } else {
            // 조회결과가 없다 -> 사용할 수 있다.
            return "ok";
        }
    }
}
