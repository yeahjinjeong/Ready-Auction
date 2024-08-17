package com.readyauction.app.user.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.common.handler.UserNotFoundException;
import com.readyauction.app.file.model.dto.FileDto;
import com.readyauction.app.file.model.service.NcpObjectStorageService;
import com.readyauction.app.user.dto.MemberDto;
import com.readyauction.app.user.dto.MemberRegisterRequestDto;
import com.readyauction.app.user.dto.MemberUpdateRequestDto;
import com.readyauction.app.user.dto.ProfileDto;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    private final AmazonS3Client amazonS3Client;
    private final AccountService accountService;
    @Value("${spring.s3.bucket}")
    private String bucketName;
    @Autowired
    private NcpObjectStorageService ncpObjectStorageService;


    public void register(MemberRegisterRequestDto dto) {
        // 1. dto -> entity 변환
        Member member = dto.toMember();
        // 기본권한 설정
        member.setDefaultAuthorities();

        // repository의 save메서드 호출 (조건. entity객체를 넘겨줘야 함)
        member = memberRepository.save(member);
        accountService.create(member.getId());
    }

    public void update(MemberUpdateRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail());
        member.changeName(dto.getName());
    }

    public Member findMemberByEmail(String email) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
        return memberOptional.orElseThrow(() -> new UserNotFoundException(email));
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

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }


    /* 지영 작업 시작 - 프로필 */

    // findMember
    public MemberDto findMemberDtoByEmail(String email) {
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new UserNotFoundException("No member found with email: " + email);
        }

        return MemberDto.toMemberDto(member);
    }

    // ProfileDto
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

    public String uploadImage(MultipartFile image, String email) throws IOException {
        String filePath = "profile/" + email + "/";
        List<MultipartFile> files = new ArrayList<>();
        files.add(image);
        List<FileDto> uploadedFiles = ncpObjectStorageService.uploadFiles(files, filePath);
        return uploadedFiles.get(0).getUploadFileUrl();
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String key = imageUrl.substring(imageUrl.indexOf(bucketName) + bucketName.length() + 1);
            deleteFile(key);
        }
    }

    public void deleteFile(String key) {
        try {
            amazonS3Client.deleteObject(bucketName, key);
            System.out.println("파일이 성공적으로 삭제되었습니다.");
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            System.out.println("파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public void updateProfile(String email, String nickname, MultipartFile image, String removeImage) throws IOException {
        Member member = findMemberByEmail(email);
        member.setNickname(nickname);

        boolean removeImageFlag = "true".equalsIgnoreCase(removeImage);

        if (removeImageFlag && member.getProfilePicture() != null) {
            deleteImage(member.getProfilePicture());
            member.setProfilePicture(null);
        }

        if (!image.isEmpty()) {
            String imageUrl = uploadImage(image, email);
            member.setProfilePicture(imageUrl);
        }

        save(member);
    }


    /* 지영 작업 끝 */


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