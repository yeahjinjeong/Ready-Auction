package com.readyauction.app.report.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long productId;
    private String reportReason;
    @CreationTimestamp
    private LocalDateTime reportDate;

    // 추가 필드 예: 신고 대상 상품 ID, 신고자 ID 등
}