package com.readyauction.app.user.service;

import com.readyauction.app.common.handler.UserNotFoundException;
import com.readyauction.app.user.dto.MemberDTO;
import com.readyauction.app.user.dto.MemberUpdateRequestDto;
import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.UserStatus;
import com.readyauction.app.user.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void save(MemberDTO memberDTO) {
        // 1. dto -> entity 변환
        // 2. repository의 save 메서드 호출
        Member memberEntity = Member.toMember(memberDTO);
        memberEntity.setUserStatus(UserStatus.active);
        memberEntity.setMannerScore(0);
        memberEntity.setProfilePicture("https://kr.object.ncloudstorage.com/ready-auction-bucket/sample-folder/87133e3b-797b-4894-b0bd-59f0d5b3b712.jpeg");
        // repository의 save메서드 호출 (조건. entity객체를 넘겨줘야 함)
        memberRepository.save(memberEntity);
    }

//    public User findMemberByEmail(String email){
//        return memberRepository.findByEmail(email);
//    }

    public Member findMemberByEmail(String email) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
        return memberOptional.orElseThrow(() -> new UserNotFoundException(email));
    }

    public MemberDTO login(MemberDTO memberDTO) {
        /*
            1. 회원이 입력한 이메일로 DB에서 조회를 함
            2. DB에서 조회한 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 판단
         */
        System.out.println("로그인 디비 조회중");
        Member memberEntity = memberRepository.findByEmail(memberDTO.getEmail());
        System.out.println(memberEntity.toString());
        if (memberEntity != null) {
            // 조회 결과가 있다(해당 이메일을 가진 회원 정보가 있다)
            if (passwordEncoder.matches(memberDTO.getPassword(), memberEntity.getPassword()) == true) {
//            if (memberDTO.getPassword().equals(memberEntity.getPassword()) == true) {

                System.out.println("비밀번호 일치");
                // 비밀번호 일치
                // entity -> dto 변환 후 리턴
                MemberDTO dto = MemberDTO.toMemberDTO(memberEntity);
                return dto;
            } else {
                // 비밀번호 불일치(로그인실패)
                System.out.println("불일치 ");
                return null;
            }
        } else {
            // 조회 결과가 없다(해당 이메일을 가진 회원이 없다)
            System.out.println("회원 없음");
            return null;
        }
    }

    public List<MemberDTO> findAll() {
        List<Member> memberEntityList = memberRepository.findAll();
        List<MemberDTO> memberDTOList = new ArrayList<>();
        for (Member memberEntity: memberEntityList) {
            memberDTOList.add(MemberDTO.toMemberDTO(memberEntity));
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            memberDTOList.add(memberDTO);
        }
        return memberDTOList;
    }

    public MemberDTO findById(Long id) {
        Optional<Member> optionalMemberEntity = memberRepository.findById(id);
        if (optionalMemberEntity.isPresent()) {
//            MemberEntity memberEntity = optionalMemberEntity.get();
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            return memberDTO;
            return MemberDTO.toMemberDTO(optionalMemberEntity.get());
        } else {
            return null;
        }

    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }


    public MemberDTO updateForm(String myEmail) {
        Member optionalMemberEntity = memberRepository.findByEmail(myEmail);
        if (optionalMemberEntity != null) {
            return MemberDTO.toMemberDTO(optionalMemberEntity);
        } else {
            return null;
        }
    }

    // Profile 데이터 수정
    @Transactional
    public void updateProfile(MemberUpdateRequestDto dto) {
//        Member member = memberRepository.findByEmail(dto.getEmail());
        Member member = memberRepository.findByEmail("ssg@gmail.com");
        if (member == null) {
            // 예외를 던지거나 로그를 남기고 종료
            throw new UserNotFoundException("Member not found with email: " + dto.getEmail());
        }
        member.setNickname(dto.getNickname());
        member.setProfilePicture(dto.getProfilePicture());
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