package com.readyauction.app.user.service;

import com.readyauction.app.user.dto.MemberDTO;
import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.User;
import com.readyauction.app.user.entity.UserStatus;
import com.readyauction.app.user.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public void save(MemberDTO memberDTO) {
        // 1. dto -> entity 변환
        // 2. repository의 save 메서드 호출
        Member memberEntity = Member.toMember(memberDTO);
        memberEntity.setUserStatus(UserStatus.active);
//        memberEntity.setGender(Gender.M);
        memberEntity.setMannerScore(0);
        memberEntity.setProfilePicture("https://kr.object.ncloudstorage.com/ready-auction-bucket/sample-folder/87133e3b-797b-4894-b0bd-59f0d5b3b712.jpeg");
        // repository의 save메서드 호출 (조건. entity객체를 넘겨줘야 함)
        memberRepository.save(memberEntity);
    }

    public MemberDTO login(MemberDTO memberDTO) {
        /*
            1. 회원이 입력한 이메일로 DB에서 조회를 함
            2. DB에서 조회한 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 판단
         */
        Member memberEntity = memberRepository.findByEmail(memberDTO.getEmail());
        if (memberEntity != null) {
            // 조회 결과가 있다(해당 이메일을 가진 회원 정보가 있다)

            if (memberEntity.getPassword().equals(memberDTO.getPassword())) {
                // 비밀번호 일치
                // entity -> dto 변환 후 리턴
                MemberDTO dto = MemberDTO.toMemberDTO(memberEntity);
                return dto;
            } else {
                // 비밀번호 불일치(로그인실패)
                return null;
            }
        } else {
            // 조회 결과가 없다(해당 이메일을 가진 회원이 없다)
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

//    @Transactional
//    public Member updateMember(Member member) {
//        Member existingMember = memberRepository.findById(member.getId())
//                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + member.getId()));
//        existingMember.setNickname(member.getNickname());
//        existingMember.setPicture(member.getPicture());
//        existingMember.setAddress(member.getAddress());
//        existingMember.setMannersScore(member.getMannersScore());
//        existingMember.setCashPoint(member.getCashPoint());
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
