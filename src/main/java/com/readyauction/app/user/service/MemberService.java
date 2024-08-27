package com.readyauction.app.user.service;

import com.readyauction.app.cash.service.AccountService;
import com.readyauction.app.common.handler.UserNotFoundException;
import com.readyauction.app.ncp.dto.FileDto;
import com.readyauction.app.ncp.service.NcpObjectStorageService;
import com.readyauction.app.report.entity.Dislike;
import com.readyauction.app.report.entity.Like;
import com.readyauction.app.report.entity.MannerReport;
import com.readyauction.app.report.repository.ReportRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final NcpObjectStorageService ncpObjectStorageService;
    private final AccountService accountService;
    private final ReportRepository reportRepository;
    @Value("${spring.s3.bucket}")
    private String bucketName;

    public Member register(MemberRegisterRequestDto dto) {
        // dto -> entity 변환
        Member member = dto.toMember();
        // 기본권한 설정
        member.setDefaultAuthorities();
        member = memberRepository.save(member);
        // 계좌 자동 생성
        accountService.create(member.getId());
        // 매너지수 기본 50 세팅
        member.updateMannerScore(50);
        return member;// 저장 후 Member 반환
    }

    public void update(MemberUpdateRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail());
        member.changeName(dto.getName());
    }

    public Member findMemberByEmail(String email) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
        return memberOptional.orElseThrow(() -> new UserNotFoundException(email));
    }

    public Member findMemberById(Long id) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        return memberOptional.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member findById(Long id){
        return memberRepository.findById(id).get();
    }

    /** 프로필 **/

    public MemberDto findMemberDtoByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UserNotFoundException("No member found with email: " + email);
        }
        return new MemberDto(member);
    }

    public ProfileDto findProfileDtoById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + memberId));

        // memberId로 모든 MannerReport 가져오기
        List<MannerReport> mannerReports = reportRepository.findByMemberId(memberId);

        // Like와 Dislike 항목을 합산
        Map<Like, Long> likeCounts = new EnumMap<>(Like.class);
        Map<Dislike, Long> dislikeCounts = new EnumMap<>(Dislike.class);

        for (MannerReport report : mannerReports) {
            report.getLikes().forEach(like ->
                    likeCounts.merge(like, 1L, Long::sum));
            report.getDislikes().forEach(dislike ->
                    dislikeCounts.merge(dislike, 1L, Long::sum));
        }

        return ProfileDto.builder()
                .nickname(member.getNickname())
                .address(member.getAddress())
                .mannerScore(member.getMannerScore())
                .profilePicture(member.getProfilePicture())
                .likeCounts(likeCounts)
                .dislikeCounts(dislikeCounts)
                .build();
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

    public String findEmailById(Long id){
        Member member = memberRepository.findById(id).get();
        return member.getEmail();
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