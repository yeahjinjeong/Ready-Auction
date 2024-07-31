package com.readyauction.app.auction.entity;

import jakarta.persistence.*;

public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer price;

    private Integer status; // 구매 대기, 거래중, 구매확정

    // Getters and Setters
}