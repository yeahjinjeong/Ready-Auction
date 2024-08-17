package com.readyauction.app.user.service;

import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.common.handler.UserNotFoundException;
import com.readyauction.app.ncp.dto.FileDto;
import com.readyauction.app.ncp.service.NcpObjectStorageService;
import com.readyauction.app.user.dto.MemberDto;
import com.readyauction.app.user.dto.MemberRegisterRequestDto;
import com.readyauction.app.user.dto.MemberUpdateRequestDto;
import com.readyauction.app.user.dto.ProfileDto;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final NcpObjectStorageService ncpObjectStorageService;
    private final AccountService accountService;
    @Value("${spring.s3.bucket}")
    private String bucketName;

    public Member register(MemberRegisterRequestDto dto) {
        // 1. dto -> entity 변환
        Member member = dto.toMember();
        // 기본권한 설정
        member.setDefaultAuthorities();

        // repository의 save메서드 호출 (조건. entity객체를 넘겨줘야 함)
        member = memberRepository.save(member);
        accountService.create(member.getId());
        return memberRepository.save(member); // 저장 후 Member 반환
    }

    public void update(MemberUpdateRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail());
        member.changeName(dto.getName());
    }

    public Member findMemberByEmail(String email) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
        return memberOptional.orElseThrow(() -> new UserNotFoundException(email));
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }


    /** 프로필 **/

    public MemberDto findMemberDtoByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UserNotFoundException("No member found with email: " + email);
        }
        return new MemberDto(member);
    }

    // Member 엔티티 -> ProfileDto
    public ProfileDto toProfileDto(String email) {
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Member 엔티티의 toProfileDto 메소드를 호출하여 ProfileDto로 변환
        return member.toProfileDto();
    }

    // Profile 수정을 위한 저장
    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }

    public String uploadImage(String email, MultipartFile image) {
        String filePath = "profile/" + email;
        List<MultipartFile> files = new ArrayList<>();
        files.add(image);
        List<FileDto> uploadedFiles = ncpObjectStorageService.uploadFiles(files, filePath);
        return uploadedFiles.get(0).getUploadFileUrl();
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String key = imageUrl.substring(imageUrl.indexOf(bucketName) + bucketName.length() + 1);
            ncpObjectStorageService.deleteFile(key);
        }
    }

    public void updateProfile(String email, String nickname, MultipartFile image, @RequestParam("isRemovedImage") boolean isRemovedImage) throws IOException {
        Member member = memberRepository.findByEmail(email);
        member.setNickname(nickname);

        // 이미지 제거 요청이 있을 경우
        if (isRemovedImage) {
            // 기존 프로필 이미지 삭제
            deleteImage(member.getProfilePicture());

            // Member 엔티티의 profilePicture를 null로 업데이트
            member.setProfilePicture(null);
        } else {
            // 기존 프로필 이미지가 있는 경우 삭제
            if (member.getProfilePicture() != null) {
                deleteImage(member.getProfilePicture());
            }

            // 새 프로필 이미지 업로드
            String newProfilePicture = uploadImage(email, image);
            member.setProfilePicture(newProfilePicture);
        }

        member.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        save(member);
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