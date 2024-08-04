package com.readyauction.app.auction.entity;

import com.readyauction.app.member.entity.Member;
import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer myPrice;

    private Timestamp bidTime;

    private Integer bidState;

    private Boolean highBid;

    // Getters and Setters
}





