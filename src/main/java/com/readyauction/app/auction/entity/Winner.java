package com.readyauction.app.auction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Winner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long productId;

    private Integer price;


    private Timestamp winnerTime;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status; // 구매 대기, 거래중, 구매확정

    // Getters and Setters
}