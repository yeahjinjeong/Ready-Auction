package com.readyauction.app.user.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.readyauction.app.common.handler.UserNotFoundException;
import com.readyauction.app.file.model.dto.FileDto;
import com.readyauction.app.user.dto.MemberDto;
import com.readyauction.app.user.dto.MemberRegisterRequestDto;
import com.readyauction.app.user.dto.MemberUpdateRequestDto;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
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

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${spring.s3.bucket}")
    private String bucketName;

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

    public Member findMemberByEmail(String email) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
        return memberOptional.orElseThrow(() -> new UserNotFoundException(email));
    }

//    public MemberDto login(MemberDto memberDto) {
//        /*
//            1. 회원이 입력한 이메일로 DB에서 조회를 함
//            2. DB에서 조회한 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 판단
//         */
//        System.out.println("로그인 디비 조회중");
//        Member memberEntity = memberRepository.findByEmail(memberDto.getEmail());
//        System.out.println(memberEntity.toString());
//        if (memberEntity != null) {
//            // 조회 결과가 있다(해당 이메일을 가진 회원 정보가 있다)
//            if (passwordEncoder.matches(memberDto.getPassword(), memberEntity.getPassword()) == true) {
////            if (memberDTO.getPassword().equals(memberEntity.getPassword()) == true) {
//
//                System.out.println("비밀번호 일치");
//                // 비밀번호 일치
//                // entity -> dto 변환 후 리턴
//                MemberDto dto = MemberDto.toMemberDto(memberEntity);
//                return dto;
//            } else {
//                // 비밀번호 불일치(로그인실패)
//                System.out.println("불일치 ");
//                return null;
//            }
//        } else {
//            // 조회 결과가 없다(해당 이메일을 가진 회원이 없다)
//            System.out.println("회원 없음");
//            return null;
//        }
//    }

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


    /* 지영 작업 시작 - 프로필 */

    // Profile 수정을 위한 저장
    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }

    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }

    public String uploadImage(MultipartFile image, String email) throws IOException {
        String filePath = "profile/" + email + "/";
        List<MultipartFile> files = new ArrayList<>();
        files.add(image);
        List<FileDto> uploadedFiles = uploadFiles(files, filePath);
        return uploadedFiles.get(0).getUploadFileUrl();
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String key = imageUrl.substring(imageUrl.indexOf(bucketName) + bucketName.length() + 1);
            deleteFile(key);
        }
    }

    public List<FileDto> uploadFiles(List<MultipartFile> multipartFiles, String filePath) {
        List<FileDto> s3files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getUuidFileName(originalFileName);
            String uploadFileUrl = "";
            String keyName = filePath + uploadFileName;

            if (amazonS3Client.doesObjectExist(bucketName, keyName)) {
                uploadFileUrl = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;
            } else {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(multipartFile.getSize());
                objectMetadata.setContentType(multipartFile.getContentType());

                try (InputStream inputStream = multipartFile.getInputStream()) {
                    amazonS3Client.putObject(
                            new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                                    .withCannedAcl(CannedAccessControlList.PublicRead));

                    uploadFileUrl = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            s3files.add(
                    FileDto.builder()
                            .originalFileName(originalFileName)
                            .uploadFileName(uploadFileName)
                            .uploadFilePath(filePath)
                            .uploadFileUrl(uploadFileUrl)
                            .build());
        }

        return s3files;
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
