package com.readyauction.app.auction.entity;

import com.readyauction.app.member.entity.Member;
import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    @ManyToOne
    private Account sendAccount;

    private Long receiveAccountId;

    private Integer payment;

    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // Getters and Setters
}
