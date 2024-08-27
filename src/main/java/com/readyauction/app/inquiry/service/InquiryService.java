package com.readyauction.app.inquiry.service;

import com.readyauction.app.inquiry.dto.InquiryAnswerDto;
import com.readyauction.app.inquiry.dto.InquiryDetailDto;
import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.dto.InquiryReqDto;
import com.readyauction.app.inquiry.entity.Answer;
import com.readyauction.app.inquiry.entity.Inquiry;
import com.readyauction.app.inquiry.entity.InquiryStatus;
import com.readyauction.app.inquiry.repository.InquiryRepository;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public List<InquiryDto> findAll() {
        List<InquiryDto> inquiryDtos = inquiryRepository.findAllAndNickname();
//        List<InquiryDto> inquiryDtos = new ArrayList<>();
//        inquiry.forEach((i) -> {
//            String author = memberRepository.findNicknamesByMemberId(i);
//            InquiryDto inquiryDto = InquiryDto.toInquiryListDto(i, author);
//            inquiryDtos.add(inquiryDto);
//        });
        return inquiryDtos;
    }

    public InquiryDetailDto findAndNicknameById(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id).get(); // 해당 글만 찾기
        Member member = memberRepository.findById(inquiry.getAuthorId()).get();

        return InquiryDetailDto.toInquiryDetailDto(inquiry, member.getNickname());

//        InquiryDetailDto inquiryDetailDto = inquiryRepository.findInquiryAndNicknameById(id).get(); // 해당 글만 찾기
//        return inquiryDetailDto;
    }

    public void addAnswer(Long authorId, InquiryAnswerDto inquiryAnswerDto) {
        Inquiry inquiry = inquiryRepository.findById(inquiryAnswerDto.getInquiryId()).get();
        Answer answer = Answer.builder()
                .authorId(authorId)
                .content(inquiryAnswerDto.getContent())
                .answeredAt(LocalDateTime.now().withNano(0))
                .build();
        inquiry.addAnswer(answer);
        inquiry.changeStatus(InquiryStatus.COMPLETE);
    }

    public void changeAnswer(Long authorId, InquiryAnswerDto inquiryAnswerDto) {
        Inquiry inquiry = inquiryRepository.findById(inquiryAnswerDto.getInquiryId()).get();
        inquiry.getAnswers().forEach((a) -> {
            System.out.println(a.getAnsweredAt());
            if (a.getAuthorId().equals(authorId) && a.getAnsweredAt().equals(inquiryAnswerDto.getAnsweredAt())){
                a.setContent(inquiryAnswerDto.getContent());
            }
        });
    }

    public void deleteAnswer(Long authorId, InquiryAnswerDto inquiryAnswerDto) {
        Inquiry inquiry = inquiryRepository.findById(inquiryAnswerDto.getInquiryId()).get();
        List<Answer> answers = inquiry.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).getAuthorId().equals(authorId) && answers.get(i).getAnsweredAt().equals(inquiryAnswerDto.getAnsweredAt())){
                answers.remove(i);
            }
        }
        inquiry.deleteAnswer(answers);
        if (answers.isEmpty()) {
            inquiry.changeStatus(InquiryStatus.PENDING);
        }
    }

    // 1:1 문의 등록하기
    public void registerInquiry(InquiryReqDto inquiryReqDto) {
        // 현재 인증된 사용자의 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName(); // 로그인한 이메일
        Long authorId = memberService.findMemberDtoByEmail(currentUserName).getId(); // 이메일을 통해 사용자 ID 가져오기

        // Inquiry 객체 생성
        Inquiry inquiry = Inquiry.builder()
                .authorId(authorId) // 가져온 사용자 ID를 설정
                .category(inquiryReqDto.getCategory())
                .title(inquiryReqDto.getTitle())
                .content(inquiryReqDto.getContent())
                .status(InquiryStatus.PENDING) // 기본 상태로 'PENDING' 설정
                .build();

        // Inquiry 객체를 데이터베이스에 저장
        inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getInquiriesByUserId(Long userId) {
        return inquiryRepository.findByAuthorId(userId); // 사용자 ID로 문의 내역을 조회
    }
}
