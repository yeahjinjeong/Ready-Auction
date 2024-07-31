package com.readyauction.app.auction.entity;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private Long sendAccountId;

    private Long receiveAccountId;

    private Integer payment;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Getters and Setters
}
