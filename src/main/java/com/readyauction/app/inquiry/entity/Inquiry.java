package com.readyauction.app.inquiry.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "tbl_inquiry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long authorId; // 문의자
    @Enumerated(EnumType.STRING)
    private InquiryCategory category; // 카테고리
    private String title; // 문의 제목
    private String content; // 문의 내용
    private Timestamp createdAt; // 작성일시
    @Enumerated(EnumType.STRING)
    private InquiryStatus status; // 문의 처리 상태

    @ElementCollection
    @CollectionTable(name = "tbl_answer", joinColumns = @JoinColumn(name = "inquiry_id"))
    @Column(name = "text")
    @OrderColumn(name = "idx")
    private List<Answer> answers; // 댓글
}
