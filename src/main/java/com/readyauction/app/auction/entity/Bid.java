package com.readyauction.app.auction.entity;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer myPrice;

    private Date bidTime;

    private Integer bidState;

    private Boolean highBid;

    // Getters and Setters
}





