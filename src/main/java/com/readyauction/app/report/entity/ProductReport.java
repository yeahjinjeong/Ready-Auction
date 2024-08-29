package com.readyauction.app.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "tbl_product_report")
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
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;
    // 추가 필드 예: 신고 대상 상품 ID, 신고자 ID 등
}