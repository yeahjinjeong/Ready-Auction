package com.readyauction.app.inquiry.service;

import com.readyauction.app.inquiry.dto.InquiryAnswerDto;
import com.readyauction.app.inquiry.dto.InquiryDetailDto;
import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.entity.Answer;
import com.readyauction.app.inquiry.entity.Inquiry;
import com.readyauction.app.inquiry.repository.InquiryRepository;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.ion.Timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

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
                .answeredAt(LocalDateTime.now().withSecond(0).withNano(0))
                .build();
        inquiry.addAnswer(answer);
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
    }
}
