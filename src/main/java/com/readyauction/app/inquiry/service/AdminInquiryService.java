package com.readyauction.app.inquiry.service;

import com.readyauction.app.inquiry.dto.InquiryAnswerDto;
import com.readyauction.app.inquiry.dto.InquiryDetailDto;
import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.entity.Answer;
import com.readyauction.app.inquiry.entity.Inquiry;
import com.readyauction.app.inquiry.entity.InquiryStatus;
import com.readyauction.app.inquiry.repository.InquiryRepository;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminInquiryService {
    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    public Page<InquiryDto> findAll(Pageable pageable) {
        return inquiryRepository.findAllAndNickname(pageable);
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
}
