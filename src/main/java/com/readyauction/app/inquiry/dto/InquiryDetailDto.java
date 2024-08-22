package com.readyauction.app.inquiry.dto;

import com.readyauction.app.inquiry.entity.Answer;
import com.readyauction.app.inquiry.entity.Inquiry;
import com.readyauction.app.inquiry.entity.InquiryCategory;
import com.readyauction.app.inquiry.entity.InquiryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDetailDto {
    private Long id;
    private InquiryCategory category; // 카테고리
    private String title; // 문의 제목
    private String nickname; // 문의자
    private Timestamp createdAt; // 작성일시
    private InquiryStatus status; // 문의 처리 상태
    private String content; // 문의 내용
    private List<Answer> answers;

    public static InquiryDetailDto toInquiryDetailDto(Inquiry inquiry, String nickname) {
        return new InquiryDetailDto(
                inquiry.getId(),
                inquiry.getCategory(),
                inquiry.getTitle(),
                nickname,
                inquiry.getCreatedAt(),
                inquiry.getStatus(),
                inquiry.getContent(),
                inquiry.getAnswers()
        );
    }
}
