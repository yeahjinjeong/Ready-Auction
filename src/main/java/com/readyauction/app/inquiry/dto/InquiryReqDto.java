package com.readyauction.app.inquiry.dto;

import com.readyauction.app.inquiry.entity.InquiryCategory;
import com.readyauction.app.inquiry.entity.InquiryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InquiryReqDto {
    private Long id;
    private Long authorId; // 문의자
    private InquiryCategory category; // 카테고리
    private String title; // 문의 제목
    private String content; // 문의 내용
    private LocalDateTime createdAt; // 작성일시
    private InquiryStatus status; // 문의 처리 상태
}
