package com.readyauction.app.auction.entity;

import jakarta.persistence.*;

@Entity
public class Winner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long productId;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status; // 구매 대기, 거래중, 구매확정

    // Getters and Setters
}