package com.readyauction.app.auction.entity;

import com.readyauction.app.member.entity.Member;
import jakarta.persistence.*;


public class AuctionResult {

    private Long memberId;

    private Long productId;

    private Integer price;
    @Enumerated(EnumType.STRING)
    private Integer status; // 구매 대기, 거래중, 구매확정

    // Getters and Setters
}