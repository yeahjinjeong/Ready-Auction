package com.readyauction.app.inquiry.service;

import com.readyauction.app.inquiry.dto.InquiryDetailDto;
import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.entity.Inquiry;
import com.readyauction.app.inquiry.repository.InquiryRepository;
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
        Member member = memberRepository.findById(id).get();
        Inquiry inquiry = inquiryRepository.findById(id).get();

        return InquiryDetailDto.toInquiryDetailDto(inquiry, member.getNickname());
    }
}
